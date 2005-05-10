<%@include file="cbc_inc.jspf"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	SubBus[] areaSubs =
		capControlCache.getSubsByArea( cbcSession.getLastArea() );
%>

<HTML>
<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>
<SCRIPT type="text/javascript">
// -------------------------------------------
// Page scoped javascript variables
// -------------------------------------------
var intSubID = -1;
</SCRIPT>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<META id="GENERATOR" content="IBM WebSphere Studio">
<TITLE>Substations</TITLE>
</HEAD>


<body onload="callBack();">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
    <%@include file="cbc_header.jspf"%>
    </td>
  </tr>

    <td> 
      <table width="95%" border="0" cellspacing="0" cellpadding="0" align="center" height="30">
        <tr>
          <td valign="bottom" colspan="2">
            <div class="rAlign">
				<cti:breadCrumb>
					<cti:crLink url="subareas.jsp" title="SubBus Areas" cssClass="crumbs" />
					<cti:crLink url="subs.jsp" title="Substations" cssClass="crumbs" />
				</cti:breadCrumb>

				<form id="findForm" action="results.jsp" method="post">
					<p class="main">Find: <input type="text" name="searchCriteria">
					<INPUT type="image" name="Go" src="images\GoButton.gif" alt="Find"></p>
				</form>
            </div>
          </td>

        </tr>
      </table>

      <table width="95%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td class="cellImgFill"><img src="images\Header_left.gif" class="cellImgFill"></td>
          <td class="trimBGColor cellImgShort">Substation Buses In Area:  <%=cbcSession.getLastArea()%></td>
          <td class="cellImgFill"><img src="images\Header_right.gif" class="cellImgFill"></td>
        </tr>
        <tr>
          <td class="cellImgFill lAlign" background="images\Side_left.gif"></td>
          <td>
          
          <div class="scrollLarge">
            <table id="subTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnheader lAlign">				
				<td>
				<input type="checkbox" id="chkAllBx" onclick="checkAll(this, 'chkBxSubs');"/>
				Sub Name</td>
                <td>State</td>
                <td>Target</td>
                <td>VAR Load / Est.</td>
                <td>Date/Time</td>
                <td>PFactor / Est.</td>
                <td>Watts</td>
                <td>Daily / Max Ops</td>
              </tr>

		<form id="subForm" action="feeders.jsp" method="post">
			<input type="hidden" name="<%=CBCSessionInfo.STR_SUBID%>" />
<%
for( int i = 0; i < areaSubs.length; i++ )
{
	String css = (i % 2 == 0 ? "tableCell" : "altTableCell");
	SubBus subBus = areaSubs[i];
%>
	        <tr class="<%=css%>">
				<td>
				<input type="checkbox" id="chkBxSubs" />
				<a href="#" onclick="postMany('subForm', '<%=CBCSessionInfo.STR_SUBID%>', <%=subBus.getCcId()%>)">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
				</a></td>
				
				<td>
<cti:isPropertyTrue propertyid="<%= CBCSettingsRole.ALLOW_CONTROLS %>">
	<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" href="javascript:void(0);" onmouseover="intSubID=<%=subBus.getCcId()%>;menuAppear(event, 'subPopupMenu')" onmouseout="menuDisappear(event, 'subPopupMenu')">
</cti:isPropertyTrue>
<cti:isPropertyFalse propertyid="<%= CBCSettingsRole.ALLOW_CONTROLS %>">
	<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>">
</cti:isPropertyFalse>

<font color="<%=CBCDisplay.getHTMLFgColor(subBus)%>">
<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
</font>
</a>
				</td>

				<td><a type="param1" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN)%></a>
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

<!-------------- Form for submitting substation commands ---------------->
<form id="frmSubCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="redirectURL" value="<%=request.getRequestURL()%>">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_SUB%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">

<div id="subPopupMenu" class = "popupMenu"> 
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td class="popupCell"><img src="images\Header_left.gif" class="popupHeader"></td>
      <td class="trimBGColor popupHeader">SubBus Control</td>
      <td class="popupCell"><img src="images\Header_right.gif" class="popupHeader"></td>
    </tr>
    <tr>
      <td class="popupCell lAlign" background="images\Side_left.gif"></td>
      <td>
        <table id="subTable" width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', intSubID, 'cmdID', <%=CBCCommand.CONFIRM_CLOSE%>)">Confirm Sub</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', intSubID, 'cmdID', <%=CBCCommand.ENABLE_SUBBUS%>)">Enable Sub</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', intSubID, 'cmdID', <%=CBCCommand.DISABLE_SUBBUS%>)">Disable Sub</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', intSubID, 'cmdID', <%=CBCCommand.RESET_OPCOUNT%>)">Reset Op Counts</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', intSubID, 'cmdID', <%=CBCCommand.WAIVE_SUB%>)">Waive Sub</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', intSubID, 'cmdID', <%=CBCCommand.UNWAIVE_SUB%>)">Unwaive Sub</a>
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

</HTML>