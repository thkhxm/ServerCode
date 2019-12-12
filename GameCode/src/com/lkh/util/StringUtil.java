package com.lkh.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * 字符串工具
 * 
 * @author tim.huang 2015年12月22日
 */
public class StringUtil {
	
	/**
	 *  ;  
	 */
	public static String DEFAULT_SP = "[;]";
	/**
	 *  ,
	 */
	public static String DEFAULT_ST = "[,]";
	/**
	 * =
	 */
	public static String DEFAULT_EQ = "[=]";
	
	/**
	 * :
	 */
	public static String DEFAULT_FH = "[:]";
	
	public static int[] toIntArray(String str,String sp) {
		if("".equals(str) || str == null){
			return new int[0];
		}
		//填充数值参数
		String [] v = str.split(sp);
		int [] vInt = new int[v.length];
		for(int tv = 0 ; tv < v.length; tv++){
			vInt[tv] = Integer.parseInt(v[tv]);
		}
		return vInt;
	}
	
	public static String [] toStringArray(String str,String sp){
		if("".equals(str) || str == null){
			return new String[0];
		}
		//填充数值参数
		String [] v = str.split(sp);
		return v;
	}

	public static String formatString(String str,Object... vals){
		StringBuffer sb = new StringBuffer();
		int start = 0,end = 0;
		for(Object val : vals){
			end = str.indexOf("{}");
			if(start>str.length() || end<0){
				break;
			}
			sb.append(str.substring(start,end));
			if(val instanceof Date){
				LocalDateTime da = LocalDateTimeUtil.toLocalDateTime((Date)val);
				sb.append(LocalDateTimeUtil.formatDate(da));
			}else if(val instanceof Throwable){
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				Throwable t = (Throwable)val;
				try {
					t.printStackTrace(pw);
					sb.append(sw.toString());
				} finally {
					pw.close();
				}
			}else{
				sb.append(val.toString());
			}
			str = str.substring(end+2);
		}
		sb.append(str);
		return sb.toString();
	}
	
	public static String formatSQL(Object... values){
		StringBuffer sb = new StringBuffer();
		int length = values.length;
		int over = length-1;
		for(int i =0 ; i < length;i++){
			if(values[i] instanceof Date){
				LocalDateTime da = LocalDateTimeUtil.toLocalDateTime((Date)values[i]);
				sb.append(LocalDateTimeUtil.formatDate(da));
			}else if(values[i] instanceof LocalDateTime){
				LocalDateTime da = (LocalDateTime)values[i];
				sb.append(LocalDateTimeUtil.formatDate(da));
			}else if(values[i] instanceof LocalDateTime){
				LocalDateTime la = (LocalDateTime)values[i];
				sb.append(LocalDateTimeUtil.formatDate(la));
			}else if(values[i] instanceof Boolean){
				boolean bl = (boolean)values[i];
				sb.append(bl?1:0);
			}else{
				sb.append(values[i]);
			}
			if(i==over){
				sb.append("\n");
			}else{
				sb.append("\t");
			}
		}
		return sb.toString();
	}
	
    public static String getRootPath(URL url) {
        String fileUrl = url.getFile();
        int pos = fileUrl.indexOf('!');

        if (-1 == pos) {
            return fileUrl;
        }

        return fileUrl.substring(5, pos);
    }

    public static String dotToSplash(String name) {
        return name.replaceAll("\\.", "/");
    }

    public static String trimExtension(String name) {
        int pos = name.indexOf('.');
        if (-1 != pos) {
            return name.substring(0, pos);
        }

        return name;
    }

    public static String trimURI(String uri) {
        String trimmed = uri.substring(1);
        int splashIndex = trimmed.indexOf('/');

        return trimmed.substring(splashIndex);
    }
    
    public static String upperCase(String str) {  
        char[] ch = str.toCharArray();  
        if (ch[0] >= 'a' && ch[0] <= 'z') {  
            ch[0] = (char) (ch[0] - 32);  
        }  
        return new String(ch);  
    }  
    
    

}
