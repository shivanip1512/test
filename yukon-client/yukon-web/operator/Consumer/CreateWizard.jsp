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
                  <td width="265" height = "28" class="BlueHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer 
                    Account Information&nbsp;&nbsp;</td>
                  
                <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="blueLink">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="blueLink">Log Off</a>&nbsp;</span></div>
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
          <td  valign="top" width="101"><% String pageName = "CreateWizard.jsp"; %><%@ include file="include/Nav.jsp" %> </td>
          <td width="1" bgcolor="#000000"><img src=""../Images/Icons/VerticalRule.gif"" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "CREATE WIZARD"; %><%@ include file="include/InfoSearchBar.jsp" %>
              <form name ="MForm" action = "CreateContact.jsp">
              
			    
                <table width="45%" border="0" height="187">
                  <tr> 
                    <td colspan = "2"> <span class="TitleHeader">Acct #<%= account.getAccountNumber() %></span><br>
                      <span class="NavText"><%= primContact.getFirstName() %>&nbsp;<%= primContact.getLastName() %><br>
                      <!--<%= account.getCompany() %><br> -->
                      <%= primContact.getHomePhone() %></span><br>
                      <span class="NavText"><%= primContact.getWorkPhone() %></span> 
                      <hr>
                    </td>
                  </tr>
                  <tr> 
                    <td width="56%" align = "right"><span class="TableCell"> Contact(s)</span></td>
                    <td width="44%">
                      <input type="radio" name="radiobutton" value="CreateContact.jsp" onClick = "changeAction()" checked>
                      </td>
                  </tr>
                  <tr> 
                    <td width="56%" align = "right"><span class="TableCell"> Call 
                      Tracking</span></td>
                    <td width="44%"><span class="TableCell"> 
                      <input type="radio" name="radiobutton" value="CreateCalls.jsp" onClick = "changeAction()">
                      </span></td>
                  </tr>
                  <tr> 
                    <td width="56%" align = "right"><span class="TableCell" > 
                      Appliance</span></td>
                    <td width="44%"><span class="TableCell"> 
                      <input type="radio" name="radiobutton" value="CreateAppliances.jsp" onClick = "changeAction()">
                      </span></td>
                  </tr>
                  <tr> 
                    <td width="56%" align = "right"><span class="TableCell"> Hardware</span></td>
                    <td width="44%"><span class="NavText"> 
                      <input type="radio" name="radiobutton" value="CreateHardware.jsp" onClick = "changeAction()">
                      </span></td>
                  </tr>
                  <tr> 
                    <td width="56%" align = "right" class = "TableCell"><b>New 
                      Account </b> </td>
					  
                    <td><b>
                      <input type="radio" name="radiobutton" value="CreateAccount.jsp" onClick = "changeAction()">
                      </b></td>
                  </tr>
                </table><br>
                <table width="150" border="0">
                  <tr> 
                    <td align = "center" > 
                      <input type="submit" name="Submit" value="Create">
                    </td>
                    <td> 
                      <input type="button" name="Submit2" value="Cancel" onClick = "goBack()">
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
