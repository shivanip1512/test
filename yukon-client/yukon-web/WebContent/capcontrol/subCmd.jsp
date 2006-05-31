<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage module="capcontrol_internal" title="">
<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.util.*" %>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int subId = ParamUtil.getInteger(request, "subId", 0);
	SubBus subBus = capControlCache.getSubBus( new Integer(subId) );
%>


<!-- Form for submitting substation commands -->
<form id="frmSubCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_SUB%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">

<div class="cmdPopupMenu">
  <cti:titledContainer title="<%=subBus.getCcName()%>">
        <table id="subTable" width="100%" border="0" cellspacing="0" cellpadding="0">
<% if ( subId <= 0 ) { %>
          <tr><td>
          	<span class="optDeselect">No SubBus selected</span>
		  </td></tr>
		   <tr><td>
          	<a href="feeders.jsp" class="optDeselect" 
				>Home</a>
		  </td></tr>
		  
<% } else { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.CONFIRM_CLOSE%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Confirm Sub</a>
		  </td></tr>

<% if( subBus.getCcDisableFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.ENABLE_SUBBUS%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Enable Sub</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.DISABLE_SUBBUS%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Disable Sub</a>
		  </td></tr>
<% } %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.RESET_OPCOUNT%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Reset Op Counts</a>
		  </td></tr>

<% if(! subBus.getVerificationFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.CMD_ALL_BANKS%>); top.document.getElementById('tempIFrame').style.display='none';"
		  >Verify AllBanks </a>
		  </td></tr>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.CMD_FQ_BANKS%>); top.document.getElementById('tempIFrame').style.display='none';"
		  >Verify FailedAndQuestionableBanks </a>
		  </td></tr>
		  <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.CMD_FAILED_BANKS%>); top.document.getElementById('tempIFrame').style.display='none';"
		  >Verify FailedBanks </a>
		  </td></tr>
		  <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.CMD_QUESTIONABLE_BANKS%>); top.document.getElementById('tempIFrame').style.display='none';"
		  >Verify QuestionableBanks  </a>
		  </td></tr>

<% } else { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.CMD_DISABLE_VERIFY%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Verify Stop</a>
		  </td></tr>
<% } %>		  
		  


<% } %>

        </table>
  </cti:titledContainer>
</div>
</form>
</cti:standardPage>