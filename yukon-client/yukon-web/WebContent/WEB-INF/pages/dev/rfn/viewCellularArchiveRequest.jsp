<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="dev" page="rfnTest.viewCellularCommArchiveRequest">

	<div class="notes">
        <br/>This simulator sends Cellular Comm Archive Requests, which are sent by Network Manager to inform Yukon of several cellular points.
        <br/>The four points are the Comm Status point, the RSSI, RSRP, RSRQ, and SINR. Yukon stores these are point data, and they can be manipulated
        <br/>by point injection as well, but this gives us another avenue to test Yukon. 
        <br/>To use this simulator you'll need to have already created the device, as Yukon does not automatically create devices from these messages.
        <br/>Add the serial number for an existing device, and select the correct manufacturer and model from the dropdown, then fill out all fields
        <br/>below with the desired values. After hitting send the message will be transmitted to Yukon.  
     </div><br/>

	<script type="text/javascript">
		$(document).ready(function() {
			$('.js-sendCellularArchive').click(function() {
                $('#cellularArchiveForm').submit();
            });
		})
	</script>

	<tags:sectionContainer2 nameKey="sendCellularArchiveRequest">
		<form:form action="sendCellularArchiveRequest" method="post" modelAttribute="cellularArchive" id="cellularArchiveForm">
			<cti:csrfToken/>
			<tags:nameValueContainer>
				<tags:nameValue name="Serial Number">
					<form:input path="serialNumber" size="10" />
				</tags:nameValue>
				
				<tags:nameValue name="Manufacturer and Model">
                    <form:select path="manufacturerModel">
                    <c:forEach var="cellType" items="${cellularTypes}">
                        <form:option value="${cellType}"><cti:msg2 key="${cellType.type}" /> (${cellType.manufacturer} ${cellType.model})</form:option>
                    </c:forEach>
                    </form:select>
                </tags:nameValue>
				
				<tags:nameValue name="Comm Status">
					<form:select path="nodeConnectionState">
                            <form:option value="ACTIVE">Connected</form:option>
                            <form:option value="NOT_ACTIVE">Disconnected</form:option>
                        </form:select>
				</tags:nameValue>
				
				<tags:nameValue name="RSSI">
					<form:input path="rssi" />
				</tags:nameValue>
				
				<tags:nameValue name="RSRP">
					<form:input path="rsrp" />
				</tags:nameValue>
				
				<tags:nameValue name="RSRQ">
					<form:input path="rsrq" />
				</tags:nameValue>
				
				<tags:nameValue name="SINR">
					<form:input path="sinr" />
				</tags:nameValue>
			</tags:nameValueContainer>
			
			<div class="page-action-area">
                <cti:button nameKey="send" classes="js-blocker js-sendCellularArchive"/>
            </div>
		</form:form>
	</tags:sectionContainer2>
    
</cti:standardPage>