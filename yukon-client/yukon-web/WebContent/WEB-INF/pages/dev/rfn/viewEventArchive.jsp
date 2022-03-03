<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="dev" page="rfnTest.viewEventArchive">

    <script type="text/javascript">
        function showEventSpecificInfo() {
            var eventType = $('.js-event-type').val()
            if (eventType === 'CELLULAR_APN_CHANGED') {
                yukon.ui.alertWarning('Random APN will be generated.');
            } else {
                yukon.ui.removeAlerts();
                $('#remoteMeterConfiguration').toggle(eventType === 'REMOTE_METER_CONFIGURATION_FAILURE'
                    || eventType === 'REMOTE_METER_CONFIGURATION_FINISHED');
                $('#dnp3AddressChanged').toggle(eventType === 'DNP3_ADDRESS_CHANGED');
            }
        }
        $(document).ready(function() {
            $('.js-sendEventArchive').click(function() {
                $('#eventForm').submit();
            });
            $(".optional").prop("placeholder", "optional");
            $('.js-event-type').change(function() {
                showEventSpecificInfo();
            })
            showEventSpecificInfo();
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
                    <form:select cssClass="js-event-type" path="rfnConditionType" items="${rfnConditionTypes}"/>
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

            <tags:sectionContainer id="remoteMeterConfiguration" title="Included only for REMOTE_METER_CONFIGURATION_FAILURE/FINISHED">
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

            <tags:sectionContainer id="dnp3AddressChanged" title="Included only for DNP3_ADDRESS_CHANGED">
                <tags:nameValueContainer2>
                    <tags:inputNameValue nameKey=".dataType.OLD_DNP3_ADDRESS" path="oldDnp3Address" />
                    <tags:inputNameValue nameKey=".dataType.NEW_DNP3_ADDRESS" path="newDnp3Address" />
                </tags:nameValueContainer2>
            </tags:sectionContainer>

            <tags:sectionContainer title="Event Start Time">
                <ul>
                    <li>Leaving Event Start Time blank will default it to 60 seconds before the event is sent</li>
                    <li>Enter a value of -1 to simulate a RESTORE event from a meter with "old" firmware (doesn't include this meta-data)</li>
                    <li>The page load (<cti:formatDate type="TIME" value="${event.timestamp}"/>) time in milliseconds: ${event.timestampAsMillis}</li>
                    <li>60 seconds prior to page load is: ${event.timestampAsMillis - 60000}</li>
                </ul>
            </tags:sectionContainer>

            <div class="page-action-area">
                <cti:button nameKey="send" classes="js-blocker js-sendEventArchive"/>
            </div>
            
        </form:form>
    </tags:sectionContainer2>
            
    <br/><br/>
    <tags:sectionContainer title="Send Outage/Restore Events">
        <span>This Simulator will Send Outage/Restore Events to the selected Device Group, x Milliseconds apart. Select whether Outage or Restore will be the first message sent or if the first message should be random. Events sent are logged in Webserver_RfnComms.</span>
        <br/><br/>
        <form:form action="sendOutageRestore" method="post" modelAttribute="restoreOutageEvent">
             <cti:csrfToken/>
            <tags:nameValueContainer>
                <tags:nameValue name="Device Group" nameColumnWidth="220px">
                     <cti:list var="group"><cti:item value="${deviceGroup}"/></cti:list>
                    <tags:deviceGroupPicker inputName="deviceGroup" inputValue="${group}"/>
                </tags:nameValue>
                <tags:nameValue name="Milliseconds" nameColumnWidth="220px">
                    <tags:input path="milliseconds"/>
                </tags:nameValue>
                <tags:nameValue name="First Message" nameColumnWidth="220px">
                    <tags:selectWithItems path="firstEvent" items="${outageRestoreEventTypes}"/>
                </tags:nameValue>
                <tags:nameValue name="Random Outage/Restore First" nameColumnWidth="220px">
                    <tags:checkbox path="firstEventRandom"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            
            <div class="page-action-area">
                <button type="submit">Send Outage/Restore</button>
            </div>
        
        </form:form>
    
    </tags:sectionContainer>   
    
</cti:standardPage>