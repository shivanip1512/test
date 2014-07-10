package com.cannontech.web.common.pao;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.LiteYukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.updater.UpdateValue;
import com.cannontech.web.updater.point.PointDataRegistrationService;
import com.cannontech.web.util.WebFileUtils;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/device/*")
public class PaoPointsController {
    
    @Autowired private DeviceDao deviceDao;
    @Autowired private PaoLoadingService paoLoadingService;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private PointDataRegistrationService registrationService;
    @Autowired private YukonPointHelper yukonPointHelper;
    
    @RequestMapping("points")
    public String points(ModelMap model, int deviceId, YukonUserContext userContext,
            @DefaultSort(dir=Direction.asc, sort="POINTNAME") SortingParameters sorting) {
        
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        List<LiteYukonPoint> liteYukonPoints = yukonPointHelper.getYukonPoints(device, sorting);
        
        model.addAttribute("device", device);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("points", liteYukonPoints);
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        buildColumn(model, accessor, PointSortField.ATTRIBUTE, sorting);
        buildColumn(model, accessor, PointSortField.POINTNAME, sorting);
        buildColumn(model, accessor, PointSortField.POINTTYPE, sorting);
        buildColumn(model, accessor, PointSortField.POINTOFFSET, sorting);
        
        return "device/points.jsp";
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
    
    @RequestMapping("download")
    public void download(HttpServletResponse response, YukonUserContext context, int deviceId) throws IOException {
        
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
        
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        
        List<LiteYukonPoint> points = yukonPointHelper.getYukonPoints(device);
        
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
        
        String deviceName = paoLoadingService.getDisplayablePao(device).getName();
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, deviceName + "_Points.csv");
    }
    
}