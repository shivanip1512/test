package com.cannontech.database.data.device;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.capcontrol.CapBankController701x;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.device.lm.LMGroupPoint;
import com.cannontech.database.data.device.lm.LMGroupRipple;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PortTypes;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.port.TerminalServerSharedPort;


public final class DeviceFactory {

    public final static DeviceBase createDevice(int paoTypeId) {
        return createDevice(PaoType.getForId(paoTypeId));
    }
    
    public final static DeviceBase createDevice(PaoType paoType) {
        PaoUtils.validateDeviceType(paoType);

        DeviceBase returnDevice = null;

        switch( paoType )
        {
        //Dumb and Smart type transmitted devices
        case CCU710A:
            returnDevice = new CCU710A();
            break;
        case CCU711:
            returnDevice = new CCU711();
            break;
        case CCU721:
            returnDevice = new CCU721();
            break;
        case TCU5000:
            returnDevice = new TCU5000();
            break;
        case TCU5500:
            returnDevice = new TCU5500();
            break;
        case SERIES_5_LMI:
            returnDevice = new Series5LMI();
            break;
        case RTC:
            returnDevice = new RTC();
            break;
        case LCU415:
            returnDevice = new LCU415();
            break;
        case LCULG:
            returnDevice = new LCULG();
            break;
        case LCU_T3026:
            returnDevice = new LCUT3026();
            break;          
        case LCU_ER:
            returnDevice = new LCUER();
            break;
        case RTU_DNP:
        case RTU_MODBUS:
        case RTU_DART:
            returnDevice = new DNPBase();
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
            break;

            //use only 1 ION class for now!! 1-7-2003
        case ION_7700:
        case ION_7330:
        case ION_8300:
            returnDevice = new Ion7700();
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
            break;

        case RTUILEX:
            returnDevice = new RTUILEX();
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
            break;
        case RTUWELCO:
            returnDevice = new RTUWELCO();
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
            break;

            //IEDMeter type devices
        case ALPHA_A1:
        case ALPHA_PPLUS:
            returnDevice = new Alpha();
            break;
        case FULCRUM: //replaced Schlumberger
        case VECTRON: //replaced Schlumberger
        case QUANTUM:
            returnDevice = new Schlumberger();
            break;
        case KV:
            returnDevice = new KV();
            break;
        case KVII:
            returnDevice = new KV();
            break;
        case SENTINEL:
            returnDevice = new Sentinel();
            break;
        case FOCUS:
            returnDevice = new Focus();
            break;
        case ALPHA_A3:
            returnDevice = new Alpha();
            break;
        case LANDISGYRS4:
            returnDevice = new LandisGyrS4();
            break;
        case DAVISWEATHER:
            returnDevice = new DavisWeather();
            break;
        case DR_87:
            returnDevice = new DR87();
            break;
        case SIXNET:
            returnDevice = new Sixnet();
            break;
        case TRANSDATA_MARKV:
            returnDevice = new TransdataMarkV();
            break;
        case IPC430SL:
        case IPC430S4:
        case IPC420AD:
        case IPC410AL:
            IPCMeter meter = new IPCMeter();
            meter.setDeviceType(paoType.getDbString());
            //set up comm channel
            TerminalServerSharedPort comms = (TerminalServerSharedPort) PortFactory.createPort(PortTypes.TSERVER_SHARED);
            meter.setCommChannel(comms);
            returnDevice = meter;
            break;
        case RFN410FL:
        case RFN410FX:
        case RFN410FD:
        case RFN420FL:
        case RFN420FX:
        case RFN420FD:
        case RFN420CL:
        case RFN420CD:
        case RFN430A3:
        case RFN430KV:
        case RFWMETER:
            returnDevice = new RfnMeterBase();
            returnDevice.setDeviceClass(PaoClass.RFMESH.getDbString());
            returnDevice.setDeviceType(paoType.getDbString());
            break;
            
        case LCR6200_RFN:
        case LCR6600_RFN:
            returnDevice = new RfnBase();
            returnDevice.setDeviceClass(PaoClass.RFMESH.getDbString());
            returnDevice.setDeviceType(paoType.getDbString());
            break;

        //Carrier type devices
        case MCT213:
            returnDevice = new MCT213();
            break;
        case MCT318L:
            returnDevice = new MCT318L();
            break;
        case MCT310ID:
            returnDevice = new MCT310ID();
            break;
        case MCT310IDL:
            returnDevice = new MCT310IDL();
            break;
        case MCT310IL:
            returnDevice = new MCT310IL();
            break;
        case MCT310CT:
            returnDevice = new MCT310CT();
            break;
        case MCT310IM:
            returnDevice = new MCT310IM();
            break;
        case MCT310:
            returnDevice = new MCT310();
            break;
        case MCT410IL:
            returnDevice = new MCT410IL();
            break;
        case MCT410CL:
            returnDevice = new MCT410CL();
            break;
        case MCT410FL:
            returnDevice = new MCT410FL();
            break;
        case MCT410GL:
            returnDevice = new MCT410GL();
            break;
        case MCT420FL:
            returnDevice = new MCT420FL();
            break;
        case MCT420FLD:
            returnDevice = new MCT420FLD();
            break;
        case MCT420CL:
            returnDevice = new MCT420CL();
            break;
        case MCT420CLD:
            returnDevice = new MCT420CLD();
            break;
        case MCT430A:
            returnDevice = new MCT430A();
            break;
        case MCT430S4:
            returnDevice = new MCT430S4();
            break;
        case MCT430SL:
            returnDevice = new MCT430SL();
            break;
        case MCT430A3:
            returnDevice = new MCT430A3();
            break;
        case MCT470:
            returnDevice = new MCT470();
            break;
        case MCT318:
            returnDevice = new MCT318();
            break;
        case MCT360:
            returnDevice = new MCT360();
            break;
        case MCT370:
            returnDevice = new MCT370();
            break;
        case MCT240:
            returnDevice = new MCT240();
            break;
        case MCT248:
            returnDevice = new MCT248();
            break;
        case MCT250:
            returnDevice = new MCT250();
            break;
        case MCT210:
            returnDevice = new MCT210();
            break;
        case LMT_2:
            returnDevice = new LMT2();
            break;
        case DCT_501:
            returnDevice = new DCT_501();
            break;

        case REPEATER:
            returnDevice = new Repeater900();
            break;
        case REPEATER_902:
            returnDevice = new Repeater902();
            break;
        case REPEATER_800:
            returnDevice = new Repeater800();
            break;
        case REPEATER_801:
            returnDevice = new Repeater801();
            break;
        case REPEATER_850:
            returnDevice = new Repeater850();
            break;
        case REPEATER_921:
            returnDevice = new Repeater921();
            break;
        case TAPTERMINAL:
            returnDevice = new PagingTapTerminal();
            break;
        case TNPP_TERMINAL:
            returnDevice = new TNPPTerminal();
            break;
        case WCTP_TERMINAL:
            returnDevice = new WCTPTerminal();
            break;
        case SNPP_TERMINAL:
            returnDevice = new SNPPTerminal();
            break;
        case RDS_TERMINAL:
            returnDevice = new RDSTerminal();
            break;

        case LM_GROUP_EMETCON:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupEmetcon();
            returnDevice.setDeviceType( PAOGroups.STRING_EMETCON_GROUP[0] );
            returnDevice.setDeviceClass( DeviceClasses.STRING_CLASS_GROUP );
            break;
        case LM_GROUP_DIGI_SEP:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupDigiSep();
            returnDevice.setDeviceClass( paoType.getPaoClass().getDbString() );
            break;
        case LM_GROUP_VERSACOM:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupVersacom();
            returnDevice.setDeviceType( PAOGroups.STRING_VERSACOM_GROUP[0] );
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_EXPRESSCOMM:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupExpressCom();
            returnDevice.setDeviceType( PAOGroups.STRING_EXPRESSCOMM_GROUP[0] );
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_RIPPLE:
            returnDevice = new LMGroupRipple();
            returnDevice.setDeviceType(PAOGroups.STRING_RIPPLE_GROUP[0]);
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_POINT:
            returnDevice = new LMGroupPoint();
            returnDevice.setDeviceType(PAOGroups.STRING_POINT_GROUP[0]);
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_MCT:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupMCT();
            returnDevice.setDeviceType( PAOGroups.STRING_MCT_GROUP[0] );
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_SA305:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupSA305();
            returnDevice.setDeviceType(PAOGroups.STRING_SA305_GROUP[0]);
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_SA205:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupSA205();
            returnDevice.setDeviceType(PAOGroups.STRING_SA205_GROUP[0]);
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_SADIGITAL:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupSADigital();
            returnDevice.setDeviceType(PAOGroups.STRING_SADIGITAL_GROUP[0]);
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case LM_GROUP_GOLAY:
            returnDevice = new com.cannontech.database.data.device.lm.LMGroupGolay();
            returnDevice.setDeviceType(PAOGroups.STRING_GOLAY_GROUP[0]);
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;
        case MACRO_GROUP:
            returnDevice = new MacroGroup();
            returnDevice.setDeviceType(PAOGroups.STRING_MACRO_GROUP[0]);
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_GROUP);
            break;


        case CAPBANK:
            returnDevice = new com.cannontech.database.data.capcontrol.CapBank();
            returnDevice.setDeviceType( PAOGroups.STRING_CAP_BANK[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;
        case CAPBANKCONTROLLER:
            returnDevice = new com.cannontech.database.data.capcontrol.CapBankControllerVersacom();
            returnDevice.setDeviceType( PAOGroups.STRING_CAP_BANK_CONTROLLER[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;
        case CBC_EXPRESSCOM:
            returnDevice = new com.cannontech.database.data.capcontrol.CapBankControllerExpresscom();
            returnDevice.setDeviceType( PAOGroups.STRING_CBC_EXPRESSCOM[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;
        case CBC_7010:
            returnDevice = new CapBankController701x();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_7010[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;    
        case CBC_7011:
            returnDevice = new CapBankController701x();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_7010[1] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;
        case CBC_7012:
            returnDevice = new CapBankController701x();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_7010[2] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;
        case CBC_8020:
        case CBC_8024:
            returnDevice = new CapBankController702x();
            returnDevice.setDeviceType(paoType.getPaoTypeName());
            returnDevice.setDeviceClass(paoType.getPaoClass().getDbString());
            break;
        case CBC_FP_2800:
            returnDevice = new com.cannontech.database.data.capcontrol.CapBankController_FP_2800();
            returnDevice.setDeviceType( PAOGroups.STRING_CBC_FP_2800[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;
        case DNP_CBC_6510:
            returnDevice = new com.cannontech.database.data.capcontrol.CapBankController6510();
            returnDevice.setDeviceType( PAOGroups.STRING_DNP_CBC_6510[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;
        case CBC_7020:
            returnDevice = new CapBankController702x();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_7020[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;      

        case CBC_7022:
            returnDevice = new CapBankController702x();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_7020[1] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;      

        case CBC_7023:
            returnDevice = new CapBankController702x();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_7020[2] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;      

        case CBC_7024:
            returnDevice = new CapBankController702x();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_7020[3] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;      

        case CBC_DNP:
            returnDevice = new CapBankControllerDNP();
            returnDevice.setDeviceType(PAOGroups.STRING_CBC_DNP[0] );
            returnDevice.setDeviceClass(PAOGroups.STRING_CAT_CAPCONTROL);
            break;

            //The new MCT broadcast group (lead meter broadcast)
        case MCTBROADCAST:
            returnDevice = new MCT_Broadcast();
            returnDevice.setDeviceType( PAOGroups.STRING_MCT_BROADCAST[0] );
            break;


            // Two Way LCR
        case LCR3102:
            returnDevice = new TwoWayLCR();
            returnDevice.setDeviceType(PAOGroups.STRING_LCR_3102[0]);
            break;

            // not a real device    
        case VIRTUAL_SYSTEM:
            returnDevice = new VirtualDevice();
            returnDevice.setDeviceType( PAOGroups.STRING_VIRTUAL_SYSTEM[0] );
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_VIRTUAL);
            break;

        case NEUTRAL_MONITOR :
            returnDevice = new NeutralMonitor();
            returnDevice.setDeviceType(  PAOGroups.STRING_NEUTRAL_MONITOR[0] );
            break;

        case FAULT_CI:
            returnDevice = new FaultCI();
            returnDevice.setDeviceType(  PAOGroups.STRING_FAULT_CI[0] );
            break;

            // a system reserved device place holder (not to be available to users) 
        case SYSTEM:
            returnDevice = new SystemDevice();
            returnDevice.setDeviceType( PAOGroups.STRING_SYSTEM[0] );
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_SYSTEM);
            break;

        case RTM:
            returnDevice = new RTM();
            returnDevice.setDeviceClass(DeviceClasses.STRING_CLASS_RTU);
            break;
        default:
            returnDevice = new GenericDevice();
            returnDevice.setDeviceClass(paoType.getPaoClass().getDbString());
            returnDevice.setPAOCategory(paoType.getPaoCategory().name());
            returnDevice.setDeviceType(paoType.getPaoTypeName());
        }

        //Set a couple reasonable defaults
        if( returnDevice.getPAOCategory() == null )
            returnDevice.setPAOCategory( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_DEVICE );

        returnDevice.setDisableFlag('N');
        returnDevice.getDevice().setAlarmInhibit('N');
        returnDevice.getDevice().setControlInhibit('N');
        setDeviceDefaults(paoType.getDeviceTypeId(), returnDevice);
        
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
