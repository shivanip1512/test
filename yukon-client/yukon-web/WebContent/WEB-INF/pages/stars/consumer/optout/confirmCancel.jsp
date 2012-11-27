<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>


<c:set var="actionUrl" value="/stars/consumer/optout/deviceSelection"/>

<cti:standardPage module="consumer" page="confirmCancel">
    <cti:standardMenu/>

    <h3><cti:msg key="yukon.dr.consumer.optout.header"/></h3>

    <div align="center">
        <cti:msg htmlEscape="true" key="yukon.dr.consumer.optout.confirmCancel.question" argument="${optOut.inventory.displayName}"/>

        <br><br>
		<form action="/stars/consumer/optout/cancel" method="post">
			<input type="hidden" name="eventId" value="${optOut.eventId}">
			<input type="submit" name="submit" value="<cti:msg key="yukon.dr.consumer.optout.cancelOptOut"/>" class="formSubmit">
			<input type="button" value="<cti:msg key="yukon.dr.consumer.optout.noCancelOptOut"/>" class="formSubmit" onclick="window.location='/stars/consumer/optout'">
		</form>
    </div>     
</cti:standardPage>    
