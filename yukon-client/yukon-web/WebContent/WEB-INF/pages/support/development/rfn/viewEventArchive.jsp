<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="support" page="rfnTest">

	<script>
		jQuery(document).ready(function() {
			jQuery('.f_sendEventArchive').click(function() {
			    combineDateAndTimeFields('timestamp');
			    jQuery('#eventForm').submit();
			});
		});
	</script>

    <tags:sectionContainer2 nameKey="sendEvent">
        <form:form action="sendEvent" method="post" commandName="event" id="eventForm">
            <tags:nameValueContainer>
                <tags:nameValue name="Serial Number">
                    <form:input path="serialFrom" /> to <form:input path="serialTo"/>
                </tags:nameValue>
                
                <tags:nameValue name="Manufacturer">
                    <form:select path="manufacturer">
                        <form:option value="LGYR">LGYR</form:option>
                        <form:option value="ITRN">ITRN</form:option>
                        <form:option value="Eka">Eka</form:option>
                        <form:option value="EE">EE</form:option>
                        <form:option value="GE">GE</form:option>
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValue name="Model">
                    <form:select path="model">
                        <form:option value="FocuskWh">FocuskWh</form:option>
                        <form:option value="C2SX">C2SX</form:option>
                        <form:option value="water_sensor">water_sensor</form:option>
                        <form:option value="water_sensor">water_node</form:option>
                        <form:option value="A3R">A3R</form:option>
                        <form:option value="Centron">Centron</form:option>
                        <form:option value="kV2">kV2</form:option>
                        <form:option value="FocusAXD">FocusAXD</form:option>
                        <form:option value="FocusAXR">FocusAXR</form:option>
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValue name="Event Type">
                    <form:select path="rfnConditionType" styleClass="rfnConditionTypes" items="${rfnConditionTypes}"/>
                </tags:nameValue>
                
                <tags:nameValue name="Num Events Per Meter">
                    <form:input path="numEventPerMeter" />
                </tags:nameValue>
                
                <tags:nameValue name="Num Alarms Per Meter">
                    <form:input path="numAlarmPerMeter" />
                </tags:nameValue>
                
		        <tags:nameValue name="Event Time">
		            <tags:dateTimeInput path="timestamp" fieldValue="${event.timestamp}" />
		        </tags:nameValue>
            </tags:nameValueContainer>

			<tags:nameValueContainer2>
				<strong><u>Condition Data Types (meta data)</u></strong>
				<tags:nameValue2 nameKey=".dataType.CLEARED">
					<form:select path="cleared">
						<form:option value="true">True</form:option>
						<form:option value="false">False</form:option>
					</form:select>
				</tags:nameValue2>
				<tags:inputNameValue nameKey=".dataType.COUNT" path="count" />
				<tags:inputNameValue nameKey=".dataType.DIRECTION" path="direction" />
				<tags:inputNameValue nameKey=".dataType.MEASURED_VALUE" path="measuredValue" />
				<tags:inputNameValue nameKey=".dataType.OUTAGE_START_TIME" path="outageStartTime" />
				<tags:inputNameValue nameKey=".dataType.THRESHOLD_VALUE" path="thresholdValue" />
				<tags:inputNameValue nameKey=".dataType.UOM" path="uom" />
				<tags:inputNameValue nameKey=".dataType.UOM_MODIFIERS" path="uomModifiers" />
			</tags:nameValueContainer2>
			<br>
			<div>Outage Start Time helper info:</div>
			<ul>
	            <li>-Leaving this field blank above will default it to 60 seconds from now</li>
	            <li>-Enter a value of -1 to simulate a RESTORE event from a meter with "old" firmware (doesn't include this meta-data)</li>
	            <li>-The current (<cti:formatDate type="TIME" value="${event.timestamp}"/>) time in milliseconds: ${event.timestampAsMillis}</li>
	            <li>-60 seconds less than this is: ${event.timestampAsMillis - 60000}</li>
			</ul>

			<div class="pageActionArea">
                <cti:button nameKey="send" styleClass="f_blocker f_sendEventArchive"/>
            </div>
        </form:form>
    </tags:sectionContainer2>
    
</cti:standardPage>