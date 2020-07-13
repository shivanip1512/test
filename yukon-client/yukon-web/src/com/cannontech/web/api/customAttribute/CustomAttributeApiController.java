package com.cannontech.web.api.customAttribute;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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

import com.cannontech.common.exception.ResourceNotFoundException;
import com.cannontech.common.pao.attribute.dao.AttributeDao;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.admin.AttributeValidator;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@RestController
@RequestMapping("/attributes")
@CheckPermissionLevel(property = YukonRoleProperty.ADMIN_MANAGE_ATTRIBUTES, level = HierarchyPermissionLevel.OWNER)
public class CustomAttributeApiController {

    @Autowired private AttributeDao attributeDao;
    @Autowired private AttributeValidator customAttributeValidator;

    @PostMapping("")
    public ResponseEntity<Object> create(@Valid @RequestBody CustomAttribute customAttribute) {
        attributeDao.saveCustomAttribute(customAttribute);
        return new ResponseEntity<>(customAttribute, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> retrieve(@PathVariable int id) throws ResourceNotFoundException {
        CustomAttribute attribute;
        try {
            attribute = attributeDao.getCustomAttribute(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("No custom attribute with the ID " + id + " was found");
        }
        return new ResponseEntity<>(attribute, HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable int id, @Valid @RequestBody CustomAttribute customAttribute)
            throws ResourceNotFoundException {
        CustomAttribute attribute = attributeDao.getCustomAttribute(id);
        if (attribute == null) {
            throw new ResourceNotFoundException("No custom attribute with the ID " + id + " was found");
        }
        customAttribute.setId(id);
        attributeDao.saveCustomAttribute(customAttribute);
        customAttribute = attributeDao.getCustomAttribute(customAttribute.getId());
        return new ResponseEntity<>(customAttribute, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) throws ResourceNotFoundException {
        CustomAttribute attribute = attributeDao.getCustomAttribute(id);
        if (attribute == null) {
            throw new ResourceNotFoundException("No custom attribute with the ID " + id + " was found");
        }
        attributeDao.deleteCustomAttribute(id);
        Map<String, Integer> jsonResponse = new HashMap<String, Integer>();
        jsonResponse.put("id", id);
        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<Object> list() {
        return new ResponseEntity<>(attributeDao.getCustomAttributes(), HttpStatus.OK);
    }

    @InitBinder("customAttribute")
    public void setBinder(WebDataBinder binder) {
        binder.addValidators(customAttributeValidator);
    }
}
