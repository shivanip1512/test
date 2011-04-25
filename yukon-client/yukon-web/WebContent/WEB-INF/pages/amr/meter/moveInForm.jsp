<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>

<form action="/spring/meter/moveInRequest?deviceId=${meter.deviceId}" id="moveInForm" method="post">
    
	<tags:boxContainer2 nameKey="moveInForm" hideEnabled="false">

		<tags:nameValueContainer2>
		
			<tags:nameValue2 nameKey=".meterNumber">
				<input name="meterNumber" size="10" type="text" value="${meter.meterNumber}" />
			</tags:nameValue2>
			<tags:nameValue2 nameKey=".deviceName">
			    <input name="deviceName" size="30" type="text" value="${meter.name}" />
			</tags:nameValue2>
			<tags:nameValue2 nameKey=".moveInDate">
				<tags:dateInputCalendar fieldName="moveInDate" fieldValue="${currentDate}" />
			</tags:nameValue2>
			<tags:nameValue2 nameKey=".emailNotification">
			    <input name="emailAddress" type="text" />
			</tags:nameValue2>

		</tags:nameValueContainer2>

	    <br><br>
	    <cti:msg2 var="movingIn" key=".movingIn"/>
	    <cti:msg2 var="moveIn" key=".moveIn"/>
	    <cti:msg2 var="moveInDesc" key=".moveInDesc"/>
	    <tags:slowInput myFormId="moveInForm" label="${moveIn}" labelBusy="${movingIn}" description="${moveInDesc}" />
	</tags:boxContainer2>
</form>
