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
	public static final int EDITABLE_LCR_SERIAL = 19;
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

	public static final int TWOWAYCONFIG = 33;
	
	public static final int TAG = 34;
	//CheckBox Tree Models
	public static final int LMGROUP_CHECKBOX = 35;
	public static final int MCT_CHECKBOX = 36;
	public static final int DEVICE_CHECKBOX=37;
	public static final int TRANSMITTER_CHECKBOX=38;
	public static final int COMMCHANNEL_CHECKBOX=39;
	public static final int COLLECTIONGROUP_CHECKBOX=40;
		
	public static final int LMCONSTRAINT = 41;
	public static final int LMSCENARIO = 42;
	
	public static final int EDITABLE_VERSACOM_SERIAL = 43;
	public static final int EDITABLE_EXPRESSCOM_SERIAL = 44;
	public static final int EDITABLE_SA305_SERIAL = 45;
	public static final int EDITABLE_SA205_SERIAL = 46;
	
	public static final int SEASON = 47;
	
	public static final int LMGROUPSA305 = 48;
	public static final int LMGROUPSA205 = 49;
	public static final int LMGROUPSADIGITAL = 50;
	public static final int GOLAY = 51;

	public static final int ENERGYCOMPANY_CHECKBOX=52;
	public static final int SYSTEMLOG_TYPES_CHECKBOX=53;
	
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
/*21*/CICustomerTreeModel.class,
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
/*33 */ConfigModel.class,
		TagModel.class,
		LMGroupsCheckBoxModel.class,
		MCTCheckBoxTreeModel.class,
		DeviceCheckBoxTreeModel.class,
		TransmitterCheckBoxTreeModel.class,
		CommChannelCheckBoxTreeModel.class,
		CollectionGroupCheckBoxTreeModel.class,
		LMConstraintModel.class,
/* 42*/LMScenarioModel.class,
		EditableVersacomModel.class,
		EditableExpresscomModel.class,
		EditableSA305Model.class,
		EditableSA205Model.class,
/* 47*/	SeasonModel.class,
		LMGroupSA305Model.class,
		LMGroupSA205Model.class,
		LMGroupSADigitalModel.class,
		LMGroupGolayModel.class,
		EnergyCompanyCheckBoxTreeModel.class,
/* 53*/	SystemLogTypeCheckBoxTreeModel.class
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

	/**
	 * @return
	 */
	public static Class getModelClass(int type)
	{
		return typeToClassMap[type];
	}

	/**
	 * @param type
	 * @return
	 */
	public static boolean isEditableSerial(int type)
	{
		if( type == EDITABLE_SA205_SERIAL ||
			type == EDITABLE_SA305_SERIAL || 
			type == EDITABLE_VERSACOM_SERIAL || 
			type == EDITABLE_EXPRESSCOM_SERIAL || 
			type == EDITABLE_LCR_SERIAL)
				return true;
		return false; 
	}
}