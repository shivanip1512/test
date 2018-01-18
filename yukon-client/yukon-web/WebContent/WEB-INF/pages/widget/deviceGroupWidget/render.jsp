<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags/jsTree" %>

<script type="text/javascript">
    
    $(document).ready(function() {
        
        var dlg = document.getElementById('editGroupTreeDialog');
        if (!dlg) {
            $('body').append('<div id="editGroupTreeDialog"></div>');
        }

        var successMsg = '${successMsg}';
        if (successMsg != null && successMsg.length > 0 ) {
            $('.success').show();
            setTimeout(function() {
                $('.success').fadeOut('slow', function() {
                    $('.success').hide();
                });
            }, 5000);
         }
        $('#deviceGroupWidgetTree').on('yukon.ui.widget.DeviceGroupWidget.save', function() {
            var groupIds = $('#groupIds').val();
            
            var widget = $('#groupIds').closest('.widgetWrapper');
            var id = widget.attr('id');
            id = id.substring(id.indexOf("_") + 1);

            var widget = yukon.widgets[id];
            widget.setParameter('groupIds', groupIds);
            yukon.ui.block('#currentGroups');
            widget.doDirectActionRefresh('update');
        })
    });
    
</script>

<div id="currentGroups" class="js-block-this">

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
                                         displayCheckboxes="false"/>

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
                <cti:button nameKey="edit" icon="icon-pencil" id="showPopupButton" classes="M0"/>
            </div>
        </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>

</div>