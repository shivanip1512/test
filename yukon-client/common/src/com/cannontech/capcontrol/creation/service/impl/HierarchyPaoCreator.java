package com.cannontech.capcontrol.creation.service.impl;

import com.cannontech.capcontrol.creation.model.HierarchyImportData;
import com.cannontech.common.pao.pojo.CompleteCapBank;
import com.cannontech.common.pao.pojo.CompleteCapControlArea;
import com.cannontech.common.pao.pojo.CompleteCapControlFeeder;
import com.cannontech.common.pao.pojo.CompleteCapControlSpecialArea;
import com.cannontech.common.pao.pojo.CompleteCapControlSubstation;
import com.cannontech.common.pao.pojo.CompleteCapControlSubstationBus;
import com.cannontech.common.pao.pojo.CompleteYukonPaObject;

/*package*/ enum HierarchyPaoCreator {
    CAP_CONTROL_AREA {
        @Override
        public CompleteYukonPaObject getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapControlArea pao = new CompleteCapControlArea();
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    CAP_CONTROL_SPECIAL_AREA {
        @Override
        public CompleteYukonPaObject getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapControlSpecialArea pao = new CompleteCapControlSpecialArea();
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    CAP_CONTROL_SUBSTATION {
        @Override
        public CompleteYukonPaObject getCompleteYukonPao(HierarchyImportData data) {
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
        public CompleteYukonPaObject getCompleteYukonPao(HierarchyImportData data) {
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
        public CompleteYukonPaObject getCompleteYukonPao(HierarchyImportData data) {
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
        public CompleteYukonPaObject getCompleteYukonPao(HierarchyImportData data) {
            CompleteCapBank pao = new CompleteCapBank();
            if (data.getMapLocationId() != null) {
                pao.setMapLocationId(data.getMapLocationId());
            }
            populateYukonPaObjectData(pao, data);
            return pao;
        }
    },
    ;
    
    public abstract CompleteYukonPaObject getCompleteYukonPao(HierarchyImportData data);
    
    private static void populateYukonPaObjectData(CompleteYukonPaObject pao, HierarchyImportData data) {
        pao.setPaoName(data.getName());
        
        if (data.getDescription() != null) {
            pao.setDescription(data.getDescription());
        }
        if (data.isDisabled() != null) {
            pao.setDisabled(data.isDisabled());
        }
    }
}
