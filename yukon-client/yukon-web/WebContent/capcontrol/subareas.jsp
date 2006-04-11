<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Substation Bus Areas" module="capcontrol">
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas"/>
</cti:breadCrumbs>

    <cti:titledContainer title="Substation Bus Areas">
          
		<form id="areaForm" action="subs.jsp" method="post">
			<input type="hidden" name="<%=CBCSessionInfo.STR_CBC_AREA%>" />
            <table id="areaHeaderTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnHeader lAlign">				
				<td>Area Name</td>
                <td>Setup</td>
                <td>Closed kVARS</td>
                <td>Tripped kVARS</td>
                <td>PFactor/Est.</td>
              </tr>
              </table>
              <div class="scrollLarge">
		<table id="areaTable" width="98%" border="0" cellspacing="0" cellpadding="0" >
<%
		String css = "tableCell";
		for( int i = 0; i < capControlCache.getAreaNames().size(); i++ )
		{
			css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
			String areaStr = (String)capControlCache.getAreaNames().get(i);
			SubBus[] areaBuses = capControlCache.getSubsByArea(areaStr);
			//Feeder[] areaFeeders = capControlCache.getFeedersByArea(areaStr);
			CapBankDevice[] areaCapBanks = capControlCache.getCapBanksByArea(areaStr);
			
			String totalVars =
				CBCUtils.format( CBCUtils.calcTotalVARS(areaCapBanks) );
			String availVars =
				CBCUtils.format( CBCUtils.calcAvailableVARS(areaCapBanks) );

			String currPF = CBCDisplay.getPowerFactorText(
								CBCUtils.calcAvgPF(areaBuses), true);
			String estPF = CBCDisplay.getPowerFactorText(
								CBCUtils.calcAvgEstPF(areaBuses), true);
%>
	        <tr class="<%=css%>">
				<td>				
				<input type="image" id="showAreas<%=i%>"
					src="images/nav-plus.gif"
					onclick="showRowElems( 'allAreas<%=i%>', toggleImg('showAreas<%=i%>') ); return false;"/>
				<a href="#" class="<%=css%>" onclick="postMany('areaForm', '<%=CBCSessionInfo.STR_CBC_AREA%>', '<%=areaStr%>')">
				<%=areaStr%></a>
				</td>
				<td><%=areaBuses.length%> Substation(s)</td>
				<td><%=totalVars%></td>
				<td><%=availVars%></td>
				<td><%=currPF%> / <%=estPF%></td>
			</tr>


			<a id="allAreas<%=i%>">
<%
		for( int j = 0; j < areaBuses.length; j++ )
		{
			SubBus subBus = areaBuses[j];
			Feeder[] subFeeders = capControlCache.getFeedersBySub(subBus.getCcId());
			CapBankDevice[] subCapBanks = capControlCache.getCapBanksBySub(subBus.getCcId());
%>
		        <tr class="<%=css%>" style="display: none;">
					<td><font class="lIndent"><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN)%></font></td>
					<td><%=subFeeders.length%> Feeder(s), <%=subCapBanks.length%> Bank(s)</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
<% 		} %>
			</a>


<% } %>

            </table>
			</div>
			</form>
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('areaTable','areaHeaderTable');});
</script>
      </cti:titledContainer>
</cti:standardPage>