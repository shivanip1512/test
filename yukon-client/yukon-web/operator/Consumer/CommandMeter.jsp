<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>

<%@ page import="com.cannontech.database.cache.functions.PAOFuncs"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>

<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsMCT starsMCT = inventories.getStarsMCT(invNo - inventories.getStarsLMHardwareCount());
	int deviceID = starsMCT.getDeviceID();

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);
%>

<jsp:useBean id="ycBean" class="com.cannontech.yc.gui.YC" scope="session">
	<jsp:setProperty name="ycBean" property="*"/>
</jsp:useBean>
<jsp:setProperty name="ycBean" property="*"/>
<jsp:setProperty name="ycBean" property="deviceID" value="<%=deviceID%>"/>

<% if( request.getParameter("execute") != null){%>
	<jsp:setProperty name="ycBean" property="*"/>
	<jsp:forward page="&lt%= request.getContextPath() %&gt/servlet/CommanderServlet?InvNo=<%=invNo%>"/>
	<%System.out.println(" HERE" );
	 return;
	}%>

<%
String[] defaultKeys = {
	"Read Energy", 
	"Read Energy (update)"
};
String[] defaultValues = {
	"getvalue kwh", 
	"getvalue kwh update"
};
//comands for disconnect meters
String[] disconnectKeys = {
	"Read Energy", 
	"Read Energy (update)", 
	"Read Disconnect Status", 
	"Disconnect Meter", 
	"Connect Meter"
};
String[] disconnectValues = {
	"getvalue kwh", 
	"getvalue kwh update", 
	"getstatus disconnect", 
	"control disconnect", 
	"control connect"
};
%>

<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<SCRIPT language="JavaScript">
function disableButton(x)
{
	x.disabled = true;
	document.commandForm.submit();
}
</SCRIPT>
</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="ConsumerImage.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Customer Account Information&nbsp;&nbsp;</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                    <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="102" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <% String pageName = "CommandMeter.jsp?InvNo=" + deviceID;%>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "METER - CONTROL COMMANDS"; %>
			  <br>
			  <table width="100%" border="0" cellspacing="0" cellpadding="3">
			    <tr>
			      <td align="center" class="TitleHeader"><%= header %></td>
			    </tr>
			  </table>

			  <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
                <table width="575" border="0" cellspacing="0" cellpadding="3">
                  <tr> 
                    <td width="30%" class="SubtitleHeader" align="right"> Meter Name:</td>
                    <td width="70%" class="TableCell"><%=liteYukonPao.getPaoName()%></td>
                  </tr>
                  <tr> 
                    <td width="30%" class="SubtitleHeader" align="right">Meter Type:</td>
                    <td width="70%" class="TableCell"><%=PAOGroups.getPAOTypeString(liteYukonPao.getType())%></td>
                  </tr>

<!--                  <%@ include file="include/Command.jsp"%>-->
 			  <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet?InvNo=<%=invNo%>">
   			    <input type="hidden" name="deviceID" value="<%=deviceID%>">

				<input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <tr> 
                  <td width="30%" class="SubtitleHeader" align="right">Execute Command :</td>
                  <td width="70%"> 
                    <select name="command">
                    <%
                      String tempCommand = ycBean.getCommand().replaceAll("noqueue", "").trim();
                      com.cannontech.common.util.KeysAndValues keysAndVals = 
						new com.cannontech.common.util.KeysAndValues(defaultKeys, defaultValues);
                      if( DeviceTypesFuncs.isDisconnectMCT(liteYukonPao.getType()))
						keysAndVals = new com.cannontech.common.util.KeysAndValues(disconnectKeys, disconnectValues);
                      
					  for (int i = 0; i < keysAndVals.getKeys().length; i++)
                  	  {
                  	    out.print("<OPTION value='" + keysAndVals.getValues()[i] + "' ");
                  	  	if( keysAndVals.getValues()[i].equalsIgnoreCase(tempCommand))
                  	  		out.print("SELECTED");
               	  		out.println(">" + keysAndVals.getKeys()[i] + "</option>");
                 	  }%>
                    <input type="submit" name="execute" value="Execute" onClick="disableButton(this)">
                  </td>
                </tr>
                <tr align="left"> 
                  <td class="SubtitleHeader" colspan="2" valign="top">&nbsp;</td>
                </tr>
              </form>
 			  <form name="textForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet?InvNo=<%=invNo%>">
				<input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <tr> 
                  <td colspan="2"> 
                    <div align="center">
                      <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="20" wrap="VIRTUAL"><%= ycBean.getResultText()%></textarea>
                      <input type="submit" name="clearText" value="Clear Results">
                      <input type="reset" name="refresh" value="Refresh"">
                    </div>
                  </td>
                </tr>
                <br>
              </form>

                </table>
                <br>
            </div>
            </td>
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
