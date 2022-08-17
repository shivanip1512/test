<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest.viewDataSimulator">
    <cti:tabs>
        <cti:tab title="RFN Meter Simulator">
            <div class="column-12-12 clearfix">
                <div class="column one">
                    <form id='formData'">
                        <cti:csrfToken />
                        <cti:msg2 key="modules.dev.rfnMeterSimulator.helpText" var="helpText"/>
                        <tags:sectionContainer2 nameKey="rfnMeterSimulator">
                            <div id='rfnMeterForm'>
                                <tags:nameValueContainer2>
                                    <tags:nameValue2 nameKey=".rfnMeterSimulator.rfnMeterType">
                                        <tags:selectWithItems path="currentSettings.paoType"
                                            items="${paoTypes}" id="pao-type"
                                            defaultItemLabel="ALL RFN Type" defaultItemValue="ALL RFN Type" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".lcrDataSimulator.duplicates">
                                         <input id="percentOfDuplicates" name="percentOfDuplicates" type="text" value=${currentSettings.percentOfDuplicates} maxlength="3" size="3"> %
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".rfnMeterDataSimulator.reportingInterval">
                                    <select name="reportingInterval">
                                        <c:forEach var="reportingInterval" items="${rfnMeterReportingIntervals}">
                                            <c:choose>
                                                <c:when test="${selectedReportingInterval.equals(reportingInterval)}">
                                                    <option value="${reportingInterval}" selected="selected"><cti:msg2 key="${reportingInterval}"/></option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${reportingInterval}"><cti:msg2 key="${reportingInterval}"/></option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </select>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </div>
                            <div>
                                <br/>
                                <cti:button id="send-message" nameKey="sendRfnMeterMessages" />
                                <cti:button id="stop-send-message" nameKey="stopSendingRfnMeterMessages"
                                    classes="dn" />
                            </div>
                            <div class="button-group button-group-toggle">
                                <div class="js-sim-startup" data-simulator-type="RFN_METER">
                                    <cti:button nameKey="runSimulatorOnStartup.automatic" classes="yes enable-startup"/>
                                    <cti:button nameKey="runSimulatorOnStartup.manual" classes="no disable-startup"/>  
                                </div>
                            </div>
                        </tags:sectionContainer2>
                        <tags:sectionContainer2 nameKey="rfnMeterSimulatorTest">
                            <div id='rfnMeterForm'>
                                <tags:nameValueContainer2>
                                    <tags:nameValue2 nameKey=".rfnMeterSimulator.deviceId">
                                        <input id="deviceId"" name="deviceId"" type="text" value=${currentSettings.deviceId}> 
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </div>
                            <div>
                                <br />
                                <cti:button  id="send-test" nameKey="testMetersArchiveRequest" />
                            </div>
                        </tags:sectionContainer2>
                    </form>
                </div>
        
                <div id="taskStatusDiv" class="column two nogutter">
                    <tags:sectionContainer title="Rfn Meter Simulator Status">
                        <div id="taskStatusMessage"></div>
                        <div>
                            <div>
                                <span id="status-running" style="font-size: 12pt; color: blue;">${dataSimulatorStatus.running}</span>
                            </div>
                            <div>
                                Start Time: <span id="status-start-time">${dataSimulatorStatus.startTime}</span>
                            </div>
                            <div>
                                Stop Time: <span id="status-stop-time">${dataSimulatorStatus.stopTime}</span>
                            </div>
                            <div>
                                Success: <span id="status-num-success" class="success">${dataSimulatorStatus.success}</span>
                            </div>
                            <div>
                                Failure: <span id="status-num-failed" class="error">${dataSimulatorStatus.failure}</span>
                            </div>
                            <div>
                                Last Injection Time: <span id="status-last-injection-time">${dataSimulatorStatus.lastInjectionTime}</span>
                            </div>
                        </div>
                    </tags:sectionContainer>
                </div>
            </div>
        </cti:tab>
    
        <cti:tab title="RFN Read and Control Simulator">
        
            <div id="read-simulator-popup" class="dn" data-title="Read Simulator Parameters" data-width="800">
                <tags:nameValueContainer tableClass="natural-width">
                    <tags:nameValue name="Reply1">
                        This is the initial reply that will be contained in the simulated Network Manager response for read.
                        The available options are from the enum RfnMeterReadingReplyType.
                    </tags:nameValue>
                    <tags:nameValue name="Reply1 Fail Rate">
                        This value is used to calculate the chance of the inputed response failing. It's meant to deliver this rate over time,
                        so for a 10% fail rate, it is expected that 1 out of the next 10 read requests will fail, 
                        but it's not guaranteed that 1 out of 10 request will fail. It could end up being more or less.
                    </tags:nameValue>
                    <tags:nameValue name="Reply2">
                        This is the data reply that will be contained in the simulated Network Manager response for read.
                        The available options are from the enum RfnMeterReadingDataReplyType.
                    </tags:nameValue>
                    <tags:nameValue name="Reply2 Fail Rate">
                        This value is used to calculate the chance of the inputed response failing. It's meant to deliver this rate over time,
                        so for a 10% fail rate, it is expected that 1 out of the next 10 read requests will fail, 
                        but it's not guaranteed that 1 out of 10 request will fail. It could end up being more or less.
                    </tags:nameValue>
                    <tags:nameValue name="Model Mismatch">
                        This value is used to calculate the chance of the response failing due to a mismatched RfnIdentifier Model.
                        It's meant to deliver this rate over time, so for a 10% fail rate, it is expected that 1 out of the next 10 read requests 
                        will fail, but it's not guaranteed that 1 out of 10 request will fail. It could end up being more or less.
                    </tags:nameValue>
                    <tags:nameValue name="Note">
                        Reply1 and Reply2 refer to the Reply1 and Reply2 referenced from the class RfnMeterReadService.
                    </tags:nameValue>
                </tags:nameValueContainer>
            </div>
        
            <div id="disconnect-simulator-popup" class="dn" data-title="Disconnect Simulator Parameters" data-width="800">
                <tags:nameValueContainer tableClass="natural-width">
                    <tags:nameValue name="Reply1">
                        This is the initial reply that will be contained in the simulated Network Manager response for disconnect.
                        The available options are from the enum RfnMeterDisconnectInitialReplyType.
                    </tags:nameValue>
                    <tags:nameValue name="Reply1 Fail Rate">
                        This value is used to calculate the chance of the inputed response failing. It's meant to deliver this rate over time,
                        so for a 10% fail rate, it is expected that 1 out of the next 10 disconnect requests will fail, 
                        but it's not guaranteed that 1 out of 10 request will fail. It could end up being more or less.
                    </tags:nameValue>
                    <tags:nameValue name="Reply2">
                        This is the confirmation reply that will be contained in the simulated Network Manager response for disconnect.
                        The available options are from the enum RfnMeterDisconnectConfirmationReplyType.
                    </tags:nameValue>
                    <tags:nameValue name="Reply2 Fail Rate">
                        This value is used to calculate the chance of the inputed response failing. It's meant to deliver this rate over time,
                        so for a 10% fail rate, it is expected that 1 out of the next 10 disconnect requests will fail, 
                        but it's not guaranteed that 1 out of 10 request will fail. It could end up being more or less.
                    </tags:nameValue>
                    <tags:nameValue name="Note">
                        Reply1 and Reply2 refer to the Reply1 and Reply2 referenced from the class RfnMeterDisconnectService.
                    </tags:nameValue>
                    <tags:nameValue name="Query State">
                        This value is used as the disconnect state response to a Disconnect QUERY request.
                    </tags:nameValue>
                </tags:nameValueContainer>
            </div>
        
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th>Data Type</th>
                        <th>Status</th>
                        <th>Actions</th>
                        <th>Parameters</th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tags:sectionContainer2 nameKey="rfnMeterReadAndControlMeterSimulator" helpText="yukon.web.modules.dev.rfnMeterReadAndControlMeterSimulator.helpText">
                    <tr>
                        <form action="startMetersReadRequest" method="POST">
                            <cti:csrfToken/>
                            <td>Read</td>
                            <td>
                                <c:if test="${meterReadReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not meterReadReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${meterReadReplyActive}">
                                    <cti:button label="Disable"  type="button" href="stopMetersReadRequest"/>
                                </c:if>
                                <c:if test="${not meterReadReplyActive}">
                                    <cti:button label="Enable" type="submit" />
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${meterReadReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Reply1">
                                            <tags:selectWithItems path="currentRfnMeterReadAndControlReadSimulatorSettings.readReply1"
                                                    items="${rfnMeterReadReplies}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply1 Fail Rate" style="background-color: white;">
                                            <input type="text" name="readReply1FailPercent"  style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlReadSimulatorSettings.readReply1FailPercent}" disabled="true"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2">
                                           <tags:selectWithItems path="currentRfnMeterReadAndControlReadSimulatorSettings.readReply2"
                                                    items="${rfnMeterReadDataReplies}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2 Fail Rate" style="background-color: white;">
                                            <input type="text" name="readReply2FailPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlReadSimulatorSettings.readReply2FailPercent}" disabled="true"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Model Mismatch Rate" style="background-color: white;">
                                            <input type="text" name="modelMismatchPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlReadSimulatorSettings.modelMismatchPercent}" disabled="true"/>%
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not meterReadReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Reply1">
                                            <tags:selectWithItems path="currentRfnMeterReadAndControlReadSimulatorSettings.readReply1"
                                                    items="${rfnMeterReadReplies}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply1 Fail Rate" style="background-color: white;">
                                            <input type="text" name="readReply1FailPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlReadSimulatorSettings.readReply1FailPercent}"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2">
                                           <tags:selectWithItems path="currentRfnMeterReadAndControlReadSimulatorSettings.readReply2" 
                                                    items="${rfnMeterReadDataReplies}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2 Fail Rate" style="background-color: white;">
                                            <input type="text" name="readReply2FailPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlReadSimulatorSettings.readReply2FailPercent}"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Model Mismatch Rate" style="background-color: white;">
                                            <input type="text" name="modelMismatchPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlReadSimulatorSettings.modelMismatchPercent}"/>%
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#read-simulator-popup" label="Parameter Details"/>
                            </td>
                        </form>
                    </tr>
                    <tr>
                        <form action="startMetersDisconnectRequest" method="POST">
                            <cti:csrfToken/>
                            <td>Disconnect</td>
                            <td>
                                <c:if test="${meterDisconnectReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not meterDisconnectReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${meterDisconnectReplyActive}">
                                    <cti:button label="Disable"  type="button" href="stopMetersDisconnectRequest"/>
                                </c:if>
                                <c:if test="${not meterDisconnectReplyActive}">
                                    <cti:button label="Enable" type="submit" />
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${meterDisconnectReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Reply1">
                                            <tags:selectWithItems path="currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply1"
                                                    items="${rfnMeterDisconnectInitialReplies}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply1 Fail Rate">
                                            <input type="text" name="disconnectReply1FailPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply1FailPercent}" disabled="true"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2">
                                           <tags:selectWithItems path="currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply2" 
                                                    items="${rfnMeterDisconnectConfirmationReplies}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2 Fail Rate">
                                            <input type="text" name="disconnectReply2FailPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply2FailPercent}" disabled="true"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Query State">
                                           <tags:selectWithItems path="currentRfnMeterReadAndControlDisconnectSimulatorSettings.queryResponse" 
                                                    items="${rfnMeterDisconnectQueryResponses}" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not meterDisconnectReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Reply1">
                                            <tags:selectWithItems path="currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply1"
                                                    items="${rfnMeterDisconnectInitialReplies}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply1 Fail Rate">
                                            <input type="text" name="disconnectReply1FailPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply1FailPercent}"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2">
                                           <tags:selectWithItems path="currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply2" 
                                                    items="${rfnMeterDisconnectConfirmationReplies}"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2 Fail Rate">
                                            <input type="text" name="disconnectReply2FailPercent" style="width: 40px;" 
                                            value="${currentRfnMeterReadAndControlDisconnectSimulatorSettings.disconnectReply2FailPercent}"/>%
                                        </tags:nameValue>
                                        <tags:nameValue name="Query State">
                                           <tags:selectWithItems path="currentRfnMeterReadAndControlDisconnectSimulatorSettings.queryResponse" 
                                                    items="${rfnMeterDisconnectQueryResponses}"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#disconnect-simulator-popup" label="Parameter Details"/>
                            </td>
                        </form>
                    </tr>
                    <tr>
                        <td>All Simulators</td>
                        <c:set var="maxSimulators" value="2"/>
                        <td>
                            <c:set var="clazz" value="${numberOfSimulatorsRunning eq 0 ? 'error' : 'pending'}"/>
                            <div class="user-message ${clazz}">${numberOfSimulatorsRunning}/${maxSimulators}</div>
                        </td>
                        <td colspan="2">
                            <c:if test="${numberOfSimulatorsRunning lt maxSimulators}">
                                <cti:button id="enable-all" label="Enable All" type="button"/>
                            </c:if>
                            <c:if test="${numberOfSimulatorsRunning gt 0}">
                                <cti:button label="Disable All" type="button" href="disableAllRfnReadAndControl"/>
                            </c:if>
                            <div class="button-group button-group-toggle">
                                <div class="js-sim-startup" data-simulator-type="RFN_METER_READ_CONTROL">
                                    <cti:button nameKey="runSimulatorOnStartup.automatic" classes="yes enable-startup"/>
                                    <cti:button nameKey="runSimulatorOnStartup.manual" classes="no disable-startup"/>
                                </div>
                            </div>
                        </td>
                    </tr>
                    
                </tags:sectionContainer2>
            </table>
        </cti:tab>
    </cti:tabs>
    
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.rfnMeterSimulator.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js" />
</cti:standardPage>
