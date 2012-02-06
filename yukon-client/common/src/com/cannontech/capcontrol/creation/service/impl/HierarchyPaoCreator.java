package com.cannontech.capcontrol.creation.service.impl;

import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.common.pao.model.CompleteCapBank;
import com.cannontech.common.pao.model.CompleteCapControlArea;
import com.cannontech.common.pao.model.CompleteCapControlFeeder;
import com.cannontech.common.pao.model.CompleteCapControlSpecialArea;
import com.cannontech.common.pao.model.CompleteCapControlSubstation;
import com.cannontech.common.pao.model.CompleteCapControlSubstationBus;
import com.cannontech.common.pao.model.CompleteYukonPao;

/*package*/ enum HierarchyPaoCreator {
    CAP_CONTROL_AREA {
        @Override
        public CompleteYukonPao getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapControlArea pao = new CompleteCapControlArea();
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    CAP_CONTROL_SPECIAL_AREA {
        @Override
        public CompleteYukonPao getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapControlSpecialArea pao = new CompleteCapControlSpecialArea();
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    CAP_CONTROL_SUBSTATION {
        @Override
        public CompleteYukonPao getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapControlSubstation pao = new CompleteCapControlSubstation();
            if (data.getMapLocationId() != null) {
                pao.setMapLocationId(data.getMapLocationId());
            }
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    CAP_CONTROL_SUBBUS {
        @Override
        public CompleteYukonPao getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapControlSubstationBus pao = new CompleteCapControlSubstationBus();
            if (data.getMapLocationId() != null) {
                pao.setMapLocationId(data.getMapLocationId());
            }
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    CAP_CONTROL_FEEDER {
        @Override
        public CompleteYukonPao getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapControlFeeder pao = new CompleteCapControlFeeder();
            if (data.getMapLocationId() != null) {
                pao.setMapLocationId(data.getMapLocationId());
            }
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    CAPBANK {
        @Override
        public CompleteYukonPao getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapBank pao = new CompleteCapBank();
            if (data.getMapLocationId() != null) {
                pao.setMapLocationId(data.getMapLocationId());
            }
            if (data.getBankOpState() != null) {
                pao.setOperationalState(data.getBankOpState());
            }
            if (data.getCapBankSize() != null) {
                pao.setBankSize(data.getCapBankSize());
            }
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    ;
    
    public abstract CompleteYukonPao getCompleteYukonPao(HierarchyImportData data);
    
    private static void populateYukonPaObjectData(CompleteYukonPao pao, HierarchyImportData data) {
        pao.setPaoName(data.getName());
        
        if (data.getDescription() != null) {
            pao.setDescription(data.getDescription());
        }
        if (data.isDisabled() != null) {
            pao.setDisabled(data.isDisabled());
        }
    }
}
