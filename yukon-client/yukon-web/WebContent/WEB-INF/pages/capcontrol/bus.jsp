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

<cti:url var="action" value="/capcontrol/buses"/>
<form:form commandName="bus" action="${action}" method="POST">
    <div class="column-12-12">
        <div class="column one">
            <cti:tabs>
                <cti:tab title="Info">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Name">
                            <tags:switchButton path="capControlSubstationBus.multiMonitorControlBoolean" classes="dn"/>
                            <form:hidden path="pAOStatistics"/>
                            <form:hidden path="id"/>
                            <tags:input path="name"/>
                        </tags:nameValue>
                        <tags:nameValue name="Disabled">
                            <tags:switchButton path="disabled"/>
                        </tags:nameValue>
                        <tags:nameValue name="Geographical Name">
                            <tags:input path="geoAreaName"/>
                        </tags:nameValue>
                        <tags:nameValue name="Map Location ID">
                            <tags:input path="capControlSubstationBus.mapLocationID"/>
                        </tags:nameValue>
                        <tags:nameValue name="Parent">
                            <c:if test="${empty parentName}">
                                <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                            </c:if>
                            <c:if test="${not empty parentName}">
                                <cti:url var="parentLink" value="${parentLink}" />
                                <a href="${parentLink}">${parentName}</a>
                            </c:if>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </cti:tab>
                <cti:tab title="Strategy">

                    <%-- SEASON SCHEDULE --%>
                    <tags:nameValueContainer2 tableClass="stacked">
                        <tags:nameValue2 nameKey=".schedule.season">
                            <c:set var="clazz" value="${seasonSchedule.exists ? 'hint' : ''}"/>
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
                                                <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
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
                            <c:set var="clazz" value="${holidaySchedule.exists ? 'hint' : ''}"/>
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
                <%-- TODO Make this tab fully functional --%>
                <cti:tab title="Schedules">
                    <table class="section-table">
                        <thead>
                            <tr>
                                <th>Schedule</th>
                                <th>Command</th>
                                <th>OV/UV</th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach var="schedule" items="${bus.schedules}" varStatus="status">
                            <c:set var="idx" value="${status.index}" />
                            <tr>
                                <td>
                                    <%-- TODO Select Schedule--%>
                                    <tags:input path="schedules[${idx}].scheduleID"/>
                                </td>
                                <td>
                                    <%-- TODO Select for defaults --%>
                                    <tags:input path="schedules[${idx}].command"/>
                                </td>
                                <td>
                                    <tags:switchButton path="schedules[${idx}].disableOvUvBoolean"/>
                                </td>
                            </tr>
                            </c:forEach>
                            <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <c:set var="idx" value="${fn:length(bus.schedules)}" />
                            <tr>
                                <td>
                                    <tags:input path="schedules[${idx}].scheduleID"/>
                                </td>
                                <td>
                                    <tags:input path="schedules[${idx}].command"/>
                                </td>
                                <td>
                                    <tags:switchButton path="schedules[${idx}].disableOvUvBoolean"/>
                                </td>
                            </tr>
                            </cti:displayForPageEditModes>
                        </tbody>
                        </table>
                </cti:tab>
                <%-- TOOD Maybe an add schedule button? --%>
            </cti:tabs>
        </div>
        <div class="column two nogutter">
            <cti:tabs>
                <cti:tab title="Control Points">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Volt Reduction Control Point">
                            <form:hidden id="volt-reduction-bus-point-input" path="capControlSubstationBus.voltReductionPointId"/>
                            <tags:pickerDialog
                                id="voltReductionPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="volt-reduction-bus-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.voltReductionPointId}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>
                        <tags:nameValue name="Substation Bus Disable Point">
                            <%-- Point Picker --%>
                            <form:hidden id="disable-bus-point-input" path="capControlSubstationBus.disableBusPointId"/>
                            <tags:pickerDialog
                                id="disableBusPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="disable-bus-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.disableBusPointId}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>

                        <tags:nameValue name="Use Per Phase Var Data">
                            <tags:switchButton path="capControlSubstationBus.usePhaseDataBoolean" 
                                toggleGroup="perPhase" toggleAction="hide"/>
                        </tags:nameValue>
                        <tags:nameValue name="Use Totalized Values" data-toggle-group="perPhase">
                            <tags:switchButton path="capControlSubstationBus.controlFlagBoolean"/>
                        </tags:nameValue>

                        <%-- Var point when not per phase --%>
                        <%-- Phase A when not per phase --%>
                        <%-- TODO Maybe invent a way to change this text? --%>
                        <tags:nameValue name="Var Point / Phase A">
                            <form:hidden id="var-point-input" path="capControlSubstationBus.currentVarLoadPointID"/>
                            <tags:pickerDialog
                                id="varPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="var-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.currentVarLoadPointID}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>
                        <tags:nameValue name="Phase B" data-toggle-group="perPhase">
                            <form:hidden id="phase-b-point-input" path="capControlSubstationBus.phaseB"/>
                            <tags:pickerDialog
                                id="phaseBPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="phase-b-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.phaseB}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>
                        <tags:nameValue name="Phase C" data-toggle-group="perPhase">
                            <form:hidden id="phase-c-point-input" path="capControlSubstationBus.phaseC"/>
                            <tags:pickerDialog
                                id="phaseCPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="phase-c-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.phaseC}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>

                        <tags:nameValue name="Watt Point">
                            <form:hidden id="watt-point-input" path="capControlSubstationBus.currentWattLoadPointID"/>
                            <tags:pickerDialog
                                id="wattPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="watt-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.currentWattLoadPointID}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>

                        <tags:nameValue name="Volt Point">
                            <form:hidden id="volt-point-input" path="capControlSubstationBus.currentVoltLoadPointID"/>
                            <tags:pickerDialog
                                id="voltPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="volt-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.currentVoltLoadPointID}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </cti:tab>
                <cti:tab title="Attached Points">
                    <div class="scroll-md">
                        <c:forEach var="pointMap" items="${points}">
                            <c:if test="${not empty pointMap.value}">
                                <h4><i:inline key="yukon.common.point.pointType.${pointMap.key}"/></h4>
                                <table class="compact-results-table row-highlighting">
                                    <thead></thead>
                                    <tfoot></tfoot>
                                    <tbody>
                                        <c:forEach var="point" items="${pointMap.value}">
                                            <tr>
                                                <td>
                                                    <cti:url var="pointUrl" value="/tools/points/${point.pointId}" /> 
                                                    <a href="${pointUrl}">${fn:escapeXml(point.name)}</a>
                                                </td>
                                                <td class="state-indicator">
                                                    <cti:pointStatus pointId="${point.pointId}" statusPointOnly="${true}"/>
                                                </td>
                                                <td class="wsnw">
                                                    <cti:pointValue pointId="${point.pointId}" format="SHORT" />
                                               </td>
                                               <td class="wsnw">
                                                    <tags:historicalValue pao="${area}" pointId="${point.pointId}" />
                                               </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </c:forEach>
                    </div>
                    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                        <div class="action-area">
                            <tags:pointCreation paoId="${bus.id}" />
                        </div>
                    </cti:checkRolesAndProperties>
                </cti:tab>
                <cti:tab title="Dual Bus">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Enable Dual Bus">
                            <tags:switchButton path="capControlSubstationBus.dualBusEnabledBoolean"/>
                        </tags:nameValue>
                        <tags:nameValue name="Alternate Bus">
                            <%-- TODO Bus picker --%>
                            <tags:input path="capControlSubstationBus.altSubPAOId"/>
                        </tags:nameValue>
                        <tags:nameValue name="Switch Point">
                            <form:hidden id="switch-point-input" path="capControlSubstationBus.switchPointID"/>
                            <tags:pickerDialog
                                id="switchPointPicker"
                                type="pointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="switch-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty bus.capControlSubstationBus.switchPointID}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </cti:tab>
            </cti:tabs>
        </div>
    </div>
    <cti:csrfToken/>

    <tags:sectionContainer title="Feeders" styleClass="clear">
        <c:if test="${empty feederList}">
            <span class="empty-list">No Assigned Feeders</span>
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
                        <cti:url var="feederUrl" value="/editor/cbcBase.jsf">
                            <cti:param name="type" value="2"/>
                            <cti:param name="itemid" value="${feeder.ccId}"/>
                        </cti:url>
                        <a href="${feederUrl}">${fn:escapeXml(feeder.ccName)}</a>
                    </li>
                    </c:forEach>
                </ul>
            </c:if>
        </c:if>
        <c:if test="${canEdit}">
            <div class="action-area">
                <cti:button nameKey="edit" icon="icon-pencil"
                    data-popup=".js-edit-feeders-popup" data-popup-toggle=""/>
            </div>
        </c:if>
    </tags:sectionContainer>


    <div class="page-action-area">

        <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cti:url var="editUrl" value="/capcontrol/buses/${bus.id}/edit"/>
            <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
        </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>

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
</form:form>

<cti:url var="url" value="/capcontrol/buses/${bus.id}"/>
<form:form id="delete-bus" method="DELETE" action="${url}"></form:form>

<%-- EDIT SUBSTATIONS POPUP --%>
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
    

</cti:standardPage>