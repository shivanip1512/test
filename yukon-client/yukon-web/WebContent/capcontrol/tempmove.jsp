<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:url var="ControlOrderPage" value="/capcontrol/feederBankInfo.jsp"/>
<cti:standardPage title="Temp CapBank Move" module="capcontrol_internal">
<%@include file="cbc_inc.jspf"%>

<jsp:useBean id="capControlCache"
	class="com.cannontech.cbc.web.CapControlCache"
	type="com.cannontech.cbc.web.CapControlCache" scope="application"></jsp:useBean>

<%
	int bankid = ParamUtil.getInteger(request, "bankid");
	List<CBCArea> allAreas = capControlCache.getCbcAreas();
	SubBus[] allSubs = capControlCache.getAllSubBuses();		
	CapBankDevice capBank = capControlCache.getCapBankDevice( new Integer(bankid) );
	int oldfdrid = 0;
	
	if( capBank == null )
	{
		allSubs = new SubBus[0];
	}
	else
	{
		oldfdrid = capBank.getParentID();
	}

	
%>

<script type="text/javascript"> 
function updateFeederBankInfo()
{
	var feederbankurl = "?FeederID=";
	feederbankurl += getSelectedFeeder();
    new Ajax.Updater({ success: 'ControlOrders', failure: 'ControlOrders' }, '${ControlOrderPage}' + feederbankurl, { parameters: {method: 'post'} });
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

function setFeederIDinRedirectURL( id )
{
	$("redirectURL").value += "?FeederID=" + id;
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
	<td class="rAlign"><input id="submitOne" type="button" value="Submit" onclick="setFeederIDinRedirectURL(getSelectedFeeder());postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getOrder('Display'), 'opt[3]', getOrder('Close'), 'opt[4]', getOrder('Trip') );" />
	</td>
</tr>

</table>   

<div id="ControlOrders" style="margin-left: 10%; margin-right: 10%;height:25%;max-height:25%;margin-bottom:2%;margin-top:2%;">
    <jsp:include page=""/>
</div>

      <div style="margin-left: 10%; margin-right: 10%;" >
<cti:titledContainer title="Feeders Eligible for the Move" >
<%
String css = "tableCell";
int z = 0;
String indent = "\t";
for( CBCArea area : allAreas )
{

	SubBus[] subsOnArea = capControlCache.getSubsByArea( area.getPaoID() );

	if( subsOnArea.length <= 0 ) continue;
	
	css = "altTableCell";
	%>
	
		<div>		
		<input type="image" id="chkBxArea<%=z%>"
			src="images/nav-plus.gif"
			onclick="showDiv( 'areaId<%=z %>' );toggleImg( 'chkBxArea<%=z%>'); return false;">
		<%=area.getPaoName() %>

	
		<div class="<%=css%>" style="display:none" id="areaId<%=z %>">
	<%	
	for( int i = 0; i < subsOnArea.length; i++ )
	{
		SubBus subBus = subsOnArea[i];	
		Feeder[] feeders = capControlCache.getFeedersBySub(subBus.getCcId());
		css = "tableCell";
		if( feeders.length <= 0 ) continue;
		
		
	%>

		<div>
		<input class="lIndent" type="image" id="chkBxSub<%=i + "_" + z%>"
			src="images/nav-plus.gif"
			onclick="showDiv( 'subId<%=i + "_" + z%>' );toggleImg('chkBxSub<%=i + "_" + z%>'); return false;">
			<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN) %>
		
		<div class="<%=css%>" style="display:none" id="subId<%=i + "_" + z%>" >
	<%
		for( int j = 0; j < feeders.length; j++ )
		{
			Feeder feeder = feeders[j];
			if( feeder.getCcId().intValue() == oldfdrid )
				continue;
	%>
			<div>
			<input class="capbankTempMoveLink" type="radio" name="feeder" id="feederId<%=feeder.getCcId()%>" onclick="selectFeeder(<%=feeder.getCcId()%>);" >
			<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %>
			
			</div>			
	<%	} %>
		</div></div>
	<% } %>
	</div></div>
<% z++;
	} %>
	    </cti:titledContainer>
    </div>

</form>
</div>

</cti:standardPage>
