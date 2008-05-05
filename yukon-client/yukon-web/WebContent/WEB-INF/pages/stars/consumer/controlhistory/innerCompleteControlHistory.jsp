<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<table align="center" width="99%">
    <c:forEach var="controlHistoryEvent" items="${controlHistoryEventMap}">
        <c:set var="eventList" value="${controlHistoryEvent.value}"/>
        <c:set var="eventListSize" value="${fn:length(eventList)}"/>
        <c:set var="rowspan" value="${eventListSize > 0 ? eventListSize : 1}"/>
        
        <tr>
            <th><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.deviceLabel"/></th>
            <th><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.startDate"/></th>
            <th><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.endDate"/></th>
            <th><cti:msg key="yukon.dr.consumer.innercompletecontrolhistory.controlDuration"/></th>
        </tr>
        
        <tr>
            <td valign="top" rowspan="${rowspan + 1}">${controlHistoryEvent.key}</td>
        </tr>
        
        <c:choose>
            <c:when test="${eventListSize > 0}">
                <c:forEach var="event" items="${eventList}">
                    <tr class="<ct:alternateRow odd='altRow' even=''/>">
                        <td><cti:formatDate  value="${event.startDate}" type="BOTH"/></td>
                        <td><cti:formatDate  value="${event.endDate}" type="BOTH"/></td>
                        <td><cti:formatDuration type="HM" value="${event.duration}"/></td>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="4"><div style="height: 0.5em;"></div></td>
                </tr>
            </c:when>
            <c:otherwise>
                <tr class="${rowClass}">
                    <td>---</td>
                    <td>---</td>
                    <td>---</td>
                </tr>
            </c:otherwise>
        </c:choose>
        
    </c:forEach>
</table>