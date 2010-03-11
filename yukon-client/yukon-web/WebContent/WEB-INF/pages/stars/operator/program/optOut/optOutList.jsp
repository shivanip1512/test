<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="operator" page="optOut">
    <cti:standardMenu />

<cti:url var="actionUrl" value="/spring/stars/operator/program/optOut/optOutQuestions">
    <cti:param name="accountId" value="${customerAccount.accountId}" />
    <cti:param name="energyCompanyId" value="${energyCompanyId}" />
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

        <tags:boxContainer2 key=".header">

        <i:inline key=".description"/>
        <c:if test="${!empty alreadyOptedOutItems}">
            <br><i:inline key=".someAlreadyOptedOut"/>
        </c:if>

        <br>
        <br>

        <form id="form" action="${actionUrl}" method="POST" onsubmit="createJSON();">
            <div id="controlEventsDiv" style="height: auto;">
                <table class="compactResultsTable" width="99%">
                    <tr>
                        <th></th>
                        <th><i:inline key=".hardware"/></th>
                        <th><i:inline key=".programAssigned"/></th>
                        <th><i:inline key=".information"/></th>
                    </tr>
                    <c:forEach var="displayableInventory" items="${displayableInventories}">
                        <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                        <c:set var="optOutCount" value="${optOutCounts[inventoryId]}"/>

                        <tr class="<tags:alternateRow odd="altRow" even=""/>">
                            <td align="left">
                            	<c:choose>
                            		<c:when test="${displayableInventory.currentlyOptedOut && isSameDay}">
                            		    <input id="unused_${inventoryId}" disabled="disabled" type="checkbox"></input>
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
                            <td align="left">
 								<c:if test="${!optOutCount.optOutsRemaining}">
                                    <i:inline key=".noMoreOptOutsAvailable"/><br>
                                </c:if>
                                <c:if test="${displayableInventory.currentlyOptedOut}">
                                   <i:inline key=".currentlyOptedOut"/><br>
                                </c:if>
                                <c:if test="${not empty displayableInventory.currentlyScheduledOptOut}">
                                   <i:inline key=".currentlyScheduledOptedOut"/><br>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>    
                </table>
            </div>

            <br>
            <span style="padding-right: 0.5em;">
                <input type="submit" width="80px" value="<i:inline key='.save'/>"></input>
            </span>    
            <cti:url var="optOutUrl" value="/spring/stars/operator/program/optOut">
            	<cti:param name="accountId" value="${customerAccount.accountId}" />
            	<cti:param name="energyCompanyId" value="${energyCompanyId}" />
            </cti:url>
            <input type="button" width="80px" value="<i:inline key='.cancel'/>"
                   onclick="javascript:location.href='${optOutUrl}';"></input>
            
            <input type="hidden" name="durationInDays" value="${durationInDays}"></input>
            <input type="hidden" name="startDate" value="${startDate}"></input>
        </form>
        </tags:boxContainer2>
    </div>    
    
</cti:standardPage>    