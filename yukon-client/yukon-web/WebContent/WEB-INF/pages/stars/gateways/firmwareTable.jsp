<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.manageFirmware, yukon.web.modules.operator.gateways.list">
    <table id="firmware-table" class="compact-results-table">
        <thead>
            <tr>
                <tags:sort column="${TIMESTAMP}"/>
                <th><i:inline key=".firmwareUpdates.totalGateways"/></th>
                <th><i:inline key=".firmwareUpdates.totalServers"/></th>
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
                                <div class="progress dif vat">
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
    <c:if test="${empty firmwareUpdates}">
        <span class="empty-list compact-results-table"><i:inline key=".firmwareUpdates.none"/></span>
    </c:if>
</cti:msgScope>