<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div id="${widgetParameters.widgetId}_events">
    <c:choose>
        <c:when test="${empty valueMap}">
            <span class="strongMessage fl">
                <i:inline key=".noEvents"/>
            </span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".table.timestamp"/></th>
                        <th><i:inline key=".table.event"/></th>
                        <th><i:inline key=".table.state"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${valueMap}" var="entry">
                        <tr>
                            <td><cti:formatDate type="BOTH" value="${entry.pointValueHolder.pointDataTimeStamp}"/></td>
                            <td><spring:escapeBody>${entry.pointName}</spring:escapeBody></td>
                            <cti:pointColor pointId="${entry.pointValueHolder.id}" rawState="${entry.pointValueHolder.value}" var="color"/>
                            <td class="${color}">
                                <cti:pointValueFormatter format="VALUE" value="${entry.pointValueHolder}" />
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <div class="action-area">
        <cti:formatDate value="${defaultStartInstant}" type="DATE" var="fromInstant"/>
        <cti:url value="/amr/meterEventsReport/report" var="meterEventsReportUrl">
            <cti:param name="collectionType" value="idList"/>
            <cti:param name="idList.ids" value="${deviceId}"/>
            <cti:param name="includeDisabledPaos" value="${meter.disabled}"/>
            <cti:param name="sort" value="DATE"/>
            <cti:param name="fromInstant" value="${fromInstant}"/>
            <cti:param name="descending" value="true"/>
        </cti:url>
        <c:if test="${not empty valueMap}">
            <a href="${meterEventsReportUrl}" class="showAll fl"><i:inline key=".allEvents"/></a>
           </c:if>
        <tags:widgetActionUpdate method="render" nameKey="refresh" container="${widgetParameters.widgetId}_events" icon="icon-arrow-refresh"/>
    </div>
</div>
