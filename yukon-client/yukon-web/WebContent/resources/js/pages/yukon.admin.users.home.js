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
     * This method saves the form passed as a parameter to this method. It calls the URL passed as a parameter to save 
     * the form.
     * @private
     * @param form {Object} The form to be saved.
     * @param dialogDiv {Object} The dialog that contains the form to be saved.
     * @param url {String} The url to be called to save the form.  
     */
    _saveForm = function (form, dialogDiv, url) {
        form.ajaxSubmit({
            url: yukon.url(url),
            type: 'post',
            success: function (result, status, xhr, $form) {
                dialogDiv.dialog('close');
                var resultId;
                if (result.hasOwnProperty('groupId')) {
                    resultId = result.groupId;
                } else {
                    resultId = result.userGroupId; 
                }
                window.location.href = yukon.url(url + "/" + resultId);
            },
            error: function (xhr, status, error, $form) {
                form.html(xhr.responseText);
            }
        });
    },
    
    /** 
     * @type {Object} _targets - A dictionary mapping picker type to object holding the identifier
     * property for the picker type and the url to navigate to when an item from the picker is chosen.
     */
    _targets = {
        'userPicker': { id: 'userId', url: '/admin/users/{id}' },
        'userGroupPicker': { id: 'userGroupId', url: '/admin/user-groups/{id}' },
        'roleGroupPicker': { id: 'groupId', url: '/admin/role-groups/{id}' }
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
                window.location.href = yukon.url(url.replace('{id}', id));
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
                        url: yukon.url('/admin/username/available'),
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
                    url: yukon.url('/admin/users'), 
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        $('.js-new-user-dialog').dialog('close');
                        window.location.href = yukon.url('/admin/users/' + result.userId);
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
            $('.js-new-user-group-dialog').on('yukon:admin:user:group:create', function () {
                _saveForm($('#new-user-group-form'), $('.js-new-user-group-dialog'), '/admin/user-groups');
            });
            
            $(document).on('submit', '#new-user-group-form', function (event) {
                event.preventDefault();
                _saveForm($('#new-user-group-form'), $('.js-new-user-group-dialog'), '/admin/user-groups');
            });
            /** 
             * OK button on the new role group popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and go to user page if validation succeded. 
             */
            $('.js-new-role-group-dialog').on('yukon:admin:role:group:create', function () {
                _saveForm($('#new-role-group-form'), $('.js-new-role-group-dialog'), '/admin/role-groups');
            });
            
            $(document).on('submit', '#new-role-group-form', function (event) {
                event.preventDefault();
                _saveForm($('#new-role-group-form'), $('.js-new-role-group-dialog'), '/admin/role-groups');
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.admin.users.home.init(); });