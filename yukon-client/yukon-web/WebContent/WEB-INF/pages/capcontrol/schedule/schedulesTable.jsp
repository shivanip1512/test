<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.capcontrol">

    <c:choose>
        <c:when test="${searchResult.hitCount == 0}">
            <span class="empty-list"><i:inline key=".schedules.noSchedules"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".schedule"/></th>
                        <th><i:inline key=".lastRunTime"/></th>
                        <th><i:inline key=".nextRunTime"/></th>
                        <th><i:inline key=".interval"/></th>
                        <th><i:inline key=".schedules.disabled"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${scheduleList}">
                        <tr>
                            <td class="wsnw">
                                <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                                    <cti:param name="type" value="3"/>
                                    <cti:param name="itemid" value="${item.scheduleID}"/>
                                </cti:url>
                                <a href="${editUrl}">${fn:escapeXml(item.scheduleName)}</a>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.lastRunTime.time <= startOfTime}">
                                        <i:inline key="yukon.web.defaults.dashes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatDate value="${item.lastRunTime}" type="DATEHM" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.nextRunTime.time <= startOfTime}">
                                        <i:inline key="yukon.web.defaults.dashes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatDate value="${item.nextRunTime}" type="DATEHM" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${fn:escapeXml(item.intervalRateString)}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.disabled}">
                                        <i:inline key="yukon.web.defaults.yes"/>
                                    </c:when>
                                    <c:otherwise>
                                         <i:inline key="yukon.web.defaults.no"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <c:set var="baseUrl" value="schedulesTable" />
    <tags:pagingResultsControls baseUrl="${baseUrl}" result="${pagedResults}" adjustPageCount="true"/>
    
</cti:msgScope>
