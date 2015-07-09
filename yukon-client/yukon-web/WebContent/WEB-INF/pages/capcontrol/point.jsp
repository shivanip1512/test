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

    <cti:url var="action" value="/capcontrol/points" />
    <form:form id="strategy-from" commandName="pointModel" action="${action}" method="POST">
    
        <cti:csrfToken />
        <form:hidden path="pointBase.point.pointID"/>
        
        <h3>Info</h3>
        <tags:nameValueContainer tableClass="natural-width">
        
            <tags:nameValue name="Id">
                ${pointModel.pointBase.point.pointID}
            </tags:nameValue>
            <tags:nameValue name="Name"><tags:input path="pointBase.point.pointName"/></tags:nameValue>
            
            <%-- Only an input for creation --%>
            <%-- Static Options: selLists.pointTypes --%>
            <tags:nameValue name="Type"><tags:input path="pointBase.point.pointTypeEnum"/></tags:nameValue>
            
            <%-- Never editable --%>
            <tags:nameValue name="Parent">${fn:escapeXml(parent.paoName)}</tags:nameValue>
            
            
            <%-- ptEditorForm.logicalGroups --%>
            <%-- Static Options: Default, SOE --%>
            <tags:nameValue name="Timing Group"><tags:input path="pointBase.point.logicalGroup"/></tags:nameValue>
            
            <tags:nameValue name="Disable Point">
                <tags:switchButton path="pointBase.point.outOfService"/>
            </tags:nameValue>
            
        </tags:nameValueContainer>
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'Analog' or 
                      pointModel.pointBase.point.pointType eq 'PulseAccumulator' or
                      pointModel.pointBase.point.pointType eq 'CalcAnalog'}">

            <h3>Settings</h3>
            <tags:nameValueContainer tableClass="natural-width">
            
                <%-- Static Options: selLists.ptArchiveType --%>
                <tags:nameValue name="Archive Data"><tags:input path="pointBase.point.archiveType"/></tags:nameValue>
                
                <%-- Enabled when archiveType in (ON_TIMER, ON_TIMER_OR_UPDATE) --%>
                <%-- Static Options: ptEditorForm.archiveInterval --%>
                <tags:nameValue name="Archive Interval"><tags:input path="pointBase.point.archiveInterval"/></tags:nameValue>
                
                <%-- Static Options: ptEditorForm.uofMs --%>
                <tags:nameValue name="Units of Measure"><tags:input path="pointBase.pointUnit.uomID"/></tags:nameValue>
                
                
                <%-- Static Options: ptEditorForm.decimalDigits --%>
                <tags:nameValue name="Decimal Digits"><tags:input path="pointBase.pointUnit.decimalPlaces"/></tags:nameValue>
    
                <%-- Dynamic Options: ptEditorForm.stateGroups --%>
                <tags:nameValue name="State Group"><tags:input path="pointBase.point.stateGroupID"/></tags:nameValue>
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'Status'}">

            <h3>Settings</h3>
            <tags:nameValueContainer tableClass="natural-width">
                
                <tags:nameValue name="Archive Status">
                    <%-- Static Options: selLists.ptArchiveType --%>
                    <tags:switchButton path="pointBase.point.archiveStatusData"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'Status' or
                      pointModel.pointBase.point.pointType eq 'CalcStatus'}">
                      
            <tags:nameValueContainer tableClass="natural-width">
                
                <%-- Static Options: ptEditorForm.stateGroups --%>
                <tags:nameValue name="State Group"><tags:input path="pointBase.point.stateGroupID"/></tags:nameValue>
                
                <tags:nameValue name="Initial State">
                    <%-- Dynamic list based on point.stateGroupID --%>
                    <%-- Dynamic Options: ptEditorForm.initialStates --%>
                    <tags:input path="pointBase.pointStatus.initialState"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'CalcAnalog' or
                      pointModel.pointBase.point.pointType eq 'CalcStatus'}">
                      
            <tags:nameValueContainer tableClass="natural-width">
                
                <%-- Static Options: selLists.ptUpdateType --%>
                <tags:nameValue name="Update Type">
                    <tags:input path="pointBase.point.calcBase.updateType"/>
                </tags:nameValue>
                
                <%-- Enabled when point.calcBase.updateType in ('On Timer', 'On Timer+Change') --%>
                <%-- Static Options: ptEditorForm.archiveInterval --%>
                <tags:nameValue name="Update Rate">
                    <tags:input path="pointBase.point.calcBase.periodicRate"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'Analog'}">
        
            <h2>Physical Setup (Analog)</h2>
            <tags:nameValueContainer tableClass="natural-width">
                
                <%-- The physical offset value within the current device or parent this point belongs to --%>
                <%-- 0 = No offset set --%>
                <%-- 0 - 99999999 --%>
                <tags:nameValue name="Point Offset"><tags:input path="pointBase.point.pointOffset"/></tags:nameValue>
                
                <%-- The amount the value of this point must deviate before the point is read and updated --%>
                <%-- 0 = No deadband set --%>
                <%-- 0 - 99999999 --%>
                <tags:nameValue name="Deadband"><tags:input path="pointBase.pointAnalog.deadband"/></tags:nameValue>
                
                <%-- A value that is always applied to the raw reading of this point --%>
                <%-- -99999999 - 99999999 --%>
                <tags:nameValue name="Multiplier"><tags:input path="pointBase.pointAnalog.multiplier"/></tags:nameValue>

                <%-- A value that is added to the raw reading when making calculations --%>
                <%-- -99999999 - 99999999 --%>
                <tags:nameValue name="Data Offset"><tags:input path="pointBase.pointAnalog.dataOffset"/></tags:nameValue>
                
                
            </tags:nameValueContainer>
            
            <h3>Control Settings</h3>
            <tags:nameValueContainer tableClass="natural-width">
            
                
                <%-- Enabled when Control Type is not NONE --%>
                <%-- Check this box to disable control for this point --%>
                <tags:nameValue name="Control Inhibit">
                    <tags:switchButton path="pointBase.pointAnalogControl.controlInhibited" />
                </tags:nameValue>
                
                <%-- Check this box to disable control for this point --%>
                <%-- Static Options: selLists.ptAnalogControlTypes --%>
                <tags:nameValue name="Control Type">
                    <tags:input path="pointBase.pointAnalogControl.controlType" />
                </tags:nameValue>
                
                <%-- Enabled when Control Type is not NONE --%>
                <%-- Specifies the physical location used for wiring the relay point --%>
                <%-- -99999999 - 99999999 --%>
                <%-- -99999999 - 99999999 --%>
                <tags:nameValue name="Control Pt Offset">
                    <tags:input path="pointBase.pointAnalogControl.controlOffset"/>
                </tags:nameValue>
            </tags:nameValueContainer>
            
        </c:if>
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'PulseAccumulator' or
                      pointModel.pointBase.point.pointType eq 'DemandAccumulator'}">
                      
            <h2>Physical Setup (Accumulator)</h2>
            <tags:nameValueContainer>
                <tags:nameValue name="Point Offset">
                    <%-- 0 = no offset set --%>
                    <%-- 0 - 99999999 --%>
                    <tags:input path="pointBase.point.pointOffset"/>
                </tags:nameValue>
                <tags:nameValue name="Multiplier">
                    <%-- A value that is always applied to the raw reading of this point --%>
                    <%-- -99999999 - 99999999 --%>
                    <tags:input path="pointBase.pointAccumulator.multiplier"/>
                </tags:nameValue>
                <tags:nameValue name="Data Offset">
                    <%-- A value that is added to the raw reading when making calculations --%>
                    <%-- -99999999 - 99999999 --%>
                    <tags:input path="pointBase.pointAccumulator.dataOffset"/>
                </tags:nameValue>
            </tags:nameValueContainer>
        </c:if>
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'Analog' or
                      pointModel.pointBase.point.pointType eq 'PulseAccumulator' or
                      pointModel.pointBase.point.pointType eq 'DemandAccumulator'}">
                      
            <h3>Stale Data</h3>
            <tags:nameValueContainer tableClass="natural-width">
                <%-- Turn on/off stale data checking --%>
                <tags:nameValue name="Enable">
                    <tags:switchButton path="staleData.enabled" />
                </tags:nameValue>
                
                <%-- Disabled when not staleData.enabled --%>
                <%-- 0 - 99999999 --%>
                <tags:nameValue name="Time"><tags:input path="staleData.time"/> min</tags:nameValue>
                
                <%-- Disabled when not staleData.enabled --%>
                <%-- staleData.updateStyles --%>
                <tags:nameValue name="Update Style"><tags:input path="staleData.updateStyle"/></tags:nameValue>
            </tags:nameValueContainer>
            
            <h3>Limits</h3>
            <tags:nameValueContainer tableClass="natural-width">
            
                <%-- The first limit that can be set for this point, used to determine if an alarm condition is active --%>
                <%-- Determines visibilty of the below limitOne inputs --%>
                <tags:nameValue name="Limit 1">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="limit1"/>
                </tags:nameValue>
                
                <%-- The upper value for this limit (used for an alarming condition) --%>
                <%-- -99999999 - 99999999 --%>
                <tags:nameValue name="Limit One High"><tags:input path="pointBase.limitOne.highLimit"/></tags:nameValue>
                
                <%-- The lower value for this limit (used for an alarming condition) --%>
                <%-- -99999999 - 99999999 --%>
                <tags:nameValue name="Limit One Low"><tags:input path="pointBase.limitOne.lowLimit"/></tags:nameValue>

                <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                <%-- 0 - 99999999 --%>
                <tags:nameValue name="Limit One Duration"><tags:input path="pointBase.limitOne.limitDuration"/> secs</tags:nameValue>
                
                <%-- The first limit that can be set for this point, used to determine if an alarm condition is active --%>
                <%-- Determines visibilty of the below limitTwo inputs --%>
                <tags:nameValue name="Limit 2">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="limit2"/>
                </tags:nameValue>
                
                <%-- The upper value for this limit (used for an alarming condition) --%>
                <%-- -99999999 - 99999999 --%>
                <tags:nameValue name="Limit Two High"><tags:input path="pointBase.limitTwo.highLimit"/></tags:nameValue>
                
                <%-- The lower value for this limit (used for an alarming condition) --%>
                <%-- -99999999 - 99999999 --%>
                <tags:nameValue name="Limit Two Low"><tags:input path="pointBase.limitTwo.lowLimit"/></tags:nameValue>

                <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                <%-- 0 - 99999999 --%>
                <tags:nameValue name="Limit Two Duration"><tags:input path="pointBase.limitTwo.limitDuration"/> secs</tags:nameValue>
            </tags:nameValueContainer>
            
            <h3>Reasonability</h3>
            <tags:nameValueContainer>
                <tags:nameValue name="High Reasonability">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="highReasonability"/>
                </tags:nameValue>
                
                <%-- All readings exceeding this value are ignored --%>
                <tags:nameValue name="High Reasonability Limit">
                    <tags:input path="pointBase.pointUnit.highReasonabilityLimit"/>
                </tags:nameValue>
                
                <tags:nameValue name="Low Reasonability">
                    <%-- TODO Actually wire this up --%>
                    <tags:switchButton name="lowReasonability"/>
                </tags:nameValue>
                
                <%-- All readings less than this value are ignored --%>
                <tags:nameValue name="Low Reasonability Limit">
                    <tags:input path="pointBase.pointUnit.lowReasonabilityLimit"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
        </c:if> 
        
        <c:if test="${pointModel.pointBase.point.pointType eq 'Status'}">
                      
            <h2>Physical Setup (Status)</h2>
            <tags:nameValueContainer tableClass="natural-width">
                
                <%-- The physical offset value within the current device or parent this point belongs to --%>
                <%-- 0 = No offset set --%>
                <%-- 0 - 99999999 --%>
                <tags:nameValue name="Point Offset"><tags:input path="pointBase.point.pointOffset"/></tags:nameValue>
                
                <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                <%-- Check this box to disable control for this point --%>
                <tags:nameValue name="Control Inhibit">
                    <tags:switchButton path="pointBase.pointStatusControl.controlInhibited"/>
                </tags:nameValue>
                
                <%-- Check this box to disable control for this point --%>
                <tags:nameValue name="Control Type">
                    <tags:selectWithItems path="pointBase.pointStatusControl.controlType" 
                        items="${statusControlTypes}" itemValue="controlName"/>
                </tags:nameValue>
                
                <tags:nameValue name="Control Point Offset">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- Specifies the physical location used for wiring the relay point --%>
                    <%-- -99999999 - 99999999 --%>
                    <tags:input path="pointBase.pointStatusControl.controlOffset"/>
                </tags:nameValue>
                
                <tags:nameValue name="Close Time 1">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- Specify how long each relay stays energized --%>
                    <%-- 0 - 99999 --%>
                    <tags:input path="pointBase.pointStatusControl.closeTime1"/> ms
                </tags:nameValue>
                
                <tags:nameValue name="Close Time 2">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- Specify how long each relay stays energized --%>
                    <%-- 0 - 99999 --%>
                    <tags:input path="pointBase.pointStatusControl.closeTime1"/> ms
                </tags:nameValue>
                
                <tags:nameValue name="Command Timeout">
                    <%-- Enabled when pointStatusControl.controlType not 'None' --%>
                    <%-- The length of time to use to scan for a state change following control. 
                           An alarm is raised if a state change is not detected. --%>
                    <%-- 0 - 9999999 --%>
                    <tags:input path="pointBase.pointStatusControl.commandTimeOut"/> sec
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

            <h3>Stale Data</h3>
            <tags:nameValueContainer tableClass="natural-width">
                <%-- Turn on/off stale data checking --%>
                <tags:nameValue name="Enable">
                    <tags:switchButton path="staleData.enabled" />
                </tags:nameValue>
                
                <%-- Disabled when not staleData.enabled --%>
                <%-- 0 - 99999999 --%>
                <tags:nameValue name="Time"><tags:input path="staleData.time"/>min</tags:nameValue>
                
                <%-- Disabled when not staleData.enabled --%>
                <%-- staleData.updateStyles --%>
                <tags:nameValue name="Update Style"><tags:input path="staleData.updateStyle"/></tags:nameValue>
            </tags:nameValueContainer>
        </c:if>
       
            
        <h2>Alarming</h2>
        <tags:nameValueContainer>
            <%-- Options: ptEditorForm.notifcationGrps --%>
            <tags:nameValue name="Notification Group"><tags:input path="pointBase.pointAlarming.notificationGroupID"/></tags:nameValue>
            
            <%-- Notify when alarms are Acknowledged --%>
            <tags:nameValue name="Alarm Acknowledge">
                <tags:switchButton path="pointBase.pointAlarming.notifyOnAck"/>
            </tags:nameValue>
            
            <%-- Notify when alarms Clear --%>
            <tags:nameValue name="Alarm Acknowledge">
                <tags:switchButton path="pointBase.pointAlarming.notifyOnClear"/>
            </tags:nameValue>
            
            <tags:nameValue name="Disable All Alarms">
                <tags:switchButton path="pointBase.point.alarmsDisabled"/>
            </tags:nameValue>
            
            <c:forEach var="alarmTableEntry" items="${pointModel.alarmTableEntries}" varStatus="status">
                
                <tags:nameValue name="${pointModel.alarmTableEntries[status.index].condition} Category">
                    <%-- ptEditorForm.alarmCategories  --%>
                    <tags:input path="alarmTableEntries[${status.index}].generate"/>
                </tags:nameValue>
                
                <tags:nameValue name="${pointModel.alarmTableEntries[status.index].condition} Notify">
                    <%-- selLists.ptAlarmNotification --%>
                    <tags:input path="alarmTableEntries[${status.index}].excludeNotify"/>
                </tags:nameValue>
            </c:forEach>
            
        </tags:nameValueContainer>
            
        <h2>FDR Point Editor</h2>
        <tags:nameValueContainer>
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
                    <tags:selectWithItems items="${fdrInterfaceTypes}" path="pointBase.pointFDRList[${fdrIdx}].interfaceType"/>
                </tags:nameValue>
                <tags:nameValue name="Direction ${fdrIdx}">
                    <%-- FDRDirectionsMap[interfaceType] --%>
                    
                    <tags:selectWithItems items="${fdrDirections}" path="pointBase.pointFDRList[${fdrIdx}].directionType" itemValue="value"/>
                </tags:nameValue>
                <c:if test="${not empty fdrTranslation.interfaceType}">
                <tags:nameValue name="Translation">
                    <tags:input path="pointBase.pointFDRList[${fdrIdx}].translation"/>
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
                </c:if>
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