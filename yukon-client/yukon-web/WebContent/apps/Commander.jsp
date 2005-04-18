<%@ page import="com.cannontech.database.data.lite.LiteDeviceTypeCommand"%> 
<%@ page import="com.cannontech.database.data.lite.LiteCommand"%> 
<%@ page import="com.cannontech.database.cache.functions.CommandFuncs"%>

<%
//set the deviceID of the YC_BEAN
YC_BEAN.setDeviceID(deviceID);
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
              <% String header = "METER - CONTROL COMMANDS"; %>
			  <br>
			<table width="100%" border="0" cellspacing="0" cellpadding="3">
			  <tr>
			    <td align="center" class="TitleHeader"><%= header %></td>
			  </tr>
			</table>

			<% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <table width="575" border="0" cellspacing="0" cellpadding="3" class="TableCell">
              <tr> 
                <td width="30%" class="SubtitleHeader" align="right"> Meter Name:</td>
                <td width="50%" class="TableCell"><%=liteYukonPao.getPaoName()%></td>
              </tr>
              <tr> 
                <td width="30%" class="SubtitleHeader" align="right">Meter Type:</td>
                <td width="50%" class="TableCell"><%=PAOGroups.getPAOTypeString(liteYukonPao.getType())%></td>
              </tr>
 			  <form name="commandForm" method="POST" action="<%= request.getContextPath() %>/servlet/CommanderServlet">
   			    <input type="hidden" name="deviceID" value="<%=deviceID%>">
   			    <input type="hidden" name="timeOut" value="8000">
   			    <%--The jsp wrapping this page needs to tell us "redirect"s value --%>
				<input id="redirect" type="hidden" name="REDIRECT" value="<%= redirect %>">
				<input id="referrer" type="hidden" name="REFERRER" value="<%= redirect %>">
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
			  <tr>
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
			  <tr>
              	<% if (request.getParameter("InvNo") != null)	//we came from the Customer Account page
              	{%>
				    <td colspan="3" width="90%" align="right"><a href='<%= request.getContextPath() %>/operator/Consumer/CommandInv.jsp?InvNo=<%=invNo%>' class="Link1">Go to Custom Commander</a>
			    <%}
			    else{%>
				    <td colspan="3" width="90%" align="right"><a href='<%= request.getContextPath() %>/apps/CommandDevice.jsp?deviceID=<%=deviceID%>' class="Link1">Go to Custom Commander</a>			    
			    <%}%>
			    
			    </td>
			  </tr>
              <br>
              </form>
            </table>
            <br>
            </div>
          </td>