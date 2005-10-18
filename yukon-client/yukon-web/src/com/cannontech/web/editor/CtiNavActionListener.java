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

			if( ctiNav != null ) {
				
				String red = "";

				//redirect to our module redirect page first, if it set, else
				// we return to the module exit page
				if( ctiNav.getModuleRedirectPage() != null ) {
					red = ctiNav.getModuleRedirectPage();
					ctiNav.setModuleRedirectPage( null );
				}
				else {
					red = ctiNav.getModuleExitPage();
				}

				context.getExternalContext().redirect( red );
			}
			else
				CTILogger.warn("CtiNavObject not found in session, ignoring redirect request" );


			//remove any & all session variables that are no longer needed
			JSFParamUtil.removeJSFVar("capControlForm");
			JSFParamUtil.removeJSFVar("ptEditorForm");
			JSFParamUtil.removeJSFVar("paoDeleteForm");

			
			context.responseComplete();

		} catch( IOException ioe ) {
			CTILogger.error("Unable to redirect request", ioe );
		}

	}	
	
	
}