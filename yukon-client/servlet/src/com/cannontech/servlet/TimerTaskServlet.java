/*
 * Created on Jan 28, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.stars.web.util.TimerTaskUtil;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class TimerTaskServlet extends HttpServlet {
    
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();
		
		TimerTaskUtil.restartAllTimerTasks();
		
		String preloadData = RoleFuncs.getGlobalPropertyValue( SystemRole.STARS_PRELOAD_DATA );
		if (CtiUtilities.isTrue( preloadData ))
			StarsDatabaseCache.getInstance().loadData();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
		if (req.getParameter("Restart") != null) {
			TimerTaskUtil.restartAllTimerTasks();
			resp.getWriter().println("All timer tasks restarted");
		}
	}
    
}
