package com.cannontech.web.support.logging;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.CollationUtils;
import com.cannontech.common.util.BinaryPrefix;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.FileUtil;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.input.DatePropertyEditorFactory;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.base.Function;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

/**
 * LogMenuController handles the retrieving of log file names from the local and
 * remote log directories, and returns names in two lists thru the ModelAndView
 * 
 * @see view for this controller: menu.jsp
 */
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
@Controller
public class LogExplorerController {
    private Logger log = YukonLogManager.getLogger(LogExplorerController.class);
    private File localDir = new File(BootstrapUtils.getServerLogDir());

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private DatePropertyEditorFactory datePropertyEditorFactory;

    /**
     * Stores all log filenames from local and remote directories in two lists
     * and then saves the lists in the ModelAndView
     * 
     * @return a model and view containing lists of log files names (a menu of
     *         file names)
     */
    @RequestMapping(value = "/logging/menu", method = RequestMethod.GET)
    public String menu(ModelMap map, @RequestParam(required = false, defaultValue = "/") String file,
            @RequestParam(required = false, defaultValue = "today") String show,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false) LocalDate customStart, @RequestParam(required = false) LocalDate customEnd,
            HttpServletRequest request, FlashScope flashScope, YukonUserContext userContext) throws IOException {

        File logDir = sanitizeAndVerify(new File(localDir, file));
        Validate.isTrue(logDir.isDirectory());

        Instant from;
        Instant to;
        DateTimeZone tz = DateTimeZone.getDefault();
        LocalDate now = LocalDate.now(tz);
        switch (show) {
        case "custom":
            from = TimeUtil.toMidnightAtBeginningOfDay(customStart, tz);
            to = TimeUtil.toMidnightAtEndOfDay(customEnd, tz);
            break;
        case "today":
            from = TimeUtil.toMidnightAtBeginningOfDay(now, tz);
            to = TimeUtil.toMidnightAtEndOfDay(now, tz);
            break;
        case "lastWeek":
            from = TimeUtil.toMidnightAtBeginningOfDay(now.minusDays(6), tz);
            to = TimeUtil.toMidnightAtEndOfDay(now, tz);
            break;
        case "lastMonth":
        default:
            from = TimeUtil.toMidnightAtBeginningOfDay(now.minusMonths(1), tz);
            to = TimeUtil.toMidnightAtEndOfDay(now, tz);
            break;
        }

        map.addAttribute("maxDate", now);
        map.addAttribute("customStart", customStart != null ? customStart.toDate() : from);
        map.addAttribute("customEnd", customEnd != null ? customEnd.toDate() : now.toDate());

        List<File> localLogList = Lists.newArrayList();
        List<File> localDirectoryList = Lists.newArrayList();

        populateFileLists(logDir, localLogList, localDirectoryList, from, to);

        // get directory names
        List<String> directoryNameList = Lists.transform(localDirectoryList, fileToNameFunction);

        Multimap<String, LogFile> results = sortLogs(localLogList, "date".equals(sortBy), userContext);

        String baseDir = localDir.getCanonicalPath().substring(localDir.getCanonicalPath().indexOf('\\')); // Remove
                                                                                                           // C:

        map.addAttribute("isSubDirectory", !isLogRoot(logDir));
        map.addAttribute("currentDirectory", getRootlessFilePath(logDir.getParentFile()));
        map.addAttribute("logBaseDir", baseDir.replace("\\", "/"));
        map.addAttribute("file", HtmlUtils.htmlEscape(file));
        map.addAttribute("directories", directoryNameList);
        map.addAttribute("logList", results.asMap());

        if (results.size() == 0) {
            flashScope.setMessage(
                YukonMessageSourceResolvable.createSingleCode("yukon.web.modules.support.logMenu.noLogsFound"),
                FlashScopeMessageType.WARNING);
        }

        map.addAttribute("sortBy", StringEscapeUtils.escapeXml(sortBy));
        map.addAttribute("show", StringEscapeUtils.escapeXml(show));

        return "logging/menu.jsp";
    }

    /**
     * Extracts a log file/filename from the local or remote
     * directory and stores them in the ModelAndView object.
     */
    @RequestMapping(value = "/logging/view", method = RequestMethod.GET)
    public String viewLog(ModelMap map, @RequestParam(required = false, defaultValue = "/") String file,
            @RequestParam(required = false, defaultValue = "50") int numLines,
            @RequestParam(required = false, defaultValue = "0") int offSet, YukonUserContext userContext)
            throws IOException {

        File logFile = sanitizeAndVerify(new File(localDir, file));
        Validate.isTrue(logFile.isFile());

        long lastModL = logFile.lastModified();
        long fileLengthL = logFile.length();

        // Checks to see if the logFile exists and has the ability to be read
        if ((logFile != null) && (logFile.canRead())) {
            String lastMod =
                dateFormattingService.format(new Date(lastModL), DateFormattingService.DateFormatEnum.BOTH, userContext);
            String fileLength = String.valueOf(fileLengthL / 1024);
            List<String> logLines = FileUtil.readLines(logFile, numLines, offSet);

            map.addAttribute("logLines", logLines);

            String applicationName = fileToApplicationNameFunction.apply(logFile);
            map.addAttribute("logFile", new LogFile(logFile, applicationName));
            String rootlessDirFileString = getRootlessFilePath(logFile.getParentFile());
            map.addAttribute("rootlessDirFileString", rootlessDirFileString);
            map.addAttribute("fileDateMod", lastMod);
            map.addAttribute("fileLength", fileLength);
        } else {
            log.warn("Could not read log file: " + logFile);
        }

        map.addAttribute("file", HtmlUtils.htmlEscape(file));
        map.addAttribute("logFilePath", file);
        map.addAttribute("numLines", numLines);

        map.addAttribute("widePage", true);

        return "logging/logTail.jsp";
    }

    @RequestMapping(value = "/logging/view/update", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> tailUpdate(@RequestBody LogUpdateRequest logupdateRequest, YukonUserContext userContext)
            throws IOException {

        long oldFileLength = logupdateRequest.getFileLength();
        int linesPerUpdate = logupdateRequest.getNumLines();
        String fileName = logupdateRequest.getFile();

        // takes the data and checks to see if the log file has changed
        Map<String, Object> jsonResponse = new HashMap<>();
        List<String> jsonLogLines = new ArrayList<>();
        jsonResponse.put("numLines", linesPerUpdate);

        File logFile = sanitizeAndVerify(new File(localDir, fileName));
        Validate.isTrue(logFile.isFile());

        if (logFile.canRead()) {
            // Setting up the last modified variable for the JSON
            String lastModStr =
                dateFormattingService.format(new Instant(logFile.lastModified()),
                    DateFormattingService.DateFormatEnum.LONG_DATE_TIME, userContext);
            jsonResponse.put("lastModified", lastModStr);

            String readableFileSize =
                messageSourceResolver.getMessageSourceAccessor(userContext).getMessage(
                    BinaryPrefix.getCompactRepresentation(logFile.length()));

            jsonResponse.put("readableFileSize", readableFileSize);
            jsonResponse.put("fileSize", logFile.length());

            List<String> logLines = FileUtil.readLines(logFile, linesPerUpdate, oldFileLength);
            if (logLines != null && logFile.length() != oldFileLength) {
                for (String logLine : logLines) {
                    if (logLine != null) {
                        jsonLogLines.add(HtmlUtils.htmlEscape(logLine));
                    }
                }
            }
        } else {
            log.warn("Could not read log file: " + logFile);
        }

        jsonResponse.put("logLines", jsonLogLines);

        return jsonResponse;
    }

    /**
     * Gets the requested file and returns it back thru the response object.
     * 
     * @Override of AbstractController method
     * @return null
     */
    @RequestMapping(value = "/logging/download", method = RequestMethod.GET)
    public String download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/plain");

        String file = ServletRequestUtils.getStringParameter(request, "file", "");
        File logFile = sanitizeAndVerify(new File(localDir, file));
        Validate.isTrue(logFile.isFile());

        if (logFile != null) {
            // set response header to the log filename
            response.setHeader("Content-Disposition", "attachment; filename=" + logFile.getName());
            response.setHeader("Content-Length", Long.toString(logFile.length()));

            // Download the file thru the response object
            FileCopyUtils.copy(logFile.toURI().toURL().openStream(), response.getOutputStream());
        }

        return null;
    }

    /**
     * Iterates through everything in the given directory and sort into files and directories.
     * 
     * Filters the files by creation date. If a file's creation date falls between the instants (inclusive)
     * the file will be included.
     */
    public static void populateFileLists(File currentDir, Collection<File> localLogList,
            Collection<File> localDirectoryList, final Instant from, final Instant to) {
        // iterates through everything in the given directory and sort into files and directories

        File[] localDirAndFiles = currentDir.listFiles(FileUtil.creationDateFilter(from, to, false));

        for (File fileObj : localDirAndFiles) {

            if (fileObj.isDirectory()) {
                localDirectoryList.add(fileObj);
            } else if (isLog(fileObj.getName())) {
                localLogList.add(fileObj);
            }
        }
    }

    private Function<File, String> getFileCreationDate = new Function<File, String>() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        @Override
        public String apply(File from) {
            try {
                return df.format(FileUtil.getCreationDate(from));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Returns an ordering for files.
     * 
     * If sortByApplication is false the files will be sorted by date.
     */
    private Ordering<File> getLogOrdering(YukonUserContext userContext, boolean sortByDate) {
        // Sort by application name
        Ordering<String> caseInsensitiveOrdering = CollationUtils.getCaseInsensitiveOrdering(userContext);
        Ordering<File> applicationNameOrdering = caseInsensitiveOrdering.onResultOf(fileToApplicationNameFunction);

        Ordering<File> fileCreationOrdering = Ordering.natural().onResultOf(getFileCreationDate).reverse();

        // compound the two orderings
        if (sortByDate) {
            return fileCreationOrdering.compound(applicationNameOrdering);
        }
        return applicationNameOrdering.compound(fileCreationOrdering);

    }

    private LinkedHashMultimap<String, LogFile> sortLogs(List<File> logs, boolean sortByDate,
            YukonUserContext userContext) throws IOException {
        Collections.sort(logs, getLogOrdering(userContext, sortByDate));
        LinkedHashMultimap<String, LogFile> result = LinkedHashMultimap.create();
        for (File log : logs) {
            String applicationName = fileToApplicationNameFunction.apply(log);
            String dateHeading =
                dateFormattingService.format(FileUtil.getCreationDate(log), DateFormatEnum.LONG_DATE, userContext);

            if (sortByDate) {
                result.put(dateHeading, new LogFile(log, applicationName));
            } else {
                result.put(applicationName, new LogFile(log, dateHeading));
            }
        }

        return result;
    }

    /**
     * This method checks to see if the file extension is the right type
     */
    public static boolean isLog(String logName) {
        return logName.endsWith("log") || logName.endsWith("xml");
    }

    private File sanitizeAndVerify(File file) throws IOException {

        file = file.getCanonicalFile();
        if (!file.exists()) {
            log.warn(file.getName() + " doesn't exist.");
            throw new IllegalArgumentException("File either doesn't exist or isn't in a valid directory.");
        } else if (!isFileUnderRoot(localDir, file)) {
            log.warn(file.getName() + " isn't under the log root directory.");
            throw new IllegalArgumentException("File either doesn't exist or isn't in a valid directory.");
        }

        return file;
    }

    private String getRootlessFilePath(File file) {
        if (isLogRoot(file)) {
            return "/";
        } else {
            return getRootlessFilePath(file.getParentFile()) + file.getName() + "/";
        }
    }

    private boolean isLogRoot(File file) {
        if (file == null) {
            return true;
        }
        return FileUtil.areFilesEqual(file, localDir);
    }

    /**
     * Checks to see if the file is under root, but is not root.
     */
    private boolean isFileUnderRoot(File root, File logFile) {
        File file = logFile;
        do {
            if (file.equals(root)) {
                return true;
            }
            file = file.getParentFile();
        } while (file != null);

        return false;
    }

    public static final Function<File, String> fileToApplicationNameFunction = new Function<File, String>() {
        // The following pattern looks for a base file name (group 1) that is the
        // leading part of the file name minus an optional underscore, a 1-8
        // digit number, and the extension.
        Pattern dayNumberPattern = Pattern.compile("(.+?)_?\\d{1,8}\\.[^.]+$");

        @Override
        public String apply(File file) {
            String fileName = file.getName();

            Matcher patternMatches = dayNumberPattern.matcher(fileName);
            if (patternMatches.matches()) {
                return StringUtils.capitalize(patternMatches.group(1));
            }

            return fileName;
        }
    };

    private final Function<File, String> fileToNameFunction = new Function<File, String>() {
        @Override
        public String apply(File from) {
            return from.getName();
        }
    };

    public static class LogFile {

        private File file;
        private String identifier;

        public LogFile(File file, String identifier) {
            this.file = file;
            this.identifier = identifier;
        }

        public String getName() {
            return file.getName();
        }

        public MessageSourceResolvable getSize() {
            return BinaryPrefix.getCompactRepresentation(file.length());
        }

        public Instant getDate() {
            return new Instant(file.lastModified());
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, YukonUserContext userContext) {
        PropertyEditor dateTimeEditor =
            datePropertyEditorFactory.getLocalDatePropertyEditor(DateFormatEnum.DATE, userContext);
        binder.registerCustomEditor(LocalDate.class, dateTimeEditor);
    }
}
