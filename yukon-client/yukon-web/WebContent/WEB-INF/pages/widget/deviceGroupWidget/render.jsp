<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<c:set var="tree_id" value="deviceGroupWidgetTreePopup" />

<script type="text/javascript">
    
    // to submit widget form once the hidden groupIds field has been set
    function commitGroupIds() {
        ${widgetParameters.jsWidget}.doDirectActionContainerRefresh('add', 'currentGroups');
    }
    
</script>

<div id="currentGroups">
    <jsp:include page="/WEB-INF/pages/widget/deviceGroupWidget/currentGroups.jsp" />
</div>

<cti:checkRole role="operator.DeviceActionsRole.ROLEID">
<cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">

    <cti:msg2 key=".groupTree.title" var="addDeviceTitle"/>
    <cti:msg2 key=".groupTree.submitButtonText" var="submitText"/>
    <cti:msg2 key=".groupTree.closeButtonText" var="closeText"/>
    <cti:msg2 var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />

    <br>
    <input type="button" id="showPopupButton" value="${addDeviceTitle}" class="formSubmit"/>
    
    <ext:multiNodeValueSelectingPopupTree    fieldId="groupIds"
                                        fieldName="groupIds"
                                        nodeValueName="groupId"
                                        submitButtonText="${submitText}"
                                        cancelButtonText="${closeText}"
                                        submitCallback="commitGroupIds();"
                                        
                                        id="deviceGroupWidgetPopupTree"
                                        treeAttributes="{}"
                                        triggerElement="showPopupButton"
                                        dataJson="${groupDataJson}"
                                        title="${addDeviceTitle}"
                                        width="432"
                                        height="600"
                                        noSelectionAlertText="${noGroupSelectedAlertText}" />
                                        
</cti:checkProperty>
</cti:checkRole>
                                                    
