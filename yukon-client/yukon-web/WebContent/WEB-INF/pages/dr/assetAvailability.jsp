<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div data-dialog class="js-point-data-dialog"></div>

<c:choose>
    <c:when test="${assetAvailabilitySummary.totalDeviceCount <= 0}">
        <span class="empty-list">
            <cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.noDevices"/>
        </span>
    </c:when>
    <c:otherwise>
        <cti:toJson id="js-asset-availability-summary" object="${assetAvailabilitySummary}"/>

        <cti:default var="showDownload" value="${true}"/>
        <cti:default var="allowPing" value="${true}" />

        <cti:msgScope paths="modules.operator.hardware.assetAvailability,modules.dr.assetDetails.status.">
            <c:set var="unavailableSize" value="${assetAvailabilitySummary.unavailable.deviceCount}"/>

            <c:forEach var="status" items="${statusTypes}">
                <input type="hidden" class="js-asset-${status}" value="<cti:msg2 key="${status}"/>"/>
            </c:forEach>

            <!-- Pie Chart -->
            <div class="js-asset-availability-pie-chart-summary"></div>

            <div class="column-24 clearfix">
                <div class="column one nogutter">
                    <div class="action-area">
                        <c:if test="${showDownload}">
                            <cm:dropdown icon="icon-page-white-excel" type="button" key="yukon.web.components.button.download.label" triggerClasses="fr" menuClasses="no-icons">
                                <c:if test="${assetAvailabilitySummary.active.deviceCount > 0}">
                                    <cm:dropdownOption key="yukon.web.modules.dr.assetDetails.status.ACTIVE" href="${paoId}/aa/download/active"/>
                                </c:if>
                                <c:if test="${assetAvailabilitySummary.inactive.deviceCount > 0}">
                                    <cm:dropdownOption key="yukon.web.modules.dr.assetDetails.status.INACTIVE" href="${paoId}/aa/download/inactive"/>
                                </c:if>
                                <c:if test="${assetAvailabilitySummary.optedOut.deviceCount > 0}">
                                    <cm:dropdownOption key="yukon.web.modules.dr.assetDetails.status.OPTED_OUT" href="${paoId}/aa/download/opted_out"/>
                                </c:if>
                                <c:if test="${unavailableSize > 0}">
                                    <cm:dropdownOption key="yukon.web.modules.dr.assetDetails.status.UNAVAILABLE" href="${paoId}/aa/download/unavailable"/>
                                </c:if>
                                <li class="divider"></li>
                                <cm:dropdownOption label="All" href="${paoId}/aa/download/all"/>
                            </cm:dropdown>
                        </c:if>
                        <c:if test="${allowPing}">
                            <c:choose>
                                <c:when test="${unavailableSize > maxPingableDevices}">
                                    <cti:button id="noPingButton" nameKey="noPingDevices" disabled="true" icon="icon-ping"/>
                                </c:when>
                                <c:when test="${unavailableSize > 0}">
                                    <cti:button id="pingButton" nameKey="pingDevices" busy="true" icon="icon-ping"/>
                                </c:when>
                            </c:choose>
                        </c:if>
                        <span id="pingResults" style="display:none">
                            <tags:updateableProgressBar 
                                countKey="ASSET_AVAILABILITY_READ/${paoId}/SUCCESS_COUNT"
                                totalCountKey="ASSET_AVAILABILITY_READ/${paoId}/TOTAL_COUNT"
                                failureCountKey="ASSET_AVAILABILITY_READ/${paoId}/FAILED_COUNT" 
                                hideCount="true" hidePercent="false"
                                completionCallback="yukon.dr.assetDetails.unbusyPingButton" />
                        </span>
                    </div>
                </div>
            </div>
        </cti:msgScope>
    </c:otherwise>
</c:choose>