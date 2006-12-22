<%@page import="com.cannontech.core.dao.DaoFactory" %>
<%@page import="com.cannontech.util.ServletUtil"%>
<%@page import="com.cannontech.roles.yukon.SystemRole"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="org.apache.commons.lang.ObjectUtils"%>
<%@page import="com.cannontech.common.util.CtiUtilities"%>
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
	"<b>Status code:</b> " + status_code.toString() +
	"<br><b>Message</b>: " + message.toString() +
	"<br><b>Error type</b>: " + error_type.toString() +
	"<br><b>Request URI</b>: " + request_uri.toString() +
	"<hr>");		
    ServletUtil.printNiceHtmlStackTrace(throwable, p);
%>

<html>
<head>
<title>Yukon Error Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="<c:url value="/WebConfig/yukon/CannonStyle.css"/>" type="text/css">

<script type="text/javascript" src="<c:url value="/JavaScript/prototype.js"/>"></script>
<script type="text/javascript">
<!--
function showElem( chkBox ) {
	var elem = $('stackTrace');
	elem.style.display = (chkBox.checked ? 'block' : 'none')

}
Event.observe(window, 'load', function(){showElem($('stTraceChk'));});
//-->
</script>

</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<br>
<table width="60%" border="1" align="center" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr> 
    <td align = "center" height="51" valign = "bottom"><img src="<%=request.getContextPath()%><%= logo %>">
      <table width="100%" border="0" height="43">
        <tr> 
          <td align = "center"><span class="TableCell"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#999999"><b><font face="Arial, Helvetica, sans-serif" color="#666666" size="4">
          An error occured while processing your request</font></b></font> </span></td>
        </tr>
        
		<tr> 
		    <td align = "center"><span class="TableCell"><b><font size="4">
		    Try to execute your request again.</font></b></span>
			</td>
		</tr>
        
      </table>
      
    </td>
  </tr>

	<tr><td>
		<label><input type="checkbox" id="stTraceChk" onclick="showElem( this );"/>
		<b>Detailed information</b></label>
	</td></tr>

  <tr>
    <td valign = "top" class="MainText">
    <div style="display: none" id="stackTrace">
    <%=sw.getBuffer().toString()%>
    </div>
    </td>
  </tr>  


</table>
<br>
<div align="center" class="TableCell1">
  <img src="<%=request.getContextPath()%>/YukonLogo.gif" width="139" height="29">
</div>
</body>

</html>
