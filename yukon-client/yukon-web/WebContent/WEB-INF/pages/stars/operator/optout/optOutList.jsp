<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<cti:url var="actionUrl" value="/stars/operator/optout/optoutQuestions"/>

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
    inputElement.value = Object.toJSON(array);
    
    $('form').appendChild(inputElement);
}
</script>    

    <div align="center">
		<c:if test="${!empty error}">
			<span class="errorMessage"><cti:msg key="${error}"/></span><br>
		</c:if>

        <h3><cti:msg key="yukon.dr.operator.optoutlist.header"/></h3>
    
        <cti:msg key="yukon.dr.operator.optoutlist.description"/>
        <c:if test="${!empty alreadyOptedOutItems}">
            <br><cti:msg key="yukon.dr.operator.optoutlist.someAlreadyOptedOut"/>
        </c:if>

        <br>
        <br>

        <form id="form" action="${actionUrl}" method="POST" onsubmit="createJSON();">
            <div id="controlEventsDiv" style="height: auto;">
                <table class="resultsTable" align="center" width="99%">
                    <tr>
                        <th></th>
                        <th align="left"><cti:msg key="yukon.dr.operator.optoutlist.hardware"/></th>
                        <th align="left"><cti:msg key="yukon.dr.operator.optoutlist.programAssigned"/></th>
                    </tr>
                    <c:forEach var="displayableInventory" items="${displayableInventories}">
                        <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                        <c:set var="optOutCount" value="${optOutCounts[inventoryId]}"/>

                        <tr class="<ct:alternateRow odd="altRow" even=""/>">
                            <td align="left">
                            	<c:choose>
                            		<c:when test="${displayableInventory.currentlyOptedOut && isSameDay}">
                            		    <input id="unused_${inventoryId}" checked="checked" disabled="disabled" type="checkbox"></input>
                            		</c:when>
                            		<c:otherwise>
                                        <input type="hidden" name="inventoryId" value="${inventoryId}"/>
                                        <input id="check_${inventoryId}" type="checkbox"></input>
                            		</c:otherwise>
                            	</c:choose>
                            </td>
                            <td align="left" title="${displayableInventory.serialNumber}">
                                <spring:escapeBody htmlEscape="true">${displayableInventory.displayName}</spring:escapeBody>
                            </td>
                            <td align="left">
                                <c:set var="programsList" value="${displayableInventory.programs}"/>
                                <c:set var="count" value="0"/>
                                
                                <c:forEach var="program" items="${programsList}">
                                    <c:set var="count" value="${count + 1}"/>
                                    <cti:msg key="${program.displayName}"/><c:if test="${fn:length(programsList) != count}">,</c:if>
                                </c:forEach>    
                            </td>
                        </tr>
                        <c:if test="${!optOutCount.optOutsRemaining || displayableInventory.currentlyOptedOut}">
                            <tr class="<ct:alternateRow odd="altRow" even="" skipToggle="true"/>">
                                <td>&nbsp;</td>
                                <td colspan="2">
                                <c:if test="${!optOutCount.optOutsRemaining}">
                                    <cti:msg key="yukon.dr.consumer.optoutlist.noMoreOptOutsAvailable"/><br>
                                </c:if>
                                <c:if test="${displayableInventory.currentlyOptedOut}">
                                   <cti:msg key="yukon.dr.consumer.optoutlist.currentlyOptedOut"/><br>
                                </c:if>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>    
                </table>
            </div>

            <br>
            <span style="padding-right: 0.5em;">
                <input type="submit" value="<cti:msg key='yukon.dr.operator.optoutlist.save'/>"></input>
            </span>    
            <cti:url var="optOutUrl" value="/operator/Consumer/OptOut.jsp"/>
            <input type="button" value="<cti:msg key='yukon.dr.operator.optoutlist.cancel'/>"
                   onclick="javascript:location.href='${optOutUrl}';"></input>
            
            <input type="hidden" name="durationInDays" value="${durationInDays}"></input>
            <input type="hidden" name="startDate" value="${startDate}"></input>
        </form>
    </div>    
    
    