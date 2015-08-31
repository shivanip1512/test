<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.init.confirmation">

<div class="stacked-md">
	<h3><i:inline key=".parameters"/></h3>
	
	<spring:hasBindErrors name="event">
        <form:errors cssClass="error"/><br>
    </spring:hasBindErrors>
    
    <tags:nameValueContainer2>
        <c:if test="${not empty event.startTime}">
           <tags:nameValue2 nameKey=".notificationTime">
               <cti:formatDate type="FULL" value="${event.notificationTime}"/>
           </tags:nameValue2>
        </c:if>
        
	    <tags:nameValue2 nameKey=".startTime">
	        <cti:formatDate type="FULL" value="${event.startTime}"/>
	    </tags:nameValue2>
	    
	    <c:if test="${event.eventType.accounting or event.eventType.notification}">
		    <tags:nameValue2 nameKey=".duration">
		        <cti:formatDuration TYPE="DHMS_REDUCED" value="${event.duration * 60 * 1000}"/>
		    </tags:nameValue2>
	    </c:if>
	    
	    <c:if test="${event.eventType.economic}">
	       <tags:nameValue2 nameKey=".numberOfWindows">
	           ${event.numberOfWindows}
	       </tags:nameValue2>
	    </c:if>
	    
	    <c:if test="${event.eventType.accounting}">
	        <tags:nameValue2 nameKey=".reason">
                ${fn:escapeXml(event.message)}
            </tags:nameValue2>
	    </c:if>
	    
	    <c:if test="${event.eventType.notification}">
		    <tags:nameValue2 nameKey=".message">
		        ${fn:escapeXml(event.message)}
		    </tags:nameValue2>
		</c:if>
	</tags:nameValueContainer2>
</div>

<c:if test="${event.eventType.economic}">
    <div class="stacked-md">
        <h3><i:inline key=".prices"/></h3>
        <table class="compact-results-table natural-width">
            <thead>
                <cti:formatDate var="tz" type="TIMEZONE" value="${event.startTime}"/>
                <th><i:inline key=".pricesStartTime" arguments="${tz}"/></th>
                <th class="tar"><i:inline key=".energyPrice"/></th>
            </thead>
            <tbody>
	            <c:forEach var="price" items="${event.windowPrices}" varStatus="status">
	                <tr>
	                    <td>
                            <cti:formatDate type="DATEHM" value="${windowTimes[status.index]}"/>
                        </td>
	                    <td class="tar">${price}</td>
	                </tr>
	            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>

<h3><i:inline key=".customers"/></h3>
<ul>
    <c:forEach var="customerNotif" items="${customerNotifs}">
        <li>
            ${fn:escapeXml(customerNotif.customer.companyName)}
        </li>
    </c:forEach>
</ul>

<cti:url var="url" value="/dr/cc/program/${event.programId}/createEvent"/>
<form:form modelAttribute="event" action="${url}">
    <cti:csrfToken/>
    
    <form:hidden path="eventType"/>
    <form:hidden path="programId"/>
    <form:hidden path="notificationTime"/>
    <form:hidden path="startTime"/>
    <form:hidden path="duration"/>
    <form:hidden path="message"/>
    <form:hidden path="numberOfWindows"/>
    <form:hidden path="windowPrices"/>
    <form:hidden path="selectedGroupIds"/>
    <form:hidden path="selectedCustomerIds"/>

	<div class="page-action-area">
	    <cti:button type="submit" classes="action primary" nameKey="confirm"/>
	    
	    <cti:url var="cancelUrl" value="/dr/cc/home"/>
	    <cti:button href="${cancelUrl}" nameKey="cancel"/>
	</div>
</form:form>
</cti:standardPage>