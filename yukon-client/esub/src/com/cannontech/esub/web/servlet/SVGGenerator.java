 package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.util.SVGOptions;
import com.cannontech.roles.cicustomer.EsubDrawingsRole;

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

		//Assume this ends with .svg or .svgz		
		if(jlxPath.toLowerCase().endsWith(".svg")) {
			jlxPath = jlxPath.substring(0, jlxPath.length()-4) + ".jlx";
		}
		else 
		if(jlxPath.toLowerCase().endsWith(".svgz")) {		
			jlxPath = jlxPath.substring(0, jlxPath.length()-5) + ".jlx";
		}
		else {
			CTILogger.error("Request didn't end in svg or svgz, don't know what to do with it!");
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
		
		//decode the name just in case it as encoded first
		jlxPath = URLDecoder.decode( jlxPath, "UTF-8" );

				
		Writer w = resp.getWriter();
		
		try {
			Drawing d = new Drawing();
			d.load(jlxPath);
		 
			//Check if this user has access to this drawing!	
			LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);
			DrawingMetaElement metaElem = d.getMetaElement();
			
			// User requires the role specific to access this drawing
			// and also the Esub VIEW role to see it, which should we be using?
			if( AuthFuncs.checkRole(user, metaElem.getRoleID()) != null &&	
				(AuthFuncs.checkRoleProperty(user, com.cannontech.roles.operator.EsubDrawingsRole.VIEW) ||
				 AuthFuncs.checkRoleProperty(user, EsubDrawingsRole.VIEW))) {				
							
				boolean canEdit = AuthFuncs.checkRoleProperty(user, com.cannontech.roles.operator.EsubDrawingsRole.EDIT);
				boolean canControl = AuthFuncs.checkRoleProperty(user, com.cannontech.roles.operator.EsubDrawingsRole.CONTROL);
				
				SVGOptions svgOptions = new SVGOptions();
				svgOptions.setStaticSVG(false);
				svgOptions.setScriptingEnabled(true);
				svgOptions.setEditEnabled(canEdit);
				svgOptions.setControlEnabled(canControl);
				
				com.cannontech.esub.util.SVGGenerator gen = new com.cannontech.esub.util.SVGGenerator(svgOptions);				
				gen.generate(w, d);	
			}
		}
		catch(Exception e ) {
			CTILogger.error("Error generating svg", e);
		}
	}

	public void init(ServletConfig cfg) throws ServletException {		
		super.init(cfg);
	}

}
