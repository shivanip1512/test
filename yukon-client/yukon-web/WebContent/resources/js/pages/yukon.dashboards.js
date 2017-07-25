yukon.namespace('yukon.dashboards');

/**
 * Module for the dashboard pages.
 * @module yukon.dashboards
 * @requires JQUERY
 * @requires yukon
 */
yukon.dashboards = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
            
        saveFormDetails : function (form) {
            form.ajaxSubmit({
                success: function (result, status, xhr, $form) {
                    var dashboardId = result.dashboardId;
                    window.location.href = yukon.url('/dashboards/' + dashboardId + '/edit');
                },
                error: function (xhr, status, error, $form) {
                    form.html(xhr.responseText);
                }
            });
        },
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** 'Save' button clicked on the dashboard details popup. */
            $(document).on('yukon:dashboard:details:save', function (ev) {
                var dialog = $(ev.target),
                    form = dialog.find('#dashboard-details');
                mod.saveFormDetails(form);
            });
            
            $(document).on('yukon:dashboard:remove', function (ev) {
                var container = $(ev.target),
                    dashboardId = container.data('dashboardId');
                $.ajax({
                    url: yukon.url('/dashboards/' + dashboardId + '/delete'),
                    type: 'post'
                }).done(function () {
                    window.location.reload();
                });
            });
            
            /** Change Owner  */
            $(document).on('yukon:dashboard:changeOwner', function (ev, items, picker) {
                var tableData = $(this).closest('td'),
                    pickerIdParts = picker.pickerId.split('_'),
                    dashboardId = pickerIdParts[1],
                    userId = items[0].userId;
                window.location.href = yukon.url('/dashboards/' + dashboardId + '/changeOwner/' + userId);
            });
            
            /** Assign Users  */
            $(document).on('yukon:dashboard:assignUsers', function (ev) {
                var dialog = $(ev.target),
                    pickerId = dialog.find('#pickerId').val(),
                    dashboardId = dialog.find('#dashboardId').val(),
                    dashboardType = dialog.find('#dashboardType').val(),
                    selectedUsers = yukon.pickers['dashboardUsersPicker_' + pickerId].selectedItems,
                    users = selectedUsers.map(function (item) { return item.userId; });
                
                var data = {
                    pageType: dashboardType,
                    users: users
                };
                
                $.ajax({
                    url: yukon.url('/dashboards/' + dashboardId + '/assignUsers'),
                    type: 'post',
                    data: data
                }).done(function () {
                    window.location.reload();
                });
            });
            
            /** Unassign Users  */
            $(document).on('yukon:dashboard:unassignUsers', function (ev, items, picker) {
                var users = items.map(function (item) { return item.userId; }),
                    dashboardId = picker.extraArgs,
                    data = {
                        users: users
                    };
                
                $.ajax({
                    url: yukon.url('/dashboards/' + dashboardId + '/unassignUsers'),
                    type: 'post',
                    data: data
                }).done(function () {
                    window.location.reload();
                });
            });
            
            $(document).on('click', '.js-show-user-picker', function () {
                var dashboardId = $(this).data('dashboardId');
                yukon.pickers['userPicker_' + dashboardId].show();
            });

            $(document).on('submit', '#dashboard-details', function (ev) {
                var form = $(ev.target);
                yukon.dashboards.saveFormDetails(form);
                return false;
            });
            
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.dashboards.init(); });