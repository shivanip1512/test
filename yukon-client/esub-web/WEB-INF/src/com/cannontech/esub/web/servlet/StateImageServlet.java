package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.esub.util.UpdateUtil;

/**
 * Returns the name of the image that corresponds to a point's current state.
 * Currently must be a status point, maybe be analog in the future.
 * 
 * Required Parameters:
 * id		- id of the point
 * 
 * @author alauinger
 *
 */
public class StateImageServlet extends HttpServlet {

	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		int id = Integer.parseInt(req.getParameter("id"));
	
		Writer writer = resp.getWriter();
		writer.write(UpdateUtil.getStateImageName(id));	
		writer.flush();
	}

}
