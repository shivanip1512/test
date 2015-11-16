<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.init">

<h3><i:inline key=".eventParameters"/></h3>

<%-- Form action will vary, depending on what type of program is being activated --%>
<c:choose>
	<c:when test="${isAdjust}">
       <cti:url var="url" value="/dr/cc/program/${event.programId}/event/${event.adjustEventId}/completeEventAdjustment"/>
    </c:when>
	<c:when test="${event.eventType.economic}">
	    <cti:url var="url" value="/dr/cc/program/${event.programId}/pricing"/>
	</c:when>
	<c:otherwise>
	   <cti:url var="url" value="/dr/cc/program/${event.programId}/groupSelection"/>
	</c:otherwise>
</c:choose>


<form:form modelAttribute="event" action="${url}">
    <cti:csrfToken/>
    
    <tags:nameValueContainer2>
        <form:hidden path="eventType"/>
        <form:hidden path="programId"/>
        <form:hidden path="initialEventId"/>
        <form:hidden path="adjustEventId"/>
        
        <%-- Start and notification times --%>
        <c:if test="${not event.eventExtension}">
	        <c:set var="eventType" value="${event.eventType}"/>
	        <c:if test="${eventType.notification or eventType.economic}">
		        <tags:nameValue2 nameKey=".notificationTime">
		            <dt:dateTime path="notificationTime"/>
		        </tags:nameValue2>
		    </c:if>
		    <tags:nameValue2 nameKey=".startTime">
		        <dt:dateTime path="startTime"/>
		    </tags:nameValue2>
	    </c:if>
	    
	    <%-- For extensions, the notification and start times are not editable. --%>
	    <c:if test="${event.eventExtension}">
	        <tags:nameValue2 nameKey=".notificationTime">
	            <cti:formatDate value="${event.notificationTime}" type="FULL"/>
	        </tags:nameValue2>
	        <tags:nameValue2 nameKey=".startTime">
                <cti:formatDate value="${event.startTime}" type="FULL"/>
            </tags:nameValue2>
	        <form:hidden path="startTime"/>
	        <form:hidden path="notificationTime"/>
	    </c:if>
	    
	    <%-- Duration --%>
	    <c:if test="${eventType.accounting or eventType.notification}">
	        <tags:nameValue2 nameKey=".duration">
	            <tags:input path="duration"/>
	        </tags:nameValue2>
	    </c:if>
	    
	    <%-- Reason / Message --%>
	    <c:if test="${eventType.accounting}">
	       <tags:nameValue2 nameKey=".reason">
	           <tags:input path="message"/>
	       </tags:nameValue2>
	    </c:if>
	    <c:if test="${eventType.notification}">
           <tags:nameValue2 nameKey=".message">
               <tags:input path="message"/>
           </tags:nameValue2>
        </c:if>
        
        <%-- Number of windows --%>
        <c:if test="${eventType.economic}">
            <tags:nameValue2 nameKey=".numberOfWindows">
                <tags:input path="numberOfWindows"/>
            </tags:nameValue2>
        </c:if>
    </tags:nameValueContainer2>
    
    <div class="page-action-area">
        <cti:button type="submit" classes="action primary" nameKey="next"/>
        
        <cti:url var="cancelUrl" value="/dr/cc/home"/>
        <cti:button href="${cancelUrl}" nameKey="cancel"/>
    </div>
</form:form>
</cti:standardPage>