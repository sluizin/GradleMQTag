<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="/MQPO" prefix="mqpo" %>
<%@ taglib uri="/MQTable" prefix="mqtable" %>
<!DOCTYPE html>
<html>
<head lang="en">
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<body>
    <div class="wrap">
        <div>
            
            <div class="left" style="width:120%;">
            
<!-- 1111111111111111111 -->   
<mqtable:Basic psize="4" p="0" range="" test="v6=='地标美食' || v6=='品牌内衣'">  
<mqtable:url isdynamic="true" url="http://192.168.1.110:94/infor.txt" n0="index" n1="count11" n2="count22" n3="count33" aa="bb">
<li>v0:{v0}--v1:{v1}--v3:{v3}--v4:{v4}--v5:{v5}--v6:{v6}--v7:{v7}--v8:{v8,"地标美食"}--v9:{v9}--v10:{v10}<br>
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
(1)标签内含jsp动态语句&emsp;:&emsp;<%for(int i=0;i<4;i++){ %>[v<%=i %>]{v<%=i %>}<%} %><br>
(2)标签自定义jsp变量 下标/总数/过滤后的数量/当前页的记录数&emsp;:&emsp;<%=index %>/<%=count11 %>/<%=count22 %>/<%=count33 %><br>
<mqtable:Break test="v0==5"/>
<mqtable:Continue test="v0==2"/>
(3)读取数据[{v0,"def"}]&emsp;:&emsp;v0:{v0}--v1:{v1}--v3:{v3}--v4:{v4}--v5:{v5}--v6:{v6}--v7:{v7}--v8:{v8,"地标美食"}--v9:{v9}--v10:{v10}<br>
(4)使用属性读出数据[\$\{v1\}]&emsp;:&emsp;${v0}--${v1}<br>
<mqtable:If test="v0=='3' || v0=='1'">
(5)IF 判断[字符串比较]&emsp;:&emsp;[v0]{v0} [v1]{v2} =={v3}<br>
</mqtable:If><mqtable:If test="v3==6.90">
(6)IF 判断[数值类型比较]&emsp;:&emsp;[v0]{v0} [v1]{v2} =={v3}<br>
</mqtable:If>
<mqtable:If test="v0==1">
	aaaa<aaa><br>
	<mqtable:If test="v0==1">
	<else>zzz<mqtable:If test="v0==1">中国人</mqtable:If>zzz</else><br>
	</mqtable:If>
	<mqtable:If test="v0==2"><else>zzz人zzz</else><br></mqtable:If>
	acac<ccc><br>
</mqtable:If>
===================================================================================================

</li>
</mqtable:url>
</mqtable:Basic><br>
		</div>
        </div><!--End of right-box-->
    </div>
</body>
</html>