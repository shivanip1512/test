<%@ attribute name="eventLogFilter" required="true" type="com.cannontech.common.events.model.EventLogFilter" %>
<%@ attribute name="count" required="true" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<tags:nameValue2 nameKey=".dateFilter" label="${eventLogFilter.key}">

    <tags:dateInputCalendar fieldName="eventLogFilters[${count-1}].filterValue.startDate" springInput="true" showErrorOnNextLine="false"/>
    <tags:dateInputCalendar fieldName="eventLogFilters[${count-1}].filterValue.stopDate" springInput="true" />

</tags:nameValue2>