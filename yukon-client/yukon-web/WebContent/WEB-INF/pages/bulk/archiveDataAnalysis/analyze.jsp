<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="analysis.progress">

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle" />
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle" />
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}" />
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />      
        <%-- ADA Progress --%>
        <cti:crumbLink><i:inline key="yukon.web.modules.amr.analysis.progress.pageName"/></cti:crumbLink>
    </cti:breadCrumbs>

    <tags:boxContainer2 nameKey="yukon.web.modules.amr.analysis.progress">
        
        <div class="smallBoldLabel notesSection">
            <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
        </div>
        
        <%-- ANALYSIS PARAMETERS --%>
        <div class="bottomPadded">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".dateRange">
                    <cti:formatInterval type="DATEHM" value="${analysis.dateTimeRange}"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".interval">
                    <cti:formatPeriod value="${analysis.intervalPeriod}" type="DHMS_REDUCED" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".pointQuality">
                    <c:choose>
                        <c:when test="${analysis.excludeBadPointQualities}">
                            <i:inline key="yukon.web.modules.amr.analysis.normalQualitiesOnly" />
                        </c:when>
                        <c:otherwise>
                            <i:inline key="yukon.web.modules.amr.analysis.allQualities" />
                        </c:otherwise>
                    </c:choose>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".attribute">
                    ${analysis.attribute.description}
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>

        <%-- PROGRESS --%>
        <div class="bottomPadded">
            <tags:resultProgressBar totalCount="${callbackResult.totalItems}" 
                countKey="BACKGROUNDPROCESS/${resultsId}/COMPLETED_LINES"
                progressLabelTextKey="yukon.web.modules.amr.analysis.progress.progressLabel"
                statusTextKey="BACKGROUNDPROCESS/${resultsId}/STATUS_TEXT" />
        </div>

        <%-- SUCCESS --%>
        <div class="normalBoldLabel bottomPadded">
            <i:inline key="yukon.web.modules.amr.analysis.progress.successLabel" />
            <span class="okGreen">
                <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/>
            </span>
        </div>

        <%-- FAIL --%>
        <div class="normalBoldLabel bottomPadded">
            <i:inline key="yukon.web.modules.amr.analysis.progress.failLabel" />
            <span class="errorRed">
                <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/>
            </span>
        </div>
        
        <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['viewResultsDiv'],true)" initialize="true" value="BACKGROUNDPROCESS/${resultsId}/IS_COMPLETE_WITH_SUCCESSES" />
        
        <div id="viewResultsDiv" style="display:none;">
            <cti:url var="resultsUrl" value="/spring/bulk/archiveDataAnalysis/results/view">
                <cti:param name="analysisId" value="${analysis.analysisId}"/>
            </cti:url>
            <cti:button key="viewResultsButton" href="${resultsUrl}"/>
        </div>
    </tags:boxContainer2>
</cti:standardPage>