<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.collectionActions">

    <tags:bulkActionContainer   key="yukon.common.device.bulk.collectionActions" deviceCollection="${deviceCollection}">

        <%-- LABELS AND DESCRIPTIONS --%>
        <cti:msg var="headerGroupMgt" key="yukon.common.device.bulk.collectionActions.header.groupManagement"/>
        <cti:msg var="addToGroupLabel" key="yukon.common.device.bulk.collectionActions.addToGroupLabel"/>
        <cti:msg var="addToGroupDescription" key="yukon.common.device.bulk.collectionActions.addToGroupDescription"/>
        <cti:msg var="removeFromGroupLabel" key="yukon.common.device.bulk.collectionActions.removeFromGroupLabel"/>
        <cti:msg var="removeFromGroupDescription" key="yukon.common.device.bulk.collectionActions.removeFromGroupDescription"/>

        <cti:msg var="headerCommands" key="yukon.common.device.bulk.collectionActions.header.commands"/>
        <cti:msg var="sendCommandLabel" key="yukon.common.device.bulk.collectionActions.sendCommandLabel"/>
        <cti:msg var="sendCommandDescription" key="yukon.common.device.bulk.collectionActions.sendCommandDescription"/>
        <cti:msg var="readAttributeLabel" key="yukon.common.device.bulk.collectionActions.readAttributeLabel"/>
        <cti:msg var="readAttributeDescription" key="yukon.common.device.bulk.collectionActions.readAttributeDescription"/>
        <cti:msg var="routeLocateLabel" key="yukon.common.device.bulk.collectionActions.routeLocateLabel"/>
        <cti:msg var="routeLocateDescription" key="yukon.common.device.bulk.collectionActions.routeLocateDescription"/>
        <cti:msg var="disconnectLabel" key="yukon.common.device.bulk.collectionActions.disconnectLabel"/>
        <cti:msg var="disconnectDescription" key="yukon.common.device.bulk.collectionActions.disconnectDescription"/>
        <cti:msg var="demandResetLabel" key="yukon.common.device.bulk.collectionActions.demandResetLabel"/>
        <cti:msg var="demandResetDescription" key="yukon.common.device.bulk.collectionActions.demandResetDescription"/>

        <cti:msg var="headerReporting" key="yukon.common.device.bulk.collectionActions.header.reporting"/>
        <cti:msg var="deviceCollectionReportLabel" key="yukon.common.device.bulk.collectionActions.deviceCollectionReportLabel"/>
        <cti:msg var="deviceCollectionReportDescription" key="yukon.common.device.bulk.collectionActions.deviceCollectionReportDescription"/>
        <cti:msg var="deviceCollectionMapLabel" key="yukon.common.device.bulk.collectionActions.deviceCollectionMapLabel"/>
        <cti:msg var="deviceCollectionMapDescription" key="yukon.common.device.bulk.collectionActions.deviceCollectionMapDescription"/>
        <cti:msg var="dataAnalysisLabel" key="yukon.common.device.bulk.collectionActions.dataAnalysisLabel"/>
        <cti:msg var="dataAnalysisDiscription" key="yukon.common.device.bulk.collectionActions.dataAnalysisDiscription"/>
        <cti:msg var="archivedValueDataExporterLabel" key="yukon.common.device.bulk.collectionActions.archivedValueDataExporterLabel"/>
        <cti:msg var="archivedValueDataExporterDescription" key="yukon.common.device.bulk.collectionActions.archivedValueDataExporterDescription"/>

        <cti:msg var="headerEditing" key="yukon.common.device.bulk.collectionActions.header.editing"/>
        <cti:msg var="massChangeLabel" key="yukon.common.device.bulk.collectionActions.massChangeLabel"/>
        <cti:msg var="massChangeDescription" key="yukon.common.device.bulk.collectionActions.massChangeDescription"/>
        <cti:msg var="changeDeviceTypeLabel" key="yukon.common.device.bulk.collectionActions.changeDeviceTypeLabel"/>
        <cti:msg var="changeDeviceTypeDescription" key="yukon.common.device.bulk.collectionActions.changeDeviceTypeDescription"/>
        <cti:msg var="massDeleteLabel" key="yukon.common.device.bulk.collectionActions.massDeleteLabel"/>
        <cti:msg var="massDeleteDescription" key="yukon.common.device.bulk.collectionActions.massDeleteDescription"/>

        <cti:msg var="headerConfigActions" key="yukon.common.device.bulk.collectionActions.header.configActions"/>
        <cti:msg var="assignConfigLabel" key="yukon.common.device.bulk.collectionActions.assignConfigLabel"/>
        <cti:msg var="assignConfigDescription" key="yukon.common.device.bulk.collectionActions.assignConfigDescription"/>
        <cti:msg var="unassignConfigLabel" key="yukon.common.device.bulk.collectionActions.unassignConfigLabel"/>
        <cti:msg var="unassignConfigDescription" key="yukon.common.device.bulk.collectionActions.unassignConfigDescription"/>
        <cti:msg var="sendConfigLabel" key="yukon.common.device.bulk.collectionActions.sendConfigLabel"/>
        <cti:msg var="sendConfigDescription" key="yukon.common.device.bulk.collectionActions.sendConfigDescription"/>
        <cti:msg var="readConfigLabel" key="yukon.common.device.bulk.collectionActions.readConfigLabel"/>
        <cti:msg var="readConfigDescription" key="yukon.common.device.bulk.collectionActions.readConfigDescription"/>
        <cti:msg var="verifyConfigLabel" key="yukon.common.device.bulk.collectionActions.verifyConfigLabel"/>
        <cti:msg var="verifyConfigDescription" key="yukon.common.device.bulk.collectionActions.verifyConfigDescription"/>
        <cti:msg var="configureDataStreamingLabel" key="yukon.common.device.bulk.collectionActions.configureDataStreamingLabel"/>
        <cti:msg var="configureDataStreamingDescription" key="yukon.common.device.bulk.collectionActions.configureDataStreamingDescription"/>
        <cti:msg var="removeDataStreamingLabel" key="yukon.common.device.bulk.collectionActions.removeDataStreamingLabel"/>
        <cti:msg var="removeDataStreamingDescription" key="yukon.common.device.bulk.collectionActions.removeDataStreamingDescription"/>

        <cti:msg var="headerPointActions" key="yukon.common.device.bulk.collectionActions.header.pointActions"/>
        <cti:msg var="addPointsLabel" key="yukon.common.device.bulk.collectionActions.addPointsLabel"/>
        <cti:msg var="addPointsDescription" key="yukon.common.device.bulk.collectionActions.addPointsDescription"/>
        <cti:msg var="updatePointsLabel" key="yukon.common.device.bulk.collectionActions.updatePointsLabel"/>
        <cti:msg var="updatePointsDescription" key="yukon.common.device.bulk.collectionActions.updatePointsDescription"/>
        <cti:msg var="removePointsLabel" key="yukon.common.device.bulk.collectionActions.removePointsLabel"/>
        <cti:msg var="removePointsDescription" key="yukon.common.device.bulk.collectionActions.removePointsDescription"/>

        <%-- URLS --%>
        <cti:url var="selectGroupUrl" value="/bulk/group/selectGroup"/>
        
        <cti:url var="massChangeUrl" value="/bulk/massChange/massChangeSelect"/>
        <cti:url var="changeDeviceTypeUrl" value="/bulk/changeDeviceType/chooseDeviceType"/>
        <cti:url var="massDeleteUrl" value="/bulk/massDelete/massDelete"/>
        
        <cti:url var="sendCommandUrl" value="/group/commander/collectionProcessing"/>
        <cti:url var="readAttributeUrl" value="/group/groupMeterRead/homeCollection"/>
        <cti:url var="routeLocateUrl" value="/bulk/routeLocate/home"/>
        <cti:url var="disconnectUrl" value="/bulk/disconnect/home"/>
        <cti:url var="demandResetUrl" value="/bulk/demand-reset/action"/>
        
        <cti:url var="assignConfigUrl" value="/bulk/config/assignConfig"/>
        <cti:url var="unassignConfigUrl" value="/bulk/config/unassignConfig"/>
        <cti:url var="sendConfigUrl" value="/bulk/config/sendConfig"/>
        <cti:url var="readConfigUrl" value="/bulk/config/readConfig"/>
        <cti:url var="verifyConfigUrl" value="/bulk/config/verifyConfig"/>
        <cti:url var="configureDataStreamingUrl" value="/bulk/dataStreaming/configure"/>
        <cti:url var="removeDataStreamingUrl" value="/bulk/dataStreaming/remove"/>
        
        <cti:url var="deviceCollectionReportUrl" value="/bulk/deviceCollectionReport"/>
        <cti:url var="dataAnalysisUrl" value="/bulk/archiveDataAnalysis/home/setup"/>
        <cti:url var="archivedValueDataExporterUrl" value="/tools/data-exporter/view"/>
        <cti:url var="deviceCollectionMapUrl" value="/tools/map"/>
        
        <cti:url var="addPointsUrl" value="/bulk/addPoints/home"/>
        <cti:url var="updatePointsUrl" value="/bulk/updatePoints/home"/>
        <cti:url var="removePointsUrl" value="/bulk/removePoints/home"/>
        
        <%-- THE GRID --%>
        <cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                <c:if test="${showGroupManagement}" >
                    <cti:dataGridCell>
                        <tags:sectionContainer title="${headerGroupMgt}">
                            <table>
                                <%-- ADD TO GROUP --%>
                                <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
                                    <tags:collectionActionTr inputValue="ADD" buttonValue="${addToGroupLabel}" inputName="addRemove" 
                                        description="${addToGroupDescription}" action="${selectGroupUrl}" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                                
                                <%-- REMOVE FROM GROUP --%>
                                <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
	                                <tags:collectionActionTr inputValue="REMOVE" buttonValue="${removeFromGroupLabel}" inputName="addRemove" 
                                        description="${removeFromGroupDescription}" action="${selectGroupUrl}" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                            </table>
                        </tags:sectionContainer>
                    </cti:dataGridCell>
                </c:if>
                
                <c:if test="${showEditing}">
                    <cti:dataGridCell>
                        <tags:sectionContainer title="${headerEditing}">
                            <table>
                                <cti:checkRolesAndProperties value="MASS_CHANGE">
                                    <%-- MASS CHANGE --%>
                                    <tags:collectionActionTr buttonValue="${massChangeLabel}" description="${massChangeDescription}"
                                        action="${massChangeUrl}" deviceCollection="${deviceCollection}"/>
                                    
                                    <%-- CHANGE TYPE --%>
                                    <tags:collectionActionTr buttonValue="${changeDeviceTypeLabel}" description="${changeDeviceTypeDescription}"
                                        action="${changeDeviceTypeUrl}" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                                
                                <cti:checkRolesAndProperties value="MASS_DELETE">
                                    <%-- MASS DELETE --%>
                                    <tags:collectionActionTr buttonValue="${massDeleteLabel}" description="${massDeleteDescription}"
                                        action="${massDeleteUrl}" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                            </table>
                        </tags:sectionContainer>
                    </cti:dataGridCell>
                </c:if>
                
                <cti:dataGridCell>
                    <tags:sectionContainer title="${headerCommands}">
                        <table>
                            <cti:checkRolesAndProperties value="GROUP_COMMANDER">
                                <%-- GROUP COMMANDER --%>
                                <tags:collectionActionTr buttonValue="${sendCommandLabel}" description="${sendCommandDescription}"
                                    action="${sendCommandUrl}" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                            
                            <%-- GROUP ATTRIBUTE READ --%>
                            <tags:collectionActionTr buttonValue="${readAttributeLabel}" description="${readAttributeDescription}"
                                    action="${readAttributeUrl}" deviceCollection="${deviceCollection}"/>
                            
                            <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                                <%-- LOCATE ROUTE --%>
                                <tags:collectionActionTr buttonValue="${routeLocateLabel}" description="${routeLocateDescription}"
                                    action="${routeLocateUrl}" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>

                            <%-- DISCONNECT --%>
                            <cti:checkRolesAndProperties value="GROUP_DISCONNECT_CONTROL">
                                <tags:collectionActionTr buttonValue="${disconnectLabel}"
                                    description="${disconnectDescription}" action="${disconnectUrl}"
                                    deviceCollection="${deviceCollection}" />
                            </cti:checkRolesAndProperties>
                            
                            <%-- DEMAND RESET--%>
                            <cti:checkRolesAndProperties value="GROUP_DEMAND_RESET">
                                <tags:collectionActionTr buttonValue="${demandResetLabel}"
                                    description="${demandResetDescription}" action="${demandResetUrl}"
                                    deviceCollection="${deviceCollection}" />
                            </cti:checkRolesAndProperties>
 
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
                
                <cti:dataGridCell>
                    <tags:sectionContainer title="${headerConfigActions}">
                        <table>
                            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                                <%-- ASSIGN CONFIG --%>
                                <tags:collectionActionTr buttonValue="${assignConfigLabel}" description="${assignConfigDescription}"
                                    action="${assignConfigUrl}" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                            
                            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                                <%-- UNASSIGN CONFIG --%>
                                <tags:collectionActionTr buttonValue="${unassignConfigLabel}" description="${unassignConfigDescription}"
                                    action="${unassignConfigUrl}" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                            
                            <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                                <%-- SEND CONFIG --%>
                                <tags:collectionActionTr buttonValue="${sendConfigLabel}" description="${sendConfigDescription}"
                                    action="${sendConfigUrl}" deviceCollection="${deviceCollection}"/>
                            
                                <%-- READ CONFIG --%>
                                <tags:collectionActionTr buttonValue="${readConfigLabel}" description="${readConfigDescription}"
                                    action="${readConfigUrl}" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                            
                            <%-- VERIFY CONFIG --%>
                            <tags:collectionActionTr buttonValue="${verifyConfigLabel}" description="${verifyConfigDescription}"
                                action="${verifyConfigUrl}" deviceCollection="${deviceCollection}"/>
                                
                            <%-- CONFIGURE DATA STREAMING --%>
                            <tags:collectionActionTr buttonValue="${configureDataStreamingLabel}" description="${configureDataStreamingDescription}"
                                action="${configureDataStreamingUrl}" deviceCollection="${deviceCollection}"/>
                                
                            <%-- REMOVE DATA STREAMING --%>
                            <tags:collectionActionTr buttonValue="${removeDataStreamingLabel}" description="${removeDataStreamingDescription}"
                                action="${removeDataStreamingUrl}" deviceCollection="${deviceCollection}"/>
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
            </cti:checkRolesAndProperties>
            <cti:dataGridCell>
                <tags:sectionContainer title="${headerReporting}">
                    <table>
                        <%-- DEVICE REPORT --%>
                        <tags:collectionActionTr buttonValue="${deviceCollectionReportLabel}" description="${deviceCollectionReportDescription}"
                            action="${deviceCollectionReportUrl}" deviceCollection="${deviceCollection}"/>
                        
                        <cti:checkRolesAndProperties value="ARCHIVED_DATA_ANALYSIS">
                            <%-- DATA ANALYSIS --%>
                            <tags:collectionActionTr buttonValue="${dataAnalysisLabel}" description="${dataAnalysisDiscription}"
                                action="${dataAnalysisUrl}" deviceCollection="${deviceCollection}"/>
                        </cti:checkRolesAndProperties>        
                        
                        <cti:checkRolesAndProperties value="ARCHIVED_DATA_EXPORT">
                            <%-- ARCHIVED DATA EXPORT --%>
                            <tags:collectionActionTr buttonValue="${archivedValueDataExporterLabel}" description="${archivedValueDataExporterDescription}"
                                action="${archivedValueDataExporterUrl}" deviceCollection="${deviceCollection}"/>
                        </cti:checkRolesAndProperties>
                        
                        <%-- MAP DEVICES --%>
                        <tags:collectionActionTr buttonValue="${deviceCollectionMapLabel}" description="${deviceCollectionMapDescription}"
                            action="${deviceCollectionMapUrl}" deviceCollection="${deviceCollection}"/>
                    </table>
                </tags:sectionContainer>
            </cti:dataGridCell>
            
            <c:if test="${showAddRemovePoints}">
                <cti:dataGridCell>
                    <tags:sectionContainer title="${headerPointActions}">
                        <table>
                            <%-- ADD POINTS --%>
                            <tags:collectionActionTr buttonValue="${addPointsLabel}" description="${addPointsDescription}"
                                action="${addPointsUrl}" deviceCollection="${deviceCollection}"/>
                            
                            <%-- UPDATE POINTS --%>
                            <tags:collectionActionTr buttonValue="${updatePointsLabel}" description="${updatePointsDescription}"
                                action="${updatePointsUrl}" deviceCollection="${deviceCollection}"/>
                            
                            <%-- REMOVE POINTS --%>
                            <tags:collectionActionTr buttonValue="${removePointsLabel}" description="${removePointsDescription}"
                                action="${removePointsUrl}" deviceCollection="${deviceCollection}"/>
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
            </c:if>
        </cti:dataGrid>
    </tags:bulkActionContainer>
</cti:standardPage>