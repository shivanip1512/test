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
            <c:choose>
                <c:when test="${verificationInfo.configuration != null}">
                    <a href="javascript:void(0);" data-popup="#data-streaming-popup">${fn:escapeXml(verificationInfo.configuration.name)}</a>
                </c:when>
                <c:otherwise>
                    <i:inline key=".removeConfiguration"/>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="page-action-area">
            <cti:url var="assignUrl"  value="/bulk/dataStreaming/verification" />
            <form:form id="configureForm" method="post" commandName="verificationInfo" action="${assignUrl}">
                <cti:csrfToken/>
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                <form:hidden path="configuration.id" />

                <c:forEach var="deviceUnsupported" varStatus="status" items="${verificationInfo.deviceUnsupported}">
                    <div class="user-message warning">${deviceUnsupported.detail}<tags:selectedDevicesPopup deviceCollection="${deviceUnsupported.deviceCollection}"/></div>
                </c:forEach>
                
                <tags:sectionContainer title="Gateway Impact">
                    <div class="scroll-md">
                        <c:set var="sendDisabled" value="false"/>
                        <c:forEach var="gateway" varStatus="status" items="${verificationInfo.gatewayLoadingInfo}">
                            <c:set var="msgIcon" value="icon-accept"/>
                            <c:if test="${gateway.proposedPercent >= 100}">
                                <c:set var="sendDisabled" value="true"/>
                                <c:set var="msgIcon" value="icon-exclamation"/>
                            </c:if>
                            <div><cti:icon icon="${msgIcon}"/>${gateway.detail}</div>
                        </c:forEach>
                    </div>
                </tags:sectionContainer>
        
                <div style="margin-top:50px;"><b>
                    <c:choose>
                        <c:when test="${verificationInfo.configuration != null}">
                            <i:inline key=".message"/>
                        </c:when>
                        <c:otherwise>
                            <i:inline key=".removeMessage"/>
                        </c:otherwise>
                    </c:choose>
                </b></div>
                
                <div class="page-action-area">
                    <cti:button nameKey="back" href="javascript:window.history.back()" name="backButton" classes="action" />
                    <cti:button disabled="${sendDisabled}" nameKey="send" type="submit" name="sendButton" classes="primary action" />
                    <c:if test="${sendDisabled}">
                        <div class="warning">
                            <i:inline key=".gatewayOverloaded"/>
                        </div>
                    </c:if>
                </div>
            </form:form>
        </div>
    </tags:bulkActionContainer>
    
    <div data-dialog id="data-streaming-popup" data-width="400" data-title="<cti:msg2 key=".configuration"/>" class="dn">
        <c:set var="config" value="${verificationInfo.configuration}"/>
        <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
    </div>
        
</cti:standardPage>