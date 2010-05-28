<%@ attribute name="previousOptOutList" required="true" type="java.util.List" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<c:choose>
    <c:when test="${fn:length(previousOptOutList) > 0}">
        <table id="deviceTable" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th class="nonwrapping"><i:inline key=".device"/></th>
                <th><i:inline key=".program"/></th>
                <th class="nonwrapping"><i:inline key=".dateActive"/></th>
                <th class="nonwrapping"><i:inline key=".durationHeader"/></th>
                <th class="nonwrapping"><i:inline key=".actionLog"/></th>
            </tr>
            
            <c:forEach var="optOut" items="${previousOptOutList}">
                <tr class="<tags:alternateRow odd="altRow" even=""/>">
                    <td valign="top" class="nonwrapping">
                        <spring:escapeBody htmlEscape="true">${optOut.inventory.displayName}</spring:escapeBody>
                    </td>
                    <td valign="top">
                        <c:forEach var="program" items="${optOut.programList}" varStatus="status">
                            <c:if test="${status.count != 1}"><br></c:if>
                            <spring:escapeBody htmlEscape="true">${program.programName}</spring:escapeBody>  
                        </c:forEach>
                    </td>
                    <td valign="top" class="nonwrapping">
                       <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                    </td>
                    <td valign="top" class="nonwrapping">
                        <c:choose>
                            <c:when test="${optOut.state == 'SCHEDULE_CANCELED'}">
                                <i:inline key=".canceled"/>
                            </c:when>
                            <c:otherwise>
                                <cti:formatTimePeriod startDate="${optOut.startDate}" endDate="${optOut.stopDate}" type="DH_ABBR"/>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td valign="top" class="nonwrapping">
                        <c:forEach var="actionLogMessage" items="${previousOptOutDetails[optOut.eventId]}">
                            <cti:msg2 key="${actionLogMessage}"/><br>
                        </c:forEach>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:when>
    <c:otherwise>
        <i:inline key=".noPreviousOptOuts"/>
    </c:otherwise>
</c:choose>