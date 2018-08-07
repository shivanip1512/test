<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div class="js-pao-selection-container dib vat">
    <cti:uniqueIdentifier var="id" />
    <input type="hidden" class="js-unique-identifier" value="${id}"/>
    <input type="hidden" class="js-allDevices-enum-val" value="${allDevicesEnumValue}"/>
    <input type="hidden" class="js-selectIndividually-enum-val" value="${selectIndividuallyEnumValue}"/>
    <input type="hidden" class="js-byDeviceGroup-enum-val" value="${byDeviceGroupsEnumValue}"/>
    
    <cti:list var="groups">
        <c:forEach var="subGroup" items="${deviceGroupNames}">
            <cti:item value="${subGroup}"/>
        </c:forEach>
    </cti:list>
    
    <tags:hidden path="paoSelectionMethod"/>
    <div class="dib" style="margin-left: -4px">
        <tags:selectDevicesIndividuallyOrByGroup uniqueId="${id}"
                                                 pickerType="meterPicker"
                                                 initialIds="${paoNoteFilter.paoIds}"
                                                 pickerCallback="yukon:paonotessearch:paosselected"
                                                 pickerCss="push-down-3"
                                                 pickerPath="paoIds"
                                                 deviceGroupNames="${groups}"
                                                 deviceGroupPickerDialogId="js-paonotes-device-group-picker_${id}"
                                                 deivceGroupPickerCss="push-down-3"
                                                 deviceGroupPickerPath="deviceGroups"
                                                 allowMultipleGroupSelection="true"/>
        <input type="hidden" id="js-clear-device-group-selection_${id}"/>
        <cti:msg2 var="lblAllDevices" key=".allDevices"/>
        <div class="dib js-all-devices-lbl vat push-down-3">${lblAllDevices}</div>
    </div>
</div>

<cti:includeScript link="/resources/js/common/yukon.paonotes.paoselection.js"/>