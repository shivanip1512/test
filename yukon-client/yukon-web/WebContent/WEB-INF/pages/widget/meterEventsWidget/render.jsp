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
            <table class="compactResultsTable">
                <thead>
                    <tr>
                        <th><i:inline key=".table.timestamp"/></th>
                        <th><i:inline key=".table.event"/></th>
                        <th><i:inline key=".table.state"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${valueMap}" var="entry">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td><cti:formatDate type="BOTH" value="${entry.pointValueHolder.pointDataTimeStamp}"/></td>
                            <td><spring:escapeBody>${entry.pointName}</spring:escapeBody></td>
                            <td class="eventStatus<cti:pointValueFormatter value="${entry.pointValueHolder}" format="VALUE"/>">
                                <cti:pointValueFormatter value="${entry.pointValueHolder}" format="VALUE"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <div class="actionArea">
    	<cti:url value="/amr/meterEventsReport/reportAll" var="meterEventsReportUrl">
    		<cti:param name="collectionType" value="idList"/>
    		<cti:param name="idList.ids" value="${deviceId}"/>
    		<cti:param name="includeDisabledPaos" value="${meter.disabled}"/>
    	</cti:url>
		<c:if test="${not empty valueMap}">
	    	<a href="${meterEventsReportUrl}" class="showAll fl">
	    		<i:inline key=".allEvents"/>
	   		</a>
   		</c:if>
        <tags:widgetActionUpdate hide="false" method="render" nameKey="refresh" container="${widgetParameters.widgetId}_events"/>
    </div>
</div>
