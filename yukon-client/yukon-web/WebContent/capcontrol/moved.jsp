<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>

<cti:standardPage title="CapBank Moved" module="blank">
<%@include file="capcontrolHeader.jspf"%>

<%
    CapControlCache capControlCache = YukonSpringHook.getBean("cbcCache", CapControlCache.class);
	String feederID = request.getParameter("FeederID");
	String subbusIDstr = request.getParameter("subbusID");
	Integer subbusID = null;
	if( subbusIDstr != null )
	{
		subbusID = Integer.valueOf(subbusIDstr);
	}
	String feederName = "null";
	String subName = "null";
	int substationID = 0;
	int areaID = 0;
	if( feederID != null){
		Integer fid = Integer.valueOf(feederID);
		Feeder feederobj = capControlCache.getFeeder(fid);
		feederName = feederobj.getCcName();
		SubBus sub = capControlCache.getSubBus(feederobj.getParentID());
		subName = sub.getCcName();
		substationID = sub.getParentID();
		SubStation substation = capControlCache.getSubstation(substationID);
		areaID = substation.getParentID();
	}
%>

<% if( subbusID != null ) {%>
	<cti:standardMenu/>
<%} %>

<div >
 <form id="capbankmoved" action="/capcontrol/moved.jsp" method="post">
	
	<div style="text-align: center;font-weight: bold;">CapBank moved to <%= feederName %> on <%= subName %>. </div>
	<% if( subbusID != null ) {%>
		<div style="text-align: center;"><a href="/oneline/OnelineCBCServlet?id=<%=subbusID %>&redirectURL=/capcontrol/tier/feeders?isSpecialArea=false&amp;substationId=<%=substationID %>&amp;areaId=<%=areaID %>">Return to OneLine</a></div>
	<%} %>
</form>
</div>

</cti:standardPage>