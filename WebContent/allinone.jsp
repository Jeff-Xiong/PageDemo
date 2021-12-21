<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ALL IN ONE</title>
<style type="text/css">
.paramTable, .dataTable {
	border-collapse: collapse;
	margin: 0 auto;
}

.paramTable td {
	padding: 5px;
}

.paramTable td {
	padding: 5px;
}

.dataTable th {
	background-color: #2F5496;
	color: #FFF;
	height: 40px;
	text-align: center;
	border: 1px solid #B4C6E7;
	padding-left: 5px;
	padding-right: 5px;
}

.dataTable tbody td {
	border: 1px solid #E7E6E6;
	padding-left: 5px;
	padding-right: 5px;
	height: 30px;
}

.dataTable tbody tr:nth-child(odd) td {
	background: #fff;
}

.dataTable tbody tr:nth-child(even) td {
	background: #F4F5F8;
}

.dataTable tfoot td {
	height: 40px;
}
</style>
<script type="text/javascript">
	function toPage(pno){
		document.queryForm.pageNum.value = pno;
		document.queryForm.submit();
	}
</script>
</head>
<body>
	<form name="queryForm" action="allinone" method="post">
		<table class="paramTable">
			<tr>
				<td>账户开立时间：</td>
				<td><input name="openDateStart" type="date" value="${openDateStart}" /> - <input name="openDateEnd" type="date" value="${openDateEnd}" /></td>
				<td rowspan="3"><input type="button" value="查询" onclick="toPage(1);"></td>
			</tr>
			<tr>
				<td>账户开立机构代码：</td>
				<td><input name="openbrnCode" type="text" value="${openbrnCode}" /></td>
			</tr>
			<tr>
				<td>账户开立机构名称：</td>
				<td><input name="openbrnName" type="text" value="${openbrnName}" /></td>
			</tr>
		</table>
		<input type="hidden" name="pageNum" value="${pageNum}" />
	</form>
	<table class="dataTable">
		<thead id="dataHead">
			<tr>
				<th>对公单位名称</th>
				<th>客户号</th>
				<th>账号</th>
				<th>币种</th>
				<th>账户开立时间</th>
				<th>账户开立机构代码</th>
				<th>账户开立机构</th>
				<th>是否睡眠户</th>
				<th>状态</th>
			</tr>
		</thead>
		<tbody id="dataBody">
		  <%List<Object[]> list = (List<Object[]>) request.getAttribute("list");
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {%>
			<tr>
		          <%Object[] objs = list.get(i);
				    for (int c = 0; c < objs.length; c++) {%>
				<td><%=objs[c]==null ? "" :objs[c]%></td>
			      <%}%>
			</tr>
			  <%}
			} else {%>
			<tr><td colspan="9" align="center">无数据</td></tr>
		  <%}%>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="10" align="center">
					<button class="pageBtn" data-type="first" ${pageNum == 1 ? 'disabled="disabled"' : ''} onclick="toPage(1);">首页</button>
					<button class="pageBtn" data-type="prev" ${pageNum == 1 ? 'disabled="disabled"' : ''} onclick="toPage(${pageNum-1});">上一页</button>
					第${pageNum}页/共<span id="pageNumMax">${pageNumMax}</span>页
					<button class="pageBtn" data-type="next" ${pageNum == pageNumMax ? 'disabled="disabled"' : ''} onclick="toPage(${pageNum+1});">下一页</button>
					<button class="pageBtn" data-type="last" ${pageNum == pageNumMax ? 'disabled="disabled"' : ''} onclick="toPage(${pageNumMax});">末页</button>
				</td>
			</tr>
		</tfoot>
	</table>
</body>
</html>