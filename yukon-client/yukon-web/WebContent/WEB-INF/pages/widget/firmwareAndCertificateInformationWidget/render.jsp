<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.firmwareAndCertificateInformationWidget, yukon.web.modules.operator.gateways">
    <div id="js-firmware-and-certificate-info">
        <tags:sectionContainer2 nameKey="firmwareUpdates">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".version.current">
                            <c:choose>
                                <c:when test="${isCurrentVersionAvailable}">
                                    ${fn:escapeXml(gatewayReleaseVersions)}
                                </c:when>
                                <c:otherwise>
                                    <cti:icon icon="icon-loading-bars"/>
                                </c:otherwise>
                            </c:choose>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".version.available">
                            <c:choose>
                                <c:when test="${isCurrentVersionAvailable && empty gatewayAvailableVersions}">
                                    <i:inline key="yukon.common.na"/>
                                </c:when>
                                <c:when test="${not empty gatewayAvailableVersions}">
                                    ${fn:escapeXml(gatewayAvailableVersions)}
                                </c:when>
                                <c:otherwise>
                                    <cti:icon icon="icon-loading-bars"/>
                                </c:otherwise>
                            </c:choose>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
            </div>
        </tags:sectionContainer2>
    
        <tags:sectionContainer2 nameKey="certificateUpdates">
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th width="20%"><i:inline key=".list.firmwareUpdates.timestamp"/></th>
                        <th width="40%"><i:inline key=".cert"/></th>
                        <th width="40%"><i:inline key=".cert.update.gateways"/></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="update" items="${certUpdates}">
                        <tr>
                            <td>
                                <cti:formatDate value="${update.timestamp}" type="FULLHM"/>
                            </td>
                            <td>${fn:escapeXml(update.fileName)}</td>
                            <c:set var="all" value="${update.gateways}"/>
                            <td>
                                ${fn:escapeXml(all[0].name)}<c:if test="${fn:length(all) > 1}">,&nbsp;${fn:escapeXml(all[1].name)}</c:if>
                                <c:if test="${fn:length(all) > 2}">
                                    <i:inline key=".certificateUpdates.cert.update.more" arguments="${fn:length(all) - 2}"/>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <c:if test="${empty certUpdates}">
                <span class="empty-list compact-results-table"><i:inline key=".certificateUpdates.cert.updates.none"/></span>
            </c:if>
        </tags:sectionContainer2>
    
        <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="OWNER">
            <cti:url var="manageCertificate" value="/stars/gateways/certificateUpdates"/>
            <span class="fr"><a href="${manageCertificate}"><i:inline key="yukon.common.viewDetails"/></a></span>
        </cti:checkRolesAndProperties>
    </div>

    <cti:includeScript link="/resources/js/widgets/yukon.widget.firmwareAndCertificateInformation.js"/>
</cti:msgScope>