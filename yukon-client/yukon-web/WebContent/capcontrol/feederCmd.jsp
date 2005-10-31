<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.util.*" %>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int feederId = ParamUtil.getInteger(request, "feederId", 0);	
	Feeder feeder = capControlCache.getFeeder( new Integer(feederId) );
%>

<HTML>
<HEAD>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">


<body>

<!-------------- Form for submitting feeder commands ---------------->
<form id="frmFdrCmd" action="/servlet/CBCServlet" method="post">
<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_FEEDER%>">
<input type="hidden" name="paoID">
<input type="hidden" name="cmdID">

<div id="fdrPopupMenu" >
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

<% if( feeder.getCcDisableFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.ENABLE_FEEDER%>)">Enable Feeder</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.DISABLE_FEEDER%>)">Disable Feeder</a>
		  </td></tr>
<% } %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.RESET_OPCOUNT%>)">Reset Op Counts</a>
		  </td></tr>

<% if( feeder.getWaiveControlFlag().booleanValue() ) { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.UNWAIVE_FEEDER%>)">Unwaive Feeder</a>
		  </td></tr>
<% } else { %>
          <tr><td>
          	<a href="#" class="optDeselect"
				onmouseover="changeOptionStyle(this)"
				onclick="postMany('frmFdrCmd', 'paoID', <%=feederId%>, 'cmdID', <%=CBCCommand.WAIVE_FEEDER%>)">Waive Feeder</a>
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

</body>
</html>