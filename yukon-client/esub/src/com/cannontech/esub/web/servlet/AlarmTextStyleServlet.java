package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.common.cache.PointChangeCache;

/**
 * AlarmTextStyleServlet chooses between two svg styles given a list of point ids 
 * 
 * will take a list of point ids and two svg styles
 * If none of the points are in alarm then style 1 will be returned
 * Otherwise style2 will be returned.
 * 
 * Required Parameters:
 * id - 		The list of point ids, comma separated ie "32,43,54"
 * style1 -		The style to return if none of the points are in alarm
 * style2 - 	The style to return if at least one of the points are in alarm
 *
 * Example:
 * 
 * @author alauinger
 */
public class AlarmTextStyleServlet extends HttpServlet {

	private static final String POINT_ID_KEY = "id";
	private static final String FILL1_KEY = "fill1";
	private static final String FILL2_KEY = "fill2";
		
	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		String idStr = req.getParameter(POINT_ID_KEY);
		String fill1 = req.getParameter(FILL1_KEY);
		String fill2 = req.getParameter(FILL2_KEY);
	
		/* check if any of the points are in alarm*/		
		PointChangeCache pcc = PointChangeCache.getPointChangeCache();
		int[] pointIDs = parseIDString(idStr);				
		boolean inAlarm = false;
		
		for(int i = 0; i < pointIDs.length; i++) {
			if(pcc.getSignal(pointIDs[i]) != null) {
				inAlarm = true;
				break;				
			}
		}
		
		/* write the correct svg style */
		Writer writer = resp.getWriter();
		
		if(!inAlarm) {		
			writer.write(fill1);
		}
		else {
			writer.write(fill2);
		}
		
		writer.flush();
	}

	/**
	 * @see javax.servlet.Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig cfg) throws ServletException {
		super.init(cfg);							
	}
	
	/**
	 * Parase a comma separated list of point ids into an int[]
	 * @param s
	 * @return
	 */
	private int[] parseIDString(String s) {
		StringTokenizer tok = new StringTokenizer(s, ",", false);
		
		ArrayList idList = new ArrayList(20);
		while(tok.hasMoreTokens()) {
			String tokStr = tok.nextToken();
			//System.out.println(tokStr);
			idList.add(new Integer(tokStr));
		}
		
		int[] pointIDs = new int[idList.size()];
		for(int i = 0; i < pointIDs.length; i++) {
			pointIDs[i] = ((Integer) idList.get(i)).intValue();
		}
		
		return pointIDs;
	}

}
