<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.manageCertificates">
    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
        <div id="gateway-cert-popup"
             class="dn"
             data-dialog
             data-title="<cti:msg2 key=".cert.update.label"/>"
             data-event="yukon:assets:gateway:cert:update"
             data-help-text="<cti:msg key="yukon.web.modules.operator.gateways.cert.update.note"/>"
             data-url="<cti:url value="/stars/gateways/cert-update/options"/>"
             data-width="450"
             data-height="280"
             data-ok-text="<cti:msg2 key="components.button.start.label"/>">
        </div>
    </cti:checkRolesAndProperties>

    <div id="page-actions" class="dn">
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
            <cm:dropdownOption data-popup="#gateway-cert-popup" icon="icon-drive-go">
                <i:inline key=".cert.update.label"/>
            </cm:dropdownOption>
        </cti:checkRolesAndProperties>
    </div>

    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
        <div>
            <tags:widget bean="certificateDetailsWidget"/>
        </div>
    </cti:checkRolesAndProperties>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.manageCertificate.js"/>
</cti:standardPage>