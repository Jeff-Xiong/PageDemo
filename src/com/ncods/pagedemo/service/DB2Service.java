package com.ncods.pagedemo.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ncods.pagedemo.Page;
import com.ncods.pagedemo.Utils;
import com.ncods.pagedemo.dao.Dao;

/**
 * 业务访问对象
 * @author Administrator
 *
 */
public class DB2Service implements Service {
	private Dao dao;

	public DB2Service() {
		this.dao = new Dao();
	}

	public DB2Service(Dao dao) {
		this.dao = dao;
	}
	
	/**
	 * 拼装SQL
	 * @param sqlCls  
	 * 		0 - 所有数据 sql
	 * 		1 - count   sql
	 * 		2 - 分页    sql
	 * @return
	 */
	private Object[] buildSql(StringBuilder sb, String openDateStart, String openDateEnd,
			String openbrnCode, String openbrnName, int sqlCls, Page page) {
		if(sqlCls == 1) {
			sb.append("SELECT COUNT(1) ");
		} else {
			if(sqlCls == 0) {
				sb.append("SELECT\r\n");
			} else {
				sb.append("SELECT * FROM (\r\n"
						+ "SELECT\r\n"
						+ "          ROWNUMBER() OVER(ORDER BY A.OWNCLT, A.RICHNBR) ROWNUM,\r\n");
			}
			sb.append("NVL(C.NAMEEX, B.CNAME) AS CNAME\r\n"
				+ ",A.OWNCLT AS OWNCLT\r\n"
				+ ",A.RICHNBR AS RICHNBR\r\n"
				+ ",NVL(BZ.CCYNAME, A.CCYNBR) AS CCYNBR\r\n"
				+ ",A.OPENDATE AS OPENDATE\r\n"
				+ ",A.OPENBRN AS OPENBRN\r\n"
				+ ",NVL(DB.BRNNAME, ' ') AS BRNNAME\r\n"
				+ ",CASE WHEN A.SLEEPTAG='Y' THEN 'Y' ELSE 'N' END AS SLEEPTAG\r\n"
				+ ",(SELECT PM.PARMVALUE FROM DW.F_CM_PARM PM WHERE PM.PARENTCODE = 'RCRICHSTATUS' AND PM.PARMCODE = A.RICHSTATUS) AS RICHSTATUS\r\n");
		}
		sb.append("FROM DW.F_DEP_DEPE_RICHMST A\r\n"
				+ "LEFT JOIN DW.F_CI_CIE_COMPANY B ON A.OWNCLT = B.CLTNBR\r\n"
				+ "LEFT JOIN DW.F_CI_CIE_NAMEEX C ON A.OWNCLT = C.CLTNBR\r\n"
				+ "LEFT JOIN DW.F_CM_CURRENCY BZ ON A.CCYNBR = BZ.CCYNBR\r\n"
				+ "LEFT JOIN DW.DIM_BRANCH DB ON DB.BRNNBR = A.OPENBRN\r\n"
				+ "LEFT JOIN\r\n"
				+ "                (\r\n"
				+ "                 SELECT OWNCLT\r\n"
				+ "                 FROM DW.F_DEP_DEPE_RICHMST\r\n"
				+ "                 WHERE CCYNBR = '156'\r\n"
				+ "                 AND RICHSTATUS <> 'C'\r\n"
				+ "                 ) T\r\n"
				+ "                 ON A.OWNCLT = T.OWNCLT\r\n"
				+ "WHERE A.CCYNBR <> '156'\r\n"
				+ "AND T.OWNCLT IS NULL\r\n");
		List<Object> paramList = new ArrayList<Object>();
		if(Utils.isNotBlank(openDateStart)) {
			sb.append(" AND A.OPENDATE >= ?");
			paramList.add(openDateStart);
		}
		if(Utils.isNotBlank(openDateEnd)) {
			sb.append(" AND A.OPENDATE <= ?");
			paramList.add(openDateEnd);
		}
		if(Utils.isNotBlank(openbrnCode)) {
			sb.append(" AND A.OPENBRN = ?");
			paramList.add(openbrnCode);
		}
		if(Utils.isNotBlank(openbrnName)) {
			sb.append(" AND DB.BRNNAME LIKE ?");
			paramList.add("%"+openbrnName+"%");
		}
		if(sqlCls == 0) {
			sb.append(" ORDER BY A.OWNCLT, A.RICHNBR");
		} else if(sqlCls == 2) {
			sb.append(") WHERE ROWNUM > "+page.getStartIdx()+" FETCH FIRST "+page.getPageSize()+" ROWS ONLY");
		}
		
		return paramList.toArray();
	}
	
	@Override
	public List<HashMap<String, Object>> queryForeignAccount(String openDateStart, String openDateEnd, 
			String openbrnCode, String openbrnName) throws SQLException {
		StringBuilder sb = new StringBuilder();
		Object[] params = this.buildSql(sb, openDateStart, openDateEnd, openbrnCode, openbrnName, 0, null);
		return this.getDao().query4Map(sb.toString(), params);
	}
	
	@Override
	public Page queryForeignAccount4Page(Page page, String openDateStart, String openDateEnd, String openbrnCode,
			String openbrnName) throws SQLException {
		StringBuilder sb = new StringBuilder();
		Object[] params = this.buildSql(sb, openDateStart, openDateEnd, openbrnCode, openbrnName, 1, null);
		List<Long> l = this.getDao().query(sb.toString(), (objs, heads) -> {
			return ((Number) objs[0]).longValue();
		}, params);
		Long totalNum = l.get(0);
		page.setTotalNum(totalNum);
		if (totalNum > 0) {
			StringBuilder sb2 = new StringBuilder();
			params = this.buildSql(sb2, openDateStart, openDateEnd, openbrnCode, openbrnName, 2, page);
			List<HashMap<String, Object>> list = this.getDao().query4Map(sb2.toString(), params);
			page.setDataList(list);
		}
		return page;
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	
	
	
}
