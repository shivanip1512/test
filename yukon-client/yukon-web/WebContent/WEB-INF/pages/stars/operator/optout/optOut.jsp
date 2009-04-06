<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<link rel="stylesheet" href="../../WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css">

<h3><cti:msg key="yukon.dr.operator.optout.header"/></h3>

<c:if test="${allOptedOut}">
	<cti:msg key="yukon.dr.operator.optout.allOptedOut"/>
</c:if>
<c:if test="${!optOutsAvailable}">
    <cti:msg key="yukon.dr.operator.optout.noOptOutsAvailable"/>
</c:if>

<c:if test="${!allOptedOut && optOutsAvailable}">
	<cti:msg key="yukon.dr.operator.optout.description"/><br><br>

	<form action="/spring/stars/operator/optout/optout2" method="POST">
	    <table>
	        <tr>
	            <td align="right">
	                <cti:msg key="yukon.dr.operator.optout.startDate"/>
	            </td>

	            <cti:formatDate  value="${currentDate}" type="DATE" var="formattedDate"/>

	            <td align="left">
	                <ct:dateInputCalendar fieldName="startDate" fieldValue="${formattedDate}"/>
	            </td>
	        </tr>

	        <tr>
	            <td align="right">
	                <cti:msg key="yukon.dr.operator.optout.duration"/>
	            </td>

	            <td align="left">
	                <select name="durationInDays">
	                    
	                    <c:forEach var="x" begin="1" end="${maxOptOutDays}" step="1">
	                    
	                       <c:set var="key" value="${(x == 1) ? 'yukon.dr.operator.optout.day' : 'yukon.dr.operator.optout.days' }"/>
	                    
	                       <option value="${x}">${x} <cti:msg key="${key}"/></option>
	                    
	                    </c:forEach>
	                    
	                </select>
	            </td>
	        </tr>

	        <tr>
	            <td align="center" colspan="2">
	                <br>
	                <input type="submit" value="<cti:msg key='yukon.dr.operator.optout.apply'/>"></input>
	            </td>
	        </tr>
	    </table>
	</form>
</c:if>


<br><br>
<!-- Current Opt Outs -->
<cti:msg key="yukon.dr.operator.optout.currentOptOuts"/>

<c:choose>
    <c:when test="${fn:length(currentOptOutList) > 0}">
        <table id="deviceTable" class="miniResultsTable">
            <tr class="<ct:alternateRow odd="" even="altRow"/>">
                <th><cti:msg key="yukon.dr.operator.optout.device"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.program"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.status"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.dateActive"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.actions"/></th>
            </tr>
            
            <c:forEach var="optOut" items="${currentOptOutList}">
                <tr class="<ct:alternateRow odd="" even="altRow"/>">
                    <td>${optOut.inventory.displayName}</td>
                    <td>
                        <c:forEach var="program" items="${optOut.programList}">
                            ${program.programName} 
                        </c:forEach>
                    </td>
                    <td><cti:msg key="${optOut.state.formatKey}"/></td>
                    <td>
                        <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                    </td>
                    <td>
                        <form action="/spring/stars/operator/optout/cancel" method="post">
                            <input type="hidden" name="eventId" value="${optOut.eventId}">
                            <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.cancel"/>">
                        </form>
                        <c:if test="${optOut.state == 'START_OPT_OUT_SENT'}">
                            <form action="/spring/stars/operator/optout/repeat" method="post">
                                <input type="hidden" name="inventoryId" value="${optOut.inventory.inventoryId}">
                                <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.repeat"/>">
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <br><cti:msg key="yukon.dr.operator.optout.noCurrentOptOuts"/>
    </c:otherwise>
</c:choose>

<br><br>
<!-- Opt Out Limits -->
<cti:msg key="yukon.dr.operator.optout.optOutLimits"/>

<table id="deviceTable" class="miniResultsTable">
    <tr class="<ct:alternateRow odd="" even="altRow"/>">
        <th><cti:msg key="yukon.dr.operator.optout.device"/></th>
        <th><cti:msg key="yukon.dr.operator.optout.used"/></th>
        <c:if test="${!noOptOutLimits}">
            <th><cti:msg key="yukon.dr.operator.optout.allowAdditional"/></th>
        </c:if>
        <th><cti:msg key="yukon.dr.operator.optout.remaining"/></th>
        <c:if test="${!noOptOutLimits}">
            <th><cti:msg key="yukon.dr.operator.optout.reset"/></th>
        </c:if>
    </tr>
    
    <c:forEach var="inventory" items="${displayableInventories}">
        <tr class="<ct:alternateRow odd="" even="altRow"/>">
            <td>${inventory.displayName}</td>
            <td>${inventory.usedOptOuts}</td>
            <c:if test="${!noOptOutLimits}">
	            <td>
	                <form action="/spring/stars/operator/optOut/allowAnother" method="post">
	                    <input type="hidden" name="inventoryId" value="${inventory.inventoryId}">
	                    <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.allowAnother"/>">
	                </form>
	            </td>
	        </c:if>
            <td>
                <c:choose>
                    <c:when test="${noOptOutLimits}">
                        <cti:msg key="yukon.dr.consumer.optout.unlimitedRemaining"/>
                    </c:when>
                    <c:otherwise>
                        ${inventory.remainingOptOuts}
                    </c:otherwise>
                </c:choose>
            </td>
            <c:if test="${!noOptOutLimits}">
	            <td>
	                <form action="/spring/stars/operator/optOut/resetToLimit" method="post">
	                    <input type="hidden" name="inventoryId" value="${inventory.inventoryId}">
	                    <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.clear"/>">
	                </form>
	            </td>
	        </c:if>
        </tr>
    </c:forEach>
</table>

<br><br>
<!-- Opt Out History -->
<cti:msg key="yukon.dr.operator.optout.optOutHistory"/>
<c:choose>
    <c:when test="${fn:length(previousOptOutList) > 0}">
        <table id="deviceTable" class="miniResultsTable">
            <tr class="<ct:alternateRow odd="" even="altRow"/>">
                <th><cti:msg key="yukon.dr.operator.optout.device"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.program"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.dateScheduled"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.dateActive"/></th>
                <th><cti:msg key="yukon.dr.operator.optout.durationHeader"/></th>
            </tr>
            
            <c:forEach var="optOut" items="${previousOptOutList}">
                <tr class="<ct:alternateRow odd="" even="altRow"/>">
                    <td>${optOut.inventory.displayName}</td>
                    <td>
                        <c:forEach var="program" items="${optOut.programList}">
                            ${program.programName} 
                        </c:forEach>
                    </td>
                    <td>
                       <cti:formatDate value="${optOut.scheduledDate}" type="DATEHM"/>
                    </td>
                    <td>
                       <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${optOut.state == 'SCHEDULE_CANCELED'}">
                                <cti:msg key="yukon.dr.operator.optout.canceled"/>
                            </c:when>
                            <c:otherwise>
                                <cti:formatTimePeriod startDate="${optOut.startDate}" endDate="${optOut.stopDate}" type="DH"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <input type="button" name="viewAll" value="<cti:msg key="yukon.dr.operator.optout.viewAll" />" onClick="location='OptOutHistory.jsp'">
        <br><br>
    </c:when>
    <c:otherwise>
        <br><cti:msg key="yukon.dr.operator.optout.noPreviousOptOuts"/>
    </c:otherwise>
</c:choose>
    