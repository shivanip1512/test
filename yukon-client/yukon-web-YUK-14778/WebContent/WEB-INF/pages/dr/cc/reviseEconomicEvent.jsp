<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.init.reviseEconomicEvent">
<h3><i:inline key=".newPrices"/></h3>

<cti:url var="action" value="/dr/cc/program/${programId}/event/${eventId}/reviseComplete"/>
<form action="${action}">
	<table class="name-value-table natural-width">
	    <c:forEach var="price" items="${nextRevisionPrices}">
	       <tr>
	            <td class="name">
	                <cti:formatDate type="DATEHM" value="${price.startTime}"/>
	            </td>
	            <td>
	                <input name="prices" value="${price.energyPrice}"/>
	            </td>
	            <td>
	                <i:inline key=".priceUnits"/>
	            </td>
	       </tr>
	    </c:forEach>
	</table>
	<div class="page-action-area">
	    <cti:button type="submit" classes="action primary" nameKey="submitButton"/>
	        
	    <cti:url var="cancelUrl" value="/dr/cc/program/${programId}/event/${eventId}/detail"/>
	    <cti:button href="${cancelUrl}" nameKey="cancel"/>
	</div>
</form>

</cti:standardPage>