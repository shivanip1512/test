<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="com.cannontech.core.dao.DaoFactory"%>
<%@ page import="com.cannontech.core.service.DateFormattingService"%>
<%@ page import="com.cannontech.core.service.DateFormattingService.DateFormatEnum"%>
<%@ page import="com.cannontech.yukon.cbc.StreamableCapObject"%>
<%@ page import="com.cannontech.yukon.cbc.Feeder"%>
<%@ page import="com.cannontech.yukon.cbc.SubBus"%>
<%@ page import="com.cannontech.cbc.web.CBCWebUtils"%>
<%@ page import="com.cannontech.spring.YukonSpringHook"%>
<%@ page import="com.cannontech.cbc.cache.CapControlCache"%>
<%@ page import="com.cannontech.util.ServletUtil"%>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory"%>
<%@ page import="com.cannontech.common.constants.LoginController"%>
<%@ page import="com.cannontech.servlet.YukonUserContextUtils"%>
<%@ page import="com.cannontech.user.YukonUserContext"%>
<%@ page import="com.cannontech.web.navigation.CtiNavObject"%>
<jsp:directive.page
	import="com.cannontech.database.db.capcontrol.RecentControls" />
<cti:standardPage title="Results" module="capcontrol">
	<cti:standardMenu />
	<%@include file="cbc_inc.jspf"%>



	<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />

	<!-- necessary DIV element for the OverLIB popup library -->
	<div id="overDiv"
		style="position: absolute; visibility: hidden; z-index: 1000;"></div>

	<%
	FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
	YukonUserContext context = YukonUserContextUtils.getYukonUserContext(request);
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);
	CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
	PointDao pointDao = (PointDao)YukonSpringHook.getBean("pointDao");
	PaoDao paoDao = (PaoDao)YukonSpringHook.getBean("paoDao");
	DateFormattingService dateFormattingService = (DateFormattingService)YukonSpringHook.getBean("dateFormattingService");
	CtiNavObject nav = (CtiNavObject) request.getSession(false).getAttribute(ServletUtil.NAVIGATE);
    String returnURL = nav.getPreviousPageForSearch();
	nav.getHistory().push(returnURL);
	
	int MAX_DAYS_CNT = 7;	
	String type = ParamUtil.getString(request, "type", "");
	String[] strPaoids = ParamUtil.getStrings(request, "value");
	int dayCnt = ParamUtil.getInteger(request, "dayCnt", 1);
	
	Hashtable<Long, List>  logData = new Hashtable<Long, List>();
	String[] titles = new String[ strPaoids.length ];
	if( CBCWebUtils.TYPE_RECENT_CNTRLS.equals(type) )
	{
		boolean hasSystem = false;
		for( int i = 0; i < strPaoids.length; i++ )
		{
			int id = Integer.parseInt( strPaoids[i] );
			StreamableCapObject cbcPAO = filterCapControlCache.getCapControlPAO(new Integer(id));

			if( cbcPAO != null )
			{
				List<RecentControls> events= CBCWebUtils.getCCEventsForPAO(new Long (id), cbcPAO.getCcType(), filterCapControlCache, dayCnt);
				logData.put(new Long(id), events);
				titles[i] = filterCapControlCache.getCapControlPAO(new Integer(id)) +
						"  : Previous " + dayCnt + " days of controls (" + (logData.get(new Long(id))).size() + " events found)";

				//temp hack for only showing 1 table for a SubBus or Feeder
				// since each SubBus or Feeder checked returns the same CapControl system data
				if( cbcPAO instanceof Feeder || cbcPAO instanceof SubBus )
				{
					if( !hasSystem )
					{
						titles[i] = "SYSTEM EVENTS" +
							"  : Previous " + dayCnt + " days of controls (" + (logData.get(new Long(id))).size() + " events found)";
					
						hasSystem = true;
					}
					else
						titles[i] = null;
				}

			}

		}

	}

%>
    <cti:breadCrumbs>
	   <cti:crumbLink url="subareas.jsp" title="Home" /> 
	   <cti:crumbLink url="<%=ServletUtil.getFullURL(request)%>" title="Events" />
	</cti:breadCrumbs>

	<div align="left">
	<table id="filterTable">
		<tr>
			<td>Filter By Date <br />
			<select
				onchange="dateFilter('rcDateFilter', <%=Arrays.toString( strPaoids) %>, '<%=type%>')"
				id="rcDateFilter">
				<% 
				for (int i=1; i <= MAX_DAYS_CNT; i ++) {
				    out.println ("<option value=\"" + i + "\""); 
				    if (i == dayCnt)
				        out.println(" selected");
				    out.println (">" + i + " Day (s) </option>");
				}
				%>
			</select></td>
		</tr>
	</table>
	</div>
	<%
//int paosShown = 0;
Enumeration<Long> paoIDs = logData.keys();
while(paoIDs.hasMoreElements()){
	Long paoID = paoIDs.nextElement();
	if (logData.get(paoID) != null)
	{
	    int id = paoID.intValue();

%>

	<cti:titledContainer
		title="<%="Events for " + DaoFactory.getPaoDao().getLiteYukonPAO(id).getPaoName()%>">


		<form id="resForm" action="feeders.jsp" method="post"><input
			type="hidden" name="itemid" />
		<table id="resHeaderTable<%=id%>" width="100%" border="0"
			cellspacing="0" cellpadding="0">
			<tr class="columnHeader lAlign">
				<td>Timestamp</td>
				<td>Device Controlled</td>
				<td>Item</td>
				<td>Event</td>
				<td>User</td>
			</tr>
		</table>

		<div class="scrollMed">
		<table id="resTable<%=id%>" width="98%" border="0" cellspacing="0"
			cellpadding="0">
			<%			
	if( (logData.get(paoID)).size() <= 0 ) {
%>
			<tr class="alert cAlign">
				<td>No data found</td>
				<td></td>
				<td></td>
			</tr>
			<%
	}
	else for( int j = 0; j < logData.get(paoID).size(); j++ )
	{
		String css = (j % 2 == 0 ? "tableCell" : "altTableCell");
	RecentControls event = (RecentControls)logData.get(paoID).get(j);
%>
			<tr class="<%=css%>">
				<td><%=dateFormattingService.formatDate(event.getTimestamp(),DateFormatEnum.BOTH,context)%></td>
				<% if (event.getPointId() == null || event.getPointId() <= 0) {%>
                <td>----</td>
                <%}else {%>
                <td><%=paoDao.getYukonPAOName(pointDao.getLitePoint(event.getPointId().intValue()).getPaobjectID())%></td>
                <%}%>
				<% if (event.getTimestamp() == null) {%>
				<td>----</td>
				<%}else {%>
				<td><%=event.getItem()%></td>
				<%}%>
				<% if (event.getEvent() == null) {%>
				<td>----</td>
				<%}else{%>
				<td><%=event.getEvent()%></td>
				<%}%>
				<% if (event.getUser()== null) {%>
				<td>----</td>
				<%}else{%>
				<td><%=event.getUser()%></td>
				<%}%>
			</tr>
			<%	

		}  

%>

		</table>
		</div>
		</form>
	</cti:titledContainer>
	<br/>
	<% }

}

	if (strPaoids.length > 0) {
	for (int i=0; i < strPaoids.length; i++) {
	int id = Integer.parseInt(strPaoids[i]);
%>

	<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('resTable<%=id%>','resHeaderTable<%=id%>');});
</script>
	<% }

} 

%>
	<input type="button" value="Back"
		onclick="javascript:location.href='<%=returnURL %>'">

</cti:standardPage>