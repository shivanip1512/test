<html>
<head>
<title>Consumer Energy Services</title>
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
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Buyback</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="Main"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
          <% String pageName = "user_ee_profile.jsp"; %>
          <%@ include file="nav.jsp" %> </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
		    <td width="657" valign="top" bgcolor="#FFFFFF">
              <table width="657" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                <td width="650" class="Main" valign="top"> 
                  
                    <struts:form name="checker" type="com.cannontech.validate.PageBean" scope="session" action="user_ee.jsp?tab=accept"></struts:form>
                  <center>
                      <table width="650" border="0" cellspacing="0" cellpadding="0">
                        <tr>
                        <td width="605" class="Main" > 
                          <p align="center"><b><br>
                            ADMINISTRATION - CUSTOMER PROFILE</b><br>
                            There must be a Primary Contact. Any number of additional 
                            contacts may also be included. 
                        </td>
                          
                        <td width="45"> 
                          <input type="submit" name="Add" value="Add">
                        </td>
                        </tr>
                      </table>
                    <br>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr>
                        <td>
                          <hr>
                          <span class="Main"><b>CONTACT (PRIMARY)</b></span></td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td class="TableCell" width="60">First Name:</td>
                        <td class="TableCell" width="132"> 
                          <input type="text" name="textfield">
                        </td>
                        <td class="TableCell" width="69"> 
                          <div align="right">Last Name:</div>
                        </td>
                        <td class="TableCell" width="277"> 
                          <input type="text" name="textfield2">
                        </td>
                        <td class="TableCell" width="62"> 
                          <input type="submit" name="Delete" value="Delete">
                        </td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td width="31" class="TableCell">Email:</td>
                        <td width="90" class="TableCell"> 
                          <input type="text" name="textfield3" size="12">
                        </td>
                        <td width="71" class="TableCell"> 
                          <div align="right">Phone 1:</div>
                        </td>
                        <td width="88" class="TableCell"> 
                          <input type="text" name="textfield32" size="12">
                        </td>
                        <td width="67" class="TableCell"> 
                          <div align="right">Phone 2:</div>
                        </td>
                        <td width="75" class="TableCell"> 
                          <input type="text" name="textfield33" size="12">
                        </td>
                        <td width="61" class="TableCell"> 
                          <div align="right">Pager:</div>
                        </td>
                        <td width="85" class="TableCell"> 
                          <input type="text" name="textfield34" size="12">
                        </td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td class="TableCell" width="58">User Name:</td>
                        <td class="TableCell" width="144"> 
                          <input type="text" name="textfield4">
                        </td>
                        <td class="TableCell" width="61"> 
                          <div align="right">Password:</div>
                        </td>
                        <td class="TableCell" width="321"> 
                          <input type="text" name="textfield5">
                        </td>
                      </tr>
                    </table>
                    <br>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td> 
                          <hr>
                          <span class="Main"><b>CONTACT 2</b></span></td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td class="TableCell" width="60">First Name:</td>
                        <td class="TableCell" width="132"> 
                          <input type="text" name="textfield6">
                        </td>
                        <td class="TableCell" width="69"> 
                          <div align="right">Last Name:</div>
                        </td>
                        <td class="TableCell" width="277"> 
                          <input type="text" name="textfield22">
                        </td>
                        <td class="TableCell" width="62"> 
                          <input type="submit" name="Delete2" value="Delete">
                        </td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td width="31" class="TableCell">Email:</td>
                        <td width="90" class="TableCell"> 
                          <input type="text" name="textfield35" size="12">
                        </td>
                        <td width="71" class="TableCell"> 
                          <div align="right">Phone 1:</div>
                        </td>
                        <td width="88" class="TableCell"> 
                          <input type="text" name="textfield322" size="12">
                        </td>
                        <td width="67" class="TableCell"> 
                          <div align="right">Phone 2:</div>
                        </td>
                        <td width="75" class="TableCell"> 
                          <input type="text" name="textfield332" size="12">
                        </td>
                        <td width="61" class="TableCell"> 
                          <div align="right">Pager:</div>
                        </td>
                        <td width="85" class="TableCell"> 
                          <input type="text" name="textfield342" size="12">
                        </td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td class="TableCell" width="58">User Name:</td>
                        <td class="TableCell" width="144"> 
                          <input type="text" name="textfield42">
                        </td>
                        <td class="TableCell" width="61"> 
                          <div align="right">Password:</div>
                        </td>
                        <td class="TableCell" width="321"> 
                          <input type="text" name="textfield52">
                        </td>
                      </tr>
                    </table>
                    <br>
                    <table width="200" border="0" cellspacing="0" cellpadding="2">
                      <tr>
                        <td> 
                          <div align="right">
                            <input type="submit" name="Submit" value="Submit">
                          </div>
                        </td>
                        <td>
                          <input type="submit" name="Cancel" value="Cancel">
                        </td>
                      </tr>
                    </table>
                  <p>&nbsp;</p></center>


   </td>
  </tr>
</table>


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
