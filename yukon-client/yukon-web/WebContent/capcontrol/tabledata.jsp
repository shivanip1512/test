<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.yukon.cbc.StreamableCapObject" %>
<%@ page import="com.cannontech.yukon.cbc.Feeder" %>
<%@ page import="com.cannontech.yukon.cbc.SubBus" %>
<%@ page import="com.cannontech.database.db.capcontrol.CCEventLog" %>
<%@ page import="com.cannontech.cbc.web.CBCWebUtils" %>
<%@ page import="com.cannontech.database.data.pao.DeviceTypes" %>
<%@ page import="com.cannontech.database.data.pao.CapControlTypes" %>
<%@ page import="com.cannontech.database.db.point.SystemLog" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Results" module="capcontrol">
<cti:standardMenu/>
<%@include file="cbc_inc.jspf"%>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas" />
    <cti:crumbLink url="subs.jsp" title="Substations" />
    <cti:crumbLink url="feeders.jsp" title="Feeders" />
    <cti:crumbLink url="<%=ServletUtil.getFullURL(request)%>" title="Events" />
</cti:breadCrumbs>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
	int MAX_PAOIDS = 3;
	int PREV_DAY_COUNT = 7;

	String type = ParamUtil.getString(request, "type", "");
	String[] strPaoids = ParamUtil.getStrings(request, "value");
	String[] titles = new String[ strPaoids.length ];
	List[] logData = new List [100];
	if( CBCWebUtils.TYPE_RECENT_CNTRLS.equals(type) )
	{
		boolean hasSystem = false;
		for( int i = 0; i < strPaoids.length; i++ )
		{
			int id = Integer.parseInt( strPaoids[i] );
			StreamableCapObject cbcPAO = (StreamableCapObject)capControlCache.getCapControlPAO(new Integer(id));

			if( cbcPAO != null )
			{
				logData[i] = CBCWebUtils.getCCEventsForPAO(new Long (id), cbcPAO.getCcType(), capControlCache, PREV_DAY_COUNT);
				titles[i] = capControlCache.getCapControlPAO(new Integer(id)) +
						"  : Previous " + PREV_DAY_COUNT + " days of controls (" + ((List)logData[i]).size() + " events found)";

				//temp hack for only showing 1 table for a SubBus or Feeder
				// since each SubBus or Feeder checked returns the same CapControl system data
				if( cbcPAO instanceof Feeder || cbcPAO instanceof SubBus )
				{
					if( !hasSystem )
					{
						titles[i] = "SYSTEM EVENTS" +
							"  : Previous " + PREV_DAY_COUNT + " days of controls (" + ((List)logData[i]).size() + " events found)";
					
						hasSystem = true;
					}
					else
						titles[i] = null;
				}

			}

		}

	}
	else
	{
	}
	

%>


<%
int paosShown = 0;
for( int i = 0; i <logData.length; i++) {
//if( titles[i] != null ) {
//	paosShown++;
if ((List)logData[i] != null) {
int id = Integer.parseInt( strPaoids[i] );
%>          

      <cti:titledContainer title="<%="Events for " + DaoFactory.getPaoDao().getLiteYukonPAO(id).getPaoName()%>" >

          
			<form id="resForm" action="feeders.jsp" method="post">
			<input type="hidden" name="itemid" />
            <table id="resHeaderTable<%=i%>" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnHeader lAlign">				
				<td>Timestamp</td>
                <td>Item</td>
                <td>Event</td>
                <td>User</td>                
              </tr>
             </table>

			<div class="scrollMed">
				<table id="resTable<%=i%>" width="98%" border="0" cellspacing="0" cellpadding="0">
<%			
	if( ((List)logData[i]).size() <= 0 ) {
%>			
        <tr class="alert cAlign">
			<td>No data found</td>
			<td></td>
			<td></td>
		</tr>
<%
	}
	else for( int j = 0; j < ((List)logData[i]).size(); j++ )
	{
		String css = (j % 2 == 0 ? "tableCell" : "altTableCell");
		int paoType = DaoFactory.getPaoDao().getLiteYukonPAO(id).getType();
		if ( (paoType == CapControlTypes.CAP_CONTROL_FEEDER) || (paoType == CapControlTypes.CAP_CONTROL_SUBBUS)) {
			CCEventLog systLog = (CCEventLog)((List)logData[i]).get(j);	
%>
	        <tr class="<%=css%>">
				<td><%=Formatters.DATETIME.format(systLog.getDateTime())%></td>
				<% if (systLog.getDateTime() == null) {%>
				<td> ---- </td>
				<%}else {%>		
				<td><%=DaoFactory.getPaoDao().getLiteYukonPAO(systLog.getSubId().intValue()).getPaoName()%></td>
				<%}%>
				<% if (systLog.getText() == null) {%>
				<td>----</td>
				<%}else{%>
				<td><%=systLog.getText()%></td>
				<%}%>
				<% if (systLog.getUserName() == null) {%>
				<td>----</td>
				<%}else{%>
				<td><%=systLog.getUserName()%></td>
				<%}%>
			</tr>
<%	} else if (paoType == DeviceTypes.CAPBANK) { 
			SystemLog systLog = (SystemLog)((List)logData[i]).get(j);	
			
%>				
	 <tr class="<%=css%>">
				<% if (systLog.getDateTime() == null) {%>
				<td>-----</td> 
				<%}else {%>
				<td><%=Formatters.DATETIME.format(systLog.getDateTime())%></td>
				<% }%>
				<% if (systLog.getAction() == null) {%>
				<td>-----</td> 
				<%}else {%>
				<td><%=systLog.getAction()%></td>
				<% }%>
				<% if (systLog.getDescription() == null) {%>
				<td>-----</td> 
				<%}else {%>
				<td><%=systLog.getDescription()%></td>
				<% }%>
				<% if (systLog.getUserName() == null) {%>
				<td>-----</td> 
				<%}else {%>
				<td><%=systLog.getUserName()%></td>
				<% }%>								
	</tr>
	<%}%>
<%}%>

            </table>
           </div>
	  </form>
     

      </cti:titledContainer>
<% } %>

<% } %>
<%
if (strPaoids.length > 0) {
for (int i=0; i < strPaoids.length; i++) {
%>
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('resTable<%=i%>','resHeaderTable<%=i%>');});
</script>
<% } %>
<% } %>


</cti:standardPage>