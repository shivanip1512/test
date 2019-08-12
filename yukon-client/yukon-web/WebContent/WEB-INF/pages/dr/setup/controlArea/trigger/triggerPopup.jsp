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
    <div class="js-trigger-controls">
        <form:form modelAttribute="controlAreaTrigger" action="${action}" method="post" id="js-controlArea-trigger-form">
            <cti:csrfToken/>
            <form:hidden id="point-trigger-identification" path="triggerPointId"/>
            <form:hidden id="point-peak-tracking" path="peakPointId"/>
            <form:hidden id="point-threshold-settings" path="thresholdPointId"/>
            <form:hidden id="point-trigger-identification-name" path="triggerPointName"/>
            <form:hidden id="trigger-type" path="triggerType"/>
            <form:hidden id="min-restore-offset" path="minRestoreOffset"/>
            <form:hidden path="triggerId"/>
            <input id="js-trigger-id" name="id" type="hidden"/>
            <input id="js-peak-point-id" value="${controlAreaTrigger.peakPointId}" type="hidden"/>
            <cti:displayForPageEditModes modes="CREATE">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".trigger.type" nameColumnWidth="170px">
                        <tags:selectWithItems items="${triggerTypes}" path="triggerType" id="js-trigger-type"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <tags:nameValueContainer2 tableClass="js-threshold-point-section">
                    <tags:nameValue2 nameKey=".trigger.minRestoreOffset" nameColumnWidth="170px">
                        <tags:numeric path="minRestoreOffset" size="11" minValue="-99999.9999" maxValue="99999.9999" id="js-threshold-point-min-restore-offset"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.identification" nameColumnWidth="170px">
                        <tags:pickerDialog 
                            id="thresholdPointTriggerIdentification"
                            type="notSystemPointPicker"
                            linkType="selectionLabel"
                            selectionProperty="paoPoint"
                            buttonStyleClass="M0"
                            destinationFieldId="point-trigger-identification"
                            viewOnlyMode="${mode == 'VIEW'}"
                            includeRemoveButton="${true}"
                            removeValue="0"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.usePeakTracking" nameColumnWidth="170px">
                        <tags:switchButton  name="usePeak" onNameKey=".yes.label" offNameKey=".no.label" checked="${!empty controlAreaTrigger.peakPointId}" 
                            id="js-use-peak-tracking-threshold-point"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.peakTracking" id="js-peak-tracking" nameColumnWidth="170px">
                        <tags:pickerDialog
                        id="thresholdPointPeakTracking"
                        type="notSystemPointPicker"
                        linkType="selectionLabel"
                        selectionProperty="paoPoint"
                        buttonStyleClass="M0"
                        destinationFieldId="point-peak-tracking"
                        viewOnlyMode="${mode == 'VIEW'}"
                        includeRemoveButton="${true}"
                        removeValue="0"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.thresholdPointSettings" nameColumnWidth="170px">
                        <tags:pickerDialog
                            id="thresholdPointThresholdSettings"
                            type="notSystemPointPicker"
                            linkType="selectionLabel"
                            selectionProperty="paoPoint"
                            buttonStyleClass="M0"
                            destinationFieldId="point-threshold-settings"
                            viewOnlyMode="${mode == 'VIEW'}"
                            includeRemoveButton="${true}"
                            removeValue="0"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <tags:nameValueContainer2 tableClass="js-threshold-section dn">
                    <tags:nameValue2 nameKey=".trigger.THRESHOLD" nameColumnWidth="170px">
                        <tags:numeric path="threshold" size="5" minValue="-999999.99999999" maxValue="999999.99999999"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.minRestoreOffset" nameColumnWidth="170px">
                        <tags:numeric path="minRestoreOffset" size="5" minValue="-99999.9999" maxValue="99999.9999" id="js-threshold-min-restore-offset"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.atku" nameColumnWidth="170px">
                        <tags:numeric path="atku" size="5" minValue="-2147483648" maxValue="2147483647"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.projectionType" nameColumnWidth="170px">
                        <tags:selectWithItems items="${projectionTypes}" path="controlAreaProjection.projectionType" id="js-threshold-projection-type"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.samples" nameColumnWidth="170px">
                        <tags:numeric path="controlAreaProjection.projectionPoint" minValue="2" maxValue="12" stepValue="1" id="js-threshold-samples"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.ahead" nameColumnWidth="170px">
                        <tags:intervalDropdown path="controlAreaProjection.projectAheadDuration" intervals="${projectAheadDurations}" id="js-threshold-ahead"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.identification" nameColumnWidth="170px">
                        <tags:pickerDialog
                            id="thresholdTriggerIdentification"
                            type="notSystemPointPicker"
                            linkType="selectionLabel"
                            selectionProperty="paoPoint"
                            buttonStyleClass="M0"
                            destinationFieldId="point-trigger-identification"
                            viewOnlyMode="${mode == 'VIEW'}"
                            includeRemoveButton="${true}"
                            removeValue="0"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.usePeakTracking" nameColumnWidth="170px">
                        <tags:switchButton  name="usePeak" onNameKey=".yes.label" offNameKey=".no.label" checked="${!empty controlAreaTrigger.peakPointId}"
                            id="js-use-peak-tracking-threshold"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.peakTracking" id="js-peak-tracking" nameColumnWidth="170px">
                        <tags:pickerDialog
                            id="thresholdPeakTracking"
                            type="notSystemPointPicker"
                            linkType="selectionLabel"
                            selectionProperty="paoPoint"
                            buttonStyleClass="M0"
                            destinationFieldId="point-peak-tracking"
                            viewOnlyMode="${mode == 'VIEW'}"
                            includeRemoveButton="${true}"
                            removeValue="0"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <tags:nameValueContainer2 tableClass="js-status-section dn">
                    <tags:nameValue2 nameKey=".trigger.identification" nameColumnWidth="170px">
                        <tags:pickerDialog
                            id="statusTriggerIdentification"
                            type="notSystemPointPicker"
                            linkType="selectionLabel"
                            selectionProperty="paoPoint"
                            buttonStyleClass="M0"
                            destinationFieldId="point-trigger-identification"
                            viewOnlyMode="${mode == 'VIEW'}"
                            includeRemoveButton="${true}"
                            removeValue="0"
                            endEvent="yukon:trigger:identification:complete"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trigger.normalState" nameColumnWidth="170px" rowClass="dn" id="js-normal-state">
                        <tags:selectWithItems items="${normalStates}" path="normalState" defaultItemValue="0" id="js-status-normal-state-create"
                        itemLabel="name" itemValue="id"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".trigger.type" nameColumnWidth="170px">
                        <span class="js-trigger-type-view-mode">
                           <i:inline key="yukon.web.modules.dr.setup.controlArea.trigger.${controlAreaTrigger.triggerType}"/>
                        </span>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                <c:set var="triggerType" value="${controlAreaTrigger.triggerType}"/>
                <c:choose>
                    <c:when test="${triggerType eq 'THRESHOLD_POINT'}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".trigger.minRestoreOffset" nameColumnWidth="170px">
                                <tags:numeric path="minRestoreOffset" size="11" minValue="-99999.9999" maxValue="99999.9999" id="js-threshold-point-min-restore-offset"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.identification" nameColumnWidth="170px">
                                <tags:pickerDialog
                                    id="thresholdPointTriggerIdentification"
                                    type="notSystemPointPicker"
                                    linkType="selectionLabel"
                                    selectionProperty="paoPoint"
                                    buttonStyleClass="M0"
                                    destinationFieldId="point-trigger-identification"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    includeRemoveButton="${true}"
                                    removeValue="0"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.usePeakTracking" nameColumnWidth="170px">
                                <tags:switchButton  name="usePeak" onNameKey=".yes.label" offNameKey=".no.label" checked="${!empty controlAreaTrigger.peakPointId}" 
                                    id="js-use-peak-tracking-threshold-point"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.peakTracking" id="js-peak-tracking" nameColumnWidth="170px">
                                <c:choose>
                                    <c:when test="${!empty controlAreaTrigger.peakPointId}">
                                        <tags:pickerDialog
                                            id="thresholdPointPeakTracking"
                                            type="notSystemPointPicker"
                                            linkType="selectionLabel"
                                            selectionProperty="paoPoint"
                                            buttonStyleClass="M0"
                                            destinationFieldId="point-peak-tracking"
                                            viewOnlyMode="${mode == 'VIEW'}"
                                            includeRemoveButton="${true}"
                                            removeValue="0"/>
                                    </c:when>
                                    <c:otherwise>
                                        <i:inline key=".trigger.none"/>
                                    </c:otherwise>
                                </c:choose>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.thresholdPointSettings" nameColumnWidth="170px">
                                <tags:pickerDialog
                                    id="thresholdPointThresholdSettings"
                                    type="notSystemPointPicker"
                                    linkType="selectionLabel"
                                    selectionProperty="paoPoint"
                                    buttonStyleClass="M0"
                                    destinationFieldId="point-threshold-settings"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    includeRemoveButton="${true}"
                                    removeValue="0"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:when>
                    <c:when test="${triggerType eq 'THRESHOLD'}">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".trigger.THRESHOLD" nameColumnWidth="170px">
                                <tags:numeric path="threshold" size="5" minValue="-999999.99999999" maxValue="999999.99999999"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.minRestoreOffset" nameColumnWidth="170px">
                                <tags:numeric path="minRestoreOffset" size="5" minValue="-99999.9999" maxValue="99999.9999" id="js-threshold-min-restore-offset"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.atku" nameColumnWidth="170px">
                                <tags:numeric path="atku" size="5" minValue="-2147483648" maxValue="2147483647"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.projectionType" nameColumnWidth="170px">
                                <tags:selectWithItems items="${projectionTypes}" path="controlAreaProjection.projectionType" id="js-threshold-projection-type"/>
                            </tags:nameValue2>
                            <c:set var = "projectionType" value="${controlAreaProjection.projectionType}"></c:set>
                            <c:choose>
                                <c:when test="${not empty projectionType}">
                                    <tags:nameValue2 nameKey=".trigger.samples" nameColumnWidth="170px">
                                        <tags:numeric path="controlAreaProjection.projectionPoint" minValue="2" maxValue="12" stepValue="1"
                                            id="js-threshold-samples"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".trigger.ahead" nameColumnWidth="170px">
                                        <tags:intervalDropdown path="controlAreaProjection.projectAheadDuration" intervals="${projectAheadDurations}" id="js-threshold-ahead"/>
                                    </tags:nameValue2>
                                </c:when>
                                <c:otherwise>
                                    <tags:nameValue2 nameKey=".trigger.samples" nameColumnWidth="170px">
                                        <tags:numeric path="controlAreaProjection.projectionPoint" minValue="2" maxValue="12" stepValue="1"
                                            id="js-threshold-samples"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".trigger.ahead" nameColumnWidth="170px">
                                        <tags:intervalDropdown path="controlAreaProjection.projectAheadDuration" intervals="${projectAheadDurations}"
                                            id="js-threshold-ahead"/>
                                    </tags:nameValue2>
                                </c:otherwise>
                            </c:choose>
                            <tags:nameValue2 nameKey=".trigger.identification" nameColumnWidth="170px">
                                <tags:pickerDialog
                                    id="thresholdTriggerIdentification"
                                    type="notSystemPointPicker"
                                    linkType="selectionLabel"
                                    selectionProperty="paoPoint"
                                    buttonStyleClass="M0"
                                    destinationFieldId="point-trigger-identification"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    includeRemoveButton="${true}"
                                    removeValue="0"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.usePeakTracking" nameColumnWidth="170px">
                                <tags:switchButton  name="usePeak" onNameKey=".yes.label" offNameKey=".no.label" checked="${!empty controlAreaTrigger.peakPointId}" 
                                    id="js-use-peak-tracking-threshold"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.peakTracking" id="js-peak-tracking" nameColumnWidth="170px">
                                <c:choose>
                                    <c:when test="${!empty controlAreaTrigger.peakPointId}">
                                        <tags:pickerDialog
                                            id="thresholdPeakTracking"
                                            type="notSystemPointPicker"
                                            linkType="selectionLabel"
                                            selectionProperty="paoPoint"
                                            buttonStyleClass="M0"
                                            destinationFieldId="point-peak-tracking"
                                            viewOnlyMode="${mode == 'VIEW'}"
                                            includeRemoveButton="${true}"
                                            removeValue="0"/>
                                    </c:when>
                                    <c:otherwise>
                                      <i:inline key=".trigger.none"/>
                                    </c:otherwise>
                                </c:choose>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:when>
                    <c:otherwise>
                        <tags:nameValueContainer2>
                        
                            <tags:nameValue2 nameKey=".trigger.identification" nameColumnWidth="170px">
                                <tags:pickerDialog
                                    id="statusTriggerIdentification"
                                    type="notSystemPointPicker"
                                    linkType="selectionLabel"
                                    selectionProperty="paoPoint"
                                    buttonStyleClass="M0"
                                    destinationFieldId="point-trigger-identification"
                                    viewOnlyMode="${mode == 'VIEW'}"
                                    includeRemoveButton="${true}"
                                    removeValue="0"
                                    endEvent="yukon:trigger:identification:complete"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trigger.normalState" nameColumnWidth="170px">
                                <tags:selectWithItems items="${normalStates}" path="normalState" defaultItemValue="0" id="js-status-normal-state-edit"
                                    itemLabel="name" itemValue="id"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </c:otherwise>
                </c:choose>
            </cti:displayForPageEditModes>
        </form:form>
    </div>
</cti:msgScope>
<cti:includeScript link="YUKON_TIME_FORMATTER"/>
<cti:includeScript link="/resources/js/pages/yukon.dr.setup.controlArea.trigger.js"/>