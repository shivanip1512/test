<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.deviceReconfig.setupSchedule.pageTitle"/>
<cti:msg var="schedulingOptionSectionTitle" key="yukon.web.modules.dr.deviceReconfig.setupSchedule.schedulingOptionSectionTitle"/>
<cti:msg var="reconfigurationStyleSectionTitle" key="yukon.web.modules.dr.deviceReconfig.setupSelection.reconfigurationStyleSectionTitle"/>

<cti:standardPage title="${pageTitle}" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Device Reconfiguration" />
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <cti:includeScript link="/JavaScript/deviceReconfig.js"/>
    
    <script type="text/javascript">

    	Event.observe (window, 'load', function() {
    			recalcBatch();
			}
    	);
		
    </script>
    
    <tags:simpleDialog id="deviceReconfigSharedPreviewDialog"/>
    
    <%-- MISC FORMS --%>
    <tags:simpleDialog id="deviceReconfigPreviewScheduleDialog"/>
    <form id="cancelForm" action="/spring/stars/operator/deviceReconfig/home" method="get"></form>
    
    
    
    
    
    
        <tags:boxContainer title="Setup Device Configuration Schedule" hideEnabled="false">
    
    	<div style="font-size:11px;">
    				

    
    <table cellpadding="2">
    
        
        <tr>
            <td valign="top" colspan="2" class="smallBoldLabel">
Device Count: 195 <img onclick="javascript:showSelectedDevices(this, 'selectedDevicesPopup_70', 'selectedDevicesPopup_70InnerDiv', '/spring/bulk/selectedDevicesTableForDeviceCollection?collectionType=idList&idList.ids=2324%2c2330', '/WebConfig/yukon/Icons/magnifier.gif', '/WebConfig/yukon/Icons/indicator_arrows.gif');" title="View names of selected devices." src="/WebConfig/yukon/Icons/magnifier.gif" onmouseover="javascript:this.src='/WebConfig/yukon/Icons/magnifier_zoom_in.gif'" onmouseout="javascript:this.src='/WebConfig/yukon/Icons/magnifier.gif'">
            </td>
        </tr>
        
        
        
        
        
        
        
        
    </table>
    <br>
    
    
    
    
    
    
    
    
    
    
    
    <%-- SCHEDULE FORM --%>
    <form id="setupScheduleForm" action="/spring/stars/operator/deviceReconfig/process" method="post">
    
    	<%--
    	<div style="font-size:14px;">
	    	<b>${inventoryIdsCount}</b> devices have been selected. 
	    	<br>
	    	Set the options below to determine how the re-configuration task should be scheduled.
	    	<br><br>
	    	<input type="hidden" id="inventoryIdsCount" name="inventoryIdsCount" value="${inventoryIdsCount}">
    	</div>
    	 --%>
    	 
    	 <input type="hidden" id="inventoryIdsCount" name="inventoryIdsCount" value="195">
    	
	    <tags:sectionContainer title="${reconfigurationStyleSectionTitle}">
	    
	    	<input type="radio" name="reconfigurationStyle" value="CURRENT_SETTINGS" id="reconfigurationStyleByCurrentSettingsRadio" <c:if test="${deviceReconfigOptions.reconfigurationStyle == 'CURRENT_SETTINGS'}">checked</c:if>>
	    	<b>Current Configuration</b>
	    	<img src="${helpImg}">
	    	<br><br>
	    
	    	<input type="radio" name="reconfigurationStyle" value="LOAD_GROUP" id="reconfigurationStyleByLoadGroupRadio" <c:if test="${deviceReconfigOptions.reconfigurationStyle == 'LOAD_GROUP'}">checked</c:if>>
	    	<b>Load Group Addressing</b>
	    	<input type="hidden" id="reconfigStyleByLoadGroupId" name="reconfigStyleByLoadGroupId" value="${deviceReconfigOptions.reconfigStyleByLoadGroupId}">
		   	<cti:paoPicker pickerId="reconfigStyleByLoadGroupIdPicker" paoIdField="reconfigStyleByLoadGroupId" asButton="true" finalTriggerAction="reconfigStyleByLoadGroupIdPickerComplete" constraint="com.cannontech.common.search.criteria.LMGroupCriteria">Select</cti:paoPicker>
	    	<img src="${helpImg}">
	    	
	    </tags:sectionContainer>
	    <br>
    	
    	<tags:sectionContainer title="${schedulingOptionSectionTitle}">
    	
    		<c:set var="nameValueGapHeight" value="10px"/>
	    	<tags:nameValueContainer>
	    	
	    		<%-- START DATE --%>
	    		<tags:nameValue name="Start Date">
	    			<tags:dateInputCalendar fieldId="startDate" fieldName="startDate" fieldValue=""/>
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    		
	    		<%-- START TIME --%>
	    		<tags:nameValue name="Start Time">
	    			
					<select id="startHour" name="startHour">
						<c:forEach var="hour" begin="1" end="12" step="1">
							<option value="${hour}" <c:if test="${backing.startHour == hour}">selected</c:if>>${hour}</option>
						</c:forEach>
					</select> :
					
					<select id="startMinute" name="startMinute">
						<c:forEach var="minute" begin="0" end="59" step="5">
							<option value="${minute}" <c:if test="${backing.startMinute == minute}">selected</c:if>>
								<c:if test="${minute < 10}">
									<c:set var="minute" value="0${minute}"/>
								</c:if>
								${minute}
							</option>
						</c:forEach>
					</select>
					
					<select id="startAmPm" name="startAmPm">
						<option value="AM" <c:if test="${backing.startAmPm == 'AM'}">selected</c:if>>AM</option>
						<option value="PM" <c:if test="${backing.startAmPm == 'PM'}">selected</c:if>>PM</option>
					</select>
	
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    		
	    		<%-- PAGE DELAY --%>
	    		<tags:nameValue name="Page Delay" nameColumnWidth="160px">
	    			<input type="text" id="pageDelay" name="pageDelay" value="2" size="4" maxlength="4"> Seconds
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    	
	    		<%-- SPLIT --%>
	    		<tags:nameValue name="Batches" nameColumnWidth="160px">
	    			 
    				<input type="text" id="batchCount" name="batchCount" value="2" size="4" maxlength="4" onkeyup="recalcBatch();"> 
    				<span class="subtleGray">
    					<span id="splitInfoSpan">
	    					Split ${inventoryIdsCount} devices into <span id="batchCountDisplay"></span> batches of <span id="perBatchCountDisplay"></span>.
	    				</span>
	    				<span id="splitErrorSpan" class="errorRed" style="display:none;">Invalid batch number.</span>
	    			</span>
	    			
	    		</tags:nameValue>
	    		<tags:nameValueGap gapHeight="${nameValueGapHeight}"/>
	    		
	    		<%-- BATCH DELAY --%>
	    		<tags:nameValue name="Batch Delay">
	    			<input type="text" id="batchDelay" name="batchDelay" value="60" size="4" maxlength="4"> Minutes
	    		</tags:nameValue>
	    	
	    	</tags:nameValueContainer>
    	
    	</tags:sectionContainer>
    
    	
	    <%-- BUTTONS --%>
    	<br>
    	<input type="button" value="Preview" onclick="previewSchedule();" class="formSubmit">
    	<tags:slowInput myFormId="setupScheduleForm" label="Submit" />
    
    </form>
    
    </tags:boxContainer>
    
</cti:standardPage>