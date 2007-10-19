<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:standardPage title="Feeders" module="capcontrol">
<%@include file="cbc_inc.jspf"%>
<%@ page import="com.cannontech.common.constants.LoginController" %>
<%@ page import="com.cannontech.core.dao.DaoFactory" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.cbc.util.CBCUtils" %>

<jsp:useBean id="filterCapControlCache"
	class="com.cannontech.cbc.web.FilterCapControlCacheImpl"
	type="com.cannontech.cbc.web.FilterCapControlCacheImpl" scope="application"></jsp:useBean>
	
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>
<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
	String nd = "\"return nd();\"";
	int subid = ccSession.getLastSubID();
	Integer areaId = ccSession.getLastAreaId();
	LiteYukonUser user = (LiteYukonUser) session.getAttribute(LoginController.YUKON_USER);	
	String popupEvent = DaoFactory.getAuthDao().getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
    boolean showFlip = Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(user, CBCSettingsRole.SHOW_FLIP_COMMAND)).booleanValue();
    if (popupEvent == null) popupEvent = "onmouseover";
	SubStation substation = filterCapControlCache.getSubstation( new Integer(subid) );
	List<SubBus> subBuses = filterCapControlCache.getSubBusesBySubStation(substation);
	List<Feeder> feeders = filterCapControlCache.getFeedersBySubStation(substation);
	List<CapBankDevice> capBanks = filterCapControlCache.getCapBanksBySubStation(substation);

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
  	<cti:crumbLink url="specialSubAreas.jsp" title="Special SubBus Areas" />
<%
} else{
%>
	<cti:crumbLink url="subareas.jsp" title="SubBus Areas" />
<%
}
%>
    <cti:crumbLink url="substations.jsp" title="Substations" />
    <cti:crumbLink url="feeders.jsp" title="Feeders" />
</cti:breadCrumbs>

<script type="text/javascript">
   	Event.observe(window, 'load', function () {callBack();});
    GreyBox.preloadGreyBoxImages();

	//returned when a cap bank menu is triggered to appear
	function popupWithHiLite (html, width, height, offsetx, offsety, rowID, color) {
		hiLiteTRow (rowID, color);
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
   	
 </script>
<%
String css = "tableCell";
%>
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
				</td>
				
				<td >
				<%
				if( hasControl ) {
				%>
					<!--Create  popup menu html-->
					<input id="cmd_substation_<%=substation.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />				
					<a type="state" name="cti_dyn" id="<%=substation.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(substation)%>;" 
						href="javascript:void(0);"
					    <%=popupEvent%> ="return overlib($F('cmd_sub_<%=substation.getCcId()%>'), STICKY, WIDTH,210, HEIGHT,170, OFFSETX,-15,OFFSETY,-15,MOUSEOFF, FULLHTML);"
					    onmouseout = <%=nd%> > 
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
				<th>Sub Name</th>
				<th>State</th>
				<th>Target</th>
				<th>KVAR Load / Est.</th>
				<th>Date/Time</th>
				<th>PFactor / Est.</th>
				<th>Watts / Volts</th>
				<th>Daily / Max Ops</th>
			</tr>

<%
css = "tableCell";
for( int i = 0; i < subBuses.size(); i++ ) {
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	SubBus subBus = subBuses.get(i);
%>
			<tr class="altTableCell" id="tr_sub_<%=subBus.getCcId()%>">
				<td id="anc_<%=subBus.getCcId()%>"><input type="checkbox" name="cti_chkbxSubs" value="<%=subBus.getCcId()%>"/>
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_NAME_COLUMN)%> 
				<%
					if( subBus.getVerificationFlag().booleanValue() ) {
					%>
					<span class="popupImg"
						onmouseover="statusMsg(this, 'This SubBus is currently being<br>used in a Verification schedule');" >
					(v)</span>
				<%
				}
				%>				
				<input type="image" id="showSnap"
					src="images/nav-plus.gif"
					onclick="showRowElems( 'subSnapShot', 'showSnap'); return false;"/>
				</td>
				
				<td >
				<%
				if( hasControl ) {
				%>
					<!--Create  popup menu html-->
					<input id="cmd_sub_<%=subBus.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />				
					<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>"
						style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;"
						href="javascript:void(0);"
					    <%=popupEvent%> ="return overlib(
							$F('cmd_sub_<%=subBus.getCcId()%>'),
							STICKY, WIDTH,210, HEIGHT,170, OFFSETX,-15,OFFSETY,-15,MOUSEOFF, FULLHTML);"
					    onmouseout = <%=nd%> > 
					    
				<%
				} else {
				%>
					<a type="state" name="cti_dyn" id="<%=subBus.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(subBus)%>;" >
				<%
				}
				%>
					<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_CURRENT_STATE_COLUMN)%>
					</a>
				</td>
				
				<td><a onmouseover="javascript:togglePopup('subPFPopup_<%=subBus.getCcId()%>')" 
				       	onmouseout="javascript:togglePopup('subPFPopup_<%=subBus.getCcId()%>')"
					   	type="param1" name="cti_dyn" id="<%=subBus.getCcId()%>">
				<%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_COLUMN)%></a>
				<div class="ccPFPopup" id="subPFPopup_<%=subBus.getCcId()%>_<%=CBCUtils.isPowerFactorControlled(subBus.getControlUnits())%>" style="display:none" > 
				  <span type="param9" name="cti_dyn" id="<%=subBus.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getSubBusValueAt(subBus, CBCDisplay.SUB_TARGET_POPUP)%></span>
				</div>
				</td>
				<td><a onmouseover="javascript:togglePopup('subVarLoadPopup_<%=subBus.getCcId()%>')" 
				       	onmouseout="javascript:togglePopup('subVarLoadPopup_<%=subBus.getCcId()%>')"
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
			<tr>
				<table id="subSnapShot">
				<%
				if (subBus != null) {
					String areaName = CBCUtils.getAreaNameFromSubId(subBus.getCcId().intValue());	
					int varPoint = subBus.getCurrentVarLoadPointID().intValue();
					int wattPoint = subBus.getCurrentWattLoadPointID().intValue();
					int voltPoint = subBus.getCurrentVoltLoadPointID().intValue();
				%>
			        <tr class="tableCell" style="display: none;">
			        <td>
			        <b><u>Substation Info</u></b>
			        </td>	
			        </tr>
			        <tr class="tableCell" style="display: none;">
			        <!--td><font  class="lIndent">Area:<%=subBus.getCcArea()%></font></td-->
			        <td><font  class="lIndent">Area:<%=areaName%></font></td>
					</tr>
			        <tr class="tableCell" style="display: none;">
			        <td><b><font  class="lIndent">Control Method: <%=subBus.getControlMethod()%> (<%=subBus.getControlUnits()%>)</font></b></td>
					</tr>
			        <tr class="tableCell" style="display: none;">
			     	<%
			   	        String vrPoint = "(none)";
			   	        if (varPoint != 0) vrPoint = DaoFactory.getPointDao().getPointName(varPoint);
			     	%>
			        <td><b><font  class="lIndent">Var Point: <%=vrPoint%></font></b></td>
					</tr>
				    <tr class="tableCell" style="display: none;">
			      	<%
			    	        String wPoint = "(none)";
			    	        if (wattPoint != 0) wPoint = DaoFactory.getPointDao().getPointName(wattPoint);
			      	%>
			        <td><b><font  class="lIndent">Watt Point: <%=wPoint%></font></b></td>
					</tr>
				    <tr class="tableCell" style="display: none;">
			        <%
			 		        String vPoint = "(none)";
			 		        if (voltPoint != 0) vPoint = DaoFactory.getPointDao().getPointName(voltPoint);
			        %>
			        <td><b><font  class="lIndent">Volt Point: <%=vPoint%>
			        </font></b></td>
					</tr>
			<%		
				}
			%>
				</table>
			</tr>
<%
}
%>
		</table>
	</cti:titledContainer>

	<br>

	<cti:titledContainer title="Feeders">

    <!--form id="fdrForm" action="feeders.jsp" method="post"-->
       <table id=fdrHeaderTable width="100%" border="0" cellspacing="0" cellpadding="0">
         <tr class="columnHeader lAlign">
         <td><input type="checkbox" name="chkAllFdrsBx"
              onclick="checkAll(this, 'cti_chkbxFdrs');" /> Feeder Name</td>
         <td>State</td>
         <td>Target</td>
         <td>kVAR Load / Est.</td>
         <td>Date/Time</td>
         <td>PFactor / Est.</td>
         <td>kW / Volts</td>
         <td>Daily / Max Ops</td>
         </tr>
       </table>

			<div>
			<table id="fdrTable" width="98%" border="0" cellspacing="0" cellpadding="0">
<%
css = "tableCell";
for( int i = 0; i < feeders.size(); i++ )
{
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	Feeder feeder = feeders.get(i);
%>
				<tr class="<%=css%>">
					<td><input type="checkbox" name="cti_chkbxFdrs" value="<%=feeder.getCcId()%>"/><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_NAME_COLUMN)%></td>

					<td>
<%
if( hasControl ) {
%>
	<input id="cmd_fdr_<%=feeder.getCcId()%>" type="hidden" name = "cmd_dyn" value= "" />	
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>"
		style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;"
		href="javascript:void(0);"
	    <%=popupEvent%> ="return overlib(
			$F('cmd_fdr_<%=feeder.getCcId()%>'),
			STICKY, WIDTH,<%=feeder.getCcName().length() * 8 +  75%>, HEIGHT,75, OFFSETX,-15, OFFSETY,-15,
			MOUSEOFF, FULLHTML);"
	    onmouseout = <%=nd%> >	
<%
	} else {
	%>
	<a type="state" name="cti_dyn" id="<%=feeder.getCcId()%>" style="color: <%=CBCDisplay.getHTMLFgColor(feeder)%>;" >
<%
}
%>
	<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_CURRENT_STATE_COLUMN)%>
</a>
					</td>
					<td><a onmouseover="javascript:togglePopup('feederPFPopup_<%=feeder.getCcId()%>')"
						   onmouseout="javascript:togglePopup('feederPFPopup_<%=feeder.getCcId()%>')"
						   type="param1" name="cti_dyn" id="<%=feeder.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TARGET_COLUMN)%></a>
					<div class="ccPFPopup" id="feederPFPopup_<%=feeder.getCcId()%>_<%=CBCUtils.isPowerFactorControlled(feeder.getControlUnits())%>" style="display:none" > 
					  <span type="param8" name="cti_dyn" id="<%=feeder.getCcId()%>"><%=CBCUtils.CBC_DISPLAY.getFeederValueAt(feeder, CBCDisplay.FDR_TARGET_POPUP)%></span>
					</div>
					</td>
					<td ><a onmouseover="javascript:togglePopup('feederVarLoadPopup_<%=feeder.getCcId()%>')" 
					       	onmouseout="javascript:togglePopup('feederVarLoadPopup_<%=feeder.getCcId()%>')"
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
				</tr>
<%
}
%>

				
			</table>
			</div>
            <!--/form-->
<script type="text/javascript">
Event.observe(window, 'load', function() { new CtiNonScrollTable('fdrTable','fdrHeaderTable');    }, false);
</script>

			</cti:titledContainer>

	<br>

	<cti:titledContainer title="Capacitor Banks" id="last_titled_container">
         <!--form id="capBankForm" action="feeders.jsp" method="post"-->
             <table id="capBankHeaderTable" width="100%" border="0" cellspacing="0" cellpadding="0">
             
             <tr class="columnHeader lAlign">
                <td><input type="checkbox" name="chkAllBanksBx"
                    onclick="checkAll(this, 'cti_chkbxBanks');" /> </td>
                    <td> CBC Name
                    </td>                    

                    <td> CB Name (Order)
                        <img class="rAlign popupImg" src="images\question.gif"
                            onmouseover="statusMsg(this, 'Order is the order the CapBank will control in.<br>Commands that can be sent to a field device are initiated from this column');" />
                 </td>                    
                <td  >State <img class="rAlign popupImg" src="images\question.gif"
                        onmouseover="statusMsg(this, 'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column');"/>
                </td>

                <td id="cb_state_td_hdr2" style="display:none" > Op Count Value</td>
                <td>Date/Time</td>
                <td>Bank Size</td>
                <td>Parent Feeder</td>
                <td>Daily/Total Op</td>
              </tr>              
             </table>
		<div id="capBankDiv">
			<table id="capBankTable" width="98%" border="0" cellspacing="0" cellpadding="0" >

<td id="parent_fdr_td"/>  

<%
css = "tableCell";
for( int i = 0; i < capBanks.size(); i++ )
{
	CapBankDevice capBank = capBanks.get(i);
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	int deviceID = capBank.getControlDeviceID().intValue();
    LiteYukonPAObject obj = DaoFactory.getPaoDao().getLiteYukonPAO(deviceID);
    String rowColor = ((i % 2) == 0) ? "#eeeeee" : "white";
%>
				<tr class="<%=css%>" id="tr_cap_<%=capBank.getCcId()%>">
										

					<td><input type="checkbox" name="cti_chkbxBanks" value="<%=capBank.getCcId()%>" /></td>
					<td>
					<%
								String name = "---";
								Integer cdId = capBank.getControlDeviceID();
								if (cdId.intValue() != 0)
								{
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
						    <%=popupEvent%>= "popupWithHiLite ($F('cmd_cap_<%=capBank.getCcId()%>_field'),
						    									155,110,40,15,
						    									'tr_cap_<%=capBank.getCcId()%>',
						    									'yellow'); "
						    onmouseout = "return hidePopupHiLite('tr_cap_<%=capBank.getCcId()%>', '<%=rowColor%>');" >	
							
							<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN)%>
						</a>
					<%
					} else {
					%>
						<span>
							<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_NAME_COLUMN)%>
						</span>
					<%
					}
					%>

					<!-- -------------------------------------->
					<%
					if (CBCUtils.isTwoWay(obj)) {
					%>					
						<a href="#" 
							onclick="return GB_show('Device <%=obj.getPaoName()%>', 'cbcPointTimestamps.jsp?cbcID=<%=obj.getLiteID()%>', 500, 600)" >
					        <img class="rAlign popupImg" src="images\magnifier.gif"
                        		onmouseover="statusMsg(this, 'Click here to see the timestmap information.<br>for the cap bank controller device');" />
       
					</a>
					<%
					}
					%>
						<a href="#" 
							onclick="return GB_show('<center> Cap Bank Additional Information </center>', '/spring/capcontrol/capAddInfo?paoID=<%=capBank.getCcId()%>', 500, 600)" 
                            onmouseover="statusMsg(this, 'Click to see additional information for the cap bank');" >       
						x
					</a>
					</td>

					<td >
					<%
					if( hasControl ) {
					%>
						<input id="cmd_cap_<%=capBank.getCcId()%>_system" type="hidden" name = "cmd_dyn" value= "" />
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>"
							style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;"
							href="javascript:void(0);"
						    <%=popupEvent%>= "popupWithHiLite ($F('cmd_cap_<%=capBank.getCcId()%>_system'),
    									155,200,40,15,
    									'tr_cap_<%=capBank.getCcId()%>',
    									'yellow'); "
						    onmouseout = "return hidePopupHiLite('tr_cap_<%=capBank.getCcId()%>', '<%=rowColor%>');" >		
					<%
							} else {
							%>
						<a type="state" name="cti_dyn" id="<%=capBank.getCcId()%>" href="javascript:void(0);" style="color: <%=CBCDisplay.getHTMLFgColor(capBank)%>;" >
					<%
					}
					%>
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_STATUS_COLUMN)%>
					</a>
					
					</td>
					<td id="cap_opcnt_span<%=capBank.getCcId()%>" style="display:none; " >
						<label for="opcount" id="opcnt_label"> Op Count: </label>
						<input type="text" name="opcount" id="opcnt_input<%=capBank.getCcId()%>" maxlength="5" size="5"/>
						<a href="javascript:void(0);" onclick="return executeCapBankCommand (<%=capBank.getCcId()%>,12,false,'Reset_OpCount', 'cap_opcnt_span<%=capBank.getCcId()%>');" >
						Reset</a>
					
					</td>
					<td><a type="param1" name="cti_dyn" id="<%=capBank.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_TIME_STAMP_COLUMN)%></a>
					</td>
					
					<td><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_BANK_SIZE_COLUMN)%></td>
                    <td>
                    <input id="cmd_cap_move_back_<%=capBank.getCcId()%>" type="hidden" value= "" />
                    <a href="javascript:void(0);"
                        <% if( capBank.isBankMoved() ) { %>
               
                            class="warning" <%=popupEvent%>=
                            "
                            return overlib(
                                    $F('cmd_cap_move_back_<%=capBank.getCcId()%>'),
                                    STICKY, WIDTH,155, HEIGHT,50, OFFSETX,-15,OFFSETY,-15, FULLHTML);"
                                    onmouseout = <%=nd%> 
                        <% } else { %>
                            onmouseover="statusMsg(this, 'Click here to temporarily move this CapBank from it\'s current parent feeder');"
                            onclick="return GB_show('CapBank Temp Move Target (Pick feeder by clicking on name)','tempmove.jsp?bankid='+<%=capBank.getCcId()%>, 500, 700, onGreyBoxClose);"
                        <% } %>
                        ><%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_PARENT_COLUMN)%>
                    </a>                    
                    </td>
					<td>
					<a type="param2" name="cti_dyn" id="<%=capBank.getCcId()%>">
					<%=CBCUtils.CBC_DISPLAY.getCapBankValueAt(capBank, CBCDisplay.CB_DAILY_TOTAL_OP_COLUMN)%></a>
					</td>
				</tr>
				<% } %>
			</table>
		</div>
		<input type="hidden" id="lastUpdate" value="">
	</cti:titledContainer>
<script type="text/javascript"> 
	Event.observe(window, 'load', function() {  initFilter($('parent_fdr_td'), $('capBankTable'), 7); new CtiNonScrollTable('capBankTable','capBankHeaderTable'); });
</script>
    <div style = "display:none" id = "outerDiv">
        <cti:titledContainer title="Current Status">
            <div id="cmd_msg_div" />
        </cti:titledContainer>
    </div>
</cti:standardPage>