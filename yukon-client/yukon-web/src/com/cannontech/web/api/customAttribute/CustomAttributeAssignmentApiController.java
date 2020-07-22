package com.cannontech.web.api.customAttribute;

import java.util.ArrayList;
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

import com.cannontech.common.device.dao.DevicePointDao;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.admin.dao.CustomAttributeDao;
import com.cannontech.web.admin.dao.CustomAttributeDao.SortBy;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/attributeAssignment")
@CheckPermissionLevel(property = YukonRoleProperty.ADMIN_MANAGE_ATTRIBUTES, level = HierarchyPermissionLevel.OWNER)
public class CustomAttributeAssignmentApiController {

    @Autowired private AttributeDao attributeDao;
    @Autowired private CustomAttributeAssignmentValidator customAttributeAssignmentValidator;
    @Autowired private CustomAttributeAssignmentCreationValidator customAttributeAssignmentCreationValidator;
    @Autowired private CustomAttributeDao customAttributeDao;

    @PostMapping("")
    public ResponseEntity<Object> create(@Valid @RequestBody Assignment assignment) {
        attributeDao.saveAttributeAssignment(assignment);
        CustomAttribute customAttribute = attributeDao.getCustomAttribute(assignment.getAttributeId());
        AttributeAssignment attributeAssignment = new AttributeAssignment(assignment, customAttribute);

        return new ResponseEntity<>(attributeAssignment, HttpStatus.OK);
    }

    @GetMapping("/{attributeAssignmentId}")
    public ResponseEntity<Object> retrieve(@PathVariable int attributeAssignmentId) {
        return new ResponseEntity<>(attributeDao.getAssignmentById(attributeAssignmentId), HttpStatus.OK);
    }

    @PatchMapping("/{attributeAssignmentId}")
    public ResponseEntity<Object> update(@PathVariable int attributeAssignmentId, @Valid @RequestBody Assignment assignment) {
        assignment.setAttributeId(attributeAssignmentId);
        attributeDao.saveAttributeAssignment(assignment);
        CustomAttribute customAttribute = attributeDao.getCustomAttribute(assignment.getAttributeId());
        AttributeAssignment attributeAssignment = new AttributeAssignment(assignment, customAttribute);

        return new ResponseEntity<>(attributeAssignment, HttpStatus.OK);
    }

    @DeleteMapping("/{attributeAssignmentId}")
    public ResponseEntity<Object> delete(@PathVariable int attributeAssignmentId) {
        attributeDao.deleteAttributeAssignment(attributeAssignmentId);

        Map<String, Object> jsonResponse = new HashMap<String, Object>();
        jsonResponse.put("id", attributeAssignmentId);

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Object> list(ArrayList<Integer> attributeIds, ArrayList<PaoType> paoTypes,
            @DefaultSort(dir = Direction.asc, sort = "attributeName") SortingParameters sorting) {
        SortBy sortBy = CustomAttributeDao.SortBy.valueOf(sorting.getSort());
        Direction direction = sorting.getDirection();

        return new ResponseEntity<>(customAttributeDao.getCustomAttributeDetails(attributeIds, paoTypes, sortBy, direction),
                HttpStatus.OK);
    }

    @InitBinder("assignment")
    public void setupBinderDelete(WebDataBinder binder) {
         binder.setValidator(customAttributeAssignmentValidator);

        String attributeAssignmentId = ServletUtils.getPathVariable("attributeAssignmentId");
        if (attributeAssignmentId == null) {
            binder.setValidator(customAttributeAssignmentCreationValidator);
        }
    }

}
