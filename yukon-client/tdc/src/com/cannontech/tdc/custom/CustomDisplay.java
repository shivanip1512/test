package com.cannontech.tdc.custom;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.UnitMeasureFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;


/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class CustomDisplay
{
	public static final String DYNAMIC_ROW = "**DYNAMIC**";
	
	
	// Column type names -STATIC DATA
	public static final String COLUMN_TYPE_POINTID = "PointID";
	public static final String COLUMN_TYPE_POINTNAME = "PointName";
	public static final String COLUMN_TYPE_POINTTYPE = "PointType";
	public static final String COLUMN_TYPE_POINTSTATE = "PointState"; //Point ablement
	public static final String COLUMN_TYPE_DEVICENAME = "DeviceName";	
	public static final String COLUMN_TYPE_DEVICETYPE = "DeviceType";	
	public static final String COLUMN_TYPE_TXT_MSG = "DeviceCurrentState"; //PAO Description
	public static final String COLUMN_TYPE_DEVICEID = "DeviceID";
	
	// Column type names -DYNAMIC DATA
	public static final String COLUMN_TYPE_POINTVALUE = "PointValue";
	public static final String COLUMN_TYPE_POINTQUALITY = "PointQuality";
	public static final String COLUMN_TYPE_POINTTIMESTAMP = "PointTimeStamp";
	public static final String COLUMN_TYPE_UOFM = "UofM";
	public static final String COLUMN_TYPE_STATE = "State";
	public static final String COLUMN_TYPE_POINTIMAGE = "PointImage";
	public static final String COLUMN_TYPE_QUALITYCNT = "QualityCount";


	private CustomDisplay()
	{
		super();
	}
		

	public static String[] getValue( String[] types, int pointid )
	{	
		String[] retValues = new String[ types.length ];
		//find the point and pao
		LitePoint litePoint = PointFuncs.getLitePoint(pointid);
		
		if( litePoint == null )
		{
			CTILogger.error(" Unable to find litepoint with a pointid of " + pointid );
			return retValues;
		}
		
		LiteYukonPAObject litePAO = PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() );
		 
		for( int i = 0; i < types.length; i++ )
		{
			if( types[i].equalsIgnoreCase(COLUMN_TYPE_DEVICENAME) )
			{
				retValues[i] = litePAO.getPaoName();
			}
			else if( types[i].equalsIgnoreCase(COLUMN_TYPE_POINTNAME) )
			{
				retValues[i] = litePoint.getPointName();
			}
			else if( types[i].equalsIgnoreCase(COLUMN_TYPE_POINTTYPE) )
			{
				retValues[i] = PointTypes.getType( litePoint.getPointType() );
			}
			else if( types[i].equalsIgnoreCase(COLUMN_TYPE_DEVICETYPE) )
			{
				retValues[i] = PAOGroups.getPAOTypeString( litePAO.getType() );
			}
			else if( types[i].equalsIgnoreCase(COLUMN_TYPE_POINTID) )
			{
				retValues[i] = String.valueOf(litePoint.getPointID());
			}
			else if( types[i].equalsIgnoreCase(COLUMN_TYPE_DEVICEID) )
			{
				retValues[i] = String.valueOf(litePAO.getLiteID());
			}
			else if( types[i].equalsIgnoreCase(COLUMN_TYPE_UOFM) )
			{
				LiteUnitMeasure lum = UnitMeasureFuncs.getLiteUnitMeasureByPointID(
											litePoint.getPointID());

				if( lum != null )	
					retValues[i] = lum.getUnitMeasureName();
				else
					retValues[i] = CtiUtilities.STRING_NONE;
			}
			else if( types[i].equalsIgnoreCase(COLUMN_TYPE_TXT_MSG) )
			{
				retValues[i] = litePAO.getPaoDescription();
			}
			else
				retValues[i] = DYNAMIC_ROW;
		}
		
		return retValues;
	}
	
}
