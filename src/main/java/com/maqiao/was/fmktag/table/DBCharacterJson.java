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
 * 
 * 数据源 Json
 * 参数:
 * type: <br>
 * 0:本地工程下的目录资源[与index.html同级]<br>
 * 1:外部资源路径文件[http://static.99114.com/static/zhuanti/XXXX/db/YYYY.txt] <br>
 * sourcefile: 资源文件路径与文件名 <br>
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 *
 */
@SuppressWarnings("rawtypes")
public class DBCharacterJson  extends DBAbstractCharacter{
	/** json头 */
	static final String jsonValue = "jsonValues";
	

	public final List<BeanLine> StringToBeanlineList(final String content) {
		if (content == null || content.length() == 0) return new ArrayList<BeanLine>(0);
		List<BeanLine> list=new ArrayList<BeanLine>(0);
		JSONObject obj=JSON.parseObject(content);
		JSONArray jsarr = obj.getJSONArray(jsonValue);
		for(int i=0;i<jsarr.size();i++) {
			JSONArray p= (JSONArray) jsarr.get(i);
			list.add(new BeanLine(p));
		}
		return list;
	}

	public static void main(String[] args) 
	{
		//{"value":"ttc","jsonValues":[["a","b","c","d"],["A","B","C"],["1","2","3"],[100,200,300]]}
		String str="{\"value\":\"ttc\",\"jsonValues\":[[\"a\",\"b\",\"c\",\"d\"],[\"A\",\"B\",\"C\"],[\"1\",\"2\",\"3\"],[100,200,300]]}";
		DBCharacterJson t=new DBCharacterJson();
		List<BeanLine> list=t.StringToBeanlineList(str);
		for(int i=0;i<list.size();i++) {
			BeanLine e=list.get(i);
			System.out.println("e:"+e.toString());
		}
	}
	public DBCharacterJson() {
		
	}
	public DBCharacterJson(HttpServletRequest request, Map params) {
		this.request = request;
		this.params = params;
	}

	@Override
	void ProjectInitialization() {		
	}

}
