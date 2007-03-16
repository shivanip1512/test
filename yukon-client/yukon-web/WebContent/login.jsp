<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Energy Services Operations Center</title>           

        
<!-- Layout CSS files -->
<link rel="stylesheet" type="text/css" href="/WebConfig/yukon/styles/StandardStyles.css" >
<link rel="stylesheet" href="WebConfig/yukon/styles/loginStyles.css" type="text/css" />

<!-- Individual files from includeCss tag on the request page -->
<!--  (none)  -->

<!-- Energy Company specific style sheets (EnergyCompanyRole)-->

<!-- Consolidated Script Files -->
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
				<div class="loginTitleText">Energy Services Operations Center</div>
			</div>

			<div class="loginMainSection">
				<cti:titledContainer title="Login">
					<c:if test="${!empty param.failed}">
						<div class="loginErrorMsg">* Invalid Username/Password</div>
					</c:if>
					
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
					
				</cti:titledContainer>
			</div>
		</div>
		
		<div class="loginCopyright">
			<%@ include file="include/full_copyright.jsp" %>
		</div>
		
	</body>
</html>
