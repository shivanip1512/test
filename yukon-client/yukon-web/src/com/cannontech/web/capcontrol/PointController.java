package com.cannontech.web.capcontrol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.capcontrol.models.PointModel;
import com.cannontech.web.capcontrol.service.PointEditorService;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.CAP_CONTROL_ACCESS)
public class PointController {
    
    
    @Autowired PointEditorService pointEditorService;
    @Autowired IDatabaseCache dbCache;
    
    
    @RequestMapping(value="points/{id}", method=RequestMethod.GET)
    public String view(ModelMap model, @PathVariable int id) {
        
        model.addAttribute("mode", PageEditMode.VIEW);
        PointModel pointModel = pointEditorService.getModelForId(id);
        
        return setUpModel(model, pointModel);
    }
    
    @RequestMapping(value="points/{id}/edit", method=RequestMethod.GET)
    public String edit(ModelMap model, @PathVariable int id) {
        
        model.addAttribute("mode", PageEditMode.EDIT);
        PointModel pointModel = pointEditorService.getModelForId(id);
        
        return setUpModel(model, pointModel);
    }
    
    private String setUpModel(ModelMap model, PointModel pointModel) {
        
        Object modelPointBase= model.get("pointBase");
        if (modelPointBase instanceof PointModel) {
            pointModel = (PointModel) modelPointBase;
        }
        
        model.addAttribute("pointModel", pointModel);
        
        LiteYukonPAObject parent = dbCache.getAllPaosMap().get(pointModel.getPointBase().getPoint().getPaoID());
        
        model.addAttribute("fdrInterfaceTypes", FdrInterfaceType.values());
        model.addAttribute("fdrDirections", FdrDirection.values());
        model.addAttribute("statusControlTypes", StatusControlType.values());
        
        model.addAttribute("parent", parent);
        
        return "point.jsp";
    }
    
    @RequestMapping(value={"points"}, method=RequestMethod.POST)
    @CheckRoleProperty(YukonRoleProperty.CBC_DATABASE_EDIT)
    public String save(
            @ModelAttribute("pointModel") PointModel pointModel,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return bindAndForward(pointModel, result, redirectAttributes);
        }
        
        int id;
        try {
            id = pointEditorService.save(pointModel);
        } catch (Exception e) {
            //Something happened to make the config invalid since validation.
            result.rejectValue("name", "Something Broke");
            
            return bindAndForward(pointModel, result, redirectAttributes);
        }
        
        return "redirect:points/" + id;
    }
    
    private String bindAndForward(PointModel pointModel, BindingResult result, RedirectAttributes attrs) {
        
        attrs.addFlashAttribute("pointModel", pointModel);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.pointModel", result);
        
        if (pointModel.getId() == null) {
            return "redirect:points/create";
        }
        
        return "redirect:points/" + pointModel.getId() + "/edit";
    }
}
