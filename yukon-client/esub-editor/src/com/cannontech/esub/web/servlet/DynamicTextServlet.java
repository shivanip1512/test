package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.cache.PointChangeCache;
import com.cannontech.esub.util.UpdateUtil;

/**
 * PointData will take a point id and a set of attributes for that point and
 * will attempt return a formatted string containing this information.
 * 
 * Required Parameters:
 * id - 		The id of the point to return data for
 * dattrib -    The OR'd attributes of the point you want returned
 * 
 * *dattrib is a bit field, see definition in com.cannontch.esub.element.DynamicText
 * 
 * Example:
 * PointData?id=545&dattrib=3
 * 
 * Will return a string containing the current value and the unit of measure
 * of the point with id 545.
 * 
 * It might look something like this:
 * 34.5 KWH
 * 
 * 
 * 
 * @author alauinger
 */
public class DynamicTextServlet extends HttpServlet {

	private static final String POINT_ID_KEY = "id";
	private static final String DISPLAY_ATTRIBUTE_KEY = "dattrib";
	
	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		String idStr = req.getParameter(POINT_ID_KEY);
		String displayAttribStr = req.getParameter(DISPLAY_ATTRIBUTE_KEY);
				
		int id = Integer.parseInt(idStr);
		int dattrib = Integer.parseInt(displayAttribStr);
				
		PointChangeCache pcc = PointChangeCache.getPointChangeCache();
		
		Writer writer = resp.getWriter();		
		writer.write(UpdateUtil.getDynamicTextString(id, dattrib));
		writer.flush();
	}

	/**
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);							
	}

}
