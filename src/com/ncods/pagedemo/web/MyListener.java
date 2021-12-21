package com.ncods.pagedemo.web;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.ncods.pagedemo.dao.MyDataSource;

/**
 * 连接数据库需要耗费时间，程序启动时就连接数据
 * 
 * @author xiongzj
 *
 */
@WebListener
public class MyListener implements ServletContextListener {

	private DataSource dataSource;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("==========contextInitialized==============");
		dataSource = MyDataSource.getInstance();
		try {
			// 测试获取数据库链接
			Connection conn = dataSource.getConnection();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("===========contextDestroyed=============");
		if (dataSource != null) {
			if (dataSource instanceof DruidDataSource) {
				DruidDataSource ds = (DruidDataSource) dataSource;
				ds.close();
			}
		}
	}

}
