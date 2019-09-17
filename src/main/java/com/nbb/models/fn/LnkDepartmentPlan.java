package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;


/**
 * The persistent class for the lut_forms database table.
 * 
 */
@Entity
@Table(name="lnk_department_plan")
@NamedQuery(name="LnkDepartmentPlan.findAll", query="SELECT l FROM LnkDepartmentPlan l")
public class LnkDepartmentPlan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LDP_SEQ")
    @SequenceGenerator(sequenceName = "lnk_department_plan_seq", allocationSize = 1, name = "LDP_SEQ")
	private Long id;
	@Column(name = "create_date")
	private String createDate;
	private long auditCount;
	private long orgid;
	private long planid;
	

	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="orgid", insertable=false, updatable=false)
	@JsonBackReference
	private LutDepartment lutDepartment;

	public LnkDepartmentPlan() {
	}

	public Long getId() {
		return id;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public long getAuditCount() {
		return auditCount;
	}

	public void setAuditCount(long auditCount) {
		this.auditCount = auditCount;
	}

	public long getOrgid() {
		return orgid;
	}

	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}

	public long getPlanid() {
		return planid;
	}

	public void setPlanid(long planid) {
		this.planid = planid;
	}

	public LutDepartment getLutDepartment() {
		return lutDepartment;
	}

	public void setLutDepartment(LutDepartment lutDepartment) {
		this.lutDepartment = lutDepartment;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}