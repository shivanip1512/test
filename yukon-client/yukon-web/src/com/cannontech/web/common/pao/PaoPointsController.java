package com.cannontech.web.common.pao;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.pao.service.YukonPointHelper;
import com.cannontech.web.updater.point.PointDataRegistrationService;

@Controller
@RequestMapping("/device/*")
public class PaoPointsController extends PointsControllerBase {
    
    /**
     * Get the list of all points for the device specified and order them as requested.
     * @param model - the model
     * @param deviceId - the device id of the device whose point list we want to see
     * @param orderBy - ordering criterion
     * @param descending - whether or not the sorting is in descending order
     */
    @RequestMapping("points")
    public String points(ModelMap model, int deviceId, String orderBy, Boolean descending) {
        
        model.addAttribute("module", "amr");
        model.addAttribute("page", "meterPoints");
        
        return super.points(model, deviceId, orderBy, descending);
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
    @RequestMapping("download")
    public void download(HttpServletResponse response, YukonUserContext context, int deviceId, String orderBy, Boolean descending) throws IOException {
        super.download(response, context, deviceId, orderBy, descending);
    }
    
}