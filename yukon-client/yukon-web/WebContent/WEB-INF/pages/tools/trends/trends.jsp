<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="${pageName}">
    
    <div id="label-json" class="dn">${fn:escapeXml(labels)}</div>
    
    <div id="page-buttons">
        <c:if test="${not empty trends}">
            <cm:dropdown type="button" icon="icon-download" key="yukon.web.components.button.export.label">
                <cm:dropdownOption key=".printChart" icon="icon-printer" classes="js-print"/>
                <li class="divider">
                <cm:dropdownOption key=".downloadPng" icon="icon-picture" classes="js-dl-png"/>
                <cm:dropdownOption key=".downloadJpg" icon="icon-picture" classes="js-dl-jpg"/>
                <cm:dropdownOption key=".downloadPdf" icon="icon-page-white-acrobat" classes="js-dl-pdf"/>
                <li class="divider">
                <cm:dropdownOption key=".downloadCsv" icon="icon-page-white-excel" classes="js-dl-csv" data-trend-id="${trendId}"/>
            </cm:dropdown>
        </c:if>
    </div>

    <div class="column-7-17 clearfix">
        <c:choose>
            <c:when test="${not empty trends}">
            <div class="column one">
                <div class="side-nav trend-list">
                    <ul>
                        <c:forEach var="trend" items="${trends}">
                            <c:if test="${trendId == trend.graphDefinitionID}"><c:set var="selected" value="selected"/></c:if>
                            <c:if test="${trendId != trend.graphDefinitionID}"><c:set var="selected" value=""/></c:if>
                            <li data-graph-id="${trend.graphDefinitionID}" class="${selected}">
                                <a href="<cti:url value="/tools/trends/${trend.graphDefinitionID}"/>">${fn:escapeXml(trend.name)}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
                <div class="column two nogutter trend-container content-box no-transitions pr" data-trend="${trendId}"><div class="glass"></div></div>
            </c:when>
            <c:otherwise>
            <div class="column one">
                <span class="empty-list"><i:inline key=".trend.noTrends"/></span>
            </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.trends.js"/>
    <cti:includeScript link="/resources/js/common/yukon.trends.js"/>
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="HIGH_STOCK_EXPORTING"/>
    <cti:includeScript link="HIGH_STOCK_NO_DATA"/>
    
</cti:standardPage>