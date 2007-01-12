package com.amdswireless.messages.rx;


public class AppMessageTypeF extends DataMessage implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
	private final String msgClass = "RAW";
	private int byteCount;
	private int[] bytesReceived;

	public AppMessageTypeF( char[] msg ) {
		super(msg);
		super.setMessageType(0xF);
		int offset=31;
		this.byteCount=(int)msg[offset+0];
		if ( byteCount > 27 ) {
			byteCount = 27;
		}
		bytesReceived = new int[this.byteCount];
		for (int i=0; i<byteCount; i++) {
			bytesReceived[i]=(int)msg[offset+i+1];
		}
	}

	public int getByteCount() {
		return this.byteCount;
	}

	public int[] getBytesReceived() {
		return this.bytesReceived;
	}
	public String getMsgClass() {
		return this.msgClass;
	}
}
