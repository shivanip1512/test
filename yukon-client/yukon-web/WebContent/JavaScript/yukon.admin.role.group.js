yukon.namespace('yukon.admin.role.group');

/**
 * Module for the role group page.
 * @module yukon.admin.role.group
 * @requires JQUERY
 * @requires yukon
 */
yukon.admin.role.group = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** New user groups chosen to add to the role group. */
            $(document).on('yukon:admin:role:group:add:user:groups', function (ev, items, picker) {
                
                var groups = items.map(function (item) { return item.userGroupId; });
                var groupId = $('#role-group-id').val();
                
                $.ajax({
                    url: yukon.url('/adminSetup/role-groups/' + groupId + '/add-user-groups'),
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

$(function () { yukon.admin.role.group.init(); });