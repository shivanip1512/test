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
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.search.lucene.index.PageType;
import com.cannontech.web.search.lucene.index.site.PaoPageIndexBuilder.PaoTypeHandler;

public class CapControlPaoTypeHandler implements PaoTypeHandler {
    @Autowired private RolePropertyDao rolePropertyDao;

    @Override
    public Set<PaoType> getTypesHandled() {
        return PaoType.getCapControlTypes();
    }

    @Override
    public void buildDocument(DocumentBuilder builder, YukonResultSet rs, PaoIdentifier paoIdentifier)
            throws SQLException {
        int paoId = paoIdentifier.getPaoId();

        builder.pageType(PageType.LEGACY);
        String paoName = rs.getString("paoName");
        builder.module(SiteModule.CAPCONTROL.getName());
        PaoType paoType = paoIdentifier.getPaoType();
        builder.pageName("edit." + paoType.name());
        builder.path("/editor/cbcBase.jsf?type=2&itemid=" + paoId);
        builder.pageArgs(paoName);

        // Non-orphaned areas, substations and subbusses have view pages that would be better to use.  We need to
        // know if they are orphans or not though for this to work.
        /*
        if (paoType == PaoType.CAP_CONTROL_AREA || paoType == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            builder.pageName("area");
            builder.path("/capcontrol/tier/substations?isSpecialArea="
                    + (paoType == PaoType.CAP_CONTROL_SPECIAL_AREA) + "&bc_areaId=" + paoId);
            // areaName
            builder.pageArgs(paoName);
        } else if (paoType == PaoType.CAP_CONTROL_SUBSTATION) {
            builder.pageName("substation");
            builder.path("/capcontrol/tier/feeders?isSpecialArea=false&substationId=" + paoId);
            // substationName
            builder.pageArgs(paoName);
        } else if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
            builder.pageName("ivvc.busView");
            builder.path("/capcontrol/ivvc/bus/detail?subBusId=" + paoId + "&isSpecialArea=false");
            // subBusName
            builder.pageArgs(paoName);
        }
         */
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user)) {
            return new TermQuery(new Term("module", SiteModule.CAPCONTROL.getName()));
        }
        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user, PaoIdentifier paoIdentifier) {
        return true;
    }
}
