<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.certificateUpdates">
    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
        <div id="gateway-cert-popup"
             class="dn"
             data-dialog
             data-title="<cti:msg2 key=".cert.update.label"/>"
             data-event="yukon:assets:gateway:cert:update"
             data-help-text="<cti:msg key="yukon.web.modules.operator.gateways.cert.update.note"/>"
             data-url="<cti:url value="/stars/gateways/cert-update/options"/>"
             data-width="450"
             data-height="250"
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

    <div id="gateway-cert-details-popup" class="dn"></div>

    <div data-url="<cti:url value="/stars/gateways/certificateUpdates"/>" data-static> 
        <table id="cert-table" class="compact-results-table">
            <thead>
                <tr>
                    <tags:sort column="${TIMESTAMP}"/>
                    <tags:sort column="${CERTIFICATE}"/>
                    <th><i:inline key=".cert.update.gateways"/></th>
                    <th><i:inline key="yukon.common.status"/></th>
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
                                    <div class="progress dib vat">
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
            <span class="empty-list compact-results-table"><i:inline key=".cert.updates.none"/></span>
        </c:if>
    </div>
    <div id="gateway-templates" class="dn">
        <table>
            <tr class="js-new-cert-update" data-yui="">
                <td class="js-cert-update-timestamp">
                    <a href="javascript:void(0);"></a>
                </td>
                <td class="js-cert-update-file"></td>
                <td class="js-cert-update-gateways"></td>
                <td class="js-cert-update-status">
                    <div class="progress dib vat">
                        <div class="progress-bar progress-bar-success"></div>
                        <div class="progress-bar progress-bar-danger"></div>
                    </div>
                    <span class="js-percent"></span>
                </td>
                <td class="js-cert-update-pending subtle"></td>
                <td class="js-cert-update-failed error"></td>
                <td class="js-cert-update-successful success"></td>
            </tr>
        </table>
        <cti:toJson object="${text}" id="gateway-text"/>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.assets.gateway.list.js"/>
</cti:standardPage>