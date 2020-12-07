<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings">

<script>
    yukon.historical.readings.setDownloadUrl(${pointId});
</script>

<cti:msg2 key=".download.tooltip" var="download"/>
<cti:msg2 key=".trend.tooltip" var="trendToolTip"/>
<input class="js-popup-title" type="hidden" value="${fn:escapeXml(title)}"> 

<div class="form-control clearfix">
    <c:if test="${points.size() == maxRowsDisplay}">
        <span class="detail">${resultLimit}</span>
    </c:if>
    <div class="fr">
        <tags:nameValueContainer2>
            <select id="duration_${pointId}" name="duration" data-point-id="${pointId}">
                <c:forEach var="duration" items="${duration}">
                    <option value="${duration.key}" data-download-url="${duration.value}">
                        <i:inline key=".${duration.key}"/>
                    </option>
                </c:forEach>
            </select>
            <c:choose>
                <c:when test="${showTrend}">
                    <div class="button-group">
                        <cti:button id="download_${pointId}" title = "${download}" renderMode="buttonImage" 
                                    icon="icon-page-white-excel" data-point-id="${pointId}"/>
                        <cti:button id="trend_${pointId}" renderMode="buttonImage" icon="icon-chart-line" 
                                    data-point-id="${pointId}" title="${trendToolTip}"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <cti:button id="download_${pointId}" title = "${download}" renderMode="buttonImage" 
                                icon="icon-page-white-excel" data-point-id="${pointId}" classes="fr left"/>
                </c:otherwise>
            </c:choose>
        </tags:nameValueContainer2>
    </div>
</div>

<div class="js-trend-container_${pointId} dn js-block-this">
    <br>
    <br>
    <div class="filter-container">
        <span class="fr cp close js-close-trend-btn"><cti:icon icon="icon-close-x"/></span>
        <div id="trend-graph_${pointId}"></div>
    </div>
    <br>
    <br>
</div>

<c:choose>
    <c:when test="${empty points}">
        <div class="empty-list"><i:inline key=".notFound"/></div>
    </c:when>
    <c:otherwise>
        <cti:url value="/meter/historicalReadings/values" var="viewUrl">
            <cti:param name="pointId" value="${pointId}"/>
        </cti:url>
        <div data-url="${viewUrl}" class="js-values-table-${pointId}"><%@ include file="values.jsp" %></div>
    </c:otherwise>
</c:choose>
</cti:msgScope>
