<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:msgScope paths="yukon.web.modules.operator.routes">
    <cti:standardPage module="operator" page="routes.${mode}">
        <tags:setFormEditMode mode="${mode}"/>
        
        <!-- Actions drop-down -->
        <cti:displayForPageEditModes modes="VIEW">
            <div id="page-actions" class="dn">
                <cti:url var="createUrl" value="/stars/device/routes/create"/>
                <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" href="${createUrl}"/>
                <cti:url var="editUrl" value="/stars/device/routes/${communicationRoute.deviceId}/edit" />
                <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}"/>
                <!-- delete button and confirm popup -->
                <cm:dropdownOption icon="icon-delete" key="yukon.web.components.button.delete.label" id="js-delete-option"
                                   data-ok-event="yukon:routes:delete" classes="js-hide-dropdown"/>
                    <d:confirm on="#js-delete-option" nameKey="confirmDelete" argument="${communicationRoute.deviceName}" />
                <cti:url var="deleteUrl" value="/stars/device/routes/${communicationRoute.deviceId}/delete" />
                <form:form id="js-route-delete-form" action="${deleteUrl}" method="delete" modelAttribute="communicationRoute">
                    <cti:csrfToken />
                </form:form>
            </div>
        </cti:displayForPageEditModes>
    
        <!-- page contents -->
        <cti:url var="action" value="/stars/device/routes/save" />
        <form:form modelAttribute="communicationRoute" action="${action}" method="post" id="js-comm-route-form">
            <cti:csrfToken />
            <form:hidden path="deviceId" />
            <tags:sectionContainer2 nameKey="general">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey="yukon.common.name">
                        <tags:input path="deviceName" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".selectSignalTransmitter" valueClass="dib">
                        <tags:pickerDialog id="signalTransmitterPicker${id}" 
                                               type="signalTransmitterPicker" 
                                               destinationFieldName="signalTransmitterId"
                                               linkType="selection" 
                                               selectionProperty="paoName"
                                               icon="icon-magnifier"
                                               multiSelectMode="false"
                                               allowEmptySelection="false" 
                                               viewOnlyMode="${mode == 'VIEW'}"
                                               initialId="${communicationRoute.signalTransmitterId}"
                                               extraArgs="${communicationRoute.deviceType}"/>
                         <tags:hidden path="signalTransmitterId"/>
                     </tags:nameValue2>
                     <tags:nameValue2 nameKey=".defaultRoute">
                         <tags:switchButton path="defaultRoute" onNameKey=".yes.label" offNameKey=".no.label"/>
                     </tags:nameValue2>
                </tags:nameValueContainer2>
                
                <!-- page action buttons -->
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <cti:button type="submit" nameKey="save" classes="primary action" busy="true"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="CREATE">
                        <cti:url var="viewUrl" value="/stars/device/routes/list" />
                        <cti:button nameKey="cancel" href="${viewUrl}"/>
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="EDIT">
                        <cti:url var="viewUrl" value="/stars/device/routes/${communicationRoute.deviceId}" />
                        <cti:button nameKey="cancel" href="${viewUrl}"/>
                    </cti:displayForPageEditModes>
                </div>
            </tags:sectionContainer2>
        </form:form>
        <cti:includeScript link="/resources/js/pages/yukon.assets.routes.js"/>
    </cti:standardPage>
</cti:msgScope>
