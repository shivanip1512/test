<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="filterValue" tagdir="/WEB-INF/tags/filterValue" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="eventViewer.byType">
<cti:includeScript link="/resources/js/pages/yukon.support.eventlog.type.js"/>

    <cti:linkTabbedContainer>
        <cti:msg2 key=".byCategory.contextualPageName" var="byCategoryTab"/>
        <cti:msg2 key=".byType.contextualPageName" var="byTypeTab"/>
        <cti:linkTab selectorName="${byCategoryTab}">
            <c:url value="viewByCategory" />
        </cti:linkTab>
        <cti:linkTab selectorName="${byTypeTab}" initiallySelected="true">
            <c:url value="viewByType" />
        </cti:linkTab>
    </cti:linkTabbedContainer>
   
    <%-- Filtering popup --%>
    <cti:msg2 var="eventLogFiltersTitle" key=".eventLogFilters" />
   

    <%-- Event Type Section with Event Type popup tree --%>
    <table>
        <tr>
            <td>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".eventType">
                        <a id="showPopupButton" title="<cti:msg2 key=".eventLogTypeTreeSector.hoverText"/>" href="javascript:void(0);">
                            <c:choose>
                                <c:when test="${empty filter or empty filter.eventLogType}">
                                    <span class="empty-list"><i:inline key=".noEventLogTypeSelected" /></span>
                                </c:when>
                                <c:otherwise>
                                    <span>${filter.eventLogType}</span>
                                </c:otherwise>
                            </c:choose>
                            <cti:icon icon="icon-folder" classes="fn"/>
                        </a>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </td>
        </tr>
    
        <tr>
            <td>
                <%-- EVENT CATEGORY HIERARCHY BOX --%>
                <cti:msg2 var="selectEventLog" key=".selectEventLog"/>
                <cti:msg2 var="cancel" key=".cancel"/>
                <cti:msg2 var="noEventLogSelected" key=".noEventLogSelected"/>
                                                            
                <t:popupTree id="eventCategoryEditorTree"
                             styleClass="event-tree"
                             triggerElement="showPopupButton"
                             dataJson="${allEventCategoriesDataJson}" 
                             highlightNodePath="${extSelectedNodePath}"
                             title="${selectEventLog}"
                             treeParameters="{onActivate: yukon.dynatree.redirectOnActivate}"
                             includeControlBar="true"/>
            </td>
        </tr>
       
    </table>
     <c:choose>
            <c:when test="${not empty filter}">
                <form:form id="filter-form" modelAttribute="filter" method="get">
                    <form:hidden path="eventLogType"/>
                    <tags:nameValueContainer2 >
        
                        <tags:nameValue2 nameKey=".eventLogDateRange">
                            <dt:dateRange startPath="startDate" endPath="stopDate" />
                        </tags:nameValue2>
        
                        <c:forEach var="eventLogFilter" items="${filter.eventLogFilters}" varStatus="status">
                            <c:if test="${eventLogFilter.eventLogColumnType eq 'STRING'}" >
                                <filterValue:stringFilterValue eventLogFilter="${eventLogFilter}" count="${status.count}" />
                            </c:if>
                            <c:if test="${eventLogFilter.eventLogColumnType eq 'NUMBER'}" >
                                <filterValue:numberFilterValue eventLogFilter="${eventLogFilter}" count="${status.count}"/>
                            </c:if>
                            <c:if test="${eventLogFilter.eventLogColumnType eq 'DATE'}">
                                <filterValue:dateFilterValue eventLogFilter="${eventLogFilter}" count="${status.count}" />
                            </c:if>
                        </c:forEach>
                        
                    </tags:nameValueContainer2>
                </form:form>
            </c:when>
        </c:choose>
    <div class="action-area stacked">
        <c:if test="${not empty filter}">
            <cti:button id="filter-btn" nameKey="filter" classes="action primary fl"/>
        </c:if>
        <c:choose>
            <c:when test="${maxCsvRows > searchResult.hitCount}">
                <cti:button nameKey="download" href="${csvLink}" icon="icon-page-excel"/>
            </c:when>
            <c:otherwise>
                <cti:button nameKey="download" href="${csvLink}" id="csvExportButton" icon="icon-page-excel"/>
                <d:confirm on="#csvExportButton" nameKey="confirmExport"/>
            </c:otherwise>
        </c:choose>
    </div>
    
  
    
    <cti:msg var="eventsTitle" key="yukon.common.events.title"/>
   
    <tags:sectionContainer title="${eventsTitle}" controls="${controls}">
        <c:choose>
            <c:when test="${fn:length(searchResult.resultList) == 0}">
                <span class="empty-list"><i:inline key="yukon.common.events.noResults"/></span>
            </c:when>
            <c:otherwise>
                <div id="events-container" data-static>
                    <table class="compact-results-table">
                        <thead>
                            <tr>
                                <th><i:inline key=".dateAndTime"/></th>
                                <c:forEach items="${columnNames}" var="column">
                                    <th title="${column.argumentColumn.columnName}"><i:inline key="${column.label}"/></th>
                                </c:forEach>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach items="${searchResult.resultList}" var="row">
                                <tr>
                                <td title="<cti:msg2 htmlEscape="true" key="${row.eventLog.messageSourceResolvable}"/>"><cti:formatDate type="FULL" value="${row.eventLog.dateTime}"/></td>
                                    <c:forEach items="${row.parameters}" var="parameter">
                                        <td>${fn:escapeXml(parameter)}</td>
                                    </c:forEach>
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