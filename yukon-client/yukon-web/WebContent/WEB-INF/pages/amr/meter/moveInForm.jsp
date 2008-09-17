<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>


<form action="/spring/meter/moveInRequest?deviceId=${meter.deviceId}" id="moveInForm" method="post">
    
    <input name="deviceId" type="hidden" value="${meter.deviceId}" />
    <input name="meterNumberOld" type="hidden"
        value="${meter.meterNumber}" />
    <input name="deviceNameOld" type="hidden" value="${meter.name}" />

	<ct:boxContainer title="Move In" hideEnabled="false">

		<ct:nameValueContainer>
		
			<ct:nameValue name="Meter Number">
				<input name="meterNumber" size="10" type="text" value="${meter.meterNumber}" />
			</ct:nameValue>
			<ct:nameValue name="Device Name">
			    <input name="deviceName" size="30" type="text" value="${meter.name}" />
			</ct:nameValue>
			<ct:nameValue name="Move In Date">
				<ct:dateInputCalendar fieldName="moveInDate" fieldValue="${currentDate}" />
			</ct:nameValue>
			<ct:nameValue name="Email Notification">
			    <input name="emailAddress" type="text" />
			</ct:nameValue>
		
		</ct:nameValueContainer>
	
	    <br><br>
	
	    <ct:slowInput myFormId="moveInForm" label="Move In" labelBusy="Moving In" description="A meter reading is being calculated based on usage for the date supplied" />
	    
	</ct:boxContainer>
</form>
