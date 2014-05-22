package com.cannontech.database.data.capcontrol;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.pao.YukonPAObject;

public final class CCYukonPAOFactory {

    public final static YukonPAObject createCapControlPAO(PaoType paoType) {
        YukonPAObject retBase = null;

        switch (paoType) {
        case CAP_CONTROL_SPECIAL_AREA:
            retBase = new CapControlSpecialArea();
            break;
        case CAP_CONTROL_AREA:
            retBase = new CapControlArea();
            break;

        case CAP_CONTROL_SUBSTATION:
            retBase = new CapControlSubstation();
            break;
        case CAP_CONTROL_SUBBUS:
            retBase = new CapControlSubBus();
            break;

        case CAP_CONTROL_FEEDER:
            retBase = new CapControlFeeder();
            break;

        case LOAD_TAP_CHANGER:
            retBase = new VoltageRegulator(PaoType.LOAD_TAP_CHANGER);
            break;

        case GANG_OPERATED:
            retBase = new VoltageRegulator(PaoType.GANG_OPERATED);
            break;

        case PHASE_OPERATED:
            retBase = new VoltageRegulator(PaoType.PHASE_OPERATED);
            break;

        default:
            retBase = DeviceFactory.createDevice(paoType);
        }

        return retBase;
    }
}