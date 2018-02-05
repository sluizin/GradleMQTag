/**
 * 
 */
package com.maqiao.was.fmktag.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.WrappingTemplateModel;

/**
 * * FreeMarker 自定义标签实现输出内容体。
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
@SuppressWarnings("rawtypes")
public final class SjFreemarkerActive implements TemplateDirectiveModel {
	/*
	 * (non-Javadoc)
	 * @see freemarker.template.TemplateDirectiveModel#execute(freemarker.core.Environment, java.util.Map, freemarker.template.TemplateModel[],
	 * freemarker.template.TemplateDirectiveBody)
	 */
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		String format = getString(params, "format");
		/* 初步判断 */
		if (format == null || format.length() == 0) {
			body.render(env.getOut());
			return;
		}
		if (loopVars.length > 1) throw new TemplateModelException("At most one loop variable is allowed.");
		if (body == null) throw new RuntimeException("标签内部至少要加一个空格 missing body");
		List<BeanLine> list = new ArrayList<BeanLine>();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		DBAbstract db = null;
		if ("text".equals(format)) db = new DBCharacterText(request, params);
		if (db == null && "json".equals(format)) db = new DBCharacterJson(request, params);
		if (db == null && "xml".equals(format)) db = new DBCharacterXml(request, params);
		if (db == null && ("xls".equals(format) ||"xlsx".equals(format))) db = new DBInputStreamExcel(request, params);
		
		if (db != null) list = db.getList();
		if (list == null || list.size() == 0) {
			body.render(env.getOut());
			return;
		}
		/* 后期处理 过滤属性行、过滤下标组、排序 */
		db.Postprocessing(list);	
		for (int i = 0, len = list.size(); i < len; i++)
			list.get(i).setRequest(request);
		loopVars[0] = WrappingTemplateModel.getDefaultObjectWrapper().wrap(list);
		body.render(env.getOut());
	}



	public static void main(String[] args) {
		String orderby = " v000 asc ";
		boolean t = orderby.matches(DBAbstract.PARAM_OrderByExp);
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
		if (suffix < 0) return newList;
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
