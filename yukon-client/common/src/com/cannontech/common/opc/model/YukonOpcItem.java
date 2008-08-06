package com.cannontech.common.opc.model;

public interface YukonOpcItem {
	
	public int getHandle();
	public void setHandle(int handle);
	
	public int getServerHandle();
	public void setServerHandle(int serverHandle);
	
	public String getGroupName();
	public void setGroupName(String name);
	
	public String getServerName();
	public void setServerName(String serverName);
	
	public int getPointType();
	public void setPointType(int pointType);
   
	public int getPointId();
    public void setPointId(int pointId);
    
    public Integer getOffset();
    public void setOffset(int offset);

    public Double getMultiplier();
    public void setMultiplier(double multiplier);

	public String getItemName();
	public void setItemName(String opcItemName);
}
