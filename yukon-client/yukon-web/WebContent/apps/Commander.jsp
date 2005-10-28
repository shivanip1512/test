<%@ page import="com.cannontech.database.data.lite.LiteDeviceTypeCommand"%> 
<%@ page import="com.cannontech.database.data.lite.LiteCommand"%> 
<%@ page import="com.cannontech.database.cache.functions.CommandFuncs"%>
<%@ page import="com.cannontech.database.db.command.CommandCategory"%>
<%
//set the deviceID of the YC_BEAN
YC_BEAN.setDeviceID(deviceID);

if( serialType.equals("xcom"))
{
	YC_BEAN.setDeviceType(CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL);
	YC_BEAN.setSerialNumber(YC_BEAN.getDeviceType(), serialNum);
}
else if( serialType.equals("vcom"))
{
	YC_BEAN.setDeviceType(CommandCategory.STRING_CMD_VERSACOM_SERIAL);
	YC_BEAN.setSerialNumber(YC_BEAN.getDeviceType(), serialNum);
}
else if( serialType.equals("sa205") )
{
	YC_BEAN.setDeviceType(CommandCategory.STRING_CMD_SA205_SERIAL);
	YC_BEAN.setSerialNumber(YC_BEAN.getDeviceType(), serialNum);
}
else if( serialType.equals("sa305"))
{
	YC_BEAN.setDeviceType(CommandCategory.STRING_CMD_SA305_SERIAL);
	YC_BEAN.setSerialNumber(YC_BEAN.getDeviceType(), serialNum);
}

else
	YC_BEAN.setDeviceType(deviceID);	//default to the type of the deviceID selected
%>          
	<SCRIPT language="JavaScript">
	function disableButton(x)
	{
		x.disabled = true;
		document.commandForm.submit();
	}
	function loadCommand()
	{
		document.commandForm.command.value = document.commandForm.commonCommand.value;
	}
	</SCRIPT>

          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center">
              <% String header = "CONTROL COMMANDS"; %>
			  <br>
			<table width="100%" border="0" cellspacing="0" cellpadding="3">
			  <tr>
			    <td align="center" class="TitleHeader"><%= header %></td>
			  </tr>
			</table>

			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <table width="600" border="0" cellspacing="0" cellpadding="3" class="TableCell">
              <tr> 
                <td width="30%" class="SubtitleHeader" align="right">Name:</td>
                <% String name;
                if( serialType.equals("xcom"))
                	name = "Expresscom";
                else if( serialType.equals("vcom"))
                	name = "Versacom";
                else if( serialType.equals("sa205"))
                	name = "DCU-205";
                else if( serialType.equals("sa305"))
                	name = "DCU-305";
                else                
                	 name = liteYukonPao.getPaoName();
                %>
                <td width="50%" class="TableCell"><%=name%></td>
              </tr>
              <tr> 
                <td width="30%" class="SubtitleHeader" align="right">Type:</td>
                <td width="50%" class="TableCell"><%=YC_BEAN.getDeviceType()%></td>
              </tr>
 			  <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
   			    <input type="hidden" name="deviceID" value="<%=deviceID%>">
   			    <input type="hidden" name="timeOut" value="8000">
   			    <%--The jsp wrapping this page needs to tell us "redirect"s value --%>
				<input id="redirect" type="hidden" name="REDIRECT" value="<%= redirect %>">
				<input id="referrer" type="hidden" name="REFERRER" value="<%= redirect %>">

			  <% if( serialType.length() > 0) {%>
			  <tr> 
                <td width="30%" class="SubtitleHeader" align="right">Serial Number:</td>
                <td width="50%">
                  <input type="text" name="serialNumber" size="20" value="<%=serialNum%>">
				</td>
			  </tr>
              <tr> 
                <td width="30%" class="SubtitleHeader" height="2" align="right">Route:</td>
                <td width="70%" height="2"> 
                  <select id="routeID" name="routeID">
				    <OPTION VALUE="-1">Select a Route
                    <%
				  LiteYukonPAObject[] validRoutes = YC_BEAN.getValidRoutes();
                  for (int i = 0; i < validRoutes.length; i++)
                  {%>
                      <OPTION VALUE="<%=((LiteYukonPAObject)validRoutes[i]).getYukonID()%>" <%=YC_BEAN.getRouteID()==((LiteYukonPAObject)validRoutes[i]).getYukonID() ? "selected" : ""%>><%=((LiteYukonPAObject)validRoutes[i]).getPaoName()%>
                  <%}
                  %>
                  </select>
                </td>
              </tr>
			  
			  <%}%>
              <tr> 
                <td width="30%" class="SubtitleHeader" align="right">Common Commands:</td>
                <td width="50%">
                  <select name="commonCommand" onChange="loadCommand()" onInit="loadCommand()">
					<OPTION value="">Select a Command</option>				  
				  <%
                  	String tempCommand = YC_BEAN.getCommandString().replaceAll("noqueue", "").trim();
					for (int i = 0; i < YC_BEAN.getLiteDeviceTypeCommandsVector().size(); i++)
                  	{
						LiteDeviceTypeCommand ldtc = (LiteDeviceTypeCommand)YC_BEAN.getLiteDeviceTypeCommandsVector().get(i);
						LiteCommand lc = CommandFuncs.getCommand(ldtc.getCommandID());
						%>
                  		<option value='<%=lc.getCommand()%>' <%=(lc.getCommand().equalsIgnoreCase(tempCommand))? "selected":""%> ><%=lc.getLabel()%></option>
                 	<%}%>
				  </select>
                </td>
              </tr>
			  <tr> 
                <td width="30%" class="SubtitleHeader" align="right">Execute Command:</td>
                <td width="50%">
                  <input type="text" name="command" size="40" value="<%=YC_BEAN.getCommandString()%>">
				</td>
				<td width="20%" rowspan=2 align='left'>
                  <input type="submit" name="execute" value="Execute" onClick="disableButton(this)">
                </td>
			  </tr>
              <tr align="left"> 
                <td class="SubtitleHeader" colspan="2" valign="top">&nbsp;</td>
              </tr>
              <tr> 
                <td colspan="3"> 
                  <div align="center">
                  <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="20" wrap="VIRTUAL"><%= YC_BEAN.getResultText()%></textarea>
                    <input type="submit" name="clearText" value="Clear Results">
                    <input type="reset" name="refresh" value="Refresh" onClick="window.location.reload()">
                  </div>
                </td>
              </tr>
              <tr><td colspan="3">
			  <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" class="TableCell">              
              <tr> 
				<% String gotoLink = "";
			  	if (request.getParameter("InvNo") != null){	//we came from the Customer Account page
		          gotoLink =  request.getContextPath()+"/operator/Consumer/CommandInv.jsp?InvNo="+invNo+"&command=null";
		        } else {
		          gotoLink = request.getContextPath()+"/apps/CommandDevice.jsp?deviceID="+deviceID+"&command=null";
		        }
   				if (isMCT410) {%>
				<td align="center"><a href='<%= gotoLink%>' name="advCommander410"  class="Link1">Commander: MCT410 Custom</a></td>
			  <%}%>
	          </tr>			          
	          <tr>
			  	<%
			  	  if (request.getParameter("InvNo") != null) {	//we came from the Customer Account page
            	    gotoLink = request.getContextPath()+"/operator/Consumer/CommandInv.jsp?InvNo="+invNo+"&lp";
      			  } else {
				    gotoLink = request.getContextPath()+"/apps/CommandDevice.jsp?deviceID="+deviceID+"&lp";
                  }
			  	  if (isMCT410 ) {%>
			  	  <td align="center"><a href='<%= gotoLink%>' name="advCommander410"  class="Link1">Commander: MCT410 Profile</a></td>
			  <%}%>
	          </tr>    			  
              </table>
              </td>
              </tr>
              <br>
              </form>
            </table>
            <br>
            </div>
          </td>