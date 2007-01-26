
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="com.cannontech.common.version.VersionTools" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.roles.yukon.SystemRole" %>


<%
	String logo =  DaoFactory.getRoleDao().getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );

	boolean starsExists = VersionTools.starsExists();
%>

<html>
	<head>
		<title>Energy Services Operations Center</title>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
		<link rel="stylesheet" href="WebConfig/yukon/styles/loginStyles.css" type="text/css">
	</head>

	<body onLoad="document.forms.form1.USERNAME.focus()">
	
		<div class="loginMain">
		
			<div class="loginTopSection">
				<div class="loginImage"><img src="<%= logo %>" /></div>
				<div class="loginTitleIntroText">Welcome to</div>
				<div class="loginTitleText">Customer Help Center</div>
			</div>
			
			<div class="loginMainSection">
				<c:if test="${!empty param.failedMsg}">
					<div class="loginErrorMsg">* ${param.failedMsg}</div>
				</c:if>
				<c:if test="${!empty param.success}">
					<div class="loginSuccessMsg">Password request has been sent successfully</div>
				</c:if>
				
				<div class="loginHeaderText">
					Account Information
				</div>
				<div class="loginIntroText">
					Please enter any or all known information about your account.
				</div>
				
				<form name="form1" method="post" action=
<%	
	if( starsExists ) { 
%>
					"<%=request.getContextPath()%>/servlet/StarsPWordRequest">
<%	
	} else { 
%>
					"<%=request.getContextPath()%>/servlet/PWordRequest">
<% 
	}
%>
              		<table width="290" border="0" cellspacing="0" cellpadding="3" align="center">
                		<tr> 
                  			<td align="right">User Name:</td>
                  			<td align="left" valign="bottom"> 
                    			<input type="text" id="USERNAME" name="USERNAME" size="26">
                  			</td>
                		</tr>
                		<tr> 
			                <td align="right">Email:</td>
			                <td align="left" valign="bottom"> 
                    			<input type="text" name="EMAIL" size="26">
                  		</td>
                		</tr>
                		<tr> 
                  			<td align="right">First Name:</td>
                  			<td align="left" valign="bottom"> 
                    			<input type="text" name="FIRST_NAME" size="26">
                  			</td>
                		</tr>
                		<tr> 
                  			<td align="right">Last Name:</td>
                  			<td align="left" valign="bottom"> 
                    			<input type="text" name="LAST_NAME" size="26">
                  			</td>
                		</tr>
<%
	if( starsExists ) {
%>
                		<tr> 
                  			<td align="right">Account #:</td>
                  			<td align="left" valign="bottom"> 
                    			<input type="text" name="ACCOUNT_NUM" size="26">
                  			</td>
                		</tr>
<%	
	} 
%>

						<tr> 
                  			<td align="right">Energy Provider:</td>
                  			<td align="left" valign="middle"> 
			                    <input type="text" name="ENERGY_COMPANY" size="26">
            		      	</td>
                		</tr>
                		<tr> 
                  			<td align="right">Your Notes:</td>
                  			<td align="left" valign="bottom">
                    			<textarea name="NOTES" cols="20" rows="5"></textarea>
                  			</td>
                		</tr>
                		<tr> 
                  			<td colspan="2" align="center"> 
                      			<input type="submit" name="Submit2" value="Submit">
                  			</td>
                		</tr>
              		</table>
				</form>
        	</div>
        </div>
        
	</body>
</html>
