<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="operator" page="optOut.inventoryList">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/optOut.css"/>
    
<cti:url var="actionUrl" value="/spring/stars/operator/program/optOut/optOutQuestions">
    <cti:param name="accountId" value="${accountId}" />
</cti:url>

<script type="text/javascript">
function createJSON() {
    var array = new Array();
    var index = 0;

    $$('INPUT').each(function(input) {
        if (input) {
            var name = input.name;
            if (name == 'inventoryId') {
                var inventoryId = input.value;
                var checked = $('check_' + inventoryId).checked;
                if (checked) {
                    array[index++] = input.value;
                }    
            }
        }
    });


    // error message here
    
    var inputElement = document.createElement('input');
    inputElement.type = 'hidden';
    inputElement.name = 'jsonInventoryIds';
    inputElement.value = array.toJSON();
    
    $('form').appendChild(inputElement);
}
</script>    

    <div>
		<c:if test="${!empty error}">
			<span class="errorMessage"><cti:msg key="${error}"/></span><br>
		</c:if>

        <c:if test="${!empty alreadyOptedOutItems}">
            <br><i:inline key=".someAlreadyOptedOut"/>
        </c:if>

        <br>
        <br>

        <c:set var="showNextButton" value="false" />
        <form:form id="form" commandName="optOutBackingBean" 
                   action="/spring/stars/operator/program/optOut/optOutQuestions" 
                   method="POST" onsubmit="createJSON();">
            <input type="hidden" name="accountId" value="${accountId}" />

        <tags:boxContainer2 nameKey="selectDevice">
            <div id="controlEventsDiv" style="height: auto;">
                <table class="resultsTable rowHighlighting selectDeviceTable">
                    <tr>
                        <th><i:inline key=".device"/></th>
                        <th><i:inline key=".programAssigned"/></th>
                        <th><i:inline key=".information"/></th>
                    </tr>
                    <c:forEach var="displayableInventory" items="${displayableInventories}">
                        <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                        <c:set var="optOutCount" value="${optOutCounts[inventoryId]}"/>

                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td title="${displayableInventory.serialNumber}">
                                <label for="check_${inventoryId}">
                                    <c:choose>
                                        <c:when test="${displayableInventory.currentlyOptedOut && isSameDay}">
                                            <input id="unused_${inventoryId}" disabled="disabled" type="checkbox"></input>
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="showNextButton" value="true" />
                                            <input type="hidden" name="inventoryId" value="${inventoryId}"/>
                                            <input id="check_${inventoryId}" type="checkbox"></input>
                                        </c:otherwise>
                                    </c:choose>
                                    <spring:escapeBody htmlEscape="true">${displayableInventory.displayName}</spring:escapeBody>
                                </label>
                            </td>
                            <td>
                                <c:set var="programsList" value="${displayableInventory.programs}"/>
                                <c:set var="count" value="0"/>
                                
                                <c:forEach var="program" items="${programsList}">
                                    <c:set var="count" value="${count + 1}"/>
                                    <spring:escapeBody htmlEscape="true">
                                        <cti:msg key="${program.displayName}"/>
                                    </spring:escapeBody>
                                    <c:if test="${fn:length(programsList) != count}"><br></c:if>
                                </c:forEach>    
                            </td>
                            <td>
 								<c:if test="${!optOutCount.optOutsRemaining}">
                                    <i:inline key=".noMoreOptOutsAvailable"/><br>
                                </c:if>
                                <c:if test="${displayableInventory.currentlyOptedOut}">
                                   <i:inline key=".currentlyOptedOut"/><br>
                                </c:if>
                                <c:if test="${not empty displayableInventory.currentlyScheduledOptOut}">
                                   <i:inline key=".currentlyScheduledOptOut"/><br>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>    
                </table>
            </div>
        </tags:boxContainer2>

        <br>
        <span style="padding-right: 0.5em;">
            <c:if test="${showNextButton}">
                <c:choose>
                    <c:when test="${optOutQuestionsExist}">
                        <input type="submit" value="<cti:msg2 key='.next'/>">
                    </c:when>
                    <c:otherwise>
                        <input type="submit" value="<cti:msg2 key='.save'/>">
                    </c:otherwise>
                </c:choose>
            </c:if>
        </span>
        <cti:url var="optOutUrl" value="/spring/stars/operator/program/optOut">
        	<cti:param name="accountId" value="${accountId}" />
        </cti:url>
        <input type="button" value="<cti:msg2 key='.cancel'/>"
               onclick="javascript:location.href='${optOutUrl}';">
            
        <form:hidden path="durationInDays" />
        <form:hidden path="startDate"/>
        </form:form>
    </div>    
    
</cti:standardPage>