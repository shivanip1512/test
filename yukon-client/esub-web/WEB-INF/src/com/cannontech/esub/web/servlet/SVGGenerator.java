package com.cannontech.esub.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.util.Pair;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.editor.element.DrawingMetaElement;
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
		
		
		BufferedReader rdr;
		Writer w = resp.getWriter();
		
		try {

			Drawing d = new Drawing();
			d.load(jlxPath);
		 
			//Check if this user has access to this drawing!	
			SessionInfo	info = (SessionInfo) req.getSession(false).getAttribute(SessionInfo.SESSION_KEY);	
			LiteYukonUser user = info.getUser();
			DrawingMetaElement metaElem = d.getMetaElement();
			if( AuthFuncs.checkRole(user, metaElem.getViewRoleID()) != null) {
				Pair editPair = AuthFuncs.checkRole(user, metaElem.getEditRoleID());
				boolean canEdit = (editPair != null);

				com.cannontech.esub.util.SVGGenerator gen = new com.cannontech.esub.util.SVGGenerator();				
				gen.generate(w, d, canEdit, false);	
			
				//req.getRequestDispatcher(uri).forward(req,resp);
			}
		}
		catch(Exception e ) {
			e.printStackTrace(new PrintWriter(w));
		}
}
	
	/**
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig arg0) throws ServletException {
		super.init(arg0);
		log("loaded...");
	}
}
