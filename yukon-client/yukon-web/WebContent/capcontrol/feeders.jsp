<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@page import="com.cannontech.roles.capcontrol.CBCOnelineSettingsRole"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Feeders" module="capcontrol">
<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.core.dao.PaoDao" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.cbc.util.CBCUtils" %>

	
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>
<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
    PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);	
    CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
	String nd = "\"return nd();\"";
	String currentPageURL = request.getRequestURL().toString();
	int subid = ccSession.getLastSubID();
	Integer areaId = ccSession.getLastAreaId();
	String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
	
    boolean showFlip = Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(user, CBCSettingsRole.SHOW_FLIP_COMMAND)).booleanValue();
    if (popupEvent == null) popupEvent = "onmouseover";
	SubStation substation = filterCapControlCache.getSubstation( new Integer(subid) );
	List<SubBus> subBuses = filterCapControlCache.getSubBusesBySubStation(substation);
    Collections.sort(subBuses, CBCUtils.SUB_DISPLAY_COMPARATOR);
	List<Feeder> feeders = filterCapControlCache.getFeedersBySubStation(substation);
	List<CapBankDevice> capBanks = filterCapControlCache.getCapBanksBySubStation(substation);
	
	String lastStr = (String) request.getSession(false).getAttribute("lastAccessed");
	int lastAccessed = (lastStr == null) ? -1:Integer.parseInt(lastStr);
	
	boolean hasControl = CBCWebUtils.hasControlRights(session);
	boolean special = filterCapControlCache.isSpecialCBCArea(areaId);
	
	
%>
<script type="text/javascript"> 
	function togglePopup( v ){
		if( document.getElementById(v + '_true') != null )
		{
			$(v + '_true').toggle();
		}
		else if( document.getElementById(v) != null ){
			$(v).toggle();
		}
	}
</script>
<cti:standardMenu/>
<cti:breadCrumbs>
<%
if(special){
%>
  	<cti:crumbLink url="subareas.jsp" title="Home" />
  	<cti:crumbLink url="specialSubAreas.jsp" title="Special Substation Areas" />
<%
} else{
%>
	<cti:crumbLink url="subareas.jsp" title="Home" />
	<cti:crumbLink url="subareas.jsp" title="Substation Areas" />
<%
}
%>
    <cti:crumbLink url="substations.jsp" title="Substations" />
    <cti:crumbLink url="feeders.jsp" title="Feeders" />
</cti:breadCrumbs>

<script type="text/javascript">
   	Event.observe(window, 'load', function () {callBack();});
   	Event.observe(window, 'load', function () {highlightLast();});
    
    GreyBox.preloadGreyBoxImages();

	function highlightLast()
	{
		var id = $("lastAccessedID").value;
		if( id != -1 )
		{
			var elem = $('tr_cap_'+id);
			//verify the id is a capbank. if not no highlight.
			
			if( elem != null ){//find a way to test is teh element is present!
				hiLiteTRow ('tr_cap_'+id , 'lightgrey');
			}
		}
	}

	//returned when a cap bank menu is triggered to appear
	function popupWithHiLite (html, width, height, offsetx, offsety, rowID, color) {
		
		return overlib (html, STICKY, WIDTH, width, HEIGHT, height, OFFSETX, offsetx, OFFSETY, offsety, MOUSEOFF, FULLHTML, ABOVE);
	}
	
	//returned when a cap bank menu is triggered to disappear
	function hidePopupHiLite (rowID, color) {
		nd();
		hiLiteTRow (rowID, color);
	}

	function hiLiteTRow (id, color) {
   		$(id).style.backgroundColor = color;
   	}
   	
   	function onGreyBoxClose () {
   	   window.parent.location.replace('feeders.jsp');
   	}
   	
   	function getSubBusMenu(id){
   		var html = new String($F('cmd_sub_'+id));
   		overlib(html, FULLHTML, STICKY);
   	}
   	
   	function getSubstationMenu(id){
   		var html = new String($F('cmd_substation_'+id));
   		overlib(html, FULLHTML, STICKY);
   	}
   	
   	function getFeederMenu(id){
   		var html = new String($F('cmd_fdr_'+id));
   		overlib(html, FULLHTML, STICKY);
   	}
   	
   	function getCapBankMenu(id){
   		var html = new String($F('cmd_cap_'+id+'_field'));
   		overlib(html, FULLHTML, STICKY);
   		hiLiteTRow ('tr_cap_'+id , 'yellow');
   	}
   	
   	function getCapBankSystemMenu(id){
   		var html = new String($F('cmd_cap_'+id+'_system'));
   		overlib(html, FULLHTML, STICKY);
   		hiLiteTRow ('tr_cap_'+id , 'yellow');
   	}
   	   	
   	function getCapBankTempMoveBack(id){
   		var html = new String($F('cmd_cap_move_back_' + id));
   		overlib(html, FULLHTML, STICKY);
   	}
 </script>
<%
String css = "tableCell";
%>
<input type="hidden" id="lastAccessedID" value="<%= lastAccessed %>">

	<cti:titledContainer title="Substation">
		<table id="substationTable" width="98%" border="0" cellspacing="0" cellpadding="0">
			<tr class="columnHeader lAlign">
				<th>Substation Name</th>
				<th>State</th>
			</tr>
			<%
			if( substation != null ) {
			%>
			<tr class="altTableCell" id="tr_substation_<%=substation.getCcId()%>">
				<td id="anc_<%=substation.getCcId()%>">
					<input type="checkbox" name="cti_chkbxSubs" value="<%=substation.getCcId()%>"/>
					<%=CBCUtils.CBC_DISPLAY.getSubstationValueAt(substation, CBCDisplay.SUB_NAME_COLUMN)%> 
					<% if(substation.getSpecialAreaEnabled()){
					 	String spcAreaName = paoDao.getYukonPAOName(substation.getSpecialAreaId());
					 %>
						 <font color="red">SA <%=spcAreaName%></font>
					<%}%>
				</td>
				
				<td >
				<%
				if( hasControl ) {
				%>
					<!--Create  popup menu html-->
					<input id="cmd_substation_<%=substation.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />				
					<a type="state" name="cti_dyn" id="<%=substation.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(substation)%>;" 
						href="javascript:void(0);"
					    <%=popupEvent%>="getSubstationMenu('<%=substation.getCcId()%>');">
				<%
				} else {
				%>
					<a type="state" name="cti_dyn" id="<%=substation.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(substation)%>;" >
				<%
				}
				%>
					<%=CBCUtils.CBC_DISPLAY.getSubstationValueAt(substation, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
					</a>
				</td>
			</tr>
			<%
			}
			%>
		</table>
	</cti:titledContainer>
	
	<br>

	<cti:titledContainer title="Substation Bus">

		<table id="subTable" width="98%" border="0" cellspacing="0"
			cellpadding="0">
			<tr class="columnHeader lAlign">
				<td><input type="checkbox" name="chkAllSubBusesBx" onclick="checkAll(this, 'cti_chkbxSubBuses');" /></td>
				<td id="subSelect">
				<select id='subBusFilter' onchange='applySubBusFilter(this);'>
				<option>All SubBuses</option>
				<% for( SubBus sub: subBuses) {%>
					<option><%=sub.getCcName()%></option>
				<%}%>
				</select>
				</td>
				<td/>
				<td>State</td>
				<td>Target</td>
				<td>kVAR Load / Est.</td>
				<td>Date/Time</td>
				<td>PFactor / Est.</td>
				<td>kW / Volts</td>
				<td>Daily / Max Ops</td>
			</tr>

<%
css = "tableCell";
for( SubBus subBus: subBuses ) {
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
%>
			<tr class="<%=css%>" id="tr_sub_<%=subBus.getCcId()%>">
				<td id="anc_<%=subBus.getCcId()%>"><input type="checkbox" name="cti_chkbxSubs" value="<%=subBus.getCcId()%>"/>
					<input type="image" id="showSnap<%=subBus.getCcId()%>" src="images/nav-plus.gif" onclick="showRowElems( 'subSnapShot<%=subBus.getCcId()%>', 'showSnap<%=subBus.getCcId()%>'); return false;"/>
				</td>
				<td id="subName">
				<a href="/capcontrol/oneline/OnelineCBCServlet?id=<%=subBus.getCcId()%>&redirectURL=<%=currentPageURL %>" title="Click to view One-Line" ><span><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN)%></span></a>
				<% if( subBus.getVerificationFlag().booleanValue() ) {%>
					<span class="popupImg" onmouseover="statusMsg(this, 'This SubBus is currently being<br>used in a Verification schedule');" >(v)</span>
				<%}%>
				</td>
				
				<td>        
                
                    <input type="warning" name="cti_dyn" id="<%=subBus.getCcId()%>" style="display:none">
                    </input>
                    
                    <span id ="warning_ok_<%=subBus.getCcId()%>" style="display:">
                        <img src="/capcontrol/images/Green.gif"/>
                    </span>
                    <span id ="warning_alert_<%=subBus.getCcId()%>" style="display:none" 
                          onmouseover="showDynamicPopup($('subWarningPopup_<%=subBus.getCcId()%>'));" 
                          onmouseout="nd();"
                          >
                            <img src="/capcontrol/images/Yellow.gif"/>
                    </span>
            
                    <div class="ccVarLoadPopup" id="subWarningPopup_<%=subBus.getCcId()%>" style="display:none" > 
                      <span type="param11" name="cti_dyn" id="<%=subBus.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_WARNING_POPUP)%></span>
                    </div>

                </td>
				
				<td >
				<% if( hasControl ) {%>
					<!--Create  popup menu html-->
					<input id="cmd_sub_<%=subBus.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />				
					<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>"
						style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;"
						href="javascript:void(0);"
						<%=popupEvent%> ="getSubBusMenu('<%=subBus.getCcId()%>');"> 
				<% } else {%>
					<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;" >
				<%}%>
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
					</a>
				</td>
				<td><a onmouseover="showDynamicPopup($('subPFPopup_<%=subBus.getCcId()%>_<%=CBCUtils.isPowerFactorControlled(subBus.getControlUnits())%>'))"
						onmouseout="nd();"
					   	type="param1" 
					   	name="cti_dyn" 
					   	id="<%=subBus.getCcId()%>">
						<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN)%>
					</a>
					<div class="ccPFPopup" id="subPFPopup_<%=subBus.getCcId()%>_<%=CBCUtils.isPowerFactorControlled(subBus.getControlUnits())%>" style="display:none" > 
					  <span type="param9" name="cti_dyn" id="<%=subBus.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_POPUP)%></span>
					</div>
				</td>
				<td><a onmouseover="showDynamicPopup($('subVarLoadPopup_<%=subBus.getCcId()%>'));" 
				       	onmouseout="nd();"
						type="param2" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_COLUMN)%></a>
				<div class="ccVarLoadPopup" id="subVarLoadPopup_<%=subBus.getCcId()%>" style="display:none" > 
				  <span type="param10" name="cti_dyn" id="<%=subBus.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_VAR_LOAD_POPUP)%></span>
				</div>
				</td>
				<td><a type="param3" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TIME_STAMP_COLUMN)%></a>
				</td>
				<td><a type="param4" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_POWER_FACTOR_COLUMN)%></a>
				</td>
				<td><a type="param5" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_WATTS_COLUMN)%></a>
				</td>
				<td><a type="param6" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_DAILY_OPERATIONS_COLUMN)%></a>
				</td>
			</tr>
			<tr class="tableCellSnapShot">
				<td colspan="11">
				<table id="subSnapShot<%=subBus.getCcId()%>">
				
				<%
											if (subBus != null) {
											String areaName = CBCUtils.getAreaNameFromSubStationId(subBus.getCcId().intValue());	
											int varPoint = subBus.getCurrentVarLoadPointID().intValue();
											int wattPoint = subBus.getCurrentWattLoadPointID().intValue();
											int voltPoint = subBus.getCurrentVoltLoadPointID().intValue();
								%>
			        <tr class="tableCellSnapShot" style="display: none;">
			        <td>
			        <b><u>Substation Info</u></b>
			        </td>	
			        </tr>
			        <tr class="tableCellSnapShot" style="display: none;">
			        <td><font  class="lIndent">Area:<%=areaName%></font>
			        <% if(substation.getSpecialAreaEnabled()){
					 	String spcAreaName = paoDao.getYukonPAOName(substation.getSpecialAreaId());
					 %>
						 <font color="red"><%=spcAreaName%> IS ENABLED</font>
					<%}%>
			        </td>
					</tr>
			        <tr class="tableCellSnapShot" style="display: none;">
			        <td><b><font  class="lIndent">Control Method: <%=subBus.getControlMethod()%> (<%=subBus.getControlUnits()%>)</font></b></td>
					</tr>
			        <tr class="tableCellSnapShot" style="display: none;">
			     	<%
			   	        String vrPoint = "(none)";
			   	        if (varPoint != 0) vrPoint = DaoFactory.getPointDao().getPointName(varPoint);
			     	%>
			        <td><b><font  class="lIndent">Var Point: <%=vrPoint%></font></b></td>
					</tr>
				    <tr class="tableCellSnapShot" style="display: none;">
			      	<%
			    	        String wPoint = "(none)";
			    	        if (wattPoint != 0) wPoint = DaoFactory.getPointDao().getPointName(wattPoint);
			      	%>
			        <td><b><font  class="lIndent">Watt Point: <%=wPoint%></font></b></td>
					</tr>
				    <tr class="tableCellSnapShot" style="display: none;">
			        <%
			 		        String vPoint = "(none)";
			 		        if (voltPoint != 0) vPoint = DaoFactory.getPointDao().getPointName(voltPoint);
			        %>
			        <td><b><font  class="lIndent">Volt Point: <%=vPoint%>
			        </font></b></td>
			        </tr>
			<%	 }%>
				
				</table>
				</td>
			</tr>

<%}%>
		</table>
	</cti:titledContainer>

	<br>

	<cti:titledContainer title="Feeders">

    <!--form id="fdrForm" action="feeders.jsp" method="post"-->
		<!-- <table id=fdrHeaderTable width="100%" border="0" cellspacing="0" cellpadding="0">  -->
		<table id="fdrTable" width="98%" border="0" cellspacing="0" cellpadding="0">
        	<tr class="columnHeader lAlign">
         		<td><input type="checkbox" name="chkAllFdrsBx" onclick="checkAll(this, 'cti_chkbxFdrs');" />
         			<select id='feederFilter' onchange='applyFeederSelectFilter(this);'>
						<option>All Feeders</option>
						<% for( Feeder feeder: feeders) {%>
						<option><%=feeder.getCcName()%></option>
						<%}%>
					</select>
				</td>
				<td/>
         		<td>State</td>
         		<td>Target</td>
         		<td>kVAR Load / Est.</td>
         		<td>Date/Time</td>
         		<td>PFactor / Est.</td>
         		<td>kW / Volts</td>
         		<td>Daily/Max Ops</td>
         	</tr>
       	<!-- </table>  -->
		
<%
css = "tableCell";
for( int i = 0; i < feeders.size(); i++ )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	Feeder feeder = feeders.get(i);
%>
				<tr class="<%=css%>">
					<td>
						<input type="checkbox" name="cti_chkbxFdrs" value="<%=feeder.getCcId()%>"/>
						<span><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN)%></span>
					</td>
					
					<td>        
                
	                    <input type="warning" name="cti_dyn" id="<%=feeder.getCcId()%>" style="display:none">
	                    </input>
	                    
	                    <span id ="warning_ok_<%=feeder.getCcId()%>" style="display:">
	                        <img src="/capcontrol/images/Green.gif"/>
	                    </span>
	                    <span id ="warning_alert_<%=feeder.getCcId()%>" style="display:none" 
	                          onmouseover="showDynamicPopup($('subWarningPopup_<%=feeder.getCcId()%>'));" 
	                          onmouseout="nd();"
	                          >
	                            <img src="/capcontrol/images/Yellow.gif"/>
	                    </span>
	            
	                    <div class="ccVarLoadPopup" id="subWarningPopup_<%=feeder.getCcId()%>" style="display:none" > 
	                      <span type="param11" name="cti_dyn" id="<%=feeder.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_WARNING_POPUP)%></span>
	                    </div>

                    </td>
					
					<td>
<%
if( hasControl ) {
%>
						<input id="cmd_fdr_<%=feeder.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />	
						<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>"
							style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;"
							href="javascript:void(0);"
						    <%=popupEvent%> ="getFeederMenu('<%=feeder.getCcId()%>');"> 
<%
	} else {
	%>
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;" >
<%}%><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
						</a>
					</td>
					<td><a onmouseover="showDynamicPopup($('feederPFPopup_<%=feeder.getCcId()%>_<%=CBCUtils.isPowerFactorControlled(feeder.getControlUnits())%>'));"
						   onmouseout="nd();"
						   type="param1" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TARGET_COLUMN)%></a>
					<div class="ccPFPopup" id="feederPFPopup_<%=feeder.getCcId()%>_<%=CBCUtils.isPowerFactorControlled(feeder.getControlUnits())%>" style="display:none" > 
					  <span type="param8" name="cti_dyn" id="<%=feeder.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TARGET_POPUP)%></span>
					</div>
					</td>
					<td ><a onmouseover="showDynamicPopup($('feederVarLoadPopup_<%=feeder.getCcId()%>'));" 
					       	onmouseout="nd();"
							type="param2" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_VAR_LOAD_COLUMN)%></a>
					<div class="ccVarLoadPopup" id="feederVarLoadPopup_<%=feeder.getCcId()%>" style="display:none" > 
					  <span type="param9" name="cti_dyn" id="<%=feeder.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_VAR_LOAD_POPUP)%></span>
					</div>
					</td>
					<td><a type="param3" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TIME_STAMP_COLUMN)%></a>
					</td>
					<td><a type="param4" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_POWER_FACTOR_COLUMN)%></a>
					</td>
					<td><a type="param5" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_WATTS_COLUMN)%></a>
					</td>
					<td><a type="param6" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_DAILY_OPERATIONS_COLUMN)%></a>
					</td>
					<td id="hiddenSubName" style="display:none"><%=filterCapControlCache.getSubBusNameForFeeder(feeder)%></td>
				</tr>
<%
}
%>
		</table>

    </cti:titledContainer>

	<br>

	<cti:titledContainer title="Capacitor Banks" id="last_titled_container">
		<!--  <table id="capBankHeaderTable" width="100%" border="0" cellspacing="0" cellpadding="0">-->
        <div id="capBankDiv">
		<table id="capBankTable" width="98%" border="0" cellspacing="0" cellpadding="0" >
            <tr class="columnHeader lAlign">
                <td><input type="checkbox" name="chkAllBanksBx" onclick="checkAll(this, 'cti_chkbxBanks');" /> </td>
                <td></td>    
                <td>CBC Name</td>
                <td>CB Name (Order) <img class="rAlign popupImg" src="images\question.gif" onmouseover="statusMsg(this, 'Order is the order the CapBank will control in.<br>Commands that can be sent to a field device are initiated from this column');" /></td>                    
                <td>State <img class="rAlign popupImg" src="images\question.gif" onmouseover="statusMsg(this, 'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column');"/> <span id="cb_state_td_hdr2" style="display:none" >[Op Count Value]</span> </td>
                
                <td>Date/Time</td>
                <td>Bank Size</td>
                <td>Parent Feeder</td>
                <td>Daily/Total Op</td>
			</tr>              
<%
css = "tableCell";
for( int i = 0; i < capBanks.size(); i++ ) {
	CapBankDevice capBank = capBanks.get(i);
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	int deviceID = capBank.getControlDeviceID().intValue();
    LiteYukonPAObject obj = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
    String rowColor = ((i % 2) == 0) ? "#eeeeee" : "white";
%>
			<tr class="<%=css%>" id="tr_cap_<%=capBank.getCcId()%>">
				<td><input type="checkbox" name="cti_chkbxBanks" value="<%=capBank.getCcId()%>"/></td>
				<td>		
				
					<input type="warning" name="cti_dyn" id="<%=capBank.getCcId()%>" style="display:none">
					</input>
					
					<span id ="warning_ok_<%=capBank.getCcId()%>" style="display:">
				       	<img src="/capcontrol/images/Green.gif"/>
				    </span>
				    <span id ="warning_alert_<%=capBank.getCcId()%>" style="display:none" 
						  onmouseover="showDynamicPopup($('capWarningPopup_<%=capBank.getCcId()%>'));" 
				       	  onmouseout="nd();"
				       	  >
				       	  	<img src="/capcontrol/images/Yellow.gif"/>
				    </span>
			
					<div class="ccVarLoadPopup" id="capWarningPopup_<%=capBank.getCcId()%>" style="display:none" > 
					  <span type="param7" name="cti_dyn" id="<%=capBank.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_WARNING_POPUP, user)%></span>
					</div>

				</td>
				<td>
				<%
				String name = "---";
				Integer cdId = capBank.getControlDeviceID();
				if (cdId.intValue() != 0) {
				    name = DaoFactory.getPaoDao().getYukonPAOName(cdId);    
				}
				%> 
				<%=name%>
				</td>
				<td>
				<%
				if( hasControl ) {
				%>
					<input id="cmd_cap_<%=capBank.getCcId()%>_field" type="hidden" name = "cmd_dyn" value= "" />
                    <!--2-way device designator-->
                    <input id="2_way_<%=capBank.getCcId()%>" type="hidden" value="<%=CBCUtils.isTwoWay(obj)%>"/>
                    <input id="showFlip_<%=capBank.getCcId()%>" type="hidden" value="<%=showFlip%>"/>
                    <input id="is701x_<%=capBank.getCcId()%>" type="hidden" value="<%=CBCUtils.is701xDevice(obj)%>"/>
                    <a href="javascript:void(0);"
                    <%=popupEvent%> ="getCapBankMenu('<%=capBank.getCcId()%>');" 
                    onmouseout = "hidePopupHiLite('tr_cap_<%=capBank.getCcId()%>', '<%=rowColor%>');">
                    	<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN, user)%>
					</a>
					<%
					} else {
					%>
					<span>
						<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN, user)%>
					</span>
					<%
					}
					%>
					<!-- -------------------------------------->
					<%
					if (CBCUtils.isTwoWay(obj)) {
					%>					
					<a href="#" onclick="return GB_show('Device <%=obj.getPaoName()%>', 'cbcPointTimestamps.jsp?cbcID=<%=obj.getLiteID()%>', 500, 600)" >
						<img class="rAlign popupImg" src="images\magnifier.gif" onmouseover="statusMsg(this, 'Click here to see the timestmap information.<br>for the cap bank controller device');" />
					</a>
					<%
					}
					%>
					<a href="#" onclick="return GB_show('<center> Cap Bank Additional Information </center>', '/spring/capcontrol/capAddInfo?paoID=<%=capBank.getCcId()%>', 500, 600)" onmouseover="statusMsg(this, 'Click to see additional information for the cap bank');">x</a>
				</td>
				<td >
					<%
					if( hasControl ) {
					%>
					<input id="cmd_cap_<%=capBank.getCcId()%>_system" type="hidden" name = "cmd_dyn" value= "" />
					<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>"
						style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;"
						href="javascript:void(0);"
					    onclick ="getCapBankSystemMenu('<%=capBank.getCcId()%>');"
					    onmouseout = "hidePopupHiLite('tr_cap_<%=capBank.getCcId()%>', '<%=rowColor%>');"
					    onmouseover = "showDynamicPopup($('capBankStatusPopup_<%=capBank.getCcId()%>'))"
					    >
					<%
					} else {
					%>
					<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>" href="javascript:void(0);" style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;" >
					<%
					}
					%>
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN, user)%>
					</a>
				<span id="cap_opcnt_span<%=capBank.getCcId()%>" style="display:none; " >
					<label for="opcount" id="opcnt_label"> Op Count: </label>
					<input type="text" name="opcount" id="opcnt_input<%=capBank.getCcId()%>" maxlength="5" size="5"/>
					<a href="javascript:void(0);" onclick="return executeCapBankCommand (<%=capBank.getCcId()%>,12,false,'Reset_OpCount', 'cap_opcnt_span<%=capBank.getCcId()%>');" >Reset</a>
				</span>
					<div id="capBankStatusPopup_<%=capBank.getCcId()%>" style="display:none" > 
					  <span type="param6" name="cti_dyn" id="<%=capBank.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_POPUP, user)%></span>
					</div>
				</td>
				<td><a type="param1" name="cti_dyn" id="<%=capBank.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN, user)%></a>
				</td>
				<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_SIZE_COLUMN, user)%></td>
                <td>
                    <input id="cmd_cap_move_back_<%=capBank.getCcId()%>" type="hidden" value="" />
                    <a href="javascript:void(0);"
                    <% if( capBank.isBankMoved() ) { %>
	                    class="warning" 
	                    <%=popupEvent%>="getCapBankTempMoveBack('<%=capBank.getCcId()%>');" 
	                    onmouseout = <%=nd%> 
                    <% } else { %>
                        onmouseover="statusMsg(this, 'Click here to temporarily move this CapBank from it\'s current parent feeder');"
                        onclick="return GB_show('CapBank Temp Move Target (Pick feeder by clicking on name)','tempmove.jsp?bankid='+<%=capBank.getCcId()%>, 500, 700, onGreyBoxClose);"
                    <% } %>
                    	><span><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_PARENT_COLUMN, user)%></span>
                    	</a>                    
                    </td>
					<td>
						<a type="param2" name="cti_dyn" id="<%=capBank.getCcId()%>">
						<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_DAILY_TOTAL_OP_COLUMN, user)%></a>
					</td>
				</tr>
				<% } %>
			</table>
		</div>
		<input type="hidden" id="lastUpdate" value="">
	</cti:titledContainer>
    <div style = "display:none" id = "outerDiv">
        <cti:titledContainer title="Current Status">
            <div id="cmd_msg_div" />
        </cti:titledContainer>
    </div>
</cti:standardPage>