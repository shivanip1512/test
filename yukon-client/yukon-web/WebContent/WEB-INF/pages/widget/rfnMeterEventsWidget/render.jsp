<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

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
                            <td><cti:formatDate type="BOTH" value="${entry.key.pointDataTimeStamp}"/></td>
                            <td>${entry.value}</td>
                            <td class="eventStatus<cti:pointValueFormatter value="${entry.key}" format="VALUE"/>">
                                <cti:pointValueFormatter value="${entry.key}" format="VALUE"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <div class="actionArea">
    	<cti:url value="/spring/amr/rfnEventsReport/reportAll" var="rfnEventsReportUrl">
    		<cti:param name="collectionType" value="idList"/>
    		<cti:param name="idList.ids" value="${deviceId}"/>
    	</cti:url>
    	<a href="${rfnEventsReportUrl}" class="rfnEventsReportLink">
    		<i:inline key=".allEvents"/>
   		</a>
        <tags:widgetActionUpdate hide="false" method="render" nameKey="refresh" container="${widgetParameters.widgetId}_events"/>
    </div>
</div>
