<%@ tag body-content="empty" description="Tag for adding a flotchart onto the page via json. See FlotChartServiceImpl.java for examples of what this expected JSON should look like"%>

<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>

<%@ include file="defaultTagLibs.jspf" %>
<flot:defaultIncludes/>
<cti:includeScript link="JQUERY_FLOTCHARTS_PIE" />

<%@ attribute name="chartId" %>
<%@ attribute name="classes" %>
<%@ attribute name="data" type="java.util.Map" required="true" description="Expected data is a Map of string to object representing the json data. Specifically {datas: [{label: 'somelabel', data: somedata, tooltip: 'sometooltip'}], options: {nested objects of options}, type: 'line/bar/pie'}. See FlotChartServiceImpl.java for more specific examples"%>

<cti:default var="chartId" value="flotChartId"/>

<div class="flotchart_container ${classes}">
    <%@ include file="defaultElements.jspf" %>
</div>

<script>
jQuery(function() {
    var chartId = '${chartId}';
    var data = ${cti:jsonString(data)};
    yukon.Flot.addChart({
        chartId: chartId,
        type: data.type,
        data: data.datas,
        options: data.options
    });
    yukon.Flot.charts[chartId].methods.plotGraph(chartId);
});
</script>
