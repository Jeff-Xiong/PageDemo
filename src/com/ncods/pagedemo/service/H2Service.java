package com.ncods.pagedemo.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ncods.pagedemo.Page;
import com.ncods.pagedemo.Utils;
import com.ncods.pagedemo.dao.Dao;

public class H2Service implements Service {

	private Dao dao;

	public H2Service() {
		this.dao = new Dao();
	}

	/**
	 * 拼装查询条件
	 * 
	 * @param whereand 有查询条件时，会先加上whereand，再加条件
	 * @return
	 */
	private Object[] assembleParam(StringBuilder sb, String whereand, String openDateStart, String openDateEnd,
			String openbrnCode, String openbrnName) {
		List<String> conditionList = new ArrayList<String>();
		List<Object> paramList = new ArrayList<Object>();
		if (Utils.isNotBlank(openDateStart)) {
			conditionList.add("OPENDATE >= ?");
			paramList.add(openDateStart);
		}
		if (Utils.isNotBlank(openDateEnd)) {
			conditionList.add("OPENDATE <= ?");
			paramList.add(openDateEnd);
		}
		if (Utils.isNotBlank(openbrnCode)) {
			conditionList.add("OPENBRN = ?");
			paramList.add(openbrnCode);
		}
		if (Utils.isNotBlank(openbrnName)) {
			conditionList.add("BRNNAME LIKE ?");
			paramList.add("%" + openbrnName + "%");
		}
		if (conditionList.size() > 0) {
			sb.append(whereand);
			for (int i = 0; i < conditionList.size(); i++) {
				if (i > 0) {
					sb.append(" AND ");
				}
				sb.append(conditionList.get(i));
			}
		}
		return paramList.toArray();
	}

	@Override
	public Page queryForeignAccount4Page(Page page, String openDateStart, String openDateEnd, String openbrnCode,
			String openbrnName) throws SQLException {
		// 获取总条数
		StringBuilder totalCntSqlSb = new StringBuilder("SELECT COUNT(1) FROM F_DEP_DEPE_RICHMST");
		Object[] params = this.assembleParam(totalCntSqlSb, " WHERE ", openDateStart, openDateEnd, openbrnCode,
				openbrnName);
		List<Long> l = this.getDao().query(totalCntSqlSb.toString(), (objs, heads) -> {
			return ((Number) objs[0]).longValue();
		}, params);
		Long totalNum = l.get(0);
		page.setTotalNum(totalNum);
		// 总条数不为0时，查询数据
		if (totalNum > 0) {
			StringBuilder sb = new StringBuilder("SELECT * FROM F_DEP_DEPE_RICHMST");
			params = this.assembleParam(sb, " WHERE ", openDateStart, openDateEnd, openbrnCode, openbrnName);
			sb.append(" ORDER BY OWNCLT, RICHNBR");
			sb.append(" LIMIT " + page.getPageSize() + " OFFSET " + page.getStartIdx()); // H2 分页
			List<HashMap<String, Object>> list = this.getDao().query4Map(sb.toString(), params);
			page.setDataList(list);
		}
		return page;
	}

	@Override
	public List<HashMap<String, Object>> queryForeignAccount(String openDateStart, String openDateEnd, String openbrnCode,
			String openbrnName) throws SQLException {
		StringBuilder sb = new StringBuilder("SELECT * FROM F_DEP_DEPE_RICHMST");
		Object[] params = this.assembleParam(sb, " WHERE ", openDateStart, openDateEnd, openbrnCode, openbrnName);
		sb.append(" ORDER BY OWNCLT, RICHNBR");
		return this.getDao().query4Map(sb.toString(), params);
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

}
