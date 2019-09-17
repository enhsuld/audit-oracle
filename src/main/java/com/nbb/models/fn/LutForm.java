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
@Table(name="lut_forms")
@NamedQuery(name="LutForm.findAll", query="SELECT l FROM LutForm l")
public class LutForm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FORM_SEQ")
    @SequenceGenerator(sequenceName = "lut_forms_seq", allocationSize = 1, name = "FORM_SEQ")
	private long id;

	private String data1;

	private String data2;

	private String data3;

	private int data4;

	private boolean data5;

	private String data6;

	private String data7;

	private String data8;
	
	private boolean data10;
	
	private boolean data12;
	
	private int data13;
	
	private int data11;
	
	private int orderid;
		
	private Long parentid;

	//bi-directional many-to-one association to LutForm
	@ManyToOne
	@JoinColumn(name="parentid", nullable = true,insertable=false,updatable=false)
	@JsonBackReference
	private LutForm lutForm;

	//bi-directional many-to-one association to LutForm
	@OneToMany(fetch = FetchType.LAZY, mappedBy="lutForm")
	@JsonBackReference
	private List<LutForm> lutForms;

	public LutForm() {
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public Long getParentid() {
		return parentid;
	}
	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public int getData4() {
		return data4;
	}

	public void setData4(int data4) {
		this.data4 = data4;
	}

	public boolean isData5() {
		return data5;
	}

	public void setData5(boolean data5) {
		this.data5 = data5;
	}

	public String getData6() {
		return data6;
	}

	public void setData6(String data6) {
		this.data6 = data6;
	}

	public String getData7() {
		return data7;
	}

	public void setData7(String data7) {
		this.data7 = data7;
	}

	public String getData8() {
		return data8;
	}

	public void setData8(String data8) {
		this.data8 = data8;
	}
	
	public boolean isData10() {
		return data10;
	}

	public void setData10(boolean data10) {
		this.data10 = data10;
	}
	
	public int getData11() {
		return data11;
	}

	public void setData11(int data11) {
		this.data11 = data11;
	}
	
	public boolean isData12() {
		return data12;
	}

	public void setData12(boolean data12) {
		this.data12 = data12;
	}
	
	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public LutForm getLutForm() {
		return this.lutForm;
	}

	public void setLutForm(LutForm lutForm) {
		this.lutForm = lutForm;
	}

	public List<LutForm> getLutForms() {
		return this.lutForms;
	}

	public void setLutForms(List<LutForm> lutForms) {
		this.lutForms = lutForms;
	}

	public LutForm addLutForm(LutForm lutForm) {
		getLutForms().add(lutForm);
		lutForm.setLutForm(this);

		return lutForm;
	}

	public LutForm removeLutForm(LutForm lutForm) {
		getLutForms().remove(lutForm);
		lutForm.setLutForm(null);

		return lutForm;
	}

	public int getData13() {
		return data13;
	}

	public void setData13(int data13) {
		this.data13 = data13;
	}
}