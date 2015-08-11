package com.cannontech.web.util;

import java.io.IOException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.servlet.nav.CBCNavigationUtil;

public class JSFParamUtil {

	/**
	 * Allows a JSF variable to be retrieved in a JSP scriptlet
	 */
	public static Object getJSFVar(String varName) {
		if (varName == null) {
			return null;
		}

		FacesContext currentInstance = FacesContext.getCurrentInstance();
		if (currentInstance == null) {
			return null;
		}

		return currentInstance.getApplication().getVariableResolver().resolveVariable(
				FacesContext.getCurrentInstance(), varName);
	}

	/**
	 * Returns the request parameter from the JSF framework
	 */
	public static String getJSFReqParam(String paramName) {
		if (paramName == null) {
			return null;
		}

		return (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(paramName);
	}

	/**
	 * Allows URL parameters to be retrived.
	 */
	public static String[] getReqParamsVar(String varName) {
		ExternalContext ex = FacesContext.getCurrentInstance().getExternalContext();

		if (varName != null && ex != null) {
			Object retVal = ex.getRequestParameterValuesMap().get(varName);

			if (retVal instanceof String[]) {
				return (String[])retVal;
			}
			if (retVal != null) {
				return new String[]{ retVal.toString() };
			}
		}

		return null;
	}

	/**
	 * Returns the value of a child element with the given name
	 */
	public static Object getChildElemValue(UIComponent comp, String childName) {
		if(comp != null && childName != null) {
			for(int i = 0; i < comp.getChildCount(); i++) {
				UIComponent child = (UIComponent)comp.getChildren().get(i);

				if (childName.equals(child.getAttributes().get("name"))) {
					return child.getAttributes().get("value");
				}
			}
		}

		return "";
	}

	/**
	 * Returns the current YukonUser in the session scope
	 */
	public static LiteYukonUser getYukonUser() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			ExternalContext externalContext = fc.getExternalContext();
			HttpSession session = (HttpSession)externalContext.getSession(false);

			if(session == null) {
				CTILogger.warn("The current HttpSession is NULL");
				return null;
			}

			return (LiteYukonUser)session.getAttribute(LoginController.YUKON_USER);
		}
		return null;
	}

    public static void goToPointEditor(final FacesMessage fm) {
        try {
            String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
            String red = "/common/points/";
            String pointId = getJSFReqParam("ptID");

            String location = contextPath + red + pointId;

			//bookmark the current page
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
			CBCNavigationUtil.bookmarkLocationAndRedirect(location, session);
			FacesContext.getCurrentInstance().getExternalContext().redirect(location);
			FacesContext.getCurrentInstance().responseComplete();
		}  catch (IOException e) {
			fm.setDetail("ERROR - Couldn't redirect. CBControllerEditor:pointClick. " + e.getMessage());
		} catch (Exception e) {
			//add some code to handle null session exception
		} finally {
			if(fm.getDetail() != null) {
				FacesContext.getCurrentInstance().addMessage("point_click", fm);
			}
		}
	}
}