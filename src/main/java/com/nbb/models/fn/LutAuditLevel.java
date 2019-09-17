package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the LUT_AUDIT_LEVEL database table.
 * 
 */
@Entity
@Table(name="LUT_AUDIT_LEVEL")
@NamedQuery(name="LutAuditLevel.findAll", query="SELECT l FROM LutAuditLevel l")
public class LutAuditLevel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LAL_SEQ")
    @SequenceGenerator(sequenceName = "LUT_AUDIT_LEVEL_seq", allocationSize = 1, name = "LAL_SEQ")
	private long id;

	private String levelname;

	public LutAuditLevel() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLevelname() {
		return this.levelname;
	}

	public void setLevelname(String levelname) {
		this.levelname = levelname;
	}

}