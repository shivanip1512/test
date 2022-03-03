<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
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
                        <span class="label bg-color-green" title="${hoverText}">${summary.devicesWithoutWarningsCount}</span>
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
                    <cti:msg2 var="deviceLabel" key=".gateways"/>
                    <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalGateways}"
                                                      deviceWarningsCount="${summary.warningGateways}"
                                                      deviceLabel="${deviceLabel}"
                                                      fromDetailPage="${fromDetailPage}"
                                                      deviceType="GATEWAY" />
                </c:if>
                <c:if test="${summary.totalRelays != 0}">
                    <td width="10%">
                    </td>
                    <cti:msg2 var="deviceLabel" key=".relays"/>
                    <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalRelays}"
                                                      deviceWarningsCount="${summary.warningRelays}"
                                                      deviceLabel="${deviceLabel}"
                                                      fromDetailPage="${fromDetailPage}"
                                                      deviceType="RELAY" />
                </c:if>
            </tr>
        </c:if>
        <c:if test="${summary.totalCcus != 0 || summary.totalRepeaters != 0}">
            <tr>
                <c:if test="${summary.totalCcus != 0}">
                    <cti:msg2 var="deviceLabel" key=".CCUs"/>
                    <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalCcus}"
                                                      deviceWarningsCount="${summary.warningCcus}"
                                                      deviceLabel="${deviceLabel}" 
                                                      fromDetailPage="${fromDetailPage}"
                                                      deviceType="CCU" />
                </c:if>
                <c:if test="${summary.totalRepeaters != 0}">
                    <td width="10%">
                    </td>
                    <cti:msg2 var="deviceLabel" key=".repeaters"/>
                    <tags:infrastructureWarningsCount deviceTotalCount="${summary.totalRepeaters}"
                                                      deviceWarningsCount="${summary.warningRepeaters}"
                                                      deviceLabel="${deviceLabel}" 
                                                      fromDetailPage="${fromDetailPage}"
                                                      deviceType="REPEATER" />
                </c:if>
            </tr>
        </c:if>
    </table>
   
</cti:msgScope>