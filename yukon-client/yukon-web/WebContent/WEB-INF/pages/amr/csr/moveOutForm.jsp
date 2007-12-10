<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>


<form action="/spring/csr/moveOutRequest?deviceId=${meter.deviceId}" id="moveOutForm"
    method="post">
    <input name="deviceId" type="hidden" value="${meter.deviceId}" />

    <span class="internalSectionHeader"> Move Out Date: </span>
    <ct:dateInputCalendar fieldName="moveOutDate"
        fieldValue="${currentDate}" />
    &nbsp;
    <br />

    <span class="internalSectionHeader"> Email Notification: </span>
    <input name="emailAddress" type="text" />
    <br /><br />

    <ct:slowInput myFormId="moveOutForm" label="Move Out"
        labelBusy="Moving Out" description="A meter reading is being calculated based on usage for the date supplied" />
</form>
