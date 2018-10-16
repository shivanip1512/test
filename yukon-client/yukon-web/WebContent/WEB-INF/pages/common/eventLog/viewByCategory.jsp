<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support" page="eventViewer.byCategory">

<script>
$(function () {
    $('#events-container').attr('data-url', 'viewByCategory?' + $('#filter-form').serialize());
    
    $('#filter-btn').click(function (ev) {
        var itemsPerPage = $('#events-container .paging-area').data('pageSize');
        $('#filter-form').append('<input type="hidden" name="itemsPerPage" value="' + itemsPerPage + '">');
        $('#filter-form').submit();
    });
});
</script>
    
    <cti:linkTabbedContainer>
        <cti:msg2 key=".byCategory.contextualPageName" var="byCategoryTab"/>
        <cti:msg2 key=".byType.contextualPageName" var="byTypeTab"/>
        <cti:linkTab selectorName="${byCategoryTab}" initiallySelected="true">
            <c:url value="viewByCategory" />
        </cti:linkTab>
        <cti:linkTab selectorName="${byTypeTab}">
            <c:url value="viewByType" />
        </cti:linkTab>
    </cti:linkTabbedContainer>

    <%-- Filter Options --%>
    <cti:msg2 var="filterOptionsLabel" key=".filterOptions"/>
    <tags:sectionContainer title="${filterOptionsLabel}">
        <form:form id="filter-form" action="viewByCategory" modelAttribute="filter" method="get">
            <tags:nameValueContainer2>
                
                <tags:nameValue2 nameKey=".categories">
                    <form:select path="categories" multiple="true" size="5">
                        <form:options items="${eventCategoryList}"/>
                    </form:select>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".filterValue">
                    <tags:input path="filterValue" autocomplete="off"/>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".dateRange">
                    <span class="dateRangeInputField">
                        <dt:dateRange startPath="startDate" endPath="stopDate" />
                    </span>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>

            <div class="action-area">
                <cti:button id="filter-btn" nameKey="filter" classes="primary action fl"/>
                <c:choose>
                    <c:when test="${maxCsvRows > searchResult.hitCount}">
                        <cti:button nameKey="download" href="${csvLink}" icon="icon-page-excel"/>
                    </c:when>
                    <c:otherwise>
                        <cti:button nameKey="download" id="download-btn" icon="icon-page-excel" href="${csvLink}"/>
                        <d:confirm on="#download-btn" nameKey="confirmExport"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </tags:sectionContainer>

    <%-- Event Log Results --%>
    <cti:msg var="eventsTitle" key="yukon.common.events.title"/>
    <tags:sectionContainer title="${eventsTitle}">
        <c:choose>
            <c:when test="${empty searchResult.resultList}">
                <span class="empty-list"><i:inline key="yukon.common.events.noResults"/></span>
            </c:when>
            <c:otherwise>
                <div id="events-container" data-url="${url}" data-static>
                    <table class="compact-results-table">
                        <thead>
                            <tr>
                                <th><i:inline key="yukon.common.events.columnHeader.event"/></th>
                                <th><i:inline key="yukon.common.events.columnHeader.dateAndTime"/></th>
                                <th><i:inline key="yukon.common.events.columnHeader.message"/></th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach items="${searchResult.resultList}" var="event">
                                <tr>
                                    <td>${fn:escapeXml(event.eventType)}</td>
                                    <td><cti:formatDate type="BOTH" value="${event.dateTime}" /></td>
                                    <td><i:inline key="${event.messageSourceResolvable}" htmlEscape="true"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <tags:pagingResultsControls result="${searchResult}" adjustPageCount="true" hundreds="true"/>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer>

</cti:standardPage>