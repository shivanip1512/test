package com.cannontech.web.stars.rtu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;

@Controller
public class RtuController {
    
    @Autowired private RtuDnpService rtuDnpService;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;
    @Autowired private IDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private static final Map<BuiltInAttribute,String> formatMappings = ImmutableMap.<BuiltInAttribute,String>builder()
            .put(BuiltInAttribute.FIRMWARE_VERSION, "{rawValue|firmwareVersion}")
            .put(BuiltInAttribute.IP_ADDRESS, "{rawValue|ipAddress}")
            .put(BuiltInAttribute.NEUTRAL_CURRENT_SENSOR, "{rawValue|neutralCurrent}")
            .put(BuiltInAttribute.SERIAL_NUMBER, "{rawValue|long}")
            .put(BuiltInAttribute.UDP_PORT, "{rawValue|long}")
            .put(BuiltInAttribute.LAST_CONTROL_REASON, "{rawValue|lastControlReason}")
            .put(BuiltInAttribute.IGNORED_CONTROL_REASON, "{rawValue|ignoredControlReason}")
            .build();
    
    @RequestMapping(value = "rtu/{id}", method = RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        model.addAttribute("timeIntervals", TimeIntervals.getCapControlIntervals());
        model.addAttribute("scanGroups", CapControlCBC.ScanGroup.values());
        model.addAttribute("availablePorts", cache.getAllPorts());
        RtuDnp rtu = rtuDnpService.getRtuDnp(id);
        model.addAttribute("rtu", rtu);
        getPointsForModel(id, model);
        return "/rtu/rtuDetail.jsp";
    }
    
    @RequestMapping(value = "rtu/child/{id}/points", method = RequestMethod.GET)
    public String getPoints(ModelMap model, @PathVariable int id) {
        getPointsForModel(id, model);
        return "/rtu/childPoints.jsp";
    }
    
    @RequestMapping(value = "rtu/{id}/allPoints", method = RequestMethod.GET)
    public String getAllPoints(ModelMap model, @PathVariable int id, @ModelAttribute("filter") RtuPointsFilter filter, BindingResult bindingResult,
           @DefaultSort(dir=Direction.asc, sort="pointName") SortingParameters sorting, PagingParameters paging, YukonUserContext userContext) {
        //TODO: Call new method to get all points
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        RtuPointsSortBy sortBy = RtuPointsSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        for (RtuPointsSortBy column : RtuPointsSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }
        Map<PointType, List<PointInfo>> allPoints = new HashMap<>();
        allPoints.putAll(getPointsForModel(id, model));
        RtuDnp rtu = rtuDnpService.getRtuDnp(id);
        for (DisplayableDevice device : rtu.getChildDevices()) {
            allPoints.putAll(getPointsForModel(device.getPaoIdentifier().getPaoId(), model));
        }
        List<String> allPointNames = new ArrayList<>();
        for (List<PointInfo> info : allPoints.values()) {
            info.forEach(p -> {
                if (!allPointNames.contains(p.getName())) {
                    allPointNames.add(p.getName());
                }
            });
        }
        Collections.sort(allPointNames, Ordering.natural());
        model.addAttribute("pointNames", allPointNames);
        model.addAttribute("rtuId", id);
        model.addAttribute("points", allPoints);
        model.addAttribute("filter", filter);
        List<PointType> types = Arrays.asList(PointType.values());
        Comparator<PointType> comparator = (o1, o2) -> o1.getPointTypeString().compareTo(o2.getPointTypeString());
        Collections.sort(types, comparator);
        model.addAttribute("pointTypes", types);
        model.addAttribute("devices", rtu.getChildDevices());
        
        return "/rtu/allPoints.jsp";
    }
    
    private Map<PointType, List<PointInfo>> getPointsForModel(int paoId, ModelMap model) {
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        Map<PointType, List<PointInfo>> points = pointDao.getAllPointNamesAndTypesForPAObject(paoId);
        //check for special formats
        for(List<PointInfo> pointList : points.values()){
            for(PointInfo point : pointList){
                LitePoint litePoint = pointDao.getLitePoint(point.getPointId());
                PointIdentifier pid = new PointIdentifier(point.getType(), litePoint.getPointOffset());
                PaoTypePointIdentifier pptId = PaoTypePointIdentifier.of(pao.getPaoType(), pid);
                //This set should contain 0 items if there is not a special format, or 1 if there is
                Set<BuiltInAttribute> attributes = attributeService.findAttributesForPoint(pptId, formatMappings.keySet());
                for (BuiltInAttribute attribute: attributes) {
                    if (formatMappings.get(attribute) != null) {
                        point.setFormat(formatMappings.get(attribute));
                    }
                }
            }
        }
        model.addAttribute("points", points);
        return points;
    }
    
    public enum RtuPointsSortBy implements DisplayableEnum {

        pointName,
        pointValue,
        dateTime,
        offset,
        deviceName,
        pointType;

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.operator.rtuDetail." + name();
        }
    }
    
}