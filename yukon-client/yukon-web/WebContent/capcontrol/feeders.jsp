<%@page import="java.util.Arrays" %>
<%@include file="cbc_inc.jspf"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int subid = cbcSession.getLastSubID();
	
	SubBus subBus = capControlCache.getSubBus( new Integer(subid) );
	Feeder[] feeders = capControlCache.getFeedersBySub( new Integer(subid) );
	CapBankDevice[] capBanks = capControlCache.getCapBanksBySub( new Integer(subid) );

%>

<HTML>
<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>
<SCRIPT type="text/javascript">
<!--
	// -------------------------------------------
	// Page scoped javascript variables
	// -------------------------------------------
	var intFeederID = -1;
//-->
</SCRIPT>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet"
	href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>"
	type="text/css">

<META name="GENERATOR" content="IBM WebSphere Studio">
<TITLE>Feeders</TITLE>
</HEAD>

<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><%@include file="cbc_header.jspf"%></td>
	</tr>

	<td>
	<table width="95%" border="0" cellspacing="0" cellpadding="0"
		align="center" height="30">
		<tr>
			<td valign="bottom" colspan="2">

			<div class="rAlign">
			<cti:breadCrumb>
				<cti:crLink url="subareas.jsp" title="SubBus Areas" cssClass="crumbs" />
				<cti:crLink url="subs.jsp" title="Substations" cssClass="crumbs" />
				<cti:crLink url="feeders.jsp" title="Feeders" cssClass="crumbs" />
			</cti:breadCrumb>			
			
			<form name="findForm" action="results.jsp" method="post">
				<p class="main">Find: <input type="text" name="searchCriteria">
				<INPUT type="image" name="Go" src="images\GoButton.gif" alt="Find"></p>
			</form>

			</div>
			</td>
		</tr>
	</table>

	<table width="95%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="cellImgFill"><img src="images\Header_left.gif"
				class="cellImgFill"></td>
			<td class="trimBGColor cellImgShort">Substation</td>
			<td class="cellImgFill"><img src="images\Header_right.gif"
				class="cellImgFill"></td>
		</tr>
		<tr>
			<td class="cellImgFill lAlign" background="images\Side_left.gif"></td>
			<td>

			<table id="subTable" width="100%" border="0" cellspacing="0"
				cellpadding="0">
				<tr class="columnheader lAlign">
					<td>Sub Name</td>
					<td>State</td>
					<td>Target</td>
					<td>VAR Load Est.</td>
					<td>Date/Time</td>
					<td>PFactor / Est.</td>
					<td>Watts</td>
					<td>Daily / Max Ops</td>
				</tr>

				<tr class="altTableCell">
					<td><input type="checkbox" name="chkBxSubs" /> <%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
					</td>
					<td><font color="<%=CBCDisplay.getHTMLFgColor(subBus)%>"> <%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN) %>
					</font></td>
					<td><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TIME_STAMP_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_POWER_FACTOR_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_WATTS_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN) %></td>
				</tr>
			</table>
			</td>
			<td class="cellImgFill rAlign" background="images\Side_right.gif"></td>
		</tr>
		<tr>
			<td class="cellImgShort"><img src="images\Bottom_left.gif"></td>
			<td class="cellImgShort" background="images\Bottom.gif"></td>
			<td class="cellImgShort"><img src="images\Bottom_right.gif"></td>
		</tr>
	</table>

	<br>

	<table width="95%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="cellImgFill"><img src="images\Header_left.gif"
				class="cellImgFill"></td>
			<td class="trimBGColor cellImgShort">Feeders</td>
			<td class="cellImgFill"><img src="images\Header_right.gif"
				class="cellImgFill"></td>
		</tr>
		<tr>
			<td class="cellImgFill lAlign" background="images\Side_left.gif"></td>
			<td>

			<div class="scrollSmall">
			<table id="fdrTable" width="100%" border="0" cellspacing="0"
				cellpadding="0">

				<form name="fdrForm" action="feeders.jsp" method="post">
				<tr class="columnheader lAlign">
					<td><input type="checkbox" name="chkAllFdrsBx"
						onclick="checkAll(this, 'chkBxFdrs');" /> Feeder Name</td>
					<td>State</td>
					<td>Target</td>
					<td>VAR Load Est.</td>
					<td>Date/Time</td>
					<td>PFactor / Est.</td>
					<td>Watts</td>
					<td>Daily Ops</td>
				</tr>
				<%
for( int i = 0; i < feeders.length; i++ )
{
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
	Feeder feeder = feeders[i];
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="chkBxFdrs" /> <%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></td>

					<td>
<cti:isPropertyTrue propertyid="<%= CBCSettingsRole.ALLOW_CONTROLS %>">
	<a href="javascript:void(0);" onmouseover="intFeederID=<%=feeder.getCcId()%>;menuAppear(event, 'fdrPopupMenu')" onmouseout="menuDisappear(event, 'fdrPopupMenu')">
</cti:isPropertyTrue>
<font color="<%=CBCDisplay.getHTMLFgColor(feeder)%>">
	<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
</font>
<cti:isPropertyTrue propertyid="<%= CBCSettingsRole.ALLOW_CONTROLS %>">
	</a>
</cti:isPropertyTrue>
					</td>

					<td><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TARGET_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_VAR_LOAD_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TIME_STAMP_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_POWER_FACTOR_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_WATTS_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN) %></td>
				</tr>
<% } %>
				</form>
			</table>
			</div>

			</td>
			<td class="cellImgFill rAlign" background="images\Side_right.gif"></td>
		</tr>
		<tr>
			<td class="cellImgShort"><img src="images\Bottom_left.gif"></td>
			<td class="cellImgShort" background="images\Bottom.gif"></td>
			<td class="cellImgShort"><img src="images\Bottom_right.gif"></td>
		</tr>
	</table>

	<br>

	<table width="95%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="cellImgFill"><img src="images\Header_left.gif"
				class="cellImgFill"></td>
			<td class="trimBGColor cellImgShort">Capacitor Banks</td>
			<td class="cellImgFill"><img src="images\Header_right.gif"
				class="cellImgFill"></td>
		</tr>
		<tr>
			<td class="cellImgFill lAlign" background="images\Side_left.gif"></td>
			<td>

		<div class="scrollSmall">
			<table id="capBankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
				<form name="capBankForm" action="feeders.jsp" method="post">
				<tr class="columnheader lAlign">
					<td><input type="checkbox" name="chkAllBanksBx"
						onclick="checkAll(this, 'chkBxBanks');" /> CB Name (Order)</td>
					<td>State</td>
					<td>Bank Address</td>
					<td>Date/Time</td>
					<td>Bank Size</td>
					<td>Parent Feeder</td>
					<td>Op Count</td>
				</tr>
<%
for( int i = 0; i < capBanks.length; i++ )
{
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
	CapBankDevice capBank = capBanks[i];
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="chkBxBanks"
						value="<%=capBank.getCcId()%>" /> <%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN) %></td>

					<td><font color="<%=CBCDisplay.getHTMLFgColor(capBank)%>"> <%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN) %>
					</font></td>
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_ADDRESS_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_SIZE_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_PARENT_COLUMN) %></td>
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_OP_COUNT_COLUMN) %></td>
				</tr>
				<% } %>
				</form>
			</table>
		</div>


			</td>
			<td class="cellImgFill rAlign" background="images\Side_right.gif"></td>
		</tr>
		<tr>
			<td class="cellImgShort"><img src="images\Bottom_left.gif"></td>
			<td class="cellImgShort" background="images\Bottom.gif"></td>
			<td class="cellImgShort"><img src="images\Bottom_right.gif"></td>
		</tr>
	</table>




	</td>
</table>
</body>
</HTML>

<!-------------- Form for submitting feeder commands ---------------->
<form name="frmFdrCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="redirectURL" value="<%=request.getRequestURL()%>">
<input type="hidden" name="controlType" value="FEEDER_CNTRL">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">

<div id="fdrPopupMenu" class = "popupMenu"> 
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td class="popupCell"><img src="images\Header_left.gif" class="popupHeader"></td>
      <td class="trimBGColor popupHeader">Feeder Control</td>
      <td class="popupCell"><img src="images\Header_right.gif" class="popupHeader"></td>
    </tr>
    <tr>
      <td class="popupCell lAlign" background="images\Side_left.gif"></td>
      <td>
        <table id="fdrTable" width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', intFeederID, 'cmdID', <%=CBCCommand.ENABLE_FEEDER%>)">Enable Feeder</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', intFeederID, 'cmdID', <%=CBCCommand.DISABLE_FEEDER%>)">Disable Feeder</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', intFeederID, 'cmdID', <%=CBCCommand.RESET_OPCOUNT%>)">Reset Op Counts</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', intFeederID, 'cmdID', <%=CBCCommand.WAIVE_FEEDER%>)">Waive Feeder</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', intFeederID, 'cmdID', <%=CBCCommand.UNWAIVE_FEEDER%>)">Unwaive Feeder</a>
		  </td></tr>
        </table>
      </td>
      <td class="popupCell rAlign" background="images\Side_right.gif"></td>
    </tr>
    <tr>
      <td class="popupCell"><img src="images\Bottom_left.gif"></td>
      <td class="popupCell" background="images\Bottom.gif"></td>
      <td class="popupCell"><img src="images\Bottom_right.gif"></td>
    </tr>
  </table>
</div>
</form>