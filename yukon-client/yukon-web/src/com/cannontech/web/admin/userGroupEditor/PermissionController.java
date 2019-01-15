package com.cannontech.web.admin.userGroupEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.authorization.service.PaoPermissionEditorService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

@Controller
@CheckRoleProperty({ YukonRoleProperty.ADMIN_SUPER_USER, YukonRoleProperty.ADMIN_LM_USER_ASSIGN })
public class PermissionController {
    
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private YukonUserDao userDao;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired @Qualifier("user") private PaoPermissionEditorService<LiteYukonUser> userPermissions;
    @Autowired @Qualifier("userGroup") private PaoPermissionEditorService<LiteUserGroup> groupPermissions;
    @Autowired private UsersEventLogService usersEventLogService;
    
    private static final Logger log = YukonLogManager.getLogger(PermissionController.class);
    
    private static final Comparator<LiteYukonPAObject> byName = new Comparator<LiteYukonPAObject>() {
        @Override
        public int compare(LiteYukonPAObject o1, LiteYukonPAObject o2) {
            return o1.getPaoName().compareTo(o2.getPaoName());
        }
    };
    
    /** USER PERMISSIONS */
    
    @RequestMapping("users/{userId}/permissions")
    public String user(ModelMap model, @PathVariable int userId) {
        
        LiteYukonUser user = userDao.getLiteYukonUser(userId);
        
        List<LiteYukonPAObject> lmVisible = userPermissions.getPaos(user, Permission.LM_VISIBLE);
        List<LiteYukonPAObject> paoVisible = userPermissions.getPaos(user, Permission.PAO_VISIBLE);
        Collections.sort(lmVisible, byName);
        Collections.sort(paoVisible, byName);
        model.addAttribute("lmVisible", lmVisible);
        model.addAttribute("paoVisible", paoVisible);
        
        model.addAttribute("lm", Permission.LM_VISIBLE);
        model.addAttribute("pao", Permission.PAO_VISIBLE);
        
        Map<String, Object> data = ImmutableMap.of(
                "type", "users",
                "id", user.getUserID(),
                "lm_exclude", Lists.transform(lmVisible, YukonPao.TO_PAO_ID),
                "vv_exclude", Lists.transform(paoVisible, YukonPao.TO_PAO_ID));
        model.addAttribute("data", data);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("page", "user");
        
        return "userGroupEditor/permissions.jsp";
    }
    
    @RequestMapping("users/{userId}/permissions/remove")
    public void removeUserPermission(HttpServletResponse resp, @PathVariable int userId, YukonUserContext userContext,
            int paoId, Permission permission) {
        
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
        LiteYukonUser me = userContext.getYukonUser();
        LiteYukonUser user = userDao.getLiteYukonUser(userId);
        String type = permission == Permission.PAO_VISIBLE ? "DENY" : "ALLOW";
        userPermissions.removePermission(user, pao, permission);
        usersEventLogService.permissionRemoved(type, user.getUsername(), pao.getPaoName(), permission, me);
        log.info(me.getUsername() + " removed permission " + permission + ": " + type
                + " for " + pao
                + " from " + user.getUsername());
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping(value="users/{userId}/permissions/add", method=RequestMethod.POST)
    public @ResponseBody List<Map<String, Object>> addUserPermission(
            YukonUserContext userContext,
            @PathVariable int userId,
            @RequestParam("paoIds[]") int[] paoIds,
            Permission permission) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        boolean allow = permission == Permission.LM_VISIBLE;
        List<Map<String, Object>> paos = new ArrayList<>();
        
        for (int paoId : paoIds) {
            Map<String, Object> pao = new HashMap<>();
            LiteYukonPAObject lyp = dbCache.getAllPaosMap().get(paoId);
            pao.put("id", paoId);
            pao.put("name", lyp.getPaoName());
            pao.put("type", accessor.getMessage(lyp.getPaoType()));
            paos.add(pao);
        }
        LiteYukonUser me = userContext.getYukonUser();
        LiteYukonUser user = userDao.getLiteYukonUser(userId);
        userPermissions.addPermissions(user, Ints.asList(paoIds), permission, allow);
        
        for (Map<String, Object> pao : paos) {
            log.info(me.getUsername() + " added permission " + permission + ": " + (allow ? "ALLOW" : "DENY")
                    + " for " + pao.get("name")
                    + " to " + user.getUsername());
            usersEventLogService.permissionAdded(allow ? "ALLOW" : "DENY", user.getUsername(), pao.get("name").toString(), permission, me);
        }
        
        return paos;
    }
    
    /** USER GROUP PERMISSIONS */
    
    @RequestMapping("user-groups/{groupId}/permissions")
    public String group(ModelMap model, @PathVariable int groupId) {
        
        LiteUserGroup group = userGroupDao.getLiteUserGroup(groupId);
        
        List<LiteYukonPAObject> lmVisible = groupPermissions.getPaos(group, Permission.LM_VISIBLE);
        List<LiteYukonPAObject> paoVisible = groupPermissions.getPaos(group, Permission.PAO_VISIBLE);
        Collections.sort(lmVisible, byName);
        Collections.sort(paoVisible, byName);
        model.addAttribute("lmVisible", lmVisible);
        model.addAttribute("paoVisible", paoVisible);
        
        model.addAttribute("lm", Permission.LM_VISIBLE);
        model.addAttribute("pao", Permission.PAO_VISIBLE);
        
        Map<String, Object> data = ImmutableMap.of(
                "type", "user-groups",
                "id", group.getUserGroupId(),
                "lm_exclude", Lists.transform(lmVisible, YukonPao.TO_PAO_ID),
                "vv_exclude", Lists.transform(paoVisible, YukonPao.TO_PAO_ID));
        model.addAttribute("data", data);
        model.addAttribute("userGroupName", group.getUserGroupName());
        model.addAttribute("userGroupId", group.getUserGroupId());
        model.addAttribute("page", "user.group");
        
        return "userGroupEditor/permissions.jsp";
    }
    
    @RequestMapping("user-groups/{groupId}/permissions/remove")
    public void removeGroupPermission(HttpServletResponse resp, @PathVariable int groupId, YukonUserContext userContext,
            int paoId, Permission permission) {
        
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
        LiteYukonUser me = userContext.getYukonUser();
        LiteUserGroup group = userGroupDao.getLiteUserGroup(groupId);
        String type = permission == Permission.PAO_VISIBLE ? "DENY" : "ALLOW";
        groupPermissions.removePermission(group, pao, permission);
        
        log.info(me.getUsername() + " removed permission " + permission + ": " + type
                + " for " + pao
                + " from " + group.getUserGroupName());
        usersEventLogService.permissionRemoved(me, type, group.getUserGroupName(), pao.getPaoName(), permission);
        
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @RequestMapping(value="user-groups/{groupId}/permissions/add", method=RequestMethod.POST)
    public @ResponseBody List<Map<String, Object>> addGroupPermission( 
            YukonUserContext userContext,
            @PathVariable int groupId,
            @RequestParam("paoIds[]") int[] paoIds,
            Permission permission) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        boolean allow = permission == Permission.LM_VISIBLE;
        List<Map<String, Object>> paos = new ArrayList<>();
        for (int paoId : paoIds) {
            Map<String, Object> pao = new HashMap<>();
            LiteYukonPAObject lyp = dbCache.getAllPaosMap().get(paoId);
            
            pao.put("id", paoId);
            pao.put("name", lyp.getPaoName());
            pao.put("type", accessor.getMessage(lyp.getPaoType()));
            paos.add(pao);
        }
        LiteYukonUser me = userContext.getYukonUser();
        LiteUserGroup group = userGroupDao.getLiteUserGroup(groupId);
        groupPermissions.addPermissions(group, Ints.asList(paoIds), permission, allow);
        
        for (Map<String, Object> pao : paos) {
            log.info(me.getUsername() + " added permission " + permission + ": " + (allow ? "ALLOW" : "DENY")
                    + " for " + pao.get("name")
                    + " to " + group.getUserGroupName());
            usersEventLogService.permissionAdded(me, allow ? "ALLOW" : "DENY", group.getUserGroupName(), pao.get("name").toString(), permission);
        }
        return paos;
    }
    
}