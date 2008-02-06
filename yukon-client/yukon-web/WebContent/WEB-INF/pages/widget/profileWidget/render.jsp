<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<script type="text/javascript"> 

	var progressUpdaters = new Array();
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
<div id="${widgetParameters.widgetId}_channelScanning"></div>
<script type="text/javascript"> 
    scanningUpdater = new Ajax.PeriodicalUpdater('${widgetParameters.widgetId}_channelScanning', '/spring/widget/profileWidget/refreshChannelScanningInfo?deviceId=${deviceId}', {method: 'post', frequency: 90});
</script>
<br/>

<%--PAST PROFILES--%>
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
			<tags:widgetActionUpdate hide="${!isReadable}" method="initiateLoadProfile" label="Start" labelBusy="Start" container="${widgetParameters.widgetId}_results"/>
		</td>
	</tr>
	
</table>
<br/>

<%--RESULTS--%>
<div id="${widgetParameters.widgetId}_results">
    <c:url var="ongoingProfilesUrl" value="/WEB-INF/pages/widget/profileWidget/ongoingProfiles.jsp" />
    <jsp:include page="${ongoingProfilesUrl}" />
</div>
