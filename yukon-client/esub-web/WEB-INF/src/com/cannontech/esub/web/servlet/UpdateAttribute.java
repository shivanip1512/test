package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.esub.editor.element.PointAttributes;

/**
 * Update a point attribute in the database.
 * 
 * Required Parameters:
 * id		- The point id
 * dattrib 	- The attribute to update (see DynamicText, only 1 bit can be set)
 * value	- The new value for the attribute
 * 
 * @author alauinger
 */
public class UpdateAttribute extends HttpServlet {

	// Parameter Names
	private static final String ID = "id";
	private static final String DATTRIB = "dattrib";
	private static final String VALUE = "value";
		
	/**
	 * @see javax.servlet.http.HttpServlet#service(HttpServletRequest, HttpServletResponse)
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		
		Writer out = resp.getWriter();
		
		String idStr = req.getParameter(ID);
		String dattribStr = req.getParameter(DATTRIB);
		String valueStr = req.getParameter(VALUE);
		
		if( idStr == null || dattribStr == null || valueStr == null ) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);	
			return;
		}
		
		int id = -1;
		int dattrib = -1;
		
		try {
			id = Integer.parseInt(idStr);
			dattrib = Integer.parseInt(dattribStr);	
		}
		catch(NumberFormatException nfe) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
			return;
		}
		
		LitePoint lp = PointFuncs.getLitePoint(id);
		
		if( lp == null ) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
			return;
		}
			
		DBPersistent dbObj = LiteFactory.createDBPersistent(lp);
		
		if( dbObj == null ) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
			return;
		}
		
		Transaction retrieveTrans = Transaction.createTransaction(Transaction.RETRIEVE, dbObj);
		try {
			retrieveTrans.execute();
		}
		catch(TransactionException te) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
			return;
		}
						
		if( PointAttributes.setAttribute(dbObj, dattrib, valueStr) == null ) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
			return;
		}
		
		Transaction updateTrans = Transaction.createTransaction(Transaction.UPDATE, dbObj);
		
		try {
			updateTrans.execute();
		}
		catch(TransactionException te) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);		
			return;
		}		
	}

			
		

}
