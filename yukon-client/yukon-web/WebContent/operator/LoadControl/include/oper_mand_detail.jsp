<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>
<%
    com.cannontech.loadcontrol.data.LMCurtailCustomer customers[] = 
        cache.getCurtailmentCustomers( programID.longValue() );

    LMProgramCurtailment program = cache.getCurtailmentProgram( programID.longValue() );

    boolean showAck = true;

    if( request.getParameter("noack") != null )
        showAck = false;

%>
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
                <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load Response</td>
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
                  <form name="form1" method="get" action="oper_mand.jsp?tab=current">
                    <p><br>
                      <input type="submit" name="tab" value="Current">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=new">
				    <p> 
                      <input type="submit" name="tab" value="New">
                    </p>
				  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=history">
				    <p> 
                      <input type="submit" name="tab" value="History">
                    </p>
					</form>
				   <form name="form1" method="get" action="oper_mand.jsp?tab=programs">
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
<table width="657" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td width="650"valign="top" class="TitleHeader">
      <p align="center"><br>NOTIFICATION - PROGRAM DETAILS FOR:<br><%= program.getYukonName() %></p>
      <p align="center">Click on a company name to view the customer profile.</p>
      <table width="600" border="1" cellspacing="0" cellpadding="2" align="center">
        <tr> 
          <td width="25%" class="HeaderCell">Company</td>
          <td width="15%" class="HeaderCell">Acknowledged</td>
          <td width="15%" class="HeaderCell">Ack Date/Time</td>
          <td width="15%" class="HeaderCell">Ack User</td>
          <td width="15%" class="HeaderCell">Ack Type</td>
        </tr>
        <%
                        // Iterator through all the customers
                        for( int i = 0; i < customers.length; i++)
                        {                            
                            String companyName = customers[i].getCompanyName();

                            String ack = customers[i].getAckStatus();
                            Date ackDateTime = customers[i].getAckDateTime();
                            String l_user = customers[i].getNameOfAckPerson();

                            GregorianCalendar ackCal = new GregorianCalendar();
                            ackCal.setTime(ackDateTime);

                            if( ackCal.get( Calendar.YEAR ) <= 1990 )
                                ackDateTime = null;

                            if( l_user.equalsIgnoreCase("null") )
                                l_user = "-";

                            if( showAck ||
                                (!showAck && ack.equalsIgnoreCase(com.cannontech.loadcontrol.data.LMCurtailCustomer.ACK_UNACKNOWLEDGED)) )
                            {                            
                  %>
        <tr> 
                        <td width="25%" class="TableCell"> <a href="oper_mand.jsp?tab=profile&customerid=<%= customers[i].getCustomerID() %>" class="Link1"><%= companyName %></a> 
                        </td>
          </td>
          <td width="15%" class="TableCell"> 
              <%= ack %> 
          </td>
          <td width="15%" class="TableCell"> 
              <% if( ackDateTime != null ) { %>
              <%= timePart.format(ackDateTime) + " " + datePart.format(ackDateTime) %> 
              <% } else { %>
              - 
              <% } %>
          </td>
          <td width="15%" class="TableCell"> 
              <% if( customers[i].getUserIDname() != null && !customers[i].getUserIDname().equalsIgnoreCase("null") ) { %>
              <%= customers[i].getUserIDname() %> 
              <% } else { %>
              - 
              <% } %>
          </td>
          <td width="15%" class="TableCell"> 
              <% if( customers[i].getUserIDname() != null && !customers[i].getUserIDname().equalsIgnoreCase("null") ) { %>
              Web Response 
              <% } else { %>
              - 
              <% } %>
          </td>
          
        </tr>
        <%
                            }
                        }
                  %>
      </table>
                    <p align="center" class="SubtitleHeader"> 
                      <%
                    if( showAck )
                    {
                %>
                      <a href="oper_mand.jsp?tab=current&prog=<%= programID %>&noack=true" class="Link1"> 
                      Unacknowledged Customers</a> 
                      <%
                    }
                    else
                    {
                %>
                      <a href="oper_mand.jsp?tab=current&prog=<%= programID %>" class="Link1"> 
                      All Customers</a> 
                      <%
                    }
                 %>
                    </p>
                    <p align="center" class="SubtitleHeader"> <a href="oper_mand.jsp?tab=Current" class="Link1">Back to Current Summary</a> <br>
              <br>
    </td>
  </tr>
</table>    
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
