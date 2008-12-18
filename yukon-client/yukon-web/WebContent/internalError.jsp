<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.cannontech.core.dao.DaoFactory" %>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.roles.yukon.SystemRole"%>
<%@page import="com.cannontech.database.data.lite.LiteYukonUser"%>
<%@page import="com.cannontech.common.exception.NotLoggedInException"%>
<%@page import="org.apache.commons.lang.ObjectUtils"%>
<%@page import="com.cannontech.common.util.CtiUtilities"%>
<jsp:directive.page import="com.cannontech.common.version.VersionTools"/>
<jsp:directive.page import="com.cannontech.roles.application.WebClientRole"/>
<jsp:directive.page import="org.apache.commons.lang.BooleanUtils"/>
<jsp:directive.page import="com.cannontech.web.util.ErrorHelperFilter"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page isErrorPage="true" %>

<%
String logo = "/" +
	DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );

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
try {
    String suppressStackStr = DaoFactory.getAuthDao().getRolePropertyValue( ServletUtil.getYukonUser(request), WebClientRole.SUPPRESS_ERROR_PAGE_DETAILS );
    showStack = !BooleanUtils.toBoolean(suppressStackStr);
} finally {
}

String friendlyExceptionMessage = ErrorHelperFilter.getFriendlyExceptionMessage(pageContext.getServletContext(), throwable);
%>

<html>
<head>
<title>Yukon Error Page</title>
<link rel="stylesheet" href="<c:url value="/WebConfig/yukon/StandardStyles.css"/>" type="text/css">

<style type="text/css">

  body {
    background: #eee;
  }
  #error {
    background: white;
    width: 90%;
    margin-left: auto;
    margin-right: auto;
    margin-top: 30px;
    padding: 20px;
    text-align: center;
    border: solid 2px #888;
  }
  
  #errorMain {
    font-weight: bold;
    font-size: 150%;
  }
  
  #errorFriendly {
    font-weight: bold;
    font-size: 130%;
    color: #CC0000;
  }
  
  #stackTrace {
    text-align: left;
    font-size: 11px;
  }
  
</style>

<script type="text/javascript" src="<c:url value="/JavaScript/prototype.js"/>"></script>
<script type="text/javascript">
function showStack( chkBox ) {
	var elem = $('stackTrace');
	elem.style.display = 'block';
    $('showMore').style.display = 'none';

}
</script>

</head>

<body>
<div id="error">
<div id="errorImg"><a href="<%=homeUrl%>"><img style="border:none;" src="<%=request.getContextPath()%><%= logo %>"></a></div>
<% if (friendlyExceptionMessage != null && showStack) { %>
<br/>
<div id="errorFriendly"><%=friendlyExceptionMessage %></div>
<br/>
<% } else{%>
<div id="errorMain">An error occurred while processing your request</div>
<% }%>
<% if (errorKey == null) { %>
<div id="errorSub">Try to execute your request again.</div>
<% } else { %>
<div id="errorSub">Try to execute your request again (<%=errorKey %>).</div>
<% } %>
<% if (showStack) { %>
<div id="showMore"><a href="javascript:showStack()">Detailed information</a></div>
<div style="display: none" id="stackTrace">
    <b>Yukon Version:</b> <%= VersionTools.getYUKON_VERSION()%>
    <br><b>Yukon Version Details:</b> <%= VersionTools.getYukonDetails() %>
    <br><b>Status code:</b> <%= status_code.toString()%>
    <br><b>Message</b>: <%= message.toString()%>
    <br><b>Error type</b>: <%= error_type.toString()%>
    <br><b>Request URI</b>: <%= request_uri.toString()%>      
    <%= ServletUtil.printNiceHtmlStackTrace(throwable)%>
</div>
<% } %>
</body>

</html>
