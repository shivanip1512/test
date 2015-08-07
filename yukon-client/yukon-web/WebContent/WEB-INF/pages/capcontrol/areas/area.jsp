<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %> <%-- Used in Trends --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="area">
<cti:includeScript link="/resources/js/pages/yukon.da.area.js"/>
<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

<%-- EDIT INFO POPUP --%>
<div class="dn js-edit-info-popup"
    data-dialog
    data-title="<cti:msg2 key=".edit.info" arguments="${areaName}"/>"
    data-url="<cti:url value="/capcontrol/areas/${areaId}/info/edit"/>"
    data-event="yukon:vv:area:info:save"></div>

<%-- EDIT SUBSTATIONS POPUP --%>
<div class="dn js-edit-stations-popup"
    data-dialog
    data-area-id="${areaId}"
    data-title="<cti:msg2 key=".edit.stations"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/areas/${areaId}/stations/edit"/>"
    data-event="yukon:vv:area:stations:save"
    data-height="300"
    data-width="750"></div>

<%-- EDIT STRATEGY ASSIGNMENT POPUP --%>
<div class="dn js-edit-strat-popup"
    data-dialog
    data-object-id="${areaId}"
    data-title="<cti:msg2 key=".edit.strategy"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/strategy-assignment/${areaId}"/>"
    data-event="yukon:vv:strategy-assignment:save"
    data-width="500"></div>

<div class="column-12-12 clearfix">
    <div class="column one">
        <cti:tabs containerName="yukon:da:area:info-v-strategy:tab">
            <cti:msg2 var="infoTitle" key=".infoContainer.title"/>
            <cti:tab title="${infoTitle}" selected="true" tabId="info-tab">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".name" valueClass="wbba">
                        <span>${fn:escapeXml(areaName)}</span>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".geoName">
                        <span>${fn:escapeXml(description)}</span>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".state" rowClass="wsnw">
                        <span class="box state-box js-cc-state-updater" data-pao-id="${areaId}">&nbsp;</span>
                        <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="true" 
                                value="${updater}/${areaId}/STATE_FLAGS"/>
                        <cti:capControlValue paoId="${areaId}" type="${updater}" format="STATE"/>
                     </tags:nameValue2>
                     <tags:nameValue2 nameKey=".voltReduction">
                        <c:choose>
                            <c:when test="${not empty voltReduction}">
                                <span>${fn:escapeXml(voltReduction)}</span>:&nbsp;
                                <cti:pointStatus pointId="${voltReductionId}"/>
                                <cti:pointValue pointId="${voltReductionId}" format="VALUE"/>
                            </c:when>
                            <c:otherwise><em><i:inline key="yukon.common.none.choice"/></em></c:otherwise>
                        </c:choose>
                     </tags:nameValue2>
                </tags:nameValueContainer2>
                <capTags:warningImg paoId="${areaId}" type="${updater}" alertBox="true"/>
                <c:if test="${canEdit}">
                    <div class="action-area">
                        <cti:button nameKey="edit" icon="icon-pencil" 
                            data-popup=".js-edit-info-popup" data-popup-toggle=""/>
                    </div>
                </c:if>
            </cti:tab>
            
            <cti:msg2 var="stratTitle" key=".stratContainer.title"/>
            <cti:tab title="${stratTitle}" tabId="strat-tab">
                <%-- SEASON SCHEDULE --%>
                <tags:nameValueContainer2 tableClass="stacked">
                    <tags:nameValue2 nameKey=".schedule.season">
                        <c:set var="clazz" value="${seasonSchedule.scheduleId == -1 ? 'hint' : ''}"/>
                        <span class="${clazz}">${fn:escapeXml(seasonSchedule.scheduleName)}</span>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <table class="full-width dashed stacked">
                    <thead>
                        <tr>
                            <th><i:inline key=".season"/></th>
                            <th><i:inline key=".strategy"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="season" items="${seasons.keySet()}">
                            <c:set var="strat" value="${seasons[season]}"/>
                            <tr>
                                <td>${fn:escapeXml(season.seasonName)}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty strat}">
                                            <i:inline key="yukon.common.none.choice"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:url var="url" value="/capcontrol/strategies/${strat.id}"/>
                                            <a href="${url}">${fn:escapeXml(strat.name)}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <%-- HOLIDAY SCHEDULE --%>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".schedule.holiday">
                        <c:set var="clazz" value="${holidaySchedule.holidayScheduleId == -1 ? 'hint' : ''}"/>
                        <span class="${clazz}">${fn:escapeXml(holidaySchedule.holidayScheduleName)}</span>
                    </tags:nameValue2>
                    <c:if test="${not empty holidayStrat}">
                        <tags:nameValue2 nameKey=".strategy">
                            <cti:url var="url" value="/capcontrol/strategies/${holidayStrat.id}"/>
                            <a href="${url}">${fn:escapeXml(holidayStrat.name)}</a>
                        </tags:nameValue2>
                    </c:if>
                </tags:nameValueContainer2>
                
                <c:if test="${canEdit}">
                    <div class="action-area">
                        <cti:button nameKey="edit" icon="icon-pencil" 
                            data-popup=".js-edit-strat-popup" data-popup-toggle=""/>
                    </div>
                </c:if>
            </cti:tab>
        </cti:tabs>
    </div>
    
    <div class="column two nogutter">
        <cti:tabs containerName="yukon:da:area:stats-v-points:tab">
            <cti:msg2 var="statsTitle" key=".statsContainer.title"/>
            <cti:tab title="${statsTitle}" tabId="stats-tab" classes="scroll-lg">
                <div class="column-12-12">
                    <div class="column one">
                        <tags:nameValueContainer2 tableClass="name-collapse">
                            <tags:nameValue2 nameKey=".availableKvars">
                                <cti:capControlValue paoId="${areaId}" type="${updater}" format="KVARS_AVAILABLE"
                                    initialize="false"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".unavailableKvars">
                                <cti:capControlValue paoId="${areaId}" type="${updater}" format="KVARS_UNAVAILABLE"
                                    initialize="false"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:nameValueContainer2 tableClass="name-collapse">
                            <tags:nameValue2 nameKey=".closedKvars">
                                <cti:capControlValue paoId="${areaId}" type="${updater}" format="KVARS_CLOSED"
                                    initialize="false"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trippedKvars">
                                <cti:capControlValue paoId="${areaId}" type="${updater}" format="KVARS_TRIPPED"
                                    initialize="false"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                </div>
                <div class="clear">
                    <tags:nameValueContainer2 tableClass="name-collapse">
                        <tags:nameValue2 nameKey=".pfactorEstimated" rowClass="powerFactor">
                            <cti:capControlValue paoId="${areaId}" type="${updater}" format="PFACTOR"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </cti:tab>
            
            <cti:msg2 var="pointsTitle" key=".pointsContainer.title"/>
            <cti:tab title="${pointsTitle}" tabId="points-tab" classes="scroll-lg">
                <c:choose>
                    <c:when test="${empty points}">
                        <span class="empty-list"><i:inline key="yukon.common.points.none"/></span>
                    </c:when>
                    <c:otherwise>
                        <table class="compact-results-table row-highlighting">
                            <thead></thead>
                            <tfoot></tfoot>
                            <tbody>
                            <c:forEach var="point" items="${points}">
                                 <tr>
                                     <td>
                                        <cti:url var="pointUrl" value="/capcontrol/points/${point.pointId}"/>
                                        <a href="${pointUrl}">${fn:escapeXml(point.pointName)}</a>
                                     </td>
                                     <td class="state-indicator">
                                        <c:if test="${point.paoPointIdentifier.pointIdentifier.pointType.status}">
                                            <cti:pointStatus pointId="${point.pointId}"/>
                                        </c:if>
                                     </td>
                                     <td class="wsnw"><cti:pointValue pointId="${point.pointId}" format="SHORT"/></td>
                                     <td class="wsnw"><tags:historicalValue pao="${area}" pointId="${point.pointId}"/></td>
                                 </tr>
                             </c:forEach>
                            </tbody>
                        </table>
                    </c:otherwise>
                </c:choose>
                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                    <div class="action-area">
                        <tags:pointCreation paoId="${areaId}"/>
                    </div>
                </cti:checkRolesAndProperties>
            </cti:tab>
        </cti:tabs>
    </div>
</div>

<tags:sectionContainer2 nameKey="substationsContainer" arguments="${areaName}">
    
    <table id="subTable" class="compact-results-table has-alerts dashed">
        <thead>
            <tr>
                <th>&nbsp;</th>
                <th><i:inline key=".name"/></th>
                <th></th>
                <th><i:inline key=".state"/></th>
                <th class="tar"><i:inline key=".availableKvars"/></th>
                <th class="tar"><i:inline key=".unavailableKvars"/></th>
                <th class="tar"><i:inline key=".closedKvars"/></th>
                <th class="tar"><i:inline key=".trippedKvars"/></th>
                <th class="tar"><i:inline key=".pfactorEstimated"/></th>
            </tr>
        </thead>
        
        <c:forEach var="station" items="${subStations}">
        
            <c:set var="substationId" value="${station.id}"/>
            <cti:url value="/capcontrol/tier/feeders" var="feederLink">
                <cti:param name="substationId" value="${substationId}"/>
            </cti:url>
            
            <tr data-pao-id="${substationId}">
                <%-- ROW ICON --%>
                <td>
                    <capTags:warningImg paoId="${substationId}" type="SUBSTATION"/>
                </td>
                <%-- SUB NAME --%>
                <td>
                    <a href="${feederLink}" id="anc_${substationId}">
                        ${fn:escapeXml(station.name)}
                    </a>
                    <div class="error">
                        <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="SA_ENABLED_MSG"
                            defaultBlank="true" initialize="false"/>
                    </div>
                </td>
                <%-- STATE BOX --%>
                <td class="state-indicator">
                    <span class="box state-box js-cc-state-updater" data-pao-id="${substationId}">&nbsp;</span>
                    <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="false"
                        value="SUBSTATION/${substationId}/STATE_FLAGS"/>
                </td>
                <%-- STATE --%>
                <td class="wsnw">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="STATE" initialize="false"/>
                </td>
                <%-- AVAILABLE VARS --%>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_AVAILABLE"
                        initialize="false"/>
                </td>
                <%-- UNAVAILABLE VARS --%>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION"
                        format="KVARS_UNAVAILABLE" initialize="false"/>
                </td>
                <%-- CLOSED VARS --%>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_CLOSED"
                        initialize="false"/>
                </td>
                <%-- TRIPPED VARS --%>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_TRIPPED"
                        initialize="false"/>
                </td>
                <%-- POWER FACTOR --%>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="PFACTOR" initialize="false"/>
                </td>
            </tr>
        </c:forEach>
        
    </table>
    
    <c:if test="${canEdit}">
        <div class="action-area">
            <cti:button nameKey="edit" icon="icon-pencil" 
                data-popup=".js-edit-stations-popup" data-popup-toggle=""/>
        </div>
    </c:if>
    
</tags:sectionContainer2>

</cti:standardPage>