package com.cannontech.esub;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.point.PointAnalog;
import com.cannontech.database.db.point.PointLimit;

/**
 * 
 * @author alauinger
 */
public class PointAttributes {

	// Available display attributes
	public static final int VALUE = 0x00000001;
	public static final int UOFM = 0x00000002;
	public static final int NAME = 0x00000004;
	public static final int LAST_UPDATE = 0x00000008;
	public static final int PAO = 0x00000010;
	public static final int LOW_LIMIT = 0x00000020;
	public static final int HIGH_LIMIT = 0x00000040;
	public static final int LIMIT_DURATION = 0x00000080;
	public static final int ALARM_TEXT = 0x00000100;
	public static final int STATE_TEXT = 0x000000200;
	public static final int MULTIPLIER = 0x000000400;
	public static final int DATA_OFFSET = 0x000000800;
		
	/**
	 * Takes a fully filled in dbpersistent and attempts to set the given
	 * attribute.  If unable will return null.
	 * 
	 * Currently only LOW_LIMIT, HIGH_LIMIT,LIMIT_DURATION, MULTIPLIER,
	 * AND DATA_OFFSET are supported.
	 * 
	 * @param dbObj
	 * @param attrib
	 * @param newValue
	 * @return DBPersistent
	 */
	public static DBPersistent setAttribute(DBPersistent dbObj, int attrib, Object newValue) {
		try {
		switch( attrib ) {
			case LOW_LIMIT:
			{
				PointLimit lp = getPointLimit(dbObj);
				if( lp != null ) {
					lp.setLowLimit(new Double(newValue.toString()));
				}
			}
			break;
			
			case HIGH_LIMIT:
			{
				PointLimit lp = getPointLimit(dbObj);
				if( lp != null ) {
					lp.setHighLimit(new Double(newValue.toString()));
				}
			}
			break;
			
			case LIMIT_DURATION: //always treat this as minutes -> seconds conversion
			{
				PointLimit lp = getPointLimit(dbObj);
				if( lp != null ) {
					int secs = Integer.parseInt(newValue.toString()) * 60;					
					lp.setLimitDuration(new Integer(secs));
				}
			}
			break;
			
			case MULTIPLIER:
			{
				PointAnalog pa = getPointAnalog(dbObj);
				if( pa != null ) {
					pa.setMultiplier(new Double(newValue.toString()));
				}
			}
			break;
			
			case DATA_OFFSET:	
			{
				PointAnalog pa = getPointAnalog(dbObj);
				if( pa != null ) {
					pa.setDataOffset(new Double(newValue.toString()));
				}
			}
			break;
			
			default:
				CTILogger.warn("Unsupported attribute specified for update");
				return null;		
		}
		}
		catch(Exception e) {
			CTILogger.warn("Unable to update dbpersistent attribute", e);
			return null;
		}
					
		return dbObj;
	}
	
	private static PointLimit getPointLimit(DBPersistent dbObj) {
		if(dbObj instanceof ScalarPoint) {
			ScalarPoint sp = (ScalarPoint) dbObj;
			Vector limits = sp.getPointLimitsVector();
			if(limits != null && limits.size() > 0) {
				return (PointLimit) limits.get(0);				
			}
		}
		
		return null;
	}
	
	private static PointAnalog getPointAnalog(DBPersistent dbObj) {
		if(dbObj instanceof AnalogPoint) {
			AnalogPoint ap = (AnalogPoint) dbObj;
			return ap.getPointAnalog();
		}
		
		return null;
	}
}
