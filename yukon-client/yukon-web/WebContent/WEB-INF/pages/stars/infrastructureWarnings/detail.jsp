<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="infrastructureWarnings" smartNotificationsEvent="INFRASTRUCTURE_WARNING">
    <div class="js-page-additional-actions dn">
        <cti:url value="/notifications/events/infrastructureWarnings" var="recentNotificationDetail"/>
        <cm:dropdownOption key="yukon.web.modules.smartNotifications.notificationDetail" icon="icon-calendar-view-month" href="${recentNotificationDetail}"/>
    </div>

    <cti:msgScope paths="widgets.infrastructureWarnings">

        <c:set var="fromDetailPage" value="true"/>
        <%@ include file="summaryTable.jsp" %>
            
        <div class="filter-section">
            <hr>
            <cti:url var="action" value="/stars/infrastructureWarnings/filteredResults" />
            <form id="warnings-form" action="${action}" method="GET">
                <i:inline key="yukon.common.filterBy"/>&nbsp;
                <span class="fr cp"><cti:icon icon="icon-help" data-popup="#results-help"/></span>
                <cti:msg2 var="helpTitle" key=".detail.helpTitle"/>
                <div id="results-help" class="dn" data-width="600" data-height="400" data-title="${helpTitle}"><cti:msg2 key=".detail.helpText"/></div>
                <div class="button-group MR10">
                    <c:forEach var="type" items="${deviceTypes}">
                        <c:set var="selected" value="${false}"/>
                        <c:if test="${fn:contains(selectedTypes, type)}">
                            <c:set var="selected" value="${true}"/>
                        </c:if>
                        <cti:msg2 var="deviceType" key=".category.${type}"/>
                        <tags:check name="types" classes="M0" value="${type}" label="${deviceType}" checked="${selected}"></tags:check>
                    </c:forEach>
                </div>
                
                <input type="checkbox" name="highSeverityOnly" class="MR5"/><i:inline key=".highSeverityOnly"/>
            
                <cti:button nameKey="filter" classes="js-filter-results primary action fn ML15"/>
            </form>
            <hr>
        </div>
        
        <div id="results-table">
        </div>

        <div class="dn" id="js-pao-notes-popup"></div>
        <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
        <cti:includeScript link="/resources/js/pages/yukon.infrastructurewarnings.detail.js"/>
    
    </cti:msgScope>
    
</cti:standardPage>