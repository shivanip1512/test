/**
 * 
 */
package com.cannontech.sensus;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class GpuffConfig {
    @SuppressWarnings("unused")
    private Logger log = YukonLogManager.getLogger(GpuffConfig.class);

    int serial;
    String apn = null;
    String user = null;
    String password = null;
    String ip0 = null;
    String port0 = null;
    String ip1 = null;
    String port1 = null;
    boolean periodic = true;
    
    public int getSerial() {
        return serial;
    }
    public void setSerial(int serial) {
        this.serial = serial;
    }
    public String getApn() {
        return apn;
    }
    public void setApn(String apn) {
        if(apn == null || apn.compareTo("") == 0) apn = "isp.cingular";
        this.apn = sizeString(apn, 64);
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        if(user == null || user.compareTo("") == 0) user = null;
        this.user = sizeString(user, 16);
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        if(password == null || password.compareTo("") == 0) password = null;
        this.password = sizeString(password, 8);
    }
    public String getIp0() {
        return ip0;
    }
    public void setIp0(String ip0) {
        if(ip0 == null || ip0.compareTo("") == 0) ip0 = null;
        this.ip0 = sizeString(ip0, 64);
    }
    public String getPort0() {
        return port0;
    }
    public void setPort0(String port0) {
        if(port0 == null || port0.compareTo("") == 0) port0 = null;
        this.port0 = sizeString(port0, 6);
    }
    public String getIp1() {
        return ip1;
    }
    public void setIp1(String ip1) {
        if(ip1 == null || ip1.compareTo("") == 0) ip1 = null;
        this.ip1 = sizeString(ip1, 64);
    }
    public String getPort1() {
        return port1;
    }
    public void setPort1(String port1) {
        if(port1 == null || port1.compareTo("") == 0) port1 = null; 
        this.port1 = sizeString(port1, 6);
    }
    
    private String sizeString(String src, int len) {
        if(src != null && src.length() > len)
            return src.substring(0, len);

        return src;
    }
    
    @Override
    public String toString() {
        return ( getSerial() + 
        " U/P:" + getUser()+
        "/" + getPassword() + 
        " APN:" + getApn() +
        " IP0:" + getIp0()+ 
        ":" + getPort0() +
        " IP1:" + getIp1() +
        ":" + getPort1() +
        " Periodic: " + isPeriodic());
    }

    public boolean isPeriodic() {
        return periodic;
    }
    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((apn == null) ? 0 : apn.hashCode());
        result = prime * result + ((ip0 == null) ? 0 : ip0.hashCode());
        result = prime * result + ((ip1 == null) ? 0 : ip1.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + (periodic ? 1231 : 1237);
        result = prime * result + ((port0 == null) ? 0 : port0.hashCode());
        result = prime * result + ((port1 == null) ? 0 : port1.hashCode());
        result = prime * result + serial;
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GpuffConfig other = (GpuffConfig) obj;
        //log.info("This : " + this.toString());
        //log.info("Other: " + other.toString());
        if (apn == null) {
            if (other.apn != null)
                return false;
        } else if (!apn.equals(other.apn))
            return false;
        if (ip0 == null) {
            if (other.ip0 != null)
                return false;
        } else if (!ip0.equals(other.ip0))
            return false;
        if (ip1 == null) {
            if (other.ip1 != null)
                return false;
        } else if (!ip1.equals(other.ip1))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (periodic != other.periodic)
            return false;
        if (port0 == null) {
            if (other.port0 != null)
                return false;
        } else if (!port0.equals(other.port0))
            return false;
        if (port1 == null) {
            if (other.port1 != null)
                return false;
        } else if (!port1.equals(other.port1))
            return false;
        if (serial != other.serial)
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }
   
}