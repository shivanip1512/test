	<SCRIPT language="JavaScript">
	function disableButton(x)
	{
		x.disabled = true;
		document.commandForm.submit();
	}
	</SCRIPT>
	
<%
//set the deviceID of the YCBean
ycBean.setDeviceID(deviceID);


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
   			    <%--The jsp wrapping this page needs to tell us "redirect"s value --%>
				<input id="redirect" type="hidden" name="REDIRECT" value="<%= redirect %>">
				<input id="referrer" type="hidden" name="REFERRER" value="<%= redirect %>">
              <tr> 
                <td width="30%" class="SubtitleHeader" align="right">Execute Command :</td>
                <td width="70%"> 
                  <select name="command">
                  <%
                  	String tempCommand = ycBean.getCommandString().replaceAll("noqueue", "").trim();
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
                 	}
                  %>
				  </select>
                  <input type="submit" name="execute" value="Execute" onClick="disableButton(this)">
                </td>
              </tr>
              <tr align="left"> 
                <td class="SubtitleHeader" colspan="2" valign="top">&nbsp;</td>
              </tr>
              <tr> 
                <td colspan="2"> 
                  <div align="center">
                  <textarea id="resultText" name="resultText" class="TableCell" readonly="readonly" cols="100" rows="20" wrap="VIRTUAL"><%= ycBean.getResultText()%></textarea>
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