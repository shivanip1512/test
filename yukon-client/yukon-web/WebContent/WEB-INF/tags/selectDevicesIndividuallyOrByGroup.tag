<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="uniqueId" description="Used as part of an html id attribute and part of a javascript function name. 
                                           Should not contain invalid characters like '.' or '-'." %>
<%@ attribute name="pickerCss" required="false" description="The css classes to be applied picker." %>
<%@ attribute name="pickerType" required="true" description="The type of the device picker." %>
<%@ attribute name="pickerPath" required="false" description="The field to bind to the picker." %>
<%@ attribute name="initialIds" type="java.util.HashSet" required="false" description="Ids of items to be pre-selected." %>
<%@ attribute name="pickerCallback" required="false" description="The callback fired when an individual device is selected." %>
<%@ attribute name="deviceGroupPickerDialogId" required="false" description="The dialog id for the device group picker." %>
<%@ attribute name="deviceGroupNames" type="java.util.ArrayList" required="false" description="List of device group names per-selected." %>
<%@ attribute name="deivceGroupPickerCss" required="false" description="The css classes to be applied to the device group picker." %>
<%@ attribute name="deviceGroupPickerPath" required="false" description="The field to bind to the device group picker." %>
<%@ attribute name="allowMultipleGroupSelection" required="false" description="Allows multiple device groups to be selected if set to true." %>
<%@ attribute name="defaultPaoSelectionMethod" type="com.cannontech.common.pao.PaoSelectionMethod"
              description="The pao selection to be set by default" required="false"%>

<cti:msgScope paths="yukon.common, device.bulk.selectDevicesIndividuallyOrByGroup">
    <cti:msg2 var="lblGroup" key=".group"/>
    <cti:msg2 var="lblSelected" key=".selected"/>
    <cti:msg2 var="lblAll" key=".all"/>
    
    <div class="js-device-selection-container dib">
        <c:set var="allDevicesBtnCss" value="${(defaultPaoSelectionMethod == 'allDevices' or empty defaultPaoSelectionMethod) ? 'on' : ''}"/>
        <c:set var="selectIndividuallyBtnCss" value="${defaultPaoSelectionMethod == 'selectIndividually' ? 'on' : ''}"/>
        <c:set var="byDeviceGroupsBtnCss" value="${defaultPaoSelectionMethod == 'byDeviceGroups' ? 'on' : ''}"/>
        
        <div class="button-group button-group-toggle">
            <cti:button label="${lblAll}" icon="icon-brick-add" classes="js-all-devices js-device-selection ${allDevicesBtnCss}"/>
            <cti:button label="${lblGroup}" icon="icon-folder" classes="js-select-by-device-group js-device-selection ${byDeviceGroupsBtnCss}"/>
            <cti:button label="${lblSelected}" icon="icon-table" classes="js-device-selection js-select-individually ${selectIndividuallyBtnCss}"/>
        </div>
        
        <div class="dib dn js-picker-dialog ${pickerCss}">
            <tags:pickerDialog type="${pickerType}"
                               id="paoPicker_${uniqueId}"
                               linkType="selection"
                               selectionProperty="paoName"
                               destinationFieldName="paoIds"
                               allowEmptySelection="true"
                               multiSelectMode="true"
                               initialIds="${initialIds}"
                               endEvent="${pickerCallback}"/>

            <c:if test="${not empty pickerPath}">
                <tags:bind path="${pickerPath}"/>
            </c:if>
        </div>

        <div class="js-device-group-picker-container ${deivceGroupPickerCss} dib dn">
            <tags:deviceGroupPicker inputName="deviceGroupNames"
                                    inputValue="${deviceGroupNames}"
                                    multi="${allowMultipleGroupSelection}"
                                    dialogId="${deviceGroupPickerDialogId}"/>
            <c:if test="${not empty deviceGroupPickerPath}">
                <tags:bind path="${deviceGroupPickerPath}"/>
            </c:if>
        </div>
    </div>
</cti:msgScope>
<cti:includeScript link="/resources/js/common/yukon.selectdevicesindividuallyorbygroup.js"/>