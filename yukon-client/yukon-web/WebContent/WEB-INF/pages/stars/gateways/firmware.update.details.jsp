<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways.firmware.update, yukon.common">

<div class="stacked clear">
    <h3>
        <i:inline key=".failed"/>&nbsp;
        <span class="badge badge-error">${fn:length(results.failed)}</span>
    </h3>
    <c:if test="${not empty results.failed}">
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".serialNumber"/></th>
                    <th><i:inline key=".releaseVersion"/></th>
                    <th><i:inline key=".availableVersion"/></th>
                    <th><i:inline key=".url"/></th>
                    <th><i:inline key=".failType"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="entry" items="${results.failed}">
                    <c:set var="gateway" value="${gateways.get(entry.gatewayPaoId)}"/>
                    <tr>
                        <cti:url var="url" value="/stars/gateways/${entry.gatewayPaoId}"/>
                        <td class="wsnw"><a href="${url}">${fn:escapeXml(gateway.name)}</a></td>
                        <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                        <td>${fn:escapeXml(entry.originalVersion)}</td>
                        <td>${fn:escapeXml(entry.newVersion)}</td>
                        <td>${fn:escapeXml(entry.updateServerUrl)}</td>
                        <td><i:inline key="${entry.status}"/></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

<div class="stacked">
    <h3>
        <i:inline key=".successful"/>&nbsp;
        <span class="badge badge-success">${fn:length(results.success)}</span>
    </h3>
    <c:if test="${not empty results.success}">
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".serialNumber"/></th>
                    <th><i:inline key=".releaseVersion"/></th>
                    <th><i:inline key=".availableVersion"/></th>
                    <th><i:inline key=".url"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="entry" items="${results.success}">
                    <c:set var="gateway" value="${gateways.get(entry.gatewayPaoId)}"/>
                    <tr>
                        <cti:url var="url" value="/stars/gateways/${entry.gatewayPaoId}"/>
                        <td class="wsnw"><a href="${url}">${fn:escapeXml(gateway.name)}</a></td>
                        <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                        <td>${fn:escapeXml(entry.originalVersion)}</td>
                        <td>${fn:escapeXml(entry.newVersion)}</td>
                        <td>${fn:escapeXml(entry.updateServerUrl)}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

<div class="stacked">
    <h3>
        <i:inline key=".pending"/>&nbsp;
        <span class="badge">${fn:length(results.pending)}</span>
    </h3>
    <c:if test="${not empty results.pending}">
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".serialNumber"/></th>
                    <th><i:inline key=".releaseVersion"/></th>
                    <th><i:inline key=".availableVersion"/></th>
                    <th><i:inline key=".url"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="entry" items="${results.pending}">
                    <c:set var="gateway" value="${gateways.get(entry.gatewayPaoId)}"/>
                    <tr>
                        <cti:url var="url" value="/stars/gateways/${entry.gatewayPaoId}"/>
                        <td class="wsnw"><a href="${url}">${fn:escapeXml(gateway.name)}</a></td>
                        <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                        <td>${fn:escapeXml(entry.originalVersion)}</td>
                        <td>${fn:escapeXml(entry.newVersion)}</td>
                        <td>${fn:escapeXml(entry.updateServerUrl)}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>

</cti:msgScope>
