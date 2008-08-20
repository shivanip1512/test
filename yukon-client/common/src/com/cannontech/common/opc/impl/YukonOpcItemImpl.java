package com.cannontech.common.opc.impl;

import com.cannontech.common.opc.model.YukonOpcItem;

/**
 * Written to hold the yukon point id that matches with the OPC object.
 * @author tspar
 *
 */
public class YukonOpcItemImpl implements YukonOpcItem {
	
	private int offset = 0;
	private double multiplier = 1.0;
	private String itemName = "";
	private int handle = 0;	
	private int serverHandle = 0;
	private String serverName;
	private String groupName;	
	
	private int pointType;
	private int pointId;
	
	public YukonOpcItemImpl() {
		
	}
	
	@Override
	public int getPointType() {
        return pointType;
    }
    
	@Override
	public int getPointId() {
		return pointId;
	}
	
	@Override
	public void setPointId (int pointId) {
		this.pointId = pointId;
	}
	
	@Override
	public void setPointType (int pointType) {
		this.pointType = pointType;
	}
	
	@Override
    public Integer getOffset() {
        return offset;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public Double getMultiplier() {
        return multiplier;
    }

    @Override
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
	public String getItemName() {
		return itemName;
	}

	@Override
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	@Override
	public int getHandle() {
		return handle;
	}
	@Override
	public void setHandle(int handle) {
		this.handle = handle;
	}
	
	@Override
	public int getServerHandle() {
		return serverHandle;
	}
	@Override
	public void setServerHandle(int serverHandle) {
		this.serverHandle = serverHandle;
	}
	
	@Override
	public String getGroupName() {
		return groupName;
	}
	@Override
	public void setGroupName(String name) {
		this.groupName = name;
	}

	@Override
	public String getServerName() {
		return serverName;
	}

	@Override
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
