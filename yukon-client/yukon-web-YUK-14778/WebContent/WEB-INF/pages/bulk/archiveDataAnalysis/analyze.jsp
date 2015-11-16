<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="bulk.analysis.progress">

    <tags:sectionContainer2 nameKey="yukon.web.modules.tools.bulk.analysis.progress">
        
        <div class="stacked notes">
            <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
        </div>
        
        <%-- ANALYSIS PARAMETERS --%>
        <div class="stacked">
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
                            <i:inline key="yukon.web.modules.tools.bulk.analysis.normalQualitiesOnly" />
                        </c:when>
                        <c:otherwise>
                            <i:inline key="yukon.web.modules.tools.bulk.analysis.allQualities" />
                        </c:otherwise>
                    </c:choose>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".attribute">
                    <i:inline key ="${analysis.attribute}"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>

        <%-- PROGRESS --%>
        <div class="stacked">
            <tags:resultProgressBar totalCount="${callbackResult.totalItems}" 
                countKey="BACKGROUNDPROCESS/${resultsId}/COMPLETED_LINES"
                progressLabelTextKey="yukon.web.modules.tools.bulk.analysis.progress.progressLabel"
                statusTextKey="BACKGROUNDPROCESS/${resultsId}/STATUS_TEXT" />
        </div>

        <%-- SUCCESS --%>
        <div class="fwb stacked">
            <i:inline key="yukon.web.modules.tools.bulk.analysis.progress.successLabel" />
            <span class="success">
                <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/>
            </span>
        </div>

        <%-- FAIL --%>
        <div class="fwb stacked">
            <i:inline key="yukon.web.modules.tools.bulk.analysis.progress.failLabel" />
            <span class="error">
                <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/>
            </span>
        </div>
        
        <div id="viewResultsDiv" style="display:none;">
            <cti:url var="resultsUrl" value="/bulk/archiveDataAnalysis/results/view">
                <cti:param name="analysisId" value="${analysis.analysisId}"/>
            </cti:url>
            <cti:button nameKey="viewResultsButton" href="${resultsUrl}"/>
        </div>
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['viewResultsDiv'],true)" initialize="true" value="BACKGROUNDPROCESS/${resultsId}/IS_COMPLETE_WITH_SUCCESSES" />
    </tags:sectionContainer2>
</cti:standardPage>