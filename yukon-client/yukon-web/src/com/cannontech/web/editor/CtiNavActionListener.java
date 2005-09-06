package com.cannontech.web.editor;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.web.navigation.CtiNavObject;
import com.cannontech.web.util.JSFParamUtil;

/**
 * @author ryan
 *
 */
public class CtiNavActionListener implements ActionListener {
	
	public void processAction(ActionEvent event) throws AbortProcessingException
	{
		FacesContext context = FacesContext.getCurrentInstance();

		try {

			VariableResolver vr = context.getApplication().getVariableResolver();
			CtiNavObject ctiNav = (CtiNavObject)vr.resolveVariable(context, "CtiNavObject");

			if( ctiNav != null )
				context.getExternalContext().redirect( ctiNav.getModuleExitPage() );
			else
				CTILogger.warn("CtiNavObject not found in session, ignoring redirect request" );


			//remove any & all session variables that are not longer needed
			JSFParamUtil.removeJSFVar("capControlForm");
			JSFParamUtil.removeJSFVar("ptEditorForm");

			
			context.responseComplete();

		} catch( IOException ioe ) {
			CTILogger.error("Unable to redirect request", ioe );
		}

	}	
	
	
}