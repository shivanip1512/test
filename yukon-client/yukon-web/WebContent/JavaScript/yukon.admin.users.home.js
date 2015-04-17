yukon.namespace('yukon.admin.users.home');

/**
 * Module for the users and groups home page.
 * @module yukon.admin.users.home
 * @requires JQUERY
 * @requires yukon
 */
yukon.admin.users.home = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    _targets = {
        'userPicker': {id: 'userId', url: '/adminSetup/user/view?userId='},
        'userGroupPicker': {id: 'userGroupId', url: '/adminSetup/userGroup/view?userGroupId='},
        'roleGroupPicker': {id: 'groupId', url: '/adminSetup/roleGroup/view?roleGroupId='}
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            // Show pickers
            yukon.pickers['userPicker'].show();
            yukon.pickers['userGroupPicker'].show();
            yukon.pickers['roleGroupPicker'].show();
            // jquery ui tabs
            $('#tabs').tabs();
            
            $(document).on('yukon:admin:users-home:picker:complete', function (ev, items, picker) {
                var target = _targets[picker.pickerId];
                var id = items[0][target.id];
                var url = target.url;
                window.location.href = yukon.url(url + id);
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.admin.users.home.init(); });