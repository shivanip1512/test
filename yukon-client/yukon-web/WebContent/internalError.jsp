<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>
<%@page import="com.cannontech.core.roleproperties.dao.RolePropertyDao"%>
<%@page import="com.cannontech.spring.YukonSpringHook" %>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.database.data.lite.LiteYukonUser"%>
<%@page import="com.cannontech.common.exception.NotLoggedInException"%>
<%@page import="org.apache.commons.lang3.ObjectUtils"%>
<%@page import="com.cannontech.common.util.CtiUtilities"%>
<jsp:directive.page import="com.cannontech.common.version.VersionTools"/>
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
    
    showStack = !rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.SUPPRESS_ERROR_PAGE_DETAILS, user);
    if (showStack) {
        pageContext.setAttribute("yukonVersion", VersionTools.getYUKON_VERSION());
        pageContext.setAttribute("yukonDetails", VersionTools.getYukonDetails());
        
    }
    pageContext.setAttribute("loggedIn", true);

} catch (NotLoggedInException ignore) {
    pageContext.setAttribute("loggedIn", false);
}    
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
<cti:includeCss link="NORMALIZE" force="true"/>
<cti:includeCss link="YUKON" force="true"/>
<c:if test="${loggedIn}">
    <cti:css key="yukon.web.error.errorStyles"/>
</c:if>
<c:if test="${!loggedIn}">
    <link rel="stylesheet" href="<cti:url value="/WebConfig/yukon/errorStyles.css"/>" type="text/css">
</c:if>


<c:if test="${loggedIn}">
    <c:set var="logoHtml"><cti:logo key="yukon.web.error.logo"/></c:set>
    <cti:msg var="genericMainMessage" key="yukon.web.error.genericMainMessage"/>
    <cti:msg var="genericErrorMessage" key="yukon.web.error.genericSubMessage"/>
    

</c:if>

<c:if test="${!loggedIn}">
    <c:set var="logoHtml">
        <img class="logoImage" src="<cti:url value="/WebConfig/yukon/YukonBW.gif"/>">
    </c:set>
    <c:set var="genericMainMessage" value="An error occurred while processing your request."/>
    <c:set var="genericErrorMessage" value="Try to execute your request again."/>
</c:if>

</head>

<body>
<div id="error">
    <div id="errorImg">
        <a href="<cti:url value="/"/>">${logoHtml}</a>
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
             <div id="errorMain">
                ${genericMainMessage}
            </div>
        </c:otherwise>
    </c:choose>

    <c:choose>
        <c:when test="${empty errorKey}">
            <div id="errorSub">
                ${genericErrorMessage}
            </div>
        </c:when>
        <c:otherwise>
            <div id="errorSub">
                <cti:msg key="yukon.web.error.genericSubMessageWithKey" argument="${errorKey}"/>
            </div>
        </c:otherwise>
    </c:choose>
    
    <c:if test="${showStack}">
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
                    <strong>Message</strong>: <span id="rootErrorMessage">${fn:escapeXml(message)} </span>
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
</div>
</body>

</html>
