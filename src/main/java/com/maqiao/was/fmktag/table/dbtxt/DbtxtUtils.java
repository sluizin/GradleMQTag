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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public final class DbtxtUtils {

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
			return StringToBeanlineList(DbtxtUtils.autoChange(outString, autoChange));
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
			return StringToBeanlineList(DbtxtUtils.autoChange(outString, autoChange));
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
			if ((e = DbtxtUtils.beanLineChange(splitStr[i])) != null) BeanLineList.add(e);
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
	 * 
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
}
