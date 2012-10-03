package com.cannontech.web.bulkimporter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@CheckRoleProperty(YukonRoleProperty.IMPORTER_ENABLED)
public class BulkImporterHomeController implements Controller  {

    @Autowired private YukonSettingsDao yukonSettingsDao = null;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ModelAndView mav = new ModelAndView("bulkimporter/importer.jsp");
        
        // messages from importFile redirect
        String msgType = ServletRequestUtils.getStringParameter(request, "msgType");
        String msgStr = ServletRequestUtils.getStringParameter(request, "msgStr");
        mav.addObject("msgType", msgType);
        mav.addObject("msgStr", msgStr);
        
        // init results table headers
        addColumnInfoToMav(mav);
        
        // checking importer for communication access
        boolean isCommunicationEnabled = yukonSettingsDao.getSettingBooleanValue(YukonSetting.BULK_IMPORTER_COMMUNICATIONS_ENABLED);
        mav.addObject("importerCommunicationsEnabled", isCommunicationEnabled);
        
        return mav;
    }
    
    
    /**
     * Add lists of column names to mav in order to setup the results tables
     * 
     * @param mav
     */
    private void addColumnInfoToMav(ModelAndView mav) {
        
        String[] FailureColumnNames = {"Name", "Reason For Failure", "Date / Time"};
        String[] PendingCommColumnNames = {"Name", "Route", "Substation"};
        String[] FailureCommColumnNames = {"Name", "Route", "Substation", "Error", "Date / Time"};
        
        mav.addObject("refreshRate", "15000");
        mav.addObject("failureColumnNames", FailureColumnNames);
        mav.addObject("pendingCommsColumnNames", PendingCommColumnNames);
        mav.addObject("failureCommsColumnNames", FailureCommColumnNames);
    }

}