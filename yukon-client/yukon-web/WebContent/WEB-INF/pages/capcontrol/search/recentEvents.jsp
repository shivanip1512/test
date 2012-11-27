<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="recentEvents">
<%@include file="/capcontrol/capcontrolHeader.jspf"%>

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value="" />

    <div class="padBottom">
        <i:inline key=".filterByDate"/>
        <form action="/capcontrol/search/recentEvents" id="daysFilterForm">
            <input type="hidden" name="value" value="${paoIdString}">
            <select id="rcDateFilter" name="dayCnt" onchange="$('daysFilterForm').submit()">
                <c:forEach var="i" begin="1" end="7">
                    <option value="${i}" <c:if test="${i == dayCnt}">selected</c:if>><cti:msg2 key=".days" argument="${i}"/></option>
                </c:forEach>
            </select>
        </form>
    </div>

    <c:forEach var="eventSet" items="${listOfEventSets}">
    
        <tags:boxContainer2 nameKey="eventsContainer" 
                arguments="${eventSet.paoName}" 
                hideEnabled="true" 
                showInitially="true"
                styleClass="padBottom">
                
            <c:choose>
                <c:when test="${empty eventSet.controlEvents}">
                    <i:inline key=".noEvents"/>
                </c:when>
                <c:otherwise>
                    <table id="resTable${eventSet.paoId}" class="compactResultsTable">
                    
                        <thead>
                            <tr>
                                <th><i:inline key=".timestamp"/></th>
                                <th><i:inline key=".deviceControlled"/></th>
                                <th><i:inline key=".item"/></th>
                                <th><i:inline key=".event"/></th>
                                <th><i:inline key=".user"/></th>
                            </tr>
                        </thead>
                    
                        <tbody>
                        
                            <c:forEach var="event" items="${eventSet.controlEvents}">
                                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                
                                    <td>${event.formattedTimestamp}</td>
                                    
                                    <c:choose>
                                        <c:when test="${event.pointId == null || event.pointId <= 0}">
                                            <td><i:inline key="yukon.web.defaults.dashes"/></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><spring:escapeBody htmlEscape="true">${event.deviceControlled}</spring:escapeBody></td>
                                        </c:otherwise>
                                    </c:choose>
        
                                    <c:choose>
                                        <c:when test="${event.timestamp == null}">
                                            <td><i:inline key="yukon.web.defaults.dashes"/></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><spring:escapeBody htmlEscape="true">${event.item}</spring:escapeBody></td>
                                        </c:otherwise>
                                    </c:choose>
        
                                    <c:choose>
                                        <c:when test="${event.event == null}">
                                            <td><i:inline key="yukon.web.defaults.dashes"/></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><spring:escapeBody htmlEscape="true">${event.event}</spring:escapeBody></td>
                                        </c:otherwise>
                                    </c:choose>
        
                                    <c:choose>
                                        <c:when test="${event.user == null}">
                                            <td><i:inline key="yukon.web.defaults.dashes"/></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><spring:escapeBody htmlEscape="true">${event.user}</spring:escapeBody></td>
                                        </c:otherwise>
                                    </c:choose>
                                </tr>
                            </c:forEach>
                            
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </tags:boxContainer2>
            
    </c:forEach>
    
</cti:standardPage>