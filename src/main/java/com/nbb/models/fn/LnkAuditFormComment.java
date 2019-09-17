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
@Table(name="lnk_audit_form_comment")
@NamedQuery(name="LnkAuditFormComment.findAll", query="SELECT l FROM LnkAuditFormComment l")
public class LnkAuditFormComment implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMMENT_SEQ")
    @SequenceGenerator(sequenceName = "lnk_audit_form_comment_seq", allocationSize = 1, name = "COMMENT_SEQ")
	private Long id;
	private String comtext;
	private String username;	
	@Column(name = "create_date")
	private String createDate;
	private long formid;
	private long appid;
	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="appid", insertable=false, updatable=false)
	@JsonBackReference
	private LnkAuditForm lnkAuditForm;

	public LnkAuditFormComment() {
	}

	public Long getId() {
		return id;
	}
	
	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}

	public String getComtext() {
		return comtext;
	}

	public void setComtext(String comtext) {
		this.comtext = comtext;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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

	public LnkAuditForm getLnkAuditForm() {
		return lnkAuditForm;
	}

	public void setLnkAuditForm(LnkAuditForm lnkAuditForm) {
		this.lnkAuditForm = lnkAuditForm;
	}
	
}