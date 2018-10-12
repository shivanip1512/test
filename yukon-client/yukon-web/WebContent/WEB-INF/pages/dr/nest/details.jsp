<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="nest.details">
    <tags:sectionContainer2 nameKey="nestSyncControls">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".nest.syncSchedule">
                <form:form action="nest/settings" method="POST" modelAttribute="nestSyncSettings">
                    <cti:csrfToken/>
                    <span id="nest-sync-settings-row">
                        <tags:hidden path="sync" id="nest-sync"/>
                        <div class="button-group button-group-toggle nest-sync-button-group-toggle" style="padding-right:20px;">
                            <cti:button nameKey="on" classes="on yes M0"/>
                            <cti:button nameKey="off" classes="no M0"/>
                        </div>
                        <span class="nest-sync-time-slider">
                            <tags:timeSlider startName="scheduledSyncTime" startValue="${scheduledSyncTime}" timeFormat="HHMM" maxValue="1425" displayTimeToLeft="true"/>
                        </span>
                        <span class="fr">
                            <cti:button nameKey="save" type="submit" style="margin-left:20px;"/>            
                            <cti:button nameKey="syncNow" href="nest/syncNow" title="${syncTitle}" disabled="${!syncNowEnabled}"/>
                        </span>
                    </span>
                </form:form>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".nest.lastSync" nameClass="PT10" valueClass="PT10">
                ${lastSyncTime}
            </tags:nameValue2>
        
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    
    <tags:sectionContainer2 nameKey="nestSyncDiscrepancies">
        <cti:url var="action" value="/dr/nest/discrepancies" />
        <form:form id="discrepancyForm" action="${action}" method="GET">
        
            <div class="filter-section">
        
                <i:inline key="yukon.common.filterBy"/>&nbsp;
                <i:inline key=".nest.syncTime"/>:&nbsp;
                <select name="syncId">
                    <c:forEach var="sync" items="${syncTimes}">
                        <c:set var="selected" value=""/>
                        <c:if test="${sync.id == selectedSyncId}">
                            <c:set var="selected" value="selected='selected'"/>
                        </c:if>
                        <option value="${sync.id}" ${selected}><cti:formatDate type="BOTH" value="${sync.startTime}"/></option>
                    </c:forEach>
                </select>
                
                <div class="button-group">
                    <c:forEach var="type" items="${types}">
                        <c:set var="checked" value="${false}"/>
                        <c:forEach var="selType" items="${selectedTypes}">
                            <c:if test="${selType eq type}">
                                <c:set var="checked" value="${true}"/>
                            </c:if>
                        </c:forEach>
                        <tags:check name="types" key=".${type}" classes="M0 js-type" value="${type}" checked="${checked}"></tags:check>
                    </c:forEach>
                </div>
                <hr/>
            </div>
        
            <span class="fwn"><i:inline key="yukon.common.filteredResults"/></span>
            <span class="badge js-count">${discrepancies.hitCount}</span>&nbsp;<i:inline key=".details.nestSyncDiscrepancies.title"/>
            <span class="js-cog-menu">
                <cm:dropdown icon="icon-cog">
                    <cm:dropdownOption icon="icon-csv" key="yukon.common.download" classes="js-download"/>  
                </cm:dropdown>
            </span>
        
        </form:form>
    
        <cti:url var="dataUrl" value="/dr/nest/discrepancies">
            <c:forEach var="type" items="${selectedTypes}">
                <cti:param name="types" value="${type}"/>
            </c:forEach>
            <cti:param name="syncId" value="${selectedSyncId}"/>
        </cti:url>
        <div id="discrepancyContainer" data-url="${dataUrl}">
            <%@ include file="discrepanciesTable.jsp" %>
        </div>
        
    </tags:sectionContainer2>
	<cti:includeScript link="/resources/js/pages/yukon.dr.nest.js"/>
</cti:standardPage>
