package com.cannontech.web.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.userpage.model.SiteMapCategory;
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
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.support.rfn.message.RfnSupportBundleRequest;
import com.cannontech.support.rfn.message.RfnSupportBundleResponseType;
import com.cannontech.support.rfn.message.SupportBundleRequestType;
import com.cannontech.support.service.SupportBundleService;
import com.cannontech.support.service.SupportBundleWriter;
import com.cannontech.support.service.impl.RFNetworkSupportBundleService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.support.SiteMapHelper.SiteMapWrapper;
import com.cannontech.web.support.SupportBundle.BundleRangeSelection;
import com.cannontech.web.support.logging.LogExplorerController;
import com.cannontech.web.support.logging.LogExplorerController.LogFile;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Controller
public class SupportController {

    private final static String baseKey = "yukon.web.modules.support.supportBundle";
    private final static String manualsFolderName = CtiUtilities.getYukonBase() + "/Manuals/";
    private final static Pattern pdfFileName = Pattern.compile("(.*)\\.pdf$");
    @Autowired private RFNetworkSupportBundleService rfNetworkSupportBundleService;
    @Autowired private SupportBundleService supportBundleService;
    @Autowired private List<SupportBundleWriter> writerList;
    @Autowired private DateFormattingService dateFormattingService;
    @Autowired private SupportBundleService bundleService;
    @Autowired private PoolManager poolManager;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private SiteMapHelper siteMapHelper;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private ServerDatabaseCache serverDatabaseCache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    @GetMapping("info")
    public String info(ModelMap model){
        model.addAttribute("buildInfo", VersionTools.getBuildInfo());
        model.addAttribute("versionDetails", VersionTools.getYukonDetails());
        return "info.jsp";
    }
    
    @GetMapping({"","/support"})
    public String support(ModelMap model, YukonUserContext context) {
        return supportBundle(model, new SupportBundle(), context);
    }

    @GetMapping("/manual")
    public void manual(String manualName, HttpServletResponse response) throws IOException  {
        File f = new File(manualsFolderName + manualName + ".pdf");
        InputStream is = new FileInputStream(f);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + manualName + ".pdf\"");
        FileCopyUtils.copy(is, response.getOutputStream());
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
        
        Instant from;
        Instant to;
        DateTimeZone tz = DateTimeZone.getDefault();
        LocalDate now = LocalDate.now(tz);
            from = TimeUtil.toMidnightAtBeginningOfDay(now, tz);
            to = TimeUtil.toMidnightAtEndOfDay(now, tz);
        LogExplorerController.populateFileLists(logDir, localLogList, localDirectoryList, from, to);
        
        List<LogFile> todaysLogs = Lists.newArrayList();
        for (File log : localLogList){
            todaysLogs.add( new LogFile(log, LogExplorerController.fileToApplicationNameFunction.apply(log)));
        }

        objectFormattingService.sortDisplayableValues(todaysLogs, null, null, context);
        
        
        model.addAttribute("todaysLogs", todaysLogs);
        
        model.addAttribute("dbUrl", poolManager.getPrimaryUrl());
        model.addAttribute("dbUser", poolManager.getPrimaryUser());
    }
    
    private void setUpLinks(ModelMap model, YukonUserContext context) {
        Map<SiteMapCategory, List<SiteMapWrapper>> siteMap = siteMapHelper.getSiteMap(context);
        List<SiteMapWrapper> fullCategory = siteMap.get(SiteMapCategory.SUPPORT);
        
        List<SiteMapPage> excludePages = Lists.newArrayList();
        excludePages.add(SiteMapPage.SUPPORT);
        excludePages.add(SiteMapPage.DATABASE_VALIDATION);
        // Check if there are any devices that support battery analysis ( currently only supports water nodes )
        if(serverDatabaseCache.getAllPaoTypes().stream().noneMatch(PaoType::supportsBatteryAnalysis)) {
            excludePages.add(SiteMapPage.BATTERY_NODE_ANALYSIS);
        }
        
        List<SiteMapWrapper> supportPages = Lists.newArrayList();
        for(SiteMapWrapper wrapper : fullCategory){
            if ( ! excludePages.contains(wrapper.getPage())) {
                supportPages.add(wrapper);
            }
        }        model.addAttribute("supportPages", supportPages);
    }
   
    
    /**
     * Adds names of manuals found in Yukon/Manuals to the model
     */
    private void setUpManuals(ModelMap model) {
        File folder = new File(manualsFolderName);
        List<String> fileNames = new ArrayList<>();
        if(folder.listFiles() != null){
            for(File file : folder.listFiles()){
                Matcher m = pdfFileName.matcher(file.getName());
                if (m.find()) {
                    fileNames.add(m.group(1));
                }
            }
        }
        model.addAttribute("manuals", fileNames);
    }

    private String supportBundle(ModelMap model, SupportBundle bundle, YukonUserContext context) {
        setUpLogsAndInfo(model, context);
        setUpLinks(model, context);
        setUpManuals(model);
        List<String> previousBundles = new ArrayList<>();
        for(File f : bundleService.getBundles()){
            previousBundles.add(f.getName());
        }
        
        model.addAttribute("supportBundle", bundle);
        model.addAttribute("now", new Date());
        model.addAttribute("rfSupportBundle", new RfSupportBundle());
        model.addAttribute("bundleRangeSelectionOptions", BundleRangeSelection.values());
        model.addAttribute("bundleList", previousBundles);
        model.addAttribute("rfBundleList", getPreviousRfBundleNames());
        model.addAttribute("writerList", writerList);
        model.addAttribute("inProgress", bundleService.isInProgress());
        return "support.jsp";
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


    @PostMapping("createBundle")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public String createBundle(ModelMap model, @ModelAttribute SupportBundle bundle,
            BindingResult result, FlashScope flash, YukonUserContext userContext) {

        detailsValidator.validate(bundle, result);

        if (result.hasErrors()) {
            List<MessageSourceResolvable> messages = YukonValidationUtils.errorsForBindingResult(result);
            flash.setMessage(messages, FlashScopeMessageType.ERROR);

            return supportBundle(model, bundle, userContext);
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

    private Validator detailsRfValidator = new SimpleValidator<RfSupportBundle>(RfSupportBundle.class) {
        @Override
        public void doValidation(RfSupportBundle rfBundle, Errors errors) {
            ValidationUtils.rejectIfEmpty(errors, "customerName", baseKey + ".errorMsg.empty");
            YukonValidationUtils.checkExceedsMaxLength(errors, "customerName",
                    rfBundle.getCustomerName(), 40);

            Pattern validCharacters = Pattern.compile("^[a-zA-Z0-9_\\-\\(\\)&%.# ]*$");
            YukonValidationUtils.regexCheck(errors,
                    "customerName",
                    rfBundle.getCustomerName(),
                    validCharacters,
                    baseKey + ".errorMsg.invalidCharacters");
        }
    };
   
    @PostMapping("createRfBundle")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public String createRFBundle(@ModelAttribute RfSupportBundle rfSupportBundle, BindingResult result, ModelMap model, RfnSupportBundleRequest rfRequest,
            YukonUserContext userContext, HttpServletResponse resp) throws Exception {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        detailsRfValidator.validate(rfSupportBundle, result);
        model.addAttribute("rfSupportBundle", rfSupportBundle);
        model.addAttribute("now", new Date());
        model.addAttribute("rfBundleList", getPreviousRfBundleNames());
        
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("errorMessage", accessor.getMessage("yukon.web.error.fieldErrorsExist"));
            return "supportBundle/rfSupportBundle.jsp";
        }
        String suffix = new DateTime().toString(DateTimeFormat.forPattern("yyyy-MM-dd-HHmmss"));
        String fileName = rfSupportBundle.getCustomerName() + "-" + suffix;
        rfRequest.setFileName(fileName);
        rfRequest.setFromTimestamp(rfSupportBundle.getDate().getTime());
        rfRequest.setType(SupportBundleRequestType.NETWORK_DATA);
        rfNetworkSupportBundleService.send(rfRequest);
       
        return "supportBundle/rfSupportBundle.jsp";
    }
    
    @GetMapping("viewRfBundle")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public String viewRFBundle(ModelMap model) throws Exception {

        model.addAttribute("rfBundleList", getPreviousRfBundleNames());

        return "supportBundle/rfPreviousBundleTab.jsp";
    }

    @GetMapping("viewBundleProgress")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public String viewBundleProgress() {
        return "supportBundle/viewProgress.jsp";
    }

    @GetMapping("bundleInProgress")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public @ResponseBody Map<String, Object> bundleInProgress() {
        Map<String, Object> json = new HashMap<>();
        
        boolean inProgress = supportBundleService.isInProgress();
        json.put("inProgress", inProgress);
        if (!inProgress) {
            json.put("fileName", supportBundleService.getMostRecentBundle().getName());
        }
        return json;
    }
   
    @GetMapping("rfBundleInProgress")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public @ResponseBody Map<String, Object> rfBundleInProgress(YukonUserContext userContext) {
        Map<String, Object> json = new HashMap<>();
        RfnSupportBundleResponseType status = rfNetworkSupportBundleService.getStatus();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        boolean isCompleted = false;
        String message = null;
        if (status == null) {
            isCompleted = true;
            message = accessor.getMessage("yukon.web.modules.support.rfSupportBundle.timeout");
        } else {
            switch (status) {
            case STARTED:
            case INPROGRESS:
                isCompleted = false;
                message = accessor.getMessage("yukon.web.modules.support.rfSupportBundle.started");
                break;
            case COMPLETED:
                isCompleted = true;
                message = accessor.getMessage("yukon.web.modules.support.rfSupportBundle.success");
                break;
            case FAILED:
                isCompleted = true;
                message = accessor.getMessage("yukon.web.modules.support.rfSupportBundle.failed");
                break;
            case TIMEOUT:
                isCompleted = true;
                message = accessor.getMessage("yukon.web.modules.support.rfSupportBundle.timeout");
                break;
            default:
                break;
            }
        }
        json.put("isCompleted", isCompleted);
        json.put("message", message);
        json.put("status", status);
        return json;
    }

    @GetMapping("getBundleProgress")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public String getBundleProgress(ModelMap model) {
        Map<String, Boolean> thingsDoneMap = supportBundleService.getWritersDone();
        model.addAttribute("thingsDoneMap", thingsDoneMap);
        model.addAttribute("inProgress", supportBundleService.isInProgress());
        model.addAttribute("writerList", writerList);

        return "supportBundle/buildStatus.jsp";
    }

    @GetMapping("infoOnBundle")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public @ResponseBody Map<String, String> infoOnBundle(String fileName, YukonUserContext userContext) {

        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(userContext);
        Map<String, String> json = Maps.newHashMapWithExpectedSize(3);

        File bundle = getBundleFileForFileName(fileName, false);
        if (bundle == null){
            json.put("fileName", accessor.getMessage(baseKey + ".ftpUpload.failed.NO_FILE", ""));
            json.put("fileSize", "");
            json.put("fileDate", "");
            return json;
        }

        String fileSize = accessor.getMessage(BinaryPrefix.getCompactRepresentation(bundle.length()));

        json.put("fileName", bundle.getName());
        json.put("fileSize", fileSize);
        json.put("fileDate", dateFormattingService.format(bundle.lastModified(), DateFormatEnum.DATE, userContext));
        return json;
    }

    @PostMapping("downloadBundle")
    @CheckRole(YukonRole.OPERATOR_ADMINISTRATOR)
    public void downloadBundle(HttpServletResponse resp, String fileName, boolean isRfBundle) throws IOException {

        File bundleToDownload = null;
        if (isRfBundle) {
            bundleToDownload = getBundleFileForFileName(fileName, true);
        } else {
            bundleToDownload = getBundleFileForFileName(fileName, false);
        }
        if (bundleToDownload == null) {
            return;
        }
        
        resp.setContentType("application/zip");

        // set response header to the filename
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + ServletUtil.urlEncode(bundleToDownload.getName()) + "\"");
        resp.setHeader("Content-Length", Long.toString(bundleToDownload.length()));

        // Download the file through the response object
        FileCopyUtils.copy(new FileInputStream(bundleToDownload), resp.getOutputStream());
    }

    private File getBundleFileForFileName(String fileName, boolean isRfBundle) {
        List<File> files = null;
        if (isRfBundle) {
            files = supportBundleService.getRfBundles();
        } else {
            files = supportBundleService.getBundles();
        }
        for (File f : files) {
            if (fileName.equals(f.getName())) {
                return f;
            }
        }
        return null;
    }

    /**
     * Fetch previous RF bundle files and return them in list
     */
    private List<String> getPreviousRfBundleNames() {
        List<String> previousRfBundles = new ArrayList<>();
        for (File f : bundleService.getRfBundles()) {
            previousRfBundles.add(f.getName());
        }
        return previousRfBundles;
    }
}