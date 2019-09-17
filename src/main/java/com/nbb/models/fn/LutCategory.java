package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;


/**
 * The persistent class for the LUT_CATEGORY database table.
 * 
 */
@Entity
@Table(name="lut_category")
@NamedQuery(name="LutCategory.findAll", query="SELECT l FROM LutCategory l")
public class LutCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LC_SEQ")
    @SequenceGenerator(sequenceName = "lut_category_seq", allocationSize = 1, name = "LC_SEQ")
	private long id;

	private String categoryname;

	//bi-directional many-to-one association to SubAuditOrganization
	@OneToMany(mappedBy="lutCategory")
	@JsonBackReference
	private List<SubAuditOrganization> subAuditOrganizations;
	
	

	public LutCategory() {
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCategoryname() {
		return this.categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public List<SubAuditOrganization> getSubAuditOrganizations() {
		return this.subAuditOrganizations;
	}

	public void setSubAuditOrganizations(List<SubAuditOrganization> subAuditOrganizations) {
		this.subAuditOrganizations = subAuditOrganizations;
	}
}