<%
	String programStatus = "In Service";     
		programStatus = (String) session.getAttribute("PROGRAM_STATUS");
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
<!--
function doReenable(form) {
	form.action.value = "EnableService";
	form.submit();
}

function MM_popupMsg(msg) { //v1.0
  return confirm(msg);
}
//-->
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">  
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101">
		  <% String pageName = "ChangeForm.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" align = "center"> 
      		  <% String header = "CHANGE PROGRAM"; %>
              <%@ include file="include/InfoSearchBar.jsp" %> 
          
            <p><span class="MainText">Please complete the following form to change the program:</span> </p>
            <hr width = "90%">
          
                <table width="500" border="0" cellspacing="0" cellpadding="3" valign="top">
                  <tr> 
                    <td class="TableCell"> 
                      
                  <p>Reason for changing the program:</p>
                    </td>
                  </tr>
                  <tr> 
                    <td> 
                      <div align="left"> 
                        <input type="text" name="textfield4" size="80">
                      </div>
                    </td>
                  </tr>
                  <tr> 
                    <td class="TableCell"> Satisfaction with the program:</td>
                  </tr>
                  <tr> 
                    <td> 
                      <div align="left"> 
                        <input type="text" name="textfield42" size="80">
                      </div>
                    </td>
                  </tr>
                  <tr> 
                    
                <td class="TableCell"> Would the customer be interested in other 
                  programs. If so, which ones:</td>
                  </tr>
                  <tr> 
                    <td> 
                      <div align="left"> 
                        <input type="text" name="textfield43" size="80">
                      </div>
                    </td>
                  </tr>
                  <tr> 
                    <td class="TableCell"> Comments:</td>
                  </tr>
                  <tr> 
                    <td> 
                      <div align="left"> 
                        <input type="text" name="textfield44" size="80">
                      </div>
                    </td>
                  </tr>
                </table>
                <form method="post" action="Programs.jsp">
              <p align="center"> 
                <input type="submit" name="Submit" value="Submit">
                <input type="Submit" name="Input" value="Cancel">
              <p>&nbsp;</p></form>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>  
<br>
</body>
</html>
