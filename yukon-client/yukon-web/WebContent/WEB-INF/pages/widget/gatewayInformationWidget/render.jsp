<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways.detail,modules.operator.gateways,modules.operator,yukon.common">

    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="UPDATE">
        <%-- Edit Popup --%>
        <cti:url var="editUrl" value="/widget/gatewayInformationWidget/edit">
            <cti:param name="deviceId" value="${gateway.paoIdentifier.paoId}"/>
            <cti:param name="shortName" value="gatewayInformationWidget"/>
        </cti:url>
        <div id="gateway-edit-popup"
             data-dialog class="dn"
             data-title="<cti:msg2 key=".edit.title"/>"
             data-url="${editUrl}"
             data-id="${gateway.paoIdentifier.paoId}"
             data-width="680" 
             data-height="600"
             data-event="yukon:assets:gateway:save" 
             data-load-event="yukon:assets:gateway:edit:load" 
             data-ok-text="<cti:msg2 key="components.button.save.label"/>"></div>
    

        <%-- Configure Popup --%>
        <cti:url var="configureUrl" value="/widget/gatewayInformationWidget/configure">
            <cti:param name="deviceId" value="${gateway.paoIdentifier.paoId}"/>
            <cti:param name="shortName" value="gatewayInformationWidget"/>
        </cti:url>
        <div id="gateway-configure-popup" data-dialog class="dn" data-title="<cti:msg2 key=".configure.title"/>"
             data-url="${configureUrl}"
             data-id="${gateway.paoIdentifier.paoId}"
             data-width="580" 
             data-event="yukon:assets:gateway:configure" 
             data-load-event="yukon:assets:gateway:configure:load" 
             data-ok-text="<cti:msg2 key="components.button.save.label"/>"></div>
    </cti:checkRolesAndProperties>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".name" valueClass="js-gw-name">${fn:escapeXml(gateway.name)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".type">
            <tags:paoType yukonPao="${gateway}" showLink="false"/>
        </tags:nameValue2>
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
        <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
            <tags:nameValue2 nameKey=".streamingCapacity" data-gateway="${gateway.paoIdentifier.paoId}">
                <c:choose>
                    <c:when test="${gateway.dataStreamingSupported}">
                        <c:set var="color" value="badge-success"/>
                        <c:if test="${gateway.data.dataStreamingLoadingPercent > 100}">
                            <c:set var="color" value="badge-${gateway.data.dataStreamingLoadingPercent > 120 ? 'error':'warning'}"/>
                        </c:if>
                        <span class="badge ${color} cp js-streaming-capacity" title="<cti:msg2 key=".streamingDetail"/>"><fmt:formatNumber pattern="###.##%" value="${gateway.data.dataStreamingLoadingPercent / 100}"/></span>
                        <cti:attributeResolver pao="${gateway}" attribute="${streamingCapacity}" var="pointId"/>
                        <cti:url var="valuesUrl" value="/meter/historicalReadings/view">
                            <cti:param name="pointId" value="${pointId}"/>
                            <cti:param name="attribute" value="${streamingCapacity}"/>
                        </cti:url>
                        <a href="javascript:void(0);" data-popup="#load-popup"><i:inline key=".streamingCapacityHistory"/></a>
                        <div id="load-popup" data-width="500" data-height="400" data-url="${valuesUrl}"></div>
                    </c:when>
                    <c:otherwise>
                        <i:inline key="yukon.common.unsupported"/>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
        </cti:checkLicenseKey>
        <%-- <c:if test="${not empty gateway.data.ipv6Prefix}">
            <tags:nameValue2 nameKey=".gateways.ipv6prefix" valueClass="js-gw-ipv6">${fn:escapeXml(gateway.data.ipv6Prefix)}</tags:nameValue2>
        </c:if> --%>
    </tags:nameValueContainer2>
    
    <cti:checkRolesAndProperties value="MANAGE_INFRASTRUCTURE" level="UPDATE">
        <div class="buffered clearfix fr">
            <c:set var="clazz" value="${empty gateway.data ? 'dn' : ''}"/>
            <cti:button nameKey="edit" icon="icon-pencil" data-popup="#gateway-edit-popup" 
                classes="${clazz} js-edit"/>
            <%-- <c:if test="${gateway.ipv6Supported}">
                <cti:button nameKey="configure" icon="icon-cog-edit" data-popup="#gateway-configure-popup" 
                    classes="${clazz}"/>
            </c:if> --%>
        </div>
    </cti:checkRolesAndProperties>
    
</cti:msgScope>

<cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>
