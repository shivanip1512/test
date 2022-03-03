<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator.gateways.firmwareInformationWidget, yukon.web.modules.operator.gateways">
    <div id="js-firmware-info">
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
    </div>

    <cti:includeScript link="/resources/js/widgets/yukon.widget.firmwareInformation.js"/>
</cti:msgScope>