<%@ attribute name="eventLogFilter" required="true" type="com.cannontech.common.events.model.EventLogFilter" %>
<%@ attribute name="count" required="true" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<tags:nameValue2 nameKey=".stringFilter" label="${eventLogFilter.key}">
    <tags:input path="eventLogFilters[${count-1}].filterValue.filterValue" />
</tags:nameValue2>

