<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.controlArea">
    <tags:setFormEditMode mode="${mode}"/>
    <input id="js-form-trigger-edit-mode" value="${mode}" type="hidden"/>
    <div class="user-message error dn js-trigger-identification-error">
        <cti:msg2 key=".trigger.identification.required"/>
    </div>
    <div class="user-message error dn js-peak-tracking-error">
        <cti:msg2 key=".trigger.peakTracking.required"/>
    </div>
    <div class="user-message error dn js-threshold-setting-error">
        <cti:msg2 key=".trigger.thresholdPointSettings.required"/>
    </div>
    <cti:url var="action" value="/dr/setup/controlArea/trigger/save"/>
    <c:set var="triggerClass" value="${mode == 'VIEW' ? '' : 'spaced-form-controls'}"/>
    <div class="js-trigger-controls ${triggerClass}">
        <form:form modelAttribute="controlAreaTrigger" action="${action}" method="post" id="js-controlArea-trigger-form">
            <cti:csrfToken/>
            <form:hidden path="triggerId"/>
            <input id="js-trigger-id" name="id" type="hidden"/>
            <form:hidden id="point-trigger-identification-name" path="triggerPointName"/>
            <c:set var="triggerType" value="${!empty controlAreaTrigger.triggerType ? controlAreaTrigger.triggerType : 'THRESHOLD_POINT'}"/>
            <c:set var="peakTrackClass" value="${!empty controlAreaTrigger.peakPointId ? '' : 'dn'}"/>
            <c:set var="projectionClass" value="${controlAreaTrigger.controlAreaProjection.projectionType == 'LSF' ? '' : 'dn'}"/>
            <c:set var="thresholdClass" value="${triggerType == 'THRESHOLD' ? '' : 'dn'}"/>
            <c:set var="thresholdPointClass" value="${triggerType == 'THRESHOLD_POINT' ? '' : 'dn'}"/>
            <c:set var="statusClass" value="${triggerType == 'STATUS' ? '' : 'dn'}"/>
            <c:set var="thresholdOrThresholdPointClass" value="${triggerType != 'STATUS' ? '' : 'dn'}"/>
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".trigger.type">
                    <cti:displayForPageEditModes modes="CREATE">
                        <tags:selectWithItems items="${triggerTypes}" path="triggerType" id="js-trigger-type" />
                    </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="VIEW,EDIT">
                        <form:hidden path="triggerType"/>
                        <i:inline key=".trigger.${triggerType}"/>
                    </cti:displayForPageEditModes>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.identification" valueClass="vam">
                    <form:hidden id="trigger-point-id" path="triggerPointId"/>
                    <tags:pickerDialog 
                        id="triggerIdentification"
                        type="devicePointPicker"
                        linkType="selection"
                        selectionProperty="paoPoint"
                        buttonStyleClass="M0"
                        destinationFieldId="trigger-point-id"
                        viewOnlyMode="${mode == 'VIEW'}"
                        includeRemoveButton="${true}"
                        removeValue="0"
                        endEvent="yukon:trigger:identification:complete"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.THRESHOLD" rowClass="js-threshold ${thresholdClass}">
                    <tags:numeric path="threshold" size="5" minValue="-999999" maxValue="999999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.minRestoreOffset" rowClass="js-threshold js-threshold-point ${thresholdOrThresholdPointClass}">
                    <tags:numeric path="minRestoreOffset" size="5" minValue="-99999" maxValue="99999"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.atku" rowClass="js-threshold ${thresholdClass}">
                    <tags:numeric path="atku" size="5" minValue="-2147483648" maxValue="2147483647"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.projectionType" rowClass="js-threshold ${thresholdClass}">
                    <tags:selectWithItems items="${projectionTypes}" path="controlAreaProjection.projectionType" id="js-threshold-projection-type"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.samples" rowClass="js-threshold js-threshold-samples-row ${projectionClass}">
                    <tags:numeric path="controlAreaProjection.projectionPoint" size="5" minValue="2" maxValue="12" id="js-threshold-samples"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.ahead" rowClass="js-threshold js-threshold-ahead-row ${projectionClass}">
                    <tags:intervalDropdown path="controlAreaProjection.projectAheadDuration" intervals="${projectAheadDurations}" id="js-threshold-ahead"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.usePeakTracking" rowClass="js-threshold js-threshold-point ${thresholdOrThresholdPointClass}">
                    <tags:switchButton name="usePeak" onNameKey=".yes.label" offNameKey=".no.label" checked="${!empty controlAreaTrigger.peakPointId}" id="js-use-peak-tracking"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.peakTracking" rowClass="js-threshold js-threshold-point ${peakTrackClass} js-peak-tracking" valueClass="vam">
                    <form:hidden id="peak-point-id" path="peakPointId"/>
                    <tags:pickerDialog
                        id="thresholdPointPeakTracking"
                        type="devicePointPicker"
                        linkType="selection"
                        selectionProperty="paoPoint"
                        buttonStyleClass="M0"
                        destinationFieldId="peak-point-id"
                        viewOnlyMode="${mode == 'VIEW'}"
                        includeRemoveButton="${true}"
                        removeValue="0"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.thresholdPointSettings" rowClass="js-threshold-point ${thresholdPointClass}" valueClass="vam">
                    <form:hidden id="threshold-point-id" path="thresholdPointId"/>
                    <tags:pickerDialog
                        id="thresholdPointThresholdSettings"
                        type="devicePointPicker"
                        linkType="selection"
                        selectionProperty="paoPoint"
                        buttonStyleClass="M0"
                        destinationFieldId="threshold-point-id"
                        viewOnlyMode="${mode == 'VIEW'}"
                        includeRemoveButton="${true}"
                        removeValue="0"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".trigger.normalState" id="js-normal-state" rowClass="js-status ${statusClass}">
                    <tags:selectWithItems items="${normalStates}" path="normalState" defaultItemValue="0" id="js-status-normal-state"
                    itemLabel="name" itemValue="id"/>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </form:form>
    </div>
</cti:msgScope>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>
<cti:includeScript link="/resources/js/pages/yukon.dr.setup.controlArea.trigger.js"/>