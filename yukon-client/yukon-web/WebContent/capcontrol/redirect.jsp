
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<script type="text/javascript">
window.onload  = redirectOnTO;   

function redirectOnTO () {
setTimeout ("cant_access_page()", 5000);
}

function cant_access_page () {
<%if ( request.getParameter("redirectUrl")!= null) 
{%>window.location = "<%=request.getParameter("redirectUrl")%>" ;
<%}else{%>

window.location ="/dashboard";
<%}%>
}
</script>

<html>
<title> Redirect</title>
<body>
<h2>
<%
if ( request.getParameter("reason") != null )
	out.print(request.getParameter("reason"));
else
	out.print ("<font color='red'> Reason for redirect wasn't provided. </font>");
%> 
Redirecting....*** 
</h2>
<%
if ( request.getParameter("message") != null)
	out.print(request.getParameter("message"));
%>
</body>
</html>
