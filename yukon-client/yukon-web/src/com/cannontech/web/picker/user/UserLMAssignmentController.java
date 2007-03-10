package com.cannontech.web.picker.user;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.PaoTypeSearcher;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.search.criteria.LMDeviceCriteria;
import com.cannontech.core.authorization.model.UserGroupPermissionList;
import com.cannontech.core.authorization.model.UserPaoPermission;
import com.cannontech.core.authorization.service.PaoPermissionService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.picker.YukonObjectPickerController;

public class UserLMAssignmentController extends YukonObjectPickerController {
    private YukonUserDao yukonUserDao;
    private YukonGroupDao yukonGroupDao;
    private PaoPermissionService paoPermissionService;
    private PaoTypeSearcher paoTypeSearcher;
    
    public UserLMAssignmentController() {
        super();
    }
    
    public ModelAndView refreshUser(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        JSONObject jsonUser = new JSONObject(RequestUtils.getStringParameter(request, "selectedItem", ""));
        LiteYukonUser selectedUser = yukonUserDao.getLiteYukonUser(jsonUser.getInt("userId"));
        //List<UserPaoPermission> permList = paoPermissionService.getUserPermissions(selectedUser).getUserPermissionList();
        List<UltraLightPao> paos = new ArrayList<UltraLightPao>();
        /*for(int j = 0; j < permList.size(); j++) {
            if(permList.get(j).getPermission() == Permission.LM_VISIBLE) {
                SearchResult<UltraLightPao> searchReturn = paoTypeSearcher.search(String.valueOf(permList.get(j).getPaoId()), new LMDeviceCriteria());
                
            }
        }*/
        UltraLightPao ultra = new UltraLightPao() {
            public String getPaoName() {
                return "Minnesota";
            }
            public String getType() {
                return "LM CONTROL AREA";
            }
            public int getPaoId() {
                return 1;
            }
        };
        UltraLightPao ultra2 = new UltraLightPao() {
            public String getPaoName() {
                return "Colorado";
            }
            public String getType() {
                return "LM CONTROL AREA";
            }
            public int getPaoId() {
                return 2;
            }
        };
        paos.add(ultra);
        paos.add(ultra2);
        mav.addObject("assignedLMPaos", paos);
        
        return mav;
    }
    
    public ModelAndView removePao(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("json");
        String removedPaoId = RequestUtils.getStringParameter(request, "removedId", "");
        JSONArray assignedArray = new JSONArray(RequestUtils.getStringParameter(request, "assigned", ""));
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
}
