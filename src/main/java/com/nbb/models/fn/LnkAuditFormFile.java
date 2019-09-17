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
@Table(name="lnk_audit_form_file")
@NamedQuery(name="LnkAuditFormFile.findAll", query="SELECT l FROM LnkAuditFormFile l")
public class LnkAuditFormFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AFF_SEQ")
    @SequenceGenerator(sequenceName = "lnk_audit_form_file_seq", allocationSize = 1, name = "AFF_SEQ")
	private Long id;
	private String filename;
	private String fname;
	private long fsize;
	private String fileurl;	
	@Column(name = "create_date")
	private String createDate;
	private long formid;

	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="formid", insertable=false, updatable=false)
	@JsonBackReference
	private LnkAuditForm lnkAuditForm;

	public LnkAuditFormFile() {
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

	public long getFormid() {
		return formid;
	}

	public void setFormid(long formid) {
		this.formid = formid;
	}

	public LnkAuditForm getLnkAuditForm() {
		return lnkAuditForm;
	}

	public void setLnkAuditForm(LnkAuditForm lnkAuditForm) {
		this.lnkAuditForm = lnkAuditForm;
	}
	
}