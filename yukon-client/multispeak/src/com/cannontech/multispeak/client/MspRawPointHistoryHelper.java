package com.cannontech.multispeak.client;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;

public class MspRawPointHistoryHelper {

    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoAuthorizationService paoAuthorizationService;

    /**
     * Returns a list of paObjects for PaoTag.LM_PROGRAM that user has permission to access.
     * @return
     */
    public List<LiteYukonPAObject> getAuthorizedProgramsList(LiteYukonUser user) {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
        List<LiteYukonPAObject> programs = Lists.newArrayList();
        for (PaoType paoType : paoTypes) {
            List<LiteYukonPAObject> toFilter = paoDao.getLiteYukonPAObjectByType(paoType);
            programs.addAll(paoAuthorizationService.filterAuthorized(user, toFilter, Permission.LM_VISIBLE));
        }
        return programs;
    }

}
