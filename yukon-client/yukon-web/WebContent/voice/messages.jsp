<?xml version="1.0"?>
<vxml version="2.0">

<%@page import="com.cannontech.util.ServletUtil" %>
<%@page import="com.cannontech.yukon.conns.NotifClientConnection" %>
<%@page import="com.cannontech.yukon.conns.NotifRequestException" %>
<%@page import="com.cannontech.servlet.ConnServlet" %>
<%
String username = ServletUtil.getYukonUser(session).getUsername();
String token = (String)session.getAttribute("TOKEN");
String resultStr = null;

try {
    ConnServlet connContainer =
    	(ConnServlet)application.getAttribute(ConnServlet.SERVLETS_CONTEXT_ID);

	//request the display string from the server. We will wait a fixed
	// number of seconds on this request before a timeout.
	resultStr = connContainer.getNotifcationConn().requestMessage( token );
}
catch( NotifRequestException nre )
{
	//we either timed-out here or no message was returned for the given token
	
}

%>

<form id="message">

	<block>
	Welcome too message box <%=username%>, here is your token <%=token%>.  Good by.
	</block>
	<disconnect/>
</form>

</vxml>