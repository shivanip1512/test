package com.cannontech.web.capcontrol.ivvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.CapBankPointDelta;
import com.cannontech.capcontrol.model.CcEvent;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.VoltageLimitedDeviceInfo;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.model.ZoneVoltagePointsHolder;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.cache.FilterCacheFactory;
import com.cannontech.cbc.commands.CapControlCommandExecutor;
import com.cannontech.cbc.commands.CommandResultCallback;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.LazyList;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.capcontrol.VoltageRegulatorPointMapping;
import com.cannontech.database.data.lite.LitePointUnit;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.enums.Phase;
import com.cannontech.enums.RegulatorPointMapping;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.capcontrol.model.CapControlServerResponse;
import com.cannontech.message.capcontrol.model.CommandType;
import com.cannontech.message.capcontrol.model.DynamicCommand;
import com.cannontech.message.capcontrol.model.DynamicCommand.DynamicCommandType;
import com.cannontech.message.capcontrol.model.DynamicCommand.Parameter;
import com.cannontech.message.capcontrol.streamable.CapBankDevice;
import com.cannontech.message.capcontrol.streamable.StreamableCapObject;
import com.cannontech.message.capcontrol.streamable.SubStation;
import com.cannontech.message.capcontrol.streamable.VoltageRegulatorFlags;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.capcontrol.ivvc.models.VfGraph;
import com.cannontech.web.capcontrol.ivvc.service.VoltageFlatnessGraphService;
import com.cannontech.web.capcontrol.models.ViewableCapBank;
import com.cannontech.web.capcontrol.util.CapControlWebUtils;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.PaoIdentifierPropertyEditor;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

@RequestMapping("/ivvc/zone/*")
@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class ZoneDetailController {

    @Autowired private FilterCacheFactory filterCacheFactory;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private ZoneService zoneService;
    @Autowired private ZoneDtoHelper zoneDtoHelper;
    @Autowired private VoltageRegulatorService voltageRegulatorService;
    @Autowired private VoltageFlatnessGraphService voltageFlatnessGraphService;
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private CapControlCommandExecutor executor;
    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    @Autowired private StrategyDao strategyDao;
    @Autowired private ZoneDao zoneDao;

    public static class ZoneVoltageDeltas {
        private List<CapBankPointDelta> pointDeltas = LazyList
                .ofInstance(CapBankPointDelta.class);
        
        public ZoneVoltageDeltas() {
            super();
        }
        public ZoneVoltageDeltas(List<CapBankPointDelta> pointDeltas) {
            this.pointDeltas = pointDeltas;
        }
        public List<CapBankPointDelta> getPointDeltas() {
            return pointDeltas;
        }
        public void setPointDeltas(List<CapBankPointDelta> pointDeltas) {
            this.pointDeltas = pointDeltas;
        }
    }

    @RequestMapping
    public String detail(ModelMap model, HttpServletRequest request, YukonUserContext context,
                         int zoneId, Boolean isSpecialArea) {
        setupDetails(model, request, context, zoneId, isSpecialArea);

        List<VoltageLimitedDeviceInfo> infos = ccMonitorBankListDao.getDeviceInfoByZoneId(zoneId);
        ZoneVoltagePointsHolder zoneVoltagePointsHolder = new ZoneVoltagePointsHolder(zoneId, infos);
        model.addAttribute("zoneVoltagePointsHolder", zoneVoltagePointsHolder);

        return "ivvc/zoneDetail.jsp";
    }

    @RequestMapping
    public String voltagePoints(ModelMap model, HttpServletRequest request,
                                YukonUserContext context, int zoneId, Boolean isSpecialArea) {
        List<VoltageLimitedDeviceInfo> infos = ccMonitorBankListDao.getDeviceInfoByZoneId(zoneId);
        ZoneVoltagePointsHolder zoneVoltagePointsHolder = new ZoneVoltagePointsHolder(zoneId, infos);
        setupVoltagePointAttributes(zoneVoltagePointsHolder, model, context, zoneId, isSpecialArea);
        return "ivvc/voltagePointsEdit.jsp";
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public String updateVoltagePoints(@ModelAttribute ZoneVoltagePointsHolder zoneVoltagePointsHolder,
                                      BindingResult bindingResult,
                                      ModelMap model, YukonUserContext context, int zoneId,
                                      Boolean isSpecialArea, FlashScope flashScope) {
        setupVoltagePointAttributes(zoneVoltagePointsHolder, model, context, zoneId, isSpecialArea);
        
        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return "ivvc/voltagePointsEdit.jsp";
        }

        try {
            CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(context.getYukonUser());
            Zone zone = zoneDao.getZoneById(zoneId);
            int subBusId = zone.getSubstationBusId();
            CapControlStrategy strategy = strategyDao.getForId(cache.getSubBus(subBusId).getStrategyId());

            // Loop over your zoneVoltagePoints and set the limits to the strategies if Override is false
            for (VoltageLimitedDeviceInfo voltagePoint : zoneVoltagePointsHolder.getPoints()) {
                if (voltagePoint.isOverrideStrategy()) continue;

                double lowerLimit = voltagePoint.getLowerLimit(); 
                double upperLimit = voltagePoint.getUpperLimit();
                for ( PeakTargetSetting setting : strategy.getTargetSettings()) {
                    if (setting.getType() == TargetSettingType.LOWER_VOLT_LIMIT) {
                        lowerLimit = Double.parseDouble(setting.getPeakValue());
                    }
                    if (setting.getType() == TargetSettingType.UPPER_VOLT_LIMIT) {
                        upperLimit = Double.parseDouble(setting.getPeakValue());
                    }
                }

                voltagePoint.setLowerLimit(lowerLimit);
                voltagePoint.setUpperLimit(upperLimit);
            }

            ccMonitorBankListDao.updateDeviceInfo(zoneVoltagePointsHolder.getPoints());
            MessageSourceResolvable successMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.voltagePoints.updateSuccess");
            flashScope.setConfirm(successMessage);
        } catch (DataAccessException e) {
            MessageSourceResolvable errorMessage =
                new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.voltagePoints.updateFailure");
            flashScope.setError(errorMessage);
        }

        return "redirect:/spring/capcontrol/ivvc/zone/voltagePoints";
    }

    private void setupVoltagePointAttributes(ZoneVoltagePointsHolder zoneVoltagePointsHolder, ModelMap model,
                                             YukonUserContext context, int zoneId,
                                             Boolean isSpecialArea) {
        setupBreadCrumbs(model, context, zoneId, isSpecialArea);
        model.addAttribute("zoneId", zoneId);
        model.addAttribute("zoneVoltagePointsHolder", zoneVoltagePointsHolder);

        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT,
                                                               context.getYukonUser());
        model.addAttribute("hasEditingRole", hasEditingRole);

        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(context.getYukonUser());
        Zone zone = zoneDao.getZoneById(zoneId);
        int subBusId = zone.getSubstationBusId();
        int strategyId = cache.getSubBus(subBusId).getStrategyId();
        CapControlStrategy strategy = strategyDao.getForId(strategyId);
        model.addAttribute("strategy", strategy);
    }

    @RequestMapping
    public String voltageDeltas(ModelMap model, HttpServletRequest request,
                                YukonUserContext context, int zoneId, Boolean isSpecialArea) {
        setupDeltas(model, request, zoneId);
        setupBreadCrumbs(model, context, zoneId, isSpecialArea);
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT,
                                          context.getYukonUser());
        model.addAttribute("hasEditingRole", hasEditingRole);
        model.addAttribute("zoneId", zoneId);
        return "ivvc/zoneVoltageDeltas.jsp";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String deltaUpdate(@ModelAttribute ZoneVoltageDeltas zoneVoltageDeltas,
                              BindingResult bindingResult,
                              ModelMap model,
                              int zoneId,
                              Boolean isSpecialArea,
                              YukonUserContext context,
                              HttpServletRequest request,
                              FlashScope flashScope) {
        try {
            for (CapBankPointDelta pointDelta : zoneVoltageDeltas.getPointDeltas()) {
                double deltaAsDbl = Double.valueOf(pointDelta.getDelta());
    
                DynamicCommand command = new DynamicCommand(DynamicCommandType.DELTA);
                command.addParameter(Parameter.DEVICE_ID, pointDelta.getBankId());
                command.addParameter(Parameter.POINT_ID, pointDelta.getPointId());
                command.addParameter(Parameter.POINT_RESPONSE_DELTA, deltaAsDbl);
                command.addParameter(Parameter.POINT_RESPONSE_STATIC_DELTA, pointDelta.isStaticDelta() ? 1 : 0);
                
                CommandResultCallback callback = new CommandResultCallback() {
                    private CapControlServerResponse response;
                    private String errorMessage;
                    @Override
                    public void processingExceptionOccured(String errorMessage) {
                        this.errorMessage = errorMessage;
                    }
                    @Override
                    public CapControlServerResponse getResponse() {
                        return response;
                    }
                    @Override
                    public void recievedResponse(CapControlServerResponse message) {
                        if (!message.isSuccess()) {
                            this.errorMessage = message.getResponse();
                        }
                        this.response = message;
                    }
                    @Override
                    public String getErrorMessage() {
                        return errorMessage;
                    }
                };
    
                CapControlServerResponse response = executor.blockingExecute(command, CapControlServerResponse.class, callback);
                if (response.isTimeout()) {
                    flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneVoltageDeltas.updateTimeout"));
                } else if (!response.isSuccess()) {
                    flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneVoltageDeltas.updateFailure", callback.getErrorMessage()));
                } else {
                    flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneVoltageDeltas.updateSuccess"));
                }
    
                if (isSpecialArea == null) {
                    isSpecialArea = false;
                }
            }
        } catch (NumberFormatException e) {
            // The user entered something invalid into the delta field
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.modules.capcontrol.ivvc.zoneVoltageDeltas.updateFailureFormat"));
        }

        model.addAttribute("isSpecialArea", isSpecialArea);
        model.addAttribute("zoneId", zoneId);
        return "redirect:/spring/capcontrol/ivvc/zone/voltageDeltas";
    }
    
    @RequestMapping
    public String chart(ModelMap model, YukonUserContext context, int zoneId) {
        LiteYukonUser user = context.getYukonUser();
        
        boolean zoneAttributesExist = voltageFlatnessGraphService. zoneHasRequiredRegulatorPointMapping(zoneId, RegulatorPointMapping.VOLTAGE_Y, user);
        model.addAttribute("zoneAttributesExist", zoneAttributesExist);

        if (zoneAttributesExist) {
            VfGraph graph = voltageFlatnessGraphService.getZoneGraph(context, zoneId);
            model.addAttribute("graph", graph);
            model.addAttribute("graphSettings", graph.getSettings());
        }
        
        return "ivvc/flatnessGraphLine.jsp";
    }
    
    private void setupDetails(ModelMap model, HttpServletRequest request, YukonUserContext context, int zoneId, Boolean isSpecialArea) {
        LiteYukonUser user = context.getYukonUser();
        if(isSpecialArea == null) {
            isSpecialArea = false;
        }
        model.addAttribute("isSpecialArea",isSpecialArea);
        
        boolean hasEditingRole = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, user);
        model.addAttribute("hasEditingRole", hasEditingRole);
        
        boolean hasControlRole = rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_CAPBANK_CONTROLS, user);
        model.addAttribute("hasControlRole", hasControlRole);
        
        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZoneId(zoneId, user);
        
        setupZoneDetails(model, cache, zoneDto);
        setupIvvcEvents(model, zoneDto.getZoneId(), zoneDto.getSubstationBusId());
        setupCapBanks(model, cache, zoneDto);
        setupBreadCrumbs(model, cache, zoneDto, isSpecialArea);
        setupRegulatorPointMappings(model, zoneDto);
        setupRegulatorCommands(model, zoneDto);

        model.addAttribute("subBusId", zoneDto.getSubstationBusId());
        int updaterDelay = Integer.valueOf(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DATA_UPDATER_DELAY_MS, user));
        model.addAttribute("updaterDelay", updaterDelay);
    }

    private void setupZoneDetails(ModelMap model, CapControlCache cache, AbstractZone zoneDto) {
        Map<Phase, Integer> regulatorIdMap = Maps.newHashMapWithExpectedSize(3);
        Map<Phase, String> regulatorNameMap = Maps.newHashMapWithExpectedSize(3);
        Map<Phase, RegulatorToZoneMapping> regulators = zoneDto.getRegulators();
        for (Entry<Phase, RegulatorToZoneMapping> entry : regulators.entrySet()) {
            VoltageRegulatorFlags regulatorFlags = cache.getVoltageRegulatorFlags(entry.getValue().getRegulatorId());
            Integer regulatorId = regulatorFlags.getCcId();
            String regulatorName = regulatorFlags.getCcName();
            regulatorIdMap.put(entry.getKey(), regulatorId);
            regulatorNameMap.put(entry.getKey(), regulatorName);
        }
        model.addAttribute("regulatorIdMap", regulatorIdMap);
        model.addAttribute("regulatorNameMap", regulatorNameMap);
        model.addAttribute("zoneDto", zoneDto);
        model.addAttribute("zoneId", zoneDto.getZoneId());
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

    private void setupRegulatorCommands(ModelMap model, AbstractZone abstractZone) {
        Map<Phase, RegulatorToZoneMapping> regulators = abstractZone.getRegulators();
        Map<Phase, String> regulatorTypeMap = Maps.newHashMapWithExpectedSize(3);
        for (Entry<Phase, RegulatorToZoneMapping> entry : regulators.entrySet()) {
            YukonPao regulatorPao = paoDao.getYukonPao(entry.getValue().getRegulatorId());
            PaoType regType = regulatorPao.getPaoIdentifier().getPaoType();
            String regTypeString = regType.getDbString();
            regulatorTypeMap.put(entry.getKey(), regTypeString);
        }
        model.addAttribute("regulatorTypeMap", regulatorTypeMap);
        model.addAttribute("scanCommandHolder", CommandType.VOLTAGE_REGULATOR_INTEGRITY_SCAN);
        model.addAttribute("tapDownCommandHolder", CommandType.VOLTAGE_REGULATOR_TAP_POSITION_LOWER);
        model.addAttribute("tapUpCommandHolder", CommandType.VOLTAGE_REGULATOR_TAP_POSITION_RAISE);
        model.addAttribute("enableRemoteCommandHolder", CommandType.VOLTAGE_REGULATOR_REMOTE_CONTROL_ENABLE);
        model.addAttribute("disableRemoteCommandHolder", CommandType.VOLTAGE_REGULATOR_REMOTE_CONTROL_DISABLE);
    }
    
    private void setupIvvcEvents(ModelMap model, int zoneId, int subBusId) {
        final int rowLimit = 20;
        List<CcEvent> events = zoneService.getLatestEvents(zoneId, subBusId, rowLimit, null, null);
        model.addAttribute("events", events);
        Instant mostRecentDateTime;
        if (events != null && !events.isEmpty()) {
            mostRecentDateTime = events.get(0).getDateTime();
        } else {
            mostRecentDateTime = new Instant();
        }
        model.addAttribute("mostRecentDateTime", mostRecentDateTime);
    }
    
    @RequestMapping
    public String getRecentEvents(ModelMap model, YukonUserContext userContext, int zoneId, int subBusId, String mostRecent) {
        DateTime dt = new DateTime(mostRecent).toDateTime(userContext.getJodaTimeZone());
        final int rowLimit = 20;
        List<CcEvent> events = zoneService.getLatestEvents(zoneId, subBusId, rowLimit, dt.toInstant(), null);
        model.addAttribute("events", events);
        return "ivvc/recentEvents.jsp";
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
    
    private void setupRegulatorPointMappings(ModelMap model, AbstractZone abstractZone) {
        Map<Phase, RegulatorToZoneMapping> regulators = abstractZone.getRegulators();
        Map<Phase, List<VoltageRegulatorPointMapping>> pointMappingsMap = Maps.newHashMapWithExpectedSize(3);
        for (Entry<Phase, RegulatorToZoneMapping> entry: regulators.entrySet()) {
            int regId = entry.getValue().getRegulatorId();
            List<VoltageRegulatorPointMapping> pointMappings = voltageRegulatorService.getPointMappings(regId);
            Collections.sort(pointMappings);
            pointMappingsMap.put(entry.getKey(), pointMappings);
        }
        model.addAttribute("regulatorPointMappingsMap", pointMappingsMap);
    }
    
    private void setupDeltas(ModelMap model, HttpServletRequest request, int zoneId) {
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
        
        // Set the CapBankPointDelta.deltaRounded value to the delta value rounded down
        // to the number of decimal places stored in the PointUnit table for that point
        for (CapBankPointDelta pointDelta: pointDeltas) {
            LitePointUnit pointUnit = pointDao.getPointUnit(pointDelta.getPointId());
            BigDecimal bdDelta = new BigDecimal(pointDelta.getDelta()).setScale(pointUnit.getDecimalPlaces(),
                                                               RoundingMode.HALF_DOWN);
            pointDelta.setDeltaRounded(bdDelta.doubleValue());
        }
        
        model.addAttribute("searchResults", searchResults);
    }

    private void setupBreadCrumbs(ModelMap model, YukonUserContext context, int zoneId,
                                  Boolean isSpecialArea) {
        LiteYukonUser user = context.getYukonUser();
        if (isSpecialArea == null) {
            isSpecialArea = false;
        }
        model.addAttribute("isSpecialArea", isSpecialArea);

        CapControlCache cache = filterCacheFactory.createUserAccessFilteredCache(user);
        AbstractZone zoneDto = zoneDtoHelper.getAbstractZoneFromZoneId(zoneId, user);
        setupBreadCrumbs(model, cache, zoneDto, isSpecialArea);
    }

    private void setupBreadCrumbs(ModelMap model, CapControlCache cache, AbstractZone zoneDto,
                                  boolean isSpecialArea) {

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
        
        model.addAttribute("bc_areaId", area.getCcId());
        model.addAttribute("bc_areaName", areaName);
        model.addAttribute("substationId", station.getCcId());
        model.addAttribute("substationName", substationName);
        model.addAttribute("subBusId", subBusId);
        model.addAttribute("subBusName", subBusName);
        model.addAttribute("zoneName", zoneName);
    }
    
    @InitBinder
    public void setupBinder(WebDataBinder binder) {
        binder.registerCustomEditor(PaoIdentifier.class, new PaoIdentifierPropertyEditor());
    }

}