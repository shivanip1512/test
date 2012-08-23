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
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.TextView;
import com.cannontech.web.widget.support.WidgetControllerBase;
import com.cannontech.web.widget.support.WidgetParameterHelper;

public abstract class UserGroupPermissionEditorWidget<T> extends WidgetControllerBase {

    private PaoDao paoDao = null;
    private PaoPermissionEditorService<T> editorService = null;

    public PaoDao getPaoDao() {
        return paoDao;
    }
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public PaoPermissionEditorService<T> getEditorService() {
        return editorService;
    }
    public void setEditorService(PaoPermissionEditorService<T> editorService) {
        this.editorService = editorService;
    }
    
    @Override
    public ModelAndView identity(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView mav = super.identity(request, response);
    	
    	Permission permission = getPermission(request);
    	
    	mav.addObject("permission", permission);
    	mav.addObject("permissionDescription", permission);
    	return mav;
    }

    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView mav = new ModelAndView("userGroupPermissionEditor/render.jsp");

        T group = getAffected(request);
//        mav.addObject("group", group);

        putPaosInModel(request, mav, group);
        addPermissionDefaultObject(request, mav);
        return mav;
    }
    
    protected abstract T getAffected(HttpServletRequest request) throws Exception;

    public ModelAndView removePao(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String paoIdList = WidgetParameterHelper.getRequiredStringParameter(request, "paoIdsList");
        Integer paoId = Integer.valueOf(WidgetParameterHelper.getRequiredIntParameter(request, "paoId"));

        List<Integer> idList = ServletUtil.getIntegerListFromString(paoIdList);

        // Remove pao
        idList.remove(paoId);

        return this.getPaoTableMav(request, idList);

    }

    public ModelAndView addPao(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String paoIdList = WidgetParameterHelper.getRequiredStringParameter(request,"paoIdsList");
        List<Integer> idList = ServletUtil.getIntegerListFromString(paoIdList);

        String newPaoIdStr = WidgetParameterHelper.getRequiredStringParameter(request, "newPaoId");
        List<Integer> newPaoIds = ServletUtil.getIntegerListFromString(newPaoIdStr);

        for (Integer paoId : newPaoIds) {
            if (!idList.contains(paoId)) {
                idList.add(paoId);
            }
        }

        return this.getPaoTableMav(request, idList);
    }

    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String paoIdList = WidgetParameterHelper.getRequiredStringParameter(request, "paoIdsList");
        boolean allow = WidgetParameterHelper.getRequiredBooleanParameter(request, "allow");
        Permission permission = getPermission(request);

        T group = getAffected(request);

        if (editorService.savePermissions(group,
        								  ServletUtil.getIntegerListFromString(paoIdList),
                                          permission, allow)) {
            return new ModelAndView(new TextView("Save Successful"));
        }
        ModelAndView mav = new ModelAndView(new TextView("Save Failed"));
        addPermissionDefaultObject(request, mav);
        return mav;
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

    /**
     * Add text for permission.default to mav
     * @param request
     * @param mav
     * @throws ServletRequestBindingException
     */
	private void addPermissionDefaultObject(HttpServletRequest request,
			ModelAndView mav) throws ServletRequestBindingException {
		Permission permission = getPermission(request);
        String instructionText = "should";
        if(permission.getDefault()) {
        	instructionText = "should not";
        }
        
        mav.addObject("instructionText", instructionText);
	}
	
    private Permission getPermission(HttpServletRequest request) throws ServletRequestBindingException {
        String permissionStr = WidgetParameterHelper.getRequiredStringParameter(request, "permission");
        Permission permission = Permission.valueOf(permissionStr);
        return permission;
    }

    /**
     * Helper method to get and populate the paoTable MAV
     * @param idList - List of paoIds to add to the view
     * @return Populated paoTable MAV
     */
    private ModelAndView getPaoTableMav(HttpServletRequest request, List<Integer> idList) throws ServletRequestBindingException {
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
        addPermissionDefaultObject(request, mav);
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
                    return pao.getPaoType().getDbString();
                }
            });
        }

        return liteList;
    }
}