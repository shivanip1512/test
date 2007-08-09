package com.cannontech.web.editor.model;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;


import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.util.JSFUtil;

public class EditorDataModelImpl implements EditorDataModel {
  
    private DBPersistent dbObj = null;

    private final FacesMessage fm = new FacesMessage();
    
    public EditorDataModelImpl(DBPersistent dbPersistent) {
        super();
        dbObj = dbPersistent;

    }

    /**
     * method will redirect to the util function that takes in the id of the point parent 
     * and ptID parameter from the Faces request
     * @param ae
     */
    public  void goToPointEditor (ActionEvent ae) {
        JSFParamUtil.goToPointEditor(((YukonPAObject)dbObj).getPAObjectID(), fm);
    }
    
    public List <LitePoint> getPaoPoints () {
        return DaoFactory.getCBCDao().getPaoPoints(((YukonPAObject)dbObj) );
    }


    public void updateDataModel() {
        
    }
    
    public void createWizardLink () {
        String id = JSFParamUtil.getJSFReqParam("type");
        JSFUtil.redirect("/editor/cbcWizBase.jsf?type=" + id);
    }

    public void createEditorLink () {
        // save current page for return
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        CBCNavigationUtil.bookmarkThisLocation(session);
        String type = JSFParamUtil.getJSFReqParam("strattype");
        String itemid = JSFParamUtil.getJSFReqParam("stratitemid");
        JSFUtil.redirect("/editor/cbcBase.jsf?type=" + type + "&itemid=" + itemid);
    }
    
}
