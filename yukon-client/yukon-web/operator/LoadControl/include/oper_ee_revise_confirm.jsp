<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers

   function goBack() {
  location = "<%=request.getContextPath()%>/operator/LoadControl/oper_ee.jsp?tab=revise&prog=<%= progIdStr %>&offer=<%= offerIdStr %>&rev=<%= revNumStr %>&error=false"
  }

  //End hiding script -->
  </SCRIPT>
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
                <td width="235" valign="middle">&nbsp;</td>
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
                      <input type="submit" name="tab" value="History">
                    </p>
					</form>
				   <form name="form1" method="get" action="oper_ee.jsp?tab=programs">
                    <p> 
                      <input type="submit" name="tab" value="Programs">
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
    <tr> 
                <td width="650" valign="top"> 
                  <p align="center" class="TitleHeader"><br>
                    <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/> - REVISE OFFER 
                    <%= offerIdStr %> - <%= revNumStr %> <br></p>
                  <p align="center" class="MainText">The total target amount is <%= totAmountStr %> 
                    kW. Please confirm the offer information below.</p>
                  <form action="oper_ee.jsp?tab=reviseconfirm" method="post">
          <input type=hidden name="confirmed" value="true">
          <input type=hidden name="prog" value="<%= progIdStr %>">
          <input type=hidden name="offer" value="<%= offerIdStr %>">
          <input type=hidden name="rev" value="<%= revNumStr %>">
          <input type=hidden name="date" value="<%= dateStr %>">
          <input type=hidden name="notifydate" value="<%= notifyDateStr %>">
          <input type=hidden name="notifytime" value="<%= notifyTimeStr %>">
          <input type=hidden name="expiredate" value="<%= expireDateStr %>">
          <input type=hidden name="expiretime" value="<%= expireTimeStr %>">
          <%
	for (int i = 0; i < 24; i++) {
%>
          <input type=hidden name="prices" value="<%= newPriceStrs[i] %>">
          <input type=hidden name="amount" value="<%= newAmountStrs[i] %>">
                  <%
	}
%>
                  <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
          <tr> 
            <td width="100" valign="TOP" class="SubtitleHeader"><p align=RIGHT>Program:</td>
            <td width="100" valign="TOP" class="Subtext"><%= programStr %></td>
            <td width="100" valign="TOP" class="SubtitleHeader"><p align=RIGHT>Notify Date:</td>
            <td width="80" valign="TOP" class="Subtext"><%= notifyDateStr %></td>
            <td width="100" valign="TOP" class="SubtitleHeader"><p align=RIGHT>Notify Time:</td>
            <td width="120" valign="TOP" class="Subtext"><%= notifyTimeStr %></td>
          </tr>
          <tr> 
            <td width="100" valign="TOP" class="SubtitleHeader"><p align=RIGHT>Control Date:</td>
            <td width="100" valign="TOP" class="Subtext"><%= dateStr %></td>
            <td width="100" valign="TOP" class="SubtitleHeader"><p align=RIGHT>Expire Date:</td>
            <td width="80" valign="TOP" class="Subtext"><%= expireDateStr %></td>
            <td width="100" valign="TOP" class="SubtitleHeader"><p align=RIGHT>Expire Time:</td>
            <td width="80" valign="TOP" class="Subtext"><%= expireTimeStr %></td>
          </tr>
        </table>
        <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
          <tr> 
            <td width="50%"> 
              <p> 
              <center>
                <table width="310" border="1" cellspacing="0" cellpadding="2">
                  <tr> 
                    <td width="90" height="23" valign="TOP" class="HeaderCell">Hour 
                      Ending</td>
                              <td width="110" height="23" valign="TOP" class="HeaderCell">Offer 
                                in $/ kWh</td>
                              <td width="110" height="23" valign="TOP" class="HeaderCell">Target 
                                in kW</td>
                  </tr>
                  <%
		  	for (int i = 0; i < 12; i++) {
				String endingHourStr = String.valueOf(i+1) + ":00";
		  %>
                  <tr> 
                    <td width="90" height="10" valign="TOP" class="TableCell"><%= endingHourStr %></td>
                    <td width="110" height="10" valign="TOP" class="TableCell"><%= priceStrs[i] %></td>
                    <td width="110" height="10" class="TableCell"><%= amountStrs[i] %></td>
                  </tr>
                  <%
		  	}
		  %>
                </table>
              </center>
            </td>
            <td width="50%"> 
              <p> 
              <center>
                <table width="310" border="1" cellspacing="0" cellpadding="2">
                  <tr> 
                    <td width="90" height="23" valign="TOP" class="HeaderCell">Hour 
                      Ending</td>
                              <td width="100"height="23" valign="TOP" class="HeaderCell">Offer 
                                in $/ kWh</td>
                              <td width="110" height="23" valign="TOP" class="HeaderCell">Target 
                                in kW</td>
                  </tr>
                  <%
		  	for (int i = 12; i < 24; i++) {
				String endingHourStr = String.valueOf(i+1) + ":00";
		  %>
                  <tr> 
                    <td width="90" height="10" valign="TOP" class="TableCell"><%= endingHourStr %></td>
                    <td width="110" height="10" valign="TOP" class="TableCell"><%= priceStrs[i] %></td>
                    <td width="110" height="10" class="TableCell"><%= amountStrs[i] %></td>
                  </tr>
                  <%
		  	}
		  %>
                </table>
              </center>
            </td>
          </tr>
        </table>
        <center>
          <%=checker.getError("offertooearly")%>        
        </center>
        <table width="580"
    border="0" cellspacing="0" cellpadding="4" height="10" align="center">
          <tr> 
            <td width="50%" valign="TOP" height="10"> 
              <p align=RIGHT> 
                <input type="submit" value="Send" border="0" name="image">
            </td>
            <td width="50%" valign="TOP" height="10"><input type = "button" value="Cancel" name = "cancel" onclick = "goBack()"></td>
          </tr>
        </table>
      <p>&nbsp;</p></td>
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
