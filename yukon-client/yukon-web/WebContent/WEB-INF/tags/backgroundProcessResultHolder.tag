<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="resultsTypeMsgKey" required="true" %>
<%@ attribute name="callbackResult" required="true" 
        type="com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder"%>

<cti:msgScope paths="yukon.common.device.bulk">

<c:set var="resultsId" value="${callbackResult.resultsId}"/>

<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>

<%-- NOTE --%>
<table class="stacked-md">
    <tr>
        <td><span><i:inline key=".${resultsTypeMsgKey}Results.noteLabel"/></span></td>
        <td><i:inline key=".${resultsTypeMsgKey}Results.noteText"/></td>
    </tr>
</table>

<%-- PROGRESS --%>
<div class="stacked-md">
    <tags:resultProgressBar totalCount="${callbackResult.totalItems}"
                        countKey="BACKGROUNDPROCESS/${resultsId}/COMPLETED_LINES"
                        progressLabelTextKey=".${resultsTypeMsgKey}Results.progressLabel"
                        statusTextKey="BACKGROUNDPROCESS/${resultsId}/STATUS_TEXT"/>
</div>

<%-- SUCCESS COUNT --%>
<div class="fwb stacked">
    <i:inline key=".${resultsTypeMsgKey}Results.successLabel"/>:&nbsp;
    <span class="success"><cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/></span>
</div>

<%-- SUCCESS DEVICE COLLECTION --%>
<c:if test="${callbackResult.successDevicesSupported}">
    <div id="js-success-actions" class="dn stacked">
        <cti:link href="/bulk/collectionActions" key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel">
            <cti:mapParam value="${callbackResult.successDeviceCollection.collectionParameters}"/>
        </cti:link>
        <tags:selectedDevicesPopup deviceCollection="${callbackResult.successDeviceCollection}"/>
    </div>
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-success-actions'],true)" 
        initialize="true" value="BACKGROUNDPROCESS/${resultsId}/IS_COMPLETE_WITH_SUCCESSES"/>
</c:if>

<%-- FAILURE COUNT --%>
<div class="fwb stacked">
    <i:inline key=".${resultsTypeMsgKey}Results.processingExceptionLabel"/>:&nbsp;
    <span class="error fn">
        <cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/>
    </span>
</div>

<%-- FAILURE DETAILS --%>
<div id="js-error-actions" class="dn stacked">

    <ul class="simple-list stacked">
        <%-- FAILURE DEVICE COLLECTION --%> 
        <c:if test="${callbackResult.failureDevicesSupported}">
            <li>
                <cti:link href="/bulk/collectionActions" key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel">
                    <cti:mapParam value="${callbackResult.failureDeviceCollection.collectionParameters}"/>
                </cti:link>
                <tags:selectedDevicesPopup deviceCollection="${callbackResult.failureDeviceCollection}"/>
            </li>
        </c:if>
           
        <%-- FAILURE FILE DOWNLOAD --%>
        <c:if test="${callbackResult.failureFileSupported}">
            <li>
                <tags:downloadBulkFailuresFile resultsId="${resultsId}" showText="true"/>
            </li>
        </c:if>
    </ul>
    <%-- FAILURE DETAILS LINK --%>
    <c:if test="${callbackResult.failureReasonsListSupported}">
        <a href="javascript:void(0);" data-show-hide="#processing-errors-${resultsId}">
            <i:inline key="yukon.common.failures.view"/>
        </a>
        <cti:url var="url" value="/bulk/processing-errors">
            <cti:param name="resultsId" value="${resultsId}"/>
            <cti:param name="isFileUpload" value="${not empty fileName}"/>
        </cti:url>
        <div id="processing-errors-${resultsId}" data-url="${url}" class="dn buffered"></div>
    </c:if>
</div>

<cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['js-error-actions'],true)" 
    initialize="true" value="BACKGROUNDPROCESS/${resultsId}/IS_COMPLETE_WITH_FAILURES"/>

</cti:msgScope>