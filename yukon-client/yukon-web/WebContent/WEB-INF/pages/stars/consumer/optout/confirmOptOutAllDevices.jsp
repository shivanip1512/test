<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="actionUrl" value="/stars/consumer/optout/optOutQuestions"/>

<cti:standardPage module="consumer" page="optoutlist">
    <cti:standardMenu/>

    <h3><cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.header"/></h3>

    <div align="center">
        <cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.description"/>

        <br>
        <br>

        <form:form id="form" action="${actionUrl}" method="POST" modelAttribute="optOutBackingBean">
            <cti:csrfToken/>
            <table class="results-table" align="center" width="99%">
                <tr>
                    <th><cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.hardware"/></th>
                    <th><cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.programAssigned"/></th>
                </tr>
                <c:forEach var="displayableInventory" items="${displayableInventories}">
                    <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                    <c:set var="optOutCount" value="${optOutCounts[inventoryId]}"/>

                    <c:if test="${not noOptOutsAvailableLookup[displayableInventory]}">
                        <tr>
                            <td align="left" title="${displayableInventory.serialNumber}">
                                <input type="hidden" name="inventoryId" value="${inventoryId}"/>
                                <input type="hidden" id="inventory_${inventoryId}" name="inventoryIds" value="${inventoryId}"></input>
                                <label for="inventory_${inventoryId}">
                                    <spring:escapeBody htmlEscape="true">${displayableInventory.displayName}</spring:escapeBody>
                                </label>
                            </td>
                            <td align="left">
                                <label for="inventory_${inventoryId}">
                                <c:set var="programsList" value="${displayableInventory.programs}"/>
                                <c:set var="count" value="0"/>
    
                                <c:forEach var="program" items="${programsList}">
                                    <c:set var="count" value="${count + 1}"/>
                                    <spring:escapeBody htmlEscape="true">
                                        <cti:msg key="${program.displayName}"/>
                                    </spring:escapeBody>
                                    <c:if test="${fn:length(programsList) != count}">,</c:if>
                                </c:forEach>
                                </label>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>
            <br>

            <div class="page-action-area">
                <cti:msg var="save" key="yukon.dr.consumer.confirmOptOutAllDevices.save"/>
                <cti:button type="submit" value="${save}" label="${save}" busy="true" blockPage="true"/>
                <cti:url var="optOutUrl" value="/stars/consumer/optout"/>
                <cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.cancel" var="cancel"/>
                <cti:button label="${cancel}" href="${optOutUrl}"/>
            </div>
            <form:hidden path="durationInDays"/>
            <form:hidden path="startDate"/>
        </form:form>
    </div>

</cti:standardPage>
