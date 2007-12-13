<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>


<form action="/spring/csr/moveOutRequest?deviceId=${meter.deviceId}" id="moveOutForm" method="post">

    <input name="deviceId" type="hidden" value="${meter.deviceId}" />

	<ct:boxContainer title="Move Out" hideEnabled="false">
	
		<ct:nameValueContainer>
		
			<ct:nameValue name="Move Out Date">
			    <ct:dateInputCalendar fieldName="moveOutDate" fieldValue="${currentDate}" />
			</ct:nameValue>
			<ct:nameValue name="Email Notification">
			    <input name="emailAddress" type="text" />
			</ct:nameValue>
		
		</ct:nameValueContainer>
	
	    <br /><br />
	
	    <ct:slowInput myFormId="moveOutForm" label="Move Out"
	        labelBusy="Moving Out" description="A meter reading is being calculated based on usage for the date supplied" />

	</ct:boxContainer>
</form>
