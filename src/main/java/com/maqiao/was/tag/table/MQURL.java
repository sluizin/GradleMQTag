package com.maqiao.was.tag.table;

import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

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
		if (newUrl.length() == 0) return null;
		try {
			URL Url = new URL(newUrl);
			@SuppressWarnings("unused")
			InputStream in = Url.openStream();
			return Url;
		} catch (Exception e1) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			return getURL(httpRequest, newUrl);
		}
	}

	/**
	 * @param request HttpServletRequest
	 * @param url String
	 * @return URL
	 */
	private static final URL getURL(HttpServletRequest request, String url) {
		String newurl = "";
		String requestUrl = request.getRequestURL().toString();//得到请求的URL地址
		String requestUri = request.getRequestURI();//得到请求的资源
		if (url.substring(0, 1).equals("/")) {
			/* /abc/txt */
			int lastInt = requestUrl.lastIndexOf(requestUri);
			newurl = requestUrl.substring(0, lastInt) + url;
		} else {
			/* abc/txt */
			int lastInt = requestUrl.lastIndexOf("/");
			newurl = requestUrl.substring(0, lastInt) + "/" + url;
		}
		try {
			URL Url = new URL(newurl);
			@SuppressWarnings("unused")
			InputStream in = Url.openStream();
			return Url;
		} catch (Exception e1) {
		}
		return null;
	}

}
