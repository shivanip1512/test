<?xml version="1.0"?>
<vxml version="2.0" xmlns="http://www.w3.org/2001/vxml">

<%@page import="com.cannontech.util.ServletUtil" %>
<%@page import="com.cannontech.yukon.conns.NotifRequestException" %>
<%@page import="com.cannontech.servlet.ConnServlet" %>
<%
try {
  String token = (String)session.getAttribute("TOKEN");
  if (token == null) {
    token = request.getParameter("TOKEN");
  }
  String completeStr = request.getParameter("COMPLETE");
  boolean complete = (completeStr != null && completeStr.equalsIgnoreCase("yes"));


  ConnServlet connContainer =
    	(ConnServlet)application.getAttribute(ConnServlet.SERVLETS_CONTEXT_ID);

  connContainer.getNotifcationConn().sendConfirmation(token, complete);
} catch (Exception e) {
}
%>

<form id="done">
  <block>
    <disconnect/>
  </block>
</form>
</vxml>
