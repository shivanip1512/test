<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="assetId" required="true" type="java.lang.Integer" %>
<%@ attribute name="assetAvailabilitySummary" required="true" type="com.cannontech.dr.assetavailability.AssetAvailabilitySummary" %>
<%@ attribute name="pieJSONData" required="true" type="java.util.Map" %>
<%@ attribute name="showDetails" required="true" type="java.lang.Boolean" %>
<%@ attribute name="showDownload" type="java.lang.Boolean" %>

<cti:default var="showDownload" value="${true}"/>

<cti:msgScope paths="modules.operator.hardware.assetAvailability">

<c:set var="activeSize" value="${assetAvailabilitySummary.activeSize}"/>
<c:set var="inactiveSize" value="${assetAvailabilitySummary.inactiveSize}"/>
<c:set var="optedOutSize" value="${assetAvailabilitySummary.optedOutSize}"/>
<c:set var="unavailableSize" value="${assetAvailabilitySummary.unavailableSize}"/>

<div class="column-12-12 clearfix">
    <div class="column one">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".active">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${activeSize}"/>
                <span class="success">${activeSize} ${numberOfDevices}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".inactive">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${inactiveSize}"/>
                <span class="warning">${inactiveSize} ${numberOfDevices}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".optedOut">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${optedOutSize}"/>
                <span class="disabled">${optedOutSize} ${numberOfDevices}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".unavailable">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${unavailableSize}"/>
                <span class="error">${unavailableSize} ${numberOfDevices}</span>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </div>
    <div class="column two nogutter">
        <!-- Pie Chart -->
        <div style="max-height: 100px;">
            <flot:jsonPieChart data="${pieJSONData}" classes="small-pie"/>
        </div>
    </div>
</div>

<div class="column-24 clearfix">
    <div class="column one nogutter">
        <c:if test="${showDetails}">
            <cti:url var="assetDetailsUrl" value="/dr/assetAvailability/detail">
                <cti:param name="controlAreaOrProgramOrScenarioId" value="${assetId}"/>
            </cti:url>
            <div>
                <a href="${assetDetailsUrl}">
                    <cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.viewDetails"/>
                </a>
            </div>
        </c:if>
        
        <div class="action-area">
            <c:if test="${showDownload}">
                <cm:dropdown icon="icon-page-white-excel" type="button" key="yukon.web.components.button.download.label" triggerClasses="fr" menuClasses="no-icons">
                    <c:if test="${activeSize > 0}">
                        <cm:dropdownOption label="Active" href="${assetId}/aa/download/active"/>
                    </c:if>
                    <c:if test="${inactiveSize > 0}">
                        <cm:dropdownOption label="Inactive" href="${assetId}/aa/download/inactive"/>
                    </c:if>
                    <c:if test="${optedOutSize > 0}">
                        <cm:dropdownOption label="Opted Out" href="${assetId}/aa/download/opted_out"/>
                    </c:if>
                    <c:if test="${unavailableSize > 0}">
                        <cm:dropdownOption label="Unavailable" href="${assetId}/aa/download/unavailable"/>
                    </c:if>
                    <li class="divider"></li>
                    <cm:dropdownOption label="All" href="${assetId}/aa/download/all"/>
                </cm:dropdown>
            </c:if>
            <c:choose>
                <c:when test="${unavailableSize > maxPingableDevices}">
                    <cti:button id="noPingButton" nameKey="noPingDevices" disabled="true" icon="icon-ping"/>
                </c:when>
                <c:when test="${unavailableSize > 0}">
                    <cti:button id="pingButton" nameKey="pingDevices" busy="true" icon="icon-ping"/>
                </c:when>
            </c:choose>
            <span id="pingResults" style="display:none">
                <tags:updateableProgressBar 
                    countKey="ASSET_AVAILABILITY_READ/${assetId}/SUCCESS_COUNT"
                    totalCountKey="ASSET_AVAILABILITY_READ/${assetId}/TOTAL_COUNT"
                    failureCountKey="ASSET_AVAILABILITY_READ/${assetId}/FAILED_COUNT" 
                    hideCount="true" hidePercent="false"
                    completionCallback="yukon.dr.assetDetails.unbusyPingButton" />
            </span>
        </div>
    </div>
</div>
    
</cti:msgScope>