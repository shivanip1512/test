<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.init.groupSelection">
<h3><i:inline key=".selectGroups"/></h3>

<cti:url var="url" value="/dr/cc/program/${event.programId}/customerVerification"/>
<form:form modelAttribute="event" action="${url}">
    <cti:csrfToken/>
    
    <form:hidden path="eventType"/>
    <form:hidden path="programId"/>
    <form:hidden path="notificationTime"/>
    <form:hidden path="startTime"/>
    <form:hidden path="duration"/>
    <form:hidden path="message"/>
    <form:hidden path="numberOfWindows"/>
    
    <c:forEach var="group" items="${availableGroups}">
        <form:checkbox path="selectedGroupIds" value="${group.group.id}"/> ${group.group.name}<br>
    </c:forEach>
    
    <div class="page-action-area">
        <cti:button type="submit" classes="action primary" nameKey="next"/>
        
        <cti:url var="cancelUrl" value="/dr/cc/home"/>
        <cti:button href="${cancelUrl}" nameKey="cancel"/>
    </div>
    
</form:form>
</cti:standardPage>