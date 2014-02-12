yukon.namespace('yukon.UserPreferences');
yukon.UserPreferences = (function () {
    /**
     * Singleton that manages the javascript in User Preferences
     * 
     * @class User Preferences javascript
     * @requires jQuery 1.6+
     */
    /* PRIVATE VARIABLES AND METHODS */
    var _get_pref_button_with_same_option = function(jqueryElement, event) {
            var btn_sameOption = jqueryElement.closest("td").find("button:not(.f-pref-default)[data-value="+ jQuery(event.currentTarget).attr('data-value') +"]");
            return btn_sameOption;
        },

        _set_enum_preference = function(event, target_button) {
            var option = jQuery(event.currentTarget);
            var row = option.closest("tr");
            var prefName = row.attr('data-type');
            var prefVal = option.attr('data-value');
            var userId = jQuery("input[name='userId']").val();

            jQuery.ajax({
                type: "POST",
                url: _url_change_preference,
                data: {'userId': userId, 'prefName': prefName, 'prefValue': prefVal}
            }).done(function(data) {
                option.closest("td").find("button").removeClass("on");
                target_button.addClass("on");
            });
        },

        _set_string_preference = function(jquery_btn_save) {
            var input = jquery_btn_save.closest('td').find('input');
            var prefVal = input.attr('value');
            // Pref's type is stored on the TR
            var prefName = input.closest('tr').attr('data-type');
            var userId = jQuery("input[name='userId']").val();

            // Disable the preference's controls + change the icon
            input.attr("disabled", "disabled");
            jquery_btn_save.attr("disabled", "disabled");
            var btn_icon = jquery_btn_save.find('i');
            btn_icon.removeClass("icon-disk").addClass("icon-spinner");

            jQuery.ajax({
                type: "POST",
                url: _url_change_preference,
                data: {'userId': userId, 'prefName': prefName, 'prefValue': prefVal}
            }).done( function(data) {
                if (data.success) {
                    input.attr('value', prefVal).attr('title', prefVal).attr('prev-value', prefVal);
                    input.removeClass("error");
                    jquery_btn_save.hide();
                    return false;
                }
                input.addClass("error");
            }).always( function(data) {
                // reinstate the pref's controls + revert save icon to normal.
                input.removeAttr('disabled');
                jquery_btn_save.removeAttr('disabled');
                btn_icon.removeClass("icon-spinner").addClass("icon-disk");
            });
            return false;
        },

        /**
         * @param jQueryElement     A control with the extended name, including index as: "[nn]"
         * @returns                 int the index
         */
        _calc_contact_notif_row_index = function(jQueryElement) {
            var name = jQueryElement.attr("name");
            var iStart = name.indexOf("[")+1;
            var iEnd = name.indexOf("]");
            var str_id = name.substring(iStart,iEnd);
            return parseInt(str_id, 10);
        },

        _renumber_contact_notif_controls = function(jQueryRow, newIndex) {
            var ctrlType = jQueryRow.find("select");
            var nameType = _control_name_prefix +"["+ newIndex +"].contactNotificationType";
            var ctrlVal = jQueryRow.find("input");
            var nameVal = _control_name_prefix +"["+ newIndex +"].notificationValue";
            ctrlType.attr({
                name: nameType,
                id: nameType
            });
            ctrlVal.attr({
                name: nameVal,
                id: nameVal
            });
        },
        _initialized = false,

        _control_name_prefix = "contact.otherNotifications",
        _default_button_class = "f-pref-default",
        _evt_do_change_password = "evt_ajaxsubmit_confirm_password",

        _url_change_preference = "/user/updatePreference.json",
        _url_change_all_preferences = "/user/updatePreferences/all/default.json",
        _url_update_password = "/user/updatePassword.json",
        userPreferencesMod;

    userPreferencesMod = {

        /* PUBLIC METHODS */

        init: function() {
            if (_initialized) {
                return;
            }

            userPreferencesMod.disable_password_user_input();

            jQuery(document).on("click", "#tbl_preferences .selection_group button:not(.on):not(.f-pref-default)", this.set_enum_preference);
            jQuery(document).on("click", "#tbl_preferences .selection_group .f-pref-default", this.set_enum_preference_default);
            jQuery(document).on("click", "#tbl_preferences form.f-pref-form .save-preference", this.set_string_preference_from_btn_save);
            jQuery(document).on("keypress", "#tbl_preferences form.f-pref-form input", this.set_string_preference_on_return);
            jQuery(document).on("click", "#tbl_preferences form.f-pref-form .f-pref-default", this.set_string_preference_from_default);
            jQuery(document).on("keyup", '#tbl_preferences input[type="text"]', this.toggle_preference_save_button);
            jQuery(document).on("change", '#tbl_preferences input[type="text"]', this.toggle_preference_save_button);
            jQuery(document).on("type", "#tbl_preferences input", this.set_string_preference);

            jQuery(document).on(_evt_do_change_password, "#dlg_change_password", this.submit_change_password);
            jQuery(document).on("dialogclose", "#dlg_change_password", this.blankout_password_form);

            jQuery(document).on("keyup", 'input[type="text"]', this.update_input_title);
            jQuery(document).on("click", "#btn_addContactInfo", this.add_contact_notif_row);
            jQuery(document).on("change", ".f-has_selectOne", this.select_remove_selectOne);

            jQuery(document).on("click", "#contactNotifs td .removeBtn", this.remove_contact_notif_row);
            jQuery(document).on("change", "select.f-contactNotif-type", this.update_contact_notif_input_formatting);

            jQuery(document).on('yukonDialogConfirmOk', '#yukon_dialog_confirm', this.reset_all_preferences);

            _initialized = true;
        },

        // Disable all Ctrl+ commands and the context menu.  Only vulnerability left may be outside events and special keyboard keys (eg. 'paste' key)...
        disable_password_user_input: function() {
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

        submit_change_password: function(event){
            var form = jQuery("#loginBackingBean");
            form.parent().find("input").attr('disabled', 'disabled');
            form.parent().find("button").attr('disabled', 'disabled');
            var ctrl_opwd = form.find('input[name="oldPassword"]');
            var ctrl_npwd = form.find('input[name="password1"]');
            var ctrl_cpwd = form.find('input[name="password2"]');
            var userId = jQuery("input[name='userId']").val();

            jQuery.ajax({
                type: "POST",
                url: _url_update_password,
                data: {'userId': userId, 
                       'oldPassword': ctrl_opwd.val(), 
                       'newPassword': ctrl_npwd.val(), 
                       'confirmPassword': ctrl_cpwd.val()
                }
            }).done(function(results) {
                form.find('input').removeClass("error");
                if (results.success) {
                    jQuery("#dlg_change_password").dialog('close');
                    jQuery("#password_row td.value").removeClass('warning').text(results.new_date);
                } else {
                    // manually create a list of error messages to display
                    var err_string = '<ol class="error">';
                    for(var ii=0; ii < results.errors.length; ii++) {
                        var err=results.errors[ii];
                        err_string += '<li><i class="icon icon-cross"></i>'+ err.message +'</li>';
                        form.find('input[name="'+ err.field +'"]').addClass("error");
                    }
                    var err_list = jQuery(".password_errors");
                    err_list.html(err_string +'</ol>');
                    err_list.show();
                }
                // Let the user use the form again:
                form.find("input").removeAttr('disabled');
                form.parent().find("button").removeAttr('disabled');
            });
        },

        blankout_password_form: function(event) {
            var form = jQuery(event.currentTarget);
            form.find('input[type="password"]').val("");
            form.find("input").removeAttr('disabled').removeClass("error");
            form.find("button").removeAttr('disabled');
            jQuery("#dlg_change_password .icon-accept, #dlg_change_password .icon-cross, #dlg_change_password .icon-blank")
                .removeClass("icon-accept")
                .removeClass("icon-cross")
                .removeClass("icon-blank")
                .addClass("icon-blank");
            jQuery(".password_errors").text('').hide();
        },

        toggle_preference_save_button: function(event) {
            var ctrl_input = jQuery(event.currentTarget);
            if (ctrl_input.val() != ctrl_input.attr('prev-value')) {
                ctrl_input.closest('td').find('.save-preference').show();
            } else {
                ctrl_input.closest('td').find('.save-preference').hide();
            }
        },

        update_contact_notif_input_formatting: function(event) {
            var ctrl_select = jQuery(event.currentTarget);
            var selected_option_val = ctrl_select.find(":selected").val();
            var input = ctrl_select.closest("tr").find("input.f-contactNotif-val");

            var is_phone = selected_option_val == "CALL_BACK_PHONE" || selected_option_val == "CELL_PHONE" || selected_option_val == "FAX" || selected_option_val == "HOME_PHONE" || selected_option_val == "PHONE" || selected_option_val == "WORK_PHONE";
            input.removeClass("f-formatPhone");
            if (is_phone) {
                input.addClass("f-formatPhone");
                for (var ii=0; ii < input.length; ii++) {
                    yukon.ui.formatPhone(input[ii]);
                }
            }
        },

        // The <d:confirm> gets hit, then it comes here:
        reset_all_preferences: function(event) {
            var table = jQuery("#tbl_preferences");
            var userId = jQuery("input[name='userId']").val();

            jQuery.ajax({
                type: "POST",
                url: _url_change_all_preferences,
                data: {'userId': userId}
            }).done(function(json) {
                // Go through the data and turn on enums
                json.preferences.forEach(function(preference, index, fullarray) {
                    var row = table.find('tr[data-type='+ preference.name +']');
                    row.find("button").removeClass("on");
                    row.find('button[data-value='+ preference.defaultVal +']:not(.f-pref-default)').addClass("on");
                });
            });
        },

        set_enum_preference: function(event) {
            _set_enum_preference(event, jQuery(event.currentTarget));
        },

        set_enum_preference_default: function(event) {
            var btn_sameOption = _get_pref_button_with_same_option(jQuery(event.currentTarget), event);
            _set_enum_preference(event, btn_sameOption);
        },

        set_string_preference_from_btn_save: function(event) {
            _set_string_preference(jQuery(event.currentTarget));
        },

        set_string_preference_from_default: function(event) {
            var btn_def = jQuery(event.currentTarget);
            var my_container = btn_def.closest('td');
            // Copy over the default string saved in a related control
            my_container.find('input[type="text"]').val(btn_def.attr("data-value"));
            _set_string_preference(my_container.find('.save-preference'));
        },

        set_string_preference_on_return: function(event) {
            if (event.keyCode == 13) {
                event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
                event.stopPropagation();
                _set_string_preference(jQuery(event.currentTarget).closest('td').find('.save-preference'));
                return false;
            }
        },

        update_input_title: function(event) {
            var ctrl = jQuery(event.currentTarget);
            // Disallow keys?
            //if (event.keyCode == 13) {
            if (ctrl.val().length < 1) {
                ctrl.removeAttr('title');
            } else {
                ctrl.attr('title', ctrl.val());
            }
        },

        add_contact_notif_row: function(event) {
            var new_row =jQuery("#template_contactNotif").clone();
            if(new_row.length < 1) {
                alert("INTERNAL ERROR #1: missing contact info template row");
                return;
            }
            new_row.removeAttr("id");
            var ctrlType = new_row.find("select");
            ctrlType.addClass("f-has_selectOne");

            // calculate the next_index so we can set the controls names.
            var last_existing_row = jQuery("#contactNotifs").find("tr:not(#template_contactNotif):last");
            var next_index = 0;
            if(last_existing_row.length > 0) {
                next_index = 1+ _calc_contact_notif_row_index(last_existing_row.find("select"));
            }
            _renumber_contact_notif_controls(new_row, next_index);

            jQuery("#contactNotifs tbody").append(new_row);
            new_row.show();
            jQuery("#contactNotifs").siblings(".f-display_empty").hide();
            ctrlType.focus();
        },

        remove_contact_notif_row: function(event) {
            var row = jQuery(event.currentTarget).closest('tr');
            var prev_row = row.prev("tr:not(#template_contactNotif)");
            var index = 0;
            if (prev_row.length != 0) {
                index = 1+ _calc_contact_notif_row_index(prev_row.find("select"));
            }
            var sibs = row.next();
            while(sibs.length > 0) {
                _renumber_contact_notif_controls(sibs, index);
                index++;
                sibs = sibs.next();
            }
            row.remove();
        },

        select_remove_selectOne: function(event) {
            var ctrl_select = jQuery(event.currentTarget);
            var selected_option_val = ctrl_select.find(":selected").val();
            if(selected_option_val != '-1') {
                ctrl_select.find('option[value="-1"]').remove();
                ctrl_select.removeClass("f-has_selectOne");
            }
        }
    };
    return userPreferencesMod;
})();

jQuery(function() {
    yukon.UserPreferences.init();
});
