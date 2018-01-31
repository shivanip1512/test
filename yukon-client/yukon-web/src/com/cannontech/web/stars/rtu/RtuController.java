package com.cannontech.web.stars.rtu;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.capcontrol.service.CbcHelperService;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.rtu.dao.RtuDnpDao.SortBy;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.rtu.model.RtuPointDetail;
import com.cannontech.common.rtu.model.RtuPointsFilter;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointInfo;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.TimeIntervals;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.editor.CapControlCBC;
import com.cannontech.yukon.IDatabaseCache;

@Controller
public class RtuController {
    
    @Autowired private RtuDnpService rtuDnpService;
    @Autowired private PointDao pointDao;
    @Autowired private IDatabaseCache cache;
    @Autowired private CbcHelperService cbcHelperService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
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

        model.addAttribute("pointNames", allPointNames);
        model.addAttribute("details", details);
        model.addAttribute("rtuId", id);
        model.addAttribute("filter", filter);

        model.addAttribute("pointTypes", types);
        model.addAttribute("devices", rtu.getChildDevices());

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

}