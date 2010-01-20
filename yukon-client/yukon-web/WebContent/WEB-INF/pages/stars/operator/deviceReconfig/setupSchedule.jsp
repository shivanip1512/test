<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.deviceReconfig.setupSchedule.pageTitle"/>
<cti:msg var="schedulingOptionSectionTitle" key="yukon.web.modules.dr.deviceReconfig.setupSchedule.schedulingOptionSectionTitle"/>
<cti:msg var="reconfigurationStyleSectionTitle" key="yukon.web.modules.dr.deviceReconfig.setupSelection.reconfigurationStyleSectionTitle"/>

<cti:standardPage title="Device Reconfiguration" module="dr">

	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Inventory Operations" />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/setupSelection" title="Inventory Selection" />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/chooseOperation">Inventory Actions</cti:crumbLink>
	    <cti:crumbLink>Device Reconfiguration</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>Device Reconfiguration</h2>
    <br>
    
    <cti:includeScript link="/JavaScript/deviceReconfig.js"/>
    
    <tags:simpleDialog id="deviceReconfigSharedPreviewDialog"/>
    

    <%-- ERRORS --%>
    <c:if test="${not empty errors}">
    	<tags:errorMessages/>
    	<br>
    </c:if>
    
    <%-- MISC FORMS --%>
    <tags:simpleDialog id="deviceReconfigPreviewScheduleDialog"/>
    <form id="cancelForm" action="/spring/stars/operator/deviceReconfig/home" method="get"></form>
    
    
    
    
	<tags:boxContainer title="Setup Device Reconfiguration" hideEnabled="false">
    
    	<div style="font-size:11px;">
		    <table cellpadding="2">
		        <tr>
		            <td valign="top" colspan="2" class="smallBoldLabel">
						Device Count: 1650 <img onclick="javascript:showSelectedDevices(this, 'selectedDevicesPopup_70', 'selectedDevicesPopup_70InnerDiv', '/spring/bulk/selectedDevicesTableForDeviceCollection?collectionType=idList&idList.ids=2324%2c2330', '/WebConfig/yukon/Icons/magnifier.gif', '/WebConfig/yukon/Icons/indicator_arrows.gif');" title="View names of selected devices." src="/WebConfig/yukon/Icons/magnifier.gif" onmouseover="javascript:this.src='/WebConfig/yukon/Icons/magnifier_zoom_in.gif'" onmouseout="javascript:this.src='/WebConfig/yukon/Icons/magnifier.gif'">
		            </td>
		        </tr>
		        
		    </table>
	    </div>
	    <br>
    
    
    
    <%-- SCHEDULE FORM --%>
    <form id="setupScheduleForm" action="/spring/stars/operator/deviceReconfig/process" method="post">
    
    	 
    	 <input type="hidden" id="inventoryIdsCount" name="inventoryIdsCount" value="195">
    	
	    <tags:sectionContainer title="${reconfigurationStyleSectionTitle}">
	    
	    	<input type="radio" name="reconfigurationStyle" value="CURRENT_SETTINGS" id="reconfigurationStyleByCurrentSettingsRadio" <c:if test="${deviceReconfigOptions.reconfigurationStyle == 'CURRENT_SETTINGS'}">checked</c:if>>
	    	<b>Current Configuration</b>
	    	<img src="${helpImg}">
	    	<br><br>
	    
	    	<input type="radio" name="reconfigurationStyle" value="LOAD_GROUP" id="reconfigurationStyleByLoadGroupRadio" <c:if test="${deviceReconfigOptions.reconfigurationStyle == 'LOAD_GROUP'}">checked</c:if>>
	    	<b>Load Group Addressing</b>
	    	&nbsp;
	    	<span class="subtleGray"><i>Select Load Group</i></span>
   			<a href="javascript:void(0);" title=""  id="chooseGroupIcon_${uniqueId}" style="text-decoration:none;">	
				<img src="/WebConfig/yukon/Icons/database_add.gif">
			</a>
	    	
	    </tags:sectionContainer>
	    <br><br>
    	
    	<tags:sectionContainer title="${schedulingOptionSectionTitle}">
    	
    		<c:set var="nameValueGapHeight" value="10px"/>
	    	<tags:nameValueContainer>
	    	
	    		<%-- NAME --%>
	    		<tags:nameValue name="Name">
	    			<input type="text" value="Golay Group 1 ">
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    	
	    		<%-- RUN SCHEDULE --%>
	    		<tags:nameValue name="Run Schedule">
	    			<tags:cronExpressionData state="${cronState}" id="runScheduleCron"></tags:cronExpressionData>
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    		
	    		<%-- START TIME --%>
	    		<tags:nameValue name="Duration">
	    			
					<select id="startHour" name="startHour">
						<c:forEach var="hour" begin="1" end="12" step="1">
							<option value="${hour}" <c:if test="${6 == hour}">selected</c:if>>${hour}</option>
						</c:forEach>
					</select>
					Hours
					
					<select id="startMinute" name="startMinute">
						<c:forEach var="minute" begin="0" end="50" step="5">
							<option value="${minute}" <c:if test="${backing.startMinute == minute}">selected</c:if>>
								${minute}
							</option>
						</c:forEach>
					</select>
					Minutes
						
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    		
	    		<%-- PAGE DELAY --%>
	    		<tags:nameValue name="Page Delay" nameColumnWidth="160px">
	    			<input type="text" id="pageDelay" name="pageDelay" value="2" size="4" maxlength="4"> Seconds
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    		
	    		<%-- ESTIMATED TIME --%>
	    		<tags:nameValue name="Estimated Time">
	    			<span class="subtleGray">
	    				9.2 runs @ 6 hours each.
	    				<br>
	    				180 devices per run @ 2 second page delay.
	    			</span>
	    		</tags:nameValue>
	    	
	    	</tags:nameValueContainer>
    	
    	</tags:sectionContainer>
    
    	
	    <%-- BUTTONS --%>
    	<br>
    	<tags:slowInput myFormId="setupScheduleForm" label="Submit" width="80px"/>
    
    </form>
    
    </tags:boxContainer>
    
</cti:standardPage>