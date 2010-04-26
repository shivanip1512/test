<%@ attribute name="displayableControlHistoryMap" required="true" type="java.util.List"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<c:forEach var="displayableControlHistory" items="${displayableControlHistoryMap}">
    <c:set var="controlHistory" value="${displayableControlHistory.controlHistory}" />
    <c:set var="eventList" value="${controlHistory.currentHistory}" />
    <c:set var="eventListSize" value="${fn:length(eventList)}" />
    <c:set var="rowspan" value="${eventListSize > 0 ? eventListSize : 1}" />
	
    <c:if test="${empty lastControlHistoryEntry or 
               not (lastControlHistoryEntry eq controlHistory.displayName)}">

        <tags:sectionContainer title="${controlHistory.displayName}" escapeTitle="true">
            <dr:controlHistoryEvents controlHistoryEventList="${eventList}" 
                                     showControlSummary="true" />
        </tags:sectionContainer>
        <br />

        <c:set var="lastControlHistoryEntry" value="${controlHistory.displayName}" />
    </c:if>
</c:forEach>