<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewGatewayDataSimulator">
    <div id="data-parameters-popup" class="dn" data-title="Gateway Data Simulation Parameters" data-width="800">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Always Return GWY-800 Model">
                The simulator will ignore the gateway model in the request, and return a GWY-800 (a.k.a. Gateway 2.0). 
                When Yukon receives a response for a gateway 1.5, but the response model is set to GWY-800, Yukon
                automatically changes the old gateway to a GWY-800.
            </tags:nameValue>
            <tags:nameValue name="Data Streaming Loading">
                This represents the percentage of total data streaming capacity that is being used by the gateway.
            </tags:nameValue>
            <tags:nameValue name="# of NOT ready nodes">
                This represents the number of nodes that the gateway is aware of, but are not in the "ready" state. The
                ready and not-ready node counts are combined for the gateway total node count.
            </tags:nameValue>
            <tags:nameValue name="# of ready nodes">
                This represents the number of nodes that the gateway is aware of that are in the "ready" state. The
                ready and not-ready node counts are combined for the gateway total node count.
            </tags:nameValue>
            <tags:nameValue name="Failsafe Mode">
                Report all gateways as being in "failsafe" mode.
            </tags:nameValue>
            <tags:nameValue name="Connection Status">
                Report all gateways as either being Connected, Disconnected.
            </tags:nameValue>
        </tags:nameValueContainer>
    </div>
    
    <div id="update-parameters-popup" class="dn" data-title="Gateway Update Simulation Parameters" data-width="800">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Update Result for Create">
                The result to return for gateway create operations.
            </tags:nameValue>
            <tags:nameValue name="Update Result for Edit">
                The result to return for gateway edit operations.
            </tags:nameValue>
            <tags:nameValue name="Update Result for Delete">
                The result to return for gateway delete operations.
            </tags:nameValue>
            <tags:nameValue name="Update Result for Ipv6 Prefix">
                The result to return for ipv6 prefix update operation.
            </tags:nameValue>
        </tags:nameValueContainer>
    </div>
    
    <div id="cert-parameters-popup" class="dn" data-title="Gateway Certificate Upgrade Simulation Parameters" data-width="800">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Ack Type">
                Sent as the initial reply (acknowledgement that the message was received by Network Manager).
            </tags:nameValue>
            <tags:nameValue name="Update Status Type">
                Sent as the final reply, determining whether the request has succeeded or failed.
            </tags:nameValue>
        </tags:nameValueContainer>
    </div>
    
    <div id="firmware-version-popup" class="dn" data-title="Gateway Firmware Update Server Simulation Parameters" data-width="800">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Version">
                The firmware version string reported as available on the update server (if the reply type is "success").
            </tags:nameValue>
            <tags:nameValue name="Reply Type">
                Determines whether the operations are successful or encounter an "error".
            </tags:nameValue>
        </tags:nameValueContainer>
    </div>
    
    <div id="firmware-update-popup" class="dn" data-title="Gateway Firmware Update Simulation Parameters" data-width="800">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Update Result Type">
                Determines whether the update operations are successful or encounter an "error".
            </tags:nameValue>
        </tags:nameValueContainer>
    </div>
    
    <div id="tabs">
        <ul>
            <li><a href="#simulators">Simulators</a></li>
            <li><a href="#tools">Tools</a></li>
        </ul>
        <div id="simulators" class="clearfix">
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th>Data Type</th>
                        <th>Status</th>
                        <th>Actions</th>
                        <th>Parameters</th>
                        <th>Description</th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <tr>
                        <form action="enableGatewayDataReply" method="POST">
                            <cti:csrfToken/>
                            <td>Gateway Data</td>
                            <td>
                                <c:if test="${autoDataReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not autoDataReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoDataReplyActive}">
                                    <cti:button label="Disable" type="button" href="disableGatewayDataReply"/>
                                </c:if>
                                <c:if test="${not autoDataReplyActive}">
                                    <cti:button label="Enable" type="submit"/>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoDataReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Data Streaming Loading">
                                            <input type="text" name="currentDataStreamingLoading" style="width: 40px;" value="${dataSettings.currentDataStreamingLoading}" disabled="true"/>%
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Always Return GWY-800 Model">
                                            <tags:checkbox path="dataSettings.returnGwy800Model" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="# of NOT ready nodes">
                                            <input name="numberOfNotReadyNodes" type="text" style="width: 45px;" value="${dataSettings.numberOfNotReadyNodes}" disabled="true">
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="# of ready nodes">
                                            <input name="numberOfReadyNodes" type="text" style="width: 45px;" value="${dataSettings.numberOfReadyNodes}" disabled="true">
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Failsafe Mode">
                                            <tags:checkbox path="dataSettings.failsafeMode" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Connection Status">
                                            <select name="connectionStatus" disabled>
                                                <c:forEach var="connectionType" items="${connectionTypes}">
                                                    <c:set var="selected" value=""/>
                                                    <c:if test="${connectionType eq dataSettings.connectionStatus}">
                                                        <c:set var="selected" value="selected"/>
                                                    </c:if>
                                                    <option value="${connectionType}" ${selected}>${connectionType}</option>
                                                </c:forEach>
                                            </select>                                        
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoDataReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Data Streaming Loading">
                                            <input type="text" name="currentDataStreamingLoading" style="width: 40px;" value="${dataSettings.currentDataStreamingLoading}"/>%
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Always Return GWY-800 Model">
                                            <tags:checkbox path="dataSettings.returnGwy800Model"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="# of NOT ready nodes">
                                            <input name="numberOfNotReadyNodes" type="text" style="width: 45px;" value="${dataSettings.numberOfNotReadyNodes}">
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="# of ready nodes">
                                            <input name="numberOfReadyNodes" type="text" style="width: 45px;" value="${dataSettings.numberOfReadyNodes}">
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Failsafe Mode">
                                            <tags:checkbox path="dataSettings.failsafeMode"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Connection Status">
                                            <select name="connectionStatus">
                                                <c:forEach var="connectionType" items="${connectionTypes}">
                                                    <c:set var="selected" value=""/>
                                                    <c:if test="${connectionType eq dataSettings.connectionStatus}">
                                                        <c:set var="selected" value="selected"/>
                                                    </c:if>
                                                    <option value="${connectionType}" ${selected}>${connectionType}</option>
                                                </c:forEach>
                                            </select>                                        
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#data-parameters-popup" label="Parameter Details"/>
                            </td>
                            <td>
                                The simulator automatically processes GatewayDataRequests and sends GatewayDataResponses.<br>
                            </td>
                        </form>
                    </tr>
                    <tr>
                        <form action="enableGatewayUpdateReply" method="POST">
                            <cti:csrfToken/>
                            <td>Gateway Create/Edit/Delete</td>
                            <td>
                                <c:if test="${autoUpdateReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not autoUpdateReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoUpdateReplyActive}">
                                    <cti:button label="Disable" type="button" href="disableGatewayUpdateReply"/>
                                </c:if>
                                <c:if test="${not autoUpdateReplyActive}">
                                    <cti:button label="Enable" type="submit"/>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoUpdateReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Result for Create">
                                            <tags:selectWithItems path="updateSettings.createResult"
                                                items="${gatewayUpdateResultTypes}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Edit">
                                            <tags:selectWithItems path="updateSettings.editResult"
                                                items="${gatewayUpdateResultTypes}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Delete">
                                            <tags:selectWithItems path="updateSettings.deleteResult"
                                                items="${gatewayUpdateResultTypes}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Ipv6 Prefix">
                                            <tags:selectWithItems path="updateSettings.ipv6PrefixUpdateResult"
                                                items="${gatewayConfigResultTypes}" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoUpdateReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Result for Create">
                                            <tags:selectWithItems path="updateSettings.createResult"
                                                items="${gatewayUpdateResultTypes}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Edit">
                                            <tags:selectWithItems path="updateSettings.editResult"
                                                items="${gatewayUpdateResultTypes}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Delete">
                                            <tags:selectWithItems path="updateSettings.deleteResult"
                                                items="${gatewayUpdateResultTypes}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Ipv6 Prefix">
                                            <tags:selectWithItems path="updateSettings.ipv6PrefixUpdateResult"
                                                items="${gatewayConfigResultTypes}"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#update-parameters-popup" label="Parameter Details"/>
                            </td>
                            <td>
                                The simulator automatically processes GatewayCreateRequests, GatewayEditRequests, and GatewayDeleteRequests, and returns GatewayUpdateResponses.
                            </td>
                        </form>
                    </tr>
                    <tr>
                        <form action="enableGatewayCertificateReply" method="POST">
                            <cti:csrfToken/>
                            <td>Certificate Upgrade</td>
                            <td>
                                <c:if test="${autoCertificateReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not autoCertificateReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoCertificateReplyActive}">
                                    <cti:button label="Disable" type="button" href="disableGatewayCertificateReply"/>
                                </c:if>
                                <c:if test="${not autoCertificateReplyActive}">
                                    <cti:button label="Enable" type="submit"/>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoCertificateReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Status Type">
                                            <tags:selectWithItems path="certificateSettings.deviceUpdateStatus"
                                                items="${acceptedUpdateStatusTypes}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Ack Type">
                                            <tags:selectWithItems path="certificateSettings.ackType"
                                                items="${ackTypes}" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoCertificateReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Status Type">
                                            <tags:selectWithItems path="certificateSettings.deviceUpdateStatus"
                                                items="${acceptedUpdateStatusTypes}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Ack Type">
                                            <tags:selectWithItems path="certificateSettings.ackType"
                                                items="${ackTypes}"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#cert-parameters-popup" label="Parameter Details"/>
                            </td>
                            <td>
                                The simulator automatically processes RfnGatewayUpgradeRequests and sends RfnGatewayUpgradeResponses.<br>
                            </td>
                        </form>
                    </tr>
                    <tr>
                        <form action="enableFirmwareVersionReply" method="POST">
                            <cti:csrfToken/>
                            <td>Firmware Version</td>
                            <td>
                                <c:if test="${autoFirmwareVersionReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not autoFirmwareVersionReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoFirmwareVersionReplyActive}">
                                    <cti:button label="Disable" type="button" href="disableFirmwareVersionReply"/>
                                </c:if>
                                <c:if test="${not autoFirmwareVersionReplyActive}">
                                    <cti:button label="Enable" type="submit"/>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoFirmwareVersionReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Version">
                                            <input type="text" name="version" value="${firmwareVersionSettings.version}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="ReplyType">
                                            <tags:selectWithItems path="firmwareVersionSettings.result"
                                                items="${firmwareVersionReplyTypes}" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                
                                <c:if test="${not autoFirmwareVersionReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Version">
                                            <input type="text" name="version" value="${firmwareVersionSettings.version}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="ReplyType">
                                            <tags:selectWithItems path="firmwareVersionSettings.result"
                                                items="${firmwareVersionReplyTypes}"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#firmware-version-popup" label="Parameter Details"/>
                            </td>
                            <td>
                                The simulator automatically processes RfnUpdateServerAvailableVersionRequests and sends an RfnUpdateServerAvailableVersionResponse.
                            </td>
                        </form>
                    </tr>
                    <tr>
                        <form action="enableGatewayFirmwareReply" method="POST">
                            <cti:csrfToken/>
                            <td>Firmware Upgrade</td>
                            <td>
                                <c:if test="${autoFirmwareReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not autoFirmwareReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoFirmwareReplyActive}">
                                    <cti:button label="Disable" type="button" href="disableGatewayFirmwareReply"/>
                                </c:if>
                                <c:if test="${not autoFirmwareReplyActive}">
                                    <cti:button label="Enable" type="submit"/>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${autoFirmwareReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Result Type">
                                            <tags:selectWithItems path="firmwareSettings.resultType"
                                                items="${firmwareUpdateResultTypes}" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoFirmwareReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Result Type">
                                            <tags:selectWithItems path="firmwareSettings.resultType"
                                                items="${firmwareUpdateResultTypes}"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#firmware-update-popup" label="Parameter Details"/>
                            </td>
                            <td>
                                The simulator automatically processes RfnGatewayFirmwareUpdateRequests and sends an RfnGatewayFirmwareUpdateResponse.
                            </td>
                        </form>
                    </tr>
                    <tr>
                        <c:set var="maxSimulators" value="5"/>
                        <td>All Simulators</td>
                        <td>
                            <c:set var="clazz" value="${numberOfSimulatorsRunning eq 0 ? 'error' : 'pending'}"/>
                            <div class="user-message ${clazz}">${numberOfSimulatorsRunning}/${maxSimulators}</div>
                        </td>
                        <td colspan="2">
                            <c:if test="${numberOfSimulatorsRunning lt maxSimulators}">
                                <cti:button id="enable-all" label="Enable All" type="button"/>
                            </c:if>
                            <c:if test="${numberOfSimulatorsRunning gt 0}">
                                <cti:button label="Disable All" type="button" href="disableAllGatewaySimulators"/>
                            </c:if>
                            <div class="button-group button-group-toggle">
                                <div class="js-sim-startup" data-simulator-type="GATEWAY">
                                    <cti:button nameKey="runSimulatorOnStartup.automatic" classes="yes enable-startup"/>
                                    <cti:button nameKey="runSimulatorOnStartup.manual" classes="no disable-startup"/>
                                </div>  
                            </div>
                        </td>
                        <td>Activates all gateway simulator functions with either the user's saved parameters or the default parameter values if the user has never started a given simulator function.</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div id="tools" class="clearfix">
            <tags:sectionContainer title="Create New Gateway">
                Queue an unsolicited gateway archive message, as though it were coming from Network Manager (which will create a new gateway device in Yukon).
                <br><br>
                
                <form action="createNewGateway">
                    <cti:csrfToken/>
                    <tags:nameValueContainer tableClass="natural-width">
                        <tags:nameValue name="Serial Number">
                            <input name="serial" value="7500000019">
                        </tags:nameValue>
                        <tags:nameValue name="Gateway Model">
                            <select name="gatewayModel" >
                                <c:forEach var="gatewayModelType" items="${gatewayModelTypes}">
                                    <option value="${gatewayModelType}"}>${gatewayModelType}</option>
                                </c:forEach>
                            </select> 
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <cti:button label="Create" type="submit"/>
                </form>
            </tags:sectionContainer>
            <tags:sectionContainer title="Send Gateway Response Data">
                Queue an unsolicited gateway response data message and also create new gateway when data message is received continuously for more than 2 hours 
                and if that gateway doesn't exist in Yukon.

                <br><br>
                
                <form action="sendGatewayDataResponse">
                    <cti:csrfToken/>
                    <tags:nameValueContainer tableClass="natural-width">
                        <tags:nameValue name="Serial Number">
                            <input name="serial" value="7500000019">
                        </tags:nameValue>
                        <tags:nameValue name="Gateway Model">
                            <select name="gatewayModel" >
                                <c:forEach var="gatewayModelType" items="${gatewayModelTypes}">
                                    <option value="${gatewayModelType}"}>${gatewayModelType}</option>
                                </c:forEach>
                            </select> 
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <cti:button label="Send" type="submit"/>
                </form>
            </tags:sectionContainer>
            <tags:sectionContainer title="Delete Gateway">
                 Queue an unsolicited gateway delete message, as though it were coming from Network Manager (which will delete gateway device in Yukon).
                <br><br>
                <form action="deleteGateway">
                    <cti:csrfToken/>
                    <tags:nameValueContainer tableClass="natural-width">
                        <tags:nameValue name="Serial Number">
                            <input name="serial" value="7500000019">
                        </tags:nameValue>
                        <tags:nameValue name="Gateway Model">
                            <select name="gatewayModel" >
                                <c:forEach var="gatewayModelType" items="${gatewayModelTypes}">
                                    <option value="${gatewayModelType}"}>${gatewayModelType}</option>
                                </c:forEach>
                            </select> 
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <cti:button label="Delete" type="submit"/>
                </form>
            </tags:sectionContainer>
        </div>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.gatewayDataSimulator.events.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js"/>
</cti:standardPage>