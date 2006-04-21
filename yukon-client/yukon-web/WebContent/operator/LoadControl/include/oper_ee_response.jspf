<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

</head>
<%

    /* Purpose is to display a customers response to a specific offer revision 
       Requires the following variables be implicit:
       
       Integer energyCompany
       Integer programID
       Integer offerID
       Integer revisionNumber
    */
                                       
    /* keep this in server timezone */                                                 
    java.text.SimpleDateFormat responseDateFormat = new java.text.SimpleDateFormat("MM/dd/yy");
                                                 
    //Now locate the specific offer and revision
    LMProgramEnergyExchange[] programs = cache.getEnergyCompanyEnergyExchangePrograms(energyCompanyID);
    LMProgramEnergyExchange program = null;
    LMEnergyExchangeOffer offer = null;
    LMEnergyExchangeOfferRevision revision = null;

    if( programs != null )
    {
               
        for( int k = 0; k < programs.length; k++ )
        {
            java.util.Vector offers = programs[k].getEnergyExchangeOffers();

            for( int l = 0; l < offers.size(); l++ )
            {
                LMEnergyExchangeOffer o = (LMEnergyExchangeOffer) offers.elementAt(l);
                if( o.getOfferID().intValue() == offerID.intValue() )
                {
                    java.util.Vector revisions = o.getEnergyExchangeOfferRevisions();
                    if( revisions != null )
                    {
                        for( int m = 0; m < revisions.size(); m++ )
                        {
                            LMEnergyExchangeOfferRevision r = (LMEnergyExchangeOfferRevision) revisions.elementAt(m);
                            if( r.getRevisionNumber().intValue() == revisionNumber.intValue() )
                            {
                                program = programs[k];
                                offer = o;
                                revision = r;
                                break;
                            }
                        }
                    }
                }
            }
        }

    }
                                                       
    // Find the customer object we are dealing with
    LMEnergyExchangeCustomer[] customers = cache.getEnergyExchangeCustomers(program.getYukonID().longValue());
    LMEnergyExchangeCustomer customer = null;

    for( int i = 0; i < customers.length; i++ )
    {
        if( customers[i].getCustomerID().intValue() == customerID.intValue() )
        {
            customer = customers[i];
            break;
        }
    }

    
    
    //Now locate a customer reply, if any
    LMEnergyExchangeCustomerReply reply = null;

    if( customer != null )
    {    
        java.util.Vector replies = customer.getEnergyExchangeCustomerReplies();
        
        if( replies != null )
        {
            for( int j = 0; j < replies.size(); j++ )
            {
                LMEnergyExchangeCustomerReply r = (LMEnergyExchangeCustomerReply) replies.elementAt(j);
                if( r.getOfferID().intValue() == offerID.intValue() &&
                    r.getRevisionNumber().intValue() == revisionNumber.intValue() )
                {
                    reply = r;
                    break;
                }
            }
        }
    }
  
    // sum off all hourly commitments
    int totalCommitted = 0;

    // format hours so the hour always has two digits
    java.text.NumberFormat hourFormat = new java.text.DecimalFormat();
    hourFormat.setMinimumIntegerDigits(2);

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
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr> 
               <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;<cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/></td>
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
            <div align="center"> 
              <p class="TitleHeader"><br>
                <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/> - CUSTOMER SUMMARY<br>
                <%= customer.getCompanyName() %><br>
              </p>
            </div> 
            <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
          <tr> 
            <td width="75" class="SubtitleHeader"><p align=RIGHT>&nbsp;Offer ID:</td> 
            <td width="35" class="Subtext"><%= offerID + "-" + revisionNumber %></td>
            <td width="75" class="SubtitleHeader"><p align=RIGHT>Control Date:</td>
            <td width="75" class="Subtext"><%= responseDateFormat.format( offer.getOfferDate() ) %></td>
            <td width="75" class="SubtitleHeader"><p align=RIGHT>Expires:</td>
            <td width="115" class="Subtext"><%= eeTimeFormat.format( revision.getOfferExpirationDateTime() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + eeDateFormat.format( revision.getOfferExpirationDateTime() ) %>
            <td width="75" class="SubtitleHeader"><p align=RIGHT>User:</td>
            <td width="75" class="Subtext">
              <% if( reply != null ) { %>
              <%= reply.getNameOfAcceptPerson() %> 
              <% } else {%>
              - 
              <% } %>
            </td>
          </tr>
        </table>
        <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
          <tr> 
            <td width="50%" height="84"> 
              <p>
              <center>
                <table width="310" border="1" cellspacing="0" cellpadding="2">
                  <tr> 
                    <td width="90" valign="TOP" class="HeaderCell">Hour Ending</td>
                        <td width="110" valign="TOP" class="HeaderCell">Offer Price in $ per kWh</td>
                        <td width="110" valign="TOP" class="HeaderCell">CLR in kW</td>
                  </tr>
                  <%
              for( int h = 0; h < 12; h++ )
              {  
                  String time;
                  String price = "---";
                  String amount = "---";

                  time = hourFormat.format( h+1 ) + ":00";

                  if( offer != null )
                  {
					    double priceVal = ((LMEnergyExchangeHourlyOffer) revision.getEnergyExchangeHourlyOffers().elementAt(h)).getPrice().doubleValue() / 100;
                        price = priceFormat.format(priceVal);  
                  }

                  if( reply != null && reply.getEnergyExchangeHourlyCustomer() != null &&
                      reply.getEnergyExchangeHourlyCustomer().size() > h )
                  {
                      LMEnergyExchangeHourlyCustomer hc = (LMEnergyExchangeHourlyCustomer) reply.getEnergyExchangeHourlyCustomer().elementAt(h);
                      Double a = hc.getAmountCommitted();

                      totalCommitted += a.longValue();
                      amount = a.toString();                   
                  }
              %>
                  <tr> 
                    <td width="90" valign="TOP" class="TableCell"><%= time %></td>
                    <td width="110" valign="TOP" class="TableCell"><%= price %></td>
                    <td width="110" class="TableCell"><%= amount %></td>
                  </tr>
                  <%
              }
          %>
                </table>
              </center>
            </td>
            <td width="50%" height="84"> 
              <p>
              <center>
                <table width="310" border="1" cellspacing="0" cellpadding="2">
                  <tr> 
                    <td width="90" valign="TOP" class="HeaderCell"> Hour Ending</td>
                        <td width="110" valign="TOP" class="HeaderCell">Offer 
                          Price in $ per kWh</td>
                        <td width="110" valign="TOP" class="HeaderCell">CLR in 
                          kW</td>
                  </tr>
                  <%
              for( int h = 12; h < 24; h++ )
              {  
                  String time;
                  String price = "---";
                  String amount = "---";

                  time = hourFormat.format( h+1 ) + ":00";

                  if( offer != null )
                  {
					  double priceVal = ((LMEnergyExchangeHourlyOffer) revision.getEnergyExchangeHourlyOffers().elementAt(h)).getPrice().doubleValue() / 100;
					  price = priceFormat.format(priceVal);  
                  }

                  if( reply != null && reply.getEnergyExchangeHourlyCustomer() != null &&
                      reply.getEnergyExchangeHourlyCustomer().size() > h )
                  {
                      LMEnergyExchangeHourlyCustomer hc = (LMEnergyExchangeHourlyCustomer) reply.getEnergyExchangeHourlyCustomer().elementAt(h);
                      Double a = hc.getAmountCommitted();

                      totalCommitted += a.longValue();
                      amount = a.toString();
                  }
          %>
                  <tr> 
                    <td width="90" valign="TOP" class="TableCell"><%= time %></td>
                    <td width="110" valign="TOP" class="TableCell"><%= price %></td>
                    <td width="110" class="TableCell"><%= amount %></td>
                  </tr>
                  <%
              }
          %>
                </table>
              </center>
            </td>
          </tr>
        </table>
	<br>
        <table width="600" border="0" cellspacing="0" cellpadding="0" align="center">
          <tr> 
            <td width="83" height="29"  class="SubtitleHeader"> 
              <div align="right">Comments:&nbsp;</div>
            </td>
            <td class="Subtext" width="511" height="29"><%= reply.getEnergyExchangeNotes() %>
            </td>
          </tr>
        </table>
        <br>
        <p> 
          <center>
                <span class="SubtitleHeader">TOTAL: <%= totalCommitted %> kWh</span> 
              </center>
        </p>
            <p align="center" class="SubtitleHeader"> <a href="<%= referrer %>" class="Link1">Back to Customer List</a> 
              <br>
              <br>
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
