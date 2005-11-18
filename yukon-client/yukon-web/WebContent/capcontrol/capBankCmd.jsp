<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.util.*" %>


<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	String cmdType = ParamUtil.getString(request, "cmdType", "system"); //field or system
	int capBankId = ParamUtil.getInteger(request, "capBankId", 0);
	CapBankDevice capBank = capControlCache.getCapBankDevice( new Integer(capBankId) );
%>

<HTML>
<HEAD>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<SCRIPT type="text/javascript">
<!--
// -------------------------------------------
// Page scoped javascript variables
// -------------------------------------------
var imgToggle = false;
function toggleImg( imgID )
{
	var imgElem = document.getElementById(imgID);
	if( imgToggle )
		imgElem.src='images/arrowright.gif';
	else
		imgElem.src='images/arrowdown.gif';

	imgToggle = !imgToggle;
}
//-->
</SCRIPT>


<body>

<!-------------- Form for submitting CapBank commands ---------------->
<form id="frmCapBankCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_CAPBANK%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">
<input type="hidden" name="opt">

<% if( "field".equalsIgnoreCase(cmdType) ) {%>
<div id="bankFldPopupMenu" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td class="popupCell"><img src="images\Header_left.gif" class="popupHeader"></td>
      <td class="trimBGColor popupHeader"><%=capBank.getCcName()%></td>
      <td class="popupCell"><img src="images\Header_right.gif" class="popupHeader"></td>
    </tr>
    <tr>
      <td class="popupCell lAlign" background="images\Side_left.gif"></td>
      <td>
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.CONFIRM_OPEN%>)"
				>Confirm</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.OPEN_CAPBANK%>)"
				>Open Capacitor</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.CLOSE_CAPBANK%>)"
				>Close Capacitor</a>
		  </td></tr>
		  
		<cti:checkProperty propertyid="<%= CBCSettingsRole.CBC_ALLOW_OVUV %>"> 		  
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.BANK_ENABLE_OVUV%>)"
				>Enable OV/UV</a>
		  </td></tr>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.BANK_DISABLE_OVUV%>)"
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

<% } else { %>

<div id="bankSysPopupMenu" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td class="popupCell"><img src="images\Header_left.gif" class="popupHeader"></td>
      <td class="trimBGColor popupHeader"><%=capBank.getCcName()%></td>
      <td class="popupCell"><img src="images\Header_right.gif" class="popupHeader"></td>
    </tr>
    <tr>
      <td class="popupCell lAlign" background="images\Side_left.gif"></td>
      <td>
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
<% if( capBank.getCcDisableFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.ENABLE_CAPBANK%>)"
				>Enable CapBank</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.DISABLE_CAPBANK%>)"
				>Disable CapBank</a>
		  </td></tr>
<% } %>

<% if( capBank.isBankMoved() ) { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.RETURN_BANK_TO_FEEDER%>)"
				>Temp Move Back</a>
		  </td></tr>
<% } %>


<%	
	LiteState[] cbcStates = CBCDisplay.getCBCStateNames();
	for( int i = 0; i < cbcStates.length; i++ )
	{ %>
          <tr><td>
          	<a class="optDeselect" href="javascript:void(0);"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'opt', <%=cbcStates[i].getStateRawState()%>, 'cmdID', <%=CBCCommand.CMD_MANUAL_ENTRY%>)"
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
<% } %>

</form>

</body>
</html>