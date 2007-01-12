
package com.amdswireless.messages.rx;


public class AppMessageTypeB extends DataMessage implements AppMessage, java.io.Serializable {
/*
 * Byte		Data
 * 0		WD Resets/Alarm Data
 * 	0:0	Leak Detected
 * 	0:1	Broken Pipe Detected
 * 	0:2	(reserved)
 *	0:3	(reserved)
 * 1		Actual Battery Voltage
 * 2		Time Since Event
 * 3-6		Current Reading
 * 7		Device Temperature (degrees C)
*/
	private transient static final long serialVersionUID = 1;
        private transient static final String[] lastMsgTypeArr = {"Normal Mode", "mPass Mode", "Boost Mode", "Normal 1/2 Baud Rate"};
	private final String msgClass = "ALARM";
	private boolean leakDetected;
	private boolean brokenPipeDetected;
	private float	batteryVoltage;
	private int	timeSinceEvent;
	private long	timeOfEvent;
	private int	currentReading;
	private byte	deviceTemperature;
        private float   batteryVoltageUnderLoad;
        private String  lastMessageType;

	public AppMessageTypeB( char[] msg ) {
		super(msg);
		super.setMessageType(0xB);
		int offset=31;
		leakDetected =( (msg[0+offset]&0x01)==1 ) ? true : false;
		brokenPipeDetected= ( (msg[0+offset]&0x02)==1 ) ? true : false;
                if ( (int)(msg[1+offset] & 80 ) == 0x80 ) {
                    this.batteryVoltage= (int)(msg[1+offset] & 0x7F ) /16 + 4;
                } else {
                    this.batteryVoltage= (int)(msg[1+offset] & 0x7F ) /62 + 2;
                }
		this.timeSinceEvent= (int)(msg[3+offset]) * 6;
		this.timeOfEvent = super.getToi()-timeSinceEvent;
		this.currentReading=(int)(msg[4+offset]>>>4)+(int)(msg[5+offset]<<4)+(int)(msg[6+offset]<<12)+(int)(msg[7+offset]);
		this.deviceTemperature=(byte)msg[8+offset];
                if ( (int)(msg[9+offset] & 80 ) == 0x80 ) {
                    this.batteryVoltageUnderLoad = (int)(msg[9+offset] & 0x7F ) /16 + 4;
                } else {
                    this.batteryVoltageUnderLoad = (int)(msg[9+offset] & 0x7F ) /62 + 2;
                }
                int lastMsgTypeIdx = (int)(msg[10+offset] & 0x3 );
                this.lastMessageType = lastMsgTypeArr[lastMsgTypeIdx];
	}

	/**
	 * @return Returns the batteryVoltage.
	 */
	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	/**
	 * @return Returns the brokenPipeDetected.
	 */
	public boolean isBrokenPipeDetected() {
		return brokenPipeDetected;
	}

	/**
	 * @return Returns the currentReading.
	 */
	public int getCurrentReading() {
		return currentReading;
	}

	/**
	 * @return Returns the deviceTemperature.
	 */
	public byte getDeviceTemperature() {
		return deviceTemperature;
	}

	/**
	 * @return Returns the leakDetected.
	 */
	public boolean isLeakDetected() {
		return leakDetected;
	}

	/**
	 * @return Returns the timeOfEvent.
	 */
	public long getTimeOfEvent() {
		return timeOfEvent;
	}

        public float getBatteryVoltageUnderLoad() {
            return this.batteryVoltageUnderLoad;
        }

        public String getLastMessageType() {
            return this.lastMessageType;
        }

	/**
	 * @return Returns the timeSinceEvent.
	 */
	public int getTimeSinceEvent() {
		return timeSinceEvent;
	}
	public String getMsgClass() {
		return this.msgClass;
	}
}
