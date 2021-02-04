package com.cannontech.web.api.customAttribute;

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

import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.admin.dao.CustomAttributeDao;
import com.cannontech.web.admin.dao.CustomAttributeDao.SortBy;
import com.cannontech.web.admin.service.impl.CustomAttributeService;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/attributeAssignments")
@CheckPermissionLevel(property = YukonRoleProperty.ADMIN_MANAGE_ATTRIBUTES, level = HierarchyPermissionLevel.OWNER)
public class CustomAttributeAssignmentApiController {

    @Autowired private AttributeDao attributeDao;
    @Autowired private CustomAttributeDao customAttributeDao;
    @Autowired private CustomAttributeService customAttributeService;
    @Autowired private SystemEventLogService systemEventLogService;
    @Autowired private CustomAttributeAssignmentCreationApiValidator customAttributeAssignmentCreationApiValidator;
    @Autowired private CustomAttributeAssignmentApiValidator customAttributeAssignmentApiValidator;



    @PostMapping("")
    public ResponseEntity<Object> create(@Valid @RequestBody Assignment assignment, YukonUserContext userContext) {
        return new ResponseEntity<>(customAttributeService.createAttributeAssignment(assignment, userContext), HttpStatus.CREATED);
    }

    @GetMapping("/{attributeAssignmentId}")
    public ResponseEntity<Object> retrieve(@PathVariable int attributeAssignmentId) {
        return new ResponseEntity<>(attributeDao.getAssignmentById(attributeAssignmentId), HttpStatus.OK);
    }

    @PatchMapping("/{attributeAssignmentId}")
    public ResponseEntity<Object> update(@PathVariable int attributeAssignmentId, @Valid @RequestBody Assignment assignment, YukonUserContext userContext) {
        assignment.setAttributeAssignmentId(attributeAssignmentId);
        return new ResponseEntity<>(customAttributeService.updateAttributeAssignment(assignment, userContext), HttpStatus.OK);
    }

    @DeleteMapping("/{attributeAssignmentId}")
    public ResponseEntity<Object> delete(@PathVariable int attributeAssignmentId, YukonUserContext userContext) {
        customAttributeService.deleteAttributeAssignment(attributeAssignmentId, userContext);
        Map<String, Object> jsonResponse = new HashMap<String, Object>();
        jsonResponse.put("id", attributeAssignmentId);

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    /**
     * 
     * Example url:
     * /api/attributeAssignment?attributeIds=1&attributeIds=3&paoTypes=VIRTUAL_SYSTEM&paoTypes=RFN420FL&sort=attributeName&dir=desc
     * 
     */
    @GetMapping("")
    public ResponseEntity<Object> list(Integer[] attributeIds, PaoType[] paoTypes,
            @DefaultSort(dir = Direction.asc, sort = "attributeName") SortingParameters sorting) {
        SortBy sortBy = CustomAttributeDao.SortBy.valueOf(sorting.getSort());
        Direction direction = sorting.getDirection();
        List<Integer> attributeIdList = new ArrayList<>();
        if (attributeIds != null) {
            attributeIdList = Arrays.asList(attributeIds);
        }
        List<PaoType> paoTypeList = new ArrayList<>();
        if (paoTypes != null) {
            paoTypeList = Arrays.asList(paoTypes);
        }

        return new ResponseEntity<>(customAttributeDao.getCustomAttributeDetails(attributeIdList, paoTypeList, sortBy, direction),
                HttpStatus.OK);
    }

    @InitBinder("assignment")
    public void setupBinderDelete(WebDataBinder binder) {
        binder.setValidator(customAttributeAssignmentApiValidator);

        String attributeAssignmentId = ServletUtils.getPathVariable("attributeAssignmentId");
        if (attributeAssignmentId == null) {
            binder.setValidator(customAttributeAssignmentCreationApiValidator);
        }
    }

}
