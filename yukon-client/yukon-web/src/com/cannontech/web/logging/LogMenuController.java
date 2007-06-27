package com.cannontech.web.logging;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.PoolManager;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.util.ServletUtil;

/**
 * LogMenuController handles the retrieving of log
 * file names from the local and remote log directories,
 * and returns names in two lists thru the ModelAndView
 * @see view for this controller: menu.jsp
 * @author dharrington
 */
public class LogMenuController extends LogController {
    private AuthDao authDao;
    private PoolManager poolManager;
    
    /**
    * Stores all log filenames from local and remote directories
    * in two lists and then saves the lists in the ModelAndView
    * @Override
    * @return a model and view containing lists of log files
    * names (a menu of file names)
    */
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        authDao.verifyRole(ServletUtil.getYukonUser(request), AdministratorRole.ROLEID);
        
        ModelAndView mav = new ModelAndView("menu");
        
        mav.addObject("versionDetails", VersionTools.getYukonDetails());
        mav.addObject("dbUser", poolManager.getPrimaryUser());
        mav.addObject("dbUrl", poolManager.getPrimaryUrl());
       
        //lists to hold log file names
        List<String> localLogList = new ArrayList<String>();
               
        //create file filter to only allow log files thru
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml") || name.endsWith(".log");
            }
        };
        //extract local log file names and add to a list
        File[] localFiles = getLocalDir().listFiles(filter);
        if (!ArrayUtils.isEmpty(localFiles)) {
            for (File logFile : localFiles) {
                localLogList.add(logFile.getName());
            }
        }
        
        //add local list to model
        mav.addObject("localLogList", localLogList);
        
        return mav;
    }
    
    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }

    @Required
    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }
}
