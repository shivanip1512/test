<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage title="Event Log" module="support" page="eventViewer.byCategory">
    <cti:standardMenu menuSelection="events|byCategory" />
    <c:set var="baseUrl" value="/common/eventLog/viewByCategory"/>
    
    <cti:linkTabbedContainer>
        <cti:msg2 key=".byCategory.contextualPageName" var="byCategoryTab"/>
        <cti:msg2 key=".byType.contextualPageName" var="byTypeTab"/>
        <cti:linkTab selectorName="${byCategoryTab}" tabHref="viewByCategory" initiallySelected="true"></cti:linkTab>
        <cti:linkTab selectorName="${byTypeTab}" tabHref="viewByType" ></cti:linkTab>
    </cti:linkTabbedContainer>
    
    <%-- Filter Options --%>
    <cti:msg2 var="filterOptionsLabel" key=".filterOptions"/>
    <tags:sectionContainer title="${filterOptionsLabel}">
        <form:form id="byCategoryForm" action="/common/eventLog/viewByCategory" 
                   commandName="eventLogCategoryBackingBean" method="get">
            <tags:hidden path="itemsPerPage"/>
 
            <tags:nameValueContainer2>
                
                <tags:nameValue2 nameKey=".categories">
                    <form:select path="categories" multiple="true" size="5">
                        <form:options items="${eventCategoryList}"/>
                    </form:select>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".filterValue">
                    <tags:input path="filterValue" autocomplete="false"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".dateRange">
                    <span class="dateRangeInputField">
						<dt:dateRange startPath="startDate" endPath="stopDate" />
                    </span>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            <br>
            <button type="submit"><i class="icon icon-filter"></i><span class="label"><i:inline key=".filterButton"/></span></button>
            <c:choose>
	            <c:when test="${maxCsvRows > searchResult.hitCount}">
	                <cti:button nameKey="csvExport" href="${csvLink}" icon="icon-page-excel"/>
	            </c:when>
	            <c:otherwise>
	                <cti:button nameKey="csvExport" id="csvExportButton" icon="icon-page-excel"/>
	                <tags:confirmDialog nameKey=".confirmExport" on="#csvExportButton" href="${csvLink}" styleClass="f_closePopupOnSubmit" submitName="export"/>
	            </c:otherwise>
	        </c:choose>
        </form:form>
    </tags:sectionContainer>

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
            <c:forEach items="${searchResult.resultList}" var="event">
                <tr>
                    <td nowrap="nowrap"><spring:escapeBody htmlEscape="true">${event.eventType}</spring:escapeBody></td>
                    <td nowrap="nowrap"><cti:formatDate type="BOTH" value="${event.dateTime}" /></td>
                    <td><cti:msg key="${event.messageSourceResolvable}" /></td>
                </tr>
            </c:forEach>
            <c:if test="${empty searchResult.resultList}">
            <tr>
                <td colspan="3"><cti:msg key="yukon.common.events.noResults"/></td>
            </tr>
        </c:if>
        </table>
    </tags:pagedBox>

</cti:standardPage>