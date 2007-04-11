package com.cannontech.web.picker.user;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.PaoTypeSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.criteria.LMDeviceCriteria;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.model.UserPaoPermission;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.picker.YukonObjectPickerController;

public class UserLMAssignmentController extends YukonObjectPickerController {
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private PaoDao paoDao;
    private PaoGroupsWrapper paoGroupsWrapper;
    private PaoPermissionService paoPermissionService;
    private PaoTypeSearcher paoTypeSearcher;
    
    public UserLMAssignmentController() {
        super();
    }
    
    public ModelAndView refreshUser(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        JSONObject jsonUser = new JSONObject(RequestUtils.getStringParameter(request, "selectedItem", ""));
        LiteYukonUser selectedUser = yukonUserDao.getLiteYukonUser(jsonUser.getInt("userId"));
        List<UserPaoPermission> permList = paoPermissionService.getUserPermissions(selectedUser).getUserPermissionList();
        List<UltraLightPao> paos = new ArrayList<UltraLightPao>();
        for(UserPaoPermission perm : permList) {
            if(perm.getPermission().compareTo(Permission.LM_VISIBLE) == 0) {
                final int paoId = perm.getPaoId();
                LiteYukonPAObject pao = paoDao.getLiteYukonPAO(paoId);
                PaoGroupsWrapper paoGroupsWrapper = (PaoGroupsWrapper) YukonSpringHook.getBean("paoGroupsWrapper");
                final String type = paoGroupsWrapper.getPAOTypeString(pao.getType());
                final String name = pao.getPaoName();
                UltraLightPao ultraLightPao = new UltraLightPao() {
                    public String getType() {
                        return type;
                    }
                    public int getPaoId() {
                        return paoId;
                    }
                    public String getPaoName() {
                        return name;
                    }
                };
                paos.add(ultraLightPao);
            }
        }
        
        mav.addObject("assignedLMPaos", paos);
        
        return mav;
    }
    
    public ModelAndView removePao(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        JSONObject currentJson = new JSONObject(ServletRequestUtils.getStringParameter(request, "currentJson", ""));
        JSONArray assignedArray = currentJson.getJSONArray("assigned");
        String removedPaoId = currentJson.getString("removeId");
        List<UltraLightPao> paos = new ArrayList<UltraLightPao>();
        for(int j = 0; j < assignedArray.length(); j++) {
            JSONObject jsonPao = assignedArray.getJSONObject(j);
            //don't forget that JSONArray counts nulls when it returns the length of itself
            if(jsonPao != null) {
                final int paoId = jsonPao.getInt("paoId");
                if(paoId != Integer.parseInt(removedPaoId)) {
                    final String type = jsonPao.getString("type");
                    final String name = jsonPao.getString("paoName");
                    UltraLightPao ultraLightPao = new UltraLightPao() {
                        public String getType() {
                            return type;
                        }
                        public int getPaoId() {
                            return paoId;
                        }
                        public String getPaoName() {
                            return name;
                        }
                    };
                    paos.add(ultraLightPao);
                }
            }
        }
        
        mav.addObject("assignedLMPaos", paos);
        return mav;
    }
    
    public ModelAndView addPaoToUser(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        JSONObject jsonPao = new JSONObject(RequestUtils.getStringParameter(request, "selectedItem", ""));
        final int paoId = jsonPao.getInt("paoId");
        final String type = jsonPao.getString("type");
        final String name = jsonPao.getString("paoName");
        UltraLightPao ultraLightPao = new UltraLightPao() {
            public String getType() {
                return type;
            }
            public int getPaoId() {
                return paoId;
            }
            public String getPaoName() {
                return name;
            }
        };
        mav.addObject("addedPao", ultraLightPao);
        return mav;
    }
    
    public ModelAndView submitUserChanges(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        JSONObject currentJson = new JSONObject(ServletRequestUtils.getStringParameter(request, "currentJson", ""));
        JSONArray assignedArray = currentJson.getJSONArray("assigned");
        String userId = currentJson.getString("userId");
        List<UltraLightPao> paos = new ArrayList<UltraLightPao>();
        LiteYukonUser user = new LiteYukonUser(Integer.parseInt(userId));
        Set<Integer> existingLMPermissions = paoPermissionService.getPaoIdsForUserPermission(user, Permission.LM_VISIBLE);
        List<Integer> processedPaoIds = new ArrayList<Integer>();
        for(int j = 0; j < assignedArray.length(); j++) {
            JSONObject jsonPao = assignedArray.getJSONObject(j);
            //don't forget that JSONArray counts nulls when it returns the length of itself
            if(jsonPao != null) {
                final int paoId = jsonPao.getInt("paoId");
                LiteYukonPAObject pao = new LiteYukonPAObject(paoId);
                if(! existingLMPermissions.contains(new Integer(paoId)) && ! processedPaoIds.contains(new Integer(paoId))) {
                    /*Save the new permission*/
                    paoPermissionService.addPermission(user, pao, Permission.LM_VISIBLE);
                }
                final String type = jsonPao.getString("type");
                final String name = jsonPao.getString("paoName");
                UltraLightPao ultraLightPao = new UltraLightPao() {
                    public String getType() {
                        return type;
                    }
                    public int getPaoId() {
                        return paoId;
                    }
                    public String getPaoName() {
                        return name;
                    }
                };
                paos.add(ultraLightPao);
                processedPaoIds.add(paoId);
            }
        }
        
        Object[] previous = existingLMPermissions.toArray();
        for(int i = 0; i < previous.length; i++) {
            if(! processedPaoIds.contains(previous[i])) {
                LiteYukonPAObject pao = new LiteYukonPAObject(((Integer)previous[i]).intValue());
                paoPermissionService.removePermission(user, pao, Permission.LM_VISIBLE);
            }
        }
        
        mav.addObject("assignedLMPaos", paos);
        mav.addObject("statusSuccess", "Permission changes have been saved.");
        return mav;
    }
    
    public ModelAndView refreshGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ModelAndView mav = new ModelAndView("json");
        mav.addObject("showAll", false);
        
        return mav;
    }

    public PaoPermissionService getPaoPermissionService() {
        return paoPermissionService;
    }

    public void setPaoPermissionService(PaoPermissionService paoPermissionService) {
        this.paoPermissionService = paoPermissionService;
    }

    public YukonGroupDao getYukonGroupDao() {
        return yukonGroupDao;
    }

    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }

    public YukonUserDao getYukonUserDao() {
        return yukonUserDao;
    }

    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }

    public PaoTypeSearcher getPaoTypeSearcher() {
        return paoTypeSearcher;
    }

    public void setPaoTypeSearcher(PaoTypeSearcher paoTypeSearcher) {
        this.paoTypeSearcher = paoTypeSearcher;
    }

    public PaoDao getPaoDao() {
        return paoDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public PaoGroupsWrapper getPaoGroupsWrapper() {
        return paoGroupsWrapper;
    }

    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
}
