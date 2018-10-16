<%@ tag body-content="empty" description="FlotChart tag for displaying historical pointId data as a line or bar chart (See FlotChartServiceImpl.java:getMeterGraphData)" %>

<%@ attribute name="title" required="true"%>
<%@ attribute name="pointIds" required="true" description="Comma seperated list of pointIds"%>
<%@ attribute name="primaryWeatherLocationId" description="Primary Weather Location Id"%>
<%@ attribute name="isTemperatureChecked" description="Checkbox selection (true or false) for temperature option"%>
<%@ attribute name="startDate" required="true"%>
<%@ attribute name="endDate" required="true"%>
<%@ attribute name="interval" required="true" description="Either ChartInterval.java object or the String representation"%>
<%@ attribute name="converterType" required="true"  description="Either ConverterType.java object or the String representation"%>
<%@ attribute name="graphType"  description="Either GraphType.java object or the String representation"%>
<%@ attribute name="reloadInterval" description="The reload interval of the added chart (in seconds)"%>
<%@ attribute name="ymin"%>
<%@ attribute name="ymax"%>

<%@ include file="defaultTagLibs.jspf" %>

<cti:uniqueIdentifier var="chartId" prefix="flotChart_"/>

<div class="flotchart_container">
    <%@ include file="defaultElements.jspf" %>
</div>

<c:url var="chartUrl" scope="page" value="/amr/chart/chart">
    <c:param name="pointIds" value="${pointIds}" />
    <c:param name="primaryWeatherLocationId" value="${primaryWeatherLocationId}" />
    <c:if test="${empty isTemperatureChecked}">
        <c:set var="isTemperatureChecked" value="false"/>
    </c:if>
    <c:param name="isTemperatureChecked" value="${isTemperatureChecked}" />
    <c:param name="startDate" value="${startDate}" />
    <c:param name="endDate" value="${endDate}" />
    <c:param name="interval" value="${interval}" />
    <c:param name="graphType" value="${pageScope.graphType}" />
    <c:param name="converterType" value="${converterType}" />
    <!-- to set the charts y min/max values -->
    <c:if test="${not empty pageScope.ymax}">
        <c:param name="yMax" value="${pageScope.ymax}" />
    </c:if>
    <c:if test="${not empty pageScope.ymin}">
        <c:param name="yMin" value="${pageScope.ymin}" />
    </c:if>
    
</c:url>

<c:choose>
    <c:when test="${not empty reloadInterval}">
        <script>
           $(function() {
               yukon.flot.reloadChartOnInterval({chartId: '${chartId}',
                                                 dataUrl: '${chartUrl}',
                                                 reloadInterval: ${reloadInterval} * 1000});
           });
        </script>
    </c:when>
    <c:otherwise>
        <script>
            $(function() {
                var chartId = '${chartId}';
                $.ajax({
                    url: '${chartUrl}',
                    dataType : 'json'
                }).done(function (data, textStatus, jqXHR) {
                    yukon.flot.addChart({
                        chartId: chartId,
                        type: data.type,
                        data: data.datas,
                        options: data.options
                    });
                    yukon.flot.charts[chartId].methods.plotGraph(chartId);
                });
            });
        </script>
    </c:otherwise>
</c:choose>