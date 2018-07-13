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
                                <cti:button id="enable-startup" nameKey="runSimulatorOnStartup.automatic" classes="yes"/>
                                <cti:button id="disable-startup" nameKey="runSimulatorOnStartup.manual" classes="no"/>  
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
    
        <cti:tab title="RFN Network Manager Simulator" >
        
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
                    <tags:sectionContainer2 nameKey="rfnNetwrokManagerMeterSimulator">
                        <td>Read</td>
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
                                <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Reply1">
                                        </tags:nameValue>
                                        <tags:nameValue name="Reply2">
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                    <div>
                                    <br/>
                                    <cti:button id="send-message" nameKey="sendRfnMeterMessages" />
                                    <cti:button id="stop-send-message" nameKey="stopSendingRfnMeterMessages"
                                        classes="dn" />
                                </div>
                            </td>
                            <td>
                            <div class="button-group button-group-toggle">
                                <div class="js-sim-startup" data-simulator-type="RFN_METER_NETWORK">
                                <cti:button id="enable-startup" nameKey="runSimulatorOnStartup.automatic" classes="yes"/>
                                <cti:button id="disable-startup" nameKey="runSimulatorOnStartup.manual" classes="no"/>  
                                </div>
                            </div>
                            </td>
                    </tags:sectionContainer2>
                </tbody>
                <tbody>
                <td>Disconnect</td>
                            <td>
                                <c:if test="${autoUpdateReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not autoUpdateReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                
                                 <cti:button id="disconnect-message" nameKey="sendRfnDisconnectMessage" />
                                 <cti:button id="stop-disconnect-message" nameKey="stopRfnDisconnectMessage" />
                                <c:if test="${autoUpdateReplyActive}">
                                    <cti:button label="Disable" type="button" href="disableGatewayUpdateReply"/>
                                </c:if>
                                <c:if test="${not autoUpdateReplyActive}">
                                    <cti:button label="Enable" type="submit"/>
                                </c:if>
                            </td>
                </tbody>
            </table>
        
        </cti:tab>
    </cti:tabs>
    
    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.rfnMeterSimulator.js" />
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js" />
</cti:standardPage>
