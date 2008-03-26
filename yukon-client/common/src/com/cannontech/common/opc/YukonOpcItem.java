package com.cannontech.common.opc;

import javafish.clients.opc.component.OpcItem;
/**
 * Written to hold the yukon point id that matches with the OPC object.
 * @author tspar
 *
 */
public class YukonOpcItem extends OpcItem {
	private static final long serialVersionUID = 634920768029324467L;
	
	private Integer pointId;
	private Integer PointType;
	
    public YukonOpcItem(String itemName, boolean active, String accessPath, Integer pointId, Integer pointType) {
        super(itemName, active, accessPath);
        this.pointId = pointId;
        this.PointType = pointType;
    }

	
	public Integer getPointType() {
        return PointType;
    }

    public void setPointType(Integer pointType) {
        PointType = pointType;
    }
    
	public Integer getPointId() {
		return pointId;
	}

	public void setPointId(Integer pointId) {
		this.pointId = pointId;
	}
	
}
