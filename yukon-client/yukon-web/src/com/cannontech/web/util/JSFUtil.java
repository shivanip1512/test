package com.cannontech.web.util;

import java.io.IOException;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.cannontech.clientutils.CTILogger;

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
    
    public static void addNullMessage(String message) {
        FacesMessage msg = new FacesMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public static void handleException(String message, Throwable t) {
        FacesMessage msg = new FacesMessage(message, t.getMessage());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        CTILogger.error(message, t);
    }
    
    public static String redirect(String url) {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
            FacesContext.getCurrentInstance().responseComplete();
        } catch (IOException e) {
            CTILogger.error("Unable to redirect to " + url, e);
        }

        return null;
    }

}
