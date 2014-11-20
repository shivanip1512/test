/**
 * Singleton that manages the password updating
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.ui.passwords');

yukon.ui.passwords = (function () {
    
    'use strict';
    
    var 
    _initialized = false,
    _minLength, _newPwSelector, _confirmPwSelector, _saveButtonSelector, _userId,
    
    mod = {
        
        init: function () {
            
            if (_initialized) return;
            
            var manager = $('.password-manager');
            
            _minLength = manager.data('minLength'),
            _newPwSelector = manager.data('newPassword'),
            _confirmPwSelector = manager.data('confirmPassword'),
            _saveButtonSelector = manager.data('saveButton'),
            _userId = manager.data('userId');
            
            $(_newPwSelector).keyup(function (ev) {
                
                $(_saveButtonSelector).prop('disabled', true);
                
                $.ajax({
                    url: yukon.url('/login/check-password'),
                    type: 'post',
                    data: {
                        userId: _userId,
                        password: $(_newPwSelector).val()
                    },
                    dataType: 'json'
                }).done(function (data) {
                    mod.indicatePassFail(data);
                    return false;
                }).fail(function (err) {
                    mod.indicatePassFail($.parseJSON(err.responseText));
                    return false;
                });
                
                return false;
            });
            
            $(_newPwSelector + ', ' + _confirmPwSelector).keyup(function (ev) {
                
                var confirm = $(_confirmPwSelector).val(),
                    password = $(_newPwSelector).val(),
                    meetsRequirements = $('.password-manager ul:first > li > .icon-cross');
                
                $('.js-password-mismatch').toggleClass('vh', confirm === password);
                if (meetsRequirements.length == 0 
                        && confirm === password 
                        && password.length >= _minLength) {
                    $(_saveButtonSelector).prop('disabled', false);
                } else {
                    $(_saveButtonSelector).prop('disabled', true);
                }
            });
            
            _initialized = true;
        },
        
        indicatePassFail: function (data) {
            
            $('.password-manager .icon-tick').removeClass('vh');
            
            var errors = ['policy_errors', 'rule_errors'],
                validations = ['policy_validations', 'rule_validations'],
                i, j;
            
            for (i = 0; i < validations.length; i++) {
                if (data[validations[i]]) {
                    for (j = 0; j < data[validations[i]].length; j++) {
                        $('.' + data[validations[i]][j] + ' .icon')
                        .removeClass('vh');
                    }
                }
            }
            
            for (i = 0; i < errors.length; i++) {
                if (data[errors[i]]) {
                    for (j = 0; j < data[errors[i]].length; j++) {
                        $('.' + data[errors[i]][j] + ' .icon').addClass('vh');
                    }
                }
            }
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ui.passwords.init(); });