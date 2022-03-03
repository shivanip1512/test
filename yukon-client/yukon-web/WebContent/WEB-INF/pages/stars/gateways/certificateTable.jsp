<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.manageCertificates, yukon.web.modules.operator.gateways.list, yukon.web.modules.operator.gateways">
    <table id="cert-table" class="compact-results-table">
        <thead>
            <tr>
                <tags:sort column="${TIMESTAMP}" width="15%"/>
                <tags:sort column="${CERTIFICATE}"/>
                <th><i:inline key=".cert.update.gateways"/></th>
                <th width="10%"><i:inline key="yukon.common.status"/></th>
                <tags:sort column="${PENDING}"/>
                <tags:sort column="${FAILED}"/>
                <tags:sort column="${SUCCESSFUL}"/>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="update" items="${certUpdates}">
                <tr data-yui="${update.yukonUpdateId}">
                    <td class="js-cert-update-timestamp">
                        <a href="javascript:void(0);"><cti:formatDate value="${update.timestamp}" type="DATEHM_12"/></a>
                    </td>
                    <td class="js-cert-update-file">${fn:escapeXml(update.fileName)}</td>
                    <c:set var="all" value="${update.gateways}"/>
                    <td class="js-cert-update-gateways">
                        ${fn:escapeXml(all[0].name)}<c:if test="${fn:length(all) > 1}">,&nbsp;${fn:escapeXml(all[1].name)}</c:if>
                        <c:if test="${fn:length(all) > 2}">
                            <i:inline key=".cert.update.more" arguments="${fn:length(all) - 2}"/>
                        </c:if>
                    </td>
                    <td class="js-cert-update-status">
                        <c:choose>
                            <c:when test="${fn:length(update.pending) == 0}">
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
                    <td class="js-cert-update-pending subtle">${fn:length(update.pending)}</td>
                    <td class="js-cert-update-failed error">${fn:length(update.failed)}</td>
                    <td class="js-cert-update-successful success">${fn:length(update.successful)}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <c:if test="${empty certUpdates}">
        <span class="empty-list compact-results-table"><i:inline key=".manageCertificates.cert.updates.none"/></span>
    </c:if>
</cti:msgScope>