package com.cannontech.web.search.lucene.index.site;

import java.sql.SQLException;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.core.authorization.service.PaoAuthorizationService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.search.lucene.index.site.PaoPageIndexBuilder.PaoTypeHandler;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class DrPaoTypeHandler implements PaoTypeHandler {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired protected PaoAuthorizationService paoAuthorizationService;

    private final Set<PaoType> controlAreaPaoTypes;
    private final Set<PaoType> scenarioPaoTypes;
    private final Set<PaoType> programPaoTypes;
    private final Set<PaoType> loadGroupPaoTypes;

    private final Set<PaoType> allTypes;

    @Autowired
    public DrPaoTypeHandler(PaoDefinitionDao paoDefinitionDao) {
        controlAreaPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_CONTROL_AREA);
        scenarioPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_SCENARIO);
        programPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
        loadGroupPaoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_GROUP);

        Builder<PaoType> builder = ImmutableSet.builder();
        builder.addAll(controlAreaPaoTypes);
        builder.addAll(scenarioPaoTypes);
        builder.addAll(programPaoTypes);
        builder.addAll(loadGroupPaoTypes);
        allTypes = builder.build();
    }

    @Override
    public Set<PaoType> getTypesHandled() {
        return allTypes;
    }

    @Override
    public void buildDocument(DocumentBuilder builder, YukonResultSet rs, PaoIdentifier paoIdentifier)
            throws SQLException {
        int paoId = paoIdentifier.getPaoId();
        PaoType paoType = paoIdentifier.getPaoType();

        builder.module(SiteModule.DR.getName());
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
        builder.pageName(pageName);
        builder.path(path);

        builder.pageArgs(rs.getString("paoName"));
        // builder.summaryArgs();
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        if (!rolePropertyDao.checkRole(YukonRole.DEMAND_RESPONSE, user)) {
            return new TermQuery(new Term("module", SiteModule.DR.getName()));
        }
        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user, PaoIdentifier paoIdentifier) {
        return paoAuthorizationService.isAuthorized(user, Permission.LM_VISIBLE, paoIdentifier);
    }
}
