<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.history">
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">

<div class="column-24">
    <div class="column one nogutter">
        <h2>${program.name} / ${program.programType.name}</h2>
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th>Event #</th>
                    <th>State</th>
                    <th>Start Time</th>
                    <th>Duration</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="event" varStatus="oneEvent" items="${eventHistory}">
                <tr>
                    <cti:url value="/dr/cc/program/${program.id}/event/${event.id}/detail" var="eventDetailUrl"/>
                    <td><a href="${eventDetailUrl}">${event.displayName}</a></td>
                    <td>${event.stateDescription}</td>
                    <td><cti:formatDate value="${event.startTime}" type="DATEHM"/></td>
                    <td>${event.duration}</td>
                    <td>Delete button</td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

</cti:msgScope>
</cti:standardPage>