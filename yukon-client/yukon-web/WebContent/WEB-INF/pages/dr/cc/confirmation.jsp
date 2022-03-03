<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<cti:standardPage module="dr" page="cc.init.confirmation">

<div class="stacked-md">
    
    <spring:hasBindErrors name="event">
        <div class="stacked-md">
            <spring:bind path="event">
                <c:forEach items="${status.errorMessages}" var="error">
                    <span class="error">${error}</span>
                </c:forEach>
            </spring:bind>
        </div>
    </spring:hasBindErrors>
    
    <h3><i:inline key=".parameters"/></h3>
    
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
            <cti:formatDuration type="DHMS_REDUCED" value="${event.duration * 60 * 1000}"/>
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
    <c:forEach var="extensionCustomer" items="${extensionCustomers}">
        <li>${fn:escapeXml(extensionCustomer.companyName)}</li>
    </c:forEach>
</ul>

<div class="page-action-area">
    <cti:button classes="action primary" nameKey="confirm"  id="copy-option" data-popup="#confirm-popup"/>
    <cti:msg2 var="confirmationTitle" key="yukon.web.modules.dr.cc.init.confirmationPopup.title"/>
    <cti:msg2 var="confirmText" key="yukon.web.components.button.ok.label"/>
    <div class="dn" id="confirm-popup" data-title="${confirmationTitle}" data-dialog data-ok-text="${confirmText}" 
         data-event="yukon:event:confirm">
        <cti:list var="arguments">
            <cti:item value="${program.name}"/>
            <cti:item value="${program.programType.name}"/>
        </cti:list>
        <h3><i:inline key=".messagedetail" arguments="${arguments}"/></h3>
        <br><b><i:inline key=".confirmation.parameters"/></b></br>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".notificationTime">
                <cti:formatDate type="FULL" value="${event.notificationTime}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".startTime">
                <cti:formatDate type="FULL" value="${event.startTime}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".stopTime">
                <cti:formatDate type="FULL" value="${event.stopTime}"/>
            </tags:nameValue2>
            <c:if test="${event.eventType.accounting or event.eventType.notification}">
                <tags:nameValue2 nameKey=".confirmation.duration">
                    <cti:formatDuration type="DHMS_REDUCED" value="${event.duration * 60 * 1000}"/>
                </tags:nameValue2>
            </c:if>
        </tags:nameValueContainer2>
    </div>
    <cti:url var="cancelUrl" value="/dr/cc/home"/>
    <cti:button href="${cancelUrl}" nameKey="cancel"/>
</div>

<cti:url var="url" value="/dr/cc/program/${event.programId}/createEvent"/>
<form:form id="confirm-form" modelAttribute="event" action="${url}">
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
    <form:hidden path="initialEventId"/>
</form:form>
<cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js" />
</cti:standardPage>