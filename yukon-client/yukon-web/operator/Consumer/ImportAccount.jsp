<%@ include file="include/StarsHeader.jsp" %>
<%
	String importID = AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT);
	if (importID == null || importID.equals("(none)")) {
		response.sendRedirect("../Operations.jsp");
		return;
	}
	
	boolean isStars = importID.equalsIgnoreCase("STARS");
%>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "IMPORT ACCOUNTS"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
			  
			  <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager" <% if (!isStars) { %>enctype="multipart/form-data"<% } %>>
                <input type="hidden" name="action" value="<%= (isStars)?"PreprocessStarsData":"PreprocessImportData" %>">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/<%= (isStars)?"ImportAccount2.jsp":"ImportAccount1.jsp" %>">
<%	if (!isStars) { %>
                <table width="500" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText"> 
                      <div align="center">Enter the import file name, then click 
                        &quot;Next&quot;. </div>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="150"> 
                      <div align="right">Import File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="ImportFile" size="35">
                    </td>
                  </tr>
                </table>
                <%	} else { // importID="STARS" %>
                <table width="500" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText"> 
                      <div align="center">If you want to import the selection 
                        lists, please do so before importing any other file:</div>
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="150"> 
                      <div align="right">Selection List File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="SelListFile" size="35">
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center">
                      <input type="submit" name="Submit2" value="Submit">
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="150"> 
                      <div align="right">Customer File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="CustFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Service Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="ServInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Inventory File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="InvFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Receiver File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="RecvrFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Meter File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="MeterFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Load Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="LoadInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">AC Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="ACInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">WH Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="WHInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Generator Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="GenInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Irrigation Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="IrrInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Grain Dryer Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="GDryInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Heat Pump Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="HPInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Storage Heat Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="SHInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Dual Fuel Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="DFInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">General Load Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="GenlInfoFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Work Order File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="WorkOrderFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Residence Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="ResInfoFile" size="35">
                    </td>
                  </tr>
                </table>
<%	} %>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td width="50%" align="right"> 
                      <input type="submit" name="Submit" value="Next">
                    </td>
                    <td width="50%"> 
                      <input type="reset" name="Cancel" value="Cancel">
                    </td>
                  </tr>
                </table>
              </form>
            </div>
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
