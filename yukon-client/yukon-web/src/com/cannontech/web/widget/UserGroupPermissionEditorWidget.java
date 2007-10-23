package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.common.search.UltraLightPao;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.authorization.service.PaoPermissionEditorService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.web.util.TextView;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public abstract class UserGroupPermissionEditorWidget<T> extends WidgetControllerBase {

    private PaoDao paoDao = null;
    private PaoGroupsWrapper paoGroups = null;
    private PaoPermissionEditorService<T> editorService = null;

    public PaoDao getPaoDao() {
        return paoDao;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public PaoGroupsWrapper getPaoGroups() {
        return paoGroups;
    }

    public void setPaoGroups(PaoGroupsWrapper paoGroups) {
        this.paoGroups = paoGroups;
    }

    public PaoPermissionEditorService<T> getEditorService() {
        return editorService;
    }

    public void setEditorService(PaoPermissionEditorService<T> editorService) {
        this.editorService = editorService;
    }

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("userGroupPermissionEditor/render.jsp");

        T group = getAffected(request);
//        mav.addObject("group", group);

        putPaosInModel(request, mav, group);

        return mav;
    }
    
    protected abstract T getAffected(HttpServletRequest request) throws Exception;

    public ModelAndView removePao(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String paoIdList = WidgetParameterHelper.getRequiredStringParameter(request, "paoIdsList");
        Integer paoId = Integer.valueOf(WidgetParameterHelper.getRequiredIntParameter(request, "paoId"));

        List<Integer> idList = this.getPaoIdList(paoIdList);

        // Remove pao
        idList.remove(paoId);

        return this.getPaoTableMav(idList);

    }

    public ModelAndView addPao(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String paoIdList = WidgetParameterHelper.getRequiredStringParameter(request,"paoIdsList");
        Integer paoId = WidgetParameterHelper.getRequiredIntParameter(request, "newPaoId");

        List<Integer> idList = this.getPaoIdList(paoIdList);

        // Add pao
        if (!idList.contains(paoId)) {
            idList.add(paoId);
        }

        return this.getPaoTableMav(idList);

    }

    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String paoIdList = WidgetParameterHelper.getRequiredStringParameter(request, "paoIdsList");
        boolean allow = WidgetParameterHelper.getRequiredBooleanParameter(request, "allow");
        Permission permission = getPermission(request);

        T group = getAffected(request);

        if (editorService.savePermissions(group,
                                          this.getPaoIdList(paoIdList),
                                          permission, allow)) {
            return new ModelAndView(new TextView("Save Successful"));
        }

        return new ModelAndView(new TextView("Save Failed"));
    }

    protected void putPaosInModel(HttpServletRequest request, ModelAndView mav, T it) throws Exception {
        
        Permission permission = getPermission(request);
        
        List<LiteYukonPAObject> paoList = editorService.getPaos(it, permission);
        mav.addObject("paoList", getUltraLitePaoList(paoList));

        MappingList<LiteYukonPAObject, Integer> idList = new MappingList<LiteYukonPAObject, Integer>(paoList, new ObjectMapper<LiteYukonPAObject, Integer>() {
            public Integer map(LiteYukonPAObject from) {
                return from.getLiteID();
            }
        });
        String sb = StringUtils.join(idList, ",");
        mav.addObject("paoIds", sb);
    }

    private Permission getPermission(HttpServletRequest request) throws ServletRequestBindingException {
        String permissionStr = WidgetParameterHelper.getRequiredStringParameter(request, "permission");
        Permission permission = Permission.valueOf(permissionStr);
        return permission;
    }

    /**
     * Helper method to convert a string of paoIds into a list
     * @param paoIdList - String of comma separated paoIds
     * @return Integer list of paoIds
     */
    protected List<Integer> getPaoIdList(String paoIdList) {

        List<Integer> idList = new ArrayList<Integer>();

        if (paoIdList != null && paoIdList.length() > 0) {
            String[] ids = paoIdList.split(",");
            for (String id : ids) {
                idList.add(Integer.valueOf(id));
            }
        }
        return idList;

    }

    /**
     * Helper method to get and populate the paoTable MAV
     * @param idList - List of paoIds to add to the view
     * @return Populated paoTable MAV
     */
    private ModelAndView getPaoTableMav(List<Integer> idList) {
        ModelAndView mav = new ModelAndView("userGroupPermissionEditor/render.jsp");

        List<LiteYukonPAObject> paoList = new ArrayList<LiteYukonPAObject>();
        StringBuffer idBuffer = new StringBuffer();
        for (Integer id : idList) {
            paoList.add(paoDao.getLiteYukonPAO(id));

            if (idBuffer.length() > 0) {
                idBuffer.append(",");
            }
            idBuffer.append(id);
        }

        mav.addObject("paoList", getUltraLitePaoList(paoList));
        mav.addObject("paoIds", idBuffer.toString());
        mav.addObject("showSave", true);
        return mav;
    }

    protected List<UltraLightPao> getUltraLitePaoList(List<LiteYukonPAObject> paoList) {

        List<UltraLightPao> liteList = new ArrayList<UltraLightPao>();
        for (final LiteYukonPAObject pao : paoList) {
            liteList.add(new UltraLightPao() {

                public int getPaoId() {
                    return pao.getLiteID();
                }

                public String getPaoName() {
                    return pao.getPaoName();
                }

                public String getType() {
                    return paoGroups.getPAOTypeString(pao.getType());
                }
            });
        }

        return liteList;

    }

}
