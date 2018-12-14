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


<cti:checkRolesAndProperties value="FEEDER_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
    <c:set var="hasFeederCommandsAndActionsAccess" value="true"/>
</cti:checkRolesAndProperties>

<c:if test="${hasFeederCommandsAndActionsAccess}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="feederState_"]');
    </script>
</c:if>

<div class="js-page-additional-actions dn">

    <cti:displayForPageEditModes modes="VIEW,EDIT">
        <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <li class="divider" />
            </cti:checkRolesAndProperties>
        </cti:checkRolesAndProperties>

        <c:if test="${!orphan}">
            <c:if test="${hasFeederCommandsAndActionsAccess}">
                <cm:dropdownOption linkId="feederState_${feeder.id}" key=".substation.feeder.actions" icon="icon-cog" href="javascript:void(0);" />
            </c:if>
        </c:if>
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cm:dropdownOption key=".edit.capbanks" icon="icon-add-remove" data-popup=".js-edit-capbanks-popup" />
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>

    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
            <cti:url var="editUrl" value="/capcontrol/feeders/${feeder.id}/edit" />
            <cm:dropdownOption  key="components.button.edit.label" icon="icon-pencil" href="${editUrl}" />
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
    
    <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
        <li class="divider" />
        <c:set var="dividerAdded" value="true"/>
    </cti:checkRolesAndProperties>
    <c:if test="${hasFeederCommandsAndActionsAccess && !dividerAdded}">
        <li class="divider" />
    </c:if>
    
    <cm:dropdownOption classes="js-show-comments" key=".menu.viewComments" icon="icon-comment" data-pao-id="${feeder.id}" 
        data-pao-name="${fn:escapeXml(feeder.name)}"/>
    <cti:url var="recentEventsUrl" value="/capcontrol/search/recentEvents?value=${feeder.id}" />
    <cm:dropdownOption href="${recentEventsUrl}" key=".menu.viewRecentEvents" icon="icon-calendar-view-month"/>
</div>

<cti:url var="action" value="/capcontrol/feeders"/>
<cti:displayForPageEditModes modes="CREATE">
    <c:if test="${not empty parent}">
        <cti:url var="action" value="/capcontrol/feeders?parentId=${parent.liteID}"/>
    </c:if>
</cti:displayForPageEditModes>
<form:form modelAttribute="feeder" action="${action}" method="POST">
    <cti:csrfToken/>
    <div class="column-12-12">
        <div class="column one">
            <cti:tabs>
                <cti:msg2 var="infoTab" key=".infoTab"/>
                <cti:tab title="${infoTab}">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <form:hidden path="pAOStatistics"/>
                            <form:hidden path="id"/>
                            <tags:input path="name" maxlength="60" autofocus="autofocus"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".status">
                            <tags:switchButton path="disabled" inverse="${true}"
                                offNameKey=".disabled.label" onNameKey=".enabled.label" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".mapLocationID">
                            <tags:input path="capControlFeeder.mapLocationID" maxlength="64"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".parent">
                            <c:if test="${empty parent }">
                                <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                            </c:if>
                            <c:if test="${not empty parent}">
                                <cti:url var="editParent" value="/capcontrol/buses/${parent.liteID}"/>
                                    <a href="${editParent}">${parent.paoName}</a>
                            </c:if>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </cti:tab>
                <cti:displayForPageEditModes modes="EDIT,VIEW">
                    <%@ include file="strategyTab.jsp" %>
                </cti:displayForPageEditModes>
             </cti:tabs>
           </div>
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <div class="column two nogutter">
                <cti:tabs>
                    <cti:msg2 var="controlPointsTab" key=".controlPointsTab"/>
                    <cti:tab title="${controlPointsTab}">
                        <tags:nameValueContainer2>
    
                            <tags:nameValue2 nameKey=".points.usePerPhaseVarData">
                                <tags:switchButton path="capControlFeeder.usePhaseDataBoolean" toggleGroup="perPhase" toggleAction="hide" classes="js-per-phase"/>
                            </tags:nameValue2>
                            <%-- Only when per phase --%>
                            <tags:nameValue2 nameKey=".points.useTotalizedValues" data-toggle-group="perPhase">
                                <tags:switchButton path="capControlFeeder.controlFlagBoolean"/>
                            </tags:nameValue2>
                            
                            <!-- If Use Per Phase Data is On, label should be Phase A otherwise label should be Var Point -->
                            <span id="phaseALabel" class="dn"><i:inline key=".points.phaseA"/></span>
                            <span id="varLabel" class="dn"><i:inline key=".points.var"/></span>
                            <c:set var="varPointDefaultLabel" value=".points.var"/>
                            <c:if test="${feeder.capControlFeeder.usePhaseDataBoolean}">
                                <c:set var="varPointDefaultLabel" value=".points.phaseA"/>
                            </c:if>
                            
                            <tags:nameValue2 id="varPointLabel" nameKey="${varPointDefaultLabel}">  
                                    <form:hidden id="var-point-input" path="capControlFeeder.currentVarLoadPointID" />
                                    <tags:pickerDialog
                                        id="varPointPicker"
                                        type="varPointPicker"
                                        linkType="selectionLabel"
                                        selectionProperty="paoPoint"
                                        destinationFieldId="var-point-input"
                                        viewOnlyMode="${mode == 'VIEW'}"
                                        allowEmptySelection="${true}"
                                        includeRemoveButton="${true}"
                                        removeValue="0" />
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <c:if test="${empty feeder.capControlFeeder.currentVarLoadPointID || feeder.capControlFeeder.currentVarLoadPointID == 0}">
                                            <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                        </c:if>
                                    </cti:displayForPageEditModes>
                                </tags:nameValue2>
                                
                            <%-- Var point when not per phase --%>
                            <%-- Phase A when per phase --%>
                                <%-- Only when per phase --%>
                                <tags:nameValue2 nameKey=".points.phaseB" data-toggle-group="perPhase">
                                    <form:hidden id="phase-b-point-input" path="capControlFeeder.phaseB"/>
                                    <tags:pickerDialog
                                        id="phaseBPointPicker"
                                        type="varPointPicker"
                                        linkType="selectionLabel"
                                        selectionProperty="paoPoint"
                                        destinationFieldId="phase-b-point-input"
                                        viewOnlyMode="${mode == 'VIEW'}"
                                        allowEmptySelection="${true}"
                                        includeRemoveButton="${true}"
                                        removeValue="0" />
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <c:if test="${empty feeder.capControlFeeder.phaseB || feeder.capControlFeeder.phaseB == 0}">
                                            <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                        </c:if>
                                    </cti:displayForPageEditModes>
                                </tags:nameValue2>
                                <%-- Only when per phase --%>
                                <tags:nameValue2 nameKey=".points.phaseC" data-toggle-group="perPhase">
                                    <form:hidden id="phase-c-point-input" path="capControlFeeder.phaseC"/>
                                    <tags:pickerDialog
                                        id="phaseCPointPicker"
                                        type="varPointPicker"
                                        linkType="selectionLabel"
                                        selectionProperty="paoPoint"
                                        destinationFieldId="phase-c-point-input"
                                        viewOnlyMode="${mode == 'VIEW'}"
                                        allowEmptySelection="${true}"
                                        includeRemoveButton="${true}"
                                        removeValue="0" />
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <c:if test="${empty feeder.capControlFeeder.phaseC || feeder.capControlFeeder.phaseC == 0}">
                                            <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                        </c:if>
                                    </cti:displayForPageEditModes>
                                </tags:nameValue2>
    
                            <tags:nameValue2 nameKey=".points.watt">
                                <form:hidden id="watt-point-input" path="capControlFeeder.currentWattLoadPointID"/>
                                <tags:pickerDialog
                                    id="wattPointPicker"
                                    type="wattPointPicker"
                                    linkType="selectionLabel"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="watt-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty feeder.capControlFeeder.currentWattLoadPointID || feeder.capControlFeeder.currentWattLoadPointID == 0}">
                                        <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                    </c:if>
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
    
                            <tags:nameValue2 nameKey=".points.volt">
                                <form:hidden id="volt-point-input" path="capControlFeeder.currentVoltLoadPointID"/>
                                <tags:pickerDialog
                                    id="voltPointPicker"
                                    type="voltPointPicker"
                                    linkType="selectionLabel"
                                    selectionProperty="paoPoint"
                                    destinationFieldId="volt-point-input"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    allowEmptySelection="${true}"
                                    includeRemoveButton="${true}"
                                    removeValue="0" />
                                <cti:displayForPageEditModes modes="VIEW">
                                    <c:if test="${empty feeder.capControlFeeder.currentVoltLoadPointID || feeder.capControlFeeder.currentVoltLoadPointID == 0}">
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
                                    <tags:pointCreation paoId="${feeder.id}" />
                                </div>
                            </cti:checkRolesAndProperties>
                        </cti:tab>
                    </cti:tabs>
                 </div>
             </cti:displayForPageEditModes>
          </div>
          
                        
     <div class="page-action-area tabbed-container">

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
                
     <cti:displayForPageEditModes modes="EDIT,VIEW">   
        <tags:sectionContainer2 nameKey="capBanksSection">
            <c:if test="${empty capBankList}">
                <span class="empty-list"><i:inline key=".feeder.noAssignedCapBanks"/></span>
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
                            <cti:url var="capBankUrl" value="/capcontrol/capbanks/${capBank.ccId}"/>
                            <a href="${capBankUrl}">${capBank.ccName}</a>
                        </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </c:if>
        </tags:sectionContainer2>
    </cti:displayForPageEditModes>

</form:form>

    <cti:url var="url" value="/capcontrol/feeders/${feeder.id}"/>
    <form:form id="delete-feeder" method="DELETE" action="${url}">
        <cti:csrfToken/>
    </form:form>

<%-- EDIT CAP BANKS POPUP --%>
<div class="dn js-edit-capbanks-popup"
    data-dialog
    data-parent-id="${feeder.id}"
    data-title="<cti:msg2 key=".edit.capbanks"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/feeders/${feeder.id}/capbanks/edit"/>"
    data-event="yukon:vv:children:save"
    data-height="500"
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