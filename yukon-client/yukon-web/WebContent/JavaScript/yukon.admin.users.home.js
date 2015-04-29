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
    
    /** 
     * @type {Object} _password_types - A hash of AuthenticationCategory to true/false whether the 
     * category uses passwords. 
     */
    _password_types = {},
    
    /** 
     * @type {Object} _targets - A dictionary mapping picker type to object holding the identifier
     * property for the picker type and the url to navigate to when an item from the picker is chosen.
     */
    _targets = {
        'userPicker': {id: 'userId', url: '/adminSetup/user/?/view'},
        'userGroupPicker': {id: 'userGroupId', url: '/adminSetup/user-groups/?/view'},
        'roleGroupPicker': {id: 'groupId', url: '/adminSetup/role-groups/?/view'}
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _password_types = yukon.fromJson('#password-types');
            
            // Show pickers
            yukon.pickers['userPicker'].show();
            yukon.pickers['userGroupPicker'].show();
            yukon.pickers['roleGroupPicker'].show();
            // jquery ui tabs
            $('#tabs').tabs();
            
            /** Navigate to user, user group or role group page when chosen. */
            $(document).on('yukon:admin:users-home:picker:complete', function (ev, items, picker) {
                var target = _targets[picker.pickerId];
                var id = items[0][target.id];
                var url = target.url;
                window.location.href = yukon.url(url.replace('?', id));
            });
            
            /** Show/hide password fields when auth category changes. */
            $(document).on('change', '.js-auth-category', function (ev) {
                var category = $(this).val();
                $('.js-pw-row').toggle(_password_types[category]);
            });
            
            /** Check if username is available. */
            $(document).on('input', '.js-username', function (ev) {
                
                var username = $(this).val();
                var indicator = $('.js-username-available');
                
                if (username.trim() === '') {
                    indicator.hide();
                } else {
                    $.ajax({
                        url: yukon.url('/adminSetup/username/available'),
                        data: { q: username }
                    }).done(function (result) {
                        indicator.show()
                        .toggleClass('error', !result.available)
                        .toggleClass('success', result.available);
                        if (result.available) {
                            indicator.text(indicator.data('available'));
                        } else {
                            indicator.text(indicator.data('unavailable'));
                        }
                    });
                }
            });
            
            /** 
             * OK button on the new user popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and go to user page if validation succeded. 
             */
            $('.js-new-user-dialog').on('yukon:admin:user:create', function (ev) {
                
                $('#new-user-form').ajaxSubmit({
                    url: yukon.url('/adminSetup/user'), 
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        $('.js-new-user-dialog').dialog('close');
                        window.location.href = yukon.url('/adminSetup/user/' + result.userId + '/view');
                    },
                    error: function (xhr, status, error, $form) {
                        $('#new-user-form').html(xhr.responseText);
                    }
                });
            });
            
            /** 
             * OK button on the new user group popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and go to user page if validation succeded. 
             */
            $('.js-new-user-group-dialog').on('yukon:admin:user:group:create', function (ev) {
                
                $('#new-user-group-form').ajaxSubmit({
                    url: yukon.url('/adminSetup/user-groups'),
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        $('.js-new-user-group-dialog').dialog('close');
                        window.location.href = yukon.url('/adminSetup/user-groups/' + result.userGroupId + '/view');
                    },
                    error: function (xhr, status, error, $form) {
                        $('#new-user-group-form').html(xhr.responseText);
                    }
                });
            });
            
            /** 
             * OK button on the new role group popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and go to user page if validation succeded. 
             */
            $('.js-new-role-group-dialog').on('yukon:admin:role:group:create', function (ev) {
                
                $('#new-role-group-form').ajaxSubmit({
                    url: yukon.url('/adminSetup/role-group'),
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        $('.js-new-role-group-dialog').dialog('close');
                        window.location.href = yukon.url('/adminSetup/role-groups/view?roleGroupId=' + result.groupId);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#new-role-group-form').html(xhr.responseText);
                    }
                });
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.admin.users.home.init(); });