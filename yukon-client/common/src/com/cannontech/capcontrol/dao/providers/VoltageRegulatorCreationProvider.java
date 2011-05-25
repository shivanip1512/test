package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.dao.providers.fields.VoltageRegulatorFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.providers.BaseCreationProvider;

public class VoltageRegulatorCreationProvider extends BaseCreationProvider<VoltageRegulatorFields> {

    private VoltageRegulatorDao voltageRegulatorDao;
    
    @Override
    public float getOrder() {
        return 1;
    }

    @Override
    public boolean isTypeSupported(PaoType paoType) {
        return paoType == PaoType.LOAD_TAP_CHANGER ||
            paoType == PaoType.GANG_OPERATED ||
            paoType == PaoType.PHASE_OPERATED;
    }

    @Override
    public Class<VoltageRegulatorFields> getRequiredFields() {
        return VoltageRegulatorFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, VoltageRegulatorFields fields) {
        voltageRegulatorDao.add(paoIdentifier.getPaoId(), fields.getKeepAliveTimer(), fields.getKeepAliveConfig());
    }

    @Autowired
    public void setVoltageRegulatorDao(VoltageRegulatorDao voltageRegulatorDao) {
        this.voltageRegulatorDao = voltageRegulatorDao;
    }
}
