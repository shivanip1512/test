<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.cannontech.core.dao.DaoFactory" %>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.roles.yukon.SystemRole"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="org.apache.commons.lang.ObjectUtils"%>
<%@page import="com.cannontech.common.util.CtiUtilities"%>
<jsp:directive.page import="com.cannontech.common.version.VersionTools"/>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page isErrorPage="true" %>

<%
String logo = "/" +
	DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );

Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
// if the above returned null, this page was probably called via the JSP exception handler
// because this page is declared as an error page, the exception object will be populated
throwable = (Throwable)ObjectUtils.defaultIfNull(throwable, exception);
Throwable root = CtiUtilities.getRootCause(throwable);

Object status_code = request.getAttribute("javax.servlet.error.status_code");
status_code = ObjectUtils.defaultIfNull(status_code, "no status code");
Object message = root.getMessage();
message = ObjectUtils.defaultIfNull(message, "no message");
Object error_type = request.getAttribute("javax.servlet.error.exception_type");
error_type = ObjectUtils.defaultIfNull(error_type, "no error type");
Object request_uri = request.getAttribute("javax.servlet.error.request_uri");
request_uri = ObjectUtils.defaultIfNull(request_uri, "no request uri");

StringWriter sw = new StringWriter();
PrintWriter p = new PrintWriter(sw);
p.write(
	"<b>Yukon Version:</b> " + VersionTools.getYUKON_VERSION() +
	"<br><b>Status code:</b> " + status_code.toString() +
	"<br><b>Message</b>: " + message.toString() +
	"<br><b>Error type</b>: " + error_type.toString() +
	"<br><b>Request URI</b>: " + request_uri.toString());		
    ServletUtil.printNiceHtmlStackTrace(throwable, p);
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
<div id="errorImg"><img src="<%=request.getContextPath()%><%= logo %>"></div>
<div id="errorMain">An error occured while processing your request</div>
<div id="errorSub">Try to execute your request again.</div>
<div id="showMore"><a href="javascript:showStack()">Detailed information</a></div>
<div style="display: none" id="stackTrace">
    <%=sw.getBuffer().toString()%>
</div>
</body>

</html>
