<%@ tag body-content="empty" description="Tag for adding a flotchart onto the page via ajax. See FlotChartServiceImpl.java for examples of what this expected JSON should look like"%>

<%@ include file="defaultTagLibs.jspf" %>
<%@ include file="defaultIncludes.jspf" %>
<cti:includeScript link="JQUERY_FLOTCHARTS_PIE" />

<%@ attribute name="chartId"%>
<%@ attribute name="url" required="true" description="Expected return data is a JSONObject. Specifically {datas: [{label: 'somelabel', data: somedata, tooltip: 'sometooltip'}], options: {nested objects of options}, type: 'line/bar/pie'}. See FlotChartServiceImpl.java for more specific examples"%>

<cti:default var="chartId" value="flotChartId"/>

<div class="flotchart_container">
    <%@ include file="defaultElements.jspf" %>
</div>

<div class="flotchart_container">
    <div id="${chartId}" class="flotchart"></div>
</div>
<script>
jQuery(function() {
    var chartId = '${chartId}';
    jQuery.ajax({
        url: '${url}',
        success: function(data) {
            Yukon.Flot.addChart({
                chartId: chartId,
                type: data.type,
                data: data.datas
            });
            Yukon.Flot.charts[chartId].methods.plotGraph(chartId);
        }
    });
});
</script>
