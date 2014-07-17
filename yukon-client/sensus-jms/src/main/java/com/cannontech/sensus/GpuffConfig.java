/**
 * 
 */
package com.cannontech.sensus;

import java.util.Date;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class GpuffConfig {
    @SuppressWarnings("unused")
    private Logger log = YukonLogManager.getLogger(GpuffConfig.class);

    public static int GPUFF_APN_SIZE = 64;
    public static int GPUFF_IP_SIZE = 64;
    public static int GPUFF_PORT_SIZE = 6;
    public static int GPUFF_USER_SIZE = 24;
    public static int GPUFF_PW_SIZE = 24;
    protected int columnNum;
    int serial;
    String requestConfig = null;
    String apn = null;
    String fromUser = null;
    String fromPassword = null;
    String user = null;
    String password = null;
    String ip0 = null;
    String port0 = null;
    String ip1 = null;
    String port1 = null;
    int periodic = 0;
    
    private int fwMajor = 0;
    private int fwMinor = 0;
    private int resetCount = 0;
    
    private Date lastReconfigDate = new Date();
    private int reconfigCount = 0;
    
    public int getSerial() {
      return this.serial;
    }
    public void setSerial(int serial) {
      this.serial = serial;
    }
    public String getApn() {
      return this.apn;
    }
    
    public int getReconfigLimit() {
        return 5;
    }
    public long getReconfigTimeout() {
        return 1800000L;
    }
    public boolean isTargetGAO() {
        boolean targetGAO = false;
        
        if(ip0 != null && (ip0.equals("www.gridadvisoronline.com") || ip0.equals("216.17.94.141"))) 
                targetGAO = true;
        if(ip1 != null && (ip1.equals("www.gridadvisoronline.com") || ip1.equals("216.17.94.141"))) 
            targetGAO = true;
        
        return targetGAO;
    }
    public void setApn(String apn) {
      if ((apn == null) || (apn.compareTo("") == 0)) apn = "isp.cingular";
      this.apn = sizeString(apn, GPUFF_APN_SIZE);
    }
    public String getUser() {
      return this.user;
    }
    public void setUser(String user) {
      if ((user == null) || (user.compareTo("") == 0)) user = null;
      this.user = sizeString(user, GPUFF_USER_SIZE);
    }
    public String getPassword() {
      return this.password;
    }
    public void setPassword(String password) {
      if ((password == null) || (password.compareTo("") == 0)) password = null;
      this.password = sizeString(password, GPUFF_PW_SIZE);
    }
    public String getIp0() {
      return this.ip0;
    }
    public void setIp0(String ip0) {
      if ((ip0 == null) || (ip0.compareTo("") == 0)) ip0 = null;
      this.ip0 = sizeString(ip0, GPUFF_IP_SIZE);
    }
    public String getPort0() {
      return this.port0;
    }
    public void setPort0(String port0) {
      if ((port0 == null) || (port0.compareTo("") == 0)) port0 = null;
      this.port0 = sizeString(port0, GPUFF_PORT_SIZE);
    }
    public String getIp1() {
      return this.ip1;
    }
    public void setIp1(String ip1) {
      if ((ip1 == null) || (ip1.compareTo("") == 0)) ip1 = null;
      this.ip1 = sizeString(ip1, GPUFF_IP_SIZE);
    }
    public String getPort1() {
      return this.port1;
    }
    public void setPort1(String port1) {
      if ((port1 == null) || (port1.compareTo("") == 0)) port1 = null;
      this.port1 = sizeString(port1, GPUFF_PORT_SIZE);
    }

    private String sizeString(String src, int len) {
      if ((src != null) && (src.length() > len)) {
        return src.substring(0, len);
      }
      return src;
    }

    public String toString()
    {
      return getSerial() + " From U/P:" + getFromUser() + "/" + getFromPassword() + " U/P:" + getUser() + "/" + getPassword() + " APN:" + getApn() + " IP0:" + getIp0() + ":" + getPort0() + " IP1:" + getIp1() + ":" + getPort1() + " Periodic: " + getPeriodic();
    }

    public int getPeriodic()
    {
      return this.periodic;
    }
    public void setPeriodic(int periodic) {
      this.periodic = periodic;
    }

    public int hashCode() {
      int result = 1;
      result = 31 * result + (this.apn == null ? 0 : this.apn.hashCode());
      result = 31 * result + (this.ip0 == null ? 0 : this.ip0.hashCode());
      result = 31 * result + (this.ip1 == null ? 0 : this.ip1.hashCode());

      result = 31 * result + this.periodic;
      result = 31 * result + (this.port0 == null ? 0 : this.port0.hashCode());
      result = 31 * result + (this.port1 == null ? 0 : this.port1.hashCode());
      result = 31 * result + this.serial;
      return result;
    }

    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      GpuffConfig other = (GpuffConfig)obj;
      if (this.apn == null) {
        if (other.apn != null)
          return false;
      } else if (!this.apn.equals(other.apn))
        return false;
      if (this.ip0 == null) {
        if (other.ip0 != null)
          return false;
      } else if (!this.ip0.equals(other.ip0))
        return false;
      if (this.ip1 == null) {
        if (other.ip1 != null)
          return false;
      } else if (!this.ip1.equals(other.ip1))
        return false;
      if (this.periodic != other.periodic)
        return false;
      if (this.port0 == null) {
        if (other.port0 != null)
          return false;
      } else if (!this.port0.equals(other.port0))
        return false;
      if (this.port1 == null) {
        if (other.port1 != null)
          return false;
      } else if (!this.port1.equals(other.port1))
        return false;
      if (this.serial != other.serial)
        return false;
      return true;
    }

    public void setRequestConfig(String requestConfig) {
      this.requestConfig = requestConfig;
    }
    public String getRequestConfig() {
      return this.requestConfig;
    }
    public boolean isRequestConfig() {
      return this.requestConfig.compareToIgnoreCase("Y") == 0;
    }
    public String getFromUser() {
      return this.fromUser;
    }
    public void setFromUser(String fromUser) {
      if ((fromUser == null) || (fromUser.compareTo("") == 0)) fromUser = null;
      this.fromUser = sizeString(fromUser, GPUFF_USER_SIZE);
    }
    public String getFromPassword() {
      return this.fromPassword;
    }
    public void setFromPassword(String fromPassword) {
      if ((fromPassword == null) || (fromPassword.compareTo("") == 0)) fromPassword = null;
      this.fromPassword = sizeString(fromPassword, GPUFF_PW_SIZE);
    }
    public void decodeRow(String[] row) {
      this.columnNum = 0;

      String str = row[(this.columnNum++)];
      setSerial(Integer.parseInt(str));
      str = row[(this.columnNum++)];
      setRequestConfig(str);
      str = row[(this.columnNum++)];
      setFromUser(str);
      setFromPassword(row[(this.columnNum++)]);
      setUser(row[(this.columnNum++)]);
      setPassword(row[(this.columnNum++)]);
      setApn(row[(this.columnNum++)]);
      setIp0(row[(this.columnNum++)]);
      setPort0(row[(this.columnNum++)]);
      setIp1(row[(this.columnNum++)]);
      setPort1(row[(this.columnNum++)]);
      str = row[(this.columnNum++)];
      setPeriodic(Integer.parseInt(str));
    }

    public void setFwMajor(int fwMajor) {
        this.fwMajor = fwMajor;
    }
    public int getFwMajor() {
        return fwMajor;
    }
    public void setFwMinor(int fwMinor) {
        this.fwMinor = fwMinor;
    }
    public int getFwMinor() {
        return fwMinor;
    }
    public void setResetCount(int resetCount) {
        this.resetCount = resetCount;
    }
    public int getResetCount() {
        return resetCount;
    }
    public void setLastReconfigDate(Date lastReconfigDate) {
        this.lastReconfigDate = lastReconfigDate;
    }
    public Date getLastReconfigDate() {
        return lastReconfigDate;
    }
    public void setReconfigCount(int reconfigCount) {
        this.reconfigCount = reconfigCount;
    }
    public int getReconfigCount() {
        return reconfigCount;
    }
}