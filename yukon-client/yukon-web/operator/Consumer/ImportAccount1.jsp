<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany" %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
function changeCategory(checkBox, index) {
	var programs, catIDs, progIDs, defProgIDs;
	
	if (checkBox.checked) {
		programs = document.getElementsByName("Program" + index);
		if (programs.length > 0)
			programs[0].checked = true;
		catIDs = document.getElementsByName("CatID");
		progIDs = document.getElementsByName("ProgID");
		defProgIDs = document.getElementsByName("DefProgID");
		catIDs[index].value = checkBox.value;
		progIDs[index].value = defProgIDs[index].value;
	}
	else {
		programs = document.getElementsByName("Program" + index);
		for (i = 0; i < programs.length; i++)
			programs[i].checked = false;
		catIDs = document.getElementsByName("CatID");
		progIDs = document.getElementsByName("ProgID");
		catIDs[index].value = "";
		progIDs[index].value = "";
	}
}

function changeProgram(radioBtn, index) {
	var categories = document.getElementsByName("AppCat");
	var catIDs = document.getElementsByName("CatID");
	var progIDs = document.getElementsByName("ProgID");
	
	if (progIDs[index].value == radioBtn.value) return;	// Nothing is changed
	
	categories[index].checked = true;
	catIDs[index].value = categories[index].value;
	progIDs[index].value = radioBtn.value;
}
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "IMPORT ACCOUNTS - PROGRAM ENROLLMENT"; %>
              <%@ include file="include/InfoSearchBar2.jsp" %>
              <table width="500" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td class="MainText"> 
                    <div align="center">Select the programs to be enrolled in, 
                      then click &quot;Submit&quot; to start importing accounts.</div>
                  </td>
                </tr>
              </table>
            </div>
			
			<form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager">
			  <input type="hidden" name="action" value="ImportCustAccounts">
			  <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/ImportAccount.jsp">
              <table border="1" cellspacing="0" cellpadding="2" width="300" align="center">
                <tr> 
                  <td width="100" class="HeaderCell" align = "center">Description</td>
                  <td width="186" class="HeaderCell"> 
                    <div align="center">Program Enrollment</div>
                  </td>
                </tr>
                <%
	int numProgCat = 0;
	int numEnrolledProg = 0;
	
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		if (category.getStarsEnrLMProgramCount() == 0) continue;
%>
                <tr> 
                  <td width="100" align = "center"><img src="../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
                  </td>
                  <td width="186" align = "center"> 
                    <table width="110" border="0" cellspacing="0" cellpadding="1" align="center">
                      <input type="hidden" name="CatID">
                      <input type="hidden" name="ProgID">
                      <input type="hidden" name="DefProgID" value="<%= category.getStarsEnrLMProgram(0).getProgramID() %>">
                      <tr> 
                        <td width="23"> 
                          <input type="checkbox" name="AppCat" value="<%= category.getApplianceCategoryID() %>" onClick="changeCategory(this, <%= numProgCat %>)">
                        </td>
                        <td class="TableCell" nowrap><%= category.getStarsWebConfig().getAlternateDisplayName() %></td>
                      </tr>
                    </table>
                    <%
		if (category.getStarsEnrLMProgramCount() > 1) {
			/* If more than one program under this category, show the program list */
%>
                    <table width="110" border="0" cellspacing="0" cellpadding="0" align="center">
                      <%
			for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
				StarsEnrLMProgram prog = category.getStarsEnrLMProgram(j);
				/* Each row is a program in this category */
%>
                      <tr> 
                        <td width="37"> 
                          <div align="right"> 
                            <input type="radio" name="Program<%= numProgCat %>" value="<%= prog.getProgramID() %>" onClick="changeProgram(this, <%= numProgCat %>)">
                          </div>
                        </td>
                        <td class="TableCell" nowrap><%= ServletUtils.getProgramDisplayNames(prog)[1] %></td>
                      </tr>
                      <%
			}	// End of program
%>
                    </table>
                    <%
		}	// End of program list
%>
                  </td>
                </tr>
                <%
		numProgCat++;
	}
%>
              </table>
              <br>
              <table width="300" border="0" cellspacing="0" cellpadding="5" align="center">
                <tr> 
                  <td width="50%" align="right"> 
                    <input type="submit" name="Submit" value="Submit">
                  </td>
                  <td width="50%"> 
                    <input type="button" name="Cancel" value="Cancel" onclick="location.href='ImportAccount.jsp'">
                  </td>
                </tr>
              </table>
            </form>
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
