package com.cannontech.servlet;

import javax.servlet.http.*;
/**
 * Returns low, high, or duration limit value for a given point id.
 *
 * Parameters:
 * id 	-	The point id
 * type -	[ low | high | duration ]
 * db   -   The database alias to use, defaults to "yukon"
 * Creation date: (12/7/2001 10:51:19 AM)
 * @author: Aaron Lauinger 
 */
public class PointLimitsServlet extends HttpServlet {
	private String dbAlias = "yukon";
/**
 * PointLimitsServlet constructor comment.
 */
public PointLimitsServlet() {
	super();
}
/**
 * service method comment.
 */
public void service(
    javax.servlet.ServletRequest req,
    javax.servlet.ServletResponse resp)
    throws javax.servlet.ServletException, java.io.IOException
{
    resp.setContentType("text/plain");

    //required parameter
    Object id = req.getAttribute("id");
    Object type = req.getAttribute("type");
    Object dbVal = req.getAttribute("db");
	String db;

    if (dbVal != null)
    	db = dbVal.toString();
   	else
        db = dbAlias;

    java.sql.Connection conn = null;

    try
        { 
        conn = com.cannontech.database.PoolManager.getInstance().getConnection(dbAlias);

        com.cannontech.database.data.point.ScalarPoint p =
            (com.cannontech.database.data.point.ScalarPoint) com.cannontech.database.data.point.PointFactory.retrievePoint(
                new Integer(id.toString()),
                db);

        com.cannontech.database.db.point.PointLimit limit = 
        	(com.cannontech.database.db.point.PointLimit) p.getPointLimitsVector().get(0);

        if( type.toString().equalsIgnoreCase("low") )
        	resp.getWriter().write( limit.getLowLimit().toString() );
       	else
       	if( type.toString().equalsIgnoreCase("high") )
       		resp.getWriter().write( limit.getHighLimit().toString() );
       	else
       	if( type.toString().equalsIgnoreCase("duration") )
       	{	
	       	resp.getWriter().write( Integer.toString(limit.getLimitDuration().intValue() / 60) );
       	}
       	else
       		resp.getWriter().write("xtype");
       		
        resp.getWriter().flush();	
    }
    catch (java.sql.SQLException e)
        {
        e.printStackTrace();
    }
    finally
        {
        try
            {
            if (conn != null)
                conn.close();
        }
        catch (java.sql.SQLException e2)
            {
        }
    }
}
}
