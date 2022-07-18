/*
package com.cannontech.web.api.schedules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.schedules.model.HolidaySchedule;
import com.cannontech.web.api.schedules.service.HolidayScheduleService;

@RestController
@RequestMapping("/holidaySchedules")
public class HolidayScheduleApiController {
    @Autowired private HolidayScheduleService holidayScheduleService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody HolidaySchedule holidaySchedule) {
        return new ResponseEntity<>(holidayScheduleService.create(holidaySchedule), HttpStatus.CREATED);
    }
}
*/
