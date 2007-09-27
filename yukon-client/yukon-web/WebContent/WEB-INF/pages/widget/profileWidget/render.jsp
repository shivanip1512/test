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

</script>
<%--CHANNLES PROFILING--%>
<br/>
<b>Start/Stop Profiling:</b>
<br/>
<table class="miniResultsTable">

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
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Stop" labelBusy="Stopping" toggleChannel1ProfilingOn="false" /></td>
			</c:when>
			<c:otherwise>
				<td style="font-weight:bold;color:#BB0000">Off</td>
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Start" labelBusy="Starting" toggleChannel1ProfilingOn="true" /></td>
			</c:otherwise>
		</c:choose>
	</tr>
	
	<tr>
		<td>Channel 4 (Voltage)</td>
		<td>${chan4Interval}</td>
		<c:choose>
			<c:when test="${chan4CollectionOn}">
				<td style="font-weight:bold;color:#339900">On</td>
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Stop" labelBusy="Stopping" toggleChannel4ProfilingOn="false" /></td>
			</c:when>
			<c:otherwise>
				<td style="font-weight:bold;color:#BB0000">Off</td>
				<td><tags:widgetActionRefresh method="toggleProfiling" label="Start" labelBusy="Starting" toggleChannel4ProfilingOn="true" /></td>
			</c:otherwise>
		</c:choose>
	</tr>
	
</table>
	
	
<br/>
	
<%--PAST PROFILES--%>
<%--<tags:hideReveal title="Past Profiles" showInitially="true" identifier="pastProfiles">--%>
<b>Request Past Profile:</b> <span style="color:#CC0000;font-weight:bold;">${initiateMessage}</span>
<br/>


<table class="miniResultsTableLeft">
	<tr>
		<td class="label">Channel:</td>
		<td>
			<select name="channel" style="height:20px;">
	   			<option value="1">Channel 1 (Load)</option>
	   			<option value="4">Channel 4 (Voltage)</option>
	   		</select>
	   	</td>
	</tr>
	
	<tr>
		<td class="label">Start Date:</td>
		<td><tags:dateInputCalendar fieldName="startDateStr" fieldValue="${startDateStr}"></tags:dateInputCalendar></td>
	</tr>
	
	<tr>
		<td class="label">Stop Date:</td>
		<td><tags:dateInputCalendar fieldName="stopDateStr" fieldValue="${stopDateStr}"></tags:dateInputCalendar></td>
	</tr>
	
	<tr>
		<td class="label">Email:</td>
		<td>
			<input id="email" name="email" type="text" size="25" style="height:16px;">
			&nbsp;<tags:widgetActionRefresh method="initiateLoadProfile" label="Start" labelBusy="Starting"/>
		</td>
	</tr>
	
</table>
<br/>



<%--ONGOING PROFILES--%>
<%--<tags:hideReveal title="Ongoing Profiles" showInitially="true" identifier="ongoingProfiles">--%>
<b>Ongoing Profiles: </b>


<c:choose>

	<c:when test="${empty pendingRequests}">
		None
	</c:when>

	<c:when test="${not empty pendingRequests}">
	
		<br/>
		<table class="miniResultsTable">
			<tr>
				<th>Requested By</th>
				<th>Channel</th>
				<th>Date Range</th>
				<th>Cancel</th>
			</tr>

			<c:forEach var="pendingRequest" items="${pendingRequests}">

				<tr>

					<td>${pendingRequest.email}</td>
					<td>${pendingRequest.channel}</td>
					<td>
						${pendingRequest.from}
						<br>
						${pendingRequest.to}
					</td>

					<td>
						<tags:widgetActionRefreshImage method="cancelLoadProfile"
							requestId="${pendingRequest.requestId}" title="Cancel"
							imgSrc="/WebConfig/yukon/Icons/cancel.gif"
							imgSrcHover="/WebConfig/yukon/Icons/cancel.gif" />
					</td>

				</tr>

			</c:forEach>

		</table>
		
	</c:when>
	
</c:choose>
	
	
	
	
	
	
	
	
	