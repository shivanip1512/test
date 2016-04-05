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
        String paoName = rs.getString("paoName");
        PaoType paoType = paoIdentifier.getPaoType();

        builder.module(SiteModule.CAPCONTROL.getName());
        builder.pageType(PageType.LEGACY);
        
        if (paoType == PaoType.CAP_CONTROL_AREA || paoType == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            
            builder.pageType(PageType.USER_PAGE);
            builder.pageName("area");
            builder.path("/capcontrol/areas/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAP_CONTROL_SUBSTATION) {

            builder.pageName("substation");
            builder.path("/capcontrol/substations/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
            builder.pageName("bus");
            builder.path("/capcontrol/buses/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAP_CONTROL_FEEDER) {
            builder.pageName("feeder");
            builder.path("/capcontrol/feeders/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType == PaoType.CAPBANK) {
            builder.pageName("bank");
            builder.path("/capcontrol/capbanks/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType.isCbc()) {
            builder.pageName("cbc");
            builder.path("/capcontrol/cbc/" + paoId);
            builder.pageArgs(paoName);

        } else if (paoType.isRegulator()) {
            builder.pageName("regulator.VIEW");
            builder.path("/capcontrol/regulators/" + paoId);
            builder.pageArgs(paoName);
        } 
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
