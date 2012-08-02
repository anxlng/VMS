/**
 * data-server. 2012-6-26
 */
package com.anxlng.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 下边format每次设置pattern，然后进行时间格式的转换；可能会出现同步问题
 * @author tangjixing <anxlng@sina.com>
 */
public class TimeFunc {

	public static final String DATE = "yyyy-MM-dd";
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MSEC = "yyyy-MM-dd HH:mm:ss.sss";
    public static final String DATE_TIME_MSEC_FOR_ID = "yyyyMMddHHmmsssss";
    
    private static final SimpleDateFormat DATE_FORMAT = (SimpleDateFormat)DateFormat.getDateInstance();
    

    /** 获得指定格式的时间的字符串表示
     * @param pattern 格式字符串
     * @return 相应格式的时间字符串
     */
    public static String getDateTime(String pattern) {
    	synchronized (DATE_FORMAT) {
    		DATE_FORMAT.applyPattern(pattern);
            return DATE_FORMAT.format(new Date());
    	}
    }
    
    /** 获得指定格式的时间的字符串表示
     * @param date java.util.Date
     * @return 相应格式的时间字符串
     */
    public static String getDateTime(Date date, String pattern) {
    	synchronized (DATE_FORMAT) {
    		DATE_FORMAT.applyPattern(pattern);
            return DATE_FORMAT.format(date);
    	}
    }
    
    /**
     * 把字符串格式数据转换成时间对象，如果pattern不符合或者
     * @param date
     * @param pattern pattern必须是标准的格式，否则程序运行不正常如果pattern为空，则默认为 yyyy-MM-dd HH:mm:ss
     * @return 若pattern格式不正确，则返回null
     */
    public static Date toDate(String date, String pattern) {
    	synchronized (DATE_FORMAT) {
    		DATE_FORMAT.applyPattern(pattern);
            try {
				return DATE_FORMAT.parse(date);
			} catch (ParseException e) {
				return null;
			}
    	}
    }
    
    /**
     * utc时间转成本地时间
     * @param date
     * @param pattern
     * @return
     */
    public static String utcToDefault(String date, String pattern) {
    	synchronized (DATE_FORMAT) {
    		DATE_FORMAT.applyPattern(pattern);
    		try {
				long millis = DATE_FORMAT.parse(date).getTime();
				millis = millis + TimeZone.getDefault().getRawOffset(); //加上偏移量
				return DATE_FORMAT.format(new Date(millis));
			} catch (ParseException e) {
				return null;
			}
    	}
    }
    
    /**
     * 本地时间转成utc时间
     * 说明：new Date().getTime() 这个毫秒数，是CMT+0 时区的毫秒数，只有在显示的时候才根据时区进行转换：
	 * 可以这样说，这个毫秒数，就是utc时间的毫秒数，
	 * 因此要转成哪个时区的，设置dateformat的时区，就根据时区获取格式化后的时间
	 * 两种办法，一种是直接设置时区，一种是求差偏移量
     * @param date
     * @param pattern
     * @return
     */
    public static String defaultToUtc(String date, String pattern) {
    	synchronized (DATE_FORMAT) {
    		DATE_FORMAT.applyPattern(pattern);
    		try {
				long millis = DATE_FORMAT.parse(date).getTime(); 
				DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//				millis = millis - TimeZone.getDefault().getRawOffset(); //减去偏移量
				return DATE_FORMAT.format(new Date(millis));
			} catch (ParseException e) {
				return null;
			} finally {
				DATE_FORMAT.setTimeZone(TimeZone.getDefault());
			}
    	}
    }
    
    
    
    public static Date toDate(String times) {
    	return toDate(times, DATE_TIME);
    }

    /**
     * yyyy-MM-dd
     */
    public static String getDate() {
        return getDateTime(DATE);
    }
    
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String getDateTime() {
        return getDateTime(DATE_TIME);
    }

    /**
     * yyyy-MM-dd HH:mm:ss:sss
     */
    public static String getDateTimeMsec() {
        return getDateTime(DATE_TIME_MSEC);
    }
    

    /**
     * yyyyMMddHHmmsssss
     */
    public static String getIdFromDataTimeMsec(){
        return getDateTime(DATE_TIME_MSEC_FOR_ID);
    }

    /**
     * 获得当前时间的长整形
     * @return
     */
    public static long currentTimeMillis() {
        return new Date().getTime();
    }
    
    /**
     * pattern yyyy-MM-dd HH:mm:ss
     */
    public static long stringtoLong(String times) {
    	return stringtoLong(times, DATE_TIME);
    }

    /**
     * 把传入的时间，格式化成长整形;
     * 注意，时间格式如果不匹配，方法不抛出异常，返回0L
     * @param date 2009-10-20 12:34:34
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 如果formats格式和times格式不匹配 ，返回0L
     */
    public static long stringtoLong(String date, String pattern) {
        try {
            return toDate(date, pattern).getTime();
        } catch (NullPointerException e) {
            return 0L;
        }
    }
    
    /**
     * 默认格式:yyyy-MM-dd HH:mm:ss:sss
     * @param time
     * @return
     */
    public static String longtoString(long time) {
        return longtoString(time, DATE_TIME);
    }

    /**获得传入的长整形时间转换成格式后的时间，
     * @param pattern
     * @param time
     * @return
     */
    public static String longtoString(long time, String pattern) {
        return getDateTime(new Date(time), pattern);
    }
    
    /**
     * 把当前的日期转换成另一种显示格式
     * @param time  当前的时间
     * @param nowPattern 当前时间的显示格式
     * @param toPattern 转换后的日期格式
     * @return
     */
    public static String transform(String time, String nowPattern, String toPattern) {
        synchronized (DATE_FORMAT) {
            DATE_FORMAT.applyPattern(nowPattern);
            try {
                Date d = DATE_FORMAT.parse(time);
                DATE_FORMAT.applyPattern(toPattern);
                return DATE_FORMAT.format(d);
            } catch (ParseException e) {
                return null;
            }
        }
    }
    
}
