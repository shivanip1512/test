package com.amdswireless.messages.rx;


public class AppMessageType8 extends DataMessage implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private transient static int offset=31;
	private final String msgClass="BUDDY";
	private int buddyId;
	private int buddyAppCode;
	private int buddyRfSequence;
	private int buddyAppSequence;
	private int receivedSignalLevel;
	private int receivedNoiseLevel;
    private int queueTime;

    public AppMessageType8() {
    	super();
    	super.setMessageClass(msgClass);
    	super.setMessageType(8);
    }
	public AppMessageType8( char[] msg ) {
		super(msg);
		super.setMessageType(8);
        this.buddyId=(int)(msg[3+offset]<<24)+(int)(msg[2+offset]<<16)+(int)(msg[1+offset]<<8)+(int)msg[0+offset];
		this.buddyAppCode=msg[4+offset];
		this.buddyRfSequence=msg[5+offset];
		this.buddyAppSequence=msg[6+offset];
		this.receivedSignalLevel=msg[7+offset];
		this.receivedNoiseLevel=msg[8+offset];
        this.queueTime=(int)msg[9+offset];
	}

	public int getBuddyId() {
		return this.buddyId;
	}
	public int getBuddyAppCode() {
		return this.buddyAppCode;
	}
	public int getBuddyRfSequence() {
		return this.buddyRfSequence;
	}
	public int getBuddyAppSequence() {
		return this.buddyAppSequence;
	}
	public int getReceivedSignalLevel() {
		return this.receivedSignalLevel;
	}
	public int getReceivedNoiseLevel() {
		return this.receivedNoiseLevel;
	}
	
	/**
	 * @return Returns the time in seconds that the buddy device stored the message before forwarding it.
	 */
	public int getQueueTime() {
		return this.queueTime;
	}
	public String getMsgClass() {
		return this.msgClass;
	}
	/**
	 * @param buddyAppCode The buddyAppCode to set.
	 */
	public void setBuddyAppCode(int buddyAppCode) {
		this.buddyAppCode = buddyAppCode;
		message[offset+4] = (char)(buddyAppCode & 0xFF);
	}
	/**
	 * @param buddyAppSequence The buddyAppSequence to set.
	 */
	public void setBuddyAppSequence(int buddyAppSequence) {
		this.buddyAppSequence = buddyAppSequence;
		message[offset+6] = (char)(buddyAppSequence & 0xFF);
	}
	/**
	 * @param buddyId The buddyId to set.
	 */
	public void setBuddyId(int buddyId) {
		this.buddyId = buddyId;
		message[offset+2] = (char)(buddyId & 0xFF );
        message[offset+3] = (char)((buddyId & 0xFF00) >>8 );
        message[offset+0] = (char)((buddyId & 0xFF0000) >> 16);
        message[offset+1] = (char)(message[23] & ( buddyId & 0xF000000) >> 24);

	}
	/**
	 * @param buddyRfSequence The buddyRfSequence to set.
	 */
	public void setBuddyRfSequence(int buddyRfSequence) {
		this.buddyRfSequence = buddyRfSequence;
		message[offset+5] =(char)(buddyRfSequence & 0xFF);
	}
	/**
	 * @param queueTime The queueTime to set.
	 */
	public void setQueueTime(int queueTime) {
		this.queueTime = queueTime;
		message[9+offset] = (char)(queueTime & 0xFF);
	}
	/**
	 * @param receivedNoiseLevel The receivedNoiseLevel to set.
	 */
	public void setReceivedNoiseLevel(int receivedNoiseLevel) {
		this.receivedNoiseLevel = receivedNoiseLevel;
		message[offset+8] = (char)(receivedNoiseLevel & 0xFF);
	}
	/**
	 * @param receivedSignalLevel The receivedSignalLevel to set.
	 */
	public void setReceivedSignalLevel(int receivedSignalLevel) {
		this.receivedSignalLevel = receivedSignalLevel;
		message[offset+7] = (char)(receivedSignalLevel & 0xFF);
	}
}
