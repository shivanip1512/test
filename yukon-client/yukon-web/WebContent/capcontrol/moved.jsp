<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="CapBank Moved" module="capcontrol_internal">
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	String feederID = request.getParameter("FeederID");
	Feeder feederobj = capControlCache.getFeeder(Integer.valueOf(feederID));
	String feederName = feederobj.getCcName();
	SubBus sub = capControlCache.getSubBus(feederobj.getParentID());
	String subName = sub.getCcName();
%>

<div >
 <form id="capbankmoved" action="/capcontrol/moved.jsp" method="post">
	
	<div class="capbankMoved" >CapBank moved to <%= feederName %> on <%= subName %>. </div>
	
</form>
</div>

</cti:standardPage>