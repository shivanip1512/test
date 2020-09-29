package com.cannontech.web.api.aggregateIntervalDataReport;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService;
import com.cannontech.web.tools.reports.service.AggregateIntervalReportService.AggregateIntervalReportFilter;

@RestController
@RequestMapping("/aggregateIntervalDataReport/report")
public class AggregateIntervalDataReportApiController {

    @Autowired private AggregateIntervalDataReportValidator aggregateIntervalDataReportValidator;
    @Autowired private AggregateIntervalReportService aggregateIntervalReportService;


    @PostMapping("")
    public ResponseEntity<Object> create(@Valid @RequestBody AggregateIntervalReportFilter aggregateIntervalReportFilter, YukonUserContext userContext) {
        return new ResponseEntity<>(aggregateIntervalReportService.getIntervalDataReport(aggregateIntervalReportFilter, userContext), HttpStatus.OK);
    }

    @InitBinder("aggregateIntervalReportFilter")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.setValidator(aggregateIntervalDataReportValidator);
    }
}
