yukon.namespace('yukon.admin.user.group');

/**
 * Module for the user group page.
 * @module yukon.admin.user.group
 * @requires JQUERY
 * @requires yukon
 */
yukon.admin.user.group = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** New users to add to the group was chosen, add it. */
            $(document).on('yukon:admin:user:group:add:user', function (ev, items, picker) {
                
                var users = items.map(function (item) { return item.userId; });
                var groupId = $('#user-group-id').val();
                
                $.ajax({
                    url: yukon.url('/adminSetup/user-groups/' + groupId + '/add-users'),
                    type: 'post',
                    data: {
                        users: users
                    }
                }).done(function () {
                    window.location.reload();
                });
            });
            
            /** New role groups to add to the group was chosen, add it. */
            $(document).on('yukon:admin:user:group:add:role:group', function (ev, items, picker) {
                
                var groups = items.map(function (item) { return item.groupId; });
                var groupId = $('#user-group-id').val();
                
                $.ajax({
                    url: yukon.url('/adminSetup/user-groups/' + groupId + '/add-role-groups'),
                    type: 'post',
                    data: {
                        groups: groups
                    }
                }).always(function () {
                    window.location.reload();
                });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.admin.user.group.init(); });