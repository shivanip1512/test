<%@page import="com.cannontech.database.cache.functions.RoleFuncs"%>
<%@page import="com.cannontech.roles.yukon.SystemRole"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>

<%
String logo = "/" +
	RoleFuncs.getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );


Object status_code = request.getAttribute("javax.servlet.error.status_code");
Object message = request.getAttribute("javax.servlet.error.message");
Object error_type = request.getAttribute("javax.servlet.error.exception_type");
Throwable throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
Object request_uri = request.getAttribute("javax.servlet.error.request_uri");

StringWriter sw = new StringWriter();
PrintWriter p = new PrintWriter(sw);
p.write(
	"<b>Status code:</b> " + status_code.toString() +
	"<br><B>Message</b>: " + message.toString() +
	"<br><B>Error type</b>: " + error_type.toString() +
	"<br><B>Request URI</b>: " + request_uri.toString() +
	"<hr><pre>");		

if( throwable != null ) {
	throwable.printStackTrace( p );
}
p.write("</pre>");
%>

<html>
<head>
<title>Yukon Error Page</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="/WebConfig/yukon/CannonStyle.css" type="text/css">

<script type="text/javascript">
<!--
function showElem( elemId, chkBox ) {
	var elem = document.getElementById(elemId);
	elem.style.display = (chkBox.checked ? 'block' : 'none')

}
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
		<label><input type="checkbox" id="stTraceChk" onclick="showElem( 'stackTrace', this );"/>
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
