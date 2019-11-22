package com.cannontech.common.dr.setup;

import com.cannontech.common.dr.gear.setup.fields.BeatThePeakGearFields;
import com.cannontech.common.dr.gear.setup.fields.EcobeeCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.EcobeeSetpointGearFields;
import com.cannontech.common.dr.gear.setup.fields.HoneywellCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.ItronCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.LatchingGearFields;
import com.cannontech.common.dr.gear.setup.fields.MasterCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.NestCriticalCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.NestStandardCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.NoControlGearFields;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.fields.RotationGearFields;
import com.cannontech.common.dr.gear.setup.fields.SepCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.SepTemperatureOffsetGearFields;
import com.cannontech.common.dr.gear.setup.fields.SimpleThermostatRampingGearFields;
import com.cannontech.common.dr.gear.setup.fields.SmartCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.TargetCycleGearFields;
import com.cannontech.common.dr.gear.setup.fields.ThermostatSetbackGearFields;
import com.cannontech.common.dr.gear.setup.fields.TimeRefreshGearFields;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.lm.GearControlMethod;

/**
 * Factory to return LM Model objects for a paoType
 */
public class LMModelFactory {
    public final static LoadGroupBase createLoadGroup(PaoType paoType) {

        LoadGroupBase loadGroup = null;

        switch (paoType) {
        case LM_GROUP_METER_DISCONNECT:
            loadGroup = new LoadGroupDisconnect();
            break;
        case LM_GROUP_HONEYWELL:
            loadGroup = new LoadGroupHoneywell();
            break;
        case LM_GROUP_ECOBEE:
            loadGroup = new LoadGroupEcobee();
            break;
        case LM_GROUP_NEST:
            loadGroup = new LoadGroupNest();
            break;
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_RFN_EXPRESSCOMM:
            loadGroup = new LoadGroupExpresscom();
            break;
        case LM_GROUP_VERSACOM:
            loadGroup = new LoadGroupVersacom();
            break;
        case LM_GROUP_DIGI_SEP:
            loadGroup = new LoadGroupDigiSep();
            break;
        case LM_GROUP_ITRON:
            loadGroup = new LoadGroupItron();
            break;
        case LM_GROUP_EMETCON:
            loadGroup = new LoadGroupEmetcon();
            break;
        case LM_GROUP_MCT:
            loadGroup = new LoadGroupMCT();
            break;
        case LM_GROUP_POINT:
            loadGroup = new LoadGroupPoint();
            break;

        }
        return loadGroup;
    }
    
    public final static ProgramGearFields createProgramGearFields(GearControlMethod controlMethod) {

        ProgramGearFields gearFields = null;

        switch (controlMethod) {
        case TrueCycle:
        case MagnitudeCycle:
        case SmartCycle: 
            gearFields = new SmartCycleGearFields();
            break;
        case TargetCycle:
            gearFields = new TargetCycleGearFields();
            break;
        case EcobeeCycle:
            gearFields = new EcobeeCycleGearFields();
            break;
        case EcobeeSetpoint:
            gearFields = new EcobeeSetpointGearFields();
            break;
        case HoneywellCycle:
            gearFields = new HoneywellCycleGearFields();
            break;
        case NestCriticalCycle:
            gearFields = new NestCriticalCycleGearFields();
            break;
        case ItronCycle:
            gearFields = new ItronCycleGearFields();
            break;
        case NestStandardCycle:
            gearFields = new NestStandardCycleGearFields();
            break;
        case SepCycle:
            gearFields = new SepCycleGearFields();
            break;
        case MasterCycle:
            gearFields = new MasterCycleGearFields();
            break;
        case TimeRefresh:
            gearFields = new TimeRefreshGearFields();
            break;
        case Rotation:
            gearFields = new RotationGearFields();
            break;
        case Latching:
            gearFields = new LatchingGearFields();
            break;
        case ThermostatRamping:
            gearFields = new ThermostatSetbackGearFields();
            break;
        case SimpleThermostatRamping:
            gearFields = new SimpleThermostatRampingGearFields();
            break;
        case SepTemperatureOffset:
            gearFields = new SepTemperatureOffsetGearFields();
            break;
        case BeatThePeak:
            gearFields = new BeatThePeakGearFields();
            break;
        case NoControl:
            gearFields = new NoControlGearFields();
            break;
        }
        return gearFields;
    }

    /**
     * Sets the appropriate copy class based upon the PaoType
     */
    public final static LMCopy createLoadGroupCopy(PaoType paoType) {
        LMCopy loadGroup = null;
        switch (paoType) {
        case LM_GROUP_EXPRESSCOMM:
        case LM_GROUP_EMETCON:
        case LM_GROUP_MCT:
        case LM_GROUP_RIPPLE:
        case LM_GROUP_VERSACOM:
            loadGroup = new LoadGroupCopy();
            break;
        default:
            loadGroup = new LMCopy();
            break;
        }
        return loadGroup;
    }

}
