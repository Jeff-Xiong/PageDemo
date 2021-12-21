package com.ncods.pagedemo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

/**
 * 数据访问对象Data Access Object
 * 
 * @author xiongzj
 *
 */
public class Dao {

	private DataSource dataSource;

	public Dao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Dao() {
		this.dataSource = MyDataSource.getInstance();
	}

	public List<Object[]> query4Array(String sql, Object... params) throws SQLException {
		return query(sql, (objs, heads) -> {
			return objs;
		}, params);
	}

	public List<HashMap<String, Object>> query4Map(String sql, Object... params) throws SQLException {
		return query(sql, (objs, heads) -> {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < heads.length; i++) {
				String key = heads[i];
				Object val = objs[i];
				map.put(key, val);
			}
			return map;
		}, params);
	}

	public <T> List<T> query(String sql, Transformer<T> trans, Object... params) throws SQLException {
		System.out.println(sql);
		try (Connection conn = this.dataSource.getConnection(); PreparedStatement stat = conn.prepareStatement(sql);) {
			if (!(params == null || params.length == 0)) {
				for (int i = 0; i < params.length; i++) {
					stat.setObject(i + 1, params[i]);
				}
			}
			ResultSet rs = stat.executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int colCnt = metaData.getColumnCount();
			String[] heads = new String[colCnt];
			for (int i = 1; i <= colCnt; i++) {
				heads[i - 1] = metaData.getColumnLabel(i);
			}
			List<T> res = new ArrayList<T>();
			while (rs.next()) {
				Object[] objs = new Object[colCnt];
				for (int i = 1; i <= colCnt; i++) {
					objs[i - 1] = rs.getObject(i);
				}
				res.add(trans.transformTuple(objs, heads));
			}
			return res;
		} catch (SQLException e) {
			throw e;
		}
	}

}
