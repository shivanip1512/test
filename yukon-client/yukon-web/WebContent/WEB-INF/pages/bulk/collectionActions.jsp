<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.collectionActions">

    <cti:msgScope paths="yukon.common.device.bulk.collectionActions">

        <tags:bulkActionContainer key="yukon.common.device.bulk.collectionActions" deviceCollection="${deviceCollection}">
            
            <%-- THE GRID --%>
            <cti:dataGrid cols="3">
                <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                    <cti:dataGridCell>
                        <tags:sectionContainer2 nameKey="header.commands" styleClass="w30">
                            <table>
                                <cti:checkRolesAndProperties value="GROUP_COMMANDER">
                                    <%-- GROUP COMMANDER --%>
                                    <tags:collectionActionTr linkKey=".sendCommandLabel" descriptionKey=".sendCommandDescription" icon="icon-app icon-app-32-page-go"
                                        action="/group/commander/collectionProcessing" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                                
                                <%-- GROUP ATTRIBUTE READ --%>
                                <tags:collectionActionTr linkKey=".readAttributeLabel" descriptionKey=".readAttributeDescription" icon="icon-app icon-app-32-page-magnify"
                                        action="/group/groupMeterRead/homeCollection" deviceCollection="${deviceCollection}"/>
                                
                                <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                                    <%-- LOCATE ROUTE --%>
                                    <tags:collectionActionTr linkKey=".routeLocateLabel" descriptionKey=".routeLocateDescription" icon="icon-app icon-app-32-pin"
                                        action="/bulk/routeLocate/home" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
    
                                <%-- DISCONNECT --%>
                                <cti:checkRolesAndProperties value="GROUP_DISCONNECT_CONTROL">
                                    <tags:collectionActionTr linkKey=".disconnectLabel" descriptionKey=".disconnectDescription" icon="icon-app icon-app-32-disconnect"
                                        action="/bulk/disconnect/home" deviceCollection="${deviceCollection}" />
                                </cti:checkRolesAndProperties>
                                
                                <%-- DEMAND RESET--%>
                                <cti:checkRolesAndProperties value="GROUP_DEMAND_RESET">
                                    <tags:collectionActionTr linkKey=".demandResetLabel" descriptionKey=".demandResetDescription" icon="icon-app icon-app-32-update"
                                        action="/bulk/demand-reset/action" deviceCollection="${deviceCollection}" />
                                </cti:checkRolesAndProperties>
     
                            </table>
                        </tags:sectionContainer2>
                    </cti:dataGridCell>
                    
                    <cti:dataGridCell>
                        <tags:sectionContainer2 nameKey="header.configActions" styleClass="w30">
                            <table>
                                <%-- DEVICE CONFIGS --%>
                                <tags:collectionActionTr linkKey=".deviceConfigsLabel" descriptionKey=".deviceConfigsDescription" icon="icon-app icon-app-32-cog"
                                    action="/bulk/config/deviceConfigs" deviceCollection="${deviceCollection}"/>
                                    
                                <%--DATA STREAMING - check for CPARM and ROLE PROPERTY--%>
                                <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                                    <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
                                        <%-- CONFIGURE DATA STREAMING --%>
                                        <tags:collectionActionTr linkKey=".configureDataStreamingLabel" descriptionKey=".configureDataStreamingDescription" icon="icon-app icon-app-32-datastreaming-configure"
                                            action="/bulk/dataStreaming/configure" deviceCollection="${deviceCollection}"/>
                                            
                                        <%-- REMOVE DATA STREAMING --%>
                                        <tags:collectionActionTr linkKey=".removeDataStreamingLabel" descriptionKey=".removeDataStreamingDescription" icon="icon-app icon-app-32-datastreaming-remove"
                                            action="/bulk/dataStreaming/remove" deviceCollection="${deviceCollection}"/>
                                    </cti:checkRolesAndProperties>
                                </cti:checkRolesAndProperties>
                                
                            </table>
                        </tags:sectionContainer2>
                    </cti:dataGridCell>
                    
                    <c:if test="${showEditing}">
                        <cti:dataGridCell>
                            <tags:sectionContainer2 nameKey="header.editing" styleClass="w30">
                                <table>
                                    <cti:checkRolesAndProperties value="MASS_CHANGE">
                                        <%-- MASS CHANGE --%>
                                        <tags:collectionActionTr linkKey=".massChangeLabel" descriptionKey=".massChangeDescription" icon="icon-app icon-app-32-database-edit"
                                            action="/bulk/massChange/massChangeSelect" deviceCollection="${deviceCollection}"/>
                                        
                                        <%-- CHANGE TYPE --%>
                                        <tags:collectionActionTr linkKey=".changeDeviceTypeLabel" descriptionKey=".changeDeviceTypeDescription" icon="icon-app icon-app-32-database-refresh"
                                            action="/bulk/changeDeviceType/chooseDeviceType" deviceCollection="${deviceCollection}"/>
                                    </cti:checkRolesAndProperties>
                                    
                                    <cti:checkRolesAndProperties value="MASS_DELETE">
                                        <%-- MASS DELETE --%>
                                        <tags:collectionActionTr linkKey=".massDeleteLabel" descriptionKey=".massDeleteDescription" icon="icon-app icon-app-32-database-delete"
                                            action="/bulk/massDelete/massDelete" deviceCollection="${deviceCollection}"/>
                                    </cti:checkRolesAndProperties>
                                </table>
                            </tags:sectionContainer2>
                        </cti:dataGridCell>
                    </c:if>
                    
                    <c:if test="${showGroupManagement}" >
                        <cti:dataGridCell>
                            <tags:sectionContainer2 nameKey="header.groupManagement" styleClass="w30">
                                <table>
                                    <%-- ADD TO GROUP --%>
                                    <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
                                        <tags:collectionActionTr inputValue="ADD" linkKey=".addToGroupLabel" descriptionKey=".addToGroupDescription" inputName="addRemove" 
                                            action="/bulk/group/selectGroup" deviceCollection="${deviceCollection}" icon="icon-app icon-app-32-folder-add"/>
                                    </cti:checkRolesAndProperties>
                                    
                                    <%-- REMOVE FROM GROUP --%>
                                    <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
    	                                <tags:collectionActionTr inputValue="REMOVE" linkKey=".removeFromGroupLabel" descriptionKey=".removeFromGroupDescription" inputName="addRemove" 
                                            action="/bulk/group/selectGroup" deviceCollection="${deviceCollection}" icon="icon-app icon-app-32-folder-delete"/>
                                    </cti:checkRolesAndProperties>
                                </table>
                            </tags:sectionContainer2>
                        </cti:dataGridCell>
                    </c:if>
     
                    <c:if test="${showAddRemovePoints}">
                        <cti:dataGridCell>
                            <tags:sectionContainer2 nameKey="header.pointActions" styleClass="w30">
                                <table>
                                    <%-- ADD POINTS --%>
                                    <tags:collectionActionTr linkKey=".addPointsLabel" descriptionKey=".addPointsDescription" icon="icon-app icon-app-32-points-add"
                                        action="/bulk/addPoints/home" deviceCollection="${deviceCollection}"/>
                                    
                                    <%-- UPDATE POINTS --%>
                                    <tags:collectionActionTr linkKey=".updatePointsLabel" descriptionKey=".updatePointsDescription" icon="icon-app icon-app-32-points-update"
                                        action="/bulk/updatePoints/home" deviceCollection="${deviceCollection}"/>
                                    
                                    <%-- REMOVE POINTS --%>
                                    <tags:collectionActionTr linkKey=".removePointsLabel" descriptionKey=".removePointsDescription" icon="icon-app icon-app-32-points-delete"
                                        action="/bulk/removePoints/home" deviceCollection="${deviceCollection}"/>
                                </table>
                            </tags:sectionContainer2>
                        </cti:dataGridCell>
                    </c:if>
                    
                    <cti:dataGridCell>
                        <tags:sectionContainer2 nameKey="header.reporting" styleClass="w30">
                            <table>
                                <%-- DEVICE REPORT --%>
                                <tags:collectionActionTr linkKey=".deviceCollectionReportLabel" descriptionKey=".deviceCollectionReportDescription" icon="icon-app icon-app-32-database-table"
                                    action="/bulk/deviceCollectionReport" deviceCollection="${deviceCollection}"/>
                                
                                <cti:checkRolesAndProperties value="ARCHIVED_DATA_ANALYSIS">
                                    <%-- DATA ANALYSIS --%>
                                    <tags:collectionActionTr linkKey=".dataAnalysisLabel" descriptionKey=".dataAnalysisDescription" icon="icon-app icon-app-32-report-magnify"
                                        action="/bulk/archiveDataAnalysis/home/setup" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>        
                                
                                <cti:checkRolesAndProperties value="ARCHIVED_DATA_EXPORT">
                                    <%-- ARCHIVED DATA EXPORT --%>
                                    <tags:collectionActionTr linkKey=".archivedValueDataExporterLabel" descriptionKey=".archivedValueDataExporterDescription" icon="icon-app icon-app-32-page-excel"
                                        action="/tools/data-exporter/view" deviceCollection="${deviceCollection}"/>
                                </cti:checkRolesAndProperties>
                                
                                <%-- MAP DEVICES --%>
                                <tags:collectionActionTr linkKey=".deviceCollectionMapLabel" descriptionKey=".deviceCollectionMapDescription" icon="icon-app icon-app-32-map"
                                    action="/tools/map" deviceCollection="${deviceCollection}"/>
                            </table>
                        </tags:sectionContainer2>
                    </cti:dataGridCell>
    
                </cti:checkRolesAndProperties>
    
            </cti:dataGrid>
        </tags:bulkActionContainer>
    </cti:msgScope>
</cti:standardPage>