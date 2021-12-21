package com.ncods.pagedemo.dao;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 数据库连接池，使用单例<br>
 * 内部连接池用的是阿里的druid
 * 
 * @author xiongzj
 *
 */
public class MyDataSource {
	
	public static final String DB_TYPE_DB2 = "DB2";
	public static final String DB_TYPE_H2 = "H2";

	private MyDataSource() {
	}

	public static DataSource getInstance() {
		return Holer.dataSource;
	}

	public static String getDbType() {
		return Holer.dbType;
	}

	private static class Holer {
		private static String dbType = MyDataSource.DB_TYPE_DB2;
		private static DruidDataSource dataSource;
		static {
			Properties prop = new Properties();
			try {
				prop.load(MyDataSource.class.getResourceAsStream("/druid.properties"));
				String url = prop.getProperty("druid.url");
				if (url.startsWith("jdbc:h2")) { // 判断是否是h2数据库
					String classPath = MyDataSource.class.getResource("/").toString();
					url = url.replace("${CLASS_PATH}", classPath);
					prop.setProperty("druid.url", url);
					dbType = MyDataSource.DB_TYPE_H2;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			dataSource = new DruidDataSource();
			dataSource.configFromPropety(prop);
		}
	}

}
