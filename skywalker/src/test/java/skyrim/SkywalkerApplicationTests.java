package skyrim;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;

import skyrim.SkywalkerApplication;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SkywalkerApplication.class)
@WebAppConfiguration
public class SkywalkerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
