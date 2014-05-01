<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="home">
    
    <cti:includeScript link="YUKON_TIME_FORMATTER"/>
    <cti:includeScript link="/JavaScript/yukon.dr.dashboard.js"/>
    <cti:includeScript link="/JavaScript/yukon.dr.dataUpdater.showAction.js"/>
    <cti:includeScript link="/JavaScript/yukon.dr.ecobee.js"/>
    <cti:includeScript link="/JavaScript/yukon.hide.reveal.js"/>

    <tags:simpleDialog id="drDialog"/>

    <div class="column-12-12">
        <div class="column one">
            <tags:sectionContainer2 nameKey="favorites" styleClass="stacked">
                <c:if test="${empty favorites}">
                    <span class="empty-list"><i:inline key=".noFavorites"/></span>
                </c:if>
                <c:if test="${!empty favorites}">
                    <div class="scroll-large">
                        <table class="compact-results-table row-highlighting has-actions dashed">
                            <thead>
                            <tr>
                                <th>
                                    <tags:sortLink nameKey="nameHeader" baseUrl="${baseUrl}" fieldName="NAME"
                                        sortParam="favSort" descendingParam="favDescending"/>
                                </th>
                                <th>
                                    <tags:sortLink nameKey="typeHeader" baseUrl="${baseUrl}" fieldName="TYPE"
                                        sortParam="favSort" descendingParam="favDescending"/>
                                </th>
                                <th>
                                    <tags:sortLink nameKey="stateHeader" baseUrl="${baseUrl}" fieldName="STATE"
                                        sortParam="favSort" descendingParam="favDescending"/>
                                </th>
                            </tr>
                            </thead>
                            <tfoot></tfoot>
                            <cti:msg2 var="undoText" key="yukon.common.undo"/>
                            <tbody data-undo-text="${undoText}">
                            <c:forEach var="pao" items="${favorites}">
                                <cti:msg2 var="removedText" key="yukon.web.modules.operations.dashboard.favorites.removed" htmlEscapeArguments="false"
                                    arguments="${pao.name}"/>
                                <tr data-removed-text="${removedText}">
                                    <td>
                                        <cti:paoDetailUrl yukonPao="${pao}" var="detailUrl"/>
                                        <cti:paoDetailPageName yukonPao="${pao}" var="detailPageName"/>
    
                                        <cti:button classes="b-favorite remove" nameKey="favorite" renderMode="image" icon="icon-star" 
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
                                <td class="name"><a href="<cti:url value="/dr/rf/details/day"/>"><i:inline key=".rfPerformance.last24Hr"/>:</a></td>
                                <td class="value full-width"><dr:rfPerformanceStats test="${last24Hr}"/></td>
                            </tr>
                            <tr>
                                <td class="name"><a href="<cti:url value="/dr/rf/details/week"/>"><i:inline key=".rfPerformance.last7Days"/>:</a></td>
                                <td class="value full-width"><dr:rfPerformanceStats test="${last7Days}"/></td>
                            </tr>
                            <tr>
                                <td class="name"><a href="<cti:url value="/dr/rf/details/month"/>"><i:inline key=".rfPerformance.last30Days"/>:</a></td>
                                <td class="value full-width"><dr:rfPerformanceStats test="${last30Days}"/></td>
                            </tr>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="action-area">
                        <a href="<cti:url value="/dr/rf/details"/>"><i:inline key=".details"/></a>
                        <cti:button nameKey="configure" icon="icon-cog-edit" popup="#broadcast-config"/>
                    </div>
                    <div dialog id="broadcast-config" data-form data-width="500" data-title="<cti:msg2 key=".rfPerformance.configure.title"/>" class="dn">
                        <form:form action="rf/performance" method="POST" commandName="settings">
                            <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                                <tags:nameValue2 nameKey=".rfPerformance.configure.dailyTestCommand" valueClass="full-width" nameClass="wsnw">
                                    <div class="column-6-18 clearfix stacked">
                                        <div class="column one">
                                            <span class="f-time-label fwb">&nbsp;</span>
                                            <tags:hidden path="time" id="rf-performance-command-time"/>
                                        </div>
                                        <div class="column two nogutter">
                                            <div class="f-time-slider" style="margin-top: 7px;"></div>
                                        </div>
                                    </div>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".rfPerformance.configure.emailResults">
                                    <tags:hidden path="email" id="rf-performance-email"/>
                                    <div class="button-group toggle-on-off">
                                        <cti:button nameKey="on" classes="on yes M0"/>
                                        <cti:button nameKey="off" classes="no M0"/>
                                    </div>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".rfPerformance.configure.dailyEmail" rowClass="f-email-schedule">
                                    <div class="column-6-18 clearfix stacked">
                                        <div class="column one">
                                            <span class="f-email-time-label fwb">&nbsp;</span>
                                            <tags:hidden path="emailTime" id="rf-performance-email-time"/>
                                        </div>
                                        <div class="column two nogutter">
                                            <div class="f-email-time-slider" style="margin-top: 7px;"></div>
                                        </div>
                                    </div>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".rfPerformance.configure.notifGroups" rowClass="f-notif-group">
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
            <c:if test="${showEcobeeStats}">
                <tags:sectionContainer2 nameKey="ecobee">
                    <div class="stacked">
                        <tags:nameValueContainer2 naturalWidth="false">
                            <tr>
                                <td class="name">Queries (${month}):</td>
                                <td class="value full-width">
                                    <div class="progress query-statistics" style="width: 80px;float:left;"
                                        data-query-counts='{ "currentMonthDataCollectionCount": "${currentMonthDataCollectionCount}",
                                            "currentMonthDemandResponseCount": "${currentMonthDemandResponseCount}",
                                            "currentMonthSystemCount": "${currentMonthSystemCount}" }'>
                                        <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 27.0%"></div>
                                        <div class="progress-bar progress-bar-info" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 50.0%"></div>
                                        <div class="progress-bar progress-bar-default" role="progressbar" aria-valuenow="0.0%" aria-valuemin="0" aria-valuemax="100" style="width: 23.0%"></div>
                                    </div>
                                    <div class="fl query-counts" style="margin-left: 10px;" title="<cti:msg2 key=".ecobee.details.statistics.title"/>">
                                        <span class="query-total" style="margin-right: 10px;width:48px;display: inline-block;">15200</span>
                                        <span class="label label-success">4104</span>
                                        <span class="label label-info">7600</span>
                                        <span class="label label-default">3496</span>
                                    </div>
                                </td>
                            </tr>
                            <tr class="dn">
                                <td class="name">Issues:</td>
                                <td class="value full-width"><span class="label label-danger">6</span> devices, <span class="label label-danger">8</span> groups</td>
                            </tr>
                            <tr>
                                <td class="name"></td>
                                <td class="value full-width"></td>
                            </tr>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="action-area">
                        <a href="<cti:url value="/dr/ecobee"/>"><i:inline key=".details"/></a>
                        <cti:button nameKey="configure" popup="#ecobee-config" icon="icon-cog-edit"/>
                        <div dialog data-form id="ecobee-config" data-width="500" data-title="<cti:msg2 key=".ecobee.configure.title"/>" class="dn">
                            <form:form action="ecobee/settings" method="POST" commandName="ecobeeSettings">
                                <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                                    <tags:nameValue2 nameKey=".ecobee.configure.errorChecking" rowId="ecobee-error-checking-toggle" valueClass="full-width">
                                        <tags:hidden path="checkErrors" id="ecobee-check-errors"/>
                                        <div class="button-group toggle-on-off">
                                            <cti:button nameKey="on" classes="on yes M0"/>
                                            <cti:button nameKey="off" classes="no M0"/>
                                        </div>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".ecobee.configure.dailyErrorCheck" rowId="ecobee-error-check-schedule" valueClass="full-width">
                                        <div class="column-6-18 clearfix stacked">
                                            <div class="column one">
                                                <span class="f-time-label fwb">&nbsp;</span>
                                                <tags:hidden path="errorCheckTime" id="ecobee-error-check-time"/>
                                            </div>
                                            <div class="column two nogutter">
                                                <div class="f-time-slider" style="margin-top: 7px;"></div>
                                            </div>
                                        </div>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".ecobee.configure.dataCollection" rowId="ecobee-data-collection-toggle" valueClass="full-width">
                                        <tags:hidden path="dataCollection" id="ecobee-data-collection"/>
                                        <div class="button-group toggle-on-off">
                                            <cti:button nameKey="on" classes="on yes M0"/>
                                            <cti:button nameKey="off" classes="no M0"/>
                                        </div>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </form:form>
                        </div>
                    </div>
                </tags:sectionContainer2>
            </c:if>
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
                    <div class="scroll-large">
                        <table class="compact-results-table row-highlighting has-actions dashed">
                            <thead>
                            <tr>
                                <th>
                                    <tags:sortLink nameKey="nameHeader" baseUrl="${baseUrl}" fieldName="NAME"
                                        sortParam="rvSort" descendingParam="rvDescending"/>
                                </th>
                                <th>
                                    <tags:sortLink nameKey="typeHeader" baseUrl="${baseUrl}" fieldName="TYPE"
                                        sortParam="rvSort" descendingParam="rvDescending"/>
                                </th>
                                <th>
                                    <tags:sortLink nameKey="stateHeader" baseUrl="${baseUrl}" fieldName="STATE"
                                        sortParam="rvSort" descendingParam="rvDescending"/>
                                </th>
                                <th class="action-column"></th>
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
</cti:standardPage>
