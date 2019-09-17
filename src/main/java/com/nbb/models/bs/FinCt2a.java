package com.nbb.models.bs;

import java.io.Serializable;
import javax.persistence.*;

import com.nbb.models.fn.MainAuditRegistration;


/**
 * The persistent class for the FIN_ST2A database table.
 * 
 */
@Entity
@Table(name="fin_ct2a")
@NamedQuery(name="FinCt2a.findAll", query="SELECT f FROM FinCt2a f")
public class FinCt2a implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CT2A_SEQ")
    @SequenceGenerator(sequenceName = "fin_ct2a_seq", allocationSize = 1, name = "CT2A_SEQ")
	private long id;

	private String data1;

	private String data2;

	private String data3;

	private String data4;

	//private Long formid;
	
	private long orgcatid;

	private String orgcode;
	
	private String cyear;

	private long planid;

	private long stepid;
	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="planid",nullable = false,insertable=false,updatable=false)
	private MainAuditRegistration mainAuditRegistration;

	public FinCt2a() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getData1() {
		return this.data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return this.data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return this.data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return this.data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	/*public long getFormid() {
		return this.formid;
	}

	public void setFormid(long formid) {
		this.formid = formid;
	}*/

	public long getOrgcatid() {
		return orgcatid;
	}

	public void setOrgcatid(long orgcatid) {
		this.orgcatid = orgcatid;
	}

	public long getPlanid() {
		return planid;
	}

	public void setPlanid(long planid) {
		this.planid = planid;
	}

	public long getStepid() {
		return stepid;
	}

	public void setStepid(long stepid) {
		this.stepid = stepid;
	}

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getCyear() {
		return cyear;
	}

	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	public MainAuditRegistration getMainAuditRegistration() {
		return this.mainAuditRegistration;
	}

	public void setMainAuditRegistration(MainAuditRegistration mainAuditRegistration) {
		this.mainAuditRegistration = mainAuditRegistration;
	}
}