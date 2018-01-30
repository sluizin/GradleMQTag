package com.maqiao.was.fmktag.table.dbtxt;

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
		if(url==null || url.length()==0)return null;
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
		//System.out.println("newurl:"+newurl);
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
