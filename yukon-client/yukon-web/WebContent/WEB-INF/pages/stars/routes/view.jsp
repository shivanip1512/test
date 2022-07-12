<%@ taglib prefix="assets" tagdir="/WEB-INF/tags/assets"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.operator.routes">
    <cti:standardPage module="operator" page="routes.${mode}">
    <tags:setFormEditMode mode="${mode}" />
    
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <cti:url var="action" value="/stars/device/routes/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" data-popup="#js-create-route-popup"/>
        </div>
        
        <!-- popup for create route -->
        <div id="js-create-route-popup" class="dn" data-title="<i:inline key=".createObject.title"/>" data-width="400">
            <table style="width:100%">
                <tr>
                    <td>
                        <cti:url var="createCommRoute" value="create"/>
                        <a href="${createCommRoute}"><i:inline key=".routes.createCommRoute.create"/></a>
                    </td>
                    <td>
                        <cti:url var="createMacroRoute" value="create"/>
                        <a href="${createMacroRoute}"><i:inline key=".routes.createMacroRoute.create"/></a>
                    </td>
                </tr>
            </table>
        </div>
    </cti:displayForPageEditModes>

    <!-- page contents -->
    <cti:url var="action" value="/stars/device/routes/save" />
    <form:form modelAttribute="communicationRoute" action="${action}" method="post" id="js-comm-route-form">
        <cti:csrfToken />
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                        <tags:input path="deviceName" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".selectSignalTransmitter">
                    <tags:pickerDialog id="signalTransmitterPicker${id}" 
                                           type="signalTransmitterPicker" 
                                           destinationFieldName="signalTransmitterId"
                                           linkType="selection" 
                                           selectionProperty="paoName"
                                           icon="icon-magnifier"
                                           multiSelectMode="false"
                                           allowEmptySelection="false" 
                                           viewOnlyMode="${mode == 'VIEW'}"
                                           initialId="${communicationRoute.signalTransmitterId}"/>
                    <tags:hidden path="signalTransmitterId"/>
                 </tags:nameValue2>
                 <tags:nameValue2 nameKey=".defaultRoute">
                        <tags:switchButton path="defaultRoute" onNameKey=".yes.label" offNameKey=".no.label"/>
                 </tags:nameValue2>
            </tags:nameValueContainer2>
                <div class="page-action-area">
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <cti:button type="submit" nameKey="save" classes="primary action js-save" busy="true"/>
                        <cti:url var="viewUrl" value="/stars/device/routes/list" />
                        <cti:button nameKey="cancel" href="${viewUrl}"/>
                    </cti:displayForPageEditModes>
                </div>
        </tags:sectionContainer2>
    </form:form>
    </cti:standardPage>
</cti:msgScope>
