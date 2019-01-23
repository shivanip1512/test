<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <table width="70%">
        <c:if test="${fromDetailPage}">
            <tr>
                <td colspan="5" style="padding-left:70px;">
                    <span class="fl">
                        <cti:msg2 var="allTypesLabel" key=".allTypes"/>
                        ${allTypesLabel}:&nbsp;&nbsp;
                        <cti:msg2 var="hoverText" key=".noWarning.hoverText" argument="${allTypesLabel}"/>
                        <span class="label bg-color-pie-green" title="${hoverText}">${summary.devicesWithoutWarningsCount}</span>
                        <cti:msg2 var="hoverText" key=".warning.hoverText" argument="${allTypesLabel}"/>
                        <span class="label bg-color-orange" title="${hoverText}">${summary.devicesWithWarningsCount}</span>
                    </span>
                    <cti:url var="downloadAll" value="/stars/infrastructureWarnings/downloadAll"/>
                    <cti:icon icon="icon-csv" nameKey="downloadAll" href="${downloadAll}" style="margin-top:2px;"/>
                 </td>
            </tr>
        </c:if>
        <c:if test="${summary.totalGateways != 0 || summary.totalRelays != 0}">
            <tr>
                <c:if test="${summary.totalGateways != 0}">
                    <cti:msg2 var="gatewayLabel" key=".gateways"/>
                    <td>
                        <c:choose>
                            <c:when test="${fromDetailPage}">
                                ${gatewayLabel}:
                            </c:when>
                            <c:otherwise>
                                <a href="${allWarningsUrl}?types=GATEWAY" target="_blank">${gatewayLabel}</a>:
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <cti:msg2 var="hoverText" key=".noWarning.hoverText" argument="${gatewayLabel}"/>
                        <span class="label bg-color-pie-green" title="${hoverText}">${summary.totalGateways - summary.warningGateways}</span>
                        <cti:msg2 var="hoverText" key=".warning.hoverText" argument="${gatewayLabel}"/>
                        <span class="label bg-color-orange" title="${hoverText}">${summary.warningGateways}</span>
                    </td>
                </c:if>
                <c:if test="${summary.totalRelays != 0}">
                    <cti:msg2 var="relayLabel" key=".relays"/>
                    <td width="10%">
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${fromDetailPage}">
                                ${relayLabel}:
                            </c:when>
                            <c:otherwise>
                                <a href="${allWarningsUrl}?types=RELAY" target="_blank">${relayLabel}</a>:
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <cti:msg2 var="hoverText" key=".noWarning.hoverText" argument="${relayLabel}"/>
                        <span class="label bg-color-pie-green" title="${hoverText}">${summary.totalRelays - summary.warningRelays}</span>
                        <cti:msg2 var="hoverText" key=".warning.hoverText" argument="${relayLabel}"/>
                        <span class="label bg-color-orange" title="${hoverText}">${summary.warningRelays}</span>
                    </td>
                </c:if>
            </tr>            
        </c:if>
        <c:if test="${summary.totalCcus != 0 || summary.totalRepeaters != 0}">
            <tr>
                <c:if test="${summary.totalCcus != 0}">
                    <cti:msg2 var="ccuLabel" key=".CCUs"/>
                    <td>
                        <c:choose>
                            <c:when test="${fromDetailPage}">
                                ${ccuLabel}:
                            </c:when>
                            <c:otherwise>
                                <a href="${allWarningsUrl}?types=CCU" target="_blank">${ccuLabel}</a>:
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <cti:msg2 var="hoverText" key=".noWarning.hoverText" argument="${ccuLabel}"/>
                        <span class="label bg-color-pie-green" title="${hoverText}">${summary.totalCcus - summary.warningCcus}</span>
                        <cti:msg2 var="hoverText" key=".warning.hoverText" argument="${ccuLabel}"/>
                        <span class="label bg-color-orange" title="${hoverText}">${summary.warningCcus}</span>
                    </td>
                </c:if>
                <c:if test="${summary.totalRepeaters != 0}">
                    <cti:msg2 var="repeaterLabel" key=".repeaters"/>
                    <td width="10%">
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${fromDetailPage}">
                                ${repeaterLabel}:
                            </c:when>
                            <c:otherwise>
                                <a href="${allWarningsUrl}?types=REPEATER" target="_blank">${repeaterLabel}</a>:
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <cti:msg2 var="hoverText" key=".noWarning.hoverText" argument="${repeaterLabel}"/>
                        <span class="label bg-color-pie-green" title="${hoverText}">${summary.totalRepeaters - summary.warningRepeaters}</span>
                        <cti:msg2 var="hoverText" key=".warning.hoverText" argument="${repeaterLabel}"/>
                        <span class="label bg-color-orange" title="${hoverText}">${summary.warningRepeaters}</span>
                    </td>
                </c:if>
            </tr>
        </c:if>
    </table>
   
</cti:msgScope>