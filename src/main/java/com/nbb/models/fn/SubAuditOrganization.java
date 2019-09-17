package com.nbb.models.fn;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;



/**
 * The persistent class for the SUB_AUDIT_ORGANIZATIONS database table.
 * 
 */
@Entity
@Table(name="sub_audit_organizations")
@NamedQuery(name="SubAuditOrganization.findAll", query="SELECT s FROM SubAuditOrganization s")
public class SubAuditOrganization implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SAO_SEQ")
    @SequenceGenerator(sequenceName = "sub_audit_organizations_seq", allocationSize = 1, name = "SAO_SEQ")
	private long id;

	private String accemail;

	private String accfullname;

	private String accphone;

	private String accprof;

	private String accwyear;
	
	private String accsurname;
	
	private String accpos;
	
	private String headsurname;
	
	private String headpos;
	
	private String headwyear;
	
	private long auditresult1;
	
	private long auditresult2;
	
	private long auditresult3;
	
	private String plan1;
	
	private String plan2;
	
	private String plan3;
	
	private String report1;
	
	private String report2;
	
	private String report3;
	
	private String headwnum;
	
	private String comwnum;
	
	private String serwnum;
	
	private String conwnum;
	
	private String otherwnum;
	
	private String statedir;
	
	private String owndir;

	private String address;

	private String aimagname;

	private String banks;

	private String createdate;

	private String email;

	private String fax;

	private String fsorg;

	private String heademail;

	private String headfullname;

	private String headorder;

	private String headphone;

	private String headprof;

	private String headreg;

	private String ndorg;


	private String orgcode;

	private String orgname;

	private String parentid;

	private String phone;

	private long regid;

	private String soumname;

	private String statebanks;

	private String stateregid;

	private String taxorg;

	private String web;
	
	private long departmentid;
	
	private long categoryid;
	
	private long fincategoryid;
	
	private long progid;


	//bi-directional many-to-one association to LutCategory
	@ManyToOne
	@JoinColumn(name="categoryid", insertable=false, updatable=false)
	@JsonManagedReference
	private LutCategory lutCategory;

	//bi-directional many-to-one association to LutDepartment
	@ManyToOne
	@JoinColumn(name="departmentid" ,insertable=false, updatable=false)
	@JsonManagedReference
	private LutDepartment lutDepartment;
	
	@ManyToOne
	@JoinColumn(name="progid" ,insertable=false, updatable=false)
	@JsonManagedReference
	private LutExpProgcategory lutExpProgcategory;


	//bi-directional many-to-one association to LutFincategory
	@ManyToOne
	@JoinColumn(name="fincategoryid",nullable = false,insertable=false,updatable=false)
	@JsonManagedReference
	private LutFincategory lutFincategory;
	
	//bi-directional many-to-one association to LnkOrgyplanreport
	@OneToMany(mappedBy="subAuditOrganization")
	@JsonBackReference
	private List<LnkOrgyplanreport> lnkOrgyplanreports;

	public SubAuditOrganization() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(long categoryid) {
		this.categoryid = categoryid;
	}

	public long getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(long departmentid) {
		this.departmentid = departmentid;
	}

	public String getAccemail() {
		return this.accemail;
	}

	public void setAccemail(String accemail) {
		this.accemail = accemail;
	}

	public String getAccfullname() {
		return this.accfullname;
	}

	public void setAccfullname(String accfullname) {
		this.accfullname = accfullname;
	}

	public String getAccphone() {
		return this.accphone;
	}

	public void setAccphone(String accphone) {
		this.accphone = accphone;
	}

	public String getAccprof() {
		return this.accprof;
	}

	public void setAccprof(String accprof) {
		this.accprof = accprof;
	}

	public String getAccwyear() {
		return this.accwyear;
	}

	public void setAccwyear(String accwyear) {
		this.accwyear = accwyear;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAimagname() {
		return this.aimagname;
	}

	public void setAimagname(String aimagname) {
		this.aimagname = aimagname;
	}

	public String getBanks() {
		return this.banks;
	}

	public void setBanks(String banks) {
		this.banks = banks;
	}

	public String getCreatedate() {
		return this.createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFsorg() {
		return this.fsorg;
	}

	public void setFsorg(String fsorg) {
		this.fsorg = fsorg;
	}

	public String getHeademail() {
		return this.heademail;
	}

	public void setHeademail(String heademail) {
		this.heademail = heademail;
	}

	public String getHeadfullname() {
		return this.headfullname;
	}

	public void setHeadfullname(String headfullname) {
		this.headfullname = headfullname;
	}

	public String getHeadorder() {
		return this.headorder;
	}

	public void setHeadorder(String headorder) {
		this.headorder = headorder;
	}

	public String getHeadphone() {
		return this.headphone;
	}

	public void setHeadphone(String headphone) {
		this.headphone = headphone;
	}

	public String getHeadprof() {
		return this.headprof;
	}

	public void setHeadprof(String headprof) {
		this.headprof = headprof;
	}

	public String getHeadreg() {
		return this.headreg;
	}

	public void setHeadreg(String headreg) {
		this.headreg = headreg;
	}

	public String getNdorg() {
		return this.ndorg;
	}

	public void setNdorg(String ndorg) {
		this.ndorg = ndorg;
	}

	public String getOrgcode() {

		return this.orgcode;
	}


	public void setOrgcode(String orgcode) {

		this.orgcode = orgcode;
	}

	public String getOrgname() {
		return this.orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}


	public String getParentid() {
		return this.parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getRegid() {
		return this.regid;
	}

	public void setRegid(long regid) {
		this.regid = regid;
	}

	public String getSoumname() {
		return this.soumname;
	}

	public void setSoumname(String soumname) {
		this.soumname = soumname;
	}

	public String getStatebanks() {
		return this.statebanks;
	}

	public void setStatebanks(String statebanks) {
		this.statebanks = statebanks;
	}


	public String getStateregid() {

		return this.stateregid;
	}

	public void setStateregid(String stateregid) {

		this.stateregid = stateregid;
	}

	public String getTaxorg() {
		return this.taxorg;
	}

	public void setTaxorg(String taxorg) {
		this.taxorg = taxorg;
	}

	public String getWeb() {
		return this.web;
	}	

	public long getFincategoryid() {
		return fincategoryid;
	}

	public void setFincategoryid(long fincategoryid) {
		this.fincategoryid = fincategoryid;
	}

	public long getProgid() {
		return progid;
	}

	public void setProgid(long progid) {
		this.progid = progid;
	}

	public void setWeb(String web) {
		this.web = web;
	}	

	public String getAccsurname() {
		return accsurname;
	}

	public void setAccsurname(String accsurname) {
		this.accsurname = accsurname;
	}

	public String getAccpos() {
		return accpos;
	}

	public void setAccpos(String accpos) {
		this.accpos = accpos;
	}

	public String getHeadpos() {
		return headpos;
	}

	public String getHeadsurname() {
		return headsurname;
	}

	public void setHeadsurname(String headsurname) {
		this.headsurname = headsurname;
	}

	public void setHeadpos(String headpos) {
		this.headpos = headpos;
	}

	public String getHeadwyear() {
		return headwyear;
	}

	public void setHeadwyear(String headwyear) {
		this.headwyear = headwyear;
	}

	public long getAuditresult1() {
		return auditresult1;
	}

	public void setAuditresult1(long auditresult1) {
		this.auditresult1 = auditresult1;
	}

	public long getAuditresult2() {
		return auditresult2;
	}

	public void setAuditresult2(long auditresult2) {
		this.auditresult2 = auditresult2;
	}

	public long getAuditresult3() {
		return auditresult3;
	}

	public void setAuditresult3(long auditresult3) {
		this.auditresult3 = auditresult3;
	}

	public String getPlan1() {
		return plan1;
	}

	public void setPlan1(String plan1) {
		this.plan1 = plan1;
	}

	public String getPlan2() {
		return plan2;
	}

	public void setPlan2(String plan2) {
		this.plan2 = plan2;
	}

	public String getPlan3() {
		return plan3;
	}

	public void setPlan3(String plan3) {
		this.plan3 = plan3;
	}

	public String getReport1() {
		return report1;
	}

	public void setReport1(String report1) {
		this.report1 = report1;
	}

	public String getReport2() {
		return report2;
	}

	public void setReport2(String report2) {
		this.report2 = report2;
	}

	public String getReport3() {
		return report3;
	}

	public void setReport3(String report3) {
		this.report3 = report3;
	}

	public String getHeadwnum() {
		return headwnum;
	}

	public void setHeadwnum(String headwnum) {
		this.headwnum = headwnum;
	}

	public String getComwnum() {
		return comwnum;
	}

	public void setComwnum(String comwnum) {
		this.comwnum = comwnum;
	}

	public String getSerwnum() {
		return serwnum;
	}

	public void setSerwnum(String serwnum) {
		this.serwnum = serwnum;
	}

	public String getConwnum() {
		return conwnum;
	}

	public void setConwnum(String conwnum) {
		this.conwnum = conwnum;
	}

	public String getOtherwnum() {
		return otherwnum;
	}

	public void setOtherwnum(String otherwnum) {
		this.otherwnum = otherwnum;
	}

	public String getStatedir() {
		return statedir;
	}

	public void setStatedir(String statedir) {
		this.statedir = statedir;
	}

	public String getOwndir() {
		return owndir;
	}

	public void setOwndir(String owndir) {
		this.owndir = owndir;
	}

	public LutCategory getLutCategory() {
		return this.lutCategory;
	}

	public void setLutCategory(LutCategory lutCategory) {
		this.lutCategory = lutCategory;
	}

	public LutDepartment getLutDepartment() {
		return this.lutDepartment;
	}

	public void setLutDepartment(LutDepartment lutDepartment) {
		this.lutDepartment = lutDepartment;
	}

	/*public LutExpProgcategory getLutExpProgcategory() {
		return this.lutExpProgcategory;
	}

	public void setLutExpProgcategory(LutExpProgcategory lutExpProgcategory) {
		this.lutExpProgcategory = lutExpProgcategory;
	}*/

	public LutFincategory getLutFincategory() {
		return this.lutFincategory;
	}

	public void setLutFincategory(LutFincategory lutFincategory) {
		this.lutFincategory = lutFincategory;
	}
	public List<LnkOrgyplanreport> getLnkOrgyplanreports() {
		return this.lnkOrgyplanreports;
	}

	public void setLnkOrgyplanreports(List<LnkOrgyplanreport> lnkOrgyplanreports) {
		this.lnkOrgyplanreports = lnkOrgyplanreports;
	}

	public LnkOrgyplanreport addLnkOrgyplanreport(LnkOrgyplanreport lnkOrgyplanreport) {
		getLnkOrgyplanreports().add(lnkOrgyplanreport);
		lnkOrgyplanreport.setSubAuditOrganization(this);

		return lnkOrgyplanreport;
	}

	public LnkOrgyplanreport removeLnkOrgyplanreport(LnkOrgyplanreport lnkOrgyplanreport) {
		getLnkOrgyplanreports().remove(lnkOrgyplanreport);
		lnkOrgyplanreport.setSubAuditOrganization(null);

		return lnkOrgyplanreport;
	}

}