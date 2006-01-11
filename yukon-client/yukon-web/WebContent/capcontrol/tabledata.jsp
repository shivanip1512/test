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
	SystemLogData[][] logData = new SystemLogData[strPaoids.length][0];

	if( CBCWebUtils.TYPE_RECENT_CNTRLS.equals(type) )
	{
		boolean hasSystem = false;
		for( int i = 0; i < strPaoids.length; i++ )
		{
			int id = Integer.parseInt( strPaoids[i] );
			StreamableCapObject cbcPAO = capControlCache.getCapControlPAO(new Integer(id));

			if( cbcPAO != null )
			{
				logData[i] = CBCWebUtils.getRecentControls( id, capControlCache, PREV_DAY_COUNT );
				titles[i] = capControlCache.getCapControlPAO(new Integer(id)) +
						"  : Previous " + PREV_DAY_COUNT + " days of controls (" + logData[i].length + " events found)";

				//temp hack for only showing 1 table for a SubBus or Feeder
				// since each SubBus or Feeder checked returns the same CapControl system data
				if( cbcPAO instanceof Feeder || cbcPAO instanceof SubBus )
				{
					if( !hasSystem )
					{
						titles[i] = "SYSTEM EVENTS" +
							"  : Previous " + PREV_DAY_COUNT + " days of controls (" + logData[i].length + " events found)";
					
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
for( int i = 0; i < titles.length && paosShown < MAX_PAOIDS; i++ ) {
if( titles[i] != null ) {
	paosShown++;
%>          
      <cti:titledContainer title="<%=titles[i]%>">

          <div class="scrollMed">
			<form id="resForm" action="feeders.jsp" method="post">
			<input type="hidden" name="itemid" />
            <table id="resTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnheader lAlign">				
				<td>Timestamp</td>
                <td>Item</td>
                <td>Event</td>
              </tr>

<%			
	if( logData[i].length <= 0 ) {
%>			
        <tr class="alert cAlign">
			<td>No data found</td>
			<td></td>
			<td></td>
		</tr>
<%
	}
	else for( int j = 0; j < logData[i].length; j++ )
	{
		String css = (j % 2 == 0 ? "tableCell" : "altTableCell");
		SystemLogData systLog = logData[i][j];	
%>
	        <tr class="<%=css%>">
				<td><%=Formatters.DATETIME.format(systLog.getDateTime())%></td>
				<td><%=systLog.getAction()%></td>
				<td><%=systLog.getDescription()%></td>
			</tr>
<%	} %>

            </table>
			</form>
        </div>

      </cti:titledContainer>
<% } %>
<% } %>



</cti:standardPage>