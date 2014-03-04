<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>

<script type="text/javascript"> 
jQuery(function () {
    jQuery("#trendWidget").find(".icon-help").click(function() {
        jQuery("#trendWidgetWhatsThisText")
            .html('<cti:msg2 javaScriptEscape="true" key="${attributeGraphType.description}"/>');
    });
    // init for a specific DOM element and its children
    yukon.ui.dateTimePickers.ancestorInit('#optionalDateFields');
});
</script>

<c:choose>
    
    <c:when test="${attributeGraphType != null}">

        <%-- THE CHART --%>
        <flot:trend title="${title}" pointIds="${pointId}"
            startDate="${graphStartMillis}" endDate="${graphStopMillis}"
            interval="${interval}"
            converterType="${attributeGraphType.converterType}"
            graphType="${graphType}"
            ymin="0"/>

        <table class="compact-results-table trend-settings no-stripes">
        
            <%-- ATTRIBUTES GRAPH TYPES --%>
            <tr>
                <td><i:inline key=".graphType"/></td>
                
                <td>
                    <c:forEach var="agt" items="${availableAttributeGraphs}" varStatus="status">
                        <c:if test="${!status.first}">|</c:if>
                        <cti:msg2 var="graphTypeLabel" key="${agt.label}"/>
                        <tags:widgetLink method="render?attribute=${agt.attribute}" title="${graphTypeLabel}" selected="${agt == attributeGraphType}"><i:inline key="${agt.label}"/></tags:widgetLink>
                    </c:forEach>
                </td>
            </tr>
        
            <%-- PERIOD --%>
            <tr>
                <td><i:inline key=".timePeriod"/></td>
                <cti:msg2 var="prev24Hours" key=".prev24HourData"/>
                <cti:msg2 var="prevWeeksData" key=".prevWeeksData"/>
                <cti:msg2 var="prevMonthsData" key=".prevMonthsData"/>
                <cti:msg2 var="prev3MonthsData" key=".prev3MonthsData"/>
                <cti:msg2 var="prevYearsData" key=".prevYearsData"/>
                <cti:msg2 var="customDateRange" key=".customDateRange"/>
                <cti:msg2 var="oneDay" key=".oneDay"/>
                <cti:msg2 var="oneWeek" key=".oneWeek"/>
                <cti:msg2 var="oneMonth" key=".oneMonth"/>
                <cti:msg2 var="threeMonths" key=".threeMonths"/>
                <cti:msg2 var="oneYear" key=".oneYear"/>
                <cti:msg2 var="custom" key=".custom"/>
            
                <td>
                    <tags:widgetLink method="render?period=DAY" title="${prev24Hours}" selected="${period == 'DAY'}">${oneDay}</tags:widgetLink>
                    |
                    <tags:widgetLink method="render?period=WEEK" title="${prevWeeksData}" selected="${period == 'WEEK'}">${oneWeek}</tags:widgetLink>
                    |
                    <tags:widgetLink method="render?period=MONTH" title="${prevMonthsData}" selected="${period == 'MONTH'}">${oneMonth}</tags:widgetLink>
                    |
                    <tags:widgetLink method="render?period=THREEMONTH" title="${prev3MonthsData}" selected="${period == 'THREEMONTH'}">${threeMonths}</tags:widgetLink>
                    |
                    <tags:widgetLink method="render?period=YEAR" title="${prevYearsData}" selected="${period == 'YEAR'}">${oneYear}</tags:widgetLink>
                    |
                    <tags:widgetLink method="render?period=NOPERIOD" title="${customDateRange}" selected="${period == 'NOPERIOD'}">${custom}</tags:widgetLink>
                </td>
                
            </tr>
        
            <%-- CUSTOM DATES --%>
            <c:choose>
            
                <c:when test="${period == 'NOPERIOD'}">
                    <tr id="optionalDateFields" name="optionalDateFields">
                        <td colspan="2">
                            <tags:widgetActionRefresh nameKey="reloadUsingCustomDates" method="render" icon="icon-arrow-refresh" renderMode="buttonImage" classes="fr"/>
                            <div class="fr"><dt:dateRange startName="startDateParam" startValue="${startDate}" endName="stopDateParam" endValue="${stopDate}"/></div>
                        </td>
                    </tr>
                </c:when>

                <c:otherwise>
                    <span class="dn">
                        <dt:date name="startDateParam" value="${startDate}"/>
                        <dt:date name="stopDateParam" value="${stopDate}"/>
                    </span>
                </c:otherwise>
                
            </c:choose>
        
        
            <%-- CHART STYLE --%>
            <tr>
                <td><i:inline key=".chartStyle"/></td>
            
                <td class="last">
                    <cti:msg2 var="lineGraph" key=".lineGraph"/>
                    <cti:msg2 var="line" key=".line"/>
                    <cti:msg2 var="barGraph" key=".barGraph"/>
                    <cti:msg2 var="bar" key=".bar"/>
                    <tags:widgetLink method="render?graphType=LINE" title="${lineGraph}" selected="${graphType == 'LINE'}"><i:inline key=".line"/></tags:widgetLink>
                    |
                    <tags:widgetLink method="render?graphType=COLUMN" title="${barGraph}" selected="${graphType == 'COLUMN'}"><i:inline key=".bar"/></tags:widgetLink>
                </td>
            </tr>
        
            <%-- TABULAR DATA REPROTS --%>
            <tr>
                <td>
                    <c:choose>
                        <c:when test="${attributeGraphType.attribute == 'USAGE' || attributeGraphType.attribute == 'USAGE_WATER'}">
                            <i:inline key=".archivedUsageData"/>
                        </c:when>
                        <c:otherwise>
                            <i:inline key=".tabularUsageData"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="<cti:url value="/amr/reports/${tabularDataViewer}?def=rawPointHistoryDefinition&pointId=${pointId}&startDate=${startDateMillis}&stopDate=${stopDateMillis}" />"><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></a>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="csvView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="pdfView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                </td>
            </tr>
            
            <c:if test="${attributeGraphType.attribute == 'USAGE' || attributeGraphType.attribute == 'USAGE_WATER'}">
                <tr>
                    <td><b><i:inline key=".normalizedUsageData"/></b></td>
                    
                    <td>
                        <a href="<cti:url value="/amr/reports/${tabularDataViewer}?def=normalizedUsageDefinition&pointId=${pointId}&startDate=${startDateMillis}&stopDate=${stopDateMillis}&attribute=${attributeGraphType.attribute}" />"><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></a>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="normalizedUsageDefinition" viewType="csvView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}" attribute="${attributeGraphType.attribute}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                        |
                        <cti:simpleReportLinkFromNameTag definitionName="normalizedUsageDefinition" viewType="pdfView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}" attribute="${attributeGraphType.attribute}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                    </td>
                </tr>
            </c:if>
        
        </table>
        
    </c:when>
        
    <c:otherwise>
        <i:inline key=".noTrendsAvailable"/>
    </c:otherwise>

</c:choose>