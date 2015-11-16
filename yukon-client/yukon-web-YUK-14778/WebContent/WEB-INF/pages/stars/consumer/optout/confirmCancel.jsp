<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<c:set var="actionUrl" value="/stars/consumer/optout/deviceSelection"/>

<cti:standardPage module="consumer" page="confirmCancel">
    <cti:standardMenu/>

    <h3><cti:msg key="yukon.dr.consumer.optout.header"/></h3>

    <div align="center">
        <cti:msg htmlEscape="true" key="yukon.dr.consumer.optout.confirmCancel.question" argument="${optOut.inventory.displayName}"/>

        <br><br>
        <form action="/stars/consumer/optout/cancel" method="post">
            <cti:csrfToken/>
            <input type="hidden" name="eventId" value="${optOut.eventId}">
            <cti:msg key="yukon.dr.consumer.optout.cancelOptOut" var="cancel"/>
            <cti:msg key="yukon.dr.consumer.optout.noCancelOptOut" var="noCancel"/>
            <cti:button type="submit" name="submit" label="${cancel}" value="${cancel}"/>
            <cti:button value="${noCancel}" label="${noCancel}" href="/stars/consumer/optout"/>
        </form>
    </div>     
</cti:standardPage>