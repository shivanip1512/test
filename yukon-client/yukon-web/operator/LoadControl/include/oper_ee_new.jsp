<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function confirm_form(f) {
	f.programname.value = f.program.options[f.program.selectedIndex].text;
	return true;
}
   function goBack() {
  location = "<%=request.getContextPath()%>/operator/LoadControl/oper_ee.jsp"
  }
</script>
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;<cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/></td>
                <td width="235" height = "28" valign="middle">&nbsp;</td>
                
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
          <td  valign="top" width="101"> 
                        <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top" align="center"> 
                  <form name="form1" method="get" action="oper_ee.jsp?tab=current">
                    <p><br>
                      <input type="submit" name="tab" value="Current">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_ee.jsp?tab=new">
				    <p> 
                      <input type="submit" name="tab" value="New">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_ee.jsp?tab=history">
				    <p> 
                      <input type="submit" name="new" value="History">
                    </p>
					</form>
				   <form name="form1" method="get" action="oper_ee.jsp?tab=programs">
                    <p> 
                      <input type="submit" name="new" value="Programs">
                    </p>
                  </form>
                  <p>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <table width="657" border="0" cellspacing="0" cellpadding="0" align="left">
              <struts:form name="checker" type="com.cannontech.validate.PageBean" action="oper_ee.jsp?tab=new" onSubmit="return confirm_form(this)">
                <tr> 
                  <td width="650" class="TitleHeader" valign="top"> 
                    <p align="center"><br>
                    <input type=hidden name="submitted" value="true">
                    <input type=hidden name="programname">
                    <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/> - NEW OFFER</p>
                    <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                      <tr> 
                        <td width="21%" class="SubtitleHeader" valign = "top"><p align=RIGHT>Program:</td>
                        <td width="12%" class="Subtext" valign = "top">
	                      <select name="program">
	                      <%
							for (int i = 0; i < programIds.length; i++)
							{
								if (checker.get("program")!= null && checker.get("program").equalsIgnoreCase(programIds[i]))
									out.println("<OPTION VALUE=" + programIds[i] + " SELECTED>" + programNames[i]);
								else
									out.println("<OPTION VALUE=" + programIds[i] + ">" + programNames[i]);
							}%>
						  </select>
                        </td>
                        <td width="21%" class="SubtitleHeader" valign = "top"><p align=RIGHT>Notify Date:</td>
                        <td width="12%" class="Subtext" valign = "top"><struts:text property="notifydate" size="10" pattern="@date"/>
                          <span class = "TableCell"><%= checker.getError("notifydate")%></span>
                        </td>
                        <td width="21%" class="SubtitleHeader" valign = "top"><p align=RIGHT>Notify Time:&nbsp;</td>
                        <td width="12%" class="Subtext" valign = "top"><struts:text property="notifytime" size="10" pattern="@time"/>
                          <span class = "TableCell"><%= checker.getError("notifytime") %></span>
                        </td>
                        <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
                      </tr> 
                      <tr> 
                        <td width="21%" class="SubtitleHeader" valign = "top"><p align=RIGHT>Control Date:</td>
                        <td width="12%" class="Subtext" valign = "top"><struts:text property="date" size="10" pattern="@date"/>
                          <span class = "TableCell"><%= checker.getError("date") %></span>
                        </td>
                        <td width="21%" class="SubtitleHeader" valign = "top"><p align=RIGHT>Expire Date:</td>
                        <td width="12%" class="Subtext" valign = "top"><struts:text property="expiredate" size="10" pattern="@date"/>
                          <span class = "TableCell"><%= checker.getError("expiredate") %></span>
                        </td>
                        <td width="21%" class="SubtitleHeader" valign = "top"><p align=RIGHT>Expire Time:&nbsp;</td>
                        <td width="12%" class="Subtext" valign = "top"><struts:text property="expiretime" size="10" pattern="@time"/>
                          <span class = "TableCell"><%= checker.getError("expiretime") %></span>
                        </td>
                        <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                      <tr>
                        <td width = "100%" class="TableCell" valign="top">
            	          <p align="center"><%=checker.getError("amounterror")%>
                            <%= checker.getError("formaterror") %>
                            <%=checker.getError("nooffer")%>
                        </td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                      <tr> 
                        <td width="50%"> 
                          <p><center>
                            <table width="310" border="1" cellspacing="0" cellpadding="2">
                              <tr> 
                                <td width="90" valign="TOP" class="HeaderCell">Hour Ending</td>
                                <td width="110" valign="TOP" class="HeaderCell">Offer Price in $ per kWh</td>
                                <td width="110" valign="TOP" class="HeaderCell">Target in kW</td>
                              </tr>
                              <%
                              for (int i = 0; i < 12; i++) {
                                String endingHourStr = String.valueOf(i+1) + ":00";
                              %>
                                <tr> 
                                  <td width="90" valign="TOP" class="TableCell"><%= endingHourStr %></td>
                                  <td width="110" valign="TOP" class="TableCell"><struts:text property="prices" size="8" pattern="@real" value="<%= priceStrs[i] %>"/></td>
                                  <td width="110" valign="TOP" class="TableCell"><struts:text property="amount" size="8" pattern="@real" value="<%= amountStrs[i] %>"/><span class="TableCell"><%=checker.getError("amounterror"+String.valueOf(i))%></span></td>
                                </tr>
                              <% } %>
                            </table>
                          </center>
                        </td>
                        <td width="50%"> 
                          <p><center>
                            <table width="310" border="1" cellspacing="0" cellpadding="2">
                              <tr> 
                                <td width="90" valign="TOP" class="HeaderCell">Hour Ending</td>
                                <td width="110" valign="TOP" class="HeaderCell">Offer Price in $ per kWh</td>
                                <td width="110" valign="TOP" class="HeaderCell">Target in kW</td>
                              </tr>
                              <%
                              for (int i = 12; i < 24; i++) {
                                String endingHourStr = String.valueOf(i+1) + ":00";
                              %>
                                <tr> 
                                  <td width="90" valign="TOP" class="TableCell"><%= endingHourStr %></td>
                                  <td width="110" valign="TOP" class="TableCell"><struts:text property="prices" size="8" pattern="@real" value="<%= priceStrs[i] %>"/></td>
                                  <td width="110" valign="TOP" class="TableCell"><struts:text property="amount" size="8" pattern="@real" value="<%= amountStrs[i] %>"/><span class="TableCell"><%=checker.getError("amounterror"+String.valueOf(i))%></span></td>
                                </tr>
                              <% } %>
                            </table>
                          </center>
                        </td>
                      </tr>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="4" height="10" align="center">
                      <tr> 
                        <td width="385" height="10"> 
                          <p align=RIGHT> 
                            <!-- <img src="DittoButton.gif" width="73" height="22" alt="Ditto Button">&nbsp; -->
                        </td>
                        <td width="211" valign="TOP" height="10"> 
                          <div align="right"> 
                            <input type="submit" value="Confirm" border="0" name="image">
                          </div>
                        </td>
                        <td width="80" valign="TOP" height="10"> 
                          <div align="right"><input type = "button" value="Cancel" name = "cancel" onclick = "goBack()"></div>
                        </td>
                      </tr>
                    </table>
                    <p>&nbsp;</p>
                  </td>
                </tr>
              </table>
            </struts:form>
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
