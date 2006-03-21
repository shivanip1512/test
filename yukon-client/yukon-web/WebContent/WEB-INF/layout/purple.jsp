<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page import="com.cannontech.roles.application.WebClientRole" %>

<cti:outputDoctype htmlLevel="transitional"/>
<html>
<head>
    <title><c:out value="${ctiPageTitle}"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <cti:outputHeadContent>
          <cti:includeCss link="/WebConfig/yukon/CannonStyle.css"/>
          <cti:includeCss link="/WebConfig/yukon/PurpleHeaderBar.css"/>
          <cti:includeScript link="/JavaScript/prototype.js"/>
        </cti:outputHeadContent>
        <link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
    
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0" link="#000000" vlink="#000000" alink="#000000" onload="init()">
<cti:outputContent/>
</body>

</html>
