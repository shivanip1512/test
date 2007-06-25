<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="submitUrl" value="/spring/macsscheduler/schedules" />

<h2>
    Scheduled Scripts
</h2>
<div id="schedules">
    <table class="resultsTable">
        <tr>
            <th>
                Schedule Name
            </th>
            <th>
                Current State
            </th>
            <th>
                Start Date/Time
            </th>
            <th>
                Stop Date/Time
            </th>
        </tr>
        <c:forEach var="schedule" items="${list}">
            <c:choose>
                <c:when test="${schedule.currentState == 'Disabled'}">
                    <c:set var="color" scope="page">#FF6666</c:set>
                </c:when>
                <c:when test="${schedule.currentState == 'Running'}">
                    <c:set var="color" scope="page">#99FF99</c:set>
                </c:when>
                <c:otherwise>
                    <c:set var="color" scope="page">white</c:set>
                </c:otherwise>
            </c:choose>
            <tr style="background-color: ${color }">
                <td>
                    <c:choose>
                        <c:when
                            test="${editable && schedule.currentState != 'Disabled'}">
                            <a
                                href="${submitUrl}/controlView?id=${schedule.id}">${schedule.scheduleName}</a>
                        </c:when>
                        <c:otherwise>
                        ${schedule.scheduleName}    
                    </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    ${schedule.currentState}
                </td>
                <td>
                    <cti:formatDate value="${schedule.nextRunTime}"
                        type="both" var="formattedStartTime" />
                    ${formattedStartTime}
                </td>
                <td>
                    <cti:formatDate value="${schedule.nextStopTime}"
                        type="both" var="formattedStopTime" />
                    ${formattedStopTime}
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

