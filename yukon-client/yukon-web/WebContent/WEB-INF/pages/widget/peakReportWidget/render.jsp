<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<%--REQUEST--%>
<c:if test="${! empty dateErrorMessage}">
	<div style="color:#FF0000;font-weight:bold;">${dateErrorMessage}</div>
	<br/>
</c:if>

<table class="compactResultsTable">

	<tr>
		<td class="label" nowrap="nowrap">Channel:</td>
		<td>
			<select name="channel" style="width:145px;">
				<c:forEach var="channelInfo" items="${availableChannels}">
	   				<option value="${channelInfo.channelNumber}" ${channelInfo.selected}>${channelInfo.channelDescription}</option>
	   			</c:forEach>
	   		</select>
	   	</td>
	   	<td colspan="2" align="right">
	   		<tags:widgetActionRefresh method="requestReport" label="Get Report" labelBusy="Reading"/>
	   	</td>
	</tr>
	
	<tr>
		<td class="label">Report Type:</td>
		<td colspan="3">
			<select name="peakType" style="height:20px;width:145px;">
				<c:forEach var="peakTypeInfo" items="${availablePeakTypes}">
	   				<option value="${peakTypeInfo.peakType}" ${peakTypeInfo.selected}>${peakTypeInfo.peakTypeDisplayName}</option>
	   			</c:forEach>
	   		</select>
	   	</td>
	</tr>
</table> 

<table class="compactResultsTable">
	<tr>
		<td class="label">Start:</td>
		<td><tags:dateInputCalendar fieldName="startDateStr" fieldValue="${startDateStr}"></tags:dateInputCalendar></td>
		<td class="label">Stop: </td>
		<td><tags:dateInputCalendar fieldName="stopDateStr" fieldValue="${stopDateStr}"></tags:dateInputCalendar></td>
	</tr>
</table>



<%--RESULTS--%>
<c:if test="${! empty peakResult}">
	<br/>
	
	<c:choose>
	
		<c:when test="${!peakResult.noData && peakResult.deviceError == ''}">
		
			<table class="compactResultsTable">
				
				<tr>
					<th>Report: <div style="font-weight:normal;display:inline;">${peakResult.peakTypeReportDisplayName}</div></th>
					<th align="right" style="font-weight:normal;color:#666666;">${peakResult.runDateDisplay}</th>
				</tr>
		
				<tr>
					<td class="label">Period:</td>
					<td>${peakResult.periodStartDateDisplay} - ${peakResult.periodStopDateDisplay}</td>
				</tr>
				
				<tr>
					<td class="label">Avg Daily / Total Usage:</td>
					<td>${peakResult.averageDailyUsage} / ${peakResult.totalUsage} kWH</td>
				</tr>
				<tr>
					<td class="label">Peak ${peakResult.peakTypeDisplayName}:</td>
					<td>${peakResult.peakValue}</td>
				</tr>
				
				<c:choose>
					<c:when test="${peakResult.peakType == 'INTERVAL'}">
						<tr>
							<td class="label">Peak Interval Demand:</td>
							<td>${peakResult.demand} kW</td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td class="label">Peak Day Total Usage:</td>
							<td>${peakResult.usage} kWH</td>
						</tr>
					</c:otherwise>
				</c:choose>
				
				
			
			
			</table>

		</c:when>
	
		<c:otherwise>
			There was an error reading the meter<br>
  			<c:forEach items="${peakResult.errors}" var="error">
    			<tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
    			${error.porter}<br>
    			${error.troubleshooting}<br>
    			</tags:hideReveal><br>
  			</c:forEach>
		</c:otherwise>
	
	</c:choose>


</c:if>


