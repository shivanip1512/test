<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Temp CapBank Move" module="capcontrol_internal">
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int bankid = ParamUtil.getInteger(request, "bankid");
	SubBus[] allSubs = capControlCache.getAllSubBuses();		
	CapBankDevice capBank = capControlCache.getCapBankDevice( new Integer(bankid) );
	int oldfdrid = 0;
	
	if( capBank == null )
	{
		allSubs = new SubBus[0];
	}
	else
	{
		oldfdrid = capBank.getParentID();
	}
	
%>
<div >
 <!--sort of a hack on REDIRECTURL input for now, but I can not figure out how to get around the use of the proxy in XmlHTTP calls-->
 <form id="frmCapBankMove" action="/servlet/CBCServlet" method="post">
	<input type="hidden" name="redirectURL" value="/capcontrol/feeders.jsp">
	<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_CAPBANK%>">
	<input type="hidden" name="paoID" value="<%=bankid%>">
	<input type="hidden" name="cmdID" value="<%=CBCCommand.CMD_BANK_TEMP_MOVE%>">
	<input type="hidden" name="opt" value="<%=oldfdrid%>">  <!--Old Feeder ID-->
	<input type="hidden" name="opt"> <!--New Feeder ID-->
	<input type="hidden" name="opt"> <!--Control Order-->

      
      <div class="scrollHuge">
        <table id="innerTable" width="95%" border="0" cellspacing="0" cellpadding="0">
			<tr class="columnHeader lAlign">
				<td>Name</td>
				<td>State</td>
				<td>VAR Load / Est.</td>
				<td>PFactor / Est.</td>
				<td class="rAlign">New Control Order:
				<input type="text" id="txtCntrlOrder" class="tableCell" size="1" maxlength="3" value="1">
				</td>
			</tr>

<%
String css = "tableCell";
for( int i = 0; i < allSubs.length; i++ )
{
	SubBus subBus = allSubs[i];	
	Feeder[] feeders = capControlCache.getFeedersBySub(subBus.getCcId());

	if( feeders.length <= 0 ) continue;
	
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
%>

			<tr class="<%=css%>">
				<td>
				<input type="image" id="chkBxSub<%=i%>"
					src="images/nav-plus.gif"
					onclick="showRowElems( 'subId<%=i%>', 'chkBxSub<%=i%>' ); return false;"/>
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
				</td>
				<td></td>
				<td></td>
				<td></td>
				<td></td>				
			</tr>

			<a id="subId<%=i%>">
<%
	for( int j = 0; j < feeders.length; j++ )
	{
		Feeder feeder = feeders[j];
		
		//do not include the current feeder this bank belongs to
		if( feeder.getCcId().intValue() == oldfdrid )
			continue;
%>
				<tr class="<%=css%>" style="display: none;">
					<td><a href="#" class="<%=css%> lIndent"
						onclick="postMany('frmCapBankMove', 'opt[1]', <%=feeder.getCcId()%>, 'opt[2]', frmCapBankMove.txtCntrlOrder.value);parent.parent.GB_hide()">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></a>
					</td>
					<td>
					<font color="<%=CBCDisplay.getHTMLFgColor(feeder)%>">
						<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
					</font>
					</td>
					<td>
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_VAR_LOAD_COLUMN)%></a>
					</td>
					<td>
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_POWER_FACTOR_COLUMN)%></a>
					</td>

					<td>
					</td>
				</tr>
<%	} %>
			</a>
<% } %>

        </table>
    </div>

  
</form>
</div>

</cti:standardPage>
