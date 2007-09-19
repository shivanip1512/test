<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
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

function showDiv( v ){
	$(v).toggle();
}
function getSelectedFeeder()
{
	var selectedFeeder = $("selectedFeeder");
	return selectedFeeder.value;
}
function getControlOrder()
{
	var asd = $("txtCntrlOrder");
	return asd.value;
}

function setFeederIDinRedirectURL( id )
{
	$("redirectURL").value += "?FeederID=" + id;
}

function selectFeeder( fid ){
	var selectedFeeder = $("selectedFeeder");
	if( selectedFeeder.value != 0 )
	{
		showDiv(selectedFeeder.value+"_unselected");
		showDiv(selectedFeeder.value+"_selected");	
		selectedFeeder.value = fid;
		showDiv(fid+"_unselected");
		showDiv(fid+"_selected");
	}
	else
	{
		selectedFeeder.value = fid;
		showDiv(fid+"_unselected");
		showDiv(fid+"_selected");
	}
}

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
	<input type="hidden" name="opt"> <!--Control Order-->
	<input type="hidden" name="selectedFeeder" value=0 id="selectedFeeder">

   <table id="innerTable" width="95%" border="0" cellspacing="0" cellpadding="0">
<tr class="columnHeader lAlign">
	<td>Select Feeder Below</td>
	<td></td>
	<td class="rAlign">New Control Order:
	<input type="text" id="txtCntrlOrder" class="tableCell" size="1" maxlength="3" value="1">
	</td>
	<td><input id="submitOne" type="button" value="Submit" onclick="setFeederIDinRedirectURL(getSelectedFeeder());postMany('frmCapBankMove', 'opt[1]', getSelectedFeeder(), 'opt[2]', getControlOrder() );" >
	</td>
</tr>
</table>   
      <div class="scrollHuge">

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
		<%=area.getPaoName() %></input>

	
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
		</input>
		<div class="<%=css%>" style="display:none" id="subId<%=i + "_" + z%>" >
	<%
		for( int j = 0; j < feeders.length; j++ )
		{
			Feeder feeder = feeders[j];
			if( feeder.getCcId().intValue() == oldfdrid )
				continue;
	%> <!--  -->
			<div class="capbankTempMoveLink" style="display:visible" id="<%=feeder.getCcId()%>_unselected"
					onclick="selectFeeder(<%=feeder.getCcId()%>);">
				<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></div>
			<div class="capbankTempMoveSelected" style="display:none" id="<%=feeder.getCcId()%>_selected"
					onclick="selectFeeder(<%=feeder.getCcId()%>);">
				<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN) %></div>
			
	<%	} %>
		</div></div>
	<% } %>
	</div></div>
<% z++;
	} %>
    </div>
</form>
</div>

</cti:standardPage>
