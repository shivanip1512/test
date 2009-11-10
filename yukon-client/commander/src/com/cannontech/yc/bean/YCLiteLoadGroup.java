/*
 * Created on Aug 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yc.bean;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class YCLiteLoadGroup implements YukonPao {
    private PaoIdentifier paoIdentifier = null;
	private int routeID = -1;
	private double kwCapacity = 0; 
	private String serial = null;
	
	/**
	 * 
	 */
	public YCLiteLoadGroup(int groupID_, double kwCapacity_, int routeID_, String serial_, PaoType paoType) {
		super();
		paoIdentifier = new PaoIdentifier(groupID_, paoType); 
		kwCapacity = kwCapacity_;
		routeID = routeID_;
		serial = serial_;
	}

	public String getSerial()
	{
		return serial;
	}
    public double getKwCapacity()
    {
        return kwCapacity;
    }
    public int getRouteID()
    {
        return routeID;
    }
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }
}