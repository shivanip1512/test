package com.cannontech.capcontrol.dao.providers;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.dao.providers.fields.VoltageRegulatorFields;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.service.PaoProviderTableEnum;
import com.cannontech.common.pao.service.impl.PaoTypeProvider;

public class VoltageRegulatorProvider implements PaoTypeProvider<VoltageRegulatorFields> {
    private VoltageRegulatorDao voltageRegulatorDao;
    
    @Override
    public PaoProviderTableEnum getSupportedTable() {
    	return PaoProviderTableEnum.REGULATOR;
    };

    @Override
    public Class<VoltageRegulatorFields> getRequiredFields() {
        return VoltageRegulatorFields.class;
    }

    @Override
    public void handleCreation(PaoIdentifier paoIdentifier, VoltageRegulatorFields fields) {
        voltageRegulatorDao.add(paoIdentifier.getPaoId(), fields.getKeepAliveTimer(), fields.getKeepAliveConfig());
    }
    
    @Override
    public void handleUpdate(PaoIdentifier paoIdentifier, VoltageRegulatorFields fields) {
    	voltageRegulatorDao.update(paoIdentifier.getPaoId(), fields.getKeepAliveTimer(), fields.getKeepAliveConfig());
    }
    
    @Override
    public void handleDeletion(PaoIdentifier paoIdentifier) {
    	voltageRegulatorDao.delete(paoIdentifier.getPaoId());
    };

    @Autowired
    public void setVoltageRegulatorDao(VoltageRegulatorDao voltageRegulatorDao) {
        this.voltageRegulatorDao = voltageRegulatorDao;
    }
}
