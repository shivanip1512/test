<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="${pageTitle}" module="amr">
<cti:msgScope paths="yukon.common.device.groupMeterRead.resultDetail">

    <cti:url var="recentResultsUrl" value="/group/groupMeterRead/resultsList"/>
    <cti:list var="arguments">
        <cti:item value="${recentResultsUrl}" />
    </cti:list>
    
    <c:set var="resultKey" value="${resultWrapper.result.key}"/>

    <cti:msg2 var="pageTitle" key=".pageTitle"/>
    <cti:msg2 var="recentResultsTitle" key=".recentResultsTitle"/>
    <cti:msg2 var="noteLabel" key=".noteLabel"/>
    <cti:msg2 var="successSectionTitle" key=".section.success"/>
    <cti:msg2 var="failedSectionTitle" key=".section.failed"/>
    <cti:msg2 var="unsupportedSectionTitle" key=".section.unsupported"/>
    <cti:msg2 var="viewSuccess" key=".section.success.view"/>
    <cti:msg2 var="viewFailedReasons" key=".section.failed.viewFailReasons"/>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/dashboard" title="Home"/>
        
        <%-- results list --%>
        <cti:crumbLink url="/group/groupMeterRead/resultsList">${recentResultsTitle}</cti:crumbLink>
        
        <%-- read logs detail --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    
<script type="text/javascript">
function refreshResults(kind, container) {
    if (container.is(':visible')) {
        var url = yukon.url('/group/groupMeterRead/') + kind;
        $(container).load(url, {'resultKey': '${resultWrapper.result.key}'});
    }
}
$(function() {
    $('.js-view-success').click(function(e) {
        var container = $('#successResultsDiv${resultKey}');
        container.toggle();
        refreshResults('successList', container);
    });
    $('.js-view-failed').click(function(e) {
        var container = $('#errorsResultsDiv${resultKey}');
        container.toggle();
        refreshResults('errorsList', container);
    });
});
</script>
<style>.result {margin-left: 20px;}</style>
    
    <h2 class="page-title">${pageTitle}</h2>

    <c:set var="sectionTitle">Reading '${attributesDescription}' on <cti:msg2 key="${resultWrapper.result.deviceCollection.description}"/></c:set>
    <tags:sectionContainer id="readLogsDetailContainer" title="${sectionTitle}">
    
        <%-- NOTE --%>
        <table class="stacked">
            <tr>
                <td class="strong-label-small">${noteLabel}</td>
                <td class="detail"><i:inline key=".noteText" arguments="${arguments}" /></td>
            </tr>
        </table>

        <%-- PROGRESS --%>
        <div class="stacked">
            <c:set var="totalCount" value="${resultWrapper.result.originalDeviceCollectionCopy.deviceCount}"/>
            <tags:resultProgressBar totalCount="${totalCount}"
                                    countKey="GROUP_METER_READ/${resultKey}/COMPLETED_ITEMS"
                                    progressLabelTextKey=".progressLabel"
                                    statusTextKey="GROUP_METER_READ/${resultKey}/STATUS_TEXT"
                                    statusClassKey="GROUP_METER_READ/${resultKey}/STATUS_CLASS"/>
            
            <%-- device collection action --%>
            <div id="allDevicesActionsDiv" class="dn result">
                <cti:url var="bulkAllUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${resultWrapper.result.originalDeviceCollectionCopy.collectionParameters}"/>
                </cti:url>
                <a href="${bulkAllUrl}"><i:inline key=".allResults"/></a>
                <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.originalDeviceCollectionCopy}"/>
            </div>
            
            <%-- cre action --%>
            <div id="creResultsDiv" class="dn result">
                <c:if test="${not empty resultWrapper.result.commandRequestExecutionIdentifier}">
                    <cti:url var="creResultsUrl" value="/common/commandRequestExecutionResults/detail">
                        <cti:param name="commandRequestExecutionId" value="${resultWrapper.result.commandRequestExecutionIdentifier.commandRequestExecutionId}"/>
                    </cti:url>
                    <a href="${creResultsUrl}"><i:inline key=".creResults"/></a>
                </c:if>
            </div>
        </div>
        
        <%-- SUCCESS --%>
        <div class="stacked">
            <div class="fwb stacked">${successSectionTitle} <span class="success"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/SUCCESS_COUNT"/></span></div>
            <div id="js-success-actions" class="dn result">
                <div>
                    <cti:url var="bulkSuccessUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${resultWrapper.result.successCollection.collectionParameters}"/>
                    </cti:url>
                    <a href="${bulkSuccessUrl}"><i:inline key=".successResults"/></a>
                    <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.successCollection}"/>
                </div>
                <div>
                    <a href="javascript:void(0);" class="js-view-success"><i:inline key=".section.success.view"/></a>
                </div>
                <div id="successResultsDiv${resultKey}" class="dn"></div>
            </div>
        </div>
    
        <%-- PROCESSING EXCEPTION --%>
        <div class="stacked">
            <div class="fwb stacked">${failedSectionTitle} <span class="error"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/FAILURE_COUNT"/></span></div>
            <div id="js-error-actions" class="dn result">
                <div>
                    <cti:url var="bulkUrl" value="/bulk/collectionActions">
                        <cti:mapParam value="${resultWrapper.result.failureCollection.collectionParameters}"/>
                    </cti:url>
                    <a href="${bulkUrl}"><i:inline key=".failureResults"/></a>
                    <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.failureCollection}"/>
                </div>
                <div>
                    <a href="javascript:void(0);" class="js-view-failed">View Failure Reasons</a>
                </div>
                <div id="errorsResultsDiv${resultKey}" class="dn"></div>
            </div> 
        </div>
        
        <%-- UNSUPPORTED --%>
        <div class="stacked">
            <div class="fwb stacked">${unsupportedSectionTitle} <span class="error"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/UNSUPPORTED_COUNT"/></span></div>
            <div id="unsupportedActionsDiv" class="dn result">
                <cti:url var="bulkUnsupportedUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${resultWrapper.result.unsupportedCollection.collectionParameters}"/>
                </cti:url>
                <a href="${bulkUnsupportedUrl}"><i:inline key=".unsupportedResults"/></a>
                <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.unsupportedCollection}"/>
            </div>
        </div>
        
    </tags:sectionContainer>
    
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['allDevicesActionsDiv','creResultsDiv','js-success-actions','js-error-actions','unsupportedActionsDiv'],true)" initialize="true" value="GROUP_METER_READ/${resultKey}/IS_COMPLETE"/>

</cti:msgScope>
</cti:standardPage>