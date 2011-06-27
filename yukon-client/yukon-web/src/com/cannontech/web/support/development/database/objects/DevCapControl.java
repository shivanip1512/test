package com.cannontech.web.support.development.database.objects;

import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.google.common.collect.Lists;

public class DevCapControl extends DevObject {
    private int numAreas = 1;
    private int numSubs = 1;
    private int numSubBuses = 1;
    private int numFeeders = 1;
    private int numCapBanks = 3;
    private int numCBCs = 3;
    private int numRegulators = 10;
    private int offset = 0;
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
    
    public int getNumAreas() {
        return numAreas;
    }

    public void setNumAreas(int numAreas) {
        this.numAreas = numAreas;
    }

    public int getNumSubs() {
        return numSubs;
    }

    public void setNumSubs(int numSubs) {
        this.numSubs = numSubs;
    }

    public int getNumSubBuses() {
        return numSubBuses;
    }

    public void setNumSubBuses(int numSubBuses) {
        this.numSubBuses = numSubBuses;
    }

    public int getNumFeeders() {
        return numFeeders;
    }

    public void setNumFeeders(int numFeeders) {
        this.numFeeders = numFeeders;
    }

    public int getNumCapBanks() {
        return numCapBanks;
    }

    public void setNumCapBanks(int numCapBanks) {
        this.numCapBanks = numCapBanks;
    }

    public int getNumCBCs() {
        return numCBCs;
    }

    public void setNumCBCs(int numCBCs) {
        this.numCBCs = numCBCs;
    }

    public int getNumRegulators() {
        return numRegulators;
    }

    public void setNumRegulators(int numRegulators) {
        this.numRegulators = numRegulators;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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

    private int getNumCBCsToCreate() {
        int num = 0;
        for (DevPaoType type: cbcTypes) {
            if (type.isCreate()) {
                num += numCBCs;
            }
        }
        return num;
    }
    
    private int getNumRegulatorsToCreate() {
        int num = 0;
        for (DevPaoType type: regulatorTypes) {
            if (type.isCreate()) {
                num += numRegulators;
            }
        }
        return num;
    }
    
    @Override
    public int getTotal() {
        int numTotalSubs = numAreas * numSubs;
        int numTotalSubBuses = numTotalSubs * numSubBuses;
        int numTotalFeeders = numTotalSubBuses * numFeeders;
        int numTotalCapBanks = numTotalFeeders * numCapBanks;
        int numTotalCBCs = numTotalCapBanks * getNumCBCsToCreate();
        int numTotalRegulators = getNumRegulatorsToCreate();
        int total = numAreas + numTotalSubs + numTotalSubBuses + numTotalFeeders + numTotalCapBanks + numTotalCBCs + numTotalRegulators;
        return total;
    }
}
