
<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>

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
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr>
			  <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Buyback</td> 
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log 
                    Off</a>&nbsp;</span></div>
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
              <div align="center"><center>
                    <p><br>
 
                    <%
	if (pending != null) {
		if (pending.equalsIgnoreCase("confirm"))
			out.println("<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><table width=\"500\" border=\"1\" cellpadding = \"10\" cellspacing = \"0\" height=\"150\" align = \"center\"><tr><td width=\"100%\"><CENTER><FONT FACE=\"Arial, Helvetica, sans-serif\">Your confirmation of the offer has been sent, please wait...</FONT></CENTER></td></tr></table><br>");
		else if (pending.equalsIgnoreCase("decline"))
			out.println("<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p><table width=\"500\" border=\"1\" cellpadding = \"10\" cellspacing = \"0\" height=\"150\" align = \"center\"><tr><td width=\"100%\"><CENTER><FONT FACE=\"Arial, Helvetica, sans-serif\">Your decision to decline the offer has been sent, please wait...</FONT></CENTER></td></tr></table><br>");
	}
	else {
		if (numNewOffers > 0)
		{
%>
                    <p class="TitleHeader">YOU HAVE A NEW OFFER</p>
                    <table width="590" border="1" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td width="20%" height="26" class="HeaderCell">Offer ID</td>
                        <td width="20%" height="26" class="HeaderCell">Offer Date</td>
                        <td width="30%" height="26" class="HeaderCell">Notify Date/Time</td>
                        <td width="30%" height="26" class="HeaderCell">Expire Date/Time</td>
                      </tr>
                      <%
			for (int i = 0; i < offerList.size(); i++)
			{
				Boolean newOffer = (Boolean) newOfferList.get(i);
				if ( newOffer != null && newOffer.booleanValue() )
				{
					offer = (LMEnergyExchangeOffer) offerList.get(i);
					revision = (LMEnergyExchangeOfferRevision) revisionList.get(i);
%>
                      <tr> 
                        <td width="20%" height="10" class="TableCell"><a href="user_ee.jsp?tab=accept&offer=<%= revision.getOfferID() %>&rev=<%= revision.getRevisionNumber() %>"><%= revision.getOfferID() + " - " + revision.getRevisionNumber()%></a></td>
                        <td width="20%" height="10" class="TableCell"><%= datePart.format( offer.getOfferDate()) %></td>
                        <td width="30%" height="10" class="TableCell"><%= timePart.format( revision.getNotificationDateTime() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format( revision.getNotificationDateTime() ) %>
                         <td width="30%" height="10" class="TableCell"><%= timePart.format( revision.getOfferExpirationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format( revision.getOfferExpirationDateTime()) %></td>
                      </tr>
                      <%
				}
			}
%>
                    </table>
                    <%
		 }

		if (numAcceptedOffers > 0)
		{
%>
                    <p class="TitleHeader">RECENTLY RESPONDED OFFERS</p>
                    <table width="590" border="1" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td width="15%" height="26" class="HeaderCell">Offer ID</td>
                        <td width="20%" height="26" class="HeaderCell">Offer Date</td>
                        <td width="20%" height="26" class="HeaderCell">Accept</td>
                        <td width="30%" height="26" class="HeaderCell">Accept Date/Time</td> 
                        <td width="15%" height="26" class="HeaderCell">User</td>
                      </tr>
                      <%		
			for (int i = 0; i < offerList.size(); i++)
			{
				Boolean newOffer = (Boolean) newOfferList.get(i);
				if ( newOffer != null && !newOffer.booleanValue() )
				{
					offer = (LMEnergyExchangeOffer) offerList.get(i);
					revision = (LMEnergyExchangeOfferRevision) revisionList.get(i);
					reply = (LMEnergyExchangeCustomerReply) replyList.get(i);

                    if( reply != null )
                    {                    
%>
                      <tr> 
                        <td width="15%" height="10" class="TableCell"><a href="user_ee.jsp?tab=offer&offer=<%= revision.getOfferID() %>&rev=<%= revision.getRevisionNumber() %>"><%= revision.getOfferID() + " - " + revision.getRevisionNumber()%></a></td>
                        <td width="20%" height="10" class="TableCell"><%= datePart.format( offer.getOfferDate()) %></td>
                        <td width="20%" height="10" class="TableCell"><%= reply.getAcceptStatus() %></td>
                        <td width="30%" height="10" class="TableCell"><%= timePart.format( reply.getAcceptDateTime() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format( reply.getAcceptDateTime() ) %></td>
                        <td width="15%" height="10" bgcolor="#CCCCCC" class="TableCell"><%= reply.getNameOfAcceptPerson() %></td>
                      </tr>
                      <%
                    }
				}
			}
%>
                    </table>
                    <%
		}

		if (numNewOffers == 0 && numAcceptedOffers == 0)
		{
%>
                    <p class="TitleHeader">NO OFFERS AT THIS TIME</p>
                    <%
		}

        com.cannontech.web.history.EnergyExchangeHistory history = null;
		com.cannontech.web.history.HEnergyExchangeProgram[] historyPrograms = null; 
		com.cannontech.web.history.HEnergyExchangeProgramOffer[] historyProgramOffers = null; 

        try
        {        
            history = new com.cannontech.web.history.EnergyExchangeHistory(dbAlias);
		    historyPrograms = history.getEnergyExchangePrograms();
			historyProgramOffers = history.getEnergyExchangeProgramOffers();

		    boolean foundReplies = false;
		    com.cannontech.web.history.HEnergyExchangeCustomerReply[] replies = null;
                  
		for (int i = 0; i < historyPrograms.length; i++)
		{
		   com.cannontech.web.history.HEnergyExchangeCustomer[] customers = historyPrograms[i].getEnergyExchangeCustomers();

			for (int j = 0; j < customers.length; j++)
			{
				if ( customers[j].getCustomerId() == customerID )
				{
					replies = customers[j].getEnergyExchangeCustomerReplies();
					foundReplies = true;
					break;
				}
			}

			if (foundReplies)
				break;
		}

		if (!foundReplies)
			replies = new com.cannontech.web.history.HEnergyExchangeCustomerReply[0];
%>
                    <p class="TitleHeader">CURTAILMENT HISTORY</p>
                    <table width="590" border="1" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td width="15%" height="26" class="HeaderCell">Offer ID</td>
                        <td width="20%" height="26" class="HeaderCell">Offer Date</td>
                        <td width="20%" height="26" class="HeaderCell">Accept</td>
                        <td width="30%" height="26" class="HeaderCell">Accept Date/Time</td>
                        <td width="15%" height="26" class="HeaderCell">User</td>
                      </tr>
                      <%
		for (int i = 0; i < replies.length; i++)
		{
			com.cannontech.web.history.HEnergyExchangeCustomerReply historyReply = replies[i];

			String offerDateStr = "-";
			for (int x = 0; x < historyProgramOffers.length; x++)
			{
				if(  historyReply.getOfferId() == historyProgramOffers[x].getOfferId())
				{
					if(!historyProgramOffers[x].getRunStatus().equalsIgnoreCase("open"))
					{
						offerDateStr = datePart.format(historyProgramOffers[x].getOfferDate());
%>
                      <tr> 
                        <td width="15%" height="10" class="TableCell"> <a href="user_ee.jsp?tab=offer&offer=<%= historyReply.getOfferId() %>&rev=<%= historyReply.getRevisionNumber() %>"><%= historyReply.getOfferId() + " - " + historyReply.getRevisionNumber()%></a> 
                        </td>
                        <td width="20%" height="10" class="TableCell"><%= offerDateStr %></td>
                        <td width="20%" height="10" class="TableCell"><%= historyReply.getAcceptStatus() %></td>
                        <td width="30%" height="10" class="TableCell"><%= timePart.format( historyReply.getAcceptDateTime() ) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format( historyReply.getAcceptDateTime() ) %></td>
                        <td width="15%" height="10" class="TableCell"><%= historyReply.getNameOfAcceptPerson() %></td>
                      </tr>
                      <%	
                    }
                }
			}
		}
%>
                    </table>
                    <%
	}
    
    catch(Exception e )
    {
        
    }
    finally
    {
        history.gc();
    }
    }
%>
                    <br>
                  </center>
                </div>
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
