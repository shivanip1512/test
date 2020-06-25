package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class AttributesController {

    @GetMapping("/config/attributes")
    public String attributes(ModelMap model) {
        retrieveCustomAttributes(model);
        return "config/attributes.jsp";
    }
    
    @PostMapping("/config/attribute/create")
    public String attributeCreate(ModelMap model, String name) {
        //TODO: Call Create REST API with name
        retrieveCustomAttributes(model);
        return "config/attributes.jsp";
    }
    
    @PostMapping("/config/attribute/{id}/update")
    public String updateAttribute(ModelMap model, @PathVariable int id, String name) {
        //TODO: Call Update REST API with id and name
        retrieveCustomAttributes(model);
        return "config/attributes.jsp";
    }
    
    @DeleteMapping("/config/attribute/{id}/delete")
    public String deleteAttribute(ModelMap model, @PathVariable int id) {
        //TODO: Call Delete REST API with id
        retrieveCustomAttributes(model);
        return "config/attributes.jsp";
    }
    
    private void retrieveCustomAttributes(ModelMap model) {
        //TODO: Call list REST API
        //mock up data for now
        List<DeviceBaseModel> atts = new ArrayList<>();
        atts.add(new DeviceBaseModel(123, null, "CustomAttribute-001", true));
        atts.add(new DeviceBaseModel(124, null, "CustomAttribute-002", true));
        atts.add(new DeviceBaseModel(125, null, "CustomAttribute-003", true));
        atts.add(new DeviceBaseModel(126, null, "CustomAttribute-004", true));
        atts.add(new DeviceBaseModel(127, null, "CustomAttribute-005", true));
        atts.add(new DeviceBaseModel(128, null, "CustomAttribute-006", true));
        atts.add(new DeviceBaseModel(129, null, "CustomAttribute-007", true));
        atts.add(new DeviceBaseModel(130, null, "CustomAttribute-008", true));
        atts.add(new DeviceBaseModel(131, null, "CustomAttribute-009", true));
        atts.add(new DeviceBaseModel(132, null, "CustomAttribute-010", true));
        atts.add(new DeviceBaseModel(133, null, "CustomAttribute-011", true));
        atts.add(new DeviceBaseModel(134, null, "CustomAttribute-012", true));
        atts.add(new DeviceBaseModel(135, null, "CustomAttribute-013", true));
        atts.add(new DeviceBaseModel(136, null, "CustomAttribute-014", true));
        atts.add(new DeviceBaseModel(137, null, "CustomAttribute-015", true));
        atts.add(new DeviceBaseModel(138, null, "CustomAttribute-016", true));
        atts.add(new DeviceBaseModel(139, null, "CustomAttribute-017", true));
        atts.add(new DeviceBaseModel(140, null, "CustomAttribute-018", true));
        atts.add(new DeviceBaseModel(141, null, "CustomAttribute-019", true));
        atts.add(new DeviceBaseModel(142, null, "CustomAttribute-020", true));

        model.addAttribute("attributes", atts);
    }
}