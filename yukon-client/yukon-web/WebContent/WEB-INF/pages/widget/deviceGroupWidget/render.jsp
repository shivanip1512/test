<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>

<script type="text/javascript">
    
    jQuery(document).ready(function() {
        
        var dlg = document.getElementById('multiNodeValueSelectingTreeDialog');
        if (!dlg) {
            jQuery('body').append('<div id="multiNodeValueSelectingTreeDialog"></div>');
        }

        jQuery('#showPopupButton').click(function() {
            var parameters = ${cti:jsonString(widgetParameters)};
            jQuery('#multiNodeValueSelectingTreeDialog').load('/widget/deviceGroupWidget/edit', parameters);
        });

        jQuery('#multiNodeValueSelectingTreeDialog').bind('dialogSubmit', function() {
            if (!setNodeValues_deviceGroupWidgetTree()) {
                return;
            }
            var groupIds = jQuery(document.getElementById('groupIds')).val();
            ${widgetParameters.jsWidget}.setParameter('groupIds', groupIds);
            ${widgetParameters.jsWidget}.doDirectActionContainerRefresh('update', 'currentGroups');
            jQuery('#multiNodeValueSelectingTreeDialog').dialog('close');
        });
        
        var successMsg = "${successMsg}";
        if (successMsg != null && successMsg.length > 0 ) {
            jQuery('.success').show();
            setTimeout(function() {
                jQuery('.success').fadeOut('slow', function() {
                    jQuery('.success').hide();
                });
            }, 5000);
         }
    });
    
</script>

<div id="currentGroups">

    <div class="success">
        <c:if test="${successMsg != null}"> ${successMsg} </c:if>
    </div>
                    
    <jsTree:nodeValueSelectingInlineTree fieldId="groupName" 
                                         fieldName="groupName"
                                         nodeValueName="groupName" 
                                         fieldValue="${groupName}"
                                         multiSelect="true"
                                         id="selectGroupTree" 
                                         dataJson="${currentGroupsDataJson}"
                                         includeControlBar="true"
                                         highlightNodePath="${selectedNodePath}"
                                         displayCheckboxes="true"
                                         styleClass="contained static-mode"/>

    <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
        <cti:checkProperty property="operator.DeviceActionsRole.DEVICE_GROUP_MODIFY">
            <div class="actionArea">
                <cti:button nameKey="edit" type="button" id="showPopupButton" />
            </div>
        </cti:checkProperty>
    </cti:checkRole>

</div>
