package com.ncods.pagedemo.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 所有的都在一起
 * 
 * @author xiongzj
 *
 */
@WebServlet("/allinone")
public class AllinoneServlet extends HttpServlet {

	private static final long serialVersionUID = 7876603184611557963L;
	
	DruidDataSource dataSource;

	@Override
	public void init() throws ServletException {
		// 初始化时，创建数据源
		dataSource = new DruidDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:" + AllinoneServlet.class.getResource("/") + "H2DATA");
		dataSource.setUsername("dw");
		dataSource.setPassword("dw");
		dataSource.setTestWhileIdle(false);
		System.out.println("AllinoneServlet inited");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		try {
			// 1. 获取请求参数
			request.setCharacterEncoding("UTF-8");

			int pageNum = 1;
			int pageSize = 10;
			String pNum = request.getParameter("pageNum");
			if (pNum != null && !"".endsWith(pNum.trim())) {
				pageNum = Integer.parseInt(pNum);
			}
			request.setAttribute("pageNum", pageNum);

			String openDateStart = request.getParameter("openDateStart");
			String openDateEnd = request.getParameter("openDateEnd");
			String openbrnCode = request.getParameter("openbrnCode");
			String openbrnName = request.getParameter("openbrnName");

			request.setAttribute("openDateStart", openDateStart);
			request.setAttribute("openDateEnd", openDateEnd);
			request.setAttribute("openbrnCode", openbrnCode);
			request.setAttribute("openbrnName", openbrnName);

			// 2. 连接数据库
//			String driverClassName = "org.h2.Driver";
//			String url = "jdbc:h2:" + AllinoneServlet.class.getResource("/") + "H2DATA";
//			String username = "dw";
//			String password = "dw";
//			Class.forName(driverClassName);
//			Connection conn = DriverManager.getConnection(url, username, password);
			Connection conn = dataSource.getConnection();

			// 3. 查询数据
			// 拼装条件sql
			StringBuilder sb = new StringBuilder();
			List<Object> paramList = new ArrayList<Object>();
			if (openDateStart != null && !"".endsWith(openDateStart.trim())) {
				sb.append(" AND OPENDATE >= ?");
				paramList.add(openDateStart);
			}
			if (openDateEnd != null && !"".endsWith(openDateEnd.trim())) {
				sb.append(" AND OPENDATE <= ?");
				paramList.add(openDateEnd);
			}
			if (openbrnCode != null && !"".endsWith(openbrnCode.trim())) {
				sb.append(" AND OPENBRN = ?");
				paramList.add(openbrnCode);
			}
			if (openbrnName != null && !"".endsWith(openbrnName.trim())) {
				sb.append(" AND BRNNAME LIKE ?");
				paramList.add("%" + openbrnName + "%");
			}

			// 3.1 查总条数
			String sql = "SELECT COUNT(1) FROM F_DEP_DEPE_RICHMST WHERE 1=1 " + sb.toString();
			PreparedStatement ps = conn.prepareStatement(sql);
			if (paramList.size() > 0) {
				for (int i = 0; i < paramList.size(); i++) {
					ps.setObject(i + 1, paramList.get(i));
				}
			}
			ResultSet rs = ps.executeQuery();
			long totalCnt = 0;
			if (rs.next()) {
				totalCnt = rs.getLong(1);
			}
			rs.close();
			ps.close();

			if (totalCnt > 0) {
				int pageNumMax = (int) totalCnt / pageSize + (totalCnt % pageSize == 0 ? 0 : 1);
				request.setAttribute("pageNumMax", pageNumMax);

				// 3.2 分页查询数据
				String sql2 = "SELECT * FROM F_DEP_DEPE_RICHMST WHERE 1=1 " + sb.toString()
						+ " ORDER BY OWNCLT, RICHNBR" + " LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize);
				PreparedStatement ps2 = conn.prepareStatement(sql2);
				if (paramList.size() > 0) {
					for (int i = 0; i < paramList.size(); i++) {
						ps2.setObject(i + 1, paramList.get(i));
					}
				}
				ResultSet rs2 = ps2.executeQuery();
				List<Object[]> list = new ArrayList<Object[]>();
				ResultSetMetaData metaData = rs2.getMetaData();
				int colCnt = metaData.getColumnCount();
				while (rs2.next()) {
					Object[] obj = new Object[colCnt];
					for (int i = 0; i < colCnt; i++) {
						obj[i] = rs2.getObject(i + 1);
					}
					list.add(obj);
				}
				rs2.close();
				ps2.close();

				request.setAttribute("list", list); // 将查询结果放进requset在jsp中取出
			} 

			conn.close(); // 关闭数据库连接

			// 4. 转发到jsp页面
			request.getRequestDispatcher("allinone.jsp").forward(request, resp);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	public void destroy() {
		dataSource.close();
	}
}
