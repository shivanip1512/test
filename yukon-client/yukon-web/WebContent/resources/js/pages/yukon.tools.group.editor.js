yukon.namespace('yukon.tools.group.editor');

/**
 * Module that manages the Device Groups Editor page
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.tools.group.editor = (function () {

    'use strict';
    var initialized = false;

    // js implementation of DeviceGroupUtil.isValidName(name).
    function isValidGroupName(name) {
        if(name == null || name.trim() == '' || (name.indexOf('/') != -1) || (name.indexOf('\\') != -1)) {
            return false;
        }
        return true;
    }
    
    function retrieveGroupDetails() {
        //remove all current dialogs so it doesn't use old content
        $('.ui-dialog').remove();
        $('.js-error').addClass('dn');
        var groupName = $('#groupName').val();
        var groupNameEncoded = encodeURIComponent(groupName);
        var url = yukon.url('/group/editor/selectedDeviceGroup?groupName=' + groupName);
        var redirectUrl = yukon.url('/group/editor/home?groupName=' + groupNameEncoded);
        
        $.ajax({
            url: url
        }).done(function (data) {
            $('#subViewDiv').html(data);   
            //make refresh of screen maintain selected device group
            window.history.pushState({path:redirectUrl},'',redirectUrl);
        });
    }

    var mod = {
            
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
            if(!isValidGroupName(newName)) {
                $(".js-invalid-group-name").removeClass('dn');
                $("#" + nameId).focus();
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
                        
            if (initialized) return;
            
            $(document).on('click', '.js-edit-grp-name', function () {
                $("#editGroupNamePopup").dialog({width: 500});
            });
            
            $(document).on('click', '.js-add-sub-grp', function () {
                $("#addSubGroupPopup").dialog({width: 500});
            });
            
            $('#groupName').on('change', function () {
                retrieveGroupDetails();
            });
            
            initialized = true;

        }
    };

    return mod;
}());

$(function () { yukon.tools.group.editor.init(); });
