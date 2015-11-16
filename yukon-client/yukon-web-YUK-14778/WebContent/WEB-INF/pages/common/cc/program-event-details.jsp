<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage title="Event Overview" module="commercialcurtailment_user">
<c:set var="event" value="${eventDetail}"/>
<table class="compact-results-table">
    <thead>
        <tr>
            <th>Event Number</th>
            <th>Start Time</th>
            <th>Duration</th>
            <th>Reason</th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <tr>
            <td>${eventDetail.displayName}</td>
            <td><cti:formatDate type="DATEHM" value="${eventDetail.startTime}"/></td>
            <td>${eventDetail.duration}</td>
            <td>${eventDetail.reason}</td>
        </tr>
    </tbody>
</table>
</cti:standardPage>
