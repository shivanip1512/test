<%@ page import="com.cannontech.roles.yukon.SystemRole" %>
<%@ page import="com.cannontech.database.cache.functions.RoleFuncs" %>

<%
String logo = 
	RoleFuncs.getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="WebConfig/yukon/esubBGStyle.css" type="text/css">

<SCRIPT LANGUAGE="JavaScript">
function popUp(url) {
sealWin=window.open(url,"win",'toolbar=0,location=0,directories=0,status=1,menubar=1,scrollbars=1,resizable=1,width=500,height=450');
self.name = "mainWin";
}
</SCRIPT>
</head>

<body class="BGNoRepeat" text="#000000" leftmargin="0" topmargin="0" onLoad="document.forms.form1.USERNAME.focus()">
<div align="center"><br>
  <img src="esub/images/esubHeader.gif" width="347" height="30"> <br>
</div>
<table width="450" border="1" align="center" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr> 
    <td valign = "top" class = "MainText"> 
      <div align="center"><img src="<%= logo %>"><br>
        <%
	String errorMsg = "";
	if(request.getParameter("failed") != null) 
		errorMsg = "* Invalid Username/Password";
%>
      </div>
      <div align="center">
        <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\"> " + errorMsg + "</span><br>"); %>
      </div>
      <table width="367"  height="186" border="0" align="center" cellpadding="0" cellspacing="0" class = "MainText">
        <tr> 
          <td rowspan = "3" width="555" bgcolor="#FFFFFF" height="102" valign="top" ><br>
          
                <center>Please perform the one-time install of the svg viewer by clicking on the logo below.</center><br>
              <div align="center"><a href="http://www.adobe.com/svg/viewer/install/"><img src="esub/images/svgdownload.gif" width="88" height="31" border="0"></a></div>
              <br>
              <table width="400">
                <tr>
                  <td>
                    <hr></td></tr></table>
              <br>
            <div align="center"><b>SIGN IN</b><br>
              Please enter your username and password below.</div>
            <form name="form1" method="post" action="/servlet/LoginController">
              <input type="hidden" name="ACTION" value="LOGIN">
              <table width="250" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="83" class = "MainText"> 
                    <div align="right"> User Name</div>
                  </td>
                  <td width="117" valign="bottom"> 
                    <input type="text" name="USERNAME">
                  </td>
                </tr>
                <tr> 
                  <td width="83" class = "MainText"> 
                    <div align="right">Password:</div>
                  </td>
                  <td width="117"> 
                    <input type="password" name="PASSWORD">
                  </td>
                </tr>
                <tr> 
                  <td width="83">&nbsp;</td>
                  <td width="117"> 
                    <div align="left"> 
                      <input type="submit" name="Submit2" value="Submit">
                    </div>
                  </td>
                </tr>
              </table>
              <div align="center">
                <br>
              <br>
              <br>
                If you need help or have forgotten your password, click <a href="<%=request.getContextPath()%>/pwordreq.jsp">here</a>.
                </center>
            </form>
          </td>
        </tr>
      </table>
      <div align="center"> </div>
    </td>
  </tr>
</table>
<br>
<div align="center" class="TableCell1"><img src="esub/images/YukonLogoWhite.gif" width="132" height="28"></div>
</body>

</html>
