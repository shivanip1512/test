<%@page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:outputDoctype levels="${info.htmlLevel}, transitional"/>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<title><c:out value="${info.title}" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
        <!-- Layout CSS files -->
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/CannonStyle.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/PurpleHeaderBar.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>">

        <cti:url var="webClientRoleStyleSheet">
          <jsp:attribute name="value" trim="true">/WebConfig/<cti:getProperty property="STYLE_SHEET" /></jsp:attribute>
        </cti:url>
        <link rel="stylesheet" type="text/css" href="${webClientRoleStyleSheet}" >

        <!-- Module CSS files from module_config.xml -->
        <c:forEach items="${moduleConfigCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Individual files from includeCss tag on the request page -->
        <c:forEach items="${innerContentCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Login Group specific style sheets (YukonRoleProperty.STD_PAGE_STYLE_SHEET)-->
        <c:forEach items="${loginGroupCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Consolidated Script Files -->
        <c:forEach items="${javaScriptFiles}" var="file"><script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>

        <!-- Some prerequisite globals -->
        <cti:url var="appName" value="/"/>
        <c:if test="${appName == '/'}">
            <%-- When we're running as the root web application, we need an empty string for a prefix. --%>
            <c:set var="appName" value=""/>
        </c:if>
        <script type="text/javascript">
        var YG = {
                PHONE: {
                    FORMATS: <cti:msg2 key="yukon.common.phoneNumberFormatting.formats"/>
                },
                APP_NAME: '${appName}'
        };
        </script>
</head>

<body id="PurplePageBody" class="Background" text="#000000" leftmargin="0" topmargin="0"
	link="#000000" vlink="#000000" alink="#000000" onload="init()">
<cti:outputContent writable="${bodyContent}"/>
</body>

</html>
