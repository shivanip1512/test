package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.dao.providers.fields.VoltageRegulatorFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.service.providers.BaseCreationProvider;

public class VoltageRegulatorCreationProvider extends BaseCreationProvider<VoltageRegulatorFields> {
    private VoltageRegulatorDao voltageRegulatorDao;
    private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    public float getOrder() {
        return 1;
    }

    @Override
    public boolean isTypeSupported(PaoType paoType) {
        boolean isSupported = paoDefinitionDao.isTagSupported(paoType, PaoTag.VOLTAGE_REGULATOR);
        return isSupported;
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
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}
