<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="actionUrl" value="/spring/stars/consumer/optout/optOutQuestions"/>

<cti:standardPage module="consumer" page="optoutlist">
    <cti:standardMenu/>

    <h3><cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.header"/></h3>

    <div align="center">
        <cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.description"/>

        <br>
        <br>

        <form:form id="form" action="${actionUrl}" method="POST" commandName="optOutBackingBean">
            <table class="resultsTable" align="center" width="99%">
                <tr>
                    <th><cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.hardware"/></th>
                    <th><cti:msg key="yukon.dr.consumer.confirmOptOutAllDevices.programAssigned"/></th>
                </tr>
                <c:forEach var="displayableInventory" items="${displayableInventories}">
                    <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                    <c:set var="optOutCount" value="${optOutCounts[inventoryId]}"/>

                    <c:if test="${not noOptOutsAvailableLookup[displayableInventory]}">
                        <tr class="<tags:alternateRow odd='altRow' even=""/>">
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

            <span style="padding-right: 0.5em;">
                <input type="submit" value="<cti:msg key='yukon.dr.consumer.confirmOptOutAllDevices.save'/>"></input>
            </span>

            <cti:url var="optOutUrl" value="/spring/stars/consumer/optout" />
            <input type="button" value="<cti:msg key='yukon.dr.consumer.confirmOptOutAllDevices.cancel'/>"
                   onclick="javascript:location.href='${optOutUrl}';"></input>


            <form:hidden path="durationInDays"/>
            <form:hidden path="startDate"/>
        </form:form>
    </div>

</cti:standardPage>
