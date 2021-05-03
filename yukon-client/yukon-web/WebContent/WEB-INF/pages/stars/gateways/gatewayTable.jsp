<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.operator, yukon.web.modules.operator.gateways, yukon.web.modules.operator.gateways.list">
    <table id="gateways-table" class="compact-results-table has-actions has-alerts">
        <thead>
            <tr>
                <th class="row-icon"/>
                <th class="row-icon"/>
                <tags:sort column="${NAME}"/>
                <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                    <th width="10%"><i:inline key=".streamingCapacity"/></th>
                </cti:checkLicenseKey>
                <tags:sort column="${SERIALNUMBER}"/>
                <th><i:inline key=".ipaddress"/></th>
                <tags:sort column="${FIRMWAREVERSION}"/>
                <tags:sort column="${LASTCOMMUNICATION}"/>
                <th><i:inline key=".detail.dataCollection.title"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach items="${gateways}" var="gateway">
                <cti:url var="detailUrl" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
                <tr data-gateway="${gateway.paoIdentifier.paoId}" data-loaded="${not empty gateway.data}">
                    <td>
                        <c:if test="${notesList.contains(gateway.paoIdentifier.paoId)}">
                            <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                            <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${gateway.paoIdentifier.paoId}"/>
                        </c:if>
                    </td>
                    <c:choose>
                        <c:when test="${not empty gateway.data}">
                            <c:set var="data" value="${gateway.data}"/>
                            <c:set var="title"><cti:msg2 key=".connectionStatus.${data.connectionStatus}"/></c:set>
                            <c:set var="clazz" value="${data.connectionStatus == 'CONNECTED' ? 'green' : 'red'}"/>
                            <td class="state-indicator js-gw-conn-status" title="${title}">
                                <span class="state-box ${clazz}"></span>
                            </td>
                            <td class="js-gw-name"><a href="${detailUrl}">${fn:escapeXml(gateway.name)}</a></td>
                            <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                                <td class="js-gw-capacity" width="10%">
                                    <c:choose>
                                        <c:when test="${gateway.dataStreamingSupported}">
                                            <c:set var="color" value="badge-success"/>
                                            <c:if test="${data.dataStreamingLoadingPercent > 100}">
                                                <c:set var="color" value="badge-warning"/>
                                            </c:if>
                                            <c:if test="${data.dataStreamingLoadingPercent > 120}">
                                                <c:set var="color" value="badge-error"/>
                                            </c:if>
                                            <span class="badge ${color} cp js-streaming-capacity" title="<cti:msg2 key=".streamingDetail"/>"><fmt:formatNumber pattern="###.##%" value="${data.dataStreamingLoadingPercent / 100}"/></span>
                                        </c:when>
                                        <c:otherwise>
                                            <i:inline key="yukon.common.unsupported"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </cti:checkLicenseKey>
                            <td class="js-gw-sn">${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                            <td class="js-gw-ip">${fn:escapeXml(gateway.data.ipAddress)}</td>
                            <td class="js-gw-rv" width="10%">
                                <span class="js-gw-rv-text">
                                    ${fn:escapeXml(gateway.data.releaseVersion)}
                                </span>
                            </td>
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
                            <td class="js-gw-last-comm ${clazz}" 
                                title="<cti:formatDate type="DATEHM" value="${gateway.data.lastCommStatusTimestamp}"/>">
                                <i:inline key=".lastCommStatus.${gateway.data.lastCommStatus}"/>
                            </td>
                            <td class="js-gw-data-collection">
                                <div class="dif vat progress">
                                    <c:set var="clazz" value="progress-bar-success"/>
                                    <c:if test="${gateway.totalCompletionLevelWarning}">
                                        <c:set var="clazz" value="progress-bar-warning"/>
                                    </c:if>
                                    <c:if test="${gateway.totalCompletionLevelDanger}">
                                        <c:set var="clazz" value="progress-bar-danger"/>
                                    </c:if>
                                    <div class="progress-bar ${clazz}" 
                                        style="width: ${gateway.totalCompletionPercentage}%"></div>
                                </div>&nbsp;
                                <span class="js-gw-data-collection-percent">
                                    <fmt:formatNumber pattern="###.##%" value="${gateway.totalCompletionPercentage / 100}"/>
                                </span>
                            </td>
                            <td>
                                <cm:dropdown data-name="${fn:escapeXml(gateway.name)}" data-id="${gateway.paoIdentifier.paoId}">
                                    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="INTERACT">
                                        <cm:dropdownOption icon="icon-connect" key=".connect" classes="js-gw-connect"/>
                                    </cti:checkRolesAndProperties>
                                    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="CREATE">
                                        <cm:dropdownOption icon="icon-disconnect" key=".disconnect" classes="js-gw-disconnect"/>
                                    </cti:checkRolesAndProperties>
                                    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="INTERACT">
                                        <li class="divider"></li>
                                    </cti:checkRolesAndProperties>
                                    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="VIEW">
                                        <cm:dropdownOption icon="icon-table-row-insert" key=".collectData" classes="js-gw-collect-data"/>
                                    </cti:checkRolesAndProperties>
                                </cm:dropdown>
                            </td>
                        </c:when>
                        <c:otherwise>
                            <td><cti:icon icon="icon-loading-bars"/></td>
                            <td><a href="${detailUrl}">${fn:escapeXml(gateway.name)}</a></td>
                            <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
                                <td><cti:icon icon="icon-loading-bars"/></td>
                            </cti:checkLicenseKey>
                            <td>${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</td>
                            <td><cti:icon icon="icon-loading-bars"/></td>
                            <td><cti:icon icon="icon-loading-bars"/></td>
                            <td><cti:icon icon="icon-loading-bars"/></td>
                            <td><cti:icon icon="icon-loading-bars"/></td>
                            <td></td>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:msgScope>