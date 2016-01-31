package skyrim.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Entreprise implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String nom;
	private String typePrestation;
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreation;
	
	@OneToMany(mappedBy="entreprise", fetch=FetchType.LAZY)
	@Column(name="idEntreprise")
	private List<EntrepriseReport> reports;
	
	public List<EntrepriseReport> getReports() {
		return reports;
	}
	public void setReports(List<EntrepriseReport> reports) {
		this.reports = reports;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getTypePrestation() {
		return typePrestation;
	}
	public void setTypePrestation(String typePrestation) {
		this.typePrestation = typePrestation;
	}
	public Date getDateCreation() {
		return dateCreation;
	}
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	
	
}
