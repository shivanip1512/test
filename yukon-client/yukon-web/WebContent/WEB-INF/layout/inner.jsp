<%@ page errorPage="/internalError.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:outputDoctype levels="${info.htmlLevel}, strict"/>
<html>
    <head>
    	<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
        <title></title>           

        <!-- Layout CSS files -->
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/reset.css"/>" >
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>" />
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/icons.css"/>"/>
        <link rel="stylesheet" type="text/css" href="<cti:url value="/WebConfig/yukon/styles/functional-overrides.css"/>" />

        <!-- Module CSS files from module_config.xml -->
        <c:forEach items="${moduleConfigCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Individual files from includeCss tag on the request page -->
        <c:forEach items="${innerContentCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Login Group specific style sheets (WebClientRole.STD_PAGE_STYLE_SHEET)-->
        <c:forEach items="${loginGroupCss}" var="file"><link rel="stylesheet" type="text/css" href="<cti:url value="${file}"/>" >
        </c:forEach>
        
        <!-- Consolidated Script Files -->
        <c:forEach items="${javaScriptFiles}" var="file"><script type="text/javascript" src="<cti:url value="${file}"/>"></script>
        </c:forEach>
    </head>
<body>

<div id="InnerContent">
<cti:outputContent writable="${bodyContent}"/>
</div> <!-- Content -->

</body>
</html>