<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.init.customerVerification">
<cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>

<c:if test="${isSplit}">
    <cti:msg2 var="heading" key=".removeCustomers"/>
    <cti:url var="action" value="/dr/cc/program/${program.id}/event/${eventId}/split"/>
    <c:set var="submitKey" value="remove"/>
</c:if>
<c:if test="${not isSplit}">
    <cti:msg2 var="heading" key=".verifyCustomers"/>
    <cti:url var="action" value="/dr/cc/program/${event.programId}/confirmation"/>
    <c:set var="submitKey" value="next"/>
</c:if>

<h3>${heading}</h3>

<form:form modelAttribute="event" action="${action}">
    <cti:csrfToken/>
    
    <spring:hasBindErrors name="event">
        <form:errors cssClass="error"/><br>
    </spring:hasBindErrors>
    
    <form:hidden path="eventType"/>
    <form:hidden path="programId"/>
    <form:hidden path="notificationTime"/>
    <form:hidden path="startTime"/>
    <form:hidden path="duration"/>
    <form:hidden path="message"/>
    <form:hidden path="numberOfWindows"/>
    <form:hidden path="windowPrices"/>
    <form:hidden path="selectedGroupIds"/>
    
    <div id="customerTableDiv" class="stacked-md">
    <table class=".compact-results-table .striped">
        <thead>
            <tr>
                <th><i:inline key=".customer"/></th>
                <th><i:inline key=".currentLoad"/></th>
                <th><i:inline key=".contractFirmDemand"/></th>
                <th><i:inline key=".loadReduction"/></th>
                <th><i:inline key=".constraintStatus"/></th>
                <th></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="customerNotif" items="${customerNotifs}">
            <tr>
	            <td>
	               <c:set var="disabled" value="false"/>
	               <c:if test="${not empty exclusions[customerNotif.id]}">
	                   <c:forEach var="exclusion" items="${exclusions[customerNotif.id]}">
	                       <c:if test="${exclusion.forceExcluded}">
			                   <c:set var="disabled" value="true"/>
		                   </c:if>
	                   </c:forEach>
	               </c:if>
                   <form:checkbox path="selectedCustomerIds" value="${customerNotif.id}" 
	                              onclick="yukon.dr.curtailment.doCalcSelectedLoad();"
	                              disabled="${disabled}"/>
	               ${fn:escapeXml(customerNotif.customer.companyName)}
	            </td>
	            <td>
	               <span class="js-current-load">
	                   ${currentLoadUpdaters[customerNotif.id]}
	               </span>
	            </td>
	            <td>
	               <span class="js-cfd">
	                   ${cfdUpdaters[customerNotif.id]}
	               </span>
	            </td>
	            <td>
	               <span class="js-load-reduct">
	                   <i:inline key="yukon.common.dashes"/>
	               </span>
	            </td>
	            <td>
	               ${customerConstraintStatuses[customerNotif.id]}
	            </td>
	            <c:if test="${not empty exclusions[customerNotif.id]}">
	               <td>
	                   <span class="js-exclusion-reason error">
	                       <c:forEach var="exclusion" items="${exclusions[customerNotif.id]}" varStatus="status">
	                           <c:if test="${status.index gt 0}"><br></c:if>
	                           <i:inline key="${exclusion}"/>
	                       </c:forEach>
	                   </span>
	               </td>
	            </c:if>
	        </tr>
	        </c:forEach>
        </tbody>
    </table>
    </div>
    
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".loadReductionSelected">
            <span class="js-load-reduct-total">
                <i:inline key="yukon.common.dashes"/>
            </span>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <div class="page-action-area">
        <cti:button type="submit" classes="action primary" nameKey="${submitKey}"/>
        
        <cti:url var="cancelUrl" value="/dr/cc/home"/>
        <cti:button href="${cancelUrl}" nameKey="cancel"/>
    </div>
    
    <cti:dataUpdaterCallback function="yukon.dr.curtailment.doCalcSelectedLoad" initialize="true"/>
</form:form>
</cti:standardPage>