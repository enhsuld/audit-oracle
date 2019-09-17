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
@Table(name="lnk_audit_problems")
@NamedQuery(name="LnkAuditProblem.findAll", query="SELECT l FROM LnkAuditProblem l")
public class LnkAuditProblem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AP_SEQ")
    @SequenceGenerator(sequenceName = "lnk_audit_problems_seq", allocationSize = 1, name = "AP_SEQ")
	private Long id;

	private String acc;

	private String problem;

	private double amount;
	
	private int stepid;

	@Column(name="is_matter")
	private int matter;

	@Column(name="comment_type")
	private int commentType;
	
	@Column(name="akt_zaalt")
	private String aktZaalt;
	
	@Column(name="akt_name")
	private String aktName;

	private int answer;

	private int result;
	
	@Column(name="acc_code")
	private int accCode;
	
	private long appid;
	
	@Column(name="com_amount")
	private double comAmount;
	
	@Column(name="com_result")
	private int comResult;
	
	@Column(name="com_akt_name")
	private String comAktName;
	
	@Column(name="com_akt_zaalt")
	private String comAktZaalt;
	
	@Column(name="com_matter")
	private int comMatter;
	
	@Column(name="fin_date")
	private String finDate;
	
	private boolean finish;
	
	@Column(name="is_active")
	private boolean active;
	
	@Column(name="offer_date")
	private String offerDate;
	

	@Column(name="ins_date")
	private String insDate;
	
	@Column(name="report_id")
	private long reportId;
	
	@Column(name="final_amount")
	private int finalAmount;
	
	@Column(name="final_akt_amount")
	private int finalAktAmount;
	
	@Column(name="final_ash_amount")
	private int finalAshAmount;
	
	@Column(name="final_adv_amount")
	private int finalZuvAmount;
	
	//bi-directional many-to-one association to MainAuditRegistration
	@ManyToOne
	@JoinColumn(name="appid", insertable=false, updatable=false)
	@JsonBackReference
	private MainAuditRegistration mainAuditRegistration;

	public LnkAuditProblem() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAcc() {
		return acc;
	}

	public void setAcc(String acc) {
		this.acc = acc;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getStepid() {
		return stepid;
	}

	public void setStepid(int stepid) {
		this.stepid = stepid;
	}

	public int getMatter() {
		return matter;
	}

	public void setMatter(int matter) {
		this.matter = matter;
	}

	public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}

	public String getAktZaalt() {
		return aktZaalt;
	}

	public void setAktZaalt(String aktZaalt) {
		this.aktZaalt = aktZaalt;
	}

	public String getAktName() {
		return aktName;
	}

	public void setAktName(String aktName) {
		this.aktName = aktName;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getAccCode() {
		return accCode;
	}

	public void setAccCode(int accCode) {
		this.accCode = accCode;
	}

	public long getAppid() {
		return appid;
	}

	public void setAppid(long appid) {
		this.appid = appid;
	}

	public double getComAmount() {
		return comAmount;
	}

	public void setComAmount(double comAmount) {
		this.comAmount = comAmount;
	}

	public int getComResult() {
		return comResult;
	}

	public void setComResult(int comResult) {
		this.comResult = comResult;
	}

	public String getComAktName() {
		return comAktName;
	}

	public void setComAktName(String comAktName) {
		this.comAktName = comAktName;
	}

	public String getComAktZaalt() {
		return comAktZaalt;
	}

	public void setComAktZaalt(String comAktZaalt) {
		this.comAktZaalt = comAktZaalt;
	}

	public int getComMatter() {
		return comMatter;
	}

	public void setComMatter(int comMatter) {
		this.comMatter = comMatter;
	}

	public String getFinDate() {
		return finDate;
	}

	public void setFinDate(String finDate) {
		this.finDate = finDate;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getOfferDate() {
		return offerDate;
	}

	public void setOfferDate(String offerDate) {
		this.offerDate = offerDate;
	}
		
	public String getInsDate() {
		return insDate;
	}

	public void setInsDate(String insDate) {
		this.insDate = insDate;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}

	public int getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(int finalAmount) {
		this.finalAmount = finalAmount;
	}

	public int getFinalAktAmount() {
		return finalAktAmount;
	}

	public void setFinalAktAmount(int finalAktAmount) {
		this.finalAktAmount = finalAktAmount;
	}

	public int getFinalAshAmount() {
		return finalAshAmount;
	}

	public void setFinalAshAmount(int finalAshAmount) {
		this.finalAshAmount = finalAshAmount;
	}

	public int getFinalZuvAmount() {
		return finalZuvAmount;
	}

	public void setFinalZuvAmount(int finalZuvAmount) {
		this.finalZuvAmount = finalZuvAmount;
	}

	public MainAuditRegistration getMainAuditRegistration() {
		return mainAuditRegistration;
	}

	public void setMainAuditRegistration(MainAuditRegistration mainAuditRegistration) {
		this.mainAuditRegistration = mainAuditRegistration;
	}
				
}