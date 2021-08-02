package com.cannontech.web.admin;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.logger.service.YukonLoggerService.SortBy;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.SystemLogger;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
public class YukonLoggersController {

    private static final Logger log = YukonLogManager.getLogger(YukonLoggersController.class);

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ApiControllerHelper apiControllerHelper;
    @Autowired private YukonLoggersValidator yukonLoggersValidator;
    
    private static final String baseKey = "yukon.web.modules.adminSetup.config.loggers.";
    private static final String redirectLink = "redirect:/admin/config/loggers/allLoggers";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";

    @GetMapping("/config/loggers/allLoggers")
    public String getAllLoggers(@DefaultSort(dir = Direction.asc, sort = "loggerName") SortingParameters sorting,
            String loggerName, LoggerLevel[] loggerLevels, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {
        retrieveLoggers(sorting, loggerName, loggerLevels, model, userContext, request);
        model.addAttribute("loggerLevels", LoggerLevel.values());
        return "config/loggers.jsp";
    }

    @GetMapping("/config/loggers")
    public String addLogger(@ModelAttribute YukonLogger logger, ModelMap model) {

        model.addAttribute("loggerLevels", LoggerLevel.values());
        model.addAttribute("isEditMode", false);
        model.addAttribute("logger", logger);
        Date expirationDate = new Date();
        model.addAttribute("now", expirationDate);
        model.addAttribute("allowDateTimeSelection", true);

        return "config/addLoggerPopup.jsp";
    }

    @PostMapping("/config/loggers")
    public String saveLogger(@ModelAttribute YukonLogger yukonLogger, Boolean specifiedDateTime, HttpServletRequest request,
            HttpServletResponse resp, YukonUserContext userContext, BindingResult result, RedirectAttributes redirectAttributes) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<String, Object>();
        yukonLoggersValidator.validate(yukonLogger, result);
        if (result.hasErrors()) {
            return bindAndForward(yukonLogger, result, redirectAttributes);
        }
        try {
            ResponseEntity<? extends Object> response = save(yukonLogger, specifiedDateTime, request, userContext);
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                json.put("isSystemLogger", SystemLogger.isSystemLogger(yukonLogger.getLoggerName()));
                json.put("successMessage", accessor.getMessage("yukon.common.save.success", yukonLogger.getLoggerName()));
                return JsonUtils.writeResponse(resp, json);
            }
        } catch (ApiCommunicationException e) {
            log.error(e);
            json.put("errorMessage", accessor.getMessage(communicationKey));
        } catch (RestClientException ex) {
            log.error("Error saving logger. Error: {}", ex.getMessage());
            json.put("errorMessage", accessor.getMessage("yukon.web.api.save.error", yukonLogger.getLoggerName(), ex.getMessage()));
        }
        return JsonUtils.writeResponse(resp, json);
    }

    private String bindAndForward(YukonLogger yukonLogger, BindingResult result, RedirectAttributes redirectAttributes) {
       // redirectAttributes.addFlashAttribute("yukonLogger", yukonLogger);
       // redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.yukonLogger", result);
        if (yukonLogger.getLoggerId() == -1) {
            return "redirect:loggers";
        }
        return "redirect:loggers/" + yukonLogger.getLoggerId();

    }

    @GetMapping("/config/loggers/{loggerId}")
    public String editLogger(@ModelAttribute YukonLogger logger, @PathVariable Integer loggerId, ModelMap model,
            HttpServletRequest request, YukonUserContext userContext) {
        try {
            String getUrl = apiControllerHelper.findWebServerUrl(request, userContext,
                    ApiURL.loggerUrl + "/" + loggerId);

            ResponseEntity<? extends Object> loggerResponse = apiRequestHelper.callAPIForObject(userContext, request,
                    getUrl, HttpMethod.GET, YukonLogger.class, Integer.class);

            if (loggerResponse.getStatusCode() == HttpStatus.OK) {
                logger = (YukonLogger) loggerResponse.getBody();
                model.addAttribute("logger", logger);
                model.addAttribute("loggerLevels", LoggerLevel.values());
                model.addAttribute("isEditMode", true);
                Date expirationDate = new Date();
                model.addAttribute("now", expirationDate);
                model.addAttribute("specifiedDateTime", logger.getExpirationDate() != null);

                boolean allowDateTimeSelection = !SystemLogger.isSystemLogger(logger.getLoggerName());
                model.addAttribute("allowDateTimeSelection", allowDateTimeSelection);
            }
        } catch (ApiCommunicationException e) {
            log.error(e);
        } catch (RestClientException ex) {
            log.error("Error retreiving logger. Error: {}", ex.getMessage());
        }
        return "config/addLoggerPopup.jsp";
    }

    private ResponseEntity<? extends Object> save(YukonLogger logger, Boolean specifiedDateTime,
            HttpServletRequest request, YukonUserContext userContext) {
        
        if (BooleanUtils.isNotTrue(specifiedDateTime)) {
            logger.setExpirationDate(null);
        }
        if (logger.getLoggerId() == -1) {
            String url = apiControllerHelper.findWebServerUrl(request, userContext, ApiURL.loggerUrl);
            return apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, YukonLogger.class,
                    logger);
        } else {
            String url = apiControllerHelper.findWebServerUrl(request, userContext,
                    ApiURL.loggerUrl + "/" + logger.getLoggerId());
            return apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.PATCH, YukonLogger.class,
                    logger);
        }
    }

    @DeleteMapping("/config/loggers/{loggerId}")
    public String deleteLogger(@PathVariable Integer loggerId, String loggerName, ModelMap model, FlashScope flashScope,
            HttpServletRequest request, YukonUserContext userContext) {

        String deleteUrl = apiControllerHelper.findWebServerUrl(request, userContext,
                ApiURL.loggerUrl + "/" + loggerId);
        ResponseEntity<? extends Object> deleteResponse = apiRequestHelper.callAPIForObject(userContext, request,
                deleteUrl, HttpMethod.DELETE, Object.class, Integer.class);

        if (deleteResponse.getStatusCode() == HttpStatus.OK) {
            flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", loggerName));
        } else {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String inValidId = accessor.getMessage(baseKey + "inValidId");
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", loggerName, inValidId));
        }
        return redirectLink;
    }

    @GetMapping("/config/loggers/filter")
    public String filter(@DefaultSort(dir = Direction.asc, sort = "loggerName") SortingParameters sorting,
            String loggerName, LoggerLevel[] loggerLevels, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {
        retrieveLoggers(sorting, loggerName, loggerLevels, model, userContext, request);

        return "config/userLoggersTable.jsp";
    }

    @GetMapping("/config/loggers/getSystemLoggers")
    public String getSystemLoggers(@DefaultSort(dir = Direction.asc, sort = "loggerName") SortingParameters sorting,
            String loggerName, LoggerLevel[] loggerLevels, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {
        retrieveLoggers(sorting, loggerName, loggerLevels, model, userContext, request);

        return "config/systemLoggersTable.jsp";
    }

    private void retrieveLoggers(SortingParameters sorting, String loggerName, LoggerLevel[] loggerLevels,
            ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        List<YukonLogger> userLoggers = new ArrayList<YukonLogger>();
        List<YukonLogger> systemLoggers = new ArrayList<YukonLogger>();
        List<YukonLogger> loggers = new ArrayList<YukonLogger>();

        FilterSortBy sortBy = FilterSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<SortableColumn> columns = new ArrayList<>();
        for (FilterSortBy column : FilterSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

        try {
            String url = apiControllerHelper.findWebServerUrl(request, userContext, ApiURL.loggerUrl);
            URIBuilder ub = new URIBuilder(url);
            ub.addParameter("sort", sortBy.getValue().name());
            ub.addParameter("dir", dir.name());
            if (loggerLevels != null) {
                for (LoggerLevel level : loggerLevels) {
                    ub.addParameter("loggerLevels", level.name());
                }
            }
            if (loggerName != null) {
                ub.addParameter("loggerName", loggerName);
            }
            ResponseEntity<List<? extends Object>> loggersResponse = apiRequestHelper.callAPIForList(userContext,
                    request, ub.toString(), YukonLogger.class, HttpMethod.GET, YukonLogger.class);
            if (loggersResponse.getStatusCode() == HttpStatus.OK) {
                loggers = (List<YukonLogger>) loggersResponse.getBody();
            }

        } catch (ApiCommunicationException e) {
            log.error(e);
        } catch (RestClientException ex) {
            log.error("Error retrieving loggers. Error: {}", ex.getMessage());
        } catch (URISyntaxException e) {
            log.error("URI syntax error while creating builder for retrieving loggers", e);
        }

        loggers.stream().forEach(logger -> {
            if (SystemLogger.isSystemLogger(logger.getLoggerName())) {
                systemLoggers.add(logger);
            } else {
                userLoggers.add(logger);
            }
        });
        model.addAttribute("userLoggers", userLoggers);
        model.addAttribute("systemLoggers", systemLoggers);
    }

    public enum FilterSortBy implements DisplayableEnum {

        loggerName(SortBy.NAME),
        loggerLevel(SortBy.LEVEL),
        expirationDate(SortBy.EXPIRATION);

        private final SortBy value;

        private FilterSortBy(SortBy value) {
            this.value = value;
        }

        public SortBy getValue() {
            return value;
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.adminSetup.config.loggers." + name();
        }
    }
}