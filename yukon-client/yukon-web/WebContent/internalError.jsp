<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.cannontech.core.dao.DaoFactory" %>
<%@page import="com.cannontech.spring.YukonSpringHook" %>
<%@page import="com.cannontech.system.dao.GlobalSettingsDao" %>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.system.GlobalSetting"%>
<%@page import="com.cannontech.database.data.lite.LiteYukonUser"%>
<%@page import="com.cannontech.common.exception.NotLoggedInException"%>
<%@page import="org.apache.commons.lang.ObjectUtils"%>
<%@page import="com.cannontech.common.util.CtiUtilities"%>
<jsp:directive.page import="com.cannontech.common.version.VersionTools"/>
<jsp:directive.page import="com.cannontech.roles.application.WebClientRole"/>
<jsp:directive.page import="org.apache.commons.lang.BooleanUtils"/>
<jsp:directive.page import="com.cannontech.web.util.ErrorHelperFilter"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@page isErrorPage="true" %>

<%
    GlobalSettingsDao globalSettingsDao = YukonSpringHook.getBean("globalSettingsDao", GlobalSettingsDao.class);
String logo = "/" +  globalSettingsDao.getString(GlobalSetting.WEB_LOGO_URL);

String homeUrl = "/";
try {
    LiteYukonUser user = ServletUtil.getYukonUser(request);
    homeUrl = ServletUtil.createSafeUrl(request, DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.HOME_URL));
} catch (NotLoggedInException ignore) { }    


Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
// if the above returned null, this page was probably called via the JSP exception handler
// because this page is declared as an error page, the exception object will be populated
throwable = (Throwable)ObjectUtils.defaultIfNull(throwable, exception);
String errorKey = (String)request.getAttribute(ErrorHelperFilter.LOG_KEY);
Throwable root = CtiUtilities.getRootCause(throwable);

Object status_code = request.getAttribute("javax.servlet.error.status_code");
status_code = ObjectUtils.defaultIfNull(status_code, "no status code");
Object message = root.getMessage();
message = ObjectUtils.defaultIfNull(message, "no message");
Object error_type = request.getAttribute("javax.servlet.error.exception_type");
error_type = ObjectUtils.defaultIfNull(error_type, "no error type");
Object request_uri = request.getAttribute("javax.servlet.error.request_uri");
request_uri = ObjectUtils.defaultIfNull(request_uri, "no request uri");

boolean showStack = true;
String suppressStackStr = DaoFactory.getAuthDao().getRolePropertyValue( ServletUtil.getYukonUser(request), WebClientRole.SUPPRESS_ERROR_PAGE_DETAILS );
showStack = !BooleanUtils.toBoolean(suppressStackStr);

String friendlyExceptionMessage = ErrorHelperFilter.getFriendlyExceptionMessage(pageContext.getServletContext(), throwable);
%>

<html>
<head>
<title>Yukon Error Page</title>
<link rel="stylesheet" href="<cti:url value="/WebConfig/yukon/styles/StandardStyles.css"/>" type="text/css">
<cti:css key="yukon.web.error.errorStyles"/>

</head>

<body>
<div id="error">
<div id="errorImg">
    <cti:link key="yukon.web.error.logoLink" href="/" escapeBody="false">
        <cti:logo key="yukon.web.error.logo" />
    </cti:link>
</div>
<% if (friendlyExceptionMessage != null) { %>
<br/>
<div id="errorFriendly">
<c:set var="friendlyExceptionMessage" value="<%=friendlyExceptionMessage %>"/>
<cti:msg key="yukon.web.error.detailedFriendlyMessage" argument="${friendlyExceptionMessage}" />
</div>
<br/>
<% } else{%>
<div id="errorMain"><cti:msg key="yukon.web.error.genericMainMessage" /></div>
<% }%>

<% if (errorKey == null) { %>
<div id="errorSub"><cti:msg key="yukon.web.error.genericSubMessage"/></div>
<% } else { %>
<c:set var="errorKey" value="<%=errorKey %>"/>
    <div id="errorSub">
        <cti:msg key="yukon.web.error.genericSubMessageWithKey" argument="${errorKey}"/>
    </div>
<% } %>
<% if (showStack) { %>
<cti:includeScript link="PROTOTYPE" force="true"/>
<script type="text/javascript">
function showStack( chkBox ) {
	var elem = $('stackTrace');
	elem.style.display = 'block';
    $('showMore').style.display = 'none';

}
</script>
<div id="errorDetail">
<div id="showMore"><a href="javascript:showStack()">Detailed information</a></div>
<div style="display: none" id="stackTrace">
    <b>Yukon Version:</b> <%= VersionTools.getYUKON_VERSION()%>
    <br><b>Yukon Version Details:</b> <%= VersionTools.getYukonDetails() %>
    <br><b>Status code:</b> <%= status_code.toString()%>
    <br><b>Message</b>: <spring:escapeBody htmlEscape="true"><%= message.toString()%></spring:escapeBody>
    <br><b>Error type</b>: <%= error_type.toString()%>
    <br><b>Request URI</b>: <%= request_uri.toString()%>
    <%= ServletUtil.printNiceHtmlStackTrace(throwable)%>
</div>
</div>
<% } %>
</body>

</html>
