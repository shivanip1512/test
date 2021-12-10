<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.list">

    <tags:alertBox type="warning" classes="dn js-download-warning" includeCloseButton="true">
        <i:inline key=".downloadNetworkWarning"/>
    </tags:alertBox>

    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
        <div id="gateway-create-popup" class="dn"
            data-dialog
            data-title="<cti:msg2 key=".create.gateway.label"/>" 
            data-url="<cti:url value="/stars/gateways/create"/>" 
            data-width="680" 
            data-min-width="680" 
            data-height="600"
            data-event="yukon:assets:gateway:save" 
            data-ok-text="<cti:msg2 key="components.button.save.label"/>" 
            data-load-event="yukon:assets:gateway:load">
        </div>
    </cti:checkRolesAndProperties>

    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
            <cm:dropdownOption data-popup="#gateway-create-popup" icon="icon-plus-green">
                <i:inline key="yukon.common.create"/>
            </cm:dropdownOption>
            <li class="divider"></li>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="VIEW">
            <cti:url var="comprehensiveMapUrl" value="/stars/comprehensiveMap/home"/>
            <cm:dropdownOption key="yukon.web.modules.operator.comprehensiveMap.pageName" href="${comprehensiveMapUrl}" icon="icon-map-pins"/>
            <cm:dropdownOption key=".downloadNetwork" icon="icon-csv" classes="js-download-network-map"/>
        </cti:checkRolesAndProperties>
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
            <li class="divider"></li>
            <cti:url var="manageCertificate" value="/stars/gateways/manageCertificates"/>
            <cm:dropdownOption icon="icon-drive-go" key=".manageCertificates" href="${manageCertificate}"/>
            <cti:url var="manageFirmware" value="/stars/gateways/manageFirmware"/>
            <cm:dropdownOption icon="icon-drive-go" key=".manageFirmware" href="${manageFirmware}"/>
        </cti:checkRolesAndProperties>
    </div>

    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="VIEW">
        <div>
            <tags:widget bean="gatewayListWidget"/>
        </div>
        <br>
        <div>
            <cti:msg2 var="helpTextWidget" key="yukon.web.widgets.infrastructureWarningsWidget.helpText"/>
            <tags:widget bean="infrastructureWarningsWidget" helpText="${helpTextWidget}" infrastructureWarningDeviceCategory="${infrastructureWarningDeviceCategory}"/>
        </div>
        <cti:includeScript link="/resources/js/widgets/yukon.widget.infrastructureWarnings.js"/>
    </cti:checkRolesAndProperties>
</cti:standardPage>