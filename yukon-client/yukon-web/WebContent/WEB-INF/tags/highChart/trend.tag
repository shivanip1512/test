<%@ tag body-content="empty" description="HighChart tag for displaying historical pointId data as a line or bar chart (See HighChartServiceImpl.java:getMeterGraphData)" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="title" required="true"%>
<%@ attribute name="pointIds" required="true" description="Comma seperated list of pointIds"%>
<%@ attribute name="temperaturePointId" type="java.lang.Integer" description="pointId for Primary Weather Location temperature"%>
<%@ attribute name="isTemperatureChecked" type="java.lang.Boolean" description="Checkbox selection (true or false) for temperature option"%>
<%@ attribute name="startDate" required="true"%>
<%@ attribute name="endDate" required="true"%>
<%@ attribute name="interval" required="true" description="Either ChartInterval.java object or the String representation"%>
<%@ attribute name="converterType" required="true"  description="Either ConverterType.java object or the String representation"%>
<%@ attribute name="graphType"  description="Either GraphType.java object or the String representation"%>
<%@ attribute name="reloadInterval" description="The reload interval of the added chart (in seconds)"%>
<%@ attribute name="temperatureChartInterval" description="Chart interval for temperature trend"%>
<%@ attribute name="ymin"%>
<%@ attribute name="ymax"%>
<%@ attribute name="chartWidth" type="java.lang.Integer" required="true" description="Width of the chart."%>
<%@ attribute name="chartHeight" type="java.lang.Integer" required="true" description="Heigth of the chart."%>

<cti:uniqueIdentifier var="uniqueId"/>

<c:url var="chartUrl" scope="page" value="/amr/chart/getChartJson">
    <c:param name="pointIds" value="${pointIds}" />
    <c:param name="temperaturePointId" value="${temperaturePointId}" />
    <c:if test="${empty isTemperatureChecked}">
        <c:set var="isTemperatureChecked" value="false"/>
    </c:if>
    <c:param name="isTemperatureChecked" value="${isTemperatureChecked}" />
    <c:param name="startDate" value="${startDate}" />
    <c:param name="endDate" value="${endDate}" />
    <c:param name="interval" value="${interval}" />
    <c:param name="graphType" value="${pageScope.graphType}" />
    <c:param name="converterType" value="${converterType}" />
    <c:if test="${not empty temperatureChartInterval}">
        <c:param name="temperatureChartInterval" value="${temperatureChartInterval}" />
    </c:if>
    <!-- to set the charts y min/max values -->
    <c:if test="${not empty pageScope.ymax}">
        <c:param name="yMax" value="${pageScope.ymax}" />
    </c:if>
    <c:if test="${not empty pageScope.ymin}">
        <c:param name="yMin" value="${pageScope.ymin}" />
    </c:if>
</c:url>

<div id="js-chart-container-${uniqueId}" class="js-highchart-graph-container"></div>

<c:choose>
    <c:when test="${not empty reloadInterval}">
        <script>
            $(function() {
                var params = {
                    containerIdentifier: "#js-chart-container-${uniqueId}",
                    title: "${pageScope.title}",
                    height: "${chartHeight}",
                    width: "${chartWidth}",
                    reloadInterval: "${pageScope.reloadInterval}",
                    chartUrl: "${pageScope.chartUrl}"
                };
                yukon.highChart.reloadChartAtInterval(params);
            });
        </script>
    </c:when>
    <c:otherwise>
        <script>
            $(function() {
                $.ajax({
                    url: '${pageScope.chartUrl}',
                    dataType : 'json'
                }).done(function (response, textStatus, jqXHR) {
                    var tooltipProperties = {
                        tooltip: {
                            positioner: function(_, _, point) {
                                var chartPosition = this.chart.pointer.getChartPosition();
                                return {
                                    x: chartPosition.left + point.plotX,
                                    y: chartPosition.top + point.plotY + 50
                                }
                            }
                        }
                    };
                    response = $.extend({}, tooltipProperties, response),
                    yukon.highChart.buildChart($("#js-chart-container-${uniqueId}"), response, "${pageScope.title}", "${chartHeight}", "${chartWidth}");
                });
            });
        </script>
    </c:otherwise>
</c:choose>