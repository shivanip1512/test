package com.cannontech.web.search.lucene.index.site;

import java.sql.SQLException;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.search.lucene.index.site.PaoPageIndexBuilder.PaoTypeHandler;
import com.google.common.collect.ImmutableSet;

public class CommChannelPaoTypeHandler implements PaoTypeHandler {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoLoadingService paoLoadingService;

    private final static Set<PaoType> allTypes = PaoType.getPortTypes();
    private final static Set<YukonRole> allowedRoles = ImmutableSet.of(YukonRole.DEVICE_MANAGEMENT);

    @Override
    public Set<PaoType> getTypesHandled() {
        return allTypes;
    }

    @Override
    public void buildDocument(DocumentBuilder builder, YukonResultSet rs, PaoIdentifier paoIdentifier) throws SQLException {
        int paoId = paoIdentifier.getPaoId();

        builder.module(SiteModule.OPERATOR.getName());
        
        String path = "/stars/device/commChannel/" + paoId;
        String pageName = "commChannelDetail";
        
        if (paoIdentifier.getPaoType().equals(PaoType.RFN_1200)) {
            path = "/stars/device/rfn1200/" + paoId;
            pageName = "rfn1200Detail";
        }
        
        builder.pageName(pageName);
        builder.path(path);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(paoIdentifier);
        String deviceName = displayablePao.getName();

        builder.pageArgs(deviceName);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {

        boolean allowed = false;
        for (YukonRole role : allowedRoles) {
            if (rolePropertyDao.checkRole(role, user)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            return new TermQuery(new Term("module", SiteModule.OPERATOR.getName()));
        }

        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user, PaoIdentifier paoIdentifier) {
        return true;
    }
}
