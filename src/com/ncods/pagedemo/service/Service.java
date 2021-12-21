package com.ncods.pagedemo.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import com.ncods.pagedemo.Page;

public interface Service {

	/**
	 * 不分页查询，用于前台分页
	 */
	List<HashMap<String, Object>> queryForeignAccount(String openDateStart, String openDateEnd, String openbrnCode,
			String openbrnName) throws SQLException;

	/**
	 * 分页查询数据
	 * @param page 分页信息
	 */
	Page queryForeignAccount4Page(Page page, String openDateStart, String openDateEnd, String openbrnCode,
			String openbrnName) throws SQLException;
}
