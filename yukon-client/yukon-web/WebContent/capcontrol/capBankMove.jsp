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

<div  class="cmdPopupMenu">
  <cti:titledContainer title="<%=capBank.getCcName()%>">
        <table id="bankTable" width="100%" border="0" cellspacing="0" cellpadding="0">

<% if( capBank.isBankMoved() ) { %>
          <tr><td>
          	<a id="tempMoveBack" href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmCapBankCmd', 'paoID', <%=capBankId%>, 'cmdID', <%=CBCCommand.RETURN_BANK_TO_FEEDER%>); top.document.getElementById('tempIFrame').style.display='none'; setTimeout ('window.parent.location.replace (\'feeders.jsp\')', <%=CBCUtils.TEMP_MOVE_REFRESH%>);"
				>Temp Move Back</a>
		  </td></tr>
<% } else {%>
	
<script type="text/javascript">
	setTimeout ('window.parent.location.replace (\'feeders.jsp\')', 0);
</script>

<%} %>


        </table>
   </cti:titledContainer>
</div>

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