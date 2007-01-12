package com.amdswireless.messages.tx;



// TODO log exeptions
// buddy id handled by super

class DisplayLineInvalid	extends Exception {
	private static final long serialVersionUID = 1L;}
class DisplayTextOverflow	extends Exception {
	private static final long serialVersionUID = 1L;}
class DisplayTextInvalid	extends Exception {
	private static final long serialVersionUID = 1L;}
class DirectionInvalid		extends Exception {
	private static final long serialVersionUID = 1L;}

public class TxDisplayMsg extends TxMsg {
	private static final long serialVersionUID = 1L;
	private short		displayLine		= 0;
	private short		displayableLength	= 16;
	private short		appdataBase		= 30;
	private String		displayText		= "";

	public TxDisplayMsg() {
		super();
		try {
			super.setCommand((short)0x54);
			super.setLength((short)0x24);
			super.setAppCode((short)0x7);
			super.setMiscTx((short)0x1);
			super.setCommandType((short)0x0E);
			super.setNcId((short)0x01);
			//super.updateMsg((short)0x0, 30);		// default to outgoing set-display msg
			super.setToi(new java.util.Date().getTime()/1000);
		} catch ( Exception ex ) {
			System.err.println("Error initializing TxDisplayMsg:  "+ex );
		}
		displayLine=0x1;
		displayText = "";
	}

	public void setDisplayLine(short line) throws DisplayLineInvalid {
		System.out.println("Setting display line to "+line );
		switch(line) {
			case 0x1: // Top Line
				displayLine=0x1; 
				try { super.updateMsg((short)0x00, 29); } catch ( Exception ex ) { }
				break;
			case 0x2: // Bottom Line
				displayLine=0x2; 
				try { super.updateMsg((short)0x01, 29); } catch ( Exception ex ) { }
				break;
			default: throw new DisplayLineInvalid();
		}
	}

	public void setDisplayText(String text) throws DisplayTextInvalid, DisplayTextOverflow {
		displayText = text;
		int len = displayText.length();
		if(len <= displayableLength) { 
			for(short idx = 0; idx <= len; idx++) {
				char c = displayText.charAt(idx);
				if(!isPrintableAscii(c)) { throw new DisplayTextInvalid(); }
				try {						 // set appdata
					super.updateMsg((short)c, appdataBase + idx);		
				} catch ( Exception ex ) {
				}
			}
		}
		else { throw new DisplayTextOverflow(); }
	}

	public String getMsgText() {
		super.updateMsg((short)(displayLine-1), 29 );
		for(short idx = 0; idx < displayText.length(); idx++)	{ 
			char c = displayText.charAt(idx);
			try {	super.updateMsg((short)c, appdataBase + idx);	} catch ( Exception ex ) { }
		}
		return super.getMsgText();
	}


	public short getLine()			{ return displayLine; }
	public String getText()			{ return displayText; }

	private boolean isPrintableAscii(char c) { 
		if(32 < c || 125 > c) { return true; } // upper/lower case, and some punctuation
		return false;
	}

}
