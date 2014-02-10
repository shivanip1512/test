<%@ tag body-content="empty" description="Device group name picker" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="dataJson" required="true" %>
<%@ attribute name="classes" %>
<%@ attribute name="fieldId" description="If not supplied, the fieldName will be used" %>
<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="fieldValue" %>
<%@ attribute name="linkGroupName" description="Will make the group name a link that when clicked brings you to the group editor for group" %>
<%@ attribute name="noGroupSelectedText" %>
<%@ attribute name="showSelectedDevicesIcon" %>
<%@ attribute name="submitCallback" description="optional additional function to call when group is picked" %>

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

<cti:default var="fieldId" value="${fieldName}"/>

<script>
    // ignores if present, creates if not
    yukon.namespace('yukon.ui.dialogs');
    if ('undefined' === typeof yukon.ui.dialogs.${uniqueId}) {
        yukon.namespace('yukon.ui.dialogs.${uniqueId}');
    }

    jQuery(document).on('click', '#viewSelectedDevices_${uniqueId}', function() {
        var url = '<cti:url value="/bulk/selectedDevicesTableForGroupName"/>' + '?groupName=' + encodeURIComponent(jQuery(document.getElementById("${fieldId}")).val());
        showSelectedDevices('#viewSelectedDevices_${uniqueId}', 'showSelectedDevices_${uniqueId}', url);
    });

    jQuery(document).on('click', '.deviceGroupLink_${uniqueId}', function() {
        // ugly? but we can't sumbit a real form since the tag will most likely appear within a form already.
        // this should be safe though, it is the same way that a redirecting ext tree works (ha).
        var url = '<cti:url value="/group/editor/home"/>' + '?groupName=' + encodeURIComponent(jQuery(document.getElementById("${fieldId}")).val());
        window.location.href = url;
    });
    
    jQuery(document).on('click', '.chooseGroupIcon_${uniqueId}', function() {
        var uniqueId = '${uniqueId}',
            devGroupTree,
            groupTreeObj,
            titleBarObj,
            dialogCont,
            dialogHeight,
            offsetTop,
            buttonPaneObj,
            treeHelperPane,
            dialogContOffset,
            maxHeight,
            TREE_DIALOG_MAX_WIDTH = 450,
            // TODO: coalesce this function and put in a separate JavaScript file that can be shared
            // between this tag file and popupTree.tag
            calcMaxHeight = function (dialogCont, titleBar, treeHelper, buttonPane) {
                var paneHeights = titleBar.height() + treeHelper.height() + buttonPane.height(),
                    maxHeight;
                // 8-pixel slop addresses top and bottom padding on the elements comprising the popup
                maxHeight = dialogCont.height() - paneHeights - 8;
                return maxHeight;
            };

        if ('undefined' === typeof yukon.ui.dialogs.${uniqueId}.init) {
            yukon.ui.dialogs.${uniqueId}.init = 'inited';
        } else {
            // for some reason, this is called multiple times
            if (true === jQuery('#window_selectGroupTree_${uniqueId}').dialog('isOpen')) {
                return;
            }
        }
        jQuery('#window_selectGroupTree_${uniqueId}').dialog('open');
        if (-1 !== uniqueId.indexOf('deviceGroupNameSelectorTag')) {
            devGroupTree = document.getElementById('window_selectGroupTree_${uniqueId}');
            groupTreeObj = jQuery(devGroupTree);
            titleBarObj = groupTreeObj.prev('.ui-dialog-titlebar');
            dialogCont = groupTreeObj.closest('.ui-dialog');
            offsetTop = dialogCont.offset().top;
            treeHelperPane = groupTreeObj.find('.tree_helper_controls');
            // Store the first value of the top offset of the dialog. Subsequent values vary
            // considerably and undermine the positioning logic
            if ('undefined' === typeof yukon.ui.dialogs.${uniqueId}.offsetTop) {
                yukon.ui.dialogs.${uniqueId}.offsetTop = offsetTop;
            } else {
                offsetTop = yukon.ui.dialogs.${uniqueId}.offsetTop;
            }
            dialogHeight = window.windowHeight - offsetTop;
            // height set on dialog. when tree expanded
            groupTreeObj.dialog('option', 'height', dialogHeight + titleBarObj.height());
            // make dialog wider so long entries don't force horizontal scrollbars
            groupTreeObj.dialog('option', 'width', TREE_DIALOG_MAX_WIDTH);
            // no scrollbars on tree div, prevents double scrollbars
            groupTreeObj.css('overflow', 'hidden');
            dialogContOffset = dialogCont.offset();
            buttonPaneObj = jQuery(groupTreeObj.nextAll('.ui-dialog-buttonpane')[0]);
            // position dialog
            dialogCont.offset({'left' : dialogContOffset.left, 'top' : offsetTop - buttonPaneObj.height()});
            // size tree height so it is fully scrollable
            maxHeight = calcMaxHeight(dialogCont, titleBarObj, treeHelperPane, buttonPaneObj);
            // set max height of tree dialog so the scrolling works through all the entries
            jQuery('#selectGroupTree_${uniqueId}').css('max-height', maxHeight);
        }
    });
    
	function setSelectedGroupName_${uniqueId}() {
		<c:if test="${empty pageScope.fieldValue}">
			jQuery('#noGroupSelectedText_${uniqueId}').hide();
		</c:if>
		jQuery('#deviceGroupName_${uniqueId}').html(jQuery(document.getElementById("${fieldId}")).val());

		if (${pageScope.showSelectedDevicesIcon}) {
			jQuery('#viewDevicesIconSpan_${uniqueId}').show();
		}
		
		if (${pageScope.linkGroupName}) {
			jQuery('.deviceGroupLink_${uniqueId}').show();
		}
	}

</script>
<div class="dib wsnw ${pageScope.classes}">

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
	class="chooseGroupIcon_${uniqueId}"><i class="icon icon-folder-edit"></i></a>

<%-- MAGNIFIER ICON --%>
<c:if test="${pageScope.showSelectedDevicesIcon}">
	<cti:msg var="popupTitle" key="yukon.common.device.bulk.selectedDevicesPopup.popupTitle" />
	<cti:msg var="warning" key="yukon.common.device.bulk.selectedDevicesPopup.warning" />
	<span id="viewDevicesIconSpan_${uniqueId}" <c:if test="${empty pageScope.fieldValue}">style="display:none;"</c:if>>
		<a id="viewSelectedDevices_${uniqueId}" href="javascript:void(0);" title="${popupTitle}" class="dib"><i class="icon icon-magnifier"></i></a>
		<div id="showSelectedDevices_${uniqueId}" title="${popupTitle}" class="dn"></div>
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
                                submitCallback="setSelectedGroupName_${uniqueId}();${pageScope.submitCallback}"
                                id="selectGroupTree_${uniqueId}"
                                dataJson="${dataJson}"
                                title="${pickerTitleText}"
                                noSelectionAlertText="${noGroupSelectedText}"
                                includeControlBar="true" />
