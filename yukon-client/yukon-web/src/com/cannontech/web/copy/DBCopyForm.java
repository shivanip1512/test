package com.cannontech.web.copy;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.web.editor.DBEditorForm;
import com.cannontech.web.exceptions.CBCExceptionMessages;
import com.cannontech.web.util.CBCCopyUtils;

public class DBCopyForm extends DBEditorForm {

	private static final String NULL_COPY_OBJECT = "Copy object was null";
	private static final String ERROR_COPY = "Could not copy object. ";
	
	private DBPersistent copyObject = null;
	private DBPersistent origObject = null;
	private boolean copyPoints = false;
	private String paoName = "";
	private boolean showCopyPoints = true;
	

	public DBCopyForm() {
		super();

	}

	public void init(int id) {
		origObject = CBCCopyUtils.getDBPersistentByID( id );
		if (CBCCopyUtils.isPoint(origObject)) {
			setPaoName(((PointBase) origObject).getPoint().getPointName());
			showCopyPoints = false;
		}
		else 
			setPaoName(((YukonPAObject) origObject).getPAOName());
			
	}

	protected void checkForErrors() throws Exception {

	}

	public void copyDBObject() {
		FacesMessage message = new FacesMessage();
		message.setDetail(CBCExceptionMessages.DB_UPDATE_SUCCESS);

		if (origObject != null) {
			try {
				copyObject = CBCCopyUtils.copy(origObject);
				if (CBCCopyUtils.isPoint(copyObject))
				{
					((PointBase)copyObject).getPoint().setPointName( getPaoName() );
				}
				else 
				{
					((YukonPAObject)copyObject).setPAOName( getPaoName() );
				}
				addDBObject(copyObject, message);
				
			
			} catch (IllegalArgumentException e) {
				message.setDetail(ERROR_COPY + e.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			} catch (TransactionException e) {
				message.setDetail(ERROR_COPY + e.getMessage());
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
			} finally {
				FacesContext.getCurrentInstance().addMessage("copy_object",
						message);
			}
			if (message.getSeverity() != FacesMessage.SEVERITY_ERROR) {
				if (copyObject instanceof CapBankController) {
					if (copyPoints)
						CBCCopyUtils.copyAllPointsForPAO(
								((CapBankController) origObject).getPAObjectID(),
								((CapBankController) copyObject).getPAObjectID());
						routeToEditor(((CapBankController) copyObject)
							.getPAObjectID().intValue());
				}
				if (copyObject instanceof CapBankController702x) {
					if (copyPoints)
						CBCCopyUtils.copyAllPointsForPAO(
								((CapBankController702x) origObject)
										.getPAObjectID(),
								((CapBankController702x) copyObject)
										.getPAObjectID());
					routeToEditor(((CapBankController702x) copyObject)
							.getPAObjectID().intValue());
				}
				if (copyObject instanceof PointBase) {
					routeToEditor(((PointBase) copyObject).getPoint()
							.getPointID().intValue());
				}
			}
		}
		else { 
			if (origObject != null) {
				if (origObject instanceof YukonPAObject)
					routeToEditor(((YukonPAObject) origObject).getPAObjectID()
						.intValue());
				else
					routeToEditor(((PointBase) origObject).getPoint().getPointID()
						.intValue());
				}
			else {
					message.setDetail(NULL_COPY_OBJECT);
					FacesContext.getCurrentInstance()
							.addMessage("copy_object", message);
					return;
				}	
		}	
	}



	private void routeToEditor(int copyPaobjectID) {
		FacesMessage facesMessage = new FacesMessage();
		
		if (copyObject != null) 
		{
			if (CBCCopyUtils.isPoint(copyObject) ) {
		            routeToPointEditor(copyPaobjectID);
				}
			else {
	            routeToCBCEditor(copyPaobjectID);			
			}
		}
		else if (origObject != null) {
			if (CBCCopyUtils.isPoint(origObject) ) {
				routeToPointEditor(copyPaobjectID);
			}
			else {
				routeToCBCEditor(copyPaobjectID);
			}
		}
		else {
			FacesMessage message = facesMessage;
			message.setDetail("Could not route - object is null");
			FacesContext.getCurrentInstance()
					.addMessage("copy_object", message);
			return;
		}		
	}

	/**
	 * @param copyPaobjectID
	 */
	private void routeToCBCEditor(int copyPaobjectID) {
		String red = "cbcBase.jsf?type=2&itemid=" + copyPaobjectID;
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(red);
		    FacesContext.getCurrentInstance().responseComplete();
		} 
		catch (Exception e) {
			CTILogger.error("DBCopyForm.routeToEditor. ERROR - " + e.getMessage());     	
		}
	}

	/**
	 * @param copyPaobjectID
	 */
	private void routeToPointEditor(int copyPaobjectID) {
		String red = "pointBase.jsf?itemid=" + copyPaobjectID;
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(red);
			FacesContext.getCurrentInstance().responseComplete();
		} 
		catch (IOException e) {
			CTILogger.error("DBCopyForm.routeToEditor. ERROR - " + e.getMessage());
		}
	}

	public String getPaoName() {
		return paoName;
	}

	public void setPaoName(String paoName) {
		this.paoName = paoName;
	}

	public boolean isCopyPoints() {
		return copyPoints;
	}

	public void setCopyPoints(boolean copyPoints) {
		this.copyPoints = copyPoints;
	}

	public boolean isShowCopyPoints() {
		return showCopyPoints;
	}

}
