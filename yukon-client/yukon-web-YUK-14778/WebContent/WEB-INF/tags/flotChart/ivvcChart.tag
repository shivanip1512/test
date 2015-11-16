<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>

<%@ include file="defaultTagLibs.jspf" %>
<%@ include file="defaultAttributes.jspf" %>

<flot:defaultIncludes/>

<div class="flotchart_container">
    <%@ include file="defaultElements.jspf" %>
   
</div>
<script src="https://code.highcharts.com/stock/highstock.js"></script>
<script src="https://code.highcharts.com/stock/modules/exporting.js"></script>
<cti:includeScript link="/resources/js/pages/yukon.voltvar.ivvc.js"/>
<script type="text/javascript">
$(function () {
        var chartId = ${subBusId};
        console.debug("chartId:" + chartId);
        if (chartId) {
            yukon.voltvar.ivvc.init(chartId);
        }
    }
);

</script>
