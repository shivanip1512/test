<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>

<cti:outputDoctype htmlLevel="transitional" />
<html>
<head>
<title><c:out value="${ctiPageTitle}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<cti:outputHeadContent>
	<cti:includeCss link="/WebConfig/yukon/CannonStyle.css" />
	<cti:includeCss link="/WebConfig/yukon/PurpleHeaderBar.css" />
	<cti:includeCss>
		<jsp:attribute name="link" trim="true">
		    /WebConfig/<cti:getProperty property="WebClientRole.STYLE_SHEET" />
		</jsp:attribute>
	</cti:includeCss>
	<cti:includeScript link="/JavaScript/prototype.js" />
</cti:outputHeadContent>
</head>

<body id="PurplePageBody" class="Background" text="#000000" leftmargin="0" topmargin="0"
	link="#000000" vlink="#000000" alink="#000000" onload="init()">
<cti:outputContent />
</body>

</html>
