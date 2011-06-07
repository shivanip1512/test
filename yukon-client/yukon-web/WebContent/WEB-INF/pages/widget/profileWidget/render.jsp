<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<script type="text/javascript"> 
    var scanningUpdater = null;
    
    function toggleChanPopup(popupDivName) {
        
        $(popupDivName).toggle();
        
        // stop the ajax so it doesn't reload the popup as we're using it
        if ($(popupDivName).visible()) {
            scanningUpdater.stop();
        }
        else {
            scanningUpdater.start();
        }
    }
    
    function doToggleScanning(channelNum, newToggleVal) {
        $('channelNum').value = channelNum;
        $('newToggleVal').value = newToggleVal;
        ${widgetParameters.jsWidget}.doDirectActionRefresh("toggleProfiling");
    }
</script>

<%-- CHANNEL SCANNING --%>
<c:if test="${not empty toggleErrorMsg}">
    <div style="font-weight:bold;color:#BB0000">${toggleErrorMsg}</div>
    <br>
</c:if>

<c:set var="channelScanDiv" value="${widgetParameters.widgetId}_channelScanning"/>
<div id="${channelScanDiv}"></div>
<script>
    var refreshCmd = 'refreshChannelScanningInfo';
    var refreshParams = {'deviceId':${deviceId}};
    var refreshPeriod = 90;
    scanningUpdater = ${widgetParameters.jsWidget}.doPeriodicRefresh(refreshCmd,
                                                                     refreshParams, refreshPeriod,
                                                                     '${channelScanDiv}');
</script>

<%--PAST PROFILES, don't display if the device does not support --%>
<cti:checkRole role="operator.MeteringRole.ROLEID">
<cti:checkProperty property="operator.MeteringRole.PROFILE_COLLECTION">
    <br/>
	<table class="compactResultsTable">
		<tr>
			<th colspan="3" align="left">Request Past Profile:</th>
		</tr>
	
		<tr>
			<td class="label">Channel:</td>
			<td colspan="2">
				<select name="channel" style="height:20px;">
					<c:forEach var="channel" items="${availableChannels}">
		   				<option value="${channel.channelNumber}">${channel.channelDescription}</option>
		   			</c:forEach>
		   		</select>
		   	</td>
		</tr>
		
		<tr>
			<td class="label">Start Date:</td>
			<td colspan="2">
				<tags:dateInputCalendar fieldName="startDateStr" fieldValue="${startDateStr}"></tags:dateInputCalendar>
			</td>
		</tr>
		
		<tr>
			<td class="label">Stop Date:</td>
			<td colspan="2">
				<tags:dateInputCalendar fieldName="stopDateStr" fieldValue="${stopDateStr}"></tags:dateInputCalendar>
			</td>
		</tr>
		
		<tr>
			<td class="label">Email:</td>
			<td class="last">
				<input id="email" name="email" type="text" value="${email}" size="25" style="height:16px;">
			</td>
	  	    <td class="last" align="right">
	           <tags:widgetActionRefresh method="initiateLoadProfile" label="Start" labelBusy="Start"/>
            </td>
		</tr>
	</table>

    <c:if test="${not empty errorMsgRequest}">
    <div class="errorMessage" style="none">Error Retrieving Past Profile:</div>
        <tags:hideReveal title="${errorMsgRequest}" showInitially="false"/>
    </c:if>
    <br>
</cti:checkProperty>
</cti:checkRole>

<%-- DAILY USAGE REPORT --%>
<form id="reportForm" action="/spring/widget/profileWidget/viewDailyUsageReport">

<input type="hidden" name="deviceId" value="${deviceId}">

<table class="compactResultsTable">
	<tr>
		<th colspan="5" align="left">Daily Usage Report:</th>
	</tr>

    <tr>
        <td class="label">Start Date:</td>
        <td>
            <tags:dateInputCalendar fieldName="reportStartDateStr" fieldValue="${startDateStr}"></tags:dateInputCalendar>
        </td>
        <td class="label">Stop Date:</td>
        <td>
            <tags:dateInputCalendar fieldName="reportStopDateStr" fieldValue="${stopDateStr}"></tags:dateInputCalendar>
        </td>
        <td class="last" align="right">
           <tags:slowInput myFormId="reportForm" labelBusy="View Report" label="View Report" />
        </td>
    </tr>
	
</table>
</form>
