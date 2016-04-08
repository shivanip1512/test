<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %> <%-- Used in Trends --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<c:set var="pageName" value="area.${mode}"/>
<cti:standardPage module="capcontrol" page="${pageName}">

<cti:includeScript link="/resources/js/pages/yukon.da.area.js"/>
<%@ include file="/capcontrol/capcontrolHeader.jspf" %>
<tags:setFormEditMode mode="${mode}"/>

<c:if test="${hasAreaControl}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="areaState_"]');
    </script>
</c:if>

<div class="js-page-additional-actions dn">
    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <li class="divider" />
    </cti:checkRolesAndProperties>
    <c:if test="${hasAreaControl}">
        <cm:dropdownOption linkId="areaState_${areaId}" key=".area.actions" icon="icon-cog" href="javascript:void(0);" />
    </c:if>
</div>

<%-- EDIT INFO POPUP --%>
<div class="dn js-edit-info-popup"
    data-dialog
    data-title="<cti:msg2 key=".edit.info" arguments="${areaName}"/>"
    data-url="<cti:url value="/capcontrol/areas/${areaId}/info/edit"/>"
    data-event="yukon:vv:area:info:save"
    data-delete ></div>

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

<cti:url var="action" value="/capcontrol/areas"/>
<form:form modelAttribute="area" action="${action}" method="POST">
    <cti:csrfToken/>
    <form:hidden path="id"/>
    <form:hidden path="type"/>
<div class="column-12-12 clearfix">
    <div class="column one">
        <cti:tabs>
                <cti:msg2 var="infoTab" key=".infoTab"/>
                <cti:tab title="${infoTab}">
                 <cti:displayForPageEditModes modes="EDIT,VIEW">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name" valueClass="wbba">
                            <span>${fn:escapeXml(areaName)}</span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".geoName">
                            <span>${fn:escapeXml(description)}</span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".state" rowClass="wsnw">
                            <cti:displayForPageEditModes modes="EDIT,VIEW">
                                <span class="box state-box js-cc-state-updater" data-pao-id="${areaId}">&nbsp;</span>
                                <cti:dataUpdaterCallback function="yukon.da.updaters.stateColor" initialize="true" 
                                        value="${updater}/${areaId}/STATE_FLAGS"/>
                                <cti:capControlValue paoId="${areaId}" type="${updater}" format="STATE"/>
                            </cti:displayForPageEditModes>
                         </tags:nameValue2>
                         <tags:nameValue2 nameKey=".voltReduction">
                            <cti:displayForPageEditModes modes="EDIT,VIEW">
                                <c:choose>
                                    <c:when test="${not empty voltReduction}">
                                        <span>${fn:escapeXml(voltReduction)}</span>:&nbsp;
                                        <cti:pointStatus pointId="${voltReductionId}"/>
                                        <cti:pointValue pointId="${voltReductionId}" format="VALUE"/>
                                    </c:when>
                                    <c:otherwise><em><i:inline key="yukon.common.none.choice"/></em></c:otherwise>
                                </c:choose>
                            </cti:displayForPageEditModes>
                         </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <capTags:warningImg paoId="${areaId}" type="${updater}" alertBox="true"/>
                              
                    <c:if test="${canEdit}">
                        <div class="action-area">
                            <cti:button nameKey="edit" icon="icon-pencil" 
                                data-popup=".js-edit-info-popup" data-popup-toggle=""/>
                        </div>
                    </c:if>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="CREATE">
                    <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                        <tags:nameValue2 nameKey="yukon.common.name">
                            <tags:input path="name" size="35" maxlength="60"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.common.type">
                            <i:inline key="${area.type}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".geoName">
                            <tags:input path="description" size="35" maxlength="60"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".state">
                            <tags:switchButton path="disabled" inverse="true" 
                                offNameKey=".disabled" onNameKey=".enabled" offClasses="M0"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".voltReduction">
                            <c:set var="active" value="${not empty area.voltReductionPoint}"/>
                            <tags:switchButton name="vrActive" checked="${active}" offClasses="M0" classes="js-volt-reduct"/>
                            <c:set var="initial" value="${not active ? '' : area.voltReductionPoint}"/>
                            <tags:pickerDialog type="voltReductionPointPicker" id="voltReduction" allowEmptySelection="true"
                                destinationFieldName="voltReductionPoint" initialId="${initial}" 
                                buttonStyleClass="js-picker-btn ${not active ? 'dn' : ''}"
                                linkType="selection" selectionProperty="pointName"/>
                        </tags:nameValue2>
                     </tags:nameValueContainer2>
                    <div class="page-action-area">
                        <cti:button nameKey="save" type="submit" classes="primary action"/>
                        <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
                    </div>
                </cti:displayForPageEditModes>
            </cti:tab>
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <%@ include file="/WEB-INF/pages/capcontrol/strategyTab.jsp" %>
            </cti:displayForPageEditModes>
        </cti:tabs>
    </div>
    <cti:displayForPageEditModes modes="EDIT,VIEW">
        <div class="column two nogutter">
            <cti:tabs>
                <cti:msg2 var="statisticsTab" key=".statisticsTab"/>
                <cti:tab title="${statisticsTab}">
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
                
                <cti:msg2 var="attachedPointsTab" key=".attachedPointsTab"/>
                <cti:tab title="${attachedPointsTab}">
                    <div class="scroll-md">
                        <%@ include file="/WEB-INF/pages/capcontrol/pointsTable.jsp" %>
                    </div>
                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                        <div class="action-area">
                            <tags:pointCreation paoId="${areaId}" />
                        </div>
                    </cti:checkRolesAndProperties>
                </cti:tab>
            </cti:tabs>
        </div>
    </cti:displayForPageEditModes>
</div>


<cti:displayForPageEditModes modes="EDIT,VIEW">

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
                <cti:url value="/capcontrol/substations/${substationId}" var="substationLink" />
                
                <tr data-pao-id="${substationId}">
                    <%-- ROW ICON --%>
                    <td>
                        <capTags:warningImg paoId="${substationId}" type="SUBSTATION"/>
                    </td>
                    <%-- SUB NAME --%>
                    <td>
                        <a href="${substationLink}" id="anc_${substationId}">
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
</cti:displayForPageEditModes>
    
</form:form>
    
<cti:url var="url" value="/capcontrol/areas/${area.id}"/>
<form:form id="delete-area" method="DELETE" action="${url}">
    <cti:csrfToken/>
</form:form>
    
</cti:standardPage>