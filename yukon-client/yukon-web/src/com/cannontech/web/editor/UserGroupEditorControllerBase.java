package com.cannontech.web.editor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.search.UltraLightPao;
import com.cannontech.core.authorization.service.PaoPermissionEditorService;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.web.util.TextView;

public abstract class UserGroupEditorControllerBase<T> extends MultiActionController {

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

    public UserGroupEditorControllerBase() {
        super();
    }

    protected abstract String getPickerId();

    protected abstract T getAffected(HttpServletRequest request);

    public ModelAndView removePao(HttpServletRequest request, HttpServletResponse response) {

        String paoIdList = request.getParameter("paoIdList");
        Integer paoId = Integer.valueOf(request.getParameter("paoId"));

        List<Integer> idList = this.getPaoIdList(paoIdList);

        // Remove pao
        idList.remove(paoId);

        return this.getPaoTableMav(idList);

    }

    public ModelAndView addPao(HttpServletRequest request, HttpServletResponse response) {

        String paoIdList = request.getParameter("paoIdList");
        Integer paoId = Integer.valueOf(request.getParameter("paoId"));

        List<Integer> idList = this.getPaoIdList(paoIdList);

        // Add pao
        if (!idList.contains(paoId)) {
            idList.add(paoId);
        }

        return this.getPaoTableMav(idList);

    }

    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {

        String paoIdList = request.getParameter("paoIdList");

        T group = getAffected(request);

        if (editorService.savePermissions(group,
                                          this.getPaoIdList(paoIdList),
                                          Permission.LM_VISIBLE)) {
            return new ModelAndView(new TextView("Save Successful"));
        }

        return new ModelAndView(new TextView("Save Failed"));
    }

    protected void putPaosInModel(ModelAndView mav, T it) {
        List<LiteYukonPAObject> paoList = editorService.getPaos(it, Permission.LM_VISIBLE);
        mav.addObject("paoList", getUltraLitePaoList(paoList));

        StringBuffer sb = new StringBuffer();
        for (LiteYukonPAObject pao : paoList) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(pao.getYukonID());
        }
        mav.addObject("paoIds", sb.toString());
        mav.addObject("pickerId", getPickerId());
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
    protected ModelAndView getPaoTableMav(List<Integer> idList) {
        ModelAndView mav = new ModelAndView("/paoTable");

        List<LiteYukonPAObject> paoList = new ArrayList<LiteYukonPAObject>();
        StringBuffer idBuffer = new StringBuffer();
        for (Integer id : idList) {
            paoList.add(paoDao.getLiteYukonPAO(id));

            if (idBuffer.length() > 0) {
                idBuffer.append(",");
            }
            idBuffer.append(id);
        }

        mav.addObject("pickerId", getPickerId());
        mav.addObject("paoList", getUltraLitePaoList(paoList));
        mav.addObject("paoIds", idBuffer.toString());

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