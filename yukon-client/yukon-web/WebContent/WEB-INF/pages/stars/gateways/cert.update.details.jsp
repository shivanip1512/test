<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways.cert.update, yukon.common">

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".file" >
        ${fn:escapeXml(update.fileName)}
    </tags:nameValue2>
</tags:nameValueContainer2>

<div class="stacked">
<h3><i:inline key=".failed"/>&nbsp;&nbsp;<span class="badge badge-error">${fn:length(update.failed)}</span></h3>
<c:if test="${not empty update.failed}">
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".serialNumber"/></th>
                <th><i:inline key=".ipaddress"/></th>
                <th><i:inline key=".failType"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="gatewayEntry" items="${update.failedStatusMap}">
                <tr data-gateway-id="${gatewayEntry.key.paoIdentifier.paoId}">
                    <cti:url var="url" value="/stars/gateways/${gatewayEntry.key.paoIdentifier.paoId}"/>
                    <td><a href="${url}">${fn:escapeXml(gatewayEntry.key.name)}</a></td>
                    <td>${gatewayEntry.key.rfnIdentifier.sensorSerialNumber}</td>
                    <td>${gatewayEntry.key.data.ipAddress}</td>
                    <td><i:inline key=".fail.${gatewayEntry.value}"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
</div>

<div class="stacked">
<h3><i:inline key=".successful"/>&nbsp;&nbsp;<span class="badge badge-success">${fn:length(update.successful)}</span></h3>
<c:if test="${not empty update.successful}">
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".serialNumber"/></th>
                <th><i:inline key=".ipaddress"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="gateway" items="${update.successful}">
                <tr data-gateway-id="${gateway.paoIdentifier.paoId}">
                    <cti:url var="url" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
                    <td><a href="${url}">${fn:escapeXml(gateway.name)}</a></td>
                    <td>${gateway.rfnIdentifier.sensorSerialNumber}</td>
                    <td>${gateway.data.ipAddress}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
</div>

<div class="stacked">
<h3><i:inline key=".pending"/>&nbsp;&nbsp;<span class="badge">${fn:length(update.pending)}</span></h3>
<c:if test="${not empty update.pending}">
    <table class="compact-results-table">
        <thead>
            <tr>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".serialNumber"/></th>
                <th><i:inline key=".ipaddress"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="gateway" items="${update.pending}">
                <tr data-gateway-id="${gateway.paoIdentifier.paoId}">
                    <cti:url var="url" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
                    <td><a href="${url}">${fn:escapeXml(gateway.name)}</a></td>
                    <td>${gateway.rfnIdentifier.sensorSerialNumber}</td>
                    <td>${gateway.data.ipAddress}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</c:if>
</div>

</cti:msgScope>