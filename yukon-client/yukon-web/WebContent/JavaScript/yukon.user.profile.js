/**
 * Singleton that manages the user profile page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.userProfile');

yukon.userProfile = (function () {
    
    var 
    
    _initialized = false,
    
    _phone_types = ['CALL_BACK_PHONE', 'CELL_PHONE', 'FAX', 'HOME_PHONE', 'PHONE', 'WORK_PHONE'],
    
    _setEnumPreference = function (ev) {
        
        var btn = $(this),
            row = btn.closest('tr'),
            url = yukon.url('/user/updatePreference.json'),
            params = {
                userId: $('#user-id').val(),
                prefName: row.data('type'),
                prefValue: btn.data('value')
            };
        
        $.ajax({ type: 'post', url: url, data: params });
    },
    
    _submitChangePassword = function (ev) {
        
        $('#change-password-popup form').ajaxSubmit({
            success: function (result, status, xhr, $form) {
                $('#change-password-popup').dialog('close');
                window.location.reload();
            }
        });
    },
    
    _resetAllPreferences = function (ev) {
        
        var table = $('#preferences-table'),
            url = yukon.url('/user/updatePreferences/all/default.json'),
            params = { userId: $('#user-id').val() };
        
        $.ajax({ type: 'post', url: url, data: params })
        .done(function (json) {
            // Go through the data and turn on enums
            json.preferences.forEach(function (preference, index, fullarray) {
                var row = table.find('tr[data-type=' + preference.name + ']');
                row.find('.button').removeClass('on');
                row.find('.button-group [data-value='+ preference.defaultVal +']').addClass('on');
            });
        });
    },
    
    _restoreDefault = function (ev) {
        $(this).next().find('[data-value="' + $(this).data('value') + '"]').trigger('click');
    },
    
    _adjustFormatting = function (ev) {
        
        var select = $(this), type = select.val(),
        input = select.closest('tr').find('input.js-notif-value'),
        isPhone = _phone_types.indexOf(type) !== -1;
        
        input.removeClass('js-format-phone');
        if (isPhone) {
            input.addClass('js-format-phone');
            for (var i = 0; i < input.length; i++) {
                yukon.ui.formatPhone(input[i]);
            }
        }
    },
    
    _removePlaceholder = function (ev) {
        
        var select = $(this), value = select.find(':selected').val();
        
        if (value != '-1') {
            select.find('option[value="-1"]').remove();
            select.removeClass('js-has-select-one');
        }
    }
    
    _addNotification = function (ev) {
        
        var newRow = $('#contact-notif-template').clone().removeAttr('id'),
            select = newRow.find('select').addClass('js-has-select-one');
        
        $('#contact-notif-table tbody').append(newRow);
        
        yukon.ui.reindexInputs('#contact-notif-table');
        
        newRow.show();
        $('.js-no-other-notifs').hide();
        select.focus();
    },
    
    _removeNotification = function (ev) {
        $(this).closest('tr').remove();
        yukon.ui.reindexInputs('#contact-notif-table');
    },
    
    mod = {
            
        init: function () {
            
            if (_initialized) return;
            
            $(document).on('click', '#preferences-table .js-pref-options .button-group .button', _setEnumPreference);
            $(document).on('click', '#preferences-table .js-pref-options .js-pref-default', _restoreDefault);
            $(document).on('click', '#add-notif-btn', _addNotification);
            $(document).on('click', '#contact-notif-table td .js-remove', _removeNotification);
            $(document).on('change', '.js-has-select-one', _removePlaceholder);
            $(document).on('change', 'select.js-notif-type', _adjustFormatting);
            $(document).on('yukon:user:profile:pref:defaults', _resetAllPreferences);
            $(document).on('yukon:user:profile:password:save', _submitChangePassword);
            
            _initialized = true;
        }
    
    };
    
    return mod;
})();

$(function () { yukon.userProfile.init(); });