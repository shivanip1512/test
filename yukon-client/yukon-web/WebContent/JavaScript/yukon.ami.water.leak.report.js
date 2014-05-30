/**
 * Singleton that manages the javascript in the Water Leak Report
 * @requires jQuery 1.6+
 */

yukon.namespace('yukon.ami');
yukon.namespace('yukon.ami.waterLeakReport');

yukon.ami.waterLeakReport = (function () {
    
    var _interval_data_form_selector = '#intervalDataForm',
        _interval_data_form_btn_selector = '#intervalDataForm button',
        _filter_form_selector = '#filterForm',
        _schedule_form_selector = '#scheduleForm',
        _reset_form_selector = '#resetForm',
        _csv_leak_form_selector = '#csvLeakForm',
        _csv_interval_form_selector = '#csvIntervalForm',
        _leak_filter_dialog = '#leakFilterDialog',
        _leak_schedule_dialog = '#leakScheduleDialog',
        _form_input_name = 'selectedPaoIds',
        _all_selector = '#f-check_all:checkbox',
        _single_selector = 'tbody tr input.f-check_single:checkbox',
        _initialized = false,
        _f_filter_values = [],
        /* --------------- */
        /* private methods */
        /* --------------- */

        _init_filter = function () {
            if ($('input#hasFilterError').val() === 'true') {
                open_leakFilterDialog();
            }
        },

        _init_schedule = function () {
            if ($('input#hasScheduleError').val() === 'true') {
                open_leakScheduleDialog();
            }
        },

        _init_filter_key_bindings = function (e) {
            if (e.keyCode == 13) { // enter
                mod.filter_submit();
            }
        },

        _init_open_filter_key_binding = function (e) {
            var filter_dialog = $('#leakFilterDialog');
            if (e.keyCode == 70 /* f (for "filter") */ &&
                    !$(e.target).is('input') && /* don't open the dialog if the user is typing in an input field */
                    filter_dialog.length === 1 &&
                    !filter_dialog.is(':visible')) {
                open_leakFilterDialog();
            }
            if (e.keyCode == 83 /* s (for "schedule") */ &&
                    !$(e.target).is('input') && /* don't open the dialog if the user is typing in an input field */
                    filter_dialog.length === 1 &&
                    !filter_dialog.is(':visible')) {
                open_leakScheduleDialog();
            }
        },

        _init_interval_data_form = function () {
            $(_single_selector + ':checked').each(function () {
                var paoId = _get_row_pao_id(this);
                _add_to_interval_data_form(paoId);
            });
            _set_interval_data_btn_disabled();
        },

        _filter_dialog_open = function (e) {
            if (_f_filter_values.length > 0) {
                _restore_filter_values();
            }
            //remove the stupid <br> tag that screws up styling for threshold errors
            $(document.getElementById('threshold.errors')).prev('br').remove();

            // if there are filter errors... set the focus to the first one of these
            yukon.ui.focusFirstError();
        },

        _store_filter_values = function () {
            var the_filter = $(_filter_form_selector);
            _f_filter_values.from_datetime  = the_filter.find('input.f-from_datetime').val();
            _f_filter_values.from_hour        = the_filter.find('input.f-from_hour').val();
            _f_filter_values.to_datetime    = the_filter.find('input.f-to_datetime').val();
            _f_filter_values.to_hour          = the_filter.find('input.f-to_hour').val();
            _f_filter_values.threshold        = the_filter.find('input.f-threshold').val();
            _f_filter_values.include_disabled = the_filter.find('input.f-include_disabled_paos').is(':checked');
        },

        _restore_filter_values = function () {
            var the_filter = $(_filter_form_selector);
            the_filter.find('input.f-from_datetime').val(_f_filter_values.from_datetime);
            the_filter.find('input.f-from_hour').val(_f_filter_values.from_hour);
            the_filter.find('input.f-to_datetime').val(_f_filter_values.to_datetime);
            the_filter.find('input.f-to_hour').val(_f_filter_values.to_hour);
            the_filter.find('input.f-threshold').val(_f_filter_values.threshold);
            the_filter.find('input.f-include_disabled_paos').prop('checked', _f_filter_values.include_disabled);
        },

        _filter_group_clicked = function () {
            open_leakFilterDialog();
            $("[class^='chooseGroupIcon_deviceGroupNameSelectorTag_']", _leak_filter_dialog).trigger('click');
        },
        _filter_individual_clicked = function () {
            open_leakFilterDialog();
            selectDevicesPickerFilter.show();
        },
        _filter_to_date_clicked = function () {
            open_leakFilterDialog();
            $('.f-to_datetime', _leak_filter_dialog).focus();
            return false;
        },
        _filter_date_range_clicked = function () {
            open_leakFilterDialog();
            $('.f-from_datetime', _leak_filter_dialog).focus();
        },
        _filter_threshold_clicked = function (e) {
            open_leakFilterDialog();
            $('.f-threshold', _leak_filter_dialog).focus();
        },
        _filter_reset_disabled_devices_clicked = function () {
            $('.f-include_disabled_paos', _leak_filter_dialog).val(false);
            mod.filter_submit();
            return false;
        },
        _filter_disabled_devices_clicked = function () {
            open_leakFilterDialog();
            $('input', _leak_filter_dialog).blur();
        },

        _set_filter_form_hidden_input = function (name, value) {
            $(_filter_form_selector + ' input[name="'+name+'"]').remove();
            $(_filter_form_selector).append('<input type="hidden" name="'+name+'" value="'+value+'"/>');
        },

        _set_schedule_form_hidden_input = function (name, value) {
            $(_schedule_form_selector + ' input[name="'+name+'"]').remove();
            $(_schedule_form_selector).append('<input type="hidden" name="'+name+'" value="'+value+'"/>');
        },

        _all_checked = function () {
            if ($(this).is(':checked')) {
                var check_singles = $(this).closest('table').find(_single_selector); 
                check_singles.each(function () {
                    var paoId = _get_row_pao_id(this);
                    _add_to_interval_data_form(paoId);
                });
            } else {
                $(_interval_data_form_selector + " input[name='" + _form_input_name + "']").remove();
            }
            _set_interval_data_btn_disabled();
        },

        _single_checked = function () {
            var paoId = _get_row_pao_id(this);
            if ($(this).is(':checked')) {
                _add_to_interval_data_form(paoId);
            } else {
                _remove_from_interval_data_form(paoId);
            }
            _set_interval_data_btn_disabled();
        },

        _set_interval_data_btn_disabled = function () {
            var checked = $(_single_selector).is(':checked');
            $(_interval_data_form_btn_selector).prop('disabled', !checked);
        },

        _export_leak_csv = function () {
            $(_csv_leak_form_selector).submit();
        },

        _export_interval_csv = function () {
            $(_csv_interval_form_selector).submit();
        },

        _disable_filter_buttons = function () {
            $('.ui-button', '.ui-dialog-buttonset').prop('disabled', true);
        },

        _get_row_pao_id = function (elem) {
            return $(elem).closest('tr').find('input.the_pao_id').val();
        },

        _add_to_interval_data_form = function (paoId) {
            if (!$(_interval_data_form_selector + " input[name='" + _form_input_name + "'][value='" + paoId + "']").length) {
                $(_interval_data_form_selector).prepend("<input type='hidden' name='" + _form_input_name + "' value='"+paoId+"'>");
            }
        },
        _remove_from_interval_data_form = function (paoId) {
            $(_interval_data_form_selector + " input[value='"+paoId+"']").remove();
        },
        mod;

    mod = {
        /* -------------- */
        /* public methods */
        /* -------------- */

        init: function () {
            if (!_initialized) {
                /* report.jsp */
                $(_all_selector).checkAll(_single_selector);
                $(_all_selector).on('change', _all_checked);
                $(_single_selector).on('change', _single_checked);
                $(_leak_filter_dialog).on('keyup', _init_filter_key_bindings);
                $(document).on('dialogopen', _leak_filter_dialog, _filter_dialog_open);
                $(document).on('keyup', _init_open_filter_key_binding);
                $('.f-open_filter_dialog').on('click', function () {yukon.ui._autofocus();});
                $('#exportLeakCsv').on('click', _export_leak_csv);
                _init_filter();
                _store_filter_values();
                _init_interval_data_form();
                
                /* report.jsp filter labels */
                $('.filter-settings .f-filter_group_clicked').on('click', _filter_group_clicked);
                $('.filter-settings .f-filter_individual_clicked').on('click', _filter_individual_clicked);
                $('.filter-settings .f-filter_date_range .f-filter_to_date_clicked').on('click', _filter_to_date_clicked);
                $('.filter-settings .f-filter_date_range').on('click', _filter_date_range_clicked);
                $('.filter-settings .f-filter_threshold_clicked').on('click', _filter_threshold_clicked);
                $('.filter-settings .f-filter_reset_disabled_devices_clicked').on('click', _filter_reset_disabled_devices_clicked);
                $('.filter-settings .f-disabled_devices').on('click', _filter_disabled_devices_clicked);
                $('.filter-settings .f-reset_filter_submit').on('click', this.reset_filter_submit);

                /* intervalData.jsp */
                $('#exportIntervalCsv').on('click', _export_interval_csv);

                _initialized = true;
            }
            if ($('input#jobId').length) {
                open_leakScheduleDialog();
            }
            _init_schedule();
        },
        
        filter_group_selected_callback: function () {
            _set_filter_form_hidden_input('collectionType', 'group');
        },
        
        filter_individual_selected_callback: function () {
            _set_filter_form_hidden_input('collectionType', 'idList');
        },
        
        schedule_group_selected_callback: function () {
            _set_schedule_form_hidden_input('collectionType', 'group');
        },
        
        schedule_individual_selected_callback: function () {
            _set_schedule_form_hidden_input('collectionType', 'idList');
        },
        
        filter_submit: function () {
            _disable_filter_buttons();
            $(_filter_form_selector).submit();
        },
        
        schedule_submit: function () {
            _disable_filter_buttons();
            $(_schedule_form_selector).submit();
        },
        
        reset_filter_submit: function () {
            _disable_filter_buttons();
            $(_reset_form_selector).submit();
        }
    };
    return mod;
})();

$(function () {
    yukon.ami.waterLeakReport.init();
});