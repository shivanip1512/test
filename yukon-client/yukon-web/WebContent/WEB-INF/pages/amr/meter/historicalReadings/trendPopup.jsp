<%@ taglib prefix="highChart" tagdir="/WEB-INF/tags/highChart" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<highChart:trend title="${title}" pointIds="${pointId}" startDate="${startDateMillis}" endDate="${endDateMillis}" 
                 interval="${interval}" converterType="${converterType}" graphType="${graphType}" ymin="0"
                 chartWidth="720" chartHeight="300"/>
                 
<cti:includeScript link="/resources/js/pages/yukon.highChart.js"/>
