package com.cannontech.web.api.loggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.clientutils.logger.service.YukonLoggerService.SortBy;
import com.cannontech.common.log.model.LoggerLevel;
import com.cannontech.common.log.model.YukonLogger;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;

@RestController
@RequestMapping("/config/loggers")
public class YukonLoggersApiController {

    @Autowired private YukonLoggerService loggerService;
    @Autowired private YukonLoggersApiValidator yukonLoggersApiValidator;
    
    @GetMapping
    public ResponseEntity<Object> getAll(String loggerName, LoggerLevel[] loggerLevels,
            @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting) {
        SortBy sortBy = SortBy.valueOf(sorting.getSort());
        Direction direction = sorting.getDirection();
        List<LoggerLevel> LoggerLevelList = new ArrayList<>();
        if (loggerLevels != null) {
            LoggerLevelList = Arrays.asList(loggerLevels);
        }
        return new ResponseEntity<Object>(loggerService.getLoggers(loggerName, sortBy, direction, LoggerLevelList),
                HttpStatus.OK);
    }

    @GetMapping("/{loggerId}")
    public ResponseEntity<Object> get(@PathVariable int loggerId) {
        return new ResponseEntity<Object>(loggerService.getLogger(loggerId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody YukonLogger yukonLogger) {
        return new ResponseEntity<Object>(loggerService.addLogger(yukonLogger), HttpStatus.CREATED);
    }

    @PatchMapping("/{loggerId}")
    public ResponseEntity<Object> update(@PathVariable int loggerId, @Valid @RequestBody YukonLogger yukonLogger) {
        yukonLogger.setLoggerId(loggerId);
        return new ResponseEntity<Object>(loggerService.updateLogger(loggerId, yukonLogger), HttpStatus.OK);
    }

    @DeleteMapping("/{loggerId}")
    public ResponseEntity<Object> delete(@PathVariable int loggerId) {
        Map<String, Integer> loggerMap = new HashMap<String, Integer>();
        int id = loggerService.deleteLogger(loggerId);
        loggerMap.put("loggerId", id);
        return new ResponseEntity<Object>(loggerMap, HttpStatus.OK);
    }
    
    @InitBinder("yukonLogger")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(yukonLoggersApiValidator);
    }
}