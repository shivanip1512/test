<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.tools.collectionActions">

    <c:if test="${not empty deviceCollection}">
        <div class="js-device-collection-template dn">
            <tags:selectedDevicesPopup deviceCollection="${deviceCollection}"/>
        </div>
    </c:if>

    <cti:flashScopeMessages/>

    <c:if test="${!empty deviceCollection}">
        <input type="hidden" id="deviceCollectionCount" value="${deviceCollection.deviceCount}"/>
        <cti:msg2 var="deviceDescription" key="${deviceCollection.description}"/>
        <input type="hidden" id="deviceCollectionDescription" value="${fn:escapeXml(deviceDescription)}"/>
        <cti:url value="/collectionActions/home" var="refreshLink">
            <cti:mapParam value="${deviceCollection.collectionParameters}"/>
        </cti:url>
        <input type="hidden" id="refreshLink" value="${refreshLink}"/>
        <c:if test="${!empty redirectUrl}">
            <cti:url value="${redirectUrl}" var="redirectLink">
                <cti:mapParam value="${deviceCollection.collectionParameters}"/>
            </cti:url>
            <input type="hidden" id="redirectUrl" value="${redirectLink}"/>
            <input type="hidden" id="actionString" value="${actionString}"/>
        </c:if>
    
        <%-- THE GRID --%>
        <cti:dataGrid cols="3">
            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                <cti:dataGridCell>
                    <tags:sectionContainer2 nameKey="header.commands" styleClass="w300">
                        <table>
                            <cti:checkRolesAndProperties value="GROUP_COMMANDER">
                                <%-- GROUP COMMANDER --%>
                                <tags:collectionActionTr linkKey=".sendCommandLabel" descriptionKey=".sendCommandDescription" 
                                    icon="icon-app icon-app-32-page-go" action="/group/commander/sendCommandInputs" 
                                    deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                            </cti:checkRolesAndProperties>
                            
                            <%-- GROUP ATTRIBUTE READ --%>
                            <tags:collectionActionTr linkKey=".readAttributeLabel" descriptionKey=".readAttributeDescription" 
                                icon="icon-app icon-app-32-page-magnify" action="/group/groupMeterRead/readAttributeInputs" 
                                deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                            
                            <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                                <%-- LOCATE ROUTE --%>
                                <tags:collectionActionTr linkKey=".routeLocateLabel" descriptionKey=".routeLocateDescription" 
                                    icon="icon-app icon-app-32-pin" action="/bulk/routeLocate/routeLocateInputs" 
                                    deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                            </cti:checkRolesAndProperties>
    
                            <%-- DISCONNECT --%>
                            <cti:checkRolesAndProperties value="GROUP_DISCONNECT_CONTROL">
                                <tags:collectionActionTr linkKey=".disconnectLabel" descriptionKey=".disconnectDescription" 
                                    icon="icon-app icon-app-32-disconnect" action="/bulk/disconnect/disconnectInputs" 
                                    deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                            </cti:checkRolesAndProperties>
                            
                            <%-- DEMAND RESET--%>
                            <cti:checkRolesAndProperties value="GROUP_DEMAND_RESET">
                                <tags:collectionActionTr linkKey=".demandResetLabel" descriptionKey=".demandResetDescription" 
                                    icon="icon-app icon-app-32-update" action="/bulk/demand-reset/actionInputs" 
                                    deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                            </cti:checkRolesAndProperties>
    
                        </table>
                    </tags:sectionContainer2>
                </cti:dataGridCell>
                
                <c:set var="showConfigSection" value="false"/>
                <cti:checkRolesAndProperties value="MASS_CHANGE">
                    <c:set var="showConfigSection" value="true"/>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                    <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
                        <c:set var="showConfigSection" value="true"/>
                    </cti:checkRolesAndProperties>
                </cti:checkRolesAndProperties>
                <c:if test="${showConfigSection}">
                    <cti:dataGridCell>
                        <tags:sectionContainer2 nameKey="header.configActions" styleClass="w300">
                            <table>
                                <cti:checkRolesAndProperties value="MASS_CHANGE">
                                    <%-- DEVICE CONFIGS --%>
                                    <tags:collectionActionTr linkKey=".deviceConfigsLabel" descriptionKey=".deviceConfigsDescription" 
                                        icon="icon-app icon-app-32-cog" action="/bulk/config/deviceConfigsInputs" 
                                        deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                </cti:checkRolesAndProperties>
                                
                                <%--DATA STREAMING - check for CPARM and ROLE PROPERTY--%>
                                <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                                    <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
                                        <%-- CONFIGURE DATA STREAMING --%>
                                        <tags:collectionActionTr linkKey=".configureDataStreamingLabel" 
                                            descriptionKey=".configureDataStreamingDescription" icon="icon-app icon-app-32-datastreaming-configure" 
                                            action="/bulk/dataStreaming/configureInputs"  deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                        
                                        <%-- REMOVE DATA STREAMING --%>
                                        <tags:collectionActionTr linkKey=".removeDataStreamingLabel" 
                                            descriptionKey=".removeDataStreamingDescription" icon="icon-app icon-app-32-datastreaming-remove"
                                            action="/bulk/dataStreaming/removeInputs" deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                    </cti:checkRolesAndProperties>
                                </cti:checkRolesAndProperties>
                            </table>
                        </tags:sectionContainer2>
                    </cti:dataGridCell>
                </c:if>
                
                <cti:checkRolesAndProperties value="MASS_CHANGE,MASS_DELETE">
                    <cti:dataGridCell>
                        <tags:sectionContainer2 nameKey="header.editing" styleClass="w300">
                            <table>
                                <cti:checkRolesAndProperties value="MASS_CHANGE">
                                    <%-- MASS CHANGE --%>
                                    <tags:collectionActionTr linkKey=".massChangeLabel" descriptionKey=".massChangeDescription" 
                                        icon="icon-app icon-app-32-database-edit" action="/bulk/massChange/massChangeSelectInputs" 
                                        deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                    
                                    <%-- CHANGE TYPE --%>
                                    <tags:collectionActionTr linkKey=".changeDeviceTypeLabel" descriptionKey=".changeDeviceTypeDescription" 
                                        icon="icon-app icon-app-32-database-refresh" action="/bulk/changeDeviceType/chooseDeviceTypeInputs" 
                                        deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                </cti:checkRolesAndProperties>
                                
                                <cti:checkRolesAndProperties value="MASS_DELETE">
                                    <%-- MASS DELETE --%>
                                    <tags:collectionActionTr linkKey=".massDeleteLabel" descriptionKey=".massDeleteDescription" 
                                        icon="icon-app icon-app-32-database-delete" action="/bulk/massDelete/massDeleteInputs" 
                                        deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                </cti:checkRolesAndProperties>
                            </table>
                        </tags:sectionContainer2>
                    </cti:dataGridCell>
                </cti:checkRolesAndProperties>
                
                <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
                    <cti:dataGridCell>
                        <tags:sectionContainer2 nameKey="header.groupManagement" styleClass="w300">
                            <table>
                                <%-- ADD TO GROUP --%>
                                <tags:collectionActionTr inputValue="ADD" linkKey=".addToGroupLabel" 
                                    descriptionKey=".addToGroupDescription" inputName="addRemove" action="/bulk/group/selectGroup"
                                    deviceCollection="${deviceCollection}" icon="icon-app icon-app-32-folder-add"/>
                            
                                <%-- REMOVE FROM GROUP --%>
                                <tags:collectionActionTr inputValue="REMOVE" linkKey=".removeFromGroupLabel" 
                                    descriptionKey=".removeFromGroupDescription" inputName="addRemove" 
                                    action="/bulk/group/selectGroup" deviceCollection="${deviceCollection}" 
                                    icon="icon-app icon-app-32-folder-delete"/>
                            </table>
                        </tags:sectionContainer2>
                    </cti:dataGridCell>
                </cti:checkRolesAndProperties>
    
                <cti:checkRolesAndProperties value="ADD_REMOVE_POINTS">
                    <cti:dataGridCell>
                        <tags:sectionContainer2 nameKey="header.pointActions" styleClass="w300">
                            <table>
                                <%-- ADD POINTS --%>
                                <tags:collectionActionTr linkKey=".addPointsLabel" descriptionKey=".addPointsDescription" 
                                    icon="icon-app icon-app-32-points-add" action="/bulk/addPoints/addPointsInputs" 
                                    deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                
                                <%-- UPDATE POINTS --%>
                                <tags:collectionActionTr linkKey=".updatePointsLabel" descriptionKey=".updatePointsDescription" 
                                    icon="icon-app icon-app-32-points-update" action="/bulk/updatePoints/updatePointsInputs" 
                                    deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                                
                                <%-- REMOVE POINTS --%>
                                <tags:collectionActionTr linkKey=".removePointsLabel" descriptionKey=".removePointsDescription" 
                                    icon="icon-app icon-app-32-points-delete" action="/bulk/removePoints/removePointsInputs" 
                                    deviceCollection="${deviceCollection}" ajaxSubmit="true"/>
                            </table>
                        </tags:sectionContainer2>
                    </cti:dataGridCell>
                </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>
            
            <cti:dataGridCell>
                <tags:sectionContainer2 nameKey="header.reporting" styleClass="w300">
                    <table>
                        <%-- DEVICE REPORT --%>
                        <tags:collectionActionTr linkKey=".deviceCollectionReportLabel" 
                            descriptionKey=".deviceCollectionReportDescription" icon="icon-app icon-app-32-database-table" 
                            action="/bulk/deviceCollectionReport" deviceCollection="${deviceCollection}"/>
                        
                        <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                            <cti:checkRolesAndProperties value="ARCHIVED_DATA_ANALYSIS">
                                <%-- DATA ANALYSIS --%>
                                <tags:collectionActionTr linkKey=".dataAnalysisLabel" descriptionKey=".dataAnalysisDescription" 
                                    icon="icon-app icon-app-32-report-magnify" action="/bulk/archiveDataAnalysis/home/setup" 
                                    deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                        
                            <cti:checkRolesAndProperties value="ARCHIVED_DATA_EXPORT">
                                <%-- ARCHIVED DATA EXPORT --%>
                                <tags:collectionActionTr linkKey=".archivedValueDataExporterLabel" 
                                    descriptionKey=".archivedValueDataExporterDescription" icon="icon-app icon-app-32-page-excel"
                                    action="/tools/data-exporter/view" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                        </cti:checkRolesAndProperties>
                        
                        <%-- MAP DEVICES --%>
                        <tags:collectionActionTr linkKey=".deviceCollectionMapLabel" descriptionKey=".deviceCollectionMapDescription" 
                            icon="icon-app icon-app-32-map" action="/tools/map" deviceCollection="${deviceCollection}"/>
                    </table>
                </tags:sectionContainer2>
            </cti:dataGridCell>
    
        </cti:dataGrid>
    
    </c:if>

</cti:msgScope>
