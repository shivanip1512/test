<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="consumer" page="optoutlist">
    <cti:standardMenu/>

    <c:if test="${!empty error}">
        <span class="error"><cti:msg key="${error}"/></span><br>
    </c:if>

    <h3><cti:msg key="yukon.dr.consumer.optoutlist.header"/></h3>

    <div align="center">
        <cti:msg key="yukon.dr.consumer.optoutlist.description"/>
        <br>

        <c:set var="showNextButton" value="false" />
        <cti:url var="actionUrl" value="/stars/consumer/optout/optOutQuestions"/>
        <form:form id="form" action="${actionUrl}" method="POST" modelAttribute="optOutBackingBean">
            <cti:csrfToken/>
            <table class="results-table" align="center" width="99%">
                <tr>
                    <th></th>
                    <th><cti:msg key="yukon.dr.consumer.optoutlist.hardware"/></th>
                    <th><cti:msg key="yukon.dr.consumer.optoutlist.programAssigned"/></th>
                </tr>
                <c:forEach var="displayableInventory" items="${displayableInventories}">
                    <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                    <c:set var="optOutCount" value="${optOutCounts[inventoryId]}"/>

                    <tr>
                        <td align="left">
                            <c:choose>
                                <c:when test="${noOptOutsAvailableLookup[displayableInventory]}">
                                    <input id="unused_${inventoryId}" checked="checked" disabled="disabled" type="checkbox"></input>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="showNextButton" value="true" />
                                    <input type="hidden" name="inventoryId" value="${inventoryId}"/>
                                    <input id="inventory_${inventoryId}" type="checkbox" name="inventoryIds" value="${inventoryId}"></input>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td align="left" title="${displayableInventory.serialNumber}">
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
                    <c:if test="${!optOutCount.optOutsRemaining || 
                             displayableInventory.currentlyOptedOut || 
                             not empty displayableInventory.currentlyScheduledOptOut}">
                        <tr>
                            <td>&nbsp;</td>
                            <td colspan="2">
                                <c:if test="${!optOutCount.optOutsRemaining  ||
                                          not empty displayableInventory.currentlyScheduledOptOut && ((optOutCount.remainingOptOuts - 1) eq 0) && isSameDay}">
                                    <cti:msg key="yukon.dr.consumer.optoutlist.noMoreOptOutsAvailable"/><br>
                                </c:if>
                                <c:if test="${displayableInventory.currentlyOptedOut}">
                                   <cti:msg key="yukon.dr.consumer.optoutlist.currentlyOptedOut"/><br>
                                </c:if>
                                <c:if test="${not empty displayableInventory.currentlyScheduledOptOut}">
                                    <cti:msg key="yukon.dr.consumer.optoutlist.currentlyScheduledOptOut"/><br>
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </table>
            <br>
            <c:if test="${showNextButton}">
                <span style="padding-right: 0.5em;">
                    <cti:msg var="save" key="yukon.dr.consumer.optoutlist.save"/>
                    <cti:button type="submit" value="${save}" label="${save}" busy="true" blockPage="true"/>
                </span>
            </c:if>
            <cti:url var="optOutUrl" value="/stars/consumer/optout" />
            <cti:msg key="yukon.dr.consumer.optoutlist.cancel" var="cancel"/>
            <cti:button label="${cancel}" href="${optOutUrl}"/>

            <form:hidden path="durationInDays"/>
            <form:hidden path="startDate"/>
        </form:form>
    </div>
</cti:standardPage>
