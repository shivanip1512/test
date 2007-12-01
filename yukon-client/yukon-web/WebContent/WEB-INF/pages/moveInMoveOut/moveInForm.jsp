<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>


<form action="/spring/moveInMoveOut/moveInRequest?deviceId=${meter.deviceId}" id="moveInForm" method="post">
    <input name="deviceId" type="hidden" value="${meter.deviceId}" />
    <input name="meterNumberOld" type="hidden"
        value="${meter.meterNumber}" />
    <input name="deviceNameOld" type="hidden" value="${meter.name}" />

    <span class="internalSectionHeader"> Meter Number: </span>
    <input name="meterNumber" size="10" type="text"
        value="${meter.meterNumber}" />
    <br />

    <span class="internalSectionHeader"> Device Name: </span>
    <input name="deviceName" size="30" type="text" value="${meter.name}" />
    <br />

    <span class="internalSectionHeader"> Move In Date: </span>
    <ct:dateInputCalendar fieldName="moveInDate"
        fieldValue="${currentDate}" />
    <br />

    <span class="internalSectionHeader"> Email Notification: </span>
    <input name="emailAddress" type="text" />
    <br /><br />

    <ct:slowInput myFormId="moveInForm" label="Move In" labelBusy="Moving In" description="A meter reading is being calculated based on usage for the date supplied" />
</form>
