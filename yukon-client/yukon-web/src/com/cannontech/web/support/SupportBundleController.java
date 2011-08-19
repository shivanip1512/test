package com.cannontech.web.support;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeUtil;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.support.model.SupportBundle;
import com.cannontech.support.service.SupportBundleService;
import com.cannontech.support.service.SupportBundleSource;
import com.cannontech.support.service.impl.BundleRangeSelection;
import com.cannontech.tools.sftp.SftpStatus;
import com.cannontech.tools.sftp.SftpWriter;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.flashScope.FlashScopeMessageType;
import com.google.common.collect.Sets;

@Controller
@RequestMapping("supportBundle/*")
public class SupportBundleController {
    private final static String baseKey = "yukon.web.modules.support.supportBundle";

    private SupportBundleService supportBundleService;
    private DateFormattingService dateFormattingService;
    private List<SupportBundleSource> sourceList;
    private static final String FTP_USER = "?"; // We have not received a user/pass yet
    private static final String FTP_PASS = "?"; 
    private static final String FTP_HOST = "?"; 
    private static final String FTP_DIR = "Support Bundles";

    private Validator detailsValidator =
        new SimpleValidator<SupportBundle>(SupportBundle.class) {
            @Override
            public void doValidation(SupportBundle supportBundle, Errors errors) {
                ValidationUtils.rejectIfEmpty(errors, "customerName", baseKey + ".errorMsg.empty");
                YukonValidationUtils.checkExceedsMaxLength(errors,  "customerName",
                                                           supportBundle.getCustomerName(), 40);

                Pattern validCharacters = Pattern.compile("^[a-zA-Z0-9_\\-\\(\\)&%.#]{0,}$");
                YukonValidationUtils.regexCheck(errors,
                                                "customerName",
                                                supportBundle.getCustomerName(),
                                                validCharacters,
                                                baseKey + ".errorMsg.invalidCharacters");
            }
        };

    @RequestMapping("view")
    public String supportBundle(HttpServletRequest request, ModelMap model,
                                YukonUserContext userContext) {

        return supportBundle(request, model, new SupportBundle(sourceList), userContext);
    }

    private String supportBundle(HttpServletRequest request, ModelMap model,
                                 SupportBundle supportBundle,
                                 YukonUserContext userContext) {

        List<File> previousBundles = supportBundleService.getBundles();
        model.addAttribute("supportBundle", supportBundle);
        model.addAttribute("bundleRangeSelection", BundleRangeSelection.values());
        model.addAttribute("bundleList", previousBundles);
        model.addAttribute("sourceList", sourceList);
        model.addAttribute("inProgress", supportBundleService.isInProgress());

        return "supportBundle.jsp";
    }

    @RequestMapping("viewProgress")
    public String createBundleView(HttpServletRequest request, ModelMap model,
                                   YukonUserContext userContext)
            throws Exception {

        return "createBundle.jsp";
    }

    @RequestMapping("createBundle")
    public String createBundle(HttpServletRequest request, ModelMap model,
                                SupportBundle supportBundle, BindingResult bindingResult,
                                YukonUserContext userContext, FlashScope flashScope)
            throws Exception {

        detailsValidator.validate(supportBundle, bindingResult);

        if (bindingResult.hasErrors()) {
            List<MessageSourceResolvable> messages =
                YukonValidationUtils.errorsForBindingResult(bindingResult);
            flashScope.setMessage(messages, FlashScopeMessageType.ERROR);
            return supportBundle(request, model, supportBundle, userContext);
        }

        model.addAttribute("sourceList", sourceList);

        Set<String> OptionalSourcesToInclude = Sets.newHashSet();
        OptionalSourcesToInclude.addAll(Arrays.asList(supportBundle.getOptionalSourcesToInclude()));

        LocalDate stop = new LocalDate(DateTimeZone.getDefault());
        LocalDate start = stop.minus(supportBundle.getBundleRangeSelection().getDuration());

        if (!supportBundleService.isInProgress()) {
            supportBundleService
                .bundle(TimeUtil.toMidnightAtBeginningOfDay(start, DateTimeZone.getDefault()),
                        TimeUtil.toMidnightAtEndOfDay(stop, DateTimeZone.getDefault()),
                        supportBundle.getInfo(),
                        supportBundle.getCustomerName(),
                        OptionalSourcesToInclude);

        }
        return "createBundle.jsp";

    }

    @RequestMapping(method = RequestMethod.POST, value = "createBundle/getStatus")
    public String getBundleCreationStatus(HttpServletRequest request, ModelMap model)
            throws Exception {

        Map<String, Boolean> thingsDoneMap = supportBundleService.getSourcesDone();
        model.addAttribute("thingsDoneMap", thingsDoneMap);
        model.addAttribute("inProgress", supportBundleService.isInProgress());
        model.addAttribute("sourceList", sourceList);
        model.addAttribute("mostRecentBundle", supportBundleService.getMostRecentBundle());

        return "bundleBuildStatus.jsp";
    }

    @RequestMapping("upload")
    public String upload(HttpServletRequest request, ModelMap model,
                         YukonUserContext yukonUserContext, int fileNum, SftpStatus ftpStatus, String username, String password, String host)
            throws Exception {

        List<File> bundleFileList = supportBundleService.getBundles();

        if (bundleFileList.size() > fileNum) {
            File bundleToSend = bundleFileList.get(fileNum);

            SftpWriter ftp = null;
            
            if (ftpStatus == SftpStatus.SEND_ERROR) {
                ftp = new SftpWriter(username, password, FTP_DIR, host);
            } else {
                ftp = new SftpWriter(FTP_USER, FTP_PASS, FTP_DIR, FTP_HOST);
            }
            
            ftpStatus = ftp.sendFile(bundleToSend);

            String fileDate =
                dateFormattingService.format(new Date(bundleToSend.lastModified()),
                                             DateFormatEnum.DATE, yukonUserContext);
            
            model.addAttribute("fileSize", CtiUtilities.formatFileSize(bundleToSend.length()));
            model.addAttribute("fileDate", fileDate);
            model.addAttribute("filename", bundleToSend.getName());
            model.addAttribute("fileNum", fileNum);
            model.addAttribute("ftpStatus", ftpStatus);
            model.addAttribute("doUpload", true);

            return "send.jsp";
        }

        return "redirect:view";
    }

    @RequestMapping("send")
    public String send(HttpServletRequest request, ModelMap model,
                       YukonUserContext yukonUserContext, int fileNum)
            throws Exception {

        List<File> bundleFileList = supportBundleService.getBundles();

        if (bundleFileList.size() > fileNum) {
            File bundleToSend = bundleFileList.get(fileNum);

            String fileDate =
                dateFormattingService.format(new Date(bundleToSend.lastModified()),
                                             DateFormatEnum.DATE,
                                             yukonUserContext);

            model.addAttribute("fileSize", CtiUtilities.formatFileSize(bundleToSend.length()));
            model.addAttribute("fileDate", fileDate);
            model.addAttribute("filename", bundleToSend.getName());
            model.addAttribute("fileNum", fileNum);
            model.addAttribute("ftpStatus", SftpStatus.SENDING);
            model.addAttribute("doUpload", false);
            
            return "send.jsp";
        }

        return "redirect:view";
    }

    @RequestMapping(value = "download", method = RequestMethod.GET)
    public String download(HttpServletRequest request, HttpServletResponse response, int fileNum)
            throws Exception {
        response.setContentType("application/zip");

        List<File> bundleFileList = supportBundleService.getBundles();

        if (bundleFileList.size() > fileNum) {
            File bundleToDownload = bundleFileList.get(fileNum);
            // set response header to the filename
            response.setHeader("Content-Disposition",
                               "attachment; filename=" + bundleToDownload.getName());
            response.setHeader("Content-Length", Long.toString(bundleToDownload.length()));

            // Download the file through the response object
            FileCopyUtils.copy(new FileInputStream(bundleToDownload), response.getOutputStream());
        }

        return null;
    }

    @Autowired
    public void setDateFormatingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }

    @Autowired
    public void setSupportBundleService(SupportBundleService supportBundleService) {
        this.supportBundleService = supportBundleService;
    }

    @Autowired
    public void setSourceList(List<SupportBundleSource> sourceList) {
        this.sourceList = sourceList;
    }

}
