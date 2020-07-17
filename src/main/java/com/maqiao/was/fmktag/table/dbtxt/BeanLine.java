/**
 * 
 */
package com.maqiao.was.fmktag.table.dbtxt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.alibaba.fastjson.JSONArray;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * 节点 为一行数据 某个数据为一个单元数据
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public final class BeanLine {
	/** value值串 */
	List<String> list = new ArrayList<String>(10);
	HttpServletRequest request;

	public BeanLine() {

	}

	/**
	 * 构造(赋值)
	 * @param parr com.alibaba.fastjson.JSONArray
	 */
	public BeanLine(JSONArray parr) {
		set(parr);
	}

	/**
	 * 构造(赋值)
	 * @param rs ResultSet
	 */
	public BeanLine(ResultSet rs) {
		set(rs);
	}

	/**
	 * 构造(赋值)
	 * @param splitStr String[]
	 */
	public BeanLine(String... splitStr) {
		set(splitStr);
	}

	/**
	 * 补齐 从0 -- (max-1)之间，如遇到null，则换成空字符串
	 */
	public final void completion() {
		completion(this.length());
	}

	/**
	 * 补齐 从0 -- (max-1)之间，如遇到null，则换成空字符串，如超出则添加空字符串
	 * @param max int
	 */
	public final void completion(int max) {
		for (int i = 0; i < max; i++) {
			if (i >= list.size()) {
				list.add("");
				continue;
			}
			String e = list.get(i);
			if (e == null) list.set(i, "");
		}
	}

	/**
	 * 截取含有中文的字符串
	 * @param suffix int
	 * @param size int
	 * @return String
	 */
	public final String cut(int suffix, final int size) {
		return cut(get(suffix), size);
	}

	/**
	 * 截取含有中文的字符串
	 * @param str String
	 * @param size int
	 * @return String
	 */
	public final String cut(final String str, final int size) {
		if (str == null || str.length() == 0 || size <= 0) return "";
		String out = BeanLineUtils.getSubString(str, 0, size);
		if (!str.equals(out)) return out + "..";
		return out;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BeanLine other = (BeanLine) obj;
		if (list == null) {
			if (other.list != null) return false;
		} else if (!list.equals(other.list)) return false;
		return true;
	}

	/**
	 * 把BeanLine对象转成Json，并是否过滤空属性(null || "")
	 * @param delSpace boolean
	 * @return String
	 */
	public final String exportJsonObject(final boolean delSpace) {
		StringBuilder sb = new StringBuilder(20);
		if (list.size() == 0) return "";
		sb.append('{');
		for (int i = 0, len = list.size(); i < len; i++) {
			String value = list.get(i);
			if (delSpace && (value == null || value.length() == 0)) continue;
			sb.append("v" + i);
			sb.append(':');
			if (value == null) {
				sb.append("null");
			} else {
				sb.append(jsonVkey);
				sb.append(value);
				sb.append(jsonVkey);
			}
		}
		sb.append('}');
		if (sb.length() < 3) return "";
		return sb.toString();
	}

	/**
	 * 格式化，所有列如为null，则转换成空字符
	 */
	public final void format() {
		for (int i = 0, len = list.size(); i < len; i++)
			if (list.get(i) == null) list.set(i, "");
	}

	/**
	 * 得到vI的值，多列查找<br>
	 * 如果没有查找，则返回null
	 * @param arrs int[]
	 * @return String
	 */
	public final String getArrays(int... arrs) {
		for (int i : arrs) {
			if (i >= 0 && i < list.size()) return list.get(i);
		}
		return null;
	}
	/**
	 * 判断值与对象是否相同，是否忽略大小写
	 * @param i int
	 * @param val String
	 * @param ignoreCase boolean
	 * @return boolean
	 */
	public final boolean equalsValue(int i,String val,boolean ignoreCase) {
		String v=get(i);
		if(v==null)return false;
		if(ignoreCase)return v.equalsIgnoreCase(val);else return v.equals(val);
	}
	/**
	 * 得到vI的值，多列查找<br>
	 * 如果没有查找，则返回null
	 * @param i int
	 * @return String
	 */
	public final String get(int i) {
		if (i >= 0 && i < list.size()) return list.get(i);
		return null;
	}

	/**
	 * 去掉金额中的无效小数 如.0 .00
	 * @param i int
	 * @return String
	 */
	public final String getRealmoney(int i) {
		String str = get(i);
		if (str == null) return null;
		if (str.endsWith(".00")) return str.substring(0, str.indexOf(".00"));
		if (str.endsWith(".0")) return str.substring(0, str.indexOf(".0"));
		if (str.endsWith(".")) return str.substring(0, str.indexOf("."));
		return str;
	}

	/**
	 * 去掉金额中的无效小数 如 . .0 .00
	 * @param i int
	 * @return String
	 */
	public final String getRealmoneycomplete(int i) {
		String str = get(i);
		if (str == null) return null;
		return BeanLineUtils.removeEndsStr(str, ".", ".0", ".00", ".000");
	}

	/**
	 * 得到标准2位小数的金额
	 * @param i int
	 * @return String
	 */
	public final String getRealmoneystandard(int i) {
		String str = get(i);
		if (str == null) return null;
		str = str.trim();
		int index = str.lastIndexOf(".");
		if (str.equals("面议")) return str;
		if (index <= 0) return str + ".00";
		if (str.endsWith(".")) return str + "00";
		int sy = str.length() - index;
		if (sy == 2) return str + "0";
		return str;
	}

	/**
	 * 通过3个字段得到正则结果字符串
	 * @param urlIndex int
	 * @param regExIndex int
	 * @param groupIndex int
	 * @return String
	 */
	public String getRegEx(int urlIndex, int regExIndex, int groupIndex) {
		if (!(isSafeIndex(urlIndex) && isSafeIndex(regExIndex) && isSafeIndex(groupIndex))) return null;
		String url = get(urlIndex);
		String regEx = get(regExIndex);
		int group = -1;
		try {
			group = Integer.parseInt(get(groupIndex));
		} catch (NumberFormatException e) {
		}
		return BeanLineUtils.getRegExUrl(request, url, regEx, group);
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * 判断各段中是否含有关键字，如果含有则以\t进行组合
	 * @param key String
	 * @return String[]
	 */
	public final String[] getTransverseIndexOf(final String key) {
		List<String> newList = new ArrayList<String>(4);
		for (int i = 0, len = list.size(); i < len; i++) {
			String value = list.get(i);
			if (value != null && value.indexOf(key) > -1) newList.add(value);
		}
		String[] t = {};
		return newList.toArray(t);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	/**
	 * 判断节点是否格式化，是否含有null
	 * @return boolean
	 */
	public final boolean isFormat() {
		for (int i = 0, len = list.size(); i < len; i++)
			if (list.get(i) == null) return false;
		return true;
	}

	/**
	 * 是否全部为null<br>
	 * 当list为零个，或多个列都为null，则返回true
	 * @return boolean
	 */
	public final boolean isNull() {
		if (list.size() == 0) return true;
		for (String e : list) {
			if (e != null) return false;
		}
		return true;
	}

	/**
	 * 判断index是否合法
	 * @param index int
	 * @return boolean
	 */
	public final boolean isSafeIndex(int index) {
		return 0 <= index && index <= list.size();
	}

	/**
	 * 节点内含有有效数据，null或空字符串(多个空字符串)为无效数据
	 * @return boolean
	 */
	public final boolean isValid() {
		for (String e : list) {
			if (e != null && e.trim().length() > 0) return true;
		}
		return false;
	}

	/**
	 * 长度
	 * @return int
	 */
	public final int length() {
		return list.size();
	}
	/**
	 * 移除某列
	 * @param index int
	 * @return boolean
	 */
	public final boolean removeCol(int index) {
		if(index<0 || index>=list.size())return false;
		list.remove(index);
		return true;
	}

	public final void set(ResultSet rs) {
		if (rs == null) return;
		try {
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();//获取行的数量
			for (int i = 1; i <= columnCount; i++) {
				Object obj = rs.getObject(i);
				setUnit(obj == null ? null : obj.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 依次向BeanLine对象中的各属性赋值
	 * @param parr com.alibaba.fastjson.JSONArray
	 */
	public final void set(JSONArray parr) {
		Object[] arrs = parr.toArray();
		for (int i = 0; i < arrs.length; i++)
			list.add(arrs[i].toString());
	}

	/**
	 * 依次向BeanLine对象中的各属性赋值
	 * @param splitStr String[]
	 */
	public final void set(String... splitStr) {
		list = Arrays.asList(splitStr);
	}

	public final void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 依次向BeanLine对象中的各属性赋值 以关键字对字符串进行分组赋值
	 * @param str String
	 * @param splitKey String
	 */
	public final void setSplit(String str, String splitKey) {
		if (str == null || str.length() == 0 || splitKey == null) return;
		String[] splitStr = str.split(splitKey);
		set(splitStr);
	}

	/**
	 * 向list里添加单元
	 * @param str String
	 */
	public final void setUnit(String str) {
		list.add(str);
	}

	/**
	 * 把一段含有<code>{\@fiela}</code>的代码的html LI转换成BeanLine节点，进行替换<br>
	 * <code>
	 * "&lt;li&gt;{\@ v0} /{\@ v0}&lt;/li&gt;"
	 * "&lt;li&gt;XXXXXXXXXXX  /YYYYYYYYYYY&lt;/li&gt;"
	 * </code>
	 * @param htmlBeanLine String
	 * @param e BeanLine
	 * @return String
	 */
	public final String SjFMBeanLineHtml(final String htmlBeanLine, final BeanLine e) {
		if (htmlBeanLine == null || htmlBeanLine.length() == 0) return "";
		String htmlStr = htmlBeanLine;
		for (int i = 0, len = list.size(); i < len; i++) {
			String fieldsign = ACC_fmHtml_sign.replace("FieldName", "v" + i);
			htmlStr = htmlStr.replace(fieldsign, list.get(i));

		}
		return htmlStr;
	}

	/**
	 * 所有非null单元过滤两侧空格
	 */
	public void trim() {
		for (int i = 0, len = list.size(); i < len; i++) {
			String val = list.get(i);
			if (val == null) continue;
			list.set(i, val.trim());
		}
	}

	@Override
	public String toString() {
		return "BeanLine [" + (request != null ? "request=" + request + ", " : "") + (list != null ? "list=" + list : "") + "]";
	}

	/** 在freemarker中的html里作属性标志时使用，属性值[FieldName]替换 */
	static final String ACC_fmHtml_sign = "{@FieldName}";

	private static final char jsonVkey = '"';

	public static void main(String[] args) {
		String content = "a.0bc.....00.0.000.0.0.0.0";
		System.out.println("=" + BeanLineUtils.removeEndsStr(content, ".", ".0", ".00", ".000"));
	}

}
