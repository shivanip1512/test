<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="filterValue" tagdir="/WEB-INF/tags/filterValue" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage title="Event Log" module="support" page="eventViewer.byType">
    <cti:standardMenu menuSelection="events|byType" />
    <c:set var="baseUrl" value="/common/eventLog/viewByType"/>
    
    <cti:linkTabbedContainer>
        <cti:msg2 key=".byCategory.contextualPageName" var="byCategoryTab"/>
        <cti:msg2 key=".byType.contextualPageName" var="byTypeTab"/>
        <cti:linkTab selectorName="${byCategoryTab}" tabHref="viewByCategory"></cti:linkTab>
        <cti:linkTab selectorName="${byTypeTab}" tabHref="viewByType" initiallySelected="true"></cti:linkTab>
    </cti:linkTabbedContainer>
    
    <%-- Filtering popup --%>
    <cti:msg2 var="eventLogFiltersTitle" key=".eventLogFilters" />
    <tags:simplePopup id="filterPopup" title="${eventLogFiltersTitle}">
        <c:choose>
            <c:when test="${not empty eventLogTypeBackingBean}">
                <form:form id="filterForm" action="" commandName="eventLogTypeBackingBean" method="get">
                <form:hidden path="eventLogType"/>
                    <tags:nameValueContainer2>
        
                        <tags:nameValue2 nameKey=".eventLogDateRange">
                            <dt:dateRange startPath="startDate" endPath="stopDate" />
                        </tags:nameValue2>
        
                        <c:forEach var="eventLogFilter" items="${eventLogTypeBackingBean.eventLogFilters}" varStatus="status">
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
                    <div class="actionArea">
                        <cti:button nameKey="filter" type="submit" classes="action primary"/>
                    </div>
                </form:form>
            </c:when>
            <c:otherwise>
                <i:inline key=".noFilterOptionsAvailable"/>
            </c:otherwise>
        </c:choose>
    </tags:simplePopup>

    <%-- Event Type Section with Event Type popup tree --%>
    <table>
        <tr>
            <td>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".eventType">
                        <a id="showPopupButton" title="<cti:msg2 key=".eventLogTypeTreeSector.hoverText"/>" href="javascript:void(0);">
                        <c:choose>
                            <c:when test="${empty eventLogTypeBackingBean or empty eventLogTypeBackingBean.eventLogType}">
                                <span class="label empty-list"><i:inline key=".noEventLogTypeSelected" /></span>
                            </c:when>
                            <c:otherwise>
                                <span class="label">${eventLogTypeBackingBean.eventLogType}</span>
                            </c:otherwise>
                        </c:choose>
                            <i class="icon icon-folder"></i>
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
                                                            
                <jsTree:popupTree id="eventCategoryEditorTree"
                               treeCss="/WebConfig/yukon/styles/lib/dynatree/eventType.css"
                               triggerElement="showPopupButton"
                               dataJson="${allEventCategoriesDataJson}" highlightNodePath="${extSelectedNodePath}"
                               title="${selectEventLog}"
                               treeParameters="{onActivate: TreeHelper.redirect_node_data_href_onActivate}"
                               includeControlBar="true" styleClass="contained"/>
            </td>
        </tr>
        
    </table>
    <div class="actionArea stacked">
	    <c:choose>
	        <c:when test="${maxCsvRows > searchResult.hitCount}">
	            <cti:button nameKey="csvExport" href="${csvLink}" icon="icon-page-excel"/>
	        </c:when>
	        <c:otherwise>
	            <cti:button nameKey="csvExport" id="csvExportButton" icon="icon-page-excel"/>
	            <tags:confirmDialog nameKey=".confirmExport" on="#csvExportButton" href="${csvLink}" styleClass="f_closePopupOnSubmit" submitName="export"/>
	        </c:otherwise>
	    </c:choose>
    </div>
    <cti:msg var="eventsTitle" key="yukon.common.events.title"/>
    <tags:pagedBox filterDialog="filterPopup" title="${eventsTitle}" searchResult="${searchResult}" baseUrl="${baseUrl}" pageByHundereds="true">
    
        <c:choose>
            <c:when test="${searchResult.count == 0}">
                <span class="empty-list"><i:inline key="yukon.common.events.noResults"/></span>
            </c:when>
            <c:otherwise>
                <table class="compactResultsTable rowHighlighting" style="width: 100%;">
                    <tr>
                      <th><i:inline key=".dateAndTime"/></th>
                        <c:forEach items="${columnNames}" var="column">
                            <th title="${column.argumentColumn.columnName}">
                                <i:inline key="${column.label}"/>
                            </th>
                        </c:forEach>
                    </tr>
                    <c:forEach items="${searchResult.resultList}" var="row">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td title="<cti:msg2 htmlEscape="true" key="${row.eventLog.messageSourceResolvable}"/>"><cti:formatDate type="FULL" value="${row.eventLog.dateTime}"/></td>
                            <c:forEach items="${row.parameters}" var="parameter">
                                <td>
                                    <spring:escapeBody htmlEscape="true">${parameter}</spring:escapeBody>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </tags:pagedBox>

</cti:standardPage>