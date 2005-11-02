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
				<cti:breadCrumb>
					<cti:crLink url="subareas.jsp" title="SubBus Areas" cssClass="crumbs" />
					<cti:crLink url="subs.jsp" title="Substations" cssClass="crumbs" />
					<cti:crLink url="feeders.jsp" title="Feeders" cssClass="crumbs" />
				</cti:breadCrumb>
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
						<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" href="javascript:void(0);"
						    onmouseover="overlib(
								createIFrame('subCmd.jsp?subId=<%=subBus.getCcId()%>', 135, 90, 'if1', 0),
								STICKY, WIDTH,135, HEIGHT,90,
								MOUSEOFF, FULLHTML);"
						    onmouseout="nd();">
					<% } else { %>
						<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>">
					<% } %>
			
					<font color="<%=CBCDisplay.getHTMLFgColor(subBus)%>">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
					</font>
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
for( int i = 0; i < feeders.length; i++ )
{
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
	Feeder feeder = feeders[i];
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="cti_chkbxFdrs" value="<%=feeder.getCcId()%>"/><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></td>

					<td>
<% if( hasControl ) { %>
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>"
		href="javascript:void(0);"
	    onmouseover="overlib(
			createIFrame('feederCmd.jsp?feederId=<%=feeder.getCcId()%>', 135, 80, 'if1', 0),
			STICKY, WIDTH,135, HEIGHT,80,
			MOUSEOFF, FULLHTML);"
	    onmouseout="nd();">		
<% } else { %>
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>">
<% } %>
<font color="<%=CBCDisplay.getHTMLFgColor(feeder)%>">
	<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
</font>
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
			<table id="capBankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
				<form id="capBankForm" action="feeders.jsp" method="post">
				<tr class="columnheader lAlign">
					<td><input type="checkbox" name="chkAllBanksBx"
						onclick="checkAll(this, 'cti_chkbxBanks');" /> CB Name (Order)
						<img class="rAlign popupImg" src="images\question.gif"
			      			onmouseover="statusMsg(this, 'Order is the order the CapBank will control in.<br>Commands that can be sent to a field device are initiated from this column');" />
					</td>
					<td>State <img class="rAlign popupImg" src="images\question.gif"
				      		onmouseover="statusMsg(this, 'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column');"/>
					</td>
					<td>Bank Address</td>
					<td>Date/Time</td>
					<td>Bank Size</td>
					<td>Parent Feeder</td>
					<td>Op Count</td>
				</tr>
<%
for( int i = 0; i < capBanks.length; i++ )
{
	CapBankDevice capBank = capBanks[i];
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="cti_chkbxBanks" value="<%=capBank.getCcId()%>"/>
					<% if( hasControl && !CtiUtilities.STRING_NONE.equals(subBus.getControlUnits()) ) { %>
						<a href="javascript:void(0);"
						    onmouseover="overlib(
								createIFrame('capBankCmd.jsp?capBankId=<%=capBank.getCcId()%>&cmdType=field', 135, 120, 'if1', 0),
								STICKY, WIDTH,135, HEIGHT,120,
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
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>" href="javascript:void(0);"
						    onmouseover="overlib(
								createIFrame('capBankCmd.jsp?capBankId=<%=capBank.getCcId()%>&cmdType=system', 160, 200, 'if1', 0),
								STICKY, WIDTH,160, HEIGHT,200,
								MOUSEOFF, FULLHTML);"
						    onmouseout="nd();">
							
					<% } else { %>
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>" href="javascript:void(0);">
					<% } %>
					<font color="<%=CBCDisplay.getHTMLFgColor(capBank)%>"> <%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN) %>
					</font>
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


<!-------------- Form for submitting feeder commands ---------------->
<form id="frmFdrCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="redirectURL" value="<%=request.getRequestURL()%>">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_FEEDER%>">
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



<!-------------- Form for submitting Field CapBank commands ---------------->
<form id="frmCapBankCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="redirectURL" value="<%=request.getRequestURL()%>">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_CAPBANK%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">
<input type="hidden" name="opt">

<div id="bankFldPopupMenu" class="popupMenu"> 
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td class="popupCell"><img src="images\Header_left.gif" class="popupHeader"></td>
      <td class="trimBGColor popupHeader">Field Cmds
      <img class="rAlign popupImg" src="images\question.gif" onmouseover="statusMsg(this, 'Field Commands are any operations that send out a command message to a field device');"/>
      </td>
      <td class="popupCell"><img src="images\Header_right.gif" class="popupHeader"></td>
    </tr>
    <tr>
      <td class="popupCell lAlign" background="images\Side_left.gif"></td>
      <td>
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.CONFIRM_OPEN%>)"
				>Confirm</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.OPEN_CAPBANK%>)"
				>Open Capacitor</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.CLOSE_CAPBANK%>)"
				>Close Capacitor</a>
		  </td></tr>
		  
		<cti:checkProperty propertyid="<%= CBCSettingsRole.CBC_ALLOW_OVUV %>"> 		  
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.BANK_ENABLE_OVUV%>)"
				>Enable OV/UV</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.BANK_DISABLE_OVUV%>)"
				>Disable OV/UV</a>
		  </td></tr>
		</cti:checkProperty>


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


<div id="bankSysPopupMenu" class="popupMenu"> 
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td class="popupCell"><img src="images\Header_left.gif" class="popupHeader"></td>
      <td class="trimBGColor popupHeader">System Cmds
      <img class="rAlign popupImg" src="images\question.gif" onmouseover="statusMsg(this, 'System Commands are any operations that do NOT send out a command message to a field device');"/>
      </td>
      <td class="popupCell"><img src="images\Header_right.gif" class="popupHeader"></td>
    </tr>
    <tr>
      <td class="popupCell lAlign" background="images\Side_left.gif"></td>
      <td>
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.ENABLE_CAPBANK%>)"
				>Enable CapBank</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.DISABLE_CAPBANK%>)"
				>Disable CapBank</a>
		  </td></tr>

          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'cmdID', <%=CBCCommand.RETURN_BANK_TO_FEEDER%>)"
				>Temp Move Back</a>
		  </td></tr>

          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="showPopUp('tempmove.jsp?bankid='+intCapBankID);"
				>Temp Move...</a>
		  </td></tr>

          <tr><td>
          	<a class="optDeselect" href="javascript:void(0);"
	          	onmouseover="changeOptionStyle(this)"
	          	onclick="showSubMenu('invisBankStates'); toggleImg('img1');">
			<img id="img1" border="0" src="images/arrowright.gif" width="12" height="10">
	        Manual Entry</a>
		  </td></tr>


<%	
	LiteState[] cbcStates = CBCDisplay.getCBCStateNames();
	for( int i = 0; i < cbcStates.length; i++ )
	{ %>
          <tr id="invisBankStates" style="display: none;"><td>
          	<a href="#" class="optDeselect" style="margin-left: 5px;"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', intCapBankID, 'opt', <%=cbcStates[i].getStateRawState()%>, 'cmdID', <%=CBCCommand.CMD_MANUAL_ENTRY%>)"
				><%=cbcStates[i]%></a>
		  </td></tr>
<% } %>



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

<%@include file="cbc_footer.jspf"%>

</HTML>