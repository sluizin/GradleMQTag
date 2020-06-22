package com.maqiao.was.fmktag.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;

/**
 * 数据源 Xml
 * 参数:
 * type: <br>
 * 0:本地工程下的目录资源[与index.html同级]<br>
 * 1:外部资源路径文件[http://static.99114.com/static/zhuanti/XXXX/db/YYYY.txt] <br>
 * sourcefile: 资源文件路径与文件名 <br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings("rawtypes")
public final class DBInputStreamCharXml extends DBAbstractInputStreamCharacter  implements InterfaceAccpetVal{

	@Override
	List<BeanLine> StringToBeanlineList(String content) {
		if (content == null || content.length() == 0) return new ArrayList<BeanLine>(0);
		List<BeanLine> list = new ArrayList<BeanLine>(10);
		try {
			Document doc = DocumentHelper.parseText(content);
			Element root = doc.getRootElement(); // 获取根节点
			Iterator<?> rootIterator = root.elementIterator();
			while (rootIterator.hasNext()) {
				Element recordEle = (Element) rootIterator.next();
				Iterator<?> iter2 = recordEle.elementIterator();
				if (!iter2.hasNext()) continue;
				BeanLine e = new BeanLine();
				while (iter2.hasNext()) {
					Element recordEle2 = (Element) iter2.next();
					String title = recordEle2.getText();
					e.setUnit(title);
				}
				list.add(e);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return list;
	}


	/**
	 * 数据源XML 构造函数
	 * @param request HttpServletRequest
	 * @param params Map
	 */
	public DBInputStreamCharXml(HttpServletRequest request, Map params) {
		super( request, params);
		super.acceptVal();
		acceptVal();
	}

	@Override
	public void acceptVal() {
		
	}

	@Override
	void changeSourcefile() {
		
	}

	@Override
	String[] getAutoSearchFilesExt() {
		String[] arr= {"xml"};
		return arr;
	}

}
