<%@include file="cbc_inc.jspf"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int MAX_PAOIDS = 3;
	int PREV_DAY_COUNT = 5;

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

<HTML>
<HEAD>
<%@ page 
language="java"
contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"
%>
<link rel="stylesheet" href="base.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">

<TITLE>Results</TITLE>
</HEAD>

<body>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td><%@include file="cbc_header.jspf"%></td>
	</tr>

	<td>
	<table width="95%" border="0" cellspacing="0" cellpadding="0"
		align="center" height="30">
		<tr>
			<td valign="bottom" colspan="2">

			<div class="rAlign">
			<cti:breadCrumb>
				<cti:crLink url="subareas.jsp" title="SubBus Areas" cssClass="crumbs" />
				<cti:crLink url="subs.jsp" title="Substations" cssClass="crumbs" />
				<cti:crLink url="feeders.jsp" title="Feeders" cssClass="crumbs" />
				<cti:crLink url="<%=ServletUtil.getFullURL(request)%>" title="Events" cssClass="crumbs" />
			</cti:breadCrumb>			
			
			<form id="findForm" action="results.jsp" method="post">
				<p class="main">Find: <input type="text" name="searchCriteria">
				<INPUT type="image" name="Go" src="images\GoButton.gif" alt="Find"></p>
			</form>

			</div>
			</td>
		</tr>
	</table>


<%
int paosShown = 0;
for( int i = 0; i < titles.length && paosShown < MAX_PAOIDS; i++ ) {
if( titles[i] != null ) {
	paosShown++;
%>          
      <table width="95%" border="0" cellspacing="0" cellpadding="0">
        <tr> 
          <td class="cellImgFill"><img src="images\Header_left.gif" class="cellImgFill"></td>
          <td class="trimBGColor cellImgShort"><%=titles[i]%></td>
          <td class="cellImgFill"><img src="images\Header_right.gif" class="cellImgFill"></td>
        </tr>
        <tr>
          <td class="cellImgFill lAlign" background="images\Side_left.gif"></td>
          <td>

          <div class="scrollLarge">
            <table id="resTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnheader lAlign">				
				<td>Timestamp</td>
                <td>Item</td>
                <td>Event</td>
              </tr>

			<form id="resForm" action="feeders.jsp" method="post">
			<input type="hidden" name="itemid" />
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
			</form>

            </table>
        </div>

          </td>
          <td class="cellImgFill rAlign" background="images\Side_right.gif"></td>
        </tr>
        <tr>
          <td class="cellImgShort"><img src="images\Bottom_left.gif"></td>
          <td class="cellImgShort" background="images\Bottom.gif"></td>
          <td class="cellImgShort"><img src="images\Bottom_right.gif"></td>
        </tr>
	    <tr class="columnHeader">
			<td><br></td>
		</tr>
      </table>
<% } %>
<% } %>


	</td>
</table>
</body>


</HTML>
