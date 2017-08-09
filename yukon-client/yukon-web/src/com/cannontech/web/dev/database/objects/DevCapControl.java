package com.cannontech.web.dev.database.objects;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.development.model.DevObject;
import com.cannontech.development.model.DevPaoType;
import com.cannontech.simulators.RegulatorVoltageControlMode;
import com.google.common.collect.Lists;

public class DevCapControl extends DevObject {
    private Integer numAreas = 1;
    private Integer numSubs = 1;
    private Integer numSubBuses = 3;
    private Integer numFeeders = 2;
    private Integer numCapBanks = 4;
    private Integer numRegulators = 10;
    private Integer offset = 0;
    private boolean useIvvcControlType = true;
    private RegulatorVoltageControlMode regulatorVoltageControlMode;
    private DevPaoType cbcType = new DevPaoType(PaoType.CBC_8020);
    private List<DevPaoType> cbcTypes =
        Lists
            .newArrayList(new DevPaoType(PaoType.CBC_7010),
                          new DevPaoType(PaoType.CBC_7011),
                          new DevPaoType(PaoType.CBC_7012),
                          new DevPaoType(PaoType.CBC_7020),
                          new DevPaoType(PaoType.CBC_7022),
                          new DevPaoType(PaoType.CBC_7023),
                          new DevPaoType(PaoType.CBC_7024),
                          new DevPaoType(PaoType.CBC_8020),
                          new DevPaoType(PaoType.CBC_8024),
                          new DevPaoType(PaoType.CBC_FP_2800));
    private List<DevPaoType> regulatorTypes =
        Lists
        .newArrayList(new DevPaoType(PaoType.LOAD_TAP_CHANGER),
                      new DevPaoType(PaoType.GANG_OPERATED),
                      new DevPaoType(PaoType.PHASE_OPERATED));
    
    public Integer getNumAreas() {
        return numAreas;
    }

    public void setNumAreas(Integer numAreas) {
        this.numAreas = numAreas;
    }

    public Integer getNumSubs() {
        return numSubs;
    }

    public void setNumSubs(Integer numSubs) {
        this.numSubs = numSubs;
    }

    public Integer getNumSubBuses() {
        return numSubBuses;
    }

    public void setNumSubBuses(Integer numSubBuses) {
        this.numSubBuses = numSubBuses;
    }

    public Integer getNumFeeders() {
        return numFeeders;
    }

    public void setNumFeeders(Integer numFeeders) {
        this.numFeeders = numFeeders;
    }

    public Integer getNumCapBanks() {
        return numCapBanks;
    }

    public void setNumCapBanks(Integer numCapBanks) {
        this.numCapBanks = numCapBanks;
    }

    public Integer getNumRegulators() {
        return numRegulators;
    }

    public void setNumRegulators(Integer numRegulators) {
        this.numRegulators = numRegulators;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public DevPaoType getCbcType() {
        return cbcType;
    }

    public void setCbcType(DevPaoType cbcType) {
        this.cbcType = cbcType;
    }

    public List<DevPaoType> getCbcTypes() {
        return cbcTypes;
    }

    public void setCbcTypes(List<DevPaoType> cbcTypes) {
        this.cbcTypes = cbcTypes;
    }

    public List<DevPaoType> getRegulatorTypes() {
        return regulatorTypes;
    }

    public void setRegulatorTypes(List<DevPaoType> regulatorTypes) {
        this.regulatorTypes = regulatorTypes;
    }

    public boolean isUseIvvcControlType() {
        return useIvvcControlType;
    }

    public void setUseIvvcControlType(boolean useIvvcControlType) {
        this.useIvvcControlType = useIvvcControlType;
    }

    @Override
    public int getTotal() {
        int numTotalSubs = numAreas * numSubs; // 1
        int numTotalSubBuses = numTotalSubs * numSubBuses;
        int numTotalFeeders = numTotalSubBuses * numFeeders;
        int numTotalCapBanks = numTotalFeeders * numCapBanks;
        int numTotalCBCs = getCbcType() != null ? numTotalCapBanks : 0;
        int numTotalRegulators = isUseIvvcControlType() ? numTotalSubBuses * numTotalFeeders * 3 : 0;  // If IVVC, then create regulators count = total buses count in area * total feeders count * 3 regulators per zone  
        int total = numAreas + numTotalSubs + numTotalSubBuses + numTotalFeeders + numTotalCapBanks + numTotalCBCs + numTotalRegulators + 1 ;// 1 RTU;
        return total;
    }

    public RegulatorVoltageControlMode getRegulatorVoltageControlMode() {
        return regulatorVoltageControlMode;
    }

    public void setRegulatorVoltageControlMode(RegulatorVoltageControlMode regulatorVoltageControlMode) {
        this.regulatorVoltageControlMode = regulatorVoltageControlMode;
    }

}
