package com.cannontech.web.api.aggregateIntervalDataReport;

import java.util.ArrayList;
import java.util.List;

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
//    @Autowired private AggregateIntervalReportService aggregateIntervalReportService;


    @PostMapping("")
    public ResponseEntity<Object> create(@Valid @RequestBody AggregateIntervalReportFilter aggregateIntervalReportFilter, YukonUserContext userContext) {
        return new ResponseEntity<>(getIntervalDataReport(aggregateIntervalReportFilter, userContext), HttpStatus.OK);
//        return new ResponseEntity<>(aggregateIntervalReportService.getIntervalDataReport(filter, userContext), HttpStatus.OK);
    }

    @InitBinder("aggregateIntervalReportFilter")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.setValidator(aggregateIntervalDataReportValidator);
    }
    
    // This is only for testing purposes and is to be removed when the service layer is wired up
    public List<List<String>> getIntervalDataReport(AggregateIntervalReportFilter filter, YukonUserContext context){
        List<List<String>> report = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        row.add("02/04/2020");
        row.add("01:15:00");
        row.add("4");
        report.add(row);
        row.add("02/04/2020");
        row.add("01:30:00");
        row.add("5");
        report.add(row);
        row = new ArrayList<>();
        row.add("02/04/2020");
        row.add("01:45:00");
        row.add("10");
        report.add(row);
        row = new ArrayList<>();
        row.add("02/04/2020");
        row.add("02:00:00");
        row.add(" ");
        report.add(row);
        return report;
    }

}
