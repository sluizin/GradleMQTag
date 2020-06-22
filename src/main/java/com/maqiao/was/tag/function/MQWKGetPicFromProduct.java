package com.maqiao.was.tag.function;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.maqiao.was.fmktag.table.dbtxt.BeanLineUtils;
/**
 * 通过网库商品地址得到商品图片地址
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
public class MQWKGetPicFromProduct  extends SimpleTagSupport {
	JspContext pc;

	public JspContext getJspContext() {
		return pc;
	}

	public void setJspContext(JspContext pc) {
		this.pc = pc;
	}
	static final String regEx="jqimg=\"([^\"]+)\"";
	static final String staticLine=System.getProperty("line.separator");
	@Override
	//当遇到标签时就会执行这个方法
	public void doTag() throws JspException, IOException {
		HttpServletRequest request=(HttpServletRequest) ((PageContext)this.getJspContext()).getRequest();
		String http=request.getParameter("http");
		if(http==null||http.length()==0) {
			pc.getOut().write("");
			return;
		}
		String[] arr=http.split(staticLine);
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<arr.length;i++) {
			String line=arr[i];
			line=line.replace('\"', ' ').trim();
			System.out.println("["+i+"/"+arr.length+"]line:"+line);
			String pic=BeanLineUtils.getRegExUrl(request, line, regEx, 1);
			//sb.append(line);
			if(pic!=null) {
				sb.append(pic+"<br/>");
			}else {
				sb.append("<br/>");
			}
		}
		pc.getOut().write(sb.toString());
		//getJspBody().invoke(null);
	}

}
