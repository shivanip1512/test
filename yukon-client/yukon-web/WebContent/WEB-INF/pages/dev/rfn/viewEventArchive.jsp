<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="dev" page="rfnTest">

    <script type="text/javascript">
        $(document).ready(function() {
            $('.js-sendEventArchive').click(function() {
                $('#eventForm').submit();
            });
            $(".optional").prop("placeholder", "optional");
        });
    </script>

    <tags:sectionContainer2 nameKey="sendEvent">
        <form:form action="sendEvent" method="post" modelAttribute="event" id="eventForm">
            <cti:csrfToken/>
            <tags:nameValueContainer>
                <tags:nameValue name="Serial Number">
                    <form:input path="serialFrom" size="5" />
                    to 
                    <form:input path="serialTo" size="5" cssClass="optional"/>
                </tags:nameValue>
                
                <tags:nameValue name="Manufacturer and Model">
                    <form:select path="manufacturerModel">
                    <c:forEach var="group" items="${rfnTypeGroups}">
                        <optgroup label="${group.key}">
                        <c:forEach var="mm" items="${group.value}">
                            <form:option value="${mm}"><cti:msg2 key="${mm.type}" /> (${mm.manufacturer} ${mm.model})</form:option>
                        </c:forEach>
                        </optgroup>
                    </c:forEach>
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValue name="Event Type">
                    <form:select path="rfnConditionType" items="${rfnConditionTypes}"/>
                </tags:nameValue>
                
                <tags:nameValue name="Num Events Per Meter">
                    <form:input path="numEventPerMeter" />
                </tags:nameValue>
                
                <tags:nameValue name="Num Alarms Per Meter">
                    <form:input path="numAlarmPerMeter" />
                </tags:nameValue>
                
                <tags:nameValue name="Event Time">
                    <dt:dateTime id="timestamp" path="timestamp" value="${event.timestamp}"/>
                </tags:nameValue>
            </tags:nameValueContainer>

            <tags:sectionContainer title="Condition Data Types (meta data)">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".dataType.CLEARED">
                        <form:select path="cleared">
                            <form:option value="true">True</form:option>
                            <form:option value="false">False</form:option>
                        </form:select>
                    </tags:nameValue2>
                    <tags:inputNameValue nameKey=".dataType.COUNT" path="count" />
                    <tags:inputNameValue nameKey=".dataType.DIRECTION" path="direction" />
                    <tags:inputNameValue nameKey=".dataType.MEASURED_VALUE" path="measuredValue" />
                    <tags:inputNameValue nameKey=".dataType.EVENT_START_TIME" path="outageStartTime" />
                    <tags:inputNameValue nameKey=".dataType.THRESHOLD_VALUE" path="thresholdValue" />
                    <tags:inputNameValue nameKey=".dataType.UOM" path="uom" />
                    <tags:inputNameValue nameKey=".dataType.UOM_MODIFIERS" path="uomModifiers" />
                </tags:nameValueContainer2>
            </tags:sectionContainer>

            <tags:sectionContainer title="Included only for REMOTE_METER_CONFIGURATION_FAILED">
                <tags:nameValueContainer2>
                    <tags:inputNameValue nameKey=".dataType.METER_CONFIGURATION_ID" path="meterConfigurationId" />
                    <tags:nameValue2 nameKey=".dataType.METER_STATUS_CODE">
                        <form:select path="meterConfigurationStatusCode">
                            <c:forEach var="statusCode" items="${meterStatusCodes}">
                                <form:option value="${statusCode.code}" label="${statusCode.code} - ${statusCode.status}"/>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".dataType.METER_STATUS_DETAIL">
                        <form:select path="meterConfigurationStatusDetail">
                            <c:forEach var="detail" items="${meterStatusDetails}">
                                <form:option value="${detail.code}" label="${detail.code} - ${detail.status}"/>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer>
            <br>
            <div>Outage Start Time helper info:</div>
            <ul>
                <li>-Leaving this field blank above will default it to 60 seconds from now</li>
                <li>-Enter a value of -1 to simulate a RESTORE event from a meter with "old" firmware (doesn't include this meta-data)</li>
                <li>-The current (<cti:formatDate type="TIME" value="${event.timestamp}"/>) time in milliseconds: ${event.timestampAsMillis}</li>
                <li>-60 seconds less than this is: ${event.timestampAsMillis - 60000}</li>
            </ul>

            <div class="page-action-area">
                <cti:button nameKey="send" classes="js-blocker js-sendEventArchive"/>
            </div>
        </form:form>
    </tags:sectionContainer2>
    
</cti:standardPage>