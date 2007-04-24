<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Substation Bus Areas" module="capcontrol">
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>
	<%
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);	
	CapControlUserOwnerDAO userOwner = new CapControlUserOwnerDAO (capControlCache, user);
    String nd = "\"return nd();\"";
    String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
    if (popupEvent == null) popupEvent = "onmouseover"; 
	%>
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<cti:standardMenu/>

<cti:breadCrumbs>
    <cti:crumbLink url="subareas.jsp" title="SubBus Areas"/>
</cti:breadCrumbs>

    <cti:titledContainer title="Substation Bus Areas" id="last_titled_container">
          
		<form id="areaForm" action="subs.jsp" method="post">
			<input type="hidden" name="<%=CBCSessionInfo.STR_CBC_AREA%>" />
            <table id="areaHeaderTable" width="100%" border="0" cellspacing="0" cellpadding="0">
              <tr class="columnHeader lAlign">				
				<td>Area Name</td>
                <td>State</td>
                <td>Setup</td>
                <td>Closed kVARS</td>
                <td>Tripped kVARS</td>
                <td>PFactor/Est.</td>
              </tr>
              </table>
              <div >
		<table id="areaTable" width="98%" border="0" cellspacing="0" cellpadding="0" >
<%
		String css = "tableCell";
		for( int i = 0; i < userOwner.getCbcAreas().size(); i++ )
		{
			css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
			CBCArea area = (CBCArea)userOwner.getCbcAreas().get(i);
			
			SubBus[] areaBuses = userOwner.getSubsByArea(area.getPaoID());
			
			CapBankDevice[] areaCapBanks = userOwner.getCapBanksByArea(area.getPaoID());
			
			String totalVars =
				CBCUtils.format( CBCUtils.calcTotalVARS(areaCapBanks) );
			String availVars =
				CBCUtils.format( CBCUtils.calcAvailableVARS(areaCapBanks) );

			String currPF = CBCDisplay.getPowerFactorText(
								CBCUtils.calcAvgPF(areaBuses), true);
			String estPF = CBCDisplay.getPowerFactorText(
								CBCUtils.calcAvgEstPF(areaBuses), true);
            String areaState = ((Boolean)(userOwner.getAreaStateMap().get(area.getPaoName())))?"ENABLED":"DISABLED";
%>
	        <tr class="<%=css%>">
				<td>				
				<input type="checkbox" name="cti_chkbxAreas" value="<%=area.getPaoID()%>"/>
				<input type="image" id="showAreas<%=i%>"
					src="images/nav-plus.gif"
					onclick="showRowElems( 'allAreas<%=i%>', toggleImg('showAreas<%=i%>') ); return false;"/>
				<a href="#" class="<%=css%>" onclick="postMany('areaForm', '<%=CBCSessionInfo.STR_CBC_AREA%>', '<%=area.getPaoName()%>')">
				<%=area.getPaoName()%></a>
				</td>
                <td>
                <!--Create  popup menu html-->               
                <div id = "serverMessage<%=i%>" style="display:none" > </div>
                
                <input id="cmd_area_<%=area.getPaoID()%>" type="hidden" name = "cmd_dyn" value= "" />
                <a id="area_state_<%=i%>" name="area_state" 
                    style="<%=css%>"
                    href="javascript:void(0);"
                    <%= popupEvent %> ="return overlib(
                        $F('cmd_area_<%=area.getPaoID()%>'),
                        STICKY, WIDTH,210, HEIGHT,170, OFFSETX,-15,OFFSETY,-15,
                        MOUSEOFF, FULLHTML);"
                    onmouseout= <%=nd%> >
                <%=areaState%>
                </a>
                </td>
				<td><%=areaBuses.length%> Substation(s)</td>
				<td><%=totalVars%></td>
				<td><%=availVars%></td>
				<td><%=currPF%> / <%=estPF%></td>
			</tr>


			<a id="allAreas<%=i%>">
<%
if (areaBuses.length > 0) {		
	for( int j = 0; j < areaBuses.length; j++ )
		{
			SubBus subBus = areaBuses[j];
			Feeder[] subFeeders = userOwner.getFeedersBySub(subBus.getCcId());
			CapBankDevice[] subCapBanks = userOwner.getCapBanksBySub(subBus.getCcId());
%>
		        <tr class="<%=css%>" style="display: none;">
					<td><font class="lIndent"><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN)%></font></td>
					<td><%=subFeeders.length%> Feeder(s), <%=subCapBanks.length%> Bank(s)</td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
<% 		} %>
	<%}%>
  </a>
<% } %>

            </table>
			</div>
			</form>
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('areaTable','areaHeaderTable');});
Event.observe(window, 'load', function () {
    getServerData();     
});

function getServerData() {
var els = document.getElementsByName('area_state');
for (var i=0; i < els.length; i++) {
     new Ajax.PeriodicalUpdater("serverMessage"+i, '/servlet/CBCServlet', 
                                {method:'post', asynchronous:true, parameters:'areaIndex='+i, frequency:5, onSuccess: updateAreaMenu});
     

}

}


function updateAreaMenu (resp) {
var msgs = resp.responseText.split(':');
var areaname = msgs[0];
var areaindex = msgs[1];
var areaID = msgs[2];
var areastate = msgs[3];
//update state
document.getElementById ('area_state_' + areaindex).innerHTML = areastate;
//update menu
if (areastate == 'ENABLED')
    document.getElementById('cmd_area_' + areaID).value = generate_SubAreaMenu(areaID,areaname, 0);
if (areastate == 'DISABLED')
    document.getElementById('cmd_area_' + areaID).value = generate_SubAreaMenu(areaID,areaname, 1);
}



</script>
      </cti:titledContainer>
      <div style = "display:none" id = "outerDiv">
      <cti:titledContainer title="Current Status">
          <div id="cmd_msg_div" />
      </cti:titledContainer>
</cti:standardPage>