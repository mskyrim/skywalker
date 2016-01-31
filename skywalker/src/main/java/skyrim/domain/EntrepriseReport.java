package skyrim.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class EntrepriseReport implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column()
	private Long id;
	private int responseDelay;
	private int efficiency;
	private int delayCompliance;
	private int budgetCompliance;
	private int deliveryQuality;
	private String comment;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Entreprise entreprise;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Engagement engagment;
	
	public Entreprise getEntreprise() {
		return entreprise;
	}
	public void setEntreprise(Entreprise entreprise) {
		this.entreprise = entreprise;
	}
	public Engagement getEngagment() {
		return engagment;
	}
	public void setEngagment(Engagement engagment) {
		this.engagment = engagment;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getResponseDelay() {
		return responseDelay;
	}
	public void setResponseDelay(int responseDelay) {
		this.responseDelay = responseDelay;
	}
	public int getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(int efficiency) {
		this.efficiency = efficiency;
	}
	public int getDelayCompliance() {
		return delayCompliance;
	}
	public void setDelayCompliance(int delayCompliance) {
		this.delayCompliance = delayCompliance;
	}
	public int getBudgetCompliance() {
		return budgetCompliance;
	}
	public void setBudgetCompliance(int budgetCompliance) {
		this.budgetCompliance = budgetCompliance;
	}
	public int getDeliveryQuality() {
		return deliveryQuality;
	}
	public void setDeliveryQuality(int deliveryQuality) {
		this.deliveryQuality = deliveryQuality;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
}
