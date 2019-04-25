yukon.namespace('yukon.tools.group.editor');

/**
 * Module that manages the Device Groups Editor page
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.tools.group.editor = (function () {

    'use strict';
    var _initialized = false,

    // js implementation of DeviceGroupUtil.isValidName(name).
    _isValidGroupName = function (name) {
        if(name == null || name.trim() == '' || (name.indexOf('/') != -1) || (name.indexOf('\\') != -1) || (name.length > 60)) {
            return false;
        }
        return true;
    },

    mod = {
            
        retrieveGroupDetails : function(groupName, selectNode) {
            //remove all current dialogs so it doesn't use old content
            $('.ui-dialog').remove();
            var groupNameEncoded = encodeURIComponent(groupName),
                showDevices = $('#showDevices').val(),
                url = yukon.url('/group/editor/selectedDeviceGroup?groupName=' + escape(groupName) + "&showDevices=" + showDevices),
                redirectUrl = yukon.url('/group/editor/home?groupName=' + groupNameEncoded);

            $.ajax({
                url: url
            }).done(function (data) {
                $('#subViewDiv').html(data);
                window.history.pushState({path:redirectUrl},'',redirectUrl);
                if (selectNode) {
                    var selectedKey = $('#selectedGroup').val();
                    //select and scroll to the group
                    $("#selectGroupTree").dynatree("getTree").selectKey(selectedKey);
                    $("#selectGroupTree").dynatree("getTree").getNodeByKey(selectedKey).activateSilently();
                    $("#selectGroupTree").dynatree("getTree").getNodeByKey(selectedKey).focus();
                }
            });
        },
            
        showDevices : function (groupName) {
            $("#showDevicesButton").attr("disabled", "disabled");
            $("#deviceMembers").load(yukon.url('/group/editor/getDevicesForGroup'), {'groupName': groupName});
        },
            
        removeAllDevices : function (groupName) {
            $("#removeAllDevicesButton").attr("disabled", "disabled");
            $("#deviceMembers").load(yukon.url('/group/editor/removeAllDevicesFromGroup'), {'groupName': groupName});
        },
        
        checkAndSubmitNewName : function (nameId, formId, buttonId) {
            var newName = $("#" + nameId).val();
            if(!_isValidGroupName(newName)) {
                $(".js-invalid-group-name").removeClass('dn');
                $("#" + nameId).focus();
                yukon.ui.unbusy('#' + buttonId);
            } else {
                $("#" + buttonId).attr("disabled", "disabled");
                $("#" + formId).submit();
            }
        },

        removeGroup : function(formName) {
            if (formName != null) {
                $("#" + formName).submit();
            }
        },
        
        submitMoveGroupForm : function() {
            $("#moveGroupForm").submit();
        },
        submitCopyContentsToGroupForm : function() {
            $("#copyContentsToGroupForm").submit();
        },

        init : function () {
            var hideOnGroupLoad = false;
            if (_initialized) return;
            
            $(document).on('click', '.js-edit-grp-name', function () {
                var grpName = $(this).data("group-name"),
                    grpFullName = $(this).data("group-full-name");
                $('#editPopupGroupName').val(grpFullName);
                $('#newGroupName').val(grpName);
                $("#editGroupNamePopup").dialog({width: 500});
            });
            
            $(document).on('click', '.js-add-sub-grp', function () {
                var grpName = $(this).data("group-name");
                $('#addPopupGroupName').val(grpName);
                $("#addSubGroupPopup").dialog({width: 500});
            });
            
            $(document).on('click', '.js-show-devices', function () {
                var grpName = $(this).data("group-name");
                yukon.tools.group.editor.showDevices(grpName);
            });
            
            $(document).on('click', '.js-edit-grp-name-save', function () {
                yukon.tools.group.editor.checkAndSubmitNewName('newGroupName', 'editGroupNameForm', 'editGroupNameSaveButton');
            });
            
            $(document).on('click', '.js-add-sub-grp-save', function () {
                yukon.tools.group.editor.checkAndSubmitNewName('childGroupName', 'addSubGroupForm', 'addSubGroupSaveButton');
            });
            
            $('#groupName').on('change', function () {
                if (hideOnGroupLoad == true) {
                    $('.js-error').addClass('dn');
                }
                var groupName = $('#groupName').val();
                yukon.tools.group.editor.retrieveGroupDetails(groupName, false);
                hideOnGroupLoad = true;
            });
            
            $(document).on('click', '#js-subgroups', function (event) {
                event.preventDefault();
                var groupName = $(this).data("groupName");
                yukon.tools.group.editor.retrieveGroupDetails(groupName, true);
            });
            
            $(document).on('yukon:devicegroup:removealldevices', function (event) {
                yukon.tools.group.editor.removeAllDevices($("#js-group-full-name").val())
            });
            
            $(document).on('yukon:devicegroup:removegroup', function (event) {
                yukon.tools.group.editor.removeGroup($(event.target).data('form-id'));
            });
            
            _initialized = true;

        }
    };

    return mod;
})();

$(function () { yukon.tools.group.editor.init(); });
