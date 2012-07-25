/**
 * data-server. 2012-7-24
 */
package com.anxlng.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class GISFunc {

    /* 数字格式化工具 */
    static NumberFormat format = NumberFormat.getNumberInstance();
    //地球半径 极半径=6356.755 平均半径=6371.004 赤道半径=6378.140，单位：千米
    static final double EARTH_RADIUS = 6378.137;
    
    /* 这个后期若要进行国际化，需要修改 */
    static String[] dir = 
            {"正北", "东北", "正东", "东南", "正南", "西南", "正西", "西北", "正北"};
    
    
    /**
     * 弧度，某个经度或者纬度所在切面的弧度
     * 
     * @param d 经度 &　纬度
     * @return
     */
    private static double radian(double d) {
        return d * Math.PI / 180.0;
    }
    
    /**
     * 获取两点之间的距离
     * @param lng1 第一个点经度
     * @param lat1 第一个点纬度
     * @param lng2 第二个点经度
     * @param lat2 第二个点纬度
     * @return 返回零点距离，单位：千米
     */
    public static double GetDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = radian(lat1);
        double radLat2 = radian(lat2);
        double a = radLat1 - radLat2;
        double b = radian(lng1) - radian(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }
    
    public static double GetDistance(MPoint pt1, MPoint pt2) {
        return GetDistance(pt1.X, pt1.Y, pt2.X, pt2.Y);
    }
    
    /**
     * 转换“度”格式的纬度为 度分秒 格式
     * @param lat 纬度
     * @return
     */
    public static String latDegToDMS(double lat) {
        return degToDMS(lat) + (lat > 0 ? "N" : "S");
    }
    
    /**
     * 转换“度”格式的纬度为 度分秒 格式
     * @param lon 经度
     * @return
     */
    public static String lonDegToDMS(double lon) {
        return degToDMS(lon) + (lon > 0 ? "E" : "W");
    }
    
    /* 转换“度”格式的经度或者纬度为 度分秒 格式*/
    private static String degToDMS(double geo) {
        
        double _geo = Math.abs(geo);
        int d = (int) _geo;
        double _m = (_geo - d) * 60;
        int m = (int) _m;
        double _s = (_m - m) * 60;

      //设定小数最大为1位   ，那么显示的最后会四舍五入的
        String s = format(_s, 1);
        return d + "°" + m + "′" + s + "″";
    }
    
    public static String format(double d, int digits) {
        synchronized (format) { // 要进行同步，
            format.setMaximumFractionDigits(digits);
            return format.format(d);
        }
    }

    public static String degFormat(double geo) {
        return format(geo, 4);
    }
    
    /**
     * 方向的文字描述，通过方向获取角度的文字描述
     * @param d
     * @return
     */
    public static String direction(double d){
        if (d < 0) d = d + 360;
        int index = (int)((d + 22.5) / 45);
        return dir[index];
    }
    
    /**
     * 格式化方向角度, 角度以正北为0度, 顺时针旋转
     * 返回的格式为 - 角度(描述) 如：-22.8(西北)
     * @param d 方向角度
     * @return 
     */
    public static String formatDirection(double d) {
        StringBuilder bd = new StringBuilder(format(d, 1));
        bd.append('(');
        bd.append(direction(d));
        bd.append(')');
        return bd.toString();
    }
    
    /** 计算多边形面积
     * @param list 多边形的顶点坐标队列
     * @return
     */
    public static double areaOfPolygon(List<int[]> list){
        if (list == null) return 0;
        
        int size = list.size();
        if (size < 3) return 0;
        
        double s = list.get(0)[1] * (list.get(size - 1)[0] - list.get(1)[0]);
        for (int i = 0; i < list.size(); i++) {
            s += list.get(i)[1] * (list.get(i - 1)[0] - list.get((i + 1) % size)[0]);
        }

        return s / 2d;
    }
    
    /**
     * 验证区域多边形的边是否有相交的现象,这个效率应该是特别低的，不停的在计算面积
     * @return
     */
    public static boolean checkPolygon(List<int[]> list){
        boolean b = false;
        int size = list.size();
        double s = 1;
        int count = 0;
        for(int i = 0 ; i< size ; i++){
            ArrayList<int[]> smalList = new ArrayList<int[]>();
            smalList.add(list.get(i % size));
            smalList.add(list.get((i + 1) % size));
            smalList.add(list.get((i + 2) % size));
            if(i == 0) {
                s = areaOfPolygon(smalList);
            } else {
                if(s * areaOfPolygon(smalList) < 0){
                    count ++;
                }
            }
        }
        if(count >= 2){
            b = true;
        }
        return b;
    }
    
 
    /**判断点在多边形内还是多边形外
     * @param p
     * @param list
     * @return true :内，   false ： 外
     */
    public static boolean pointInPolygon(double[] p, List<double[]> list){
        boolean  flag=false;
        int n = list.size();
        
        for(int i = 0; i < n; i++) {
            double dp[] = list.get(i);
            if (p[1] < dp[1] && p[1] < list.get((i+1) % n)[1]) {
                continue;
            }
            
            // 如果这里出现
            if (p[0] >= dp[0] && p[0] >= list.get((i + 1) % n)[1]) {
                continue;
            }
            
            double dx = list.get((i + 1) % n)[0] - dp[0];
            double dy = list.get((i + 1) % n)[1] - dp[1];
            
            double t = (p[0] - dp[0]) / dx;//求得交点的t值
            double y = t * dy + dp[1];

                
            if(y <= p[1] && t >= 0 && t <= 1) {
                flag = !flag;
            } 
        }
        return flag;
    }
    
    /**判断点在多边形内还是多边形外
     * @param p
     * @param list
     * @return true :内，   false ： 外
     */
    public static boolean pointInPolygon(MPoint p, List<double[]> list){
        boolean  flag=false;
        int n = list.size();
        
        for(int i = 0; i < n; i++) {
            double dp[] = list.get(i);
            if (p.Y < dp[1] && p.Y < list.get((i+1) % n)[1]) {
                continue;
            }
            
            // 如果这里出现
            if (p.X >= dp[0] && p.X >= list.get((i + 1) % n)[1]) {
                continue;
            }
            
            double dx = list.get((i + 1) % n)[0] - dp[0];
            double dy = list.get((i + 1) % n)[1] - dp[1];
            
            double t = (p.X - dp[0]) / dx;//求得交点的t值
            double y = t * dy + dp[1];

                
            if(y <= p.Y && t >= 0 && t <= 1) {
                flag = !flag;
            } 
        }
        return flag;
    }
    

    static class MPoint {

        final double X;
        final double Y;

        private MPoint(double lon, double lat) {
            this.X = lon;
            this.Y = lat;
        }

        static MPoint getInstance(double lon, double lat) {
            return new MPoint(lon, lat);
        }
    }
}
