package com.cannontech.web.editor;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.web.navigation.CtiNavObject;

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

			if ( ctiNav != null ) {
				
				String red = "";
				HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
                
			    red = CBCNavigationUtil.goBack(session);

			    
			    if (ctiNav.getModuleRedirectPage() != null) {
                    //redirect to our module redirect page first, if it set, else
                    // we return to the module exit page
                    red = ctiNav.getModuleRedirectPage();
                    ctiNav.setModuleRedirectPage( null );
				} else if (red.equalsIgnoreCase("") ) {
					red = ctiNav.getModuleExitPage();
					ctiNav.setModuleExitPage( null );
				}

				if (red.equalsIgnoreCase(ctiNav.getModuleExitPage()) && ctiNav.getPreservedAddress() != null) {
                    //This is here for when we are switching in and out of JSF pages, 
				    // it preserves the return point since it will fall off of our 2 page history in CtiNav
				    ctiNav.setCurrentPage(ctiNav.getPreservedAddress());
                    ctiNav.setPreservedAddress(null);
                }
			    
				context.getExternalContext().redirect( red );
                context.responseComplete();
			}
			else
				CTILogger.warn("CtiNavObject not found in session, ignoring redirect request" );

			
           
         

		} catch( IOException ioe ) {
			CTILogger.error("Unable to redirect request", ioe );
		}

	}	
	
	
}