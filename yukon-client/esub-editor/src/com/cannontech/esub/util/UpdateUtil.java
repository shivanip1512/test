package com.cannontech.esub.util;

import java.text.DecimalFormat;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.cache.functions.UnitMeasureFuncs;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.esub.editor.element.DynamicText;
import com.cannontech.message.dispatch.message.PointData;

/**
 * 
 * @author alauinger
 */
public class UpdateUtil {

	public static String getDynamicTextString(int pointID, int displayAttrib) {
		PointChangeCache pcc = PointChangeCache.getPointChangeCache();
	
		String text = "";
		boolean prev = false;	
		if( (displayAttrib & DynamicText.VALUE) != 0 ) {			
			PointData pData = pcc.getValue(pointID);
	
			if (pData != null) {
				DecimalFormat f = new DecimalFormat();
				f.setMaximumFractionDigits(2);
				text = f.format(pData.getValue());
				prev = true;
			}
		}
							
		if( (displayAttrib & DynamicText.UOFM) != 0 ) {
			if( prev ) 
				text += " ";
				
			LiteUnitMeasure lum = UnitMeasureFuncs.getLiteUnitMeasureByPointID(pointID);
			text += lum.getUnitMeasureName();
			prev = true;
		}
							
		if( (displayAttrib & DynamicText.NAME) != 0 ) {
			if( prev ) 
				text += " ";
		
			text += PointFuncs.getPointName(pointID);
			prev = true;
		}
							
		if( (displayAttrib & DynamicText.PAO) != 0 ) {
			if( prev ) 
				text += " ";
			//find the pao for this point
			LitePoint lp = PointFuncs.getLitePoint(pointID);
			text += PAOFuncs.getYukonPAOName(lp.getPaobjectID());
			prev = true;
		}
									
		if( (displayAttrib & DynamicText.LAST_UPDATE) != 0 ) {
			PointData pData = pcc.getValue(pointID);
			if( prev ) 
				text += " ";

			text += pData.getPointDataTimeStamp();
 			prev = true;
		}	
		
		if( !prev )
			text = "-";
			
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
