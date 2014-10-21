<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="gateways.list">
    
    <div id="page-buttons" class="dn">
        <cti:button icon="icon-plus-green" nameKey="create" data-popup="#gateway-create-popup"/>
    </div>
    <div id="gateway-create-popup" class="dn" data-title="<cti:msg2 key=".create.title"/>" data-url="gateways/create" 
        data-width="570" data-min-width="570" data-dialog data-event="yukon:assets:gateway:save" 
        data-ok-text="<cti:msg2 key="components.button.save.label"/>" data-load-event="yukon:assets:gateway:load">
    </div>
    <table id="gateways-table" class="compact-results-table has-actions has-alerts">
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
                <tr data-gateway="${gateway.paoIdentifier.paoId}" data-loaded="${not empty gateway.data}">
                    <c:choose>
                        <c:when test="${not empty gateway.data}">
                            <c:set var="data" value="${gateway.data}"/>
                            <c:set var="title"><cti:msg2 key=".connectionStatus.${data.connectionStatus}"/></c:set>
                            <c:set var="clazz" value="${data.connectionStatus == 'CONNECTED' ? 'green' : 'red'}"/>
                            <td class="js-gw-conn-status" title="${title}">
                                <span class="state-box ${clazz}"></span>
                            </td>
                            <cti:url var="gatewayDetailLink" value="gateways/${gateway.paoIdentifier.paoId}"/>
                            <td class="js-gw-name"><a href="${gatewayDetailLink}">${fn:escapeXml(gateway.name)}</a></td>
                            <td class="js-gw-sn">${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                            <td class="js-gw-ip">${fn:escapeXml(gateway.data.ipAddress)}</td>
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
                            <td class="js-gw-last-comm ${clazz}" title="<cti:formatDate type="DATEHM" value="${gateway.data.lastCommStatusTimestamp}"/>">
                                <i:inline key=".lastCommStatus.${gateway.data.lastCommStatus}"/>
                            </td>
                            <td class="js-gw-data-collection">
                                <div class="dib vat progress">
                                    <c:set var="clazz" value="progress-bar-success"/>
                                    <c:if test="${gateway.totalCompletionLevelWarning}">
                                        <c:set var="clazz" value="progress-bar-warning"/>
                                    </c:if>
                                    <c:if test="${gateway.totalCompletionLevelDanger}">
                                        <c:set var="clazz" value="progress-bar-danger"/>
                                    </c:if>
                                    <div class="progress-bar ${clazz}" style="width: ${gateway.totalCompletionPercentage}%"></div>
                                </div>&nbsp;
                                <span class="js-data-collection-percent">
                                    <fmt:formatNumber pattern="###.##%" value="${gateway.totalCompletionPercentage / 100}"/>
                                </span>
                            </td>
                            <td class="action-column">
                                <cm:dropdown>
                                    <cm:dropdownOption icon="icon-connect" key=".connect"/>
                                    <cm:dropdownOption icon="icon-disconnect" key=".disconnect"/>
                                    <li class="divider"></li>
                                    <cm:dropdownOption icon="icon-table-row-insert" key=".collectData"/>
                                </cm:dropdown>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td><cti:icon icon="icon-loading-bars"/></td>
                            <td></td>
                            <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                            <td colspan="4"><i:inline key=".loadingGatewayData"/></td>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
    <cti:includeScript link="/JavaScript/yukon.assets.gateway.list.js"/>
    <cti:includeScript link="/JavaScript/yukon.assets.gateway.shared.js"/>
    
<div id="gateway-templates" class="dn">
    <table>
        <tr class="js-loaded-row" data-gateway="" data-loaded="true">
            <td class="js-gw-conn-status"><span class="state-box"></span></td>
            <td class="js-gw-name"><a></a></td>
            <td class="js-gw-sn"></td>
            <td class="js-gw-ip"></td>
            <td class="js-gw-last-comm"></td>
            <td class="js-gw-data-collection">
                <div class="dib vat progress">
                    <div class="progress-bar progress-bar-success"></div>
                </div>&nbsp;
                <span class="js-gw-data-collection-percent"></span>
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
        <tr class="js-loading-row" data-gateway="" data-loaded="false">
            <td><cti:icon icon="icon-loading-bars"/></td>
            <td></td>
            <td></td>
            <td colspan="4"><i:inline key=".loadingGatewayData"/></td>
        </tr>
    </table>
</div>
</cti:standardPage>