package com.cannontech.database.data.device;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.device.lm.LMGroupPoint;
import com.cannontech.database.data.device.lm.LMGroupRipple;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;


public final class DeviceFactory {
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.data.device.DeviceBase
 * @param deviceType int
 */
public final static DeviceBase createDevice(int deviceType) {

	DeviceBase returnDevice = null;

	switch( deviceType )
	{
		//Dumb and Smart type transmitted devices
		case PAOGroups.CCU710A:
			returnDevice = new CCU710A();
			break;
		case PAOGroups.CCU711:
			returnDevice = new CCU711();
			break;
		case PAOGroups.TCU5000:
			returnDevice = new TCU5000();
			break;
		case PAOGroups.TCU5500:
			returnDevice = new TCU5500();
			break;
		case PAOGroups.SERIES_5_LMI:
			returnDevice = new Series5LMI();
			break;
		case PAOGroups.RTC:
			returnDevice = new RTC();
			break;
		case PAOGroups.LCU415:
			returnDevice = new LCU415();
			break;
		case PAOGroups.LCULG:
			returnDevice = new LCULG();
			break;
		case PAOGroups.LCU_T3026:
			returnDevice = new LCUT3026();
			break;			
		case PAOGroups.LCU_ER:
			returnDevice = new LCUER();
			break;
      case PAOGroups.RTU_DNP:
      case PAOGroups.RTU_DART:
         returnDevice = new DNPBase();
         returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
         break;

		//use only 1 ION class for now!! 1-7-2003
      case PAOGroups.ION_7700:
      case PAOGroups.ION_7330:
      case PAOGroups.ION_8300:
         returnDevice = new Ion7700();
         returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
         break;

		case PAOGroups.RTUILEX:
			returnDevice = new RTUILEX();
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
			break;
		case PAOGroups.RTUWELCO:
			returnDevice = new RTUWELCO();
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
			break;
			
		//IEDMeter type devices
		case PAOGroups.ALPHA_A1:
		case PAOGroups.ALPHA_PPLUS:
			returnDevice = new Alpha();
			break;
		case PAOGroups.FULCRUM: //replaced Schlumberger
		case PAOGroups.VECTRON: //replaced Schlumberger
		case PAOGroups.QUANTUM:
			returnDevice = new Schlumberger();
			break;
		case PAOGroups.KV:
			returnDevice = new KV();
			break;
		case PAOGroups.KVII:
			returnDevice = new KV();
			break;
		case PAOGroups.LANDISGYRS4:
			returnDevice = new LandisGyrS4();
			break;
		case PAOGroups.DAVISWEATHER:
			returnDevice = new DavisWeather();
			break;
		case PAOGroups.DR_87:
			returnDevice = new DR87();
			break;
		case PAOGroups.SIXNET:
			returnDevice = new Sixnet();
			break;
		case PAOGroups.TRANSDATA_MARKV:
			returnDevice = new TransdataMarkV();
			break;

				
		//Carrier type devices
		case PAOGroups.MCT213:
			returnDevice = new MCT213();
			break;
		case PAOGroups.MCT318L:
			returnDevice = new MCT318L();
			break;
		case PAOGroups.MCT310ID:
			returnDevice = new MCT310ID();
			break;
		case PAOGroups.MCT310IDL:
			returnDevice = new MCT310IDL();
			break;
		case PAOGroups.MCT310IL:
			returnDevice = new MCT310IL();
			break;
      case PAOGroups.MCT310CT:
         returnDevice = new MCT310CT();
         break;
      case PAOGroups.MCT310IM:
         returnDevice = new MCT310IM();
         break;
		case PAOGroups.MCT310:
			returnDevice = new MCT310();
			break;
		case PAOGroups.MCT410_KWH_ONLY:
			returnDevice = new MCT410_KWH_Only();
			break;
		case PAOGroups.MCT410IL:
			returnDevice = new MCT410IL();
			break;
		case PAOGroups.MCT318:
			returnDevice = new MCT318();
			break;
		case PAOGroups.MCT360:
			returnDevice = new MCT360();
			break;
		case PAOGroups.MCT370:
			returnDevice = new MCT370();
			break;
		case PAOGroups.MCT240:
			returnDevice = new MCT240();
			break;
		case PAOGroups.MCT248:
			returnDevice = new MCT248();
			break;
		case PAOGroups.MCT250:
			returnDevice = new MCT250();
			break;
		case PAOGroups.MCT210:
			returnDevice = new MCT210();
			break;
		case PAOGroups.LMT_2:
			returnDevice = new LMT2();
			break;
		case PAOGroups.DCT_501:
			returnDevice = new DCT_501();
			break;

		case PAOGroups.REPEATER:
			returnDevice = new Repeater900();
			break;
		case PAOGroups.REPEATER_800:
			returnDevice = new Repeater800();
			break;
		case PAOGroups.TAPTERMINAL:
			returnDevice = new PagingTapTerminal();
			break;
		case PAOGroups.WCTP_TERMINAL:
			returnDevice = new WCTPTerminal();
			break;

		case PAOGroups.LM_GROUP_EMETCON:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupEmetcon();
			returnDevice.setDeviceType( PAOGroups.STRING_EMETCON_GROUP[0] );
			returnDevice.setDeviceClass( DeviceClasses.STRING_CLASS_GROUP );
			break;
		case PAOGroups.LM_GROUP_VERSACOM:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupVersacom();
			returnDevice.setDeviceType( PAOGroups.STRING_VERSACOM_GROUP[0] );
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_EXPRESSCOMM:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupExpressCom();
			returnDevice.setDeviceType( PAOGroups.STRING_EXPRESSCOMM_GROUP[0] );
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_RIPPLE:
			returnDevice = new LMGroupRipple();
			returnDevice.setDeviceType(PAOGroups.STRING_RIPPLE_GROUP[0]);
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_POINT:
			returnDevice = new LMGroupPoint();
			returnDevice.setDeviceType(PAOGroups.STRING_POINT_GROUP[0]);
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_MCT:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupMCT();
			returnDevice.setDeviceType( PAOGroups.STRING_MCT_GROUP[0] );
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_SA305:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupSA305();
			returnDevice.setDeviceType(PAOGroups.STRING_SA305_GROUP[0]);
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_SA205:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupSA205();
			returnDevice.setDeviceType(PAOGroups.STRING_SA205_GROUP[0]);
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_SADIGITAL:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupSADigital();
			returnDevice.setDeviceType(PAOGroups.STRING_SADIGITAL_GROUP[0]);
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.LM_GROUP_GOLAY:
			returnDevice = new com.cannontech.database.data.device.lm.LMGroupGolay();
			returnDevice.setDeviceType(PAOGroups.STRING_GOLAY_GROUP[0]);
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;
		case PAOGroups.MACRO_GROUP:
			returnDevice = new MacroGroup();
			returnDevice.setDeviceType(PAOGroups.STRING_MACRO_GROUP[0]);
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
			break;


		case PAOGroups.CAPBANK:
			returnDevice = new com.cannontech.database.data.capcontrol.CapBank();
			returnDevice.setDeviceType( PAOGroups.STRING_CAP_BANK[0] );
			returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
			break;
		case PAOGroups.CAPBANKCONTROLLER:
			returnDevice = new com.cannontech.database.data.capcontrol.CapBankControllerVersacom();
			returnDevice.setDeviceType( PAOGroups.STRING_CAP_BANK_CONTROLLER[0] );
			returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
			break;
		case PAOGroups.CBC_FP_2800:
			returnDevice = new com.cannontech.database.data.capcontrol.CapBankController_FP_2800();
			returnDevice.setDeviceType( PAOGroups.STRING_CBC_FP_2800[0] );
			returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
			break;
      case PAOGroups.DNP_CBC_6510:
         returnDevice = new com.cannontech.database.data.capcontrol.CapBankController6510();
         returnDevice.setDeviceType( PAOGroups.STRING_DNP_CBC_6510[0] );
         returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
         break;


		//The new MCT broadcast group (lead meter broadcast)
		case PAOGroups.MCTBROADCAST:
			returnDevice = new MCT_Broadcast();
			returnDevice.setDeviceType( PAOGroups.STRING_MCT_BROADCAST[0] );
			break;

			
		// not a real device	
		case PAOGroups.VIRTUAL_SYSTEM:
			returnDevice = new VirtualDevice();
			returnDevice.setDeviceType( PAOGroups.STRING_VIRTUAL_SYSTEM[0] );
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_VIRTUAL);
			break;
			
		case PAOGroups.RTM:
			returnDevice = new RTM();
			returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
			break;
	}

	//Set a couple reasonable defaults
	if( returnDevice.getPAOCategory() == null )
		returnDevice.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );

	returnDevice.setDisableFlag( new Character('N') );
	returnDevice.getDevice().setAlarmInhibit(new Character('N') );
	returnDevice.getDevice().setControlInhibit(new Character('N') );
	setDeviceDefaults( deviceType, returnDevice );
		
	return returnDevice;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 3:39:00 PM)
 */
private final static DeviceBase setDeviceDefaults( int type, DeviceBase returnDevice ) 
{
	returnDevice.setDeviceType( com.cannontech.database.data.pao.PAOGroups.getPAOTypeString(type) );

	if( DeviceTypesFuncs.isMCT(type)
      && (type == PAOGroups.MCT360 || type == PAOGroups.MCT370) )
	{
		((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setConnectedIED("None");
		((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setIEDScanRate(new Integer(120));
		((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setDefaultDataClass(new Integer(0));
		((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setDefaultDataOffset(new Integer(0));
		((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setPassword("None");
		((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setRealTimeScan(new Character('N'));
	}


	//programmer error at this point, let us know
	if( returnDevice.getPAOClass() == null )
		throw new IllegalStateException(
			"A device exists that has a (null) PAOClass, DeviceBaseClass = " +
			returnDevice.getClass().getName() );

	return returnDevice;
}
}
