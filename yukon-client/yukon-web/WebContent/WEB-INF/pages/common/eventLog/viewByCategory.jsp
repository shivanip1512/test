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
        <form:form id="filter-form" modelAttribute="filter" action="downloadByCategory" method="POST">
            <cti:csrfToken/>
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
                    <tags:eventDateRangeInput startDatePath="startDate" stopDatePath="stopDate"></tags:eventDateRangeInput>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>

            <div class="action-area">
                <cti:button id="filterByCategory-btn" nameKey="filter" classes="primary action fl"/>
                <c:choose>
                    <c:when test="${maxCsvRows > searchResult.hitCount}">
                        <cti:button nameKey="download" icon="icon-page-excel" classes="js-download"/>
                    </c:when>
                    <c:otherwise>
                        <cti:button nameKey="download" id="download-btn" icon="icon-page-excel"/>
                        <d:confirm on="#download-btn" nameKey="confirmExport" okClasses="js-download"/>
                    </c:otherwise>
                </c:choose>
            </div>
        </form:form>
    </tags:sectionContainer>

    <%-- Event Log Results --%>
	<cti:msg var="eventsTitle" key="yukon.common.events.title" />
	<tags:sectionContainer title="${eventsTitle}">
        <cti:url var="dataUrl" value="/common/eventLog/filterByCategory"/>
        <div id="events-container" data-url="${dataUrl}">
            <jsp:include page="filteredResults.jsp"/>
        </div>
	</tags:sectionContainer>
    
    <cti:includeScript link="/resources/js/pages/yukon.support.eventlog.js"/>

</cti:standardPage>