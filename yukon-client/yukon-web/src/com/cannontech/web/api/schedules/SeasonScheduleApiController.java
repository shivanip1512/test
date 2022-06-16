package com.cannontech.web.api.schedules;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.schedules.model.SeasonSchedule;
import com.cannontech.web.api.schedules.service.SeasonScheduleService;

@RestController
@RequestMapping("/seasonSchedules")
public class SeasonScheduleApiController {

    @Autowired private SeasonScheduleService seasonScheduleService;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody SeasonSchedule seasonSchedule) {
        return new ResponseEntity<>(seasonScheduleService.create(seasonSchedule), HttpStatus.CREATED);
    }

}
