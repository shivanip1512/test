<%@ include file="include/StarsHeader.jsp" %>
<%
	String importID = ServerUtils.forceNotNone(AuthFuncs.getRolePropertyValue(lYukonUser, ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT));
	boolean isStars = importID.equalsIgnoreCase("STARS");
%>
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
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
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
			  
			  <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/StarsAdmin">
                <input type="hidden" name="action" value="<%= (isStars)?"PreprocessStarsData":"ImportCustAccounts" %>">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/<%= (isStars)?"ImportAccount2.jsp":"ImportAccount.jsp" %>">
<%	if (!isStars) { %>
                <table width="500" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText"> 
                      <div align="center">Enter the import file name and select 
                        the program(s) the imported accounts are to be enrolled 
                        in.</div>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                  <tr> 
                    <td> 
                      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                        <tr> 
                          <td width="75%" align="center">Import file name: 
                            <input type="text" name="ImportFile" maxlength="100" size="40">
                            <br>
                            <br>
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
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                </table>
<%	} else { // importID="STARS" %>
                <table width="400" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="150"> 
                      <div align="right">Customer File: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="CustFile" size="40">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Service Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="ServInfoFile" size="40">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Inventory File: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="InvFile" size="40">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Receiver File: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="RecvrFile" size="40">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Meter File: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="MeterFile" size="40">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Load Info File: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="LoadInfoFile" size="40">
                    </td>
                  </tr>
                  <tr> 
                    <td width="150"> 
                      <div align="right">Work Order File: </div>
                    </td>
                    <td width="250"> 
                      <input type="text" name="WorkOrderFile" size="40">
                    </td>
                  </tr>
                </table>
<%	} %>
                <br>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td width="50%" align="right"> 
<%	if (!isStars) { %>
                      <input type="submit" name="Submit" value="Submit">
<%	} else { %>
                      <input type="submit" name="Submit" value="Next">
<%	} %>
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
