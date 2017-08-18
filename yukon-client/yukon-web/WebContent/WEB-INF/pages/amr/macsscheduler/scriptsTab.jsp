<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:msgScope paths="modules.tools.schedule">
    <c:set var="pathKey" value="${templateReceived ? 'schedule.scriptOptions.' : 'scriptOptions.'}" />

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".scriptOptions.fileName">
            <tags:input path="${pathKey}fileName" maxlength="180" size="30" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".scriptOptions.description">
            <tags:input path="${pathKey}description" maxlength="180" size="30" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <br />
    <c:set var="clazz" value="${schedule.template.isNoTemplateSelected() ? 'dn' : ''}" />
    <div id="script-tabs" class="${clazz}">
        <cti:tabs>
            <cti:msg2 var="meterReadTab" key=".scriptOptions.meterReadTab" />
            <cti:tab title="${meterReadTab}">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".scriptOptions.deviceGroup">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:list var="groups">
                                <cti:item value="${schedule.scriptOptions.groupName}"/>
                            </cti:list>
                            <tags:deviceGroupPicker inputName="scriptOptions.groupName" inputValue="${groups}"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            ${schedule.scriptOptions.groupName}
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.porterTimeout">
                        <tags:input path="${pathKey}porterTimeout" size="5" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.filePath">
                        <tags:input path="${pathKey}filePath" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.missedFileName">
                        <tags:input path="${pathKey}missedFileName" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.successFileName">
                        <tags:input path="${pathKey}successFileName" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <c:set var="clazz" value="${schedule.template.isRetry() ? 'dn' : ''}" />
                <tags:sectionContainer2 nameKey="scriptOptions.retrySection"
                    styleClass="${clazz} js-retry-section">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".scriptOptions.retryCount">
                            <tags:input path="${pathKey}retryCount" size="5" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scriptOptions.queueOffCount">
                            <tags:input path="${pathKey}queueOffCount" size="5" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scriptOptions.maxRetryHours">
                            <tags:input path="${pathKey}maxRetryHours" size="5" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
                <c:set var="clazz" value="${schedule.template.isIed() ? '' : 'dn'}" />
                <tags:sectionContainer2 nameKey="scriptOptions.iedSetupSection"
                    styleClass="${clazz} js-ied-section">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".scriptOptions.touRate">
                            <tags:selectWithItems path="${pathKey}touRate" items="${touRates}" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scriptOptions.resetDemand">
                            <tags:switchButton path="${pathKey}demandResetSelected"
                                classes="js-demand-reset" toggleGroup="demandReset"
                                toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scriptOptions.resetRetryCount"
                            data-toggle-group="demandReset">
                            <tags:input path="${pathKey}demandResetRetryCount" size="5" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".scriptOptions.iedType"
                            data-toggle-group="demandReset">
                            <tags:selectWithItems path="${pathKey}iedType" items="${iedTypes}" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </tags:sectionContainer2>
            </cti:tab>
            <cti:msg2 var="optionsTab" key=".scriptOptions.optionsTab" />
            <cti:tab title="${optionsTab}">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".scriptOptions.sendEmailNotification">
                        <tags:switchButton path="${pathKey}notificationSelected"
                            toggleGroup="sendEmail" toggleAction="hide" onNameKey=".yes.label"
                            offNameKey=".no.label" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.notifyGroup"
                        data-toggle-group="sendEmail">
                        <tags:pickerDialog type="notificationGroupPicker" id="notifyUserGroupPicker"
                            selectionProperty="name" destinationFieldId="notifyUserGroupId" viewOnlyMode="${mode == 'VIEW'}"
                            linkType="selection" immediateSelectMode="true"/>
                        <tags:hidden path="${pathKey}notificationGroupId" id="notifyUserGroupId" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.messageSubject"
                        data-toggle-group="sendEmail">
                        <tags:input path="${pathKey}notificationSubject" size="30" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.generateBillingFile">
                        <tags:switchButton path="${pathKey}billingSelected"
                            toggleGroup="billingFile" toggleAction="hide" onNameKey=".yes.label"
                            offNameKey=".no.label" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.deviceGroup"
                        data-toggle-group="billingFile">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:list var="groups">
                                <cti:item value="${schedule.scriptOptions.billingGroupName}"/>
                            </cti:list>
                            <tags:deviceGroupPicker inputName="scriptOptions.billingGroupName" inputValue="${groups}" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            ${schedule.scriptOptions.billingGroupName}
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.fileFormat"
                        data-toggle-group="billingFile">
                        <tags:selectWithItems path="${pathKey}billingFormat" items="${fileFormats}" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.demandDays"
                        data-toggle-group="billingFile">
                        <tags:input path="${pathKey}billingDemandDays" size="5" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.energyDays"
                        data-toggle-group="billingFile">
                        <tags:input path="${pathKey}billingEnergyDays" size="5" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.fileName"
                        data-toggle-group="billingFile">
                        <tags:input path="${pathKey}billingFileName" size="30" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".scriptOptions.filePath"
                        data-toggle-group="billingFile">
                        <tags:input path="${pathKey}billingFilePath" size="30" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </cti:tab>
        </cti:tabs>
    </div>
    <c:set var="clazz" value="${schedule.template.isNoTemplateSelected() ? '' : 'dn'}" />
    <cti:displayForPageEditModes modes="VIEW">
        <c:set var="disableArea" value="readonly" />
    </cti:displayForPageEditModes>
    <textarea id="savedScriptText" name="scriptOptions.scriptText" cols="100" rows="20" class="${clazz}" ${disableArea}>${schedule.scriptOptions.scriptText}</textarea>
    
    <div id="text-editor" class="dn"
        data-title="<cti:msg2 key=".scriptOptions.textEditorTab"/>">
        <%@ include file="scriptEditorDialog.jsp" %>
    </div>

</cti:msgScope>







