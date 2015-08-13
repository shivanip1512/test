<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="point.${mode}">

    <tags:setFormEditMode mode="${mode}" />
    <c:set var="viewMode" value="${false}" />
    <cti:displayForPageEditModes modes="VIEW">
        <c:set var="viewMode" value="${true}" />
    </cti:displayForPageEditModes>

    <c:set var="nameValueClass" value="natural-width ${viewMode ? '' : 'with-form-controls' }" />

    <cti:url var="action" value="/tools/points/${pointModel.pointBase.point.pointType}" />
    <form:form id="point-form" commandName="pointModel" action="${action}" method="POST" data-view-mode="${viewMode}">

        <cti:csrfToken />

        <cti:tabs containerName="yukon:da:point:fields:tab">
            <cti:msg2 var="general" key=".tab.general" />
            <cti:tab title="${general}">
                <form:hidden path="pointBase.point.pointID"/>

                <tags:nameValueContainer2 tableClass="${nameValueClass} stacked-md">

                    <tags:nameValue2 nameKey=".name">
                        <tags:input path="pointBase.point.pointName"/>
                    </tags:nameValue2>

                    <tags:nameValue2 excludeColon="${true}">
                        <tags:switchButton path="pointBase.point.outOfService"  offClasses="M0"
                            offNameKey=".disabled.label" onNameKey=".enabled.label" inverse="${true}" />
                    </tags:nameValue2>

                    <tags:nameValue2 nameKey="yukon.common.type">
                        <form:hidden path="pointBase.point.pointTypeEnum" cssClass="js-point-type"/>
                        <i:inline key="${pointModel.pointBase.point.pointTypeEnum}"/>
                    </tags:nameValue2>

                    <tags:nameValue2 nameKey=".parent">
                        <form:hidden path="pointBase.point.paoID"/>
                        <cti:url var="parentLink" value="${parentLink}" />
                        <a href="${parentLink}">${parentName}</a>
                    </tags:nameValue2>

                    <tags:nameValue2 nameKey=".timingGroup">
                        <div class="button-group">
                            <c:forEach var="logicalGroup" items="${logicalGroups}">
                                <tags:radio path="pointBase.point.logicalGroup" value="${logicalGroup.dbValue}" 
                                    key="${logicalGroup}" classes="yes M0" />
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>

                <h3><i:inline key=".settings"/></h3>

                <c:if test="${isScalarType}">
                    <tags:nameValueContainer2 tableClass="${nameValueClass}">

                        <tags:nameValue2 nameKey=".archive">
                            <tags:selectWithItems path="pointBase.point.archiveType" items="${scalarArchiveTypes}"
                                inputClass="js-archive-type" />
                            <span class="js-archive-interval">
                                <c:if test="${viewMode}">(</c:if>
                                <tags:intervalStepper path="pointBase.point.archiveInterval" intervals="${archiveIntervals}" />
                                <c:if test="${viewMode}">)</c:if>
                            </span>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey="yukon.common.units">
                            <tags:selectWithItems path="pointBase.pointUnit.uomID" items="${unitMeasures}" 
                                itemValue="liteID" itemLabel="unitMeasureName" inputClass="${viewMode ? '' : 'js-init-chosen'}"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".digits">
                            <tags:stepperWithItems items="${decimalDigits}" path="pointBase.pointUnit.decimalPlaces"/>
                        </tags:nameValue2>
            
                        <tags:nameValue2 nameKey=".stateGroup">
                            <tags:selectWithItems path="pointBase.point.stateGroupID" items="${stateGroups}" 
                                itemValue="liteID" itemLabel="stateGroupName" inputClass="${viewMode ? '' : 'js-init-chosen'}"/>

                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </c:if>

                <c:if test="${isStatusType}">

                    <tags:nameValueContainer2 tableClass="${nameValueClass}">

                        <c:if test="${isStatusPoint}">
                        <tags:nameValue2 nameKey=".archive">

                            <div class="button-group">
                                <c:forEach var="archiveType" items="${statusArchiveTypes}">
                                    <tags:radio path="pointBase.point.archiveType" classes="yes M0" 
                                        key="${archiveType}" value="${archiveType}" />
                                </c:forEach>
                            </div>

                        </tags:nameValue2>
                        </c:if>

                        <tags:nameValue2 nameKey=".stateGroup">
                            <tags:selectWithItems path="pointBase.point.stateGroupID" items="${stateGroups}" 
                                itemValue="liteID" itemLabel="stateGroupName" 
                                inputClass="${viewMode ? '' : 'js-init-chosen'} js-state-group"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".initialState">
                            <tags:selectWithItems path="pointBase.pointStatus.initialState" items="${initialStates}" 
                                itemValue="liteID" itemLabel="stateText" inputClass="js-initial-state"/>
                        </tags:nameValue2>

                    </tags:nameValueContainer2>
                </c:if>

                <c:if test="${isCalcType}">

                    <tags:nameValueContainer2 tableClass="${nameValueClass}">

                        <tags:nameValue2 nameKey=".update">
                            <tags:selectWithItems path="pointBase.calcBase.updateType" items="${pointUpdateTypes}" 
                                itemValue="databaseRepresentation" inputClass="js-calc-update-type"/>
                            <span class="js-calc-period">
                                <tags:intervalStepper path="pointBase.calcBase.periodicRate" 
                                    intervals="${archiveIntervals}"/>
                            </span>
                        </tags:nameValue2>

                    </tags:nameValueContainer2>
                </c:if>
            </cti:tab>

            <c:if test="${not isCalcType}">
            <cti:msg2 var="physicalTab" key=".tab.physical" />
            <cti:tab title="${physicalTab}">

                <c:if test="${isAnalogPoint}">
                    <tags:nameValueContainer2 tableClass="${nameValueClass}">

                        <%-- The physical offset value within the current device or parent this point belongs to --%>
                        <tags:nameValue2 nameKey=".offset">
                            <%-- 0 = No offset set --%>
                            <tags:input path="pointBase.point.pointOffset" size="6"/>
                        </tags:nameValue2>

                        <%-- The amount the value of this point must deviate before the point is read and updated --%>
                        <tags:nameValue2 nameKey=".deadband">
                            <%-- 0 = No deadband set --%>
                            <tags:input path="pointBase.pointAnalog.deadband" size="6"/>
                        </tags:nameValue2>

                        <%-- A value that is always applied to the raw reading of this point --%>
                        <tags:nameValue2 nameKey=".multiplier">
                            <tags:input path="pointBase.pointAnalog.multiplier" size="6"/>
                        </tags:nameValue2>

                        <%-- A value that is added to the raw reading when making calculations --%>
                        <tags:nameValue2 nameKey=".dataOffset">
                            <tags:input path="pointBase.pointAnalog.dataOffset" size="6"/>
                        </tags:nameValue2>

                    </tags:nameValueContainer2>
                    <h3><i:inline key=".control"/></h3>
                    <tags:nameValueContainer2 tableClass="${nameValueClass}">

                        <%-- Check this box to disable control for this point --%>
                        <tags:nameValue2 nameKey=".control.type">
                            <div class="button-group">
                                <c:forEach var="analogControlType" items="${analogControlTypes}">

                                    <c:set var="yesNo" value="${analogControlType eq 'NONE' ? 'no' : 'yes'}" />

                                    <tags:radio path="pointBase.pointAnalogControl.controlType"
                                        value="${analogControlType.databaseRepresentation}" key="${analogControlType}" 
                                        classes="${yesNo} M0" inputClass="js-analog-control-type"/>
                                </c:forEach>
                            </div>
                        </tags:nameValue2>

                        <%-- Check this box to disable control for this point --%>
                        <tags:nameValue2 nameKey=".control.inhibit" rowClass="js-analog-control-input">
                            <tags:switchButton path="pointBase.pointAnalogControl.controlInhibited"  offClasses="M0" />
                        </tags:nameValue2>

                        <%-- Specifies the physical location used for wiring the relay point --%>
                        <tags:nameValue2 nameKey=".control.offset" rowClass="js-analog-control-input">
                            <tags:input path="pointBase.pointAnalogControl.controlOffset" size="6" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>

                </c:if>

                <c:if test="${isAccumulatorPoint}">

                    <tags:nameValueContainer2 tableClass="${nameValueClass}">
                        <tags:nameValue2 nameKey=".offset">
                            <%-- 0 = no offset set --%>
                            <tags:input path="pointBase.point.pointOffset" size="6"/>
                        </tags:nameValue2>

                        <%-- A value that is always applied to the raw reading of this point --%>
                        <tags:nameValue2 nameKey=".multiplier">
                            <tags:input path="pointBase.pointAccumulator.multiplier" size="6"/>
                        </tags:nameValue2>

                        <%-- A value that is added to the raw reading when making calculations --%>
                        <tags:nameValue2 nameKey=".dataOffset">
                            <tags:input path="pointBase.pointAccumulator.dataOffset" size="6"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </c:if>

                <c:if test="${isStatusPoint}">

                    <tags:nameValueContainer2 tableClass="${nameValueClass}">
                        
                        <%-- The physical offset value within the current device or parent this point belongs to --%>
                        <%-- 0 = No offset set --%>
                        <tags:nameValue2 nameKey=".offset">
                            <tags:input path="pointBase.point.pointOffset" size="6"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".control.type">
                            <tags:selectWithItems path="pointBase.pointStatusControl.controlType" 
                                items="${statusControlTypes}" itemValue="controlName" 
                                inputClass="js-status-control-type"/>
                        </tags:nameValue2>

                        <%-- Check this box to disable control for this point --%>
                        <tags:nameValue2 nameKey=".control.inhibit"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <tags:switchButton path="pointBase.pointStatusControl.controlInhibited" offClasses="M0"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".control.offset"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- Specifies the physical location used for wiring the relay point --%>
                            <tags:input path="pointBase.pointStatusControl.controlOffset" size="6"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".close.time1"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- Specify how long each relay stays energized --%>
                            <tags:input path="pointBase.pointStatusControl.closeTime1" size="6"/>
                            <i:inline key="yukon.common.durationFormatting.symbol.MS_SHORT.suffix.singular"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".close.time2"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- Specify how long each relay stays energized --%>
                            <tags:input path="pointBase.pointStatusControl.closeTime2" size="6"/>
                            <i:inline key="yukon.common.durationFormatting.symbol.MS_SHORT.suffix.singular"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".command.timeout"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- The length of time to use to scan for a state change following control. 
                                   An alarm is raised if a state change is not detected. --%>
                            <tags:input path="pointBase.pointStatusControl.commandTimeOut" size="6"/>
                            <i:inline key="yukon.common.durationFormatting.symbol.S_SHORT.suffix.singular"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".command.open"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- The OPEN command string sent out when Yukon controls this point --%>
                            <tags:input path="pointBase.pointStatusControl.stateZeroControl"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".command.close"
                            nameClass="js-status-control-input" valueClass="js-status-control-input">
                            <%-- The CLOSE command string sent out when Yukon controls this point --%>
                            <tags:input path="pointBase.pointStatusControl.stateOneControl"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </c:if>
            </cti:tab>
            </c:if>

            <cti:msg2 var="limitsTab" key=".tab.limits"/>
            <cti:tab title="${limitsTab}">

                <c:if test="${isScalarType}">
                    <tags:nameValueContainer2 tableClass="${nameValueClass}">

                        <%-- The first limit that can be set for this point, used to determine if an alarm condition is active --%>
                        <%-- Determines visibility of the below limitOne inputs --%>
                        <tags:nameValue2 nameKey=".limit.1">
                            <tags:switchButton path="pointBase.limitOneSpecified" offClasses="M0" 
                                toggleGroup="limitOne" toggleAction="disable"/>
                        </tags:nameValue2>

                        <%-- The upper value for this limit (used for an alarming condition) --%>
                        <tags:nameValue2 nameKey=".limit.upper">
                            <tags:input path="pointBase.pointLimitsMap[1].highLimit" size="6" 
                                toggleGroup="limitOne"/>
                        </tags:nameValue2>

                        <%-- The lower value for this limit (used for an alarming condition) --%>
                        <tags:nameValue2 nameKey=".limit.lower">
                            <tags:input path="pointBase.pointLimitsMap[1].lowLimit" size="6"
                                toggleGroup="limitOne"/>
                        </tags:nameValue2>

                        <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                        <tags:nameValue2 nameKey=".limit.duration">
                            <tags:input path="pointBase.pointLimitsMap[1].limitDuration" size="6"
                                toggleGroup="limitOne"/>
                            <i:inline key="yukon.common.durationFormatting.symbol.S_SHORT.suffix.singular"/>
                        </tags:nameValue2>

                        <%-- The second limit that can be set for this point, used to determine if an alarm condition is active --%>
                        <%-- Determines visibilty of the below limitTwo inputs --%>
                        <tags:nameValue2 nameKey=".limit.2">
                            <tags:switchButton path="pointBase.limitTwoSpecified" offClasses="M0"
                                toggleGroup="limitTwo" toggleAction="disable"/>
                        </tags:nameValue2>

                        <%-- The upper value for this limit (used for an alarming condition) --%>
                        <tags:nameValue2 nameKey=".limit.upper">
                            <tags:input path="pointBase.pointLimitsMap[2].highLimit" size="6"
                                toggleGroup="limitTwo"/>
                        </tags:nameValue2>

                        <%-- The lower value for this limit (used for an alarming condition) --%>
                        <tags:nameValue2 nameKey=".limit.lower">
                            <tags:input path="pointBase.pointLimitsMap[2].lowLimit" size="6"
                                toggleGroup="limitTwo"/>
                        </tags:nameValue2>

                        <%-- The number of seconds the limit must be violated before an alarm is generated --%>
                        <tags:nameValue2 nameKey=".limit.duration">
                            <tags:input path="pointBase.pointLimitsMap[2].limitDuration" size="6"
                                toggleGroup="limitTwo"/>
                            <i:inline key="yukon.common.durationFormatting.symbol.S_SHORT.suffix.singular"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".reasonability.high">
                            <tags:switchButton path="pointBase.pointUnit.highReasonabilityValid" offClasses="M0"
                                toggleGroup="highReasonability" toggleAction="hide" inputClass="js-reasonability"/>

                            <%-- All readings exceeding this value are ignored --%>
                            <tags:input path="pointBase.pointUnit.highReasonabilityLimit" size="6"
                                toggleGroup="highReasonability"/>
                        </tags:nameValue2>

                        <tags:nameValue2 nameKey=".reasonability.low">
                            <tags:switchButton path="pointBase.pointUnit.lowReasonabilityValid" offClasses="M0"
                                toggleGroup="lowReasonability" toggleAction="hide" inputClass="js-reasonability"/>

                            <%-- All readings less than this value are ignored --%>
                            <tags:input path="pointBase.pointUnit.lowReasonabilityLimit" size="6"
                                toggleGroup="lowReasonability"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </c:if>

                <h3><i:inline key=".stale.data"/></h3>

                <tags:nameValueContainer2 tableClass="${nameValueClass}">

                    <%-- Turn on/off stale data checking --%>
                    <tags:nameValue2 nameKey=".stale.data">
                        <tags:switchButton path="staleData.enabled" offClasses="M0" inputClass="js-stale-data-enabled"
                         offNameKey=".disabled.label" onNameKey=".enabled.label" />
                    </tags:nameValue2>

                    <tags:nameValue2 nameKey=".stale.time" rowClass="js-stale-data-input">
                        <tags:input path="staleData.time" size="6"/>
                        <i:inline key="yukon.common.durationFormatting.symbol.M_SHORT.suffix.singular"/>
                    </tags:nameValue2>

                    <tags:nameValue2 nameKey=".stale.update" rowClass="js-stale-data-input">
                        <div class="button-group">
                            <c:forEach var="updateStyle" items="${staleDataUpdateStyles}">
                                <tags:radio path="staleData.updateStyle" 
                                    value="${updateStyle.databaseRepresentation}" key="${updateStyle}" 
                                    classes="yes M0"/>
                            </c:forEach>
                        </div>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </cti:tab>

            <cti:msg2 var="alarmingTab" key=".tab.alarming"/>
            <cti:tab title="${alarmingTab}">
                <tags:nameValueContainer2 tableClass="${nameValueClass} stacked-lg">

                    <tags:nameValue2 nameKey=".alarm.group">
                        <tags:selectWithItems path="pointBase.pointAlarming.notificationGroupID" items="${notificationGroups}" 
                            itemValue="liteID" itemLabel="notificationGroupName" />
                    </tags:nameValue2>

                    <%-- Notify when alarms are Acknowledged --%>
                    <tags:nameValue2 nameKey=".alarm.acknowledge">
                        <tags:switchButton path="pointBase.pointAlarming.notifyOnAck" offClasses="M0"/>
                    </tags:nameValue2>

                    <%-- Notify when alarms Clear --%>
                    <tags:nameValue2 nameKey=".alarm.clear">
                        <tags:switchButton path="pointBase.pointAlarming.notifyOnClear" offClasses="M0"/>
                    </tags:nameValue2>

                    <tags:nameValue2 nameKey=".alarm.disableAll">
                        <tags:switchButton path="pointBase.point.alarmsDisabled" offClasses="M0"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>

                <table>
                    <thead>
                        <tr>
                            <th><i:inline key=".alarm.condition"/></th>
                            <th><i:inline key=".alarm.category"/></th>
                            <th><i:inline key=".alarm.notify"/></th>
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

            <cti:msg2 var="fdrTab" key=".tab.fdr"/>
            <cti:tab title="${fdrTab}">
                <div class="separated-sections">
                    <c:set var="enabledFdrs" value="0" />

                    <c:forEach var="fdrIdx" items="${fdrTranslationNumbers}">
                        <c:set var="fdrTranslation" value="${pointModel.pointBase.pointFDRList[fdrIdx]}" />

                        <c:if test="${not empty fdrTranslation.translation}">
                            <c:set var="enabledFdrs" value="${enabledFdrs + 1}" />
                        </c:if>
                        
                        <div class="section ${empty fdrTranslation.translation ? 'dn' : ''}" 
                            data-fdr-translation="${fdrIdx}">
                            <ul class="property-list clearfix stacked">
                                <li>
                                    <span class="name"><i:inline key=".fdr.interface"/></span>
                                    <span class="value">
                                        <tags:selectWithItems path="pointBase.pointFDRList[${fdrIdx}].interfaceType" 
                                            items="${fdrInterfaceTypes}" inputClass="js-fdr-interface" />
                                    </span>
                                </li>
                                <li>
                                    <span class="name"><i:inline key=".fdr.direction"/></span>
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
                                <span class="name"><i:inline key=".fdr.translation"/>:</span>
                                <span class="value">
                                    <input readonly disabled value="${fdrTranslation.translation}"
                                        size="${fn:length(fdrTranslation.translation)}">
                                </span>
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
                        <span class="empty-list"><i:inline key=".fdr.none"/></span>
                    </div>

                </div>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                <c:set var="fdrAddClass" value="${fn:length(fdrTranslationNumbers) == enabledFdrs ? 'dn' : ''}" />
                <div class="page-action-area stacked-md">
                    <cti:button icon="icon-add" nameKey="fdr.add" classes="js-add-fdr ${fdrAddClass}"/>
                </div>
                </cti:displayForPageEditModes>
            </cti:tab>
        </cti:tabs>

        <div class="page-action-area">

            <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <cti:url var="editUrl" value="/tools/points/${pointModel.id}/edit" />
                <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
            </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="EDIT">
                <cti:msg2 var="attachmentMsg" key="${attachment}"/>
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:point:delete" 
                    disabled="${!attachment.deletable}" title="${attachmentMsg}"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${pointModel.pointBase.point.pointName}"/>

                <cti:url var="viewUrl" value="/tools/points/${pointModel.id}" />
                <cti:button nameKey="cancel" href="${viewUrl}"/>

            </cti:displayForPageEditModes>

            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>

    </form:form>

    <cti:url var="deleteUrl" value="/tools/points/${pointModel.id}" />
    <form:form id="delete-point" action="${deleteUrl}" method="DELETE"></form:form>

    <cti:includeScript link="/resources/js/pages/yukon.da.point.js"/>

</cti:standardPage>