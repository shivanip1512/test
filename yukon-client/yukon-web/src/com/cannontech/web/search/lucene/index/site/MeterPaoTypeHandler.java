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

public class MeterPaoTypeHandler implements PaoTypeHandler {
    
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private PaoLoadingService paoLoadingService;
    
    private final static Set<PaoType> allTypes = PaoType.getMeterTypes();
    
    // Any of these roles is sufficient to allow the user to see both electric and water meters.
    private final static Set<YukonRole> allowedRoles = ImmutableSet.of(
            YukonRole.METERING, 
            YukonRole.APPLICATION_BILLING, 
            YukonRole.SCHEDULER, 
            YukonRole.DEVICE_ACTIONS);
    
    @Override
    public Set<PaoType> getTypesHandled() {
        return allTypes;
    }
    
    @Override
    public void buildDocument(DocumentBuilder builder, YukonResultSet rs, PaoIdentifier paoIdentifier) throws SQLException {
        String addressOrSerialNumber = null;
        int paoId = paoIdentifier.getPaoId();
        
        builder.module(SiteModule.AMI.getName());
        String path = "/meter/home?deviceId=" + paoId;
        String pageName = "meterDetail.electric";
        if (paoIdentifier.getPaoType().isWaterMeter()) {
            path = "/meter/water/home?deviceId=" + paoId;
            pageName = "meterDetail.water";
        }
        if (paoIdentifier.getPaoType().isGasMeter()) {
            path = "/meter/gas/home?deviceId=" + paoId;
            pageName = "meterDetail.gas";
        }
        builder.pageName(pageName);
        builder.path(path);
        DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(paoIdentifier);
        String deviceName = displayablePao.getName();
        
        builder.pageArgs(deviceName);
        
        String meterNumber = rs.getString("meterNumber");
        if (paoIdentifier.getPaoType().isRfn()) {
            addressOrSerialNumber = rs.getString("serialNumber");
        } else if (paoIdentifier.getPaoType().isPlc()) {
            addressOrSerialNumber = rs.getString("address");
        }

        builder.summaryArgs(meterNumber, addressOrSerialNumber);
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
            return new TermQuery(new Term("module", SiteModule.AMI.getName()));
        }
        
        return null;
    }
    
    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user, PaoIdentifier paoIdentifier) {
        return true;
    }
    
}