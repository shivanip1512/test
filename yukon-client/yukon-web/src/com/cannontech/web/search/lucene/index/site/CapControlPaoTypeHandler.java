package com.cannontech.web.search.lucene.index.site;

import java.sql.SQLException;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.cbc.cache.CapControlCache;
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
    @Autowired private CapControlCache cache;
    @Autowired private CapbankDao capbankDao;

    @Override
    public Set<PaoType> getTypesHandled() {
        return PaoType.getCapControlTypes();
    }

    @Override
    public void buildDocument(DocumentBuilder builder, YukonResultSet rs, PaoIdentifier paoIdentifier)
            throws SQLException {
        
        int paoId = paoIdentifier.getPaoId();
        String paoName = rs.getString("paoName");
        PaoType paoType = paoIdentifier.getPaoType();

        builder.module(SiteModule.CAPCONTROL.getName());
        builder.pageType(PageType.LEGACY);
        
        if (paoType == PaoType.CAP_CONTROL_AREA || paoType == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            
            builder.pageType(PageType.USER_PAGE);
            builder.pageName("area");
            builder.path("/capcontrol/tier/substations?bc_areaId=" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAP_CONTROL_SUBSTATION) {

            builder.pageName("substation");
            builder.path("/vv/substation/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
            builder.pageName("bus");
            builder.path("/vv/bus/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAP_CONTROL_FEEDER) {
            builder.pageName("feeder");
            builder.path("/vv/feeder/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAPBANK) {
            builder.pageName("bank");
            builder.path("/vv/bank/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType.isCbc()) {
            builder.pageName("cbc");
            builder.path("/vv/cbc/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType.isRegulator()) {
            builder.pageName("regulator.VIEW");
            builder.path("/capcontrol/regulators/" + paoId);
            builder.pageArgs(paoName);
        } else {
            buildEditorPage(builder, paoType, paoId, paoName);
        }
    }
    
    private void buildEditorPage(DocumentBuilder builder, PaoType paoType, int paoId, String paoName) {
        builder.pageName("edit." + paoType.name());
        builder.path("/editor/cbcBase.jsf?type=2&itemid=" + paoId);
        builder.pageArgs(paoName);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.CAP_CONTROL_ACCESS, user)) {
            return new TermQuery(new Term("module", SiteModule.CAPCONTROL.getName()));
        }
        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user, PaoIdentifier paoIdentifier) {
        return true;
    }
}
