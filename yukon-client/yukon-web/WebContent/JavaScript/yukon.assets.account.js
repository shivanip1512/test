yukon.namespace('yukon.assets.account');

/**
 * Module for the account page.
 * @module yukon.assets.account
 * @requires JQUERY
 * @requires yukon
 */
yukon.assets.account = (function () {

    'use strict';
    
    var
    _initialized = false,
    
    _mode,
    
    _showPassword = function () {
        
        var checkbox = $('.js-show-password-checkbox'),
            type = checkbox.prop('checked') ? 'text' : 'password';
        
        $('.js-password-editor-field:password').each(function() {
            var input = $(this);
            if (type === 'text') {
                input.hide();
                input.siblings('input:text').show().val(input.val());
            } else {
                input.siblings('input:text').hide();
                //ugly, but works because we have a 1:1 mapping on the fields
                input.show().val(input.siblings('input:text').val());
            }
        });
    },
    
    _generatePassword = function () {
        
        var data = { userGroupName : $('#change-password-form .js-user-group').val() },
            userId = $('#change-password-form .js-user-id');
        
        if (0 !== userId.length && parseInt(userId.val(), 10) !== 0) {
            data['userId'] = userId.val();
        }
        
        new $.ajax({
            url: yukon.url('/stars/operator/account/generatePassword'),
            data: data
        }).done(function(data) {
            $(".js-password-editor-field").each(function() {
                this.value = data;
            });
            // Check and show the password fields
            $('.js-show-password-checkbox').attr('checked', true);
            _showPassword();
        });
    },
    
    _toggleBillingAddress = function () {
        if ($('.js-use-same-address').is(':checked')) {
            _saveBillingToTempFields();
            $('.js-billing-address-1').val($('.js-service-address-1').val());
            $('.js-billing-address-2').val($('.js-service-address-2').val());
            $('.js-billing-city').val($('.js-service-city').val());
            $('.js-billing-state').val($('.js-service-state').val());
            $('.js-billing-zip').val($('.js-service-zip').val());
            _setBillingFieldsDisabled(true);
        } else {
            $('.js-billing-address-1').val($('.js-temp-billing-address-1').val());
            $('.js-billing-address-2').val($('.js-temp-billing-address-2').val());
            $('.js-billing-city').val($('.js-temp-billing-city').val());
            $('.js-billing-state').val($('.js-temp-billing-state').val());
            $('.js-billing-zip').val($('.js-temp-billing-zip').val());
            _setBillingFieldsDisabled(false);
        }
    },
    
    _saveBillingToTempFields = function () {
        $('.js-temp-billing-address-1').val($('.js-billing-address-1').val());
        $('.js-temp-billing-address-2').val($('.js-billing-address-2').val());
        $('.js-temp-billing-city').val($('.js-billing-city').val());
        $('.js-temp-billing-state').val($('.js-billing-state').val());
        $('.js-temp-billing-zip').val($('.js-billing-zip').val());
    },
    
    _setBillingFieldsDisabled = function (disabled) {
        $('.js-billing').each(function() { this.disabled = disabled; });
    },
    
    _resetPasswordDialog = function (){
        var dialog = $("#change-password-popup");
        dialog.find("input:text, input:password").val("");
        dialog.removeMessages();
        dialog.find(".error").removeClass('error');
    },
    
    _updatePassword = function () {
        
        var popup = $('#change-password-popup');
        
        popup.removeMessages();
        prepPasswordFields();
        
        if ($('.js-password-1').val() !== $('.js-password-2').val()) {
            popup.addMessage({
                message: $('.js-password-mismatch').val(), 
                messageClass: 'error'
            });
            $('.js-password-1, .js-password-2').addClass('error');
        } else {
            $('#change-password-form').ajaxSubmit({
                dataType: 'json',
                success: function (data) {
                    popup.dialog('close');
                    yukon.ui.alertSuccess(data.flash);
                },
                error: function (xhr) {
                    var data = $.parseJSON(xhr.responseText);
                    data = data.fieldErrors;
                    $('.js-password-1, .js-password-2').addClass('error');
                    if (data.password1 === data.password2) {
                        delete data.password2;
                    }
                    popup.addMessage({ message: data, messageClass: 'error' });
                }
            });
        }
    },
    
    prepPasswordFields = function() {
        // If we are showing the plain-text fields, copy them over to the password fields.
        if ($('.js-show-password-checkbox:checked').length > 0) {
            $('.js-password-editor-field:password').each(function() {
                var input = $(this);
                // ugly, but works because we have a 1:1 mapping on the fields
                input.val(input.siblings('input:text').val());
            });
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _mode = $('.js-page-mode').val();
            
            // commercial setup
            // when in VIEW mode, don't show commercial values unless the account is commercial
            if (_mode !== 'VIEW') {
                var commercial = $('.js-is-commercial-acct').prop('checked'),
                    sameAsAbove = $('.js-use-same-address').prop('checked');
                
                $('.js-company-name').prop('disabled', !commercial);
                $('.js-company-type').prop('disabled', !commercial);
                
                // billing setup
                // if sameAsAbove is checked on page load, we know the address fields all match, set them to disabled
                _toggleBillingAddress();
                _saveBillingToTempFields();
                if (sameAsAbove) {
                    _setBillingFieldsDisabled(true);
                }
            }
            
            $(document).on('yukon:assets:account:password:save', _updatePassword);
            $(document).on('click', '.js-reset-password-dialog', _resetPasswordDialog);
            $(document).on('click', '.js-prep-password-fields', prepPasswordFields);
            $(document).on('click', '.js-use-same-address', _toggleBillingAddress);
            
            $('.js-show-password-checkbox').click(_showPassword);
            
            $(document).on('click', '.js-generatePassword', _generatePassword);
            
            /** User clicked commercial checkbox, enable/disable commercial fields. */
            $('.js-is-commercial-acct').click(function () {
                $('.js-company-name').prop('disabled', !$(this).prop('checked'));
                $('.js-company-type').prop('disabled', !$(this).prop('checked'));
            });
            
            _initialized = true;
        }
        
    };
    
    return mod;
})();

$(function () { yukon.assets.account.init(); });