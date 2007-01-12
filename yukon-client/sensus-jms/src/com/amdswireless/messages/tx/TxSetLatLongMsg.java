package com.amdswireless.messages.tx;

import org.apache.log4j.Logger;

public class TxSetLatLongMsg extends TxMsg {

	private static Logger log = Logger.getLogger("twoway");
	private static final long serialVersionUID = 1L;
	private float latitude;
    private float longitude;

    public TxSetLatLongMsg() {
        super();
        try {
            super.setCommand((short)0x54);
            super.setLength((short)0x24);
            super.setAppCode((short)0x7);
            super.setMiscTx((short)0x1);
            super.setCommandType((short)0x03);
            super.setNcId((short)0x01);
            super.setToi(new java.util.Date().getTime()/1000);

			//TODO - Ask johng about this: why do some cmds set tgbid and some not?
            //super.setTgbId(0x0E);
			
        } catch ( Exception ex ) {
        }
		latitude = 0;
		longitude = 0;
    }

    public void setLatitude( float latitude ) {
        this.latitude = java.lang.Math.abs(latitude);
    }

    public void setLongitude( float longitude ) {
        this.longitude = java.lang.Math.abs(longitude);
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude(){
        return longitude;
    }

    public String getMsgText() {
		log.debug("getMsgText called; lat: " + latitude + ", long: " + longitude);
		int intLat = Float.floatToIntBits(latitude);
		int intLong = Float.floatToIntBits(longitude);
		super.updateMsg( (short) (0x000000ff & intLat)        , 29);
		super.updateMsg( (short)((0x0000ff00 & intLat) >>> 8 ), 30);
		super.updateMsg( (short)((0x00ff0000 & intLat) >>> 16), 31);
		super.updateMsg( (short)((0xff000000 & intLat) >>> 24), 32);
		super.updateMsg( (short) (0x000000ff & intLong)        , 33);
		super.updateMsg( (short)((0x0000ff00 & intLong) >>> 8 ), 34);
		super.updateMsg( (short)((0x00ff0000 & intLong) >>> 16), 35);
		super.updateMsg( (short)((0xff000000 & intLong) >>> 24), 36);
        return super.getMsgText();
    }
}
