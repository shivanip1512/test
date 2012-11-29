<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<h3><cti:msg key="yukon.dr.operator.optout.header"/></h3>

<c:if test="${!empty currentOptOutList && allOptedOut}">
	<cti:msg key="yukon.dr.operator.optout.allOptedOut"/>
</c:if>
<c:if test="${!empty currentOptOutList && !optOutsAvailable}">
    <cti:msg key="yukon.dr.operator.optout.noOptOutsAvailable"/>
</c:if>

<c:if test="${!allOptedOut}">
	<cti:msg key="yukon.dr.operator.optout.description"/><br><br>

	<form action="/stars/operator/optout/optout2" method="POST">
	    <table>
	        <tr>
	            <td align="right">
	                <cti:msg key="yukon.dr.operator.optout.startDate"/>
	            </td>

	            <cti:formatDate  value="${currentDate}" type="DATE" var="formattedDate"/>

	            <cti:getProperty var="optOutTodayOnly" property="ConsumerInfoRole.OPT_OUT_TODAY_ONLY" />

                <td align="left">
                    <c:choose>
                        <c:when test="${optOutTodayOnly}">
                            <input type="hidden" name="startDate" value="${formattedDate}" />
                            <spring:escapeBody htmlEscape="true">${formattedDate}</spring:escapeBody>
                        </c:when>
                        <c:otherwise>
                        	<dt:date name="startDate" value="${currentDate}" />
                        </c:otherwise>
                    </c:choose>
                </td>
	        </tr>

	        <tr>
	            <td align="right">
	                <cti:msg key="yukon.dr.operator.optout.duration"/>
	            </td>

	            <td align="left">
	                <select name="durationInDays">
	                    
	                    <c:forEach var="optOutPeriod" items="${optOutPeriodList}">
	                    
	                       <c:set var="key" value="${(optOutPeriod == 1) ? 'yukon.dr.operator.optout.day' : 'yukon.dr.operator.optout.days' }"/>
	                    
	                       <option value="${optOutPeriod}"><spring:escapeBody htmlEscape="true">${optOutPeriod}</spring:escapeBody><cti:msg key="${key}"/></option>
	                    
	                    </c:forEach>
	                    
	                </select>
	            </td>
	        </tr>

	        <tr>
	            <td align="center" colspan="2">
	                <br>
	                <input type="submit" value="<cti:msg key='yukon.dr.operator.optout.apply'/>" class="formSubmit">
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
        <table id="deviceTable" class="resultsTable">
            <thead>
                <tr>
                    <th><cti:msg key="yukon.dr.operator.optout.device"/></th>
                    <th><cti:msg key="yukon.dr.operator.optout.program"/></th>
                    <th><cti:msg key="yukon.dr.operator.optout.status"/></th>
                    <th><cti:msg key="yukon.dr.operator.optout.dateActive"/></th>
                    <th><cti:msg key="yukon.dr.operator.optout.actions"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="optOut" items="${currentOptOutList}">
                    <tr class="<ct:alternateRow odd="" even="altRow"/>">
                        <td><spring:escapeBody htmlEscape="true">${optOut.inventory.displayName}</spring:escapeBody></td>
                        <td>
                            <c:forEach var="program" items="${optOut.programList}">
                                <spring:escapeBody htmlEscape="true">${program.programName}</spring:escapeBody>
                            </c:forEach>
                        </td>
                        <td><cti:msg key="${optOut.state.formatKey}"/></td>
                        <td>
                            <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                        </td>
                        <td>
                            <form action="/stars/operator/optout/cancel" method="post">
                                <input type="hidden" name="eventId" value="${optOut.eventId}">
                                <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.cancel"/>" class="formSubmit">
                            </form>
                            <c:if test="${optOut.state == 'START_OPT_OUT_SENT'}">
                                <form action="/stars/operator/optout/repeat" method="post">
                                    <input type="hidden" name="inventoryId" value="${optOut.inventory.inventoryId}">
                                    <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.repeat"/>" class="formSubmit">
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <br><cti:msg key="yukon.dr.operator.optout.noCurrentOptOuts"/>
    </c:otherwise>
</c:choose>

<br><br>
<!-- Opt Out Limits -->
<cti:msg key="yukon.dr.operator.optout.optOutLimits"/>

<table id="deviceTable" class="resultsTable">
    <thead>
        <tr>
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
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="inventory" items="${displayableInventories}">
            <tr class="<ct:alternateRow odd="" even="altRow"/>">
                <td><spring:escapeBody htmlEscape="true">${inventory.displayName}</spring:escapeBody></td>
                <td>${optOutCounts[inventory.inventoryId].usedOptOuts}</td>
                <c:if test="${!noOptOutLimits}">
    	            <td>
    	                <form action="/stars/operator/optOut/allowAnother" method="post">
    	                    <input type="hidden" name="inventoryId" value="${inventory.inventoryId}">
    	                    <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.allowAnother"/>" class="formSubmit">
    	                </form>
    	            </td>
    	        </c:if>
                <td>
                    <c:choose>
                        <c:when test="${noOptOutLimits}">
                            <cti:msg key="yukon.dr.operator.optout.unlimitedRemaining"/>
                        </c:when>
                        <c:otherwise>
                            ${optOutCounts[inventory.inventoryId].remainingOptOuts}
                        </c:otherwise>
                    </c:choose>
                </td>
                <c:if test="${!noOptOutLimits}">
    	            <td>
    	               <c:choose>
    	                   <c:when test="${optOutLimit <= optOutCounts[inventory.inventoryId].remainingOptOuts}">
    	                       <cti:msg key="yukon.dr.operator.optout.atLimit"/>
    	                   </c:when>
    	                   <c:otherwise>
    			                <form action="/stars/operator/optOut/resetToLimit" method="post">
    			                    <input type="hidden" name="inventoryId" value="${inventory.inventoryId}">
    			                    <input type="submit" name="submit" value="<cti:msg key="yukon.dr.operator.optout.clear"/>" class="formSubmit">
    			                </form>
    	                   </c:otherwise>
    	               </c:choose>
    	            </td>
    	        </c:if>
            </tr>
        </c:forEach>
    </tbody>
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
                    <td><spring:escapeBody htmlEscape="true">${optOut.inventory.displayName}</spring:escapeBody></td>
                    <td>
                        <c:forEach var="program" items="${optOut.programList}">
                            <spring:escapeBody htmlEscape="true">${program.programName}</spring:escapeBody> 
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
        <input type="button" name="viewAll" value="<cti:msg key="yukon.dr.operator.optout.viewAll" />" onClick="location='OptOutHistory.jsp'" class="formSubmit">
        <br><br>
    </c:when>
    <c:otherwise>
        <br><cti:msg key="yukon.dr.operator.optout.noPreviousOptOuts"/>
    </c:otherwise>
</c:choose>
    