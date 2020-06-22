package com.maqiao.was.fmktag.table;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import freemarker.template.SimpleDate;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

/**
 * 从map中提出数据
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractParams  implements InterfaceAccpetVal{

	/** request */
	HttpServletRequest request;
	/** execute(Environment env, Map params */
	Map params;
	/** 默认配置 */
	boolean isdefaultconf=false;
	/**
	 * 数据源excel 构造函数
	 * @param request HttpServletRequest
	 * @param params Map
	 */
	public AbstractParams(HttpServletRequest request, Map params) {
		this.request = request;
		this.params = params;
		 isdefaultconf=Utils.getStaticBoolean(params, false, "isdefaultconf");
		 showMap();
	}
	
	@Override
	public void acceptVal() {
	}
	private void showMap() {
		if(params==null)return;
		Iterator paramIter = params.entrySet().iterator();
		while (paramIter.hasNext()) {
			Map.Entry ent = (Map.Entry) paramIter.next();
			Object obj = ent.getKey();
			if (obj == null) continue;
			String paramName = (String) obj;
			System.out.println("Map["+paramName+"]:"+(TemplateModel) ent.getValue());
		}
	}
	/**
	 * 抽取int[] 如果没有则直接返回def，如果对象不是数值，则为0
	 * @param def int[]
	 * @param arrs String[]
	 * @return int[]
	 */
	protected int[] getArrayInteger(int[] def, String... arrs) {
		Object obj = getObject(arrs);
		if (obj == null) return def;
		if (!(obj instanceof SimpleSequence)) return def;
		try {
			SimpleSequence arrayModel = (SimpleSequence) obj;
			int len = arrayModel.size();
			int[] newArr = new int[len];
			for (int i = 0; i < len; i++) {
				int value = 0;
				Object val=arrayModel.get(i);
				if (val instanceof TemplateNumberModel) {
					value = ((TemplateNumberModel) val).getAsNumber().intValue();
				}
				newArr[i] = value;
			}
			return newArr;
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return def;
		}
	}

	/**
	 * 抽取int[] 如果没有则直接返回空
	 * @param arrs String[]
	 * @return int[]
	 */
	protected int[] getArrayInteger(String... arrs) {
		return getArrayInteger(new int[0], arrs);
	}

	/**
	 * 抽取Boolean 如果没有对象，则返回def
	 * @param def boolean
	 * @param arrs String[]
	 * @return boolean
	 */
	protected boolean getBoolean(boolean def, String... arrs) {
		Object obj = getObject(arrs);
		if (obj == null) return def;
		if (obj instanceof Boolean) return (Boolean)obj;
		if (!(obj instanceof TemplateBooleanModel)) return def;
		try {
			return ((TemplateBooleanModel) obj).getAsBoolean();
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return def;
		}
	}

	/**
	 * 抽取Boolean 如果没有对象，则返回false
	 * @param arrs String[]
	 * @return boolean
	 */
	protected boolean getBoolean(String... arrs) {
		return getBoolean(false, arrs);
	}
	/**
	 * 抽取以字符串判断为boolean <br>
	 * true: on / true / yes / 1<br>
	 * false:off / false / no / 0<br>
	 * 其它字符串为def
	 * @param def boolean
	 * @param arrs String[]
	 * @return boolean
	 */
	protected boolean getBoolString(boolean def, String... arrs) {
		String val = getString(arrs);
		if (val == null || val.length() == 0) return def;
		val = val.toLowerCase();
		if ("on".equals(val) || "true".equals(val) || "yes".equals(val)) return true;
		if ("off".equals(val) || "false".equals(val) || "no".equals(val)) return false;
		if (Utils.isNumeric(val)) {
			int n = Integer.parseInt(val);
			if (n == 0) return false;
			if (n == 1) return true;
		}
		return def;
	}

	/**
	 * 抽取以字符串判断为boolean <br>
	 * true: on / true / yes / 1<br>
	 * false:off / false / no / 0<br>
	 * 其它字符串为false
	 * @param arrs String[]
	 * @return boolean
	 */
	protected boolean getBoolString(String... arrs) {
		return getBoolString(false, arrs);
	}

	/**
	 * 抽取日期，如果没有对象，则返回null
	 * @param def Date
	 * @param arrs String[]
	 * @return Date
	 */
	protected Date getDate(Date def, String... arrs) {
		Object obj = getObject(arrs);
		if (obj == null) return def;
		if (obj instanceof Date) return (Date)obj;
		if (!(obj instanceof SimpleDate)) return def;
		Date date = ((SimpleDate) obj).getAsDate();
		return date != null ? date : def;
	}

	/**
	 * 抽取日期，如果没有对象，则返回null
	 * @param arrs String[]
	 * @return Date
	 */
	protected Date getDate(String... arrs) {
		Date date = null;
		return getDate(date, arrs);
	}


	/**
	 * 抽取int 如果没有则直接返回default
	 * @param def int
	 * @param arrs String[]
	 * @return int
	 */
	protected int getInt(int def, String... arrs) {
		Object obj = getObject(arrs);
		if (obj == null) return def;
		if (obj instanceof Integer) return (Integer)obj;
		if (!(obj instanceof TemplateNumberModel)) return def;
		try {
			return ((TemplateNumberModel) obj).getAsNumber().intValue();
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return def;
		}
	}

	/**
	 * 抽取int 如果没有则直接返回0
	 * @param arrs String[]
	 * @return int
	 */
	protected int getInt(String... arrs) {
		return getInt(0, arrs);
	}

	public final Map getParams() {
		return params;
	}

	/**
	 * 抽取参数对象
	 * @param arrs String[]
	 * @return Object
	 */
	protected Object getObject(final String... arrs) {
		if (params == null || arrs == null || arrs.length == 0) return null;
		if(isdefaultconf && Utils.isDefault(arrs)) return Utils.getDefault(arrs);
		Iterator paramIter = params.entrySet().iterator();
		while (paramIter.hasNext()) {
			Map.Entry ent = (Map.Entry) paramIter.next();
			Object obj = ent.getKey();
			if (obj == null) continue;
			String paramName = (String) obj;
			for (String key : arrs)
				if (paramName.equals(key)) return (TemplateModel) ent.getValue();
		}
		return null;
	}

	/**
	 * 抽取String 如果没有对象，则返回null
	 * @param arrs String[]
	 * @return String
	 */
	protected String getString(String... arrs) {
		Object obj = getObject(arrs);
		if (obj == null) return null;
		if (!(obj instanceof SimpleScalar)) return null;
		String result = ((SimpleScalar) obj).getAsString();
		return result != null ? result.trim() : null;
	}
	/**
	 * 抽取String 如果没有对象，则返回def
	 * @param def String
	 * @param arrs  String[]
	 * @return String
	 */
	protected String getStringDef(String def,String... arrs) {
		String val=getString(arrs);
		if(val==null)return def;
		return val;
	}
	
	
	
	/**
	 * 设置Map
	 * @param params Map
	 */
	public final void setParams(Map params) {
		this.params = params;
	}
}
