<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>
<%        
        com.cannontech.web.history.EnergyExchangeHistory history = null;

        try
        {
        
		int offerId = 0;
		int revisionNumber = 0;

		String offerIdStr = request.getParameter("offer");
		if (offerIdStr != null)
			offerId = Integer.parseInt(offerIdStr);

		String revisionNumberStr = request.getParameter("rev");
		if (revisionNumberStr != null)
			revisionNumber = Integer.parseInt(revisionNumberStr);

		history = new com.cannontech.web.history.EnergyExchangeHistory(dbAlias);

		com.cannontech.web.history.HEnergyExchangeProgramOffer[] offerHistories = history.getEnergyExchangeProgramOffers();
		boolean foundOfferRev = false;

		for (int i = 0; i < offerHistories.length; i++)
		{
			offerHistory = offerHistories[i];
			if (offerHistory.getOfferId() != offerId)
				continue;

			com.cannontech.web.history.HEnergyExchangeOfferRevision[] revisions = offerHistory.getEnergyExchangeOfferRevisions();
			for (int j = 0; j < revisions.length; j++)
			{
				revisionHistory = revisions[j];
				if (revisionHistory.getRevisionNumber() == revisionNumber) {
					foundOfferRev = true;
					break;
				}
			}

			if (foundOfferRev)
				break;
		}

		com.cannontech.web.history.HEnergyExchangeProgram programHistory = offerHistory.getEnergyExchangeProgram();

		com.cannontech.web.history.HEnergyExchangeCustomer[] customerHistories = programHistory.getEnergyExchangeCustomers();

		for (int i = 0; i < customerHistories.length; i++)
		{
			if (customerHistories[i].getCustomerId() == customerID) {                
				replyHistory = customerHistories[i].getEnergyExchangeCustomerReply(offerId, revisionNumber);
				break;
			}
		}        

		com.cannontech.web.history.HEnergyExchangeHourlyOffer[] hourlyOffers = revisionHistory.getEnergyExchangeHourlyOffers();       

		com.cannontech.web.history.HEnergyExchangeHourlyCustomer[] hourlyCustomers = replyHistory.getEnergyExchangeHourlyCustomers();        

		for (int i = 0; i < 24; i++)
		{   
            double price = (double) hourlyOffers[i].getPrice() / 100.0;
			if (price == 0)
				priceStrs[i] = "----";
			else
				priceStrs[i] = priceFormat.format(price);
   
			double amount = 0;
			if ( replyHistory.getAcceptStatus().equalsIgnoreCase("accepted") )
				amount = hourlyCustomers[i].getAmountCommitted();
			else
				amount = hourlyOffers[i].getAmountRequested();

			if (amount == 0)
				amountStrs[i] = "----";
			else
				amountStrs[i] = numberFormat.format(amount);
		}

        // Attempt to get the customers baseline values for the date of the offer
        java.util.Date offerDate = offerHistory.getOfferDate();
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.setTime(offerDate);
        cal.set(java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + 1 );
        
        double[] baseLineValues = cache.getCustomerBaseLine( customerID, offerDate, cal.getTime() );

%>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
			  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Buyback</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
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
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150">
            <% String pageName = "user_ee.jsp"; %>
            <%@ include file="nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="609" valign="top" bgcolor="#FFFFFF"> 
            <center>
            <p><br>
            <p class="TitleHeader">OFFER TO CUSTOMER</p>
            <table width="590" border="0" cellspacing="0" cellpadding="5">
              <tr> 
                <td width="18%" class="SubtitleHeader"><p align=RIGHT>Offer History ID:</td>
                <td width="11%" class="Subtext"><%= revisionHistory.getOfferId() + " - " + revisionHistory.getRevisionNumber()%></td>
                <td width="19%" class="SubtitleHeader"><p align=RIGHT>Offer History Date:</td>
                <td width="14%" class="Subtext"><%= datePart.format( offerHistory.getOfferDate()) %></td>
                <td width="18%" class="SubtitleHeader"><p align=RIGHT>Expire Date/Time:</td>
                <td width="20%" class="Subtext"><%= timePart.format( revisionHistory.getExpirationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format( revisionHistory.getExpirationDateTime()) %></td>
              </tr>
            </table>
            <table width="590" border="0" cellspacing="0" cellpadding="5">
              <tr> 
                <td width="295"> 
                  <p>
                  <center>
                  <table width="295" border="1" cellspacing="0" cellpadding="2">
                    <tr> 
                      <td width="25%" height="10" valign="TOP" class="HeaderCell">Hour Ending </td>
                      <td width="25%" height="10" valign="TOP" class="HeaderCell">Offer in $ per kWh</td>
                   <% if (replyHistory.getAcceptStatus().equalsIgnoreCase("accepted")) { %>                      
                      <td width="25%" height="10" valign="TOP" class="HeaderCell">CLR in kW</td>
                    <% } else { %>
<!--                  <td width="25%" height="10" valign="TOP" class="HeaderCell">kW Requested</td>-->
                      <% } %>
<!--                  <td width="25%" height="10" valign="TOP" class="HeaderCell">Baseline</td>-->
                    </tr>
                  <%
               for( int i = 0; i < 12; i++ ) {
                   String hourStr = hourFormat.format(i+1) + ":00";
          %>
                    <tr> 
                      <td width="25%" height="10" class="TableCell"><%= hourStr %></td>
                      <td width="25%" height="10" class="TableCell"><%= priceStrs[i] %></td>
                    <% if (replyHistory.getAcceptStatus().equalsIgnoreCase("accepted")) {
                      		if( amountStrs[i] == "----"){
                      			 %>
                      <td width="25%" height="10" valign="TOP" class="TableCell"><%= amountStrs[i] %></td>
                    <%}
                    else{ 
                    %>
                      <td width="25%" height="10" valign="TOP" class="TableCell"><%=numberFormat.format(numberFormat.parse(amountStrs[i]))%></td>
                    <%}
                    }%>
<!--                  <td width="25%" height="10" class="TableCell"><% if(baseLineValues != null) { %><%= numberFormat.format(baseLineValues[i]) %><% } else { %>----<% } %></td>-->
                    </tr>
                  <%
               }
          %>
                  </table>
                  </center>
                </td>
                <td width="295"> 
                  <p>
                  <center>
                  <table width="295" border="1" cellspacing="0" cellpadding="2">
                    <tr> 
                      <td width="25%" height="10" valign="TOP" class="HeaderCell">Hour Ending</td>
                      <td width="25%" height="10" valign="TOP" class="HeaderCell">Offer in $ per kWh</td>
                      <% if (replyHistory.getAcceptStatus().equalsIgnoreCase("accepted")) { %>
                      <td width="25%" height="10" valign="TOP" class="HeaderCell">CLR in kW</td>
                      <% } else { %>
<!--                  <td width="25%" height="10" valign="TOP" class="HeaderCell">kW Requested</td>-->
                      <% } %>
<!--                  <td width="25%" height="10" valign="TOP" class="HeaderCell">Baseline</td>-->
                    </tr>
                  <%
               for( int i = 12; i < 24; i++ ) {
                   String hourStr = hourFormat.format(i+1) + ":00";
          %>
                    <tr> 
                      <td width="25%" height="10" valign="TOP" class="TableCell"><%= hourStr %></td>
                      <td width="25%" height="10" valign="TOP" class="TableCell"><%= priceStrs[i] %></td>
                      <% if (replyHistory.getAcceptStatus().equalsIgnoreCase("accepted")) {
                      		if( amountStrs[i] == "----"){
                      			 %>
                      <td width="25%" height="10" valign="TOP" class="TableCell"><%= amountStrs[i] %></td>
                    <%}
                    else{ 
                    %>
                      <td width="25%" height="10" valign="TOP" class="TableCell"><%=numberFormat.format(numberFormat.parse(amountStrs[i]))%></td>
                    <%}
                    }%>
<!--                  <td width="25%" height="10" valign="TOP" class="TableCell"><% if(baseLineValues != null) { %><%= numberFormat.format(baseLineValues[i]) %><% } else { %>----<% } %></td>-->
                    </tr>
                  <%
               }
          %>
                  </table>
                  </center>
                </td>
              </tr>
            </table>
            <p class="SubtitleHeader"><a href="user_ee.jsp" class="Link1">Back</a></p><p>&nbsp;</p>
            </center>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</body>
</html>
<%
        }  
        catch(Exception e )
        {   
            throw e;            
        }
        finally
        {
            //make sure database connection is closed
            if( history != null ) history.gc();
        }
%>
