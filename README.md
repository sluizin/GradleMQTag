#GradleMQTag
*** 标签组项目
* 从html中 的form通过不同的input 得到不同的图层叠加，生成一张图片
<code><pre>
<!-- 外链图片图层 -->
<mqpo:Basic group="a100" useid="false" valid="true" issave="true" iscache="false" isBG="false" x="10" y="20" width="144" height="144" color="#00ffcc" bgcolor="#000000" >  
<mqpo:Pict shape="0" autoscale="false" intercept="true" isurl="true" url="http://img1.gtimg.com/gamezone/pics/hv1/219/41/2200/143065674.jpg"/>
</mqpo:Basic>
<!-- 文本图片图层 -->
<mqpo:Basic group="a200" useid="false" valid="true" issave="true" iscache="false" isBG="false" x="100" y="420" width="200" height="200" color="#00ffcc" bgcolor="#000000" >  
<mqpo:Font text="文字内容22222" name="宋体" style="0" size="15" color="000000" linespacing="2" issmooth="false"/>
</mqpo:Basic>
 <!-- 特殊图片 二维码 -->
<mqpo:Basic group="a300" useid="false" valid="true" issave="true" iscache="false" isBG="false" x="100" y="500" width="150" height="150" color="#00ffcc" bgcolor="#000000" >  
<mqpo:SP_QRCODE spid="2" size="150" margin="0" style="11" colorarray="FC4F4F" url="http://kuaigoushop.99114.com/login/toHomeshop.do?shopid=${shopid}&ISDistri=yes"/>
</mqpo:Basic>
<!-- 特殊图片 二维码 最后进行渲染 -->
<mqpo:Basic group="a400" useid="false" valid="true" issave="true" iscache="false" isBG="false" x="100" y="700" width="150" height="150" color="#00ffcc" bgcolor="#000000" >  
<mqpo:SP_QRCODE spid="2" size="150" margin="0" style="11" colorarray="FC4F4F" url="http://kuaigoushop.99114.com/login/toHomeshop.do?shopid=${shopid}&ISDistri=yes"/>
<mqpo:rendering id="11" colorarray="aa55ee" istransparent="true" iswhite="true"/>
</mqpo:Basic>   
</pre></code>