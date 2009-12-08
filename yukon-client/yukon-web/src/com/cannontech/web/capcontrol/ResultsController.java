package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.dao.CapbankControllerDao;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.cbc.dao.FeederDao;
import com.cannontech.cbc.dao.LtcDao;
import com.cannontech.cbc.dao.SubstationBusDao;
import com.cannontech.cbc.dao.SubstationDao;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.cbc.web.CBCWebUtils;
import com.cannontech.cbc.web.CCSessionInfo;
import com.cannontech.cbc.web.ParentStringPrinter;
import com.cannontech.cbc.web.ParentStringPrinterFactory;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.CCEventLog;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ParamUtil;
import com.cannontech.web.capcontrol.models.ControlEventSet;
import com.cannontech.web.capcontrol.models.ResultRow;
import com.cannontech.web.lite.LiteBaseResults;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.cbc.StreamableCapObject;

@Controller
@RequestMapping("/search/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ResultsController {
    
    private FilterCacheFactory cacheFactory;
    private SubstationBusDao substationBusDao;
    private SubstationDao substationDao;
    private FeederDao feederDao;
    private CapbankDao capbankDao;
    private CapbankControllerDao cbcDao;
    private PaoDao paoDao;
    private ParentStringPrinterFactory printerFactory;
    private DateFormattingService dateFormattingService;
    private LtcDao ltcDao; 

    @RequestMapping
    public ModelAndView searchResults(HttpServletRequest request, LiteYukonUser user) {
        final ModelAndView mav = new ModelAndView();
        ParentStringPrinter psp = printerFactory.createParentStringPrinter(request);
        String srchCriteria = ParamUtil.getString(request, CCSessionInfo.STR_LAST_SEARCH, null);
        if( srchCriteria == null ) {
            CCSessionInfo ccSession = (CCSessionInfo) request.getSession().getAttribute("ccSession");
            srchCriteria = ccSession.getLastSearchCriteria();
        }
        
        mav.addObject("lastAreaKey", CCSessionInfo.STR_CC_AREA);
        mav.addObject("lastSubKey", CCSessionInfo.STR_SUBID);
        
        String label = srchCriteria;
        boolean orphan = true;
        
        LiteWrapper[] items = new LiteWrapper[0];
        List<LiteCapControlObject> ccObjects = null;

        if( CBCWebUtils.TYPE_ORPH_SUBSTATIONS.equals(srchCriteria) ) {
            ccObjects = substationDao.getOrphans();
            label = "Orphaned Substations";
        }
        else if( CBCWebUtils.TYPE_ORPH_SUBS.equals(srchCriteria) ) {
            ccObjects = substationBusDao.getOrphans();
            label = "Orphaned Substation Buses";
        }
        else if( CBCWebUtils.TYPE_ORPH_FEEDERS.equals(srchCriteria) ) {
            ccObjects = feederDao.getOrphans();
            label = "Orphaned Feeders";
        }
        else if( CBCWebUtils.TYPE_ORPH_BANKS.equals(srchCriteria) ) {
            ccObjects = capbankDao.getOrphans();
            label = "Orphaned CapBanks";
        }
        else if( CBCWebUtils.TYPE_ORPH_CBCS.equals(srchCriteria) ) {
            ccObjects = cbcDao.getOrphans();
            label = "Orphaned CBCs";
        }
        else if( CBCWebUtils.TYPE_ORPH_LTCS.equals(srchCriteria) ) {
            ccObjects = ltcDao.getOrphans();
            label = "Orphaned LTCs";
        }
        else {   
        	orphan = false;
            LiteBaseResults lbr = new LiteBaseResults();
            lbr.searchLiteObjects( srchCriteria );
            items = lbr.getFoundItems();
        }
        mav.addObject("label", label);
        
        List<ResultRow> rows = new ArrayList<ResultRow>();
        if(ccObjects == null) {
            for(LiteWrapper item : items) {
                ResultRow row = new ResultRow();
                row.setName(item.toString());
                row.setItemId(item.getItemID());
                row.setIsPaobject(item.getLiteType() == LiteTypes.YUKON_PAOBJECT);
                
                boolean isController = CBCUtils.isController(item.getItemID());
                row.setIsController(isController);
                
                boolean isPoint = item.getParentID() != CtiUtilities.NONE_ZERO_ID;
                String parentString = (!isPoint) ? psp.printPAO(item.getItemID()) :  psp.printPoint(item.getItemID());
                row.setParentString(parentString);
                row.setParentId(item.getParentID());
                row.setItemDescription(item.getDescription());
                row.setItemType(item.getItemType());
                rows.add(row);
            }
        } else {
            for(LiteCapControlObject item : ccObjects) {
                ResultRow row = new ResultRow();
                row.setName(item.getName());
                row.setItemId(item.getId());
                row.setIsPaobject(true);
                
                //If this is not a device, it is not a controller. Next call will catch it.
                int type = PAOGroups.getDeviceType(item.getType());
                boolean isController = CBCUtils.checkControllerByType(type);
                row.setIsController(isController);
                
                String parentString = ParentStringPrinter.ORPH_STRING;
                if (!orphan) {
                	parentString = psp.printPAO(item.getId());
                }
                row.setParentString(parentString);
                
                row.setParentId(item.getParentId());
                row.setItemDescription(item.getDescription());
                row.setItemType(item.getType());
                rows.add(row);
            }
        }
            
        mav.addObject("rows", rows);
        mav.addObject("resultsFound", rows.size());
        mav.setViewName("search/searchResults.jsp");
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        return mav;
    }

    @RequestMapping
    public ModelAndView recentControls(HttpServletRequest request, LiteYukonUser user) {
        final ModelAndView mav = new ModelAndView();
        CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
        YukonUserContext context = YukonUserContextUtils.getYukonUserContext(request);
        Integer MAX_DAYS_CNT = 7;   
        String[] strPaoids = ParamUtil.getStrings(request, "value");
        int dayCnt = ParamUtil.getInteger(request, "dayCnt", 1);
        List<ControlEventSet>  listOfEventSets = new ArrayList<ControlEventSet>();
        
        for( int i = 0; i < strPaoids.length; i++ ) {
            int id = Integer.parseInt( strPaoids[i] );
            StreamableCapObject cbcPAO = filterCapControlCache.getCapControlPAO(new Integer(id));

            if( cbcPAO != null ) {
                List<CCEventLog> events= CBCWebUtils.getCCEventsForPAO(new Long (id), cbcPAO.getCcType(), filterCapControlCache, dayCnt);
                for(CCEventLog event : events) {
                    String formattedTimestamp = dateFormattingService.format(event.getTimestamp(), DateFormatEnum.BOTH, context);
                    event.setFormattedTimestamp(formattedTimestamp);
                }
                ControlEventSet set = new ControlEventSet(id, events);
                String paoName = paoDao.getLiteYukonPAO(id).getPaoName();
                set.setPaoName(paoName);
                listOfEventSets.add(set);
            }
        }
        
        mav.addObject("dayCnt", new Integer(dayCnt));
        mav.addObject("MAX_DAYS_CNT", MAX_DAYS_CNT);
        mav.addObject("paoIdString", Arrays.toString( strPaoids));
        mav.addObject("listOfEventSets", listOfEventSets);
        mav.setViewName("search/recentControls.jsp");
        return mav;
    }
    
    @Autowired
    public void setParentStringPrinterFactory(ParentStringPrinterFactory printerFactory) {
        this.printerFactory = printerFactory;
    }
    
    @Autowired
    public void setSubstationDao(SubstationDao substationDao) {
        this.substationDao = substationDao;
    }

    @Autowired
    public void setSubstationBusDao(SubstationBusDao substationBusDao) {
        this.substationBusDao = substationBusDao;
    }
    
    @Autowired
    public void setFeederDao(FeederDao feederDao) {
        this.feederDao = feederDao;
    }
    
    @Autowired
    public void setCapbankDao(CapbankDao capbankDao) {
        this.capbankDao = capbankDao;
    }
    
    @Autowired
    public void setCapbankControllerDao(CapbankControllerDao capbankControllerDao) {
        this.cbcDao = capbankControllerDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setLtcDao(LtcDao ltcDao){
        this.ltcDao = ltcDao;
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setFilterCacheFactory (FilterCacheFactory filterCacheFactory) {
        this.cacheFactory = filterCacheFactory;
    }
}