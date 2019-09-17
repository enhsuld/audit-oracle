package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nbb.models.LutUser;

import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the LUT_POSITION database table.
 * 
 */
@Entity
@Table(name="lut_position")
@NamedQuery(name="LutPosition.findAll", query="SELECT l FROM LutPosition l")
public class LutPosition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LP_SEQ")
    @SequenceGenerator(sequenceName = "lut_position_seq", allocationSize = 1, name = "LP_SEQ")
	private long id;

	private Boolean isactive;

	private long orderid;

	private String positionname;
	
	private Boolean isstate;
	
	//bi-directional many-to-one association to LutUser
/*	@OneToMany(mappedBy="lutPosition")
	@JsonBackReference
	private List<LutUser> lutUsers;*/


	public LutPosition() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean getIsactive() {
		return this.isactive;
	}

	public void setIsactive(Boolean isactive) {
		this.isactive = isactive;
	}

	public long getOrderid() {
		return this.orderid;
	}

	public void setOrderid(long orderid) {
		this.orderid = orderid;
	}

	public String getPositionname() {
		return this.positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public Boolean getIsstate() {
		return isstate;
	}

	public void setIsstate(Boolean isstate) {
		this.isstate = isstate;
	}

	/*public List<LutUser> getLutUsers() {
		return lutUsers;
	}

	public void setLutUsers(List<LutUser> lutUsers) {
		this.lutUsers = lutUsers;
	}*/
}