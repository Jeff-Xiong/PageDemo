package com.ncods.pagedemo;

import java.util.List;

import com.alibaba.fastjson.JSONObject;

public class Page {

	public Page() {
	}

	public Page(int pageNum, int pageSize) {
		this.pageSize = pageSize;
		this.pageNum = pageNum;
	}

	private int pageSize = 10;

	private int pageNum = 1;

	private long totalNum = 0;

	private List<?> dataList;

	public long getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(long totalNum) {
		this.totalNum = totalNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public long getStartIdx() {
		return (pageNum - 1) * (long) pageSize;
	}

	public List<?> getDataList() {
		return dataList;
	}

	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}

	public JSONObject toSimpleJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("pageSize", this.getPageSize());
		obj.put("pageNum", this.getPageNum());
		obj.put("totalNum", this.getTotalNum());
		return obj;
	}

}
