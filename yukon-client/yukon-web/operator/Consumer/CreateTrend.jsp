<%@ page import="com.cannontech.database.data.customer.Customer" %>
<%@ page import="com.cannontech.database.Transaction" %>
<%
//Update the GraphCustomer list when save button pressed.
/*java.util.Enumeration enumx = request.getParameterNames();
while (enumx.hasMoreElements()) {
  	String ele = enumx.nextElement().toString();
	out.println(" --" + ele + " " + request.getParameter(ele));
}*/
if (request.getParameter("saveList") != null && Boolean.valueOf(request.getParameter("saveList")).booleanValue())
{
	String custIDStr = request.getParameter("custID");
	Integer custID = Integer.valueOf(custIDStr);
	GraphCustomerList.deleteGraphCustomerList(custID);
	java.util.Enumeration enum1 = request.getParameterNames();
	int count = 0;
	try{
	
		Customer cust = (Customer)com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
								new com.cannontech.database.data.lite.LiteCustomer(custID.intValue()));
		Transaction t = Transaction.createTransaction( Transaction.RETRIEVE, cust);
		cust = (Customer)t.execute();
		while (enum1.hasMoreElements())
		{
			String ele = enum1.nextElement().toString();
	  	  	if(ele.startsWith("custGraphs")) {
				GraphCustomerList gcl = new GraphCustomerList();
				gcl.setCustomerID(custID);
				gcl.setCustomerOrder(new Integer(count++));
				gcl.setGraphDefinitionID(new Integer(request.getParameter(ele)));
				cust.getGraphVector().add(gcl);
			}
		}
		t = Transaction.createTransaction(Transaction.UPDATE, cust);
		cust = (Customer)t.execute();
	}
	catch( java.lang.Exception e){
		com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
	}
	
}%>	

<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<SCRIPT language="JavaScript">
function disableButton(x)
{
	document.newTrendForm.saveList.value="true";
	x.disabled = true;
	document.newTrendForm.submit();
}
</SCRIPT>
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td> 
      <%@ include file="include/HeaderBar.jsp" %>
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
          <td  valign="top" width="101"><% String pageName = "CreateTrend.jsp"; %><%@ include file="include/Nav.jsp" %></td>
          <td width="1" bgcolor="#000000"><img src=""../WebConfig/yukon/Icons/VerticalRule.gif"" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div class = "MainText" align="center"><% String header = "ACCOUNT - CREATE NEW TREND"; %><%@ include file="include/InfoSearchBar.jsp" %>
			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
              
              <form name = "newTrendForm" method="POST" action="CreateTrend.jsp">
                <input type="hidden" name="custID" value="<%=account.getCustomerID()%>">
                <input type="hidden" name="saveList" value="false">
                <table width="350" border="0" height="179" cellspacing = "0">
                  <tr>
                    <td>
                      <table class = "TableCell" width="100%" border="0" cellspacing = "0" cellpadding = "1">
                        <tr> 
                          <td colspan = "2"><span class="SubtitleHeader">AVAILABLE TRENDS</span> 
                            <hr>
                          </td>
                        </tr>
                        <%
                        //Get all trends available TODO (for this energy company only!!!)
						DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
						synchronized( cache )
						{
						  int i = 0;
						  Iterator iter = cache.getAllGraphDefinitions().iterator();
						  while( iter.hasNext() )
						  {
							LiteGraphDefinition lGDef = (LiteGraphDefinition) iter.next();
							%>
						<tr>
                          <td width="35"> 
                            <input type="checkbox" name="custGraphs<%=i++%>" value="<%=lGDef.getLiteID()%>"
							  <%
							  for (int j = 0; j < custGraphs.size(); j++) {
							    GraphCustomerList gcl = (GraphCustomerList) custGraphs.get(j);
							    if( gcl.getGraphDefinitionID().intValue() == lGDef.getLiteID())
							    {
								  out.println("checked");
								  break;
								}
							  }
  							  out.println(">");
  							  %>
                          </td>
  						  <td width="300"><%=lGDef.getName()%></td>
						</tr>
  				        <%}
                        }%>
                      </table>
                    </td>
                  </tr>
                </table>
                <br>
                <table width="150" border="0">
                <tr>
                  <td align = "center" width = "50%"> 
                    <input type="submit" name="Save" value="Save" onclick="disableButton(this)">
                  </td>
                  <td width = "50%"> 
                    <input type="reset" name="Cancel" value="Cancel" >
                  </td>
                </tr>
              </table><br>
              </form>
            </div>
          
           </td>
          <td width="1" bgcolor="#000000"><img src=""../WebConfig/yukon/Icons/VerticalRule.gif"" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br>
</body>
</html>
