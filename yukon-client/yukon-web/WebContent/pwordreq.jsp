<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="com.cannontech.common.version.VersionTools" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Energy Services Operations Center</title>           

        
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css" >
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/YukonGeneralStyles.css" >
<link rel="stylesheet" href="WebConfig/yukon/styles/loginStyles.css" type="text/css" />
<script type="text/javascript" src="/JavaScript/prototype.js" ></script>
<script type="text/javascript" src="/JavaScript/CtiMenu.js" ></script>

    </head>

	<script type='text/javascript' language=''>
		<!--
		var ctiMenu = new CtiMenu('subMenu');
		// -->
	</script>
	
	<script LANGUAGE="JavaScript">
		function popUp(url) {
			sealWin=window.open(url,"win",'toolbar=0,location=0,directories=0,status=1,menubar=1,scrollbars=1,resizable=1,width=500,height=450');
			self.name = "mainWin";
		}
	</script>
	
<%
	boolean starsExists = VersionTools.starsExists();
%>

	<body class="blank_module" onLoad="document.forms.form1.USERNAME.focus()">
		
		<div id="Header">
		    <div class="stdhdr_left"><div id="TopLeftLogo"></div><div id="TopLeftLogo2"></div></div>
		    <div class="stdhdr_right"><div id="TopRightLogo"></div></div>
		    <div class="stdhdr_clear"></div>
		</div>
		<div id='Menu'>
	
			<div id='topMenu'>
				<div>
					<div class='stdhdr_leftSide'></div>
					<div class='stdhdr_rightSide'></div>
					<div style='clear: both'></div>
				</div>
			</div>
			<div id='bottomBar'>
				<div>
					<div class='stdhdr_leftSide'></div>
					<div style='clear: both'></div>
				</div>
			</div>
		</div>
	
		<div class="loginMain">
		
			<div class="loginTopSection">
				<div class="loginTitleIntroText">Welcome to</div>
				<div class="loginTitleText">Customer Help Center</div>
			</div>
	
			<div class="loginMainSection">
				<cti:titledContainer title="Account Information">		
					<c:if test="${!empty param.failedMsg}">
						<div class="loginErrorMsg">* ${param.failedMsg}</div>
					</c:if>
					<c:if test="${!empty param.success}">
						<div class="loginSuccessMsg">Password request has been sent successfully</div>
					</c:if>
					
					<div class="loginIntroText">
						Please enter any or all known information about your account.
					</div>
					
					<form name="form1" method="post" action=
	<%	
		if( starsExists ) { 
	%>
						"<c:url value="/servlet/StarsPWordRequest"/>">
	<%	
		} else { 
	%>
						"<c:url value="/servlet/PWordRequest"/>">
	<% 
		}
	%>
	              		<table width="290" border="0" cellspacing="0" cellpadding="3" align="center">
	                		<tr> 
	                  			<td align="right">Username:</td>
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
				</cti:titledContainer>
	        </div>
        </div>
        
	</body>
</html>
