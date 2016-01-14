<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewGatewayDataSimulator">
    <script type="text/javascript">
    $(function() {$('#tabs').tabs();});
    </script>
    
    <div id="data-parameters-popup" class="dn" data-title="Gateway Data Simulation Parameters" data-width="800">
        <tags:nameValueContainer tableClass="natural-width">
            <tags:nameValue name="Always Return GWY-800 Model">
                The simulator will ignore the gateway model in the request, and return a GWY-800 (a.k.a. Gateway 2.0). 
                When Yukon receives a response for a gateway 1.5, but the response model is set to GWY-800, Yukon
                automatically changes the old gateway to a GWY-800.
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
                                        <tags:nameValue name="Always Return GWY-800 Model">
                                            ${dataSettings.returnGwy800Model}
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoDataReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Always Return GWY-800 Model">
                                            <input name="alwaysGateway2" type="checkbox">
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
                                            ${updateSettings.createResult}
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Edit">
                                            ${updateSettings.editResult}
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Delete">
                                            ${updateSettings.deleteResult}
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoUpdateReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Result for Create">
                                            <select name="createResult">
                                                <c:forEach var="updateResultType" items="${gatewayUpdateResultTypes}">
                                                    <option value="${updateResultType}">${updateResultType}</option>
                                                </c:forEach>
                                            </select>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Edit">
                                            <select name="editResult">
                                                <c:forEach var="updateResultType" items="${gatewayUpdateResultTypes}">
                                                    <option value="${updateResultType}">${updateResultType}</option>
                                                </c:forEach>
                                            </select>
                                        </tags:nameValue>
                                        <tags:nameValue name="Update Result for Delete">
                                            <select name="deleteResult">
                                                <c:forEach var="updateResultType" items="${gatewayUpdateResultTypes}">
                                                    <option value="${updateResultType}">${updateResultType}</option>
                                                </c:forEach>
                                            </select>
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
                        <form action="enableGatewayCertificateReply">
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
                                            ${certificateSettings.deviceUpdateStatus}
                                        </tags:nameValue>
                                        <tags:nameValue name="Ack Type">
                                            ${certificateSettings.ackType}
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoCertificateReplyActive}">
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
                                            ${firmwareVersionSettings.version}
                                        </tags:nameValue>
                                        <tags:nameValue name="ReplyType">
                                            ${firmwareVersionSettings.result}
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                
                                <c:if test="${not autoFirmwareVersionReplyActive}">
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
                                            ${firmwareSettings.resultType}
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not autoFirmwareReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Update Result Type">
                                            <select name="updateResult">
                                                <c:forEach var="updateResult" items="${firmwareUpdateResultTypes}">
                                                    <option value="${updateResult}">${updateResult}</option>
                                                </c:forEach>
                                            </select>
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
                                <cti:button label="Enable All" type="button" href="enableAll"/>
                            </c:if>
                            <c:if test="${numberOfSimulatorsRunning gt 0}">
                                <cti:button label="Disable All" type="button" href="disableAll"/>
                            </c:if>
                        </td>
                        <td>Activates all gateway simulator functions with default parameter values.</td>
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
            <tags:sectionContainer title="Send Gateway Response Data">
                Queue an unsolicited gateway response data message and also create new gateway when data message is received continuously for more than 2 hours 
                and if that gateway doesn't exist in Yukon.

                <br><br>
                
                <form action="sendGatewayDataResponse">
                    <cti:csrfToken/>
                    <tags:nameValueContainer tableClass="natural-width">
                        <tags:nameValue name="Serial Number">
                            <input name="serial" value="7500001337">
                        </tags:nameValue>
                        <tags:nameValue name="GWY-800">
                            <input name="isGateway2" type="checkbox">
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <cti:button label="Send" type="submit"/>
                </form>
            </tags:sectionContainer>
        </div>
    </div>
</cti:standardPage>