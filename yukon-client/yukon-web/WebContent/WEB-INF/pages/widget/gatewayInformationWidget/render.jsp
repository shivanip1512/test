<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<cti:msgScope paths="modules.operator.gateways.detail,modules.operator.gateways,modules.operator">

<%-- Edit Popup --%>
<cti:url var="editUrl" value="/widget/gatewayInformationWidget/edit">
    <cti:param name="deviceId" value="${gateway.paoIdentifier.paoId}"/>
    <cti:param name="shortName" value="gatewayInformationWidget"/>
</cti:url>
<div id="gateway-edit-popup" data-dialog class="dn" data-title="<cti:msg2 key=".edit.title"/>"
        data-url="${editUrl}"
        data-id="${gateway.paoIdentifier.paoId}"
        data-width="580" 
        data-event="yukon:assets:gateway:save" 
        data-load-event="yukon:assets:gateway:edit:load" 
        data-ok-text="<cti:msg2 key="components.button.save.label"/>"></div>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".name" valueClass="js-gw-name">${fn:escapeXml(gateway.name)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".serialNumber" valueClass="js-gw-sn">${fn:escapeXml(gateway.rfnIdentifier.sensorSerialNumber)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".hardwareVersion" valueClass="js-gw-hw-version">${gateway.data.hardwareVersion}</tags:nameValue2>
        <tags:nameValue2 nameKey=".softwareVersion" valueClass="js-gw-sw-version">${gateway.data.softwareVersion}</tags:nameValue2>
        <tags:nameValue2 nameKey=".upperStackVersion" valueClass="js-gw-us-version">${gateway.data.upperStackVersion}</tags:nameValue2>
        <tags:nameValue2 nameKey=".radioVersion" valueClass="js-gw-radio-version">${gateway.data.radioVersion }</tags:nameValue2>
        <tags:nameValue2 nameKey=".releaseVersion" valueClass="js-gw-release-version">${gateway.data.releaseVersion}</tags:nameValue2>
        <tags:nameValue2 nameKey=".versionConflicts">
            <c:if test="${empty gateway.data.versionConflicts}">
                <span class="empty-list js-gw-version-conflicts"><i:inline key="yukon.common.none"/></span>
            </c:if>
            <c:if test="${not empty gateway.data.versionConflicts}">
                <span class="error js-gw-version-conflicts">
                    <c:forEach var="conflict" items="${gateway.data.versionConflicts}">
                        <i:inline key=".conflictType.${conflict}"/>,&nbsp;
                    </c:forEach>
                </span>
            </c:if>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".appMode">
            <c:set var="clazz" value="green"/>
            <c:if test="${gateway.appModeNonNormal}">
                <c:set var="clazz" value="error"/>
            </c:if>
            <span class="${clazz}" valueClass="js-gw-app-mode">
                <c:if test="${not empty gateway.data}"><i:inline key=".appMode.${gateway.data.mode}"/></c:if>
            </span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".streamingCapacity" data-gateway="${gateway.paoIdentifier.paoId}">
            <c:set var="color" value="badge-success"/>
            <c:if test="${gateway.data.dataStreamingLoadingPercent > 100}">
                <c:set var="color" value="badge-error"/>
            </c:if>
            <c:set var="linkClasses" value=""/>
            <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                <c:set var="linkClasses" value="cp js-streaming-capacity"/>
            </cti:checkRolesAndProperties>
            <span class="badge ${color} ${linkClasses}" title="<cti:msg2 key=".streamingDetail"/>"><fmt:formatNumber pattern="###.##%" value="${gateway.data.dataStreamingLoadingPercent / 100}"/></span>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
    <cti:checkRolesAndProperties value="INFRASTRUCTURE_CREATE_AND_UPDATE">
        <div class="buffered clearfix">
            <c:set var="clazz" value="${empty gateway.data ? 'dn' : ''}"/>
            <cti:button nameKey="edit" icon="icon-pencil" data-popup="#gateway-edit-popup" 
                classes="fr ${clazz} js-edit"/>
        </div>
    </cti:checkRolesAndProperties>
    
</cti:msgScope>

<cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>
<cti:includeScript link="/resources/js/pages/yukon.assets.gateway.shared.js"/>
