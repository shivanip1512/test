<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

</head>
<%@ page import="com.cannontech.loadcontrol.data.LMProgramEnergyExchange" %>
<%@ page import="com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer" %>
<%@ page import="java.util.ArrayList" %>

<%
    LMProgramEnergyExchange[] programs = cache.getEnergyCompanyEnergyExchangePrograms(energyCompanyID);
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
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td valign="top" width="101"> 
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
				  <cti:getProperty propertyid="<%=EnergyBuybackRole.ENERGY_BUYBACK_LABEL%>" defaultvalue="Energy Buyback"/> - PROGRAMS<br>
                  <br>          
				  <table width="620" border="0" cellspacing="0" cellpadding="0" align="center">
          <%
               for( int i = 0; i < programs.length; i += 2 )
               {
                    LMProgramEnergyExchange p = programs[i];   
          %>
					<tr> 
					  <td valign="top"> 
						<table width="250" border="1" cellspacing="0" cellpadding="5" align="center">
						  <tr> 
							<td class="HeaderCell" width="50%"><%= p.getYukonName() %></td>
						  </tr>
                <%
                    LMEnergyExchangeCustomer[] custs = cache.getEnergyExchangeCustomers(p.getYukonID().longValue() );
                    for( int j = 0; j < custs.length; j++ )
                    {                    
                %>
						  <tr> 
							<td class="TableCell" width="50%"><a href="oper_ee.jsp?tab=profile&customerid=<%= custs[j].getCustomerID() %>" class="Link1"><%= custs[j].getCompanyName() %></a></td>
						  </tr>
                <%
                    }
                 %>
					  	</table>
					  </td>
            <%
                    if( i+1 < programs.length )
                    {

                        p = programs[i+1];

               %>
					  <td valign="top"> 
						<table width="250" border="1" cellspacing="0" cellpadding="5" align="center">
						  <tr> 
							<td class="HeaderCell" width="50%" height="29"><%= p.getYukonName() %></td>
						  </tr>
                <%
                    custs = cache.getEnergyExchangeCustomers(p.getYukonID().longValue() );
                    for( int j = 0; j < custs.length; j++ )
                    {                    
                %>
						  <tr> 
							<td class="TableCell" width="50%"><a href="oper_ee.jsp?tab=profile&customerid=<%= custs[j].getCustomerID() %>" class="Link1"><%= custs[j].getCompanyName() %></a></td>
						  </tr>
                <%
                     }
                    }
               }
                %>
						</table>
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
    </td>
  </tr>
</table>
<br>
</body>
</html>
