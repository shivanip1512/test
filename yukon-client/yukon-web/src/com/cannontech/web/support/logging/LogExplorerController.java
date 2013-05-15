package com.cannontech.web.support.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestUtils;
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
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
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
 */
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_LOGS)
@Controller
public class LogExplorerController {
    private Logger log = YukonLogManager.getLogger(LogExplorerController.class);
    private File localDir = new File(BootstrapUtils.getServerLogDir());

    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    /**
     * Stores all log filenames from local and remote directories in two lists
     * and then saves the lists in the ModelAndView
     * @return a model and view containing lists of log files names (a menu of
     *         file names)
     */
    @RequestMapping(value = "/logging/menu", method = RequestMethod.GET)
    public String menu(ModelMap map, 
    		@RequestParam(required=false, defaultValue="/") String file,
    		YukonUserContext userContext) throws IOException {

        File logDir = sanitizeAndVerify(new File(localDir, file));
        Validate.isTrue(logDir.isDirectory());

        List<File> localLogList = Lists.newArrayList();
        List<File> localDirectoryList = Lists.newArrayList();

        // lists to hold log file names
        populateFileLists(logDir, localLogList, localDirectoryList);
 
        // get directory names
        List<String> directoryNameList = Lists.transform(localDirectoryList, fileToNameFunction);

        Multimap<String, LogFile> resultsByApplication = sortLogs(localLogList, true, userContext);;
        Multimap<String, LogFile> resultsByDate = sortLogs(localLogList, false, userContext);

        map.addAttribute("isSubDirectory", !isLogRoot(logDir));
        map.addAttribute("currentDirectory", getRootlessFilePath(logDir.getParentFile()));
        
        map.addAttribute("file", HtmlUtils.htmlEscape(file));
        map.addAttribute("dirList", directoryNameList);
        map.addAttribute("localLogListByApplication", resultsByApplication.asMap());
        map.addAttribute("localLogListByDate", resultsByDate.asMap());

        return "logging/menu.jsp";
    }

    /**
     * Extracts a log file/filename from the local or remote 
     * directory and stores them in the ModelAndView object.
     */
    @RequestMapping(value = "/logging/view", method = RequestMethod.GET)
    public String viewLog(ModelMap map,
    		@RequestParam(required=false, defaultValue="/") String file,
    		@RequestParam(required=false, defaultValue="50") int numLines,
    		@RequestParam(required=false, defaultValue="0") int offSet,
    		YukonUserContext userContext) throws IOException {

        // Gets the correct log file from the request
        File logFile = sanitizeAndVerify(new File(localDir, file));
        Validate.isTrue(logFile.isFile());
        
        long lastModL = logFile.lastModified();
        long fileLengthL = logFile.length();
       
        // Checks to see if the logFile exists and has the ability to be read
        if((logFile != null) && (logFile.canRead())){
            String lastMod = dateFormattingService.format(new Date(lastModL), DateFormattingService.DateFormatEnum.BOTH, userContext);
            String fileLength = String.valueOf(fileLengthL/1024);
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
        
        return "logging/logTail.jsp";
    }   

    @RequestMapping(value = "/logging/view/update", method = RequestMethod.POST)
    @ResponseBody
    public JSON tailUpdate(HttpServletRequest request,
                             HttpServletResponse response, 
                             @RequestBody JSONObject json,
                             ModelMap map,
                             YukonUserContext userContext) throws IOException {

        long oldFileLength = json.getLong("fileLength");
        int linesPerUpdate = json.getInt("numLines");
        String fileName = json.getString("file");

        // takes the data and checks to see if the log file has changed        
        JSONObject jsonReturn = new JSONObject();
        JSONArray jsonLogLines = new JSONArray();
        jsonReturn.put("numLines", linesPerUpdate);

        File logFile = sanitizeAndVerify(new File(localDir, fileName));
        Validate.isTrue(logFile.isFile());

        if (logFile!= null && logFile.canRead()) {
            // Setting up the last modified variable for the JSON
            String lastModStr = dateFormattingService.format(new Instant(logFile.lastModified()),
            						DateFormattingService.DateFormatEnum.LONG_DATE_TIME, userContext);
        	jsonReturn.put("lastModified", lastModStr);

        	String readableFileSize = messageSourceResolver
        			.getMessageSourceAccessor(userContext)
        			.getMessage(BinaryPrefix.getCompactRepresentation(logFile.length()));
        	
            jsonReturn.put("readableFileSize", readableFileSize);
            jsonReturn.put("fileSize", logFile.length());

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

        jsonReturn.put("logLines", jsonLogLines);

        return jsonReturn;
    }

    /**
    * Gets the requested file and returns it back thru the response object.
    * @Override of AbstractController method
    * @return null
    */
    @RequestMapping(value = "/logging/download", method = RequestMethod.GET)
    public String download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/plain");
        
        String file = ServletRequestUtils.getStringParameter(request, "file", "");
        File logFile = sanitizeAndVerify(new File(localDir, file));
        Validate.isTrue(logFile.isFile());
        
        if(logFile != null){
            //set response header to the log filename
            response.setHeader("Content-Disposition", "attachment; filename=" + logFile.getName());
            response.setHeader("Content-Length", Long.toString(logFile.length()));

            //Download the file thru the response object
            FileCopyUtils.copy(logFile.toURI().toURL().openStream(), response.getOutputStream());
        }
        
        return null;
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

    // LocalDate is a convenient class because it only knows about year, month, day
    private Function<File, Instant> getFileCreationDate = new Function<File, Instant> () {
        public Instant apply(File from) {
        	try {
				BasicFileAttributes attr = Files.readAttributes(from.toPath(), BasicFileAttributes.class);
				return new Instant(attr.creationTime().toMillis());
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
    private Ordering<File> getLogOrdering(YukonUserContext userContext, boolean sortByApplication) {
    	// Sort by application name
        Ordering<String> caseInsensitiveOrdering = CollationUtils.getCaseInsensitiveOrdering(userContext);
        Ordering<File> applicationNameOrdering = caseInsensitiveOrdering.onResultOf(fileToApplicationNameFunction);
        
        // sort by newest file and use name to resolve ties (each "day" will be a tie)
        Ordering<File> fileCreationOrdering = Ordering.natural().onResultOf(getFileCreationDate).reverse();

        // compound the two orderings
        if (sortByApplication) {
            return  applicationNameOrdering.compound(fileCreationOrdering);
        }

        return fileCreationOrdering.compound(applicationNameOrdering);
    }

    private LinkedHashMultimap<String, LogFile> sortLogs(List<File> logs, boolean sortByApplication, YukonUserContext userContext) {
        Collections.sort(logs, getLogOrdering(userContext, true));
        LinkedHashMultimap<String, LogFile> result = LinkedHashMultimap.create();

        for (File log : logs) {
            Instant modificationAsDate = getFileCreationDate.apply(log);
            String applicationName = fileToApplicationNameFunction.apply(log);
            String dateHeading = dateFormattingService.format(modificationAsDate, DateFormatEnum.LONG_DATE, userContext);
            
            if (sortByApplication) {
            	result.put(applicationName, new LogFile(log, dateHeading));
            } else {
            	result.put(dateHeading, new LogFile(log, applicationName));
            }
        }

        return result;
    }

    /**
     * This method checks to see if the file extension is the right type
     */
    public boolean isLog(String logName) {
        return ((logName.endsWith("log")) || (logName.endsWith("xml")));
    }
    
    private File sanitizeAndVerify(File file) throws IOException {

    	file = file.getCanonicalFile();
        if (!file.exists()) {
        	log.warn(file.getName() + " doesn't exist.");
        	throw new IllegalArgumentException("File either doesn't exist or isn't in a valid directory.");
        } else if (!isFileUnderRoot(localDir, file)) {
        	log.warn(file.getName() + " isn't under the log root directory.");
        	throw new IllegalArgumentException("File either doesn't exist or is isn't in a valid directory.");
        }

        return file;
    }

    private String getRootlessFilePath(File file){
        if(isLogRoot(file)){
            return "";
        } else {
            return getRootlessFilePath(file.getParentFile()) + file.getName() + "/";
        }
    }

    private boolean isLogRoot(File file){
        if(file == null){
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

    private final Function<File, String> fileToApplicationNameFunction = new Function<File, String> () {
        // The following pattern looks for a base file name (group 1) that is the
        // leading part of the file name minus an optional underscore, a 1-8 
        // digit number, and the extension.
        Pattern dayNumberPattern = Pattern.compile("(.+?)_?\\d{1,8}\\.[^.]+$");

        public String apply(File file) {
        	String fileName = file.getName();
            
            Matcher patternMatches = dayNumberPattern.matcher(fileName);
            if (patternMatches.matches()){
                return StringUtils.capitalize(patternMatches.group(1));
            }

            return fileName;
        }
    };
    
    private final Function<File, String> fileToNameFunction = new Function<File, String> () {
        public String apply(File from) {
            return from.getName();
        }
    };
    
    public class LogFile {

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
}
