package com.cannontech.database.data.device.lm;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.pao.YukonPAObject;

public final class LMFactory {

    public final static YukonPAObject createLoadManagement(PaoType paoType) {

        YukonPAObject retLm = null;

        switch (paoType) {
        case LM_GROUP_EMETCON:
            retLm = new LMGroupEmetcon();
            break;
        case LM_GROUP_DIGI_SEP:
            retLm = new LMGroupDigiSep();
            break;
        case LM_GROUP_ECOBEE:
            retLm = new LMGroupEcobee();
            break;
        case LM_GROUP_HONEYWELL:
            retLm = new LMGroupHoneywell();
            break;
        case LM_GROUP_ITRON:
            retLm = new LMGroupItron();
            break;
        case LM_GROUP_NEST:
            retLm = new LMGroupNest();
            break;
        case LM_GROUP_VERSACOM:
            retLm = new LMGroupVersacom();
            break;
        case LM_GROUP_EXPRESSCOMM:
            retLm = new LMGroupPlcExpressCom();
            break;
        case LM_GROUP_RFN_EXPRESSCOMM:
            retLm = new LMGroupRfnExpressCom();
            break;
        case LM_GROUP_POINT:
            retLm = new LMGroupPoint();
            break;
        case LM_GROUP_RIPPLE:
            retLm = new LMGroupRipple();
            break;
        case LM_GROUP_MCT:
            retLm = new LMGroupMCT();
            break;
        case LM_GROUP_SA305:
            retLm = new LMGroupSA305();
            break;
        case LM_GROUP_SA205:
            retLm = new LMGroupSA205();
            break;
        case LM_GROUP_SADIGITAL:
            retLm = new LMGroupSADigital();
            break;
        case LM_GROUP_GOLAY:
            retLm = new LMGroupGolay();
            break;
        case MACRO_GROUP:
            retLm = new MacroGroup();
            break;
        case LM_DIRECT_PROGRAM:
            retLm = new LMProgramDirect();
            break;
        case LM_SEP_PROGRAM:
            retLm = new LmProgramSep();
            break;
        case LM_ECOBEE_PROGRAM:
            retLm = new LmProgramEcobee();
            break;
        case LM_HONEYWELL_PROGRAM:
            retLm = new LmProgramHoneywell();
            break;
        case LM_ITRON_PROGRAM:
            retLm = new LmProgramItron();
            break;
        case LM_NEST_PROGRAM:
            retLm = new LmProgramNest();
            break;
        case LM_CURTAIL_PROGRAM:
            retLm = new LMProgramCurtailment();
            break;
        case LM_ENERGY_EXCHANGE_PROGRAM:
            retLm = new LMProgramEnergyExchange();
            break;
        case LM_CONTROL_AREA:
            retLm = new LMControlArea();
            break;
        case LM_SCENARIO:
            retLm = new LMScenario();
            break;
        }

        if (retLm instanceof DeviceBase) {
            ((DeviceBase) retLm).setDisableFlag(new Character('N'));
            ((DeviceBase) retLm).getDevice().setAlarmInhibit(new Character('N'));
            ((DeviceBase) retLm).getDevice().setControlInhibit(new Character('N'));
        }

        return retLm;
    }
}
