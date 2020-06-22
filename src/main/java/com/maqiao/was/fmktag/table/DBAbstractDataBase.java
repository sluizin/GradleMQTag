package com.maqiao.was.fmktag.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import java.sql.ResultSet;

@SuppressWarnings({"rawtypes"})
public abstract class DBAbstractDataBase extends DBAbstract implements InterfaceAccpetVal {
	ResultSet rs=null;

	/* 来源类型 0:本地绝对地址 1:网络地址 */
	int type = -1;
	/** 配置文件 */
	String propfile=null;
	public DBAbstractDataBase(HttpServletRequest request, Map params) {
		super(request, params);
		super.acceptVal();
		acceptVal();
	}
	@Override
	public void acceptVal() {
		/* 来源类型 0:本地绝对地址 1:网络地址 */
		type = getInt(-1,"type");
		propfile = getString("propfile");
	}

	@Override
	public List<BeanLine> getList() {
		if(rs==null)return new ArrayList<>();
		try {
			int rowCount=rs.getRow();
			List<BeanLine> list=new ArrayList<>(rowCount);
			while(rs.next()) {
				list.add(new BeanLine(rs));
			}
			return list;		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

}
