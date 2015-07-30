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
	<c:when test="${event.eventType.economic}">
	    <c:set var="urlEndPart" value="pricing"/>
	</c:when>
	<c:otherwise>
	   <c:set var="urlEndPart" value="groupSelection"/>
	</c:otherwise>
</c:choose>
<cti:url var="url" value="/dr/cc/program/${event.programId}/${urlEndPart}"/>

<form:form modelAttribute="event" action="${url}">
    <cti:csrfToken/>
    
    <tags:nameValueContainer2>
        <form:hidden path="eventType"/>
        <form:hidden path="programId"/>
        
        <c:set var="eventType" value="${event.eventType}"/>
        <c:if test="${event.eventType.notification or event.eventType.economic}">
	        <tags:nameValue2 nameKey=".notificationTime">
	            <dt:dateTime path="notificationTime"/>
	        </tags:nameValue2>
	    </c:if>
	    <tags:nameValue2 nameKey=".startTime">
	        <dt:dateTime path="startTime"/>
	    </tags:nameValue2>
	    <c:if test="${event.eventType.accounting or event.eventType.notification}">
	        <tags:nameValue2 nameKey=".duration">
	            <form:input path="duration"/>
	        </tags:nameValue2>
	    </c:if>
	    <c:if test="${event.eventType.accounting}">
	       <tags:nameValue2 nameKey=".reason">
	           <form:input path="message"/>
	       </tags:nameValue2>
	    </c:if>
	    <c:if test="${event.eventType.notification}">
           <tags:nameValue2 nameKey=".message">
               <form:input path="message"/>
           </tags:nameValue2>
        </c:if>
        <c:if test="${event.eventType.economic}">
            <tags:nameValue2 nameKey=".numberOfWindows">
                <form:input path="numberOfWindows"/>
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