package com.maqiao.was.fmktag.table;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public abstract class DBAbstract {
	/* 属性列如果为小于0则无，如果大于等于，则某行记录为属性列 */
	int attrcolumn = -1;

	/**
	 * 得到结果数据
	 * @return List<BeanLine>
	 */
	public abstract List<BeanLine> getList();

	/** execute(Environment env, Map params */
	Map params;
	/** request */
	HttpServletRequest request;
	/**
	 * 初始化
	 */
	protected void Init() {
		attrcolumn = getInt("attrcolumn");
	}

	/**
	 * @param key String
	 * @return Object
	 */
	protected Object getParams(final String key) {
		if (params == null || key == null) return null;
		Iterator paramIter = params.entrySet().iterator();
		while (paramIter.hasNext()) {
			Map.Entry ent = (Map.Entry) paramIter.next();
			String paramName = (String) ent.getKey();
			TemplateModel paramValue = (TemplateModel) ent.getValue();
			if (paramName.equals(key)) { return paramValue; }
		}
		return null;
	}

	/**
	 * 抽取String
	 * @param key String
	 * @return String
	 */
	protected String getString(String key) {
		Object obj = getParams(key);
		if (obj == null) return null;
		String result = ((SimpleScalar) obj).getAsString();
		if (result != null) result = result.trim();
		return result;
	}

	/**
	 * 抽取Boolean
	 * @param key String
	 * @return boolean
	 */
	protected boolean getBoolean(String key) {
		Object obj = getParams(key);
		if (obj == null) return false;
		try {
			return ((TemplateBooleanModel) obj).getAsBoolean();
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 抽取int 如果没有则直接返回-1
	 * @param key String
	 * @return int
	 */
	protected int getInt(String key) {
		Object obj = getParams(key);
		if (obj == null) return -1;
		try {
			return ((TemplateNumberModel) obj).getAsNumber().intValue();
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public final Map getParams() {
		return params;
	}

	public final void setParams(Map params) {
		this.params = params;
	}

}
