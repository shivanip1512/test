<html>
<head>
<title>Consumer Energy Services</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
</head>


<script language="JavaScript">

function confirm_form()
{
    checker.submitted.value = "true";
	return true;
}

function decline_form()
{
	checker.submitted.value = "false";
	checker.submit();
}
</script>

<%
  //Look up the customer curtailable amount, use this as the default amount
//  Object[][] gData2 = com.cannontech.util.ServletUtil.executeSQL( session, "select curtailamount from cicustomerbase where customerid=" +  customerID);  
//  String curtailAmount = gData2[0][0].toString();

  // Grab the 24 customers baseline values
  double[] baseLineValues = cache.getCustomerBaseLine( customerID);

  // If we couldn't find a baseline create an empty
  // array so below doesn't bomb
  if( baseLineValues == null )
    baseLineValues = new double[24];
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
          <%@ include file="nav.jsp" %> </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="609" valign="top" bgcolor="#FFFFFF">
            <table width="609" border="0" cellspacing="0" cellpadding="0">
              <tr> 
                <td width="600" class="TitleHeader" valign="top"> 
                  <p align="center"><br>OFFER TO CUSTOMER<br><br>                  
                  <center>
                  <table width="590" border="0" cellspacing="0" cellpadding="5">
                    <tr valign="top"> 
                      <td class="TitleHeader"><p align=RIGHT>Offer ID:</td>
                      <td class="MainText"> <%= revision.getOfferID() + " - " + revision.getRevisionNumber()%></td>
                      <td class="TitleHeader"><p align=RIGHT>Control Date:</td>
                      <td class="MainText"><%= datePart.format( offer.getOfferDate()) %></td>
                      <td class="TitleHeader"><p align=RIGHT>Expires:</td>
                      <td class="MainText"><%= timePart.format( revision.getOfferExpirationDateTime()) + " " + tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) + "  " + datePart.format( revision.getOfferExpirationDateTime()) %></td>
                      <td class="TitleHeader">Curtailable Amount:</td>
                      <td class="MainText">&nbsp;</td> 
                    </tr>
                  </table>
                  <struts:form name="checker" type="com.cannontech.validate.PageBean" scope="session" action="user_ee.jsp?tab=accept"> 
                  <input type=hidden name="submitted">
                  <input type=hidden name="offer" value="<%= revision.getOfferID() %>">
                  <input type=hidden name="rev" value="<%= revision.getRevisionNumber() %>">
                  <input type=hidden name="offerdate" value="<%= datePart.format(offer.getOfferDate()) %>">
                  <input type=hidden name="expiredatetime" value="<%= java.net.URLEncoder.encode( timePart.format(revision.getOfferExpirationDateTime()) + " " + datePart.format(revision.getOfferExpirationDateTime()) ) %>">
                  <font size = "1"><%= checker.getError("formaterror") %></font>
                  <table width="590" border="0" cellspacing="0" cellpadding="5" valign="top">
                    <tr> 
                      <td width="295" valign="top"> 
                        <p> 
                        <center>
                        <table width="295" border="1" cellspacing="0" cellpadding="2">
                          <tr> 
                            <td width="75" height="23" valign="TOP" class="HeaderCell">Hour Ending </td>
                            <td width="75" height="23" valign="TOP" class="HeaderCell">Offer in $ per kWh</td>
                            <td width="75" height="23" valign="TOP" class="HeaderCell">CLR in kW</td>
<!--                            <td width="75" height="23" valign="TOP" class="HeaderCell">Baseline</td>-->
                          </tr>
                  <%
               for( int i = 0; i < 12; i++ ) {
                   String hourStr = hourFormat.format(i+1) + ":00";
          %>
                          <tr> 
                            <td width="75" height="10" class="TableCell"><%= hourStr %></td>
                            <td width="75" height="10" class="TableCell"><%= priceStrs[i] %></td>
                            <td width="75" height="10" class="TableCell"> 
                      <%
				if (amountStrs[i].equals("----")) {
			%>
                              <input type=hidden name="amount" value="-999">
                      ---- 
                      <%
				}
				else {
			%>
                              <struts:text property="amount" size="8" value="<%= amountStrs[i]%>"/><span class="TableCell"><%=checker.getError("amounterror"+String.valueOf(i))%></span> 
                      <%
				}
			%>
                            </td>
            <%
                if( baseLineValues[i] > 0 )
                {                
            %>
<!--                            <td width="75" height="10" class="TableCell"><%= numberFormat.format(baseLineValues[i]) %></td>-->
            <%
                }
                else
                {                
            %>
<!--                            <td width="75" height="10" class="TableCell">-</td>-->
            <%
                }
            %>
                          </tr>
                  <%
               }
          %>
                        </table>
                        </center>
                      </td>
                      <td width="295" valign="top"> 
                        <p> 
                        <center>
                        <table width="295" border="1" cellspacing="0" cellpadding="2">
                          <tr> 
                            <td width="75" height="23" valign="TOP" class="HeaderCell">Hour Ending </td>
                            <td width="75" height="23" valign="TOP" class="HeaderCell">Offer in $ per kWh</td>
                            <td width="75" height="23" valign="TOP" class="HeaderCell">CLR in kW</td>
<!--                            <td width="75" height="23" valign="TOP" class="HeaderCell">Baseline</td>-->
                          </tr>
                  <%
               for( int i = 12; i < 24; i++ ) {
                   String hourStr = hourFormat.format(i+1) + ":00";
          %>
                          <tr> 
                            <td width="75" height="10" valign="TOP" class="TableCell"><%= hourStr %></td>
                            <td width="75" height="10" valign="TOP" class="TableCell"><%= priceStrs[i] %></td>
                            <td width="75" height="10" valign="TOP" class="TableCell"> 
                      <%
				if (amountStrs[i].equals("----")) {
				%>
                              <input type=hidden name="amount" value="-999">
                      ---- 
                      <%
				}
				else {
				%>
                              <struts:text property="amount" size="8" pattern="@real" value="<%= amountStrs[i]%>"/><span class="TableCell"><%=checker.getError("amounterror"+String.valueOf(i))%></span>
                      <%
				}
			%>
                            </td>
                    <%
                if( baseLineValues[i] > 0 )
                {                
            %>
<!--                            <td width="75" height="10" class="TableCell"><%= numberFormat.format(baseLineValues[i]) %></td>-->
            <%
                }
                else
                {                
            %>
<!--                            <td width="75" height="10" class="TableCell">-</td>-->
            <%
                }
            %>

                          </tr>
                  <%
               }
          %>
                        </table>
                        </center>
                      </td>
                    </tr>
                  </table>
                  <span class="TableCell"><%=checker.getError("amounterror")%></span>        
                  <p>IF YOU ACCEPT THIS OFFER, INDICATE YOUR ACCEPTANCE ALONG WITH YOUR<br>COMMITTED LOAD REDUCTION (CLR).</p>
                  <table width="600" border="0" cellspacing="0" cellpadding="0" align="center" class="MainText">
                    <tr> 
                      <td width="83" height="29"><div align="right">Comments:&nbsp;</div></td>
                      <td width="511" height="29"><struts:text property="comments" size="80"/></td>
                    </tr>
                  </table>
                  <table width="580" border="0" cellspacing="0" cellpadding="4" class="MainText">
                    <tr> 
                      <td width="36%" valign="TOP"> 
                        <p align=RIGHT><span class="MainText">Initials:</span>
                      </td>
                      <td width="13%" align="right" valign="TOP"> 
                        <struts:text property="initials" size="8" />
                        <span class="TableCell"><%=checker.getError("initials")%></span>
                      </td>
                      <td width="16%" valign="TOP"> 
                        <div align="right"> 
                          <input type="submit" value="Confirm" border="0" ONCLICK="return confirm_form()">
                        </div>
                      </td>
                      <td width="35%" valign="TOP"> <input TYPE="submit" value="Decline" ONCLICK="return decline_form()"></td>
                    </tr>
                  </table>
                  </struts:form> 
<!--                <form method="get" action="">-->
                  <div align="center"><span class="MainText">
<!--                    If you would like to review the terms of your energy contract, click</span> <br>
                      <input type="submit" name="Input" value="Here">
                      <br>-->
                      If you have questions or problems, call <cti:getProperty propertyid="<%=EnergyBuybackRole.SUPPORT_PHONE_NUMBER%>"/></span></div>
<!--                  </form>-->
                  <div align="center" class="SubtitleHeader"><a href = "user_ee.jsp" class = "Link1"><br>Back</a><br><br></div>
                  </center>
                </td>
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
