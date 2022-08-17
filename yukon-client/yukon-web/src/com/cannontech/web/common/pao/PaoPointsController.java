package com.cannontech.web.common.pao;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.updater.UpdateValue;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/pao/*")
public class PaoPointsController {
    
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private YukonPointHelper yukonPointHelper;
    
    @RequestMapping("{paoId}")
    public @ResponseBody LiteYukonPAObject pao(HttpServletResponse resp, @PathVariable int paoId) {
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        if (pao == null) resp.setStatus(HttpStatus.NOT_FOUND.value());
        
        return pao;
    }
   
    @RequestMapping("{paoId}/points")
    public String points(ModelMap model, @PathVariable int paoId, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "POINTNAME") SortingParameters sorting) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        List<LiteYukonPoint> liteYukonPoints = yukonPointHelper.getYukonPoints(pao, sorting, accessor);
        
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(pao).getName());
        model.addAttribute("points", liteYukonPoints);
        model.addAttribute("pao", pao);
        model.addAttribute("paoId", paoId);
        model.addAttribute("deviceId", paoId);
        
        buildColumn(model, accessor, PointSortField.ATTRIBUTE, sorting);
        buildColumn(model, accessor, PointSortField.POINTNAME, sorting);
        buildColumn(model, accessor, PointSortField.POINTTYPE, sorting);
        buildColumn(model, accessor, PointSortField.POINTOFFSET, sorting);
        
        return "pao/points.jsp";
    }
    
    @RequestMapping("{paoId}/points-simple")
    public String pointsSimple(ModelMap model, @PathVariable int paoId, YukonUserContext userContext,
            @DefaultSort(dir = Direction.asc, sort = "POINTNAME") SortingParameters sorting) {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        List<LiteYukonPoint> liteYukonPoints = yukonPointHelper.getYukonPoints(pao, sorting, accessor);
        
        model.addAttribute("points", liteYukonPoints);
        model.addAttribute("pao", pao);
        
        buildColumn(model, accessor, PointSortField.POINTNAME, sorting);
        
        return "pao/points-simple.jsp";
    }
    
    private void buildColumn(ModelMap model, MessageSourceAccessor accessor, PointSortField field, 
            SortingParameters sorting) {
        
        Direction dir = sorting.getDirection();
        PointSortField sort = PointSortField.valueOf(sorting.getSort());
        
        String text = accessor.getMessage(field);
        boolean active = sort == field;
        SortableColumn col = SortableColumn.of(dir, active, text, field.name());
        model.addAttribute(field.name(), col);
    }
    
    @RequestMapping("{paoId}/download")
    public void download(HttpServletResponse resp, YukonUserContext context, @PathVariable int paoId) throws IOException {
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);

        String[] headerRow = new String[8];
        
        headerRow[0] = accessor.getMessage(PointSortField.ATTRIBUTE);
        headerRow[1] = accessor.getMessage(PointSortField.POINTNAME);
        headerRow[2] = accessor.getMessage(PointSortField.VALUE);
        headerRow[3] = accessor.getMessage("yukon.common.units");
        headerRow[4] = accessor.getMessage(PointSortField.TIMESTAMP);
        headerRow[5] = accessor.getMessage(PointSortField.QUALITY);
        headerRow[6] = accessor.getMessage(PointSortField.POINTTYPE);
        headerRow[7] = accessor.getMessage(PointSortField.POINTOFFSET);
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        
        List<LiteYukonPoint> points = yukonPointHelper.getYukonPoints(pao);
        
        List<String[]> dataRows = Lists.newArrayList();
        for (LiteYukonPoint point: points) {
            UpdateValue value = registrationService.getLatestValue(point.getPointId(), Format.VALUE.toString(), context);
            UpdateValue units = registrationService.getLatestValue(point.getPointId(), Format.UNIT.toString(), context);
            UpdateValue date = registrationService.getLatestValue(point.getPointId(), Format.DATE.toString(), context);
            UpdateValue quality = registrationService.getLatestValue(point.getPointId(), Format.QUALITY.toString(), context);
            String[] dataRow = new String[8];
            if (point.getAttribute() != null) {
                dataRow[0] = accessor.getMessage(point.getAttribute().getMessage());
            }
            dataRow[1] = point.getPointName();
            dataRow[2] = value.getValue();
            dataRow[3] = units.getValue();
            dataRow[4] = date.getValue();
            dataRow[5] = quality.getValue();
            dataRow[6] = accessor.getMessage(point.getPaoPointIdentifier().getPointIdentifier().getPointType().getFormatKey());
            dataRow[7] = Integer.toString(point.getPaoPointIdentifier().getPointIdentifier().getOffset());
            
            dataRows.add(dataRow);
        }
        
        //write out the file
        WebFileUtils.writeToCSV(resp, headerRow, dataRows, pao.getPaoName() + "_Points.csv");
    }
    
}