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
	"<B>Status code:</B> " + status_code.toString() +
	"<BR><B>Message</B>: " + message.toString() +
	"<BR><B>Error type</B>: " + error_type.toString() +
	"<BR><B>Request URI</B>: " + request_uri.toString() +
	"<HR><PRE>");		

if( throwable != null )
	throwable.printStackTrace( p );
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
	var rows = elem.getElementsByTagName('tr');

	for( i = 0; i < rows.length; i++ ) {
		rows[i].style.display = 
			(chkBox.checked ? 'inline' : 'none');
	}
}
//-->
</script>

</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<br>
<table width="60%" border="1" align="center" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr> 
    <td align = "center" height="51" valign = "bottom"><img src="<%= logo %>">
      <table width="100%" border="0" height="43">
        <tr> 
          <td align = "center"><span class="TableCell"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#999999"><b><font face="Arial, Helvetica, sans-serif" color="#666666" size="4">
          An error occured while processing your request</font></b></font> </span></td>
        </tr>
        
		<tr> 
		    <td align = "center"><span class="TableCell"><b><font size="4">
		    Try to execute your request again.
			</td>
		</tr>
        
      </table>
      
    </td>
  </tr>

	<tr><td>
		<input type="checkbox" id="stTraceChk" onclick="showElem( 'stackTrace', this );"/>
		<B>Detailed information</B>
	</td></tr>

<a id="stackTrace">
  <tr style="display: none; overflow: scroll;">
    <td valign = "top" class="MainText">
    <%=sw.getBuffer().toString()%>
    </td>
  </tr>  
</a>

</table>
<br>
<div align="center" class="TableCell1">
  <img src="/YukonLogo.gif" width="139" height="29">
</div>
</body>

</html>
