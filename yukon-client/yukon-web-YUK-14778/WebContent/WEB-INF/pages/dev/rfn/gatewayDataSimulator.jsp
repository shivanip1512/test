<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewGatewayDataSimulator">
    <c:if test="${autoDataReplyActive}">
        <div class="user-message pending">
            Simulating Data Replies!
        </div>
    </c:if>
    <c:if test="${autoCertificateReplyActive}">
        <div class="user-message pending">
            Simulating Certificate Replies!
        </div>
    </c:if>
    <c:if test="${autoFirmwareReplyActive}">
        <div class="user-message pending">
            Simulating Firmware Update Replies!
        </div>
    </c:if>
    <c:if test="${autoFirmwareVersionReplyActive}">
        <div class="user-message pending">
            Simulating Firmware Update Server Version Replies!
        </div>
    </c:if>
    
    <tags:sectionContainer title="Create New Gateway">
        Queue an unsolicited gateway archive message, as though it were coming from Network Manager (which will create a new gateway device in Yukon).
        <br><br>
        
        <form action="createNewGateway">
            <cti:csrfToken/>
            <tags:nameValueContainer tableClass="natural-width">
                <tags:nameValue name="Name">
                    <input name="name" value="Fake Gateway">
                </tags:nameValue>
                <tags:nameValue name="Serial Number">
                    <input name="serial" value="7500001337">
                </tags:nameValue>
                <tags:nameValue name="GWY-800">
                    <input name="isGateway2" type="checkbox">
                </tags:nameValue>
            </tags:nameValueContainer>
            <cti:button label="Create" type="submit"/>
        </form>
    </tags:sectionContainer>
    
    <tags:sectionContainer title="Automatic Simulated Data Reply">
        The simulator automatically processes GatewayDataRequests and sends GatewayDataResponses.<br>
        <em>Selecting "always return GWY-800" cause the simulator to ignore the model in the request, and return a GWY-800. This will cause Yukon to automatically change the type of any old gateways.</em>
        <br><br>
        <c:if test="${not autoDataReplyActive}">
            <form action="enableGatewayDataReply" method="POST">
                <cti:csrfToken/>
                <tags:nameValueContainer tableClass="natural-width">
                    <tags:nameValue name="Always Return GWY-800 Model">
                        <input name="alwaysGateway2" type="checkbox">
                    </tags:nameValue>
                </tags:nameValueContainer>
                <cti:button label="Enable Data Reply" type="submit"/>
            </form>
        </c:if>
        <c:if test="${autoDataReplyActive}">
            <cti:button label="Disable Data Reply" type="button" href="disableGatewayDataReply"/>
        </c:if>
    </tags:sectionContainer>
    
    <tags:sectionContainer title="Automatic Simulated Certificate Reply">
        The simulator automatically processes RfnGatewayUpgradeRequests and sends RfnGatewayUpgradeResponses.<br>
        <em>The Ack Type will be sent as the initial reply. The Update Status Type will be sent as the final reply, determining whether the request succeeds or fails.</em>
        <br><br>
        <c:if test="${not autoCertificateReplyActive}">
            <form action="enableGatewayCertificateReply">
                <cti:csrfToken/>
                <tags:nameValueContainer tableClass="natural-width">
                    <tags:nameValue name="Update Status Type">
                        <select name="updateStatus">
                            <c:forEach var="updateStatus" items="${acceptedUpdateStatusTypes}">
                                <option value="${updateStatus}">${updateStatus}</option>
                            </c:forEach>
                        </select>
                    </tags:nameValue>
                    <tags:nameValue name="Ack Type">
                        <select name="ackType">
                            <c:forEach var="ackType" items="${ackTypes}">
                                <option value="${ackType}">${ackType}</option>
                            </c:forEach>
                        </select>
                    </tags:nameValue>
                </tags:nameValueContainer>
                <cti:button label="Enable Certificate Reply" type="submit"/>
            </form>
        </c:if>
        <c:if test="${autoCertificateReplyActive}">
            <cti:button label="Disable Certificate Reply" type="button" href="disableGatewayCertificateReply"/>
        </c:if>
    </tags:sectionContainer>
    
    <tags:sectionContainer title="Automatic Simulated Firmware Server Version Reply">
        The simulator automatically processes RfnUpdateServerAvailableVersionRequests and sends an RfnUpdateServerAvailableVersionResponse.
        <br><br>
        <c:if test="${not autoFirmwareVersionReplyActive}">
            <form action="enableFirmwareVersionReply" method="POST">
                <cti:csrfToken/>
                <tags:nameValueContainer tableClass="natural-width">
                    <tags:nameValue name="Version">
                        <input type="text" name="version" value="1.2.3"/>
                    </tags:nameValue>
                    <tags:nameValue name="ReplyType">
                        <select name="replyType">
                            <c:forEach var="replyType" items="${firmwareVersionReplyTypes}">
                                <option value="${replyType}">${replyType}</option>
                            </c:forEach>
                        </select>
                    </tags:nameValue>
                </tags:nameValueContainer>
                <cti:button label="Enable Firmware Server Version Reply" type="submit"/>
            </form>
        </c:if>
        <c:if test="${autoFirmwareVersionReplyActive}">
            <cti:button label="Disable Firmware Server Version Reply" type="button" href="disableFirmwareVersionReply"/>
        </c:if>
    </tags:sectionContainer>
    
    <tags:sectionContainer title="Automatic Simulated Firmware Upgrade Reply">
        The simulator automatically processes RfnGatewayFirmwareUpdateRequests and sends an RfnGatewayFirmwareUpdateResponse.
        <br><br>
        <c:if test="${not autoFirmwareReplyActive}">
            <form action="enableGatewayFirmwareReply" method="POST">
                <cti:csrfToken/>
                <tags:nameValueContainer tableClass="natural-width">
                    <tags:nameValue name="Update Result Type">
                        <select name="updateResult">
                            <c:forEach var="updateResult" items="${firmwareUpdateResultTypes}">
                                <option value="${updateResult}">${updateResult}</option>
                            </c:forEach>
                        </select>
                    </tags:nameValue>
                </tags:nameValueContainer>
                <cti:button label="Enable Firmware Upgrade Reply" type="submit"/>
            </form>
        </c:if>
        <c:if test="${autoFirmwareReplyActive}">
            <cti:button label="Disable Firmware Upgrade Reply" type="button" href="disableGatewayFirmwareReply"/>
        </c:if>
    </tags:sectionContainer>
    
</cti:standardPage>