<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:standardPage module="tools" page="bulk.dataStreaming.results">

    <cti:msgScope paths="modules.tools.bulk.dataStreaming.results">

        <table class="stacked">
            <tr>
                <td class="strong-label-small"><i:inline key=".noteLabel" /></td>
                <td class="detail"><i:inline key=".noteText" arguments="${arguments}" /></td>
            </tr>
        </table>

        <%-- PROGRESS --%>
        <div class="stacked clearfix">
            <tags:resultProgressBar totalCount="${deviceCollection.deviceCount}"
                countKey="DATA_STREAMING/${resultsId}/COMPLETED_LINES"
                progressLabelTextKey=".progressLabel"
                statusTextKey="DATA_STREAMING/${resultsId}/STATUS_TEXT" 
                statusClassKey="DATA_STREAMING/${resultsId}/STATUS_CLASS" classes="fl" />

            <cti:button id="cancel-btn" nameKey="cancel" data-key="${resultsId}" />

        </div>

        <%-- SUCCESS COUNT --%>
        <div class="fwb stacked">
            <i:inline key=".successLabel" />
            :&nbsp; <span class="success"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/SUCCESS_COUNT" /></span>
        </div>

        <%-- SUCCESS DEVICE COLLECTION --%>
        <c:if test="${result.successDeviceCollection != null}">
            <div id="js-success-actions" class="dn stacked">
                <cti:link href="/bulk/collectionActions" key=".collectionActionOnDevicesLabel">
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
            <i:inline key=".failedLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/FAILURE_COUNT" /></span>
        </div>

        <%-- FAILED DEVICE COLLECTION --%>
        <c:if test="${result.failureDeviceCollection != null}">
            <div id="js-error-actions" class="dn stacked">
                <cti:link href="/bulk/collectionActions" key=".collectionActionOnDevicesLabel">
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
            <i:inline key=".unsupportedLabel" />
            :&nbsp; <span class="error"><cti:dataUpdaterValue type="DATA_STREAMING"
                    identifier="${resultsId}/UNSUPPORTED_COUNT" /></span>
        </div>

        <%-- UNSUPPORTED DEVICE COLLECTION --%>
        <c:if test="${result.unsupportedDeviceCollection != null}">
            <div id="js-unsupported-actions" class="dn stacked">
                <cti:link href="/bulk/collectionActions" key=".collectionActionOnDevicesLabel">
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
        
        <cti:dataUpdaterCallback function="yukon.bulk.dataStreaming.progress" initialize="true"
            value="DATA_STREAMING/${resultsId}/IS_COMPLETE" />

    </cti:msgScope>
    
    <cti:includeScript link="/resources/js/pages/yukon.bulk.dataStreaming.js"/>

</cti:standardPage>