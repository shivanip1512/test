package com.cannontech.web.widget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.Lists;

/**
 * Widget used to display the Points for a Device
 */
@Controller
@RequestMapping("/devicePointsWidget/*")
public class DevicePointsWidget extends AdvancedWidgetControllerBase {
    
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private YukonPointHelper yukonPointHelper;
    @Autowired private YukonUserContextMessageSourceResolver resolver;

    @Autowired
    public DevicePointsWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput,
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @GetMapping("render")
    public String render(ModelMap model, YukonUserContext userContext, HttpServletRequest request,
                         @DefaultSort(dir = Direction.asc, sort = "POINTNAME") SortingParameters sorting, 
                         @DefaultItemsPerPage(value=250) PagingParameters paging) throws ServletRequestBindingException {
        Integer deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
        model.addAttribute("pointTypes", PointType.values());
        retrievePointsForModel(deviceId, PointType.values(), model, userContext, sorting, paging);
        return "devicePointsWidget/render.jsp";
    }
    
    @GetMapping("pointsTable")
    public String pointsTable(ModelMap model, int deviceId, PointType[] pointTypes, YukonUserContext userContext,
                         @DefaultSort(dir = Direction.asc, sort = "POINTNAME") SortingParameters sorting, 
                         @DefaultItemsPerPage(value=250) PagingParameters paging) {
        retrievePointsForModel(deviceId, pointTypes, model, userContext, sorting, paging);
        return "devicePointsWidget/pointsTable.jsp";
    }
    
    private void retrievePointsForModel(int deviceId, PointType[] pointTypes, ModelMap model, YukonUserContext userContext,
                                        SortingParameters sorting, PagingParameters paging) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(deviceId);
        model.addAttribute("device", pao);
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        
        //TODO: Replace with Points REST API when ready
        if (pointTypes == null) {
            pointTypes = PointType.values();
        }
        List<LiteYukonPoint> liteYukonPoints = yukonPointHelper.getYukonPoints(pao, sorting, accessor);
        List<PointType> pointTypeList = Arrays.asList(pointTypes);
        liteYukonPoints = liteYukonPoints.stream()
                .filter(point -> pointTypeList.contains(point.getPaoPointIdentifier().getPointIdentifier().getPointType())).collect(Collectors.toList());
        
        List<LiteYukonPoint>itemList = Lists.newArrayList(liteYukonPoints);
        
        SearchResults<LiteYukonPoint> searchResult = new SearchResults<>();
        int startIndex = paging.getStartIndex();
        int itemsPerPage = paging.getItemsPerPage();
        int endIndex = Math.min(startIndex + itemsPerPage, liteYukonPoints.size());
        
        PointSortField sortBy = PointSortField.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        itemList = itemList.subList(startIndex, endIndex);
        searchResult.setBounds(startIndex, itemsPerPage, liteYukonPoints.size());
        searchResult.setResultList(itemList);
        
        for (PointSortField column : PointSortField.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            model.addAttribute(column.name(), col);
        }

        model.addAttribute("points", searchResult);
    }

}