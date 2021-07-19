package com.cannontech.web.api.loggers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.clientutils.logger.service.YukonLoggerService;
import com.cannontech.common.log.model.YukonLogger;

@RestController
@RequestMapping("/config/loggers")
public class YukonLoggersApiController {

    @Autowired private YukonLoggerService loggerService;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return new ResponseEntity<Object>(loggerService.getLoggers(), HttpStatus.OK);

    }

    @GetMapping("{loggerId}")
    public ResponseEntity<Object> get(@PathVariable int loggerId) {
        return new ResponseEntity<Object>(loggerService.getLogger(loggerId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody YukonLogger logger) {
        return new ResponseEntity<Object>(loggerService.addLogger(logger), HttpStatus.CREATED);

    }

    @PatchMapping("{loggerId}")
    public ResponseEntity<Object> update(@RequestBody YukonLogger logger, @PathVariable int loggerId) {
        return new ResponseEntity<Object>(loggerService.updateLogger(loggerId, logger), HttpStatus.OK);

    }

    @DeleteMapping("{loggerId}")
    public ResponseEntity<Object> delete(@PathVariable int loggerId) {
        Map<String, Integer> loggerMap = new HashMap<String, Integer>();
        int id = loggerService.deleteLogger(loggerId);
        loggerMap.put("loggerId", id);
        return new ResponseEntity<Object>(loggerMap, HttpStatus.OK);

    }

}
