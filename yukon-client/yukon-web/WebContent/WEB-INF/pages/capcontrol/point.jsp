<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="capcontrol" page="point.${mode}">

    <tags:setFormEditMode mode="${mode}" />
    <c:set var="viewMode" value="${false}" />
    <cti:displayForPageEditModes modes="VIEW">
        <c:set var="viewMode" value="${true}" />
    </cti:displayForPageEditModes>
    
    <c:set var="nameValueClass" value="natural-width ${viewMode ? '' : 'with-form-controls' }" />

    <cti:url var="action" value="/capcontrol/points/${pointModel.pointBase.point.pointType}" />
    <form:form id="point-from" commandName="pointModel" action="${action}" method="POST">
    
        <cti:csrfToken />
        
        <h3>Info</h3>
        <tags:nameValueContainer tableClass="${nameValueClass}">
        
            <tags:nameValue name="Id">
                <form:hidden path="pointBase.point.pointID"/>
                ${pointModel.pointBase.point.pointID}
            </tags:nameValue>
            <tags:nameValue name="Name"><tags:input path="pointBase.point.pointName"/></tags:nameValue>
            
            <%-- Only an input for creation --%>
            <tags:nameValue name="Type">
                <form:hidden path="pointBase.point.pointTypeEnum"/>
                <i:inline key="${pointModel.pointBase.point.pointTypeEnum}"/>
            </tags:nameValue>
            
            <tags:nameValue name="Parent">
                <form:hidden path="pointBase.point.paoID"/>
                ${fn:escapeXml(parent.paoName)}
            </tags:nameValue>
            
            <tags:nameValue name="Timing Group">
                <div class="button-group">
                    <c:forEach var="logicalGroup" items="${logicalGroups}">
                        <tags:radio path="pointBase.point.logicalGroup" value="${logicalGroup.dbValue}" key="${logicalGroup}" 
                            classes="yes M0" />
                    </c:forEach>
                </div>
            </tags:nameValue>
            
            <tags:nameValue name="">
                <tags:switchButton path="pointBase.point.outOfService"  offClasses="M0"
                offNameKey=".disabled" onNameKey=".enabled" inverse="${true}" />
            </tags:nameValue>
            
        </tags:nameValueContainer>
        
        <c:if test="${isScalarType}">

            <h3>Settings</h3>
            <tags:nameValueContainer tableClass="${nameValueClass}">
            
                <tags:nameValue name="Archive Data">
                    <tags:selectWithItems path="pointBase.point.archiveType" items="${scalarArchiveTypes}"
                        itemValue="pointArchiveTypeName"/>
                    
                </tags:nameValue>
                
                <%-- TODO Enabled when archiveType in (ON_TIMER, ON_TIMER_OR_UPDATE) --%>
                <tags:nameValue name="Archive Interval">
                    <tags:intervalStepper path="pointBase.point.archiveInterval" intervals="${archiveIntervals}" />
                </tags:nameValue>
                
                <tags:nameValue name="Units of Measure">
                    <tags:selectWithItems path="pointBase.pointUnit.uomID" items="${unitMeasures}" 
                        itemValue="liteID" itemLabel="unitMeasureName" inputClass="js-init-chosen"/>
                </tags:nameValue>
                
                <tags:nameValue name="Decimal Digits">
                    <tags:stepperWithItems items="${decimalDigits}" path="pointBase.pointUnit.decimalPlaces"/>
                </tags:nameValue>
    
                <tags:nameValue name="State Group">
                    <tags:selectWithItems path="pointBase.point.stateGroupID" items="${stateGroups}" 
                        itemValue="liteID" itemLabel="stateGroupName" inputClass="js-init-chosen"/>
                    
                </tags:nameValue>
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${isStatusPoint}">

            <h3>Settings</h3>
            <tags:nameValueContainer tableClass="${nameValueClass}">
                
                <tags:nameValue name="Archive Status">
                    
                    <div class="button-group">
                        <c:forEach var="archiveType" items="${statusArchiveTypes}">
                            <tags:radio path="pointBase.point.archiveType" value="${archiveType.pointArchiveTypeName}" key="${archiveType}" 
                                classes="yes M0" />
                        </c:forEach>
                    </div>
                    
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${isStatusType}">
                      
            <tags:nameValueContainer tableClass="${nameValueClass}">
                
                <tags:nameValue name="State Group">
                    <tags:selectWithItems path="pointBase.point.stateGroupID" items="${stateGroups}" 
                        itemValue="liteID" itemLabel="stateGroupName" inputClass="js-init-chosen"/>
                </tags:nameValue>
                
                <tags:nameValue name="Initial State">
                    <%-- TODO ajax in options when point.stateGroupID changes --%>
                    <tags:selectWithItems path="pointBase.pointStatus.initialState" items="${initialStates}" 
                        itemValue="liteID" itemLabel="stateText" />
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${isCalcType}">
                      
            <tags:nameValueContainer tableClass="${nameValueClass}">
                
                <tags:nameValue name="Update Type">
                    <%-- TODO i18n --%>
                    <tags:selectWithItems path="pointBase.point.calcBase.updateType" items="${pointUpdateTypes}" 
                        itemValue="databaseRepresentation" itemLabel="databaseRepresentation" />
                </tags:nameValue>
                
                <%-- TODO Enabled when point.calcBase.updateType in ('On Timer', 'On Timer+Change') --%>
                <tags:nameValue name="Update Rate">
                    <tags:intervalStepper path="pointBase.point.calcBase.periodicRate" intervals="${archiveIntervals}" />
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${isAnalogPoint}">
        
            <h2>Physical Setup (Analog)</h2>
            <tags:nameValueContainer tableClass="${nameValueClass}">
                
                <%-- The physical offset value within the current device or parent this point belongs to --%>
                <tags:nameValue name="Point Offset">
                    <%-- 0 = No offset set --%>
                    <tags:input path="pointBase.point.pointOffset" size="6"/>
                </tags:nameValue>
                
                <%-- The amount the value of this point must deviate before the point is read and updated --%>
                <tags:nameValue name="Deadband">
                    <%-- 0 = No deadband set --%>
                    <tags:input path="pointBase.pointAnalog.deadband" size="6"/>
                </tags:nameValue>
                
                <%-- A value that is always applied to the raw reading of this point --%>
                <tags:nameValue name="Multiplier">
                    <tags:input path="pointBase.pointAnalog.multiplier" size="6"/>
                </tags:nameValue>

                <%-- A value that is added to the raw reading when making calculations --%>
                <tags:nameValue name="Data Offset">
                    <tags:input path="pointBase.pointAnalog.dataOffset" size="6"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
            
            <h3>Control Settings</h3>
            <tags:nameValueContainer tableClass="${nameValueClass}">
            
                <%-- Check this box to disable control for this point --%>
                <tags:nameValue name="Control Type">
                    <div class="button-group">
                        <c:forEach var="analogControlType" items="${analogControlTypes}">
                            <tags:radio path="pointBase.pointAnalogControl.controlType" 
                                value="${analogControlType.databaseRepresentation}" key="${analogControlType}" 
                                classes="yes M0" />
                        </c:forEach>
                    </div>
                </tags:nameValue>
                
                <%-- Check this box to disable control for this point --%>
                <tags:nameValue name="Control Inhibit">
                    <%-- Enabled when Control Type is not NONE --%>
                    <tags:switchButton path="pointBase.pointAnalogControl.controlInhibited"  offClasses="M0"/>
                </tags:nameValue>
                
                
                <%-- Specifies the physical location used for wiring the relay point --%>
                <tags:nameValue name="Control Pt Offset">
                    <%-- Enabled when Control Type is not NONE --%>
                    <tags:input path="pointBase.pointAnalogControl.controlOffset" size="6"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            
        </c:if>
        
        <c:if test="${isAccumulatorPoint}">
                      
            <h2>Physical Setup (Accumulator)</h2>
            <tags:nameValueContainer tableClass="${nameValueClass}">
                <tags:nameValue name="Point Offset">
                    <%-- 0 = no offset set --%>
                    <tags:input path="pointBase.point.pointOffset" size="6"/>
                </tags:nameValue>
                <%-- A value that is always applied to the raw reading of this point --%>
                <tags:nameValue name="Multiplier">
                    <tags:input path="pointBase.pointAccumulator.multiplier" size="6"/>
                </tags:nameValue>
                <%-- A value that is added to the raw reading when making calculations --%>
                <tags:nameValue name="Data Offset">
                    <tags:input path="pointBase.pointAccumulator.dataOffset" size="6"/>
                </tags:nameValue>
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${isScalarType}">
                      
            
            <h3>Limits</h3>
            <tags:nameValueContainer tableClass="${nameValueClass}">
            
                <%-- The first limit that can be set for this point, used to determine if an alarm condition is active --%>
                <%-- Determines visibilty of the below limitOne inputs --%>
                <tags:nameValue name="Limit 1">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="limit1" offClasses="M0"/>
                </tags:nameValue>
                
                <%-- The upper value for this limit (used for an alarming condition) --%>
                <tags:nameValue name="Upper Limit">
                    <tags:input path="pointBase.limitOne.highLimit" size="6"/>
                </tags:nameValue>
                
                <%-- The lower value for this limit (used for an alarming condition) --%>
                <tags:nameValue name="Lower Limit">
                    <tags:input path="pointBase.limitOne.lowLimit" size="6"/>
                </tags:nameValue>

                <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                <tags:nameValue name="Duration">
                    <tags:input path="pointBase.limitOne.limitDuration" size="6"/> secs
                </tags:nameValue>
                
                <%-- The second limit that can be set for this point, used to determine if an alarm condition is active --%>
                <%-- Determines visibilty of the below limitTwo inputs --%>
                <tags:nameValue name="Limit 2">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="limit2" offClasses="M0"/>
                </tags:nameValue>
                
                <%-- The upper value for this limit (used for an alarming condition) --%>
                <tags:nameValue name="Upper Limit">
                    <tags:input path="pointBase.limitTwo.highLimit" size="6"/>
                </tags:nameValue>
                
                <%-- The lower value for this limit (used for an alarming condition) --%>
                <tags:nameValue name="Lower Limit">
                    <tags:input path="pointBase.limitTwo.lowLimit" size="6"/>
                </tags:nameValue>

                <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                <tags:nameValue name="Duration">
                    <tags:input path="pointBase.limitTwo.limitDuration" size="6"/> secs
                </tags:nameValue>
                
            </tags:nameValueContainer>
            
            <h3>Reasonability</h3>
            <tags:nameValueContainer tableClass="${nameValueClass}">
                <tags:nameValue name="High Reasonability">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="highReasonability" offClasses="M0"/>
                </tags:nameValue>
                
                <%-- All readings exceeding this value are ignored --%>
                <tags:nameValue name="High Reasonability Limit">
                    <tags:input path="pointBase.pointUnit.highReasonabilityLimit" size="6"/>
                </tags:nameValue>
                
                <tags:nameValue name="Low Reasonability">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="lowReasonability" offClasses="M0"/>
                </tags:nameValue>
                
                <%-- All readings less than this value are ignored --%>
                <tags:nameValue name="Low Reasonability Limit">
                    <tags:input path="pointBase.pointUnit.lowReasonabilityLimit" size="6"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if> 
        
        <c:if test="${isStatusPoint}">
                      
            <h2>Physical Setup (Status)</h2>
            <tags:nameValueContainer tableClass="${nameValueClass}">
                
                <%-- The physical offset value within the current device or parent this point belongs to --%>
                <%-- 0 = No offset set --%>
                <tags:nameValue name="Point Offset">
                    <tags:input path="pointBase.point.pointOffset" size="6"/>
                </tags:nameValue>
                
                <%-- Check this box to disable control for this point --%>
                <tags:nameValue name="Control Type">
                    <tags:selectWithItems path="pointBase.pointStatusControl.controlType" 
                        items="${statusControlTypes}" itemValue="controlName"/>
                </tags:nameValue>
                
                <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                <%-- Check this box to disable control for this point --%>
                <tags:nameValue name="Control Inhibit">
                    <tags:switchButton path="pointBase.pointStatusControl.controlInhibited" offClasses="M0"/>
                </tags:nameValue>
                
                <tags:nameValue name="Control Point Offset">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- Specifies the physical location used for wiring the relay point --%>
                    <tags:input path="pointBase.pointStatusControl.controlOffset" size="6"/>
                </tags:nameValue>
                
                <tags:nameValue name="Close Time 1">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- Specify how long each relay stays energized --%>
                    <tags:input path="pointBase.pointStatusControl.closeTime1" size="6"/> ms
                </tags:nameValue>
                
                <tags:nameValue name="Close Time 2">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- Specify how long each relay stays energized --%>
                    <tags:input path="pointBase.pointStatusControl.closeTime2" size="6"/> ms
                </tags:nameValue>
                
                <tags:nameValue name="Command Timeout">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- The length of time to use to scan for a state change following control. 
                           An alarm is raised if a state change is not detected. --%>
                    <tags:input path="pointBase.pointStatusControl.commandTimeOut" size="6"/> sec
                </tags:nameValue>
                
                <tags:nameValue name="Open Command String">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- The OPEN command string sent out when Yukon controls this point --%>
                    <tags:input path="pointBase.pointStatusControl.stateZeroControl"/>
                </tags:nameValue>
                
                <tags:nameValue name="Close Command String">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- The CLOSE command string sent out when Yukon controls this point --%>
                    <tags:input path="pointBase.pointStatusControl.stateOneControl"/>
                </tags:nameValue>
            </tags:nameValueContainer>

        </c:if>
        
        <h3>Stale Data</h3>
        <tags:nameValueContainer tableClass="${nameValueClass}">
            <%-- Turn on/off stale data checking --%>
            <tags:nameValue name="">
                <tags:switchButton path="staleData.enabled" offClasses="M0"
                 offNameKey=".disabled" onNameKey=".enabled"/>
            </tags:nameValue>
            <%-- Disabled when not staleData.enabled --%>
            <tags:nameValue name="Time">
                <tags:input path="staleData.time" size="6"/> min
            </tags:nameValue>
            
            <%-- Disabled when not staleData.enabled --%>
            <%-- staleData.updateStyles --%>
            <tags:nameValue name="Update Style">
                <div class="button-group">
                    <c:forEach var="updateStyle" items="${staleDataUpdateStyles}">
                        <tags:radio path="staleData.updateStyle" 
                            value="${updateStyle.databaseRepresentation}" key="${updateStyle}" 
                            classes="yes M0" />
                    </c:forEach>
                </div>
            </tags:nameValue>
        </tags:nameValueContainer>
            
        <h2>Alarming</h2>
        <tags:nameValueContainer tableClass="${nameValueClass}">
            <%-- Options: ptEditorForm.notifcationGrps --%>
            <tags:nameValue name="Notification Group">
                <tags:selectWithItems path="pointBase.pointAlarming.notificationGroupID" items="${notificationGroups}" 
                    itemValue="liteID" itemLabel="notificationGroupName" />
            </tags:nameValue>
            
            <%-- Notify when alarms are Acknowledged --%>
            <tags:nameValue name="Alarm Acknowledge">
                <tags:switchButton path="pointBase.pointAlarming.notifyOnAck" offClasses="M0"/>
            </tags:nameValue>
            
            <%-- Notify when alarms Clear --%>
            <tags:nameValue name="Notify On Clear">
                <tags:switchButton path="pointBase.pointAlarming.notifyOnClear" offClasses="M0"/>
            </tags:nameValue>
            
            <tags:nameValue name="Disable All Alarms">
                <tags:switchButton path="pointBase.point.alarmsDisabled" offClasses="M0"/>
            </tags:nameValue>
            
            <c:forEach var="alarmTableEntry" items="${pointModel.alarmTableEntries}" varStatus="status">
                
                <tags:nameValue name="${pointModel.alarmTableEntries[status.index].condition} Category">
                    <%-- ptEditorForm.alarmCategories  --%>
                    <%-- TODO this should take the liteID for the value rather than converting --%>
                    <tags:selectWithItems path="alarmTableEntries[${status.index}].generate" items="${alarmCategories}" 
                        itemValue="categoryName" itemLabel="categoryName" />
                </tags:nameValue>
                
                <tags:nameValue name="${pointModel.alarmTableEntries[status.index].condition} Notify">
                    <%-- selLists.ptAlarmNotification --%>
                    <tags:selectWithItems path="alarmTableEntries[${status.index}].excludeNotify" 
                        items="${alarmNotificationTypes}" itemValue="dbString" />
                </tags:nameValue>
            </c:forEach>
            
        </tags:nameValueContainer>
            
        <h2>FDR Point Editor</h2>
        <tags:nameValueContainer tableClass="${nameValueClass}">
            <%-- TODO Only show necessary members of this list --%>
            <cti:list var="nums">
                <cti:item value="${0}" />
                <cti:item value="${1}" />
                <cti:item value="${2}" />
                <cti:item value="${3}" />
                <cti:item value="${4}" />
            </cti:list>
            <c:forEach var="fdrIdx" items="${nums}">
                <c:set var="fdrTranslation" value="${pointModel.pointBase.pointFDRList[fdrIdx]}" />
                <tags:nameValue name="Interface ${fdrIdx}">
                    <tags:selectWithItems path="pointBase.pointFDRList[${fdrIdx}].interfaceType" 
                        items="${fdrInterfaceTypes}" />
                </tags:nameValue>
                <tags:nameValue name="Direction ${fdrIdx}">
                    <%-- TODO ajax in these options --%>
                    <%-- FDRDirectionsMap[interfaceType] --%>
                    <tags:selectWithItems items="${fdrDirections}" path="pointBase.pointFDRList[${fdrIdx}].directionType" itemValue="value"/>
                </tags:nameValue>
                <tags:nameValue name="Translation">
                    
                    ${pointModel.pointBase.pointFDRList[fdrIdx].translation}
                    <form:hidden path="pointBase.pointFDRList[${fdrIdx}].translation" />
                </tags:nameValue>
                <c:forEach var="option" items="${fdrTranslation.interfaceEnum.interfaceOptionsList}">
                
                    <%-- TODO Combine these into the translation --%>
                    <tags:nameValue name="${option.optionLabel}">
                        <c:if test="${not empty option.optionValues}">
                            <%-- TODO make this a select --%>
                            Options:
                            <c:forEach var="optionValue" items="${option.optionValues}">
                                ${optionValue},
                            </c:forEach>
                        </c:if>
                    </tags:nameValue>
                </c:forEach>
            </c:forEach>
        </tags:nameValueContainer>
        
        <div class="page-action-area">
            
            <cti:displayForPageEditModes modes="VIEW">
                <cti:url var="editUrl" value="/capcontrol/points/${pointModel.id}/edit" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:point:delete"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${strategy.name}"/>
                
                <cti:url var="viewUrl" value="/capcontrol/points/${pointModel.id}" />
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>
            
    </form:form>
    
</cti:standardPage>