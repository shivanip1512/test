<%@ page language="java" %>
<%@ page import="com.cannontech.database.data.web.User" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ taglib uri="/WEB-INF/jruntags.jar" prefix="jrun" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>

<%
    String content = null;
    User user = (User) request.getSession(false).getValue("USER");
    long customerID = user.getCustomerId();

    String logo = user.getCustomerWebSettings().getHomeURL() + "/NavLogo.gif";
 
    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");
    String dbAlias = user.getDatabaseAlias();
   %>
