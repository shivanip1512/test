<%@ attribute name="groupedControlHistory" required="true" type="java.util.List"%>
<%@ attribute name="consumer" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showGroupedHistory" required="false" type="java.lang.Boolean"%>
<%@ attribute name="programId" required="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>

<c:forEach var="groupedControlHistory" items="${groupedControlHistory}">
    
    <c:choose>
        <c:when test="${not empty groupedControlHistory}"><c:set var="displayName" value="${groupedControlHistory.displayName}"/></c:when>
        <c:otherwise><cti:msg2 var="displayName" key=".deviceRemoved" /></c:otherwise>
    </c:choose>
    
    <tags:sectionContainer title="${displayName}" escapeTitle="true" styleClass="dashed stacked">
        <dr:controlHistoryEvents
            groupedHistoryEventList="${groupedControlHistory.groupedHistory}"
            showControlSummary="true" consumer="${pageScope.consumer}"
            showGroupedHistory="${pageScope.showGroupedHistory}"
            programId="${programId}"/>
    </tags:sectionContainer>

</c:forEach>