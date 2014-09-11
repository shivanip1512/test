package com.cannontech.web.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public abstract class JSFUtil {

    public static void addNullWarnMessage(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, message, null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static void addNullInfoMessage(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static void handleException(String message, Throwable t) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message + ": " + t.getMessage(), null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        CTILogger.error(message, t);
    }

    public static String redirect(String url) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            facesContext.getExternalContext().redirect(facesContext.getExternalContext().getRequestContextPath() + url);
            facesContext.responseComplete();
        } catch (IOException e) {
            CTILogger.error("Unable to redirect to " + url, e);
        }

        return null;
    }

    public static SelectItem[] convertSelectionListByName(int selectionListId) {
        return convertSelectionList(selectionListId, true);
    }

    private static SelectItem[] convertSelectionList(int selectionListId, boolean byName) {
        YukonSelectionList yukonSelectionList =
            YukonSpringHook.getBean(YukonListDao.class).getYukonSelectionList(selectionListId);
        List<YukonListEntry> yukonListEntries = yukonSelectionList.getYukonListEntries();
        SelectItem[] items = new SelectItem[yukonListEntries.size()];
        int i = 0;
        for (YukonListEntry entry : yukonListEntries) {
            Object id = entry.getEntryID();
            if (byName) {
                id = entry.getEntryText();
            }
            SelectItem selectItem = new SelectItem(id, entry.getEntryText());
            items[i++] = selectItem;
        }
        return items;
    }

    public static LiteYukonUser getYukonUser() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            ExternalContext externalContext = fc.getExternalContext();
            Map<?, ?> map = externalContext.getSessionMap();
            LiteYukonUser liteYukonUser = (LiteYukonUser) map.get(LoginController.YUKON_USER);
            return liteYukonUser;
        }
        throw new RuntimeException("Could not get Current Instance of FacesContext, returning null user");
    }

    public static YukonUserContext getYukonUserContext() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        return YukonUserContextUtils.getYukonUserContext(request);
    }
}
