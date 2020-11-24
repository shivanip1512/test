<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">
    <table width="70%">
        <tr>
            <c:choose>
                <c:when test="${infrastructureWarningDeviceCategory == 'RELAY'}">
                    <cti:msg2 var="deviceLabel" key=".relays"/>
                    <c:set var="deviceTotalCount" value="${summary.totalRelays}"/>
                    <c:set var="deviceWarningsCount" value="${summary.warningRelays}"/>
                    <c:set var="deviceLabel" value="${deviceLabel}"/>
                </c:when>
                <c:when test="${infrastructureWarningDeviceCategory == 'CCU'}">
                    <cti:msg2 var="deviceLabel" key=".CCUs"/>
                    <c:set var="deviceTotalCount" value="${summary.totalCcus}"/>
                    <c:set var="deviceWarningsCount" value="${summary.warningCcus}"/>
                    <c:set var="deviceLabel" value="${deviceLabel}"/>
                </c:when>
                <c:when test="${infrastructureWarningDeviceCategory == 'REPEATER'}">
                    <cti:msg2 var="deviceLabel" key=".repeaters"/>
                    <c:set var="deviceTotalCount" value="${summary.totalRepeaters}"/>
                    <c:set var="deviceWarningsCount" value="${summary.warningRepeaters}"/>
                    <c:set var="deviceLabel" value="${deviceLabel}"/>
                </c:when>
                <c:otherwise>
                    <cti:msg2 var="deviceLabel" key=".gateways"/>
                    <c:set var="deviceTotalCount" value="${summary.totalGateways}"/>
                    <c:set var="deviceWarningsCount" value="${summary.warningGateways}"/>
                    <c:set var="deviceLabel" value="${deviceLabel}"/>
                </c:otherwise>
            </c:choose>
            <c:if test="${deviceTotalCount != 0}">
                <tags:infrastructureWarningsCount deviceTotalCount="${deviceTotalCount}" 
                                                  deviceWarningsCount="${deviceWarningsCount}" 
                                                  deviceLabel="${deviceLabel}" 
                                                  fromDetailPage="${fromDetailPage}"/>
            </c:if>
        </tr>
    </table>
    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <span class="fr"><a href="${allWarningsUrl}?types=${infrastructureWarningDeviceCategory}" target="_blank"><i:inline key="yukon.common.viewDetails"/></a></span>
    <%@ include file="infrastructureWarningsDetails.jsp" %>
</cti:msgScope>