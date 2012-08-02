/**
 * data-import. 2012-8-2
 */
package com.anxlng.di;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.anxlng.util.TimeFunc;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class SqlBatch {

    private final Queue<HisPosition> list = new ConcurrentLinkedQueue<HisPosition>();
    
    private final Object lock = new Object();
    
    private Runnable thread = null;
    
    private Executor executor = Executors.newSingleThreadExecutor();
    
    private DBSource dbSource = new DBSource();
    
    public void add(HisPosition hp) {
        list.add(hp);
        synchronized (lock) {
            if (thread == null) {
                thread = new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            handler();
                            synchronized(lock) {
                                if (list.isEmpty()) {
                                    thread = null;
                                    break;
                                }
                            }
                        }
                    }
                    
                };
                executor.execute(thread);
            }
        }
    }
    
    public void handler() {
        Connection conn = null;
        PreparedStatement ps = null;
        int i = 0;
        try {
            while(true) {
                HisPosition hp = list.poll();
                if (hp == null) {
                    return;
                }
                
                if (conn == null || conn.isClosed()) {
                    conn = dbSource.getConnection();
                }
                
                if (ps == null) {
                    String id = TimeFunc.transform(hp.getServerTime(), TimeFunc.DATE_TIME, "yyyyMM");
                    ps = conn.prepareStatement("insert into db2inst1.P_His"+ id + 
                    "(termID,UpdateTime,Direction,Speed,Lo,La,serverTime) values (?,?,?,?,?,?,?)");
                }
                
                ps.setString(1, hp.getTermId());
                ps.setString(2, hp.getUpdateTime());
                ps.setDouble(3, hp.getDirection());
                ps.setDouble(4, hp.getSpeed());
                ps.setDouble(5, hp.getLon());
                ps.setDouble(6, hp.getLat()); 
                ps.setString(7, hp.getServerTime()); 
                ps.addBatch();
                i++;
                if (i % 2000 == 0) { // 2000提交一次
                    ps.execute();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            
        } finally {
            dbSource.close(conn);
        }
        
    }
    
    private class DBSource {
        
        String dbUser = "db2inst1";
        String dbPassword = "db2admin";
        String dbUrl = "jdbc:db2://10.1.2.20:50000/iems";
        
        
        public DBSource() {
            try {
                Class.forName("com.ibm.db2.jcc.DB2Driver");
            } catch (ClassNotFoundException e) {
                
            }
        }

        Connection getConnection() throws SQLException {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }
        
        void close(Connection conn) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception ex) {
                
            }
            
        }
        
    }
    
}
