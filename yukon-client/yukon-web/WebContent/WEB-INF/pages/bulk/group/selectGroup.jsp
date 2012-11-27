<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<c:choose>
<c:when test="${addRemove eq 'ADD'}">
    <cti:msg var="pageTitle" key="yukon.common.device.bulk.addToGroup.pageTitle"/>
    <c:set var="containerKey" value="yukon.common.device.bulk.addToGroup"/>
    <cti:msg var="buttonText" key="yukon.common.device.bulk.addToGroup.addToGroupButtonText"/>
    <cti:msg var="noDeviceGroupSelectedAlertText" key="yukon.common.device.bulk.addToGroup.noDeviceGroupSelectedAlertText"/>
    <c:set var="formAction" value="/bulk/group/addToGroup"/>
</c:when>
<c:when test="${addRemove eq 'REMOVE'}">
    <cti:msg var="pageTitle" key="yukon.common.device.bulk.removeFromGroup.pageTitle"/>
    <c:set var="containerKey" value="yukon.common.device.bulk.removeFromGroup"/>
    <cti:msg var="buttonText" key="yukon.common.device.bulk.removeFromGroup.removeFromGroupButtonText"/>
    <cti:msg var="noDeviceGroupSelectedAlertText" key="yukon.common.device.bulk.removeFromGroup.noDeviceGroupSelectedAlertText"/>
    <c:set var="formAction" value="/bulk/group/removeFromGroup"/>
</c:when>
</c:choose>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- add to group --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">
    
        function validateGroupIsSelected(btn, alertText) {
        
            if ($('groupName').value == '') {
                alert(alertText);
                return false;
            }
            
            btn.disabled = true;
            $('selectGroupForm').submit();
        }
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <tags:bulkActionContainer   key="${containerKey}" 
                                deviceCollection="${deviceCollection}">
    
        <form id="selectGroupForm" action="${formAction}" method="post">
            
             <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" /> 
                                
            <%-- SELECT DEVICE GROUP TREE INPUT --%>
            <cti:deviceGroupHierarchyJson predicates="MODIFIABLE" var="dataJson" />                                              
            <jsTree:nodeValueSelectingInlineTree   fieldId="groupName"
                                                fieldName="groupName"
                                                nodeValueName="groupName"
                                                multiSelect="false"
                                                
                                                id="selectGroupTree"
                                                dataJson="${dataJson}"
                                                width="500"
                                                height="400"
                                                includeControlBar="true"/>
                            
            <%-- ADD/REMOVE BUTTON --%>
            <input type="button" name="addRemoveButton" value="${buttonText}" onclick="return validateGroupIsSelected(this, '${noDeviceGroupSelectedAlertText}');">
    
        </form>
    
   </tags:bulkActionContainer>
    
</cti:standardPage>