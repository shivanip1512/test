package com.cannontech.web.admin.userGroupEditor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class NewRoleGroupController {
    
    @Autowired private YukonGroupDao roleGroupDao;
    @Autowired private RoleGroupValidator validator;
    @Autowired private UsersEventLogService usersEventLogService;
    
    @RequestMapping("new-role-group-dialog")
    public String newGroup(ModelMap model) {
        
        LiteYukonGroup group = new LiteYukonGroup();
        model.addAttribute("group", group);
        
        return "userGroupEditor/new-role-group.jsp";
    }
    
    @RequestMapping(value="role-groups", method=RequestMethod.POST)
    public String create(HttpServletResponse resp, 
            @ModelAttribute("group") LiteYukonGroup group, BindingResult binding, LiteYukonUser user) throws Exception {

        validator.validate(group, binding);
        
        if (binding.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return "userGroupEditor/new-role-group.jsp";
        }
        
        roleGroupDao.save(group);
        usersEventLogService.roleGroupCreated(group.getGroupName(), user);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("groupId", group.getGroupID());
        
        resp.setContentType("application/json");
        JsonUtils.getWriter().writeValue(resp.getOutputStream(), result);
        
        return null;
    }
    
}