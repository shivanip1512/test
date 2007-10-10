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
    protected abstract String getCbcPickerId();

    protected abstract T getAffected(HttpServletRequest request);

    public ModelAndView removePao(HttpServletRequest request, HttpServletResponse response) {
    	Integer paoId;
    	List<Integer> idList;
    	List<Integer> cbcIdList;
    	
        String paoIdList = request.getParameter("paoIdList");
        String paoIdString = request.getParameter("paoId");
        String cbcPaoIdList = request.getParameter("cbcPaoIdList"); 
        
        idList = this.getPaoIdList(paoIdList);
        cbcIdList = this.getPaoIdList(cbcPaoIdList);
        
        if( paoIdString.compareTo("") != 0 )
        {
        	paoId = Integer.valueOf(paoIdString);
        	if(!idList.remove(paoId))
        		cbcIdList.remove(paoId);
        }


    	return this.getPaoTableMav(idList, cbcIdList );
   }

    public ModelAndView addPao(HttpServletRequest request, HttpServletResponse response) {
    	Integer paoId;
    	List<Integer> idList;
    	List<Integer> cbcIdList;
    	
        String paoIdList = request.getParameter("paoIdList");
        String paoIdString = request.getParameter("paoId");
        String cbcPaoIdList = request.getParameter("cbcPaoIdList");
        String cbcPaoIdString = request.getParameter("cbcPaoId");
            
        idList = this.getPaoIdList(paoIdList);
        cbcIdList = this.getPaoIdList(cbcPaoIdList);
        
        if( paoIdString.compareTo("") != 0 )
        {
        	paoId = Integer.valueOf(paoIdString);
            if (!idList.contains(paoId)) {
                idList.add(paoId);
            }        	
        }
        else
        {
        	paoId = Integer.valueOf(cbcPaoIdString);
            if (!cbcIdList.contains(paoId)) {
            	cbcIdList.add(paoId);
            }
        }

    	return this.getPaoTableMav(idList, cbcIdList );


    }

    public ModelAndView save(HttpServletRequest request, HttpServletResponse response) {

        String paoIdList = request.getParameter("paoIdList");
        String cbcPaoIdList = request.getParameter("cbcPaoIdList");
        T group = getAffected(request);
        boolean lm = editorService.savePermissions(group, this.getPaoIdList(paoIdList),Permission.LM_VISIBLE);
        boolean cbc = editorService.addPermissions(group, this.getPaoIdList(cbcPaoIdList),Permission.PAO_VISIBLE);
        if ( lm && cbc) {
            return new ModelAndView(new TextView("Save Successful"));
        }

        return new ModelAndView(new TextView("Save Failed"));
    }

    protected void putPaosInModel(ModelAndView mav, T it) {
        List<LiteYukonPAObject> paoList = editorService.getPaos(it, Permission.LM_VISIBLE);
        List<LiteYukonPAObject> cbcPaoList = editorService.getPaos(it, Permission.PAO_VISIBLE);
        mav.addObject("paoList", getUltraLitePaoList(paoList));
        mav.addObject("cbcPaoList", getUltraLitePaoList(cbcPaoList));
        
        StringBuffer sb = new StringBuffer();
        for (LiteYukonPAObject pao : paoList) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(pao.getYukonID());
        }
        mav.addObject("paoIds", sb.toString());
        sb = new StringBuffer();
        for (LiteYukonPAObject pao : cbcPaoList) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(pao.getYukonID());
        }
        mav.addObject("cbcPaoIds", sb.toString());
        mav.addObject("pickerId", getPickerId());
        mav.addObject("cbcPickerId",getCbcPickerId());
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
     * @param cbcIdList - List of paoIds to add to the view
     * @return Populated paoTable MAV
     */
    protected ModelAndView getPaoTableMav(List<Integer> idList,List<Integer> cbcIdList ) {
        ModelAndView mav = new ModelAndView("paoTable.jsp");

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

        paoList = new ArrayList<LiteYukonPAObject>();
        idBuffer = new StringBuffer();
        for (Integer id : cbcIdList) {
            paoList.add(paoDao.getLiteYukonPAO(id));

            if (idBuffer.length() > 0) {
                idBuffer.append(",");
            }
            idBuffer.append(id);
        }        
        
        mav.addObject("cbcPickerId", getCbcPickerId());
        mav.addObject("cbcPaoList", getUltraLitePaoList(paoList));
        mav.addObject("cbcPaoIds", idBuffer.toString());        
        
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