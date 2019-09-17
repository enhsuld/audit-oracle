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
@Table(name="lnk_audit_files")
@NamedQuery(name="LnkAuditFile.findAll", query="SELECT l FROM LnkAuditFile l")
public class LnkAuditFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AFILES_SEQ")
    @SequenceGenerator(sequenceName = "lnk_audit_files_seq", allocationSize = 1, name = "AFILES_SEQ")
	private Long id;

	private String filename;
	

	private String fname;
	

	private long fsize;

	private String fileurl;
	
	private String mimetype;

	private String description;
	
	private String fcomment;
	
	private long appid;

	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="appid", insertable=false, updatable=false)
	@JsonBackReference
	private MainAuditRegistration mainAuditRegistration;

	public LnkAuditFile() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}



	public String getFilename() {
		return filename;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
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



	public String getDescription() {
		return description;
	}



	public void setDescription(String description) {
		this.description = description;
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

	public String getFcomment() {
		return fcomment;
	}

	public void setFcomment(String fcomment) {
		this.fcomment = fcomment;
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