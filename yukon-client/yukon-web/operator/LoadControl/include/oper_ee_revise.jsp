<html>
<head>
<%
	Integer programID = new Integer( checker.get("prog") );
	Integer offerID = new Integer( checker.get("offer") );
	Integer revisionNumber = new Integer( checker.get("rev") );

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

	java.util.Vector hrOffers = revision.getEnergyExchangeHourlyOffers();

	for (int i = 0; i < 24; i++)
	{
		double price = ((LMEnergyExchangeHourlyOffer) hrOffers.elementAt(i)).getPrice().doubleValue() / 100;
		if (price == 0)
			priceStrs[i] = "----";
		else
			priceStrs[i] = priceFormat.format( price );

		double amount = ((LMEnergyExchangeHourlyOffer) hrOffers.elementAt(i)).getAmountRequested().doubleValue();
		if (amount == 0)
			amountStrs[i] = "----";
		else
			amountStrs[i] = numberFormat.format( amount );
	}

	if ( request.getParameter("error") == null )
	{
		for( int i = 0; i < 24; i++ )
		{  
			double price = ((LMEnergyExchangeHourlyOffer) hrOffers.elementAt(i)).getPrice().doubleValue() / 100;
			if (price == 0)
				newPriceStrs[i] = "0";
			else
				newPriceStrs[i] = priceFormat.format( price );

			double amount = ((LMEnergyExchangeHourlyOffer) hrOffers.elementAt(i)).getAmountRequested().doubleValue();
			if (amount == 0)
				newAmountStrs[i] = "0";
			else
				newAmountStrs[i] = numberFormat.format( amount );
		}
	}
	else {
		newPriceStrs = (String[]) checker.getObject("prices");
		newAmountStrs = (String[]) checker.getObject("amount");
	}
%>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
   function goBack() {
  location = "<%=request.getContextPath()%>/operator/LoadControl/oper_ee.jsp?tab=current&prog=<%= program.getYukonID() %>&offer=<%= offerID %>&rev=<%= revisionNumber %>"
  }
  //End hiding script -->

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
<table width="657" border="0" cellspacing="0" cellpadding="0" align="left">
<struts:form name="checker" type="com.cannontech.validate.PageBean" action="oper_ee.jsp?tab=revise">
    <tr>      
    <td width="650" class="MainText" valign="top"> 
      <div align="center"><br>
        <input type=hidden name="submitted" value="true">
        <input type=hidden name="progname" value="<%= program.getYukonName() %>">
        <input type=hidden name="prog" value="<%= programID %>">
        <input type=hidden name="offer" value="<%= offerID %>">
        <input type=hidden name="rev" value="<%= revisionNumber %>">
        <input type=hidden name="date" value="<%= eeDateFormat.format(offer.getOfferDate()) %>">
                    <b> <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>"/> - REVISE OFFER </b><br>
                    <br>
                  </div>
      <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
          <tr> 
            <td width="100" class="SubtitleHeader" valign = "top"><p align=RIGHT>Program:</td>
            <td width="100" class="Subtext" valign = "top"><%= program.getYukonName() %></td>
            <td width="100" class="SubtitleHeader" valign = "top"><p align=RIGHT>Notify Date:</td>
            <td width="80" class="Subtext" valign = "top"><struts:text property="notifydate" size="10" pattern="@date"/> 
              <span class = "TableCell"><%= checker.getError("notifydate") %></span></td>
            <td width="100" class="SubtitleHeader" valign = "top"><p align=RIGHT>Notify Time:</td>
            <td width="80" class="Subtext" valign = "top"><struts:text property="notifytime" size="10" pattern="@time"/> 
              <span class = "TableCell"><%= checker.getError("notifytime") %></span></td>
            <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
          </tr>
          <tr> 
            <td width="100" class="SubtitleHeader" valign = "top"><p align=RIGHT>Control Date:</td>
            <td width="100" class="Subtext" valign = "top"><span class = "TableCell"><%= datePart.format(offer.getOfferDate()) %></span></td>
            <td width="100" class="SubtitleHeader" valign = "top"><p align=RIGHT>Expire Date:</td>
            <td width="80" class="Subtext"><struts:text property="expiredate" size="10" pattern="@date"/> 
              <span class = "TableCell"><%= checker.getError("expiredate") %></span></td>
            <td width="100" class="SubtitleHeader" valign = "top"><p align=RIGHT>Expire Time:</td>
            <td width="80" class="Subtext" valign = "top"><struts:text property="expiretime" size="10" pattern="@time"/> 
              <span class = "TableCell"><%= checker.getError("expiretime") %></span></td>
            <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
          </tr>
        </table>
        <table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
          <tr>
            <td width = "100%" class="TableCell" valign="top">
              <p align="center"><%=checker.getError("amounterror")%>
                <%= checker.getError("formaterror") %>
                <%=checker.getError("nooffer")%>
            </td>
          </tr>
        </table>
        <table width="640" border="0" cellspacing="0" cellpadding="5" align="center">
          <tr> 
            
                  <td> 
                    <table width="315" border="1" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td width="61" valign="TOP" class="HeaderCell">Hour Ending</td>
                        <td width="61" valign="TOP" class="HeaderCell">Offer in $/kWh</td>
                        <td width="61" valign="TOP" class="HeaderCell">New Amount</td>
                        <td width="61" valign="TOP" class="HeaderCell">Target in kW</td>
                        <td width="61" valign="TOP" class="HeaderCell">New Target</td>
                      </tr>
                      <%
					for (int i = 0; i < 12; i++) {
						String endingHourStr = String.valueOf(i+1) + ":00";
				%>
                      <tr> 
                        <td width="61" class="TableCell"><%= endingHourStr %></td>
                        <td width="61" class="TableCell"><%= priceStrs[i] %></td>
                        <td width="61" class="TableCell"><struts:text property="prices" size="6" pattern="@real" value="<%= newPriceStrs[i] %>"/></td>
                        <td width="61" class="TableCell"><%= amountStrs[i] %></td>
                        <td width="61" class="TableCell"><struts:text property="amount" size="6" pattern="@real" value="<%= newAmountStrs[i] %>"/><span class="TableCell"><%=checker.getError("amounterror"+String.valueOf(i))%></span></td>
                        </td>
                      </tr>
                      <%
					}
				%>
                    </table>
                  </td>
                  <td> 
                    <table width="315" border="1" cellspacing="0" cellpadding="2">
                      <tr> 
                        <td width="61" valign="TOP" class="HeaderCell">Hour Ending</td>
                        <td width="61" valign="TOP" class="HeaderCell">Offer in $/kWh</td>
                        <td width="61" valign="TOP" class="HeaderCell">New Amount</td>
                        <td width="61" valign="TOP" class="HeaderCell">Target in kW</td>
                        <td width="61" valign="TOP" class="HeaderCell">New Target</td>
                      </tr>
                      <%
					for (int i = 12; i < 24; i++) {
						String endingHourStr = String.valueOf(i+1) + ":00";
				%>
                      <tr> 
                        <td width="61" class="TableCell"><%= endingHourStr %></td>
                        <td width="61" class="TableCell"><%= priceStrs[i] %></td>
                        <td width="61" class="TableCell"><struts:text property="prices" size="6" pattern="@real" value="<%= newPriceStrs[i] %>"/></td>
                        <td width="61" class="TableCell"><%= amountStrs[i] %></td>
                        <td width="61" class="TableCell"><struts:text property="amount" size="6" pattern="@real" value="<%= newAmountStrs[i] %>"/><span class="TableCell"><%=checker.getError("amounterror"+String.valueOf(i))%></span></td>
                        </td>
                      </tr>
                      <%
					}
				%>
                    </table>
                  </td>
                </tr>
              </table>
        <table width="600" border="0" cellspacing="0" cellpadding="4" height="10" align="center">
          <tr> 
            <td width="385" height="10"> 
              <p align=RIGHT> 
                <!-- <img src="DittoButton.gif" width="73" height="22" alt="Ditto Button">&nbsp; -->
            </td>
            <td width="211" valign="TOP" height="10"> 
              <div align="right"> 
                <input type="submit" value="Confirm" border="0" name="image">
              </div>
            </td>
            <td width="80" valign="TOP" height="10"> 
              <div align="right"><input type = "button" value="Cancel" name = "cancel" onclick = "goBack()"></div>
            </td>
          </tr>
        </table>
        <p>&nbsp;</p>
      </td>
    </tr>
   </struts:form> 
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
