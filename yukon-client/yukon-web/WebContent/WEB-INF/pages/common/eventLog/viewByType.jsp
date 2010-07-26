<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>
<%@ taglib prefix="filterValue" tagdir="/WEB-INF/tags/filterValue" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i" %>

<cti:standardPage title="Event Log" module="support" page="byType">
<cti:standardMenu menuSelection="events|byType" />

    <%-- Filtering popup --%>
    <cti:msg2 var="eventLogFiltersTitle" key=".eventLogFilters" />
    <tags:simplePopup id="filterPopup" title="${eventLogFiltersTitle}">
        <c:choose>
            <c:when test="${not empty eventLogTypeBackingBean}">
                <form:form id="filterForm" action="" commandName="eventLogTypeBackingBean">
        
                    <tags:nameValueContainer2>
        
                        <tags:nameValue2 nameKey=".eventLogDateRange">
                            <tags:dateInputCalendar fieldName="startDate" springInput="true" showErrorOnNextLine="false"/>
                            <tags:dateInputCalendar fieldName="stopDate" springInput="true" />
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
                    <br>
                    <div style="text-align:right">
                        <input type="submit" value="<cti:msg2 key=".filter"/>" />
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
                        <c:choose>
                            <c:when test="${empty eventLogTypeBackingBean or 
                                     empty eventLogTypeBackingBean.eventLogType}">
                                <span class="subtleGray" style="font-style:italic;"><i:inline key=".noEventLogTypeSelected" /></span>
                            </c:when>
                            <c:otherwise>
                                <spring:escapeBody htmlEscape="true">${eventLogTypeBackingBean.eventLogType}</spring:escapeBody>
                            </c:otherwise>
                        </c:choose>
                        
                        <cti:img key="eventLogTypeTreeSector" id="showPopupButton"  />                        
                        
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
                                                            
                <ext:popupTree id="eventCategoryEditorTree"
                               treeAttributes="{}"
                               triggerElement="showPopupButton"
                               dataJson="${allEventCategoriesDataJson}"
                               title="${selectEventLog}"
                               width="432"
                               height="600" />

            </td>    
        </tr>
        
    </table>

    <div style="text-align: right;">
        <%-- GENERATE REPORTS --%>

        <cti:url var="csvExportUrl" value="/spring/common/eventLog/viewByType">
            <cti:param name="export" value="CSV"/>
        </cti:url>
        <cti:img key="csvExport" href="${csvExportUrl}"/>

    </div>

    <cti:msg var="eventsTitle" key="yukon.common.events.title"/>
    <tags:pagedBox filterDialog="filterPopup" title="${eventsTitle}" searchResult="${searchResults}" baseUrl="${baseUrl}" pageByHundereds="true">
    
        <c:choose>
            <c:when test="${empty dataGrid}">
                <table>
                    <tr>
                        <td colspan="2"><cti:msg key="yukon.common.events.noResults"/></td>
                    </tr>
                </table>
            </c:when>
            <c:otherwise>
                <table class="compactResultsTable rowHighlighting" style="width: 100%;">
                    <tr>
                        <c:forEach items="${columnNames}" var="columnName">
                            <th>
                                <spring:escapeBody htmlEscape="true">${columnName}</spring:escapeBody>
                            </th>
                        </c:forEach>
                    </tr>
                    <c:forEach items="${dataGrid}" var="dataRow">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <c:forEach items="${dataRow}" var="dataEntry">
                                <td>
                                    <spring:escapeBody htmlEscape="true">${dataEntry}</spring:escapeBody>
                                </td>
                            </c:forEach>
                        </tr>
                    </c:forEach>
                </table>
            </c:otherwise>
        </c:choose>
    </tags:pagedBox>

</cti:standardPage>