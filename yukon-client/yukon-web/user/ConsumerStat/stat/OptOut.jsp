<%@ include file="StarsHeader.jsp" %>
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link id="StyleSheet" rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<script language="JavaScript">
	document.getElementById("StyleSheet").href = '../../<cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_STYLE_SHEET %>"/>';
</script>

<script language="JavaScript">
<!--
function confirmSubmit(form) { //v1.0
  if (form.OptOutPeriod.value == 0) return false;
  return confirm('Are you sure you would like to temporarily opt out of all programs?');
}
//-->
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
		  <% String pageName = "OptOut.jsp"; %>
          <%@ include file="Nav.jsp" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br>
              <% String header = ServletUtils.getECProperty(ecWebSettings.getURL(), ServletUtils.WEB_TEXT_OPT_OUT_TITLE); %>
              <%@ include file="InfoBar.jsp" %>
              <table width="600" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td>
                    <hr>
                  </td>
                </tr>
              </table>
			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
		   
              <p><table width="500" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="Main">
				    <cti:getProperty file="<%= ecWebSettings.getURL() %>" name="<%= ServletUtils.WEB_TEXT_OPT_OUT_DESC %>"/>
				  </td>
                </tr>
              </table>
              <br>
<cti:checkNoRole roleid="<%= RoleTypes.HIDE_OPT_OUT_BOX %>">
              <table  border="0" cellspacing="0" cellpadding="0">
                <tr align = "center"> 
                  <td width="304" valign="top" align = "center"> 
                  <form method="post" action="/servlet/SOAPClient" onsubmit="return confirmSubmit(this)">
				  	<input type="hidden" name="action" value="OptOutProgram">
					<input type="hidden" name="REDIRECT" value="/user/ConsumerStat/stat/OptForm.jsp">
					<input type="hidden" name="REDIRECT2" value="/user/ConsumerStat/stat/General.jsp">
					<input type="hidden" name="REFERRER" value="/user/ConsumerStat/stat/OptOut.jsp">
                      <table width="200" border="1" cellspacing="0" cellpadding="3" bgcolor="#CCCCCC" >
                        <tr> 
                          <td align = "center"> 
                           
                              <p class="HeaderCell">Temporarily opt out of all 
                                programs </p>
                         
                            <table width="180" border="0" cellspacing="0" cellpadding="0" align="center">
                              <tr> 
                                <td width="180" align="center"> 
                                  <select name="OptOutPeriod">
									<option value="1">One Day</option>
									<option value="2">Two Days</option>
									<option value="3">Three Days</option>
									<option value="7">One Week</option>
									<option value="14">Two Weeks</option>
                                  </select>
                                </td>
                                <td width="180" align="center"> 
                                  <input type="submit" name="Submit" value="Submit" >
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                      <br>
                    </form>
                  </td>
                </tr>
              </table>
</cti:checkNoRole>              
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
