<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
</head>
<%
     // Find the program
    LMProgramEnergyExchange program = null;
    LMProgramEnergyExchange[] programs = cache.getEnergyCompanyEnergyExchangePrograms(energyCompanyID);
    
    for( int i = 0; i < programs.length; i++ )
    {
        if( programs[i].getYukonID().intValue() == programID.intValue() )
        {
            program = programs[i];
            break;
        }
    }

    // Find the offer
    LMEnergyExchangeOffer offer = null;

    if( program != null )
    {
        java.util.Vector offers = program.getEnergyExchangeOffers();
        if( offers != null )
        {   // go through this programs offers until we find the right one
            for( int j = 0; j < offers.size(); j++ ) 
            {
                
                
                LMEnergyExchangeOffer o = (LMEnergyExchangeOffer) offers.elementAt(j);
                  
                if(                   
                    o != null &&  
                    o.getOfferID().intValue() == offerID.intValue() )
                {   // found the correct offer
                    offer = o;
                    break;
                }                   
            }
        }
    }

    java.util.Vector offerRevisions = offer.getEnergyExchangeOfferRevisions();
    
    if( offerRevisions.size() > 0 )
    {   
 
        int totalCommitted = 0;
        LMEnergyExchangeCustomer[] customers = cache.getEnergyExchangeCustomers(program.getYukonID().longValue());               
%>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
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
                <td width="310" class="Header3">&nbsp;&nbsp;&nbsp;<cti:text key="energyexchange.text"/></td>
                <td width="235" height = "28" valign="middle">&nbsp;</td>
                
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <table width="657" border="0" cellspacing="0" cellpadding="0" align="left">
                <tr> 
                  <td width="650" class="Main"> 
                    <br>
<p align="center"><b><cti:text key="energyexchange.text"/> 
                      OFFER <%= offer.getOfferID().toString() %> - <%= revisionNumber %></b></p>
                    <% 
           java.util.Date notificationDate = ((LMEnergyExchangeOfferRevision) offerRevisions.get(revisionNumber.intValue())).getNotificationDateTime();          
                      
           if( notificationDate.after(new java.util.Date()) &&
               offer.getRunStatus().trim().equalsIgnoreCase("scheduled") )
           {
        %>
                    <p align="center">Customers will be notified at <%= notificationDate %></p>
                    <% }
           else
           {           
        %>
                    <p align="center">Click on a customer name to view the customer's 
                      offer summary.</p>
                    <%
           }
        %>
                    <table width="600" border="1" cellspacing="0" cellpadding="2" align="center">
                      <tr> 
                        <td width="300" class="HeaderCell">Customer</td>
                        <td width="150" class="HeaderCell">Accept</td>
                        <td width="150" class="HeaderCell">Total (kWh)</td>
                      </tr>
                      <% 
          for( int i = 0; i < customers.length; i++ )
        {
              LMEnergyExchangeCustomer customer = customers[i];

              java.util.Vector customerReplies = customer.getEnergyExchangeCustomerReplies();
              for( int j = 0; j < customerReplies.size(); j++ )
              {
                  LMEnergyExchangeCustomerReply reply = (LMEnergyExchangeCustomerReply) customerReplies.elementAt(j);
				  
                //  out.println("\n reply rev#: " + reply.getOfferID() + "-" + reply.getRevisionNumber() );
                //  out.println("\nrevisionNumber: " + revisionNumber );
                  if( reply.getOfferID().intValue() == offerID.intValue() &&
                      reply.getRevisionNumber().intValue() == revisionNumber.intValue() )
                  {
                      // Add up the total committed
                      int committed = 0;

                      java.util.Vector hourlyReplies = reply.getEnergyExchangeHourlyCustomer();
                      for( int k = 0; k < hourlyReplies.size(); k++ )
                      {
                        LMEnergyExchangeHourlyCustomer hourlyReply = (LMEnergyExchangeHourlyCustomer) hourlyReplies.elementAt(k);
                        committed += hourlyReply.getAmountCommitted().intValue();
                      }

                      totalCommitted += committed;

                        %>
                      <tr> 
                        <td width="300" class="TableCell"><a href="oper_ee.jsp?tab=current&prog=<%= program.getYukonID() %>&offer=<%= offerID %>&rev=<%= revisionNumber %>&cust=<%= customer.getYukonID() %>" class="Link1"><%= customer.getYukonName() %></a></td>
                        <td width="150" class="TableCell"><%= reply.getAcceptStatus() %></td>
                        <td width="150" class="TableCell"><%= committed %></td>
                      </tr>
                      <%
                  }
                }

          }
          %>
                    </table>
                    <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
                      <tr> 
                        <td width="61%"> 
                          <div align="right"> 
                            <!--<a href="UpdateOffers.jsp"><IMG SRC="UpdateButton.gif" width="73" height="22" BORDER="0"></a>-->
                          </div>
                        </td>
                        <td width="13%"> 
                          <div align="right"> 
                            <!-- <a href="TotalSummary.asp"><IMG SRC="CancelButton.gif" width="73" height="22" BORDER="0"></a>-->
                          </div>
                        </td>
						<form name="form2" method="post" action="oper_ee.jsp?tab=revise&prog=<%= programID %>&offer=<%= offerID %>&rev=<%= revisionNumber %>">
                        <td width="13%"> 
                              <input type="submit" name="tab" value="Revise">
                        </td>
						</form>
                        <form name="form2" method="post" action="oper_ee.jsp?tab=close&prog=<%= programID %>&offer=<%= offerID %>">
						<td width="13%"> 
                          <input type="submit" name="tab" value="Close">
                        </td>
						</form>
                      </tr>
                    </table>
                    <p></p>
                    <p> 
                      <center>
                        <span class="MainHeader"><b>TOTAL: <%= totalCommitted %> kW</b></span> 
                      </center>
                    </p>
                    <p> 
                      <%
    }
%>
                    </p>
                  </td>
                </tr>
              </table>
              <p></p>
              <p>&nbsp;</p>
              <p></p>

    
              <p>
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
