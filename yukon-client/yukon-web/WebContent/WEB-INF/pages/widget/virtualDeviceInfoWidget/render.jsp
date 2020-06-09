<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.virtualDeviceInfoWidget">
    <tags:setFormEditMode mode="${mode}" />
    <form:form modelAttribute="virtualDevice" id="virtual-device-form">
        <cti:csrfToken />
        <tags:hidden id="disableFlag" path="disableFlag"/>
        <tags:hidden path="paoType"/>
        <tags:hidden id="deviceId" path="liteID"/>
        
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input path="paoName" maxlength="60" inputClass="w300 wrbw dib"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".status">
                <tags:switchButton id="disableToggle" name="disableToggle" inverse="${true}" checked="${virtualDevice.disableFlag == 'Y'}"
                    offNameKey=".disabled.label" onNameKey=".enabled.label" />
            </tags:nameValue2>
        </tags:nameValueContainer2>

    </form:form>
    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="UPDATE">
            <div class="action-area">
                <cti:button icon="icon-pencil" nameKey="edit" data-popup="#js-edit-virtual-device-popup"/>
            </div>
            <cti:url var="editUrl" value="/widget/virtualDeviceInfoWidget/${virtualDevice.liteID}/edit"/>
            <cti:msg2 var="saveText" key="components.button.save.label"/>
            <cti:msg2 var="editPopupTitle" key="yukon.web.modules.operator.virtualDeviceInfoWidget.edit" argument="${virtualDevice.paoName}"/>
            <div class="dn" 
                 id="js-edit-virtual-device-popup" 
                 data-title="${editPopupTitle}" 
                 data-dialog
                 data-ok-text="${saveText}"
                 data-event="yukon:virtualDevice:save" 
                 data-url="${editUrl}"/>
            </div>
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
    <cti:includeScript link="/resources/js/pages/yukon.assets.virtualDevice.js"/>
</cti:msgScope>
