<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.collectionActions.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <cti:msg var="collectionActionsPageTitle" key="yukon.common.device.bulk.collectionActions.pageTitle"/>
        <cti:crumbLink>${collectionActionsPageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>

    <tags:bulkActionContainer   key="yukon.common.device.bulk.collectionActions" deviceCollection="${deviceCollection}">
        
        <%-- LABELS AND DESCRIPTIONS --%>
        <cti:msg var="addToGroupLabel" key="yukon.common.device.bulk.collectionActions.addToGroupLabel"/>
        <cti:msg var="addToGroupDescription" key="yukon.common.device.bulk.collectionActions.addToGroupDescription"/>
        <cti:msg var="removeFromGroupLabel" key="yukon.common.device.bulk.collectionActions.removeFromGroupLabel"/>
        <cti:msg var="removeFromGroupDescription" key="yukon.common.device.bulk.collectionActions.removeFromGroupDescription"/>
        <cti:msg var="sendCommandLabel" key="yukon.common.device.bulk.collectionActions.sendCommandLabel"/>
        <cti:msg var="sendCommandDescription" key="yukon.common.device.bulk.collectionActions.sendCommandDescription"/>
        <cti:msg var="readAttributeLabel" key="yukon.common.device.bulk.collectionActions.readAttributeLabel"/>
        <cti:msg var="readAttributeDescription" key="yukon.common.device.bulk.collectionActions.readAttributeDescription"/>
        <cti:msg var="massChangeLabel" key="yukon.common.device.bulk.collectionActions.massChangeLabel"/>
        <cti:msg var="massChangeDescription" key="yukon.common.device.bulk.collectionActions.massChangeDescription"/>
        <cti:msg var="changeDeviceTypeLabel" key="yukon.common.device.bulk.collectionActions.changeDeviceTypeLabel"/>
        <cti:msg var="changeDeviceTypeDescription" key="yukon.common.device.bulk.collectionActions.changeDeviceTypeDescription"/>
        <cti:msg var="routeLocateLabel" key="yukon.common.device.bulk.collectionActions.routeLocateLabel"/>
        <cti:msg var="routeLocateDescription" key="yukon.common.device.bulk.collectionActions.routeLocateDescription"/>
        <cti:msg var="addPointsLabel" key="yukon.common.device.bulk.collectionActions.addPointsLabel"/>
        <cti:msg var="addPointsDescription" key="yukon.common.device.bulk.collectionActions.addPointsDescription"/>
        <cti:msg var="removePointsLabel" key="yukon.common.device.bulk.collectionActions.removePointsLabel"/>
        <cti:msg var="removePointsDescription" key="yukon.common.device.bulk.collectionActions.removePointsDescription"/>
        <cti:msg var="updatePointsLabel" key="yukon.common.device.bulk.collectionActions.updatePointsLabel"/>
        <cti:msg var="updatePointsDescription" key="yukon.common.device.bulk.collectionActions.updatePointsDescription"/>
        <cti:msg var="massDeleteLabel" key="yukon.common.device.bulk.collectionActions.massDeleteLabel"/>
        <cti:msg var="massDeleteDescription" key="yukon.common.device.bulk.collectionActions.massDeleteDescription"/>
        <cti:msg var="deviceCollectionReportLabel" key="yukon.common.device.bulk.collectionActions.deviceCollectionReportLabel"/>
        <cti:msg var="deviceCollectionReportDescription" key="yukon.common.device.bulk.collectionActions.deviceCollectionReportDescription"/>
        <cti:msg var="assignConfigLabel" key="yukon.common.device.bulk.collectionActions.assignConfigLabel"/>
        <cti:msg var="assignConfigDescription" key="yukon.common.device.bulk.collectionActions.assignConfigDescription"/>
        <cti:msg var="sendConfigLabel" key="yukon.common.device.bulk.collectionActions.sendConfigLabel"/>
        <cti:msg var="sendConfigDescription" key="yukon.common.device.bulk.collectionActions.sendConfigDescription"/>
        <cti:msg var="readConfigLabel" key="yukon.common.device.bulk.collectionActions.readConfigLabel"/>
        <cti:msg var="readConfigDescription" key="yukon.common.device.bulk.collectionActions.readConfigDescription"/>
        <cti:msg var="verifyConfigLabel" key="yukon.common.device.bulk.collectionActions.verifyConfigLabel"/>
        <cti:msg var="verifyConfigDescription" key="yukon.common.device.bulk.collectionActions.verifyConfigDescription"/>
        <cti:msg var="dataAnalysisLabel" key="yukon.common.device.bulk.collectionActions.dataAnalysisLabel"/>
        <cti:msg var="dataAnalysisDiscription" key="yukon.common.device.bulk.collectionActions.dataAnalysisDiscription"/>
        
        <cti:dataGrid cols="2" tableClasses="collectionActionAlignment collectionActionCellPadding">
            <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                <c:if test="${showGroupManagement}" >
                    <cti:dataGridCell>
                        <tags:sectionContainer title="Group Management">
                            <table>
                                <%-- ADD TO GROUP --%>
                                <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
                                    <tags:collectionActionTr inputValue="ADD" buttonValue="${addToGroupLabel}" inputName="addRemove" 
                                        description="${addToGroupDescription}" action="/spring/bulk/group/selectGroup" deviceCollection="${deviceCollection}"/>
                                </cti:checkProperty>
                                
                                <%-- REMOVE FROM GROUP --%>
                                <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
	                                <tags:collectionActionTr inputValue="REMOVE" buttonValue="${removeFromGroupLabel}" inputName="addRemove" 
	                                        description="${removeFromGroupDescription}" action="/spring/bulk/group/selectGroup" deviceCollection="${deviceCollection}"/>
                                </cti:checkProperty>
                            </table>
                        </tags:sectionContainer>
                    </cti:dataGridCell>
                </c:if>
                
                <c:if test="${showEditing}">
                    <cti:dataGridCell>
                        <tags:sectionContainer title="Editing">
                            <table>
                                <%-- MASS CHANGE --%>
                                <cti:checkProperty property="operator.DeviceActionsRole.MASS_CHANGE">
                                    <tags:collectionActionTr buttonValue="${massChangeLabel}" description="${massChangeDescription}"
                                        action="/spring/bulk/massChange/massChangeSelect" deviceCollection="${deviceCollection}"/>
                                    
                                    <%-- CHANGE TYPE --%>
                                    <tags:collectionActionTr buttonValue="${changeDeviceTypeLabel}" description="${changeDeviceTypeDescription}"
                                        action="/spring/bulk/changeDeviceType/chooseDeviceType" deviceCollection="${deviceCollection}"/>
                                </cti:checkProperty>
                                
                                <%-- MASS DELETE --%>
                                <cti:checkProperty property="operator.DeviceActionsRole.MASS_DELETE">
                                    <tags:collectionActionTr buttonValue="${massDeleteLabel}" description="${massDeleteDescription}"
                                        action="/spring/bulk/massDelete/massDelete" deviceCollection="${deviceCollection}"/>
                                </cti:checkProperty>
                            </table>
                        </tags:sectionContainer>
                    </cti:dataGridCell>
                </c:if>
                
                <cti:dataGridCell>
                    <tags:sectionContainer title="Commands">
                        <table>
                            <%-- GROUP COMMANDER --%>
                            <cti:checkProperty property="operator.DeviceActionsRole.GROUP_COMMANDER">
                                <tags:collectionActionTr buttonValue="${sendCommandLabel}" description="${sendCommandDescription}"
                                    action="/spring/group/commander/collectionProcessing" deviceCollection="${deviceCollection}"/>
                            </cti:checkProperty>
                            
                            <%-- GROUP ATTRIBUTE READ --%>
                            <tags:collectionActionTr buttonValue="${readAttributeLabel}" description="${readAttributeDescription}"
                                    action="/spring/group/groupMeterRead/homeCollection" deviceCollection="${deviceCollection}"/>
                            
                            <%-- LOCATE ROUTE --%>
                            <cti:checkProperty property="operator.DeviceActionsRole.LOCATE_ROUTE">
                                <tags:collectionActionTr buttonValue="${routeLocateLabel}" description="${routeLocateDescription}"
                                    action="/spring/bulk/routeLocate/home" deviceCollection="${deviceCollection}"/>
                            </cti:checkProperty>
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
                
                <cti:dataGridCell>
                    <tags:sectionContainer title="Config Actions">
                        <table>
                            <%-- ASSIGN CONFIG --%>
                            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                                <tags:collectionActionTr buttonValue="${assignConfigLabel}" description="${assignConfigDescription}"
                                    action="/spring/bulk/config/assignConfig" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                            
                            <%-- SEND CONFIG --%>
                            <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                                <tags:collectionActionTr buttonValue="${sendConfigLabel}" description="${sendConfigDescription}"
                                    action="/spring/bulk/config/sendConfig" deviceCollection="${deviceCollection}"/>
                            
                            <%-- READ CONFIG --%>
                                <tags:collectionActionTr buttonValue="${readConfigLabel}" description="${readConfigDescription}"
                                    action="/spring/bulk/config/readConfig" deviceCollection="${deviceCollection}"/>
                            </cti:checkRolesAndProperties>
                            
                            <%-- VERIFY CONFIG --%>
                            <tags:collectionActionTr buttonValue="${verifyConfigLabel}" description="${verifyConfigDescription}"
                                action="/spring/bulk/config/verifyConfig" deviceCollection="${deviceCollection}"/>
                        </table>
                    </tags:sectionContainer>
                </cti:dataGridCell>
            </cti:checkRole>
            <cti:dataGridCell>
                <tags:sectionContainer title="Reporting">
                    <table>
                        <%-- DEVICE REPORT --%>
                        <tags:collectionActionTr buttonValue="${deviceCollectionReportLabel}" description="${deviceCollectionReportDescription}"
                            action="/spring/bulk/deviceCollectionReport" deviceCollection="${deviceCollection}"/>
                        <%-- DATA ANALYSIS --%>
                        <tags:collectionActionTr buttonValue="${dataAnalysisLabel}" description="${dataAnalysisDiscription}"
                            action="/spring/bulk/archiveDataAnalysis/home" deviceCollection="${deviceCollection}"/>
                    </table>
                </tags:sectionContainer>
            </cti:dataGridCell>
            
            <c:if test="${showAddRemovePoints}">
                    <cti:dataGridCell>
                        <tags:sectionContainer title="Add/Remove/Update Points">
                            <table>
                                <%-- ADD POINTS --%>
                                <tags:collectionActionTr buttonValue="${addPointsLabel}" description="${addPointsDescription}"
                                    action="/spring/bulk/addPoints/home" deviceCollection="${deviceCollection}"/>
                                
                                <%-- UPDATE POINTS --%>
                                <tags:collectionActionTr buttonValue="${updatePointsLabel}" description="${updatePointsDescription}"
                                    action="/spring/bulk/updatePoints/home" deviceCollection="${deviceCollection}"/>
                                
                                <%-- REMOVE POINTS --%>
                                <tags:collectionActionTr buttonValue="${removePointsLabel}" description="${removePointsDescription}"
                                    action="/spring/bulk/removePoints/home" deviceCollection="${deviceCollection}"/>
                            </table>
                        </tags:sectionContainer>
                    </cti:dataGridCell>
                </c:if>
        </cti:dataGrid>
        
    </tags:bulkActionContainer>
    
</cti:standardPage>