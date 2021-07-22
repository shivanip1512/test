<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="deviceTotalCount" required="true" description="Device Total Count" %>
<%@ attribute name="deviceWarningsCount" required="true" description="Device Warnings Count" %>
<%@ attribute name="deviceLabel" required="true" description="i18n text for Labels" %>
<%@ attribute name="fromDetailPage" required="true" description="Checks whether the call is from infrastructure warnings widget or from details page" %>
<%@ attribute name="deviceType" required="true" description="Device type as defined in InfrastructureWarningDeviceCategory" %>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <td>
        <c:choose>
            <c:when test="${fromDetailPage}">
                ${deviceLabel}:
            </c:when>
            <c:otherwise>
                <a href="${allWarningsUrl}?types=${deviceType}" target="_blank">${deviceLabel}</a>:
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <cti:msg2 var="hoverText" key=".noWarning.hoverText" argument="${deviceLabel}"/>
        <span class="label bg-color-green" title="${hoverText}">${deviceTotalCount - deviceWarningsCount}</span>
        <cti:msg2 var="hoverText" key=".warning.hoverText" argument="${deviceLabel}"/>
        <span class="label bg-color-orange" title="${hoverText}">${deviceWarningsCount}</span>
    </td>
</cti:msgScope>
