<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.roles.yukon.SystemRole" %>


<%
	String logo = DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );
%>

<html>
	<head>
		<title>Energy Services Operations Center</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<link rel="stylesheet" href="WebConfig/yukon/styles/loginStyles.css" type="text/css">

		<script LANGUAGE="JavaScript">
			function popUp(url) {
				sealWin=window.open(url,"win",'toolbar=0,location=0,directories=0,status=1,menubar=1,scrollbars=1,resizable=1,width=500,height=450');
				self.name = "mainWin";
			}
		</script>
	</head>

	<body onLoad="document.forms.form1.USERNAME.focus()">
	
		<div class="loginMain">
		
			<div class="loginTopSection">
				<div class="loginImage"><img src="<%= logo %>" /></div>
				<div class="loginTitleIntroText">Welcome to</div>
				<div class="loginTitleText">Energy Services Operations Center</div>
			</div>

			<div class="loginMainSection">
				<c:if test="${!empty param.failed}">
					<div class="loginErrorMsg">* Invalid Username/Password</div>
				</c:if>
				
				<div class="loginHeaderText">
					Sign In
				</div>
				<div class="loginIntroText">
					Please enter your username and password below.
				</div>
				
				<form name="form1" method="post" action="<c:url value="/servlet/LoginController"/>">
	            	<input type="hidden" name="ACTION" value="LOGIN">
	              	<table class="loginTable">
	                	<tr> 
		                  	<td align="right">User Name:</td>
		                  	<td align="left" valign="bottom"> 
		                    	<input type="text" id="USERNAME" name="USERNAME" class="loginTextInput">
		                  	</td>
	                	</tr>
	                	<tr> 
		                  	<td align="right">Password:</td>
		                  	<td align="left" valign="bottom"> 
	                    		<input type="password" name="PASSWORD">
	                  		</td>
	                	</tr>
	                	<tr> 
	                  		<td colspan="2" align="center"> 
	                      		<input type="submit" name="Submit2" value="Submit">
	                  		</td>
	                	</tr>
	              	</table>
	              	<div class="loginHelp">
	              		If you need help or have forgotten your password, click <a href="<c:url value="/pwordreq.jsp"/>">here</a>.
	              	</div>
				</form>
			</div>
		</div>
		
		<div class="loginCopyright">
			<%@ include file="include/full_copyright.jsp" %>
		</div>
		
	</body>
</html>
