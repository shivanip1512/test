<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>

<form action="/meter/moveOutRequest?deviceId=${meter.deviceId}" id="moveOutForm" method="post">
    <cti:csrfToken/>
    <input name="deviceId" type="hidden" value="${meter.deviceId}" />

	<ct:boxContainer2 nameKey="moveOutForm" hideEnabled="false">
	
		<ct:nameValueContainer2>
		
			<ct:nameValue2 nameKey=".moveOutDate">
			    <dt:date name="moveOutDate" value="${currentDate}" />
			</ct:nameValue2>
			<ct:nameValue2 nameKey=".emailNotification">
			    <input name="emailAddress" type="text" />
			</ct:nameValue2>
		
		</ct:nameValueContainer2>
	
	    <br /><br />
        <cti:msg2 var="moveOut" key=".moveOut"/>
        <cti:msg2 var="movingOut" key=".movingOut"/>
        <cti:msg2 var="moveOutDesc" key=".moveOutDesc"/>
	    <ct:slowInput myFormId="moveOutForm" label="${moveOut}" labelBusy="${movingOut}" description="${moveOutDesc}" />

	</ct:boxContainer2>
</form>
