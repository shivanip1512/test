<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="analysisProgress">

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
        <%-- interval data analysis --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
    </cti:breadCrumbs>

    <tags:bulkActionContainer key="yukon.web.modules.amr.analysisProgress" deviceCollection="${deviceCollection}">

        <%-- ANALYSIS PARAMETERS --%>
        <div style="padding: 0px 0px 20px 0px">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".dateRange">
                    <cti:formatDate value="${backingBean.startDate}" type="DATE" />
                    <cti:formatDate value="${backingBean.startDate}" type="TIME24H" />
                    -
                    <cti:formatDate value="${backingBean.stopDate}" type="DATE" />
                    <cti:formatDate value="${backingBean.stopDate}" type="TIME24H" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".interval">
                    <cti:formatPeriod value="${backingBean.selectedIntervalDuration}" type="DHMS_REDUCED" />
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".pointQuality">
                    <c:choose>
                        <c:when test="${backingBean.excludeBadQualities}">
                            <cti:msg key="yukon.web.modules.amr.analysisProgress.excludeBadQualities" />
                        </c:when>
                        <c:otherwise>
                            <cti:msg key="yukon.web.modules.amr.analysisProgress.acceptBadQualities" />
                        </c:otherwise>
                    </c:choose>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".attribute">
                    ${backingBean.selectedAttribute.description}
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>

        <%-- PROGRESS --%>
        <div style="padding: 0px 0px 20px 0px">
            <tags:resultProgressBar totalCount="${callbackResult.totalItems}" 
                countKey="BACKGROUNDPROCESS/${resultsId}/COMPLETED_LINES"
                progressLabelTextKey="yukon.web.modules.amr.analysisProgress.progressLabel"
                statusTextKey="BACKGROUNDPROCESS/${resultsId}/STATUS_TEXT" />
        </div>

        <%-- SUCCESS --%>
        <div class="normalBoldLabel" style="padding: 0px 0px 20px 0px">
            <cti:msg key="yukon.web.modules.amr.analysisProgress.successLabel" />
            <span class="okGreen">
                <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/>
            </span>
        </div>

        <%-- FAIL --%>
        <div class="normalBoldLabel" style="padding: 0px 0px 20px 0px">
            <cti:msg key="yukon.web.modules.amr.analysisProgress.failLabel" />
            <span class="errorRed">
                <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/>
            </span>
        </div>
        
        <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['viewResultsDiv'],true)" initialize="true" value="BACKGROUNDPROCESS/${resultsId}/IS_COMPLETE_WITH_SUCCESSES" />
        
        <div id="viewResultsDiv">
            <cti:button key="viewResultsButton" href="results?analysisId=${analysisId}"/>
        </div>
    </tags:bulkActionContainer>
</cti:standardPage>