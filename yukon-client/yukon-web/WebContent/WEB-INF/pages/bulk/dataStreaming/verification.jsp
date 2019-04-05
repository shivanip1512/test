<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="yukon.web.modules.tools.bulk.dataStreaming.verification,yukon.web.modules.tools.bulk.dataStreaming">

    <cti:flashScopeMessages/>

    <input type="hidden" id="verificationPassed" value="${verificationInfo.verificationPassed}"/>

    <div class="stacked notes">
        <cti:uniqueIdentifier var="id" />
        <strong><i:inline key=".configuration" />:</strong>&nbsp; 
        <a href="javascript:void(0);" data-popup="#data-streaming-popup-${id}">${fn:escapeXml(verificationInfo.configuration.name)}</a>
    </div>

    <cti:url var="assignUrl"  value="/bulk/dataStreaming/verification" />
    <form:form id="verificationForm" method="post" modelAttribute="verificationInfo" action="${assignUrl}">
        <cti:csrfToken/>
        
        <c:forEach var="failDevice" items="${verificationInfo.failedVerificationDevices}" varStatus="status">
            <form:hidden path="failedVerificationDevices[${status.index}]"/>
        </c:forEach>
        
        <cti:deviceCollection deviceCollection="${deviceCollection}" />
        <form:hidden path="configuration.id" />
        <c:forEach var="att" items="${configuration.attributes}" varStatus="status">
            <form:hidden path="configuration.attributes[${status.index}].attribute"/>
            <form:hidden path="configuration.attributes[${status.index}].attributeOn"/>
            <form:hidden path="configuration.attributes[${status.index}].interval"/>
        </c:forEach>
        <c:forEach var="exception" items="${verificationInfo.exceptions}">
            <div class="user-message error"><i:inline key="${exception}"/></div>
        </c:forEach>

        <tags:sectionContainer2 nameKey="devicesUnsupported">
            <div class="scroll-md">
                <c:set var="allDevicesSupported" value="true"/>
                <c:forEach var="deviceUnsupported" items="${verificationInfo.deviceUnsupported}">
                    <c:if test="${deviceUnsupported.deviceIds.size() > 0}">
                        <c:set var="allDevicesSupported" value="false"/>
                        <div><cti:icon icon="icon-error"/>${deviceUnsupported.detail}<tags:selectedDevicesPopup deviceCollection="${deviceUnsupported.deviceCollection}"/></div>
                    </c:if>
                </c:forEach>
                <c:if test="${allDevicesSupported}">
                    <div><cti:icon icon="icon-accept"/><i:inline key=".allDevicesSupported"/></div>
                </c:if>
            </div>
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="gatewayImpact">
            <div class="scroll-md">
                <c:forEach var="gateway" varStatus="status" items="${verificationInfo.gatewayLoadingInfo}">
                    <c:set var="msgIcon" value="icon-accept"/>
                    <c:if test="${gateway.proposedPercent >= 100}">
                        <c:set var="msgIcon" value="icon-exclamation"/>
                    </c:if>
                    <div><cti:icon icon="${msgIcon}"/>${gateway.detail}</div>
                </c:forEach>
            </div>
        </tags:sectionContainer2>

        <div class="PT10"><b>
            <i:inline key=".message"/>
        </b></div>

    </form:form>
    
    <div id="data-streaming-popup-${id}" data-width="400" data-title="<cti:msg2 key=".configuration"/>" class="dn">
        <c:set var="config" value="${verificationInfo.configuration}"/>
        <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
    </div>
    
</cti:msgScope>