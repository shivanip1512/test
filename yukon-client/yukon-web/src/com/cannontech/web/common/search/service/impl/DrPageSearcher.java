package com.cannontech.web.common.search.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.common.search.result.Page;
import com.cannontech.web.search.lucene.index.IndexManager;
import com.cannontech.web.search.lucene.index.PaoIndexManager;
import com.google.common.collect.ImmutableList;

@Service
public class DrPageSearcher extends AbstractPageSearcher {
    @Autowired private PaoIndexManager indexManager;
    @Autowired private PaoPermissionService paoPermissionService;

    private final BooleanQuery isDrObjectQuery;
    private final Set<PaoType> controlAreaPaoTypes;
    private final Set<PaoType> scenarioPaoTypes;
    private final Set<PaoType> programPaoTypes;
    private final Set<PaoType> loadGroupPaoTypes;
    private final static String[] searchFields = new String[] {"pao"};

    @Autowired
    public DrPageSearcher(PaoDefinitionDao paoDefinitionDao) {
        controlAreaPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_CONTROL_AREA);
        scenarioPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_SCENARIO);
        programPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
        loadGroupPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_GROUP);
        isDrObjectQuery = new BooleanQuery(false);
        for (PaoType paoType : controlAreaPaoTypes) {
            isDrObjectQuery.add(new TermQuery(new Term("type", paoType.getDbString())), Occur.SHOULD);
        }
        for (PaoType paoType : scenarioPaoTypes) {
            isDrObjectQuery.add(new TermQuery(new Term("type", paoType.getDbString())), Occur.SHOULD);
        }
        for (PaoType paoType : programPaoTypes) {
            isDrObjectQuery.add(new TermQuery(new Term("type", paoType.getDbString())), Occur.SHOULD);
        }
        for (PaoType paoType : loadGroupPaoTypes) {
            isDrObjectQuery.add(new TermQuery(new Term("type", paoType.getDbString())), Occur.SHOULD);
        }
    }

    @Override
    protected IndexManager getIndexManager() {
        return indexManager;
    }

    @Override
    protected Query getRequiredQuery() {
        return isDrObjectQuery;
    }

    @Override
    protected String[] getSearchFields() {
        return searchFields;
    }

    @Override
    protected Page buildPage(Document document, LiteYukonUser user) {
        String paoName = document.get("pao");
        @SuppressWarnings("deprecation")
        PaoType paoType = PaoType.getForDbString(document.get("type"));
        int paoId = Integer.parseInt(document.get("paoid"));
        // For some reason paoPermissionService.hasPermission is returning UNKNOWN
//        PaoIdentifier paoIdentifier = new PaoIdentifier(paoId, paoType);
//        AuthorizationResponse hasPermission =
//                paoPermissionService.hasPermission(user, paoIdentifier, Permission.LM_VISIBLE);
//        if (hasPermission != AUTHORIZED) {
//            return null;
//        }

        String module = "dr";
        String path = null;
        String pageName = null;
        if (controlAreaPaoTypes.contains(paoType)) {
            path = "/dr/controlArea/detail?controlAreaId=" + paoId;
            pageName = "controlAreaDetail";
        } else if (scenarioPaoTypes.contains(paoType)) {
            path = "/dr/scenario/detail?scenarioId=" + paoId;
            pageName = "scenarioDetail";
        } else if (programPaoTypes.contains(paoType)) {
            path = "/dr/program/detail?programId=" + paoId;
            pageName = "programDetail";
        } else if (loadGroupPaoTypes.contains(paoType)) {
            path = "/dr/loadGroup/detail?loadGroupId=" + paoId;
            pageName = "loadGroupDetail";
        }

        List<String> arguments = ImmutableList.of(paoName);
        UserPage userPage = new UserPage(0, path, false, SiteModule.getByName(module), pageName, arguments,
            null, null);

        Page page = new Page(userPage, paoName);
        return page;
    }
}
