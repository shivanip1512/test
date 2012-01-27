<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

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

    <div class="actionArea">
        <cti:button nameKey="groupTree" type="button" id="showPopupButton"/>
    </div>
                                          
    <jsTree:multiNodeValueSelectingPopupTree fieldId="groupIds"
                                          fieldName="groupIds"
                                          nodeValueName="groupId"
                                          submitButton=".groupTree.submitButtonText"
                                          cancelButton=".groupTree.closeButtonText"
                                          submitCallback="commitGroupIds();"
                                          id="deviceGroupWidgetPopupTree"
                                          triggerElement="showPopupButton"
                                          dataJson="${groupDataJson}"
                                          title=".groupTree.label"
                                          width="432"
                                          height="600"
                                          noSelectionAlert="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />                                      
</cti:checkProperty>
</cti:checkRole>
                                                    
