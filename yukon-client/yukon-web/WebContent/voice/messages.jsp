<?xml version="1.0"?>
<vxml version="2.0">

<%@page import="com.cannontech.util.ServletUtil" %>

<%
String username = ServletUtil.getYukonUser(session).getUsername();
String token = (String)session.getAttribute("TOKEN");
//LiteContact lContact = YukonUserFuncs.getLiteContact( userID );
%>

<form id="message">

	<block>
	Welcome too message box <%=username%>, here is your token <%=token%>.  Good by.
	</block>
	<disconnect/>
</form>

</vxml>