package com.cannontech.dr.loadgroup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.model.CompleteLoadGroupBase;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.dr.loadgroup.service.LoadGroupSetupService;

public class LoadGroupSetupServiceImpl implements LoadGroupSetupService {

    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private PaoDao paoDao;
    
    @Override
    public PaoIdentifier save(LoadGroupBase loadGroup) {
        CompleteLoadGroupBase completeLoadGroup = loadGroup.asCompletePao();

        if (loadGroup.getId() == null) {
            paoPersistenceService.createPaoWithDefaultPoints(completeLoadGroup, PaoType.LM_GROUP_METER_DISCONNECT);
        } else {
            paoPersistenceService.updatePao(completeLoadGroup);
        }
        return completeLoadGroup.getPaoIdentifier();
    }
    
    @Override
    public LoadGroupBase retrieve(int loadGroupId) {
        YukonPao pao = paoDao.getYukonPao(loadGroupId);
        CompleteLoadGroupBase completeLoadGroup = paoPersistenceService.retreivePao(pao, CompleteLoadGroupBase.class);
        LoadGroupBase loadGroup = LoadGroupBase.of(completeLoadGroup);
        return loadGroup;
    }

}
