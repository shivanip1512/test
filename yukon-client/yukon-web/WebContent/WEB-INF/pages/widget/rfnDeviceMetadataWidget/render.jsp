<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:msg2 key=".showAll.title" var="metadataShowAllDialogTitle"/>
<div class="dn" id="metadata_showAll" title="${metadataShowAllDialogTitle}">
    <tags:nameValueContainer altRowOn="true">
        <c:forEach var="pair" items="${metadata}">
            <%@ include file="metadataRow.jspf" %>
        </c:forEach>
        <c:if test="${!empty wifiSuperMeterData}">
            <cti:msgScope paths="yukon.web.widgets.RfnDeviceMetadataWidget.WifiSuperMeterData">
                <cti:msg2 var="label" key=".configuredApBssid"/>
                <tags:nameValue name="${label}">${fn:escapeXml(wifiSuperMeterData.configuredApBssid)}</tags:nameValue>
                <cti:msg2 var="label" key=".connectedApBssid"/>
                <tags:nameValue name="${label}">${fn:escapeXml(wifiSuperMeterData.connectedApBssid)}</tags:nameValue>
                <cti:msg2 var="label" key=".apSsid"/>
                <tags:nameValue name="${label}">${fn:escapeXml(wifiSuperMeterData.apSsid)}</tags:nameValue>
                <cti:msg2 var="label" key=".securityType"/>
                <cti:msg2 var="type" key=".securityType.${fn:escapeXml(wifiSuperMeterData.securityType)}"/>
                <tags:nameValue name="${label}">${type}</tags:nameValue>
                <cti:msg2 var="label" key=".virtualGatewayIpv6Address"/>
                <tags:nameValue name="${label}">${fn:escapeXml(wifiSuperMeterData.virtualGatewayIpv6Address)}</tags:nameValue>
            </cti:msgScope>
        </c:if>
    </tags:nameValueContainer>
</div>

<c:choose>
    <c:when test="${not empty error}">
        <i:inline key="${error}"/>
    </c:when>
    <c:otherwise>
        <div id="metadata_csrView">
            <tags:nameValueContainer altRowOn="true">
                <c:forEach var="pair" items="${csrMetadata}">
                    <%@ include file="metadataRow.jspf" %>
                </c:forEach>
            </tags:nameValueContainer>
        </div>
    </c:otherwise>
</c:choose>

<div class="action-area">
    <c:if test="${showAll}">
        <a href="javascript:void(0);" class="showAll fl" id="showAll" data-popup="#metadata_showAll"><i:inline key=".showAll.label"/></a>
    </c:if>
    <tags:widgetActionRefresh method="render" nameKey="refresh" icon="icon-arrow-refresh"/>
</div>