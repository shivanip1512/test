<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="deviceTotalCount" required="true" description="Device Total Count" %>
<%@ attribute name="deviceWarningsCount" required="true" description="Device Warnings Count" %>
<%@ attribute name="deviceLabel" required="true" description="i18n text for Labels" %>
<%@ attribute name="fromDetailPage" required="true" description="Checks whether the call is from infrastructure warnings widget or from details page" %>
<%@ attribute name="deviceType" required="true" description="Device type as defined in InfrastructureWarningDeviceCategory" %>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
        <c:set var="columnClass" value="${fromDetailPage ? '' : 'column-10-14'}"/>
        <div class="${columnClass}">
            <div class="column one nogutter">
                <b>${deviceLabel}</b>
            </div>
            <div class="column two nogutter">
                <c:choose>
                    <c:when test="${deviceWarningsCount > 0}">
                        <cti:msg2 var="hoverText" key=".warning.hoverText" argument="${deviceLabel}"/>
                        <span style="font-size:16px" title="${hoverText}">
                            <cti:icon icon="icon-error" classes="fn" href="${allWarningsUrl}?types=${deviceType}"/>
                            <b>${deviceWarningsCount}</b>
                        </span>
                        <span class="notes"> of ${deviceTotalCount}</span>
                    </c:when>
                    <c:otherwise>
                        <cti:msg2 var="hoverText" key=".noWarning.hoverText" argument="${deviceLabel}"/>
                        <span style="font-size:16px" title="${hoverText}">
                            <cti:icon icon="icon-accept" classes="fn"/>
                            <b>${deviceTotalCount}</b>
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
</cti:msgScope>
