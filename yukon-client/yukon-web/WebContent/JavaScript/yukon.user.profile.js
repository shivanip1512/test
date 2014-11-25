/**
 * Singleton that manages the user profile page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.userProfile');

yukon.userProfile = (function () {
    
    /* PRIVATE VARIABLES AND METHODS */
    var 
    mod,
    _initialized = false,
    _controlNamePrefix = 'contact.otherNotifications',
    _urlChangePreference = yukon.url('/user/updatePreference.json'),
    _urlChangeAllPreferences = yukon.url('/user/updatePreferences/all/default.json'),
    _urlUpdatePassword = yukon.url('/user/updatePassword.json'),
    
    _getPrefButtonWithSameOption = function(btn, ev) {
        return btn.closest('td')
            .find('button:not(.js-pref-default)[data-value='+ $(ev.currentTarget).attr('data-value') +']');
    },
    
    _setEnumPreference = function(ev, btn) {
        
        var option = $(ev.currentTarget);
        var row = option.closest('tr');
        var prefName = row.attr('data-type');
        var prefVal = option.attr('data-value');
        var userId = $('input[name="userId"]').val();
        
        $.ajax({
            type: 'POST',
            url: _urlChangePreference,
            data: {'userId': userId, 'prefName': prefName, 'prefValue': prefVal}
        }).done(function(data) {
            option.closest('td').find('button').removeClass('on');
            btn.addClass('on');
        });
    },
    
    _setStringPreference = function(saveButton) {
        var input = saveButton.closest('td').find('input');
        var prefVal = input.attr('value');
        // Pref's type is stored on the TR
        var prefName = input.closest('tr').attr('data-type');
        var userId = $('input[name="userId"]').val();
        
        // Disable the preference's controls + change the icon
        input.prop('disabled', true);
        saveButton.prop('disabled', true);
        var btnIcon = saveButton.find('i');
        btnIcon.removeClass('icon-disk').addClass('icon-spinner');
        
        $.ajax({
            type: 'POST',
            url: _urlChangePreference,
            data: {'userId': userId, 'prefName': prefName, 'prefValue': prefVal}
        }).done( function(data) {
            if (data.success) {
                input.attr('value', prefVal).attr('title', prefVal).attr('prev-value', prefVal);
                input.removeClass('error');
                saveButton.hide();
                return false;
            }
            input.addClass('error');
        }).always( function(data) {
            // reinstate the pref's controls + revert save icon to normal.
            input.removeAttr('disabled');
            saveButton.removeAttr('disabled');
            btnIcon.removeClass('icon-spinner').addClass('icon-disk');
        });
        return false;
    },
    
    /**
     * @param jQueryElement A control with the extended name, including index as: '[nn]'
     * @returns int the index
     */
    _calcContactNotifRowIndex = function(jQueryElement) {
        var name = jQueryElement.attr('name');
        var iStart = name.indexOf('[')+1;
        var iEnd = name.indexOf(']');
        var strId = name.substring(iStart,iEnd);
        return parseInt(strId, 10);
    },
    
    _renumberContactNotifControls = function(jQueryRow, newIndex) {
        var ctrlType = jQueryRow.find('select');
        var nameType = _controlNamePrefix +'['+ newIndex +'].contactNotificationType';
        var ctrlVal = jQueryRow.find('input');
        var nameVal = _controlNamePrefix +'['+ newIndex +'].notificationValue';
        ctrlType.attr({
            name: nameType,
            id: nameType
        });
        ctrlVal.attr({
            name: nameVal,
            id: nameVal
        });
    };
        
    mod = {
            
        init: function() {
            
            if (_initialized) return;
            
            mod.disablePasswordUserInput();
            
            $(document).on('click', '#tbl_preferences .selection_group button:not(.on):not(.js-pref-default)', this.setEnumPreference);
            $(document).on('click', '#tbl_preferences .selection_group .js-pref-default', this.setEnumPreferenceDefault);
            $(document).on('click', '#tbl_preferences form.js-pref-form .save-preference', this.setStringPreferenceFromSave);
            $(document).on('keypress', '#tbl_preferences form.js-pref-form input', this.setStringPreferenceOnReturn);
            $(document).on('click', '#tbl_preferences form.js-pref-form .js-pref-default', this.setStringPreferenceFromDefault);
            $(document).on('keyup', '#tbl_preferences input[type="text"]', this.togglePreferenceSaveButton);
            $(document).on('change', '#tbl_preferences input[type="text"]', this.togglePreferenceSaveButton);
            $(document).on('type', '#tbl_preferences input', this._setStringPreference);
            
            $(document).on('yukon:user:profile:password:save', this.submitChangePassword);
            $(document).on('dialogclose', '#change-password-popup', this.blankoutPasswordForm);
            
            $(document).on('keyup', 'input[type="text"]', this.updateInputTitle);
            $(document).on('click', '#btn_addContactInfo', this.addContactNotifRow);
            $(document).on('change', '.js-has_selectOne', this.selectRemoveSelectOne);
            
            $(document).on('click', '#contactNotifs td .js-remove', this.removeContactNotifRow);
            $(document).on('change', 'select.js-contactNotif-type', this.updateContactNotifInputFormatting);
            
            $(document).on('yukon.dialog.confirm.ok', '#yukon_dialog_confirm', this.resetAllPreferences);
            
            _initialized = true;
        },
        
        // Disable all Ctrl+ commands and the context menu.
        // Only vulnerability left may be outside events and special keyboard keys (eg. 'paste' key).
        disablePasswordUserInput: function() {
            
            $(document).on('contextmenu paste', 'input[type="password"]', function(event) {
                return false;
            });
            
            // Ctrl+V, Ctrl+v, shift+Insert.
            // NOTE: this may only work for English/ASCII inputs
            // keydown for IE, hopefully also for others instead of the more-used 'keypress'
            $(document).on('keydown', 'input[type="password"]', function(event){
                if ((event.ctrlKey == true 
                        && (event.which === '118' || event.which === '86'))
                        || (event.shiftKey === true && event.keyCode === 45)) {
                    return false;
                }
            });
        },
        
        submitChangePassword: function(event){
            
            var form = $('#change-password-form');
            form.parent().find('input').prop('disabled', true);
            form.parent().find('button').prop('disabled', true);
            var ctrlOldPassword = form.find('input[name="oldPassword"]');
            var ctrlNewPassword = form.find('input[name="password1"]');
            var ctrlConfirmPassword = form.find('input[name="password2"]');
            var userId = $('input[name="userId"]').val();
            
            $.ajax({
                type: 'POST',
                url: _urlUpdatePassword,
                data: {'userId': userId, 
                       'oldPassword': ctrlOldPassword.val(), 
                       'newPassword': ctrlNewPassword.val(), 
                       'confirmPassword': ctrlConfirmPassword.val()
                }
            }).done(function(results) {
                form.find('input').removeClass('error');
                if (results.success) {
                    $('#change-password-popup').dialog('close');
                    $('#password_row td.value').removeClass('warning').text(results.new_date);
                } else {
                    // manually create a list of error messages to display
                    var errString = '<ol class="error">';
                    for(var ii=0; ii < results.errors.length; ii++) {
                        var err=results.errors[ii];
                        errString += '<li><i class="icon icon-cross"></i>'+ err.message +'</li>';
                        form.find('input[name="'+ err.field +'"]').addClass('error');
                    }
                    var errList = $('.password_errors');
                    errList.html(errString +'</ol>');
                    errList.show();
                }
                // Let the user use the form again:
                form.find('input').prop('disabled', false);
                form.parent().find('button').prop('disabled', false);
            });
        },
        
        blankoutPasswordForm: function(event) {
            var form = $(event.currentTarget);
            form.find('input[type="password"]').val('');
            form.find('input').removeAttr('disabled').removeClass('error');
            form.find('button').removeAttr('disabled');
            $('#change-password-popup .icon-accept, #change-password-popup .icon-cross, #change-password-popup .icon-blank')
                .removeClass('icon-accept icon-cross icon-blank')
                .addClass('icon-blank');
            $('.password_errors').text('').hide();
        },
        
        togglePreferenceSaveButton: function(event) {
            var ctrlInput = $(event.currentTarget);
            if (ctrlInput.val() !== ctrlInput.attr('prev-value')) {
                ctrlInput.closest('td').find('.save-preference').show();
            } else {
                ctrlInput.closest('td').find('.save-preference').hide();
            }
        },
        
        updateContactNotifInputFormatting: function(event) {
            var ctrlSelect = $(event.currentTarget);
            var selectedOptionValue = ctrlSelect.find(':selected').val();
            var input = ctrlSelect.closest('tr').find('input.js-contactNotif-val');
            
            var isPhone = selectedOptionValue == 'CALL_BACK_PHONE' || selectedOptionValue == 'CELL_PHONE' || selectedOptionValue == 'FAX' || selectedOptionValue == 'HOME_PHONE' || selectedOptionValue == 'PHONE' || selectedOptionValue == 'WORK_PHONE';
            input.removeClass('js-format-phone');
            if (isPhone) {
                input.addClass('js-format-phone');
                for (var ii=0; ii < input.length; ii++) {
                    yukon.ui.formatPhone(input[ii]);
                }
            }
        },
        
        // The <d:confirm> gets hit, then it comes here:
        resetAllPreferences: function(event) {
            var table = $('#tbl_preferences');
            var userId = $('input[name="userId"]').val();
            
            $.ajax({
                type: 'POST',
                url: _urlChangeAllPreferences,
                data: {'userId': userId}
            }).done(function(json) {
                // Go through the data and turn on enums
                json.preferences.forEach(function(preference, index, fullarray) {
                    var row = table.find('tr[data-type='+ preference.name +']');
                    row.find('button').removeClass('on');
                    row.find('button[data-value='+ preference.defaultVal +']:not(.js-pref-default)').addClass('on');
                });
            });
        },
        
        setEnumPreference: function(event) {
            _setEnumPreference(event, $(event.currentTarget));
        },
        
        setEnumPreferenceDefault: function(event) {
            var btnSameOption = _getPrefButtonWithSameOption($(event.currentTarget), event);
            _setEnumPreference(event, btnSameOption);
        },
        
        setStringPreferenceFromSave: function(event) {
            _setStringPreference($(event.currentTarget));
        },
        
        setStringPreferenceFromDefault: function(event) {
            var btn = $(event.currentTarget);
            var td = btn.closest('td');
            // Copy over the default string saved in a related control
            td.find('input[type="text"]').val(btn.attr('data-value'));
            _setStringPreference(td.find('.save-preference'));
        },
        
        setStringPreferenceOnReturn: function(event) {
            if (event.keyCode == 13) {
                event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
                event.stopPropagation();
                _setStringPreference($(event.currentTarget).closest('td').find('.save-preference'));
                return false;
            }
        },
        
        updateInputTitle: function(event) {
            var ctrl = $(event.currentTarget);
            // Disallow keys?
            //if (event.keyCode == 13) {
            if (ctrl.val().length < 1) {
                ctrl.removeAttr('title');
            } else {
                ctrl.attr('title', ctrl.val());
            }
        },
        
        addContactNotifRow: function(event) {
            var newRow =$('#template_contactNotif').clone();
            if(newRow.length < 1) {
                alert('INTERNAL ERROR #1: missing contact info template row');
                return;
            }
            newRow.removeAttr('id');
            var ctrlType = newRow.find('select');
            ctrlType.addClass('js-has_selectOne');
            
            // calculate the nextIndex so we can set the controls names.
            var lastExistingRow = $('#contactNotifs').find('tr:not(#template_contactNotif):last');
            var nextIndex = 0;
            if(lastExistingRow.length > 0) {
                nextIndex = 1+ _calcContactNotifRowIndex(lastExistingRow.find('select'));
            }
            _renumberContactNotifControls(newRow, nextIndex);
            
            $('#contactNotifs tbody').append(newRow);
            newRow.show();
            $('#contactNotifs').siblings('.js-display_empty').hide();
            ctrlType.focus();
        },
        
        removeContactNotifRow: function(event) {
            var row = $(event.currentTarget).closest('tr');
            var prevRow = row.prev('tr:not(#template_contactNotif)');
            var index = 0;
            if (prevRow.length != 0) {
                index = 1+ _calcContactNotifRowIndex(prevRow.find('select'));
            }
            var sibs = row.next();
            while(sibs.length > 0) {
                _renumberContactNotifControls(sibs, index);
                index++;
                sibs = sibs.next();
            }
            row.remove();
        },
        
        selectRemoveSelectOne: function(event) {
            var ctrlSelect = $(event.currentTarget);
            var selectedOptionValue = ctrlSelect.find(':selected').val();
            if(selectedOptionValue != '-1') {
                ctrlSelect.find('option[value="-1"]').remove();
                ctrlSelect.removeClass('js-has_selectOne');
            }
        }
    };
    
    return mod;
})();

$(function() { yukon.userProfile.init(); });