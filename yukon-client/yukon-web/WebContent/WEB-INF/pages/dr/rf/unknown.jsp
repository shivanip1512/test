<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
    <input type="hidden" class="f-title" value="${fn:escapeXml(title)}">
    <div class="column-12-12 clearfix stacked">
        <div class="column one">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey="yukon.web.modules.operator.hardware.assetAvailability.active">${unknownDevices.numActive}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.web.modules.operator.hardware.assetAvailability.inactive">${unknownDevices.numInactive}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.web.modules.operator.hardware.assetAvailability.unavailable">${unknownDevices.numUnavailable}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNREPORTED_NEW">${unknownDevices.numUnreportedNew}</tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNREPORTED_OLD">${unknownDevices.numUnreportedOld}</tags:nameValue2>
            </tags:nameValueContainer2>
        </div>
        <div class="column two nogutter">
            <div class="f-pie-chart flotchart" style="min-height: 100px;"></div>
        </div>
    </div>
    
    <div data-reloadable>
        <%@ include file="page.jsp" %>
    </div>
    
    <div class="action-area">
        <cti:button nameKey="close" classes="f-close"/>
        <cti:button nameKey="download" icon="icon-page-white-excel" href="/dr/rf/details/unknown/${test}/download" classes="right"/>
        <cti:button nameKey="inventoryAction" icon="icon-cog-go" href="/dr/rf/details/unknown/${test}/inventoryAction" busy="true" classes="middle"/>
        <cti:button nameKey="collectionAction" icon="icon-cog-go" href="/dr/rf/details/unknown/${test}/collectionAction" busy="true" classes="left"/>
    </div>
</cti:msgScope>