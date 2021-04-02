<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="chartId" required="true"%>
<%@ attribute name="title" required="true"%>
<%@ attribute name="jsonDataAndOptions" required="true" type="java.util.Map" description="Map of JSON data. See HighChartServiceImpl.java for more specific examples"%>

<div id="js-chart-container-${chartId}" class="js-highchart-graph-container ov"></div>

<script>
$(function() {    
    var jsonData = ${cti:jsonString(jsonDataAndOptions)};
    yukon.highChart.buildChart($("#js-chart-container-${chartId}"), jsonData, "${title}", "350", "550");
});
</script>
