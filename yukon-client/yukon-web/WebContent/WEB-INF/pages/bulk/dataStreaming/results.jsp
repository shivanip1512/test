<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:standardPage module="tools" page="bulk.dataStreaming.results">

    <cti:msgScope paths="modules.tools.bulk.dataStreaming">
                
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
            <tags:resultProgressBar totalCount="${deviceCollection.deviceCount}"
                countKey="DATA_STREAMING/${resultsId}/COMPLETED_LINES"
                progressLabelTextKey=".results.progressLabel"
                statusTextKey="DATA_STREAMING/${resultsId}/STATUS_TEXT" 
                statusClassKey="DATA_STREAMING/${resultsId}/STATUS_CLASS" classes="fl" />

            <cti:button id="cancel-btn" nameKey="cancel" data-key="${resultsId}" />

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
                <cti:link href="/bulk/collectionActions" key=".results.collectionActionOnDevicesLabel">
                    <cti:mapParam
                        value="${result.successDeviceCollection.collectionParameters}" />
                </cti:link>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.successDeviceCollection}" />
            </div>
            <cti:dataUpdaterCallback
                function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-success-actions'],true)"
                initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_SUCCESSES" />
        </c:if>

        <%-- FAILED COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".results.failedLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/FAILURE_COUNT" /></span>
        </div>

        <%-- FAILED DEVICE COLLECTION --%>
        <c:if test="${result.failureDeviceCollection != null}">
            <div id="js-error-actions" class="dn stacked">
                <cti:link href="/bulk/collectionActions" key=".results.collectionActionOnDevicesLabel">
                    <cti:mapParam
                        value="${result.failureDeviceCollection.collectionParameters}" />
                </cti:link>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.failureDeviceCollection}" />
            </div>
            <cti:dataUpdaterCallback
                function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-error-actions'],true)"
                initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_FAILURES" />
        </c:if>

        <%-- UNSUPPORTED COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".results.unsupportedLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/UNSUPPORTED_COUNT" /></span>
        </div>

        <%-- UNSUPPORTED DEVICE COLLECTION --%>
        <c:if test="${result.getUnsupportedCount() > 0}">
            <div id="js-unsupported-actions" class="dn stacked">
                <cti:link href="/bulk/collectionActions" key=".results.collectionActionOnDevicesLabel">
                    <cti:mapParam
                        value="${result.unsupportedDeviceCollection.collectionParameters}" />
                </cti:link>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.unsupportedDeviceCollection}" />
            </div>
            <cti:dataUpdaterCallback
                function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-unsupported-actions'],true)"
                initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_UNSUPPORTED" />
        </c:if>
        
        <%-- CANCELLED COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".results.cancelledLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/CANCELLED_COUNT" /></span>
        </div>

        <%-- CANCELLED DEVICE COLLECTION --%>
        <c:if test="${result.canceledDeviceCollection != null}">
            <div id="js-cancelled-actions" class="dn stacked">
                <cti:link href="/bulk/collectionActions" key=".results.collectionActionOnDevicesLabel">
                    <cti:mapParam
                        value="${result.canceledDeviceCollection.collectionParameters}" />
                </cti:link>
                <tags:selectedDevicesPopup
                    deviceCollection="${result.canceledDeviceCollection}" />
            </div>
            <cti:dataUpdaterCallback
                function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-cancelled-actions'],true)"
                initialize="true" value="DATA_STREAMING/${resultsId}/IS_COMPLETE_WITH_CANCELLED" />
        </c:if>
        
        <cti:dataUpdaterCallback function="yukon.bulk.dataStreaming.progress" initialize="true"
            value="DATA_STREAMING/${resultsId}/IS_COMPLETE" />

    </cti:msgScope>
    
    <cti:includeScript link="/resources/js/pages/yukon.bulk.dataStreaming.js"/>
    
    <div data-dialog id="data-streaming-popup" data-width="400" data-title="<cti:msg2 key=".configuration"/>" class="dn">
        <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
    </div>

</cti:standardPage>