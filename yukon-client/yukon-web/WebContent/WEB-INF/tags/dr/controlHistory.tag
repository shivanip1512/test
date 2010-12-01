<%@ attribute name="displayableControlHistoryMap" required="true" type="java.util.List"%>
<%@ attribute name="consumer" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<c:forEach var="displayableControlHistory" items="${displayableControlHistoryMap}">
    <c:set var="controlHistory" value="${displayableControlHistory.controlHistory}" />
    <c:set var="eventList" value="${controlHistory.currentHistory}" />
    <c:set var="eventListSize" value="${fn:length(eventList)}" />
    <c:set var="rowspan" value="${eventListSize > 0 ? eventListSize : 1}" />
	
    <c:choose>
        <c:when test="${not empty controlHistory.displayName}"><c:set var="displayName" value="${controlHistory.displayName}"/></c:when>
        <c:otherwise><cti:msg2 var="displayName" key=".deviceRemoved" /></c:otherwise>
    </c:choose>
    
    <tags:sectionContainer title="${displayName}" escapeTitle="true">
        <dr:controlHistoryEvents controlHistoryEventList="${eventList}" showControlSummary="true" consumer="${consumer}"/>
    </tags:sectionContainer>
    <br />

</c:forEach>