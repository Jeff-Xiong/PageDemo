package com.ncods.pagedemo.service;

import com.ncods.pagedemo.dao.MyDataSource;

public class ServiceFactory {

	public static Service getService() {
		if (MyDataSource.DB_TYPE_H2.equals(MyDataSource.getDbType())) {
			return new H2Service();
		}
		return new DB2Service();
	}

}
