<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage module="capcontrol_internal" title="">
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


<!-- Form for submitting CapBank commands -->
<form id="frmCapBankCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="redirectURL" value="/capcontrol/feeders.jsp">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_CAPBANK%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">
<input type="hidden" name="opt">

<%if (capBankId > 0) {%>
<% if( "field".equalsIgnoreCase(cmdType) ) {%>
<div class="cmdPopupMenu">
  <cti:titledContainer title="<%=capBank.getCcName()%>">
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.CONFIRM_OPEN%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Confirm</a>
		  </td></tr>
          <tr><td>

          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.OPEN_CAPBANK%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Open Capacitor</a>
		  </td></tr>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.CLOSE_CAPBANK%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Close Capacitor</a>
		  </td></tr>
		  
		<cti:checkProperty propertyid="<%= CBCSettingsRole.CBC_ALLOW_OVUV %>"> 		  
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.BANK_ENABLE_OVUV%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Enable OV/UV</a>
		  </td></tr>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.BANK_DISABLE_OVUV%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Disable OV/UV</a>
		  </td></tr>
		</cti:checkProperty>

        </table>
     </cti:titledContainer>
</div>

<% } else { %>

<div  class="cmdPopupMenu">
  <cti:titledContainer title="<%=capBank.getCcName()%>">
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
<% if( capBank.getCcDisableFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.ENABLE_CAPBANK%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Enable CapBank</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.DISABLE_CAPBANK%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Disable CapBank</a>
		  </td></tr>
<% } %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.RESET_OPCOUNT%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Reset Op Counts</a>
		  </td></tr>
<%	
	LiteState[] cbcStates = CBCDisplay.getCBCStateNames();
	for( int i = 0; i < cbcStates.length; i++ )
	{ %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'opt', <%=cbcStates[i].getStateRawState()%>, 'cmdID', <%=CBCCommand.CMD_MANUAL_ENTRY%>); top.document.getElementById('tempIFrame').style.display='none';"
				><%=cbcStates[i]%></a>
		  </td></tr>
<% } %>


        </table>
   </cti:titledContainer>
</div>
<% } %>
<% } else {%>
<div class="cmdPopupMenu">
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td>
	<span class="optDeselect">No Cap Bank selected</span>
</td></tr>
<tr><td>

	<a href="feeders.jsp" class="optDeselect" 
	>Home</a>
</td></tr>
</table>
</div>
<%}%>
</form>

</cti:standardPage>