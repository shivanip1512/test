package com.cannontech.web.capcontrol;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.dao.CapbankControllerDao;
import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.FeederDao;
import com.cannontech.capcontrol.dao.SubstationBusDao;
import com.cannontech.capcontrol.dao.SubstationDao;
import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.exceptions.MissingSearchType;
import com.cannontech.cbc.util.CapControlUtils;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.db.capcontrol.CCEventLog;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ParamUtil;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.capcontrol.models.ControlEventSet;
import com.cannontech.web.capcontrol.models.ResultRow;
import com.cannontech.web.lite.LiteBaseResults;
import com.cannontech.web.lite.LiteWrapper;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/search/*")
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ResultsController {
    
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private FilterCacheFactory cacheFactory;
    private SubstationBusDao substationBusDao;
    private SubstationDao substationDao;
    private FeederDao feederDao;
    private CapbankDao capbankDao;
    private CapbankControllerDao cbcDao;
    private PaoDao paoDao;
    private ParentStringPrinterFactory printerFactory;
    private DateFormattingService dateFormattingService;
    private VoltageRegulatorDao voltageRegulatorDao; 
    
    private enum SearchType {
    	REGULATOR,
    	CBC,
    	CAPCONTROL,
    	GENERAL,
    	;
    }

    @RequestMapping
    public String searchResults(HttpServletRequest request, ModelMap model, YukonUserContext context, Integer itemsPerPage, Integer page) throws MissingSearchType {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        if(page == null){
            page = 1;
        }
        if(itemsPerPage == null){
            itemsPerPage = 25;
        }
        
        int startIndex = (page - 1) * itemsPerPage;
        
        ParentStringPrinter psp = printerFactory.createParentStringPrinter(request);
        String srchCriteria = ParamUtil.getString(request, CCSessionInfo.STR_LAST_SEARCH, null);
        if( srchCriteria == null ) {
            CCSessionInfo ccSession = (CCSessionInfo) request.getSession().getAttribute("ccSession");
            srchCriteria = ccSession.getLastSearchCriteria();
        }
        
        model.addAttribute("lastAreaKey", CCSessionInfo.STR_CC_AREA);
        model.addAttribute("lastSubKey", CCSessionInfo.STR_SUBID);
        
        String label = srchCriteria;
        boolean orphan = true;
        
        int hitCount = 0;
        List<LiteWrapper> items = Lists.newArrayList();
        SearchResult<LiteCapControlObject> ccObjects = null;
        SearchType searchType = null;

        if( CBCWebUtils.TYPE_ORPH_SUBSTATIONS.equals(srchCriteria) ) {
        	searchType = SearchType.CAPCONTROL;
            ccObjects = substationDao.getOrphans(startIndex, itemsPerPage);
            label = accessor.getMessage("yukon.web.modules.capcontrol.search.orphanedSubs.pageName");
            model.addAttribute("pageName", "orphanedSubs");
        }
        else if( CBCWebUtils.TYPE_ORPH_SUBS.equals(srchCriteria) ) {
        	searchType = SearchType.CAPCONTROL;
            ccObjects = substationBusDao.getOrphans(startIndex, itemsPerPage);
            label = accessor.getMessage("yukon.web.modules.capcontrol.search.orphanedBuses.pageName");
            model.addAttribute("pageName", "orphanedBuses");
        }
        else if( CBCWebUtils.TYPE_ORPH_FEEDERS.equals(srchCriteria) ) {
        	searchType = SearchType.CAPCONTROL;
            ccObjects = feederDao.getOrphans(startIndex, itemsPerPage);
            label = accessor.getMessage("yukon.web.modules.capcontrol.search.orphanedFeeders.pageName");
            model.addAttribute("pageName", "orphanedFeeders");
        }
        else if( CBCWebUtils.TYPE_ORPH_BANKS.equals(srchCriteria) ) {
        	searchType = SearchType.CAPCONTROL;
            ccObjects = capbankDao.getOrphans(startIndex, itemsPerPage);
            label = accessor.getMessage("yukon.web.modules.capcontrol.search.orphanedBanks.pageName");
            model.addAttribute("pageName", "orphanedBanks");
        }
        else if( CBCWebUtils.TYPE_ORPH_CBCS.equals(srchCriteria) ) {
        	searchType = SearchType.CBC;
            ccObjects = cbcDao.getOrphans(startIndex, itemsPerPage);
            label = accessor.getMessage("yukon.web.modules.capcontrol.search.orphanedCbcs.pageName");
            model.addAttribute("pageName", "orphanedCbcs");
        }
        else if( CBCWebUtils.TYPE_ORPH_REGULATORS.equals(srchCriteria) ) {
        	searchType = SearchType.REGULATOR;
            ccObjects = voltageRegulatorDao.getOrphans(startIndex, itemsPerPage);
            label = accessor.getMessage("yukon.web.modules.capcontrol.search.orphanedRegulators.pageName");
            model.addAttribute("pageName", "orphanedRegulators");
        }
        else {
            model.addAttribute("pageName", "general");
        	searchType = SearchType.GENERAL;
        	orphan = false;
            LiteBaseResults lbr = new LiteBaseResults();
            lbr.searchLiteObjects( srchCriteria );
            items = lbr.getFoundItems(startIndex, itemsPerPage);
            hitCount = lbr.getFoundItems().size();
        }
        
        model.addAttribute("label", label);
        
        List<ResultRow> rows = new ArrayList<ResultRow>();
        if (ccObjects == null) {
            for (LiteWrapper item : items) {
                ResultRow row = new ResultRow();
                row.setName(item.toString());
                row.setItemId(item.getItemID());
                row.setIsPaobject(item.getLiteType() == LiteTypes.YUKON_PAOBJECT);
                
                boolean isController = CapControlUtils.isController(item.getItemID());
                row.setIsController(isController);
                
                boolean isPoint = item.getParentID() != CtiUtilities.NONE_ZERO_ID;
                String parentString = (!isPoint) ? psp.printPAO(item.getItemID()) :  psp.printPoint(item.getItemID());
                row.setParentString(parentString);
                row.setParentId(item.getParentID());
                row.setItemDescription(item.getDescription());
                
                String displayableType = getDisplayableType(searchType, item.getItemType());
                row.setItemType(displayableType);
                
                rows.add(row);
            }
        } else {
            hitCount = ccObjects.getHitCount();
            for (LiteCapControlObject item : ccObjects.getResultList()) {
                ResultRow row = new ResultRow();
                row.setName(item.getName());
                row.setItemId(item.getId());
                row.setIsPaobject(true);
                
                //If this is not a device, it is not a controller. Next call will catch it.
                PaoType paoType = PaoType.getForDbString(item.getType());
                boolean isController = CapControlUtils.checkControllerByType(paoType);
                row.setIsController(isController);
                
                String parentString = ParentStringPrinter.ORPH_STRING;
                if (!orphan) {
                	parentString = psp.printPAO(item.getId());
                }
                row.setParentString(parentString);
                row.setParentId(item.getParentId());
                row.setItemDescription(item.getDescription());
                
                String displayableType = getDisplayableType(searchType, item.getType());
                row.setItemType(displayableType);
                
                rows.add(row);
            }
        }
        
        SearchResult<ResultRow> searchResult = new SearchResult<ResultRow>();
        searchResult.setResultList(rows);
        if (rows.isEmpty()) {
            searchResult.setBounds(0, itemsPerPage, 0);
        }
        searchResult.setBounds(startIndex, itemsPerPage, hitCount);
        
        model.addAttribute("rows", rows);
        model.addAttribute("searchResult", searchResult);
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("resultsFound", hitCount);
        
        String urlParams = request.getQueryString();
        String requestURI = request.getRequestURI() + ((urlParams != null) ? "?" + urlParams : "");
        CBCNavigationUtil.setNavigation(requestURI , request.getSession());
        
        return "search/searchResults.jsp";
    }
    
    private String getDisplayableType(SearchType searchType, String dbType) throws MissingSearchType {
    	
    	if (searchType == SearchType.REGULATOR) {
    		PaoType regType = PaoType.getForDbString(dbType);
			String returnValue = regType.getDbString();
			return returnValue;
    	} else if (searchType == SearchType.CAPCONTROL) {
    		CapControlType ccType = CapControlType.getCapControlType(dbType);
    		String returnValue = ccType.getDisplayValue();
			return returnValue;
    	} else if (searchType == SearchType.CBC) {
    		PaoType cbcType = PaoType.getForDbString(dbType);
    		String returnValue = cbcType.getDbString();
			return returnValue;
		} else if (searchType == SearchType.GENERAL) {
			return dbType;
		} else {
			throw new MissingSearchType("No search type set.");
		}
    }
    
    @RequestMapping
    public String recentEvents(HttpServletRequest request, ModelMap model, LiteYukonUser user, String value) {
        CapControlCache cache = cacheFactory.createUserAccessFilteredCache(user);
        YukonUserContext context = YukonUserContextUtils.getYukonUserContext(request);
        Integer MAX_DAYS_CNT = 7;
        
        List<Integer> paoIds = ServletUtil.getIntegerListFromString(value);
        
        int dayCnt = ParamUtil.getInteger(request, "dayCnt", 1);
        List<ControlEventSet>  listOfEventSets = new ArrayList<ControlEventSet>();
        
        for (int id : paoIds) {
            StreamableCapObject cbcPAO = cache.getCapControlPAO(id);

            if (cbcPAO != null) {
                List<CCEventLog> events= CBCWebUtils.getCCEventsForPAO(new Long (id), cbcPAO.getCcType(), cache, dayCnt);
                for (CCEventLog event : events) {
                    String formattedTimestamp = dateFormattingService.format(event.getTimestamp(), DateFormatEnum.BOTH, context);
                    event.setFormattedTimestamp(formattedTimestamp);
                }
                ControlEventSet set = new ControlEventSet(id, events);
                String paoName = paoDao.getLiteYukonPAO(id).getPaoName();
                set.setPaoName(paoName);
                listOfEventSets.add(set);
            }
        }
        
        model.addAttribute("dayCnt", new Integer(dayCnt));
        model.addAttribute("MAX_DAYS_CNT", MAX_DAYS_CNT);
        model.addAttribute("paoIdString", value);
        model.addAttribute("listOfEventSets", listOfEventSets);
        return "search/recentEvents.jsp";
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
    public void setVoltageRegulatorDao(VoltageRegulatorDao voltageRegulatorDao){
        this.voltageRegulatorDao = voltageRegulatorDao;
    }
    
    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setFilterCacheFactory (FilterCacheFactory filterCacheFactory) {
        this.cacheFactory = filterCacheFactory;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}