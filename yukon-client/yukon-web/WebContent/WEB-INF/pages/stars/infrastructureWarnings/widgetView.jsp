<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

<style>
.summaryNumbers {
    font-size:85%;
    margin-right:3px;
}
</style>

    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <table class="compact-results-table">
        <c:if test="${summary.totalGateways != 0}">
            <th class="fwn PB0"><a href="${allWarningsUrl}?types=GATEWAY" target="_blank"><cti:msg2 key=".gateways"/></a></th>
        </c:if>
        <c:if test="${summary.totalRelays != 0}">
            <th class="fwn PB0"><a href="${allWarningsUrl}?types=RELAY" target="_blank"><cti:msg2 key=".relays"/></a></th>
        </c:if>
        <c:if test="${summary.totalCcus != 0}">
            <th class="fwn PB0"><a href="${allWarningsUrl}?types=CCU" target="_blank"><cti:msg2 key=".CCUs"/></a></th>
        </c:if>
        <c:if test="${summary.totalRepeaters != 0}">
            <th class="fwn PB0"><a href="${allWarningsUrl}?types=REPEATER" target="_blank"><cti:msg2 key=".repeaters"/></a></th>
        </c:if>
        <tr>
            <c:if test="${summary.totalGateways != 0}">
                <td class="PT0">
                    <span class="label bg-color-pie-green summaryNumbers">${summary.totalGateways - summary.warningGateways}</span>
                    <span class="label bg-color-orange summaryNumbers">${summary.warningGateways}</span>
                </td>
            </c:if>
            <c:if test="${summary.totalRelays != 0}">
                <td class="PT0">
                    <span class="label bg-color-pie-green summaryNumbers">${summary.totalRelays - summary.warningRelays}</span>
                    <span class="label bg-color-orange summaryNumbers">${summary.warningRelays}</span>
                </td>
            </c:if>
            <c:if test="${summary.totalCcus != 0}">
                <td class="PT0">
                    <span class="label bg-color-pie-green summaryNumbers">${summary.totalCcus - summary.warningCcus}</span>
                    <span class="label bg-color-orange summaryNumbers">${summary.warningCcus}</span>
                </td>
            </c:if>
            <c:if test="${summary.totalRepeaters != 0}">
                <td class="PT0">
                    <span class="label bg-color-pie-green summaryNumbers">${summary.totalRepeaters - summary.warningRepeaters}</span>
                    <span class="label bg-color-orange summaryNumbers">${summary.warningRepeaters}</span>
                </td>
            </c:if>
        </tr>
    </table>  
    <span class="fr"><a href="${allWarningsUrl}" target="_blank"><cti:msg2 key=".seeAll"/></a></span>
    <br/>  
        
    <tags:sectionContainer2 nameKey="recentIssues">
        <table class="compact-results-table" width="100%">
            <th width="20%"><cti:msg2 key=".name"/></th>
            <th width="60%"><cti:msg2 key=".status"/></th>
            <th width="20%"><cti:msg2 key="yukon.common.duration"/></th>
        
            <c:choose>
                <c:when test="${warnings.size() > 0}">
                    <c:forEach var="warning" items="${warnings}">
                        <tr>
                            <td class="wsnw">
                                <cti:paoDetailUrl yukonPao="${warning.paoIdentifier}" newTab="true">
                                    <cti:deviceName deviceId="${warning.paoIdentifier.paoId}"/>
                                </cti:paoDetailUrl>
                            </td>
                            <td>
                                <c:set var="warningColor" value="warning"/>
                                <c:if test="${warning.severity == 'HIGH'}">
                                    <c:set var="warningColor" value="error"/>
                                </c:if>
                                <span class="${warningColor}">
                                    <cti:msg2 key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/>
                                </span>
                            </td>
                            <td>
                                <c:choose>                                    
                                    <c:when test="${warning.timestamp le epoch1990}">
                                        <cti:msg2 key="yukon.common.dashes" var="timestamp"/>
                                        <div title="${timestamp}">
                                            <i:inline key="${warning.approximateDurationKey}" arguments="${warning.approximateDurationValue}"/>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatDate value="${warning.timestamp}" type="FULL" var="timestamp"/>
                                        <div title="${timestamp}">
                                            <i:inline key="${warning.approximateDurationKey}" arguments="${warning.approximateDurationValue}"/>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="3">
                            <span class="empty-list"><i:inline key=".noRecentWarnings" /></span>
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </table>
    </tags:sectionContainer2>
        
    <span class="fr">
        <cti:msg2 key="yukon.web.widgets.lastUpdated" var="lastUpdatedMsg"/>
        <span class="fl" style="font-size:11px" title="${lastUpdatedMsg}"><cti:formatDate value="${lastAttemptedRefresh}" type="DATEHMS_12"/></span>
        <cti:button renderMode="image" icon="icon-arrow-refresh" title="${refreshTooltip}" classes="js-update-infrastructure-warnings" disabled="${!isRefreshPossible}"/>
    </span>

</cti:msgScope>