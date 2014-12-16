package com.cannontech.database.data.device;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.DeviceCBC;
import com.cannontech.database.db.device.DeviceAddress;
import com.cannontech.database.db.device.DeviceMCT400Series;
import com.cannontech.spring.YukonSpringHook;

public final class DeviceTypesFuncs {

    /**
     * All Meters that have a device scan rate. The meters in this function are
     * take from DeviceEditorPanel.java (//3 - DeviceScanRateEditorPanel)...probably should be updated once in a while.
     */
    public final static boolean hasDeviceScanRate(PaoType paoType) {
        if (paoType.isCcu() || 
                paoType.isTcu() || 
                paoType.isLcu() || 
                paoType.isIed() || 
                paoType.isMct() || 
                paoType.isRepeater() || 
                paoType.isRtu() ||
                paoType.isIon() ||
                paoType == PaoType.DNP_CBC_6510)
            return true;
        else
            return false;
    }

    public final static boolean isCapBankController702X(PaoType deviceType) {
        return (deviceType == PaoType.CBC_7020 || 
                deviceType == PaoType.CBC_7022 || 
                deviceType == PaoType.CBC_7023 || 
                deviceType == PaoType.CBC_7024);
    }

    public final static boolean isLoadProfile1Channel(PaoType paoType) {
        switch (paoType) {
        case MCT240:
        case MCT248:
        case MCT250:
        case MCT310IL:
        case MCT310IDL:
        case LMT_2:
            return true;

        default:
            return false;
        }
    }

    public final static boolean isLoadProfile4Channel(PaoType paoType) {
        switch (paoType) {
        case MCT318L:
        case DCT_501:
        case MCT410IL:
        case MCT410CL:
        case MCT410FL:
        case MCT410GL:
        case MCT420FL:
        case MCT420FD:
        case MCT420CL:
        case MCT420CD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
        case MCT440_2131B:
        case MCT440_2132B:
        case MCT440_2133B:
        case MCT470:
            return true;

        default:
            return false;
        }
    }

    public final static boolean isLoadProfile3Channel(PaoType paoType) {
        switch (paoType) {
        case MCT310CT:
        case MCT310IM:
            return true;

        default:
            return false;
        }
    }

    public final static boolean isLoadProfileVoltage(PaoType paoType) {
        switch (paoType) {
        case MCT410IL:
        case MCT410CL:
        case MCT410FL:
        case MCT410GL:
        case MCT420FL:
        case MCT420FD:
        case MCT420CL:
        case MCT420CD:
        case MCT440_2131B:
        case MCT440_2132B:
        case MCT440_2133B:
        case MCT470:
            return true;
        default:
            return false;
        }
    }

    public final static boolean isMCT3xx(PaoType paoType) {
        switch (paoType) {
        case MCT310:
        case MCT318:
        case MCT360:
        case MCT370:
        case MCT310ID:
        case MCT310IDL:
        case MCT310IL:
        case MCT310CT:
        case MCT310IM:
        case MCT318L:
            return true;

        default:
            return false;
        }

    }

    public static boolean isMCT2XXORMCT310XX(PaoType paoType) {
        switch (paoType) {
        case MCT210:
        case MCT213:
        case MCT240:
        case MCT248:
        case MCT250:
        case MCT310:
        case MCT310ID:
        case MCT310IDL:
        case MCT310IL:
        case MCT310CT:
        case MCT310IM:
            return true;
        default:
            return false;
        }

    }

    public static boolean isMCT4XX(PaoType paoType) {
        switch (paoType) {
        case MCT410IL:
        case MCT410CL:
        case MCT410FL:
        case MCT410GL:
        case MCT420FL:
        case MCT420FD:
        case MCT420CL:
        case MCT420CD:
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
        case MCT440_2131B:
        case MCT440_2132B:
        case MCT440_2133B:
        case MCT470:
            return true;
        default:
            return false;
        }

    }

    public static boolean isMCT430(PaoType paoType) {
        switch (paoType) {
        case MCT430A:
        case MCT430S4:
        case MCT430SL:
        case MCT430A3:
            return true;
        default:
            return false;
        }
    }

    public static boolean isMCT410(PaoType paoType) {
        switch (paoType) {
        case MCT410IL:
        case MCT410CL:
        case MCT410FL:
        case MCT410GL:
            return true;
        default:
            return false;
        }

    }

    public static boolean isMCT420(PaoType paoType) {
        switch (paoType) {
        case MCT420FL:
        case MCT420FD:
        case MCT420CL:
        case MCT420CD:
            return true;
        default:
            return false;
        }
    }

    public static boolean isDisconnectMCT(PaoType paoType) {
        switch (paoType) {
        case MCT213:
        case MCT310ID:
        case MCT310IDL:
        case MCT420FD:
        case MCT420CD:
        case MCT440_2131B:
        case MCT440_2132B:
        case MCT440_2133B:
            return true;
        default:
            return false;
        }
    }
    
    @Deprecated
    /** use ChangeDeviceTypeService */
    public static ICapBankController changeCBCType(PaoType newType, ICapBankController val) {
        
        PaoType type = newType;

        DBPersistent oldDevice = null;

        // get a deep copy of val
        try {
            oldDevice = (DBPersistent) CtiUtilities.copyObject(val);

            Transaction t = Transaction.createTransaction(Transaction.DELETE_PARTIAL, ((DBPersistent) val));

            val = (ICapBankController)t.execute();
        } catch (Exception e) {
            CTILogger.error(e);
            CTILogger.info("*** An exception occured when trying to change type of " + val + ", action aborted.");

            return val;
        }

        // create a brand new DeviceBase object
        val = (ICapBankController)DeviceFactory.createDevice(type);

        // set all the device specific stuff here
        ((DeviceBase) val).setDevice(((DeviceBase) oldDevice).getDevice());

        ((DeviceBase) val).setPAOName(((DeviceBase) oldDevice).getPAOName());

        ((DeviceBase) val).setDisableFlag(((DeviceBase) oldDevice).getPAODisableFlag());

        ((DeviceBase) val).setPAOStatistics(((DeviceBase) oldDevice).getPAOStatistics());

        // remove then add the new elements for PAOExclusion
        ((DeviceBase) val).getPAOExclusionVector().removeAllElements();
        ((DeviceBase) val).getPAOExclusionVector().addAll(((DeviceBase) oldDevice).getPAOExclusionVector());

        if (val instanceof RemoteBase && oldDevice instanceof RemoteBase) {
            RemoteBase newBase = (RemoteBase) val;
            RemoteBase oldBase = (RemoteBase) oldDevice;

            newBase.getDeviceDirectCommSettings().setPortID(oldBase.getDeviceDirectCommSettings().getPortID());
            newBase.setIpAddress(oldBase.getIpAddress());
            newBase.setPort(oldBase.getPort());
        }

        if (val instanceof IDeviceMeterGroup && oldDevice instanceof IDeviceMeterGroup) {
            ((IDeviceMeterGroup) val).setDeviceMeterGroup(((IDeviceMeterGroup) oldDevice).getDeviceMeterGroup());
        }

        if (val instanceof TwoWayDevice && oldDevice instanceof TwoWayDevice) {
            ((TwoWayDevice) val).setDeviceScanRateMap(((TwoWayDevice) oldDevice).getDeviceScanRateMap());
        }

        if (val instanceof CapBankController) {
            ((CapBankController) val).setDeviceCBC(((CapBankController) oldDevice).getDeviceCBC());
        }

        // support for the 702x devices - wasn't in the old device change type panel
        if (val instanceof CapBankController702x) {
            CapBankController702x device702 = (CapBankController702x) val;
            DeviceCBC deviceCBC = ((CapBankController702x) oldDevice).getDeviceCBC();
            DeviceAddress deviceAddress = ((CapBankController702x) oldDevice).getDeviceAddress();
            device702.setDeviceAddress(deviceAddress);
            device702.setDeviceCBC(deviceCBC);
        }

        if (val instanceof CapBankControllerDNP) {
            CapBankControllerDNP device702 = (CapBankControllerDNP) val;
            DeviceCBC deviceCBC = ((CapBankControllerDNP) oldDevice).getDeviceCBC();
            DeviceAddress deviceAddress = ((CapBankControllerDNP) oldDevice).getDeviceAddress();
            device702.setDeviceAddress(deviceAddress);
            device702.setDeviceCBC(deviceCBC);
        }

        try {
            Transaction t2 = Transaction.createTransaction(Transaction.ADD_PARTIAL, ((DBPersistent) val));

            val = (ICapBankController)t2.execute();

        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);

        }
        return val;
    }

    /**
     * Returns true for all Disconnect MCTs and All MCTs(400series) with disconnect collar defined.
     */
    public static boolean isDisconnectMCTOrHasCollar(SimpleDevice yukonDevice) {
        if (isDisconnectMCT(yukonDevice.getDeviceType())) {
            return true;
        }
        if (isMCT410(yukonDevice.getDeviceType()) || yukonDevice.getDeviceType() == PaoType.MCT420FL) {
            return DeviceMCT400Series.hasExistingDisconnectAddress(yukonDevice.getDeviceId());
        }
        return false;
    }

    public static boolean isCBCTwoWay(PaoType deviceType) {
        switch (deviceType) {
        case DNP_CBC_6510:
        case CBC_7020:
        case CBC_7022:
        case CBC_7023:
        case CBC_7024:
        case CBC_8020:
        case CBC_8024:
        case CBC_DNP:
            return true;

        default:
            return false;
        }
    }

    public static boolean isCBCOneWay(PaoType deviceType) {
        switch (deviceType) {
        case CBC_7010:
        case CBC_7011:
        case CBC_7012:
        case CBC_EXPRESSCOM:
        case CAPBANKCONTROLLER:
            return true;

        default:
            return false;
        }
    }

    public static boolean is702xDevice(PaoType deviceType) {
        switch (deviceType) {
        case CBC_7020:
        case CBC_7022:
        case CBC_7023:
        case CBC_7024:
            return true;

        default:
            return false;
        }
    }

    public static boolean is701xDevice(PaoType deviceType) {
        switch (deviceType) {
        case CBC_7010:
        case CBC_7011:
        case CBC_7012:
            return true;

        default:
            return false;
        }
    }

    public static boolean isTcpPort(int portId) {
        PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
        return paoDao.getYukonPao(portId).getPaoIdentifier().getPaoType() == PaoType.TCPPORT;
    }
}