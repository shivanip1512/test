<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div class="js-trends-widget-container js-block-this">
    <cti:uniqueIdentifier var="id" />
    <div class="label-json dn">${fn:escapeXml(labels)}</div>
    <cti:msg key="yukon.web.widgets.nextRefresh" var="nextRefreshLbl"/>
    <input type="hidden" class="js-tooltip-template" value="${nextRefreshLbl}"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey="yukon.web.widgets.trendsWidget.trendId">
            <tags:pickerDialog id="trendPicker_${id}"
                               type="trendPicker"
                               linkType="selection"
                               multiSelectMode="false"
                               selectionProperty="Name"
                               allowEmptySelection="false"
                               destinationFieldName="js-trend-id"
                               endEvent="yukon:trends:selection"
                               initialId="${trendId}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div class="js-trends-chart" data-trend="${trendId}"></div>
    <cti:url value="/tools/trends/${trendId}" var="trendsUrl"/>
    <a href="${trendsUrl}" class="js-details-link" target="_blank"><i:inline key="yukon.common.details"/></a>
    <span class="fr">
        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedLbl"/>
        <span class="fl js-last-updated" style="font-size:11px" title="${lastUpdatedLbl}"></span>
        <cti:button renderMode="image" icon="icon-arrow-refresh" classes="js-trends-update"/>
    </span>
</div>