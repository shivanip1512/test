package com.amdswireless.messages.rx;


public class DataMessage extends AndorianMessage implements java.io.Serializable {

    private transient static final long serialVersionUID = 1L;
    private transient int repReserved;
    private transient String appData;

    private int rptStatus;
    private int control;
    private int	appSeq;
    private int	appCode;
    private int	repeatLevel;
    private int	repId;
    private int rfSeq;
    private int	sigOther1;
    private int	sigOther2;
    private int	sigStrength;
    private int	sigNoise;
    private boolean	historyOverflow;
    private boolean	cutWire;
    private boolean	brownOut;
    private boolean	encrypted;
    private boolean	lowBattery;
    private boolean	meterReadFailure;
    private boolean	powerFailed;
    private boolean	powerRestored;
    private boolean	tamper;
    private int	messageType=0;
    private String messageClass;
    
    public DataMessage() {
    	super();
    	message[2] =0x54;
    }

    public DataMessage( char[] buf) {
        super(buf);
        try {
            repId = buf[24]+(buf[25]<<8)+(buf[22]<<16)+((buf[23]&0xF)<<24);
            control=buf[26];
            rptStatus=buf[28];
            this.powerFailed= (((control & 0x10) == 0x10) ?  true : false);
            this.powerRestored= (((control & 0x20) == 0x20) ? true : false);
            this.lowBattery= (((control & 0x40) == 0x40) ? true : false);
            this.encrypted= (((control & 0x80) == 0x80) ? true : false);
            this.historyOverflow=(((rptStatus & 0x01) == 0x01) ? true : false);
            this.cutWire=(((rptStatus & 0x02) == 0x02) ? true : false);
            this.tamper=(((rptStatus & 0x04) == 0x04) ? true : false);
            this.brownOut=(((rptStatus & 0x08) == 0x08) ? true : false);
            this.meterReadFailure=(((rptStatus & 0x10) == 0x10) ? true : false);
            this.repeatLevel=(rptStatus>>6);
            this.rfSeq=(control & 0xF) + ((rptStatus & 0x20)>>>1);
            repReserved=buf[27];
            appSeq=buf[29];
            appCode=buf[30];
            sigOther1=buf[59];
            sigOther2=buf[60];
            sigStrength=buf[61];
            sigNoise=buf[63];
            String ad = new String();
            for (int i=31; i<60; i++ ) {
                ad+=buf[i];
            }
            appData=ad;
        } catch ( Exception ex ) {
            System.out.println("Found a problem:  "+ex );
            System.out.println("Length of char[] buf is "+buf.length );
        }
    }

    public int getRepId() {
        return this.repId;
    }

    public int getControl() {
        return this.control;
    }

    public boolean isAcPowerFailed() {
        return powerFailed;
    }

    public boolean isPowerFailed() {
        return powerFailed;
    }

    public boolean isPowerRestored() {
        return powerRestored;
    }

    public boolean isLowBattery() {
        return lowBattery;
    }

    public boolean isPayloadEncrypted() {
        return encrypted;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public int getRepReserved() {
        return this.repReserved;
    }

    public int getRptStatus() {
        return this.rptStatus;
    }

    public int getRfSeqNumber() {
        int msb = (rptStatus & 0x20) * 32;
        int lsbs = ( control & 0xF );
        return msb+lsbs;
    }

    public boolean isHistoryOverflow() {
        return historyOverflow;
    }

    public boolean isCutWire() {
        return cutWire;
    }

    public boolean isTamper() {
        return tamper;
    }

    public boolean isBrownOut() {
        return brownOut;
    }

    public boolean isMeterReadFailure() {
        return meterReadFailure;
    }

    // additional accessors to support retrieving "base" alarms in the type 10 and type 4 messages.
    // Not all of these have name collisions with the message sub-classes (yet)
    
    public boolean isRfPowerFailed() {
        return powerFailed;
    }

    public boolean isRfPowerRestored() {
        return powerRestored;
    }

    public boolean isRfLowBattery() {
        return lowBattery;
    }

    public boolean isRfPayloadEncrypted() {
        return encrypted;
    }

    public boolean isRfEncrypted() {
        return encrypted;
    }

    public boolean isRfHistoryOverflow() {
        return historyOverflow;
    }

    public boolean isRfCutWire() {
        return cutWire;
    }

    public boolean isRfTamper() {
        return tamper;
    }

    public boolean isRfBrownOut() {
        return brownOut;
    }
    
    public boolean isRfMeterReadFailure() {
        return meterReadFailure;
    }

    public int repeatLevel() {
        return ( rptStatus >> 6 );
    }

    public int getAppSeq() {
        return this.appSeq;
    }

    /**
     * The appCode byte tells us what type of application data is contained
     * in this message. 
     * @return Integer The application Code of the appData in this message
     */

    public int getAppCode() {
        return this.appCode;
    }

    public int getSigOther1() {
        return this.sigOther1;
    }

    public int getSigOther2() {
        return this.sigOther2;
    }

    public int getSigStrength() {
        return this.sigStrength;
    }

    public int getSigNoise() {
        return this.sigNoise;
    }
    public int getRepeatLevel() {
        return this.repeatLevel;
    }
    public int getRfSeq() {
        return this.rfSeq;
    }
    public int getMessageType() {
        return this.messageType;
    }
    public void setMessageType(int t) {
        this.messageType=t;
    }

    public String getMsgClass() {
        return "UNKNOWN";
    }

    public void setAppData(String s) {
        this.appData=s;
    }

    public String getAppData() {
        return this.appData;
    }

    public void screenDump() {
        //super.screenDump();	
        System.out.println("Rep Id-----------------" + this.getRepId());
        System.out.println("Control----------------" + this.getControl());
        System.out.println("--[AC Power Fail]------" + this.isPowerFailed());
        System.out.println("--[Power Restore]------" + this.isPowerRestored());
        System.out.println("--[Low Battery]--------" + this.isLowBattery());
        System.out.println("--[Encrypted Payload]--" + this.isPayloadEncrypted());
        System.out.println("Rep Reserved-----------" + this.getRepReserved());
        System.out.println("Report Status----------" + this.getRptStatus());
        System.out.println("--[RF Sequence Number]-" + this.getRfSeqNumber());
        System.out.println("--[Histroy Overflow]---" + this.isHistoryOverflow());
        System.out.println("--[Cut Wire]-----------" + this.isCutWire());
        System.out.println("--[Tamper]-------------" + this.isTamper());
        System.out.println("--[Brownout]-----------" + this.isBrownOut());
        System.out.println("--[Read Failure]-------" + this.isMeterReadFailure());
        System.out.println("--[Repeat Level]-------" + this.repeatLevel());
        System.out.println("Application Sequence---" + this.getAppSeq());
        System.out.println("Application Code-------" + this.getAppCode());
        System.out.println("Signal Other 1---------" + this.getSigOther1());
        System.out.println("Signal Other 2---------" + this.getSigOther2());
        System.out.println("Signal Strength--------" + this.getSigStrength());
        System.out.println("Signal to Noise--------" + this.getSigNoise());
        System.out.println("Application Data-------" + this.getAppData());
    }

	public void setAppCode(int appCode) {
		this.appCode = appCode;
		message[30] = (char)this.appCode;
	}

	public void setAppSeq(int appSeq) {
		this.appSeq = appSeq;
		message[29] = (char)this.appSeq;
	}

	public void setBrownOut(boolean brownOut) {
		this.brownOut = brownOut;
		if ( this.brownOut ) {
			message[28] = (char)(message[28] & 0x08);
		}
	}

	public void setCutWire(boolean cutWire) {
		this.cutWire = cutWire;
		if ( this.cutWire ) {
			message[28] = (char)(message[28] * 0x02);
		}
	}

	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
		if ( this.encrypted ) {
			message[26] = (char)(message[26] & 0x80);
		}
	}

	public void setHistoryOverflow(boolean historyOverflow) {
		this.historyOverflow = historyOverflow;
		if ( this.historyOverflow ) {
			message[28] = (char)(message[28] & 0x01 );
		}
	}

	public void setLowBattery(boolean lowBattery) {
		this.lowBattery = lowBattery;
		if ( this.lowBattery ) {
			message[26] = (char)(message[26] & 0x40);
		}
	}

	public void setMeterReadFailure(boolean meterReadFailure) {
		this.meterReadFailure = meterReadFailure;
		if ( this.meterReadFailure ) {
			message[28] = (char)(message[28] & 0x10);
		}
	}

	public void setPowerFailed(boolean powerFailed) {
		this.powerFailed = powerFailed;
		if ( this.powerFailed ) {
			message[26] = (char)(message[26] & 0x10);
		}
	}

	public void setPowerRestored(boolean powerRestored) {
		this.powerRestored = powerRestored;
		if ( this.powerRestored ) {
			message[26] = (char)(message[26] & 0x20);
		}
	}

	public void setRepeatLevel(int repeatLevel) {
		this.repeatLevel = repeatLevel;
		if ( this.repeatLevel > 0 ) {
			message[28] = (char)(message[28] & (this.repeatLevel << 7));
		}
	}

	public void setRepId(int repId) {
		this.repId = repId;
		message[24] = (char)(repId & 0xFF );
        message[25] = (char)((repId & 0xFF00) >>8 );
        message[22] = (char)((repId & 0xFF0000) >> 16);
        message[23] = (char)(message[23] & ( repId & 0xF000000) >> 24);
	}

	public void setRepReserved(int repReserved) {
		this.repReserved = repReserved;
	}

	public void setRfSeq(int rfSeq) {
		this.rfSeq = rfSeq;
		message[26] = (char)(message[26] & rfSeq );
		message[28] = (char)(message[28] & ( (rfSeq & 0x10 ) << 5 ));
	}

	public void setSigNoise(int sigNoise) {
		this.sigNoise = sigNoise;
		message[63] = (char)sigNoise;
	}

	public void setSigOther1(int sigOther1) {
		this.sigOther1 = sigOther1;
		message[59] = (char)sigOther1;
	}

	public void setSigOther2(int sigOther2) {
		this.sigOther2 = sigOther2;
		message[60] = (char)sigOther2;
	}

	public void setSigStrength(int sigStrength) {
		this.sigStrength = sigStrength;
		message[61] = (char)sigStrength;
	}

	public void setTamper(boolean tamper) {
		this.tamper = tamper;
		if ( this.tamper ) {
			message[28] = (char)(message[28] * 0x04);
		}
	}

	/**
	 * @return Returns the messageClass.
	 */
	public String getMessageClass() {
		return messageClass;
	}

	/**
	 * @param messageClass The messageClass to set.
	 */
	public void setMessageClass(String messageClass) {
		this.messageClass = messageClass;
	}


    /*	public static void main(String[] args) {
        String initMsg = new String("FA AF 54 00 23 00 00 00 59 fa 07 41 00 01 00 24 01 88 88 88 08 00 01 07 00 03 40 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26");
        DataMessage dm = new DataMessage(DataMessage.cleanHex(initMsg));

        System.out.println("Raw Data Message Dump--" + dm + "--");
        dm.screenDump();

    }
    */}
