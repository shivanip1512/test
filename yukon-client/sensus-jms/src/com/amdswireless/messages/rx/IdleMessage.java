package com.amdswireless.messages.rx;


public class IdleMessage extends AndorianMessage implements java.io.Serializable {
	
	/**
	 * 
	 */
	private transient static final long serialVersionUID = 1L;

	private int sigNoise;

	public IdleMessage( char[] buf) {
		super(buf);
		try {
			sigNoise=buf[22];
		} catch ( Exception ex ) {
			System.out.println("Found a problem:  "+ex );
			System.out.println("Length of char[] buf is "+buf.length );
		}
	}

	public int getSigNoise() {
		return sigNoise;
	}

	public void screenDump() {
		//super.screenDump();
		System.out.println("Signal to Noise--------" + this.getSigNoise());
	}

/*	public static void main(String[] args) {
	String initMsg = new String("FA AF 54 00 23 00 00 00 59 fa 07 41 00 01 00 24 01 88 88 88 08 00 01 07 00 03 40 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26");
		IdleMessage im = new IdleMessage(IdleMessage.cleanHex(initMsg));

		System.out.println("Raw Idle Message Dump--" + im + "--");
		im.screenDump();

	}
*/}
