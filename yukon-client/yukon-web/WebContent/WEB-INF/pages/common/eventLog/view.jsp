<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage title="Event Log" module="support">
<cti:standardMenu menuSelection="other|events" />
    <c:set var="baseUrl" value="/spring/common/eventLog/view"/>
    
    <tags:sectionContainer title="Filter Options">
        <form method="get" action="view">
            <c:if test="${not empty itemsPerPage}">
                <input type="hidden" name="itemsPerPage" value="${itemsPerPage}"/>
            </c:if>
            <tags:nameValueContainer>
                <tags:nameValue name="Categories" nameColumnWidth="110px">
                    <select name="categories" multiple size="4">
                        <c:forEach items="${eventCategoryList}" var="eventCategory">
                            <c:set var="selected" value="${selectedCategories[eventCategory] ? 'selected' : ''}"/>
                            <option ${selected}><spring:escapeBody htmlEscape="true">${eventCategory.fullName}</spring:escapeBody></option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                <tags:nameValue name="Date Range">
                    <cti:formatDate var="fromDateStr" type="DATE" value="${fromDate}" nullText=""/>
                    <tags:dateInputCalendar fieldName="fromDate" fieldValue="${fromDateStr}"/>
                    
                    <cti:formatDate var="toDateStr" type="DATE" value="${toDate}" nullText=""/>
                    <tags:dateInputCalendar fieldName="toDate" fieldValue="${toDateStr}"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            <br>
            <input value="Filter" type="submit">
        </form>
    </tags:sectionContainer>
    <br>

    <cti:msg var="eventsTitle" key="yukon.common.events.title"/>
    <tags:pagedBox title="${eventsTitle}" searchResult="${searchResult}" baseUrl="${baseUrl}" pageByHundereds="true">
        <table class="compactResultsTable rowHighlighting" style="width: 100%;">
            <tr>
                <th>
                    <cti:msg key="yukon.common.events.columnHeader.event"/>
                </th>
                <th>
                    <cti:msg key="yukon.common.events.columnHeader.dateAndTime"/>
                </th>
                <th>
                    <cti:msg key="yukon.common.events.columnHeader.message"/>
                </th>
            </tr>
            <c:forEach items="${events}" var="event">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><spring:escapeBody htmlEscape="true">${event.eventType}</spring:escapeBody></td>
                    <td><cti:formatDate type="BOTH" value="${event.dateTime}" /></td>
                    <td><cti:msg key="${event.messageSourceResolvable}" /></td>
                </tr>
            </c:forEach>
            <c:if test="${empty events}">
            <tr>
                <td colspan="3"><cti:msg key="yukon.common.events.noResults"/></td>
            </tr>
        </c:if>
        </table>
    </tags:pagedBox>

</cti:standardPage>