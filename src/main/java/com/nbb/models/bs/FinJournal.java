package com.nbb.models.bs;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nbb.models.fn.MainAuditRegistration;


/**
 * The persistent class for the fin_journal database table.
 * 
 */
@Entity
@Table(name="fin_journal")
@NamedQuery(name="FinJournal.findAll", query="SELECT f FROM FinJournal f")
public class FinJournal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "JOURNAL_SEQ")
    @SequenceGenerator(sequenceName = "fin_journal_seq", allocationSize = 1, name = "JOURNAL_SEQ")
	private int id;

	private String data1;

	private long data10;

	private String data11;

	private String data12;

	private String data13;

	private String data14;

	private String data15;

	private String data16;

	private String data17;

	private String data18;

	private String data19;

	private String data2;

	private String data20;

	private String data21;

	private String data3;

	private String data4;

	private String data5;

	private String data6;

	private String data7;

	private String data8;

	private String data9;
	private long planid;
	private int stepid;
	
	private boolean a;
	private boolean b;
	private boolean c;
	private boolean d;
	private boolean e;
	private long amount;
	private String description;
	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="planid", insertable=false, updatable=false)
	@JsonBackReference
	private MainAuditRegistration mainAuditRegistration;

	public FinJournal() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isA() {
		return a;
	}

	public void setA(boolean a) {
		this.a = a;
	}

	public boolean isB() {
		return b;
	}

	public void setB(boolean b) {
		this.b = b;
	}

	public boolean isC() {
		return c;
	}

	public void setC(boolean c) {
		this.c = c;
	}

	public boolean isD() {
		return d;
	}

	public void setD(boolean d) {
		this.d = d;
	}

	public boolean isE() {
		return e;
	}

	public void setE(boolean e) {
		this.e = e;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	public long getPlanid() {
		return planid;
	}

	public void setPlanid(long planid) {
		this.planid = planid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getData1() {
		return this.data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public long getData10() {
		return this.data10;
	}

	public void setData10(long data10) {
		this.data10 = data10;
	}

	public String getData11() {
		return this.data11;
	}

	public void setData11(String data11) {
		this.data11 = data11;
	}

	public String getData12() {
		return this.data12;
	}

	public void setData12(String data12) {
		this.data12 = data12;
	}

	public String getData13() {
		return this.data13;
	}

	public void setData13(String data13) {
		this.data13 = data13;
	}

	public String getData14() {
		return this.data14;
	}

	public void setData14(String data14) {
		this.data14 = data14;
	}

	public String getData15() {
		return this.data15;
	}

	public void setData15(String data15) {
		this.data15 = data15;
	}

	public String getData16() {
		return this.data16;
	}

	public void setData16(String data16) {
		this.data16 = data16;
	}

	public String getData17() {
		return this.data17;
	}

	public void setData17(String data17) {
		this.data17 = data17;
	}

	public String getData18() {
		return this.data18;
	}

	public void setData18(String data18) {
		this.data18 = data18;
	}

	public String getData19() {
		return this.data19;
	}

	public void setData19(String data19) {
		this.data19 = data19;
	}

	public String getData2() {
		return this.data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData20() {
		return this.data20;
	}

	public void setData20(String data20) {
		this.data20 = data20;
	}

	public String getData21() {
		return this.data21;
	}

	public void setData21(String data21) {
		this.data21 = data21;
	}

	public String getData3() {
		return this.data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData4() {
		return this.data4;
	}

	public void setData4(String data4) {
		this.data4 = data4;
	}

	public String getData5() {
		return this.data5;
	}

	public void setData5(String data5) {
		this.data5 = data5;
	}

	public String getData6() {
		return this.data6;
	}

	public void setData6(String data6) {
		this.data6 = data6;
	}

	public String getData7() {
		return this.data7;
	}

	public void setData7(String data7) {
		this.data7 = data7;
	}

	public String getData8() {
		return this.data8;
	}

	public void setData8(String data8) {
		this.data8 = data8;
	}

	public String getData9() {
		return this.data9;
	}

	public void setData9(String data9) {
		this.data9 = data9;
	}

	public MainAuditRegistration getMainAuditRegistration() {
		return mainAuditRegistration;
	}

	public void setMainAuditRegistration(MainAuditRegistration mainAuditRegistration) {
		this.mainAuditRegistration = mainAuditRegistration;
	}

	public int getStepid() {
		return stepid;
	}

	public void setStepid(int stepid) {
		this.stepid = stepid;
	}
	
}