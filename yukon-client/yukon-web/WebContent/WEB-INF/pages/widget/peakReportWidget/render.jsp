<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<%--REQUEST--%>
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
	   		<tags:widgetActionUpdate method="requestReport" label="Get Report" labelBusy="Reading" container="${widgetParameters.widgetId}_results"/>
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
<div id="${widgetParameters.widgetId}_results">
    <c:url var="peakSummaryReportResultUrl" value="/WEB-INF/pages/widget/peakReportWidget/peakSummaryReportResult.jsp" />
    <jsp:include page="${peakSummaryReportResultUrl}" />
</div>
