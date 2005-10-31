<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.util.*" %>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int subId = ParamUtil.getInteger(request, "subId", 0);
	SubBus subBus = capControlCache.getSubBus( new Integer(subId) );
%>


<HTML>
<HEAD>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">


<body>

<!-------------- Form for submitting substation commands ---------------->
<form id="frmSubCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_SUB%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">

<div id="subPopupMenu" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr> 
      <td class="popupCell"><img src="images/Header_left.gif" class="popupHeader"></td>
      <td class="trimBGColor popupHeader">SubBus Control</td>
      <td class="popupCell"><img src="images/Header_right.gif" class="popupHeader"></td>
    </tr>
    <tr>
      <td class="popupCell lAlign" background="images/Side_left.gif"></td>
      <td>
        <table id="subTable" width="100%" border="0" cellspacing="0" cellpadding="0">
<% if ( subId <= 0 ) { %>
          <tr><td>
          	<span class="optDeselect">No SubBus selected</span>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.CONFIRM_CLOSE%>)">Confirm Sub</a>
		  </td></tr>

<% if( subBus.getCcDisableFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.ENABLE_SUBBUS%>)">Enable Sub</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.DISABLE_SUBBUS%>)">Disable Sub</a>
		  </td></tr>
<% } %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.RESET_OPCOUNT%>)">Reset Op Counts</a>
		  </td></tr>

<% if( subBus.getWaiveControlFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.UNWAIVE_SUB%>)">Unwaive Sub</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmSubCmd', 'paoID', <%=subId%>, 'cmdID', <%=CBCCommand.WAIVE_SUB%>)">Waive Sub</a>
		  </td></tr>
<% } %>

<% } %>

        </table>
      </td>
      <td class="popupCell rAlign" background="images/Side_right.gif"></td>
    </tr>
    <tr>
      <td class="popupCell"><img src="images/Bottom_left.gif"></td>
      <td class="popupCell" background="images/Bottom.gif"></td>
      <td class="popupCell"><img src="images/Bottom_right.gif"></td>
    </tr>
  </table>
</div>
</form>

</body>
</html>