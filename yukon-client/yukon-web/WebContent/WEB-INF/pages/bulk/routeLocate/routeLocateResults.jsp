<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.routeLocateResults">
    <cti:includeScript link="/JavaScript/yukon.bulk.routeLocate.results.js"/>

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.routeLocateResults" deviceCollection="${result.deviceCollection}">
        <div class="clearfix stacked">
            <tags:resultProgressBar totalCount="${deviceCount}"
                                    countKey="ROUTELOCATE/${resultId}/COMPLETED_COUNT"
                                    progressLabelTextKey="yukon.web.modules.tools.bulk.routeLocateResults.progressLabel"
                                    statusTextKey="ROUTELOCATE/${resultId}/STATUS_TEXT"
                                    statusClassKey="ROUTELOCATE/${resultId}/STATUS_CLASS"
                                    isAbortedKey="ROUTELOCATE/${resultId}/IS_CANCELED"/>
            <%-- set/view routes --%>
            <form id="routeLocateSettingsForm" action="<cti:url value="/bulk/routeLocate/routeSettings" />" method="get">
                <input type="hidden" name="resultId" value="${resultId}">
                <%-- cancel commands --%>
                <div id="cancelLocateDiv" class="clearfix stacked">
                    <cti:url var="cancelUrl" value="/bulk/routeLocate/cancelCommands" />
                    <cti:msg var="cancelText" key="yukon.web.modules.tools.bulk.routeLocateResults.cancelLocateButton.label" />
                    <tags:cancelCommands resultId="${resultId}" cancelUrl="${cancelUrl}" cancelButtonText="${cancelText}"/>
                </div>
                <c:choose>
                    <c:when test="${result.autoUpdateRoute}">
                        <cti:button nameKey="viewRoutesButton" type="submit" classes="js-disable-after-click js-routes-button" disabled="${not result.complete}"/>
                    </c:when>
                    <c:otherwise>
                        <cti:button nameKey="setRoutesButton" type="submit" classes="js-disable-after-click js-routes-button" disabled="${not result.complete}"/>
                    </c:otherwise>
                </c:choose>
            </form>
        </div>
        <div id="AllDevicesActionsDiv" class="clearfix stacked">
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocateResults.collectionActionOnAllDevicesLabel" class="small">
                <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.deviceCollection}" />
        </div>

        <%-- SUCCESS --%>
        <div class="fwb">
            <cti:msg key="yukon.web.modules.tools.bulk.routeLocateResults.successLabel" />: 
            <span class="success">
                <cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/LOCATED_COUNT"/>
            </span>
        </div>
        <div id="js-success-actions" class="clearfix stacked">
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}" />
        </div>

        <%-- FAILURE --%>
        <div class="fwb">
            <cti:msg key="yukon.web.modules.tools.bulk.routeLocateResults.failureLabel" />:
            <span class="error">
                <cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/NOT_FOUND_COUNT"/>
            </span>
        </div>
        <div id="commandsCanceledDiv" class="dn error">
            <cti:msg key="yukon.web.modules.tools.bulk.routeLocateResults.commandsCanceled" />
        </div>
        <div id="js-error-actions" class="clearfix stacked">
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
        </div>
    </tags:bulkActionContainer>

    <cti:dataUpdaterCallback function="yukon.bulk.routeLocate.results.complete" initialize="true" isComplete="ROUTELOCATE/${resultId}/IS_COMPLETE" isCanceled="ROUTELOCATE/${resultId}/IS_CANCELED"/>
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['AllDevicesActionsDiv','js-success-actions','js-error-actions'],true)" initialize="true" value="ROUTELOCATE/${resultId}/IS_COMPLETE" />
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['AllDevicesActionsDiv','js-success-actions','js-error-actions'],true)" initialize="true" value="ROUTELOCATE/${resultId}/IS_CANCELED" />
 </cti:standardPage>