<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0" text="#FFFFFF">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height = "28" class="Header3">&nbsp;&nbsp;&nbsp;<cti:text key="energyexchange.text"/></td>
                <td width="235" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="Link3">Log 
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
          <table width="657 border="0" cellspacing="0" cellpadding="0">
  <tr> 
                <td width="650" class="Main"valign="top"> 
                  <p align="center"><b><br>
                    CURRENT SUMMARY</b> <br>
                    <br>
                  <p align="center">Click on an Offer ID to view the offer summary.</p>
      <p align="center"><span class="MainHeader"><b>Today's <cti:text key="energyexchange.text"/> Summary - <%= datePart.format(com.cannontech.util.ServletUtil.getToday()) %></b></span> 
      <center>
        <table width="620" border="1" cellspacing="0" cellpadding="2">
          <tr valign="top"> 
            <td width="10%" class="HeaderCell">Offer ID</td>
            <td width="18%" class="HeaderCell">Program</td>
            <td width="12%" class="HeaderCell">Status</td>
            <td width="18%" class="HeaderCell">Notify Date/Time</td>
            <td width="18%" class="HeaderCell">Expire Date/Time</td>
                        <td width="12%" class="HeaderCell">Committed Total (kW)</td>
                        <td width="12%" class="HeaderCell">Target Total (kW)</td>
          </tr>
          <%
                          {
                        
                                LMProgramEnergyExchange[] eeProgs = cache.getEnergyCompanyEnergyExchangePrograms(energyCompanyID);
                                                       
                                for( int i = 0; i < eeProgs.length; i++ )
                                {
                                    for( int j = 0; j < eeProgs[i].getEnergyExchangeOffers().size(); j++ )
                                    {
                                        LMEnergyExchangeOffer offer = (LMEnergyExchangeOffer) eeProgs[i].getEnergyExchangeOffers().elementAt(j);

                                        // Check if it is today                                   
                                        GregorianCalendar nowCal = new GregorianCalendar();
                                        GregorianCalendar offerCal = new GregorianCalendar();

                                        nowCal.setTime(new Date());
                                        offerCal.setTime(offer.getOfferDate());

                                        if( nowCal.get( Calendar.YEAR ) == offerCal.get(Calendar.YEAR) &&
                                            nowCal.get( Calendar.DAY_OF_YEAR ) == offerCal.get(Calendar.DAY_OF_YEAR) )
                                        {                                    
                                            // the offer is for today, now show all revisions                                            
                                            for( int k = 0; k < offer.getEnergyExchangeOfferRevisions().size(); k++ )
                                            {                                                
                                                LMEnergyExchangeOfferRevision revision =  
                                                    (LMEnergyExchangeOfferRevision) offer.getEnergyExchangeOfferRevisions().elementAt(k);

                                                // Add up the total target from all the hourly offers
                                                 double targetTotal = 0.0;
                                                 java.util.Vector hourlyOffers = revision.getEnergyExchangeHourlyOffers();
                                                 for( int l = 0; l < hourlyOffers.size(); l++ )
                                                 {
                                                     LMEnergyExchangeHourlyOffer hourOffer = (LMEnergyExchangeHourlyOffer) hourlyOffers.elementAt(l);
                                                     targetTotal += hourOffer.getAmountRequested().doubleValue();
                                                 }
                                                 
                                                // Add up all the commited from the customer replies
                                                 double committedTotal = 0.0;
                                                 LMEnergyExchangeCustomer[] progCustomers = cache.getEnergyExchangeCustomers(eeProgs[i].getYukonID().longValue());

                                                 if( progCustomers != null )
                                                 {
                                                     for( int m = 0; m < progCustomers.length; m++ )
                                                     {
                                                         java.util.Vector replies = progCustomers[m].getEnergyExchangeCustomerReplies();
                                                         for( int n = 0; n < replies.size(); n++ )
                                                         {
                                                              LMEnergyExchangeCustomerReply reply = (LMEnergyExchangeCustomerReply) replies.elementAt(n);
                                                              if( reply.getOfferID().intValue() == revision.getOfferID().intValue() &&
                                                                  reply.getRevisionNumber().intValue() == revision.getRevisionNumber().intValue() )
                                                              {                                                              
                                                                java.util.Vector hourlyReplies = reply.getEnergyExchangeHourlyCustomer();
                                                                for( int o = 0; o < hourlyReplies.size(); o++ )
                                                                {
                                                                    LMEnergyExchangeHourlyCustomer hourlyReply = (LMEnergyExchangeHourlyCustomer) hourlyReplies.elementAt(o);
                                                                    committedTotal += hourlyReply.getAmountCommitted().doubleValue();
                                                                }
                                                              }
                                                         }
                                                     }
                                                 }
                          %>
          <tr> 
            <td class="TableCell"><a href="oper_ee.jsp?tab=current&prog=<%= offer.getYukonID() %>&offer=<%= offer.getOfferID() %>&rev=<%= revision.getRevisionNumber() %>" class="Link1"><%= offer.getOfferID() + "-" + revision.getRevisionNumber() %></a></td>
            <td class="TableCell"><%= eeProgs[i].getYukonName() %></td>
            <td class="TableCell"><%= offer.getRunStatus() %></td>
            <td class="TableCell"><%= timePart.format(revision.getNotificationDateTime()) + " " + datePart.format(revision.getNotificationDateTime()) %></td>
            <td class="TableCell"><%= timePart.format(revision.getOfferExpirationDateTime()) + " " + datePart.format(revision.getOfferExpirationDateTime()) %></td>
            <td class="TableCell"><%= committedTotal %></td>
            <td class="TableCell"><%= targetTotal %></td>
          </tr>
          <%        
                                           }
                                        }
                                    }                                    
                                }
                             }
                          %>
        </table>
      </center>
      <p>&nbsp; 
      <p align="center"><span class="MainHeader"><b>Tomorrow's 
        <cti:text key="energyexchange.text"/> Summary - <%= datePart.format(com.cannontech.util.ServletUtil.getTommorow()) %></b></span>
      <table width="620" border="1" cellspacing="0" cellpadding="2" align="center">
        <tr valign="top"> 
          <td width="10%" class="HeaderCell">Offer ID</td>
          <td width="18%" class="HeaderCell">Program</td>
          <td width="12%" class="HeaderCell">Status</td>
          <td width="18%" class="HeaderCell">Notify Date/Time</td>
          <td width="18%" class="HeaderCell">Expire Date/Time</td>
                      <td width="12%" class="HeaderCell">Committed Total (kW)</td>
                      <td width="12%" class="HeaderCell">Target Total (kW)</td>
        </tr>
        <%
                          {
                        
                                LMProgramEnergyExchange[] eeProgs = cache.getEnergyCompanyEnergyExchangePrograms(energyCompanyID);
//out.println("found: " + eeProgs.length + " ee progs for ee company id: " + energyCompanyID);
                                for( int i = 0; i < eeProgs.length; i++ )
                                {
                                  //out.println("found: " + eeProgs[i].getEnergyExchangeOffers().size() + " offers :P");
                                    for( int j = 0; j < eeProgs[i].getEnergyExchangeOffers().size(); j++ )
                                    {   
                                        LMEnergyExchangeOffer offer = (LMEnergyExchangeOffer) eeProgs[i].getEnergyExchangeOffers().elementAt(j);

                                        // Check if it is today                                   
                                        GregorianCalendar nowCal = new GregorianCalendar();
                                        GregorianCalendar offerCal = new GregorianCalendar();

                                        nowCal.setTime(new Date());
                                        offerCal.setTime(offer.getOfferDate());

                                        if( nowCal.get( Calendar.YEAR ) == offerCal.get(Calendar.YEAR) &&
                                            nowCal.get( Calendar.DAY_OF_YEAR )+1 == offerCal.get(Calendar.DAY_OF_YEAR) )
                                        {                                    
                                            // the offer is for today, now show all revisions 
                                            //out.println("found: " + offer.getEnergyExchangeOfferRevisions().size() + " revisions");
                                            for( int k = 0; k < offer.getEnergyExchangeOfferRevisions().size(); k++ )
                                            {                                                
                                                LMEnergyExchangeOfferRevision revision =  
                                                    (LMEnergyExchangeOfferRevision) offer.getEnergyExchangeOfferRevisions().elementAt(k);

                                                // Add up the total target from all the hourly offers
                                                 double targetTotal = 0.0;
                                                 java.util.Vector hourlyOffers = revision.getEnergyExchangeHourlyOffers();
                                                // out.println("found: " + hourlyOffers.size() + " hourly!!>>   ");
                                                 for( int l = 0; l < hourlyOffers.size(); l++ )
                                                 {
                                                     LMEnergyExchangeHourlyOffer hourOffer = (LMEnergyExchangeHourlyOffer) hourlyOffers.elementAt(l);
                                                     targetTotal += hourOffer.getAmountRequested().doubleValue();
                                                 }
                                                 
                                                // Add up all the commited from the customer replies
                                                 double committedTotal = 0.0;
                                                 LMEnergyExchangeCustomer[] progCustomers = cache.getEnergyExchangeCustomers(eeProgs[i].getYukonID().longValue());

                                                 if( progCustomers != null )
                                                 {
                                                     for( int m = 0; m < progCustomers.length; m++ )
                                                     {
                                                         java.util.Vector replies = progCustomers[m].getEnergyExchangeCustomerReplies();
                                                         for( int n = 0; n < replies.size(); n++ )
                                                         {
                                                              LMEnergyExchangeCustomerReply reply = (LMEnergyExchangeCustomerReply) replies.elementAt(n);
                                                              if( reply.getOfferID().intValue() == revision.getOfferID().intValue() &&
                                                                  reply.getRevisionNumber().intValue() == revision.getRevisionNumber().intValue() )
                                                              {                                                              
                                                                java.util.Vector hourlyReplies = reply.getEnergyExchangeHourlyCustomer();
                                                                for( int o = 0; o < hourlyReplies.size(); o++ )
                                                                {
                                                                    LMEnergyExchangeHourlyCustomer hourlyReply = (LMEnergyExchangeHourlyCustomer) hourlyReplies.elementAt(o);
                                                                    committedTotal += hourlyReply.getAmountCommitted().doubleValue();
                                                                }
                                                              }
                                                         }
                                                     }
                                                 }
                          %>
        <tr> 
          <td class="TableCell"><a href="oper_ee.jsp?tab=current&prog=<%= offer.getYukonID() %>&offer=<%= offer.getOfferID() %>&rev=<%= revision.getRevisionNumber() %>" class="Link1"><%= offer.getOfferID() + "-" + revision.getRevisionNumber() %></a></td>
          <td class="TableCell"><%= eeProgs[i].getYukonName() %></td>
          <td class="TableCell"><%= offer.getRunStatus() %></td>
          <td class="TableCell"> <%= timePart.format(revision.getNotificationDateTime()) + " " + datePart.format(revision.getNotificationDateTime()) %></td>
          <td class="TableCell"> <%= timePart.format(revision.getOfferExpirationDateTime()) + " " + datePart.format(revision.getOfferExpirationDateTime()) %></td>
          <td class="TableCell"> <%= committedTotal %></td>
          <td class="TableCell"> <%= targetTotal %></td>
        </tr>
        <%        
                                           }
                                        }
                                    }                                    
                                }
                             }
                          %>
      </table>
    <p>&nbsp;</p></td>
  </tr>
</table>


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
