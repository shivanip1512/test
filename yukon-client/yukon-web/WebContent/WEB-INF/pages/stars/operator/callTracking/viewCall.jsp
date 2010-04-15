<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="viewCall">
<tags:setFormEditMode mode="${mode}"/>

	<script type="text/javascript">

		submitForm = function() {
		    combineDateAndTimeFields('dateTaken');
		    return true;
		}

	</script>
	
	<form id="viewAllForm" action="/spring/stars/operator/callTracking/callList">
		<input type="hidden" name="accountId" value="${accountId}">
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
	</form>
	
	<form:form id="callReportUpdateForm" commandName="callReport" action="/spring/stars/operator/callTracking/updateCall" onsubmit="return submitForm();">
	
		<input type="hidden" name="accountId" value="${accountId}">
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
	
		<tags:nameValueContainer2 nameColumnWidth="180px">
		
			<form:hidden path="callId"/>
		
			<tags:inputNameValue nameKey=".callNumber" path="callNumber"/>
		
			<tags:nameValue2 nameKey=".date">
				<tags:dateTimeInput path="dateTaken" inline="true" fieldValue="${callReport.dateTaken}"/>
			</tags:nameValue2>
			
			<tags:yukonListEntrySelectNameValue nameKey=".type" path="callTypeId" accountId="${accountId}" listName="CALL_TYPE"/>
			
			<tags:inputNameValue nameKey=".takenBy" path="takenBy"/>
			
			<tags:textareaNameValue nameKey=".description" path="description" rows="4" cols="35"/>
		
		</tags:nameValueContainer2>
		
		<br>
		
		<%-- buttons --%>
		<tags:slowInput2 myFormId="viewAllForm" key="viewAll" width="80px"/>
		<cti:msg var="saveButtonText" key="yukon.web.components.slowInput.save.label" />
		<input type="submit" value="${saveButtonText}" class="formSubmit">
		
	</form:form>
    
</cti:standardPage>