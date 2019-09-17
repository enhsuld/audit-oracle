package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;


/**
 * The persistent class for the LUT_FINCATEGORY database table.
 * 
 */
@Entity
@Table(name="lut_fincategory")
@NamedQuery(name="LutFincategory.findAll", query="SELECT l FROM LutFincategory l")
public class LutFincategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LF_SEQ")
    @SequenceGenerator(sequenceName = "lut_fincategory_seq", allocationSize = 1, name = "LF_SEQ")
	private long id;

	private String fincategoryname;

	//bi-directional many-to-one association to SubAuditOrganization
	@OneToMany(mappedBy="lutFincategory")
	@JsonBackReference
	private List<SubAuditOrganization> subAuditOrganizations;

	public LutFincategory() {
	}
	
	public long getId() {
		return id;

	}
	public void setId(long id) {
		this.id = id;

	}
	public String getFincategoryname() {
		return this.fincategoryname;
	}

	public void setFincategoryname(String fincategoryname) {
		this.fincategoryname = fincategoryname;
	}

	public List<SubAuditOrganization> getSubAuditOrganizations() {
		return this.subAuditOrganizations;
	}

	public void setSubAuditOrganizations(List<SubAuditOrganization> subAuditOrganizations) {
		this.subAuditOrganizations = subAuditOrganizations;
	}

	public SubAuditOrganization addSubAuditOrganization(SubAuditOrganization subAuditOrganization) {
		getSubAuditOrganizations().add(subAuditOrganization);
		subAuditOrganization.setLutFincategory(this);

		return subAuditOrganization;
	}

	public SubAuditOrganization removeSubAuditOrganization(SubAuditOrganization subAuditOrganization) {
		getSubAuditOrganizations().remove(subAuditOrganization);
		subAuditOrganization.setLutFincategory(null);

		return subAuditOrganization;
	}

}