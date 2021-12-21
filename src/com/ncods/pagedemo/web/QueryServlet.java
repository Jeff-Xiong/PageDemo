package com.ncods.pagedemo.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ncods.pagedemo.Page;
import com.ncods.pagedemo.Utils;
import com.ncods.pagedemo.service.Service;
import com.ncods.pagedemo.service.ServiceFactory;

/**
 * 查询
 */
@WebServlet("/query")
public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = 7404650717706171101L;

	private Service service;

	@Override
	public void init() throws ServletException {
		this.service = ServiceFactory.getService();
		System.out.println("QueryServlet inited");
	}

	private Page createPage(HttpServletRequest request) {
		int pageNum = -1;
		int pageSize = 10;

		String pageNumStr = request.getParameter("pageNum");
		String pageSizeStr = request.getParameter("pageSize");
		if (Utils.isNotBlank(pageNumStr)) {
			try {
				pageNum = Integer.parseInt(pageNumStr);
			} catch (Exception e) {
			}
		}
		if (Utils.isNotBlank(pageSizeStr)) {
			try {
				int ps = Integer.parseInt(pageSizeStr);
				if (ps > 0) {
					pageSize = ps;
				}
			} catch (Exception e) {
			}
		}

		if (pageNum > 0) {
			return new Page(pageNum, pageSize);
		}

		return null;
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		JSONObject res = new JSONObject();

		try {
			
			request.setCharacterEncoding("UTF-8");
			String openDateStart = request.getParameter("openDateStart");
			String openDateEnd = request.getParameter("openDateEnd");
			String openbrnCode = request.getParameter("openbrnCode");
			String openbrnName = request.getParameter("openbrnName");

			Page page = createPage(request);
			List<?> list;
			if (page != null) {
				page = this.service.queryForeignAccount4Page(page, openDateStart, openDateEnd, openbrnCode,
						openbrnName);
				list = page.getDataList();
				res.put("page", page.toSimpleJSONObject());
			} else {
				list = this.service.queryForeignAccount(openDateStart, openDateEnd, openbrnCode, openbrnName);
			}
			res.put("list", list);
			res.put("succ", true);
		} catch (Exception e) {
			res.put("succ", false);
			res.put("msg", e.getMessage());
		}

		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.append(res.toJSONString());
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
