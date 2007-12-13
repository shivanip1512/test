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
<%--CHANNElS PROFILING--%>

<table class="compactResultsTable">

	<tr>
	  <th>Channel (Type)</th>
	  <th>Interval</th>
	  <th>Scan</th>
	  <th>Action</th>
	</tr>
	
	<tr align="center">
		<td align="left">Channel 1 (Load)</td>
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
	
	<tr class="last" align="center">
		<td align="left">Channel 4 (Voltage)</td>
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
<table class="compactResultsTable">
	<tr>
		<th colspan="3">Request Past Profile:</th>
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
			<tags:widgetActionUpdate method="initiateLoadProfile" label="Start" labelBusy="Start" container="${widgetParameters.widgetId}_results"/>
		</td>
	</tr>
	
</table>
<br/>

<%--RESULTS--%>
<div id="${widgetParameters.widgetId}_results">
    <c:url var="ongoingProfilesUrl" value="/WEB-INF/pages/widget/profileWidget/ongoingProfiles.jsp" />
    <jsp:include page="${ongoingProfilesUrl}" />
</div>
