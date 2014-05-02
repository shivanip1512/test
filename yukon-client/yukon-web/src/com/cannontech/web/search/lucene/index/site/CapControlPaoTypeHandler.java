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
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.capcontrol.streamable.Area;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.Feeder;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.message.capcontrol.streamable.SubStation;
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
        
        // Non-orphaned areas, substations and subbusses have view pages that would be better to use.  We need to
        // know if they are orphans or not though for this to work.
        // TODO Refactor PaoTypeHandler to support multiple pages per pao.
        int areaId = cache.getParentAreaId(paoId);
        boolean orphan = areaId <= CtiUtilities.NONE_ZERO_ID;
        
        if (paoType == PaoType.CAP_CONTROL_AREA || paoType == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            
            builder.pageType(PageType.USER_PAGE);
            builder.pageName("area");
            builder.path("/capcontrol/tier/substations?bc_areaId=" + paoId);
            builder.pageArgs(paoName);
            
        } else if (paoType == PaoType.CAP_CONTROL_SUBSTATION && !orphan) {
            
            Area area = cache.getArea(areaId);
            
            builder.pageType(PageType.USER_PAGE);
            builder.pageName("substation");
            builder.path("/capcontrol/tier/feeders?substationId=" + paoId);
            builder.pageArgs(paoName);
            builder.summaryArgs(area.getCcName());
            
        } else if (!orphan && (paoType == PaoType.CAP_CONTROL_SUBBUS 
                            || paoType == PaoType.CAP_CONTROL_FEEDER
                            || paoType == PaoType.CAPBANK)) {
            
            Area area = cache.getArea(areaId);
            SubStation substation = cache.getParentSubstation(paoId);
            
            builder.path("/capcontrol/tier/feeders?substationId=" + substation.getCcId());
            builder.pageArgs(paoName, substation.getCcName());
            
            if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
                
                builder.pageName("subbus");
                builder.summaryArgs(area.getCcName(), substation.getCcName());
                
            } else if (paoType == PaoType.CAP_CONTROL_FEEDER) {
                
                SubBus bus = cache.getParentSubBus(paoId);
                
                builder.pageName("feeder");
                builder.summaryArgs(area.getCcName(), substation.getCcName(), bus.getCcName());
                
            } else {
                
                SubBus bus = cache.getParentSubBus(paoId);
                Feeder feeder = cache.getParentFeeder(paoId);
                
                builder.pageName("capbank");
                builder.summaryArgs(area.getCcName(), substation.getCcName(), bus.getCcName(), feeder.getCcName()); 
                
            } 
            
        } else if (paoType.isCbc()) {
            
            PaoIdentifier bank = capbankDao.findCapBankByCbc(paoId);
            if (bank != null) {
                areaId = cache.getParentAreaId(bank.getPaoId());
                
                if (areaId > CtiUtilities.NONE_ZERO_ID) {
                    
                    Area area = cache.getArea(areaId);
                    SubStation substation = cache.getParentSubstation(bank.getPaoId());
                    SubBus bus = cache.getParentSubBus(bank.getPaoId());
                    Feeder feeder = cache.getParentFeeder(bank.getPaoId());
                    CapBankDevice cachedBank = cache.getCapBankDevice(bank.getPaoId());
                    
                    builder.pageName("cbc");
                    builder.path("/capcontrol/tier/feeders?substationId=" + substation.getCcId());
                    builder.pageArgs(paoName, substation.getCcName());
                    builder.summaryArgs(area.getCcName(), 
                            substation.getCcName(), 
                            bus.getCcName(), 
                            feeder.getCcName(),
                            cachedBank.getCcName());
                
                } else {
                    buildEditorPage(builder, paoType, paoId, paoName);
                }
            } else {
                buildEditorPage(builder, paoType, paoId, paoName);
            }
        } else {
            buildEditorPage(builder, paoType, paoId, paoName);
        }
//        else if (!orphan && paoType == PaoType.LOAD_TAP_CHANGER) {
            // TODO Link to IVVC page for regs
//            SubBus subbus = cache.letstrackregulatorsalready(paoId);
//            builder.pageName("ivvc.busView");
//            builder.path("/capcontrol/ivvc/bus/detail?subBusId=" + subbus.getCcId());
//            // subBusName
//            builder.pageArgs(subbus.getCcName());
//        }
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
