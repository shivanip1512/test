<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ attribute name="value" required="true" type="com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange"%>

<input type="hidden" name="dataRange.dataRangeType" value="${value.dataRangeType}">
<c:choose>
	<c:when test="${value.dataRangeType == 'DATE_RANGE'}">
		<i:inline key="yukon.web.modules.amr.archivedValueExporter.dataRangeType.DATE_RANGE"/> 
		(<cti:formatDate type="DATE" value="${value.dateRange.startDate}"/> to 
		<cti:formatDate type="DATE" value="${value.dateRange.endDate}"/>)
		<input type="hidden" name="dataRange.dateRange.startDate" value="${value.dateRange.startDate}">
		<input type="hidden" name="dataRange.dateRange.endDate" value="${value.dateRange.endDate}">
	</c:when>
	<c:when test="${value.dataRangeType == 'DAYS_PREVIOUS'}">
		<i:inline key="yukon.web.modules.amr.archivedValueExporter.dataRangeType.DAYS_PREVIOUS"/> (${value.daysPrevious})
		<input type="hidden" name="dataRange.daysPrevious" value="${value.daysPrevious}">
	</c:when>
	<c:when test="${value.dataRangeType == 'END_DATE'}">
		<i:inline key="yukon.web.modules.amr.archivedValueExporter.dataRangeType.END_DATE"/>
		(<cti:formatDate type="DATE" value="${value.endDate}"/>)
		<input type="hidden" name="dataRange.endDate" value="${value.endDate}">
	</c:when>
	<c:when test="${value.dataRangeType == 'SINCE_LAST_CHANGE_ID'}">
		<i:inline key="yukon.web.modules.amr.archivedValueExporter.dataRangeType.SINCE_LAST_CHANGE_ID"/>
	</c:when>
	<c:otherwise>
		<i:inline key="${value.dataRangeType.formatKey}"/>
	</c:otherwise>
</c:choose>