<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.dashboard">

    <div id="page-buttons" class="dn">
        <cti:button icon="icon-plus-green" nameKey="create" data-popup="#gateway-create-popup"/>
    </div>
    <div id="gateway-create-popup" class="dn" data-title="Create Gateway" data-url="gateways/create" 
        data-width="360" data-dialog data-event="yukon_assets_gateway_save">
        
    </div>
    <table class="compact-results-table has-actions has-alerts">
        <thead>
            <tr>
                <th></th>
                <th><i:inline key=".name"/><cti:icon icon="icon-bullet-arrow-down" classes="fn M0"/></th>
                <th><i:inline key=".serialNumber"/></th>
                <th><i:inline key=".ipaddress"/></th>
                <th><i:inline key=".lastComms"/></th>
                <th colspan="2"><i:inline key=".detail.dataCollection.title"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${gateways}" var="gateway">
                <tr>
<!--                 TODO : determine if they are connected and display the appropriate tool tip -->
                    <td title="<cti:msg2 key=".connected"/> <cti:formatDate type="DATEHM" value="${gateway.data.lastCommStatusTimestamp}"/>">
                        <c:set var="clazz" value="${gateway.data.connectionStatus == 'CONNECTED' ? 'green' : 'red'}"/>
                        <span class="state-box ${clazz}"></span>
                    </td>
                    <cti:url var="gatewayDetailLink" value="gateways/${gateway.paoIdentifier.paoId}"/>
                    <td><a href="${gatewayDetailLink}">${fn:escapeXml(gateway.data.name)}</a></td>
                    <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                    <td>${fn:escapeXml(gateway.data.ipAddress)}</td>
                    <td title="<cti:formatDate type="DATEHM" value="${gateway.data.lastCommStatusTimestamp}"/>">
                        <c:set var="clazz" value="green"/>
                        <c:if test="${gateway.lastCommFailed}">
                            <c:set var="clazz" value="red"/>
                        </c:if>
                        <c:if test="${gateway.lastCommMissed}">
                            <c:set var="clazz" value="orange"/>
                        </c:if>
                        <c:if test="${gateway.lastCommUnknown}">
                            <c:set var="clazz" value="subtle"/>
                        </c:if>
                        <span class="${clazz}"><i:inline key=".lastCommStatus.${gateway.data.lastCommStatus}"/></span>
                    </td>
                    <td>
                        <div class="dib vam progress">
                            <c:set var="clazz" value="progress-bar-success"/>
                            <c:if test="${gateway.totalCompletionLevelWarning}">
                                <c:set var="clazz" value="progress-bar-warning"/>
                            </c:if>
                            <c:if test="${gateway.totalCompletionLevelDanger}">
                                <c:set var="clazz" value="progress-bar-danger"/>
                            </c:if>
                            <div class="progress-bar ${clazz}" style="width: ${gateway.totalCompletionPercentage}%"></div>
                        </div>&nbsp;${gateway.totalCompletionPercentage}%
                    </td>
                    <td class="action-column">
                        <cm:dropdown>
                            <cm:dropdownOption icon="icon-connect" key=".connect"/>
                            <cm:dropdownOption icon="icon-disconnect" key=".disconnect"/>
                            <li class="divider"></li>
                            <cm:dropdownOption icon="icon-table-row-insert" key=".collectData"/>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
</cti:standardPage>