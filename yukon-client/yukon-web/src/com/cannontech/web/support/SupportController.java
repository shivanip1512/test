package com.cannontech.web.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.Displayable;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.util.BinaryPrefix;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.PoolManager;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.support.service.SupportBundleService;
import com.cannontech.support.service.SupportBundleWriter;
import com.cannontech.tools.sftp.SftpWriter.Status;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.support.SiteMapHelper.SiteMapCategory;
import com.cannontech.web.support.SiteMapHelper.SiteMapPage;
import com.cannontech.web.support.SiteMapHelper.SiteMapWrapper;
import com.cannontech.web.support.SupportBundle.BundleRangeSelection;
import com.cannontech.web.util.JsonHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Controller
@CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
public class SupportController {

    private final static String baseKey = "yukon.web.modules.support.supportBundle";
    private final static String startKey = "yukon.web.modules.support";

    @Autowired private SupportBundleService supportBundleService;
    @Autowired private List<SupportBundleWriter> writerList;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private JsonHelper jsonHelper;
    @Autowired private SupportBundleService bundleService;
    @Autowired private PoolManager poolManager;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private SiteMapHelper siteMapHelper;
    @Autowired private ObjectFormattingService objectFormattingService;

    @RequestMapping(value="info")
    public String info(ModelMap model){
        model.addAttribute("systemInformation", CtiUtilities.getSystemInfoString());
        model.addAttribute("buildInfo", VersionTools.getBuildInfo());
        model.addAttribute("versionDetails", VersionTools.getYukonDetails());
        return "info.jsp";
    }
    
    @RequestMapping(value={"","/support"})
    public String support(ModelMap model, YukonUserContext context) {
        return supportBundle(model, new SupportBundle(), context);
    }

    private void setUpLogsAndInfo(ModelMap model, YukonUserContext context){
        File logDir = new File(BootstrapUtils.getServerLogDir());
        try {
            logDir = logDir.getCanonicalFile();
        } catch (IOException e) {
            return;
        }
        Validate.isTrue(logDir.isDirectory());

        List<File> localLogList = Lists.newArrayList();
        List<File> localDirectoryList = Lists.newArrayList();
//        LogMenuController.populateFileLists(logDir, localLogList, localDirectoryList);

        List<LogFile> todaysLogs = Lists.newArrayList();
        for( File logFile : localLogList){
            LocalDate lastMod = new LocalDate(logFile.lastModified());
            if(LocalDate.now().equals(lastMod)){
               todaysLogs.add(new LogFile(logFile.getName()));
            }
        }
        objectFormattingService.sortDisplayableValues(todaysLogs, null, null, context);
        
        
        model.addAttribute("todaysLogs", todaysLogs);
        
        model.addAttribute("dbUrl", poolManager.getPrimaryUrl());
        model.addAttribute("dbUser", poolManager.getPrimaryUser());
    }
    
    //TODO
    private void setUpLinks(ModelMap model, YukonUserContext context) {
        Map<SiteMapCategory, List<SiteMapWrapper>> siteMap = siteMapHelper.getSiteMap(context);
        List<SiteMapWrapper> fullCategory = siteMap.get(SiteMapCategory.SUPPORT);
        
        List<SiteMapPage> excludePages = Lists.newArrayList();
        excludePages.add(SiteMapPage.SUPPORT);
        excludePages.add(SiteMapPage.DATABASE_VALIDATION);
        
        List<SiteMapWrapper> supportPages = Lists.newArrayList();
        for(SiteMapWrapper wrapper : fullCategory){
            if ( ! excludePages.contains(wrapper.getPage())) {
                supportPages.add(wrapper);
            }
        }        model.addAttribute("supportPages", supportPages);
    }

    private String supportBundle(ModelMap model, SupportBundle bundle, YukonUserContext context){
        setUpLogsAndInfo(model, context);
        setUpLinks(model, context);
        
        List<JSONObject> previousBundles = Lists.newArrayList();
        for(File f : bundleService.getBundles()){
            previousBundles.add(simpleJsonForBundle(f));
        }
        model.addAttribute("supportBundle", bundle);
        model.addAttribute("bundleRangeSelectionOptions", BundleRangeSelection.values() );
        model.addAttribute("bundleList", previousBundles);
        model.addAttribute("writerList", writerList);
        model.addAttribute("inProgress", bundleService.isInProgress());
        return "support.jsp";
    }


    public class LogFile implements Displayable {
        private final String fileName;
        private final MessageSourceResolvable resolvable;

        public LogFile(String fileName) {
            this.fileName = fileName;
            
            MessageSourceResolvable tempResolvable = 
                    new YukonMessageSourceResolvable(startKey +".logFile.UNKNOWN", fileName);
            
            String key = startKey +".logFile.UNKONWN";
            for(NiceNameMapping p : NiceNameMapping.values()){
                if (p.getRegex().matcher(fileName).matches()){
                     key = startKey + ".logFile." + p.name();
                     tempResolvable = new YukonMessageSourceResolvable(key);
                }
            }
            this.resolvable = tempResolvable;
        }

        public String getFileName() {
            return fileName;
        }

        @Override
        public MessageSourceResolvable getMessage() {
            return resolvable;
        }

    }
    
    public enum NiceNameMapping{
        CALC(Pattern.compile("^calc\\d.*")),
        CAP_CONTROL(Pattern.compile("^capcontrol\\d.*")),
        CCU_SIMULATOR(Pattern.compile("^ccu_simpulator\\d.*")),
        DISPATCH(Pattern.compile("^dispatch\\d.*")),
        LOAD_MANAGEMENT(Pattern.compile("^loadmanagement\\d.*")),
        PORTER(Pattern.compile("^porter\\d.*")),
        SCANNER(Pattern.compile("^scanner\\d.*")),
        SERVICE_MANAGER(Pattern.compile("^ServiceManager_\\d.*")),
        WEB_SERVER(Pattern.compile("^Webserver_\\d.*")),
        
        ;
        
        
        private Pattern regex;
        
        NiceNameMapping(Pattern regex){
            this.regex = regex;
        }
        
        public Pattern getRegex(){
            return regex;
        }
        
    }

    private Validator detailsValidator = new SimpleValidator<SupportBundle>(SupportBundle.class) {
            @Override
            public void doValidation(SupportBundle supportBundle, Errors errors) {
                ValidationUtils.rejectIfEmpty(errors, "customerName", baseKey + ".errorMsg.empty");
                YukonValidationUtils.checkExceedsMaxLength(errors,  "customerName",
                                                           supportBundle.getCustomerName(), 40);

                Pattern validCharacters = Pattern.compile("^[a-zA-Z0-9_\\-\\(\\)&%.# ]*$");
                YukonValidationUtils.regexCheck(errors,
                                                "customerName",
                                                supportBundle.getCustomerName(),
                                                validCharacters,
                                                baseKey + ".errorMsg.invalidCharacters");
            }
        };


    @RequestMapping(value="createBundle", method = RequestMethod.POST)
    public String createBundle(
            ModelMap model, 
            @ModelAttribute SupportBundle bundle,
            BindingResult result, 
            FlashScope flash, 
            YukonUserContext context) {

        detailsValidator.validate(bundle, result);

        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);

            return supportBundle(model, bundle, context);
        }

        model.addAttribute("writerList", writerList);

        Set<String> optionalWritersToInclude = Sets.newHashSet();
        optionalWritersToInclude.addAll(bundle.getOptionalWritersToInclude());

        LocalDate stop = new LocalDate(DateTimeZone.getDefault());
        LocalDate start = stop.minus(bundle.getBundleRangeSelection().getPeriod());

        if (!supportBundleService.isInProgress()) {
            supportBundleService
                .bundle(TimeUtil.toMidnightAtBeginningOfDay(start, DateTimeZone.getDefault()),
                        TimeUtil.toMidnightAtEndOfDay(stop, DateTimeZone.getDefault()),
                        bundle.getCustomerName(),
                        bundle.getComments(),
                        optionalWritersToInclude);
        }

        return "redirect:viewBundleProgress";
    }

    @RequestMapping(value="viewBundleProgress")
    public String viewBundleProgress() {
        return "supportBundle/viewProgress.jsp";
    }

    @RequestMapping(value="bundleInProgress")
    public @ResponseBody JSONObject bundleInProgress() {
        JSONObject json = new JSONObject();
        boolean inProgress = supportBundleService.isInProgress();
        json.put("inProgress", inProgress);
        if(! inProgress){
            json.put("fileName", supportBundleService.getMostRecentBundle().getName());
        }
        return json;
    }

    @RequestMapping(value="getBundleProgress")
    public String getBundleProgress(ModelMap model) {
        Map<String, Boolean> thingsDoneMap = supportBundleService.getWritersDone();
        model.addAttribute("thingsDoneMap", thingsDoneMap);
        model.addAttribute("inProgress", supportBundleService.isInProgress());
        model.addAttribute("writerList", writerList);

        return "supportBundle/buildStatus.jsp";
    }

    @RequestMapping(value="infoOnBundle")
    public @ResponseBody JSONObject infoOnBundle(String fileName, YukonUserContext context) {
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        File bundle = getBundleFileForFileName(fileName);
        if(bundle == null){
            JSONObject json = new JSONObject();
            json.put("fileName", accessor.getMessage(baseKey + ".ftpUpload.failed.NO_FILE", ""));
            json.put("fileSize", "");
            json.put("fileDate", "");
            return json;
        }

        JSONObject json = simpleJsonForBundle( bundle );

        MessageSourceResolvable fileSize = BinaryPrefix.getCompactRepresentation(bundle.length());
        json.put("fileSize", accessor.getMessage(fileSize.getCodes()[0], fileSize.getArguments()[0]));
        json.put("fileDate", dateFormattingService.format(bundle.lastModified(), DateFormatEnum.DATE, context));
        return json;
    }

    private JSONObject simpleJsonForBundle(File bundle) {
        JSONObject json = new JSONObject();
        json.put("fileName", bundle.getName());
        return json;
    }

    @RequestMapping(value="uploadBundle")
    public String uploadBundle(String fileName, FlashScope flash) {

        File bundleToSend = getBundleFileForFileName(fileName);

        if (bundleToSend == null) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".ftpUpload.failed.NO_FILE", ""));
            return "redirect:/support";
        }

        Status ftpStatus = supportBundleService.uploadViaSftp(bundleToSend);
        if (ftpStatus == Status.SUCCESS) {
            flash.setConfirm(new YukonMessageSourceResolvable(baseKey +
                ".ftpUpload.succeeded", bundleToSend.getName()));
        } else {
            flash.setError(new YukonMessageSourceResolvable(baseKey + ".ftpUpload.failed." +
                ftpStatus, bundleToSend.getName()));
        }

        return "redirect:/support";
    }

    @RequestMapping(value="downloadBundle")
    public void downloadBundle(HttpServletResponse resp, String fileName) throws IOException {
        
        File bundleToDownload = getBundleFileForFileName(fileName);
        if (bundleToDownload == null) {
            return;
        }

        resp.setContentType("application/zip");

        // set response header to the filename
        resp.setHeader("Content-Disposition", "attachment; filename=" + ServletUtil.urlEncode(bundleToDownload.getName()));
        resp.setHeader("Content-Length", Long.toString(bundleToDownload.length()));

        // Download the file through the response object
        FileCopyUtils.copy(new FileInputStream(bundleToDownload), resp.getOutputStream());
    }

    private File getBundleFileForFileName(String fileName){
        for( File f : supportBundleService.getBundles()) {
            if( fileName.equals(f.getName())) {
                return f;
            }
        }
        return null;
    }
}