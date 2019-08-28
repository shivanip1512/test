<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="collectionActions.home">
    
    <c:if test="${!empty deviceCollection}"><cti:toJson id="collectionjson" object="${deviceCollection}"/></c:if>
    <c:if test="${!empty action}">
        <cti:msg2 var="actionText" key="yukon.web.modules.tools.collectionActions.collectionAction.${action}" blankIfMissing="true"/>
        <input type="hidden" id="action" value="${actionText}"/>
    </c:if>
    
    <cti:msg2 var="noDevicesSelectedMsg" key=".noDevicesSelected"/>
    <input type="hidden" id="noDevicesSelectedMessage" value="${noDevicesSelectedMsg}"/>
    
    <cti:msg2 var="noActionExecutedMsg" key=".noActionExecuted"/>
    <input type="hidden" id="progressReportMessage" value="${noActionExecutedMsg}"/>

    <c:if test="${not empty errorMsg}">
        <div class="error">${errorMsg}</div>
    </c:if>
    
    <cti:url var="recentResultsLink" value="/collectionActions/recentResults"/>
    <div class="fr"><a href="${recentResultsLink}">Recent Results</a></div><br/>

    <div id="collectionActionsAccordion">
        <c:set var="displaySeparator" value="${empty deviceCollection ? 'dn' : ''}"/>
        <h3><i:inline key=".deviceSelectionHeader"/><span class="js-device-separator ${displaySeparator}">:</span>
            <span class="badge js-count"></span>&nbsp;<span class="js-device-description"></span>
            <span class="js-view-selected-devices"></span>
        </h3>
        <div>
            <tags:sectionContainer2 nameKey="deviceSelection">
                <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                <cti:url var="actionUrl" value="/collectionActions/deviceSelectionGetDevices">
                    <c:if test="${redirectUrl != null}">
                        <cti:param name="redirectUrl" value="${redirectUrl}" />
                    </c:if>
                    <c:if test="${actionString != null}">
                        <cti:param name="actionString" value="${actionString}"/>
                    </c:if>
                </cti:url>
                <tags:deviceSelection action="${actionUrl}" groupDataJson="${groupDataJson}"
                    pickerType="devicePicker" eventAfterSubmit="true" />
            </tags:sectionContainer2>
        </div>
        <c:set var="displaySeparator" value="${empty action ? 'dn' : ''}"/>
        <h3><i:inline key=".selectActionHeader"/><span class="js-action-separator ${displaySeparator}">:</span>
            <span class="js-action"></span>
        </h3>
        <div id="collectionActionsDiv" class="js-block-this">
            <c:choose>
                <c:when test="${empty deviceCollection}">
                    ${noDevicesSelectedMsg}
                </c:when>
                <c:otherwise>
                    <jsp:include page="collectionActions.jsp"/>
                </c:otherwise>
            </c:choose>
        </div>
        <h3><i:inline key=".actionInputsHeader"/></h3>
        <div id="actionInputsDiv" class="ov">
            <c:choose>
                <c:when test="${empty actionInputs}">
                    <cti:msg2 key=".noActionSelected"/>
                </c:when>
                <c:otherwise>
                    <jsp:include page="${actionInputs}"/>
                </c:otherwise>
            </c:choose>
        </div>
        <h3><i:inline key=".progressReportHeader"/></h3>
        <div id="progressReportDiv">
            ${noActionExecutedMsg}
        </div>
    </div>

    <cti:includeScript link="/resources/js/pages/yukon.collection.actions.js" />
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.collection.actions.progress.report.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.bulk.device.configs.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.bulk.point.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.bulk.masschange.js" />
    <cti:includeScript link="/resources/js/pages/yukon.bulk.dataStreaming.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.bulk.meterProgramming.js"/>
</cti:standardPage>