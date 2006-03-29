<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="" module="capcontrol_internal">
<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.util.*" %>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int feederId = ParamUtil.getInteger(request, "feederId", 0);	
	Feeder feeder = capControlCache.getFeeder( new Integer(feederId) );
%>


<!-- Form for submitting feeder commands -->
<form id="frmFdrCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_FEEDER%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">

<%if (feederId > 0) {%>
<div class="cmdPopupMenu">
  <cti:titledContainer title="<%=feeder.getCcName()%>">
        <table id="fdrTable" width="100%" border="0" cellspacing="0" cellpadding="0">

<% if( feeder.getCcDisableFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.ENABLE_FEEDER%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Enable Feeder</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.DISABLE_FEEDER%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Disable Feeder</a>
		  </td></tr>
<% } %>
          <tr><td>
          	<a href="javascript:void(0);" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.RESET_OPCOUNT%>); top.document.getElementById('tempIFrame').style.display='none';"
				>Reset Op Counts</a>
		  </td></tr>
		  
        </table>
  </cti:titledContainer>
</div>
<%} else {%>
<tr><td>
<span class="optDeselect">No Feeder selected</span>
</td></tr>
<tr><td>

<a href="feeders.jsp" class="optDeselect" 
>Home</a>
</td></tr>
<%}%>

</form>

</cti:standardPage>