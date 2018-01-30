package com.cannontech.database.data.device;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
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
                paoType.isIon())
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