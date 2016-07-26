<%@page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>


<h3><cti:msg key="yukon.web.modules.operator.optOut.header"/></h3>

<c:if test="${!empty currentOptOutList && allOptedOut}">
    <cti:msg key="yukon.web.modules.operator.optOut.allOptedOut"/>
</c:if>
<c:if test="${!empty currentOptOutList && !optOutsAvailable}">
    <cti:msg key="yukon.web.modules.operator.optOut.noOptOutsAvailable"/>
</c:if>
<c:if test="${!allOptedOut}">
    <cti:msg key="yukon.web.modules.operator.optOut.description"/><br><br>
    
    <form action="<cti:url value="stars/operator/optout/optout2"/>" method="POST">
        <cti:csrfToken/>
        <table>
            <tr>
                <td align="right">
                    <cti:msg key="yukon.web.modules.operator.optOut.startDate"/>
                </td>

                <cti:formatDate  value="${currentDate}" type="DATE" var="formattedDate"/>
	            <cti:getProperty var="optOutTodayOnly" property="RESIDENTIAL_OPT_OUT_TODAY_ONLY" />

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
                    <cti:msg key="yukon.web.modules.operator.optOut.duration"/>
                </td>

                <td align="left">
                    <select name="durationInDays">
                        
                        <c:forEach var="optOutPeriod" items="${optOutPeriodList}">
                        
                           <c:set var="key" value="${(optOutPeriod == 1) ? 'yukon.web.modules.operator.optOut.day' : 'yukon.web.modules.operator.optOut.days' }"/>
                        
                           <option value="${optOutPeriod}"><spring:escapeBody htmlEscape="true">${optOutPeriod}</spring:escapeBody><cti:msg key="${key}"/></option>
                        
                        </c:forEach>
                        
                    </select>
                </td>
            </tr>
        </table>
        <div class="page-action-area">
            <cti:msg var="apply" key="yukon.web.modules.operator.optOut.apply"/>
            <cti:button type="submit" value="${apply}" label="${apply}"/>
        </div>
    </form>
</c:if>


<br><br>
<!-- Current Opt Outs -->
<cti:msg key="yukon.web.modules.operator.optOut.currentOptOuts"/>

<c:choose>
    <c:when test="${fn:length(currentOptOutList) > 0}">
        <table id="deviceTable" class="results-table">
            <thead>
                <tr>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.device"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.program"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.status"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.dateActive"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.actions"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="optOut" items="${currentOptOutList}">
                    <tr>
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
                            <form action="<cti:url value="/stars/operator/optout/cancel"/>" method="post">
                                <cti:csrfToken/>
                                <input type="hidden" name="eventId" value="${optOut.eventId}">
                                <cti:msg var="cancel" key="yukon.web.modules.operator.optOut.cancel"/>
                                <cti:button type="submit" name="submit" value="${cancel}" label="${cancel}"/>
                            </form>
                            <c:if test="${optOut.state == 'START_OPT_OUT_SENT'}">
                                <form action="<cti:url value="/stars/operator/optout/repeat"/>" method="post">
                                    <cti:csrfToken/>
                                    <input type="hidden" name="inventoryId" value="${optOut.inventory.inventoryId}">
                                    <cti:msg var="repeat" key="yukon.web.modules.operator.optOut.repeat"/>
                                    <cti:button type="submit" name="submit" value="${repeat}" label="${repeat}"/>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:when>
    <c:otherwise>
        <br><cti:msg key="yukon.web.modules.operator.optOut.noCurrentOptOuts"/>
    </c:otherwise>
</c:choose>

<br><br>
<!-- Opt Out Limits -->
<cti:msg key="yukon.web.modules.operator.optOut.optOutLimits"/>

<table id="deviceTable" class="results-table">
    <thead>
        <tr>
            <th><cti:msg key="yukon.web.modules.operator.optOut.device"/></th>
            <th><cti:msg key="yukon.web.modules.operator.optOut.used"/></th>
            <c:if test="${!noOptOutLimits}">
                <th><cti:msg key="yukon.web.modules.operator.optOut.allowAdditional"/></th>
            </c:if>
            <th><cti:msg key="yukon.web.modules.operator.optOut.remaining"/></th>
            <c:if test="${!noOptOutLimits}">
                <th><cti:msg key="yukon.web.modules.operator.optOut.reset"/></th>
            </c:if>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="inventory" items="${displayableInventories}">
            <tr>
                <td><spring:escapeBody htmlEscape="true">${inventory.displayName}</spring:escapeBody></td>
                <td>${optOutCounts[inventory.inventoryId].usedOptOuts}</td>
                <c:if test="${!noOptOutLimits}">
                    <td>
                        <form action="<cti:url value="/stars/operator/optOut/allowAnother"/>" method="post">
                            <cti:csrfToken/>
                            <input type="hidden" name="inventoryId" value="${inventory.inventoryId}">
                            <cti:msg key="yukon.web.modules.operator.optOut.allowAnother" var="allowAnother"/>
                            <cti:button type="submit" name="submit" value="${allowAnother}" label="${allowAnother}"/>
                        </form>
                    </td>
                </c:if>
                <td>
                    <c:choose>
                        <c:when test="${noOptOutLimits}">
                            <cti:msg key="yukon.web.modules.operator.optOut.unlimitedRemaining"/>
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
                               <cti:msg key="yukon.web.modules.operator.optOut.atLimit"/>
                           </c:when>
                           <c:otherwise>
                                <form action="<cti:url value="/stars/operator/optOut/resetToLimit"/>" method="post">
                                    <cti:csrfToken/>
                                    <input type="hidden" name="inventoryId" value="${inventory.inventoryId}">
                                    <cti:msg var="clear" key="yukon.web.modules.operator.optOut.clear"/>
                                    <cti:button type="submit" name="submit" value="${clear}" label="${clear}"/>
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
<cti:msg key="yukon.web.modules.operator.optOut.optOutHistory"/>
<c:choose>
    <c:when test="${fn:length(previousOptOutList) > 0}">
        <table id="deviceTable" class="results-table">
            <thead>
                <tr>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.device"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.program"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.dateScheduled"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.dateActive"/></th>
                    <th><cti:msg key="yukon.web.modules.operator.optOut.durationHeader"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="optOut" items="${previousOptOutList}">
                    <tr>
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
                                    <cti:msg key="yukon.web.modules.operator.optOut.canceled"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:formatTimePeriod startDate="${optOut.startDate}" endDate="${optOut.stopDate}" type="DH"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <cti:msg key="yukon.web.modules.operator.optOut.viewAll" var="viewAll"/>
        <cti:button name="viewAll" value="${viewAll}" label="${viewAll}" onclick="location='OptOutHistory.jsp'"/>
        <br><br>
    </c:when>
    <c:otherwise>
        <br><cti:msg key="yukon.web.modules.operator.optOut.noPreviousOptOuts"/>
    </c:otherwise>
</c:choose>
