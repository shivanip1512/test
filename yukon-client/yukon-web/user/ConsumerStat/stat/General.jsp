<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../demostyle.css" type="text/css">
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
          <td width="102" height="102" background="../Mom.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td id="Header" colspan="4" height="74" background="../../Header.gif">&nbsp;</td>
<script language="JavaScript">
	document.getElementById("Header").background = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_HEADER %>"/>';
</script>
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
		  <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <table width="235" border="1" cellspacing="0" cellpadding="3" align="center">
              </table>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td width="429" valign="top"> <table width="400" border="0" cellspacing="3" cellpadding="0">
                      <tr> 
                        <td valign="bottom" class="Main">
<div align="center"><strong><br>
                            <cti:getProperty file="ecWebSettings.getURL()" name="<%= ServletUtils.WEB_TEXT_GENERAL_TITLE %>"/></strong> <br>
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
                                <div align="center"><span class="Main">Your Enrolled 
                                  Programs</span><br>
                                  <table width="200" border="0" cellspacing="0" cellpadding="0" align="center">
                                    <tr> 
                                      <td><img src="dot.gif" width="8" height="8"></td>
                                    </tr>
                                  </table>
                                  </div>
                                <table width="200" border="0" cellspacing="0" cellpadding="3" align="center">
                                  <tr class="TableCell"> 
                                    <td width="69"> 
                                      <div align="center"> <span class="TableCell"> 
                                        <img src="../<%= category.getStarsWebConfig().getLogoLocation() %>" width="60" height="59"><br>
                                        <%= program.getProgramName() %></span> 
                                      </div>
                                    </td>
                                    <td width="8"><img src="dot.gif" width="8" height="8"></td>
                                    <td width="123"> 
                                      <table width="123" border="0" cellspacing="0" cellpadding="0" class="TableCell">
                                          <tr>
                                            <td>
                                            <div align="center" class="TableCell"><cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_CONTROL %>" format="capitalized"/> 
                                              Since Midnight<br><div align="center"> 
                                        <%
		if (program.getStatus().equalsIgnoreCase(ServletUtils.OUT_OF_SERVICE)) {
%>
                                        <b>Out of Service</b> 
                                        <%
		}
		else if (todayCtrlHist.getBeingControlled()) {
%>
                                        <b>Currently<br>
                                        <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_CONTROLLING %>"/></b> 
                                        <%
		}
		else if (todayCtrlHist.getControlHistoryCount() > 0) {
%>
                                        <b>You have<br>
                                        been <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_CONTROLLED %>"/></b> 
                                        <%
		}
		else {
%>
                                        <b>You have not<br>
                                        been <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_CONTROLLED %>"/></b> 
                                        <%
		}
%>
                                      </div>
                                            </div>
                                          </td>
                                          </tr>
                                          <tr>
                                          <td class="TableCell">
                                            <div align="center">Control today 
                                              is Likely</div>
                                          </td>
                                          </tr>
                                        </table>
                                      </td>
                                    </tr>
                                  <%
	for (int i = 0; i < programs.getStarsLMProgramCount(); i++) {
		StarsLMProgram program = programs.getStarsLMProgram(i);
		StarsApplianceCategory category = null;
		StarsLMControlHistory todayCtrlHist = ServletUtils.getTodaysControlHistory( program.getStarsLMControlHistory() );
		
		for (int j = 0; j < categories.getStarsApplianceCategoryCount(); j++) {
			StarsApplianceCategory appCat = categories.getStarsApplianceCategory(j);
			if (appCat.getApplianceCategoryID() == program.getApplianceCategoryID()) {
				category = appCat;
				break;
			}
		}
%>
                                  
                                  <%
	}
%>
                                </table>
                                <table width="200" border="0" cellspacing="0" cellpadding="0" align="center">
                                  <tr>
                                    <td><img src="dot.gif" width="8" height="8"></td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table></td>
                      </tr>
                    </table></td>
                  <td width="171"><span class="Main"><b>Acct #<%= account.getAccountNumber() %></b></span><br> 
                    <span class="NavText"><%= primContact.getFirstName() %> <%= primContact.getLastName() %><br>
                    <!--<%= account.getCompany() %><br> -->
                    <%= propAddr.getStreetAddr1() %>, <%= propAddr.getStreetAddr2() %><br>
                    <%= propAddr.getCity() %>, <%= propAddr.getState() %> <%= propAddr.getZip() %><br>
                    <%= primContact.getHomePhone() %></span><br> <br> <img src="Family.jpg" width="168" height="224"> 
                  </td>
                </tr>
              </table>
            </div>
            <p>&nbsp;</p>
          </td>
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
