<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>

<form action="/spring/meter/moveInRequest?deviceId=${meter.deviceId}" id="moveInForm" method="post">
    
	<ct:boxContainer2 nameKey="moveInForm" hideEnabled="false">

		<ct:nameValueContainer2>
		
			<ct:nameValue2 nameKey=".meterNumber">
				<input name="meterNumber" size="10" type="text" value="${meter.meterNumber}" />
			</ct:nameValue2>
			<ct:nameValue2 nameKey=".deviceName">
			    <input name="deviceName" size="30" type="text" value="${meter.name}" />
			</ct:nameValue2>
			<ct:nameValue2 nameKey=".moveInDate">
				<ct:dateInputCalendar fieldName="moveInDate" fieldValue="${currentDate}" />
			</ct:nameValue2>
			<ct:nameValue2 nameKey=".emailNotification">
			    <input name="emailAddress" type="text" />
			</ct:nameValue2>

		</ct:nameValueContainer2>

	    <br><br>
	    <cti:msg2 var="movingIn" key=".movingIn"/>
	    <cti:msg2 var="moveIn" key=".moveIn"/>
	    <cti:msg2 var="moveInDesc" key=".moveInDesc"/>
	    <ct:slowInput myFormId="moveInForm" label="${moveIn}" labelBusy="${movingIn}" description="${moveInDesc}" />
	</ct:boxContainer2>
</form>
