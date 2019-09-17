package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nbb.models.bs.FinAbw;
import com.nbb.models.bs.FinAsset;
import com.nbb.models.bs.FinBudget;
import com.nbb.models.bs.FinCbw;
import com.nbb.models.bs.FinCt1a;
import com.nbb.models.bs.FinCt2a;
import com.nbb.models.bs.FinCt3a;
import com.nbb.models.bs.FinCt4a;
import com.nbb.models.bs.FinCtt1;
import com.nbb.models.bs.FinCtt2;
import com.nbb.models.bs.FinCtt3;
import com.nbb.models.bs.FinCtt4;
import com.nbb.models.bs.FinCtt5;
import com.nbb.models.bs.FinCtt6;
import com.nbb.models.bs.FinCtt7;
import com.nbb.models.bs.FinCtt8;
import com.nbb.models.bs.FinCtt9;
import com.nbb.models.bs.FinInventory;
import com.nbb.models.bs.FinJournal;
import com.nbb.models.bs.FinNt2;
import com.nbb.models.bs.FinTgt1;
import com.nbb.models.bs.FinTgt1a;
import com.nbb.models.bs.FinTrialBalance;

import java.util.List;


/**
 * The persistent class for the MAIN_AUDIT_REGISTRATION database table.
 * 
 */
@Entity
@Table(name="main_audit_registration")
@NamedQuery(name="MainAuditRegistration.findAll", query="SELECT m FROM MainAuditRegistration m")
public class MainAuditRegistration implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MAR_SEQ")
    @SequenceGenerator(sequenceName = "main_audit_registration_seq", allocationSize = 1, name = "MAR_SEQ")
	private long id;

	private String auditors;

	private int audityear;

	private int autype;

	private String checkers;

	private long depid;

	private String enddate;

	private String gencode;

	private boolean isactive;
	
	private boolean isenabled;
	
	private boolean isreport;
	
	private long orgid;
	

	private String matter;
	private String orgcode;
	
	private String excelurlplan;
	
	private String excelurlprocess;

	private String orgname;
	private String dpos;
	private String director;
	private String manager;
	private String chpos;
	private String chname;
	private String apos;
	private String aname;
	
	private int regnum;

	private long orgtype;
	
	private int payroll;

	private String startdate;

	private long stepid;
	
	private long aper;
	
	private long mper;
	
	private long tper;
	
	private long a2per;
	
	private long m2per;
	
	private long t2per;
	
	private long a3per;
	
	private long m3per;
	
	private long t3per;
	
	private Long reporttype;

	private String terguuleh;
	
	private String auditname;
	
	@Transient
	private String data1;
	@Transient
	private String data2;
	@Transient
	private String data3;
	@Transient
	private String data4;
	@Transient
	private String data5;
	@Transient
	private String data6;
	@Transient
	private String data7;
	@Transient
	private String data8;
	
	//bi-directional many-to-one association to LnkMainUser
	@OneToMany(mappedBy="mainAuditRegistration")
	@JsonBackReference
	private List<LnkAuditProblem> lnkAuditProblem;
	
	//bi-directional many-to-one association to LnkMainUser
	@OneToMany(mappedBy="mainAuditRegistration")
	//@JsonManagedReference
	@JsonBackReference
	private List<LnkAuditForm> lnkAuditForm;
	
	//bi-directional many-to-one association to LnkMainUser
	@OneToMany(mappedBy="mainAuditRegistration")
	@JsonBackReference
	private List<LnkAuditFile> lnkAuditFile;
	
	//bi-directional many-to-one association to LnkMainUser
	@OneToMany(mappedBy="mainAuditRegistration")
	@JsonBackReference
	private List<LnkAuditReport> lnkAuditReport;
	
	/*@OneToMany(mappedBy="mainAuditRegistration")
	@JsonBackReference
	private List<FinJournal> finJournal;*/
	
	//bi-directional many-to-one association to LnkMainUser
	@OneToMany(mappedBy="mainAuditRegistration")
	@JsonManagedReference
	private List<LnkMainUser> lnkMainUsers;
	
	@ManyToOne
	@JoinColumn(name="depid" ,insertable=false, updatable=false)
	@JsonBackReference
	private LutDepartment lutDepartment;
	


	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinAbw> finAbw;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinAsset> finAsset;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinBudget> finBudget;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCbw> finCbw;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCt1a> finCt1a;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCt2a> finCt2a;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCt3a> finCt3a;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCt4a> finCt4a;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt1> finCtt1;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt2> finCtt2;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt3> finCtt3;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt4> finCtt4;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt5> finCtt5;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt6> finCtt6;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt7> finCtt7;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt8> finCtt8;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinCtt9> finCtt9;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinInventory> finInventory;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinJournal> finJournal;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinNt2> finNt2;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinTgt1> finTgt1;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinTgt1a> finTgt1a;
	
	@OneToMany(mappedBy="mainAuditRegistration",cascade = CascadeType.REMOVE,orphanRemoval = true)
	@JsonBackReference
	private List<FinTrialBalance> finTrialBalance;
	

	
	
	public MainAuditRegistration() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getExcelurlplan() {
		return excelurlplan;
	}

	public void setExcelurlplan(String excelurlplan) {
		this.excelurlplan = excelurlplan;
	}

	public String getExcelurlprocess() {
		return excelurlprocess;
	}

	public void setExcelurlprocess(String excelurlprocess) {
		this.excelurlprocess = excelurlprocess;
	}

	public String getAuditors() {
		return this.auditors;
	}

	public void setAuditors(String auditors) {
		this.auditors = auditors;
	}

	public int getAudityear() {
		return this.audityear;
	}

	public void setAudityear(int audityear) {
		this.audityear = audityear;
	}
			
	public List<LnkAuditReport> getLnkAuditReport() {
		return lnkAuditReport;
	}

	public void setLnkAuditReport(List<LnkAuditReport> lnkAuditReport) {
		this.lnkAuditReport = lnkAuditReport;
	}

	public List<LnkAuditFile> getLnkAuditFile() {
		return lnkAuditFile;
	}

	public void setLnkAuditFile(List<LnkAuditFile> lnkAuditFile) {
		this.lnkAuditFile = lnkAuditFile;
	}

	public int getPayroll() {
		return payroll;
	}

	public void setPayroll(int payroll) {
		this.payroll = payroll;
	}

	public int getRegnum() {
		return regnum;
	}

	public void setRegnum(int regnum) {
		this.regnum = regnum;
	}
	
	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getChpos() {
		return chpos;
	}

	public void setChpos(String chpos) {
		this.chpos = chpos;
	}

	public String getChname() {
		return chname;
	}

	public void setChname(String chname) {
		this.chname = chname;
	}

	public String getDpos() {
		return dpos;
	}

	public void setDpos(String dpos) {
		this.dpos = dpos;
	}

	public String getApos() {
		return apos;
	}

	public void setApos(String apos) {
		this.apos = apos;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

	public List<LnkMainUser> getLnkMainUsers() {
		return lnkMainUsers;
	}

	public void setLnkMainUsers(List<LnkMainUser> lnkMainUsers) {
		this.lnkMainUsers = lnkMainUsers;
	}

	public int getAutype() {
		return this.autype;
	}

	public void setAutype(int autype) {
		this.autype = autype;
	}

	public String getCheckers() {
		return this.checkers;
	}

	public void setCheckers(String checkers) {
		this.checkers = checkers;
	}

	public long getDepid() {
		return this.depid;
	}

	public void setDepid(long depid) {
		this.depid = depid;
	}

	public String getEnddate() {
		return this.enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getGencode() {
		return this.gencode;
	}

	public void setGencode(String gencode) {
		this.gencode = gencode;
	}

	public boolean getIsactive() {
		return this.isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
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

	public long getOrgtype() {
		return this.orgtype;
	}

	public void setOrgtype(long orgtype) {
		this.orgtype = orgtype;
	}

	public String getStartdate() {
		return this.startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public long getStepid() {
		return this.stepid;
	}

	public void setStepid(long stepid) {
		this.stepid = stepid;
	}

	public String getTerguuleh() {
		return this.terguuleh;
	}

	public void setTerguuleh(String terguuleh) {
		this.terguuleh = terguuleh;
	}
	
	
	public long getAper() {
		return aper;
	}

	public void setAper(long aper) {
		this.aper = aper;
	}

	public long getMper() {
		return mper;
	}

	public void setMper(long mper) {
		this.mper = mper;
	}

	public long getTper() {
		return tper;
	}

	public void setTper(long tper) {
		this.tper = tper;
	}
	
	public long getA2per() {
		return a2per;
	}

	public void setA2per(long a2per) {
		this.a2per = a2per;
	}

	public long getM2per() {
		return m2per;
	}

	public void setM2per(long m2per) {
		this.m2per = m2per;
	}

	public long getT2per() {
		return t2per;
	}

	public void setT2per(long t2per) {
		this.t2per = t2per;
	}

	public long getA3per() {
		return a3per;
	}

	public void setA3per(long a3per) {
		this.a3per = a3per;
	}

	public long getM3per() {
		return m3per;
	}

	public void setM3per(long m3per) {
		this.m3per = m3per;
	}

	public long getT3per() {
		return t3per;
	}

	public void setT3per(long t3per) {
		this.t3per = t3per;
	}
	

	public boolean isIsenabled() {
		return isenabled;
	}

	public void setIsenabled(boolean isenabled) {
		this.isenabled = isenabled;
	}
	
	public long getOrgid() {
		return orgid;
	}

	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}
	
	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	public String getData5() {
		return data5;
	}

	public void setData5(String data5) {
		this.data5 = data5;
	}

	public String getData6() {
		return data6;
	}

	public void setData6(String data6) {
		this.data6 = data6;
	}

	public String getData7() {
		return data7;
	}

	public void setData7(String data7) {
		this.data7 = data7;
	}

	public String getData8() {
		return data8;
	}

	public void setData8(String data8) {
		this.data8 = data8;
	}

	public Long getReporttype() {
		return reporttype;
	}

	public void setReporttype(Long reporttype) {
		this.reporttype = reporttype;
	}

	public String getAuditname() {
		return auditname;
	}

	public void setAuditname(String auditname) {
		this.auditname = auditname;
	}

	public LutDepartment getLutDepartment() {
		return lutDepartment;
	}

	public void setLutDepartment(LutDepartment lutDepartment) {
		this.lutDepartment = lutDepartment;
	}

	public List<LnkAuditForm> getLnkAuditForm() {
		return lnkAuditForm;
	}

	public void setLnkAuditForm(List<LnkAuditForm> lnkAuditForm) {
		this.lnkAuditForm = lnkAuditForm;
	}

	public List<FinJournal> getFinJournal() {
		return finJournal;
	}

	public void setFinJournal(List<FinJournal> finJournal) {
		this.finJournal = finJournal;
	}

	public List<FinAbw> getFinAbw() {
		return finAbw;
	}

	public void setFinAbw(List<FinAbw> finAbw) {
		this.finAbw = finAbw;
	}

	public List<FinAsset> getFinAsset() {
		return finAsset;
	}

	public void setFinAsset(List<FinAsset> finAsset) {
		this.finAsset = finAsset;
	}

	public List<FinBudget> getFinBudget() {
		return finBudget;
	}

	public void setFinBudget(List<FinBudget> finBudget) {
		this.finBudget = finBudget;
	}

	public List<FinCbw> getFinCbw() {
		return finCbw;
	}

	public void setFinCbw(List<FinCbw> finCbw) {
		this.finCbw = finCbw;
	}

	public List<FinCt1a> getFinCt1a() {
		return finCt1a;
	}

	public void setFinCt1a(List<FinCt1a> finCt1a) {
		this.finCt1a = finCt1a;
	}

	public List<FinCt2a> getFinCt2a() {
		return finCt2a;
	}

	public void setFinCt2a(List<FinCt2a> finCt2a) {
		this.finCt2a = finCt2a;
	}

	public List<FinCt3a> getFinCt3a() {
		return finCt3a;
	}

	public void setFinCt3a(List<FinCt3a> finCt3a) {
		this.finCt3a = finCt3a;
	}

	public List<FinCt4a> getFinCt4a() {
		return finCt4a;
	}

	public void setFinCt4a(List<FinCt4a> finCt4a) {
		this.finCt4a = finCt4a;
	}

	public List<FinCtt1> getFinCtt1() {
		return finCtt1;
	}

	public void setFinCtt1(List<FinCtt1> finCtt1) {
		this.finCtt1 = finCtt1;
	}

	public List<FinCtt2> getFinCtt2() {
		return finCtt2;
	}

	public void setFinCtt2(List<FinCtt2> finCtt2) {
		this.finCtt2 = finCtt2;
	}

	public List<FinCtt3> getFinCtt3() {
		return finCtt3;
	}

	public void setFinCtt3(List<FinCtt3> finCtt3) {
		this.finCtt3 = finCtt3;
	}

	public List<FinCtt4> getFinCtt4() {
		return finCtt4;
	}

	public void setFinCtt4(List<FinCtt4> finCtt4) {
		this.finCtt4 = finCtt4;
	}

	public List<FinCtt5> getFinCtt5() {
		return finCtt5;
	}

	public void setFinCtt5(List<FinCtt5> finCtt5) {
		this.finCtt5 = finCtt5;
	}

	public List<FinCtt6> getFinCtt6() {
		return finCtt6;
	}

	public void setFinCtt6(List<FinCtt6> finCtt6) {
		this.finCtt6 = finCtt6;
	}

	public List<FinCtt7> getFinCtt7() {
		return finCtt7;
	}

	public void setFinCtt7(List<FinCtt7> finCtt7) {
		this.finCtt7 = finCtt7;
	}

	public List<FinCtt8> getFinCtt8() {
		return finCtt8;
	}

	public void setFinCtt8(List<FinCtt8> finCtt8) {
		this.finCtt8 = finCtt8;
	}

	public List<FinCtt9> getFinCtt9() {
		return finCtt9;
	}

	public void setFinCtt9(List<FinCtt9> finCtt9) {
		this.finCtt9 = finCtt9;
	}

	public List<FinInventory> getFinInventory() {
		return finInventory;
	}

	public void setFinInventory(List<FinInventory> finInventory) {
		this.finInventory = finInventory;
	}

	public List<FinNt2> getFinNt2() {
		return finNt2;
	}

	public void setFinNt2(List<FinNt2> finNt2) {
		this.finNt2 = finNt2;
	}

	public List<FinTgt1> getFinTgt1() {
		return finTgt1;
	}

	public void setFinTgt1(List<FinTgt1> finTgt1) {
		this.finTgt1 = finTgt1;
	}

	public List<FinTgt1a> getFinTgt1a() {
		return finTgt1a;
	}

	public void setFinTgt1a(List<FinTgt1a> finTgt1a) {
		this.finTgt1a = finTgt1a;
	}

	public List<FinTrialBalance> getFinTrialBalance() {
		return finTrialBalance;
	}

	public void setFinTrialBalance(List<FinTrialBalance> finTrialBalance) {
		this.finTrialBalance = finTrialBalance;
	}

	public boolean isIsreport() {
		return isreport;
	}

	public void setIsreport(boolean isreport) {
		this.isreport = isreport;
	}

	public String getMatter() {
		return matter;
	}

	public void setMatter(String matter) {
		this.matter = matter;
	}
	
		
}