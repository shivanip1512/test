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

<cti:includeScript link="/JavaScript/showSelectedDevices.js"/>

<c:url var="folderEdit" value="/WebConfig/yukon/Icons/folder_edit.gif"/>
<c:url var="folderEditOver" value="/WebConfig/yukon/Icons/folder_edit_over.gif"/>
<c:url var="mag" value="/WebConfig/yukon/Icons/magnifier.gif"/>
<c:url var="magOver" value="/WebConfig/yukon/Icons/magnifier_zoom_in.gif"/>
<c:url var="magOverDisabled" value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>

<cti:msg var="defaultNoGroupSelectedText" key="yukon.web.deviceGroups.deviceGroupPicker.deviceGroupNameSelectorTag.defaultNoGroupSelectedText"/>
<cti:msg var="pickerTitleText" key="yukon.web.deviceGroups.deviceGroupPicker.title"/>
<cti:msg var="selectButtonText" key="yukon.web.deviceGroups.deviceGroupPicker.selectButton"/>
<cti:msg var="cancelButtonText" key="yukon.web.deviceGroups.deviceGroupPicker.cancelButton"/>
<cti:uniqueIdentifier var="uniqueId" prefix="deviceGroupNameSelectorTag_"/>

<c:if test="${empty pageScope.linkGroupName}">
	<c:set var="linkGroupName" value="false"/>
</c:if>

<c:if test="${empty pageScope.showSelectedDevicesIcon}">
	<c:set var="showSelectedDevicesIcon" value="true"/>
</c:if>


<script type="text/javascript">

	function setSelectedGroupName_${uniqueId}() {
		
		<c:if test="${empty pageScope.fieldValue}">
			$('noGroupSelectedTextSpan_${uniqueId}').hide();
		</c:if>
		$('deviceGroupNameSpan_${uniqueId}').innerHTML = $('${fieldName}').value;

		if (${pageScope.showSelectedDevicesIcon}) {
			$('viewDevicesIconSpan_${uniqueId}').show();
		}
		
		if (${pageScope.linkGroupName}) {
			$('deviceGroupLinkSpan_${uniqueId}').show();
		}
	}
	
	function viewDeviceGroup_${uniqueId}() {
		// ugly? but we can't sumbit a real form since the tag will most likely appear within a form already.
		// this should be safe though, it is the same way that a redirecting ext tree works.
		var url = '<cti:url value="/spring/group/editor/home"/>' + '?groupName=' + encodeURIComponent($('${fieldName}').value);
		
		window.location = url;
	}

	function viewDevices_${uniqueId}(imgEl) {

		var url = '<cti:url value="/spring/bulk/selectedDevicesTableForGroupName"/>' + '?groupName=' + encodeURIComponent($('${fieldName}').value);

		showSelectedDevices(imgEl, 'showSelectedDevices_${uniqueId}', 'showSelectedDevices_innerDiv_${uniqueId}', url, '${mag}', '${magOverDisabled}');
	}
	
</script>

<%-- NO GROUP SELECTED TEXT --%>
<c:if test="${empty pageScope.fieldValue}">
	<c:if test="${empty pageScope.noGroupSelectedText}">
		<c:set var="noGroupSelectedText" value="${defaultNoGroupSelectedText}"/>
	</c:if>
	<span id="noGroupSelectedTextSpan_${uniqueId}" class="subtleGray" style="font-style:italic;">${defaultNoGroupSelectedText}</span>
</c:if>


<c:choose>

	<%-- PLAIN GROUP NAME --%>
	<c:when test="${not pageScope.linkGroupName}">
		<span id="deviceGroupNameSpan_${uniqueId}">${pageScope.fieldValue}</span>&nbsp;
	</c:when>

	<%-- LINKED GROUP NAME --%>
	<c:otherwise>
	
		<span id="deviceGroupLinkSpan_${uniqueId}" <c:if test="${empty pageScope.fieldValue}">style="display:none;"</c:if>>
			<a href="javascript:void(0);" onclick="viewDeviceGroup_${uniqueId}();">
				<span id="deviceGroupNameSpan_${uniqueId}">${pageScope.fieldValue}</span>
			</a>
		</span>&nbsp;
	
	</c:otherwise>

</c:choose>

		
<%-- PICKER ICON --%>	
<a href="javascript:void(0);" title="${selectDeviceGroupChooseText}"  id="chooseGroupIcon_${uniqueId}" style="text-decoration:none;">	
<img src="${folderEdit}" onmouseover="javascript:this.src='${folderEditOver}'" onmouseout="javascript:this.src='${folderEdit}'">&nbsp;
</a>	

<%-- MAGNIFIER ICON --%>
<c:if test="${pageScope.showSelectedDevicesIcon}">
<cti:msg var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle" />
<cti:msg var="warning" key="yukon.common.device.bulk.selectedDevicesPopup.warning" />
		
<span id="viewDevicesIconSpan_${uniqueId}" <c:if test="${empty pageScope.fieldValue}">style="display:none;"</c:if>>
<a href="javascript:void(0);" title="${popupTitle}"  onclick="viewDevices_${uniqueId}($('mag_${uniqueId}'));" >
	<img id="mag_${uniqueId}" src="${mag}" onmouseover="javascript:this.src='${magOver}'" onmouseout="javascript:this.src='${mag}'">
</a>&nbsp;
<tags:simplePopup id="showSelectedDevices_${uniqueId}" title="${popupTitle}">
    <div style="height:300px;overflow:auto;">
    <div class="smallBoldLabel" id="showSelectedDevices_innerDiv_${uniqueId}" style="text-align:left;"></div>
    </div>
</tags:simplePopup>
</span>
</c:if>

<%-- PICKER TREE TAG --%>	
<jsTree:nodeValueSelectingPopupTree fieldId="${fieldName}"
                                fieldName="${fieldName}"
                                fieldValue="${pageScope.fieldValue}"
                                nodeValueName="groupName"
                                submitButtonText="${selectButtonText}"
                                cancelButtonText="${cancelButtonText}"
                                submitCallback="setSelectedGroupName_${uniqueId}();${pageScope.submitCallback}"
                                
                                id="selectGroupTree_${uniqueId}"
                                triggerElement="chooseGroupIcon_${uniqueId}"
                                dataJson="${dataJson}"
                                title="${pickerTitleText}"
                                width="432"
                                height="600"
                                includeControlBar="true" />