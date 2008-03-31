<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:outputDoctype levels="${info.htmlLevel}, strict"/>
<html>
    <head>
        <title><c:out value="${info.title}"/></title>           

        <!-- Layout CSS files -->
        <link rel="stylesheet" type="text/css" href="<c:url value="/WebConfig/yukon/CannonStyle.css"/>" >
        <link rel="stylesheet" type="text/css" href="<c:url value="/WebConfig/yukon/styles/StandardStyles.css"/>" >
        <link rel="stylesheet" type="text/css" href="<c:url value="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>" >
        
        <!-- Module CSS files from module_config.xml -->
        <c:forEach items="${moduleConfigCss}" var="file"><link rel="stylesheet" type="text/css" href="<c:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Individual files from includeCss tag on the request page -->
        <c:forEach items="${innerContentCss}" var="file"><link rel="stylesheet" type="text/css" href="<c:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Login Group specific style sheets (WebClientRole.STD_PAGE_STYLE_SHEET)-->
        <c:forEach items="${loginGroupCss}" var="file"><link rel="stylesheet" type="text/css" href="<c:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Consolidated Script Files -->
        <c:forEach items="${javaScriptFiles}" var="file"><script type="text/javascript" src="<c:url value="${file}"/>"></script>
        </c:forEach>
    </head>
    
	<body class="<c:out value="${module.moduleName}"/>_module">
		<div id="Header">
		    <div class="stdhdr_left"><div id="TopLeftLogo"></div><div id="TopLeftLogo2"></div></div>
		    <div class="stdhdr_right"><img src="<cti:theme key="yukon.web.layout.standard.upperrightlogo" default="/WebConfig/yukon/YukonBW.gif" url="true"/>"></div>
		    <div class="stdhdr_clear"></div>
		</div>

        <div class="leftMenuHeader">
            <a href="/servlet/LoginController?ACTION=LOGOUT"><cti:msg key="yukon.web.menu.logout" /></a>
        </div>
        <div style="float: left;">
	        <cti:outputContent writable="${menuRenderer}"/>
        </div>
		<div id="Content">
            <cti:outputContent writable="${bodyContent}"/>
        </div>
        <div style="clear: both;" />



		                       		
		
		<div id="CopyRight">
			<cti:msg key="yukon.web.layout.standard.yukonVersion" arguments="${yukonVersion}"/>
			<cti:msg key="yukon.web.layout.standard.copyright"/>
		</div>
	
	</body>
</html>