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

    <tags:bulkActionContainer   titleKey="yukon.common.device.bulk.collectionActions.header" 
                                noteLabelKey="yukon.common.device.bulk.collectionActions.noteLabel"
                                noteTextKey="yukon.common.device.bulk.collectionActions.noteText"
                                deviceCollection="${deviceCollection}">
        
        <%-- LABELS AND DESCRIPTIONS --%>
        <cti:msg var="addToGroupLabel" key="yukon.common.device.bulk.collectionActions.addToGroupLabel"/>
        <cti:msg var="addToGroupDescription" key="yukon.common.device.bulk.collectionActions.addToGroupDescription"/>
        <cti:msg var="removeFromGroupLabel" key="yukon.common.device.bulk.collectionActions.removeFromGroupLabel"/>
        <cti:msg var="removeFromGroupDescription" key="yukon.common.device.bulk.collectionActions.removeFromGroupDescription"/>
        <cti:msg var="sendCommandLabel" key="yukon.common.device.bulk.collectionActions.sendCommandLabel"/>
        <cti:msg var="sendCommandDescription" key="yukon.common.device.bulk.collectionActions.sendCommandDescription"/>
        <cti:msg var="massChangeLabel" key="yukon.common.device.bulk.collectionActions.massChangeLabel"/>
        <cti:msg var="massChangeDescription" key="yukon.common.device.bulk.collectionActions.massChangeDescription"/>
        <cti:msg var="changeDeviceTypeLabel" key="yukon.common.device.bulk.collectionActions.changeDeviceTypeLabel"/>
        <cti:msg var="changeDeviceTypeDescription" key="yukon.common.device.bulk.collectionActions.changeDeviceTypeDescription"/>
        <cti:msg var="routeLocateLabel" key="yukon.common.device.bulk.collectionActions.routeLocateLabel"/>
        <cti:msg var="routeLocateDescription" key="yukon.common.device.bulk.collectionActions.routeLocateDescription"/>
        <cti:msg var="massDeleteLabel" key="yukon.common.device.bulk.collectionActions.massDeleteLabel"/>
        <cti:msg var="massDeleteDescription" key="yukon.common.device.bulk.collectionActions.massDeleteDescription"/>
        
        <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
        <table cellspacing="10">
        
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
                    <form id="routeLocateForm" method="get" action="/spring/csr/routeLocate/home">
                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
                        <input type="submit" id="routeLocateButton" value="${routeLocateLabel}" style="width:140px;"/>
                    </form>
                </td>
                <td>${routeLocateDescription}</td>
            </tr>
            </cti:checkProperty>
            
            <%-- MASS DELETE --%>
            <cti:checkProperty property="operator.DeviceActionsRole.MASS_DELETE">
            <tr>
                <td>
                    <form id="massDeleteForm" method="get" action="/spring/bulk/massChange/massDelete">
                        <cti:deviceCollection deviceCollection="${deviceCollection}" />
                        <input type="submit" id="massDeleteButton" value="${massDeleteLabel}" style="width:140px;"/>
                    </form>
                </td>
                <td>${massDeleteDescription}</td>
            </tr>
            </cti:checkProperty>
            
        </table>
        </cti:checkRole>
        
    </tags:bulkActionContainer>
    
</cti:standardPage>