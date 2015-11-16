<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="bulk.updateConfirm">

    <cti:msg var="headerTitle" key="yukon.common.device.bulk.updateConfirm.header"/>
    <tags:sectionContainer title="${headerTitle}" id="updateConfirmContainer">
       <cti:url var="submitUrl" value="/bulk/update/doUpdate"/>
        <form id="updateConfirmForm" action="${submitUrl}" method="post">
            <cti:csrfToken/>
            <%-- CONFIRMATION INFO --%>
            <cti:msg var="rows" key="yukon.common.device.bulk.updateConfirm.rows"/>
            <cti:msg var="identifierColumn" key="yukon.common.device.bulk.updateConfirm.identifierColumn"/>
            <cti:msg var="fieldsToUpdate" key="yukon.common.device.bulk.updateConfirm.fieldsToUpdate"/>
            
            <table cellspacing="8" border="0">
                <tr valign="top">
                    <td class="fwb">${rows}</td>
                    <td>${parsedResult.bulkFileInfo.dataCount}</td>
                </tr>
                <tr valign="top">
                    <td class="fwb">${identifierColumn}</td>
                    <td>${parsedResult.identifierBulkFieldColumnHeader}</td>
                </tr>
                <tr valign="top">
                    <td class="fwb">${fieldsToUpdate}</td>
                    <td>
                        <c:forEach var="bulkField" items="${parsedResult.updateBulkFieldColumnHeaders}">
                            ${bulkField}<br>
                        </c:forEach>
                    </td>
                </tr>
            </table>
                
            <%-- SUBMIT BUTTONS --%>
            <cti:url var="cancelUrl" value="/bulk/update/upload"/>
            <div class="page-action-area">
                <cti:button nameKey="update" type="submit" classes="primary action" busy="true"/>
                <cti:button nameKey="cancel" id="cancelButton" onclick="window.location='${cancelUrl}'"/>
            </div>
            
            <%-- UPDATE INFO ID --%>
            <input type="hidden" name="fileInfoId" value="${parsedResult.bulkFileInfo.id}">
            
        </form>
        
    </tags:sectionContainer>
    
 </cti:standardPage>