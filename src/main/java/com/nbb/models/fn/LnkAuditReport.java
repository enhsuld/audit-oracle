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
@Table(name="lnk_audit_reports")
@NamedQuery(name="LnkAuditReport.findAll", query="SELECT l FROM LnkAuditReport l")
public class LnkAuditReport implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AR_SEQ")
    @SequenceGenerator(sequenceName = "lnk_audit_reports_seq", allocationSize = 1, name = "AR_SEQ")
	private Long id;
	private String filename;
	private String fname;
	private long fsize;
	private int stepid=0;
	private String fileurl;	
	@Column(name = "create_date")
	private String createDate;
	private long appid;

	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="appid", insertable=false, updatable=false)
	@JsonBackReference
	private MainAuditRegistration mainAuditRegistration;

	public LnkAuditReport() {
	}

	public Long getId() {
		return id;
	}

	

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public long getFsize() {
		return fsize;
	}

	public void setFsize(long fsize) {
		this.fsize = fsize;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public int getStepid() {
		return stepid;
	}

	public void setStepid(int stepid) {
		this.stepid = stepid;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}



	public String getFileurl() {
		return fileurl;
	}



	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}

	public long getAppid() {
		return appid;
	}



	public void setAppid(long appid) {
		this.appid = appid;
	}



	public MainAuditRegistration getMainAuditRegistration() {
		return mainAuditRegistration;
	}

	public void setMainAuditRegistration(MainAuditRegistration mainAuditRegistration) {
		this.mainAuditRegistration = mainAuditRegistration;
	}
	
}