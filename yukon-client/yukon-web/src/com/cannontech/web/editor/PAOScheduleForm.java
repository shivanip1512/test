package com.cannontech.web.editor;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;


import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.db.pao.PAOSchedule;
import com.cannontech.servlet.nav.DBEditorNav;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.util.JSFParamUtil;

/**
 * Offers methods to acces/use PAOSchedules
 * @author ryan
 */
public class PAOScheduleForm extends DBEditorForm {

	private SelectItem[] selectScheds = null;
	private Integer currentSchedID = null;
	
	/**
	 * 
	 */
	public PAOScheduleForm() {
		super();
	}

	public PAOSchedule[] getPAOSchedules() {
		
		return PAOSchedule.getAllPAOSchedules();
	}

	/**
	 * Edit a schedule with the given id
	 */
	public void edit( ActionEvent ev ) {

		FacesContext context = FacesContext.getCurrentInstance();
		Map paramMap = context.getExternalContext().getRequestParameterMap();
		int elemID = Integer.parseInt( (String)paramMap.get("schedID") );
		//String elemID = (String)paramMap.get("schedID");
		
        CapControlForm f = new CapControlForm();
		f.initItem( elemID, DBEditorNav.EDITOR_SCHEDULE );
		
		context.getExternalContext().getSessionMap().put("capControlForm", f );		
	}

	/**
	 * Go to the editor page after we have processed the edit event
	 */
	public String goto_edit() {
		
		//map our return variable page to this list
		CtiNavObject nav = (CtiNavObject)FacesContext.getCurrentInstance().getApplication().getVariableResolver()
			.resolveVariable(FacesContext.getCurrentInstance(), "CtiNavObject");
		
		nav.setModuleRedirectPage(
			DBEditorNav.getEditorURL(DBEditorNav.EDTYPE_LIST_SCHEDULE) ); //"paoSchedList");

		return "cbcEditor";
	}

	/**
	 * Edit a schedule with the given id
	 * -- TODO: this is a workaround for a bug in MyFaces 1.1.0,
	 * 		fix this by using the <f:param> or <f:attribute> element calld id
	 * 		when the bug is fixed
	 */
	public String delete( ActionEvent ev ) {

		int elemID = Integer.parseInt(
			ev.getComponent().getAttributes().get("title").toString().trim() );


		//this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();

		try {
			super.deleteDBObject(
					new PAOSchedule( new Integer(elemID) ),
					facesMsg );
		}
		catch( Exception e ) {
			CTILogger.error( e.getMessage(), e );
			facesMsg.setDetail( "Unable to delete Schedule from the database: " + e.getMessage() );
			facesMsg.setSeverity( FacesMessage.SEVERITY_ERROR );
		}
		return "";
	}

	/**
	 * Hold all the PAOSchedules in memory for quicker access.
	 */
	public SelectItem[] getPAOSchedulesSelItems() {
		
		PAOSchedule[] scheds = getPAOSchedules();
		selectScheds = new SelectItem[scheds.length];
			
		for( int i = 0; i < scheds.length; i++ ) {
			selectScheds[i] = new SelectItem( //value, label
				scheds[i].getScheduleID(), //.toString(),
				scheds[i].getScheduleName() );
		}

		return selectScheds;
	}

	/**
	 * @return
	 */
	public Integer getCurrentSchedID() {
		return currentSchedID;
	}

	/**
	 * @param i
	 */
	public void setCurrentSchedID(Integer i) {
		currentSchedID = i;
	}
	
	/**
     * Returns true if the user has the CapControl Settings > Database Editing role property.
     * @return
     */
    public boolean isEditingAuthorized() {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        return rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, JSFParamUtil.getYukonUser());
    }

    protected void checkForErrors() throws Exception {
        // TODO Auto-generated method stub
        
    }

    public void resetForm() {
        
    }
}