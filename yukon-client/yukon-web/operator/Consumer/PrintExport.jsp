<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
 
function goBack() {
document.location = "Update.jsp";
} 
 
 function changeAction() {
 	for (var i =0; i<document.MForm.radiobutton.length; i++) {
		if (document.MForm.radiobutton[i].checked)
 			document.MForm.action = document.MForm.radiobutton[i].value;
			}
}
 
 
  //End hiding script -->
  </SCRIPT>
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
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
          <td width="1" height="102" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
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
          <td  valign="top" width="101"><% String pageName = "PrintExport.jsp"; %><%@ include file="include/Nav.jsp" %> </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "PRINT/EXPORT"; %><%@ include file="include/InfoSearchBar.jsp" %>
              <form name ="MForm" action = "">
                <table width="53%" border="1" height="189" cellspacing = "0">
                  <tr bgcolor="#CCCCCC"> 
                    <td width="50%" colspan = "2" align = "center"> 
                      <table width="55%" border="0" class = "MainText">
                        <tr> 
                          <td width="25%" align = "right">PRINT</td>
                          <td width="25%" > 
                            <input type="radio" name="radiobutton" value="radiobutton" checked>
                          </td>
                          <td width="25%" align = "right">EXPORT</td>
                          <td width="25%"> 
                            <input type="radio" name="radiobutton" value="radiobutton">
                          </td>
                        </tr>
                      </table>
                      
                    </td>
                  </tr>
                  <tr bgcolor="#FFFFFF"> 
                    <td width="50%" colspan = "2" align = "center"><span class = "TableCell">Select 
                      the following item(s) to print/export:</span> </td>
                  </tr>
                  <tr> 
                    <td width="50%"> 
                      <table width="99%" border="0" class = "TableCell">
                        <tr> 
                          <td colspan = "2" align = "center"><i><b>Account</b></i> 
                          </td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox" value="checkbox">
                          </td>
                          <td width="65%">General</td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox2" value="checkbox">
                          </td>
                          <td width="65%">Contacts</td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox3" value="checkbox">
                          </td>
                          <td width="65%">Call Tracking</td>
                        </tr>
                      </table>
                    </td>
                    <td width="50%"> 
                      <table width="99%" border="0" class = "TableCell">
                        <tr> 
                          <td colspan = "2" align = "center"><i><b>Programs</b></i> 
                          </td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox4" value="checkbox">
                          </td>
                          <td width="65%">Control History</td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox5" value="checkbox">
                          </td>
                          <td width="65%">Enrollment</td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox6" value="checkbox">
                          </td>
                          <td width="65%">Opt Out</td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr> 
                    <td width="50%" height="49"> 
                      <table width="99%" border="0" class = "TableCell">
                        <tr> 
                          <td colspan = "2" align = "center"><i><b>Metering</b></i> 
                          </td>
                        </tr>
                        <tr> 
                          <td align="right" width="35%"> 
                            <input type="checkbox" name="checkbox7" value="checkbox" >
                          </td>
                          <td width="65%">Interval Data</td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%" height="19"> 
                            <input type="checkbox" name="checkbox8" value="checkbox">
                          </td>
                          <td width="65%" height="19">Usage</td>
                        </tr>
                      </table>
                    </td>
                    <td width="50%" height="49"> 
                      <table width="99%" border="0" class = "TableCell">
                        <tr> 
                          <td colspan = "2" align = "center"><i><b>Work Orders</b></i> 
                          </td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox9" value="checkbox">
                          </td>
                          <td width="65%">Service Request</td>
                        </tr>
                        <tr> 
                          <td align = "right" width="35%"> 
                            <input type="checkbox" name="checkbox10" value="checkbox">
                          </td>
                          <td width="65%">Service History</td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr class = "TableCell"> 
                    <td width="50%"> 
                      <table width="99%" border="0" class = "TableCell">
                        <tr> 
                          <td align="right" width="35%"> 
                            <input type="checkbox" name="checkbox72" value="checkbox" >
                          </td>
                          <td width="65%"><i><b>Appliances</b></i></td>
                        </tr>
                      </table>
                    </td>
                    <td width="50%"> 
                      <table width="99%" border="0" class = "TableCell">
                        <tr> 
                          <td align="right" width="35%"> 
                            <input type="checkbox" name="checkbox722" value="checkbox" >
                          </td>
                          <td width="65%"><i><b>Hardware</b></i></td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="150" border="0">
                  <tr > 
                    <td align = "center" > 
                      <input type="submit" name="Submit" value="    OK    ">
                    </td>
                    <td> 
                      <input type="button" name="Submit2" value="Cancel" onClick = "">
                    </td>
                  </tr>
                </table>
                <p>&nbsp;</p>
                <br>
              </form>
            </div>
            <p align="center">&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
