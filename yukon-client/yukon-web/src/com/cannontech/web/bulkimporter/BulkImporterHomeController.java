package com.cannontech.web.bulkimporter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.cannontech.core.dao.RoleDao;
import com.cannontech.roles.yukon.SystemRole;

public class BulkImporterHomeController implements Controller  {

    private RoleDao roleDao = null;

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
        boolean isCommunicationEnabled = Boolean.parseBoolean(roleDao.getGlobalPropertyValue(SystemRole.BULK_IMPORTER_COMMUNICATIONS_ENABLED));
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

    @Required
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
}