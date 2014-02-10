<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>

<script type="text/javascript">
    
    jQuery(document).ready(function() {
        
        var dlg = document.getElementById('editGroupTreeDialog');
        if (!dlg) {
            jQuery('body').append('<div id="editGroupTreeDialog"></div>');
        }

        var successMsg = '${successMsg}';
        if (successMsg != null && successMsg.length > 0 ) {
            jQuery('.success').show();
            setTimeout(function() {
                jQuery('.success').fadeOut('slow', function() {
                    jQuery('.success').hide();
                });
            }, 5000);
         }
        jQuery('#deviceGroupWidgetTree').on('yukon.ui.widget.DeviceGroupWidget.save', function() {
            var groupIds = jQuery('#groupIds').val();
            ${widgetParameters.jsWidget}.setParameter('groupIds', groupIds);
            yukon.ui.elementGlass.show('#currentGroups');
            ${widgetParameters.jsWidget}.doDirectActionRefresh('update');
        })
    });
    
</script>

<div id="currentGroups" class="f-block-this">

    <div class="success">${successMsg}</div>
                    
    <t:nodeValueSelectingInlineTree fieldId="groupName" 
                                         fieldName="groupName"
                                         nodeValueName="groupName" 
                                         fieldValue="${groupName}"
                                         multiSelect="true"
                                         id="selectGroupTree" 
                                         dataJson="${currentGroupsDataJson}"
                                         includeControlBar="true"
                                         highlightNodePath="${selectedNodePath}"
                                         displayCheckboxes="false"
                                         styleClass="static-mode"/>

    <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
        <cti:checkRolesAndProperties value="DEVICE_GROUP_MODIFY">
            <t:multiNodeValueSelectingPopupTree fieldId="groupIds"
                                             fieldName="groupIds"
                                             nodeValueName="groupId"
                                             id="deviceGroupWidgetTree"
                                             treeParameters="{checkbox: true}" 
                                             triggerElement="showPopupButton"
                                             dataJson="${allGroupsDataJson}"
                                             title="yukon.web.widgets.deviceGroupWidget"
                                             cancelButton="components.button.cancel.label"
                                             submitButton="components.button.save.label"
                                             submitEvent="yukon.ui.widget.DeviceGroupWidget.save"/>
            <div class="action-area">
                <cti:button nameKey="edit" icon="icon-pencil" type="button" id="showPopupButton" />
            </div>
        </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>

</div>