<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="schedule">

    <tags:setFormEditMode mode="${mode}" />

    <cti:url var="action" value="/macsscheduler/schedules/save" />
    <form:form id="macs-schedule" commandName="schedule" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />

        <cti:tabs>
            <cti:msg2 var="generalTab" key=".generalTab" />
            <cti:tab title="${generalTab}">

                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".name">
                        <tags:input path="scheduleName" maxlength="60" size="30" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".categoryName">
                        <tags:selectWithItems path="categoryName" items="${categories}" />
                    </tags:nameValue2>
                    <cti:displayForPageEditModes modes="EDIT">
                        <c:set var="disableType" value="true"/>
                    </cti:displayForPageEditModes>
                    <tags:nameValue2 nameKey=".type">
                        <tags:selectWithItems path="type" items="${types}" inputClass="js-type" disabled="${disableType}"/>
                    </tags:nameValue2>
                    <c:set var="clazz" value="${schedule.isSimple() ? 'dn' : ''}"/>
                    <tags:nameValue2 nameKey=".template" rowClass="js-template ${clazz}">
                        <tags:selectWithItems path="template" items="${templates}" inputClass="js-template" disabled="${disableType}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <tags:sectionContainer2 nameKey="startPolicySection">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".type">
                            <tags:selectWithItems path="startPolicy.policy"
                                items="${startPolicyOptions}" />
                        </tags:nameValue2>
<%--                         <tags:nameValue2 nameKey=".startPolicy.startTime">
                            <tags:input path="startPolicy.manualStartTime" />
                        </tags:nameValue2> --%>
                        <tags:nameValue2 nameKey=".startPolicy.startDateTime">
                            <dt:dateTime path="startPolicy.startDateTime" />
                            <tags:checkbox path="startPolicy.everyYear" />
                            <i:inline key=".startPolicy.everyYear" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.daysOfWeek">
                            <div class="button-group stacked">
                                <c:forEach var="dayOfWeek" items="${daysOfWeek}">
                                    <tags:check id="${dayOfWeek}_chk"
                                        path="startPolicy.days[${dayOfWeek}]" key="${dayOfWeek}"
                                        classes="M0" />
                                </c:forEach>
                            </div>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".startPolicy.dayOfMonth">
                            <tags:input path="startPolicy.dayOfMonth" size="5"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".holiday">
                            <tags:selectWithItems path="startPolicy.holidayScheduleId"
                                items="${holidaySchedules}" itemValue="holidayScheduleId"
                                itemLabel="holidayScheduleName" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>

                <tags:sectionContainer2 nameKey="stopPolicySection">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".type">
                            <tags:selectWithItems path="stopPolicy.policy"
                                items="${stopPolicyOptions}" />
                        </tags:nameValue2>
<%--                         <tags:nameValue2 nameKey=".stopPolicy.stopTime">
                            <tags:input path="stopPolicy.manualStopTime" />
                        </tags:nameValue2> --%>
                        <tags:nameValue2 nameKey=".stopPolicy.duration">
                            <tags:input path="stopPolicy.duration" size="5"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>

            </cti:tab>

            <c:set var="clazz" value="${schedule.isScript() ? 'dn' : ''}"/>
            <cti:msg2 var="commandsTab" key=".commandsTab" />
            <cti:tab title="${commandsTab}" headerClasses="js-commands-tab ${clazz}">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".simpleOptions.start">
                        <tags:input path="simpleOptions.startCommand" size="50" maxlength="120"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".simpleOptions.stop">
                        <tags:input path="simpleOptions.stopCommand" size="50" maxlength="120"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".simpleOptions.repeatInterval">
                        <tags:input path="simpleOptions.repeatInterval" size="5"/>
                    </tags:nameValue2>
                    <c:set var="paoId" value="${schedule.simpleOptions.targetPAObjectId}" />
                    <tags:hidden id="pao-id" path="simpleOptions.targetPAObjectId" />
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <tags:nameValue2 nameKey=".simpleOptions.target" rowId="target-row">
                            <div class="button-group button-group-toggle">
                                <c:set var="clazz" value="${target == 'DEVICE' ? 'on' : ''}"/>
                                <cti:button icon="icon-database-add" nameKey="simpleOptions.device"
                                    data-show="#device-row" classes="${clazz} M0" />
                                <c:set var="clazz" value="${target == 'LOADGROUP' ? 'on' : ''}"/>
                                <cti:button icon="icon-database-add" nameKey="simpleOptions.loadGroup"
                                    data-show="#load-group-row" classes="${clazz}"/>
                            </div>
                        </tags:nameValue2>
                    </cti:displayForPageEditModes>
                    <c:set var="clazz" value="${target != 'DEVICE' ? 'dn' : ''}"/>
                    <tags:nameValue2 nameKey=".simpleOptions.device.label" rowId="device-row" rowClass="${clazz}">
                        <tags:pickerDialog type="commanderDevicePicker" id="commanderDevicePicker"
                            linkType="selection" selectionProperty="paoName"
                            destinationFieldId="pao-id" immediateSelectMode="true"
                            viewOnlyMode="${mode == 'VIEW'}" />
                    </tags:nameValue2>
                    <c:set var="clazz" value="${target != 'LOADGROUP' ? 'dn' : ''}"/>
                    <tags:nameValue2 nameKey=".simpleOptions.loadGroup.label" rowId="load-group-row" rowClass="${clazz}">
                        <tags:pickerDialog type="lmGroupPaoPermissionCheckingPicker"
                            id="lmGroupPicker" linkType="selection" selectionProperty="paoName"
                            destinationFieldId="pao-id" immediateSelectMode="true"
                            initialId="${paoId}" viewOnlyMode="${mode == 'VIEW'}" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </cti:tab>

            <c:set var="clazz" value="${schedule.isSimple() ? 'dn' : ''}"/>
            <cti:msg2 var="scriptTab" key=".scriptTab" />
            <cti:tab title="${scriptTab}" headerClasses="js-script-tab ${clazz}">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".scriptOptions.fileName">
                        <tags:input path="scriptOptions.fileName" maxlength="180" size="30" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.description">
                        <tags:input path="scriptOptions.description" maxlength="180" size="30" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <br/>
                <cti:tabs>
                    <cti:msg2 var="meterReadTab" key=".scriptOptions.meterReadTab" />
                    <cti:tab title="${meterReadTab}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".scriptOptions.deviceGroup">
                                <cti:displayForPageEditModes modes="EDIT,CREATE">
                                    <tags:deviceGroupPicker inputName="scriptOptions.groupName"/>
                                </cti:displayForPageEditModes>
                                <cti:displayForPageEditModes modes="VIEW">
                                    ${schedule.scriptOptions.groupName}
                                </cti:displayForPageEditModes>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.porterTimeout">
                                <tags:input path="scriptOptions.porterTimeout" size="5"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.filePath">
                                <tags:input path="scriptOptions.filePath" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.missedFileName">
                                <tags:input path="scriptOptions.missedFileName" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.successFileName">
                                <tags:input path="scriptOptions.successFileName" />
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        <c:set var="clazz" value="${schedule.template.isRetry() ? 'dn' : ''}"/>
                        <tags:sectionContainer2 nameKey="scriptOptions.retrySection" styleClass="${clazz} js-retry-section">
                            <tags:nameValueContainer2>
                                <tags:nameValue2 nameKey=".scriptOptions.retryCount">
                                    <tags:input path="scriptOptions.retryCount" size="5"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".scriptOptions.queueOffCount">
                                    <tags:input path="scriptOptions.queueOffCount" size="5"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".scriptOptions.maxRetryHours">
                                    <tags:input path="scriptOptions.maxRetryHours" size="5"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                        <c:set var="clazz" value="${schedule.template.isIed() ? '' : 'dn'}"/>
                        <tags:sectionContainer2 nameKey="scriptOptions.iedSetupSection" styleClass="${clazz} js-ied-section">
                            <tags:nameValueContainer2>
                                <tags:nameValue2 nameKey=".scriptOptions.touRate">
                                    <tags:selectWithItems path="scriptOptions.touRate"
                                        items="${touRates}" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".scriptOptions.resetDemand">
                                    <tags:switchButton path="scriptOptions.demandResetSelected" classes="js-demand-reset"
                                        toggleGroup="demandReset" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".scriptOptions.resetRetryCount" data-toggle-group="demandReset">
                                    <tags:input path="scriptOptions.demandResetRetryCount" size="5"/>
                                </tags:nameValue2>
                                <c:set var="clazz" value="${schedule.template.isIed300() ? '' : 'dn'}"/>
                                <tags:nameValue2 nameKey=".scriptOptions.readFrozenDemandRegister" rowClass="${clazz} js-ied-300">
                                    <div class="button-group">
                                        <c:forEach var="option" items="${frozenDemandRegisterOptions}">
                                            <tags:radio path="scriptOptions.frozenDemandRegister" value="${option}" label="${option}" classes="yes M0" />
                                        </c:forEach>
                                    </div>
                                </tags:nameValue2>
                                <c:set var="clazz" value="${schedule.template.isIed400() ? '' : 'dn'}"/>
                                <tags:nameValue2 nameKey=".scriptOptions.iedType" rowClass="${clazz} js-ied-400">
                                    <tags:selectWithItems path="scriptOptions.iedType"
                                        items="${iedTypes}" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                    </cti:tab>
                    <cti:msg2 var="optionsTab" key=".scriptOptions.optionsTab" />
                    <cti:tab title="${optionsTab}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".scriptOptions.sendEmailNotification">
                                <tags:switchButton path="scriptOptions.notificationSelected" 
                                    toggleGroup="sendEmail" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.notifyGroup" data-toggle-group="sendEmail">
                                <tags:pickerDialog type="notificationGroupPicker" id="notifyUserGroupPicker" selectionProperty="name"
                                    destinationFieldId="notifyUserGroupId" linkType="selection" immediateSelectMode="true"/>
                                <tags:hidden path="scriptOptions.notificationGroupName" id="notifyUserGroupId"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.messageSubject" data-toggle-group="sendEmail">
                                <tags:input path="scriptOptions.notificationSubject" size="30"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.generateBillingFile">
                                <tags:switchButton path="scriptOptions.billingSelected" 
                                    toggleGroup="billingFile" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.deviceGroup" data-toggle-group="billingFile">
                                <tags:deviceGroupPicker inputName="scriptOptions.billingGroupName"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.fileFormat" data-toggle-group="billingFile">
                                <tags:selectWithItems path="scriptOptions.billingFormat" items="${fileFormats}" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.demandDays" data-toggle-group="billingFile">
                                <tags:input path="scriptOptions.billingDemandDays" size="5"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.energyDays" data-toggle-group="billingFile">
                                <tags:input path="scriptOptions.billingEnergyDays" size="5"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.fileName" data-toggle-group="billingFile">
                                <tags:input path="scriptOptions.billingFileName" size="30"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".scriptOptions.filePath" data-toggle-group="billingFile">
                                <tags:input path="scriptOptions.billingFilePath" size="30"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </cti:tab>
                    <cti:msg2 var="textEditorTab" key=".scriptOptions.textEditorTab" />
                    <cti:tab title="${textEditorTab}" headerClasses="js-script-text">
                        <div class="error" id="script-error"></div>
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:set var="disableArea" value="disabled"/>
                        </cti:displayForPageEditModes>
                        <textarea id="script" cols="100" rows="20" ${disableArea}></textarea>
                    </cti:tab>
                </cti:tabs>
            </cti:tab>
        </cti:tabs>

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="editUrl" value="/macsscheduler/schedules/${id}/edit" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}" />
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" busy="true" />

                <cti:url var="viewUrl" value="/macsscheduler/schedules/${id}/view" />
                <cti:button nameKey="cancel" href="${viewUrl}" />
            </cti:displayForPageEditModes>
        </div>

    </form:form>
    <cti:toJson id="retry-types" object="${retryTypes}"/>
    <cti:toJson id="ied-300-types" object="${ied300Types}"/>
    <cti:toJson id="ied-400-types" object="${ied400Types}"/>
    
    <cti:includeScript link="/resources/js/pages/yukon.ami.macs.js" />
</cti:standardPage>




