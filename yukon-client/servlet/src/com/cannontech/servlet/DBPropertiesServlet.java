/*
 * Created on Jun 30, 2003
 */
package com.cannontech.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.util.ServletUtil;

/**
 * Writes the contents of db.properties out the responses output stream if
 * a user is found in the session.
 * @author aaron
 */
public class DBPropertiesServlet extends HttpServlet {

	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		HttpSession session = req.getSession(false);
		
		if(session != null) {
			LiteYukonUser user = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
			if(user != null) {
				sendDBProps(resp.getOutputStream());		
			}
		}	
	}
		
	private void sendDBProps(OutputStream out) {

		InputStream is = null;		
		try {		
			is = PoolManager.getDBInputStream();

			byte[] buf = new byte[4096];
			int r = -1;
			while((r = is.read(buf, 0, buf.length)) != -1) {
				out.write(buf, 0, r);
			}
		}
		catch(Exception e) {
			CTILogger.error("Couldn't load " + PoolManager.getPropertyURL(), e);
		}
		finally {
			try { if(is != null) is.close(); } catch(IOException e2) {} 			
		}
	}

}
