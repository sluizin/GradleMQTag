package com.maqiao.was.fmktag.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.maqiao.was.fmktag.table.dbtxt.BeanLine;

/**
 * 数据源 Json
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
public final class DBInputStreamCharJson extends DBAbstractInputStreamCharacter implements InterfaceAccpetVal {
	/** json头 */
	String jsonkey = "json";

	public final List<BeanLine> StringToBeanlineList(final String content) {
		if (content == null || content.length() == 0) return new ArrayList<BeanLine>(0);
		Object obj = JSON.parse(content);
		List<BeanLine> list = new ArrayList<BeanLine>(0);
		if (obj instanceof JSONObject) {
			JSONObject obj2 = JSON.parseObject(content);
			JSONArray arr = obj2.getJSONArray(jsonkey);
			for (int i = 0; i < arr.size(); i++) {
				JSONArray p = (JSONArray) arr.get(i);
				list.add(new BeanLine(p));
			}
		} else if (obj instanceof JSONArray) {
			JSONArray arr = JSON.parseArray(content);
			for (int i = 0; i < arr.size(); i++) {
				JSONArray p = (JSONArray) arr.get(i);
				list.add(new BeanLine(p));
			}
		}
		return list;
	}

	public static void main(String[] args) {
		//{"value":"ttc","jsonValues":[["a","b","c","d"],["A","B","C"],["1","2","3"],[100,200,300]]}
		String str = "{\"value\":\"ttc\",\"jsonValues\":[[\"a\",\"b\",\"c\",\"d\"],[\"A\",\"B\",\"C\"],[\"1\",\"2\",\"3\"],[100,200,300]]}";
		DBInputStreamCharJson t = new DBInputStreamCharJson(null, null);
		List<BeanLine> list = t.StringToBeanlineList(str);
		for (int i = 0; i < list.size(); i++) {
			BeanLine e = list.get(i);
			System.out.println("e:" + e.toString());
		}
	}

	/**
	 * 数据源json 构造函数
	 * @param request HttpServletRequest
	 * @param params Map
	 */
	public DBInputStreamCharJson(HttpServletRequest request, Map params) {
		super(request, params);
		super.acceptVal();
		acceptVal();
	}

	@Override
	public void acceptVal() {
		String val = this.getString("jsonkey");
		if (val != null && val.trim().length() > 0) jsonkey = val;
	}

	@Override
	void changeSourcefile() {

	}

	@Override
	String[] getAutoSearchFilesExt() {
		String[] arr = { "json" };
		return arr;
	}
}
