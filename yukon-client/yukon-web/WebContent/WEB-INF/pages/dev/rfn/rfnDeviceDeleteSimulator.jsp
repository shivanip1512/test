<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="dev" page="rfnTest.rfnDeviceDeleteSimulator">
        <cti:msg2 key=".rfnDeviceDeletionSimulator.title" var="rfnDeviceDeleteSimulatorTitle"/>
            <div id="device-delete-simulator-popup" class="dn" data-title="RFN Device Deletion Simulator Parameters" data-width="800">
                <tags:nameValueContainer tableClass="natural-width">
                    <tags:nameValue name="Initial Reply">
                        Response from NM regarding device presence in NM Db. Reply Types: OK, INVALID_RFNIDENTIFIER, NO_DEVICE, NM_FAILURE. This ack message will be printed in the Web Application Server Logs.
                    </tags:nameValue>
                    <tags:nameValue name="Confirmation Reply">
                        Response from NM regarding device deletion status. Reply Type: SUCCESS, FAILURE. This confirmation message will be printed in the Service Manager Logs.
                    </tags:nameValue>
                    <tags:nameValue name="Note">
                        If the initial reply is set to anything but OK, no confirmation response will be received.
                    </tags:nameValue>
                </tags:nameValueContainer>
            </div>
            <table class="compact-results-table no-stripes">
                <thead>
                    <tr>
                        <th>Status</th>
                        <th>Actions</th>
                        <th>Parameters</th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                
                <tags:sectionContainer2 nameKey="rfnDeviceDeletionSimulator">
                    <tr>
                        <form action="enableDeviceDeletionReply" method="POST">
                            <cti:csrfToken/>
                            <td>
                                <c:if test="${deviceDeletionReplyActive}">
                                    <div class="user-message pending">Active</div>
                                </c:if>
                                <c:if test="${not deviceDeletionReplyActive}">
                                    <div class="user-message error">Off</div>
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${deviceDeletionReplyActive}">
                                    <cti:button label="Disable"  type="button" href="disableDeviceDeletionReply"/>
                                </c:if>
                                <c:if test="${not deviceDeletionReplyActive}">
                                    <cti:button label="Enable" type="submit" />
                                </c:if>
                            </td>
                            <td>
                                <c:if test="${deviceDeletionReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Initial Reply">
                                            <tags:selectWithItems path="deletionSettings.initialReply"
                                                    items="${rfnDeviceDeleteInitialReplies}" disabled="true"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Confirmation Reply">
                                           <tags:selectWithItems path="deletionSettings.confirmationReply"
                                                    items="${rfnDeviceDeleteConfirmationReplies}" disabled="true"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <c:if test="${not deviceDeletionReplyActive}">
                                    <tags:nameValueContainer tableClass="natural-width">
                                        <tags:nameValue name="Initial Reply">
                                            <tags:selectWithItems path="deletionSettings.initialReply"
                                                    items="${rfnDeviceDeleteInitialReplies}" disabled="false"/>
                                        </tags:nameValue>
                                        <tags:nameValue name="Confirmation Reply">
                                           <tags:selectWithItems path="deletionSettings.confirmationReply" 
                                                    items="${rfnDeviceDeleteConfirmationReplies}" disabled="false"/>
                                        </tags:nameValue>
                                    </tags:nameValueContainer>
                                </c:if>
                                <cti:button renderMode="labeledImage" icon="icon-help" data-popup="#device-delete-simulator-popup" label="Parameter Details"/>
                            </td>
                        </form>
                    </tr>
                </tags:sectionContainer2>
            </table>
    <cti:includeScript link="/resources/js/pages/yukon.dev.simulators.simulatorStartup.js" />
</cti:standardPage>
