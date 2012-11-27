<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage page="waterLeakReport.report.intervalData" module="amr">

	<cti:includeScript link="/JavaScript/water_leak_report.js" />

	<form:form id="csvIntervalForm" action="csvWaterLeakIntervalData" method="get" commandName="backingBean">
		<cti:deviceCollection deviceCollection="${backingBean.deviceCollection}" />
		<tags:sortFields backingBean="${backingBean}" />
		<%@ include file="reportFilterFormValues.jspf"%>
	</form:form>

    <%@ include file="leakAlgorithmDialog.jspf"%>

	<c:if test="${collectionFromReportResults != null && filterResult.hitCount > 0}">
		<c:set var="actionsMenu">
			<tags:dropdownActions>
				<li>
                    <cti:link href="/bulk/collectionActions"
                        key="yukon.web.modules.amr.waterLeakReport.report.performCollectionAction">
						<cti:mapParam value="${collectionFromReportResults.collectionParameters}" />
					</cti:link>
                </li>
				<li>
                    <a id="exportIntervalCsv" href="javascript:void(0);">
                        <i:inline key=".exportCSV" />
                    </a>
                </li>
			</tags:dropdownActions>
		</c:set>
	</c:if>

    <c:set var="detection_algorithm">
        <a href="javascript:void(0);" class="f_open_detection_algorithm"><i:inline key="yukon.web.modules.amr.waterLeakReport.report.leakDetectionAlgorithm"/></a>
    </c:set>
	<div class="page_help">
		<cti:msg2 key=".leakIndicationDescription" arguments="${detection_algorithm}" htmlEscapeArguments="false"/>
	</div>

	<tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}"
		baseUrl="intervalData" titleLinkHtml="${actionsMenu}">
		<table class="compactResultsTable">
			<tr>
				<th>
                    <tags:sortLink nameKey="tableHeader.deviceName" baseUrl="intervalData" fieldName="DEVICE_NAME" isDefault="false" />
                </th>
				<th>
                    <tags:sortLink nameKey="tableHeader.meterNumber" baseUrl="intervalData" fieldName="METER_NUMBER" />
                </th>
				<th>
                    <tags:sortLink nameKey="tableHeader.deviceType" baseUrl="intervalData" fieldName="PAO_TYPE" />
                </th>
				<th>
                    <tags:sortLink nameKey="tableHeader.usage" baseUrl="intervalData" fieldName="USAGE" />
                </th>
				<th>
                    <tags:sortLink nameKey="tableHeader.date" baseUrl="intervalData" fieldName="DATE" isDefault="true" />
                </th>
			</tr>
			<c:forEach var="row" items="${filterResult.resultList}">
				<c:set var="disabledClass" value="" />
				<c:if test="${row.meter.disabled}">
					<c:set var="disabledClass" value="subtleGray" />
				</c:if>
				<c:set var="leakRowClass" value="" />
				<c:if test="${row.pointValueHolder.value == row.leakRate}">
					<c:set var="leakRowClass" value="lightRedBackground" />
				</c:if>
				<tr class="<tags:alternateRow odd="" even="altRow"/> ${disabledClass} ${leakRowClass}">
					<td>
                        <cti:paoDetailUrl yukonPao="${row.meter}">
                            ${fn:escapeXml(row.meter.name)}
						</cti:paoDetailUrl>
                    </td>
					<td>${fn:escapeXml(row.meter.meterNumber)}</td>
					<td><tags:paoType yukonPao="${row.meter}" /></td>
					<td><cti:pointValueFormatter value="${row.pointValueHolder}" format="VALUE" /></td>
					<td><cti:formatDate type="BOTH" value="${row.pointValueHolder.pointDataTimeStamp}" /></td>
				</tr>
			</c:forEach>

			<c:if test="${fn:length(filterResult.resultList) == 0}">
				<tr>
					<td class="noResults subtleGray" colspan="3"><i:inline key=".noUsage" /></td>
				</tr>
			</c:if>
		</table>
	</tags:pagedBox2>
</cti:standardPage>