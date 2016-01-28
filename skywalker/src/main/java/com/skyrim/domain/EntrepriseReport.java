package skyrim.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class EntrepriseReport {
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
