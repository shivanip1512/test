<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<c:set var="actionUrl" value="/spring/stars/consumer/optout/confirm"/>

<cti:standardPage module="consumer" page="optout">
    <cti:standardMenu />
    
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
    
    <h3><cti:msg key="yukon.dr.consumer.optoutlist.header"/></h3>
    
    <div align="center">
        <cti:msg key="yukon.dr.consumer.optoutlist.description"/>
        
        <br>
        <br>
        
        <form id="form" action="${actionUrl}" method="POST" onsubmit="createJSON();">
            <table width="100%">
                <tr>
                    <td>
                        <cti:msg key="yukon.dr.consumer.optoutlist.programOptOutTitle" var="programOptOutTitle"/>
                        <ct:boxContainer title="${programOptOutTitle}" hideEnabled="false">
                            <div id="controlEventsDiv" style="height: auto;">
                                <table align="center" width="99%">
                                    <tr>
                                        <th></th>
                                        <th><cti:msg key="yukon.dr.consumer.optoutlist.hardware"/></th>
                                        <th><cti:msg key="yukon.dr.consumer.optoutlist.programAssigned"/></th>
                                    </tr>
                                    <c:forEach var="displayableInventory" items="${displayableInventories}">
                                        <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                                        <input type="hidden" name="inventoryId" value="${inventoryId}"/>
                                        
                                        <tr class="<ct:alternateRow odd='altRow' even=''/>">
                                            <td align="left">
                                                <input id="check_${inventoryId}" type="checkbox" checked="checked"></input>
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
                                    </c:forEach>    
                                </table>
                            </div>
                        </ct:boxContainer>
                    </td>
                </tr>
                
                <tr>
                    <td align="center">
                        <br>
                        <input type="submit" value="<cti:msg key='yukon.dr.consumer.optoutlist.save'/>"></input>
                    </td>
                </tr>
            </table>
            
            <input type="hidden" name="durationInDays" value="${durationInDays}"></input>
            <input type="hidden" name="startDate" value="${startDate}"></input>
        </form>
    </div>    
    
</cti:standardPage>    
    