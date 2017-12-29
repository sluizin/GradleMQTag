/**
 * 
 */
package com.maqiao.was.fmktag.table.dbtxt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.Consts;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public final class BeanLine {
	HttpServletRequest request;
	/** v最大值 如 v20 maxPointI=20 */
	public static final int maxPointI = 20;
	/** 在freemarker中的html里作属性标志时使用，属性值[FieldName]替换 */
	static final String ACC_fmHtml_sign = "{@FieldName}";
	String v0 = "";
	String v1 = "";
	String v2 = "";
	String v3 = "";
	String v4 = "";
	String v5 = "";
	String v6 = "";
	String v7 = "";
	String v8 = "";
	String v9 = "";
	String v10 = "";
	String v11 = "";
	String v12 = "";
	String v13 = "";
	String v14 = "";
	String v15 = "";
	String v16 = "";
	String v17 = "";
	String v18 = "";
	String v19 = "";
	String v20 = "";

	public BeanLine() {

	}

	/**
	 * 构造(赋值)
	 * @param splitStr String[]
	 */
	public BeanLine(String... splitStr) {
		set(splitStr);
	}

	/**
	 * 自省，有多少个 v 单元
	 * @return int
	 */
	public final int length() {
		int sort = 0;
		Class<?> userCla = (Class<?>) this.getClass();
		Field[] fs = userCla.getFields();
		for (int ii = 0, len = fs.length; ii < len; ii++)
			if (fs[ii].getName().indexOf("v") == 0) sort++;
		return sort;
	}

	/**
	 * 依次向BeanLine对象中的各属性赋值
	 * @param splitStr String[]
	 */
	public final void set(String... splitStr) {
		final int len = splitStr.length;
		/*
		 * final int length = this.length();
		 * int maxchange = len > length ? length : len;
		 * if(maxchange==0)return;
		 * Class<?> userCla = (Class<?>) this.getClass();
		 * Field[] fs = userCla.getDeclaredFields();
		 * try {
		 * for(int i=0;i<maxchange;i++){
		 * Field f = fs[i];
		 * f.setAccessible(true);
		 * f.set(this, splitStr[i]);
		 * }
		 * } catch (IllegalArgumentException e) {
		 * LogMgr.writeErrorLog(e);
		 * } catch (IllegalAccessException e) {
		 * LogMgr.writeErrorLog(e);
		 * }
		 */

		for (int i = 0; i < len && i <= maxPointI; i++) {
			set(i, splitStr[i]);
		}
		/*
		 * if (len > 0) v0 = splitStr[0];
		 * if (len > 1) v1 = splitStr[1];
		 * if (len > 2) v2 = splitStr[2];
		 * if (len > 3) v3 = splitStr[3];
		 * if (len > 4) v4 = splitStr[4];
		 * if (len > 5) v5 = splitStr[5];
		 * if (len > 6) v6 = splitStr[6];
		 * if (len > 7) v7 = splitStr[7];
		 * if (len > 8) v8 = splitStr[8];
		 * if (len > 9) v9 = splitStr[9];
		 * if (len > 10) v10 = splitStr[10];
		 */
	}

	/**
	 * 把一段含有<code>{\@fiela}</code>的代码的html LI转换成BeanLine单元，进行替换<br>
	 * <code>
	 * "&lt;li&gt;{\@ v0} /{\@ v0}&lt;/li&gt;"
	 * "&lt;li&gt;XXXXXXXXXXX  /YYYYYYYYYYY&lt;/li&gt;"
	 * </code>
	 * @param htmlBeanLine String
	 * @param e BeanLine
	 * @return String
	 */
	public String SjFMBeanLineHtml(final String htmlBeanLine, final BeanLine e) {
		if (htmlBeanLine == null || htmlBeanLine.length() == 0) return "";
		String htmlStr = htmlBeanLine;
		Class<?> classzz = (Class<?>) this.getClass();
		Field[] fields = classzz.getFields();
		try {
			for (int i = 0, len = fields.length; i < len; i++) {
				Field f = fields[i];
				f.setAccessible(true);
				Object obj = f.get(e);
				String fieldsign = ACC_fmHtml_sign.replace("FieldName", f.getName());
				htmlStr = htmlStr.replace(fieldsign, (obj == null) ? "" : obj.toString());
			}
		} catch (Exception ef) {
		}

		return htmlStr;
	}

	/**
	 * 给指定v 位置赋值
	 * @param i int
	 * @param obj Object
	 * @return boolean
	 */
	public boolean set(int i, Object obj) {
		if (!isSafeIndex(maxPointI)) {
			new Exception("Define out of range!!!");
			return false;
		}
		try {
			long Offset = Consts.UNSAFE.objectFieldOffset(BeanLine.class.getDeclaredField("v" + i));
			Consts.UNSAFE.putObject(this, Offset, obj);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 判断index是否合法
	 * @param index int
	 * @return boolean
	 */
	public boolean isSafeIndex(int index) {
		return 0 <= index && index <= maxPointI;
	}

	/**
	 * 得到vI的值
	 * @param i int
	 * @return String
	 */
	public String get(int i) {
		Class<?> userCla = (Class<?>) this.getClass();
		Field[] fs = userCla.getDeclaredFields();
		final String v = "v" + i;
		for (int ii = 0, len = fs.length; ii < len; ii++) {
			Field f = fs[ii];
			if (!f.getName().equals(v)) continue;
			f.setAccessible(true); /* 设置些属性是可以访问的 */
			try {
				return (String) f.get(this);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 把BeanLine对象转成Json，并是否过滤空属性(null || "")
	 * @param delSpace boolean
	 * @return String
	 */
	public String exportJsonObject(final boolean delSpace) {
		StringBuilder sb = new StringBuilder(20);
		Class<?> userCla = (Class<?>) this.getClass();
		Field[] fs = userCla.getDeclaredFields();
		try {
			String fieldString = null;
			int jsoni = 0;
			sb.append('{');
			for (int i = 0, len = fs.length; i < len; i++) {
				Field f = fs[i];
				f.setAccessible(true);
				fieldString = (String) f.get(this);
				if (delSpace && (fieldString == null || fieldString.length() == 0)) continue;
				if (jsoni > 0) sb.append(',');
				jsoni++;
				sb.append(f.getName());
				sb.append(':');
				if (fieldString == null) {
					sb.append("null");
				} else {
					sb.append('"');
					sb.append(fieldString);
					sb.append('"');
				}
			}
			sb.append('}');
		} catch (Exception e) {
		}
		if (sb.length() < 3) return "";
		return sb.toString();
	}

	/**
	 * 判断各段中是否含有关键字，如果含有则以\t进行组合
	 * @param key String
	 * @return String[]
	 */
	public final String[] getTransverseIndexOf(final String key) {
		List<String> sb = new ArrayList<String>(4);
		try {
			Class<?> userCla = (Class<?>) this.getClass();
			Field[] fs = userCla.getDeclaredFields();
			for (int i = 0, len = fs.length; i < len; i++) {
				Field f = fs[i];
				/* 如果是静态属性则跳转 */
				if (((f.getModifiers() & java.lang.reflect.Modifier.STATIC) == java.lang.reflect.Modifier.STATIC)) continue;
				f.setAccessible(true);
				Object obj = f.get(this);
				if (obj == null || !(obj instanceof String)) continue;
				String value = (String) obj;
				if (value.indexOf(key) > -1) sb.add(value);
			}
		} catch (Exception e) {
		}
		String[] t = {};
		return sb.toArray(t);
	}

	public final String getV0() {
		return v0;
	}

	public final void setV0(String v0) {
		this.v0 = v0;
	}

	public final String getV1() {
		return v1;
	}

	public final void setV1(String v1) {
		this.v1 = v1;
	}

	public final String getV2() {
		return v2;
	}

	public final void setV2(String v2) {
		this.v2 = v2;
	}

	public final String getV3() {
		return v3;
	}

	public final void setV3(String v3) {
		this.v3 = v3;
	}

	public final String getV4() {
		return v4;
	}

	public final void setV4(String v4) {
		this.v4 = v4;
	}

	public final String getV5() {
		return v5;
	}

	public final void setV5(String v5) {
		this.v5 = v5;
	}

	public final String getV6() {
		return v6;
	}

	public final void setV6(String v6) {
		this.v6 = v6;
	}

	public final String getV7() {
		return v7;
	}

	public final void setV7(String v7) {
		this.v7 = v7;
	}

	public final String getV8() {
		return v8;
	}

	public final void setV8(String v8) {
		this.v8 = v8;
	}

	public final String getV9() {
		return v9;
	}

	public final void setV9(String v9) {
		this.v9 = v9;
	}

	public final String getV10() {
		return v10;
	}

	public final void setV10(String v10) {
		this.v10 = v10;
	}

	public final String getV11() {
		return v11;
	}

	public final void setV11(String v11) {
		this.v11 = v11;
	}

	public final String getV12() {
		return v12;
	}

	public final void setV12(String v12) {
		this.v12 = v12;
	}

	public final String getV13() {
		return v13;
	}

	public final void setV13(String v13) {
		this.v13 = v13;
	}

	public final String getV14() {
		return v14;
	}

	public final void setV14(String v14) {
		this.v14 = v14;
	}

	public final String getV15() {
		return v15;
	}

	public final void setV15(String v15) {
		this.v15 = v15;
	}

	public final String getV16() {
		return v16;
	}

	public final void setV16(String v16) {
		this.v16 = v16;
	}

	public final String getV17() {
		return v17;
	}

	public final void setV17(String v17) {
		this.v17 = v17;
	}

	public final String getV18() {
		return v18;
	}

	public final void setV18(String v18) {
		this.v18 = v18;
	}

	public final String getV19() {
		return v19;
	}

	public final void setV19(String v19) {
		this.v19 = v19;
	}

	public final String getV20() {
		return v20;
	}

	public final void setV20(String v20) {
		this.v20 = v20;
	}

	public final HttpServletRequest getRequest() {
		return request;
	}

	public final void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "BeanLine [v0=" + v0 + ", v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + ", v4=" + v4 + ", v5=" + v5 + ", v6=" + v6 + ", v7=" + v7 + ", v8=" + v8 + ", v9=" + v9 + ", v10=" + v10 + ", v11=" + v11 + ", v12=" + v12 + ", v13=" + v13
				+ ", v14=" + v14 + ", v15=" + v15 + ", v16=" + v16 + ", v17=" + v17 + ", v18=" + v18 + ", v19=" + v19 + ", v20=" + v20 + "]";
	}

	/**
	 * 截取含有中文的字符串
	 * @param str String
	 * @param size int
	 * @return String
	 */
	public final String cut(final String str, final int size) {
		if (str == null || str.length() == 0 || size <= 0) return "";
		String out = getSubString(str, 0, size);
		if (!str.equals(out)) return out + "..";
		return out;
	}

	/**
	 * 得到含有中文的字符串，进行截取
	 * @param str String
	 * @param pstart int
	 * @param pend int
	 * @return String
	 */
	public final String getSubString(final String str, final int pstart, final int pend) {
		String resu = "";
		int beg = 0, end = 0, count1 = 0;
		int i, j, y, cont, count, len;
		final int strlength = str.length();
		final char[] temp = new char[strlength];
		str.getChars(0, strlength, temp, 0);
		boolean[] bol = new boolean[strlength];
		for (i = 0, len = temp.length; i < len; i++)
			if (bol[i] = ((int) temp[i] > 255)) count1++;
			else bol[i] = false;
		if (pstart > strlength + count1) resu = null;
		if (pstart > pend) resu = null;
		if (pstart < 1) beg = 0;
		else beg = pstart - 1;
		if (pend > strlength + count1) end = strlength + count1;
		else end = pend;
		if (resu != null) {
			if (beg == end) {
				if (beg == (count = 0)) if (bol[0] == true) return null;
				else return new String(temp, 0, 1);
				else {
					len = beg;
					for (y = 0; y < (len--); y++)
						if (bol[y]) count++;
					if (count == 0) if ((int) temp[beg] > 255) return null;
					else return new String(temp, beg, 1);
					else if ((int) temp[len + 1] > 255) return null;
					else return new String(temp, len + 1, 1);
				}
			} else {
				int temSt = beg;
				int temEd = end - 1;
				for (i = 0; i < temSt; i++)
					if (bol[i]) temSt--;
				for (j = 0; j < temEd; j++)
					if (bol[j]) temEd--;
				if (bol[temSt]) {
					for (i = cont = 0; i <= temSt; i++)
						if (bol[i]) cont += 2;
						else cont++;
					if (pstart == cont) temSt++;
				}
				if (bol[temEd]) {
					for (i = cont = 0; i <= temEd; i++)
						if (bol[i]) cont += 2;
						else cont++;
					if (pend < cont) temEd--;
				}
				if (temSt == temEd) return new String(temp, temSt, 1);
				else if (temSt > temEd) return null;
				else return str.substring(temSt, temEd + 1);
			}
		}
		return resu;
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
		String url = this.get(urlIndex);
		String regEx = this.get(regExIndex);
		int group = -1;
		try {
			group = Integer.parseInt(this.get(groupIndex));
		} catch (NumberFormatException e) {
		}
		return DbtxtUtils.getRegExUrl(request, url, regEx, group);
	}
}
