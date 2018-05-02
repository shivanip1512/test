<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>

<flot:trend title="${title}" pointIds="${pointId}" startDate="${startDateMillis}" endDate="${endDateMillis}" 
            interval="${interval}" converterType="${converterType}" graphType="${graphType}" ymin="0"/>
