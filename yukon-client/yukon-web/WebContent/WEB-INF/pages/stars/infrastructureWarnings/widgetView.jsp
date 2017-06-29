<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="widgets.infrastructureWarnings">

    <cti:url var="allWarningsUrl" value="/stars/infrastructureWarnings/detail"/>
    <table class="compact-results-table">
        <th class="fwn PB0"><a href="${allWarningsUrl}?types=GATEWAY" target="_blank"><cti:msg2 key=".gateways"/></a></th>
        <th class="fwn PB0"><a href="${allWarningsUrl}?types=RELAY" target="_blank"><cti:msg2 key=".relays"/></a></th>
        <th class="fwn PB0"><a href="${allWarningsUrl}?types=CCU" target="_blank"><cti:msg2 key=".CCUs"/></a></th>
        <th class="fwn PB0"><a href="${allWarningsUrl}?types=REPEATER" target="_blank"><cti:msg2 key=".repeaters"/></a></th>
        <tr>
            <td class="PT0">
                <span class="label label-success" style="margin-right:5px;">${summary.totalGateways - summary.warningGateways}</span>
                <span class="label label-warning">${summary.warningGateways}</span>
            </td>
            <td class="PT0">
                <span class="label label-success" style="margin-right:5px;">${summary.totalRelays - summary.warningRelays}</span>
                <span class="label label-warning">${summary.warningRelays}</span>
            </td>
            <td class="PT0">
                <span class="label label-success" style="margin-right:5px;">${summary.totalCcus - summary.warningCcus}</span>
                <span class="label label-warning">${summary.warningCcus}</span>
            </td>
            <td class="PT0">
                <span class="label label-success" style="margin-right:5px;">${summary.totalRepeaters - summary.warningRepeaters}</span>
                <span class="label label-warning">${summary.warningRepeaters}</span>
            </td>
        </tr>
    </table>  
    <span class="fr"><a href="${allWarningsUrl}" target="_blank"><cti:msg2 key=".seeAll"/></a></span>
    <br/>  
        
    <tags:sectionContainer2 nameKey="recentIssues">
        <table class="compact-results-table">
            <th><cti:msg2 key=".name"/></th>
            <th><cti:msg2 key=".type"/></th>
            <th><cti:msg2 key=".status"/></th>
        
        <c:choose>
            <c:when test="${warnings.size() > 0}">
                <c:forEach var="warning" items="${warnings}">
                    <tr>
                        <td class="wsnw">
                            <cti:paoDetailUrl yukonPao="${warning.paoIdentifier}" newTab="true">
                                <cti:deviceName deviceId="${warning.paoIdentifier.paoId}"/>
                            </cti:paoDetailUrl>
                        </td>
                        <td class="wsnw">${warning.paoIdentifier.paoType.paoTypeName}</td>
                        <td>
                            <c:set var="warningColor" value="warning"/>
                            <c:if test="${warning.severity == 'HIGH'}">
                                <c:set var="warningColor" value="error"/>
                            </c:if>
                            <span class="${warningColor}"><cti:msg2 key="${warning.warningType.formatKey}.${warning.severity}" arguments="${warning.arguments}"/></td>
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
        <span class="fl" style="font-size:11px" title="${lastUpdatedMsg}"><cti:formatDate value="${summary.lastRun}" type="DATEHMS_12"/></span>
        <cti:msg2 key="yukon.web.widgets.forceUpdate" var="forceUpdateMsg"/>
        <cti:icon icon="icon-arrow-refresh" title="${forceUpdateMsg}" classes="js-update-infrastructure-warnings cp"/>
    </span>

</cti:msgScope>