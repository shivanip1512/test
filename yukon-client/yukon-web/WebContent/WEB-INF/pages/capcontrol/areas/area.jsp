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

<div class="column-12-12 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="infoContainer">
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
        </tags:sectionContainer2>
    </div>
    
    <div class="column two nogutter">
        <tags:sectionContainer2 nameKey="statsContainer">
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
        </tags:sectionContainer2>
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
                <td>
                    <capTags:warningImg paoId="${substationId}" type="SUBSTATION"/>
                </td>
                <td>
                    <a href="${feederLink}" id="anc_${substationId}">
                        ${fn:escapeXml(station.name)}
                    </a>
                    <div class="error">
                        <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="SA_ENABLED_MSG"
                            defaultBlank="true" initialize="false"/>
                    </div>
                </td>
                
                <td class="state-indicator">
                    <span class="box state-box js-cc-state-updater" data-pao-id="${substationId}">&nbsp;</span>
                    <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="false"
                        value="SUBSTATION/${substationId}/STATE_FLAGS"/>
                </td>
                <td class="wsnw">
                    <c:if test="${hasSubstationControl}"><a id="substationState_${substationId}"
                            href="javascript:void(0);" class="subtle-link"></c:if>
                        <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="STATE" initialize="false"/>
                    <c:if test="${hasSubstationControl}"></a></c:if>
                </td>
                
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_AVAILABLE"
                        initialize="false"/>
                </td>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION"
                        format="KVARS_UNAVAILABLE" initialize="false"/>
                </td>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_CLOSED"
                        initialize="false"/>
                </td>
                <td class="tar">
                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_TRIPPED"
                        initialize="false"/>
                </td>
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