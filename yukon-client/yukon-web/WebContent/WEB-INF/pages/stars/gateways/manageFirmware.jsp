<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.manageFirmware">
    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
        <div id="firmware-server-popup"
             data-dialog
             data-big-content
             data-width="750"
             data-height="450"
             data-url="<cti:url value="/stars/gateways/update-servers"/>"
             data-title="<cti:msg2 key=".updateServer.set"/>"
             data-event="yukon:assets:gateway:update-server:save"
             data-ok-text="<cti:msg2 key="components.button.save.label"/>">
        </div>

        <div id="send-firmware-upgrade-popup"
             data-dialog
             data-big-content
             data-width="750"
             data-height="450"
             data-help-text="<cti:msg key="yukon.web.modules.operator.gateways.firmwareUpdate.note"/>"
             data-url="<cti:url value="/stars/gateways/firmware-upgrade"/>"
             data-title="<cti:msg2 key=".firmwareUpdate"/>"
             data-event="yukon:assets:gateway:firmware-upgrade:send"
             data-ok-class="js-send-btn"
             data-ok-disabled
             data-confirm-multiple-text="<cti:msg2 key=".firmwareUpdate.confirmMultiple"/>"
             data-ok-text="<cti:msg2 key="components.button.send.label"/>">
        </div>
    </cti:checkRolesAndProperties>

    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
            <c:set var="clazz" value="${!dataExists ? 'dn' : ''}"/>
            <cm:dropdownOption data-popup="#firmware-server-popup ${clazz}" icon="icon-drive-go" disabled="${!dataExists}">
                <i:inline key=".updateServer.set"/>
            </cm:dropdownOption>
            <cm:dropdownOption data-popup="#send-firmware-upgrade-popup ${clazz}" icon="icon-drive-go" disabled="${!dataExists}">
                <i:inline key=".firmwareUpdate"/>
            </cm:dropdownOption>
        </cti:checkRolesAndProperties>
    </div>
    
    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
        <div class="column-12-12 clearfix">
            <div class="column one">
                <cti:msg2 var="helpTextWidget" key="yukon.web.widgets.firmwareInformationWidget.helpText"/>
                <tags:widget bean="firmwareInformationWidget" helpText="${helpTextWidget}"/>
            </div>
        </div>
        <br>
        <div>
            <tags:widget bean="firmwareDetailsWidget"/>
        </div>
    </cti:checkRolesAndProperties>

    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.manageFirmware.js"/>
</cti:standardPage>