<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="home">

<tags:simpleDialog id="drDialog"/>
<c:if test="${showSeasonReset}">
    <div id="page-actions" class="dn">
        <cm:dropdownOption key=".seasonCntlHrs.reset" icon="icon-arrow-swap" 
            classes="js-reset-season-hrs" data-ok-event="yukon:dr:season-cntl-hrs-reset"/>
    </div>
    <d:confirm on=".js-reset-season-hrs" nameKey="season.reset.confirm"/>
</c:if>

<div class="column-12-12">
    <div class="column one">
        <tags:sectionContainer2 nameKey="favorites" styleClass="stacked">
            <c:if test="${empty favorites}">
                <span class="empty-list"><i:inline key=".noFavorites"/></span>
            </c:if>
            <c:if test="${!empty favorites}">
                <div class="scroll-lg">
                    <table class="compact-results-table row-highlighting has-actions dashed">
                        <thead>
                            <tr>
                                <th><i:inline key=".name"/></th>
                                <th><i:inline key=".type"/></th>
                                <th><i:inline key=".state"/></th>
                                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <cti:msg2 var="undoText" key="yukon.common.undo"/>
                        <tbody data-undo-text="${undoText}">
                        <c:forEach var="pao" items="${favorites}">
                            <cti:msg2 var="removedText" key="yukon.web.modules.operations.dashboard.favorites.removed" 
                                    htmlEscapeArguments="false" arguments="${pao.name}"/>
                            <tr data-removed-text="${removedText}">
                                <td>
                                    <cti:paoDetailUrl yukonPao="${pao}" var="detailUrl"/>
                                    <cti:paoDetailPageName yukonPao="${pao}" var="detailPageName"/>
                                    <cti:button classes="b-favorite remove" nameKey="favorite" 
                                            renderMode="image" icon="icon-star" 
                                            data-name="${detailPageName}"
                                            data-module="dr"
                                            data-path="${detailUrl}"
                                            data-label-args="['${pao.name}']"/>
                                    <a href="${detailUrl}">${fn:escapeXml(pao.name)}</a>
                                </td>
                                <td>
                                    <i:inline key=".paoType.${pao.paoIdentifier.paoType}"/>
                                </td>
                                <td>
                                    <span class="fl"><dr:stateText pao="${pao}"/></span>
                                </td>
                                <td>
                                    <dr:listActions pao="${pao}"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </tags:sectionContainer2>
        
        <c:if test="${showRfPerformance}">
            <tags:sectionContainer2 nameKey="rfPerformance">
                <div class="stacked">
                    <tags:nameValueContainer2 naturalWidth="false">
                        <tr>
                            <td class="wsnw">
                                <a href="<cti:url value="/dr/rf/details/day"/>"
                                ><i:inline key=".rfPerformance.last24Hr"/>:</a>
                            </td>
                            <td class="value full-width">
                                <dr:rfPerformanceStats test="${last24Hr}"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="wsnw">
                                <a href="<cti:url value="/dr/rf/details/week"/>"
                                ><i:inline key=".rfPerformance.last7Days"/>:</a>
                            </td>
                            <td class="value full-width">
                                <dr:rfPerformanceStats test="${last7Days}"/>
                            </td>
                        </tr>
                        <tr>
                            <td class="wsnw">
                                <a href="<cti:url value="/dr/rf/details/month"/>"
                                ><i:inline key=".rfPerformance.last30Days"/>:</a>
                            </td>
                            <td class="value full-width">
                                <dr:rfPerformanceStats test="${last30Days}"/>
                            </td>
                        </tr>
                    </tags:nameValueContainer2>
                </div>
                <div class="action-area">
                    <a href="<cti:url value="/dr/rf/details"/>"><i:inline key=".details"/></a>
                    <cti:button nameKey="configure" icon="icon-cog-edit" data-popup="#broadcast-config"/>
                </div>
                <div data-dialog
                    id="broadcast-config"
                    data-form
                    data-width="500"
                    data-title="<cti:msg2 key=".rfPerformance.configure.title"/>"
                    data-load-event="yukon.dr.rf.config.load"
                    class="dn">
                    <form:form action="rf/performance" method="POST" modelAttribute="settings">
                        <cti:csrfToken/>
                        <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                            <tags:nameValue2 nameKey=".rfPerformance.configure.dailyTestCommand" 
                                    valueClass="full-width" nameClass="wsnw">
                                <tags:timeSlider startPath="time" maxValue="1425" displayTimeToLeft="true"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".rfPerformance.configure.emailResults">
                                <tags:hidden path="email" id="rf-performance-email"/>
                                <div class="button-group button-group-toggle">
                                    <cti:button nameKey="on" classes="on yes M0"/>
                                    <cti:button nameKey="off" classes="no M0"/>
                                </div>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".rfPerformance.configure.dailyEmail" rowClass="js-email-schedule">
                                <tags:timeSlider startPath="emailTime" maxValue="1425" displayTimeToLeft="true"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".rfPerformance.configure.notifGroups" rowClass="js-notif-group">
                                <tags:hidden path="notifGroupIds" id="rf-performance-notif-group-ids"/>
                                <tags:pickerDialog type="notificationGroupPicker" 
                                                   id="notificationGroupPicker"
                                                   linkType="selection"
                                                   selectionProperty="name"
                                                   allowEmptySelection="true"
                                                   multiSelectMode="true"
                                                   destinationFieldId="rf-performance-notif-group-ids"
                                                   initialIds="${settings.notifGroupIds}"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </form:form>
                </div>
            </tags:sectionContainer2>
        </c:if>
        <cti:checkRolesAndProperties value="SHOW_ECOBEE">
            <jsp:include page="/dr/ecobee/statistics"/>
        </cti:checkRolesAndProperties>
        <c:if test="${nestAvailable}">
            <jsp:include page="/dr/nest/statistics"/>
        </c:if>
        <cti:checkRolesAndProperties value="HONEYWELL_SUPPORT_ENABLED">
            <jsp:include page="/dr/recenteventparticipation/auditReport"/>
        </cti:checkRolesAndProperties>
    </div>
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="quickSearches" styleClass="stacked">
             <cti:checkRolesAndProperties value="SHOW_CONTROL_AREAS">
                 <cti:url var="quickLinkUrl" value="/dr/controlArea/list">
                     <cti:param name="state" value="active"/>
                 </cti:url>
                 <a href="${quickLinkUrl}"><i:inline key=".activeControlAreasQuickSearch"/></a><br>
             </cti:checkRolesAndProperties>
             <cti:url var="quickLinkUrl" value="/dr/program/list">
                 <cti:param name="state" value="ACTIVE"/>
             </cti:url>
             <a href="${quickLinkUrl}"><i:inline key=".activeProgramsQuickSearch"/></a><br>
             <cti:url var="quickLinkUrl" value="/dr/loadGroup/list">
                 <cti:param name="state" value="active"/>
             </cti:url>
             <a href="${quickLinkUrl}"><i:inline key=".activeLoadGroupsQuickSearch"/></a><br>
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="recents">
            <c:if test="${empty recents}">
                <span class="empty-list"><i:inline key=".noRecents" /></span>
            </c:if>
            <c:if test="${!empty recents}">
                <div class="scroll-lg">
                    <table class="compact-results-table row-highlighting has-actions dashed">
                        <thead>
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th><i:inline key=".type"/></th>
                            <th><i:inline key=".state"/></th>
                            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
                        </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                        <c:forEach var="pao" items="${recents}">
                            <tr>
                                <td>
                                    <cti:paoDetailUrl yukonPao="${pao}">${fn:escapeXml(pao.name)}</cti:paoDetailUrl>
                                </td>
                                <td>
                                    <i:inline key=".paoType.${pao.paoIdentifier.paoType}" />
                                </td>
                                <td>
                                    <dr:stateText pao="${pao}"/>
                                </td>
                                <td>
                                    <dr:listActions pao="${pao}"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </tags:sectionContainer2>
    </div>
</div>
<dt:pickerIncludes/>
<cti:includeScript link="/resources/js/pages/yukon.dr.dashboard.js"/>
<cti:includeScript link="/resources/js/pages/yukon.dr.dataUpdater.showAction.js"/>

</cti:standardPage>