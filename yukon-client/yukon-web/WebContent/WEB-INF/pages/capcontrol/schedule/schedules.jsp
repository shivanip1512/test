<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="capcontrol" page="schedules">
    <dt:pickerIncludes/>
    <cti:includeScript link="/JavaScript/yukon.da.schedules.js"/>

    <cti:linkTabbedContainer mode="section">
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.schedules.tab.title"
                     initiallySelected='${true}'>
            <cti:url value="/capcontrol/schedules" />
        </cti:linkTab>
        <cti:linkTab selectorKey="yukon.web.modules.capcontrol.scheduleAssignments.tab.title">
            <cti:url value="/capcontrol/schedules/scheduleAssignments" />
        </cti:linkTab>
    </cti:linkTabbedContainer>

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

    <%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <c:choose>
        <c:when test="${fn:length(schedules) == 0}">
            <span class="empty-list"><i:inline key=".noSchedules"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".schedule"/></th>
                        <th><i:inline key=".lastRunTime"/></th>
                        <th><i:inline key=".nextRunTime"/></th>
                        <th><i:inline key=".interval"/></th>
                        <th><i:inline key="yukon.common.enabled"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="schedule" items="${schedules}">
                        <tr data-schedule-id="${schedule.id}">
                            <td class="wsnw">
                                <a href="javascript:void(0)" class="js-edit-schedule" data-schedule-id="${schedule.id}">
                                    ${fn:escapeXml(schedule.name)}
                                </a>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${schedule.lastRunTime.millis < epoch1990.millis}">
                                        <i:inline key="yukon.web.defaults.dashes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatDate value="${schedule.lastRunTime}" type="DATEHM" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${schedule.nextRunTime.millis < epoch1990.millis}">
                                        <i:inline key="yukon.web.defaults.dashes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatDate value="${schedule.nextRunTime}" type="DATEHM" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:if test="${schedule.repeatSeconds == 0}">
                                    <cti:msg2 key="defaults.none"/>
                                </c:if>
                                <c:if test="${schedule.repeatSeconds != 0}">
                                    <cti:formatDuration type="DHMS_REDUCED" value="${schedule.repeatDuration}"/>
                                </c:if>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${schedule.disabled}">
                                         <span class="red"><i:inline key="yukon.web.defaults.no"/></span>
                                    </c:when>
                                    <c:otherwise>
                                         <span class="green"><i:inline key="yukon.web.defaults.yes"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <div class="action-area">
            <cti:button icon="icon-add" nameKey="create" data-popup="#schedule-create-popup"/>
            <cti:url var="createUrl" value="/capcontrol/schedules/create" />
        </div>
        <div id="schedule-create-popup" data-url="${createUrl}" data-dialog data-load-event="yukon:da:schedules:create" data-event="yukon:da:schedules:edit:submit" class="dn"></div>
    </cti:checkRolesAndProperties>
    <div id="schedule-popup"></div>

</cti:standardPage>