<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

</head>
<%
String queryStr = "SELECT CUSTOMERID, YUKONPAOBJECT.PAONAME FROM LMENERGYEXCHANGECUSTOMERLIST, YUKONPAOBJECT";
    queryStr += "WHERE LMENERGYEXCHANGECUSTOMERLIST.PROGRAMID = 120 AND CUSTOMERID = YUKONPAOBJECT.PAOBJECTID ";
	queryStr += " ORDER BY CUSTOMERID";

	int programId = 0;
	int offerId = 0;
	int revisionNumber = 0;

	progIdStr = request.getParameter("prog");
	if (progIdStr != null)
		programId = Integer.parseInt(progIdStr);

	offerIdStr = request.getParameter("offer");
	if (offerIdStr != null)
		offerId = Integer.parseInt(offerIdStr);

	revNumStr = request.getParameter("rev");
	if (revNumStr != null)
		revisionNumber = Integer.parseInt(revNumStr);

    System.out.println("programID: " + programId);
System.out.println("offerID: " + offerId);
System.out.println("revisionNumber: " + revisionNumber);

%> 
<body class="Background" leftmargin="0" topmargin="0">
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
                <td width="235"  height = "28" valign="middle">&nbsp;</td>
                
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
                <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/> - HISTORY DETAIL <BR>
                OFFER <%= offerId %> - <%= revisionNumber %></p>
            </div>
            <p align="center" class="MainText">Click on a customer name to view the 
              customer's offer summary.</p>
        <table width="600" border="1" cellspacing="0" cellpadding="2" align="center">
          <tr valign="top"> 
            <td width="300" class="HeaderCell">Customer</td>
            <td width="150" class="HeaderCell">Accept</td>
                <td width="150" class="HeaderCell">Total (kWh)</td>
          </tr>
          <%
	com.cannontech.web.history.EnergyExchangeHistory history = null;
	com.cannontech.web.history.HEnergyExchangeCustomerReply reply = null;
	double totalCommitted = 0;
	
	try {
		history = new com.cannontech.web.history.EnergyExchangeHistory(dbAlias);
		com.cannontech.web.history.HEnergyExchangeProgram[] programs = history.getEnergyExchangePrograms();

		for (int i = 0; i < programs.length; i++)
		{
			if (programs[i].getDeviceId() != programId)
				continue;
            System.out.println("Found program: " + programs[i].getDeviceId());
			com.cannontech.web.history.HEnergyExchangeCustomer[] customers = programs[i].getEnergyExchangeCustomers();
		    System.out.println(customers.length);
			for (int j = 0; j < customers.length; j++)
			{
				reply = customers[j].getEnergyExchangeCustomerReply(offerId, revisionNumber);                
				if (reply == null) continue;
				totalCommitted += reply.getAmountCommitted();
			%>
            <tr valign="top"> 
            <td class="TableCell"><a href="oper_ee.jsp?tab=historyresponse&prog=<%= programId %>&offer=<%= offerId %>&rev=<%= revisionNumber %>&cust=<%= customers[j].getCustomerId() %>" class="Link1"> 
              <%= customers[j].getCustomerName() %> </a></td>
            <td class="TableCell"><%= reply.getAcceptStatus() %></td>
            <td class="TableCell"><%= numberFormat.format( reply.getAmountCommitted() ) %></td>
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
            <p align="center" class="SubtitleHeader">TOTAL: <%= numberFormat.format(totalCommitted) %> kWh</p>
             <p align="center" class="SubtitleHeader"> <a href="oper_ee.jsp?tab=history" class="Link1">Back to History Summary</a><br>
            <p align="center">&nbsp;
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
