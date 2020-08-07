/**
 * 
 */
package com.maqiao.was.fmktag.table.dbtxt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.Utils;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public final class BeanLineUtils {

	/**
	 * 过滤掉无效的单元
	 * @param list List&lt;BeanLine&gt;
	 */
	public static final void filterInvalid(List<BeanLine> list) {
		int i=list.size();
		while(--i>=0) {
			BeanLine e=list.get(i);
			if(!e.isValid())list.remove(i);
		}
	}
	/**
	 * 所有非null单元过滤两侧空格
	 * @param list List&lt;BeanLine&gt;
	 */
	public static final void trim(List<BeanLine> list) {
		for(BeanLine e:list)
			e.trim();
	}
	
	/**
	 * 格式化表格 把null转成空字符串，并补齐缺少的单元，过滤掉空单元
	 * @param list List&lt;BeanLine&gt;
	 */
	public static final void format(List<BeanLine> list) {
		int max=getMaxLength(list);
		int i=list.size();
		while(--i>=0) {
			BeanLine e=list.get(i);
			if(e.isNull()) {
				list.remove(i);
				continue;
			}
			e.completion(max);
		}
		
		
	}
	/**
	 * 得到节点列表的最长宽度
	 * @param list List&lt;BeanLine&gt;
	 * @return int
	 */
	public static final int getMaxLength(List<BeanLine> list) {
		int max=0;
		for(BeanLine e:list) {
			if(e==null)continue;
			if(e.length()>max)max=e.length();
		}
		return max;		
	}


	/**
	 * 移除多个属性列，属性列下标，可以任意<br>
	 * 程序中对属性列下标进行降序排序
	 * @param list List&lt;BeanLine&gt;
	 * @param suffixs int[]
	 */
	public static final void removeColSuffix(List<BeanLine> list,int...suffixs) {
		if (list == null || list.size() == 0 || suffixs == null || suffixs.length==0) return;
		/* int数组转成Integer数组 */
		Integer[] arr=Arrays.stream(suffixs).boxed().toArray(Integer[]::new);
		/* 降序排列 */
		Arrays.sort(arr,Collections.reverseOrder());
		for (int index : arr) {
			if (index < 0) continue;
			for (BeanLine e : list)
				e.removeCol(index);
		}
	}

	/**
	 * 移除过滤行 过滤行为指定行
	 * @param list List&lt;BeanLine&gt;
	 * @param suffixs int[]
	 */
	public static final void removeRowSuffix(List<BeanLine> list,int...suffixs) {
		if (list == null || list.size() == 0 || suffixs == null) return;
		int i = list.size();
		while (--i >= 0) {
			if (Utils.isExist(suffixs, i)) list.remove(i);
		}
	}
	/**
	 * 去重复
	 * @param list List&lt;BeanLine&gt;
	 */
	public static final void distinct(List<BeanLine> list) {
		list = list.stream().distinct().collect(Collectors.toList());
	}

	/**
	 * 移除属性行
	 * @param list &lt;BeanLine&gt;
	 * @param attrRow int
	 */
	public static final void removeAttrRow(List<BeanLine> list,int attrRow) {
		if (list == null || list.size() == 0 || attrRow>=list.size() || attrRow < 0) return;
		if (attrRow > -1) list.remove(attrRow);
	}
	/**
	 * 把字符串中，所有在字符串末尾的数组中含有的全部清除<br>
	 * ("a.0bc.00.0.000.0.0.0.0",".0",".00",".000") 结果:a.0bc ,
	 * @param source String
	 * @param arrs String[]
	 * @return String
	 */
	public static final String removeEndsStr(String source, String... arrs) {
		if (source == null || source.length() == 0 || arrs.length == 0) return source;
		String key = null;
		for (String e : arrs)
			if (source.endsWith(e)) {
				key = e;
				break;
			}
		if (key == null) return source;
		int index = source.lastIndexOf(key);
		return removeEndsStr(source.substring(0, index), arrs);
	}
	
	
	
	/**
	 * 从字符串中得到某个变量组
	 * @param Content String
	 * @param regEx String
	 * @param group int
	 * @return String
	 */
	public static final String getRegExContent(String Content, String regEx, int group) {
		if (Content == null) return null;
		if (regEx == null) return null;
		Pattern pat = Pattern.compile(regEx);
		Matcher mat = pat.matcher(Content);
		if (!mat.find()) return null;
		if (group < 0 || group > mat.groupCount()) return null;
		return mat.group(group);
	}
	/**
	 * 得到url的文本文件内容<br>
	 * 是否过滤#右侧数据
	 * @param http String
	 * @param isReadUtf8 boolean
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return String
	 */
	public static final String getUrlContent(String http, boolean isReadUtf8, final String enterStr, boolean delnotes) {
		if (http == null) return null;
		http = http.trim();
		if (http.length() == 0) return null;
		String outString = "";
		try {
			outString = readFile(new URL(http), isReadUtf8, enterStr, delnotes).toString();
		} catch (MalformedURLException e) {
		}
		return outString;
	}

	/**
	 * 读取某个txt文件，按行读取，生成 List&lt;beanLine&gt;
	 * @param request HttpServletRequest
	 * @param http String
	 * @param isReadUtf8 boolean
	 * @return List&lt;beanLine&gt;
	 */
	public static final List<BeanLine> getBeanlineTxtUrl(HttpServletRequest request, final String http, boolean isReadUtf8) {
		return getBeanlineTxtUrl(request, http, isReadUtf8, null);
	}

	/**
	 * 读取某个txt文件，按行读取，生成 List&lt;beanLine&gt;<br>
	 * 设置编码转换<br>
	 * autoChange: "iso-8859-1 to utf-8"
	 * @param http String
	 * @param isReadUtf8 boolean
	 * @param autoChange String
	 * @return List&lt;beanLine&gt;
	 */
	public static final List<BeanLine> getBeanlineTxtUrl(HttpServletRequest request, final String http, final boolean isReadUtf8, final String autoChange) {
		if (http == null || http.length() == 0) return new ArrayList<BeanLine>(0);
		try {
			String outString = readFile(MQURL.getURL(request, http), isReadUtf8, "\n", false).toString();
			//System.out.println("outString:"+outString);
			return StringToBeanlineList(BeanLineUtils.autoChange(outString, autoChange));
		} catch (Exception e) {
			return new ArrayList<BeanLine>(0);
		}
	}

	/**
	 * 读取某个txt文件，按行读取，生成 List&lt;beanLine&gt;
	 * @param pathfilename String
	 * @return String
	 */
	public static final List<BeanLine> getBeanlineTxt(final String pathfilename) {
		return getBeanlineTxt(pathfilename, null);
	}

	/**
	 * 读取某个txt文件，按行读取，生成 List&lt;beanLine&gt;<br>
	 * 设置编码转换<br>
	 * autoChange: "iso-8859-1 to utf-8"
	 * @param pathfilename String
	 * @param autoChange String
	 * @return List&lt;beanLine&gt;
	 */
	public static final List<BeanLine> getBeanlineTxt(final String pathfilename, final String autoChange) {
		if (pathfilename == null || pathfilename.length() == 0) return new ArrayList<BeanLine>(0);
		try {
			String outString = readFile(pathfilename, "\n", false).toString();
			return StringToBeanlineList(BeanLineUtils.autoChange(outString, autoChange));
		} catch (Exception e) {
			return new ArrayList<BeanLine>(0);
		}
	}

	/**
	 * 把Content转成 <code>List&lt;beanLine&gt;</code>
	 * @param content String
	 * @return List&lt;BeanLine&gt;
	 */
	public static final List<BeanLine> StringToBeanlineList(final String content) {
		if (content == null || content.length() == 0) return new ArrayList<BeanLine>(0);
		final List<BeanLine> BeanLineList = new ArrayList<BeanLine>();
		final String[] splitStr = content.split("\n");
		BeanLine e = null;
		for (int i = 0; i < splitStr.length; i++)
			if ((e = BeanLineUtils.beanLineChange(splitStr[i])) != null) BeanLineList.add(e);
		return BeanLineList;
	}

	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * 是否过滤#右侧数据
	 * @param filenamepath String
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(String filenamepath, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(400);
		File file = new File(filenamepath);
		if (!file.exists()) return sb;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock;
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String str = changedLine(randomFile.readLine());
				if (str == null) continue;
				str = str.trim();
				if (str.length() == 0) continue;
				if (delnotes && str.indexOf('#') >= 0) str = str.substring(0, str.indexOf('#'));
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(enterStr);
			}
			lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return sb;
	}

	/**
	 * 通过URL得到文件内容<br>
	 * 是否过滤#右侧数据
	 * @param url URL
	 * @param isReadUtf8 boolean
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	public static final StringBuilder readFile(final URL url, final boolean isReadUtf8, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(20);
		try {
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(30000);
			urlcon.setReadTimeout(30000);
			urlcon.connect();
			String returnCode = new Integer(urlcon.getResponseCode()).toString();
			if (!returnCode.startsWith("2")) return null;
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = isReadUtf8 ? new InputStreamReader(is, "utf-8") : new InputStreamReader(is);
			BufferedReader buffer = new BufferedReader(isr);
			String l = null;
			while ((l = buffer.readLine()) != null) {
				if (delnotes && l.indexOf('#') >= 0) l = l.substring(0, l.indexOf('#'));
				if (l.length() == 0) continue;
				sb.append(l);
				sb.append(enterStr);
			}
			buffer.close();
			is.close();
			return sb;
		} catch (IOException e) {
			return sb;
		}
	}

	/**
	 * 读取文件，按行生成List对象
	 * @param filenamepath String
	 * @return List&lt;String&gt;
	 */
	@Deprecated
	public static final List<String> readFile(final String filenamepath) {
		List<String> list = new ArrayList<String>(50);
		File file = new File(filenamepath);
		if (!file.exists()) return list;
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock;
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
			String str = null;
			while (randomFile.getFilePointer() < randomFile.length()) {
				str = changedLine(randomFile.readLine());
				if (str == null) continue;
				str = str.trim();
				if (str.length() == 0) continue;
				if (str.indexOf('#') >= 0) str = str.substring(0, str.indexOf('#'));
				list.add(str);
			}
			lock.release();
		} catch (Exception e) {
		}
		return list;
	}

	/**
	 * RandomAccessFile RandomAccessFile读出时，转换成UTF-8
	 * @param line String
	 * @return String
	 */
	public static final String changedLine(final String line) {
		if (line == null) { return null; }
		try {
			byte buf[] = new byte[1];
			byte[] byteArray = new byte[line.length()];
			StringReader aStringReader = new StringReader(line);
			int character;
			int i = 0;
			while ((character = aStringReader.read()) != -1)
				byteArray[i++] = buf[0] = (byte) character;
			return new String(byteArray, "UTF-8");
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 把一行，转成beanLine对象
	 * @param str String
	 * @return beanLine
	 */
	public static final BeanLine beanLineChange(final String str) {
		if (str == null || str.length() == 0) return null;
		BeanLine bean = new BeanLine();
		String[] splitStr = str.split("\t");
		bean.set(splitStr);
		return bean;
	}

	/**
	 * 把字符串按规则进行转化<br>
	 * autoChange: "iso-8859-1 to utf-8"
	 * @param string String
	 * @param autoChange String
	 * @return String
	 */
	public static final String autoChange(final String string, final String autoChange) {
		if (string == null || string.length() == 0 || autoChange == null || autoChange.length() == 0 || autoChange.trim().toLowerCase().indexOf("to") == -1) return string;
		return autoChange(string, autoChange.trim().toLowerCase().split("to"));
	}

	/**
	 * 把字符串按规则进行转化<br>
	 * autoArray: {"iso-8859-1","utf-8"} <br>
	 * 注意:autoArray数组必须是2个单元
	 * @param string String
	 * @param autoArray String[]
	 * @return String
	 */
	public static final String autoChange(String string, String... autoArray) {
		if (autoArray.length != 2) return string;
		try {
			return new String(string.getBytes(autoArray[0].trim()), autoArray[1].trim());
		} catch (UnsupportedEncodingException e) {
		}
		return string;
	}

	/**
	 * @param url String
	 * @param regEx String
	 * @param group int
	 * @return String
	 */
	public static final String getRegExurl(String url, String regEx, int group) {
		try {
			URL Url = new URL(url);
			return getRegExUrl(Url, regEx, group);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param request HttpServletRequest
	 * @param url String
	 * @param regEx String
	 * @param group int
	 * @return String
	 */
	public static final String getRegExUrl(HttpServletRequest request, String url, String regEx, int group) {
		URL Url = MQURL.getURL(request, url);
		return getRegExUrl(Url, regEx, group);
	}

	/**
	 * @param Url URL
	 * @param regEx String
	 * @param group int
	 * @return String
	 */
	public static final String getRegExUrl(URL Url, String regEx, int group) {
		if (Url == null) return null;
		try {
			HttpURLConnection urlcon = (HttpURLConnection) Url.openConnection();
			urlcon.setConnectTimeout(30000);
			urlcon.setReadTimeout(30000);
			urlcon.connect();
			String returnCode = new Integer(urlcon.getResponseCode()).toString();
			if (!returnCode.startsWith("2")) return null;
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader buffer = new BufferedReader(isr);
			String l = null;
			String result = null;
			while ((l = buffer.readLine()) != null) {
				result = getRegExContent(l, regEx, group);
				if (result != null) return result;
			}
			buffer.close();
			is.close();
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * 得到含有中文的字符串，进行截取
	 * @param str String
	 * @param pstart int
	 * @param pend int
	 * @return String
	 */
	public static final String getSubString(final String str, final int pstart, final int pend) {
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
	 * 判断字符串是否为整数 null或空则返回false
	 * @param str String
	 * @return boolean
	 */
	public static boolean isNumeric(String str) {
		if (str == null || str.length() == 0) return false;
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57) return false;
		}
		return true;
	}

	/**
	 * BeanLine列表，按某个列进行升降序
	 * @param source List&lt;BeanLine&gt;
	 * @param suffix int
	 * @param reverse boolean
	 * @return List&lt;BeanLine&gt;
	 */
	public static final List<BeanLine> sortList(List<BeanLine> source, int suffix, boolean reverse) {
		if (source == null || source.size() < 2) return source;
		Comparator<BeanLine> comparator = (o1, o2) -> {
			String v01 = o1.get(suffix);
			String v02 = o2.get(suffix);
			if (v01 == v02) return 0;
			return Integer.valueOf(v01).compareTo(Integer.valueOf(v02));
		};
		source.sort(reverse ? comparator.reversed() : comparator);
		return source;
	}
	/**
	 * 判断list是否以纯数字进行排序
	 * @param list List&lt;BeanLine&gt;
	 * @param suffix int
	 * @return boolean
	 */
	public static final boolean isNumericField(List<BeanLine> list, int suffix) {
		for (BeanLine e : list) {
			String asc = e.get(suffix);
			if (!BeanLineUtils.isNumeric(asc)) return false;
		}
		return true;
	}
}
