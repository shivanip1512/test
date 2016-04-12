<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageName" value="bus.${mode}"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="bus.orphan.${mode}"/>
</c:if>

<cti:standardPage module="capcontrol" page="${pageName}">

<cti:includeScript link="/resources/js/pages/yukon.da.bus.js"/>

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>
<tags:setFormEditMode mode="${mode}"/>

<c:if test="${hasSubBusControl}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="busState_"]');
    </script>
</c:if>

<div class="js-page-additional-actions dn">
    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <li class="divider" />
    </cti:checkRolesAndProperties>
    <c:if test="${hasSubBusControl}">
        <cm:dropdownOption linkId="busState_${bus.id}" key=".substation.bus.actions" icon="icon-cog" href="javascript:void(0);" />
    </c:if>
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <cti:url var="editUrl" value="/capcontrol/buses/${bus.id}/edit" />
        <cm:dropdownOption  key="components.button.edit.label" icon="icon-pencil" href="${editUrl}" />
    </cti:checkRolesAndProperties>
</div>

<cti:url var="action" value="/capcontrol/buses"/>
<form:form commandName="bus" action="${action}" method="POST">
    <cti:csrfToken/>
    <div class="column-12-12">
        <div class="column one">
            <cti:tabs>
                <cti:msg2 var="infoTab" key=".infoTab"/>
                <cti:tab title="${infoTab}">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <tags:switchButton path="capControlSubstationBus.multiMonitorControlBoolean" classes="dn"/>
                            <form:hidden path="pAOStatistics"/>
                            <form:hidden path="id"/>
                            <tags:input path="name"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".status">
                            <tags:switchButton path="disabled" inverse="${true}"
                                offNameKey=".disabled.label" onNameKey=".enabled.label" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".geoName">
                            <tags:input path="geoAreaName"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".mapLocationID">
                            <tags:input path="capControlSubstationBus.mapLocationID"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".parent">
                            <c:if test="${empty parent }">
                                <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                            </c:if>
                            <c:if test="${not empty parent}">
                                <cti:url var="editParent" value="/capcontrol/substations/${parent.liteID}"/>
                                    <a href="${editParent}">${parent.paoName}</a>
                            </c:if>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </cti:tab>
                <cti:displayForPageEditModes modes="EDIT,VIEW">
                    <%@ include file="strategyTab.jsp" %>
                    <cti:msg2 var="schedulesTab" key=".schedulesTab"/>
                    <cti:tab title="${schedulesTab}">
                        <table class="full-width stacked">
                            <thead>
                                <tr>
                                    <th><i:inline key=".schedules.schedule"/></th>
                                    <th><i:inline key=".schedules.command"/></th>
                                    <th><i:inline key=".schedules.ovuv"/></th>
                                </tr>
                            </thead>
                            <tfoot></tfoot>
                            <tbody>
                                <c:forEach var="schedule" items="${bus.schedules}" varStatus="status">
                                <c:set var="idx" value="${status.index}" />
                                <tr>
                                    <td>
                                        <c:forEach var="scheduleOption" items="${allSchedules}">
                                            <c:if test="${schedule.scheduleID == scheduleOption.id}">
                                                ${fn:escapeXml(scheduleOption.name)}                                            
                                            </c:if>                              
                                        </c:forEach>
                                    </td>
                                    <td>
                                        ${fn:escapeXml(schedule.command)}                                            
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${schedule.disableOvUvBoolean}">
                                                <span class="success">On</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="error">Off</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                </c:forEach>
                            </tbody>
                            </table>
                            <c:if test="${canEdit}">
                                <div class="action-area">
                                    <cti:button nameKey="edit" icon="icon-pencil" data-popup=".js-edit-sched-popup"
                                        data-popup-toggle="" />
                                </div>
                            </c:if>
                    </cti:tab>
                </cti:displayForPageEditModes>
            </cti:tabs>
        </div>
        <cti:displayForPageEditModes modes="EDIT,VIEW">
            <div class="column two nogutter">
                <cti:tabs>
                    <cti:msg2 var="controlPointsTab" key=".controlPointsTab"/>
                    <cti:tab title="${controlPointsTab}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".points.voltReduction">
                                <form:hidden id="volt-reduction-bus-point-input" path="capControlSubstationBus.voltReductionPointId"/>
                                <tags:pickerDialog
                                    id="voltReductionPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="volt-reduction-bus-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.voltReductionPointId}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".points.substationBusDisable">
                                <%-- Point Picker --%>
                                <form:hidden id="disable-bus-point-input" path="capControlSubstationBus.disableBusPointId"/>
                                <tags:pickerDialog
                                    id="disableBusPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="disable-bus-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.disableBusPointId}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
    
                            <tags:nameValue2 nameKey=".points.usePerPhaseVarData">
                                <tags:switchButton path="capControlSubstationBus.usePhaseDataBoolean" 
                                    toggleGroup="perPhase" toggleAction="hide" classes="js-per-phase"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".points.useTotalizedValues" data-toggle-group="perPhase">
                                <tags:switchButton path="capControlSubstationBus.controlFlagBoolean"/>
                            </tags:nameValue2>
    
                            <%-- Var point when not per phase --%>
                            <%-- Phase A when not per phase --%>
                            <tags:nameValue2 nameKey=".points.varPhaseA">
                                <form:hidden id="var-point-input" path="capControlSubstationBus.currentVarLoadPointID"/>
                                <tags:pickerDialog
                                    id="varPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="var-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.currentVarLoadPointID}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".points.phaseB" data-toggle-group="perPhase">
                                <form:hidden id="phase-b-point-input" path="capControlSubstationBus.phaseB"/>
                                <tags:pickerDialog
                                    id="phaseBPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="phase-b-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.phaseB}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".points.phaseC" data-toggle-group="perPhase">
                                <form:hidden id="phase-c-point-input" path="capControlSubstationBus.phaseC"/>
                                <tags:pickerDialog
                                    id="phaseCPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="phase-c-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.phaseC}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
    
                            <tags:nameValue2 nameKey=".points.watt">
                                <form:hidden id="watt-point-input" path="capControlSubstationBus.currentWattLoadPointID"/>
                                <tags:pickerDialog
                                    id="wattPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="watt-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.currentWattLoadPointID}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
    
                            <tags:nameValue2 nameKey=".points.volt">
                                <form:hidden id="volt-point-input" path="capControlSubstationBus.currentVoltLoadPointID"/>
                                <tags:pickerDialog
                                    id="voltPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="volt-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.currentVoltLoadPointID}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </cti:tab>
                    <cti:msg2 var="attachedPointsTab" key=".attachedPointsTab"/>
                    <cti:tab title="${attachedPointsTab}">
                        <div class="scroll-md">
                            <%@ include file="pointsTable.jsp" %>
                        </div>
                        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                            <div class="action-area">
                                <tags:pointCreation paoId="${bus.id}" />
                            </div>
                        </cti:checkRolesAndProperties>
                    </cti:tab>
                    <cti:msg2 var="dualBusTab" key=".dualBusTab"/>
                    <cti:tab title="${dualBusTab}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".bus.enableDualBus">
                                <tags:switchButton path="capControlSubstationBus.dualBusEnabledBoolean" toggleGroup="dualBus" toggleAction="hide" classes="js-dual-bus"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".bus.alternateBus" data-toggle-group="dualBus">
                                <form:hidden id="switch-alt-bus-input" path="capControlSubstationBus.altSubPAOId"/>
                                <tags:pickerDialog
                                    id="altBusPicker"
                                    type="capControlSubstationBusPicker"
                                    linkType="selection"
                                    selectionProperty="paoName"
                                    destinationFieldId="switch-alt-bus-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.altSubPAOId}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".switchPoint">
                                <form:hidden id="switch-point-input" path="capControlSubstationBus.switchPointID"/>
                                <tags:pickerDialog
                                    id="switchPointPicker"
                                    type="pointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="switch-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty bus.capControlSubstationBus.switchPointID}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </cti:tab>
                </cti:tabs>
            </div>
        </cti:displayForPageEditModes>
    </div>
    <cti:csrfToken/>
    
    
    <div class="page-action-area tabbed-container">

<%--         <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cti:url var="editUrl" value="/capcontrol/buses/${bus.id}/edit"/>
            <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
        </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes> --%>

        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:button nameKey="save" type="submit" classes="primary action"/>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="EDIT">

            <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:bus:delete" />
            <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${bus.name}"/>

            <cti:url var="viewUrl" value="/capcontrol/buses/${bus.id}"/>
            <cti:button nameKey="cancel" href="${viewUrl}"/>

        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="CREATE">
            <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
        </cti:displayForPageEditModes>
    </div>
    
    <cti:displayForPageEditModes modes="EDIT,VIEW">
        <tags:boxContainer2 nameKey="feedersSection">
            <c:if test="${empty feederList}">
                <span class="empty-list"><i:inline key=".bus.noAssignedFeeders"/></span>
            </c:if>
            <c:if test="${not empty feederList}">
                <c:if test="${not orphan}">
                    <cti:msgScope paths=",yukon.web.modules.capcontrol.substation">
                        <%@ include file="tier/feederTable.jsp" %>
                    </cti:msgScope>
                </c:if>
                <c:if test="${orphan}">
                    <ul class="striped-list simple-list">
                        <c:forEach var="feeder" items="${feederList}">
                        <li>
                             <cti:url var="feederUrl" value="/capcontrol/feeders/${feeder.ccId}" />
                                <a href="${feederUrl}">
                                    ${fn:escapeXml(feeder.ccName)}
                                </a> 
                        </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </c:if>
            
            <c:if test="${canEdit}">
                <div class="action-area">
                    <cti:button nameKey="edit.feeders" icon="icon-pencil"
                        data-popup=".js-edit-feeders-popup" data-popup-toggle=""/>
                </div>
            </c:if>
            
        </tags:boxContainer2>    
            
    </cti:displayForPageEditModes>


</form:form>

<cti:url var="url" value="/capcontrol/buses/${bus.id}"/>
<form:form id="delete-bus" method="DELETE" action="${url}">
    <cti:csrfToken/>
</form:form>

<%-- EDIT FEEDERS POPUP --%>
<div class="dn js-edit-feeders-popup"
    data-dialog
    data-parent-id="${bus.id}"
    data-title="<cti:msg2 key=".edit.feeders"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/buses/${bus.id}/feeders/edit"/>"
    data-event="yukon:vv:children:save"
    data-height="300"
    data-width="750"></div>

<%-- EDIT STRATEGY ASSIGNMENT POPUP --%>
<div class="dn js-edit-strat-popup"
    data-dialog
    data-object-id="${bus.id}"
    data-title="<cti:msg2 key=".edit.strategy"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/strategy-assignment/${bus.id}"/>"
    data-event="yukon:vv:strategy-assignment:save"
    data-width="500"></div>
    
    <%-- EDIT SCHEDULES POPUP --%>
<div class="dn js-edit-sched-popup"
    data-dialog
    data-object-id="${bus.id}"
    data-title="<cti:msg2 key=".edit.schedule"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/buses/${bus.id}/schedules/edit"/>"
    data-event="yukon:vv:schedule:save"
    data-width="750"></div>
    

</cti:standardPage>