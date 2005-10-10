<?xml version="1.0" encoding="iso-8859-1"?>

<%@ page import="com.cannontech.roles.application.WebClientRole" %>
<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="t" %>

<%
// NOTE: do not put JSF components in here since there seems to be a bug
//  with the flush attribute in tiles (struts 1.2.7)  http://issues.apache.org/bugzilla/show_bug.cgi?id=21972
%>

<cti:checklogin/>

<html>
	<head>
		<title><t:getAsString name="title" /></title>			

		<link rel="stylesheet" type="text/css" href="/editor/css/base.css" />
		<link rel="stylesheet" type="text/css"
			href="/WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" />

	</head>

	<body>

		<div id="header">
			<t:insert attribute="header" flush="false" />
		</div>

		<div id="center">
			<t:insert attribute="content" flush="false" />
		</div>

		<div id="footer">
			<t:insert attribute="footer" flush="false" />
		</div>

	</body>

</html>