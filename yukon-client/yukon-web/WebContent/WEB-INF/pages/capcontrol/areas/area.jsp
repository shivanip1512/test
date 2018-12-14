<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>


<c:set var="pageName" value="area.${mode}"/>
<cti:standardPage module="capcontrol" page="${pageName}">

<cti:includeScript link="/resources/js/pages/yukon.da.area.js"/>
<%@ include file="/capcontrol/capcontrolHeader.jspf" %>
<tags:setFormEditMode mode="${mode}"/>

<cti:checkRolesAndProperties value="AREA_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
    <c:set var="hasAreaCommandsAndActionsAccess" value="true"/>
</cti:checkRolesAndProperties>
<cti:checkRolesAndProperties value="SUBSTATION_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
    <c:set var="hasSubstationCommandsAndActionsAccess" value="true"/>
</cti:checkRolesAndProperties>

<c:if test="${hasAreaCommandsAndActionsAccess}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="areaState_"]');
    </script>
</c:if>

<c:if test="${hasSubstationCommandsAndActionsAccess}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="substationState"]');
    </script>
</c:if>

<div class="js-page-additional-actions dn">
    <cti:displayForPageEditModes modes="VIEW,EDIT">
        <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <li class="divider" />
            </cti:checkRolesAndProperties>
        </cti:checkRolesAndProperties>

        <c:if test="${showAnalysis}">
            <i:simplePopup styleClass="js-analysis-trends" titleKey=".analysisTrends" id="analysisTrendsOptions" on="#analysisTrendsButton">
                <%@ include file="../tier/analysisTrendsOptions.jspf" %>
            </i:simplePopup>
            <cm:dropdownOption key=".analysis.label" id="analysisTrendsButton" icon="icon-chart-line" />
        </c:if>
    
        <i:simplePopup styleClass="js-actions-recentEvents" titleKey=".recentEvents" id="recentEventsOptions" on="#recentEventsButton">
            <%@ include file="../tier/recentEventsOptions.jspf" %>
        </i:simplePopup>
        <cm:dropdownOption key=".recentEvents.label" id="recentEventsButton" icon="icon-application-view-columns" />
        
        <c:if test="${hasAreaCommandsAndActionsAccess}">
             <cm:dropdownOption linkId="areaState_${areaId}" key=".area.actions" icon="icon-cog" href="javascript:void(0);" />
        </c:if>
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cm:dropdownOption key=".edit.stations" icon="icon-add-remove" data-popup=".js-edit-stations-popup" />
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>

    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cti:url var="editUrl" value="/capcontrol/areas/${areaId}/edit" />
            <cm:dropdownOption key="components.button.edit.label" icon="icon-pencil" href="${editUrl}" />
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
    
    <li class="divider" />
    <cm:dropdownOption classes="js-show-comments" key=".menu.viewComments" icon="icon-comment" data-pao-id="${areaId}" 
        data-pao-name="${fn:escapeXml(area.name)}"/>
    <cti:url var="recentEventsUrl" value="/capcontrol/search/recentEvents?value=${areaId}" />
    <cm:dropdownOption href="${recentEventsUrl}" key=".menu.viewRecentEvents" icon="icon-calendar-view-month"/>
</div>

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
                 <cti:displayForPageEditModes modes="VIEW">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
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
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
                        <tags:nameValue2 nameKey="yukon.common.name">
                            <tags:input path="name" size="35" maxlength="60" autofocus="autofocus"/>
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
                        <tags:nameValue2 nameKey=".voltReduction" valueClass="PT10">
                            <tags:pickerDialog 
                                id="voltReduction" 
                                type="voltReductionPointPicker" 
                                linkType="selectionLabel" 
                                selectionProperty="pointName"
                                destinationFieldName="voltReductionPoint" 
                                initialId="${area.voltReductionPoint}" 
                                buttonStyleClass="M0"
                                allowEmptySelection="${true}"
                                includeRemoveButton="${true}"
                                removeValue="0" />
                        </tags:nameValue2>
                     </tags:nameValueContainer2>
                    <div class="page-action-area">
                         <cti:button nameKey="save" type="submit" classes="primary action"/>

                        <cti:displayForPageEditModes modes="EDIT">
                
                            <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:area:delete"/>
                            <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${area.name}"/>
                
                            <cti:url var="viewUrl" value="/capcontrol/areas/${areaId}"/>
                            <cti:button nameKey="cancel" href="${viewUrl}"/>
                
                        </cti:displayForPageEditModes>
                
                        <cti:displayForPageEditModes modes="CREATE">
                            <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
                        </cti:displayForPageEditModes>
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
                    <c:set var="isPointCreate" value= "false" />
                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                        <c:set var="isPointCreate" value= "true" />
                    </cti:checkRolesAndProperties>
                    <cti:checkRolesAndProperties value="MANAGE_POINTS" level="CREATE">
                        <c:set var="isPointCreate" value= "true" />
                    </cti:checkRolesAndProperties>
                    <c:if test="${isPointCreate}">
                        <div class="action-area">
                            <tags:pointCreation paoId="${areaId}" />
                        </div>
                    </c:if>
                </cti:tab>
            </cti:tabs>
        </div>
    </cti:displayForPageEditModes>
</div>

<cti:displayForPageEditModes modes="EDIT,VIEW">

    <tags:sectionContainer2 nameKey="substationsContainer" arguments="${areaName}">
           
        <table id="subTable" class="compact-results-table has-alerts dashed row-highlighting has-actions">
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
                    <th class="tar"><i:inline key=".pfactorEstimatedOps"/></th>
                    <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
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
                        <span style="margin-right:10px;"><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="PFACTOR" initialize="false"/>
                    </td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <c:if test="${hasSubstationCommandsAndActionsAccess}">
                                <li>
                                    <a id="substationState_${substationId}" href="javascript:void(0)" class="clearfix">
                                        <cti:icon icon="icon-cog" /><span class="dib"><i:inline key=".substation.actions"/></span>
                                    </a>
                                </li>
                                <li class="divider" />
                            </c:if>
                            <cm:dropdownOption classes="js-show-comments" key=".menu.viewComments" icon="icon-comment" data-pao-id="${substationId}" 
                                data-pao-name="${fn:escapeXml(station.name)}"/>
                            <cti:url var="recentEventsUrl" value="/capcontrol/search/recentEvents?value=${substationId}" />
                            <cm:dropdownOption href="${recentEventsUrl}" key=".menu.viewRecentEvents" icon="icon-calendar-view-month"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
            
        </table>

    </tags:sectionContainer2>

</cti:displayForPageEditModes>
    
</form:form>
    
<cti:url var="url" value="/capcontrol/areas/${area.id}"/>
<form:form id="delete-area" method="DELETE" action="${url}">
    <cti:csrfToken/>
</form:form>
    
</cti:standardPage>