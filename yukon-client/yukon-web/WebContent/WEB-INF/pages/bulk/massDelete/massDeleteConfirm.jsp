<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.massDeleteConfirm">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.massDeleteConfirm.header"/>
    <tags:sectionContainer title="${headerTitle}" id="collectionActionsContainer">
    
        <%-- SELECTED DEVICES POPUP, NOTE TO USER --%>
        <table cellpadding="2">
        
            <tr>
                <td valign="top" colspan="2" class="strong-label-small">
                    <tags:selectedDevices deviceCollection="${deviceCollection}" id="selectedDevices"/>
                </td>
            </tr>
            
            <%-- NOTE --%>
            <tr>
                <td valign="top" class="strong-label-small error">
                    <cti:msg key="yukon.common.device.bulk.massDeleteConfirm.noteLabel"/>
                </td>
                <td>
                    <cti:msg key="yukon.common.device.bulk.massDeleteConfirm.noteText" arguments="${deviceCount}" />
                </td>
            </tr>
        </table>
        <cti:url value="/bulk/massDelete/doMassDelete" var="massDeleteUrl"/>
        <div class="page-action-area">
            <form id="massDeleteForm" method="post" action="${massDeleteUrl}">
                <cti:csrfToken/>
                <cti:deviceCollection deviceCollection="${deviceCollection}" />
                <cti:button nameKey="delete" type="submit" name="deleteButton" classes="primary action"/>
            </form>
        </div>
            
    </tags:sectionContainer>
    
</cti:standardPage>