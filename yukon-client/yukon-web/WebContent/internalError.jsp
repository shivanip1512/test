<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>
<%@page import="com.cannontech.core.roleproperties.dao.RolePropertyDao"%>
<%@page import="com.cannontech.spring.YukonSpringHook" %>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.database.data.lite.LiteYukonUser"%>
<%@page import="com.cannontech.common.exception.NotLoggedInException"%>
<%@page import="org.apache.commons.lang.ObjectUtils"%>
<%@page import="com.cannontech.common.util.CtiUtilities"%>
<jsp:directive.page import="com.cannontech.common.version.VersionTools"/>
<jsp:directive.page import="org.apache.commons.lang.BooleanUtils"/>
<jsp:directive.page import="com.cannontech.web.util.ErrorHelperFilter"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page isErrorPage="true" %>

<%
boolean showStack = false;
try {
    LiteYukonUser user = ServletUtil.getYukonUser(request);
    RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
    
    String suppressStackStr = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.SUPPRESS_ERROR_PAGE_DETAILS, user);
    showStack = !BooleanUtils.toBoolean(suppressStackStr);
    showStack = true;
    if (showStack) {
        pageContext.setAttribute("yukonVersion", VersionTools.getYUKON_VERSION());
        pageContext.setAttribute("yukonDetails", VersionTools.getYukonDetails());
        
    }

} catch (NotLoggedInException ignore) {}    
pageContext.setAttribute("showStack", showStack);


Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
// if the above returned null, this page was probably called via the JSP exception handler
// because this page is declared as an error page, the exception object will be populated
throwable = (Throwable)ObjectUtils.defaultIfNull(throwable, exception);
String errorKey = (String)request.getAttribute(ErrorHelperFilter.LOG_KEY);
pageContext.setAttribute("errorKey", errorKey);

Throwable root = CtiUtilities.getRootCause(throwable);

Object statusCode = request.getAttribute("javax.servlet.error.status_code");
statusCode = ObjectUtils.defaultIfNull(statusCode, "no status code");
pageContext.setAttribute("statusCode", statusCode.toString());
Object message = root.getMessage();
message = ObjectUtils.defaultIfNull(message, "no message");
pageContext.setAttribute("message", message.toString());
Object errorType = request.getAttribute("javax.servlet.error.exception_type");
errorType = ObjectUtils.defaultIfNull(errorType, "no error type");
pageContext.setAttribute("errorType", message.toString());
Object requestURI = request.getAttribute("javax.servlet.error.request_uri");
requestURI = ObjectUtils.defaultIfNull(requestURI, "no request uri");
pageContext.setAttribute("requestURI", requestURI.toString());
String friendlyExceptionMessage = ErrorHelperFilter.getFriendlyExceptionMessage(pageContext.getServletContext(), throwable);
pageContext.setAttribute("friendlyExceptionMessage", friendlyExceptionMessage);
pageContext.setAttribute("stackTrace", ServletUtil.printNiceHtmlStackTrace(throwable));
%>

<html>
<head>
<title>Yukon Error Page</title>
<link rel="stylesheet" href="<cti:url value="/WebConfig/yukon/styles/normalize.css"/>" type="text/css">
<link rel="stylesheet" href="<cti:url value="/WebConfig/yukon/styles/yukon.css"/>" type="text/css">
<cti:css key="yukon.web.error.errorStyles"/>

</head>

<body>
<div id="error">
<div id="errorImg">
    <cti:link key="yukon.web.error.logoLink" href="/" escapeBody="false">
        <cti:logo key="yukon.web.error.logo" />
    </cti:link>
</div>
<c:choose>
    <c:when test="${not empty friendlyExceptionMessage}">
        <br/>
        <div id="errorFriendly">
            <cti:msg key="yukon.web.error.detailedFriendlyMessage" argument="${friendlyExceptionMessage}" />
        </div>
        <br/>
    </c:when>
    <c:otherwise>
        <div id="errorMain"><cti:msg key="yukon.web.error.genericMainMessage" /></div>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${empty errorKey}">
        <div id="errorSub"><cti:msg key="yukon.web.error.genericSubMessage"/></div>
    </c:when>
    <c:otherwise>
        <div id="errorSub">
            <cti:msg key="yukon.web.error.genericSubMessageWithKey" argument="${errorKey}"/>
        </div>
    </c:otherwise>
</c:choose>

<c:if test="${showStack}">
    <cti:includeScript link="PROTOTYPE" force="true"/>
    <script type="text/javascript">
    function showStack( chkBox ) {
        var elem = document.getElementById('stackTrace');
        elem.style.display = 'block';
        document.getElementById('showMore').style.display = 'none';
    
    }
    </script>
    <div id="errorDetail">
        <div id="showMore">
            <a href="javascript:showStack()">Detailed information</a>
        </div>
        <div style="display: none" id="stackTrace">
             <p>
                <strong>Yukon Version:</strong> ${yukonVersion}
             </p>
             <p>
                <strong>Yukon Version Details:</strong> ${yukonVersion}
             </p>
             <p>
                <strong>Status code:</strong> ${statusCode}
            </p>
            <p>
                <strong>Message</strong>: <span id="rootErrorMessage">${fn:escapeXml(message)} 
            </p>
            <p>
                <strong>Error type</strong>: ${errorType}
            </p>
            <p>
                <strong>Request URI</strong>: ${requestURI}
            </p>
            <p>
                ${stackTrace}
            </p>
        </div>
    </div>
</c:if>
    
</body>

</html>
