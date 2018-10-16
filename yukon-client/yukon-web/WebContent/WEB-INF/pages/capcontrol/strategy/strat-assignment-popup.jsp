<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="modules.capcontrol">

<cti:url var="url" value="/capcontrol/strategy-assignment/${assignment.paoId}"/>
<form:form modelAttribute="assignment" method="post" action="${url}">
<cti:csrfToken/>
<form:hidden path="paoId"/>

<%-- SEASON SCHEDULE --%>
<tags:nameValueContainer2 tableClass="stacked with-form-controls">
    <tags:nameValue2 nameKey=".schedule.season">
        <form:select path="seasonSchedule" cssClass="js-season-schedule-select">
            <c:forEach var="schedule" items="${seasonSchedules}">
                <form:option value="${schedule.scheduleId}" label="${schedule.scheduleName}"/>
            </c:forEach>
        </form:select>
    </tags:nameValue2>
</tags:nameValueContainer2>
<table class="full-width dashed stacked with-form-controls js-seasons-table">
    <thead>
        <tr>
            <th><i:inline key=".season"/></th>
            <th><i:inline key=".strategy"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="sa" items="${assignment.seasonAssignments}" varStatus="status">
            <tr>
                <td>${sa.seasonName}</td>
                <td>
                    <form:hidden path="seasonAssignments[${status.index}].seasonName"/>
                    <form:select path="seasonAssignments[${status.index}].strategyId">
                        <form:option value="-1"><cti:msg2 key="yukon.common.none.choice"/></form:option>
                        <c:forEach var="strategy" items="${strategies}">
                            <form:option value="${strategy.id}" label="${strategy.name}"/>
                        </c:forEach>
                    </form:select>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<%-- HOLIDAY SCHEDULE --%>
<tags:nameValueContainer2 tableClass="with-form-controls">
    <tags:nameValue2 nameKey=".schedule.holiday">
        <form:select path="holidaySchedule" cssClass="js-holiday-schedule-select">
            <c:forEach var="schedule" items="${holidaySchedules}">
                <form:option value="${schedule.holidayScheduleId}" label="${schedule.holidayScheduleName}"/>
            </c:forEach>
        </form:select>
    </tags:nameValue2>
    <c:set var="clazz" value="${assignment.holidaySchedule == -1 ? 'dn' : ''}"/>
    <tags:nameValue2 nameKey=".strategy" rowClass="js-holiday-strat ${clazz}">
        <form:select path="holidayStrategy">
            <c:forEach var="strategy" items="${strategies}">
                <form:option value="${strategy.id}" label="${strategy.name}"/>
            </c:forEach>
        </form:select>
    </tags:nameValue2>
</tags:nameValueContainer2>

</form:form>

</cti:msgScope>