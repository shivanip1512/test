package com.amdswireless.messages.rx;


public class AppMessageTypeETier0 extends AppMessageTypeE implements AppMessage, java.io.Serializable {
	private transient static final long serialVersionUID = 1L;
/*
	private transient final String[] quant = {"Active Power - W",
        "Reactive Power - VAR",
        "Apparent Power - VA",
        "Phasor Power - VA",
        "Quantity Power -Q(60)",
        "Quantity Power -Q(45)",
        "Reserved",
        "Unknown" };
    private transient final String[] segmentation = {"System",
        "A-B",
        "B-C",
        "C-A",
        "neutral",
        "A-neutral",
        "B-neutral",
        "C-neutral" };
    private transient final String[] accountability = {"Quadrant 1",
        "Quadrant 2",
        "Quadrant 3",
        "Quadrant 4",
        "Net flow: delivered + received",
        "Net flow: delivered - received" };
*/
    private transient final String[] metertype = {
        "(reserved)",
        "Elster A3R",
        "Elster A3K",
        "Elster A3D",
        "Elster A3T",
        "Elster A3Q",
        "(reserved)" };
    //private final String msgClass = "TIER";

    private int subtier;
    private int meterType;
    private boolean continuouslyCumulative;
    private boolean cumulative;
    private boolean summationAvailable;
    private boolean demandAvailable;
    private boolean coincidentAvailable;
    private int peakDemandMonth;
    private int peakDemandDay;
    private int peakDemandHour;
    private int peakDemandMinute;
    private float summationReading;
    private float demandReading;
    private float cumulativeDemandReading;
    private float coincidentReading;
    private int demandResetCount;
    private int summationUnitOfMeasure;
    private int demandUnitOfMeasure;
    private int coincidentUnitOfMeasure;
    private int tierCount;
    private int subtierCount;
    private String meterTypeString;
    private String cumulativeType;

    public AppMessageTypeETier0( char[] msg ) {
        super(msg);
        super.setMessageType(0xE);
        int offset=31;
        this.subtier = ((int)(msg[0+offset] & 0x78 )>>>3);
        this.meterType = (int)msg[1+offset];
        this.meterTypeString = metertype[this.meterType];
        this.continuouslyCumulative = ( (int)(msg[2+offset] & 0x01 ) == 0x01 ) ? true : false ;
        this.cumulative = ( (int)(msg[2+offset] & 0x01 ) == 0x00 ) ? true : false ;
        this.summationAvailable = ( (int)(msg[2+offset] & 0x02 ) == 0x02 ) ? true : false;
        this.demandAvailable = ( (int)(msg[2+offset] & 0x04 ) == 0x04 ) ? true : false;
        this.coincidentAvailable = ( (int)(msg[2+offset] & 0x08 ) == 0x08 ) ? true : false;
        this.peakDemandMonth =  (int)((msg[2+offset] & 0xF0 ) >>> 4 );
        this.peakDemandDay =  (int)((msg[3+offset] & 0x1F ) );
        this.peakDemandHour =  (int)((msg[3+offset] & 0xE0 ) >>> 5 ) + ( (int)((msg[4+offset] & 0x03 )) << 3 );
        this.peakDemandMinute =  (int)((msg[4+offset] & 0xfc ) >>> 2 );
        // this is correct (/1000) according to Derl
	// Bug 267 - summation should not be /1000 for subtier 2
	if( this.subtier != 2 ) {
        this.summationReading=Float.intBitsToFloat((int)(msg[5+offset])+(int)(msg[6+offset]<<8)+(int)(msg[7+offset]<<16)+(int)(msg[8+offset]<<24))/1000.0f; 
	} else {
        this.summationReading=Float.intBitsToFloat((int)(msg[5+offset])+(int)(msg[6+offset]<<8)+(int)(msg[7+offset]<<16)+(int)(msg[8+offset]<<24));
	}
        //this.demandReading=Float.intBitsToFloat((int)(msg[9+offset])+(int)(msg[10+offset]<<8)+(int)(msg[11+offset]<<16)+(int)(msg[12+offset]<<24));
        this.demandReading=Float.intBitsToFloat((int)(msg[9+offset])+(int)(msg[10+offset]<<8)+(int)(msg[11+offset]<<16)+(int)(msg[12+offset]<<24))/1000.0f;
        //this.cumulativeDemandReading=Float.intBitsToFloat((int)(msg[13+offset])+(int)(msg[14+offset]<<8)+(int)(msg[15+offset]<<16)+(int)(msg[16+offset]<<24))/1000.0f;
        this.cumulativeDemandReading=Float.intBitsToFloat((int)(msg[13+offset])+(int)(msg[14+offset]<<8)+(int)(msg[15+offset]<<16)+(int)(msg[16+offset]<<24));
        //this.coincidentReading=Float.intBitsToFloat((int)(msg[17+offset])+(int)(msg[18+offset]<<8)+(int)(msg[19+offset]<<16)+(int)(msg[20+offset]<<24))/1000.0f;
        this.coincidentReading=Float.intBitsToFloat((int)(msg[17+offset])+(int)(msg[18+offset]<<8)+(int)(msg[19+offset]<<16)+(int)(msg[20+offset]<<24));
        this.demandResetCount = (int)msg[21+offset];
        this.summationUnitOfMeasure = (int)msg[22+offset];
        this.demandUnitOfMeasure = (int)msg[23+offset];
        this.coincidentUnitOfMeasure = (int)msg[24+offset];
        this.tierCount=(int)(msg[25+offset]&0x7); //bits 0-2
        this.subtierCount=(int)((msg[25+offset]&0x38)>>>3); //bits 3-6
    	this.meterTypeString= metertype[this.meterType];
        this.cumulativeType="cumulative";
        if ( continuouslyCumulative ) {
            cumulativeType = "continuously cumulative";
        }
    }

    public int getSubtier() {
        return this.subtier;
    }

    public boolean isContinuouslyCumulative() {
        return this.continuouslyCumulative;
    }
    public boolean isSummationAvailable() {
        return this.summationAvailable;
    }
    public boolean isDemandAvailable() {
        return this.demandAvailable;
    }
    public boolean isCoincidentAvailable() {
        return this.coincidentAvailable;
    }
    public int getPeakDemandMonth() {
        return this.peakDemandMonth;
    }
    public int getPeakDemandDay() {
        return this.peakDemandDay;
    }
    public int getPeakDemandHour() {
        return this.peakDemandHour;
    }
    public int getPeakDemandMinute() {
        return this.peakDemandMinute;
    }
    public float getSummationReading() {
        return this.summationReading;
    }
    public float getDemandReading() {
        return this.demandReading;
    }
    public float getCumulativeDemandReading() {
        return this.cumulativeDemandReading;
    }
    public float getCoincidentReading() {
        return this.coincidentReading;
    }
    public float getDemandResetCount() {
        return this.demandResetCount;
    }
    public int getTierCount() {
        return this.tierCount;
    }
    public int getSubtierCount() {
        return this.subtierCount;
    }
    /**
     * @return Returns the brand/model of meter this message originated fom/
     */
    public String getMeterTypeString() {
    	return meterTypeString;
    }

	public boolean isCumulative() {
		return cumulative;
	}

	public void setCumulative(boolean cumulative) {
		this.cumulative = cumulative;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getCoincidentUnitOfMeasure() {
		return coincidentUnitOfMeasure;
	}

	public String getCumulativeType() {
		return cumulativeType;
	}

	public int getDemandUnitOfMeasure() {
		return demandUnitOfMeasure;
	}

	public String[] getMetertype() {
		return metertype;
	}

	public int getMeterType() {
		return meterType;
	}

	public int getSummationUnitOfMeasure() {
		return summationUnitOfMeasure;
	}
}
