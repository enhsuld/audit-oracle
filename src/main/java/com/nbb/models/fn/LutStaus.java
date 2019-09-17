package com.nbb.models.fn;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;



/**
 * The persistent class for the lut_staus database table.
 * 
 */
@Entity
@Table(name="lut_staus")
@NamedQuery(name="LutStaus.findAll", query="SELECT l FROM LutStaus l")
public class LutStaus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LS_SEQ")
    @SequenceGenerator(sequenceName = "lut_staus_seq", allocationSize = 1, name = "LS_SEQ")
	private long id;
	
	@Column(name = "create_date")
	private String createDate;

	private String filename;
	
	private String savedname;

	private String fileurl;

	private int userid;

	public LutStaus() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}



	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getSavedname() {
		return savedname;
	}

	public void setSavedname(String savedname) {
		this.savedname = savedname;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFileurl() {
		return this.fileurl;
	}

	public void setFileurl(String fileurl) {
		this.fileurl = fileurl;
	}

	public int getUserid() {
		return this.userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

}