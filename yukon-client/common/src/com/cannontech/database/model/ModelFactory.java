package com.cannontech.database.model;
/**
 * This type was created in VisualAge.
 */

public final class ModelFactory 
{
	public static final int DEVICE = 0;
	public static final int IED = 1;
	public static final int MCT = 2;
	public static final int METER = 3;
	public static final int PORT = 4;
	public static final int ROUTE = 5;
	public static final int RTU = 6;
	public static final int TRANSMITTER = 7;
	public static final int LMGROUPS =8;
	public static final int LMGROUPEMETCON = 9;
	public static final int LMGROUPVERSACOM = 10;

	public static final int STATEGROUP =11;
	public static final int CAPBANK =12;
	public static final int CAPBANKCONTROLLER =13;
	public static final int CAPCONTROLSTRATEGY =14;
	public static final int NOTIFICATION_GROUP =15;
	public static final int CONTACT = 16;
	public static final int ALARM_STATES = 17;
	public static final int LMPROGRAM = 18;
	public static final int EDITABLELCRSERIAL = 19;
	public static final int LMCONTROLAREA = 20;

	public static final int CICUSTOMER = 21;
	public static final int DEVICE_CUSTOMPOINTS = 22;
	public static final int HOLIDAY_SCHEDULE = 23;
	public static final int DEVICE_METERNUMBER = 24;
	public static final int COLLECTIONGROUP = 25;
	public static final int TESTCOLLECTIONGROUP = 26;
	public static final int CAPCONTROLFEEDER = 27;

	public static final int MCTBROADCAST = 28;
	public static final int LOGINS = 29;
	public static final int LOGIN_GROUPS = 30;
	public static final int BASELINE = 31;
	
	public static final int LMGROUPEXPRESSCOM = 32;
	
	//Report models
	public static final int LMGROUPREPORT = 33;
	
	public static final int TWOWAYCONFIG = 34;
	
	public static final int TAG = 35;

	//The above is an enumeration of indices into
	//the following array
	private static Class[] typeToClassMap =
	{
		DeviceTreeModel.class,
		IEDTreeModel.class,
		MCTTreeModel.class,
		MeterTreeModel.class,
		CommChannelTreeModel.class,
		RouteTreeModel.class,
		RTUTreeModel.class,
		TransmitterTreeModel.class,
		LMGroupsModel.class,
		LMGroupEmetconModel.class,
/*10*/LMGroupVersacomModel.class,
		StateGroupTreeModel.class,
		CapBankModel.class,
		CapBankControllerModel.class,
		CapControlSubBusModel.class,
/*15*/NotificationGroupTreeModel.class,
		ContactTreeModel.class,
		AlarmCategoriesTreeModel.class,
		LMProgramModel.class,
		EditableLCRSerialModel.class,
		LMControlAreaModel.class,
/*21*/CICustomerTableModel.class,
		DeviceTree_CustomPointsModel.class,
		HolidayScheduleModel.class,
		DeviceMeterGroupModel.class,
		CollectionGroupModel.class,
		TestCollectionGroupModel.class,
/*27*/CapControlFeederModel.class,
	  	MCTBroadcastGroupTreeModel.class,
	  	LoginTreeModel.class,
		LoginGroupTreeModel.class,
		BaselineModel.class,
		LMGroupExpresscomModel.class,
		LMGroupsCheckBoxModel.class,
		ConfigModel.class,
		TagModel.class
	};

/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.model.DBTreeModel
 * @param type int
 */
public static DBTreeModel create(int type) {

	DBTreeModel returnVal = null;
	
	if( type >= 0 && type < typeToClassMap.length )
	{
		try
		{
			returnVal = (DBTreeModel) typeToClassMap[type].newInstance();
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
}