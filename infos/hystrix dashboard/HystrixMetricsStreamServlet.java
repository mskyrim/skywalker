package com.altarea.rueducommerce.storefront.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;


/**
 *
 * @author Mehdi.Dardani
 *
 */
public class HystrixMetricsStreamServlet extends HttpServlet
{

	private static final long serialVersionUID = -7548505095303313237L;

	private static final Logger logger = LoggerFactory.getLogger(HystrixMetricsStreamServlet.class);

	private static AtomicInteger concurrentConnections = new AtomicInteger(0);
	private static DynamicIntProperty maxConcurrentConnections = DynamicPropertyFactory.getInstance().getIntProperty(
			"hystrix.stream.maxConcurrentConnections", 5);

	private static volatile AtomicBoolean isDestroyed = new AtomicBoolean();

	public static void shutdown()
	{
		isDestroyed.set(true);
	}

	@Override
	public void init() throws ServletException
	{
		isDestroyed.set(false);
	}

	/**
	 * Handle incoming GETs
	 */
	@Override
	protected synchronized void doGet(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException
	{
		if (isDestroyed.get())
		{
			response.sendError(503, "Service has been shut down.");
		}
		else
		{
			handleRequest(request, response);
		}
	}

	/**
	 * Handle servlet being undeployed by gracefully releasing connections so poller threads stop.
	 */
	@Override
	public void destroy()
	{
		/* set marker so the loops can break out */
		isDestroyed.set(true);
		super.destroy();
	}

	/**
	 * - maintain an open connection with the client - on initial connection send latest data of each requested event
	 * type - subsequently send all changes for each requested event type
	 *
	 * @param request
	 * @param response
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	private void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException
	{
		/* ensure we aren't allowing more connections than we want */
		final int numberConnections = concurrentConnections.incrementAndGet();
		HystrixMetricsPoller poller = null;
		try
		{
			if (numberConnections > maxConcurrentConnections.get())
			{
				response.sendError(503, "MaxConcurrentConnections reached: " + maxConcurrentConnections.get());
			}
			else
			{

				int delay = 500;
				try
				{
					final String d = request.getParameter("delay");
					if (d != null)
					{
						delay = Math.max(Integer.parseInt(d), 1);
					}
				}
				catch (final NumberFormatException e)
				{
					// ignore if it's not a number
				}

				/* initialize response */
				response.setHeader("Content-Type", "text/event-stream;charset=UTF-8");
				response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
				response.setHeader("Pragma", "no-cache");

				final MetricJsonListener jsonListener = new MetricJsonListener();
				poller = new HystrixMetricsPoller(jsonListener, delay);
				// start polling and it will write directly to the output stream
				poller.start();
				logger.info("Starting poller");

				// we will use a "single-writer" approach where the Servlet
				// thread does all the writing
				// by fetching JSON messages from the MetricJsonListener to
				// write them to the output
				try
				{
					while (poller.isRunning() && !isDestroyed.get())
					{
						final List<String> jsonMessages = jsonListener.getJsonMetrics();
						if (jsonMessages.isEmpty())
						{
							// https://github.com/Netflix/Hystrix/issues/85
							// hystrix.stream holds connection open if no
							// metrics
							// we send a ping to test the connection so that
							// we'll get an IOException if the client has
							// disconnected
							response.getWriter().println("ping: \n");
						}
						else
						{
							for (final String json : jsonMessages)
							{
								response.getWriter().println("data: " + json + "\n");
							}
						}

						/*
						 * shortcut breaking out of loop if we have been destroyed
						 */
						if (isDestroyed.get())
						{
							break;
						}

						// after outputting all the messages we will flush the
						// stream
						response.flushBuffer();

						// explicitly check for client disconnect - PrintWriter
						// does not throw exceptions
						if (response.getWriter().checkError())
						{
							throw new IOException("io error");
						}

						// now wait the 'delay' time
						Thread.sleep(delay);
					}
				}
				catch (final InterruptedException e)
				{
					poller.shutdown();
					logger.debug("InterruptedException. Will stop polling.");
					Thread.currentThread().interrupt();
				}
				catch (final IOException e)
				{
					poller.shutdown();
					// debug instead of error as we expect to get these whenever
					// a client disconnects or network issue occurs
					logger.debug("IOException while trying to write (generally caused by client disconnecting). Will stop polling.", e);
				}
				catch (final Exception e) // NOSONAR
				{
					poller.shutdown();
					logger.error("Failed to write. Will stop polling.", e);
				}
				logger.debug("Stopping Turbine stream to connection");
			}
		}
		catch (final Exception e) // NOSONAR
		{
			logger.error("Error initializing servlet for metrics event stream.", e);
		}
		finally
		{
			concurrentConnections.decrementAndGet();
			if (poller != null)
			{
				poller.shutdown();
			}
		}
	}

	/**
	 * This will be called from another thread so needs to be thread-safe.
	 *
	 * @ThreadSafe
	 */
	private static class MetricJsonListener implements HystrixMetricsPoller.MetricsAsJsonPollerListener
	{

		/**
		 * Setting limit to 1000. In a healthy system there isn't any reason to hit this limit so if we do it will throw
		 * an exception which causes the poller to stop.
		 * <p>
		 * This is a safety check against a runaway poller causing memory leaks.
		 */
		private final LinkedBlockingQueue<String> jsonMetrics = new LinkedBlockingQueue<String>(1000);

		/**
		 * Store JSON messages in a queue.
		 */
		@Override
		public void handleJsonMetric(final String json)
		{
			jsonMetrics.add(json);
		}

		/**
		 * Get all JSON messages in the queue.
		 *
		 * @return
		 */
		public List<String> getJsonMetrics()
		{
			final ArrayList<String> metrics = new ArrayList<String>();
			jsonMetrics.drainTo(metrics);
			return metrics;
		}
	}
}
