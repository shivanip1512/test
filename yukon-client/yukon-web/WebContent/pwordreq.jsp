<%@ page import="com.cannontech.common.version.VersionTools" %>
<%@ page import="com.cannontech.database.cache.functions.RoleFuncs" %>
<%@ page import="com.cannontech.roles.yukon.SystemRole" %>


<%
String logo = 
	RoleFuncs.getGlobalPropertyValue( SystemRole.WEB_LOGO_URL );

boolean starsExists = VersionTools.starsExists();
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="WebConfig/yukon/CannonStyle.css" type="text/css">

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
          <td colspan = "2" align = "center"><span class="TableCell"><b><font size="4">Customer 
            Help Center</font></b></span> </td>
        </tr>
      </table>
      
    </td>
  </tr>
  <tr> 
    <td valign = "top" class = "MainText"> <br> 

<%
	String errorMsg = "";
	if(request.getParameter("failedMsg") != null) 
		errorMsg = "* " + request.getParameter("failedMsg");

	String succMsg = "";
	if(request.getParameter("success") != null) 
		succMsg = " Password request has been sent successfully";
%>
	  <div align="center">
	  	<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\"> " + errorMsg + "</span><br>"); %>
	  	<% if (succMsg != null) out.write("<span class=\"ConfirmMsg\"> " + succMsg + "</span><br>"); %>
	  </div>


      <table width="367"  height="186" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr> 
          <td rowspan = "3" width="555" height="102" valign="top" ><br>           
            <div align="center" class="TitleHeader">ACCOUNT INFORMATION<br>
            
            </div>
            <div align="center" class="MainText">Please enter any or all known 
              information about your account.</div>
            <form name="form1" method="post" action=
				<%	if( starsExists ) { %>
					"<%=request.getContextPath()%>/servlet/StarsPWordRequest">
				<%} else { %>
					"<%=request.getContextPath()%>/servlet/PWordRequest">
				<% } %>
              
              <table width="290" border="0" cellspacing="0" cellpadding="3" align="center">
                <tr> 
                  <td width="83" class = "MainText" align="right">User Name:</td>
                  <td width="117" valign="bottom" class="MainText"> 
                    <input type="text" name="USERNAME" size="26">
                  </td>
                </tr>
                <tr> 
                  <td width="83" class = "MainText" align="right">Email:</td>
                  <td width="117"> 
                    <input type="text" name="EMAIL" size="26">
                  </td>
                </tr>
                <tr> 
                  <td width="83" class = "MainText" align="right">First Name:</td>
                  <td width="117"> 
                    <input type="text" name="FIRST_NAME" size="26">
                  </td>
                </tr>
                <tr> 
                  <td width="83" class = "MainText" align="right">Last Name:</td>
                  <td width="117"> 
                    <input type="text" name="LAST_NAME" size="26">
                  </td>
                </tr>

<%	if( starsExists )
	{%>
                <tr> 
                  <td width="83" class = "MainText" align="right">Account #:</td>
                  <td width="117"> 
                    <input type="text" name="ACCOUNT_NUM" size="26">
                  </td>
                </tr>
<%	
	} 
%>

				<tr> 
                  <td width="83" class = "MainText" align="right">Energy Provider:</td>
                  <td width="117"> 
                    <input type="text" name="ENERGY_COMPANY" size="26">
                  </td>
                </tr>


                <tr> 
                  <td width="83" class = "MainText" align="right">Your Notes:</td>
                  <td width="117">
                    <textarea name="NOTES" cols="20" rows="5"></textarea>
                  </td>
                </tr>

                <tr> 
                  <td width="83" class = "MainText" align="right"></td>
                  <td width="117"> 
                    <div align="left"> 
                      <input type="submit" name="Submit2" value="Submit">
                    </div>
                  </td>
                </tr>

              </table>
			  
			  
              <div align="center" class="MainText">
                <br>
                <br>
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
<div align="center" class="TableCell1"><img src="YukonLogo.gif" width="139" height="29"></div>
</body>

</html>
