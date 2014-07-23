/*
 * Created on Aug 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yc.bean;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YCLiteCBC{
    private int cbcID = -1;
    private int routeID = -1;
//    private String cbcType = null;
    private String serial = null;
    
    /**
     * 
     */
    public YCLiteCBC(int cbcID_, int routeID_, String serial_) {
        super();
        cbcID = cbcID_;
        routeID = routeID_;
//        cbcType = cbcType_;
        serial = serial_;
    }
    public int getCbcID()
    {
        return cbcID;
    }
    public int getRouteID()
    {
        return routeID;
    }
    public String getSerial()
    {
        return serial;
    }
//    public String getCbcType()
//    {
//        return cbcType;
//    }
}