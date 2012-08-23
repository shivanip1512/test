package com.cannontech.web.editor.group;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.web.security.annotation.CheckRoleProperty;

//currently only used by LM group permission assignment
//additional possible uses should have associated role property requirements added when needed
@Controller
@RequestMapping("/group/*")
@CheckRoleProperty({YukonRoleProperty.ADMIN_LM_USER_ASSIGN})
public class GroupEditorController {

    @Autowired private UserGroupDao userGroupDao;

    @RequestMapping
    public String editGroup(ModelMap model, int userGroupId) throws ServletException {
        LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(userGroupId);
        
        model.addAttribute("userGroup", userGroup);
        model.addAttribute("userGroupId", userGroupId);
        model.addAttribute("userGroupName", userGroup.getUserGroupName());
        return "group/editGroup.jsp";
    }
}
