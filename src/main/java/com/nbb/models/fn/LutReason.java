package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the LUT_REASON database table.
 * 
 */
@Entity
@Table(name="lut_reason")
@NamedQuery(name="LutReason.findAll", query="SELECT l FROM LutReason l")
public class LutReason implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LR_SEQ")
    @SequenceGenerator(sequenceName = "lut_reason_seq", allocationSize = 1, name = "LR_SEQ")
	private long id;

	private String name;

	public LutReason() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}