<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ page import="com.cannontech.clientutils.CTILogger" %>

<%
	if(request.getParameter("clear") != null) {
		CTILogger.info("Manually clearing database and loadcontrol cache");
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();	
		LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
	    LoadcontrolCache cache = cs.getCache();		
	    cache.refresh();
		out.println("<b>cache cleared</b><br>");
	}
%>	
Click <a href="cache.jsp?clear=true">here</a> to clear the cache