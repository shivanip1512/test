<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib tagdir="/WEB-INF/tags/capcontrol" prefix="capTags"%>

<%@ page import="com.cannontech.spring.YukonSpringHook" %>
<%@ page import="com.cannontech.cbc.cache.CapControlCache" %>
<%@ page import="com.cannontech.cbc.cache.FilterCacheFactory" %>
<%@ page import="com.cannontech.core.dao.PaoDao" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonPAObject" %>
<%@ page import="com.cannontech.cbc.util.CBCUtils" %>

<%@page import="org.springframework.web.bind.ServletRequestUtils"%>
<c:url var="onelineCBCServlet" value="/capcontrol/oneline/OnelineCBCServlet"/>

<cti:standardPage title="Feeders" module="capcontrol">

<%@include file="cbc_inc.jspf"%>
	
<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="<%=request.getRequestURL().toString()%>"/>

<script type="text/javascript" language="JavaScript">
    Event.observe(window, 'load', checkPageExpire);

    // These two functions are neccessary since IE does not support css :hover
    function highLightRow(row) {
        row = $(row);
        row.addClassName('hover');
    }
    
    function unHighLightRow(row){
        row = $(row);
        row.removeClassName('hover');
    }

</script> 

<!-- necessary DIV element for the OverLIB popup library -->
<div id="overDiv" style="position:absolute; visibility:hidden; z-index:1000;"></div>

<%
    PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    AuthDao authDao = YukonSpringHook.getBean("authDao", AuthDao.class);
    PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
    CapControlCache capControlCache = YukonSpringHook.getBean("capControlCache", CapControlCache.class);
    FilterCacheFactory cacheFactory = YukonSpringHook.getBean("filterCacheFactory", FilterCacheFactory.class);
    
    LiteYukonUser user = ServletUtil.getYukonUser(request);
    CapControlCache filterCapControlCache = cacheFactory.createUserAccessFilteredCache(user);
    String currentPageURL = request.getRequestURL().toString();
	
    Integer subStationId = ServletRequestUtils.getIntParameter(request, "id", ccSession.getLastSubID());
    if (subStationId == null || subStationId.intValue() <= 0) {
        String location = ServletUtil.createSafeUrl(request, "/capcontrol/subareas.jsp");
        response.sendRedirect(location);
        return;
    }
    
    SubStation substation = filterCapControlCache.getSubstation(subStationId);
    if (substation == null) {
        String location = ServletUtil.createSafeUrl(request, "/capcontrol/invalidAccessErrorPage.jsp");
        response.sendRedirect(location);
        return;
    }
    
	String popupEvent = authDao.getRolePropertyValue(user, WebClientRole.POPUP_APPEAR_STYLE);
    boolean hideOneLine = Boolean.valueOf(authDao.getRolePropertyValue(user, CBCSettingsRole.HIDE_ONELINE));
	
    boolean showFlip = Boolean.valueOf(authDao.getRolePropertyValue(user, CBCSettingsRole.SHOW_FLIP_COMMAND)).booleanValue();
    if (popupEvent == null) popupEvent = "onmouseover";
    
    Integer areaId = substation.getParentID();
	List<SubBus> subBuses = capControlCache.getSubBusesBySubStation(substation);
    Collections.sort(subBuses, CBCUtils.SUB_DISPLAY_COMPARATOR);
	List<Feeder> feeders = capControlCache.getFeedersBySubStation(substation);
	List<CapBankDevice> capBanks = capControlCache.getCapBanksBySubStation(substation);
	
	String lastStr = (String) request.getSession(false).getAttribute("lastAccessed");
	int lastAccessed = (lastStr == null) ? -1:Integer.parseInt(lastStr);
	
	boolean hasControl = CBCWebUtils.hasControlRights(session);
	boolean special = capControlCache.isSpecialCBCArea(areaId);
	
	
%>

<c:set var="hasControl" value="<%=CBCWebUtils.hasControlRights(session)%>"/>
<c:set var="areaId" value="<%=areaId%>"/>
<c:set var="subStationId" value="<%=subStationId%>"/>

<cti:standardMenu/>

<cti:breadCrumbs>
<% if (special){ %>
  	<cti:crumbLink url="subareas.jsp" title="Home" />
  	<cti:crumbLink url="specialSubAreas.jsp" title="Special Substation Areas" />
<% } else{ %>
	<cti:crumbLink url="subareas.jsp" title="Home" />
	<cti:crumbLink url="subareas.jsp" title="Substation Areas" />
<% } %>
    <cti:crumbLink url="substations.jsp?id=${areaId}" title="Substations" />
    <cti:crumbLink url="feeders.jsp?id=${subStationId}" title="Feeders" />
</cti:breadCrumbs>

<script type="text/javascript">
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
    
    // gathers ids of selected subbuses and feeders, appendeds to url as target param
    // triggers call to greybox containing point chart(s)
    function loadPointChartGreyBox(title, url) {
        
        var elemSubs = document.getElementsByName('cti_chkbxSubBuses');
        var elemFdrs = document.getElementsByName('cti_chkbxFdrs');
        var validElems = new Array();
        getValidChecks( elemSubs, validElems );
        getValidChecks( elemFdrs, validElems );
        
        var targets = new Array()
        for (var i = 0; i < validElems.length; i++) {
            targets.push(validElems[i].getAttribute('value'))
        }
        
        url += '&targets=' + targets.join(',');
        
        GB_showFullScreen(title, url, null);
        return void(0);
    }

	//returned when a cap bank menu is triggered to appear
	function popupWithHiLite (html, width, height, offsetx, offsety, rowID, color) {
		
		return overlib (html, STICKY, WIDTH, width, HEIGHT, height, OFFSETX, offsetx, OFFSETY, offsety, MOUSEOFF, FULLHTML, ABOVE);
	}
	
   	function onGreyBoxClose () {
   	   window.parent.location.replace('feeders.jsp');
   	}

 </script>
<%
String css = "tableCell";
%>
<input type="hidden" id="lastAccessedID" value="<%= lastAccessed %>">

	<cti:titledContainer title="Substation">
		<table id="substationTable" width="100%" cellspacing="0" cellpadding="0">
			<tr class="columnHeader lAlign">
				<th>Substation Name</th>
                <th width="2%"></th>
				<th>State</th>
			</tr>
            <% if (substation != null) { %>
                <c:set var="thisSubStationId" value="<%=substation.getCcId()%>"/>
                <input type="hidden" id="paoId_${thisSubStationId}" value="${thisSubStationId}"></input>
                
                <tr class="altTableCell" id="tr_substation_${thisSubStationId}">
                    <td id="anc_${thisSubStationId}">
		                <input type="checkbox" name="cti_chkbxSubStation" value="${thisSubStationId}"></input>
		                <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
	                        <a href="/editor/cbcBase.jsf?type=2&itemid=<%=substation.getCcId()%>&ignoreBookmark=true">
	                            <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
		                    </a>
		                    <a href="/editor/deleteBasePAO.jsf?value=<%=substation.getCcId()%>">
		                        <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
		                    </a>
	                    </cti:checkProperty>
                        <%=substation.getCcName()%>
                        <% if (substation.getSpecialAreaEnabled()) {
                            String spcAreaName = paoDao.getYukonPAOName(substation.getSpecialAreaId());
                        %>
                        <font color="red">Special Area Enabled: <%=spcAreaName%></font>
                        <% } %>
                    </td>
                    
                    <td>
                        <capTags:warningImg paoId="${thisSubStationId}" type="SUBSTATION"/>
                    </td>
                    
                    <td>
                        <a id="substation_state_${thisSubStationId}"
                            <% if (hasControl) { %>
                                href="javascript:void(0);"
                                <%=popupEvent%>="getSubstationMenu('${thisSubStationId}');">
                            <% } %>
                            <cti:capControlValue paoId="${thisSubStationId}" type="SUBSTATION" format="STATE" />
                        </a>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('substation_state_${thisSubStationId}')" initialize="true" value="SUBSTATION/${thisSubStationId}/STATE"/>
                    </td>
                </tr>
			<% } %>
		</table>
	</cti:titledContainer>
	
	<br>

	<cti:titledContainer title="Substation Bus">

		<table id="subTable" width="100%" cellspacing="0" cellpadding="0">
			<tr class="columnHeader lAlign">
				<td><input type="checkbox" name="chkAllSubBusesBx" onclick="checkAll(this, 'cti_chkbxSubBuses');" /></td>
				<td id="subSelect">
				    <select id='subBusFilter' onchange='applySubBusFilter(this);'>
				    <option>All SubBuses</option>
				    <% for (SubBus sub: subBuses) {%>
					   <option><%=sub.getCcName()%></option>
				    <% } %>
				    </select>
				</td>
				<td width="2%"></td>
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
            <c:set var="thisSubBusId" value="<%=subBus.getCcId()%>"/>
            <input type="hidden" id="paoId_${thisSubBusId}" value="${thisSubBusId}"></input>
            
			<tr class="<%=css%>" id="tr_sub_${thisSubBusId}">
				
                <td id="anc_${thisSubBusId}"><input type="checkbox" name="cti_chkbxSubBuses" value="${thisSubBusId}"/>
					<input type="image" id="showSnap${thisSubBusId}" src="images/nav-plus.gif" onclick="showRowElems( 'subSnapShot${thisSubBusId}', 'showSnap${thisSubBusId}'); return false;"/>
				</td>
                
				<td id="subName">
				    <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
					    <a href="/editor/cbcBase.jsf?type=2&itemid=<%=subBus.getCcId()%>&ignoreBookmark=true">
	                        <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
	                    </a>
	                    
	                    <a href="/editor/deleteBasePAO.jsf?value=<%=subBus.getCcId()%>">
	                        <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
	                    </a>
                    </cti:checkProperty>
                    <a
                        <% if (!hideOneLine) { %>
				            href="${onelineCBCServlet}?id=${thisSubBusId}&redirectURL=<%=currentPageURL %>" title="Click to view One-Line"
                        <% } %> ><%=subBus.getCcName()%></a>
                    <capTags:verificationImg paoId="${thisSubBusId}" type="SUBBUS"/>
				</td>
                
				<td>
                    <capTags:warningImg paoId="${thisSubBusId}" type="SUBBUS"/>      
                </td>
				
				<td>
					<a id="subbus_state_${thisSubBusId}"
    				    <% if (hasControl) { %>
						  href="javascript:void(0);"
						  <%=popupEvent%>="getSubBusMenu('${thisSubBusId}');" 
				        <% } %> >
                        <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="STATE" />
					</a>
                    <cti:dataUpdaterCallback function="updateStateColorGenerator('subbus_state_${thisSubBusId}')" initialize="true" value="SUBBUS/${thisSubBusId}/STATE"/>
				</td>
                
				<td>
                    <c:set var="isPowerFactorControlled" value="<%=CBCUtils.isPowerFactorControlled(subBus.getControlUnits())%>"/>
                    <a onmouseover="showDynamicPopup($('subPFPopup_${thisSubBusId}_${isPowerFactorControlled}'))"
						onmouseout="nd();"
					   	id="${thisSubBusId}">
						<cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET"/>
					</a>
					<div class="ccPFPopup" id="subPFPopup_${thisSubBusId}_${isPowerFactorControlled}" style="display: none;" >
                        <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="TARGET_MESSAGE"/>     
					</div>
				</td>
				<td>
                    <a onmouseover="showDynamicPopup($('subVarLoadPopup_${thisSubBusId}'));" 
				       onmouseout="nd();"
					   id="${thisSubBusId}">
                        <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD"/>   
                    </a>
				    <div class="ccVarLoadPopup" id="subVarLoadPopup_${thisSubBusId}" style="display: none;" > 
                        <cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KVAR_LOAD_MESSAGE"/>
				    </div>
				</td>
				<td>
                    <a id="dateTime_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="DATE_TIME"/></a>
				</td>
				<td>
                    <a id="pFactor_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="PFACTOR"/></a>
				</td>
				<td>
                    <a id="kwVolts_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="KW_VOLTS"/></a>
				</td>
				<td>
                    <a id="dailyMaxOps_${thisSubBusId}"><cti:capControlValue paoId="${thisSubBusId}" type="SUBBUS" format="DAILY_MAX_OPS"/></a>
				</td>
			</tr>
			<tr class="tableCellSnapShot">
				<td colspan="11">
				<table id="subSnapShot${thisSubBusId}">
				
					<%
					if (subBus != null) {
					String areaName = CBCUtils.getAreaNameForSubStationBusIdFromCache(subBus.getCcId().intValue());	
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
			   	        if (varPoint != 0) vrPoint = pointDao.getPointName(varPoint);
			     	%>
			        <td><b><font  class="lIndent">Var Point: <%=vrPoint%></font></b></td>
					</tr>
				    <tr class="tableCellSnapShot" style="display: none;">
			      	<%
			    	        String wPoint = "(none)";
			    	        if (wattPoint != 0) wPoint = pointDao.getPointName(wattPoint);
			      	%>
			        <td><b><font  class="lIndent">Watt Point: <%=wPoint%></font></b></td>
					</tr>
				    <tr class="tableCellSnapShot" style="display: none;">
			        <%
			 		        String vPoint = "(none)";
			 		        if (voltPoint != 0) vPoint = pointDao.getPointName(voltPoint);
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

		<table id="fdrTable" width="100%" cellspacing="0" cellpadding="0">
        	<tr class="columnHeader lAlign">
         		<td><input type="checkbox" name="chkAllFdrsBx" onclick="checkAll(this, 'cti_chkbxFdrs');" />
         			<select id='feederFilter' onchange='applyFeederSelectFilter(this);'>
						<option>All Feeders</option>
						<% for( Feeder feeder: feeders) {%>
						<option><%=feeder.getCcName()%></option>
						<%}%>
					</select>
				</td>
				<td></td>
         		<td>State</td>
         		<td>Target</td>
         		<td>kVAR Load / Est.</td>
         		<td>Date/Time</td>
         		<td>PFactor / Est.</td>
         		<td>kW / Volts</td>
         		<td>Daily/Max Ops</td>
         	</tr>
<%
css = "tableCell";
for (int i = 0; i < feeders.size(); i++ ) {
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	Feeder feeder = feeders.get(i);
%>
                <c:set var="thisFeederId" value="<%=feeder.getCcId()%>"/>
                <input type="hidden" id="paoId_${thisFeederId}" value="${thisFeederId}"></input>
                
				<tr class="<%=css%>">
					<td>
						<input type="checkbox" name="cti_chkbxFdrs" value="${thisFeederId}"/>
						<cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
							<a href="/editor/cbcBase.jsf?type=2&itemid=<%=feeder.getCcId()%>&ignoreBookmark=true">
	                            <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
	                        </a>
	                        
	                        <a href="/editor/deleteBasePAO.jsf?value=<%=feeder.getCcId()%>">
	                            <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
	                        </a>
                        </cti:checkProperty>
						<span><%=feeder.getCcName()%></span>
					</td>
					
					<td>        
                        <capTags:warningImg paoId="${thisFeederId}" type="FEEDER"/>
                    </td>
					
					<td>
                        <a id="feeder_state_${thisFeederId}"    
                            <% if (hasControl) { %>
                                href="javascript:void(0);"
                                <%=popupEvent%>="getFeederMenu('${thisFeederId}');" 
                            <% } %> >
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="STATE"/>    
						</a>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('feeder_state_${thisFeederId}')" initialize="true" value="FEEDER/${thisFeederId}/STATE"/>
					</td>
                    
					<td>
                        <c:set var="isPowerFactorControlled" value="<%=CBCUtils.isPowerFactorControlled(feeder.getControlUnits())%>"/>
                        <a onmouseover="showDynamicPopup($('feederPFPopup_${thisFeederId}_${isPowerFactorControlled}'));"
						   onmouseout="nd();"
						   id="${isPowerFactorControlled}">
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET"/>
                        </a>
                        <div class="ccPFPopup" id="feederPFPopup_${thisFeederId}_${isPowerFactorControlled}" style="display: none;">
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="TARGET_MESSAGE"/>    
					   </div>
					</td>
                    
					<td>
                        <a onmouseover="showDynamicPopup($('feederVarLoadPopup_${thisFeederId}'));" 
					       onmouseout="nd();"
						  id="${thisFeederId}">
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD"/>
                        </a>
                        <div class="ccVarLoadPopup" id="feederVarLoadPopup_${thisFeederId}" style="display: none;">
                            <cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KVAR_LOAD_MESSAGE"/> 
                        </div>
					</td>
                    
					<td>
                        <a id="dateTime_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="DATE_TIME"/></a>
					</td>
					<td>
                        <a id="pFactor_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="PFACTOR"/></a>
					</td>
					<td>
                        <a id="kwVolts_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="KW_VOLTS"/></a>
					</td>
					<td>
                        <a id="dailyMaxOps_${thisFeederId}"><cti:capControlValue paoId="${thisFeederId}" type="FEEDER" format="DAILY_MAX_OPS"/></a>
					</td>
					<td id="hiddenSubName" style="display: none;"><%=capControlCache.getSubBusNameForFeeder(feeder)%></td>
				</tr>
<% } %>
		</table>

    </cti:titledContainer>

	<br>

	<cti:titledContainer title="Capacitor Banks" id="last_titled_container">
		<!--  <table id="capBankHeaderTable" width="100%" border="0" cellspacing="0" cellpadding="0">-->
        <div id="capBankDiv">
		<table id="capBankTable" width="100%" cellspacing="0" cellpadding="0" >
            <tr class="columnHeader lAlign">
                <td><input type="checkbox" name="chkAllBanksBx" onclick="checkAll(this, 'cti_chkbxBanks');" /> </td>
                <td>CBC Name</td>
                <td>CB Name (Order) <img class="rAlign popupImg" src="images\question.gif" onmouseover="statusMsg(this, 'Order is the order the CapBank will control in.<br>Commands that can be sent to a field device are initiated from this column');" /></td>                    
                <td width="2%"></td>    
                <td>State <img class="rAlign popupImg" src="images\question.gif" onmouseover="statusMsg(this, 'System Commands, those commands that do NOT send out a message to a field device, can be initiated from this column');"/> <span id="cb_state_td_hdr2" style="display:none" >[Op Count Value]</span> </td>
                
                <td>Date/Time</td>
                <td>Bank Size</td>
                <td>Parent Feeder</td>
                <td>Daily/Max/Total Op</td>
			</tr>              
<%
css = "tableCell";
for( int i = 0; i < capBanks.size(); i++ ) {
	CapBankDevice capBank = capBanks.get(i);
	css = ("tableCell".equals(css) ? "altTableCell" : "tableCell");
	int deviceID = capBank.getControlDeviceID().intValue();
    LiteYukonPAObject obj = paoDao.getLiteYukonPAO(deviceID);
    String rowColor = ((i % 2) == 0) ? "#eeeeee" : "white";
%>
            <c:set var="thisCapBankId" value="<%=capBank.getCcId()%>"/>
            <c:set var="rowColor" value="<%=rowColor%>"/>
            <input type="hidden" id="paoId_${thisCapBankId}" value="${thisCapBankId}"></input>            

			<tr class="<%=css%>" id="tr_cap_${thisCapBankId}" onmouseover="highLightRow(this)" onmouseout="unHighLightRow(this)">
				
                <td>
                    <input type="checkbox" name="cti_chkbxBanks" value="${thisCapBankId}"/>
                </td>
                
				<td>
				    <%
				    String name = "---";
				    Integer cdId = capBank.getControlDeviceID();
				    if (cdId.intValue() != 0) {
				        name = paoDao.getYukonPAOName(cdId);%>
				        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
					        <a href="/editor/cbcBase.jsf?type=2&itemid=<%=capBank.getControlDeviceID()%>">
	                            <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
	                        </a>
                        </cti:checkProperty>
                    <%
				    }
				    %> 
				    <%=name%>
				    <% if (CBCUtils.isTwoWay(obj)) { %>                 
                        <a href="#" onclick="return GB_show('Device <%=obj.getPaoName()%>', '/spring/capcontrol/oneline/popupmenu?menu=pointTimestamp&cbcID=<%=obj.getLiteID()%>', 500, 600)" >
                            <img class="rAlign popupImg" src="images\magnifier.gif" onmouseover="statusMsg(this, 'Click here to see the timestamp information for the cap bank controller device.');" />
                       </a>
                    <% } %>
				</td>
				
                <td>
				    <% if (hasControl) { %>
                        <!--2-way device designator-->
                        <input id="2_way_${thisCapBankId}" type="hidden" value="<%=CBCUtils.isTwoWay(obj)%>"/>
                        <input id="showFlip_${thisCapBankId}" type="hidden" value="<%=showFlip%>"/>
                        <input id="is701x_${thisCapBankId}" type="hidden" value="<%=CBCUtils.is701xDevice(obj)%>"/>
                        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
	                        <a href="/editor/cbcBase.jsf?type=2&itemid=<%=capBank.getCcId()%>&ignoreBookmark=true">
	                            <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
	                        </a>
	                        
	                        <a href="/editor/deleteBasePAO.jsf?value=<%=capBank.getCcId()%>">
	                            <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
	                        </a>
                        </cti:checkProperty>
                        <a href="javascript:void(0);"
                           <%=popupEvent%> ="getCapBankMenu('${thisCapBankId}');" 
                           >
                            <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_NAME"/>
                        </a>
					<% } else { %>
                        <cti:checkProperty property="CBCSettingsRole.CBC_DATABASE_EDIT">
	                        <a href="/editor/cbcBase.jsf?type=2&itemid=<%=capBank.getCcId()%>&ignoreBookmark=true">
	                            <img class="rAlign editImg" src="/editor/images/edit_item.gif"/>
	                        </a>
	                        
	                        <a href="/editor/deleteBasePAO.jsf?value=<%=capBank.getCcId()%>">
	                            <img class="rAlign editImg" src="/editor/images/delete_item.gif"/>
	                        </a>
                        </cti:checkProperty>
                        <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_NAME"/>
					<% } %>
					   <a href="#" onclick="return GB_show('<center> Cap Bank Additional Information </center>', '/spring/capcontrol/capAddInfo?paoID=${thisCapBankId}', 500, 600)" >
					       <img class="rAlign popupImg" src="images\magnifier.gif" onmouseover="statusMsg(this, 'Click to see additional information for the cap bank.');" />
					   </a>
				</td>

                <td>
                    <capTags:warningImg paoId="${thisCapBankId}" type="CAPBANK"/>
                </td>

				<td>
                    
                    <cti:pointStatusColor pointId="<%=capBank.getStatusPointID()%>">
                        <a id="capbank_status_${thisCapBankId}"
                            <c:if test="${hasControl}">
                                href="javascript:void(0);"
                                onclick ="getCapBankSystemMenu('${thisCapBankId}');"
                            </c:if> 
                        >
					       <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_STATUS"/>
                        </a>
                    </cti:pointStatusColor>
                    
				    <span id="opcount_span${thisCapBankId}" style="display: none;" >Op Count:
					   <input type="text" id="opcount_${thisCapBankId}" maxlength="5" size="5"/>
					   <a href="javascript:void(0);" onclick="executeCapBankResetOpCount(${thisCapBankId});" >Reset</a>
				    </span>
					<div id="capBankStatusPopup_${thisCapBankId}" style="display: none;">
                        <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_STATUS_MESSAGE"/>     
					</div>
				</td>
                
				<td>
                    <a id="dateTime_${thisCapBankId}"
				       onmouseover = "showDynamicPopupAbove($('capBankStatusPopup_${thisCapBankId}'))"
				       onmouseout="nd()">
                        <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="DATE_TIME"/> 
                    </a>
				</td>
                
				<td>
                    <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_SIZE"/>
                </td>
                
                <td>
                    <a href="javascript:void(0);"
                    <% if (capBank.isBankMoved()) { %>
	                    class="warning" 
	                    <%=popupEvent%>="getCapBankTempMoveBack('${thisCapBankId}');" 
                    <% } else { %>
                        onmouseover="statusMsg(this, 'Click here to temporarily move this CapBank from it\'s current parent feeder');"
                        onmouseout="nd();"
                        onclick="return GB_show('CapBank Temp Move Target (Pick feeder by clicking on name)','tempmove.jsp?bankid=<%=capBank.getCcId()%>', 500, 710, onGreyBoxClose);"
                    <% } %>
                    	>
                            <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="CB_PARENT"/>
                    	</a>                    
                    </td>
					<td>
						<a id="${thisCapBankId}">
						  <cti:capControlValue paoId="${thisCapBankId}" type="CAPBANK" format="DAILY_MAX_OPS"/>
                        </a>
					</td>
				</tr>
				<% } %>
			</table>
		</div>
		<input type="hidden" id="lastUpdate" value="">
        
	</cti:titledContainer>
    
<capTags:commandMsgDiv/>

    <ct:disableUpdaterHighlights/>
</cti:standardPage>