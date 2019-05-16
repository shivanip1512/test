package com.cannontech.web.dr.setup;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/setup/loadGroup")
public class LoadGroupSetupController {
    
    private static final String baseKey = "yukon.web.modules.dr.setup.loadGroup.";
    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupController.class);

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadGroupBase loadGroup = new LoadGroupBase();
        model.addAttribute("loadGroup", loadGroup);
        List<PaoType> switchTypes = Lists.newArrayList(PaoType.LM_GROUP_METER_DISCONNECT);
        model.addAttribute("switchTypes", switchTypes);
        return "dr/setup/loadGroup/view.jsp";
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, @PathVariable int id) {
        model.addAttribute("mode", PageEditMode.VIEW);
        LoadGroupBase loadGroup = mockBaseLMLoadGroup();
        model.addAttribute("loadGroup", loadGroup);
        return "dr/setup/loadGroup/view.jsp";
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model) {
        model.addAttribute("mode", PageEditMode.EDIT);
        LoadGroupBase loadGroup = mockBaseLMLoadGroup();
        model.addAttribute("loadGroup", loadGroup);
        return "dr/setup/loadGroup/view.jsp";
    }
    
    // TODO: Remove unused parameters
    @PostMapping("/save")
    public String save(@ModelAttribute LoadGroupBase loadGroup, FlashScope flash) {
        // TODO: Validate Load Group
        // TODO: If error, bindAndForward
        // TODO: else, save
        
        // TODO: Remove this log
        log.info("Load Group object to be saved");
        log.info("Id:" + loadGroup.getId());
        log.info("Name:" + loadGroup.getName());
        log.info("Type:" + loadGroup.getType());
        log.info("Disable Control:" + loadGroup.isDisableControl());
        log.info("Disable Group:" + loadGroup.isDisableGroup());
        flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "info.saved"));
        return "redirect:/dr/setup/loadGroup/" + 123;
    }

    private LoadGroupBase mockBaseLMLoadGroup() {
        LoadGroupBase loadGroup = new LoadGroupBase();
        loadGroup.setId(123);
        loadGroup.setName("Test Load Group");
        loadGroup.setkWCapacity(10.33f);
        loadGroup.setDisableControl(true);
        loadGroup.setDisableGroup(false);
        loadGroup.setType(PaoType.LM_GROUP_METER_DISCONNECT);
        return loadGroup;
    }

}
