package com.cannontech.web.logging;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.util.LogSortUtil;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.PoolManager;
import com.cannontech.roles.operator.AdministratorRole;
import com.cannontech.util.ServletUtil;

/**
 * LogMenuController handles the retrieving of log file names from the local and
 * remote log directories, and returns names in two lists thru the ModelAndView
 * @see view for this controller: menu.jsp
 * @author dharrington
 */
public class LogMenuController extends LogController {

    private PoolManager poolManager;

    /**
     * Stores all log filenames from local and remote directories in two lists
     * and then saves the lists in the ModelAndView
     * @Override
     * @return a model and view containing lists of log files names (a menu of
     *         file names)
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        authDao.verifyRole(ServletUtil.getYukonUser(request),
                           AdministratorRole.ROLEID);

        // Sets up the ModelAndView Object
        ModelAndView mav = new ModelAndView("menu.jsp");

        mav.addObject("versionDetails", VersionTools.getYukonDetails());
        mav.addObject("dbUser", poolManager.getPrimaryUser());
        mav.addObject("dbUrl", poolManager.getPrimaryUrl());

        // Checks the request for parameters to update the defaults
        String sortType = ServletRequestUtils.getStringParameter(request,
                                                                 "sortType",
                                                                 "alphabetic");
        String root = ServletRequestUtils.getStringParameter(request,
                                                             "root",
                                                             "/");

        // boolean reverse = ServletRequestUtils.getBooleanParameter(request,
        // "reverse", false);
        List<File> localLogList = new ArrayList<File>();
        List<String> dirSet = null;
        SortedMap<String, List<String>> resultSet = new TreeMap<String, List<String>>();

        // lists to hold log file names
        File currentDir = new File(localDir, root);
        localLogList = populateFileList(currentDir);

        if (!localLogList.isEmpty()) {
            // Checks to see how the user wants the information setup
            if (sortType.equalsIgnoreCase("date")) {
                resultSet = this.sortByDate(localLogList);
            } else {
                resultSet = this.sortByAlphabet(localLogList);
            }
        }

        // Seperates the directories from the logFiles
        if (resultSet.containsKey("Directories")) {
            dirSet = new ArrayList<String>(resultSet.get("Directories"));
            resultSet.remove("Directories");
        }

        // add local list to model
        mav.addObject("oldStateSort", sortType);
        mav.addObject("oldStatePath", root);
        mav.addObject("dirList", dirSet);
        mav.addObject("localLogList", resultSet);

        return mav;
    }

    private List<File> populateFileList(File currentDir) {
        List<File> folderListings = new ArrayList<File>();

        // Itereates through all the File objects in the given directory
        if (currentDir != null) {
            File[] localDirAndFiles = currentDir.listFiles();
            for (File fileObj : localDirAndFiles) {

                // Checks to see if the obj is a directory and adds it to the
                // results
                if (fileObj.isDirectory()) {
                    folderListings.add(fileObj);
                }
                // Checks to see if the obj is a log file and adds it to the
                // results
                if (this.isLog(fileObj.getName())) {
                    folderListings.add(fileObj);
                }
            }
        }
        return folderListings;
    }

    /**
     * @param localLogList
     * @return
     * @throws Exception
     */
    private SortedMap<String, List<String>> sortByAlphabet(List<File> localLogList) throws Exception {
        Pattern logPattern = Pattern.compile("^(.*)\\.log$");
        Map<String, String> searchMap = LogSortUtil.returnSearchMap(localLogList,logPattern);
        SortedMap<String, List<String>> sortResults = LogSortUtil.sortSearchMap(searchMap, 2);
        return sortResults;
    }

    /**
     * @param localLogList
     * @return
     * @throws Exception
     */
    private SortedMap<String, List<String>> sortByDate(List<File> localLogList)
            throws Exception {

        Map<String, String> searchMap = LogSortUtil.returnSearchMap(localLogList);
        SortedMap<String, List<String>> sortResults = LogSortUtil.sortSearchMap(searchMap);
        return sortResults;
    }

    /**
     * This method checks to see if the file extension is the right type
     * @param logName
     * @return
     */
    public boolean isLog(String logName) {
        return ((logName.endsWith("log")) || (logName.endsWith("xml")));
    }

    @Required
    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

}
