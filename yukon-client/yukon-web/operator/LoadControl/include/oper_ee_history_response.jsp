<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

</head>
<%
    java.text.NumberFormat hourFormat = new java.text.DecimalFormat();
    hourFormat.setMinimumIntegerDigits(2);
	
	int programId = 0;
	int offerId = 0;
	int revisionNumber = 0;
	int customerId = 0;

	progIdStr = request.getParameter("prog");
	if (progIdStr != null)
		programId = Integer.parseInt(progIdStr);

	offerIdStr = request.getParameter("offer");
	if (offerIdStr != null)
		offerId = Integer.parseInt(offerIdStr);

	revNumStr = request.getParameter("rev");
	if (revNumStr != null)
		revisionNumber = Integer.parseInt(revNumStr);

	String customerIdStr = request.getParameter("cust");
	if (customerIdStr != null)
		customerId = Integer.parseInt(customerIdStr);

	com.cannontech.web.history.EnergyExchangeHistory history = null;
	com.cannontech.web.history.HEnergyExchangeProgram program = null;
	com.cannontech.web.history.HEnergyExchangeProgramOffer offer = null;
	com.cannontech.web.history.HEnergyExchangeOfferRevision revision = null;
	com.cannontech.web.history.HEnergyExchangeCustomer customer = null;
	com.cannontech.web.history.HEnergyExchangeCustomerReply reply = null;

	double[] prices = new double[24];
	double[] amount = new double[24];
	double totalCommitted = 0;

	try {
		history = new com.cannontech.web.history.EnergyExchangeHistory(dbAlias);
		com.cannontech.web.history.HEnergyExchangeProgram[] programs = history.getEnergyExchangePrograms();

		for (int i = 0; i < programs.length; i++)
		{
			if (programs[i].getDeviceId() != programId)
				continue;
			program = programs[i];

			com.cannontech.web.history.HEnergyExchangeProgramOffer[] offers = program.getEnergyExchangeProgramOffers();

			for (int j = 0; j < offers.length; j++)
			{			
				if (offers[j].getOfferId() != offerId)
					continue;
				offer = offers[j];			 

				com.cannontech.web.history.HEnergyExchangeOfferRevision[] revisions = offer.getEnergyExchangeOfferRevisions();

				for (int k = 0; k < revisions.length; k++)
				{
					if (revisions[k].getRevisionNumber() != revisionNumber)
						continue;
					revision = revisions[k];					

					com.cannontech.web.history.HEnergyExchangeCustomer[] customers = program.getEnergyExchangeCustomers();

					for (int l = 0; l < customers.length; l++)
					{
						if (customers[l].getCustomerId() != customerId)
							continue;
						customer = customers[l];						

						reply = customer.getEnergyExchangeCustomerReply(offerId, revisionNumber);
					}
				}
			}
		}

		com.cannontech.web.history.HEnergyExchangeHourlyOffer[] hrOffers = revision.getEnergyExchangeHourlyOffers();
		com.cannontech.web.history.HEnergyExchangeHourlyCustomer[] hrCustomers = customer.getEnergyExchangeHourlyCustomers(offerId, revisionNumber);

		for (int i = 0; i < hrOffers.length; i++)
			prices[ hrOffers[i].getHour() ] = hrOffers[i].getPrice() / 100.0;

		for (int i = 0; i < hrCustomers.length; i++) {
			amount[ hrCustomers[i].getHour() ] = hrCustomers[i].getAmountCommitted();
			totalCommitted += amount[ hrCustomers[i].getHour() ];
		}
	}
	catch (Exception e) {
		e.printStackTrace();
	}
	finally {
		history.gc();
	}
%>
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
                    <p> <br>
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
            <div align="center">
              <p class="TitleHeader"><br>
                <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/> - CUSTOMER SUMMARY HISTORY<br><%= customer.getCustomerName() %></p>
            </div>
            <div align="center"> 
          <table width="480" border="0" cellspacing="0" cellpadding="5">
            <tr> 
              <td class="TitleHeader"><p align=RIGHT>Offer ID:</td>
              <td class="MainText"><%= offerId + "-" + revisionNumber %></td>
              <td class="TitleHeader"><p align=RIGHT>Control Date:</td>
              <td class="MainText"><%= eeTimeFormat.format( offer.getOfferDate() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + eeDateFormat.format( offer.getOfferDate() ) %></td>
              <td class="TitleHeader"><p align=RIGHT>Accept User:</td>
              <td class="MainText"><%= reply.getNameOfAcceptPerson() %></td>
            </tr>
          </table>
          <table width="600" border="0" cellspacing="0" cellpadding="5">
            <tr> 
              <td width="50%"> 
                <p> 
                <table width="310" border="1" cellspacing="0" cellpadding="2" align="center">
                  <tr> 
                    <td width="90" valign="TOP" class="HeaderCell">Hour Ending</td>
                        <td width="110" valign="TOP" class="HeaderCell">Offer Price in $ per kWh</td>
                        <td width="110" valign="TOP" class="HeaderCell">CLR in kW</td>
                  </tr>
                  <%
	for (int i = 0; i < 12; i++) {
		String time = hourFormat.format(i+1) + ":00";
%>
                  <tr> 
                    <td width="110" class="TableCell"><%= time %></td>
                    <td width="110" class="TableCell"><%= priceFormat.format(prices[i]) %></td>
                    <td width="110" class="TableCell"><%= numberFormat.format(amount[i]) %></td>
                  </tr>
                  <%
	}
%>
                </table>
              </td>
              <td width="50%"> 
                <p> 
                <table width="310" border="1" cellspacing="0" cellpadding="2" align="center">
                  <tr> 
                    <td width="90" valign="TOP" class="HeaderCell">Hour Ending</td>
                        <td width="110" valign="TOP" class="HeaderCell">Offer 
                          Price in $ per kWh</td>
                        <td width="110" valign="TOP" class="HeaderCell">SCL in 
                          kW</td>
                  </tr>
                  <%
	for (int i = 12; i < 24; i++) {
		String time = hourFormat.format(i+1) + ":00";
%>
                  <tr> 
                    <td width="110" class="TableCell"><%= time %></td>
                    <td width="110" class="TableCell"><%= priceFormat.format(prices[i]) %></td>
                    <td width="110" class="TableCell"><%= numberFormat.format(amount[i]) %></td>
                  </tr>
                  <%
	}
%>
                </table>
              </td>
            </tr>
          </table>
        </div>
            <p align="center" class="SubtitleHeader">TOTAL: <%= numberFormat.format(totalCommitted) %> kWh</p>
            <p align="center" class="SubtitleHeader"><a href="<%= referrer %>" class="Link1">Back</a><br><br>
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
