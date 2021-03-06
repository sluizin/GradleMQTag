package com.maqiao.was.fmktag.table.dbtxt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.Utils;

/**
 * 得到URL
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
public class MQURL {
	/**
	 * @param request ServletRequest
	 * @param url String
	 * @return URL
	 */
	public static final URL getURL(ServletRequest request, String url) {
		if (url == null) return null;
		String newUrl = url.trim();
		URL Url = getURLHttp(url);
		if (Url != null) return Url;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		return getURLHttpServletRequest(httpRequest, newUrl);
	}

	/**
	 * @param request HttpServletRequest
	 * @param url String
	 * @return URL
	 */
	public static final URL getURL(HttpServletRequest request, String url) {
		if (url == null) return null;
		String newUrl = url.trim();
		URL Url = getURLHttp(url);
		if (Url != null) return Url;
		return getURLHttpServletRequest(request, newUrl);
	}

	/**
	 * 直接通过url得到URL
	 * @param url String
	 * @return URL
	 */
	private static final URL getURLHttp(String url) {
		String newUrl = url.trim();
		if (newUrl.length() == 0) return null;
		try {
			URL Url = new URL(newUrl);
			@SuppressWarnings("unused")
			InputStream in = Url.openStream();
			return Url;
		} catch (Exception e1) {
			return null;
		}
	}

	/**
	 * @param request HttpServletRequest
	 * @param url String
	 * @return URL
	 */
	private static final URL getURLHttpServletRequest(HttpServletRequest request, String url) {
		if (url == null || url.length() == 0) return null;
		String newurl = "";
		String requestUrl = request.getRequestURL().toString();//得到请求的URL地址
		String requestUri = request.getRequestURI();//得到请求的资源
		if ("/".equals(url.substring(0, 1))) {
			/* /abc/txt */
			int lastInt = requestUrl.lastIndexOf(requestUri);
			newurl = requestUrl.substring(0, lastInt) + url;
		} else {
			/* abc/txt */
			int lastInt = requestUrl.lastIndexOf("/");
			newurl = requestUrl.substring(0, lastInt) + "/" + url;
		}
		System.out.println("newurlresult:" + newurl);
		try {
			newurl = encodeURLChinese(newurl);
			if (!isConnection(newurl)) return null;
			URL Url = new URL(newurl);
			@SuppressWarnings("unused")
			InputStream in = Url.openStream();//////////////////////////////////////////////////////
			return Url;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 判断网址是否存在
	 * @param urlString String
	 * @return boolean
	 */
	public static boolean isConnection(String urlString) {
		if (urlString == null || urlString.length() == 0) return false;
		try {
			URL url = new URL(urlString);
			url.openConnection().connect();
		} catch (Exception e1) {
			return false;
		}
		return true;
	}

	/**
	 * 判断网址是否存在
	 * @param url URL
	 * @return boolean
	 */
	public static boolean isConnection(URL url) {
		if (url == null) return false;
		try {
			url.openConnection().connect();
		} catch (Exception e1) {
			return false;
		}
		return true;
	}

	/**
	 * 获取按要求编码后的URL列表
	 * @param url String
	 * @return String
	 */
	public static String encodeURLChinese(String url) {
		if (url == null) return null;
		url = url.trim();
		try {
			if (!needEncoding(url)) {
				// 不需要编码
				return url;
			} else {
				String allowChars = ".!*'();:@&=+_\\-$,/?#\\[\\]{}|\\^~`<>%\"";
				return encode(url, "UTF-8", allowChars, false);

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 判断一个url是否需要编码，按需要增减过滤的字符
	 * @param url String
	 * @return boolean
	 */
	public static boolean needEncoding(String url) {
		if (url.matches("^[0-9a-zA-Z.:/?=&%~`#()-+]+$")) return false;
		return true;
	}

	/**
	 * 对字符串中的特定字符进行编码
	 * @param s
	 *            待编码的字符串
	 * @param enc
	 *            编码类型
	 * @param allowed
	 *            不需要编码的字符
	 * @param lowerCase
	 *            true:小写 false：大写
	 * @return
	 * @throws java.io.UnsupportedEncodingException
	 */
	public static final String encode(String s, String enc, String allowed, boolean lowerCase) throws UnsupportedEncodingException {
		byte[] bytes = s.getBytes(enc);
		int count = bytes.length;
		/*
		 * From RFC 2396:
		 * mark = "-" | "_" | "." | "!" | "~" | "*" | "'" | "(" | ")" reserved =
		 * ";" | "/" | ":" | "?" | "@" | "&" | "=" | "+" | "$" | ","
		 */
		char[] buf = new char[3 * count];
		int j = 0;
		for (int i = 0; i < count; i++) {
			if ((bytes[i] >= 0x61 && bytes[i] <= 0x7A) || // a..z
					(bytes[i] >= 0x41 && bytes[i] <= 0x5A) || // A..Z
					(bytes[i] >= 0x30 && bytes[i] <= 0x39) || // 0..9
					(allowed.indexOf(bytes[i]) >= 0)) {
				buf[j++] = (char) bytes[i];
			} else {
				buf[j++] = '%';
				if (lowerCase) {
					buf[j++] = Character.forDigit(0xF & (bytes[i] >>> 4), 16);
					buf[j++] = Character.forDigit(0xF & bytes[i], 16);
				} else {
					buf[j++] = lowerCaseToUpperCase(Character.forDigit(0xF & (bytes[i] >>> 4), 16));
					buf[j++] = lowerCaseToUpperCase(Character.forDigit(0xF & bytes[i], 16));
				}
			}
		}
		return new String(buf, 0, j);
	}

	public static char lowerCaseToUpperCase(char ch) {
		if (ch >= 97 && ch <= 122) { // 如果是小写字母就转化成大写字母
			ch = (char) (ch - 32);
		}
		return ch;
	}

	/**
	 * URL转成输入流，并且返回输入流
	 * @param url URL
	 * @return InputStream
	 */
	public static final InputStream getInputStream(URL url) {
		if (url == null) return null;
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//设置超时间为3秒  
			conn.setConnectTimeout(30 * 1000);
			//防止屏蔽程序抓取而返回403错误  
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			//得到输入流  
			InputStream is = conn.getInputStream();
			return is;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static final String ACC_FileHead = "file://";

	/**
	 * 通过source字符串判断文件的属性并得到相应的InputStream<br>
	 * source:<br>
	 * "file://c:/words/204.doc"<br>
	 * "/latform/2020_liwebs/excel.xls"<br>
	 * "http://www.99114.com/doc/40.doc"<br>
	 * <br>
	 * @param request HttpServletRequest
	 * @param source String
	 * @return InputStream
	 */
	public static final InputStream getInputStreamBySource(HttpServletRequest request, String source) {
		if (source == null || source.length() == 0) return null;
		source = source.trim();
		if (source.length() == 0) return null;
		String file = null;
		/*
		 * 本地绝对地址 以file://为开头
		 * 例:file://c:/words/a1.doc
		 */
		if (source.startsWith(ACC_FileHead)) file = source.substring(ACC_FileHead.length(), source.length());
		if (source.startsWith("/")) {
			if (request == null) return null;
			file = Utils.getWebBasePath(request) + source;
		}
		if (file != null) {
			try {
				File f = new File(file);
				if (!f.exists()) return null;
				if (!f.isFile()) return null;
				return new FileInputStream(f);
			} catch (Exception e) {
				return null;
			}
		}
		URL url = MQURL.getURL(request, source);
		return MQURL.getInputStream(url);
	}

	public static boolean isTopURL(String str) {
		if (str == null || str.trim().length() == 0) return false;
		str = str.trim().toLowerCase();
		String domainRules = "com.cn|dunet.cn|org.cn|gov.cn|com.hk|公司|中国zhi|网络|com|net|org|int|edu|gov|mil|arpa|Asia|biz|info|name|pro|coop|aero|museum|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cf|cg|ch|ci|ck|cl|cm|cn|co|cq|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|eh|es|et|ev|fi|fj|fk|fm|fo|fr|ga|gb|gd|ge|gf|gh|gi|gl|gm|gn|gp|gr|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|in|io|iq|ir|is|it|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|ml|mm|mn|mo|mp|mq|mr|ms|mt|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nt|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sk|sl|sm|sn|so|sr|st|su|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|va|vc|ve|vg|vn|vu|wf|ws|ye|yu|za|zm|zr|zw";
		String protocols = "https|http|ftp|rtsp|mms";
		for (String e : protocols.split("\\|"))
			if (str.startsWith(e + "://")) return true;
		String regex = "^((" + protocols + ")?://)" + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
				+ "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
				+ "|" // 允许IP和DOMAIN（域名）
				+ "(([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]+\\.)?" // 域名- www.
				+ "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
				+ "(" + domainRules + "))" // first level domain- .com or .museum
				+ "(:[0-9]{1,4})?" // 端口- :80
				+ "((/?)|" // a slash isn't required if there is no file name
				+ "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher isUrl = pattern.matcher(str);
		return isUrl.matches();
	}

	public static void main(String[] args) {
		String[] arr = { "ftp://abccc", "/def/abc.doc", "abc.zip", "http://www.sina.com/deff", "http://127.0.0.1/def.doc", "http://127.0.0.1:80/def.doc"

		};
		for (String e : arr)
			System.out.println(e + ":\t" + isTopURL(e));
	}
}
