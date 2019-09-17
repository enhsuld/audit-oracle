package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nbb.models.LutUser;

import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the LUT_DEPARTMENTS database table.
 * 
 */
@Entity
@Table(name="lut_departments")
@NamedQuery(name="LutDepartment.findAll", query="SELECT l FROM LutDepartment l")
public class LutDepartment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEP_SEQ")
    @SequenceGenerator(sequenceName = "lut_departments_seq", allocationSize = 1, name = "DEP_SEQ")
	private long id;

	private String departmentname;

	private String email;

	private boolean isactive;

	private String phone;

	private String shortname;

	private String web;
	
	private String address;
	
	private long reg;

	private String licnum;
	
	private String licexpiredate;
	
	private long isstate;
	
	private long ismultiple;
	
	private long parentid;
	
	private long plan;
	
	private long autype;
	
	@Column(name="audit_count")
	private long auditCount;
	
	//bi-directional many-to-one association to LutUser
	@OneToMany(mappedBy="lutDepartment")
	@JsonBackReference
	private List<LnkDepartmentPlan> lnkDepartmentPlan;

	//bi-directional many-to-one association to LutUser
	@OneToMany(mappedBy="lutDepartment")
	@JsonBackReference
	private List<LutUser> lutUsers;
	
	@OneToMany(mappedBy="lutDepartment")
	@JsonBackReference
	private List<MainAuditRegistration> mainAuditRegistrations;

	//bi-directional many-to-one association to SubAuditOrganization
	@OneToMany(mappedBy="lutDepartment")
	@JsonBackReference
	private List<SubAuditOrganization> subAuditOrganizations;

	public LutDepartment() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDepartmentname() {
		return this.departmentname;
	}

	public void setDepartmentname(String departmentname) {
		this.departmentname = departmentname;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getIsactive() {
		return this.isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShortname() {
		return this.shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getWeb() {
		return this.web;
	}

	public void setWeb(String web) {
		this.web = web;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public long getReg() {
		return reg;
	}

	public void setReg(long reg) {
		this.reg = reg;
	}

	public String getLicnum() {
		return licnum;
	}

	public void setLicnum(String licnum) {
		this.licnum = licnum;
	}

	public String getLicexpiredate() {
		return licexpiredate;
	}

	public void setLicexpiredate(String licexpiredate) {
		this.licexpiredate = licexpiredate;
	}

	public long getIsstate() {
		return isstate;
	}

	public void setIsstate(long isstate) {
		this.isstate = isstate;
	}

	public long getIsmultiple() {
		return ismultiple;
	}

	public void setIsmultiple(long ismultiple) {
		this.ismultiple = ismultiple;
	}

	public List<LutUser> getLutUsers() {
		return this.lutUsers;
	}

	public void setLutUsers(List<LutUser> lutUsers) {
		this.lutUsers = lutUsers;
	}

	public List<LnkDepartmentPlan> getLnkDepartmentPlan() {
		return lnkDepartmentPlan;
	}

	public void setLnkDepartmentPlan(List<LnkDepartmentPlan> lnkDepartmentPlan) {
		this.lnkDepartmentPlan = lnkDepartmentPlan;
	}

	public long getParentid() {
		return parentid;
	}

	public void setParentid(long parentid) {
		this.parentid = parentid;
	}
	
	public long getAutype() {
		return autype;
	}

	public void setAutype(long autype) {
		this.autype = autype;
	}

	public long getPlan() {
		return plan;
	}

	public void setPlan(long plan) {
		this.plan = plan;
	}

	public long getAuditCount() {
		return auditCount;
	}

	public void setAuditCount(long auditCount) {
		this.auditCount = auditCount;
	}

	public List<SubAuditOrganization> getSubAuditOrganizations() {
		return this.subAuditOrganizations;
	}

	public void setSubAuditOrganizations(List<SubAuditOrganization> subAuditOrganizations) {
		this.subAuditOrganizations = subAuditOrganizations;
	}

	public SubAuditOrganization addSubAuditOrganization(SubAuditOrganization subAuditOrganization) {
		getSubAuditOrganizations().add(subAuditOrganization);
		subAuditOrganization.setLutDepartment(this);

		return subAuditOrganization;
	}

	public SubAuditOrganization removeSubAuditOrganization(SubAuditOrganization subAuditOrganization) {
		getSubAuditOrganizations().remove(subAuditOrganization);
		subAuditOrganization.setLutDepartment(null);

		return subAuditOrganization;
	}

	public List<MainAuditRegistration> getMainAuditRegistrations() {
		return mainAuditRegistrations;
	}

	public void setMainAuditRegistrations(List<MainAuditRegistration> mainAuditRegistrations) {
		this.mainAuditRegistrations = mainAuditRegistrations;
	}
	
}