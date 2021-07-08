package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.SystemLogger;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.FilterCriteria;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.AttributesController.AssignmentSortBy;
import com.cannontech.web.api.dr.setup.dao.LMSetupDao.SortBy;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
public class YukonLoggersController {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private static final String redirectLink = "redirect:/admin/config/loggers";
    private ConcurrentHashMap<Integer, YukonLogger> cache = new ConcurrentHashMap<Integer, YukonLogger>();

    @PostConstruct
    public void init() {
        SystemLogger loggers[] = SystemLogger.values();
        for (int id = 0; id < loggers.length; id++) {
            YukonLogger logger = new YukonLogger();
            logger.setLoggerId(id);
            logger.setLoggerName(loggers[id].getLoggerName());
            logger.setLevel(loggers[id].getLevel());
            cache.put(id, logger);
        }
    }

    @GetMapping("/config/loggers/allLoggers")
    public String getAllLoggers(@DefaultSort(dir = Direction.asc, sort = "loggerName") SortingParameters sorting,
            ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            HttpServletResponse resp) {

        List<YukonLogger> userLoggers = new ArrayList<YukonLogger>();
        List<YukonLogger> systemLoggers = new ArrayList<YukonLogger>();

        for (Entry<Integer, YukonLogger> loggerEntry : cache.entrySet()) {
            YukonLogger logger = loggerEntry.getValue();
            if (SystemLogger.isSystemLogger(logger.getLoggerName())) {
                systemLoggers.add(logger);
            } else {
                userLoggers.add(logger);
            }
        }
        // add sorting here
        model.addAttribute("userLoggers", userLoggers);
        model.addAttribute("systemLoggers", systemLoggers);

        return "config/loggers.jsp";
    }

    @GetMapping("/config/loggers")
    public String addLogger(@ModelAttribute YukonLogger logger, BindingResult result,
            ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            HttpServletResponse resp, FlashScope flash, RedirectAttributes redirectAttributes) {

        model.addAttribute("loggerLevels", LoggerLevel.values());
        model.addAttribute("isEditMode", false);
        model.addAttribute("logger", logger);
        model.addAttribute("allowDateTimeSelection", true);

        return "config/addLoggerPopup.jsp";
    }

    @PostMapping("/config/loggers")
    public String createLogger(@ModelAttribute YukonLogger logger, BindingResult result,
            ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            HttpServletResponse resp, FlashScope flash, RedirectAttributes redirectAttributes) {

        save(logger);
        return redirectLink;
    }

    @GetMapping("/config/loggers/{loggerId}")
    public String editLogger(@ModelAttribute YukonLogger logger, @PathVariable Integer loggerId, BindingResult result,
            ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            HttpServletResponse resp, FlashScope flash, RedirectAttributes redirectAttributes) {
        model.addAttribute("logger", cache.get(loggerId));
        model.addAttribute("loggerLevels", LoggerLevel.values());
        model.addAttribute("isEditMode", true);
        model.addAttribute("allowDateTimeSelection", true);
        return "config/addLoggerPopup.jsp";
    }

    @PatchMapping("/config/loggers/{loggerId}")
    public String saveLogger(@ModelAttribute YukonLogger logger, @PathVariable int loggerId, BindingResult result,
            ModelMap model, YukonUserContext userContext, HttpServletRequest request,
            HttpServletResponse resp, FlashScope flash, RedirectAttributes redirectAttributes) {
        save(logger);
        return redirectLink;
    }

    private void save(YukonLogger logger) {
        if (logger.getLoggerId() == 0) {
            List<Integer> sortedKeys = cache.keySet().stream().sorted().collect(Collectors.toList());
            sortedKeys.add(sortedKeys.size());
            logger.setLoggerId(sortedKeys.get(sortedKeys.size()-1));
            cache.put(logger.getLoggerId(), logger);
        } else {
            cache.remove(logger.getLoggerId());
            cache.put(logger.getLoggerId(), logger);
        }
    }

    @GetMapping("/config/loggers/filter")
    public String filter(@ModelAttribute YukonLogger loggerFilter,
            @DefaultSort(dir = Direction.asc, sort = "loggerName") SortingParameters sorting, YukonLogger[] selectedLoggers,
            String loggerName, LoggerLevel[] loggerLevels,
            ModelMap model, PagingParameters paging, YukonUserContext userContext, HttpServletRequest request,
            HttpServletResponse resp) {
        retrieveLoggers(sorting, selectedLoggers, loggerLevels, model, userContext, request, resp);
        model.addAttribute("loggers", selectedLoggers);
        model.addAttribute("loggerLevels", LoggerLevel.values());
        return "config/userLoggersTable.jsp";
    }

    private void retrieveLoggers(SortingParameters sorting, YukonLogger[] selectedLoggers, LoggerLevel[] loggerLevels,
            ModelMap model,
            YukonUserContext userContext, HttpServletRequest request, HttpServletResponse resp) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        FilterSortBy sortBy = FilterSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<SortableColumn> columns = new ArrayList<>();
        for (FilterSortBy column : FilterSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }
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

    public enum SortBy {
        NAME("loggerName"),
        LEVEL("loggerLevel"),
        EXPIRATION("expirationDate");

        private final String dbString;

        private SortBy(String dbString) {
            this.dbString = dbString;
        }

        public String getDbString() {
            return dbString;
        }
    }
}