<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<c:choose>
	<c:when test="${readable}">
		
		<%--REQUEST--%>
		<table width="95%">
			<tr>
				<td><i:inline key=".channel"/></td>
				<td>
					<select name="channel" style="width:145px;">
						<c:forEach var="channelInfo" items="${availableChannels}">
			   				<option value="${channelInfo.channelNumber}" ${channelInfo.selected}>${channelInfo.channelDescription}</option>
			   			</c:forEach>
			   		</select>
			   	</td>
			   	<td colspan="2" align="right">
                    <cti:msg2 var="getReport" key=".getReport"/>
                    <cti:msg2 var="reading" key=".reading"/>
			   		<tags:widgetActionUpdate method="requestReport" label="${getReport}" labelBusy="${reading}" container="${widgetParameters.widgetId}_results"/>
			   	</td>
			</tr>
			
			<tr>
				<td><i:inline key=".reportType"/></td>
				<td colspan="3">
					<select name="peakType" style="height:20px;width:145px;">
						<c:forEach var="peakTypeInfo" items="${availablePeakTypes}">
			   				<option value="${peakTypeInfo.peakType}" ${peakTypeInfo.selected}>${peakTypeInfo.peakTypeDisplayName}</option>
			   			</c:forEach>
			   		</select>
			   	</td>
			</tr>
		</table> 
		
		<table>
			<tr>
				<td><i:inline key=".start"/></td>
				<td><tags:dateInputCalendar fieldName="startDateStr" fieldValue="${startDateStr}"></tags:dateInputCalendar></td>
				<td><i:inline key=".stop"/></td>
				<td><tags:dateInputCalendar fieldName="stopDateStr" fieldValue="${stopDateStr}"></tags:dateInputCalendar></td>
			</tr>
		</table>
		
		<%--RESULTS--%>
		<div id="${widgetParameters.widgetId}_results">
		    <cti:url var="peakSummaryReportResultUrl" value="/WEB-INF/pages/widget/peakReportWidget/peakSummaryReportResult.jsp" />
		    <jsp:include page="${peakSummaryReportResultUrl}" />
		</div>
	</c:when>
	<c:otherwise>
		<i:inline key=".notAuthorized"/>
	</c:otherwise>
</c:choose>
