<%@ tag body-content="empty" description="Device group name picker" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="dataJson" required="true" %>
<%@ attribute name="id" %>
<%@ attribute name="classes" %>
<%@ attribute name="fieldId" description="If not supplied, the fieldName will be used" %>
<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="fieldValue" %>
<%@ attribute name="linkGroupName" description="Will make the group name a link that when clicked brings you to the group editor for group" %>
<%@ attribute name="noGroupSelectedText" %>
<%@ attribute name="noGroupSelectedAlertText" %>
<%@ attribute name="showSelectedDevicesIcon" %>
<%@ attribute name="submitCallback" description="optional additional function to call when group is picked" %>

<cti:default var="submitCallback" value="yukon.nothing"/>

<cti:uniqueIdentifier var="uniqueId" prefix="deviceGroupNameSelectorTag_"/>

<c:if test="${empty pageScope.noGroupSelectedText}">
    <cti:msg2 var="noGroupSelectedText" key="yukon.web.deviceGroups.deviceGroupPicker.deviceGroupNameSelectorTag.defaultNoGroupSelectedText"/>
</c:if>

<c:if test="${empty pageScope.noGroupSelectedAlertText}">
    <cti:msg2 var="noGroupSelectedAlertText" key="yukon.web.deviceGroups.deviceGroupPicker.deviceGroupNameSelectorTag.defaultNoGroupSelectedAlertText"/>
</c:if>

<c:if test="${empty pageScope.linkGroupName}">
    <c:set var="linkGroupName" value="false"/>
</c:if>

<c:if test="${empty pageScope.showSelectedDevicesIcon}">
    <c:set var="showSelectedDevicesIcon" value="true"/>
</c:if>

<cti:default var="fieldId" value="${fieldName}"/>

<script>

    $(document).on('click', '#viewSelectedDevices_${uniqueId}', function() {
        var url = yukon.url('/bulk/selectedDevicesTableForGroupName?groupName=' + encodeURIComponent($('#${fieldId}').val()));
        $('#show-selected-${uniqueId}').load(url, function() {
            $('#show-selected-${uniqueId}').dialog({width:450, height: 300});
        });
    });

    $(document).on('click', '.deviceGroupLink_${uniqueId}', function() {
        // ugly? but we can't sumbit a real form since the tag will most likely appear within a form already.
        // this should be safe though, it is the same way that a redirecting ext tree works (ha).
        var url = yukon.url('/group/editor/home?groupName=' + encodeURIComponent($('#${fieldId}').val()));
        window.location.href = url;
    });
    
    $(document).on('click', '.chooseGroupIcon_${uniqueId}', function() {
        
        var 
        dialog = $('#window_selectGroupTree_${uniqueId}'),
        content = dialog.find('.ui-dialog-content');
        
        // prevents double scrollbars on tree container
        dialog.css('overflow', 'hidden');
        dialog.dialog('open');
        
        // height (26px) plus margin (3px) of controls element
        $('#selectGroupTree_${uniqueId}').css('max-height', content.height() - 29);
    });
    
    window['setSelectedGroupName_${uniqueId}'] = function() {
        if ('${empty pageScope.fieldValue}' === 'true') {
            $('#noGroupSelectedText_${uniqueId}').hide();
        }
        $('#deviceGroupName_${uniqueId}').html($(document.getElementById("${fieldId}")).val());

        if ('${pageScope.showSelectedDevicesIcon}' === 'true') {
            $('#viewDevicesIconSpan_${uniqueId}').show();
        }
        
        if ('${pageScope.linkGroupName}' === 'true') {
            $('.deviceGroupLink_${uniqueId}').show();
        }
    }

</script>

<div class="dib wsnw ${pageScope.classes}"<c:if test="${!empty pageScope.id}"> id="${id}"</c:if>>
<%-- NO GROUP SELECTED TEXT --%>
<c:if test="${empty pageScope.fieldValue}">
    <a id="noGroupSelectedText_${uniqueId}" href="javascript:void(0);"
        class="fl chooseGroupIcon_${uniqueId}">
        <span class="noSelectionPickerLabel empty-list">${noGroupSelectedText}</span>
    </a>
    <c:set var="linkCssClass" value=" dn"/>
</c:if>
<c:choose>
    <%-- PLAIN GROUP NAME --%>
    <c:when test="${not pageScope.linkGroupName}">
        <a id="deviceGroupName_${uniqueId}" href="javascript:void(0);"
           title="${selectDeviceGroupChooseText}" 
           class="chooseGroupIcon_${uniqueId} fl">${pageScope.fieldValue}&nbsp;</a>
    </c:when>

    <%-- LINKED GROUP NAME --%>
    <c:otherwise>
        <a id="deviceGroupName_${uniqueId}" href="javascript:void(0);" 
            class="deviceGroupLink_${uniqueId} fl${linkCssClass}">${pageScope.fieldValue}&nbsp;</a>
    </c:otherwise>
</c:choose>
<%-- EDIT FOLDER --%>
<a href="javascript:void(0);" title="${selectDeviceGroupChooseText}"
    class="chooseGroupIcon_${uniqueId}"><i class="icon icon-folder-edit"></i></a>
<%-- MAGNIFIER ICON --%>
<c:if test="${pageScope.showSelectedDevicesIcon}">
    <cti:msg2 var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle"/>
    <cti:msg2 var="warning" key="yukon.common.device.bulk.selectedDevicesPopup.warning"/>
    <span id="viewDevicesIconSpan_${uniqueId}" <c:if test="${empty pageScope.fieldValue}">style="display:none;"</c:if>>
        <a id="viewSelectedDevices_${uniqueId}" href="javascript:void(0);" title="${popupTitle}" class="dib"><i class="icon icon-magnifier"></i></a>
        <div id="show-selected-${uniqueId}" title="${popupTitle}" class="dn"></div>
    </span>
</c:if>
</div>
<%-- PICKER TREE TAG --%>
<cti:msg2 var="cancelButtonText" key="yukon.web.deviceGroups.deviceGroupPicker.cancelButton"/>
<cti:msg2 var="selectButtonText" key="yukon.web.deviceGroups.deviceGroupPicker.selectButton"/>
<cti:msg2 var="pickerTitleText" key="yukon.web.deviceGroups.deviceGroupPicker.title"/>
<jsTree:nodeValueSelectingPopupTree fieldId="${fieldId}"
                                fieldName="${fieldName}"
                                fieldValue="${pageScope.fieldValue}"
                                nodeValueName="groupName"
                                submitButtonText="${selectButtonText}"
                                cancelButtonText="${cancelButtonText}"
                                submitCallback="setSelectedGroupName_${uniqueId}();${pageScope.submitCallback}();"
                                id="selectGroupTree_${uniqueId}"
                                dataJson="${dataJson}"
                                title="${pickerTitleText}"
                                noSelectionAlertText="${noGroupSelectedAlertText}"
                                includeControlBar="true"/>