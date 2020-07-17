package com.maqiao.was.fmktag.table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.maqiao.was.fmktag.table.dbtxt.BeanLine;
import com.maqiao.was.fmktag.table.dbtxt.MQURL;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 工具类
 * @author Sunjian
 * @version 1.0
 * @since jdk1.8
 */
@SuppressWarnings({"rawtypes"})
public final class Utils {
	/**
	 * 利用正则表达式判断字符串是否是数字
	 * @param str
	 * @return
	 */
	public static final boolean isNumeric2(final String str) {
		if(str==null || str.trim().length()==0)return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) return false;
		return true;
	}
	/**
	 * 判断是否为正整数
	 * @param val String
	 * @return boolean
	 */
	static final boolean isNumeric(String val) {
		Pattern p = Pattern.compile("[0-9]*");
		return p.matcher(val).matches();
	}
	/**
	 * 判断index是否在arrs数组中
	 * @param arrs int[]
	 * @param index int
	 * @return boolean
	 */
	static final boolean isExist(int[] arrs,int index) {
		for (int e : arrs)
			if (e == index) return true;
		return false;
	}

	/**
	 * 对int数组进行去重并从大到小排序
	 * @param arrs int[]
	 * @return int[]
	 */
	static final int[] arrayOrder(int... arrs) {
		int[] r = {};
		if (arrs == null) return r;
		if (arrs.length < 2) return arrs;
		HashSet<Integer> hashSet = new HashSet<Integer>(); // 去重
		for (int i = 0; i < arrs.length; i++) {
			hashSet.add(arrs[i]);
		}
		Set<Integer> set = new TreeSet<>(Collections.reverseOrder());
		set.addAll(hashSet);
		Integer[] integers = set.toArray(new Integer[] {});
		int[] arr2 = Arrays.stream(integers).mapToInt(Integer::valueOf).toArray();
		return arr2;
	}
	/**
	 * RandomAccessFile RandomAccessFile读出时，转换成UTF-8
	 * @param line String
	 * @return String
	 */
	static final String changedLine(final String line) {
		if (line == null) return null;
		try {
			byte buf[] = new byte[1];
			byte[] byteArray = new byte[line.length()];
			StringReader aStringReader = new StringReader(line);
			int character;
			int i = 0;
			while ((character = aStringReader.read()) != -1)
				byteArray[i++] = buf[0] = (byte) character;
			return new String(byteArray, "UTF-8");
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 把字符串按规则进行转化<br>
	 * autoChange: "iso-8859-1 to utf-8"
	 * @param string String
	 * @param autoChange String
	 * @return String
	 */
	static final String autoChange(final String string, final String autoChange) {
		if (string == null || string.length() == 0 || autoChange == null || autoChange.length() == 0 || autoChange.trim().toLowerCase().indexOf("to") == -1) return string;
		return autoChange(string, autoChange.trim().toLowerCase().split("to"));
	}

	/**
	 * 把字符串按规则进行转化<br>
	 * autoArray: {"iso-8859-1","utf-8"} <br>
	 * 注意:autoArray数组必须是2个单元
	 * @param string String
	 * @param autoArray String[]
	 * @return String
	 */
	static final String autoChange(String string, String... autoArray) {
		if (autoArray.length != 2) return string;
		try {
			return new String(string.getBytes(autoArray[0].trim()), autoArray[1].trim());
		} catch (UnsupportedEncodingException e) {
		}
		return string;
	}

	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * 是否过滤#右侧数据
	 * @param filenamepath String
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	static final StringBuilder readFile(String filenamepath, String enterStr, boolean delnotes) {
		File file = new File(filenamepath);
		if (!file.exists()) return new StringBuilder(0);
		return readFile(file,enterStr,delnotes);
	}
	/**
	 * 通过RandomAccessFile读文件 按行读 randomFile.readLine<br>
	 * 是否过滤#右侧数据
	 * @param file File
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	static final StringBuilder readFile(File file, String enterStr, boolean delnotes) {
		if(file==null || !file.exists())return new StringBuilder(0);
		StringBuilder sb = new StringBuilder(400);
		try (RandomAccessFile randomFile = new RandomAccessFile(file, "r"); FileChannel filechannel = randomFile.getChannel();) {
			randomFile.seek(0);
			FileLock lock;
			do {
				lock = filechannel.tryLock(0L, Long.MAX_VALUE, true);
			} while (null == lock);
			Thread.sleep(10);
			while (randomFile.getFilePointer() < randomFile.length()) {
				String str = changedLine(randomFile.readLine());
				if (str == null) continue;
				str = str.trim();
				if (str.length() == 0) continue;
				if (delnotes && str.indexOf('#') >= 0) str = str.substring(0, str.indexOf('#'));
				sb.append(str);
				if (randomFile.getFilePointer() < randomFile.length()) sb.append(enterStr);
			}
			lock.release();
			randomFile.close();
		} catch (Exception e) {
		}
		return sb;
	}
	/**
	 * 通过URL得到文件内容<br>
	 * 是否过滤#右侧数据
	 * @param url URL
	 * @param isReadUtf8 boolean
	 * @param enterStr String
	 * @param delnotes boolean
	 * @return StringBuilder
	 */
	static final StringBuilder readFile(final URL url, final boolean isReadUtf8, String enterStr, boolean delnotes) {
		StringBuilder sb = new StringBuilder(20);
		if (url == null) return sb;
		try {
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setConnectTimeout(30000);
			urlcon.setReadTimeout(30000);
			urlcon.connect();
			String returnCode = new Integer(urlcon.getResponseCode()).toString();
			if (!returnCode.startsWith("2")) return null;
			InputStream is = urlcon.getInputStream();
			InputStreamReader isr = isReadUtf8 ? new InputStreamReader(is, "utf-8") : new InputStreamReader(is);
			BufferedReader buffer = new BufferedReader(isr);
			String l = null;
			while ((l = buffer.readLine()) != null) {
				if (delnotes && l.indexOf('#') >= 0) l = l.substring(0, l.indexOf('#'));
				if (l.length() == 0) continue;
				sb.append(l);
				sb.append(enterStr);
			}
			buffer.close();
			is.close();
			return sb;
		} catch (IOException e) {
			return sb;
		}
	}

	/**
	 * 得到"#"左侧的链接地址
	 * @param file String
	 * @return String
	 */
	static final String getSource(String file) {
		if(file==null)return file;
		int index=file.indexOf("#");
		if(index<=0)return file;
		return file.substring(0,index);
	}
	/**
	 * 得到链接中的数值，如没找到则输出-1
	 * @param file String
	 * @return int
	 */
	static final int getNumber(String file) {
		if(file==null)return -1;
		if (file.indexOf("#") == -1) return -1;
		int index = file.lastIndexOf("#");
		String right=file.substring(index + 1, file.length());
		if(right.indexOf("$")>-1) return -1; // 出现$，则判断为字符串
		if (Utils.isNumeric(right))	return Integer.parseInt(right); // 没有$，并且为数字的则为辅助数值
		return -1;
	}
	/**
	 * 判断"#"后面的字符串，如没有"#"或为数值，则返回null<br>
	 * 如以"$"为前缀，则把随后的数值为字符串
	 * @param file String
	 * @return String
	 */
	static final String getChar(String file) {
		if(file==null)return null;
		if (file.indexOf("#") == -1) return null;
		int index = file.lastIndexOf("#");
		String right=file.substring(index + 1, file.length());
		if(right.indexOf("$")>-1) {
			int index2=right.indexOf("$");
			return right.substring(index2+1,right.length());
		}
		if (Utils.isNumeric(right))return null;
		return right;
	}

	/**
	 * 分多种格式解析单元格的值
	 * @param cell 单元格
	 * @return 单元格的值
	 */
	static String convertCellToString(Cell cell) {
		/* 如果为null会抛出异常，应当返回空字符串 */
		if (cell == null) return "";
		/*
		 * POI对单元格日期处理很弱，没有针对的类型，日期类型取出来的也是一个double值，所以同样作为数值类型
		 * 解决日期2006/11/02格式读入后出错的问题，POI读取后变成“02-十一月-2006”格式
		 */
		if (cell.toString().contains("-") && checkDate(cell.toString())) {
			String ans = "";
			try {
				ans = new SimpleDateFormat("yyyy/MM/dd").format(cell.getDateCellValue());
			} catch (Exception e) {
				ans = cell.toString();
			}
			return ans;
		}
		cell.setCellType(CellType.STRING);
		return cell.getStringCellValue();
	}

	/**
	 * 判断是否是“02-十一月-2006”格式的日期类型
	 * @param str String
	 * @return boolean
	 */
	static boolean checkDate(String str) {
		String[] dataArr = str.split("-");
		if (dataArr.length != 3) return false;
		try {
			int x = Integer.parseInt(dataArr[0]);
			String y = dataArr[1];
			int z = Integer.parseInt(dataArr[2]);
			if (x > 0 && x < 32 && z > 0 && z < 10000 && y.endsWith("月")) return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	/**
	 * 通过下标或sheetName得到sheet
	 * @param workbook Workbook
	 * @param sheetid int
	 * @param sheetname String
	 * @return Sheet
	 */
	static Sheet getSheet(Workbook workbook, int sheetid, String sheetname) {
		if (workbook == null) return null;
		int numberOfSheets = workbook.getNumberOfSheets();
		if (sheetid > -1 && sheetid < numberOfSheets) return workbook.getSheetAt(sheetid);
		if (sheetname != null && sheetname.length() > 0) {
			Sheet sheet = workbook.getSheet(sheetname.trim());
			if (sheet != null) return sheet;
		}
		return null;
	}
	/**
	 * 获取单元格各类型值，返回字符串类型
	 * @param cell Cell
	 * @return String
	 */
	static String getCellValueByCell(Cell cell) {
		//判断是否为null或空串
		if (cell == null || cell.toString().trim().equals("")) return "";
		CellType cellType = cell.getCellTypeEnum();
		if (cellType == null) return "";
		switch (cellType) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {  //判断日期类型
				/*
				 * Date ee = cell.getDateCellValue();
				 * return ee.toString();
				 * double dd = cell.getNumericCellValue();
				 * return String.valueOf(dd);
				 * return (new DecimalFormat("#.######").format(cell.getNumericCellValue()));
				 * cellValue = DateUtil.formatDateByFormat(cell.getDateCellValue(), "yyyy-MM-dd");
				 */
				return Utils.convertCellToString(cell);
			} else {
				double dd = cell.getNumericCellValue();
				return String.valueOf(dd);
			}
		case BOOLEAN:
			boolean bb = cell.getBooleanCellValue();
			return String.valueOf(bb);
		default:
			return "";
		}
	}

	/**
	 * 把一行，转成beanLine对象
	 * @param str String
	 * @return beanLine
	 */
	static final BeanLine beanLineChange(final String str,String colSign) {
		if (str == null || str.length() == 0) return null;
		BeanLine bean = new BeanLine();
		bean.setSplit(str, colSign);
		//String[] splitStr = str.split(columnSign);
		//bean.set(splitStr);
		return bean;
	}


	/**
	 * 通过下标或sheetName得到sheet
	 * @param workbook Workbook
	 * @return Sheet
	 */
	static final Sheet getSheet(Workbook workbook,int sheetid,String sheetname,int sourceNumber,String sourceChar) {
		int numberOfSheets = workbook.getNumberOfSheets();
		System.out.println("一共" + numberOfSheets + "个sheet");
		if (numberOfSheets == 0) return null;
		Sheet sheet = null;
		sheet = Utils.getSheet(workbook, sheetid, sheetname);
		if (sheet != null) return sheet;
		sheet = Utils.getSheet(workbook, sourceNumber, sourceChar);
		if (sheet != null) return sheet;
		return null;
	}
	/**
	 * 查出此目录中所有不同的扩展名的文件列表，过滤掉重复文件名
	 * @param path
	 * @param arrs
	 * @return
	 */
	public static final List<String> getFileList(String path,String... arrs) {
		List<String> alllist=new LinkedList<>();
		if(path==null || path.length()==0)return alllist;
		File file=new File(path);
		if(!file.exists())return alllist;
		/* 如果path为文件，则设置path为当前文件的路径 */
		if(file.isFile()) path=file.getAbsolutePath();
		for(String fileExt:arrs) {
			List<String> list=new LinkedList<>();
			getFileList(fileExt,list, path);
			for(String e:list) {
				if(!alllist.contains(e))alllist.add(e);
			}
		}
		return alllist;
	}
	/**
	 * 得到某个目录里所有的文件 按文件的扩展名
	 * @param list List &lt; String &gt;
	 * @param path String
	 */
	public static final void getFileList(String fileExt,List<String> list, String path) {
		File or = new File(path);
		File[] files = or.listFiles();
		if (files == null) return;
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith("."+fileExt))list.add(file.getAbsolutePath());
			if (file.isDirectory()) getFileList(fileExt,list, file.getAbsolutePath());
		}
	}
	/**
	 * 得到链接的扩展名
	 * @param filename String
	 * @return String
	 */
	public static final String getFileExtName(String filename) {
		if(filename==null || filename.length()==0)return "";
		int index=filename.lastIndexOf('.');
		if(index<0)return "";
		String right=filename.substring(index+1,filename.length());		
		for(char e:Consts.ACC_fileExtRightSingArr) {
			index=right.indexOf(e);
			if(index==-1)continue;
			right=right.substring(0,index);
		}
		return right;
	}

	static final boolean getStaticBoolean(Map params,boolean def,String...arrs) {
		Object obj = getStaticObject(params,arrs);
		if (obj == null) return def;
		if (!(obj instanceof TemplateBooleanModel)) return def;
		try {
			return ((TemplateBooleanModel) obj).getAsBoolean();
		} catch (TemplateModelException e) {
			e.printStackTrace();
			return def;
		}
	}
	/**
	 * 抽取String 如果没有对象，则返回null
	 * @param params Map
	 * @param arrs String[]
	 * @return String
	 */
	static final String getStaticString(Map params,String...arrs) {
		Object obj = getStaticObject(params,arrs);
		if (obj == null) return null;
		if (!(obj instanceof SimpleScalar)) return null;
		String result = ((SimpleScalar) obj).getAsString();
		return result != null ? result.trim() : null;
		
	}

	/**
	 * 从map中抽取参数对象
	 * @param params Map
	 * @param arrs String[]
	 * @return Object
	 */
	static final Object getStaticObject(Map params, String...arrs) {
		if (params == null || arrs == null || arrs.length == 0) return null;
		Iterator paramIter = params.entrySet().iterator();
		while (paramIter.hasNext()) {
			Map.Entry ent = (Map.Entry) paramIter.next();
			Object obj = ent.getKey();
			if (obj == null) continue;
			String paramName = (String) obj;
			for (String key : arrs)
				if (paramName.equals(key)) return (TemplateModel) ent.getValue();
		}
		return null;
		
	}
	/**
	 * 判断是否含有默认值
	 * @param arrs String[]
	 * @return boolean
	 */
	static boolean isDefault(String...arrs) {
		for(String e:arrs) {
			if(Consts.DefConfig.containsKey(e))return true;
		}		
		return false;
	}
	/**
	 * 得到默认值
	 * @param arrs String[]
	 * @return Object
	 */
	static Object getDefault(String...arrs) {
		for(String e:arrs) {
			if(Consts.DefConfig.containsKey(e))return Consts.DefConfig.get(e);
		}		
		return null;
	}
	/**
	 * 自动匹配时，判断文件扩展名是否在允许的范围内
	 * @param arrs String[]
	 * @return boolean
	 */
	static boolean isAllowExtendName(String...arrs) {
		for(String e:arrs) {
			if(e==null || e.trim().length()==0)continue;
			e=e.trim().toLowerCase();
			for(String f:Consts.ACC_AllowFileExtendName) {
				if(f.equals(f))return true;
			}
		}		
		return false;
	}
	/**
	 * 得到文件的InputStream，无论是本地还是网络格式
	 * @param request HttpServletRequest
	 * @param type int
	 * @param sourcefile String
	 * @return InputStream
	 */
	public static final InputStream getInputStream(HttpServletRequest request,int type,String sourcefile) {
		switch (type) {
		case 0:
			String base=getWebBasePath(request);
			String sourceDBPath = base + sourcefile;
			System.out.println("sourceDBPath:" + sourceDBPath);
			try {
				return new FileInputStream(new File(sourceDBPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		case 1:
			URL url = MQURL.getURL(request, sourcefile);
			if(url==null)return null;
			System.out.println("sourceDBPath:" + url.toString());
			return MQURL.getInputStream(url);
		default:
			return null;
		}
	}
	/**
	 * 得到本网址的绝对目录<br>
	 * 得到服务器上当前绝对路径 c:/aaa/root/ 即发布目录
	 * @param request HttpServletRequest
	 * @return String
	 */
	public static final String getWebBasePath(HttpServletRequest request) {
		if(request==null)return "";
		//System.out.println("request.getRequestURI():"+request.getRequestURI());
		//System.out.println("request.getRealPath(request.getRequestURI()):"+request.getRealPath(request.getRequestURI()));
		//return new File(request.getRealPath(request.getRequestURI())).getParent() + "/";
		String root=request.getSession().getServletContext().getRealPath("/");
		System.out.println("root:"+root);
		return root;
	}
	/**
	 * 得到本地一个文件，如果没有，则试着检索出一个，用自动检索<br>
	 * 如果没有，则返回null
	 * @param source String
	 * @param arr String[]
	 * @param autosearch boolean
	 * @param autosearchid int
	 * @return String
	 */
	public static final String getBasicFile(String source,String[] arr,boolean autosearch,int autosearchid) {
		if(source==null || source.trim().length()==0)return getBasicAutoFile(source,arr,autosearch,autosearchid);
		File file=new File(source);
		if(!file.exists())return getBasicAutoFile(source,arr,autosearch,autosearchid);
		if(file.isFile())return source;
		return getBasicAutoFile(source,arr,autosearch,autosearchid);
	}
	/**
	 * 得到自寻流文件
	 * @param source String
	 * @param arr String[]
	 * @param autosearch boolean
	 * @param autosearchid int
	 * @return String
	 */
	public static final String getBasicAutoFile(String source,String[] arr,boolean autosearch,int autosearchid) {
		if(!autosearch)return null;
		List<String> list=Utils.getFileList(source, arr);
		if(list.size()==0)return null;
		if(autosearchid>-1 && autosearchid<list.size())	return list.get(autosearchid);
		return list.get(0);
	}
	/**
	 * 整理路径
	 * @param path String
	 * @return String
	 */
	public static final String groomingPath(String path) {
		if(path==null|| path.length()==0)return path;
		path=path.replace('\\', '/');
		path=path.replaceAll("//", "/");	
		return path;
	}

	public static void main(String[] args) {
		//int[] arr3 = { 22, 11, 12, 0, 5, 9, -1, 2, 1, 12, 0 };
		//Arrays.stream(arrayOrder(arr3)).forEach(System.out::println); // 将结果数组输出
		//System.out.println("#############################################################");
		String[] arr= {
				"hyttp://aac.cc/aaa/ee.aa.txt$$",
				null,
				"",
				" ",
				".",
				".text",
				"hyttp://aac.cc/aaa/ee.aa.txt",
				"hyttp://aac.cc/aaa/ee.aa.#",
				"hyttp://aac.cc/aaa/ee.aa.txt#",
				
		};
		for(String e:arr) {
			System.out.println("e:"+getFileExtName(e));
		};
		String sourceDBPath="D:\\Eclipse\\eclipse-oxygen\\Workspaces\\was-zt-web\\WebContent\\/latform/2020_618/2020_618.txt";
		System.out.println("sourceDBPath:" + Utils.groomingPath(sourceDBPath));
	}

}
