package com.cannontech.esub.util;

import java.text.DecimalFormat;
import java.util.Map;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.cache.functions.UnitMeasureFuncs;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.PointAttributes;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

/**
 * 
 * @author alauinger
 */
public class UpdateUtil {

	public static String getDynamicTextString(int pointID, int displayAttrib) {
		PointChangeCache pcc = PointChangeCache.getPointChangeCache();
	
		String text = "";
		boolean prev = false;	
		if( (displayAttrib & PointAttributes.VALUE) != 0 ) {			
			PointData pData = pcc.getValue(pointID);
			LitePointUnit lpu = PointFuncs.getPointUnit(pointID);
			
			if (pData != null && lpu != null ) {				
				DecimalFormat f = new DecimalFormat();
				f.setMaximumFractionDigits(lpu.getDecimalPlaces());
				f.setMinimumFractionDigits(lpu.getDecimalPlaces());
				text = f.format(pData.getValue());				
				prev = true;
			}
		}
							
		if( (displayAttrib & PointAttributes.UOFM) != 0 ) {
			if( prev ) 
				text += " ";
				
			LiteUnitMeasure lum = UnitMeasureFuncs.getLiteUnitMeasureByPointID(pointID);
			if( lum != null ) {
				text += lum.getUnitMeasureName();
				prev = true;
			}
		}
							
		if( (displayAttrib & PointAttributes.NAME) != 0 ) {
			if( prev ) 
				text += " ";
		
			text += PointFuncs.getPointName(pointID);
			prev = true;
		}
							
		if( (displayAttrib & PointAttributes.PAO) != 0 ) {
			if( prev ) 
				text += " ";
			//find the pao for this point
			LitePoint lp = PointFuncs.getLitePoint(pointID);
			text += PAOFuncs.getYukonPAOName(lp.getPaobjectID());
			prev = true;
		}
									
		if( (displayAttrib & PointAttributes.LAST_UPDATE) != 0 ) {
			PointData pData = pcc.getValue(pointID);
			if(pData != null ) {
				if( prev ) 
					text += " ";

				text += pData.getPointDataTimeStamp();
 				prev = true;
			}
		}	
		
		if( (displayAttrib & PointAttributes.LOW_LIMIT) != 0 ) {
			LitePointLimit lpl = PointFuncs.getPointLimit(pointID);
			if( lpl != null ) {
				text += Integer.toString(lpl.getLowLimit());
				prev = true;
			}
		}
		
		if( (displayAttrib & PointAttributes.HIGH_LIMIT) != 0 ) {
			LitePointLimit lpl = PointFuncs.getPointLimit(pointID);
			if( lpl != null ) {
				text += Integer.toString(lpl.getHighLimit());
				prev = true;
			}
		}
		
		if( (displayAttrib & PointAttributes.LIMIT_DURATION) != 0 ) {	
			// always convert seconds -> minutes!	
			LitePointLimit lpl = PointFuncs.getPointLimit(pointID);
			if( lpl != null ) {
				int min = lpl.getLimitDuration() / 60;
				text += Integer.toString(min);		
				prev = true;	
			}
		}
		
		if( (displayAttrib & PointAttributes.MULTIPLIER) != 0 ) {
			Map multMap = DefaultDatabaseCache.getInstance().getAllPointidMultiplierHashMap();
			Double mult = (Double) multMap.get( new Integer(pointID) );
			
			if( mult != null ) {
				text += mult.toString();
			}
			else {
				text += "-";
			}
			
			prev = true;
		}
		
		if( (displayAttrib & PointAttributes.DATA_OFFSET) != 0 ) {
			Map offsetMap = DefaultDatabaseCache.getInstance().getAllPointIDOffsetMap();
			Integer offset = (Integer) offsetMap.get(new Integer(pointID));
			if(offset != null) {
				text += offset.toString();
			}
			else {
				text += "-";
			}
			prev = true;			
		}
		
		if( (displayAttrib & PointAttributes.ALARM_TEXT) != 0 ) {
			LitePoint lp = PointFuncs.getLitePoint(pointID);
			Signal sig = pcc.getSignal(pointID);	
			
			if( sig != null ) {		
				text += sig.getDescription();			
			}
			else {
				text += "NOT IN ALARM";
			}
				prev = true;
		}
		
		if( (displayAttrib & PointAttributes.STATE_TEXT) != 0 ) {
			LitePoint lp = PointFuncs.getLitePoint(pointID);			
			PointData pData = pcc.getValue(pointID);
	
			if (pData != null) {
				LiteState ls = StateFuncs.getLiteState((int) lp.getStateGroupID(), (int) pData.getValue());	
				if( ls != null ) {			
					text += ls.getStateText();
					prev = true;
				}
			}
		}	
					
		if( !prev )
			text = "-";
		
		if(text.length() == 1) { //workaround for bugin adobe svg geturl function!
					text = " " + text;
		}	
		
		return text;		
}

	public static String getStateImageName(int pointID) {
		LitePoint lp = PointFuncs.getLitePoint(pointID);
		PointChangeCache pcc = PointChangeCache.getPointChangeCache();
		PointData pData = pcc.getValue(pointID);
		
		LiteState ls = StateFuncs.getLiteState(lp.getStateGroupID(), (int) pData.getValue());
		LiteYukonImage img = YukonImageFuncs.getLiteYukonImage(ls.getImageID());
		return img.getImageName();
	}	
}
