<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>">&nbsp;</td>
              </tr>
              <tr> 
                  <td width="265" height="28">&nbsp;</td>
				  <td width="253" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle">&nbsp;</td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../../login.jsp" class="Link3">Log 
                      Off</a>&nbsp;</span></div>
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
		  <% String pageName = "General.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <table width="235" border="1" cellspacing="0" cellpadding="3" align="center">
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="429" valign="top" align="center"> 
                    <table width="400" border="0" cellspacing="3" cellpadding="0">
                      <tr> 
                        <td valign="bottom" class="Main">
<div align="center"><strong><br>
                            <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_GENERAL_TITLE %>"/></strong> <br>
                            <br>
                            <br>
                            </div></td>
                      </tr>
                      <tr> 
                        <td><table width="400" border="0" cellspacing="0" cellpadding="5">
                            <tr> 
                              <td valign="top">
								<p class="Main"><%= ecWebSettings.getDescription() %></p></td>
                              <td valign="top"> 
                                <div align="center" class="MainHeader"><b>Your 
                                  Enrolled Programs</b> </div>
                                <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                                  <tr> 
                                    <td colspan="3" background="../../../Images/Icons/dot.gif" height="8"></td>
                                  </tr>
                                  <%
	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram program = programs.getStarsLMProgram(i);
		StarsApplianceCategory category = null;
		String ctrlOdds = null;
		StarsLMControlHistory todayCtrlHist = ServletUtils.getTodaysControlHistory( program.getStarsLMControlHistory() );
		
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
                                  <tr> 
                                    <td width="64"> 
                                      <div align="center"> <span class="TableCell"> 
                                        <img src="../../../Images/Icons/<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
                                        <%= program.getProgramName() %></span> 
                                      </div>
                                    </td>
                                    <td width="8" background="../../../Images/Icons/dot.gif"> </td>
                                    <td width="128" class="Main"> 
                                      <table width="128" border="0" cellspacing="0" cellpadding="0" class="TableCell" height="80">
                                        <tr height="50"> 
                                          <td> <div align="center">
<%		if (program.getStatus().equalsIgnoreCase(ServletUtils.OUT_OF_SERVICE)) { %>
                                            Out of Service 
<%		} else if (todayCtrlHist.getBeingControlled()) { %>
                                              Currently<br>
                                              <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_CONTROLLING %>"/> 
<%		} else if (todayCtrlHist.getControlHistoryCount() > 0) { %>
                                              You have<br>
                                              been <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_CONTROLLED %>"/> since midnight
<%		} else { %>
                                              You have not<br>
                                              been <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_CONTROLLED %>"/> since midnight
<%		} %>
                                            </div>
                                          </td>
                                        </tr>
                                        <tr> 
                                          <td>
<%		if (ctrlOdds != null) { %>
                                            <div align="center">Control odds:<br>
                                              <b><i><%= ctrlOdds %></i></b> </div>
<%		} %>
                                          &nbsp;</td>
                                        </tr>
                                      </table>
                                    </td>
                                  </tr>
                                  <tr> 
                                    <td colspan="3" background="../../../Images/Icons/dot.gif" height="8"></td>
                                  </tr>
                                  <%
	}
%>
                                </table>
                              </td>
                            </tr>
                          </table></td>
                      </tr>
                    </table>
                    <p><cti:checkRole roleid="<%= RoleTypes.NOTIFICATION_ON_GENERAL_PAGE %>"></cti:checkRole> 
                    </p>
                    <cti:checkRole roleid="<%= RoleTypes.NOTIFICATION_ON_GENERAL_PAGE %>"> 
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
                    <form name="form1" method="POST" action="/servlet/SOAPClient">
                      <input type="hidden" name="action" value="UpdateCtrlNotification">
                      <input type="hidden" name="REDIRECT" value="/user/ConsumerStat/stat/General.jsp">
                      <input type="hidden" name="REFERRER" value="/user/ConsumerStat/stat/General.jsp">
                      <table width="295" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" >
                        <tr> 
                          <td height="58"> 
                            <p align="center" class="TableCell1"> 
                              <input type="checkbox" name="NotifyControl" value="true"
							   <% if (primContact.getEmail().getEnabled()) out.print("checked"); %>>
                              <span class="TableCell2"> I would like to be notified 
                              by e-mail<br> of the chance for control:<br>
                              <input type="text" name="Email" maxlength="50" size="30" value="<%= primContact.getEmail().getNotification() %>">
                              <input type="submit" name="Submit" value="Submit">
                              </span></p>
                          </td>
                        </tr>
                      </table>
                    </form>
                    <%
	}
%>
                    </cti:checkRole></td>
                  <td width="171" valign="top"><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br> 
                    <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                    <!--<%= account.getCompany() %><br> -->
					<%= ServletUtils.getFormattedAddress(propAddr) %><br>
                    <%= primContact.getHomePhone() %></span><br> <br> <img src="../Family.jpg" width="168" height="224"> 
                  </td>
                </tr>
              </table>
            </div>
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
