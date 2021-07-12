package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.SystemLogger;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.sort.SortableColumn;

@Controller
public class YukonLoggersController {

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private static final String redirectLink = "redirect:/admin/config/loggers/allLoggers";
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
            String loggerName, LoggerLevel[] loggerLevels, ModelMap model, YukonUserContext userContext) {

        retrieveLoggers(sorting, loggerName, loggerLevels, model, userContext);
        model.addAttribute("filter", new YukonLogger());
        return "config/loggers.jsp";
    }

    @GetMapping("/config/loggers")
    public String addLogger(@ModelAttribute YukonLogger logger, ModelMap model, YukonUserContext userContext) {

        model.addAttribute("loggerLevels", LoggerLevel.values());
        model.addAttribute("isEditMode", false);
        model.addAttribute("logger", logger);
        Date expirationDate = new Date();
        model.addAttribute("now", expirationDate);
        model.addAttribute("allowDateTimeSelection", true);

        return "config/addLoggerPopup.jsp";
    }

    @PostMapping("/config/loggers")
    public String createLogger(@ModelAttribute YukonLogger logger) {
        save(logger);
        return redirectLink;
    }

    @GetMapping("/config/loggers/{loggerId}")
    public String editLogger(@ModelAttribute YukonLogger logger, @PathVariable Integer loggerId, ModelMap model) {
        model.addAttribute("logger", cache.get(loggerId));
        model.addAttribute("loggerLevels", LoggerLevel.values());
        model.addAttribute("isEditMode", true);
        model.addAttribute("allowDateTimeSelection", true);
        return "config/addLoggerPopup.jsp";
    }

    @PatchMapping("/config/loggers/{loggerId}")
    public String saveLogger(@ModelAttribute YukonLogger logger, @PathVariable int loggerId) {
        save(logger);
        return redirectLink;
    }

    private void save(YukonLogger logger) {
        if (logger.getLoggerId() == 0) {
            List<Integer> sortedKeys = cache.keySet().stream().sorted().collect(Collectors.toList());
            sortedKeys.add(sortedKeys.size());
            logger.setLoggerId(sortedKeys.get(sortedKeys.size() - 1));
            cache.put(logger.getLoggerId(), logger);
        } else {
            cache.remove(logger.getLoggerId());
            cache.put(logger.getLoggerId(), logger);
        }
    }

    // TODO: The filter and sorting remaining part will be covered in YUK-24684
    @GetMapping("/config/loggers/filter")
    public String filter(@ModelAttribute("filter") YukonLogger loggerFilter,
            @DefaultSort(dir = Direction.asc, sort = "loggerName") SortingParameters sorting, String loggerName,
            LoggerLevel[] loggerLevels, ModelMap model, YukonUserContext userContext) {
        retrieveLoggers(sorting, loggerName, loggerLevels, model, userContext);

        model.addAttribute("loggerName", loggerName);
        model.addAttribute("loggerLevels", LoggerLevel.values());
        model.addAttribute("filter", loggerFilter);
        return "config/userLoggersTable.jsp";
    }
    
    private void retrieveLoggers(SortingParameters sorting, String loggerName, LoggerLevel[] loggerLevels,
            ModelMap model, YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
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

        FilterSortBy sortBy = FilterSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<SortableColumn> columns = new ArrayList<>();
        for (FilterSortBy column : FilterSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }

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