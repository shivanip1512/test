<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="${pageName}">

    <div id="error-message" class="user-message error dn" >
        <cti:msg2 key="yukon.web.error.pageUpdateFailed"/>
    </div>
    
    <div id="reset-peak-error-message" class="dn"></div>
    <div id="reset-peak-success-message" class="dn"></div>

    <cti:msg var="resetPeakLbl" key="yukon.web.modules.tools.trend.resetPeak"/>
    
    <div id="label-json" class="dn">${fn:escapeXml(labels)}</div>
    
    <div id="page-actions" class="dn">
        <cti:url value="/tools/trend/create" var="createUrl"/>
        <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" href="${createUrl}"/>
    </div>
    
    <div id="page-buttons">
        <c:if test="${not empty trends}">
            <span class="form-control"><i:inline key=".trends.autoRefresh"/></span>
            <div id="trend-updater" class="button-group button-group-toggle">
                <c:set var="onClasses" value="${autoUpdate ? 'on yes' : 'yes'}"/>
                <cti:button nameKey="on" classes="${onClasses}"/>
                <c:set var="offClasses" value="${autoUpdate ? 'no' : 'on no'}"/>
                <cti:button nameKey="off" classes="${offClasses}"/>
            </div>

            <div class="js-page-additional-actions dn">
                <cti:url value="/tools/trend/${trendId}/edit" var="editUrl"/>
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}"/>
                <li class="divider">
                <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" id="js-delete-option" data-ok-event="yukon:tools:trend:delete"
                                                      classes="js-hide-dropdown"/>
                <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${trendModel.name}" />
                <cti:url var="deleteUrl" value="/tools/trend/${trendId}/delete"/>
                <form:form id="js-delete-trend-form" action="${deleteUrl}" method="delete" modelAttribute="trendModel">
                    <tags:hidden path="name"/>
                    <cti:csrfToken/>
                </form:form>
                <li class="divider">
                <cm:dropdownOption icon="icon-trend-up" data-popup=".js-reset-peak-popup" label="${resetPeakLbl}" disabled="${!isResetPeakApplicable}"/>
                <li class="divider">
                <cm:dropdownOption key=".printChart" icon="icon-printer" classes="js-print"/>
                <li class="divider">
                <cm:dropdownOption key=".downloadPng" icon="icon-picture" classes="js-dl-png"/>
                <cm:dropdownOption key=".downloadJpg" icon="icon-picture" classes="js-dl-jpg"/>
                <cm:dropdownOption key=".downloadPdf" icon="icon-page-white-acrobat" classes="js-dl-pdf"/>
                <li class="divider">
                <cm:dropdownOption key=".downloadCsv" icon="icon-page-white-excel" classes="js-dl-csv" data-trend-id="${trendId}"/>
            </div>
        </c:if>
    </div>
    <hr>

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
    
    <cti:url value="/tools/trend/renderResetPeakPopup" var="renderResetPeakPopup">
        <cti:param name="trendId" value="${trendId}"/>
    </cti:url>
    <div class="dn js-reset-peak-popup"
             data-popup
             data-dialog
             data-title="${resetPeakLbl}"
             data-url="${renderResetPeakPopup}"
             data-load-event="yukon:tools:trend:resetPeakPopupLoaded"
             data-event="yukon:tools:trend:resetPeak">
    </div>
    
    <dt:pickerIncludes />
    
    <cti:includeScript link="/resources/js/pages/yukon.tools.trends.js"/>
    <cti:includeScript link="/resources/js/common/yukon.trends.js"/>
    <cti:includeScript link="HIGH_STOCK"/>
    <cti:includeScript link="HIGH_STOCK_EXPORTING"/>
    <cti:includeScript link="HIGH_STOCK_NO_DATA"/>
    
</cti:standardPage>