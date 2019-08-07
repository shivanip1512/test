<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dr" page="setup.controlArea.${mode}">
    <tags:setFormEditMode mode="${mode}" />
    
    <input id="js-form-edit-mode" value="${mode}" type="hidden"/>
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <cti:url var="createUrl" value="/dr/setup/controlArea/create" />
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" href="${createUrl}"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="UPDATE">
                <cti:url var="editUrl" value="/dr/setup/controlArea/${controlArea.controlAreaId}/edit"/>
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="OWNER">
                <li class="divider"></li>
                <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="delete-option" data-ok-event="yukon:controlArea:delete"/>
            
                <d:confirm on="#delete-option" nameKey="confirmDelete" argument="${controlArea.name}" />
                <cti:url var="deleteUrl" value="/dr/setup/controlArea/${controlArea.controlAreaId}/delete"/>
                <form:form id="delete-controlArea-form" action="${deleteUrl}" method="delete" modelAttribute="controlArea">
                    <tags:hidden path="controlAreaId"/>
                    <tags:hidden path="name"/>
                    <cti:csrfToken/>
                </form:form>
            </cti:checkRolesAndProperties>
        </div>
    </cti:displayForPageEditModes>
    
    <cti:url var="action" value="/dr/setup/controlArea/save" />
    <form:form modelAttribute="controlArea" action="${action}" method="post" id="js-control-area-form">
        <cti:csrfToken />
        <form:hidden path="controlAreaId"/>
        
        <input id="js-trigger-ids" name="triggerIds" type="hidden"/>
        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="general">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <tags:input id="name" path="name" size="25" maxlength="60"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".controlInterval">
                            <tags:intervalDropdown path="controlInterval" intervals="${controlIntervals}" noneKey=".newDataOnly"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".minResponseTime">
                            <tags:intervalDropdown path="minResponseTime" intervals="${controlIntervals}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".dailyDefaultState">
                            <tags:selectWithItems path="dailyDefaultState" items="${defaultStates}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".requireAllTriggers">
                            <tags:switchButton path="allTriggersActiveFlag" onNameKey=".yes.label" offNameKey=".no.label"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
            <div class="column two nogutter">
                <tags:sectionContainer2 nameKey="trigger">
                    <div class="js-trigger-template-row select-box-item dn" data-trigger-id="0">
                        <span class="js-trigger-name"></span>
                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove-trigger" />
                    </div>
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <c:if test="${empty controlArea.triggers}">
                            <span class="empty-list js-no-triggers dn"><i:inline key=".noTriggersAssigned" /></span>
                        </c:if>
                        <div class="js-trigger-container select-box">
                            <c:forEach var="trigger" items="${controlArea.triggers}" varStatus="status">
                                <c:set var="triggerId" value="${triggerIds[status.index]}" />
                                <cti:url var="triggerUrl" value="/dr/setup/controlArea/renderTrigger/${triggerId}?mode=${mode}" />
                                <div class="select-box-item" data-trigger-id="${triggerId}" id="js-trigger-${triggerId}">
                                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove"
                                        id="delete-trigger-${triggerId}" data-ok-event="yukon:trigger:delete" data-id="${triggerId}"/>
                                    <d:confirm on="#delete-trigger-${triggerId}" nameKey="confirmDelete"
                                        argument="Trigger"/>
                                    <a href="${triggerUrl}" class="js-trigger-link" data-popup="#js-trigger-dialog-${triggerId}"
                                        id="js-trigger-link-${triggerId}" data-trigger-id="${triggerId}"> 
                                            <cti:triggerName pointId="${trigger.triggerPointId}" type="${trigger.triggerType}" />
                                    </a>
                                </div>
                                <div data-dialog 
                                    id="js-trigger-dialog-${triggerId}"
                                    data-url="${triggerUrl}"
                                    data-target="#js-trigger-link-${triggerId}"
                                    data-event="yukon:dr:setup:controlArea:saveTrigger"
                                    data-load-event="yukon:dr:setup:controlArea:renderTriggerFields"
                                    data-width="600"
                                    data-height="auto"
                                    data-title="<cti:triggerName pointId="${trigger.triggerPointId}" type="${trigger.triggerType}"/>">
                                </div>
                            </c:forEach>
                        </div>
                        <br />
                        <div class="action-area">
                            <cti:url var="triggerPopupUrl" value="/dr/setup/controlArea/renderTrigger" />
                            <cti:msg2 var="triggerTitle" key="yukon.web.modules.dr.setup.controlArea.triggerCreate.title" />
                            <cti:button nameKey="create" icon="icon-plus-green" classes="fr js-create-trigger" data-popup="#js-add-triggers" />
                        </div>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <c:choose>
                            <c:when test="${empty controlArea.triggers}">
                                <span class="empty-list js-no-triggers dn"><i:inline key=".noTriggersAssigned" /></span>
                            </c:when>
                            <c:otherwise>
                                <table class="compact-results-table dashed">
                                    <thead>
                                        <tr>
                                            <th><i:inline key="yukon.common.name" /></th>
                                            <th><i:inline key="yukon.common.type" /></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="trigger" items="${controlArea.triggers}" varStatus="status">
                                            <tr>
                                                <td>
                                                    <c:set var="triggerId" value="${triggerIds[status.index]}"/>
                                                    <cti:url var="triggerUrl" value="/dr/setup/controlArea/renderTrigger/${triggerId}?mode=${mode}"/>
                                                    <a href="${triggerUrl}" class="js-trigger-link" data-popup="#js-trigger-dialog-${triggerId}"
                                                        id="js-trigger-link-${triggerId}" data-trigger-id="${triggerId}">
                                                            <cti:triggerName pointId="${trigger.triggerPointId}" type="${trigger.triggerType}" />
                                                    </a>
                                                </td>
                                                <td>
                                                    <i:inline key="yukon.web.modules.dr.setup.controlArea.trigger.${trigger.triggerType}" />
                                                </td>
                                                <div data-dialog 
                                                    id="js-trigger-dialog-${triggerId}"
                                                    data-url="${triggerUrl}"
                                                    data-target="#js-trigger-link-${triggerId}"
                                                    data-event="yukon:dr:setup:controlArea:saveTrigger"
                                                    data-load-event="yukon:dr:setup:controlArea:renderTriggerFields"
                                                    data-width="600"
                                                    data-height="auto"
                                                    data-title="<cti:triggerName pointId="${trigger.triggerPointId}" type="${trigger.triggerType}"/>">
                                                </div>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </cti:displayForPageEditModes>
                    <div data-dialog
                        id="js-add-triggers"
                        data-url="${triggerPopupUrl}"
                        data-event="yukon:dr:setup:controlArea:saveTrigger"
                        data-load-event="yukon:dr:setup:controlArea:renderTriggerFields"
                        data-width="600"
                        data-height="auto"
                        data-title="<cti:msg2 key="yukon.web.modules.dr.setup.controlArea.trigger.title"/>" class="dn">
                    </div>
                    <div data-dialog
                        data-event="yukon:dr:setup:controlArea:saveTrigger"
                        data-load-event="yukon:dr:setup:controlArea:renderTriggerFields"
                        data-width="600" data-height="auto"
                        data-title="<cti:msg2 key="yukon.web.modules.dr.setup.controlArea.trigger.title"/>"
                        class="dn js-trigger-dialog-template">
                    </div>
                </tags:sectionContainer2>
                <tags:sectionContainer2 nameKey="controlWindow">
                    <c:set var="controlWindowEnabled" value="${controlArea.dailyStartTimeInMinutes > 0 || controlArea.dailyStopTimeInMinutes > 0}"/>
                    <tags:nameValueContainer2>
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <div class="notes"><i:inline key=".controlWindow.note"/></div><br/>
                        </cti:displayForPageEditModes>
                        <tags:nameValue2 nameKey=".useControlWindow">
                            <tags:switchButton classes="js-control-window" name="controlWindow" toggleGroup="controlWindow" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label" 
                                checked="${controlWindowEnabled}"/>
                        </tags:nameValue2>
                        <c:set var="controlWindowClass" value="${controlWindowEnabled ? '' : 'dn'}"/>
                        <tags:nameValue2 nameKey=".startTime" data-toggle-group="controlWindow" rowClass="${controlWindowClass}">
                            <input type="hidden" id="dailyStartTimeInMinutes" name="dailyStartTimeInMinutes" value="${controlArea.dailyStartTimeInMinutes}"/>
                            <dt:time name="dailyStartTime" id="dailyStartTime" value="${dailyStartTime}"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".stopTime" data-toggle-group="controlWindow" rowClass="${controlWindowClass}">
                            <input type="hidden" id="dailyStopTimeInMinutes" name="dailyStopTimeInMinutes" value="${controlArea.dailyStopTimeInMinutes}"/>
                            <dt:time name="dailyStopTime" id="dailyStopTime" value="${dailyStopTime}"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </div>
        
        </div>
        <div class="column-24 clearfix">
            <tags:sectionContainer2 nameKey="programAssignments">
                <cti:displayForPageEditModes modes="EDIT,CREATE">
            
                    <div class="column-12-12 clearfix select-box bordered-div">
                        <!-- Available Programs -->
                        <div class="column one">
                            <h3><i:inline key="yukon.common.available"/></h3>
                            <div class="column one bordered-div">
                            
                                <div id="js-inline-picker-container" style="height:470px;" class="oa">
                                    <tags:pickerDialog id="js-avaliable-programs-picker" type="unassignedProgramPicker" container="js-inline-picker-container"
                                                       multiSelectMode="${true}" disabledIds="${assignedProgramIds}" extraArgs="${controlArea.controlAreaId}"/>
                                </div>
                                <div class="action-area">
                                    <cti:button nameKey="add" classes="fr js-add-program" icon="icon-add"/>
                                </div>
                            </div>
                        </div>
                    
                        <!-- Assigned Programs -->
                        <div class="column two nogutter oa" style="height:560px;">
                            <h3 class="dib"><i:inline key="yukon.common.assigned"/></h3>
                            <table id="program-assignments" class="compact-results-table dashed">
                                <thead>
                                    <th width="10%"></th>
                                    <th width="50%"></th>
                                    <th><i:inline key=".startPriority"/></th>
                                    <th><i:inline key=".stopPriority"/></th>
                                </thead>
                                <tbody>
                                    <c:forEach var="program" items="${controlArea.programAssignment}" varStatus="status">
                                        <tr>
                                            <tags:hidden path="programAssignment[${status.index}].programId"/>
                                            <td>                                            
                                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove" data-id="${program.programId}"/>
                                            </td>
                                            <td>
                                                <cti:deviceName deviceId="${program.programId}"/>
                                            </td>
                                            <td>
                                                <tags:numeric path="programAssignment[${status.index}].startPriority" size="4" minValue="1" maxValue="2147483647"/>
                                            </td>
                                            <td>
                                                <tags:numeric path="programAssignment[${status.index}].stopPriority" size="4" minValue="1" maxValue="2147483647"/>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                </tbody>
                            
                            </table>
                            
                            <table>                                          
                                <tr class="js-template-row dn">
                                    <input type="hidden" class="js-program-id" name="programAssignment[0].programId" disabled="disabled"/>
                                    <td>                                            
                                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove" data-id="0"/>
                                    </td>
                                    <td class="js-program-name"></td>
                                    <td>
                                        <input type="text" name="programAssignment[0].startPriority" size="4" data-min-value="1" data-max-value="2147483647" disabled="true" class="js-start-priority"/>
                                    </td>
                                    <td>
                                        <input type="text" name="programAssignment[0].stopPriority" size="4" data-min-value="1" data-max-value="2147483647" disabled="true" class="js-stop-priority"/>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW">
                    <table id="js-assigned-programs-table" class="compact-results-table dashed">
                        <thead>
                            <tr>
                                <th><i:inline key="yukon.common.name"/></th>
                                <th><i:inline key=".startPriority"/></th>
                                <th><i:inline key=".stopPriority"/></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="assignedProgram" items="${controlArea.programAssignment}">
                                <tr>
                                    <td>
                                        <cti:url var="viewUrl" value="/dr/setup/program/${assignedProgram.programId}"/>
                                        <a href="${viewUrl}"><cti:deviceName deviceId="${assignedProgram.programId}"/></a>
                                    </td>
                                    <td>
                                        ${assignedProgram.startPriority}
                                    </td>
                                    <td>
                                        ${assignedProgram.stopPriority}
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty controlArea.programAssignment}">
                                <tr><td><span class="empty-list"><i:inline key=".noProgramsAssigned"/></span></td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </cti:displayForPageEditModes>
            </tags:sectionContainer2>
        </div>
        
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" classes="primary action js-save"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/dr/setup/controlArea/${controlArea.controlAreaId}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button id="js-cancel-btn" nameKey="cancel" />
            </cti:displayForPageEditModes>
        </div>
        
    </form:form>
        
    <cti:includeScript link="JQUERY_SCROLL_TABLE_BODY"/>
    <cti:includeScript link="YUKON_TIME_FORMATTER"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.controlArea.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.controlArea.trigger.js" />

</cti:standardPage>