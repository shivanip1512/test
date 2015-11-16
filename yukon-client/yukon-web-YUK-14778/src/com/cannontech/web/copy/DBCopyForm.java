package com.cannontech.web.copy;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.cbc.exceptions.CBCExceptionMessages;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.editor.DBEditorForm;
import com.cannontech.web.util.CBCCopyUtils;

public class DBCopyForm extends DBEditorForm {

    private static final String NULL_COPY_OBJECT = "Copy object was null";
    private static final String ERROR_COPY = "Could not copy object. ";

    private DBPersistent copyObject = null;
    private DBPersistent origObject = null;
    private boolean copyPoints = false;
    private String paoName = "";
    private String originalPaoName = "";
    private boolean showCopyPoints = true;

    public DBCopyForm() {
        super();
    }

    public void init(int id, int type) {
        origObject = CBCCopyUtils.getDBPersistentByID(id, type);
        if (CBCCopyUtils.isPoint(origObject)) {
            setPaoName(((PointBase) origObject).getPoint().getPointName());
            setOriginalPaoName(((PointBase) origObject).getPoint().getPointName());
            showCopyPoints = false;
        } else
            setPaoName(((YukonPAObject) origObject).getPAOName());
            setOriginalPaoName(((YukonPAObject) origObject).getPAOName());
    }

    @Override
    protected void checkForErrors() throws Exception {

    }

    public void copyDBObject() {
        FacesMessage message = new FacesMessage();
        message.setDetail(CBCExceptionMessages.DB_UPDATE_SUCCESS);

        if (origObject != null) {

            try {
                if (StringUtils.isBlank(paoName)) {
                    message.setDetail(ERROR_COPY + "A name must be specified for this object.");
                    message.setSeverity(FacesMessage.SEVERITY_ERROR);
                } else {
                    copyObject = CBCCopyUtils.copy(origObject);
                    if (origObject instanceof YukonPAObject) {
                        YukonPAObject paObject = (YukonPAObject)origObject;
                        PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
                        if (paoDao.isNameAvailable(paoName, paObject.getPaoType())) {
                            ((YukonPAObject) copyObject).setPAOName(paoName);
                            addDBObject(copyObject, message);
                        } else {    // name already in use
                            message.setDetail(ERROR_COPY + "There is already a Cap Bank or CBC with the name '" + paoName + "'");
                            message.setSeverity(FacesMessage.SEVERITY_ERROR);
                        }
                    }
                }    
            } catch (IllegalArgumentException e) {
                message.setDetail(ERROR_COPY + e.getMessage());
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
            } catch (TransactionException e) {
                message.setDetail(ERROR_COPY + e.getMessage());
                message.setSeverity(FacesMessage.SEVERITY_ERROR);
            } finally {
                FacesContext.getCurrentInstance().addMessage("copy_object", message);
            }
            
            if (message.getSeverity() != FacesMessage.SEVERITY_ERROR) {
                if (copyObject instanceof CapBankController) {
                    if (copyPoints)
                        CBCCopyUtils.copyAllPointsForPAO(((CapBankController) origObject).getPAObjectID(),
                                                         ((CapBankController) copyObject).getPAObjectID());
                    routeToEditor(((CapBankController) copyObject).getPAObjectID());
                }
                if (copyObject instanceof CapBankController702x) {
                    if (copyPoints)
                        CBCCopyUtils.copyAllPointsForPAO(((CapBankController702x) origObject).getPAObjectID(),
                                                         ((CapBankController702x) copyObject).getPAObjectID());
                    routeToEditor(((CapBankController702x) copyObject).getPAObjectID());
                }
                if (copyObject instanceof CapBankControllerDNP) {
                    if (copyPoints)
                        CBCCopyUtils.copyAllPointsForPAO(((CapBankControllerDNP) origObject).getPAObjectID(),
                                                         ((CapBankControllerDNP) copyObject).getPAObjectID());
                    routeToEditor(((CapBankControllerDNP) copyObject).getPAObjectID());
                }
                if (copyObject instanceof PointBase) {
                    routeToEditor(((PointBase) copyObject).getPoint().getPointID());
                }
            }
        } else {
            if (origObject != null) {
                if (origObject instanceof YukonPAObject)
                    routeToEditor(((YukonPAObject) origObject).getPAObjectID());
                else
                    routeToEditor(((PointBase) origObject).getPoint().getPointID());
            } else {
                message.setDetail(NULL_COPY_OBJECT);
                FacesContext.getCurrentInstance().addMessage("copy_object",
                                                             message);
                return;
            }
        }

    }

    private void routeToEditor(int copyPaobjectID) {
        FacesMessage facesMessage = new FacesMessage();

        if (copyObject != null) {
            routeToCBCEditor(copyPaobjectID);
        } else if (origObject != null) {
            routeToCBCEditor(copyPaobjectID);
        } else {
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
        String red = "cbcBase.jsf?type=2&itemid=" + copyPaobjectID + "&copySuccess=true";
        try {
            FacesContext currentContext = FacesContext.getCurrentInstance();
            currentContext.getExternalContext().redirect(red);
            currentContext.responseComplete();
        } catch (Exception e) {
            CTILogger.error("DBCopyForm.routeToEditor. ERROR - " + e.getMessage());
        }
    }

    public String getPaoName() {
        return paoName + " (copy)";
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

    public String getOriginalPaoName() {
        return originalPaoName;
    }

    public void setOriginalPaoName(String originalPaoName) {
        this.originalPaoName = originalPaoName;
    }

}