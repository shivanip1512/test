<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:url var="controlUrl" value="/spring/macsscheduler/schedules/controlView"/>
<c:url var="toggleUrl" value="/spring/macsscheduler/schedules/toggleUrl"/>
<c:url var="viewUrl" value="/spring/macsscheduler/schedules/view" />
<c:url var="toggleUrl" value="/spring/macsscheduler/schedules/toggleState" />

    <h2>Scheduled Scripts</h2>
    <div id="schedules" style="margin-left: 5%; margin-right: 5%;">
        <table class="resultsTable">
        <tr>
            <th id="Schedule Name">
                <form id="form_scheduleName" action="${viewUrl}" style="margin: 0px; padding: 0px;"
                    method="POST">
                    <a
                        href="javascript:document.getElementById('form_scheduleName').submit();">Schedule
                        Name</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Schedule Name'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Schedule Name" />
                </form>
            </th>
            <th id="Current State">
                <form id="form_currentState" action="${viewUrl}" style="margin: 0px; padding: 0px;"
                    method="POST">
                    <a
                        href="javascript:document.getElementById('form_currentState').submit();">Current
                        State</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Current State'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Current State" />
                </form>
            </th>
            <th id="Start Date/Time">
                <form id="form_startDate" action="${viewUrl}" style="margin: 0px; padding: 0px;"
                    method="POST">
                    <a
                        href="javascript:document.getElementById('form_startDate').submit();">Start
                        Date/Time</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Start Date/Time'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Start Date/Time" />
                </form>
            </th>
            <th id="Stop Date/Time">
                <form id="form_stopDate" action="${viewUrl}" style="margin: 0px; padding: 0px;"
                    method="POST">
                    <a
                        href="javascript:document.getElementById('form_stopDate').submit();">Stop
                        Date/Time</a>
                    <c:choose>
                        <c:when test="${sortBy == 'Stop Date/Time'}">
                            <c:choose>
                                <c:when test="${descending}">
                                    <span title="Sorted descending">&#9660;</span>
                                    <input type="hidden"
                                        name="descending" value="false" />
                                </c:when>
                                <c:otherwise>
                                    <span title="Sorted ascending">&#9650;</span>
                                    <input type="hidden"
                                        name="descending" value="true" />
                                </c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="descending"
                                value="false" />
                        </c:otherwise>
                    </c:choose>
                    <input type="hidden" name="sortBy"
                        value="Stop Date/Time" />
                </form>
            </th>
            <th id="Disable"></th>
        </tr>
        <c:forEach var="schedule" items="${list}">
                <c:choose>
                    <c:when
                        test="${schedule.webCurrentState == 'Pending'}">
                        <c:set var="color" scope="page">#FFFF33</c:set>
                    </c:when>
                    <c:when
                        test="${schedule.currentState == 'Disabled'}">
                        <c:set var="color" scope="page">#FF3333</c:set>
                    </c:when>
                    <c:when test="${schedule.currentState == 'Running'}">
                        <c:set var="color" scope="page">#33FF00</c:set>
                    </c:when>
                    <c:otherwise>
                        <c:set var="color" scope="page">white</c:set>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when
                        test="${schedule.webCurrentState == 'Pending'}">
                        <tr style="background-color: ${color }"
                            disabled="true">
                    </c:when>
                    <c:otherwise>
                        <tr style="background-color: ${color }">
                    </c:otherwise>
                </c:choose>

                <td>
                    <c:choose>
                        <c:when test="${editable && schedule.currentState != 'Disabled' && schedule.webCurrentState != 'Pending'}">
                            <form id="controlform_${schedule.id}" action="${controlUrl}" method="POST" style="margin: 0px; padding: 0px;">
                                <a href="javascript:document.getElementById('controlform_${schedule.id}').submit();">${schedule.scheduleName}</a>
                                <input type="hidden" name="id" value="${schedule.id}"/>
                                <input type="hidden" name="sortBy" value="${sortBy}"/>
                                <input type="hidden" name="descending" value="${descending}"/>
                            </form>
                        </c:when>
                        <c:otherwise>
                        ${schedule.scheduleName}    
                    </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when
                            test="${schedule.webCurrentState == 'Pending'}">
                        Updating...
                    </c:when>
                        <c:otherwise>
                        ${schedule.currentState}                        
                    </c:otherwise>
                    </c:choose>
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
                <td style="background-color: white">
                    <c:if test="${editable && schedule.webCurrentState != 'Pending'}">
                        <form id="toggleform_${schedule.id }" action="${toggleUrl}" method="POST" style="margin: 0px; padding: 0px;">
                            <a href="javascript:document.getElementById('toggleform_${schedule.id}').submit();" style="text-decoration:  none;"><c:choose><c:when test="${schedule.currentState == 'Disabled'}"><img src="<c:url value='/WebConfig/yukon/Icons/Disabled.gif'/>" title="Click to Enable" style="border-width:0px;"/></c:when><c:otherwise><img src="<c:url value='/WebConfig/yukon/Icons/Enabled.gif'/>" title="Click to Disable" style="border-width:0px;"/></c:otherwise></c:choose></a>
                            <input type="hidden" name="id"
                                value="${schedule.id}" />
                            <input type="hidden" name="sortBy" value="${sortBy}"/>
                            <input type="hidden" name="descending" value="${descending}"/>
                        </form>
                    </c:if>
                </td>
                </tr>
            </c:forEach>
        </table>
    </div>


