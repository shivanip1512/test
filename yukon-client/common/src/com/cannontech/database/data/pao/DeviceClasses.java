package com.cannontech.database.data.pao;

/**
 * This type was created in VisualAge.
 */
public final class DeviceClasses 
{
   public final static int TRANSMITTER       = 1000;
   public final static int RTU               = 1001;
   public final static int IED               = 1002;
   public final static int METER             = 1003;
   public final static int CARRIER           = 1004;
   public final static int GROUP             = 1005;
   public final static int VIRTUAL           = 1007;
   public final static int LOADMANAGEMENT    = 1008;
   public final static int SYSTEM            = 1009;
   public final static int INVALID           = 1011;
   
   public static final String STRING_CLASS_RTU = "RTU";
   public static final String STRING_CLASS_TRANSMITTER = "TRANSMITTER";
   public static final String STRING_CLASS_METER = "METER";
   public static final String STRING_CLASS_CARRIER = "CARRIER";
   public static final String STRING_CLASS_IED = "IED";
   public static final String STRING_CLASS_GROUP = "GROUP";
   public static final String STRING_CLASS_SYSTEM = "SYSTEM";
   public static final String STRING_CLASS_VIRTUAL = "VIRTUAL";
   public static final String STRING_CLASS_LOADMANAGER = "LOADMANAGEMENT";
   
   public static final int[] IN_DEVICEDIRECTCOMMSETTINGS_TABLE = 
   {
	  //DeviceDirectCommSettings
	  RTU, TRANSMITTER, METER, IED, 
   };

   public static final int[] CORE_DEVICE_CLASSES = 
   {
		CARRIER, IED, METER,
		RTU, TRANSMITTER, VIRTUAL
   };
 
/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static int getClass(String classString) {

	String compareString = classString.toUpperCase().trim();

	if( compareString.equals(STRING_CLASS_RTU) )
		return RTU;
	else
	if( compareString.equals(STRING_CLASS_TRANSMITTER) )
		return TRANSMITTER;
	else
	if( compareString.equals(STRING_CLASS_METER) )
		return METER;
	else
	if( compareString.equals(STRING_CLASS_CARRIER) )
		return CARRIER;
	else
	if( compareString.equals(STRING_CLASS_IED) )
		return IED;
	else
	if( compareString.equals(STRING_CLASS_GROUP) )
		return GROUP;
	else
	if( compareString.equals(STRING_CLASS_SYSTEM) )
		return SYSTEM;	
	else
//	if( compareString.equals(STRING_CLASS_CAPCONTROL) )
//		return CAPCONTROL;
//	else
	if( compareString.equals(STRING_CLASS_VIRTUAL) )
		return VIRTUAL;			
	else
	if( compareString.equals(STRING_CLASS_LOADMANAGER) )
		return LOADMANAGEMENT;
	else
		return INVALID;
}

/**
 * This method was created in VisualAge.
 * @return int
 * @param classString java.lang.String
 */
public final static String getClass(int classInt)
{
	switch( classInt )
	{
		case RTU:
			return STRING_CLASS_RTU;

		case TRANSMITTER:
			return STRING_CLASS_TRANSMITTER;

		case METER:
			return STRING_CLASS_METER;

		case CARRIER:
			return STRING_CLASS_CARRIER;

		case IED:
			return STRING_CLASS_IED;

		case GROUP:
			return STRING_CLASS_GROUP;

		case SYSTEM:
			return STRING_CLASS_SYSTEM;

		case VIRTUAL:
			return STRING_CLASS_VIRTUAL;

		case LOADMANAGEMENT:
			return STRING_CLASS_LOADMANAGER;
			
		default:
			return PAOGroups.STRING_INVALID;
	}

}

/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 5:08:54 PM)
 * @return boolean
 */
public final static boolean hasDeviceDirectCommSettings(int classNumber)
{
	for( int i = 0; i < IN_DEVICEDIRECTCOMMSETTINGS_TABLE.length; i++ )
	{
		if( IN_DEVICEDIRECTCOMMSETTINGS_TABLE[i] == classNumber )
			return true;
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 5:08:54 PM)
 * @return boolean
 */
public final static boolean isCoreDeviceClass(int classNumber)
{
	for( int i = 0; i < CORE_DEVICE_CLASSES.length; i++ )
	{
		if( CORE_DEVICE_CLASSES[i] == classNumber )
			return true;
	}

	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 1:46:22 PM)
 * @return boolean
 * @param deviceType int
 */
public final static boolean isMeterClass(int deviceClass) 
{
	switch(deviceClass)
	{
		case LOADMANAGEMENT:
		case CARRIER:
		case IED:
		case METER:
		case RTU:
		case TRANSMITTER:
		case VIRTUAL:
			return true;

		default:
			return false;
	}
}
}
