<%@ page import="com.cannontech.core.roleproperties.YukonRoleProperty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:outputDoctype levels="${info.htmlLevel}, transitional"/>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<title><c:out value="${info.title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <%-- Layout CSS files  --%>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/CannonStyle.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/PurpleHeaderBar.css"/>" >
        <cti:includeCss link="YUKON" force="true"/>

        <cti:url var="webClientRoleStyleSheet">
          <jsp:attribute name="value" trim="true">/WebConfig/<cti:getProperty property="STYLE_SHEET" /></jsp:attribute>
        </cti:url>
        <link rel="stylesheet" type="text/css" href="${webClientRoleStyleSheet}" >

        <%-- Module CSS files from module_config.xml --%>
        <c:forEach items="${moduleConfigCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <%-- Individual files from includeCss tag on the request page --%>
        <c:forEach items="${innerContentCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <%-- Login Group specific style sheets (YukonRoleProperty.STD_PAGE_STYLE_SHEET) --%>
        <c:forEach items="${loginGroupCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <%-- Library Script Files --%>
        <c:forEach items="${libraryScriptFiles}" var="file"><script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>
        
        <tags:jsGlobals/>
        
        <%-- Yukon Script Files --%>
        <c:forEach items="${yukonScriptFiles}" var="file"><script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>

</head>

<body id="PurplePageBody" class="Background" text="#000000" leftmargin="0" topmargin="0"
	link="#000000" vlink="#000000" alink="#000000" onload="init()">
<cti:outputContent writable="${bodyContent}"/>
</body>

</html>
