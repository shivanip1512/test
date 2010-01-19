<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="maxObjectCountBeforeScroll" value="30"/>

<c:if test="${rowCount > maxObjectCountBeforeScroll}">
	<div style="overflow:auto; height:500px;">
</c:if>

<table class="compactResultsTable">

	<tr>
		<th>Batch Number</th>
		<th>Device Count</th>
		<th>Date/Time</th>
	</tr>
	
	<c:forEach var="row" items="${previewScheduleRows}">
		<tr>
			<td>${row.batchNumber}</td>
			<td>
				${row.deviceCount}
			</td>
			<td>
				<cti:formatDate type="BOTH" value="${row.dateTime}"/>
			</td>
		</tr>
	</c:forEach>

</table>
