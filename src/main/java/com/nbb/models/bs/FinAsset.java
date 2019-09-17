package com.nbb.models.bs;

import java.io.Serializable;
import javax.persistence.*;

import com.nbb.models.fn.MainAuditRegistration;



/**
 * The persistent class for the FIN_ASSETS database table.
 * 
 */
@Entity
@Table(name="fin_assets")
@NamedQuery(name="FinAsset.findAll", query="SELECT f FROM FinAsset f")
public class FinAsset implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASSETS_SEQ")
    @SequenceGenerator(sequenceName = "fin_assets_seq", allocationSize = 1, name = "ASSETS_SEQ")
	private long id;

	private String data1;

	private String data10;

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

	private String data22;

	private String data23;

	private String data24;

	private String data25;

	private String data26;

	private String data27;

	private String data28;

	private String data29;

	private String data3;

	private String data30;

	private String data31;

	private String data32;

	private String data33;

	private String data34;

	private String data35;

	private String data36;

	private String data37;

	private String data38;

	private String data39;

	private String data4;

	private String data5;

	private String data6;

	private String data7;

	private String data8;

	private String data9;

	//private long formid;
	
	private long orgcatid;

	private String orgcode;
	
	private String cyear;

	private long planid;

	private long stepid;
	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="planid",nullable = false,insertable=false,updatable=false)
	private MainAuditRegistration mainAuditRegistration;

	public FinAsset() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getData1() {
		return this.data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData10() {
		return this.data10;
	}

	public void setData10(String data10) {
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

	public String getData22() {
		return this.data22;
	}

	public void setData22(String data22) {
		this.data22 = data22;
	}

	public String getData23() {
		return this.data23;
	}

	public void setData23(String data23) {
		this.data23 = data23;
	}

	public String getData24() {
		return this.data24;
	}

	public void setData24(String data24) {
		this.data24 = data24;
	}

	public String getData25() {
		return this.data25;
	}

	public void setData25(String data25) {
		this.data25 = data25;
	}

	public String getData26() {
		return this.data26;
	}

	public void setData26(String data26) {
		this.data26 = data26;
	}

	public String getData27() {
		return this.data27;
	}

	public void setData27(String data27) {
		this.data27 = data27;
	}

	public String getData28() {
		return this.data28;
	}

	public void setData28(String data28) {
		this.data28 = data28;
	}

	public String getData29() {
		return this.data29;
	}

	public void setData29(String data29) {
		this.data29 = data29;
	}

	public String getData3() {
		return this.data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	public String getData30() {
		return this.data30;
	}

	public void setData30(String data30) {
		this.data30 = data30;
	}

	public String getData31() {
		return this.data31;
	}

	public void setData31(String data31) {
		this.data31 = data31;
	}

	public String getData32() {
		return this.data32;
	}

	public void setData32(String data32) {
		this.data32 = data32;
	}

	public String getData33() {
		return this.data33;
	}

	public void setData33(String data33) {
		this.data33 = data33;
	}

	public String getData34() {
		return this.data34;
	}

	public void setData34(String data34) {
		this.data34 = data34;
	}

	public String getData35() {
		return this.data35;
	}

	public void setData35(String data35) {
		this.data35 = data35;
	}

	public String getData36() {
		return this.data36;
	}

	public void setData36(String data36) {
		this.data36 = data36;
	}

	public String getData37() {
		return this.data37;
	}

	public void setData37(String data37) {
		this.data37 = data37;
	}

	public String getData38() {
		return this.data38;
	}

	public void setData38(String data38) {
		this.data38 = data38;
	}

	public String getData39() {
		return this.data39;
	}

	public void setData39(String data39) {
		this.data39 = data39;
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

/*	public long getFormid() {
		return this.formid;
	}

	public void setFormid(long formid) {
		this.formid = formid;
	}*/
	public long getOrgcatid() {
		return orgcatid;
	}

	public void setOrgcatid(long orgcatid) {
		this.orgcatid = orgcatid;
	}

	
	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getCyear() {
		return cyear;
	}

	public void setCyear(String cyear) {
		this.cyear = cyear;
	}

	public long getStepid() {
		return stepid;
	}

	public void setStepid(long stepid) {
		this.stepid = stepid;
	}

	public long getPlanid() {
		return planid;
	}

	public void setPlanid(long planid) {
		this.planid = planid;
	}

	public MainAuditRegistration getMainAuditRegistration() {
		return this.mainAuditRegistration;
	}

	public void setMainAuditRegistration(MainAuditRegistration mainAuditRegistration) {
		this.mainAuditRegistration = mainAuditRegistration;
	}
}