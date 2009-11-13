package com.cannontech.web.support.logging;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.common.i18n.CollationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * LogMenuController handles the retrieving of log file names from the local and
 * remote log directories, and returns names in two lists thru the ModelAndView
 * @see view for this controller: menu.jsp
 * @author dharrington
 */
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
@Controller
public class LogMenuController extends LogController {

    private DateFormattingService dateFormattingService;
    
    private static final Function<File, String> fileToNameFunction = new Function<File, String> () {
        public String apply(File from) {
            return from.getName();
        }
    };
    
    /**
     * Stores all log filenames from local and remote directories in two lists
     * and then saves the lists in the ModelAndView
     * @Override
     * @return a model and view containing lists of log files names (a menu of
     *         file names)
     */
    @RequestMapping(value = "/logging/menu", method = RequestMethod.GET)
    public String menu(HttpServletRequest request, YukonUserContext userContext,
                       ModelMap map) throws Exception {

        // Checks the request for parameters to update the defaults
        String sortType = ServletRequestUtils.getStringParameter(request,
                                                                 "sortType",
                                                                 "alphabetic");
        File logDir = getLogFile(request);
        Validate.isTrue(logDir.isDirectory());
        
        List<File> localLogList = Lists.newArrayList();
        List<File> localDirectoryList = Lists.newArrayList();

        // lists to hold log file names
        populateFileLists(logDir, localLogList, localDirectoryList);
        
        // get directory names
        List<String> directoryNameList = Lists.transform(localDirectoryList, fileToNameFunction);

        Multimap<String, String> resultSet;
        // Checks to see how the user wants the information setup
        if (sortType.equalsIgnoreCase("date")) {
            resultSet = sortByDate(localLogList, userContext);
        } else {
            resultSet = sortByAlphabet(localLogList, userContext);
        }

        // add local list to model
        map.addAttribute("oldStateSort", sortType);
        map.addAttribute("dirFile", logDir);
        map.addAttribute("file", HtmlUtils.htmlEscape(getFileNameParameter(request)));
        map.addAttribute("dirList", directoryNameList);
        map.addAttribute("localLogList", resultSet.asMap());

        return "logging/menu.jsp";
    }

    private void populateFileLists(File currentDir, Collection<File> localLogList, Collection<File> localDirectoryList) {
        // iterates through everything in the given directory and sort into files and directories
        File[] localDirAndFiles = currentDir.listFiles();
        for (File fileObj : localDirAndFiles) {

            if (fileObj.isDirectory()) {
                localDirectoryList.add(fileObj);
            } else if (this.isLog(fileObj.getName())) {
                localLogList.add(fileObj);
            }
        }
    }
    
    private Ordering<File> getFileNameOrdering(YukonUserContext userContext) {
        Ordering<String> caseInsensitiveOrdering = CollationUtils.getCaseInsensitiveOrdering(userContext);
        Ordering<File> result = caseInsensitiveOrdering.onResultOf(fileToNameFunction);
        return result;
    }

    /**
     * @param localLogList
     * @return
     * @throws Exception
     */
    private LinkedHashMultimap<String, String> sortByAlphabet(List<File> localLogList, YukonUserContext userContext){
        Collections.sort(localLogList, getFileNameOrdering(userContext));
        
        LinkedHashMultimap<String, String> result = LinkedHashMultimap.create();
        // The following pattern looks for a base file name (group 1) that is the
        // leading part of the file name minus an optional underscore, a 1-2 
        // digit number, and the extension.
        Pattern dayNumberPattern = Pattern.compile("(.+?)_?\\d{1,2}\\.[^.]+$");
        
        for (File file : localLogList) {
            String fileName = file.getName();
            Matcher patternMatches = dayNumberPattern.matcher(fileName);
            if (patternMatches.matches()){
                String fileNameGroup = StringUtils.capitalize(patternMatches.group(1));
                result.put(fileNameGroup, fileName);
            } else {
                // give the file name its own personal group
                result.put(fileName, fileName);
            }

        }
        
        return result;
    }

    /**
     * @param localLogList
     * @return
     * @throws Exception
     */
    private LinkedHashMultimap<String, String> sortByDate(List<File> localLogList, YukonUserContext userContext) {
        // LocalDate is a convenient class because it only knows about year, month, day
        Function<File, LocalDate> getLocalDateForFile = new Function<File, LocalDate> () {
            public LocalDate apply(File from) {
                return new LocalDate(from.lastModified());
            }
        };
        
        // sort by reverse modification and use name to resolve ties (each "day" will be a tie)
        Ordering<File> fileModificationOrdering = Ordering.natural().onResultOf(getLocalDateForFile).reverse();
        Ordering<File> compoundOrdering = fileModificationOrdering.compound(getFileNameOrdering(userContext));
        Collections.sort(localLogList, compoundOrdering);
        
        LinkedHashMultimap<String, String> result = LinkedHashMultimap.create();
        
        for (File file : localLogList) {
            // using the default timezone on purpose
            LocalDate modificationAsDate = getLocalDateForFile.apply(file);
            // DateFormattingService ignores the user's timezone when formatting a Partial
            String dateHeading = dateFormattingService.format(modificationAsDate, DateFormatEnum.LONG_DATE, userContext);
            result.put(dateHeading, file.getName());
        }
        
        return result;
    }

    /**
     * This method checks to see if the file extension is the right type
     * @param logName
     * @return
     */
    public boolean isLog(String logName) {
        return ((logName.endsWith("log")) || (logName.endsWith("xml")));
    }

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

}
