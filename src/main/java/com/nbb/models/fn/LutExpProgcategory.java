package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;


/**
 * The persistent class for the LUT_EXP_PROGCATEGORY database table.
 * 
 */
@Entity
@Table(name="lut_exp_progcategory")
@NamedQuery(name="LutExpProgcategory.findAll", query="SELECT l FROM LutExpProgcategory l")
public class LutExpProgcategory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LEP_SEQ")
    @SequenceGenerator(sequenceName = "lut_exp_progcategory_seq", allocationSize = 1, name = "LEP_SEQ")
	private long id;

	private String progname;

	//bi-directional many-to-one association to SubAuditOrganization
	@OneToMany(mappedBy="lutExpProgcategory")
	@JsonBackReference
	private List<SubAuditOrganization> subAuditOrganizations;

	public LutExpProgcategory() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getProgname() {
		return this.progname;
	}

	public void setProgname(String progname) {
		this.progname = progname;
	}

	public List<SubAuditOrganization> getSubAuditOrganizations() {
		return subAuditOrganizations;
	}

	public void setSubAuditOrganizations(List<SubAuditOrganization> subAuditOrganizations) {
		this.subAuditOrganizations = subAuditOrganizations;
	}

}