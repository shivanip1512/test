<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0" text="#FFFFFF">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
               <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;<cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/></td>
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
          <table width="657 border="0" cellspacing="0" cellpadding="0">
            <tr> 
                <td width="650" class="TitleHeader"valign="top"> 
                  <p align="center"><br>
                    <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/> - CURRENT SUMMARY<br>
                    <br>
                  <p align="center">Click on an Offer ID to view the offer summary.</p>
      <p align="center"><span class="SubtitleHeader">Today's <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/> Summary - <%= datePart.format(com.cannontech.util.ServletUtil.getToday(tz)) %></span> 
      <center>
        <table width="620" border="1" cellspacing="0" cellpadding="2">
          <tr valign="top"> 
            <td class="HeaderCell">Offer ID</td>
            <td class="HeaderCell">Program</td>
            <td class="HeaderCell">Status</td>
            <td class="HeaderCell">Notify Date/Time</td>
            <td class="HeaderCell">Expire Date/Time</td>
            <td class="HeaderCell">Committed Total (kWh)</td>
            <td class="HeaderCell">Target Total (kWh)</td>
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
                                                                  reply.getRevisionNumber().intValue() <= revision.getRevisionNumber().intValue() ) 
                                                              {                                                              
                                                                java.util.Vector hourlyReplies = reply.getEnergyExchangeHourlyCustomer();
                                                                for( int o = 0; o < hourlyReplies.size(); o++ )
                                                                {
                                                                    LMEnergyExchangeHourlyCustomer hourlyReply = (LMEnergyExchangeHourlyCustomer) hourlyReplies.elementAt(o);
                                                                    if( hourlyReply.getRevisionNumber().intValue() == reply.getRevisionNumber().intValue())
                                                                      committedTotal += hourlyReply.getAmountCommitted().doubleValue();
                                                                }
                                                              }
                                                         }
                                                     }
                                                 }
                          %>
          <tr valign="top"> 
            <td class="TableCell"><a href="oper_ee.jsp?tab=current&prog=<%= offer.getYukonID() %>&offer=<%= offer.getOfferID() %>&rev=<%= revision.getRevisionNumber() %>" class="Link1"><%= offer.getOfferID() + "-" + revision.getRevisionNumber() %></a></td>
            <td class="TableCell"><%= eeProgs[i].getYukonName() %></td>
            <td class="TableCell"><%= offer.getRunStatus() %></td>
            <td class="TableCell"><%= timePart.format(revision.getNotificationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(revision.getNotificationDateTime()) %></td>
            <td class="TableCell"><%= timePart.format(revision.getOfferExpirationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(revision.getOfferExpirationDateTime()) %></td>
            <td class="TableCell"><%= numberFormat.format(committedTotal) %></td>
            <td class="TableCell"><%= numberFormat.format(targetTotal) %></td>
          </tr>
          <%        
                                           }
                                        }
                                    }                                     
                                }
                             }
                          %>
        </table>
        <br>
      </center> 
      <p align="center"><span class="SubtitleHeader">Tomorrow's 
        <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/> Summary - <%= datePart.format(com.cannontech.util.ServletUtil.getTomorrow(tz)) %></span>
      <table width="620" border="1" cellspacing="0" cellpadding="2" align="center">
        <tr valign="top"> 
          <td class="HeaderCell">Offer ID</td>
          <td class="HeaderCell">Program</td>
          <td class="HeaderCell">Status</td>
          <td class="HeaderCell">Notify Date/Time</td>
          <td class="HeaderCell">Expire Date/Time</td>
          <td class="HeaderCell">Committed Total (kWh)</td>
          <td class="HeaderCell">Target Total (kWh)</td>
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
                                                                  reply.getRevisionNumber().intValue() <= revision.getRevisionNumber().intValue() )
                                                              {                                                              
                                                                java.util.Vector hourlyReplies = reply.getEnergyExchangeHourlyCustomer();
                                                                for( int o = 0; o < hourlyReplies.size(); o++ )
                                                                {
                                                                    LMEnergyExchangeHourlyCustomer hourlyReply = (LMEnergyExchangeHourlyCustomer) hourlyReplies.elementAt(o);
                                                                    if( hourlyReply.getRevisionNumber().intValue() == reply.getRevisionNumber().intValue())
                                                                      committedTotal += hourlyReply.getAmountCommitted().doubleValue();
                                                                }
                                                              }
                                                         }
                                                     }
                                                 }
                          %>
        <tr valign="top"> 
          <td class="TableCell"><a href="oper_ee.jsp?tab=current&prog=<%= offer.getYukonID() %>&offer=<%= offer.getOfferID() %>&rev=<%= revision.getRevisionNumber() %>" class="Link1"><%= offer.getOfferID() + "-" + revision.getRevisionNumber() %></a></td>
          <td class="TableCell"><%= eeProgs[i].getYukonName() %></td>
          <td class="TableCell"><%= offer.getRunStatus() %></td>
          <td class="TableCell"> <%= timePart.format(revision.getNotificationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(revision.getNotificationDateTime()) %></td>
          <td class="TableCell"> <%= timePart.format(revision.getOfferExpirationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(revision.getOfferExpirationDateTime()) %></td>
          <td class="TableCell"> <%= numberFormat.format(committedTotal) %></td>
          <td class="TableCell"> <%= numberFormat.format(targetTotal) %></td>
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
      <p align="center"><span class="SubtitleHeader">Future
        <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/> Summary</span>
      <table width="620" border="1" cellspacing="0" cellpadding="2" align="center">
        <tr valign="top"> 
          <td class="HeaderCell">Offer ID</td>
          <td class="HeaderCell">Program</td>
          <td class="HeaderCell">Status</td>
          <td class="HeaderCell">Notify Date/Time</td>
          <td class="HeaderCell">Expire Date/Time</td>
          <td class="HeaderCell">Committed Total (kWh)</td>
          <td class="HeaderCell">Target Total (kWh)</td>
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
                                            nowCal.get( Calendar.DAY_OF_YEAR ) != offerCal.get(Calendar.DAY_OF_YEAR) &&
                                            nowCal.get( Calendar.DAY_OF_YEAR )+1 != offerCal.get(Calendar.DAY_OF_YEAR) )
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
                                                                  reply.getRevisionNumber().intValue() <= revision.getRevisionNumber().intValue() )
                                                              {                                                              
                                                                java.util.Vector hourlyReplies = reply.getEnergyExchangeHourlyCustomer();
                                                                for( int o = 0; o < hourlyReplies.size(); o++ )
                                                                {
                                                                    LMEnergyExchangeHourlyCustomer hourlyReply = (LMEnergyExchangeHourlyCustomer) hourlyReplies.elementAt(o);
                                                                    if( hourlyReply.getRevisionNumber().intValue() == reply.getRevisionNumber().intValue())
                                                                    committedTotal += hourlyReply.getAmountCommitted().doubleValue();
                                                                }
                                                              }
                                                         }
                                                     }
                                                 }
                          %>
        <tr valign="top"> 
          <td class="TableCell"><a href="oper_ee.jsp?tab=current&prog=<%= offer.getYukonID() %>&offer=<%= offer.getOfferID() %>&rev=<%= revision.getRevisionNumber() %>" class="Link1"><%= offer.getOfferID() + "-" + revision.getRevisionNumber() %></a></td>
          <td class="TableCell"><%= eeProgs[i].getYukonName() %></td>
          <td class="TableCell"><%= offer.getRunStatus() %></td>
          <td class="TableCell"> <%= timePart.format(revision.getNotificationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(revision.getNotificationDateTime()) %></td>
          <td class="TableCell"> <%= timePart.format(revision.getOfferExpirationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format(revision.getOfferExpirationDateTime()) %></td>
          <td class="TableCell"> <%= numberFormat.format(committedTotal) %></td>
          <td class="TableCell"> <%= numberFormat.format(targetTotal) %></td>
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
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
