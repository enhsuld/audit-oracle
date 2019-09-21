package com.nbb.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nbb.models.fn.LnkMainUser;
import com.nbb.models.fn.LutDepartment;
import com.nbb.models.fn.LutPosition;

import java.util.List;


/**
 * The persistent class for the lut_users database table.
 * 
 */
@Entity
@Table(name="lut_users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NamedQuery(name="LutUser.findAll", query="SELECT l FROM LutUser l")
public class LutUser implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_SEQ")
    @SequenceGenerator(sequenceName = "lut_users_seq", allocationSize = 1, name = "USER_SEQ")
	private long id;

	private String email;

	private String familyname;

	private String givenname;

	private boolean isactive;

	private String mobile;

	private String password;

	private String username;
	
	private long departmentid;
	
	private long positionid;
	
	private long autype;
	
	private String roleid;
	
	private boolean iscompany=false;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="departmentid",nullable = false,insertable=false,updatable=false)
	private LutDepartment lutDepartment;
	
	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="positionid",nullable = false,insertable=false,updatable=false)	
	@JsonBackReference
	private LutPosition lutPosition;*/

	//bi-directional many-to-one association to LnkUserrole
	@OneToMany(mappedBy="lutUser")
	@JsonManagedReference
	private List<LnkUserrole> lnkUserroles;
	
	@OneToMany(mappedBy="lutUser")
	private List<LnkMainUser> lnkMainUsers;

	public LutUser() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getAutype() {
		return autype;
	}

	public void setAutype(long autype) {
		this.autype = autype;
	}

	public boolean isIscompany() {
		return iscompany;
	}

	public void setIscompany(boolean iscompany) {
		this.iscompany = iscompany;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFamilyname() {
		return this.familyname;
	}

	public void setFamilyname(String familyname) {
		this.familyname = familyname;
	}

	public String getGivenname() {
		return this.givenname;
	}

	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}

	public boolean getIsactive() {
		return this.isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}
	
	public long getPositionid() {
		return positionid;
	}

	public void setPositionid(long positionid) {
		this.positionid = positionid;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<LnkMainUser> getLnkMainUsers() {
		return lnkMainUsers;
	}

	public void setLnkMainUsers(List<LnkMainUser> lnkMainUsers) {
		this.lnkMainUsers = lnkMainUsers;
	}

	public long getDepartmentid() {
		return departmentid;
	}

	public void setDepartmentid(long departmentid) {
		this.departmentid = departmentid;
	}

	public LutDepartment getLutDepartment() {
		return lutDepartment;
	}

	public void setLutDepartment(LutDepartment lutDepartment) {
		this.lutDepartment = lutDepartment;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<LnkUserrole> getLnkUserroles() {
		return this.lnkUserroles;
	}

	public void setLnkUserroles(List<LnkUserrole> lnkUserroles) {
		this.lnkUserroles = lnkUserroles;
	}

	public LnkUserrole addLnkUserrole(LnkUserrole lnkUserrole) {
		getLnkUserroles().add(lnkUserrole);
		lnkUserrole.setLutUser(this);

		return lnkUserrole;
	}

	public LnkUserrole removeLnkUserrole(LnkUserrole lnkUserrole) {
		getLnkUserroles().remove(lnkUserrole);
		lnkUserrole.setLutUser(null);

		return lnkUserrole;
	}

/*	public LutPosition getLutPosition() {
		return lutPosition;
	}

	public void setLutPosition(LutPosition lutPosition) {
		this.lutPosition = lutPosition;
	}*/
	
}