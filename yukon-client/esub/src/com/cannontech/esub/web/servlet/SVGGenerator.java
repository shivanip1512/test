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

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.esub.Drawing;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.esub.svg.ESubSVGGenerator;
import com.cannontech.esub.svg.ISVGGenerator;
import com.cannontech.esub.svg.SVGOptions;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

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
		
		YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(req);
        
        resp.setContentType("image/svg+xml");
		
		ServletContext sc = getServletContext();
		String uri = req.getRequestURI();
		String conPath = req.getContextPath();
        
        String jlxPath= uri.replaceFirst(conPath, "");
		jlxPath = sc.getRealPath(jlxPath);
        jlxPath = StringUtils.remove(jlxPath, "\\svgGenerator");
        int esubPathIndex = jlxPath.lastIndexOf("\\");
        String rejectedPath = jlxPath.substring(0, esubPathIndex);
        rejectedPath += "\\rejected.jlx";
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
			d.setUserContext(userContext);
            //Check if this user has access to this drawing!
			LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);
			
			d.load(jlxPath);

			DrawingMetaElement metaElem = d.getMetaElement();
			RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
			
			// User requires the role specific to access this drawing
			// and also the Esub VIEW role to see it, which should we be using?
			if( YukonSpringHook.getBean(AuthDao.class).getRole(user, metaElem.getRoleID()) != null &&	
				    rolePropertyDao.checkProperty(YukonRoleProperty.getForId(com.cannontech.roles.operator.EsubDrawingsRole.VIEW), user)) {				
							
				boolean canEdit = rolePropertyDao.checkProperty(YukonRoleProperty.getForId(com.cannontech.roles.operator.EsubDrawingsRole.EDIT), user);
				boolean canControl = rolePropertyDao.checkProperty(YukonRoleProperty.getForId(com.cannontech.roles.operator.EsubDrawingsRole.CONTROL), user);

				SVGOptions svgOptions = new SVGOptions();
				svgOptions.setStaticSVG(false);
				svgOptions.setScriptingEnabled(true);
				svgOptions.setEditEnabled(canEdit);
				svgOptions.setControlEnabled(canControl);
				svgOptions.setAudioEnabled(true); // TODO set to role property value
				ISVGGenerator gen = new ESubSVGGenerator (svgOptions);
                gen.generate(w, d);	
			} else {
			    d = new Drawing();
			    d.setUserContext(userContext);
	            d.load(rejectedPath);
	            SVGOptions svgOptions = new SVGOptions();
                svgOptions.setStaticSVG(true);
                svgOptions.setScriptingEnabled(true);
                svgOptions.setEditEnabled(false);
                svgOptions.setControlEnabled(false);
                svgOptions.setAudioEnabled(false);
	            ISVGGenerator gen = new ESubSVGGenerator (svgOptions);
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
