<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib tagdir="/WEB-INF/tags/amr" prefix="amr" %>

<cti:standardPage module="amr" page="cre.detail">

<cti:msg var="infoSectionText" key="yukon.web.modules.amr.cre.detail.info.section" />
<cti:msg var="infoTypeText" key="yukon.web.modules.amr.cre.detail.info.type" />
<cti:msg var="infoStatusText" key="yukon.web.modules.amr.cre.detail.info.status" />
<cti:msg var="infoStartTimeText" key="yukon.web.modules.amr.cre.detail.info.startTime" />
<cti:msg var="infoStopTimeText" key="yukon.web.modules.amr.cre.detail.info.stopTime" />
<cti:msg var="infoUserText" key="yukon.web.modules.amr.cre.detail.info.user" />
<cti:msg var="infoProgressText" key="yukon.web.modules.amr.cre.detail.info.progress" />

<cti:msg var="resultsSectionText" key="yukon.web.modules.amr.cre.detail.results.section" />
<cti:msg var="resultsSectionPopupInfoText" key="yukon.web.modules.amr.cre.detail.results.section.popupInfo" />
<cti:msg var="resultsTotalRequestsText" key="yukon.web.modules.amr.cre.detail.results.totalRequests" />
<cti:msg var="resultsSuccessText" key="yukon.web.modules.amr.cre.detail.results.success" />
<cti:msg var="resultsFailText" key="yukon.web.modules.amr.cre.detail.results.fail" />
<cti:msg var="resultsDetailsText" key="yukon.web.modules.amr.cre.detail.results.details" />
<cti:msg var="resultsViewReportText" key="yukon.web.modules.amr.cre.detail.results.viewReport" />
<cti:msg var="resultsHideReportText" key="yukon.web.modules.amr.cre.detail.results.hideReport" />
<cti:msg var="resultsFailStatsReportText" key="yukon.web.modules.amr.cre.detail.results.failStatsReport" />
<cti:msg var="resultsUnsupportedText" key="yukon.web.modules.amr.cre.detail.results.unsupported" />
<cti:msg var="resultsNotConfiguredText" key="yukon.web.modules.amr.cre.detail.results.notConfigured" />
<cti:msg var="resultsCanceledText" key="yukon.web.modules.amr.cre.detail.results.canceled" />
    
    <script type="text/javascript">

        function switchResultsFilterType(resultsFilterType) {

            // init request
            var url = '';
            var paramObj = {};

            // failure stats report
            if (resultsFilterType == 'FAIL_STATS') {

                url = yukon.url('/common/commandRequestExecutionResults/failureStatsReport');
                paramObj.commandRequestExecutionId = ${commandRequestExecutionId};

            // detail report (success/fail/all)
            } else {

                url = yukon.url('/common/commandRequestExecutionResults/detailsReport');
                paramObj.commandRequestExecutionId = ${commandRequestExecutionId};
                paramObj.resultsFilterType = resultsFilterType;
            }

            // show indicator, disable select
            $('#viewHideDetailsReportButtonIndicator').show();

            // request report
            $.ajax({
                type : 'POST',
                url : url,
                data : paramObj
            }).done( function (data, textStatus, jqXHR) {
                $('#detailsReportDiv').html(data);
                $('#viewHideDetailsReportButtonIndicator').hide();
                $('#detailsReportDiv').show();
            }).fail( function (jqXHR, textStatus) {
                $('#viewHideDetailsReportButtonIndicator').hide();
                $('#detailsReportDiv').show();
                $('#detailsReportDiv').html("Error getting report: " + textStatus);
            });
        }

        function countUpdateCallback() {
          //assumes data is of type Hash
            return function(data) {
                var isComplete = data.isComplete;
                if (0 !== $('#creProgressBarDiv').length && 0 !== $('#creStopTimeDiv').length) { 
                    if (isComplete == 'true') {
                        $('#creProgressBarDiv').hide();
                        $('#creStopTimeDiv').show();
                    } else if (isComplete == 'false') {
                        $('#creProgressBarDiv').show();
                        $('#creStopTimeDiv').hide();
                    }

                }
            };
        }
        
    </script>
    
    <%-- CRE INFO --%>
    <tags:sectionContainer title="${infoSectionText}" id="creInfoSection" styleClass="stacked">
                    
        <tags:nameValueContainer>

            <tags:nameValue name="${infoTypeText}" nameColumnWidth="160px">${cre.commandRequestExecutionType.shortName}</tags:nameValue>
            
            <tags:nameValue name="${infoStatusText}">
                <c:choose>
                    <c:when test="${cre.commandRequestExecutionStatus == 'FAILED'}">
                        <c:set var="statusSpanClass" value="error"/>
                    </c:when>
                    <c:when test="${cre.commandRequestExecutionStatus == 'IN_PROGRESS'}">
                        <c:set var="statusSpanClass" value="success"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="statusSpanClass" value=""/>
                    </c:otherwise>
                </c:choose>
                <span class="${statusSpanClass}">
                    <cti:msg key="${cre.commandRequestExecutionStatus.formatKey}" />
                </span>
            </tags:nameValue>
            
            <tags:nameValue name="${infoStartTimeText}"><cti:formatDate type="DATEHM" value="${cre.startTime}" nullText="N/A"/></tags:nameValue>
            
            <tags:nameValue name="${infoStopTimeText}">
                <c:choose>
                    <c:when test="${cre.commandRequestExecutionStatus == 'FAILED'}"><i:inline key="yukon.common.na"/></c:when>
                    <c:otherwise>
                        <cti:dataUpdaterCallback function="countUpdateCallback()" initialize="true" isComplete="COMMAND_REQUEST_EXECUTION/${cre.id}/IS_COMPLETE" />
                        <div id="creStopTimeDiv" <c:if test="${not isComplete}">style="display:none;"</c:if>>
                            <cti:dataUpdaterValue type="COMMAND_REQUEST_EXECUTION" identifier="${cre.id}/STOP_TIME"/>
                        </div>
                        <div id="creProgressBarDiv" <c:if test="${isComplete}">style="display:none;"</c:if>>
                            <tags:updateableProgressBar totalCount="${cre.requestCount}" countKey="COMMAND_REQUEST_EXECUTION/${cre.id}/RESULTS_COUNT"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue>
            <tags:nameValue name="${infoUserText}">${cre.userName}</tags:nameValue>
        </tags:nameValueContainer>
    </tags:sectionContainer>
    
    <%-- RESULTS --%>
    <tags:sectionContainer title="${resultsSectionText}" id="creReusltsSection" helpText="${resultsSectionPopupInfoText}" >
        <tags:nameValueContainer>
            <tags:nameValue name="${resultsTotalRequestsText}" nameColumnWidth="160px">
                <amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="REQUEST_COUNT" linkedInitially="${requestCount > 0}"/>
            </tags:nameValue>
            
            <tags:nameValue name="${resultsSuccessText}">
                <amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="SUCCESS_RESULTS_COUNT" linkedInitially="${successCount > 0}"/>
            </tags:nameValue>
            
            <tags:nameValue name="${resultsFailText}">
                <amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="FAILURE_RESULTS_COUNT" linkedInitially="${failCount > 0}"/>
            </tags:nameValue>
            
            <tags:nameValue name="${resultsNotConfiguredText}">
                <amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="NOT_CONFIGURED_COUNT" linkedInitially="${notConfiguredCount > 0}"/>
            </tags:nameValue>
            
            <tags:nameValue name="${resultsUnsupportedText}">
                <amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="UNSUPPORTED_COUNT" linkedInitially="${unsupportedCount > 0}"/>
            </tags:nameValue>
            
            <tags:nameValue name="${resultsCanceledText}">
                <amr:commandRequestExecutionResultsCountLink commandRequestExecutionId="${commandRequestExecutionId}" commandRequestExecutionUpdaterType="CANCELED_COUNT" linkedInitially="${canceledCount > 0}"/>
            </tags:nameValue>
            
            <c:if test="${failCount > 0 || successCount > 0}">
                <tags:nameValue name="${resultsViewReportText}">
                    <a href="javascript:void(0);" onclick="switchResultsFilterType('FAIL_STATS');">${resultsFailStatsReportText}</a>
                    <c:forEach var="resultsFilterType" items="${resultsFilterTypes}">
                        | <a href="javascript:void(0);" onclick="switchResultsFilterType('${resultsFilterType}');">${resultsFilterType.description}</a>
                    </c:forEach>
                    <img id="viewHideDetailsReportButtonIndicator" style="display:none;" src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>" alt="waiting"> 
                </tags:nameValue>
            </c:if>
        </tags:nameValueContainer>
            
        <div id="detailsReportDiv" style="display:none;">
        </div>
    
    </tags:sectionContainer>

</cti:standardPage>