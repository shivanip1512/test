<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
<!--
function MM_popupMsg(msg) { //v1.0
  alert(msg);
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
          <td width="102" height="102" background="../../../WebConfig/<cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_IMG_CORNER %>"/>">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height="28">&nbsp;</td>
				  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                  </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>

		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
              <div align="center"> 
                <br>
                
              <div align="center"> 
                <table width="600" border="0" cellspacing="0">
                  <tr> 
                    <td width="202"> 
                      <table width="200" border="0" cellspacing="0" cellpadding="3">
                        <tr> 
                        <td><span class="TitleHeader">Acct #<%= account.getAccountNumber() %></span><br>
                          <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                          <!--<%= account.getCompany() %><br> -->
                          <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                          <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                          <%= primContact.getHomePhone() %></span></td>
                        </tr>
                      </table>
                    </td>
                    <td width="187" valign="top"> 
                      <div align="center"><span class="TitleHeader"> CHANGE PROGRAM </span></div>
                    </td>
                    <td valign="top" width="205" align = "right">
                      <%@ include file="include/Notice.jsp" %>
                    </td>
                  </tr>
                </table>
                <table width="600" border="0" cellpadding="0" cellspacing="0">
                  <tr> 
                    <td> 
                      <hr>
                    </td>
                  </tr>
                </table>
                <p class="MainText">Please complete the following form to change your program:</p>
                <form method="post" action="Enrollment.jsp">
                  <table width="500" border="0" cellspacing="0" cellpadding="3" valign="top">
                    <tr> 
                      <td class="TableCell"> 
                        <p>* Reason for changing your program:</p>
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
                      <td class="TableCell"> Would you be interested in other programs. If so, which ones:</td>
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
                  <p align="center"> 
                  <input type="submit" name="Submit" value="Submit" >
                  <input type="button" name="Input" value="Cancel">
                </form>
                <p><span class="TableCell">
                  * This field must be completed.</span></p>
                <p>&nbsp;</p>
              </div>
            </div>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<div align="center"></div>
</body>
</html>
