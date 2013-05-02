<%@ attribute name="noGroupSelectedText" required="false" type="java.lang.String"%>
<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="fieldValue" required="false" type="java.lang.String"%>
<%@ attribute name="dataJson" required="true" type="java.lang.String"%>
<%@ attribute name="linkGroupName" required="false" type="java.lang.String"%> <%-- will make the group name a link that when clicked bring you to group editor for group --%>
<%@ attribute name="showSelectedDevicesIcon" required="false" type="java.lang.String"%> <%-- will make the group name a link that when clicked bring you to group editor for group --%>
<%@ attribute name="submitCallback" required="false" type="java.lang.String"%> <%-- optional additional function to call when group is picked --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%@ tag body-content="empty" description="Device group name picker" %>

<cti:includeScript link="/JavaScript/showSelectedDevices.js"/>

<cti:uniqueIdentifier var="uniqueId" prefix="deviceGroupNameSelectorTag_"/>

<c:if test="${empty pageScope.noGroupSelectedText}">
    <cti:msg2 var="noGroupSelectedText" key="yukon.web.deviceGroups.deviceGroupPicker.deviceGroupNameSelectorTag.defaultNoGroupSelectedText"/>
</c:if>

<c:if test="${empty pageScope.linkGroupName}">
	<c:set var="linkGroupName" value="false"/>
</c:if>

<c:if test="${empty pageScope.showSelectedDevicesIcon}">
	<c:set var="showSelectedDevicesIcon" value="true"/>
</c:if>

<script>
    jQuery(document).on('click', '#viewSelectedDevices_${uniqueId}', function() {
        var url = '<cti:url value="/bulk/selectedDevicesTableForGroupName"/>' + '?groupName=' + encodeURIComponent($('${fieldName}').value);
        showSelectedDevices('#viewSelectedDevices_${uniqueId}', 'showSelectedDevices_${uniqueId}', url);
    });
    
    jQuery(document).on('click', '.deviceGroupLink_${uniqueId}', function() {
        // ugly? but we can't sumbit a real form since the tag will most likely appear within a form already.
        // this should be safe though, it is the same way that a redirecting ext tree works (ha).
        var url = '<cti:url value="/group/editor/home"/>' + '?groupName=' + encodeURIComponent($('${fieldName}').value);
        window.location.href = url;
    });
    
    jQuery(document).on('click', '.chooseGroupIcon_${uniqueId}', function() {
        jQuery("#window_selectGroupTree_${uniqueId}").dialog('open');
    });
    
	function setSelectedGroupName_${uniqueId}() {
		
		<c:if test="${empty pageScope.fieldValue}">
			jQuery('#noGroupSelectedText_${uniqueId}').hide();
		</c:if>
		jQuery('#deviceGroupName_${uniqueId}').html(jQuery(document.getElementById("${fieldName}")).val());

		if (${pageScope.showSelectedDevicesIcon}) {
			jQuery('#viewDevicesIconSpan_${uniqueId}').show();
		}
		
		if (${pageScope.linkGroupName}) {
			jQuery('.deviceGroupLink_${uniqueId}').show();
		}
	}

</script>

<%-- NO GROUP SELECTED TEXT --%>
<c:if test="${empty pageScope.fieldValue}">
    <a id="noGroupSelectedText_${uniqueId}" href="javascript:void(0);"
        class="fl simpleLink leftOfImageLabel chooseGroupIcon_${uniqueId}">
        <span class="noSelectionPickerLabel empty-list">${noGroupSelectedText}</span>
    </a>
    <c:set var="linkCssClass" value=" dn"/>
</c:if>

<c:choose>
	<%-- PLAIN GROUP NAME --%>
	<c:when test="${not pageScope.linkGroupName}">
        <a id="deviceGroupName_${uniqueId}" href="javascript:void(0);"
           title="${selectDeviceGroupChooseText}" 
           class="chooseGroupIcon_${uniqueId} fl simpleLink leftOfImageLabel">${pageScope.fieldValue}&nbsp;</a>
	</c:when>

	<%-- LINKED GROUP NAME --%>
	<c:otherwise>
		<a id="deviceGroupName_${uniqueId}" href="javascript:void(0);" 
            class="deviceGroupLink_${uniqueId} fl leftOfImageLabel${linkCssClass}">${pageScope.fieldValue}&nbsp;</a>
	</c:otherwise>
</c:choose>
<%-- EDIT FOLDER --%>
<a href="javascript:void(0);" title="${selectDeviceGroupChooseText}"
	class="chooseGroupIcon_${uniqueId} icon icon_folder_edit"></a>

<%-- MAGNIFIER ICON --%>
<c:if test="${pageScope.showSelectedDevicesIcon}">
	<cti:msg var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle" />
	<cti:msg var="warning" key="yukon.common.device.bulk.selectedDevicesPopup.warning" />
	<span id="viewDevicesIconSpan_${uniqueId}" <c:if test="${empty pageScope.fieldValue}">style="display:none;"</c:if>>
		<a id="viewSelectedDevices_${uniqueId}" href="javascript:void(0);" title="${popupTitle}" class="icon magnifier"></a>
		<div id="showSelectedDevices_${uniqueId}" title="${popupTitle}" class="dn"></div>
	</span>
</c:if>

<%-- PICKER TREE TAG --%>
<cti:msg2 var="cancelButtonText" key="yukon.web.deviceGroups.deviceGroupPicker.cancelButton"/>
<cti:msg2 var="selectButtonText" key="yukon.web.deviceGroups.deviceGroupPicker.selectButton"/>
<cti:msg2 var="pickerTitleText" key="yukon.web.deviceGroups.deviceGroupPicker.title"/>
<jsTree:nodeValueSelectingPopupTree fieldId="${fieldName}"
                                fieldName="${fieldName}"
                                fieldValue="${pageScope.fieldValue}"
                                nodeValueName="groupName"
                                submitButtonText="${selectButtonText}"
                                cancelButtonText="${cancelButtonText}"
                                submitCallback="setSelectedGroupName_${uniqueId}();${pageScope.submitCallback}"
                                id="selectGroupTree_${uniqueId}"
                                dataJson="${dataJson}"
                                title="${pickerTitleText}"
                                noSelectionAlertText="${noGroupSelectedText}"
                                width="432"
                                height="400"
                                includeControlBar="true" />