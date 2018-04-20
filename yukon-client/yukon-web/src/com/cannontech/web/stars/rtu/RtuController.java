package com.cannontech.web.stars.rtu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rtu.dao.RtuDnpDao.SortBy;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.rtu.model.RtuPointDetail;
import com.cannontech.common.rtu.model.RtuPointsFilter;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeListType;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.deviceConfiguration.enumeration.DnpTimeOffset.Offsets;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.rtu.service.RtuService;
import com.cannontech.web.stars.rtu.validator.RtuDnpValidator;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
public class RtuController {
    
    @Autowired private RtuDnpService rtuDnpService;
    @Autowired private PointDao pointDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private CbcHelperService cbcHelperService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RtuService rtuService;
    @Autowired private DeviceConfigurationDao deviceConfigDao;
    @Autowired private RtuDnpValidator validator;

    private static final String baseKey = "yukon.web.modules.operator.rtuDetail.";

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value = "rtu-list", method = RequestMethod.GET)
    public String list(ModelMap model, @DefaultSort(dir = Direction.asc, sort = "type") SortingParameters sorting,
            PagingParameters paging, YukonUserContext userContext,
            @RequestParam(name = "rtuType", required = false) String rtuType) {
        SearchResults<LiteYukonPAObject> searchResults = new SearchResults<>();
        List<PaoType> rtuTypes = null;

        if (rtuType == null || StringUtils.equals("AllTypes", rtuType)) {
            rtuTypes = PaoType.getRtuTypes().asList();
        } else {
            rtuTypes = Lists.newArrayList(PaoType.valueOf(rtuType));
            model.addAttribute("selectedRtuType", rtuType);
        }

        List<LiteYukonPAObject> filteredRtus = rtuService.getRtusByType(rtuTypes);
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, filteredRtus.size());

        RtuSortBy sortBy = RtuSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();

        List<LiteYukonPAObject> itemList = Lists.newArrayList(filteredRtus);
        Comparator<LiteYukonPAObject> comparator = (o1, o2) -> {
            return o1.getPaoName().compareToIgnoreCase(o2.getPaoName());
        };

        if (sortBy == RtuSortBy.type) {
            comparator = (o1, o2) -> o1.getPaoType().compareTo(o2.getPaoType());
        }

        if (sortBy == RtuSortBy.status) {
            comparator = (o1, o2) -> o1.getDisableFlag().compareToIgnoreCase(o2.getDisableFlag());
        }

        if (sorting.getDirection() == Direction.desc) {
            comparator = Collections.reverseOrder(comparator);
        }
        Collections.sort(itemList, comparator);

        itemList = itemList.subList(startIndex, endIndex);
        searchResults.setBounds(startIndex, itemsPerPage, filteredRtus.size());
        searchResults.setResultList(itemList);

        model.addAttribute("rtus", searchResults);
        model.addAttribute("rtuTypes", PaoType.getRtuTypes());

        List<SortableColumn> columns = new ArrayList<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        for (RtuSortBy column : RtuSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

        return "/rtu/list.jsp";
    }
    
    @RequestMapping(value = "rtu/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id, FlashScope flash, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.VIEW);
        RtuDnp rtu = rtuDnpService.getRtuDnp(id);
        getPointsForModel(rtu.getId(), model);
        List<MessageSourceResolvable> duplicatePointMessages =
            rtuService.generateDuplicatePointsErrorMessages(rtu.getId(), request);
        flash.setError(duplicatePointMessages, FlashScopeListType.NONE);
        return setupModel(rtu, model);
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value = "rtu/{id}/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.EDIT);
        RtuDnp rtu = rtuDnpService.getRtuDnp(id);
        getPointsForModel(id, model);
        return setupModel(rtu, model);
    }

    @RequestMapping(value = "rtu/create", method = RequestMethod.GET)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        RtuDnp rtu = new RtuDnp();
        DNPConfiguration config = deviceConfigDao.getDnpConfiguration((deviceConfigDao.getDefaultDNPConfiguration()));
        rtu.setDnpConfigId(config.getConfigurationId());
        return setupModel(rtu, model);
    }

    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    @RequestMapping(value = "rtu/save", method = RequestMethod.POST)
    public String save(@ModelAttribute("rtu") RtuDnp rtu, BindingResult result, RedirectAttributes redirectAttributes,
            FlashScope flash) {
        validator.validate(rtu, result);
        if (result.hasErrors()) {
            return bindAndForward(rtu, result, redirectAttributes);
        }
        int paoId = rtuDnpService.save(rtu);
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "info.saved"));
        return "redirect:/stars/rtu/" + paoId;
    }
    
    @RequestMapping(value = "rtu/child/{id}/points", method = RequestMethod.GET)
    public String getPoints(ModelMap model, @PathVariable int id) {
        getPointsForModel(id, model);
        return "/rtu/childPoints.jsp";
    }
    
    @RequestMapping(value = "rtu/{id}/allPoints", method = RequestMethod.GET)
    public String getAllPoints(ModelMap model, @PathVariable int id, @ModelAttribute("filter") RtuPointsFilter filter, BindingResult bindingResult,
           @DefaultSort(dir=Direction.asc, sort="pointName") SortingParameters sorting, @DefaultItemsPerPage(value=250) PagingParameters paging, YukonUserContext userContext) {

        RtuPointsSortBy sortBy = RtuPointsSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        RtuDnp rtu = rtuDnpService.getRtuDnp(id);
        SearchResults<RtuPointDetail> details = rtuDnpService.getRtuPointDetail(id, filter, dir, sortBy.getValue(), paging);
        
        Map<RtuPointDetail, String> pointFormats = cbcHelperService.getPaoTypePointFormats(rtu.getPaoType(), details.getResultList(), RtuPointDetail::getPointId, r -> r.getPaoPointIdentifier().getPointIdentifier());
        
        pointFormats.forEach((rpd, format) -> rpd.setFormat(format));

        List<RtuPointDetail> rtuPointDetails = rtuDnpService.getRtuPointDetail(id);
        List<PointType> types = rtuPointDetails.stream()
                                               .map(p -> p.getPaoPointIdentifier().getPointIdentifier().getPointType())
                                               .distinct()
                                               .sorted(Comparator.comparing(PointType::getPointTypeString))
                                               .collect(Collectors.toList());

        List<String> allPointNames = rtuPointDetails.stream()
                                                    .map(p -> p.getPointName())
                                                    .distinct()
                                                    .sorted()
                                                    .collect(Collectors.toList());

        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        for (RtuPointsSortBy column : RtuPointsSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        List<DisplayableDevice> devices = new ArrayList<>();
        devices.add(new DisplayableDevice(rtu.getPaoIdentifier(), rtu.getName()));
        devices.addAll(rtu.getChildDevices());

        model.addAttribute("pointNames", allPointNames);
        model.addAttribute("details", details);
        model.addAttribute("rtuId", id);
        model.addAttribute("filter", filter);

        model.addAttribute("pointTypes", types);
        model.addAttribute("devices", devices);

        return "/rtu/allPoints.jsp";
    }
    
    private Map<PointType, List<PointInfo>> getPointsForModel(int paoId, ModelMap model) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);

        Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(paoId);

        List<PointInfo> pointList = points.values().stream().flatMap(List::stream).collect(Collectors.toList()); 
        
        //check for special formats
        Map<PointInfo, String> pointFormats = 
                        cbcHelperService.getPaoTypePointFormats(pao.getPaoType(), pointList, PointInfo::getPointId, PointInfo::getPointIdentifier);
        
        pointFormats.forEach((pi, format) -> pi.setFormat(format));
        
        model.addAttribute("points", points);

        return points;
    }
    
    private String bindAndForward(RtuDnp rtu, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("rtu", rtu);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.rtu", result);
        if (rtu.getId() == null) {
            return "redirect:create";
        }
        return "redirect:" + rtu.getId() + "/edit";
    }
    
    private String setupModel(RtuDnp rtu, ModelMap model) {
        if (model.containsAttribute("rtu")) {
            rtu = (RtuDnp) model.get("rtu");
        }
        
        model.addAttribute("threeClassTimeIntervals", TimeIntervals.getUpdateAndScanRate());
        model.addAttribute("fourClassTimeIntervals", TimeIntervals.getScanIntervals());
        model.addAttribute("altTimeIntervals", TimeIntervals.getAltIntervals());
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        model.addAttribute("availablePorts", cache.getAllPorts());
        model.addAttribute("rtu", rtu);
        
        List<LightDeviceConfiguration> configs = deviceConfigDao.getAllConfigurationsByType(rtu.getPaoType());
        model.addAttribute("configs", configs);
        
        DeviceConfiguration deviceConfiguration = deviceConfigDao.getDeviceConfiguration(rtu.getDnpConfigId());
        DNPConfiguration dnpConfig = deviceConfigDao.getDnpConfiguration(deviceConfiguration);
        if (dnpConfig == null) {
            dnpConfig = new DNPConfiguration(deviceConfiguration.getConfigurationId(), deviceConfiguration.getName(),
                deviceConfiguration.getDescription());
            dnpConfig.setTimeOffset(Offsets.UTC.toString());
            model.addAttribute("isDnpConfigCategoryAssigned", false);
        } else {
            model.addAttribute("isDnpConfigCategoryAssigned", true);
        }
        
        model.addAttribute("dnpConfig", dnpConfig);
        
        return "/rtu/rtuDetail.jsp";
    }

    public enum RtuPointsSortBy implements DisplayableEnum {

        pointName(SortBy.POINT_NAME),
        offset(SortBy.POINT_OFFSET),
        deviceName(SortBy.DEVICE_NAME),
        pointType(SortBy.POINT_TYPE);


        private RtuPointsSortBy(SortBy value) {
            this.value = value;
        }

        private final SortBy value;

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.rtuDetail." + name();
        }
    }
    
    public enum RtuSortBy implements DisplayableEnum {

        name,
        type,
        status;

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }

}