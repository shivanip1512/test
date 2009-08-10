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
        
        <table cellspacing="10">
        
            <cti:checkRole role="operator.DeviceActionsRole.ROLEID">        
            
	            <%-- ADD TO GROUP --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
	            <tr>
	                <td>
	                    <form id="deviceGroupAddForm" method="get" action="/spring/bulk/group/selectGroup">
	                        <input type="hidden" name="addRemove" value="ADD" />
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="groupEditorButton" value="${addToGroupLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${addToGroupDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- REMOVE FROM GROUP --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
	            <tr>
	                <td>
	                    <form id="deviceGroupRemoveForm" method="get" action="/spring/bulk/group/selectGroup">
	                        <input type="hidden" name="addRemove" value="REMOVE" />
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="groupEditorButton" value="${removeFromGroupLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${removeFromGroupDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- GROUP COMMANDER --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.GROUP_COMMANDER">
	            <tr>
	                <td>
	                    <form id="groupCommanderForm" method="get" action="/spring/group/commander/collectionProcessing">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="groupCommanderButton" value="${sendCommandLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${sendCommandDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- GROUP ATTRIBUTE READ --%>
	            <tr>
	                <td>
	                    <form id="groupAttributeReadForm" method="get" action="/spring/group/groupMeterRead/homeCollection">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="groupCommanderButton" value="${readAttributeLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${readAttributeDescription}</td>
	            </tr>
	            
	            <%-- MASS CHANGE --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.MASS_CHANGE">
	            <tr>
	                <td>
	                    <form id="massChangeForm" method="get" action="/spring/bulk/massChange/massChangeSelect">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="massChangeButton" value="${massChangeLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${massChangeDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- CHANGE TYPE --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.MASS_CHANGE">
	            <tr>
	                <td>
	                    <form id="changeDeviceTypeForm" method="get" action="/spring/bulk/changeDeviceType/chooseDeviceType">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="changeDeviceTypeButton" value="${changeDeviceTypeLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${changeDeviceTypeDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- LOCATE ROUTE --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.LOCATE_ROUTE">
	            <tr>
	                <td>
	                    <form id="routeLocateForm" method="get" action="/spring/bulk/routeLocate/home">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="routeLocateButton" value="${routeLocateLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${routeLocateDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- ADD POINTS --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.ADD_REMOVE_POINTS">
	            <tr>
	                <td>
	                    <form id="addPointsForm" method="get" action="/spring/bulk/addPoints/home">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="addPointsButton" value="${addPointsLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${addPointsDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- REMOVE POINTS --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.ADD_REMOVE_POINTS">
	            <tr>
	                <td>
	                    <form id="removePointsForm" method="get" action="/spring/bulk/removePoints/home">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="removePointsButton" value="${removePointsLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${removePointsDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- MASS DELETE --%>
	            <cti:checkProperty property="operator.DeviceActionsRole.MASS_DELETE">
	            <tr>
	                <td>
	                    <form id="massDeleteForm" method="get" action="/spring/bulk/massDelete/massDelete">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="massDeleteButton" value="${massDeleteLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${massDeleteDescription}</td>
	            </tr>
	            </cti:checkProperty>
	            
	            <%-- ASSIGN CONFIG --%>
	            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
	            <tr>
	                <td>
	                    <form id="assignConfigForm" method="get" action="/spring/bulk/config/assignConfig">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="assignConfigButton" value="${assignConfigLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${assignConfigDescription}</td>
	            </tr>
	            </cti:checkRolesAndProperties>
	            
	            <%-- SEND CONFIG --%>
	            <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
	            <tr>
	                <td>
	                    <form id="sendConfigForm" method="get" action="/spring/bulk/config/sendConfig">
	                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
	                        <input type="submit" id="sendConfigButton" value="${sendConfigLabel}" style="width:140px;"/>
	                    </form>
	                </td>
	                <td>${sendConfigDescription}</td>
	            </tr>
	            
	            <%-- READ CONFIG --%>
                <tr>
                    <td>
                        <form id="readConfigForm" method="get" action="/spring/bulk/config/readConfig">
                            <cti:deviceCollection deviceCollection="${deviceCollection}" />
                            <input type="submit" id="readConfigButton" value="${readConfigLabel}" style="width:140px;"/>
                        </form>
                    </td>
                    <td>${readConfigDescription}</td>
                </tr>
                </cti:checkRolesAndProperties>
                
                <%-- VERIFY CONFIG --%>
                <tr>
                    <td>
                        <form id="verifyConfigForm" method="get" action="/spring/bulk/config/verifyConfig">
                            <cti:deviceCollection deviceCollection="${deviceCollection}" />
                            <input type="submit" id="verifyConfigButton" value="${verifyConfigLabel}" style="width:140px;"/>
                        </form>
                    </td>
                    <td>${verifyConfigDescription}</td>
                </tr>
            
            </cti:checkRole>

            <%-- DEVICE REPORT --%>
            <tr>
                <td>
                    <form id="deviceCollectionReportForm" method="get" action="/spring/bulk/deviceCollectionReport">
                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
                        <input type="submit" id="deviceCollectionReportButton" value="${deviceCollectionReportLabel}" style="width:140px;"/>
                    </form>
                </td>
                <td>${deviceCollectionReportDescription}</td>
            </tr>
        </table>
        
    </tags:bulkActionContainer>
    
</cti:standardPage>