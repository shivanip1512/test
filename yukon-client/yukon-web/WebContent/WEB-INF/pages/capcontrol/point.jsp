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
    <form:form id="point-form" commandName="pointModel" action="${action}" method="POST" data-view-mode="${viewMode}">
    
        <cti:csrfToken />
        
        <cti:tabs containerName="yukon:da:point:fields:tab">
            <cti:tab title="General">
                <tags:nameValueContainer tableClass="${nameValueClass}">
                    <tags:nameValue name="Id">
                        <form:hidden path="pointBase.point.pointID"/>
                        ${pointModel.pointBase.point.pointID}
                    </tags:nameValue>
                    <tags:nameValue name="Name">
                        <tags:input path="pointBase.point.pointName"/>
                    </tags:nameValue>
                    
                    <%-- Only an input for creation --%>
                    <tags:nameValue name="Type">
                        <form:hidden path="pointBase.point.pointTypeEnum" cssClass="js-point-type"/>
                        <i:inline key="${pointModel.pointBase.point.pointTypeEnum}"/>
                    </tags:nameValue>
                    
                    <tags:nameValue name="Parent">
                        <form:hidden path="pointBase.point.paoID"/>
                        <cti:url var="parentLink" value="${parentLink}" />
                        <a href="${parentLink}">${fn:escapeXml(parent.paoName)}</a>
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
                                inputClass="js-archive-type" />
                            <span class="js-archive-interval">
                                <c:if test="${viewMode}">(</c:if>
                                <tags:intervalStepper path="pointBase.point.archiveInterval" intervals="${archiveIntervals}" />
                                <c:if test="${viewMode}">)</c:if>
                            </span>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Units of Measure">
                            <tags:selectWithItems path="pointBase.pointUnit.uomID" items="${unitMeasures}" 
                                itemValue="liteID" itemLabel="unitMeasureName" inputClass="${viewMode ? '' : 'js-init-chosen'}"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Decimal Digits">
                            <tags:stepperWithItems items="${decimalDigits}" path="pointBase.pointUnit.decimalPlaces"/>
                        </tags:nameValue>
            
                        <tags:nameValue name="State Group">
                            <tags:selectWithItems path="pointBase.point.stateGroupID" items="${stateGroups}" 
                                itemValue="liteID" itemLabel="stateGroupName" inputClass="${viewMode ? '' : 'js-init-chosen'}"/>
                            
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </c:if>
                
                <c:if test="${isStatusPoint}">

                    <tags:nameValueContainer tableClass="${nameValueClass}">
                        
                        <tags:nameValue name="Archive Data">
                            
                            <div class="button-group">
                                <c:forEach var="archiveType" items="${statusArchiveTypes}">
                                    <tags:radio path="pointBase.point.archiveType" classes="yes M0" 
                                        key="${archiveType}" value="${archiveType}" />
                                </c:forEach>
                            </div>
                            
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </c:if>
                
                <c:if test="${isStatusType}">
                              
                    <tags:nameValueContainer tableClass="${nameValueClass}">
                        
                        <tags:nameValue name="State Group">
                            <tags:selectWithItems path="pointBase.point.stateGroupID" items="${stateGroups}" 
                                itemValue="liteID" itemLabel="stateGroupName" inputClass="${viewMode ? '' : 'js-init-chosen'} js-state-group"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Initial State">
                            <tags:selectWithItems path="pointBase.pointStatus.initialState" items="${initialStates}" 
                                itemValue="liteID" itemLabel="stateText" inputClass="js-initial-state"/>
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </c:if>
                
                <c:if test="${isCalcType}">
                      
                    <tags:nameValueContainer tableClass="${nameValueClass}">
                        
                        <tags:nameValue name="Update Type">
                            <tags:selectWithItems path="pointBase.calcBase.updateType" items="${pointUpdateTypes}" 
                                itemValue="databaseRepresentation" inputClass="js-calc-update-type"/>
                            <span class="js-calc-period">
                                Rate:
                                <tags:intervalStepper path="pointBase.calcBase.periodicRate" 
                                    intervals="${archiveIntervals}"/>
                            </span>
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                </c:if>
            </cti:tab>

            <c:if test="${not isCalcType}">
            <cti:tab title="Physical Setup">
            
                <c:if test="${isAnalogPoint}">
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

                                    <c:set var="yesNo" value="${analogControlType eq 'NONE' ? 'no' : 'yes'}" />

                                    <tags:radio path="pointBase.pointAnalogControl.controlType"
                                        value="${analogControlType.databaseRepresentation}" key="${analogControlType}" 
                                        classes="${yesNo} M0" inputClass="js-analog-control-type"/>
                                </c:forEach>
                            </div>
                        </tags:nameValue>
                        
                        <%-- Check this box to disable control for this point --%>
                        <tags:nameValue name="Control Inhibit" 
                            nameClass="js-analog-control-input" valueClass="js-analog-control-input">
                            <tags:switchButton path="pointBase.pointAnalogControl.controlInhibited"  offClasses="M0" />
                        </tags:nameValue>
                        
                        
                        <%-- Specifies the physical location used for wiring the relay point --%>
                        <tags:nameValue name="Control Pt Offset" nameClass="js-analog-control-input"
                            valueClass="js-analog-control-input">
                            <tags:input path="pointBase.pointAnalogControl.controlOffset" size="6" />
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    
                </c:if>
                
                <c:if test="${isAccumulatorPoint}">
                              
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
                
                <c:if test="${isStatusPoint}">
                      
                    <tags:nameValueContainer tableClass="${nameValueClass}">
                        
                        <%-- The physical offset value within the current device or parent this point belongs to --%>
                        <%-- 0 = No offset set --%>
                        <tags:nameValue name="Point Offset">
                            <tags:input path="pointBase.point.pointOffset" size="6"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Control Type">
                            <tags:selectWithItems path="pointBase.pointStatusControl.controlType" 
                                items="${statusControlTypes}" itemValue="controlName" 
                                inputClass="js-status-control-type"/>
                        </tags:nameValue>
                        
                        <%-- Check this box to disable control for this point --%>
                        <tags:nameValue name="Control Inhibit"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <tags:switchButton path="pointBase.pointStatusControl.controlInhibited" offClasses="M0"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Control Point Offset"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- Specifies the physical location used for wiring the relay point --%>
                            <tags:input path="pointBase.pointStatusControl.controlOffset" size="6"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Close Time 1"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- Specify how long each relay stays energized --%>
                            <tags:input path="pointBase.pointStatusControl.closeTime1" size="6"/> ms
                        </tags:nameValue>
                        
                        <tags:nameValue name="Close Time 2"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- Specify how long each relay stays energized --%>
                            <tags:input path="pointBase.pointStatusControl.closeTime2" size="6"/> ms
                        </tags:nameValue>
                        
                        <tags:nameValue name="Command Timeout"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- The length of time to use to scan for a state change following control. 
                                   An alarm is raised if a state change is not detected. --%>
                            <tags:input path="pointBase.pointStatusControl.commandTimeOut" size="6"/> sec
                        </tags:nameValue>
                        
                        <tags:nameValue name="Open Command String"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- The OPEN command string sent out when Yukon controls this point --%>
                            <tags:input path="pointBase.pointStatusControl.stateZeroControl"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Close Command String"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- The CLOSE command string sent out when Yukon controls this point --%>
                            <tags:input path="pointBase.pointStatusControl.stateOneControl"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
        
                </c:if>
            
            </cti:tab>
            </c:if>
            
            <cti:tab title="Limits">
                <c:if test="${isScalarType}">
                    <tags:nameValueContainer tableClass="${nameValueClass}">
                    
                        <%-- The first limit that can be set for this point, used to determine if an alarm condition is active --%>
                        <%-- Determines visibility of the below limitOne inputs --%>
                        <tags:nameValue name="Limit 1">
                            <tags:switchButton path="pointBase.limitOneSpecified" offClasses="M0" 
                                toggleGroup="limitOne" toggleAction="disable"/>
                        </tags:nameValue>
                        
                        <%-- The upper value for this limit (used for an alarming condition) --%>
                        <tags:nameValue name="Upper Limit" >
                            <tags:input path="pointBase.pointLimitsMap[1].highLimit" size="6" 
                                toggleGroup="limitOne"/>
                        </tags:nameValue>
                        
                        <%-- The lower value for this limit (used for an alarming condition) --%>
                        <tags:nameValue name="Lower Limit">
                            <tags:input path="pointBase.pointLimitsMap[1].lowLimit" size="6"
                                toggleGroup="limitOne"/>
                        </tags:nameValue>
        
                        <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                        <tags:nameValue name="Duration">
                            <tags:input path="pointBase.pointLimitsMap[1].limitDuration" size="6"
                                toggleGroup="limitOne"/> secs
                        </tags:nameValue>
                        
                        <%-- The second limit that can be set for this point, used to determine if an alarm condition is active --%>
                        <%-- Determines visibilty of the below limitTwo inputs --%>
                        <tags:nameValue name="Limit 2">
                            <tags:switchButton path="pointBase.limitTwoSpecified" offClasses="M0"
                                toggleGroup="limitTwo" toggleAction="disable"/>
                        </tags:nameValue>
                        
                        <%-- The upper value for this limit (used for an alarming condition) --%>
                        <tags:nameValue name="Upper Limit">
                            <tags:input path="pointBase.pointLimitsMap[2].highLimit" size="6"
                                toggleGroup="limitTwo"/>
                        </tags:nameValue>
                        
                        <%-- The lower value for this limit (used for an alarming condition) --%>
                        <tags:nameValue name="Lower Limit">
                            <tags:input path="pointBase.pointLimitsMap[2].lowLimit" size="6"
                                toggleGroup="limitTwo"/>
                        </tags:nameValue>
        
                        <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                        <tags:nameValue name="Duration">
                            <tags:input path="pointBase.pointLimitsMap[2].limitDuration" size="6"
                                toggleGroup="limitTwo"/> secs
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                
                    <h3>Reasonability</h3>
                    <tags:nameValueContainer tableClass="${nameValueClass}">
                        <tags:nameValue name="High Reasonability">
                            <tags:switchButton path="pointBase.pointUnit.highReasonabilityValid" offClasses="M0"
                                toggleGroup="highReasonability" toggleAction="hide" inputClass="js-reasonability"/>
                            
                            <%-- All readings exceeding this value are ignored --%>
                            <tags:input path="pointBase.pointUnit.highReasonabilityLimit" size="6"
                                toggleGroup="highReasonability"/>
                        </tags:nameValue>
                        
                        <tags:nameValue name="Low Reasonability">
                            <tags:switchButton path="pointBase.pointUnit.lowReasonabilityValid" offClasses="M0"
                                toggleGroup="lowReasonability" toggleAction="hide" inputClass="js-reasonability"/>
                                
                            <%-- All readings less than this value are ignored --%>
                            <tags:input path="pointBase.pointUnit.lowReasonabilityLimit" size="6"
                                toggleGroup="lowReasonability"/>
                        </tags:nameValue>
                        
                        
                    </tags:nameValueContainer>
                </c:if>
                
                <h3>Stale Data</h3>
                <tags:nameValueContainer tableClass="${nameValueClass}">
                    <%-- Turn on/off stale data checking --%>
                    <tags:nameValue name="Stale Data">
                        <tags:switchButton path="staleData.enabled" offClasses="M0" inputClass="js-stale-data-enabled"
                         offNameKey=".disabled" onNameKey=".enabled" />
                    </tags:nameValue>
                    <tags:nameValue name="Time" nameClass="js-stale-data-input" valueClass="js-stale-data-input">
                        <tags:input path="staleData.time" size="6"/> min
                    </tags:nameValue>
                    
                    <%-- staleData.updateStyles --%>
                    <tags:nameValue name="Update Style" nameClass="js-stale-data-input" valueClass="js-stale-data-input">
                        <div class="button-group">
                            <c:forEach var="updateStyle" items="${staleDataUpdateStyles}">
                                <tags:radio path="staleData.updateStyle" 
                                    value="${updateStyle.databaseRepresentation}" key="${updateStyle}" 
                                    classes="yes M0"/>
                            </c:forEach>
                        </div>
                    </tags:nameValue>
                </tags:nameValueContainer>
            </cti:tab>
            <cti:tab title="Alarming">
                <tags:nameValueContainer tableClass="${nameValueClass} stacked-lg">
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
                </tags:nameValueContainer>
                    
                <table>
                    <thead>
                        <tr>
                            <th>Condition</th>
                            <th>Category</th>
                            <th>Notify</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="alarmTableEntry" items="${pointModel.alarmTableEntries}" varStatus="status">
                        <tr>
                            <td>${pointModel.alarmTableEntries[status.index].condition}</td>
                            <td>
                                <%-- TODO this should take the liteID for the value rather than converting --%>
                                <tags:selectWithItems path="alarmTableEntries[${status.index}].generate" 
                                    items="${alarmCategories}" itemValue="categoryName" itemLabel="categoryName" />
                            </td>
                            <td>
                                <%-- TODO this should take the liteID for the value rather than converting --%>
                                <tags:selectWithItems path="alarmTableEntries[${status.index}].excludeNotify" 
                                    items="${alarmNotificationTypes}" itemValue="dbString" />
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </cti:tab>
            
            <cti:tab title="FDR Setup">
                <%-- TODO Only show necessary members of this list --%>
                <cti:list var="nums">
                    <cti:item value="${0}" />
                    <cti:item value="${1}" />
                    <cti:item value="${2}" />
                    <cti:item value="${3}" />
                    <cti:item value="${4}" />
                </cti:list>
                <div class="separated-sections">
                    <c:set var="enabledFdrs" value="0" />
                    
                    <c:forEach var="fdrIdx" items="${nums}">
                        <c:set var="fdrTranslation" value="${pointModel.pointBase.pointFDRList[fdrIdx]}" />
                        
                        <c:if test="${not empty fdrTranslation.translation}">
                            <c:set var="enabledFdrs" value="${enabledFdrs + 1}" />
                        </c:if>
                        
                        <div class="section ${empty fdrTranslation.translation ? 'dn' : ''}" 
                            data-fdr-translation="${fdrIdx}">
                            <ul class="property-list clearfix stacked">
                                <li>
                                    <span class="name">Interface:</span>
                                    <span class="value">
                                        <tags:selectWithItems path="pointBase.pointFDRList[${fdrIdx}].interfaceType" 
                                            items="${fdrInterfaceTypes}" inputClass="js-fdr-interface" />
                                    </span>
                                </li>
                                <li>
                                    <span class="name">Direction:</span>
                                    <span class="value">
                                        <tags:selectWithItems  path="pointBase.pointFDRList[${fdrIdx}].directionType" 
                                            items="${fdrDirections}" itemValue="value" 
                                            inputClass="with-option-hiding js-fdr-direction"/>
                                    </span>
                                </li>
                                <cti:displayForPageEditModes modes="EDIT,CREATE">
                                <li>
                                    <cti:button renderMode="buttonImage" icon="icon-cross" classes="js-remove-fdr"/>
                                </li>
                                </cti:displayForPageEditModes>
                            </ul>
                            <cti:displayForPageEditModes modes="VIEW">
                                <span class="name">Translation:</span>
                                <span class="value">${fdrTranslation.translation}</span>
                            </cti:displayForPageEditModes>
                                    
                            <cti:displayForPageEditModes modes="EDIT,CREATE">
                            
                                <tags:input path="pointBase.pointFDRList[${fdrIdx}].translation"
                                    inputClass="dn js-fdr-translation" readonly="true"/>
                                    
                                <ul class="property-list clearfix js-translation-fields">
                                    <c:forEach var="field" items="${fdrProperties[fdrIdx]}">
                                        <c:set var="labelClasses" value="${field.hidden ? 'dn' : ''}" />
                                        <li class="${labelClasses}">
                                            <span class="name">${fn:escapeXml(field.name)}:</span>
                                            <span class="value">
                                                <c:choose>
                                                    <c:when test="${not empty field.options}">
                                                        <select name="${field.name}">
                                                            <c:forEach var="option" items="${field.options}">
                                                                <option value="${option}" <c:if test="${field.value == option}"> selected="selected"</c:if>>
                                                                    ${option}
                                                                </option>
                                                            </c:forEach>
                                                        </select>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input name="${field.name}" value="${field.value}" size="${fn:length(field.value)}">
                                                    </c:otherwise>
                                                </c:choose>
                                                ;
                                            </span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </cti:displayForPageEditModes>
                        </div>
                    </c:forEach>
                    
                    <c:set var="emptyClass" value="${enabledFdrs == 0 ? '' : 'dn'}" />
                    <div class="${emptyClass} js-fdr-empty">
                        <span class="empty-list">No FDR Translations</span>
                    </div>
                    
                </div>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                <c:set var="fdrAddClass" value="${fn:length(nums) == enabledFdrs ? 'dn' : ''}" />
                <div class="page-action-area stacked-md">
                    <cti:button icon="icon-add" nameKey="fdr.add" classes="js-add-fdr ${fdrAddClass}"/>
                </div>
                </cti:displayForPageEditModes>
            </cti:tab>
        </cti:tabs>
        
        <div class="page-action-area">
            
            <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <cti:url var="editUrl" value="/capcontrol/points/${pointModel.id}/edit" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:msg2 var="attachmentMsg" key="${attachment}"/>
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:point:delete" disabled="${!attachment.deletable}"
                 title="${attachmentMsg}"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${pointModel.pointBase.point.pointName}"/>
                
                <cti:url var="viewUrl" value="/capcontrol/points/${pointModel.id}" />
                <cti:button nameKey="cancel" href="${viewUrl}"/>
            
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>
            
    </form:form>
    
    <cti:url var="deleteUrl" value="/capcontrol/points/${pointModel.id}" />
    <form:form id="delete-point" action="${deleteUrl}" method="DELETE"></form:form>
    
    <cti:includeScript link="/resources/js/pages/yukon.da.point.js"/>
    
</cti:standardPage>