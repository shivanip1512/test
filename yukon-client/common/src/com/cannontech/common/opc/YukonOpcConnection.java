package com.cannontech.common.opc;

import com.cannontech.common.opc.model.YukonOpcItem;

public interface YukonOpcConnection {
	
	public YukonOpcItem addReceiveItem(String groupName, String itemName, int pointId, int pointType, double multiplier, int offset);
	public YukonOpcItem addSendItem(String groupName, String itemName, int pointId, int pointType, double multiplier, int offset);
	public YukonOpcItem getOpcReceiveItem(int clientHandle);
	public YukonOpcItem getOpcSendItem(int clientHandle);
	
	public void setStatusItemName(String name);
	
	public boolean removeItem(YukonOpcItem item);

	
	public void setServerName(String name);
	public String getServerName();
	
	public boolean connect();
	
	public boolean isConnected();
	public boolean isShutdown();
	public boolean isEmpty();
	
	public void shutdown();
	
    public void removeOpcConnectionListener(OpcConnectionListener listener);
    public void addOpcConnectionListener(OpcConnectionListener listener);
}
