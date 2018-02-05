package com.maqiao.was.fmktag.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
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
	/** 属性列如果为小于0则无，如果大于等于，则某行记录为属性列 */
	int attrcolumn = -1;
	/** 过滤下标数组 */
	int[] filtersuffix = {};
	/** 进行排序 */
	String orderby = null;

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
		filtersuffix = getArrayInteger("filtersuffix");
		orderby = getString("orderby");
		ProjectInitialization();
	}
	/**
	 * 本地项目接收值
	 */
	abstract void ProjectInitialization();
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
			if (paramName.equals(key)) return paramValue;
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

	/**
	 * 抽取int[] 如果没有则直接返回空
	 * @param key String
	 * @return int[]
	 */
	protected int[] getArrayInteger(String key) {
		Object obj = getParams(key);
		int[] arr = {};
		if (obj == null) return arr;
		if (!(obj instanceof SimpleSequence)) {
			System.out.println("错误，无法提取数组:" + obj.toString());
			return arr;
		}
		try {
			SimpleSequence arrayModel = (SimpleSequence) obj;
			int len = arrayModel.size();
			int[] newArr = new int[len];
			for (int i = 0; i < len; i++) {
				int value = ((TemplateNumberModel) arrayModel.get(i)).getAsNumber().intValue();
				newArr[i] = value;
			}
			return newArr;
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return arr;
		}
	}

	public final Map getParams() {
		return params;
	}

	public final void setParams(Map params) {
		this.params = params;
	}
	/**
	 * 后期处理 过滤属性行、过滤下标组、排序
	 * @param list List<BeanLine>
	 */
	public final void Postprocessing(List<BeanLine> list) {
		/* 移除排队属性行 */
		removeAttributeRow(list);
		/* 移除过滤行 */
		removeFiltersuffixRow(list);
		/* 排序 */
		orderby(list);
	}
	/**
	 * 移除属性行
	 * @param list List<BeanLine>
	 */
	private final void removeAttributeRow(List<BeanLine> list) {
		if (list == null || list.size() == 0) return;
		if (attrcolumn > -1) list.remove(attrcolumn);
	}

	/**
	 * 移除过滤行
	 * @param list List<BeanLine>
	 */
	private final void removeFiltersuffixRow(List<BeanLine> list) {
		if (list == null || list.size() == 0 || filtersuffix == null) return;
		for (int i = 0; i < filtersuffix.length; i++)
			list.remove(filtersuffix[i]);
	}

	/** 排序验证 */
	static final String PARAM_OrderByExp = "^[\\s]?(v\\d+)[\\s]?([\\s]+((desc)|(asc))[\\s]?)?$";
	/**
	 * 排序
	 * @param list List<BeanLine>
	 */
	private final void orderby(List<BeanLine> list) {
		if (orderby != null && orderby.length() > 0 && orderby.matches(PARAM_OrderByExp))
			list = setOrderby(list);
	}

	/**
	 * 排序
	 * @param list List<BeanLine>
	 * @param orderby String
	 * @return List<BeanLine>
	 */
	private final List<BeanLine> setOrderby(List<BeanLine> list) {
		if (list == null || list.size() == 0) return list;
		if (orderby == null || orderby.length() == 0 || !orderby.matches(PARAM_OrderByExp)) return list;
		String key = "";
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(orderby);
		if (matcher.find()) key = matcher.group(0);
		else return list;
		if (key == null || key.length() == 0) return list;
		final int suffix = Integer.valueOf(key).intValue();
		//if (suffix > ) return list;
		boolean reverse = orderby.indexOf("desc") > 0;
		/*
		 * jdk1.8
		 * list.sort((BeanLine h1, BeanLine h2) -> h1.get(suffix).compareTo(h2.get(suffix)));
		 */
		/*
		 * jdk1.2
		 */
		Collections.sort(list, new Comparator<BeanLine>() {
			public int compare(BeanLine arg0, BeanLine arg1) {
				if (arg0 == null || arg0.get(suffix) == null || arg1 == null || arg1.get(suffix) == null) return 0;
				if (reverse) return arg1.get(suffix).compareTo(arg0.get(suffix));
				return arg0.get(suffix).compareTo(arg1.get(suffix));
			}
		});
		return list;
	}
}
