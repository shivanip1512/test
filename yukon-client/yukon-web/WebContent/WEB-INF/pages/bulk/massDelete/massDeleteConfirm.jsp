<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.massDeleteConfirm.pageTitle"/>

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
        
        <%-- mass delete --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <%-- TITLE --%>
    <h2>${pageTitle}</h2>
    <br>
    
    <%-- BOX --%>
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.massDeleteConfirm.header"/>
    <tags:boxContainer title="${headerTitle}" id="collectionActionsContainer" hideEnabled="false">
    
        <%-- SELECTED DEVICES POPUP, NOTE TO USER --%>
        <table cellpadding="2">
        
            <tr>
                <td valign="top" colspan="2" class="smallBoldLabel">
                    <tags:selectedDevices deviceCollection="${deviceCollection}" id="selectedDevices"/>
                </td>
            </tr>
            
            <%-- NOTE --%>
            <tr>
                <td valign="top" class="smallBoldLabel errorRed">
                    <cti:msg key="yukon.common.device.bulk.massDeleteConfirm.noteLabel"/>
                </td>
                <td style="font-size:11px;">
                    <cti:msg key="yukon.common.device.bulk.massDeleteConfirm.noteText" arguments="${deviceCount}" />
                </td>
            </tr>
        </table>
        <br>
    
        <form id="massDeleteForm" method="post" action="/bulk/massDelete/doMassDelete">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- DELETE BUTTON --%>
            <cti:msg var="delete" key="yukon.common.device.bulk.massDeleteConfirm.delete" />
            <input type="submit" name="deleteButton" value="${delete}">
            <br>
            
        </form>
            
    </tags:boxContainer>
    
</cti:standardPage>