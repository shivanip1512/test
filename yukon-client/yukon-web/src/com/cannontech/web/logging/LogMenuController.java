package com.cannontech.web.logging;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
                                                 HttpServletResponse response) throws Exception {
        authDao.verifyRole(ServletUtil.getYukonUser(request), AdministratorRole.ROLEID);
        
        ModelAndView mav = new ModelAndView("menu");
        
        mav.addObject("versionDetails", VersionTools.getYukonDetails());
        mav.addObject("dbUser", poolManager.getPrimaryUser());
        mav.addObject("dbUrl", poolManager.getPrimaryUrl());
       
        //lists to hold log file names
        List<String> localLogList = new ArrayList<String>();
        localLogList = populateFileList(localLogList, new String(), getLocalDir());
        java.util.Collections.reverse(localLogList);

        //add local list to model
        mav.addObject("localLogList", localLogList);
        
        return mav;
    }
    
    private List<String> populateFileList(List<String> localLogList, String parent, File file) {
        if (!file.isDirectory()) return localLogList;
        
        if (!file.getName().equals("Log")) {
            parent = parent + file.getName() + System.getProperty("file.separator");
        }

        File[] localFiles = file.listFiles();
        for (File logFile : localFiles) {
            if (logFile.isDirectory()) {
                localLogList = populateFileList(localLogList, parent, logFile);
            }
            if (logFile.getName().endsWith("xml") || logFile.getName().endsWith("log")) {
                localLogList.add(parent + logFile.getName());
            }
        }
        return localLogList;
    }
    
    @Override
    @Required
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    @Required
    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }
}
