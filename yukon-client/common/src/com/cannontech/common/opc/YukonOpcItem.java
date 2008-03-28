package com.cannontech.common.opc;

import com.cannontech.database.data.lite.LitePoint;

import javafish.clients.opc.component.OpcItem;
/**
 * Written to hold the yukon point id that matches with the OPC object.
 * @author tspar
 *
 */
public class YukonOpcItem extends OpcItem {
	private static final long serialVersionUID = 634920768029324467L;
	
	private int pointId;
	private int pointType;
	private Integer offset = null;
	private Double multiplier = null;
	
	public YukonOpcItem(String itemName, boolean active, String accessPath, LitePoint point) {
	    super(itemName, active, accessPath);
        this.pointId = point.getPointID();
        this.pointType = point.getPointType();
	}
	
	public YukonOpcItem(String itemName, boolean active, String accessPath, LitePoint point, Integer dataOffset, Double dataMultiplier) {
	    this(itemName,active,accessPath,point);
        this.offset = dataOffset;
        this.multiplier = dataMultiplier;
    }
	
	public int getPointType() {
        return pointType;
    }

    public void setPointType(int pointType) {
        this.pointType = pointType;
    }
    
	public int getPointId() {
		return pointId;
	}

	public void setPointId(int pointId) {
		this.pointId = pointId;
	}


    public Integer getOffset() {
        return offset;
    }


    public void setOffset(int offset) {
        this.offset = offset;
    }


    public Double getMultiplier() {
        return multiplier;
    }


    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
	
}
