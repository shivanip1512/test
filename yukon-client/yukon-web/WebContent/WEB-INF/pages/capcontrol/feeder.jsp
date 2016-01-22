<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="pageName" value="feeder.${mode}"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="feeder.orphan.${mode}"/>
</c:if>

<cti:standardPage module="capcontrol" page="${pageName}">
<cti:includeScript link="/resources/js/pages/yukon.da.feeder.js"/>

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

<tags:setFormEditMode mode="${mode}"/>
<cti:url var="action" value="/capcontrol/feeders"/>
<form:form commandName="feeder" action="${action}" method="POST">
    <cti:csrfToken/>
    <div class="column-14-10">
        <div class="column one">
            <cti:tabs>
                <cti:tab title="Info">
                    <tags:nameValueContainer>
                        <tags:nameValue name="Name">
                            <form:hidden path="pAOStatistics"/>
                            <form:hidden path="id"/>
                            <tags:input path="name"/>
                        </tags:nameValue>
                        <tags:nameValue name="Disabled">
                            <tags:switchButton path="disabled"/>
                        </tags:nameValue>
                        <tags:nameValue name="Map Location ID">
                            <tags:input path="capControlFeeder.mapLocationID"/>
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
                </cti:tabs>
               </div>

            <div class="column two nogutter">
            <cti:tabs>
                <cti:tab title="Control Points">
                    <tags:nameValueContainer>

                        <tags:nameValue name="Use Per Phase Var Data">
                            <tags:switchButton path="capControlFeeder.usePhaseDataBoolean" toggleGroup="phaseGroup"/>
                        </tags:nameValue>
                        <%-- Only when per phase --%>
                        <tags:nameValue name="Use Totalized Values">
                            <tags:switchButton path="capControlFeeder.controlFlagBoolean"/>
                        </tags:nameValue>
                
                            <tags:nameValue name="Var Point / Phase A">
                                <form:hidden id="var-point-input" path="capControlFeeder.currentVarLoadPointID" />
                                <tags:pickerDialog
                                    id="varPointPicker"
                                    type="varPointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="var-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"/>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty feeder.capControlFeeder.currentVarLoadPointID || feeder.capControlFeeder.currentVarLoadPointID == 0}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue>
                            
                        <%-- Var point when not per phase --%>
                        <%-- Phase A when per phase --%>
                            <%-- Only when per phase --%>
                            <tags:nameValue name="Phase B">
                                <form:hidden id="phase-b-point-input" path="capControlFeeder.phaseB"/>
                                <tags:pickerDialog
                                    id="phaseBPointPicker"
                                    type="varPointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="phase-b-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"/>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty feeder.capControlFeeder.phaseB || feeder.capControlFeeder.phaseB == 0}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue>
                            <%-- Only when per phase --%>
                            <tags:nameValue name="Phase C">
                                <form:hidden id="phase-c-point-input" path="capControlFeeder.phaseC"/>
                                <tags:pickerDialog
                                    id="phaseCPointPicker"
                                    type="varPointPicker"
                                    linkType="selection"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="phase-c-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"/>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty feeder.capControlFeeder.phaseC || feeder.capControlFeeder.phaseC == 0}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue>

                        <tags:nameValue name="Watt Point">
                            <form:hidden id="watt-point-input" path="capControlFeeder.currentWattLoadPointID"/>
                            <tags:pickerDialog
                                id="wattPointPicker"
                                type="wattPointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="watt-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty feeder.capControlFeeder.currentWattLoadPointID || feeder.capControlFeeder.currentWattLoadPointID == 0}">
                                    <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                </c:if>
                            </cti:displayForPageEditModes>
                        </tags:nameValue>

                        <tags:nameValue name="Volt Point">
                            <form:hidden id="volt-point-input" path="capControlFeeder.currentVoltLoadPointID"/>
                            <tags:pickerDialog
                                id="voltPointPicker"
                                type="voltPointPicker"
                                linkType="selection"
                                selectionProperty="paoPoint"
                                destinationFieldId="volt-point-input"
                                viewOnlyMode="${mode == 'VIEW'}"
                                allowEmptySelection="${true}"/>
                            <cti:displayForPageEditModes modes="VIEW">
                                <c:if test="${empty feeder.capControlFeeder.currentVoltLoadPointID || feeder.capControlFeeder.currentVoltLoadPointID == 0}">
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
                                <tags:pointCreation paoId="${feeder.id}" />
                            </div>
                        </cti:checkRolesAndProperties>
                    </cti:tab>
                </cti:tabs>
             </div>
          </div>
                
    <tags:sectionContainer title="CapBanks" styleClass="clear">
        <c:if test="${empty capBankList}">
            <span class="empty-list">No Assigned Cap Banks</span>
        </c:if>
        <c:if test="${not empty capBankList}">
            <c:if test="${not orphan}">
                <cti:msgScope paths=",yukon.web.modules.capcontrol.substation">
                    <%@ include file="tier/capBankTable.jsp" %>
                </cti:msgScope>
            </c:if>
            <c:if test="${orphan}">
                <ul class="striped-list simple-list">
                    <c:forEach var="capBank" items="${capBankList}">
                    <li>
                        <cti:url var="capBankUrl" value="/capcontrol/cbc/${capBank.ccId}"/>
                        <a href="${capBankUrl}">${capBank.ccName}</a>
                    </li>
                    </c:forEach>
                </ul>
            </c:if>
        </c:if>
        <c:if test="${canEdit}">
            <div class="action-area">
                <cti:button nameKey="edit" icon="icon-pencil"
                    data-popup=".js-edit-capbanks-popup" data-popup-toggle=""/>
            </div>
        </c:if>
    </tags:sectionContainer>
        <div class="page-action-area">

        <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cti:url var="editUrl" value="/capcontrol/feeders/${feeder.id}/edit"/>
            <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
        </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:button nameKey="save" type="submit" classes="primary action"/>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="EDIT">

            <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:feeder:delete" data-feeder-id="${feeder.id}" />
            <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${feeder.name}"/>

            <cti:url var="viewUrl" value="/capcontrol/feeders/${feeder.id}"/>
            <cti:button nameKey="cancel" href="${viewUrl}"/>

        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="CREATE">
            <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
        </cti:displayForPageEditModes>
    </div>
</form:form>

    <cti:url var="url" value="/capcontrol/feeders/${feeder.id}"/>
    <form:form id="delete-feeder" method="DELETE" action="${url}"></form:form>

<%-- EDIT CAP BANKS POPUP --%>
<div class="dn js-edit-capbanks-popup"
    data-dialog
    data-parent-id="${feeder.id}"
    data-title="<cti:msg2 key=".edit.capbanks"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/feeders/${feeder.id}/capbanks/edit"/>"
    data-event="yukon:vv:children:save"
    data-height="300"
    data-width="750"></div>

<%-- EDIT STRATEGY ASSIGNMENT POPUP --%>
<div class="dn js-edit-strat-popup"
    data-dialog
    data-object-id="${feeder.id}"
    data-title="<cti:msg2 key=".edit.strategy"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/strategy-assignment/${feeder.id}"/>"
    data-event="yukon:vv:strategy-assignment:save"
    data-width="500"></div>
    
</cti:standardPage>