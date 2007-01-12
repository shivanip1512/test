/**
 * Copyright (C) 2006 Sensus MS, all rights reserved.
 */

package com.amdswireless.messages.rx;

import java.io.Serializable;

/*
 * Byte		Data
 * 0	    Command Type
 *
 * Current command types are
 * 0    Command Acknowledgement
 * 1    Set Static Setup
 * 2    set Crystal Offset
 * 3    set Lat/Long
 * 4    Set Voltage Quality Settings
 * 5    Set Time
 * 6    Load Shed
 * 7    Demand Read
 * 8    Ping
 */

public class AppMessageType7 extends DataMessage implements AppMessage, Serializable {
    /**
     * 
     */
    private transient static final long serialVersionUID = 1L;
    private transient static final int offset = 31;
    
    private final String msgClass = "ACK";
    
    private int	commandType;
    private int	commandAckType;
    private int commandRfSeq;
    
    private boolean programmerReply;
    private boolean towerReply;
    private boolean	ack;
    
    private int	auxiliaryStatus;
    private int	commandAppSequence;
    private int	commandSignalLevel;
    private int	commandNoiseLevel;
    private int	programmerId;
    private int	setupFlags;

	/**
	 *  Default constructor for encoding.
	 */
	public AppMessageType7() {		
		super();
		super.setAppCode(7);
		super.setMessageType(7);	
	}
 
	/**
	 *  Default constructor for decoding. Extract information from Andorian message.
	 *  @param msg
	 */
    public AppMessageType7( char[] msg ) {
        super(msg);
        super.setMessageType(7);

        this.commandType = (int) (msg[offset]);  //msg[0 + offset]
        this.commandAckType = (int) (msg[1+offset]);
        this.commandRfSeq = (int) (msg[2+offset] & 0x1F);
        this.programmerReply = false;
        this.towerReply = false;
        //this.ack = (msg[2 + offset] & 0x80) != 0x80;
        this.ack = (msg[2 + offset] & 0x80) != 0x80;
        this.auxiliaryStatus = (int) (msg[3 + offset]);
        this.commandAppSequence = (int) (msg[4 + offset]);
        this.commandSignalLevel = (int) (msg[5 + offset]);
        this.commandNoiseLevel = (int) (msg[6 + offset]);
        this.programmerId = (int) (((msg[7 + offset] << 8)+(msg[8 + offset])));
        this.setupFlags = (int) (msg[9 + offset]);
    }

	/**
	 * Set commandType.
	 * @param commandType
	 */
    public void setCommandType(int commandType) {
        this.commandType = commandType;
        
        message[offset] = (char) (commandType); 
    }

	/**
	 * Set commandAckType.
	 * @param commandAckType
	 */
    public void setCommandAckType(int commandAckType) {
        this.commandAckType = commandAckType;
        
        message[1 + offset] = (char) (commandAckType);
    }

	/**
	 * Set commandRfSeq.
	 * @param commandRfSeq
	 */
    public void setCommandRfSeq(int commandRfSeq) {
        this.commandRfSeq = commandRfSeq;
        
        message[2 + offset] |= (char) (commandRfSeq & 0x1F);
    }

	/**
	 * Set programmerReply.
	 * @param programmerReply
	 */
    public void setProgrammerReply(boolean programmerReply) {
        this.programmerReply = programmerReply;
    }

	/**
	 * Set towerReply.
	 * @param towerReply
	 */
    public void setTowerReply(boolean towerReply) {
        this.towerReply = towerReply;
    }

	/**
	 * Set ack.
	 * @param ack
	 */
    public void setAck(boolean ack) {
        this.ack = ack;
        
        if (!ack) {
        	message[2 + offset] |= (char) (0x80);
        }   
    }

	/**
	 * Set auxiliaryStatus.
	 * @param auxiliaryStatus
	 */
    public void setAuxiliaryStatus(int auxiliaryStatus) {
        this.auxiliaryStatus = auxiliaryStatus;
        
        message[3 + offset] = (char) (auxiliaryStatus);
    }

	/**
	 * Set commandAppSequence.
	 * @param commandAppSequence
	 */
    public void setCommandAppSequence(int commandAppSequence) {
        this.commandAppSequence = commandAppSequence;
        
        message[4 + offset] = (char) (commandAppSequence);
    }

	/**
	 * Set commandSignalLevel.
	 * @param commandSignalLevel
	 */
    public void setCommandSignalLevel(int commandSignalLevel) {
        this.commandSignalLevel = commandSignalLevel;
        
        message[5 + offset] = (char) (commandSignalLevel);
    }

	/**
	 * Set commandNoiseLevel.
	 * @param commandNoiseLevel
	 */
    public void setCommandNoiseLevel(int commandNoiseLevel) {
        this.commandNoiseLevel = commandNoiseLevel;
        
        message[6 + offset] = (char) (commandNoiseLevel);
    }

	/**
	 * Set programmerId.
	 * @param programmerId
	 */
    public void setProgrammerId(int	programmerId) {
        this.programmerId = programmerId;
        
        message[7 + offset] = (char) ((programmerId >>> 8) &0xFF);
        message[8 + offset] = (char) (programmerId & 0xFF);
    }

	/**
	 * Set setupFlags.
	 * @param setupFlags
	 */
    public void setSetupFlags(int setupFlags) {
        this.setupFlags = setupFlags;
        
        message[9 + offset] = (char) (setupFlags);
    }
    
	/**
	 * @return Returns the commandType.
	 */
    public int getCommandType() {
        return this.commandType;
    }

	/**
	 * @return Returns the commandAckType.
	 */
    public int getCommandAckType() {
        return this.commandAckType;
    }

	/**
	 * @return Returns the commandRfSeq.
	 */
    public int getCommandRfSeq() {
        return this.commandRfSeq;
    }

	/**
	 * @return Returns the programmerReply.
	 */
    public boolean isProgrammerReply() {
        return this.programmerReply;
    }

	/**
	 * @return Returns the towerReply.
	 */
    public boolean isTowerReply() {
        return this.towerReply;
    }

	/**
	 * @return Returns the ack.
	 */
    public boolean isAck() {
        return this.ack;
    }

	/**
	 * @return Returns the auxiliaryStatus.
	 */
    public int getAuxiliaryStatus() {
        return this.auxiliaryStatus;
    }

	/**
	 * @return Returns the commandAppSequence.
	 */
    public int getCommandAppSequence() {
        return this.commandAppSequence;
    }

	/**
	 * @return Returns the commandSignalLevel.
	 */
    public int getCommandSignalLevel() {
        return this.commandSignalLevel;
    }

	/**
	 * @return Returns the commandNoiseLevel.
	 */
    public int getCommandNoiseLevel() {
        return this.commandNoiseLevel;
    }

	/**
	 * @return Returns the programmerId.
	 */
    public int getProgrammerId() {
        return this.programmerId;
    }

	/**
	 * @return Returns the setupFlags.
	 */
    public int getSetupFlags() {
        return this.setupFlags;
    }
    
	/**
	 * @return Returns the msgClass.
	 */
    public String getMsgClass() {
        return this.msgClass;
    }
}
