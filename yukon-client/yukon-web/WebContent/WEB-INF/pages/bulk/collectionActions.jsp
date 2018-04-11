<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.collectionActions">

    <tags:bulkActionContainer key="yukon.common.device.bulk.collectionActions" deviceCollection="${deviceCollection}">

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
        <cti:msg var="deviceConfigsLabel" key="yukon.common.device.bulk.collectionActions.deviceConfigsLabel"/>
        <cti:msg var="deviceConfigsDescription" key="yukon.common.device.bulk.collectionActions.deviceConfigsDescription"/>
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
                                        description="${addToGroupDescription}" action="/bulk/group/selectGroup" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                                
                                <%-- REMOVE FROM GROUP --%>
                                <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
	                                <tags:collectionActionTr inputValue="REMOVE" buttonValue="${removeFromGroupLabel}" inputName="addRemove" 
                                        description="${removeFromGroupDescription}" action="/bulk/group/selectGroup" deviceCollection="${deviceCollection}"/>
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
                                        action="/bulk/massChange/massChangeSelect" deviceCollection="${deviceCollection}"/>
                                    
                                    <%-- CHANGE TYPE --%>
                                    <tags:collectionActionTr buttonValue="${changeDeviceTypeLabel}" description="${changeDeviceTypeDescription}"
                                        action="/bulk/changeDeviceType/chooseDeviceType" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                                
                                <cti:checkRolesAndProperties value="MASS_DELETE">
                                    <%-- MASS DELETE --%>
                                    <tags:collectionActionTr buttonValue="${massDeleteLabel}" description="${massDeleteDescription}"
                                        action="/bulk/massDelete/massDelete" deviceCollection="${deviceCollection}"/>
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
                                    action="/group/commander/collectionProcessing" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                            
                            <%-- GROUP ATTRIBUTE READ --%>
                            <tags:collectionActionTr buttonValue="${readAttributeLabel}" description="${readAttributeDescription}"
                                    action="/group/groupMeterRead/homeCollection" deviceCollection="${deviceCollection}"/>
                            
                            <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                                <%-- LOCATE ROUTE --%>
                                <tags:collectionActionTr buttonValue="${routeLocateLabel}" description="${routeLocateDescription}"
                                    action="/bulk/routeLocate/home" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>

                            <%-- DISCONNECT --%>
                            <cti:checkRolesAndProperties value="GROUP_DISCONNECT_CONTROL">
                                <tags:collectionActionTr buttonValue="${disconnectLabel}"
                                    description="${disconnectDescription}" action="/bulk/disconnect/home"
                                    deviceCollection="${deviceCollection}" />
                            </cti:checkRolesAndProperties>
                            
                            <%-- DEMAND RESET--%>
                            <cti:checkRolesAndProperties value="GROUP_DEMAND_RESET">
                                <tags:collectionActionTr buttonValue="${demandResetLabel}"
                                    description="${demandResetDescription}" action="/bulk/demand-reset/action"
                                    deviceCollection="${deviceCollection}" />
                            </cti:checkRolesAndProperties>
 
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
                
                <cti:dataGridCell>
                    <tags:sectionContainer title="${headerConfigActions}">
                        <table>
                            <%-- DEVICE CONFIGS --%>
                            <tags:collectionActionTr buttonValue="${deviceConfigsLabel}" description="${deviceConfigsDescription}"
                                action="/bulk/config/deviceConfigs" deviceCollection="${deviceCollection}"/>
                                
                            <%--DATA STREAMING - check for CPARM and ROLE PROPERTY--%>
                            <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                                <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
                                    <%-- CONFIGURE DATA STREAMING --%>
                                    <tags:collectionActionTr buttonValue="${configureDataStreamingLabel}" description="${configureDataStreamingDescription}"
                                        action="/bulk/dataStreaming/configure" deviceCollection="${deviceCollection}"/>
                                        
                                    <%-- REMOVE DATA STREAMING --%>
                                    <tags:collectionActionTr buttonValue="${removeDataStreamingLabel}" description="${removeDataStreamingDescription}"
                                        action="/bulk/dataStreaming/remove" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                            </cti:checkRolesAndProperties>
                            
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
            </cti:checkRolesAndProperties>
            <cti:dataGridCell>
                <tags:sectionContainer title="${headerReporting}">
                    <table>
                        <%-- DEVICE REPORT --%>
                        <tags:collectionActionTr buttonValue="${deviceCollectionReportLabel}" description="${deviceCollectionReportDescription}"
                            action="/bulk/deviceCollectionReport" deviceCollection="${deviceCollection}"/>
                        
                        <cti:checkRolesAndProperties value="ARCHIVED_DATA_ANALYSIS">
                            <%-- DATA ANALYSIS --%>
                            <tags:collectionActionTr buttonValue="${dataAnalysisLabel}" description="${dataAnalysisDiscription}"
                                action="/bulk/archiveDataAnalysis/home/setup" deviceCollection="${deviceCollection}"/>
                        </cti:checkRolesAndProperties>        
                        
                        <cti:checkRolesAndProperties value="ARCHIVED_DATA_EXPORT">
                            <%-- ARCHIVED DATA EXPORT --%>
                            <tags:collectionActionTr buttonValue="${archivedValueDataExporterLabel}" description="${archivedValueDataExporterDescription}"
                                action="/tools/data-exporter/view" deviceCollection="${deviceCollection}"/>
                        </cti:checkRolesAndProperties>
                        
                        <%-- MAP DEVICES --%>
                        <tags:collectionActionTr buttonValue="${deviceCollectionMapLabel}" description="${deviceCollectionMapDescription}"
                            action="/tools/map" deviceCollection="${deviceCollection}"/>
                    </table>
                </tags:sectionContainer>
            </cti:dataGridCell>
            
            <c:if test="${showAddRemovePoints}">
                <cti:dataGridCell>
                    <tags:sectionContainer title="${headerPointActions}">
                        <table>
                            <%-- ADD POINTS --%>
                            <tags:collectionActionTr buttonValue="${addPointsLabel}" description="${addPointsDescription}"
                                action="/bulk/addPoints/home" deviceCollection="${deviceCollection}"/>
                            
                            <%-- UPDATE POINTS --%>
                            <tags:collectionActionTr buttonValue="${updatePointsLabel}" description="${updatePointsDescription}"
                                action="/bulk/updatePoints/home" deviceCollection="${deviceCollection}"/>
                            
                            <%-- REMOVE POINTS --%>
                            <tags:collectionActionTr buttonValue="${removePointsLabel}" description="${removePointsDescription}"
                                action="/bulk/removePoints/home" deviceCollection="${deviceCollection}"/>
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
            </c:if>
        </cti:dataGrid>
    </tags:bulkActionContainer>
</cti:standardPage>