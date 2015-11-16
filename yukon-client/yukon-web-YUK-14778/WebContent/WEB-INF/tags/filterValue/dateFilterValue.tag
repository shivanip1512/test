<%@ attribute name="eventLogFilter" required="true" type="com.cannontech.common.events.model.EventLogFilter" %>
<%@ attribute name="count" required="true" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<tags:nameValue2 nameKey=".dateFilter" label="${eventLogFilter.key}">
    <dt:dateRange startPath="eventLogFilters[${count-1}].filterValue.startDate"
    			  endPath="eventLogFilters[${count-1}].filterValue.stopDate" />
</tags:nameValue2>