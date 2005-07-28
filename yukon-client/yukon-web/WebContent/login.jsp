<%@ page import="com.cannontech.database.cache.functions.RoleFuncs" %>
<%@ page import="com.cannontech.roles.yukon.SystemRole" %>


<%
String logo = 
	RoleFuncs.getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="WebConfig/yukon/CannonStyle.css" type="text/css">

<SCRIPT LANGUAGE="JavaScript">
function popUp(url) {
sealWin=window.open(url,"win",'toolbar=0,location=0,directories=0,status=1,menubar=1,scrollbars=1,resizable=1,width=500,height=450');
self.name = "mainWin";
}
</SCRIPT>
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0" onLoad="document.forms.form1.USERNAME.focus()">
<br>
<table width="450" border="1" align="center" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
  <tr> 
    <td colspan = "2" align = "center" height="51" valign = "bottom"><img src="<%= logo %>"> 
      <table width="400" border="0" height="43">
        <tr> 
          <td width="34%" align = "center"><span class="TableCell"><font face="Verdana, Arial, Helvetica, sans-serif" size="3" color="#999999"><b><font face="Arial, Helvetica, sans-serif" color="#666666" size="4">Welcome 
            to</font></b></font> </span></td>
          <td width="66%">&nbsp;</td>
        </tr>
        <tr> 
          <td colspan = "2" align = "center"><span class="TableCell"><b><font size="4">Energy 
            Services Operations Center</font></b></span> </td>
        </tr>
      </table>
      
    </td>
  </tr>
  <tr> 
    <td valign = "top" class = "MainText"> <br> 
<%
	String errorMsg = "";
	if(request.getParameter("failed") != null) 
		errorMsg = "* Invalid Username/Password";
%>
	  <div align="center"><% if (errorMsg != null) out.write("<span class=\"ErrorMsg\"> " + errorMsg + "</span><br>"); %></div>
	
      <table width="367"  height="186" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr> 
          <td rowspan = "3" width="555" height="102" valign="top" ><br> 
            <div align="center" class="TitleHeader">SIGN IN<br></div>
            <div align="center" class="MainText">Please enter your username and password below.</div>
            <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/LoginController">
              <input type="hidden" name="ACTION" value="LOGIN">
              <table width="250" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="83" class = "MainText" align="right">User Name:</td>
                  <td width="117" valign="bottom" class="MainText"> 
                    <input type="text" name="USERNAME">
                  </td>
                </tr>
                <tr> 
                  <td width="83" class = "MainText" align="right">Password:</td>
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
              <div align="center" class="MainText"><br>
                If you need help or have forgotten your password, click <a href="<%=request.getContextPath()%>/pwordreq.jsp">here</a>.
                <br>
                <br>
<!--                <a href="javascript:popUp('https://digitalid.verisign.com/as2/68fabe0200effc6cf9cd5459ba6a1736')"> -->
                <!--<img src="http://www.verisign.com/images/seals/Secure-White98x102.gif" width="98" height="102" border="0">-->
<!--                <img src="Verisign.gif" border="0"></a><br>-->
              </div>
            </form>
</td>
        </tr>
        </table>
		<div align="center"> </div>      
    </td>
  </tr>
</table>
<br>
<div align="center" class="TableCell1">
  <img src="YukonLogo.gif" width="139" height="29">
  <p><font color="#FFFFFF">
	<%@ include file="include/full_copyright.jsp" %>
  </font></p>
</div>
</body>

</html>
