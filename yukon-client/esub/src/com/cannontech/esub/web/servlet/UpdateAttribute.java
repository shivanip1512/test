package com.cannontech.esub.web.servlet;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.LoginController;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.esub.PointAttributes;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.conns.ConnPool;

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
		
		LiteYukonUser user = (LiteYukonUser) req.getSession(false).getAttribute(LoginController.YUKON_USER);
		
		if(!YukonSpringHook.getBean(RolePropertyDao.class).checkProperty(YukonRoleProperty.OPERATOR_ESUBSTATION_DRAWINGS_EDIT, user)) {
			CTILogger.info("Update request received by user without EDIT role, ip: " + req.getRemoteAddr());
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
					
		Writer out = resp.getWriter();
		
		String idStr = req.getParameter(ID);
		String dattribStr = req.getParameter(DATTRIB);
		String valueStr = req.getParameter(VALUE);
		
		if( idStr == null || dattribStr == null || valueStr == null ) {
			out.write("error");
			return;
		}
		
		int id = -1;
		int dattrib = -1;
		
		try {
			id = Integer.parseInt(idStr);
			dattrib = Integer.parseInt(dattribStr);	
		}
		catch(NumberFormatException nfe) {
			out.write("error");		
			return;
		}
		
		LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(id);
		
		if( lp == null ) {
			out.write("error");		
			return;
		}
			
		DBPersistent dbObj = LiteFactory.createDBPersistent(lp);
		
		if( dbObj == null ) {
			out.write("error");		
			return;
		}
		
		Transaction retrieveTrans = Transaction.createTransaction(Transaction.RETRIEVE, dbObj);
		try {
			retrieveTrans.execute();
		}
		catch(TransactionException te) {
			out.write("error");		
			return;
		}
						
		if( PointAttributes.setAttribute(dbObj, dattrib, valueStr) == null ) {
			out.write("error");		
			return;
		}
		
		Transaction updateTrans = Transaction.createTransaction(Transaction.UPDATE, dbObj);
		
		try {
			updateTrans.execute();
		}
		catch(TransactionException te) {
			out.write("error");		
			return;
		}	
		
		// update the cache and send out a db change	
		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		DBChangeMsg[] msg = cache.createDBChangeMessages((CTIDbChange) dbObj, DbChangeType.UPDATE);
		for(int i = 0; i < msg.length; i++ ) {
			cache.handleDBChangeMessage(msg[i]);			
		
			//send out the change
			ConnPool.getInstance().getDefDispatchConn().write(msg[i]);
		}			
	}
}
