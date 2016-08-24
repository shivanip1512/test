<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="tools" page="bulk.dataStreaming.verification">
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.dataStreaming.verification" deviceCollection="${deviceCollection}">
    
        <div class="stacked notes">
         <strong><i:inline key=".configuration"/>:</strong>&nbsp;
         <a href="javascript:void(0);" data-popup="#data-streaming-popup">${fn:escapeXml(verificationInfo.configuration.name)}</a>
        </div>

        <div class="page-action-area">
            <cti:url var="assignUrl"  value="/bulk/dataStreaming/verification" />
            <form:form id="configureForm" method="post" commandName="verificationInfo" action="${assignUrl}">
                <cti:csrfToken/>
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                <form:hidden path="configuration.id" />
                                
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
        
                <div style="margin-top:50px;"><b>
                    <i:inline key=".message"/>
                </b></div>
                
                <div class="page-action-area">
                    <cti:button nameKey="back" href="javascript:window.history.back()" name="backButton" classes="action" />
                    <cti:button disabled="${!verificationInfo.verificationPassed}" nameKey="send" type="submit" name="sendButton" classes="primary action" busy="true"/>
                </div>
            </form:form>
        </div>
    </tags:bulkActionContainer>
    
    <div id="data-streaming-popup" data-width="400" data-title="<cti:msg2 key=".configuration"/>" class="dn">
        <c:set var="config" value="${verificationInfo.configuration}"/>
        <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
    </div>
        
</cti:standardPage>