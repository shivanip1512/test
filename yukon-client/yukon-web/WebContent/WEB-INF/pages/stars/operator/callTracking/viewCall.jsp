<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="operator" page="viewCall.${mode}">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
<tags:setFormEditMode mode="${mode}"/>

    <cti:url var="deleteUrl" value="/stars/operator/callTracking/deleteCall"/>

    <form id="delete-call" action="${deleteUrl}" method="post" class="dn">
        <cti:csrfToken/>
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="deleteCallId" value="${callReport.callId}">
    </form>

    <cti:url var="submitUrl" value="/stars/operator/callTracking/updateCall"/>
    <form:form modelAttribute="callReport" action="${submitUrl}">
        <cti:csrfToken/>
        <input type="hidden" name="accountId" value="${accountId}">
    
        <tags:sectionContainer2 nameKey="callContainer">
    
            <tags:nameValueContainer2>
            
                <form:hidden path="callId"/>
            
                <c:choose>
                    <c:when test="${shouldAutoGenerateCallNumber}">
                        <form:hidden path="callNumber"/>
                    </c:when>
                    <c:otherwise>
                        <tags:inputNameValue nameKey=".callNumber" path="callNumber"/>
                    </c:otherwise>
                </c:choose>
            
                <tags:nameValue2 nameKey=".date">
                    <dt:dateTime id="dateTaken" path="dateTaken" value="${callReport.dateTaken}"/>
                </tags:nameValue2>
                
                <tags:yukonListEntrySelectNameValue nameKey=".type" path="callTypeId" energyCompanyId="${energyCompanyId}" listName="CALL_TYPE"/>
                
                <tags:inputNameValue nameKey=".takenBy" path="takenBy"/>
                
                <tags:textareaNameValue nameKey=".description" path="description" rows="4" cols="35"/>
            
            </tags:nameValueContainer2>
        
        </tags:sectionContainer2>
        
        <br>
        
        <%-- buttons --%>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:button nameKey="save" type="submit" classes="primary action"/>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url value="/stars/operator/callTracking/view" var="cancelUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="callId" value="${callReport.callId}"/>
                </cti:url>
                <cti:button nameKey="delete" classes="delete" onclick="$('#delete-call').submit();"/>
            </cti:displayForPageEditModes>
        
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url value="/stars/operator/callTracking/callList" var="cancelUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                </cti:url>
            </cti:displayForPageEditModes>
            
            <cti:button nameKey="cancel" href="${cancelUrl}"/>
            
        </cti:displayForPageEditModes>
        
        <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <cti:url value="/stars/operator/callTracking/edit" var="editUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="callId" value="${callReport.callId}"/>
                </cti:url>
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        
    </form:form>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>