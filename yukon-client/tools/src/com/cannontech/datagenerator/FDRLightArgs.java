package com.cannontech.datagenerator;

/**
 * Arguments to FDRLight
 * @author alauinger
 */
public class FDRLightArgs {
	private String srcHost;	
	private String destHost;
	
	private int srcPort;
	private int destPort;
	
	/**
	 * Returns the destHost.
	 * @return String
	 */
	public String getDestHost() {
		return destHost;
	}

	/**
	 * Returns the destPort.
	 * @return int
	 */
	public int getDestPort() {
		return destPort;
	}

	/**
	 * Returns the srcHost.
	 * @return String
	 */
	public String getSrcHost() {
		return srcHost;
	}

	/**
	 * Returns the srcPort.
	 * @return int
	 */
	public int getSrcPort() {
		return srcPort;
	}

	/**
	 * Sets the destHost.
	 * @param destHost The destHost to set
	 */
	public void setDestHost(String destHost) {
		this.destHost = destHost;
	}

	/**
	 * Sets the destPort.
	 * @param destPort The destPort to set
	 */
	public void setDestPort(int destPort) {
		this.destPort = destPort;
	}

	/**
	 * Sets the srcHost.
	 * @param srcHost The srcHost to set
	 */
	public void setSrcHost(String srcHost) {
		this.srcHost = srcHost;
	}

	/**
	 * Sets the srcPort.
	 * @param srcPort The srcPort to set
	 */
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

}
