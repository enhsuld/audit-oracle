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
@Table(name="lnk_audit_forms")
@NamedQuery(name="LnkAuditForm.findAll", query="SELECT l FROM LnkAuditForm l")
public class LnkAuditForm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AFORMS_SEQ")
    @SequenceGenerator(sequenceName = "lnk_audit_forms_seq", allocationSize = 1, name = "AFORMS_SEQ")
	private Long id;

	private String data1;

	private String data2;

	private String data3;

	private int data4;

	private boolean data5;

	private int data6;

	private String data7;

	private String data8;
	
	private String data9;
	
	private boolean data10;
	
	private boolean data12;
	
	@Column(name="data13")
	private int data13;
	
	private boolean data14;
	
	private int stepid;
	
	private Long parentid;
	
	private long appid;
	
	private long formid;
	
	private int orderid;
	
	private int levelid;

	//bi-directional many-to-one association to LnkMainUser
	@OneToMany(mappedBy="lnkAuditForm")
	private List<LnkAuditFormFile> lnkAuditFormFile;
	
	//bi-directional many-to-one association to LnkMainUser
	@OneToMany(mappedBy="lnkAuditForm")
	private List<LnkAuditFormComment> lnkAuditFormComment;
	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="appid", insertable=false, updatable=false)
	@JsonBackReference
	private MainAuditRegistration mainAuditRegistration;

	public LnkAuditForm() {
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public long getFormid() {
		return formid;
	}

	public void setFormid(long formid) {
		this.formid = formid;
	}

	public Long getParentid() {
		return parentid;
	}
	public void setParentid(Long parentid) {
		this.parentid = parentid;
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

	public int getData4() {
		return data4;
	}

	public void setData4(int data4) {
		this.data4 = data4;
	}

	public boolean isData5() {
		return data5;
	}

	public void setData5(boolean data5) {
		this.data5 = data5;
	}

	public int getData6() {
		return data6;
	}

	public void setData6(int data6) {
		this.data6 = data6;
	}

	public String getData7() {
		return this.data7;
	}

	public void setData7(String data7) {
		this.data7 = data7;
	}

	public String getData8() {
		return this.data8;
	}

	public void setData8(String data8) {
		this.data8 = data8;
	}

	public String getData9() {
		return data9;
	}

	public void setData9(String data9) {
		this.data9 = data9;
	}
	
	public boolean isData10() {
		return data10;
	}

	public void setData10(boolean data10) {
		this.data10 = data10;
	}
	
	
	public boolean isData12() {
		return data12;
	}

	public void setData12(boolean data12) {
		this.data12 = data12;
	}
	
	public int getData13() {
		return data13;
	}

	public void setData13(int data13) {
		this.data13 = data13;
	}

	public boolean isData14() {
		return data14;
	}

	public void setData14(boolean data14) {
		this.data14 = data14;
	}
	
	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}
	
	public int getLevelid() {
		return levelid;
	}

	public void setLevelid(int levelid) {
		this.levelid = levelid;
	}

	public MainAuditRegistration getMainAuditRegistration() {
		return mainAuditRegistration;
	}

	public void setMainAuditRegistration(MainAuditRegistration mainAuditRegistration) {
		this.mainAuditRegistration = mainAuditRegistration;
	}

	public List<LnkAuditFormFile> getLnkAuditFormFile() {
		return lnkAuditFormFile;
	}

	public void setLnkAuditFormFile(List<LnkAuditFormFile> lnkAuditFormFile) {
		this.lnkAuditFormFile = lnkAuditFormFile;
	}

	public int getStepid() {
		return stepid;
	}

	public void setStepid(int stepid) {
		this.stepid = stepid;
	}

	public List<LnkAuditFormComment> getLnkAuditFormComment() {
		return lnkAuditFormComment;
	}

	public void setLnkAuditFormComment(List<LnkAuditFormComment> lnkAuditFormComment) {
		this.lnkAuditFormComment = lnkAuditFormComment;
	}
	
	
	
}