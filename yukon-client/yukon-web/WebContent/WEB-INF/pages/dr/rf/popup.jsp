<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
    <input type="hidden" class="f-title" value="${fn:escapeXml(title)}">
    
    <c:if test="${unknown}">
        <div class="column-12-12 clearfix stacked">
            <div class="column one">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.AVAILABLE">${unknownStats.numAvailable}</tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNAVAILABLE">${unknownStats.numUnavailable}</tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNREPORTED_NEW">${unknownStats.numUnreportedNew}</tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.UNREPORTED_OLD">${unknownStats.numUnreportedOld}</tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <div class="f-pie-chart flotchart" style="min-height: 100px;"></div>
            </div>
        </div>
    </c:if>
    
    <div data-reloadable>
        <%@ include file="table.jsp" %>
    </div>
    
    <div class="action-area">
        <cti:button nameKey="close" classes="f-close"/>
        <cti:button nameKey="download" icon="icon-page-white-excel" href="/dr/rf/details/${type}/${test}/download" classes="right"/>
        <cti:button nameKey="inventoryAction" icon="icon-cog-go" href="/dr/rf/details/${type}/${test}/inventoryAction" busy="true" classes="middle"/>
        <cti:button nameKey="collectionAction" icon="icon-cog-go" href="/dr/rf/details/${type}/${test}/collectionAction" busy="true" classes="left"/>
    </div>
</cti:msgScope>