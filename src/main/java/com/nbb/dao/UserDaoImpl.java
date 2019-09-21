package com.nbb.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.sql.DataSource;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nbb.models.LutUser;
import com.nbb.models.bs.FinAbw;
import com.nbb.models.bs.FinAsset;
import com.nbb.models.bs.FinBudget;
import com.nbb.models.bs.FinCbw;
import com.nbb.models.bs.FinCt1a;
import com.nbb.models.bs.FinCt2a;
import com.nbb.models.bs.FinCt3a;
import com.nbb.models.bs.FinCt4a;
import com.nbb.models.bs.FinCtt1;
import com.nbb.models.bs.FinCtt2;
import com.nbb.models.bs.FinCtt3;
import com.nbb.models.bs.FinCtt4;
import com.nbb.models.bs.FinCtt5;
import com.nbb.models.bs.FinCtt6;
import com.nbb.models.bs.FinCtt7;
import com.nbb.models.bs.FinCtt8;
import com.nbb.models.bs.FinCtt9;
import com.nbb.models.bs.FinInventory;
import com.nbb.models.bs.FinJournal;
import com.nbb.models.bs.FinNt2;
import com.nbb.models.bs.FinTgt1;
import com.nbb.models.bs.FinTgt1a;
import com.nbb.models.bs.FinTrialBalance;
import com.nbb.models.fn.LnkAuditForm;
import com.nbb.models.fn.LnkAuditProblem;

@Repository
@Transactional
public class UserDaoImpl  extends JdbcDaoSupport implements UserDao {
	
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SessionFactory sessionFactory;	
	
	@Autowired 
	DataSource dataSource;
	
	@PostConstruct
	private void initialize(){
		setDataSource(dataSource);
	}
	
	

	@Override
	public void insertBatchSQL(String sql) {
		getJdbcTemplate().batchUpdate(new String[]{sql});
	}



	@Override
	public void inserBatch(final List<?> dt, String model,long planid) {

		if(model.equals("FinJournal")){		
			String sql = "INSERT INTO fin_journal " + "(planid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16,data17,data18,data19,data20,a,b,c,d,e,amount,stepid,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_journal_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinJournal item = (FinJournal) dt.get(i);
					ps.setLong(1, planid);
					ps.setString(2, item.getData1());
					ps.setString(3, item.getData2());
					ps.setString(4, item.getData3());
					ps.setString(5, item.getData4());
					ps.setString(6, item.getData5());
					ps.setString(7, item.getData6());
					ps.setString(8, item.getData7());
					ps.setString(9, item.getData8());
					ps.setString(10, item.getData9());
					ps.setLong(11, item.getData10());
					ps.setString(12, item.getData11());
					ps.setString(13, item.getData12());
					ps.setString(14, item.getData13());
					ps.setString(15, item.getData14());
					ps.setString(16, item.getData15());
					ps.setString(17, item.getData16());
					ps.setString(18, item.getData17());
					ps.setString(19, item.getData18());
					ps.setString(20, item.getData19());
					ps.setString(21, item.getData20());
					ps.setBoolean(22, true);
					ps.setBoolean(23, true);
					ps.setBoolean(24, true);
					ps.setBoolean(25, true);
					ps.setBoolean(26, true);
					ps.setInt(27, 0);
					ps.setInt(28, item.getStepid());					
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		
		else if(model.equals("lnkAuditProblem")){
			String sql = "INSERT INTO lnk_audit_problems " + "(appid,acc,problem,amount,stepid,answer,is_matter,result,comment_type,akt_name,akt_zaalt,acc_code,com_akt_name,com_akt_zaalt,ins_date,report_id,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,lnk_audit_problems.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					LnkAuditProblem item = (LnkAuditProblem) dt.get(i);
					ps.setLong(1, planid);
					ps.setString(2, item.getAcc());
					ps.setString(3, item.getProblem());
					ps.setDouble(4, item.getAmount());
					ps.setInt(5, item.getStepid());
					ps.setInt(6, item.getAnswer());
					ps.setInt(7, item.getMatter());
					ps.setInt(8, item.getResult());
					ps.setInt(9, item.getCommentType());
					ps.setString(10, item.getAktName());
					ps.setString(11, item.getAktZaalt());
					ps.setLong(12, item.getAccCode());
					ps.setString(13, item.getComAktName());
					ps.setString(14, item.getComAktZaalt());
					ps.setString(15, item.getInsDate());
					ps.setLong(16, item.getReportId());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		
		else if(model.equals("LnkAuditForm")){
			String sql = "INSERT INTO lnk_audit_forms " + "(appid,parentid,formid,data1,data2,data3,data4,data5,data6,data7,data8,data10,data13,orderid,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,lnk_audit_forms_SEQ.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					LnkAuditForm item = (LnkAuditForm) dt.get(i);
					ps.setLong(1, planid);
					if(item.getParentid()==null){
						ps.setNull(2, java.sql.Types.INTEGER);
					}else{
						ps.setLong(2, item.getParentid());
					}
					ps.setLong(3, item.getFormid());
					ps.setString(4, item.getData1());
					ps.setString(5, item.getData2());
					ps.setString(6, item.getData3());
					ps.setInt(7, item.getData4());
					ps.setBoolean(8, item.isData5());
					ps.setInt(9, item.getData6());
					ps.setString(10, item.getData7());
					ps.setString(11, item.getData8());
					ps.setBoolean(12, item.isData10());
					ps.setInt(13, item.getData13());
					ps.setInt(14, item.getOrderid());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("11.CTT6")){		
			String sql = "INSERT INTO fin_ctt6 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?, ?,?, ?, ?,?, ?, ?,?, ?, ?, fin_ctt6_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt6 item = (FinCtt6) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("23.TRIAL BALANCE")){
			String sql = "INSERT INTO fin_trial_balance " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_trial_balance_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinTrialBalance item = (FinTrialBalance) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("25.CBWS")){
			String sql = "INSERT INTO fin_cbws " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,fin_cbws_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCbw item = (FinCbw) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("24.ABWS")){
			String sql = "INSERT INTO fin_abws " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,fin_abws_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinAbw item = (FinAbw) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("21.TGT1A")){
			String sql = "INSERT INTO fin_tgt1a " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,fin_tgt1a_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinTgt1a item = (FinTgt1a) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		/*else if(model.equals("6.Payroll")){
			String sql = "INSERT INTO FIN_PAYROLL " + "(ID,orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16) VALUES (FIN_PAYROLL_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinPayroll item = (FinPayroll) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
					ps.setString(21, item.getData16());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}*/
		else if(model.equals("17.Inventory")){
			String sql = "INSERT INTO fin_inventory " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_inventory_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinInventory item = (FinInventory) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("16.Assets")){
			String sql = "INSERT INTO fin_assets " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16,data17,data18"
					+ ",data19,data20,data21,data22,data23,data24,data25,data26,data27,data28,data29,data30,data31,data32,data33,data34,data35,data36,data37,data38,data39,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_assets_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinAsset item = (FinAsset) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
					ps.setString(21, item.getData16());
					ps.setString(22, item.getData17());
					ps.setString(23, item.getData18());
					ps.setString(24, item.getData19());
					ps.setString(25, item.getData20());
					ps.setString(26, item.getData21());
					ps.setString(27, item.getData22());
					ps.setString(28, item.getData23());
					ps.setString(29, item.getData24());
					ps.setString(30, item.getData25());
					ps.setString(31, item.getData26());
					ps.setString(32, item.getData27());
					ps.setString(33, item.getData28());
					ps.setString(34, item.getData29());
					ps.setString(35, item.getData30());
					ps.setString(36, item.getData31());
					ps.setString(37, item.getData32());
					ps.setString(38, item.getData33());
					ps.setString(39, item.getData34());
					ps.setString(40, item.getData35());
					ps.setString(41, item.getData36());
					ps.setString(42, item.getData37());
					ps.setString(43, item.getData38());
					ps.setString(44, item.getData39());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
	/*	else if(model.equals("3.Journal")){
			String sql = "INSERT INTO FIN_JOURNAL " + "(ID,orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16,data17,data18"
					+ ",data19,data20,data21) VALUES (FIN_JOURNAL_SEQ.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinJournal item = (FinJournal) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
					ps.setString(21, item.getData16());
					ps.setString(22, item.getData17());
					ps.setString(23, item.getData18());
					ps.setString(24, item.getData19());
					ps.setString(25, item.getData20());
					ps.setString(26, item.getData21());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}*/
		else if(model.equals("19.Budget")){
			String sql = "INSERT INTO fin_budget " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16,data17,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_budget_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinBudget item = (FinBudget) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
					ps.setString(21, item.getData16());
					ps.setString(22, item.getData17());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("13.CTT8")){
			String sql = "INSERT INTO fin_ctt8 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,fin_ctt8_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt8 item = (FinCtt8) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("14.CTT9")){
			String sql = "INSERT INTO fin_ctt9 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,fin_ctt9_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt9 item = (FinCtt9) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("12.CTT7")){
			String sql = "INSERT INTO fin_ctt7 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16,id) VALUES (?, ?,?, ?, ?,?, ?, ?,?, ?, ?,?, ?,?,?,?,?,?,?,?,?,fin_ctt7_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt7 item = (FinCtt7) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
					ps.setString(21, item.getData16());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("7.CTT2")){
			String sql = "INSERT INTO fin_ctt2 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,fin_ctt2_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt2 item = (FinCtt2) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
				}

				@Override
				public int getBatchSize() {
					
					return dt.size();
				}
			});
		}
		else if(model.equals("10.CTT5")){
			String sql = "INSERT INTO fin_ctt5 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16,data17,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_ctt5_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt5 item = (FinCtt5) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
					ps.setString(21, item.getData16());
					ps.setString(22, item.getData17());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("9.CTT4")){
			String sql = "INSERT INTO fin_ctt4 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,fin_ctt4_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt4 item = (FinCtt4) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("8.CTT3")){
			String sql = "INSERT INTO fin_ctt3 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,data16,data17,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_ctt3_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt3 item = (FinCtt3) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
					ps.setString(21, item.getData16());
					ps.setString(22, item.getData17());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("6.CTT1")){
			String sql = "INSERT INTO fin_ctt1 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,data11,data12,data13,data14,data15,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_ctt1_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCtt1 item = (FinCtt1) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
					ps.setString(16, item.getData11());
					ps.setString(17, item.getData12());
					ps.setString(18, item.getData13());
					ps.setString(19, item.getData14());
					ps.setString(20, item.getData15());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("22.NT2")){
			String sql = "INSERT INTO fin_nt2 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,id) VALUES (?,?,?,?,?,?,?,?,?,fin_nt2_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinNt2 item = (FinNt2) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("20.TGT1")){
			String sql = "INSERT INTO fin_tgt1 " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,fin_tgt1_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinTgt1 item = (FinTgt1) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("5.CT4A")){
			String sql = "INSERT INTO fin_ct4a " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,data5,data6,data7,data8,data9,data10,id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,fin_ct4a_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCt4a item = (FinCt4a) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
					ps.setString(10, item.getData5());
					ps.setString(11, item.getData6());
					ps.setString(12, item.getData7());
					ps.setString(13, item.getData8());
					ps.setString(14, item.getData9());
					ps.setString(15, item.getData10());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("4.CT3A")){
			String sql = "INSERT INTO fin_ct3a " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,id) VALUES (?,?,?,?,?,?,?,?,?,fin_ct3a_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCt3a item = (FinCt3a) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("st2a")){
			System.out.println("2a");
			String sql = "INSERT INTO fin_ct2a " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,id) VALUES (?,?,?,?,?,?,?,?,?,fin_ct2a_seq.nextval)";
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCt2a item = (FinCt2a) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
		else if(model.equals("st1a")){
			String sql = "INSERT INTO fin_ct1a " + "(orgcode, stepid,cyear,planid,orgcatid,data1,data2,data3,data4,id) VALUES (?,?,?,?,?,?,?,?,?,fin_ct1a_seq.nextval)";
			
			getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
				public void setValues(PreparedStatement ps, int i) throws SQLException,NullPointerException {
					FinCt1a item = (FinCt1a) dt.get(i);
					ps.setString(1, item.getOrgcode());
					ps.setLong(2, item.getStepid());
					ps.setString(3, item.getCyear());
					ps.setLong(4, item.getPlanid());
					ps.setLong(5, item.getOrgcatid());
					ps.setString(6, item.getData1());
					ps.setString(7, item.getData2());
					ps.setString(8, item.getData3());
					ps.setString(9, item.getData4());
				}

				@Override
				public int getBatchSize() {
					return dt.size();
				}
			});
		}
	}
	
	@Override
	public Object saveOrUpdate(Object obj) {
		try{			
			sessionFactory.getCurrentSession().update(obj);
			return true;
		}
		catch (ConstraintViolationException err){
			err.printStackTrace();			
			return false;
		}
		
	}
	
	@Override
	public Object findAll(String domain, String whereclause) {
		Query query=null;
		if(whereclause!=null){			
			query = sessionFactory.getCurrentSession().createQuery(whereclause);			
		}
		else{
			query = sessionFactory.getCurrentSession().createQuery("from "+domain+" objlist  order by objlist.id desc ");
		}
		
		List<Object> robj = query.list();
		query = null;
		return robj;
		
	}
	
	@Override
	public Object findById(String domain,long id, String whereclause) {
		Query query=null;
		query=sessionFactory.getCurrentSession().createQuery("from "+domain+" t where t.id=:id");
		query.setParameter("id", id);
		Object robj = query.list().get(0);		
		return robj;
		
	}
	
	@Override
	public void deleteById(String domain,long obj_id, String whereclause) {
		Query query=null;
		if(whereclause!=null){			
			query= sessionFactory.getCurrentSession().createQuery("delete from "+domain+"  t where t."+whereclause+"=:obj_id");
			query.setParameter("obj_id", obj_id);
			
		}
		else{
			query= sessionFactory.getCurrentSession().createQuery("delete from "+domain+"  t where t.id=:obj_id");
    		query.setParameter("obj_id", obj_id);
		}
		//List list = query.list();
		int qresult = query.executeUpdate();
		//return qresult;
		
	}
		
	
	@Override
	public Object getHQLResult(String hqlString,String returnType){
		Query query = sessionFactory.getCurrentSession().createQuery(hqlString);
		System.out.println(query);
		
		if("list".equals(returnType)){
			return  query.list();
		}
		else if("current".equals(returnType)){
			if(query.list().size()>0){
				if(query.list().size()==1){
					return query.list().get(0);
				}
				else {
					return null;
				}
			}
			
		}
		else if("count".equals(returnType)){
			int resultInt =  ((Long)query.uniqueResult()).intValue();
			return resultInt;
		}
		
		else {
			return null;
		}
		
		return null;
	}

	public List<Object> kendojson(String request,String tablename){

		try{			
			
		//	Gson gson = new Gson ();	    
			int skip=0;
			int take=2;
			int page=0;			
			String field="";
			String order="";
			String dir="";
			JSONArray sort=null;
			String group="";
			JSONObject filter=null;
			String org="";
			String custom="";
			String nat="";
			String flquery="";
			String isspecial="";
			System.out.println("sss"+request+tablename);
			JSONObject req= new JSONObject(request);				
			skip=req.getInt("skip");			
			page=req.getInt("page");
			if(req.has("sort")){
				sort=req.getJSONArray("sort");
			}
			if(req.has("take")){
				take=req.getInt("take");
			}
			
			if(req.has("customPsize")){
				take=req.getInt("customPsize");
			}
			
			if(req.has("group")){
				group=req.getString("group");
			}
			if(req.has("filter")){
				
				if(!req.isNull("filter")){
					filter=req.getJSONObject("filter");	
				}
						
			}
		
			if(req.has("custom")){
				custom=req.getString("custom");
			}
			if(req.has("field")){
				field=req.getString("field");
			}
			if(req.has("dir")){
				dir=req.getString("dir");
			}
			if(custom.length()>0){
				flquery=custom;
			}
			if(req.has("native")){
				nat=req.getString("native");
			}
			if(req.has("isspecial")){
				isspecial=req.getString("isspecial");
			}
			
			String multiOrde="";
			
			if(sort!=null){
				JSONArray arr= sort;
				for(int i=0; i<arr.length();i++){
					String str=arr.get(i).toString();
					JSONObject srt= new JSONObject(str);
					if(srt.isNull("field")){
						field="";	
					}
					else{
						field=srt.getString("field");
						multiOrde=multiOrde+ " "+ field;
						
					}
					if(srt.isNull("dir")){
						dir="";
					}
					else{
						dir=srt.getString("dir");
						multiOrde=multiOrde + " " +dir + ",";
					}
				}
				
			}
			if(multiOrde.length()>0){
				System.out.println("$$$$ "+multiOrde.substring(0, multiOrde.length()-1));
			}
			
			
			String groupfield="";
			String groupdir="";
			if(group.length()>0){
				JSONArray arr= new JSONArray(group);
				for(int i=0; i<arr.length();i++){
					String str=arr.get(i).toString();
					JSONObject srt= new JSONObject(str);
					if(srt.isNull("field")){
						groupfield="";	
					}
					else{
						groupfield=srt.getString("field");	
					}
					if(srt.isNull("dir")){
						groupdir="";
					}
					else{
						groupdir=srt.getString("dir");
					}
				}
				
			}
			String filterfield="";
			String operator="";
			String value="";
			
			if(filter!=null){
				
				JSONObject fltr= filter;		
				
				String logic=fltr.getString("logic");
				
				JSONArray arr= fltr.getJSONArray("filters");
				for(int i=0; i<arr.length();i++){
					String str=arr.get(i).toString();
					JSONObject srt= new JSONObject(str);
					boolean fb=false;
					if(srt.isNull("field")){
						filterfield="";	
					}
					else{
						filterfield=srt.getString("field");	
					}
					if(srt.isNull("operator")){
						operator="";
					}
					else{
						operator=srt.getString("operator");
					}
					if(srt.isNull("value")){
						value="";
					}
					else{
					    if (srt.get("value") instanceof Boolean){
                            value=(srt.getBoolean("value") ? "1" : "0");
                            fb=true;
                        }
                        else{
                            value=String.valueOf(srt.get("value")).toLowerCase();
                        }
					}
					if(i>0){
						
						switch(operator){
							case "startswith":flquery=flquery+  " "+logic+" lower("+filterfield+ ") LIKE '"+value+"%'"; break;
							case "contains":flquery=flquery+  " "+logic+" lower("+filterfield+ ") LIKE '%"+value+"%'"; break;
							case "doesnotcontain":flquery=flquery+  " "+logic+" lower("+filterfield+ ") NOT LIKE '%"+value+"%'"; break;
							case "endswith":flquery=flquery+  " "+logic+" lower("+filterfield+ ") LIKE '%"+value+"'"; break;
							case "neq":flquery=flquery+  " "+logic+" lower("+filterfield+ ") != '"+value+"'"; break;
							case "eq":flquery=flquery+  " "+logic+ " "+ ((fb==true) ? filterfield : (" lower("+filterfield+ ")")) + " = '"+value+"'"; break;
							case "gte":flquery=flquery+  " "+logic+" lower("+filterfield+ ") >="+value+""; break;
						}						
					}
					else{
						if(custom.length()>0){
							switch(operator){
								case "startswith":flquery=" "+custom+" and lower("+filterfield+ ") LIKE '"+value+"%'"; break;
								case "contains":flquery=" "+custom+" and lower("+filterfield+ ") LIKE '%"+value+"%'"; break;
								case "doesnotcontain":flquery=" "+custom+" and lower("+filterfield+ ") NOT LIKE '%"+value+"%'"; break;
								case "endswith":flquery=" "+custom+" and lower("+filterfield+ ") LIKE '%"+value+"'"; break;
								case "neq":flquery=" "+custom+" and lower("+filterfield+ ") != '"+value+"'"; break;
								case "eq":flquery=" "+custom+" and " + " "+ ((fb==true) ? filterfield : (" lower("+filterfield+ ")")) +"  = '"+value+"'"; break;
								case "gte":flquery=" "+custom+" and lower("+filterfield+ ") >= '"+value+"'"; break;
							}							
						}
						else{
							switch(operator){
								case "startswith":flquery=" Where lower("+filterfield+ ") LIKE '"+value+"%'"; break;
								case "contains":flquery=" Where lower("+filterfield+ ") LIKE '%"+value+"%'"; break;
								case "doesnotcontain":flquery=" Where lower("+filterfield+ ") NOT LIKE '%"+value+"%'"; break;
								case "endswith":flquery=" Where lower("+filterfield+ ") LIKE '%"+value+"'"; break;
								case "neq":flquery=" Where lower("+filterfield+ ") != '"+value+"'"; break;
								case "eq":flquery=" Where "+ ((fb==true) ? filterfield : (" lower("+filterfield+ ")"))+" = '"+value+"'"; break;
								case "gte":flquery=" Where lower("+filterfield+ ") >= '"+value+"'"; break;
							}
						}
						
					}
					
				}
			
			}
			
			if(groupfield.isEmpty()){
				group="";
			}
			else{
				group="group by "+groupfield+" "+groupdir+"";
			}
			
			if(field.isEmpty()){
				order="order by t.id desc";
			}
			else{
				order="order by "+multiOrde.substring(0,multiOrde.length()-1)+"";
			}
					
			
			String query="from "+tablename+" t  "+flquery+"  "+group+" "+order+"";
			
			System.out.println("query " +query);
			
			if(isspecial.isEmpty()){
				Query hql = sessionFactory.getCurrentSession().createQuery(query);
				hql.setFirstResult(skip);
				hql.setMaxResults(take);
				List<Object> rlist = hql.list();
				  //List list = hql.list();
				//sessionFactory.getCurrentSession().flush();
				
				return rlist;
			}
			else {
				Query  nquery= sessionFactory.getCurrentSession().createSQLQuery(isspecial);
				List<Object> nlist=nquery.list();
				
				return nlist;
			}			
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	

	@Override
	public int resulsetcount(String request,String tablename) {
		try{			
		//	Gson gson = new Gson ();	    
			
			String field="";
			String order="";
			String dir="";
			String sort="";
			String group="";
			JSONObject filter=null;
			String org="";
			String custom="";
			String isspecial="";
			System.out.println("req "+request);
			System.out.println("req "+tablename);
			JSONObject req= new JSONObject(request);				
			
			if(req.has("filter")){
				if(!req.isNull("filter")){
					filter=req.getJSONObject("filter");	
				}
			}
		
			if(req.has("custom")){
				custom=req.getString("custom");
			}
			System.out.println("req "+request);
			System.out.println("group "+group);
			System.out.println("sort "+sort);
			System.out.println("filter "+filter);

			
			String filterfield="";
			String operator="";
			String value="";
			String flquery="";
			if(custom.length()>0){
				flquery=custom;
			}
			if(req.has("isspecial")){
				isspecial=req.getString("isspecial");
			}
			if(filter!=null){
				
				JSONObject fltr= filter;		
				
				String logic=fltr.getString("logic");
				//String filters=fltr.getString("filters");
			
				JSONArray arr= fltr.getJSONArray("filters");
				for(int i=0; i<arr.length();i++){
					String str=arr.get(i).toString();
					JSONObject srt= new JSONObject(str);
					boolean  fb=false;
					if(srt.isNull("field")){
						filterfield="";	
					}
					else{
						filterfield=srt.getString("field");	
					}
					if(srt.isNull("operator")){
						operator="";
					}
					else{
						operator=srt.getString("operator");
					}
					if(srt.isNull("value")){
						value="";
					}
					else{
					    if (srt.get("value") instanceof Boolean){
                            value=(srt.getBoolean("value") ? "1" : "0");
                            fb=true;
                        }
                        else{
                            value=String.valueOf(srt.get("value")).toLowerCase();
                        }
					}
					if(i>0){
						
						switch(operator){
							case "startswith":flquery=flquery+  " "+logic+" lower("+filterfield+ ") LIKE '"+value+"%'"; break;
							case "contains":flquery=flquery+  " "+logic+" lower("+filterfield+ ") LIKE '%"+value+"%'"; break;
							case "doesnotcontain":flquery=flquery+  " "+logic+" lower("+filterfield+ ") NOT LIKE '%"+value+"%'"; break;
							case "endswith":flquery=flquery+  " "+logic+" lower("+filterfield+ ") LIKE '%"+value+"'"; break;
							case "neq":flquery=flquery+  " "+logic+" lower("+filterfield+ ") != '"+value+"'"; break;
							case "eq":flquery=flquery+  " "+logic+ " "+ ((fb==true) ? filterfield : (" lower("+filterfield+ ")")) + " = '"+value+"'"; break;
							case "gte":flquery=flquery+  " "+logic+" lower("+filterfield+ ") >="+value+""; break;
						}						
					}
					else{
						if(custom.length()>0){
							switch(operator){
								case "startswith":flquery=" "+custom+" and lower("+filterfield+ ") LIKE '"+value+"%'"; break;
								case "contains":flquery=" "+custom+" and lower("+filterfield+ ") LIKE '%"+value+"%'"; break;
								case "doesnotcontain":flquery=" "+custom+" and lower("+filterfield+ ") NOT LIKE '%"+value+"%'"; break;
								case "endswith":flquery=" "+custom+" and lower("+filterfield+ ") LIKE '%"+value+"'"; break;
								case "neq":flquery=" "+custom+" and lower("+filterfield+ ") != '"+value+"'"; break;
								case "eq":flquery=" "+custom+" and " + " "+ ((fb==true) ? filterfield : (" lower("+filterfield+ ")")) +"  = '"+value+"'"; break;
								case "gte":flquery=" "+custom+" and lower("+filterfield+ ") >= '"+value+"'"; break;
							}							
						}
						else{
							switch(operator){
								case "startswith":flquery=" Where lower("+filterfield+ ") LIKE '"+value+"%'"; break;
								case "contains":flquery=" Where lower("+filterfield+ ") LIKE '%"+value+"%'"; break;
								case "doesnotcontain":flquery=" Where lower("+filterfield+ ") NOT LIKE '%"+value+"%'"; break;
								case "endswith":flquery=" Where lower("+filterfield+ ") LIKE '%"+value+"'"; break;
								case "neq":flquery=" Where lower("+filterfield+ ") != '"+value+"'"; break;
								case "eq":flquery=" Where "+ ((fb==true) ? filterfield : (" lower("+filterfield+ ")"))+" = '"+value+"'"; break;
								case "gte":flquery=" Where lower("+filterfield+ ") >= '"+value+"'"; break;
							}
						}
						
					}					
				}				
			}

			String query="select count(*) from "+tablename+" t "+flquery+" ";
			Query hql = sessionFactory.getCurrentSession().createQuery(query);
	    	int count = Integer.parseInt(hql.list().get(0).toString());			
			return count;
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
	
	public Object getNativeSQLResult(String queryStr, String type){
				
		try{
			if("insert".equals(type)){
				Query query=sessionFactory.getCurrentSession().createSQLQuery(queryStr);
				int numberOfRowsAffected = query.executeUpdate();
				try{
					return true;
				}
				catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
			else if("list".equals(type)){
				System.out.println("qqq"+queryStr);
				Query query=sessionFactory.getCurrentSession().createSQLQuery(queryStr);
				try{
					return query.list();
				}
				catch(Exception e){
					return null;
				}
				
			}
			else if("update".equals(type)){
				System.out.println("qqq"+queryStr);
				Query query=sessionFactory.getCurrentSession().createSQLQuery(queryStr);
				try{
					return query.executeUpdate();
				}
				catch(Exception e){
					return null;
				}
				
			}
			else if("totalSum".equals(type)){
				Query query=sessionFactory.getCurrentSession().createSQLQuery(queryStr);
				try{
					return query.list().get(0);
				}
				catch(Exception e){
					return null;
				}

			}
			else if("delete".equals(type)){
				Query query=sessionFactory.getCurrentSession().createSQLQuery(queryStr);
				int numberOfRowsAffected = query.executeUpdate();
				try{
					return true;
				}
				catch(Exception e){
					e.printStackTrace();
					return null;
				}
			}
			else {
				return null;
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public Object PeaceCrud(Object obj, String domainname, String method, Long obj_id, int page_val, int maxresult,
			String whereclause) {
		try{
    		Query query=null;
    		if("save".equals(method)){
    			try{
    				sessionFactory.getCurrentSession().saveOrUpdate(obj);
    				return true;
    			}
    			catch (ConstraintViolationException aldaa){
    				aldaa.printStackTrace();
    				
    				return false;
    			}
    			
        	}
    		else if("update".equals(method)){
    			try{    				
    				sessionFactory.getCurrentSession().update(obj);
    				return true;
    			}
    			catch (ConstraintViolationException aldaa){
    				System.out.println("ooou laitai");
    				aldaa.printStackTrace();
    				
    				return false;
    			}
    		}
        	else if ("delete".equals(method)){
        		
    			if(whereclause!=null){
        			
        			query= sessionFactory.getCurrentSession().createQuery("delete from "+domainname+"  dname where dname."+whereclause+"=:obj_id");
        			query.setParameter("obj_id", obj_id);
        			System.out.println(query);
        		}
        		else{
        			query= sessionFactory.getCurrentSession().createQuery("delete from "+domainname+"  dname where dname.id=:obj_id");
            		query.setParameter("obj_id", obj_id);
            		System.out.println(query);
        		}
    			//List list = query.list();
        		int qresult = query.executeUpdate();
        	}
        	else if ("multidelete".equals(method)){
        		
    			if(whereclause!=null){        			
        			query= sessionFactory.getCurrentSession().createQuery("delete from "+domainname + " " + whereclause);
        		}
        		else{
        			query= sessionFactory.getCurrentSession().createQuery("delete from "+domainname+"  dname where dname.id=:obj_id");
            		query.setParameter("obj_id", obj_id);
        		}
        		int qresult = query.executeUpdate();
        	}
        	else if("list".equals(method)){
        		if(whereclause!=null){
        			
        			query = sessionFactory.getCurrentSession().createQuery(whereclause);
        			System.out.println(query);
        		}
        		else{
        			query = sessionFactory.getCurrentSession().createQuery("from "+domainname+" objlist  order by objlist.id desc ");
        			System.out.println(query);
        		}
        		int pval = page_val-1;
        		query.setFirstResult(maxresult*pval);
        		query.setMaxResults(maxresult);
        		
        		List<Object> robj = query.list();
        		query = null;
        	//	sessionFactory.getCurrentSession().flush();
        		//session.getCurrentSession().clear();
        		return robj;
        	}
        	else if("current".equals(method)){
        		query=sessionFactory.getCurrentSession().createQuery("from "+domainname+" t where t.id=:obj_id");
        		query.setParameter("obj_id", obj_id);
        		Object robj = query.list().get(0);
        		System.out.println(query);
        		return robj;
        		
        	}
        	else if("calculatepage".equals(method)){
        		if(whereclause==null){
        			int resultInt =  ((Long)sessionFactory.getCurrentSession().createQuery("select count(*) from "+domainname+"").uniqueResult()).intValue();
            		System.out.println("ene bol niit bichlegiin too "+resultInt);
            		
            		sessionFactory.getCurrentSession().flush();
            		if(resultInt%maxresult==0){
            			return resultInt/maxresult;
            		}
            		else{
            			return resultInt/maxresult+1;
            		}
        		}
        		else{
        			int resultInt =  sessionFactory.getCurrentSession().createQuery(whereclause).list().size();
            		System.out.println("ene bol niit bichlegiin too "+resultInt);
            		query=null;
            		sessionFactory.getCurrentSession().flush();
            		if(resultInt%maxresult==0){
            			return resultInt/maxresult;
            		}
            		else{
            			return resultInt/maxresult+1;
            		}
        		}	
        	}
        	else if ("countrecord".equalsIgnoreCase(method)){
        		if(whereclause==null){
        			long resultInt =  ((Long)sessionFactory.getCurrentSession().createQuery("select count(*) from "+domainname+"").uniqueResult()).intValue();
            		System.out.println("ene bol niit bichlegiin too "+resultInt);
            		query=null;
            		sessionFactory.getCurrentSession().flush();
            		return resultInt;
        		}
        		else{
        			long resultInt =  ((Long) sessionFactory.getCurrentSession().createQuery(whereclause).uniqueResult()).intValue();
            		System.out.println("ene bol niit bichlegiin too "+resultInt);
            		
            		sessionFactory.getCurrentSession().flush();
            		//session.getCurrentSession().clear();
            		return resultInt;
        		}
        	}
    		
    		return 1;		
    	}
    	catch (Exception e){
    		e.printStackTrace();
    		return 0;
    	}
	}



	@Override
	public List<?> jData(Integer pageSize, Integer skip, String orderStr, String searchStr,
			String domain) {
		
	
		
		String order=null;
		
		if(orderStr!=null){
			order=orderStr;
		}
		String query="";
		if(domain!=null){
			query="from "+domain;
		}
		if(searchStr!=null){
			query=query +" "+searchStr;
		}
		if(order!=null){
			query=query +" "+order;
		}
		
		
		System.out.println("query: "+query);
		
		Query hql = sessionFactory.getCurrentSession().createQuery(query);
		hql.setFirstResult(skip);
		hql.setMaxResults(pageSize);
		List<Object> rlist = hql.list();
		return rlist;
	}
	
	@Override
	public int jDataCount(String searchStr, String domain) {
	
		String query="select count(*) from "+domain+" t "+searchStr+" ";
		Query hql = sessionFactory.getCurrentSession().createQuery(query);
    	int count = Integer.parseInt(hql.list().get(0).toString());			
		return count;
		
	}


	@Override
	public LutUser findByUserName(String id) {
		Query query=null;
		query=sessionFactory.getCurrentSession().createQuery("from LutUser t where t.username=:id");
		query.setParameter("id", id);
		LutUser robj = (LutUser) query.list().get(0);		
		return robj;
	}
	  
}