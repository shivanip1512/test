/*
 * Created on Feb 2, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import com.cannontech.analysis.data.SystemLog;
import com.cannontech.analysis.data.activity.ActivityLog;
import com.cannontech.analysis.data.device.Carrier;
import com.cannontech.analysis.data.device.MissedMeter;
import com.cannontech.analysis.data.lm.LGAccounting;
import com.cannontech.analysis.data.lm.LMControlLog;
import com.cannontech.analysis.data.statistic.CarrierCommData;
import com.cannontech.analysis.data.statistic.CommChannelData;
import com.cannontech.analysis.data.statistic.DeviceCommData;
import com.cannontech.analysis.data.statistic.TransmitterCommData;
import com.cannontech.analysis.data.device.PowerFail;
import com.cannontech.analysis.data.device.Disconnect;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReportTypes
{
	/** enums for types of objects reported on */
	public static final int CARRIER_COMM_DATA = 0;
	public static final int COMM_CHANNEL_DATA = 1;
	public static final int DEVICE_COMM_DATA = 2;
	public static final int TRANS_COMM_DATA = 3;
	
	public static final int SYSTEM_LOG_DATA = 4;
	public static final int LM_CONTROL_LOG_DATA = 5;
	public static final int LG_ACCOUNTING_DATA = 6;
	
	public static final int MISSED_METER_DATA = 7;
	public static final int CARRIER_DATA = 8;
	public static final int POWER_FAIL_DATA =9;
	public static final int DISCONNECT_DATA =10;
	
	public static final int ENERGY_COMPANY_ACTIVITY_LOG_DATA = 11;
	
	private static Class[] typeToClassMap =
	{	
		CarrierCommData.class,
		CommChannelData.class,
		DeviceCommData.class,
		TransmitterCommData.class,
		
		SystemLog.class,
		LMControlLog.class,
		LGAccounting.class,
		
		MissedMeter.class,
		Carrier.class,
		PowerFail.class,
		Disconnect.class,
		
		ActivityLog.class
	};
	
	public static Object create(int type) {

		Object returnVal = null;
	
		if( type >= 0 && type < typeToClassMap.length )
		{
			try
			{
				returnVal = typeToClassMap[type].newInstance();
			}
			catch( IllegalAccessException e1 )
			{
				com.cannontech.clientutils.CTILogger.error( e1.getMessage(), e1 );
			}
			catch( InstantiationException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );
			}
	
		}
		else
		{
			return null;
		}

		return returnVal;

	}
	
	/**
	 * Returns an array of Strings for the column names
	 * @param reportType (see ReportTypes for valid values)
	 * @return String[]
	 */
	public static String[] getColumnNames(int reportType)
	{
		return ((Reportable)ReportTypes.create(reportType)).getColumnNames();
		/*switch (reportType)
		{
			case CARRIER_COMM_DATA:
				return CarrierCommData.getColumnNames();
			case COMM_CHANNEL_DATA:
				return CommChannelData.getColumnNames();
			case DEVICE_COMM_DATA :
				return DeviceCommData.getColumnNames();
			case TRANS_COMM_DATA :
				return TransmitterCommData.getColumnNames();
			case SYSTEM_LOG_DATA :
				return SystemLog.getColumnNames();
			case LM_CONTROL_LOG_DATA :
				return LMControlLog.getColumnNames();
			case LOAD_GROUP_DATA:
				return LGAccounting.getColumnNames();
			default:
				return null;
		}*/
	}

	/**
	 * Return an array of Class types for the columns
	 * @param reportType (see ReportTypes for valid values)
	 * @return Class[]
	 */
	public static Class[] getColumnTypes(int reportType)
	{
		return ((Reportable)ReportTypes.create(reportType)).getColumnTypes();
		/*switch (reportType)
		{
			case CARRIER_COMM_DATA :
				return CarrierCommData.getColumnTypes();
			case COMM_CHANNEL_DATA:
				return CommChannelData.getColumnTypes();
			case DEVICE_COMM_DATA :
				return DeviceCommData.getColumnTypes();
			case TRANS_COMM_DATA :
				return TransmitterCommData.getColumnTypes();
			case SYSTEM_LOG_DATA :
				return SystemLog.getColumnTypes();
			case LM_CONTROL_LOG_DATA :
				return LMControlLog.getColumnTypes();
			case LOAD_GROUP_DATA:
				return LMControlLog.getColumnTypes();
			default:
				return null;
		}*/
	}
	/**
	 * Returns the title string for the reportType
	 * @param reportType (see ReportTypes for valid values)
	 * @return String
	 */
	public static String getTitleString(int reportType)
	{
		return ((Reportable)ReportTypes.create(reportType)).getTitleString();
		/*switch (reportType)
		{
			case CARRIER_COMM_DATA :
				return CarrierCommData.getTitleString();
			case COMM_CHANNEL_DATA:
				return CommChannelData.getTitleString();
			case DEVICE_COMM_DATA :
				return DeviceCommData.getTitleString();
			case TRANS_COMM_DATA :
				return TransmitterCommData.getTitleString();
			case SYSTEM_LOG_DATA :
				return SystemLog.getTitleString();
			case LM_CONTROL_LOG_DATA :
				return LMControlLog.getTitleString();
			case LOAD_GROUP_DATA:
				return LGAccounting.getTitleString();
			default:
				return "Report";
		}*/
	}	
	
	/**
	 * Returns an array of column properties
	 * @param reportType (see ReportTypes for valid values)
	 * @return ColumnProperties[] 
	 */
	public static ColumnProperties[] getColumnProperties(int reportType)
	{
		return ((Reportable)ReportTypes.create(reportType)).getColumnProperties();
		/*switch (reportType)
		{
			case CARRIER_COMM_DATA :
				return CarrierCommData.getColumnProperties();
			case COMM_CHANNEL_DATA:
				return CommChannelData.getColumnProperties();
			case DEVICE_COMM_DATA :
				return DeviceCommData.getColumnProperties();
			case TRANS_COMM_DATA :
				return TransmitterCommData.getColumnProperties();
			case SYSTEM_LOG_DATA :
				return SystemLog.getColumnProperties();
			case LM_CONTROL_LOG_DATA :
				return LMControlLog.getColumnProperties();
			case LOAD_GROUP_DATA:
				return LGAccounting.getColumnProperties();
			default:
				return null;
		}*/
	}	
}
