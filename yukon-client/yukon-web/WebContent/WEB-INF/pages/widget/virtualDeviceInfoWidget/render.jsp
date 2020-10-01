<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.common,yukon.web.modules.operator.virtualDeviceInfoWidget">
    <tags:setFormEditMode mode="${mode}" />
    <c:if test="${not empty errorMsg}"><tags:alertBox includeCloseButton="true">${fn:escapeXml(errorMsg)}</tags:alertBox></c:if>
    <form:form modelAttribute="virtualDevice" id="virtual-device-form">
        <cti:csrfToken />
        <tags:hidden path="id"/>
        <input type="hidden" id="virtualMeterType" value="${virtualMeterType}"/>
        
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".name">
                <tags:input id="name" path="name" maxlength="60" inputClass="w300 wrbw dib"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".type">
                <cti:displayForPageEditModes modes="CREATE">
                    <tags:selectWithItems path="type" items="${types}" inputClass="js-type"/>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW,EDIT">
                    <i:inline key="${virtualDevice.type.formatKey}"/>
                </cti:displayForPageEditModes>
            </tags:nameValue2>
            <c:set var="displayClass" value="${virtualDevice.type == virtualMeterType ? '' : 'dn'}"/>
            <tags:nameValue2 nameKey=".meterNumber" rowClass="js-meter-number ${displayClass}">
                <input type="text" name="meterNumber"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".status">
                <tags:switchButton path="enable" offNameKey=".disabled.label" onNameKey=".enabled.label" />
            </tags:nameValue2>
        </tags:nameValueContainer2>

    </form:form>
    <cti:displayForPageEditModes modes="VIEW">
        <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="UPDATE">
            <div class="action-area">
                <cti:button icon="icon-pencil" nameKey="edit" data-popup="#js-edit-virtual-device-popup"/>
            </div>
            <cti:url var="editUrl" value="/widget/virtualDeviceInfoWidget/${virtualDevice.id}/edit"/>
            <cti:msg2 var="saveText" key="components.button.save.label"/>
            <cti:msg2 var="editPopupTitle" key=".editWithName" argument="${virtualDevice.name}"/>
            <div class="dn" id="js-edit-virtual-device-popup" data-title="${editPopupTitle}" data-dialog data-ok-text="${saveText}"
                data-event="yukon:virtualDevice:save" data-url="${editUrl}"></div>
        </cti:checkRolesAndProperties>
    </cti:displayForPageEditModes>
    <cti:includeScript link="/resources/js/pages/yukon.assets.virtualDevice.js"/>
</cti:msgScope>
