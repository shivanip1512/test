<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="chartId" required="true"%>
<%@ attribute name="title" required="true"%>
<%@ attribute name="feederList" required="true" type="java.util.List" description="List of Feeders to show in filter dropdown"%>
<%@ attribute name="jsonDataAndOptions" required="true" type="java.util.Map" description="Map of JSON data. See HighChartServiceImpl.java for more specific examples"%>

<div id="js-chart-container-${chartId}" class="js-highchart-graph-container ov"></div>
<div class="ma" style="width: 400px">
    <cti:msg2 var="allFeedersLabel" key="yukon.web.modules.capcontrol.ivvc.zoneDetail.allFeeders"/>
    <i:inline key="yukon.web.modules.capcontrol.ivvc.zoneDetail.filterByFeeder"/>:
    <select class="js-feeder-filter" multiple="multiple" data-placeholder="${allFeedersLabel}">
        <c:forEach var="feeder" items="${feederList}">
            <option value="${feeder.ccId}">${feeder.ccName}</option>
        </c:forEach>
    </select>
</div>

<script>
$(function() {    
    var jsonData = ${cti:jsonString(jsonDataAndOptions)};
    yukon.highChart.buildChart($("#js-chart-container-${chartId}"), jsonData, "${title}", "350", "550");
});
</script>
