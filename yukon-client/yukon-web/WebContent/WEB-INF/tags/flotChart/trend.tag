<%@ tag body-content="empty" description="FlotChart tag for displaying historical pointId data as a line or bar chart (See FlotChartServiceImpl.java:getMeterGraphData)"%>

<%@ include file="defaultTagLibs.jspf" %>
<%@ include file="defaultIncludes.jspf" %>

<%@ attribute name="title" required="true"%>
<%@ attribute name="pointIds" required="true" description="Comma seperated list of pointIds"%>
<%@ attribute name="startDate" required="true"%>
<%@ attribute name="endDate" required="true"%>
<%@ attribute name="interval" required="true" description="Either ChartInterval.java object or the String representation"%>
<%@ attribute name="converterType" required="true"  description="Either ConverterType.java object or the String representation"%>
<%@ attribute name="graphType"  description="Either GraphType.java object or the String representation"%>
<%@ attribute name="reloadInterval"%>
<%@ attribute name="min"%>
<%@ attribute name="max"%>

<cti:uniqueIdentifier var="chartId" prefix="flotChart_"/>

<div class="flotchart_container">
    <%@ include file="defaultElements.jspf" %>
</div>

<c:url var="chartUrl" scope="page" value="/amr/chart/chart?title=${title}">
<%-- 	<c:param name="title" value="${title}" /> --%>
	<c:param name="pointIds" value="${pointIds}" />
	<c:param name="startDate" value="${startDate}" />
	<c:param name="endDate" value="${endDate}" />
	<c:param name="interval" value="${interval}" />
	<c:param name="graphType" value="${pageScope.graphType}" />
	<c:param name="converterType" value="${converterType}" />
    <c:if test="${not empty pageScope.reloadInterval}">
        <c:param name="reloadInterval" value="${pageScope.reloadInterval}" />
    </c:if>
    <!-- to set the charts y min/max values -->
    <c:if test="${not empty pageScope.min && not empty pageScope.max}">
        <c:param name="yMin" value="${pageScope.min}" />
        <c:param name="yMax" value="${pageScope.max}" />
    </c:if>
</c:url>

<script>
jQuery(function() {
    var chartId = '${chartId}';
    jQuery.ajax({
        url: '${chartUrl}',
        success: function(data) {
            jQuery(document.getElementById(chartId)).closest(".flotchart_container").find(".xaxis.axis_label").text(data.title);
            jQuery(document.getElementById(chartId)).closest(".flotchart_container").find(".yaxis.axis_label").text(data.ylabel);
            Yukon.Flot.addChart({
                chartId: chartId,
                type: data.type,
                data: data.datas,
                options: data.options
            });
		    /* chart it!! */
		    Yukon.Flot.charts[chartId].methods.plotGraph(chartId);
        }
    });
});
</script>
