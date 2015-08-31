<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.init.pricing">

<h3><i:inline key=".energyPrices"/></h3>

<cti:url var="url" value="/dr/cc/program/${event.programId}/groupSelection"/>
<form:form modelAttribute="event" action="${url}">
    <cti:csrfToken/>
    
    <form:hidden path="eventType"/>
    <form:hidden path="programId"/>
    <form:hidden path="notificationTime"/>
    <form:hidden path="startTime"/>
    <form:hidden path="numberOfWindows"/>
    <form:hidden path="initialEventId"/>
    
    <table class="name-value-table natural-width">
	    <c:forEach var="windowTime" items="${windowTimes}" varStatus="status">
	       <tr>
	           <td class="name">
	               <cti:formatDate type="DATEHM" value="${windowTime}"/>
	           </td>
	           <td>
	               <tags:input path="windowPrices[${status.index}]" size="10"/>
	           </td>
	           <td>
                    <i:inline key=".pricingUnits"/>
                </td>
	       </tr>
	    </c:forEach>
	</table>
    
    <div class="page-action-area">
        <cti:button type="submit" classes="action primary" nameKey="next"/>
        
        <cti:url var="cancelUrl" value="/dr/cc/home"/>
        <cti:button href="${cancelUrl}" nameKey="cancel"/>
    </div>
</form:form>
</cti:standardPage>