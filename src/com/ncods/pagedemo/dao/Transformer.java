package com.ncods.pagedemo.dao;

/**
 * 结果转换器
 * @author Administrator
 *
 */
public interface Transformer<T> {

	public T transformTuple(Object[] objs, String[] heads);
	
}
