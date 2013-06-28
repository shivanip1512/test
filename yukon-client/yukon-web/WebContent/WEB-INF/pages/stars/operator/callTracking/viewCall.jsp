<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="viewCall.${mode}">
<tags:setFormEditMode mode="${mode}"/>

	<script type="text/javascript">

		var combineDateAndTimeFieldsAndSubmit = function() {
		    combineDateAndTimeFields('dateTaken');
		    return true;
		}

	</script>

    <cti:url var="submitUrl" value="/stars/operator/callTracking/updateCall"/>
	<form:form commandName="callReport" action="${submitUrl}" onsubmit="combineDateAndTimeFieldsAndSubmit()">
	
		<input type="hidden" name="accountId" value="${accountId}">
	
		<tags:formElementContainer nameKey="callContainer">
	
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
					<tags:dateTimeInput path="dateTaken" inline="true" fieldValue="${callReport.dateTaken}"/>
				</tags:nameValue2>
				
				<tags:yukonListEntrySelectNameValue nameKey=".type" path="callTypeId" energyCompanyId="${energyCompanyId}" listName="CALL_TYPE"/>
				
				<tags:inputNameValue nameKey=".takenBy" path="takenBy"/>
				
				<tags:textareaNameValue nameKey=".description" path="description" rows="4" cols="35"/>
			
			</tags:nameValueContainer2>
		
		</tags:formElementContainer>
		
		<br>
		
		<%-- buttons --%>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:button nameKey="save" type="submit" classes="f-blocker"/>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url value="/stars/operator/callTracking/view" var="cancelUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="callId" value="${callReport.callId}"/>
                </cti:url>
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
    
</cti:standardPage>