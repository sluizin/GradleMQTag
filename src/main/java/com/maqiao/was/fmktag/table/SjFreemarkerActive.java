/**
 * 
 */
package com.maqiao.was.fmktag.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.WrappingTemplateModel;

/**
 * * FreeMarker 自定义标签实现输出内容体。
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
@SuppressWarnings("rawtypes")
public final class SjFreemarkerActive implements TemplateDirectiveModel {
	private static final String PARAM_OrderByExp = "^[\\s]?(v\\d+)[\\s]?([\\s]+((desc)|(asc))[\\s]?)?$";

	/*
	 * (non-Javadoc)
	 * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[],
	 * freemarker.template.TemplateDirectiveBody)
	 */
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		String format=getString(params, "format");
		/* 初步判断 */
		if (format == null || format.length() == 0) {
			body.render(env.getOut());
			return;
		}
		if (loopVars.length > 1) throw new TemplateModelException("At most one loop variable is allowed.");
		if (body == null) throw new RuntimeException("标签内部至少要加一个空格 missing body");
		List<BeanLine> list = new ArrayList<BeanLine>();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		if(format==null || format.length()==0 || "text".equals(format)) {
			DBCharacterText dbCharachterText=new DBCharacterText(request,params);
			list=dbCharachterText.getList();
		}else {
			
			
			
		}
		/*
		switch (type) {
		case 0:
			final String sourceDBPath = request.getSession().getServletContext().getRealPath("") + "/" + sourcefile;
			list = DbtxtUtils.getBeanlineTxt(sourceDBPath, autochange);
			break;
		case 1:
			//HttpServletRequest request2 = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			list = DbtxtUtils.getBeanlineTxtUrl(request, sourcefile, isReadUtf8, autochange);
			break;
		default:
			break;
		}*/
		if (list != null && list.size() > 0) {
			/* 排序 */
			String orderby = getString(params, "orderby");
			if (orderby != null && orderby.length() > 0 && orderby.matches(PARAM_OrderByExp)) list = orderby(list, orderby);
			for(int i=0,len=list.size();i<len;i++)
				list.get(i).setRequest(request);
			loopVars[0] = WrappingTemplateModel.getDefaultObjectWrapper().wrap(list);
		}
		body.render(env.getOut());
	}

	/**
	 * 排序
	 * @param list List<BeanLine>
	 * @param orderby String
	 * @return List<BeanLine>
	 */
	public static final List<BeanLine> orderby(List<BeanLine> list, final String orderby) {
		if (list == null || list.size() == 0) return list;
		if (orderby == null || orderby.length() == 0 || !orderby.matches(PARAM_OrderByExp)) return list;
		String key = "";
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(orderby);
		if (matcher.find()) key = matcher.group(0);
		else return list;
		if (key == null || key.length() == 0) return list;
		final int suffix = Integer.valueOf(key).intValue();
		if (suffix > BeanLine.maxPointI) return list;
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
				if (reverse) return arg1.get(suffix).compareTo(arg0.get(suffix));
				return arg0.get(suffix).compareTo(arg1.get(suffix));
			}
		});
		return list;
	}

	public static void main(String[] args) {
		String orderby = " v000 asc ";
		boolean t = orderby.matches(PARAM_OrderByExp);
		System.out.println("Hello World!:" + t);
	}

	/**
	 * 抽取String
	 * @param params Map
	 * @param key String
	 * @return String
	 */
	private static String getString(Map params, String key) {
		Object obj = getParams(params, key);
		if (obj == null) return null;
		String result = ((SimpleScalar) obj).getAsString();
		if (result != null) result = result.trim();
		return result;
	}

	/**
	 * 抽取Boolean
	 * @param params Map
	 * @param key String
	 * @return boolean
	 */
	private static boolean getBoolean(Map params, String key) {
		Object obj = getParams(params, key);
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
	 * @param params Map
	 * @param key String
	 * @return int
	 */
	private static int getInt(Map params, String key) {
		Object obj = getParams(params, key);
		if (obj == null) return -1;
		try {
			return ((TemplateNumberModel) obj).getAsNumber().intValue();
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * @param params Map
	 * @param key String
	 * @return Object
	 */
	public static final Object getParams(final Map params, final String key) {
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
	 * @param params Map
	 * @param key String
	 * @param classzz Class&lt;?&gt;
	 * @return Object
	 */
	public static final Object getParams(final Map params, final String key, Class<?> classzz) {
		Iterator paramIter = params.entrySet().iterator();
		while (paramIter.hasNext()) {
			Map.Entry ent = (Map.Entry) paramIter.next();
			String paramName = (String) ent.getKey();
			TemplateModel paramValue = (TemplateModel) ent.getValue();
			if (paramName.equals(key)) {
				if (!(paramName != null && paramName.getClass() == classzz)) {
					try {
						throw new TemplateModelException("The \"" + key + "\" parameter " + "must be " + classzz.getName() + ".");
					} catch (TemplateModelException e) {
						e.printStackTrace();
					}
				}
				return paramValue;
			}
		}
		return null;
	}

	/**
	 * 抽取某列的记录数
	 * @param list List<BeanLine>
	 * @param suffix int
	 * @param arrs String[]
	 * @return List<BeanLine>
	 */
	@Deprecated
	public static final List<BeanLine> filter(List<BeanLine> list, final int suffix, final String... arrs) {
		if (list == null || list.size() == 0) return new ArrayList<BeanLine>();
		final int len = list.size();
		List<BeanLine> newList = new ArrayList<BeanLine>(len);
		if (suffix < 0 || suffix > BeanLine.maxPointI) return newList;
		if (arrs == null || arrs.length == 0) return newList;
		/*
		 * jdk1.8
		 * newList=list.stream().filter(a->isExist(arrs,a.get(suffix))).collect(Collectors.toList());
		 */
		/*
		 * jdk1.7
		 */
		BeanLine e = null;
		for (int i = 0; i < len; i++)
			if (isExist(arrs, (e = list.get(i)).get(suffix))) newList.add(e);
		return newList;
	}

	/**
	 * 判断某个关键字在某个数组中
	 * @param arr String[]
	 * @param key String
	 * @return boolean
	 */
	private static final boolean isExist(String[] arr, String key) {
		if (key == null) return false;
		for (int i = 0, len = arr.length; i < len; i++)
			if (key.equals(arr[i])) return true;
		return false;
	}
}
