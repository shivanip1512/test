<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="dr" page="cc.history">
<cti:msgScope paths=",yukon.web.modules.commercialcurtailment.ccurtSetup">

<div class="column-24">
    <div class="column one nogutter">
        <h2>${program.name} - ${program.programType.name}</h2>
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th><i:inline key=".eventNumber"/></th>
                    <th><i:inline key=".state"/></th>
                    <th><i:inline key=".startTime"/></th>
                    <th><i:inline key=".duration"/></th>
                    <th><i:inline key=".action"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="event" items="${eventHistory}">
                <tr>
                    <cti:url value="/dr/cc/program/${program.id}/event/${event.id}/detail" var="eventDetailUrl"/>
                    <td><a href="${eventDetailUrl}">${fn:escapeXml(event.displayName)}</a></td>
                    <td>${event.stateDescription}</td>
                    <td><cti:formatDate value="${event.startTime}" type="DATEHM"/></td>
                    <td>${event.duration}</td>
                    <td>
                        <cti:url var="deleteUrl" value="/dr/cc/program/${program.id}/event/${event.id}"/>
                        <form:form action="${deleteUrl}" method="DELETE">
                            <cti:csrfToken/>
                            <cti:button type="submit" icon="icon-delete" renderMode="image" nameKey="eventDelete" id="delete-event"/>
                            <d:confirm on="#delete-event" nameKey="confirmDelete" argument="${event.displayName}" />
                        </form:form>
                    </td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

</cti:msgScope>
</cti:standardPage>