<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<cti:standardPage title="Event Log" module="support" page="byCategory">
    <cti:standardMenu menuSelection="events|byCategory" />
    <c:set var="baseUrl" value="/spring/common/eventLog/viewByCategory"/>
    
    <%-- Filter Options --%>
    <tags:sectionContainer title="Filter Options">
        <form:form id="byCategoryForm" action="/spring/common/eventLog/viewByCategory" 
                   commandName="eventLogCategoryBackingBean">
            <tags:hidden path="itemsPerPage"/>
 
            <tags:nameValueContainer2>
                
                <tags:nameValue2 nameKey=".categories">
                    <select name="categories" multiple size="4">
                        <c:forEach items="${eventCategoryList}" var="eventCategory">
                            <c:set var="selected" value="${selectedCategories[eventCategory] ? 'selected' : ''}"/>
                            <option ${selected}><spring:escapeBody htmlEscape="true">${eventCategory.fullName}</spring:escapeBody></option>
                        </c:forEach>
                    </select>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".filterValue">
                    <tags:input path="filterValue" autocomplete="false"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".dateRange">
                    <span class="dateRangeInputField">
                        <tags:dateInputCalendar fieldName="startDate" springInput="true" showErrorOnNextLine="false"/> - 
                        <tags:dateInputCalendar fieldName="stopDate" springInput="true"/>
                    </span>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            <br>
            <input value="Filter" type="submit">
        </form:form>
    </tags:sectionContainer>
    <br>

    <%-- Event Log Results --%>
    <cti:msg var="eventsTitle" key="yukon.common.events.title"/>
    <tags:pagedBox title="${eventsTitle}" searchResult="${searchResult}" baseUrl="${baseUrl}" pageByHundereds="true">
        <table class="compactResultsTable rowHighlighting">
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