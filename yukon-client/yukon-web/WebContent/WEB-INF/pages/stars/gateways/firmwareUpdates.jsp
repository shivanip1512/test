<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.manageFirmware">
    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
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
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
            <cm:dropdownOption data-popup="#firmware-server-popup" icon="icon-drive-go" classes="update-servers disabled" disabled="true">
                <i:inline key=".updateServer.set"/>
            </cm:dropdownOption>
            <cm:dropdownOption data-popup="#send-firmware-upgrade-popup" icon="icon-drive-go" classes="update-servers disabled" disabled="true">
                <i:inline key=".firmwareUpdate"/>
            </cm:dropdownOption>
        </cti:checkRolesAndProperties>
    </div>

    <div id="gateway-firmware-details-popup" class="dn"></div>

    <c:if test="${empty firmwareUpdates}">
        <div class="js-no-firmware-updates empty-list">
            <i:inline key=".firmwareUpdates.none"/>
        </div>
    </c:if>
    <div data-url="<cti:url value="/stars/gateways/firmwareDetails"/>" data-static>
        <c:set var="clazz" value="${empty firmwareUpdates ? 'dn' : ''}"/>
        <table id="firmware-table" class="compact-results-table ${clazz}">
            <thead>
                <tr>
                    <tags:sort column="${TIMESTAMP}"/>
                    <th><i:inline key="yukon.web.modules.operator.gateways.list.firmwareUpdates.totalGateways"/></th>
                    <th><i:inline key="yukon.web.modules.operator.gateways.list.firmwareUpdates.totalServers"/></th>
                    <th><i:inline key="yukon.common.status"/></th>
                    <tags:sort column="${PENDING}"/>
                    <tags:sort column="${FAILED}"/>
                    <tags:sort column="${SUCCESSFUL}"/>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="update" items="${firmwareUpdates}">
                    <tr data-update-id="${update.updateId}">
                        <td class="js-firmware-update-timestamp">
                            <a href="javascript:void(0);">
                                <cti:formatDate value="${update.sendDate}" type="DATEHM_12"/>
                            </a>
                        </td>
                        <td class="js-firmware-gateways">${update.totalGateways}</td>
                        <td class="js-firmware-update-servers">${update.totalUpdateServers}</td>
                        <td class="js-firmware-update-status">
                            <c:choose>
                                <c:when test="${update.gatewayUpdatesPending == 0}">
                                    <span class="success"><i:inline key="yukon.common.complete"/></span>
                                </c:when>
                                <c:otherwise>
                                    <div class="progress dib vat">
                                        <div class="progress-bar progress-bar-success" style="width: ${update.successPercent};"></div>
                                        <div class="progress-bar progress-bar-danger" style="width: ${update.failedPercent};"></div>
                                    </div>
                                    <span class="js-percent">${update.totalPercent}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="js-firmware-update-pending subtle">${update.gatewayUpdatesPending}</td>
                        <td class="js-firmware-update-failed error">${update.gatewayUpdatesFailed}</td>
                        <td class="js-firmware-update-successful success">${update.gatewayUpdatesSuccessful}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <cti:toJson object="${text}" id="gateway-text"/>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.list.js"/>
</cti:standardPage>