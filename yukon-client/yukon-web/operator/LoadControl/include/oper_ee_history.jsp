<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

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
		  <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <p align="center" class="TitleHeader"><br>
              <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/> - HISTORY</p>
            <p align="center" class="MainText">Click on an Offer ID to view 
              the offer summary.</p>
              <table width="600" border="1" cellspacing="0" bgcolor="white" cellpadding="2" align="center">
                <tr valign="top"> 
                  <td class="HeaderCell">Offer ID</td>
                  <td class="HeaderCell">Program</td>
                  <td class="HeaderCell">Status</td>
                  <td class="HeaderCell">Offer Date</td>
                  
                <td class="HeaderCell">Total Committed (kW)</td>
                  
                <td class="HeaderCell">Target Total (kW)</td>
                </tr>
                <%
	com.cannontech.web.history.EnergyExchangeHistory history = null;

	try {
		history = new com.cannontech.web.history.EnergyExchangeHistory(dbAlias);
		com.cannontech.web.history.HEnergyExchangeProgramOffer[] offers = history.getEnergyExchangeProgramOffers();

		java.util.GregorianCalendar nowCal = new java.util.GregorianCalendar();
		nowCal.setTime( new java.util.Date() );
 
		for (int i = 0; i < offers.length; i++) {
			java.util.GregorianCalendar offerCal = new java.util.GregorianCalendar();
			offerCal.setTime(offers[i].getOfferDate());
			if (offerCal.get(Calendar.YEAR) > nowCal.get(Calendar.YEAR) ||
				offerCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) && offerCal.get(Calendar.DAY_OF_YEAR) >= nowCal.get(Calendar.DAY_OF_YEAR))
				continue;

			com.cannontech.web.history.HEnergyExchangeProgram program = offers[i].getEnergyExchangeProgram();
			com.cannontech.web.history.HEnergyExchangeOfferRevision[] revisions = offers[i].getEnergyExchangeOfferRevisions();
			double amountCommitted = 0;
			for(int j = 0; j < revisions.length; j++) {
				double amountRequested = revisions[j].getAmountRequested();
				amountCommitted += revisions[j].getAmountCommitted();
			%>
                <tr valign="top"> 
                  <td height="23" class="TableCell"><a href="oper_ee.jsp?tab=historydetail&prog=<%= program.getDeviceId() %>&offer=<%= offers[i].getOfferId() %>&rev=<%= revisions[j].getRevisionNumber() %>" class="Link1"> 
                    <%= offers[i].getOfferId() %> - <%= revisions[j].getRevisionNumber() %> 
                    </a></td>
                  <td height="23" class="TableCell"><%= program.getProgramName() %></td>
                  <td height="23" class="TableCell"><%= offers[i].getRunStatus() %></td>
                  <td height="23" class="TableCell"><%= eeTimeFormat.format( revisions[j].getActionDateTime() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + eeDateFormat.format( revisions[j].getActionDateTime() ) %> </td>
                  <td height="23" class="TableCell"><%= numberFormat.format(amountCommitted) %></td>
                  <td height="23" class="TableCell"><%= numberFormat.format(amountRequested) %></td>
                </tr>
                <%
			}
		}
	}
	catch (Exception e) {
		e.printStackTrace();
	}
	finally {
		history.gc();
	}
%>
              </table>
              <p>&nbsp;</p>
            
            </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
