<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="com.cannontech.common.version.VersionTools" %>
<cti:verifyGlobalRolesAndProperties value="ENABLE_PASSWORD_RECOVERY"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title><cti:msg key="yukon.web.forgotPassword.pageTitle"/></title>           

         <link rel="stylesheet" type="text/css"
            href="/WebConfig/yukon/styles/StandardStyles.css">
        <link rel="stylesheet" type="text/css"
            href="/WebConfig/yukon/styles/YukonGeneralStyles.css">
        <link rel="stylesheet"
            href="/WebConfig/yukon/styles/loginStyles.css"
            type="text/css">
        
       
        <cti:includeScript link="PROTOTYPE" force="true"/>
        <script type="text/javascript" src="/JavaScript/CtiMenu.js"></script>

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
				<div class="loginTitleText"><cti:msg key="yukon.web.forgotPassword.pageTitle"/></div>
			</div>
	
			<div class="loginMainSection">
                <cti:msg var="boxTitle" key="yukon.web.forgotPassword.boxTitle"/>
				<cti:titledContainer title="${boxTitle}">		
					<c:if test="${!empty param.failedMsg}">
						<div class="loginErrorMsg"><cti:msg key="yukon.web.forgotPassword.${param.failedMsg}"/></div>
					</c:if>
					<c:if test="${!empty param.success}">
						<div class="loginSuccessMsg"><cti:msg key="yukon.web.forgotPassword.success"/></div>
					</c:if>
					
					<div class="loginIntroText">
                        <cti:msg key="yukon.web.forgotPassword.intro"/>
					</div>
					
					<form name="form1" method="post" action=
	<%	
		if( starsExists ) { 
	%>
						"<cti:url value="/servlet/StarsPWordRequest"/>">
	<%	
		} else { 
	%>
						"<cti:url value="/servlet/PWordRequest"/>">
	<% 
		}
	%>
	              		<table width="290" border="0" cellspacing="0" cellpadding="3" align="center">
	                		<tr> 
	                  			<td align="right"><cti:msg key="yukon.web.forgotPassword.username"/></td>
	                  			<td align="left" valign="bottom"> 
	                    			<input type="text" autocomplete="off" id="USERNAME" name="USERNAME" size="26">
	                  			</td>
	                		</tr>
	                		<tr> 
				                <td align="right"><cti:msg key="yukon.web.forgotPassword.email"/></td>
				                <td align="left" valign="bottom"> 
	                    			<input type="text" name="EMAIL" size="26">
	                  		</td>
	                		</tr>
	                		<tr> 
	                  			<td align="right"><cti:msg key="yukon.web.forgotPassword.firstName"/></td>
	                  			<td align="left" valign="bottom"> 
	                    			<input type="text" name="FIRST_NAME" size="26">
	                  			</td>
	                		</tr>
	                		<tr> 
	                  			<td align="right"><cti:msg key="yukon.web.forgotPassword.lastName"/></td>
	                  			<td align="left" valign="bottom"> 
	                    			<input type="text" name="LAST_NAME" size="26">
	                  			</td>
	                		</tr>
	<%
		if( starsExists ) {
	%>
	                		<tr> 
	                  			<td align="right"><cti:msg key="yukon.web.forgotPassword.account"/></td>
	                  			<td align="left" valign="bottom"> 
	                    			<input type="text" name="ACCOUNT_NUM" size="26">
	                  			</td>
	                		</tr>
	<%	
		} 
	%>
	
							<tr> 
	                  			<td align="right"><cti:msg key="yukon.web.forgotPassword.provider"/></td>
	                  			<td align="left" valign="middle"> 
				                    <input type="text" name="ENERGY_COMPANY" size="26">
	            		      	</td>
	                		</tr>
	                		<tr> 
	                  			<td align="right"><cti:msg key="yukon.web.forgotPassword.notes"/></td>
	                  			<td align="left" valign="bottom">
	                    			<textarea name="NOTES" cols="20" rows="5"></textarea>
	                  			</td>
	                		</tr>
	                		<tr> 
	                  			<td colspan="2" align="center"> 
                                    <cti:msg var="submitText" key="yukon.web.forgotPassword.submit"/>
	                      			<input type="submit" name="Submit2" value="${submitText}">
	                  			</td>
	                		</tr>
	              		</table>
					</form>
				</cti:titledContainer>
	        </div>
        </div>
        
	</body>
</html>
