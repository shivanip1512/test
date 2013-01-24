package com.cannontech.web.common.pao;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.YukonPoint;
import com.cannontech.web.common.pao.service.YukonPointHelper;
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
    
    /**
     * Get the list of all points for the device specified and order them as requested.
     * @param model - the model
     * @param request - the request
     * @param deviceId - the device id of the device whose point list we want to see
     * @param orderBy - ordering criterion
     * @param descending - whether or not the sorting is in descending order
     */
    @RequestMapping(method = RequestMethod.GET)
    public String points(ModelMap model, HttpServletRequest request, int deviceId, String orderBy, Boolean descending) {
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);

        List<YukonPoint> yukonPoints = yukonPointHelper.getYukonPoints(deviceId, orderBy, descending);
        
        model.addAttribute("device", device);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("deviceName", paoLoadingService.getDisplayablePao(device).getName());
        model.addAttribute("points", yukonPoints);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("descending", descending);
        
        return "device/points.jsp";
    }
    
    /**
     * Requests the point data for a specific device to be written out to a CSV file ordered by
     * user-defined criteria
     * @param response - the response
     * @param context - the user context (used for string formatting)
     * @param deviceId - the device id of the device whose point list we want to see
     * @param orderBy - ordering criterion
     * @param descending - whether or not the sorting is in descending order
     * @throws IOException if an error occurs writing the data to the CSV file
     */
    @RequestMapping(value="/device/download")
    public void download(HttpServletResponse response, YukonUserContext context, int deviceId, String orderBy, Boolean descending) throws IOException {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);

        String[] headerRow = new String[8];
        
        headerRow[0] = accessor.getMessage("yukon.common.attribute");
        headerRow[1] = accessor.getMessage("yukon.web.modules.amr.meterPoints.columnHeader.pointName.linkText");
        headerRow[2] = accessor.getMessage("yukon.common.value");
        headerRow[3] = accessor.getMessage("yukon.common.units");
        headerRow[4] = accessor.getMessage("yukon.common.timestamp");
        headerRow[5] = accessor.getMessage("yukon.common.quality");
        headerRow[6] = accessor.getMessage("yukon.common.events.pointType");
        headerRow[7] = accessor.getMessage("yukon.common.events.pointOffset");
        
        orderBy = (orderBy == null) ? PointSortField.POINTNAME.name() : orderBy;
        descending = (descending == null) ? false : descending;
        List<YukonPoint> points = yukonPointHelper.getYukonPoints(deviceId, orderBy, descending);
        
        List<String[]> dataRows = Lists.newArrayList();
        for (YukonPoint point: points) {
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
        
        final SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        String deviceName = paoLoadingService.getDisplayablePao(device).getName();
        
        //write out the file
        WebFileUtils.writeToCSV(response, headerRow, dataRows, deviceName + "_Points.csv");
    }
    
}