<jsp:useBean id="webImp" scope="session"
	class="com.cannontech.yimp.util.ImportWebInfoObject"
/>	
<table width="738" border="1" align="center" cellpadding="0"
	cellspacing="0">
	<tbody>
		<tr>
			<td>
			<form name="impButtonForm" method="POST" action="<%= request.getContextPath() %>/servlet/ImporterServlet">
				<table width="735" border="0" cellspacing="0" cellpadding="0"><caption align="bottom"></caption>
					<tbody>
						<tr class="HeaderCell">
							<td width="231"><SPAN class="HeaderCellCrucial">&nbsp;Override
							the import wait period: <font color="#000000"> </font></SPAN>
							</td>
							<td width="314"><input type="submit" name="forceImp"
								value="Force Manual Import Event">
							</td>
							<td width="191">
							<div align="right"><br>
							<br>
							</div>
							</td>
						</tr>
					</tbody>
				</table>
			</form>
			</td>
		</tr>
	</tbody>
</table>
<br>
RESULTS OF LAST IMPORT<br>
<table border="1" width="740">
	<tbody>
		<tr>
			<th width="369" align="left" class="HeaderCell">Last import occurred at: <font
				color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><EM><font
				color="#ff0000"><%=webImp.getLastImportTime()%></font></EM></font></th>
		</tr>
		<tr>
			<th width="369" align="left" class="HeaderCellCrucial"><font
				color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><EM><font
				color="#ff0000"><%=webImp.getTotalSuccesses()%></font></EM></font> of <font
				color="#FFFFFF" size="2" face="Arial, Helvetica, sans-serif"><EM><font
				color="#ff0000"><%=webImp.getTotalAttempts()%></font></EM></font> entries were
			successfully imported.</th>
		</tr>
	</tbody>
</table>

<br>
<div style="display:<%=webImp.getFailures().size() > 15 || webImp.getFailures().size() == 0 ? "1" : "none" %>" id="failedDataButton">
	<table width="740" border="1" cellpadding="2" cellspacing="0">
		<caption align="top" class="HeaderCellCrucial">Total Failed Data Entries: <%=webImp.getFailures().size()%> </caption>
		<tr valign="top" class="HeaderCell">
			<td align="center">
				<input style="display:<%=webImp.getFailures().size() > 0 ? "1" : "none" %>" type="button" name="ViewAllDataFails" value="View All" onclick="toggleAllDataFails()">
			</td>
		</tr>
	</table> 
</div>
<div style="display:<%=webImp.getFailures().size() <= 15 && webImp.getFailures().size() > 0 ? "1" : "none" %>" id="failedDataTable" >
	<table width="740" border="1" cellpadding="2" cellspacing="0">
		<caption align="top" class="HeaderCellCrucial">Failed Data Entries</caption>
		<tbody>
			<tr valign="top" class="HeaderCell">
				<td width="70"><font color="#000000"><%= webImp.FAILURE_COLUMN_NAMES[0] %></font></td>
				<td width="400"><font color="#ff0000"><%= webImp.FAILURE_COLUMN_NAMES[1] %></font></td>
				<td width="70"><font color="#000000"><%= webImp.FAILURE_COLUMN_NAMES[2] %></font></td>
			</tr>
<% 
             	for( int i = 0; i < webImp.getFailures().size(); i++ )
             	{
             	%>
			<tr valign="top">
				<td width="70" class="TableCell"><font color="#000000"><%= webImp.getFailName(i)  %></font></td>
				<td width="400" class="TableCell"><font color="#ff0000"><%= webImp.getErrorString(i) %></font></td>
				<td width="70" class="TableCell"><font color="#000000"><%= webImp.getFailureTime(i) %></font></td>
			</tr>
				<%
				}
				%>
		</tbody>
	</table>
	<input type="button" name="HideAllDataFails" value="Hide All" onclick="toggleAllDataFails()">
</div>
<br>

<br>
<div style="display:<%=webImp.getPendingComms().size() > 15 || webImp.getPendingComms().size() == 0 ? "1" : "none" %>" id="pendingCommButton" >
	<table width="740" border="1" cellpadding="2" cellspacing="0">
		<caption align="top" class="HeaderCellCrucial">Pending Communication Attempts: <%=webImp.getPendingComms().size()%> </caption>
		<tr valign="top" class="HeaderCell">
			<td align="center">
				<input style="display:<%=webImp.getPendingComms().size() > 0 ? "1" : "none" %>" type="button" name="ViewAllPendingComms" value="View All" onclick="toggleAllPendingComms()">
			</td>
		</tr>
	</table> 
</div>
<div style="display:<%=webImp.getPendingComms().size() <= 15 && webImp.getPendingComms().size() > 0 ? "1" : "none" %>" id="pendingCommTable" >
	<table width="740" border="1" cellpadding="2" cellspacing="0">
		<caption align="top" class="HeaderCellCrucial">Pending Communication Attempts</caption>
		<tbody>
			<tr valign="top" class="HeaderCell">
				<td width="108"><font color="#000000"><%= webImp.COMM_PENDING_COLUMN_NAMES[0] %></font></td>
				<td width="108"><font color="#000000"><%= webImp.COMM_PENDING_COLUMN_NAMES[1] %></font></td>
				<td width="108"><font color="#000000"><%= webImp.COMM_PENDING_COLUMN_NAMES[2] %></font></td>
			</tr>

<% 
             	for( int i = 0; i < webImp.getPendingComms().size(); i++ )
             	{
             	%>
			<tr valign="top">
				<td width="108" class="TableCell"><font color="#000000"><%= webImp.getPendingName(i)  %></font></td>
				<td width="108" class="TableCell"><font color="#000000"><%= webImp.getPendingRouteName(i) %></font></td>
				<td width="108" class="TableCell"><font color="#000000"><%= webImp.getPendingSubstationName(i) %></font></td>
			</tr>
<%
}
%>
		</tbody>
	</table>
	<input type="button" name="HideAllPendingComms" value="Hide All" onclick="toggleAllPendingComms()">
</div>
<br>

<br>
<div style="display:<%=webImp.getFailedComms().size() > 15 || webImp.getFailedComms().size() == 0 ? "1" : "none" %>" id="failedCommButton" >
	<table width="740" border="1" cellpadding="2" cellspacing="0">
		<caption align="top" class="HeaderCellCrucial">Failed Communication Attempts: <%=webImp.getFailedComms().size()%> </caption>
		<tr valign="top" class="HeaderCell">
			<td align="center">
				<input style="display:<%=webImp.getFailedComms().size() > 0 ? "1" : "none" %>" type="button" name="ViewAllFailedComms" value="View All" onclick="toggleAllFailedComms()">
			</td>
		</tr>
	</table> 
</div>
<div style="display:<%=webImp.getFailedComms().size() <= 15 && webImp.getFailedComms().size() > 0 ? "1" : "none" %>" id="failedCommTable" >
	<table width="740" border="1" cellpadding="2" cellspacing="0">
		<caption align="top" class="HeaderCellCrucial">Failed Communication Attempts</caption>
		<tbody>
			<tr valign="top" class="HeaderCell">
				<td width="108"><font color="#000000"><%= webImp.COMM_FAILURE_COLUMN_NAMES[0] %></font></td>
				<td width="108"><font color="#000000"><%= webImp.COMM_FAILURE_COLUMN_NAMES[1] %></font></td>
				<td width="108"><font color="#000000"><%= webImp.COMM_FAILURE_COLUMN_NAMES[2] %></font></td>
				<td width="108"><font color="#FF0000"><%= webImp.COMM_FAILURE_COLUMN_NAMES[3] %></font></td>
				<td width="108"><font color="#000000"><%= webImp.COMM_FAILURE_COLUMN_NAMES[4] %></font></td>
			</tr>
<% 
             	for( int i = 0; i < webImp.getFailedComms().size(); i++ )
             	{
             	%>
			<tr valign="top">
				<td width="108" class="TableCell"><font color="#000000"><%= webImp.getCommFailureName(i)  %></font></td>
				<td width="108" class="TableCell"><font color="#000000"><%= webImp.getCommFailureRouteName(i) %></font></td>
				<td width="108" class="TableCell"><font color="#000000"><%= webImp.getCommFailureSubstationName(i) %></font></td>
				<td width="108" class="TableCell"><font color="#FF0000"><%= webImp.getCommFailureErrorString(i) %></font></td>
				<td width="108" class="TableCell"><font color="#000000"><%= webImp.getCommFailureTime(i) %></font></td>
			</tr>
<%
}
%>
		</tbody>
	</table>
	<input type="button" name="HideAllFailedComms" value="Hide All" onclick="toggleAllFailedComms()">
</div>
<br>
<script type="text/javascript">
	function toggleAllDataFails() {
		Element.toggle("failedDataButton");
		Element.toggle("failedDataTable");
	}	
	
	function toggleAllPendingComms() {
		Element.toggle("pendingCommButton");
		Element.toggle("pendingCommTable");
	}	
	
	function toggleAllFailedComms() {
		Element.toggle("failedCommButton");
		Element.toggle("failedCommTable");
	}	
</script>