<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>

<cti:standardPage title="Temp CapBank Move" module="capcontrol_internal">
<%@include file="cbc_inc.jspf"%>

<%
    FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);	
	CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);

	int bankid = ParamUtil.getInteger(request, "bankid");
	int subbusID = 0;
	boolean oneline = ParamUtil.getBoolean(request,"oneline",false);
	List<CBCArea> allAreas = filterCapControlCache.getCbcAreas();	
	CapBankDevice capBank = filterCapControlCache.getCapBankDevice( new Integer(bankid) );
	int oldfdrid = 0;
	if( oneline ){
		subbusID = filterCapControlCache.getParentSubBusID(bankid);
	}
	if( capBank != null )
	{
		oldfdrid = capBank.getParentID();
	}
%>

<c:url var="controlOrderPage" value="/capcontrol/feederBankInfo.jsp"/>
<script type="text/javascript"> 
function updateFeederBankInfo()
{
	var params = {'FeederID': getSelectedFeeder()};
    new Ajax.Updater('ControlOrders', '${controlOrderPage}', {method: 'post', parameters: params});
}
function showDiv( v ){
	$(v).toggle();
}
function getSelectedFeeder()
{
	var selectedFeeder = $("selectedFeeder");
	return selectedFeeder.value;
}
function getOrder( str )
{
	var str = "txt" + str + "Order";
	var Obj = $(str);
	return Obj.value;
}

function setFeederIDinRedirectURL( id, oneline, subbusID )
{
	$("redirectURL").value += "?FeederID=" + id;
	if( oneline ){
		$("redirectURL").value += "&subbusID=" + subbusID;
	}
}

function selectFeeder( fid ){
	$("selectedFeeder").value = fid;
	updateFeederBankInfo();
}

Event.observe(window, 'load', updateFeederBankInfo );

</script>

<div >
 <!--sort of a hack on REDIRECTURL input for now, but I can not figure out how to get around the use of the proxy in XmlHTTP calls-->
 <form id="frmCapBankMove" action="/servlet/CBCServlet" method="post" >
	<input type="hidden" name="redirectURL" value="/capcontrol/moved.jsp" id="redirectURL" >
	<input type="hidden" name="controlType" value="<%=CBCServlet.TYPE_CAPBANK%>">
	<input type="hidden" name="paoID" value="<%=bankid%>">
	<input type="hidden" name="cmdID" value="<%=CBCCommand.CMD_BANK_TEMP_MOVE%>">
	<input type="hidden" name="opt" value="<%=oldfdrid%>">  <!--Old Feeder ID-->
	<input type="hidden" name="opt"> <!--New Feeder ID-->
	<input type="hidden" name="opt"> <!--Display Order-->
	<input type="hidden" name="opt"> <!--Close Order-->
	<input type="hidden" name="opt"> <!--Trip Order-->
	<input type="hidden" name="selectedFeeder" value=<%=oldfdrid%> id="selectedFeeder">

   <table id="innerTable" width="95%" border="0" cellspacing="0" cellpadding="0">
<tr class="columnHeader lAlign">
	<td>Select Feeder Below</td>
	<td></td>
	<td class="rAlign">Display Order:
	<input type="text" id="txtDisplayOrder" class="tableCell" size="1" maxlength="3" value="1.5">
	</td>
	<td class="rAlign">Close Order:
	<input type="text" id="txtCloseOrder" class="tableCell" size="1" maxlength="3" value="1.5">
	</td>
	<td class="rAlign">Trip Order:
	<input type="text" id="txtTripOrder" class="tableCell" size="1" maxlength="3" value="1.5">
	</td>
	<td class="rAlign"><input id="submitOne" type="button" value="Submit" onclick="setFeederIDinRedirectURL(getSelectedFeeder(),<%=oneline %>,<%=subbusID %>);postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip') );" />
	</td>
</tr>

</table>   

<div id="ControlOrders" style="margin-left: 10%; margin-right: 10%;height:25%;max-height:25%;margin-bottom:2%;margin-top:2%;">

</div>

      <div style="margin-left: 10%; margin-right: 10%;" >
<cti:titledContainer title="Feeders Eligible for the Move" >

<div style=" height: 275px; overflow: auto">
<%
String css = "altTableCell";
int z = 0;
String indent = "\t";
for( CBCArea area : allAreas )
{
	List<SubStation> stationsOnArea = filterCapControlCache.getSubstationsByArea( area.getPaoID() );
	%>
	
	<div>		
	<input type="image" id="chkBxArea<%=z%>"
		src="images/nav-plus.gif"
		onclick="showDiv( 'areaId<%=z %>' );toggleImg( 'chkBxArea<%=z%>'); return false;">
	<%=area.getPaoName() %></input>

	<div class="tableCell" style="display:none" id="areaId<%=z %>">
	
	<% for( SubStation station : stationsOnArea ){
		List<SubBus> subsOnStation = filterCapControlCache.getSubBusesBySubStation(station);
	%>
		<div>		
		<input class="lIndent" type="image" id="chkBxStation<%=station.getCcId()%>"
			src="images/nav-plus.gif"
			onclick="showDiv( 'stationId<%=station.getCcId() %>' );toggleImg( 'chkBxStation<%=station.getCcId()%>'); return false;">
		<%=station.getCcName() %></input>
	
	
		<div class="tableCell" style="display:none" id="stationId<%=station.getCcId() %>">
			<%	
			for( SubBus subBus : subsOnStation )
			{
				List<Feeder> feeders = filterCapControlCache.getFeedersBySubBus(subBus.getCcId());
			%>
		
				<div>
				<input class="llIndent" type="image" id="chkBxSub<%=subBus.getCcId()%>"
					src="images/nav-plus.gif"
					onclick="showDiv( 'subId<%=subBus.getCcId()%>' );toggleImg('chkBxSub<%=subBus.getCcId()%>'); return false;">
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %></input>
				
				<div class="altTableCell" style="display:none" id="subId<%=subBus.getCcId()%>" >
			<%
				for( Feeder feeder : feeders )
				{
					if( feeder.getCcId().intValue() == oldfdrid )
						continue;
			%>
					<div>
					<input class="capbankTempMoveLink" type="radio" name="feeder" id="feederId<%=feeder.getCcId()%>" onclick="selectFeeder(<%=feeder.getCcId()%>);" >
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></input>
					</div>			
			<%	} %>
				</div></div>
			<% } %>
			</div></div>
		<% } %>
	</div></div>
<% z++;
	} %>
	
	</div>
	    </cti:titledContainer>
    </div>

</form>
</div>

</cti:standardPage>
