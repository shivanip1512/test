<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.loadProgram, modules.dr.setup.gear">
    <input type="hidden" class="js-page-mode" value="${mode}">
    <tags:setFormEditMode mode="${mode}" />
    <input id="js-form-mode" value="${mode}" type="hidden"/>
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <cti:url var="createUrl" value="/dr/setup/loadProgram/create" />
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" href="${createUrl}" />
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="UPDATE">
                <cti:url var="editUrl" value="/dr/setup/loadProgram/${loadProgram.programId}/edit" />
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" classes="js-edit-program" />
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="CREATE">
                <li class="divider"></li>

                <!-- Copy -->
                <cm:dropdownOption key="yukon.web.components.button.copy.label" icon="icon-disk-multiple" data-popup="#copy-loadProgram-popup" />
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="DR_SETUP_PERMISSION" level="OWNER">
                <li class="divider"></li>

                <!-- Delete -->
                <cm:dropdownOption icon="icon-cross" key="yukon.web.components.button.delete.label" classes="js-hide-dropdown" id="js-delete-option"
                    data-ok-event="yukon:load-program:delete" />
                <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${loadProgram.name}" />
                <cti:url var="deleteUrl" value="/dr/setup/loadProgram/${loadProgram.programId}/delete" />
                <form:form id="js-delete-load-program-form" action="${deleteUrl}" method="delete" modelAttribute="loadProgram">
                    <tags:hidden path="name" />
                    <cti:csrfToken />
                </form:form>
            </cti:checkRolesAndProperties>
        </div>

        <!-- Copy loadProgram dialog -->
        <cti:msg2 var="copyloadProgramPopUpTitle" key="yukon.web.modules.dr.setup.loadProgram.copy" />
        <cti:url var="renderCopyloadProgramUrl" value="/dr/setup/loadProgram/${loadProgram.programId}/rendercopyloadProgram" />
        <cti:msg2 var="copyText" key="components.button.copy.label" />
        <div class="dn" id="copy-loadProgram-popup"
                        data-title="${copyloadProgramPopUpTitle}" 
                        data-dialog data-ok-text="${copyText}" 
                        data-event="yukon:loadProgram:copy"
                        data-url="${renderCopyloadProgramUrl}"
                        data-width="400"
                        data-height="auto">
        </div>
    </cti:displayForPageEditModes>

    <cti:url var="action" value="/dr/setup/loadProgram/save" />
    <form:form modelAttribute="loadProgram" action="${action}" method="post" id="js-load-program-form">
        <cti:csrfToken />
        <form:hidden path="programId" />
        <input type="hidden" name="selectedGroupIds" id="js-selected-group-ids" />
        <input type="hidden" name="selectedMemberIds" id="js-selected-member-ids" />
        <input type="hidden" name="selectedNotificationGroupIds" id="js-selected-notification-group-ids" />
        <input type="hidden" name="selectedGearsIds" id="js-selected-gear-ids" />
        <input type="hidden" id="js-selected-program" value="${selectedSwitchType}"/>

        <div class="column-12-12 clearfix">
            <div class="column one">
                <tags:sectionContainer2 nameKey="general">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <tags:input id="name" path="name" size="25" maxlength="60"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".type">
                            <cti:displayForPageEditModes modes="CREATE">
                                <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl" />
                                <tags:selectWithItems items="${switchTypes}" id="type" path="type" defaultItemLabel="${selectLbl}" defaultItemValue="" />
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="EDIT,VIEW">
                                <i:inline key="${loadProgram.type}" />
                                <form:hidden path="type" value="${loadProgram.type}" />
                            </cti:displayForPageEditModes>
                        </tags:nameValue2>
                        <c:if test="${not empty selectedSwitchType}">
                            <tags:nameValue2 nameKey=".operationalState" rowClass="noswitchtype">
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <tags:selectWithItems items="${operationalStates}" id="operationalState" path="operationalState"/>
                                </cti:displayForPageEditModes>
                                <cti:displayForPageEditModes modes="VIEW">
                                    <i:inline key="${loadProgram.operationalState}" />
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".constraint" rowClass="noswitchtype">
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <tags:selectWithItems items="${constraints}" id="constraint" path="constraint.constraintId"  itemLabel="name" itemValue="id"/>
                                </cti:displayForPageEditModes>
                                <cti:displayForPageEditModes modes="VIEW">
                                    ${fn:escapeXml(loadProgram.constraint.constraintName)}
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                        </c:if>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <c:if test="${not empty selectedSwitchType}">
                    <tags:sectionContainer2 nameKey="trigger" styleClass="noswitchtype">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".triggerOffset">
                                <tags:input path="triggerOffset" size="15" maxlength="60" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".restoreOffset">
                                <tags:input path="restoreOffset" size="15" maxlength="60" />
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </tags:sectionContainer2>
                </c:if>
            </div>

            <c:if test="${not empty selectedSwitchType}">
                <div class="column two nogutter">
                    <tags:sectionContainer2 nameKey="gears" styleClass="noswitchtype">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <div style="min-height:60px;max-height:120px;" class="bordered-div select-box">
                                <div id="js-assigned-gear" class="select-box-selected js-with-movables" data-item-selector=".select-box-item">
                                    <c:forEach var="item" items="${gearInfos}" varStatus="status">
                                        <div id="${item.id}" class="select-box-item cm js-assigned-gear" data-id="${item.id}">
                                            <cti:url var="viewUrl" value="/dr/setup/loadProgram/gear/${item.id}?mode=${mode}"/> 
                                            <a href="javascript:void(0)" data-popup="#gear-quick-view-${item.id}">
                                                ${fn:escapeXml(item.name)}
                                            </a>
                                            <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-gear-remove"
                                                        id="js-remove-gear-${item.id}" data-ok-event="yukon:dr:setup:program:gearRemoved"
                                                        data-id="${item.id}"/>
                                            <d:confirm on="#js-remove-gear-${item.id}" nameKey="confirmRemove"
                                                       argument="${fn:escapeXml(item.name)}"/>
                                            <div class="select-box-item-movers">
                                                <c:set var="disabled" value="${status.first}" />
                                                <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" disabled="${disabled}" />
                                                <c:set var="disabled" value="${status.last}" />
                                                <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${disabled}" />
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                                <div class="select-box-item cm js-assigned-gear js-template-gears-row dn" data-id="0">
                                    <span class="js-gear-name"></span>
                                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-gear-remove"
                                                data-ok-event="yukon:dr:setup:program:gearRemoved"/>
                                    <div class="select-box-item-movers">
                                        <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" />
                                        <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${true}" />
                                    </div>
                                </div>
                            </div>
                            <div class="action-area">
                                <cti:button icon="icon-add" id="createButton" nameKey="create" data-popup="#gear-create-popup-${selectedSwitchType}" />
                                <cti:url var="createGearUrl" value="/dr/setup/loadProgram/createGearPopup/${selectedSwitchType}" />
                            </div>
                            <cti:msg2 var="programGearCreation" key="yukon.web.modules.dr.setup.gear.title" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <div class="scroll-sm">
                                <table class="compact-results-table dashed">
                                    <thead>
                                        <tr>
                                            <th><i:inline key="yukon.common.name" /></th>
                                            <th><i:inline key="yukon.common.type" /></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="gearInfo" items="${gearInfos}">
                                            <tr>
                                                <td>
                                                    <cti:url var="viewUrl" value="/dr/setup/loadProgram/gear/${gearInfo.id}?mode=${mode}" /> 
                                                    <a href="javascript:void(0);" data-popup="#gear-quick-view-${gearInfo.id}">${fn:escapeXml(gearInfo.name)}</a>
                                                </td>
                                                <div id="gear-quick-view-${gearInfo.id}"
                                                     data-title="${gearInfo.name}"
                                                     data-width="900"
                                                     data-height="525"
                                                     data-load-event="yukon:dr:setup:gear:viewMode" 
                                                     data-height="auto"
                                                     data-url="${viewUrl}">
                                                 </div>
                                                <td><i:inline key="${gearInfo.controlMethod}" /></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </cti:displayForPageEditModes>
                    </tags:sectionContainer2>
                    <div id="js-control-window" class="noswitchtype">
                        <c:if test="${not empty selectedSwitchType}">
                            <tags:sectionContainer2 nameKey="controlWindow">
                                <c:set var="controlWindowOneEnabled"
                                    value="${not empty loadProgram.controlWindow.controlWindowOne.availableStartTimeInMinutes
                                          || not empty loadProgram.controlWindow.controlWindowOne.availableStopTimeInMinutes}"/>
                                <tags:nameValueContainer2>
                                    <tags:nameValue2 nameKey=".controlWindowOne">
                                        <tags:switchButton name="controlWindowOne" toggleGroup="controlWindowOne" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label"
                                            checked="${controlWindowOneEnabled}" id="controlWindowOne"/>
                                    </tags:nameValue2>
                                    <c:set var="controlWindowOneClass" value="${controlWindowOneEnabled ? '' : 'dn'}" />
                                    <tags:nameValue2 nameKey=".startTime" data-toggle-group="controlWindowOne" rowClass="${controlWindowOneClass}">
                                        <dt:timeOffset id="startTimeWindowOne" name="startTimeWindowOne" value="${loadProgram.controlWindow.controlWindowOne.availableStartTimeInMinutes}"/>
                                        <input type="hidden" id="startTimeInMinutesWindowOne" name="controlWindow.controlWindowOne.availableStartTimeInMinutes" value="${loadProgram.controlWindow.controlWindowOne.availableStartTimeInMinutes}"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".stopTime" data-toggle-group="controlWindowOne" rowClass="${controlWindowOneClass}">
                                        <dt:timeOffset id="stopTimeWindowOne" name="stopTimeWindowOne" value="${loadProgram.controlWindow.controlWindowOne.availableStopTimeInMinutes}"/>
                                        <input type="hidden" id="stopTimeInMinutesWindowOne" name="controlWindow.controlWindowOne.availableStopTimeInMinutes" value="${loadProgram.controlWindow.controlWindowOne.availableStopTimeInMinutes}"/>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                                <c:set var="controlWindowTwoEnabled" value="${not empty loadProgram.controlWindow.controlWindowTwo.availableStartTimeInMinutes
                                          || not empty loadProgram.controlWindow.controlWindowTwo.availableStopTimeInMinutes}" />
                                <tags:nameValueContainer2>
                                    <tags:nameValue2 nameKey=".controlWindowTwo">
                                    <tags:switchButton name="controlWindowTwo" toggleGroup="controlWindowTwo" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label"
                                        checked="${controlWindowTwoEnabled}" id="controlWindowTwo" />
                                    </tags:nameValue2>
                                    <c:set var="controlWindowTwoClass" value="${controlWindowTwoEnabled ? '' : 'dn'}" />
                                    <tags:nameValue2 nameKey=".startTime" data-toggle-group="controlWindowTwo" rowClass="${controlWindowTwoClass}">
                                        <dt:timeOffset id="startTimeWindowTwo" name="startTimeWindowTwo" value="${loadProgram.controlWindow.controlWindowTwo.availableStartTimeInMinutes}"/>
                                        <input type="hidden" id="startTimeInMinutesWindowTwo" name="controlWindow.controlWindowTwo.availableStartTimeInMinutes" value="${loadProgram.controlWindow.controlWindowTwo.availableStartTimeInMinutes}"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".stopTime" data-toggle-group="controlWindowTwo" rowClass="${controlWindowTwoClass}">
                                        <dt:timeOffset id="stopTimeWindowTwo" name="stopTimeWindowTwo" value="${loadProgram.controlWindow.controlWindowTwo.availableStopTimeInMinutes}"/>
                                        <input type="hidden" id="stopTimeInMinutesWindowTwo" name="controlWindow.controlWindowTwo.availableStopTimeInMinutes" value="${loadProgram.controlWindow.controlWindowTwo.availableStopTimeInMinutes}"/>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </tags:sectionContainer2>
                        </c:if>
                    </div>
                </div>
            </c:if>
        </div>
        <c:if test="${not empty selectedSwitchType}">
            <cti:tabs containerName="program_Tab1" classes="noswitchtype">
                <cti:msg2 var="loadGroupsTab" key=".loadGroupsTab" />
                <cti:tab title="${loadGroupsTab}">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <div class="column-12-12 clearfix select-box bordered-div style="margin-top: 30px;">
                            <!-- Available Groups -->
                            <div class="column one bordered-div">
                                <h3>
                                    <i:inline key="yukon.common.available" />
                                </h3>
                                <div id="js-inline-group-picker-container" style="height: 300px;" class="oa"></div>
                                <tags:pickerDialog id="js-avaliable-groups-picker" 
                                                   type="loadGroupPicker" 
                                                   container="js-inline-group-picker-container"
                                                   extraArgs="${selectedSwitchType}" 
                                                   multiSelectMode="${true}" 
                                                   disabledIds="${selectedGroupIds}" />
                                <div class="action-area">
                                    <cti:button nameKey="add" classes="fr js-add-group" icon="icon-add" />
                                </div>
                            </div>

                            <!-- Assigned Groups -->
                            <div class="column two nogutter bordered-div">
                                <h3>
                                    <i:inline key="yukon.common.assigned" />
                                </h3>
                                <div style="height: 346px;" class="oa">
                                    <div id="js-assigned-groups" class="select-box-selected js-with-movables" style="min-height: 150px;" data-item-selector=".select-box-item">
                                        <c:forEach var="item" items="${loadProgram.assignedGroups}" varStatus="status">
                                            <div class="select-box-item cm js-assigned-groups" data-id="${item.groupId}">
                                                <cti:deviceName deviceId="${item.groupId}" />

                                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-group-remove" />
                                                <div class="select-box-item-movers">
                                                    <c:set var="disabled" value="${status.first}" />
                                                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up"
                                                        disabled="${disabled}" />
                                                    <c:set var="disabled" value="${status.last}" />
                                                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down"
                                                        disabled="${disabled}" />
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="select-box-item cm js-assigned-groups js-template-group-row dn" data-id="0">
                                        <span class="js-group-name"></span>
                                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-group-remove" />
                                        <div class="select-box-item-movers">
                                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" />
                                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${true}" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW">
                        <div class="scroll-lg">
                            <table class="compact-results-table dashed">
                                <thead>
                                    <tr>
                                        <th><i:inline key="yukon.common.name" /></th>
                                        <th><i:inline key="yukon.common.type" /></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="group" items="${loadProgram.assignedGroups}">
                                        <tr>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${group.type == 'MACRO_GROUP'}">
                                                        <cti:url var="viewUrl" value="/dr/setup/macroLoadGroup/${group.groupId}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <cti:url var="viewUrl" value="/dr/setup/loadGroup/${group.groupId}"/>
                                                    </c:otherwise>
                                                </c:choose> 
                                                <a href="${viewUrl}"><cti:deviceName deviceId="${group.groupId}"/></a>
                                            </td>
                                            <td><i:inline key="${group.type}" /></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </cti:displayForPageEditModes>
                </cti:tab>
                <cti:checkRolesAndProperties value="ALLOW_MEMBER_PROGRAMS">
                    <cti:displayForPageEditModes modes="VIEW,EDIT">
                        <cti:msg2 var="memberControlTab" key=".memberControlTab" />
                        <cti:tab title="${memberControlTab}">
                            <cti:displayForPageEditModes modes="EDIT">
                                <div class="column-12-12 clearfix select-box bordered-div">
                                    <!-- Available members -->
                                    <div class="column one bordered-div">
                                        <h3>
                                            <i:inline key="yukon.common.available" />
                                        </h3>
                                        <div id="js-inline-member-picker-container" style="height: 300px;" class="oa"></div>
                                        <tags:pickerDialog id="js-avaliable-members-picker" 
                                                           type="memberControlPicker" 
                                                           container="js-inline-member-picker-container"
                                                           multiSelectMode="${true}" 
                                                           extraArgs="${loadProgram.programId}"
                                                           disabledIds="${selectedMemberIds}" />
                                        <div class="action-area">
                                            <cti:button nameKey="add" classes="fr js-add-member" icon="icon-add" />
                                        </div>
                                    </div>

                                    <!-- Assigned members -->
                                    <div class="column two nogutter bordered-div">
                                        <h3>
                                            <i:inline key="yukon.common.assigned" />
                                        </h3>
                                        <div style="height: 346px;" class="oa">
                                            <div id="js-assigned-members" class="select-box-selected js-with-movables" style="min-height: 150px;"
                                                data-item-selector=".select-box-item">
                                                <c:forEach var="item" items="${loadProgram.memberControl}" varStatus="status">
                                                    <div class="select-box-item cm js-assigned-members" data-id="${item.subordinateProgId}">
                                                        <cti:deviceName deviceId="${item.subordinateProgId}" />
                                                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-member-remove" />
                                                        <div class="select-box-item-movers">
                                                            <c:set var="disabled" value="${status.first}" />
                                                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up"
                                                                disabled="${disabled}" />
                                                            <c:set var="disabled" value="${status.last}" />
                                                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down"
                                                                disabled="${disabled}" />
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            <div class="select-box-item cm js-assigned-members js-template-member-row dn" data-id="0">
                                                <span class="js-member-name"></span>
                                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-member-remove" />
                                                <div class="select-box-item-movers">
                                                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" />
                                                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${true}" />
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW">
                                <div class="scroll-lg">
                                    <table class="compact-results-table dashed">
                                        <thead>
                                            <tr>
                                                <th><i:inline key="yukon.common.name" /></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="memberControl" items="${loadProgram.memberControl}">
                                                <tr>
                                                    <td>
                                                        <cti:url var="viewUrl" value="/dr/setup/loadProgram/${memberControl.subordinateProgId}"/>
                                                        <a href="${viewUrl}"><cti:deviceName deviceId="${memberControl.subordinateProgId}"/></a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </cti:displayForPageEditModes>

                        </cti:tab>
                    </cti:displayForPageEditModes>
                </cti:checkRolesAndProperties>
                <cti:msg2 var="notificationTab" key=".notificationTab" />
                <cti:tab title="${notificationTab}">

                    <div class="column-12-12 clearfix select-box bordered-div">
                        <cti:msg2 var="minutes" key="yukon.common.units.MINUTES" />
                        <table class="compact-results-table no-stripes">
                            <tr>
                                <td>
                                    <input type="checkbox" id="js-program-start-check" <c:if test="${not empty loadProgram.notification.programStartInMinutes}">checked="checked" </c:if>
                                    <c:if test="${mode == 'VIEW'}"> disabled="disabled"</c:if> />
                                    <i:inline key=".programStart"/>&nbsp;
                                    <span id="js-program-start-span">
                                        <tags:numeric id="js-program-start" path="notification.programStartInMinutes" size="5" maxlength="5" units="${minutes}" minValue="0" maxValue="99999" />
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <input type="checkbox" id="js-program-stop-check" <c:if test="${not empty loadProgram.notification.programStopInMinutes}">checked="checked"</c:if>
                                        <c:if test="${mode == 'VIEW'}"> disabled="disabled"</c:if> />
                                    <i:inline key=".programStop"/>&nbsp;
                                    <span id="js-program-stop-span">
                                        <tags:numeric id="js-program-stop" path="notification.programStopInMinutes" size="5" maxlength="5" units="${minutes}" minValue="0" maxValue="99999" />
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <tags:checkbox path="notification.notifyOnAdjust" id="js-notify-on-adjust"/>
                                    <i:inline key=".notifyOnAdjustment" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <tags:checkbox path="notification.enableOnSchedule" id="js-notify-on-schedule"/>
                                    <i:inline key=".notifyOnSchedule" />
                                </td>
                            </tr>
                        </table>

                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <!-- Available notification Groups -->
                            <div class="column one bordered-div">
                                <h3>
                                    <i:inline key="yukon.common.available" />
                                </h3>
                                <div id="js-inline-notification-group-picker-container" style="height: 300px;" class="oa"></div>
                                <tags:pickerDialog id="js-avaliable-notification-groups-picker" type="notificationGroupPicker"
                                    container="js-inline-notification-group-picker-container" multiSelectMode="${true}" disabledIds="${selectedNotificationGroupIds}" />
                                <div class="action-area">
                                    <cti:button nameKey="add" classes="fr js-add-notification-group" icon="icon-add" />
                                </div>
                            </div>

                            <!-- Assigned notification Groups -->
                            <div class="column two nogutter bordered-div">
                                <h3>
                                    <i:inline key="yukon.common.assigned" />
                                </h3>
                                <div style="height: 346px;" class="oa">
                                    <div id="js-assigned-notification-groups" class="select-box-selected js-with-movables" style="min-height: 150px;"
                                        data-item-selector=".select-box-item">
                                        <c:forEach var="item" items="${loadProgram.notification.assignedNotificationGroups}" varStatus="status">
                                            <div class="select-box-item cm js-assigned-notification-groups" data-id="${item.notificationGrpID}">

                                                <tags:hidden path="notification.assignedNotificationGroups[${status.index}].notificationGrpName" />
                                                ${fn:escapeXml(item.notificationGrpName)}

                                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-notification-group-remove" />
                                                <div class="select-box-item-movers">
                                                    <c:set var="disabled" value="${status.first}" />
                                                    <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up"
                                                        disabled="${disabled}" />
                                                    <c:set var="disabled" value="${status.last}" />
                                                    <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down"
                                                        disabled="${disabled}" />
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <div class="select-box-item cm js-assigned-notification-groups js-template-notification-row dn" data-id="0">
                                        <span class="js-notification-group-name"></span>
                                        <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-notification-group-remove" />
                                        <div class="select-box-item-movers">
                                            <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="left select-box-item-up js-move-up" />
                                            <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="right select-box-item-down js-move-down" disabled="${true}" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <div class="scroll-lg">
                                <table class="compact-results-table dashed">
                                    <thead>
                                        <tr>
                                            <th><i:inline key="yukon.common.name" /></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="assignedNotificationGroup" items="${loadProgram.notification.assignedNotificationGroups}">
                                            <tr>
                                                <td>${fn:escapeXml(assignedNotificationGroup.notificationGrpName)}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </cti:displayForPageEditModes>
                    </div>
                </cti:tab>
            </cti:tabs>
        </c:if>

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" id="js-program-save" />
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT">
                <cti:url var="viewUrl" value="/dr/setup/loadProgram/${loadProgram.programId}" />
                <cti:button nameKey="cancel" href="${viewUrl}" />
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="CREATE">
                <cti:button id="js-program-cancel-btn" nameKey="cancel" />
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    <div id="gear-create-popup-${selectedSwitchType}" 
         data-title="${programGearCreation}" 
         data-url="${createGearUrl}" 
         data-width="900" 
         data-height="525"
         data-event="yukon:dr:setup:program:saveGear"
         data-ok-text="<cti:msg2 key="yukon.common.save"/>" 
         data-dialog>
    </div>
    <cti:displayForPageEditModes modes="EDIT,CREATE">
    <div id="gear-popups" class="dn">
        <c:forEach var="item" items="${gearInfos}" varStatus="status">
            <cti:url var="viewUrl" value="/dr/setup/loadProgram/gear/${item.id}?mode=${mode}"/> 
            <div data-dialog
                 id="gear-quick-view-${item.id}"
                 data-title="${item.name}"
                 data-width="900"
                 data-height="525"
                 data-event="yukon:dr:setup:program:saveGear"
                 data-url="${viewUrl}"
                 data-load-event="yukon:dr:setup:program:gearRendered"
                 data-ok-text="<cti:msg2 key="yukon.common.save"/>">
            </div>
        </c:forEach>
    </div>
    </cti:displayForPageEditModes>
    <div data-dialog
         data-title="<cti:msg2 key="yukon.common.edit"/>"
         data-width="900"
         data-height="525"
         data-event="yukon:dr:setup:program:saveGear"
         data-load-event="yukon:dr:setup:program:gearRendered"
         data-ok-text="<cti:msg2 key="yukon.common.save"/>"
         class="dn js-gear-dialog-template">
    </div>
    <dt:pickerIncludes />
    <cti:includeScript link="YUKON_TIME_FORMATTER" />
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.program.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.programGear.js" />
    <cti:includeScript link="JQUERY_SCROLL_TABLE_BODY" />
</cti:msgScope>