package com.cannontech.web.api.notificationGroup;

import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.core.dao.NotificationGroupDao.SortBy;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.notificationGroup.service.NotificationGroupService;
import com.cannontech.web.notificationGroup.NotificationGroup;

@RestController
@RequestMapping("/notificationGroups")
public class NotificationGroupApiController {

    @Autowired private NotificationGroupService notificationGroupService;
    @Autowired private NotificationGroupApiValidator apiValidator;
    @Autowired private NotificationGroupApiCreateValidator createApiValidator;

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) {
        return new ResponseEntity<>(notificationGroupService.retrieve(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NotificationGroup notificationGroup) {
        return new ResponseEntity<>(notificationGroupService.create(notificationGroup), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, Integer>> delete(@PathVariable int id) {
        Integer notificationgroupId = notificationGroupService.delete(id);
        HashMap<String, Integer> notificationGroupIdMap = new HashMap<>();
        notificationGroupIdMap.put("id", notificationgroupId);
        return new ResponseEntity<>(notificationGroupIdMap, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(String name, @DefaultSort(dir = Direction.asc, sort = "NAME") SortingParameters sorting,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "itemsPerPage", defaultValue = "250") int itemsPerPage) {
        SortBy sortBy = SortBy.valueOf(sorting.getSort());
        Direction direction = sorting.getDirection();
        // Sorting happens wrt UI. Reversing the order for db query
        // Asc sort -> Disabled(UI) or Y(DB)
        if (sortBy.toString().equals("STATUS")) {
            if (direction.toString().equals("asc")) {
                direction = Direction.desc;
            } else {
                direction = Direction.asc;
            }
        }
        return new ResponseEntity<>(notificationGroupService.retrieveAll(name, sortBy, direction, page, itemsPerPage),
                HttpStatus.OK);
    }

    @InitBinder("notificationGroup")
    public void setupBinder(WebDataBinder binder) {
        binder.addValidators(apiValidator);

        String notifGrpId = ServletUtils.getPathVariable("id");
        if (notifGrpId == null) {
            binder.addValidators(createApiValidator);
        }
    }
}
