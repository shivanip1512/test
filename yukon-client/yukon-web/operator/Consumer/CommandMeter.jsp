<%@ include file="include/StarsHeader.jsp" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject"%>
<%@ page import="com.cannontech.database.data.pao.YukonPAObject"%>

<%@ page import="com.cannontech.database.cache.functions.PAOFuncs"%>
<%@ page import="com.cannontech.database.data.pao.PAOGroups"%>

<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<%
	int invNo = Integer.parseInt(request.getParameter("InvNo"));
	StarsInventory starsMCT = inventories.getStarsInventory(invNo);
	int deviceID = starsMCT.getDeviceID();

	//get the liteYukonPao using the deviceID
	LiteYukonPAObject liteYukonPao = PAOFuncs.getLiteYukonPAO(deviceID);
%>

<jsp:useBean id="YC_BEAN" class="com.cannontech.yc.gui.YC" scope="session">
	<jsp:setProperty name="YC_BEAN" property="*"/>
</jsp:useBean>
<jsp:setProperty name="YC_BEAN" property="*"/>
<jsp:setProperty name="YC_BEAN" property="deviceID" value="<%=deviceID%>"/>

<% if( request.getParameter("execute") != null){%>
	<jsp:setProperty name="YC_BEAN" property="*"/>
	<jsp:forward page="&lt%= request.getContextPath() %&gt/servlet/CommanderServlet?InvNo=<%=invNo%>"/>
	<%return;
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
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
      <%@ include file="include/HeaderBar.jsp" %>
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
            <% String pageName = "CommandMeter.jsp?InvNo=" + invNo;%>
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
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

 			  <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
   			    <input type="hidden" name="deviceID" value="<%=deviceID%>">
   			    <input type="hidden" name="timeOut" value="8000">

				<input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <tr> 
                  <td width="30%" class="SubtitleHeader" align="right">Execute Command :</td>
                  <td width="70%"> 
                    <select name="command">
                    <%
                      String tempCommand = YC_BEAN.getCommandString().replaceAll("noqueue", "").trim();
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
					</select>
                    <input type="submit" name="execute" value="Execute" onClick="disableButton(this)">
                  </td>
                </tr>
                <tr align="left"> 
                  <td class="SubtitleHeader" colspan="2" valign="top">&nbsp;</td>
                </tr>
              </form>
 			  <form name="textForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
				<input id="redirect" type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
				<input id="referrer" type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>?InvNo=<%= invNo %>">
                <tr> 
                  <td colspan="2"> 
                    <div align="center">
                      <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="20" wrap="VIRTUAL"><%= YC_BEAN.getResultText()%></textarea>
                      <input type="submit" name="clearText" value="Clear Results">
                      <input type="reset" name="refresh" value="Refresh" onClick="window.location.reload()">
                    </div>
                  </td>
                </tr>
                <br>
              </form>

                </table>
                <br>
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
