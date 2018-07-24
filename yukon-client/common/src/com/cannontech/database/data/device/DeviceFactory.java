package com.cannontech.database.data.device;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController7010;
import com.cannontech.database.data.capcontrol.CapBankController7011;
import com.cannontech.database.data.capcontrol.CapBankController7012;
import com.cannontech.database.data.capcontrol.CapBankController7020;
import com.cannontech.database.data.capcontrol.CapBankController7022;
import com.cannontech.database.data.capcontrol.CapBankController7023;
import com.cannontech.database.data.capcontrol.CapBankController7024;
import com.cannontech.database.data.capcontrol.CapBankController8020;
import com.cannontech.database.data.capcontrol.CapBankController8024;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.capcontrol.CapBankControllerLogical;
import com.cannontech.database.data.capcontrol.CapBankControllerExpresscom;
import com.cannontech.database.data.capcontrol.CapBankControllerVersacom;
import com.cannontech.database.data.capcontrol.CapBankController_FP_2800;
import com.cannontech.database.data.device.lm.LMGroupDigiSep;
import com.cannontech.database.data.device.lm.LMGroupEcobee;
import com.cannontech.database.data.device.lm.LMGroupEmetcon;
import com.cannontech.database.data.device.lm.LMGroupGolay;
import com.cannontech.database.data.device.lm.LMGroupHoneywell;
import com.cannontech.database.data.device.lm.LMGroupMCT;
import com.cannontech.database.data.device.lm.LMGroupPlcExpressCom;
import com.cannontech.database.data.device.lm.LMGroupPoint;
import com.cannontech.database.data.device.lm.LMGroupRfnExpressCom;
import com.cannontech.database.data.device.lm.LMGroupRipple;
import com.cannontech.database.data.device.lm.LMGroupSA205;
import com.cannontech.database.data.device.lm.LMGroupSA305;
import com.cannontech.database.data.device.lm.LMGroupSADigital;
import com.cannontech.database.data.device.lm.LMGroupVersacom;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.port.TerminalServerSharedPortBase;


public final class DeviceFactory {

    /**
     * @deprecated use {@link #createDevice(PaoType)}
     */
    @Deprecated
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
            returnDevice = new RTUDnp();
            break;
        case RTU_MODBUS:
            returnDevice = new RTUModbus();
            break;
        case RTU_DART:
            returnDevice = new RTUDart();
            break;
        case ION_7700:
            returnDevice = new Ion7700();
            break;
        case ION_7330:
            returnDevice = new Ion7330();
            break;
        case ION_8300:
            returnDevice = new Ion8300();
            break;
        case RTUILEX:
            returnDevice = new RTUILEX();
            break;
        case RTUWELCO:
            returnDevice = new RTUWELCO();
            break;
        //IEDMeter type devices
        case ALPHA_A1:
            returnDevice = new AlphaA1();
            break;
        case ALPHA_PPLUS:
            returnDevice = new AlphaPowerPlus();
            break;
        case ALPHA_A3:
            returnDevice = new AlphaA3();
            break;
        case FULCRUM:
            returnDevice = new Fulcrum();
            break;
        case VECTRON:
            returnDevice = new Vectron();
            break;
        case QUANTUM:
            returnDevice = new Quantum();
            break;
        case KV:
            returnDevice = new KV();
            break;
        case KVII:
            returnDevice = new KVII();
            break;
        case SENTINEL:
            returnDevice = new Sentinel();
            break;
        case FOCUS:
            returnDevice = new Focus();
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
            returnDevice = new IPC430SL();
            break;
        case IPC430S4E:
            returnDevice = new IPC430S4E();
            break;
        case IPC420FD:
            returnDevice = new IPC420FD();
            break;
        case IPC410FL:
            returnDevice = new IPC410FL();
            break;
        case RFN410FL:
        case RFN410FX:
        case RFN410FD:
        case RFN420FL:
        case RFN420FX:
        case RFN420FD:
        case RFN420FRX:
        case RFN420FRD:
        case RFN410CL:
        case RFN420CL:
        case RFN420CD:
        case RFN430A3D:
        case RFN430A3T:
        case RFN430A3K:
        case RFN430A3R:
        case RFN430KV:
//        case RFN440_2131T:
        case RFN440_2131TD:
//        case RFN440_2132T:
        case RFN440_2132TD:
//        case RFN440_2133T:
        case RFN440_2133TD:
        case RFWMETER:
        case RFW201:
        case RFG201:
        case RFN430SL0:
        case RFN430SL1:
        case RFN430SL2:
        case RFN430SL3:
        case RFN430SL4:
        case RFN510FL:
        case RFN520FAX:
        case RFN520FRX:
        case RFN520FAXD:
        case RFN520FRXD:
        case RFN530FAX:
        case RFN530FRX:
        case RFN530S4X:
        case RFN530S4EAX:
        case RFN530S4EAXR:
        case RFN530S4ERX:
        case RFN530S4ERXR:
            returnDevice = new RfnMeterBase(paoType);
            break;
            
        case LCR6200_RFN:
            returnDevice = new RfnLcr6200();
            break;
        case LCR6600_RFN:
            returnDevice = new RfnLcr6600();
            break;
        case LCR6700_RFN:
            returnDevice = new RfnLcr6700();
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
        case MCT420FD:
            returnDevice = new MCT420FD();
            break;
        case MCT420CL:
            returnDevice = new MCT420CL();
            break;
        case MCT420CD:
            returnDevice = new MCT420CD();
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
        case MCT440_2131B:
            returnDevice = new MCT440_2131B();
            break;
        case MCT440_2132B:
            returnDevice = new MCT440_2132B();
            break;
        case MCT440_2133B:
            returnDevice = new MCT440_2133B();
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
            returnDevice = new LMGroupEmetcon();
            break;
        case LM_GROUP_DIGI_SEP:
            returnDevice = new LMGroupDigiSep();
            break;
        case LM_GROUP_ECOBEE:
            returnDevice = new LMGroupEcobee();
            break;
        case LM_GROUP_HONEYWELL:
            returnDevice = new LMGroupHoneywell();
            break;
        case LM_GROUP_VERSACOM:
            returnDevice = new LMGroupVersacom();
            break;
        case LM_GROUP_EXPRESSCOMM:
            returnDevice = new LMGroupPlcExpressCom();
            break;
        case LM_GROUP_RFN_EXPRESSCOMM:
            returnDevice = new LMGroupRfnExpressCom();
            break;
        case LM_GROUP_RIPPLE:
            returnDevice = new LMGroupRipple();
            break;
        case LM_GROUP_POINT:
            returnDevice = new LMGroupPoint();
            break;
        case LM_GROUP_MCT:
            returnDevice = new LMGroupMCT();
            break;
        case LM_GROUP_SA305:
            returnDevice = new LMGroupSA305();
            break;
        case LM_GROUP_SA205:
            returnDevice = new LMGroupSA205();
            break;
        case LM_GROUP_SADIGITAL:
            returnDevice = new LMGroupSADigital();
            break;
        case LM_GROUP_GOLAY:
            returnDevice = new LMGroupGolay();
            break;
        case MACRO_GROUP:
            returnDevice = new MacroGroup();
            break;

        case CAPBANK:
            returnDevice = new CapBank();
            break;
        case CAPBANKCONTROLLER:
            returnDevice = new CapBankControllerVersacom();
            break;
        case CBC_EXPRESSCOM:
            returnDevice = new CapBankControllerExpresscom();
            break;
        case CBC_7010:
            returnDevice = new CapBankController7010();
            break;
        case CBC_7011:
            returnDevice = new CapBankController7011();
            break;
        case CBC_7012:
            returnDevice = new CapBankController7012();
            break;    
        case CBC_7020:
            returnDevice = new CapBankController7020();
            break;    
        case CBC_7022:
            returnDevice = new CapBankController7022();
            break;    
        case CBC_7023:
            returnDevice = new CapBankController7023();
            break;    
        case CBC_7024:
            returnDevice = new CapBankController7024();
            break;    
        case CBC_8020:
            returnDevice = new CapBankController8020();
            break;    
        case CBC_8024:
            returnDevice = new CapBankController8024();
            break;    
        case CBC_FP_2800:
            returnDevice = new CapBankController_FP_2800();
            break;

        case CBC_DNP:
            returnDevice = new CapBankControllerDNP();
            break;

        case CBC_LOGICAL:
            returnDevice = new CapBankControllerLogical();
            break;

            //The new MCT broadcast group (lead meter broadcast)
        case MCTBROADCAST:
            returnDevice = new MCT_Broadcast();
            break;

            // Two Way LCR
        case LCR3102:
            returnDevice = new TwoWayLCR();
            break;

            // not a real device    
        case VIRTUAL_SYSTEM:
            returnDevice = new VirtualDevice();
            break;

        case NEUTRAL_MONITOR :
            returnDevice = new NeutralMonitor();
            break;

        case FAULT_CI:
            returnDevice = new FaultCI();
            break;

            // a system reserved device place holder (not to be available to users) 
        case SYSTEM:
            returnDevice = new SystemDevice();
            break;

        case RTM:
            returnDevice = new RTM();
            break;
        case RFN_1200:
            returnDevice = new Rfn1200();
            break;
        case RFN_GATEWAY:
            returnDevice = new RfnGateway();
            break;
        case GWY800:
            returnDevice = new RfnGwy800();
            break;
        case RFN_RELAY:
            returnDevice = new RfnRelay();
            break;
        default:
            CTILogger.error("PaoType " + paoType + " is undefined for DeviceFactory");
            throw new IllegalArgumentException("PaoType " + paoType + " is undefined for DeviceFactory");
        }

        returnDevice.setDisableFlag('N');
        returnDevice.getDevice().setAlarmInhibit('N');
        returnDevice.getDevice().setControlInhibit('N');
        setDeviceDefaults(paoType, returnDevice);
        
        return returnDevice;
    }

    
    private final static DeviceBase setDeviceDefaults(PaoType paoType, DeviceBase returnDevice) {
        if (paoType == PaoType.MCT360 || paoType == PaoType.MCT370) {
            ((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setConnectedIED("None");
            ((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setIEDScanRate(new Integer(120));
            ((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setDefaultDataClass(new Integer(0));
            ((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setDefaultDataOffset(new Integer(0));
            ((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setPassword("None");
            ((MCTIEDBase) returnDevice).getDeviceMCTIEDPort().setRealTimeScan(new Character('N'));
        }

        //set up comm channel for IPCMeter types.
        if (returnDevice instanceof IPCMeter) {
            TerminalServerSharedPortBase comms = (TerminalServerSharedPortBase) PortFactory.createPort(PaoType.TSERVER_SHARED);
            ((IPCMeter)returnDevice).setCommChannel(comms);
        }
        
        return returnDevice;
    }
}
