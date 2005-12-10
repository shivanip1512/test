<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int subid = cbcSession.getLastSubID();
	
	SubBus subBus = capControlCache.getSubBus( new Integer(subid) );
	Feeder[] feeders = capControlCache.getFeedersBySub( new Integer(subid) );
	CapBankDevice[] capBanks = capControlCache.getCapBanksBySub( new Integer(subid) );

	boolean hasControl = CBCWebUtils.hasControlRights(session);
%>

<HTML>
<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>

<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet"
	href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>"
	type="text/css">

<TITLE>Feeders</TITLE>
</HEAD>

<body onload="callBack();">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><%@include file="cbc_header.jspf"%></td>
	</tr>

	<td>
	<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" height="30">
		<tr>
          <td valign="top">
	          <div class="lAlign">
				<cti:breadCrumbs>
					<cti:crumbLink url="subareas.jsp" title="SubBus Areas" />
					<cti:crumbLink url="subs.jsp" title="Substations" />
					<cti:crumbLink url="feeders.jsp" title="Feeders" />
				</cti:breadCrumbs>
	          </div>
          </td>
		
          <td valign="top">
			<div class="rAlign">
				<form id="findForm" action="results.jsp" method="post">
					<p class="main">Find: <input type="text" name="<%=CBCSessionInfo.STR_LAST_SEARCH%>">
					<INPUT type="image" name="Go" src="images\GoButton.gif" alt="Find"></p>
				</form>
			</div>
          </td>
		</tr>
	</table>

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="cellImgFill"><img src="images\Header_left.gif" class="cellImgFill"></td>
			<td class="trimBGColor cellImgShort">Substation</td>
			<td class="cellImgFill"><img src="images\Header_right.gif" class="cellImgFill"></td>
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
					<td>VAR Load / Est.</td>
					<td>Date/Time</td>
					<td>PFactor / Est.</td>
					<td>Watts</td>
					<td>Daily / Max Ops</td>
				</tr>

<%
if( subBus != null ) {
%>
				<tr class="altTableCell">
					<td><input type="checkbox" name="cti_chkbxSubs" value="<%=subBus.getCcId()%>" />
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN)%>
					</td>
					
					<td>
					<% if( hasControl ) { %>
						<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>"
							style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;"
							href="javascript:void(0);"
						    onmouseover="overlib(
								createIFrame('subCmd.jsp?subId=<%=subBus.getCcId()%>', 135, 90, 'if1', 0),
								STICKY, WIDTH,135, HEIGHT,90,
								MOUSEOFF, FULLHTML);"
						    onmouseout="nd();">
					<% } else { %>
						<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;" >
					<% } %>
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
					</a>
					</td>
					
					
					<td><a type="param1" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN) %></a>
					</td>
					<td><a type="param2" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_COLUMN)%></a>
					</td>
					<td><a type="param3" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TIME_STAMP_COLUMN)%></a>
					</td>
					<td><a type="param4" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_POWER_FACTOR_COLUMN)%></a>
					</td>
					<td><a type="param5" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_WATTS_COLUMN)%></a>
					</td>
					<td><a type="param6" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN)%></a>
					</td>
				</tr>
<% } %>

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

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
			<table id="fdrTable" width="100%" border="0" cellspacing="0" cellpadding="0">
				<form id="fdrForm" action="feeders.jsp" method="post">
				<tr class="columnheader lAlign">
					<td><input type="checkbox" name="chkAllFdrsBx"
						onclick="checkAll(this, 'cti_chkbxFdrs');" /> Feeder Name</td>
					<td>State</td>
					<td>Target</td>
					<td>VAR Load / Est.</td>
					<td>Date/Time</td>
					<td>PFactor / Est.</td>
					<td>Watts</td>
					<td>Daily Ops</td>
				</tr>
<%
String css = "tableCell";
for( int i = 0; i < feeders.length; i++ )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	Feeder feeder = feeders[i];
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="cti_chkbxFdrs" value="<%=feeder.getCcId()%>"/><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></td>

					<td>
<% if( hasControl ) { %>
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>"
		style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;"
		href="javascript:void(0);"
	    onmouseover="overlib(
			createIFrame('feederCmd.jsp?feederId=<%=feeder.getCcId()%>', 135, 75, 'if1', 0),
			STICKY, WIDTH,135, HEIGHT,75,
			MOUSEOFF, FULLHTML);"
	    onmouseout="nd();">		
<% } else { %>
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;" >
<% } %>
	<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
</a>
					</td>

					<td><a type="param1" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TARGET_COLUMN)%></a>
					</td>
					<td><a type="param2" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_VAR_LOAD_COLUMN)%></a>
					</td>
					<td><a type="param3" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TIME_STAMP_COLUMN)%></a>
					</td>
					<td><a type="param4" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_POWER_FACTOR_COLUMN)%></a>
					</td>
					<td><a type="param5" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_WATTS_COLUMN)%></a>
					</td>
					<td><a type="param6" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN)%></a>
					</td>
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

	<table width="100%" border="0" cellspacing="0" cellpadding="0">
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
			<table id="capBankTable" width="100%" border="0" cellspacing="0" cellpadding="0" >
				<form id="capBankForm" action="feeders.jsp" method="post">
				<tr class="columnHeader lAlign">
					<th><input type="checkbox" name="chkAllBanksBx"
						onclick="checkAll(this, 'cti_chkbxBanks');" /> CB Name (Order)
						<img class="rAlign popupImg" src="images\question.gif"
			      			onmouseover="statusMsg(this, 'Order is the order the CapBank will control in.<br>Commands that can be sent to a field device are initiated from this column');" />
					</th>
					<th>State <img class="rAlign popupImg" src="images\question.gif"
				      		onmouseover="statusMsg(this, 'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column');"/>
					</th>
					<th>Bank Address</th>
					<th>Date/Time</th>
					<th>Bank Size</th>
					<th>Parent Feeder</th>
					<th>Op Count</th>
				</tr>

<%
css = "tableCell";
for( int i = 0; i < capBanks.length; i++ )
{
	CapBankDevice capBank = capBanks[i];
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="cti_chkbxBanks" value="<%=capBank.getCcId()%>"/>
					<% if( hasControl && !CtiUtilities.STRING_NONE.equals(subBus.getControlUnits()) ) { %>
						<a href="javascript:void(0);"
						    onmouseover="overlib(
								createIFrame('capBankCmd.jsp?capBankId=<%=capBank.getCcId()%>&cmdType=field', 135, 110, 'if1', 0),
								STICKY, WIDTH,135, HEIGHT,110,
								MOUSEOFF, FULLHTML);"
						    onmouseout="nd();">
							
							<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN) %>
						</a>
					<% } else { %>
						<span>
							<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN) %>
						</span>
					<% } %>
					</td>

					<td>
					<% if( hasControl ) { %>
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>"
							style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;"
							href="javascript:void(0);"
						    onmouseover="overlib(
								createIFrame('capBankCmd.jsp?capBankId=<%=capBank.getCcId()%>&cmdType=system', 135, 200, 'if1', 0),
								STICKY, WIDTH,135, HEIGHT,200,
								MOUSEOFF, FULLHTML);"
						    onmouseout="nd();">
							
					<% } else { %>
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>" href="javascript:void(0);" style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;" >
					<% } %>
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN) %>
					</a>
					</td>

					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_ADDRESS_COLUMN)%></td>
					<td><a type="param1" name="cti_dyn" id="<%=capBank.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN)%></a>
					</td>
					
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_SIZE_COLUMN)%></td>
					
					<td>

		          	<a href="#"
		          		<% if( capBank.isBankMoved() ) {%>
		          			class="warning" onmouseover="statusMsg(this, 'This bank has been temporarily moved from it\'s original feeder');"
		          		<% } else { %>
							onmouseover="statusMsg(this, 'Click here to temporarily move this CapBank from it\'s current parent feeder');"
		          			onclick="showPopUp('tempmove.jsp?bankid='+<%=capBank.getCcId()%>);"
		          		<% } %>
						><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_PARENT_COLUMN)%>
					</a>					
					</td>

					<td><a type="param2" name="cti_dyn" id="<%=capBank.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_OP_COUNT_COLUMN)%></a>
					</td>
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


<%@include file="cbc_footer.jspf"%>

</HTML>