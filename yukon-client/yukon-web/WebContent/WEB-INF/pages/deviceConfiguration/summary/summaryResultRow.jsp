<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.tools.configs.summary">
    <c:set var="deviceId" value="${detail.device.paoIdentifier.paoId}"/>
    <td><cti:paoDetailUrl yukonPao="${detail.device.paoIdentifier}" newTab="true">${fn:escapeXml(detail.device.name)}</cti:paoDetailUrl></td>
    <td class="wsnw">${detail.device.paoIdentifier.paoType.paoTypeName}</td>
    <td>
        <c:choose>
            <c:when test="${detail.deviceConfig != null}">
                <cti:url var="configUrl" value="/deviceConfiguration/config/view?configId=${detail.deviceConfig.configurationId}"/>
                <a href="${configUrl}">${fn:escapeXml(detail.deviceConfig.name)}</a>
            </c:when>
            <c:otherwise>
                <i:inline key=".configurations.unassigned"/>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <c:choose>
            <c:when test="${detail.action != null}">
                <i:inline key=".actionType.${detail.action}"/>
            </c:when>
            <c:otherwise>
                ${naText}
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <c:choose>
            <c:when test="${detail.status == null}">
                ${naText}
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${detail.displayFailurePopup}">
                        <div class="dn js-failure-${deviceId}" data-dialog data-destroy-dialog-on-close data-cancel-omit="true" 
                            data-title="<cti:msg2 key=".failure"/>" data-width="600" 
                            data-url="<cti:url value="/deviceConfiguration/summary/${detail.errorCode}/displayError"/>"></div>
                        <a href="javascript:void(0);" data-popup=".js-failure-${deviceId}"><i:inline key=".statusType.${detail.status}"/></a>
                    </c:when>
                    <c:otherwise>
                        <i:inline key=".statusType.${detail.status}"/>
                        <c:if test="${detail.inProgress}">
                            <cti:dataUpdaterCallback function="yukon.deviceConfig.summary.refreshCheck(${deviceId})"
                                isInProgress="DEVICE_CONFIG_SUMMARY/${deviceId}/IS_IN_PROGRESS"/>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <c:choose>
            <c:when test="${detail.state == null}">
                ${naText}
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${detail.displayOutOfSyncPopup}">
                        <div class="dn js-outofsync-${deviceId}" data-dialog data-destroy-dialog-on-close data-cancel-omit="true" 
                            data-title="<cti:msg2 key=".outOfSync"/>" data-width="600"
                            data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/outOfSync"/>"></div>
                        <a href="javascript:void(0);" data-popup=".js-outofsync-${deviceId}"><i:inline key="${detail.state.formatKey}"/></a>
                    </c:when>
                    <c:otherwise>
                        <i:inline key="${detail.state.formatKey}"/>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </td>
    <td><cti:formatDate type="BOTH" value="${detail.actionStart}" nullText="${naText}"/></td>
    <td><cti:formatDate type="BOTH" value="${detail.actionEnd}" nullText="${naText}"/></td>
    <td>
        <cm:dropdown icon="icon-cog">
            <cti:msg2 var="historyTitle" key=".viewHistory.title" argument="${detail.device.name}"/>
            <div class="dn js-view-history-${deviceId}" data-dialog data-destroy-dialog-on-close data-cancel-omit="true" 
                data-title="${historyTitle}" data-width="600" data-height="400" data-load-event="yukon:config:viewHistory"
                data-url="<cti:url value="/deviceConfiguration/summary/${deviceId}/viewHistory"/>"></div>
            <cm:dropdownOption key=".viewHistory" data-popup=".js-view-history-${deviceId}" icon="icon-application-view-columns"/>
            <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                <c:set var="uploadClass" value="${detail.displayUpload ? 'js-device-action' : ''}"/>
                <cm:dropdownOption key=".upload" icon="icon-ping" classes="${uploadClass}" data-device-id="${deviceId}"
                    data-action="uploadConfig" disabled="${!detail.displayUpload}"/>
                <c:set var="validateClass" value="${detail.displayValidate ? 'js-device-action' : ''}"/>
                <cm:dropdownOption key=".validate" icon="icon-read" classes="${validateClass}" data-device-id="${deviceId}"
                    data-action="validateConfig" disabled="${!detail.displayValidate}"/>
            </cti:checkRolesAndProperties>
        </cm:dropdown>
    </td>
</cti:msgScope>
