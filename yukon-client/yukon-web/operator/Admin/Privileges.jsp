<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="AdminImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" class="PageHeader">&nbsp;&nbsp;&nbsp;Administration</td>
                <td width="235" height = "28" valign="middle">&nbsp;</td>
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <br>
              <span class="TitleHeader">CUSTOMER PRIVILEGES</span><br>
            </div>
              
            <p align="center" class="MainText">Select which of the following functionality customers will have access to:</p>
              <p>
              </p>
            <table width="500" border="0" cellspacing="0" cellpadding="3" align="center">
              <tr> 
                <td width="150" class="TableCell"> 
                  <div align="right"> 
                    <input type="checkbox" name="checkbox" value="checkbox">
                  </div>
                </td>
                <td width="338" class="TableCell">Opt out of programs permanently</td>
              </tr>
              <tr> 
                <td width="150" class="TableCell"> 
                  <div align="right"> 
                    <input type="checkbox" name="checkbox2" value="checkbox">
                  </div>
                </td>
                <td width="338" class="TableCell">Opt out of all programs temporarily</td>
              </tr>
              <tr> 
                <td width="150" class="TableCell"> 
                  <div align="right"> 
                    <input type="checkbox" name="checkbox3" value="checkbox">
                  </div>
                </td>
                <td width="338" class="TableCell">Sign up</td>
              </tr>
              <tr> 
                <td width="150" class="TableCell"> 
                  <div align="right"> 
                    <input type="checkbox" name="checkbox4" value="checkbox">
                  </div>
                </td>
                <td width="338" class="TableCell">Change programs</td>
              </tr>
              <tr>
                <td width="150" class="TableCell"> 
                  <div align="right">
                    <input type="checkbox" name="checkbox42" value="checkbox">
                  </div>
                </td>
                <td width="338" class="TableCell">Self Directed Control</td>
              </tr>
            </table>
              <br>
              <table width="400" border="0" cellspacing="0" cellpadding="5" align="center" bgcolor="#FFFFFF">
                <tr>
				<form name="form1" method="post" action="Privileges.jsp"> 
                  <td width="186"> 
                      <div align="right"> 
                        <input type="submit" name="" value="Submit">
                      </div>
                  </td>
				  </form>
                  <form name="form1" method="get" action="">
                  <td width="194"> 
                      <div align="left"> 
                        <input type="reset" name="Cancel" value="Cancel">
                      </div>
                  </td>
				  </form>
                </tr>
              </table>
              <p>&nbsp;</p></td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
