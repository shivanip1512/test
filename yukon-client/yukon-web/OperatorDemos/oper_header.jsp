<%@ page language="java" %>
<%@ page import="com.cannontech.database.data.web.Operator" %>
<%@ page import="com.cannontech.servlet.LCConnectionServlet" %>
<%@ page import="com.cannontech.web.loadcontrol.LoadcontrolCache" %>
<%@ taglib uri="/WEB-INF/lib/jruntags.jar" prefix="jrun" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>

<cti:checklogin/>

<%
    String content;
    Operator operator = 
        (Operator) request.getSession(false).getValue("OPERATOR");

    long energyCompanyID = operator.getEnergyCompanyID();

    LCConnectionServlet cs = (LCConnectionServlet) application.getAttribute(LCConnectionServlet.SERVLET_CONTEXT_ID);
    LoadcontrolCache cache = cs.getCache();
    java.text.SimpleDateFormat datePart = new java.text.SimpleDateFormat("MM/dd/yyyy");	  
    java.text.SimpleDateFormat timePart = new java.text.SimpleDateFormat("HH:mm");
    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MM:dd:yyyy:HH:mm:ss");

    String dbAlias = operator.getDatabaseAlias();
%>
