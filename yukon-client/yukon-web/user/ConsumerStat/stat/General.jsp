<%@ include file="include/StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td  valign="top" width="101">
		  <% String pageName = "General.jsp"; %>
          <%@ include file="include/Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <table width="235" border="1" cellspacing="0" cellpadding="3" align="center">
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="5">
                <tr> 
                  <td width="429" valign="top" align="center"> 
                    <table width="400" border="0" cellspacing="3" cellpadding="0">
                      <tr> 
                        <td valign="bottom" class="GeneralHeader">
                          <div align="center">
                            <cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_TITLE_GENERAL %>" defaultvalue="WELCOME TO ENERGY COMPANY SERVICES!"/><br>
                          </div>
                        </td>
                      </tr>
                      <tr> 
                        <td>
				          <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
                          <table width="400" border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td valign="top">
								<p class="MainText"><cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_DESC_GENERAL %>"/></p></td>
                            </tr>
							<tr>
							  <td valign="top"><br> 
                                <div align="left" class="SubtitleHeader">Your 
                                  Enrolled Programs</div>
                                <table width="400" border="0" cellspacing="0" cellpadding="3" align="center">
                                  <tr> 
                                    <td colspan="3" background="../../../WebConfig/yukon/Icons/dot.gif" height="8"></td>
                                  </tr>
<% if (programs.getStarsLMProgramCount() == 0) { %>
								  <tr>
								    <td colspan="3" class="TableCell">You are not enrolled in any program.</td>
								  </tr>
<% } else {
	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram program = programs.getStarsLMProgram(i);
		StarsApplianceCategory category = null;
		String ctrlOdds = null;
		StarsLMControlHistory todayCtrlHist = ServletUtils.getControlHistory( program.getStarsLMControlHistory(), StarsCtrlHistPeriod.PASTDAY, tz );
		
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = appCat;
				
				for (int k = 0; k < category.getStarsEnrLMProgramCount(); k++) {
					StarsEnrLMProgram enrProg = category.getStarsEnrLMProgram(k);
					if (enrProg.getProgramID() == program.getProgramID()) {
						if (enrProg.getChanceOfControl() != null)
							ctrlOdds = enrProg.getChanceOfControl().getContent();
						break;
					}
				}
				break;
			}
		}
%>
                                  <tr valign="top"> 
                                    <td width="64"> 
                                      <div align="center">
<% if (!category.getStarsWebConfig().getLogoLocation().equals("")) { %>
                                        <img src="../../../WebConfig/<%= category.getStarsWebConfig().getLogoLocation() %>"><br>
<% } %>
                                        </div>
                                    </td>
                                    <td width="8" background="../../../WebConfig/yukon/Icons/dot.gif"> 
                                    </td>
                                    <td width="328" class="MainText"> 
                                      <table width="328" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                                        <tr>
										  <td><span class="TableCell"><b><%= program.getProgramName() %></b></span><br>
                                          </td>
										</tr>  
										<tr> 
                                          <td> 
<%
		if (program.getStatus().equalsIgnoreCase(ServletUtils.OUT_OF_SERVICE)) {
			String untilStr = "";
			
			if (programHistory != null) {
				for (int j = programHistory.getStarsLMProgramEventCount() - 1; j >= 0; j--) {
					StarsLMProgramEvent event = programHistory.getStarsLMProgramEvent(j);
					
					boolean belongsToProgram = false;
					for (int k = 0; k < event.getProgramIDCount(); k++)
						if (event.getProgramID(k) == program.getProgramID()) {
							belongsToProgram = true;
							break;
						}
					
					if (belongsToProgram) {
						if (event.getYukonDefID() == com.cannontech.common.constants.YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_FUTURE_ACTIVATION)
							untilStr = " until " + histDateFormat.format(event.getEventDateTime());
						break;
					}
				}
			}
%>
                                            <div align="left">Out of service<%= untilStr %>. 
<%		} else if (todayCtrlHist.getBeingControlled()) { %>
                                              Currently <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_CONTROLLING %>" defaultvalue="controlling"/>. 
<%		} else if (todayCtrlHist.getControlHistoryCount() > 0) { %>
                                              You have been <cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_TEXT_CONTROLLED %>" defaultvalue="controlled"/> 
                                              today. 
<%		} else { %>
                                              You have not been <cti:getProperty propertyid="<%=ResidentialCustomerRole.WEB_TEXT_CONTROLLED %>" defaultvalue="controlled"/> 
                                              today. 
<%		} %>
                                            </div>
                                          </td>
                                        </tr>
<%		if (ctrlOdds != null) { %>
                                        <tr> 
                                          <td>
                                            <div><cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_ODDS_FOR_CONTROL %>" defaultvalue="odds for control" format="capital"/>:
                                              <b><i><%= ctrlOdds %></i></b> </div>
                                          </td>
                                        </tr>
<%		} %>
                                      </table>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td colspan="3" background="../../../WebConfig/yukon/Icons/dot.gif" height="8"></td>
                                  </tr>
<%
	}
 }
%>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                    <br>
<%
	boolean showNotification = false;
	for (int i = 0; i < categories.getStarsApplianceCategoryCount(); i++) {
		StarsApplianceCategory category = categories.getStarsApplianceCategory(i);
		for (int j = 0; j < category.getStarsEnrLMProgramCount(); j++) {
			StarsEnrLMProgram enrProg = category.getStarsEnrLMProgram(j);
			if (enrProg.getChanceOfControl() == null) continue;
			
			for (int k = 0; k < programs.getStarsLMProgramCount(); k++) {
				if (programs.getStarsLMProgram(k).getProgramID() == enrProg.getProgramID()) {
					showNotification = true;
					break;
				}
			}
			if (showNotification) break;
		}
		if (showNotification) break;
	}
	
	if (showNotification) {
%>
                    <form name="form1" method="POST" action="<%=request.getContextPath()%>/servlet/SOAPClient">
                      <input type="hidden" name="action" value="UpdateCtrlNotification">
                      <input type="hidden" name="REDIRECT" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/General.jsp">
                      <input type="hidden" name="REFERRER" value="<%=request.getContextPath()%>/user/ConsumerStat/stat/General.jsp">
                      <table width="328" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" >
                        <tr> 
                          <td> 
                            <p align="center"> 
                              <input type="checkbox" name="NotifyControl" value="true"
							   <% if (primContact.getEmail().getEnabled()) out.print("checked"); %>>
                              <span class="TableCell2"> 
                              I would like to be notified by e-mail of the 
                              <cti:getProperty propertyid="<%= ResidentialCustomerRole.WEB_TEXT_ODDS_FOR_CONTROL %>" defaultvalue="odds for control"/>.<br>
                              My e-mail address is:</span><br>
                              <input type="text" name="Email" maxlength="50" size="30" value="<%= primContact.getEmail().getNotification() %>">
                              <input type="submit" name="Submit" value="Submit">
                              </p>
                          </td>
                        </tr>
                      </table>
                    </form>
<%
	}
%>
                  </td>
                  <td width="171" valign="top"><span class="TitleHeader">Acct #<%= account.getAccountNumber() %></span><br> 
                    <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                    <!--<%= account.getCompany() %><br> -->
					<%= ServletUtils.formatAddress(propAddr) %><br>
                    <%= primContact.getHomePhone() %></span><br>
                    <br>
<%
	String genlImgName = ServerUtils.forceNotNone(AuthFuncs.getRolePropertyValue(lYukonUser, ResidentialCustomerRole.WEB_IMG_GENERAL));
	if (genlImgName.length() > 0) {
%>
                    <img src="../../../WebConfig/<%= genlImgName %>"> 
<%	} %>
                  </td>
                </tr>
              </table>
            </div>
            <p>&nbsp;</p>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
