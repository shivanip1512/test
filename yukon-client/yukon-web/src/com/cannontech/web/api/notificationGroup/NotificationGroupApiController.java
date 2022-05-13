package com.cannontech.web.api.notificationGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.web.api.notificationGroup.service.NotificationGroupService;
import com.cannontech.web.notificationGroup.NotificationGroup;

@RestController
@RequestMapping("/notificationGroups")
public class NotificationGroupApiController {

    @Autowired NotificationGroupService notificationGroupService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(notificationGroupService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody NotificationGroup notificationGroup) {
        return new ResponseEntity<>(notificationGroupService.create(notificationGroup), HttpStatus.CREATED);
    }
}
