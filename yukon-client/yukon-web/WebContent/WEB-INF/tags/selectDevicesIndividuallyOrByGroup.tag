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

<cti:msgScope paths="device.bulk.selectDevicesIndividuallyOrByGroup">
    <cti:msg2 var="lblGroup" key=".group"/>
    <cti:msg2 var="lblSelected" key=".selected"/>
    <cti:msg2 var="lblAllDevices" key=".allDevices"/>
    <div class="js-device-selection-container dib">
        <div class="button-group fl" style="margin-right:10px;">
            <tags:check name="js-select-by-device-group_${uniqueId}" label="${lblGroup}" classes="left"
                        forceDisplayButton="true"/>
            <tags:check name="js-select-individually_${uniqueId}" label="${lblSelected}" classes="right"
                        forceDisplayButton="true"/>
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
                               endEvent="${endEvent}"/>

            <c:if test="${not empty pickerPath}">
                <tags:bind path="${pickerPath}"/>
            </c:if>
        </div>

        <div class="js-device-group-picker-container ${deivceGroupPickerCss} dib dn">
            <tags:deviceGroupPicker inputName="deviceGroupNames"
                                    inputValue="${deviceGroupNames}"
                                    multi="${allowMultipleGroupSelection}"
                                    dialogId="${dialogId}"/>
            <c:if test="${not empty deviceGroupPickerPath}">
                <tags:bind path="${deviceGroupPickerPath}"/>
            </c:if>
        </div>
    </div>
</cti:msgScope>
<cti:includeScript link="/resources/js/common/yukon.selectdevicesindividuallyorbygroup.js"/>