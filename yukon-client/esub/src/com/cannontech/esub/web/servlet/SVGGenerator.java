package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.RoleTypes;
import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.EsubConstants;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.web.SessionInfo;

/**
 * Description Here
 * @author alauinger
 */
public class SVGGenerator extends HttpServlet {
 
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		resp.setContentType("image/svg+xml");
		
		ServletContext sc = getServletContext();
		String uri = req.getRequestURI();
		String conPath = req.getContextPath();

		String jlxPath= uri.replaceFirst(conPath, "");
		jlxPath = sc.getRealPath(jlxPath);
		
		//Assume this ends with .svg
		jlxPath = jlxPath.substring(0, jlxPath.length()-4) + ".jlx";
				
		Writer w = resp.getWriter();
		
		try {

			Drawing d = new Drawing();
			d.load(jlxPath);
		 
			//Check if this user has access to this drawing!	
			SessionInfo	info = (SessionInfo) req.getSession(false).getAttribute(SessionInfo.SESSION_KEY);	
			LiteYukonUser user = info.getUser();
			DrawingMetaElement metaElem = d.getMetaElement();
			
			// User requires the role specific to access this drawing
			// and also the Esub VIEW role to see it
			if( AuthFuncs.checkRole(user, metaElem.getRoleID()) != null &&				
				AuthFuncs.checkRole(user, RoleTypes.ESUBVIEW) != null  ) {				
				
				// enable edit functions?
				Pair editPair = AuthFuncs.checkRole(user, RoleTypes.ESUBEDIT);			
				boolean canEdit = (editPair != null);

				com.cannontech.esub.util.SVGGenerator gen = new com.cannontech.esub.util.SVGGenerator();				
				gen.generate(w, d, canEdit, false);	
			}
		}
		catch(Exception e ) {
			CTILogger.error("Error generating svg", e);
		}
	}
}
