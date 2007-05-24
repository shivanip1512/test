package com.cannontech.web.util;

import java.io.IOException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.web.editor.DBEditorForm;

public abstract class JSFUtil {

    public static void clearComponent(UIComponent theForm) {
        if (theForm == null) {
            return;
        }
        if (theForm instanceof EditableValueHolder) {
            EditableValueHolder editable = (EditableValueHolder) theForm;
            editable.setSubmittedValue(null);
            editable.setValue(null);
            editable.setLocalValueSet(false);
        }
        List<?> children = theForm.getChildren();
        for (Object child : children) {
            if (child instanceof UIComponent) {
                UIComponent childComponent = (UIComponent) child;
                clearComponent(childComponent);
            }
        }
    }

    public static void addNullWarnMessage(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
                                            message,
                                            null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static void addNullInfoMessage(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                            message,
                                            null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static void handleException(String message, Throwable t) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                            message + ": " + t.getMessage(),
                                            null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        CTILogger.error(message, t);
    }

    public static String redirect(String url) {
        try {
            FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .redirect(url);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            CTILogger.error("Unable to redirect to " + url, e);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static SelectItem[] convertSelectionList(int selectionListId) {
        YukonSelectionList yukonSelectionList = DaoFactory.getYukonListDao()
                                                          .getYukonSelectionList(selectionListId);
        List<YukonListEntry> yukonListEntries = yukonSelectionList.getYukonListEntries();
        SelectItem[] items = new SelectItem[yukonListEntries.size()];
        int i = 0;
        for (YukonListEntry entry : yukonListEntries) {
            SelectItem selectItem = new SelectItem(entry.getEntryID(),
                                                   entry.getEntryText());
            items[i++] = selectItem;
        }
        return items;
    }

    public static void resetBackingBean(String beanName) {

        FacesContext currentInstance = FacesContext.getCurrentInstance();
        if (currentInstance != null)
        {
            currentInstance
                        .getApplication()
                        .createValueBinding("#{" + beanName + "}")
                        .setValue(FacesContext.getCurrentInstance(), null);
        }
    }
    public static LiteYukonUser getYukonUser() {
        FacesContext facesCxtInstance = FacesContext.getCurrentInstance();
        LiteYukonUser liteYukonUser = null;
        if (facesCxtInstance != null)
        {
            ExternalContext externalContext = facesCxtInstance.getExternalContext();
            liteYukonUser = (LiteYukonUser) externalContext.getSessionMap().get(LoginController.YUKON_USER);
        }

        return liteYukonUser;
    }

    public static void resetForm(String name) {
        Object bean =  JSFParamUtil.getJSFVar(name);
        if (bean instanceof DBEditorForm) {
            DBEditorForm form = (DBEditorForm) bean;
            form.resetForm();
        }
    
    }

}
