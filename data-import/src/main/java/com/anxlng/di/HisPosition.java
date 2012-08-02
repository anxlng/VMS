/**
 * data-import. 2012-8-2
 */
package com.anxlng.di;

/**
 * 
 * @author tangjixing <anxlng@sina.com>
 */
public class HisPosition {

    private String termId;
    private int speed ;
    private double lon;
    private double lat;
    private double direction;
    private String updateTime;
    private String serverTime;
    /**
     * @return the termId
     */
    public String getTermId() {
        return termId;
    }
    /**
     * @param termId the termId to set
     */
    public void setTermId(String termId) {
        this.termId = termId;
    }
    /**
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }
    /**
     * @param speed the speed to set
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }
    /**
     * @param lon the lon to set
     */
    public void setLon(double lon) {
        this.lon = lon;
    }
    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }
    /**
     * @param lat the lat to set
     */
    public void setLat(double lat) {
        this.lat = lat;
    }
    /**
     * @return the direction
     */
    public double getDirection() {
        return direction;
    }
    /**
     * @param direction the direction to set
     */
    public void setDirection(double direction) {
        this.direction = direction;
    }
    /**
     * @return the updateTime
     */
    public String getUpdateTime() {
        return updateTime;
    }
    /**
     * @param updateTime the updateTime to set
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    /**
     * @return the serverTime
     */
    public String getServerTime() {
        return serverTime;
    }
    /**
     * @param serverTime the serverTime to set
     */
    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }
    
    public String toString() {
        return termId + "," + updateTime + "," + lon + "," + lat + 
                "," + speed + "," + direction + "," + serverTime;
    }
    
}
