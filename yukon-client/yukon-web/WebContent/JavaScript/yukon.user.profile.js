/**
 * Singleton that manages the user profile page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.userProfile');

yukon.userProfile = (function () {
    
    /* PRIVATE VARIABLES AND METHODS */
    var mod,
        _initialized = false,
        _controlNamePrefix = 'contact.otherNotifications',
        _evtDoChangePassword = 'evt_ajaxsubmit_confirm_password',
        _urlChangePreference = yukon.url('/user/updatePreference.json'),
        _urlChangeAllPreferences = yukon.url('/user/updatePreferences/all/default.json'),
        _urlUpdatePassword = yukon.url('/user/updatePassword.json'),
        _getPrefButtonWithSameOption = function(jqueryElement, event) {
            var btnSameOption = jqueryElement.closest('td').find('button:not(.f-pref-default)[data-value='+ jQuery(event.currentTarget).attr('data-value') +']');
            return btnSameOption;
        },

        _setEnumPreference = function(event, targetBtn) {
            var option = jQuery(event.currentTarget);
            var row = option.closest('tr');
            var prefName = row.attr('data-type');
            var prefVal = option.attr('data-value');
            var userId = jQuery('input[name="userId"]').val();

            jQuery.ajax({
                type: 'POST',
                url: _urlChangePreference,
                data: {'userId': userId, 'prefName': prefName, 'prefValue': prefVal}
            }).done(function(data) {
                option.closest('td').find('button').removeClass('on');
                targetBtn.addClass('on');
            });
        },

        _setStringPreference = function(saveButton) {
            var input = saveButton.closest('td').find('input');
            var prefVal = input.attr('value');
            // Pref's type is stored on the TR
            var prefName = input.closest('tr').attr('data-type');
            var userId = jQuery('input[name="userId"]').val();

            // Disable the preference's controls + change the icon
            input.prop('disabled', true);
            saveButton.prop('disabled', true);
            var btnIcon = saveButton.find('i');
            btnIcon.removeClass('icon-disk').addClass('icon-spinner');

            jQuery.ajax({
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

        /* PUBLIC METHODS */

        init: function() {
            if (_initialized) {
                return;
            }

            mod.disablePasswordUserInput();

            jQuery(document).on('click', '#tbl_preferences .selection_group button:not(.on):not(.f-pref-default)', this.setEnumPreference);
            jQuery(document).on('click', '#tbl_preferences .selection_group .f-pref-default', this.setEnumPreferenceDefault);
            jQuery(document).on('click', '#tbl_preferences form.f-pref-form .save-preference', this.setStringPreferenceFromSave);
            jQuery(document).on('keypress', '#tbl_preferences form.f-pref-form input', this.setStringPreferenceOnReturn);
            jQuery(document).on('click', '#tbl_preferences form.f-pref-form .f-pref-default', this.setStringPreferenceFromDefault);
            jQuery(document).on('keyup', '#tbl_preferences input[type="text"]', this.togglePreferenceSaveButton);
            jQuery(document).on('change', '#tbl_preferences input[type="text"]', this.togglePreferenceSaveButton);
            jQuery(document).on('type', '#tbl_preferences input', this._setStringPreference);

            jQuery(document).on(_evtDoChangePassword, '#dlg_change_password', this.submitChangePassword);
            jQuery(document).on('dialogclose', '#dlg_change_password', this.blankoutPasswordForm);

            jQuery(document).on('keyup', 'input[type="text"]', this.updateInputTitle);
            jQuery(document).on('click', '#btn_addContactInfo', this.addContactNotifRow);
            jQuery(document).on('change', '.f-has_selectOne', this.selectRemoveSelectOne);

            jQuery(document).on('click', '#contactNotifs td .f-remove', this.removeContactNotifRow);
            jQuery(document).on('change', 'select.f-contactNotif-type', this.updateContactNotifInputFormatting);

            jQuery(document).on('yukonDialogConfirmOk', '#yukon_dialog_confirm', this.resetAllPreferences);

            _initialized = true;
        },

        // Disable all Ctrl+ commands and the context menu.  Only vulnerability left may be outside events and special keyboard keys (eg. 'paste' key)...
        disablePasswordUserInput: function() {
            jQuery(document).on('contextmenu paste', 'input[type="password"]', function(event) {
                event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
                return false;
            });
            // Ctrl+V, Ctrl+v, shift+Insert.
            // NOTE: this may only work for English/ASCII inputs
            // keydown for IE, hopefully also for others instead of the more-used 'keypress'
            jQuery(document).on('keydown', 'input[type="password"]', function(event){
                if ((event.ctrlKey==true && (event.which == '118' || event.which == '86'))|| (event.shiftKey==true && event.keyCode==45)) {
                    event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
                    return false;
                }
            });
        },

        submitChangePassword: function(event){
            var form = jQuery('#loginBackingBean');
            form.parent().find('input').prop('disabled', true);
            form.parent().find('button').prop('disabled', true);
            var ctrlOldPassword = form.find('input[name="oldPassword"]');
            var ctrlNewPassword = form.find('input[name="password1"]');
            var ctrlConfirmPassword = form.find('input[name="password2"]');
            var userId = jQuery('input[name="userId"]').val();

            jQuery.ajax({
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
                    jQuery('#dlg_change_password').dialog('close');
                    jQuery('#password_row td.value').removeClass('warning').text(results.new_date);
                } else {
                    // manually create a list of error messages to display
                    var errString = '<ol class="error">';
                    for(var ii=0; ii < results.errors.length; ii++) {
                        var err=results.errors[ii];
                        errString += '<li><i class="icon icon-cross"></i>'+ err.message +'</li>';
                        form.find('input[name="'+ err.field +'"]').addClass('error');
                    }
                    var errList = jQuery('.password_errors');
                    errList.html(errString +'</ol>');
                    errList.show();
                }
                // Let the user use the form again:
                form.find('input').prop('disabled', false);
                form.parent().find('button').prop('disabled', false);
            });
        },

        blankoutPasswordForm: function(event) {
            var form = jQuery(event.currentTarget);
            form.find('input[type="password"]').val('');
            form.find('input').removeAttr('disabled').removeClass('error');
            form.find('button').removeAttr('disabled');
            jQuery('#dlg_change_password .icon-accept, #dlg_change_password .icon-cross, #dlg_change_password .icon-blank')
                .removeClass('icon-accept icon-cross icon-blank')
                .addClass('icon-blank');
            jQuery('.password_errors').text('').hide();
        },

        togglePreferenceSaveButton: function(event) {
            var ctrlInput = jQuery(event.currentTarget);
            if (ctrlInput.val() != ctrlInput.attr('prev-value')) {
                ctrlInput.closest('td').find('.save-preference').show();
            } else {
                ctrlInput.closest('td').find('.save-preference').hide();
            }
        },

        updateContactNotifInputFormatting: function(event) {
            var ctrlSelect = jQuery(event.currentTarget);
            var selectedOptionValue = ctrlSelect.find(':selected').val();
            var input = ctrlSelect.closest('tr').find('input.f-contactNotif-val');

            var isPhone = selectedOptionValue == 'CALL_BACK_PHONE' || selectedOptionValue == 'CELL_PHONE' || selectedOptionValue == 'FAX' || selectedOptionValue == 'HOME_PHONE' || selectedOptionValue == 'PHONE' || selectedOptionValue == 'WORK_PHONE';
            input.removeClass('f-formatPhone');
            if (isPhone) {
                input.addClass('f-formatPhone');
                for (var ii=0; ii < input.length; ii++) {
                    yukon.ui.formatPhone(input[ii]);
                }
            }
        },

        // The <d:confirm> gets hit, then it comes here:
        resetAllPreferences: function(event) {
            var table = jQuery('#tbl_preferences');
            var userId = jQuery('input[name="userId"]').val();

            jQuery.ajax({
                type: 'POST',
                url: _urlChangeAllPreferences,
                data: {'userId': userId}
            }).done(function(json) {
                // Go through the data and turn on enums
                json.preferences.forEach(function(preference, index, fullarray) {
                    var row = table.find('tr[data-type='+ preference.name +']');
                    row.find('button').removeClass('on');
                    row.find('button[data-value='+ preference.defaultVal +']:not(.f-pref-default)').addClass('on');
                });
            });
        },

        setEnumPreference: function(event) {
            _setEnumPreference(event, jQuery(event.currentTarget));
        },

        setEnumPreferenceDefault: function(event) {
            var btnSameOption = _getPrefButtonWithSameOption(jQuery(event.currentTarget), event);
            _setEnumPreference(event, btnSameOption);
        },

        setStringPreferenceFromSave: function(event) {
            _setStringPreference(jQuery(event.currentTarget));
        },

        setStringPreferenceFromDefault: function(event) {
            var btn = jQuery(event.currentTarget);
            var td = btn.closest('td');
            // Copy over the default string saved in a related control
            td.find('input[type="text"]').val(btn.attr('data-value'));
            _setStringPreference(td.find('.save-preference'));
        },

        setStringPreferenceOnReturn: function(event) {
            if (event.keyCode == 13) {
                event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
                event.stopPropagation();
                _setStringPreference(jQuery(event.currentTarget).closest('td').find('.save-preference'));
                return false;
            }
        },

        updateInputTitle: function(event) {
            var ctrl = jQuery(event.currentTarget);
            // Disallow keys?
            //if (event.keyCode == 13) {
            if (ctrl.val().length < 1) {
                ctrl.removeAttr('title');
            } else {
                ctrl.attr('title', ctrl.val());
            }
        },

        addContactNotifRow: function(event) {
            var newRow =jQuery('#template_contactNotif').clone();
            if(newRow.length < 1) {
                alert('INTERNAL ERROR #1: missing contact info template row');
                return;
            }
            newRow.removeAttr('id');
            var ctrlType = newRow.find('select');
            ctrlType.addClass('f-has_selectOne');

            // calculate the nextIndex so we can set the controls names.
            var lastExistingRow = jQuery('#contactNotifs').find('tr:not(#template_contactNotif):last');
            var nextIndex = 0;
            if(lastExistingRow.length > 0) {
                nextIndex = 1+ _calcContactNotifRowIndex(lastExistingRow.find('select'));
            }
            _renumberContactNotifControls(newRow, nextIndex);

            jQuery('#contactNotifs tbody').append(newRow);
            newRow.show();
            jQuery('#contactNotifs').siblings('.f-display_empty').hide();
            ctrlType.focus();
        },

        removeContactNotifRow: function(event) {
            var row = jQuery(event.currentTarget).closest('tr');
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
            var ctrlSelect = jQuery(event.currentTarget);
            var selectedOptionValue = ctrlSelect.find(':selected').val();
            if(selectedOptionValue != '-1') {
                ctrlSelect.find('option[value="-1"]').remove();
                ctrlSelect.removeClass('f-has_selectOne');
            }
        }
    };
    return mod;
})();

jQuery(function() {yukon.userProfile.init();});