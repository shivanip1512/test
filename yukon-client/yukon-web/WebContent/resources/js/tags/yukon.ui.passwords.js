/**
 * Singleton that manages the password updating
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.ui.passwords');

yukon.ui.passwords = (function () {
    
    'use strict';
    
    var indicatePassFail = function (data) {
        
        $('.password-manager .icon-tick').removeClass('vh');
        
        var errors = ['policy_errors', 'rule_errors'],
            validations = ['policy_validations', 'rule_validations'],
            reqNames = ["PASSWORD_DOES_NOT_MEET_POLICY_QUALITY", "INVALID_PASSWORD_LENGTH", "PASSWORD_USED_TOO_RECENTLY"],
            i, j;
        for (i = 0; i < validations.length; i++) {
            if (data[validations[i]]) {
                for (j = 0; j < data[validations[i]].length; j++) {
                    console.debug("validations:" + data[errors[i]][j]);
                    if ($.inArray(data[validations[i]][j], reqNames) > -1) {
                        $('.' + data[validations[i]][j] + ' .icon').addClass('icon-tick');
                        $('.' + data[validations[i]][j] + ' .icon').removeClass('icon-cross');
                    }
                    else {
                        $('.' + data[validations[i]][j] + ' .icon')
                        .removeClass('vh');    
                    }
                    
                }
            }
        }
        
        for (i = 0; i < errors.length; i++) {
            if (data[errors[i]]) {
                for (j = 0; j < data[errors[i]].length; j++) {
                    console.debug("Errors:" + data[errors[i]][j]);
                    if ($.inArray(data[errors[i]][j], reqNames) > -1) {
                        $('.' + data[errors[i]][j] + ' .icon').removeClass('icon-tick');
                        $('.' + data[errors[i]][j] + ' .icon').addClass('icon-cross');
                    }
                    else {
                        $('.' + data[errors[i]][j] + ' .icon').addClass('vh');    
                    }    
                    
                    
                }
            }
        }
    };
   var _initialized = false;
    
   var mod = {
        
        init: function () {
            
            if (_initialized) return;
            
            var manager = $('.password-manager'),
            
            minLength = manager.data('minLength'),
            newPwSelector = manager.data('newPassword'),
            confirmPwSelector = manager.data('confirmPassword'),
            saveButtonSelector = manager.data('saveButton'),
            userId = manager.data('userId');
            
            $(newPwSelector).keyup(function (ev) {
                
                $(saveButtonSelector).prop('disabled', true);
                
                $.ajax({
                    url: yukon.url('/login/check-password'),
                    type: 'post',
                    data: {
                        userId: userId,
                        password: $(newPwSelector).val()
                    },
                    dataType: 'json'
                }).done(function (data) {
                    indicatePassFail(data);
                    enableDisableSave();
                    return false;
                }).fail(function (err) {
                    indicatePassFail($.parseJSON(err.responseText));
                    enableDisableSave();
                    return false;
                });
                
                return false;
            });
            
            $(confirmPwSelector).keyup(function (ev) {
                enableDisableSave();
            });
            
            function enableDisableSave () {
                    var confirm = $(confirmPwSelector).val(),
                    password = $(newPwSelector).val(),
                    meetsRequirements = $('.password-manager ul > li > .icon-cross');
                
                $('.js-password-mismatch').toggleClass('vh', confirm === password);
                if (meetsRequirements.length === 0 
                        && confirm === password 
                        && password.length >= minLength) {
                    $(saveButtonSelector).prop('disabled', false).removeClass('ui-button-disabled ui-state-disabled');
                } else {
                    $(saveButtonSelector).prop('disabled', true);
                }
            }

            _initialized = true;
            
            $(newPwSelector).trigger('keyup');
        },

    };
    
    return mod;
})();

$(function () { yukon.ui.passwords.init(); });