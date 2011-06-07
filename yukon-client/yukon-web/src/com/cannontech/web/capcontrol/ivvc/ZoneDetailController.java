package com.cannontech.web.capcontrol.ivvc;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.AbstractZoneNotThreePhase;
import com.cannontech.capcontrol.model.AbstractZoneThreePhase;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.ZoneRegulator;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.web.CapControlCommandExecutor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.VoltageRegulatorPointMapping;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.CommandHolder;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubStation;
import com.cannontech.yukon.cbc.VoltageRegulatorFlags;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;


@RequestMapping("/ivvc/zone/*")
@Controller
public class ZoneDetailController {

    private FilterCacheFactory filterCacheFactory;
    private RolePropertyDao rolePropertyDao;
    private ZoneService zoneService;
    private ZoneDtoHelper zoneDtoHelper;
    private VoltageRegulatorService voltageRegulatorService;
    private VoltageFlatnessGraphService voltageFlatnessGraphService;
    private PaoDao paoDao;
    
    @RequestMapping
    public String detail(ModelMap model, FlashScope flash, HttpServletRequest request, 
                         LiteYukonUser user, int zoneId, Boolean isSpecialArea) {
        setupDetails(model, flash, request, user, zoneId, isSpecialArea);
        return "ivvc/zoneDetail.jsp";
    }
    
    @RequestMapping
    public String deltaUpdate(ModelMap model, boolean staticDelta, int bankId, int pointId, double delta, 
                              int zoneId, Boolean isSpecialArea, LiteYukonUser user, HttpServletRequest request) {
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        CapControlCommandExecutor exec = new CapControlCommandExecutor(cache, user);
        
        exec.executeDeltaUpdate(bankId,pointId,delta,staticDelta);
        
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        
        model.addAttribute("isSpecialArea", isSpecialArea);
        model.addAttribute("zoneId", zoneId);
        return "redirect:/spring/capcontrol/ivvc/zone/detail";
    }
    
    @RequestMapping
    public String chart(ModelMap model, FlashScope flash, HttpServletRequest request, 
                        YukonUserContext userContext, int zoneId) {
        boolean missingVoltageAttribute = false;
        try {
            VfGraph graph = voltageFlatnessGraphService.getZoneGraph(userContext, zoneId);
            model.addAttribute("graph", graph);
            model.addAttribute("graphSettings", graph.getSettings());
        } catch (IllegalUseOfAttribute e) {
            missingVoltageAttribute = true;
        }
        model.addAttribute("missingVoltageAttribute", missingVoltageAttribute);
        
        return "ivvc/flatnessGraphLine.jsp";
    }
    
    private void setupDetails(ModelMap model, FlashScope flash, HttpServletRequest request, 
                              LiteYukonUser user, int zoneId, Boolean isSpecialArea) {
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        model.addAttribute("isSpecialArea",isSpecialArea);        
        model.addAttribute("title", "IVVC Zone Detail View");
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole",hasEditingRole);
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZoneId(zoneId, user);
        
        setupZoneDetails(model, cache, zoneDto);
        setupIvvcEvents(model, zoneDto.getZoneId(), zoneDto.getSubstationBusId());
        setupCapBanks(model, cache, zoneDto);
        setupBreadCrumbs(model, cache, zoneDto, isSpecialArea);
        setupDeltas(model, request, cache, zoneDto.getZoneId());
        
        if (zoneDto instanceof AbstractZoneThreePhase) {
            setupThreePhaseRegulatorPointList(model, ((AbstractZoneThreePhase)zoneDto).getRegulators());
        } else {
            setupRegulatorPointList(model, ((AbstractZoneNotThreePhase)zoneDto).getRegulator());
        }

        setupRegulatorCommands(model, zoneDto);
        
        List<String> nameKeys = Lists.newArrayList("attributesRegA", "attributesRegB", "attributesRegC");
        model.addAttribute("nameKeys", nameKeys);

        model.addAttribute("subBusId", zoneDto.getSubstationBusId());
    }

    private void setupZoneDetails(ModelMap model, CapControlCache cache, AbstractZone zoneDto) {
        if (zoneDto instanceof AbstractZoneThreePhase) {
            AbstractZoneThreePhase abstractZoneThreePhase = (AbstractZoneThreePhase) zoneDto;
            VoltageRegulatorFlags regulatorFlagsPhaseA =
                cache.getVoltageRegulatorFlags(abstractZoneThreePhase.getRegulatorA().getRegulatorId());
            model.addAttribute("regulatorIdPhaseA", regulatorFlagsPhaseA.getCcId());
            model.addAttribute("regulatorNamePhaseA", regulatorFlagsPhaseA.getCcName());
            VoltageRegulatorFlags regulatorFlagsPhaseB =
                cache.getVoltageRegulatorFlags(abstractZoneThreePhase.getRegulatorB().getRegulatorId());
            model.addAttribute("regulatorIdPhaseB", regulatorFlagsPhaseB.getCcId());
            model.addAttribute("regulatorNamePhaseB", regulatorFlagsPhaseB.getCcName());
            VoltageRegulatorFlags regulatorFlagsPhaseC =
                cache.getVoltageRegulatorFlags(abstractZoneThreePhase.getRegulatorC().getRegulatorId());
            model.addAttribute("regulatorIdPhaseC", regulatorFlagsPhaseC.getCcId());
            model.addAttribute("regulatorNamePhaseC", regulatorFlagsPhaseC.getCcName());
        } else {
            VoltageRegulatorFlags regulatorFlags =
                cache.getVoltageRegulatorFlags(((AbstractZoneNotThreePhase) zoneDto).getRegulator().getRegulatorId());
            model.addAttribute("regulatorId", regulatorFlags.getCcId());
            model.addAttribute("regulatorName", regulatorFlags.getCcName());
        }

        model.addAttribute("zoneDto", zoneDto);
        addZoneEnums(model);
    }

    private void addZoneEnums(ModelMap model) {
        model.addAttribute("gangOperated", ZoneType.GANG_OPERATED);
        model.addAttribute("threePhase", ZoneType.THREE_PHASE);
        model.addAttribute("singlePhase", ZoneType.SINGLE_PHASE);
        model.addAttribute("phaseA", Phase.A);
        model.addAttribute("phaseB", Phase.B);
        model.addAttribute("phaseC", Phase.C);
    }

    private void setupRegulatorCommands(ModelMap model, AbstractZone zoneDto) {
        if (zoneDto instanceof AbstractZoneThreePhase) {
            AbstractZoneThreePhase abstractZoneThreePhase = (AbstractZoneThreePhase) zoneDto;
            YukonPao regulatorAPao = paoDao.getYukonPao(abstractZoneThreePhase.getRegulatorA().getRegulatorId());
            PaoType regAType = regulatorAPao.getPaoIdentifier().getPaoType();
            String regulatorAType = regAType.getDbString();
            model.addAttribute("regulatorAType", regulatorAType);
            
            YukonPao regulatorBPao = paoDao.getYukonPao(abstractZoneThreePhase.getRegulatorB().getRegulatorId());
            PaoType regBType = regulatorBPao.getPaoIdentifier().getPaoType();
            String regulatorBType = regBType.getDbString();
            model.addAttribute("regulatorBType", regulatorBType);
            
            YukonPao regulatorCPao = paoDao.getYukonPao(abstractZoneThreePhase.getRegulatorC().getRegulatorId());
            PaoType regCType = regulatorCPao.getPaoIdentifier().getPaoType();
            String regulatorCType = regCType.getDbString();
            model.addAttribute("regulatorCType", regulatorCType);
        } else {
            YukonPao regulatorPao = paoDao.getYukonPao(((AbstractZoneNotThreePhase) zoneDto).getRegulator().getRegulatorId());
            PaoType regType = regulatorPao.getPaoIdentifier().getPaoType();
            String regulatorType = regType.getDbString();
            model.addAttribute("regulatorType", regulatorType);
        }
        
        model.addAttribute("scanCommandHolder",CommandHolder.LTC_SCAN_INTEGRITY);
        model.addAttribute("tapDownCommandHolder",CommandHolder.LTC_TAP_POSITION_LOWER);
        model.addAttribute("tapUpCommandHolder",CommandHolder.LTC_TAP_POSITION_RAISE);
        model.addAttribute("enableRemoteCommandHolder",CommandHolder.LTC_REMOTE_ENABLE);
        model.addAttribute("disableRemoteCommandHolder",CommandHolder.LTC_REMOTE_DISABLE);
    }
    
    private void setupIvvcEvents(ModelMap model,int zoneId,int subBusId) {
        final int rowLimit = 20;
        
        List<CcEvent> events = zoneService.getLatestEvents(zoneId, subBusId, rowLimit);
        
        model.addAttribute("events", events);
    }
    
    private void setupCapBanks(ModelMap model, CapControlCache cache, AbstractZone zoneDto) {
        List<Integer> capBankIdList = zoneService.getCapBankIdsForZoneId(zoneDto.getZoneId());
        List<Integer> unassignedBankIds = zoneService.getUnassignedCapBankIdsForSubBusId(zoneDto.getSubstationBusId());
        
        //Add unassigned banks to main list, we want to display them.
        capBankIdList.addAll(unassignedBankIds);
        
        List<CapBankDevice> capBankList = Lists.newArrayList();
        for (Integer bankId : capBankIdList) {
            CapBankDevice bank = cache.getCapBankDevice(bankId);
            capBankList.add(bank);
        }
        
        List<ViewableCapBank> viewableCapBankList = CapControlWebUtils.createViewableCapBank(capBankList);
        for (ViewableCapBank bank:viewableCapBankList) {
            List<Integer> monitorPoints = zoneService.getMonitorPointsForBank(bank.getCapBankDevice().getCcId());
            if (monitorPoints.size() > 0) {
                //Grabbing the first one to display, list is sorted on display order
                bank.setVoltagePointId(monitorPoints.get(0));
            }
            
            if (unassignedBankIds.contains(bank.getCapBankDevice().getCcId())) {
                bank.setNotAssignedToZone(true);
            } else {
                bank.setNotAssignedToZone(false);
            }
        }

        Collections.sort(viewableCapBankList);

        model.addAttribute("unassignedBanksExist",unassignedBankIds.size()>0);
        model.addAttribute("capBankList", viewableCapBankList);
    }
    
    private void setupRegulatorPointList(ModelMap model, ZoneRegulator regulator) {
        int regulatorId = regulator.getRegulatorId();
        List<VoltageRegulatorPointMapping> pointMappings = voltageRegulatorService.getPointMappings(regulatorId);
        Collections.sort(pointMappings);
        model.addAttribute("regulatorPointMappings", pointMappings);
    }

    private void setupThreePhaseRegulatorPointList(ModelMap model, List<ZoneRegulator> regulators) {
        List<List<VoltageRegulatorPointMapping>> pointMappingsList = Lists.newArrayListWithCapacity(3);
        
        for (ZoneRegulator zoneRegulator: regulators) {
            int regId = zoneRegulator.getRegulatorId();
            List<VoltageRegulatorPointMapping> pointMappings = voltageRegulatorService.getPointMappings(regId);
            Collections.sort(pointMappings);
            pointMappingsList.add(pointMappings);
        }
        model.addAttribute("regulatorPointMappingsList", pointMappingsList);
    }
    
    private void setupDeltas(ModelMap model, HttpServletRequest request, CapControlCache cache, int zoneId) {
        List<Integer> bankIds = zoneService.getCapBankIdsForZoneId(zoneId);
        
        List<CapBankPointDelta> pointDeltas = zoneService.getAllPointDeltasForBankIds(bankIds);
        
        int itemsPerPage = ServletRequestUtils.getIntParameter(request, "itemsPerPage", 10);
        int currentPage = ServletRequestUtils.getIntParameter(request, "page", 1);
        int startIndex = (currentPage - 1) * itemsPerPage;
        
        SearchResult<CapBankPointDelta> searchResults = new SearchResult<CapBankPointDelta>();
        List<CapBankPointDelta> trimmedPointDeltas = Lists.newArrayList();
        
        if (startIndex < pointDeltas.size()) {
            int endIndex;
            
            if (startIndex + itemsPerPage > pointDeltas.size()) {
                endIndex = pointDeltas.size();
            } else {
                endIndex = startIndex + itemsPerPage;
            }
            
        	trimmedPointDeltas = pointDeltas.subList(startIndex,endIndex);
        }

        Ordering<CapBankPointDelta> capBankOrdering =
            Ordering.from(String.CASE_INSENSITIVE_ORDER)
                .onResultOf(new Function<CapBankPointDelta, String>() {
                    @Override
                    public String apply(CapBankPointDelta from) {
                        return from.getCbcName();
                    }
                })
                .compound(Ordering.explicit(true, false)
                    .onResultOf(new Function<CapBankPointDelta, Boolean>() {
                        @Override
                        public Boolean apply(CapBankPointDelta from) {
                            return from.getCbcName().equalsIgnoreCase(from.getAffectedDeviceName());
                        }
                    }))
                .compound(Ordering.from(String.CASE_INSENSITIVE_ORDER)
                    .onResultOf(new Function<CapBankPointDelta, String>() {
                        @Override
                        public String apply(CapBankPointDelta from) {
                            return from.getAffectedDeviceName();
                        }
                    }));

        Collections.sort(trimmedPointDeltas, capBankOrdering);

        searchResults.setResultList(trimmedPointDeltas);
        searchResults.setBounds(startIndex, itemsPerPage, pointDeltas.size());
        
        model.addAttribute("zoneId", zoneId);
        model.addAttribute("searchResults", searchResults);
    }
    
    private void setupBreadCrumbs(ModelMap model, CapControlCache cache, AbstractZone zoneDto, boolean isSpecialArea) {
        
        int subBusId = zoneDto.getSubstationBusId();
        StreamableCapObject subBus = cache.getSubBus(subBusId);
        SubStation station = cache.getSubstation(subBus.getParentID());
        
        StreamableCapObject area;
        if(isSpecialArea) {
            area = cache.getArea(station.getSpecialAreaId());
        }
        else {
            area = cache.getArea(station.getParentID());
        }
        
        String areaName = area.getCcName();
        String substationName = station.getCcName();
        String subBusName = subBus.getCcName();
        String zoneName = zoneDto.getName();
        
        model.addAttribute("areaId", area.getCcId());
        model.addAttribute("areaName", areaName);
        model.addAttribute("subStationId", station.getCcId());
        model.addAttribute("subStationName", substationName);
        model.addAttribute("subBusId", subBusId);
        model.addAttribute("subBusName", subBusName);
        model.addAttribute("zoneName", zoneName);
    }
    
    @Autowired
    public void setFilteredCapControlcache (FilterCacheFactory factory) {
        this.filterCacheFactory = factory;
    }
    
    @Autowired
    public void setRolePropertyDao (RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setZoneService (ZoneService zoneService) {
        this.zoneService = zoneService;
    }
    
    @Autowired
    public void setVoltageRegulatorService(VoltageRegulatorService voltageRegulatorService) {
        this.voltageRegulatorService = voltageRegulatorService;
    }
    
    @Autowired
    public void setVoltageFlatnessGraphService(VoltageFlatnessGraphService voltageFlatnessGraphService) {
        this.voltageFlatnessGraphService = voltageFlatnessGraphService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setZoneDtoHelpers(ZoneDtoHelper zoneDtoHelpers) {
        this.zoneDtoHelper = zoneDtoHelpers;
    }
}
