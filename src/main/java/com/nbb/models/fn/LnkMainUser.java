package com.nbb.models.fn;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nbb.models.LutUser;


/**
 * The persistent class for the LNK_MAIN_USERS database table.
 * 
 */
@Entity
@Table(name="lnk_main_users")
@NamedQuery(name="LnkMainUser.findAll", query="SELECT l FROM LnkMainUser l")
public class LnkMainUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LMU_SEQ")
    @SequenceGenerator(sequenceName = "lnk_main_users_seq", allocationSize = 1, name = "LMU_SEQ")
	private long id;

	private long positionid;

	private long userid;
	
	private long appid;
	
	@ManyToOne
	@JsonBackReference
	@JoinColumn(name="userid", insertable=false, updatable=false)
	private LutUser lutUser;

	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="appid", insertable=false, updatable=false)
	@JsonBackReference
	private MainAuditRegistration mainAuditRegistration;

	public LnkMainUser() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}

	public long getPositionid() {
		return this.positionid;
	}

	public void setPositionid(long positionid) {
		this.positionid = positionid;
	}

	public long getUserid() {
		return this.userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public MainAuditRegistration getMainAuditRegistration() {
		return this.mainAuditRegistration;
	}

	public void setMainAuditRegistration(MainAuditRegistration mainAuditRegistration) {
		this.mainAuditRegistration = mainAuditRegistration;
	}
	
	public LutUser getLutUser() {
		return this.lutUser;
	}

	public void setLutUser(LutUser lutUser) {
		this.lutUser = lutUser;
	}

}