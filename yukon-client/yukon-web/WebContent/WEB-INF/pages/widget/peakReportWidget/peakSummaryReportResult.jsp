<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<c:if test="${! empty dateErrorMessage}">
    <br/>
    <div style="color:#FF0000;font-weight:bold;">${dateErrorMessage}</div>
    <br/>
</c:if>

<c:if test="${! empty peakResult}">
	<br/>
	
	<c:choose>
	
		<c:when test="${!peakResult.noData && peakResult.deviceError == ''}">
		
			<table class="compactResultsTable">
				
				<tr>
					<th>Report: <div style="font-weight:normal;display:inline;">${reportTypeDisplayName}</div></th>
					<th align="right" style="font-weight:normal;color:#666666;">${runDateDisplay}</th>
				</tr>
		
				<tr>
					<td class="label">Period:</td>
					<td>${periodStartDateDisplay} - ${periodStopDateDisplay}</td>
				</tr>
				
				<tr>
					<td class="label">Avg Daily / Total Usage:</td>
					<td>${peakResult.averageDailyUsage} / ${peakResult.totalUsage} kWH</td>
				</tr>
				<tr>
					<td class="label">Peak ${displayName}:</td>
					<td>${peakValueStr}</td>
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

