/**
 * data-import. 2012-8-2
 */
package com.anxlng.di;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Query;


import org.apache.log4j.Logger;

import com.anxlng.util.GISFunc;
import com.anxlng.util.TimeFunc;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class CenterData {

    protected final Logger logger = Logger.getLogger(getClass());
    protected final Pattern dataPattern;
    protected final Pattern splitPattern = Pattern.compile("\\|");
    protected final SqlBatch batch = new SqlBatch();
    
    public CenterData(String dataRegx) {
        dataPattern = Pattern.compile(dataRegx);
    }
    
    public int dataImport(File file) {
        if (file == null) {
            return 0;
        }
        
        BufferedInputStream bis = null;
        BufferedReader reader = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            // 这里后边可以进行优化，根据文件的大小设置缓冲区大小
            reader = new BufferedReader(new InputStreamReader(bis), 5 * 1024 * 1024);
            String row = "";
            while ((row = reader.readLine()) != null) {
                Matcher mtch = dataPattern.matcher(row);
                if (mtch.find()) {
                    logger.info(mtch.group(0));
                    String serverTime = mtch.group(1);
                    String message = mtch.group(2);
                    String[] msgs = splitPattern.split(message);
                    handler(msgs, serverTime);
                }
            }
        } catch (Exception ex) {
            logger.warn("pattern exception", ex);
            
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e){}
            }
            if (reader != null) {
                try {
                    bis.close();
                } catch (IOException e){}
            }
        }
        return 0;
        
    }
    
    public void handler(String[] msgs, String serverTime) {
        if ("Z32".equals(msgs[0])) {
            aisHandler(msgs, serverTime);
        } else if ("Z12".equals(msgs[0])) {
            gpsHandler(msgs, serverTime);
        } else if ("Z00".equals(msgs[0])) {
            oldGpsHandler(msgs, serverTime);
        }
 
    }
    
    public void aisHandler(String[] data, String serverTime) {
        String termId = "AIS" + data[3];
        int messageId = Integer.parseInt(data[1]);
        switch(messageId) {
        case 1: 
        case 2:
        case 3: msg1_3(data, termId, serverTime);break;
        case 18: 
        case 19: msg18(data, termId, serverTime);break;
        default: break;
        }
    }
    
    private void msg1_3(String[] data, String termId, String serverTime) {
        if (data.length < 17) return;
        HisPosition hp = new HisPosition();
        hp.setTermId(termId);

        hp.setUpdateTime(serverTime);
        hp.setServerTime(serverTime);
        hp.setLon(Double.parseDouble(data[8]));
        hp.setLat(Double.parseDouble(data[9]));
        hp.setSpeed((int)Double.parseDouble(data[6]));
        hp.setDirection(Double.parseDouble(data[10]));
        store(hp);
    }
    
    private void msg18(String[] data, String termId, String serverTime) {
        if (data.length < 16) return;
        HisPosition hp = new HisPosition();
        hp.setTermId(termId);
        hp.setSpeed((int)Double.parseDouble(data[5]));
        
        double lon = Double.parseDouble(data[7]);
        double lat = Double.parseDouble(data[8]);
        hp.setLon(lon);
        hp.setLat(lat);
        hp.setDirection(Double.parseDouble(data[9]));
        hp.setUpdateTime(serverTime);
        hp.setServerTime(serverTime);
        store(hp);
    }
    
    
    public void gpsHandler(String[] msgs, String serverTime) {
        HisPosition hp = new HisPosition();
        hp.setTermId(msgs[1]);
        hp.setUpdateTime(msgs[2]);
        hp.setServerTime(serverTime);
       
        hp.setDirection(Integer.parseInt(msgs[3]));
        hp.setSpeed((int)Double.parseDouble(msgs[4]));
        hp.setLon(Double.parseDouble(msgs[5]));
        hp.setLat(Double.parseDouble(msgs[6]));
        store(hp);
    }
    
    public void oldGpsHandler(String[] msgs, String serverTime) {
        
        int len = msgs[2].length();
        if (msgs[2].length() < 45) {
            return;
        }
        
        if (msgs[2].lastIndexOf(".") != len - 3) {
            return;
        }
        
      //UD00 014427A2139.2500N11224.0710E-24.3120802173.00
        String gps = msgs[2].substring(len-45);
        char valid = gps.charAt(6);
        if (valid == 'V') { //无效的数据
            return;
        }
        String time = gps.substring(0, 6) + gps.substring(33, 39);
        String r = TimeFunc.transform(time, "HHmmssyyMMdd", TimeFunc.DATE_TIME);
        String updateTime = TimeFunc.utcToDefault(r, TimeFunc.DATE_TIME);  // 获取当地时间
        
        double lat = GISFunc.dmToDeg(gps.substring(7, 17));
        double lon = GISFunc.dmToDeg(gps.substring(17, 28));
        
        int speed = (int)Double.parseDouble(gps.substring(28, 33));
        if (speed < 0) {
            speed = 0;
        }
        
        double dir = Double.parseDouble(gps.substring(28, 33));
        if (dir < 0) {
            dir = 0;
        }
        
        HisPosition hp = new HisPosition();
        hp.setTermId(msgs[1]);
        hp.setDirection(dir);
        hp.setSpeed(speed);
        hp.setUpdateTime(updateTime);
        hp.setServerTime(serverTime);
        hp.setLat(lat);
        hp.setLon(lon);
        store(hp);
    }
    
    public void store(HisPosition hp) {
//        batch.add(hp);
        logger.debug(hp);
    }
    
    
    public static void main(String[] args) {
        String gpsReg = "(2012-08-01 [0-1][0-9]:[0-5][0-9]:[0-5][0-9]) - \\[/10\\.1\\.2\\.\\d{2}:\\d{1,}\\]message->" +
                "((Z00|Z32|Z12)\\|.*)";
        CenterData cd = new CenterData(gpsReg);
        File dir = new File("data");
        File[] logs = dir.listFiles();
        for (File log : logs) {
            if (log.isFile()) {
                cd.dataImport(log);
            }
        }
        
        //中心[INFO][pool-6-thread-7]2012-08-02 09:44:25 - [/10.1.2.34:43976]message->Z32|1|0|373068000|正常航向|5|6|1|116.709232|21.762395|32|29|26|0|0|0|2237-0-4:47
        //中心[INFO][pool-6-thread-4]2012-08-02 09:44:32 - [/10.1.2.34:43977]message->Z00|0460008063204752|UD00014427A2139.2500N11224.0710E-24.3120802173.00
        //中心[INFO][pool-6-thread-5]2012-08-02 09:44:32 - [/10.1.2.34:43975]message->Z12|BDXT00000278033|2012-08-02 09:40:21|186|13|112.096087|20.525312|0|0|0

        String gpsReg1 = "(2012-08-01 [0-1][0-9]:[0-5][0-9]:[0-5][0-9]) - \\[/10\\.1\\.2\\.\\d{2}:\\d{1,}\\]message->" +
                "((Z00|Z32|Z12)\\|.*)";
        
        String input = "中心[INFO][pool-6-thread-4]2012-08-01 09:44:32 - [/10.1.2.34:43977]message->" +
        		"Z00|0460008063204752|UD00014427A2139.2500N11224.0710W-24.3120802173.00" ;
        input = "中心[INFO][pool-6-thread-5]2012-08-01 09:44:32 - [/10.1.2.34:43975]message->" +
        		"Z12|BDXT00000278033|2012-08-02 09:40:21|186|13|112.096087|20.525312|0|0|0" ;
        input = "中心[INFO][pool-6-thread-7]2012-08-01 09:44:25 - [/10.1.2.34:43976]message->" +
        		"Z32|1|0|373068000|正常航向|5|6|1|116.709232|21.762395|32|29|26|0|0|0|2237-0-4:47";
        Pattern p = Pattern.compile(gpsReg1);
        Matcher m = p.matcher(input);
        
        Pattern sp = Pattern.compile("\\|");
        
//        System.out.println(m.find());
        if (m.find()) {
            System.out.println(m.group(0));
            System.out.println(m.group(1));
            System.out.println(m.group(2));
            System.out.println(m.group(3));
            System.out.println(m.group());
            System.out.println(m.groupCount());
            String[] data = sp.split(m.group(2));
            for (String d : data) {
                System.out.println(d);
            }
            CenterData cd1 = new CenterData(gpsReg1);
//            cd.gpsHandler(data, m.group(1));
            cd1.aisHandler(data, m.group(1));
        }
        
        
           
    }
    
}
