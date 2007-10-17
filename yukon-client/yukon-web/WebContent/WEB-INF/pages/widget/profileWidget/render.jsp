<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<script type="text/javascript"> 
	
	function toggleWhatsThis(){
		$('whatsThisText').toggle();
	}
	
	var progressUpdaters = new Array();

</script>
<%--CHANNLES PROFILING--%>
<br/>
<span class="compactResultTableDescription">Start/Stop Profiling:</span>
<br/>
<div style="height:5px;"></div>

<table class="compactResultsTable">

	<tr>
	  <th>Channel (Type)</th>
	  <th>Interval</th>
	  <th>Scan</th>
	  <th>Action</th>
	</tr>
	
	<tr>
		<td>Channel 1 (Load)</td>
		<td>${chan1Interval}</td>
		<c:choose>
			<c:when test="${chan1CollectionOn}">
				<td style="font-weight:bold;color:#339900">On</td>
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Stop" labelBusy="Stop" toggleChannel1ProfilingOn="false" /></td>
			</c:when>
			<c:otherwise>
				<td style="font-weight:bold;color:#BB0000">Off</td>
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Start" labelBusy="Start" toggleChannel1ProfilingOn="true" /></td>
			</c:otherwise>
		</c:choose>
	</tr>
	
	<tr class="last">
		<td>Channel 4 (Voltage)</td>
		<td>${chan4Interval}</td>
		<c:choose>
			<c:when test="${chan4CollectionOn}">
				<td style="font-weight:bold;color:#339900">On</td>
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Stop" labelBusy="Stop" toggleChannel4ProfilingOn="false" /></td>
			</c:when>
			<c:otherwise>
				<td style="font-weight:bold;color:#BB0000">Off</td>
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Start" labelBusy="Start" toggleChannel4ProfilingOn="true" /></td>
			</c:otherwise>
		</c:choose>
	</tr>
	
</table>
	
	
<br/>
	
<%--PAST PROFILES--%>
<span class="compactResultTableDescription">Request Past Profile:</span> <span class="compactResultTableDescription" style="color:#CC0000;">${initiateMessage}</span>
<br/>
<div style="height:5px;"></div>


<%--<tags:nameValueContainer altRowOn="true">--%>
<%--	<tags:nameValue name="Channel">--%>
<%--		<select name="channel" style="height:20px;">--%>
<%--				<c:forEach var="channel" items="${availableChannels}">--%>
<%--	   				<option value="${channel.channelNumber}">${channel.channelDescription}</option>--%>
<%--	   			</c:forEach>--%>
<%--	   		</select>--%>
<%--	</tags:nameValue>--%>
<%--	--%>
<%--	<tags:nameValue name="Start Date">--%>
<%--		<tags:dateInputCalendar fieldName="startDateStr" fieldValue="${startDateStr}"></tags:dateInputCalendar>--%>
<%--	</tags:nameValue>--%>
<%--	--%>
<%--	<tags:nameValue name="Stop Date">--%>
<%--		<tags:dateInputCalendar fieldName="stopDateStr" fieldValue="${stopDateStr}"></tags:dateInputCalendar>--%>
<%--	</tags:nameValue>--%>
<%--	--%>
<%--	<tags:nameValue name="Email">--%>
<%--		<input id="email" name="email" type="text" value="${email}" size="25" style="height:16px;">--%>
<%--		<tags:widgetActionRefresh method="initiateLoadProfile" label="Start" labelBusy="Start"/>--%>
<%--	</tags:nameValue>--%>
<%--	--%>
<%--</tags:nameValueContainer>--%>

<table class="compactResultsTableLeft">
	<tr>
		<td class="label_first">Channel:</td>
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
		<td class="label_last">Email:</td>
		<td class="last">
			<input id="email" name="email" type="text" value="${email}" size="25" style="height:16px;">
		</td>
		<td class="last" align="right">
			<tags:widgetActionRefresh method="initiateLoadProfile" label="Start" labelBusy="Start"/>
		</td>
	</tr>
	
</table>
<br/>



<%--ONGOING PROFILES--%>
<span class="compactResultTableDescription">Pending Requests:</span>


<c:choose>

<c:when test="${empty pendingRequests}">
	<span class="compactResultTableDescription" style="font-weight:normal">None</span>
</c:when>



<c:when test="${not empty pendingRequests}">
	
	<br/>
	<div style="height:5px;"></div>

	<table>
	<c:forEach var="pendingRequest" items="${pendingRequests}">
	
		<tr valign="top" class="<tags:alternateRow odd="" even="altRow"/>">
		
		<td>
	
		<tags:hideReveal title="${pendingRequest.from} - ${pendingRequest.to}" showInitially="false" identifier="pendingContainer${pendingRequest.requestId}">
	
			<table style="margin-left:20px">
				<tr>
					<td><b>Channel:</b></td>
					<td>${pendingRequest.channel}</td>
				</tr>
				<tr>
					<td><b>Requested By:</b></td>
					<td>${pendingRequest.userName}</td>
				</tr>
				<tr>
					<td colspan="2">${pendingRequest.email}</td>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
			</table>
	
		</tags:hideReveal>
		
		</td>
		
		<td>
			<div id="profileStatusBar${pendingRequest.requestId}" style="vertical-align: top"></div> 
		
			<script language="JavaScript">
	    		progressUpdaters[${pendingRequest.requestId}] = new Ajax.PeriodicalUpdater('profileStatusBar${pendingRequest.requestId}', '/spring/widget/profileWidget/percentDoneProgressBarHTML?requestId=${pendingRequest.requestId}', {method: 'post', frequency: 2});
			</script>
			
		</td>
		
		<td onClick="javascript:progressUpdaters[${pendingRequest.requestId}].stop();"><tags:widgetActionRefreshImage method="cancelLoadProfile"
								requestId="${pendingRequest.requestId}" title="Cancel"
								imgSrc="/WebConfig/yukon/Icons/action_stop.gif"
								imgSrcHover="/WebConfig/yukon/Icons/action_stop.gif" /></td>
		
		</tr>
		<tr><td colspan="3"><div style="height:5px;"></div></td></tr>
		
	</c:forEach>
	</table>
	
</c:when>
</c:choose>

