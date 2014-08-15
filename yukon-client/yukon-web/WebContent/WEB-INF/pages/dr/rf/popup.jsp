<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
    <input type="hidden" class="js-title" value="${fn:escapeXml(title)}">
    
    <c:if test="${unknown}">
        <div class="column-12-12 clearfix stacked">
            <div class="column one">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.COMMUNICATING">
                        ${unknownStats.numCommunicating}
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.NOT_COMMUNICATING">
                        ${unknownStats.numNotCommunicating}
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="yukon.common.rfPerformance.unknownStatus.NEW_INSTALL_NOT_COMMUNICATING">
                        ${unknownStats.numNewInstallNotCommunicating}
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two nogutter">
                <div class="js-pie-chart flotchart" style="min-height: 100px;"></div>
            </div>
        </div>
    </c:if>
    <cti:url var="url" value="/dr/rf/details/${type}/${test}/page"/>
    <div data-url="${url}">
        <%@ include file="table.jsp" %>
    </div>
    <cti:url var="download" value="/dr/rf/details/${type}/${test}/download"/>
    <cti:url var="inventoryAction" value="/dr/rf/details/${type}/${test}/inventoryAction"/>
    <cti:url var="collectionAction" value="/dr/rf/details/${type}/${test}/collectionAction"/>
    <div class="action-area">
        <cti:button nameKey="close" classes="js-close"/>
        <cti:button nameKey="download" icon="icon-page-white-excel" href="${download}" classes="right"/>
        <cti:button nameKey="inventoryAction" icon="icon-cog-go" href="${inventoryAction}" busy="true" classes="middle"/>
        <cti:button nameKey="collectionAction" icon="icon-cog-go" href="${collectionAction}" busy="true" classes="left"/>
    </div>
</cti:msgScope>