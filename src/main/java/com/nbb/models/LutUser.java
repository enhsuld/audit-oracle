package com.nbb.models;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nbb.models.fn.LnkMainUser;
import com.nbb.models.fn.LutDepartment;
import com.nbb.models.fn.LutPosition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * The persistent class for the lut_users database table.
 * 
 */
@Accessors(chain = true)
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name="lut_users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
//@NamedQuery(name="LutUser.findAll", query="SELECT l FROM LutUser l")
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
	private String flurl;
	private String flname;
	private long balance;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="departmentid",nullable = false,insertable=false,updatable=false)
	private LutDepartment lutDepartment;

	@OneToMany(mappedBy="lutUser")
	@JsonManagedReference
	private List<LnkUserrole> lnkUserroles;
	
	@OneToMany(mappedBy="lutUser")
	private List<LnkMainUser> lnkMainUsers;

}