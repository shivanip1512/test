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
      <%@ include file="include/HeaderBar.jsp" %>
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
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
			  
<%	if (!isStars) { %>
			  <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager" enctype="multipart/form-data">
                <input type="hidden" name="action" value="ImportCustAccounts">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportAccount.jsp">
                <table width="500" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText"> 
                      <div align="center">Enter the import file(s). Leave the 
                        box empty if you don't have the corresponding file.</div>
                    </td>
                  </tr>
                </table>
                <br>
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
                      <div align="right">Hardware File: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="HwFile" size="35">
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center"> 
                      <input type="submit" name="Submit" value="Submit">
                    </td>
                  </tr>
                </table>
              </form>
<%	} else { // importID="STARS" %>
			  <form name="form2" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
                <input type="hidden" name="action" value="ImportINIData">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportAccount.jsp">
                <table width="600" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText"> 
                      <div align="center">Import the INI files before importing 
                        any other file. The S3DATA.INI file only needs to be imported 
                        once. The STARS3.INI file must be re-imported every time 
                        your session times out or the web server restarts.</div>
                    </td>
                  </tr>
                </table>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="150"> 
                      <div align="right">S3DATA.INI: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="SelListFile" size="35">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">STARS3.INI: </div>
                    </td>
                    <td width="250"> 
                      <input type="file" name="UsrLabelFile" size="35">
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
			  </form>
			  <form name="form3" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
                <input type="hidden" name="action" value="PreprocessStarsData">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportAccount2.jsp">
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
<%	} %>
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
