package com.maqiao.was.fmktag.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import com.maqiao.was.fmktag.table.dbtxt.BeanLineUtils;

/**
 * 
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public abstract class DBAbstract extends AbstractParams implements InterfaceAccpetVal{
	/** 属性列如果为小于0则无，如果大于等于，则某行记录为属性列 */
	int attrcolumn = -1;
	/** 过滤下标数组 */
	int[] filtersuffix = {};
	/** 进行排序 */
	String orderby = null;
	/** 完整性，用于补齐，对null换成空字符串 */
	boolean comple = false;
	/** 过滤无效节点 */
	boolean filterinvalid = false;
	/** 去重 */
	boolean distinct = false;
	/** 非null单元过滤两侧空格 */
	boolean istrim = false;
	/**
	 * 得到结果数据
	 * @return List<BeanLine>
	 */
	public abstract List<BeanLine> getList();

	/**
	 * 数据源excel 构造函数
	 * @param request HttpServletRequest
	 * @param params Map
	 */
	public DBAbstract(HttpServletRequest request, Map params) {
		super(request, params);
		super.acceptVal();
	}
	@Override
	public void acceptVal() {
		attrcolumn = getInt(-1,"attrcolumn");
		filtersuffix = Utils.arrayOrder(getArrayInteger("filtersuffix"));
		orderby = getString("orderby");
		comple = getBoolean("comple");
		filterinvalid = getBoolean("filterinvalid");
		distinct = getBoolean("distinct");
		istrim = getBoolean("istrim");
	}
	/**
	 * 得到服务器上当前绝对路径 c:/aaa/root/ 即发布目录
	 * @return String
	 */
	protected String getBasicPath() {
		return Utils.getWebBasePath(request);
	}

	/**
	 * 后期处理 过滤属性行、过滤下标组、排序、自动格式化(补齐空格)、过滤无效节点、删除重复
	 * @param list List<BeanLine>
	 */
	public final void Postprocessing(List<BeanLine> list) {
		if (list == null || list.size() == 0) return;
		/* 移除排队属性行 */
		if(attrcolumn>-1)removeAttributeRow(list);
		/* 移除过滤行 */
		if(filtersuffix.length>0)removeFiltersuffixRow(list);
		/* 非null单元过滤两侧空格 */
		if(istrim)BeanLineUtils.trim(list);
		/* 排序 */
		orderby(list);
		/* 自动格式化 */
		if(comple)BeanLineUtils.format(list);
		/* 是否过滤无效节点 */
		if(filterinvalid)BeanLineUtils.filterInvalid(list);
		/* 去掉重复的节点 */
		if(distinct) {
			 list = list.stream().distinct().collect(Collectors.toList());
		}
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
	 * 移除过滤行 过滤行为指定行
	 * @param list List<BeanLine>
	 */
	private final void removeFiltersuffixRow(List<BeanLine> list) {
		if (list == null || list.size() == 0 || filtersuffix == null) return;
		int i=list.size();
		while(--i>=0) {
			if(Utils.isExist(filtersuffix, i))list.remove(i);
		}
	}

	/** 排序验证 */
	static final String PARAM_OrderByExp = "^[\\s]?(v\\d+)[\\s]?([\\s]+((desc)|(asc))[\\s]?)?$";

	/**
	 * 排序
	 * @param list List<BeanLine>
	 */
	private final void orderby(List<BeanLine> list) {
		if (orderby != null && orderby.length() > 0 && orderby.matches(PARAM_OrderByExp)) list = setOrderby(list);
	}

	/**
	 * 进行排序
	 * @param list List&lt;BeanLine&gt;
	 * @param orderby String
	 * @return List&lt;BeanLine&gt;
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
		if (suffix < 0) return list;
		/* 是否倒序 */
		boolean reverse = orderby.indexOf("desc") > 0;
		return setSorting(list, suffix, reverse);
	}

	static final List<BeanLine> setSorting(List<BeanLine> list, int suffix, boolean reverse) {
		if (BeanLineUtils.isNumericField(list, suffix)) {
			/* 排序列均为数字，则进行按数值进行排序 */
			return BeanLineUtils.sortList(list, suffix, reverse);
		}
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

	public static void main(String[] args) {
		List<BeanLine> list = new ArrayList<>();
		list.add(new BeanLine("0", "a"));
		list.add(new BeanLine("1", "b"));
		list.add(new BeanLine("5", "c"));
		list.add(new BeanLine("3", "e"));
		list.add(new BeanLine("2", "f"));
		list.add(new BeanLine("3", "g"));
		list.add(new BeanLine("1", "h"));
		for (BeanLine e : list) {
			System.out.println("source e:" + e.toString());
		}
		System.out.println("================");
		list = BeanLineUtils.sortList(list, 0, true);
		for (BeanLine e : list) {
			System.out.println("倒序 f:" + e.toString());
		}
		System.out.println("================");
		list = BeanLineUtils.sortList(list, 0, false);
		for (BeanLine e : list) {
			System.out.println("正序 f:" + e.toString());
		}
		System.out.println("================");
	}

}
