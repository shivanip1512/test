<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:standardPage module="tools" page="bulk.dataStreaming.results">

    <cti:msgScope paths="modules.tools.bulk.dataStreaming">
    
    <c:set var="resultsId" value="${result.resultsId}"/>
                
        <div class="stacked notes">
             <strong><i:inline key=".configuration"/>:</strong>&nbsp;
            <c:choose>
                <c:when test="${config != null}">
                    <a href="javascript:void(0);" data-popup="#data-streaming-popup">${fn:escapeXml(config.name)}</a>
                </c:when>
                <c:otherwise>
                    <i:inline key=".removeConfiguration"/>
                </c:otherwise>
            </c:choose>
        </div>

        <table class="stacked">
            <tr>
                <td class="strong-label-small"><i:inline key=".results.noteLabel" /></td>
                <td class="detail"><i:inline key=".results.noteText" arguments="${arguments}" /></td>
            </tr>
        </table>

        <%-- PROGRESS --%>
        <div class="stacked clearfix">
            <tags:resultProgressBar totalCount="${result.allDevicesCollection.deviceCount}"
                countKey="DATA_STREAMING/${resultsId}/COMPLETED_LINES"
                progressLabelTextKey=".results.progressLabel"
                statusTextKey="DATA_STREAMING/${resultsId}/STATUS_TEXT" 
                statusClassKey="DATA_STREAMING/${resultsId}/STATUS_CLASS" classes="fl" />

            <cti:button id="cancel-btn" nameKey="cancel" data-key="${resultsId}" />

        </div>
        
        <div class="stacked js-results-detail dn">
            <cti:url var="creResultsUrl" value="/common/commandRequestExecutionResults/detail">
                <cti:param name="commandRequestExecutionId" value="${result.execution.id}" />
            </cti:url>
            <a href="${creResultsUrl}"><i:inline key=".results.view" /></a>
        </div>

        <%-- SUCCESS COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".results.successLabel" />
            :&nbsp; <span class="success"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/SUCCESS_COUNT" /></span>
        </div>

        <%-- SUCCESS DEVICE COLLECTION --%>
        <c:if test="${result.successDeviceCollection != null}">
            <div id="js-success-actions" class="dn stacked">
                <cti:url var="successUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${result.successDeviceCollection.collectionParameters}" />
                </cti:url>
                <a href="${successUrl}"><i:inline key=".results.collectionActionOnDevicesLabel" /></a>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.successDeviceCollection}" />
            </div>
        </c:if>
        <cti:dataUpdaterCallback
            function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-success-actions'],true)"
            initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_SUCCESSES" />

        <%-- FAILED COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".results.failedLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/FAILURE_COUNT" /></span>
        </div>

        <%-- FAILED DEVICE COLLECTION --%>
        <c:if test="${result.failureDeviceCollection != null}">
            <div id="js-error-actions" class="dn stacked">
                <cti:url var="failedUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}" />
                </cti:url>
                <a href="${failedUrl}"><i:inline key=".results.collectionActionOnDevicesLabel" /></a>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.failureDeviceCollection}" />
            </div>
        </c:if>
        
        <cti:dataUpdaterCallback
            function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-error-actions'],true)"
            initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_FAILURES" />

        <%-- UNSUPPORTED COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".results.unsupportedLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/UNSUPPORTED_COUNT" /></span>
        </div>

        <%-- UNSUPPORTED DEVICE COLLECTION --%>
        <c:if test="${result.getUnsupportedCount() > 0}">
            <div id="js-unsupported-actions" class="dn stacked">
                <cti:url var="unsupportedUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${result.unsupportedDeviceCollection.collectionParameters}" />
                </cti:url>
                <a href="${unsupportedUrl}"><i:inline key=".results.collectionActionOnDevicesLabel" /></a>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.unsupportedDeviceCollection}" />
            </div>
        </c:if>
        <cti:dataUpdaterCallback
            function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-unsupported-actions'],true)"
            initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_UNSUPPORTED" />
        
        <%-- CANCELED COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".results.cancelledLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/CANCELED_COUNT" /></span>
        </div>

        <%-- CANCELED DEVICE COLLECTION --%>
        <c:if test="${result.canceledDeviceCollection != null}">
            <div id="js-canceled-actions" class="dn stacked">
                <cti:url var="canceledUrl" value="/bulk/collectionActions">
                    <cti:mapParam value="${result.canceledDeviceCollection.collectionParameters}" />
                </cti:url>
                <a href="${canceledUrl}"><i:inline key=".results.collectionActionOnDevicesLabel" /></a>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.canceledDeviceCollection}" />
            </div>
        </c:if>
        <cti:dataUpdaterCallback
            function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-canceled-actions'],true)"
            initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_CANCELED" />
        
        <cti:dataUpdaterCallback function="yukon.bulk.dataStreaming.progress" initialize="true"
            value="DATA_STREAMING/${resultsId}/IS_COMPLETE" />

    </cti:msgScope>
    
    <cti:includeScript link="/resources/js/pages/yukon.bulk.dataStreaming.js"/>
    
    <div id="data-streaming-popup" data-width="400" data-title="<cti:msg2 key=".configuration"/>" class="dn">
        <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
    </div>

</cti:standardPage>