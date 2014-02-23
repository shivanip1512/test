/**
 * Singleton that manages the javascript in the Water Leak Report
 * @requires jQuery 1.6+
 */

yukon.namespace('yukon.ami');
yukon.namespace('yukon.ami.waterLeakReport');

yukon.ami.waterLeakReport = (function() {
    
    var _interval_data_form_selector = "#intervalDataForm",
        _interval_data_form_btn_selector = "#intervalDataForm button",
        _filter_form_selector = "#filterForm",
        _schedule_form_selector = "#scheduleForm",
        _reset_form_selector = "#resetForm",
        _csv_leak_form_selector = "#csvLeakForm",
        _csv_interval_form_selector = "#csvIntervalForm",
        _leak_filter_dialog = "#leakFilterDialog",
        _leak_schedule_dialog = "#leakScheduleDialog",
        _form_input_name = "selectedPaoIds",
        _all_selector = "#f-check_all:checkbox",
        _single_selector = "tbody tr input.f-check_single:checkbox",
        _initialized = false,
        _f_cis_details_store = [],
        _f_filter_values = [],
        /* --------------- */
        /* private methods */
        /* --------------- */

        _init_filter = function() {
            if (jQuery("input#hasFilterError").val() === "true") {
                open_leakFilterDialog();
            }
        },

        _init_schedule = function() {
            if(jQuery("input#hasScheduleError").val() === "true") {
                open_leakScheduleDialog();
            }
        },

        _init_filter_key_bindings = function(e) {
            if (e.keyCode == 13) { // enter
                mod.filter_submit();
            }
        },

        _init_open_filter_key_binding = function(e) {
            var filter_dialog = jQuery("#leakFilterDialog");
            if (e.keyCode == 70 /* f (for "filter") */ &&
                    !jQuery(e.target).is("input") && /* don't open the dialog if the user is typing in an input field */
                    filter_dialog.length === 1 &&
                    !filter_dialog.is(":visible")) {
                open_leakFilterDialog();
            }
            if (e.keyCode == 83 /* s (for "schedule") */ &&
                    !jQuery(e.target).is("input") && /* don't open the dialog if the user is typing in an input field */
                    filter_dialog.length === 1 &&
                    !filter_dialog.is(":visible")) {
                open_leakScheduleDialog();
            }
        },

        _init_interval_data_form = function(){
            jQuery(_single_selector + ":checked").each(function() {
                var paoId = _get_row_pao_id(this);
                _add_to_interval_data_form(paoId);
            });
            _set_interval_data_btn_disabled();
        },

        _filter_dialog_open = function(e) {
            if (_f_filter_values.length > 0) {
                _restore_filter_values();
            }
            //remove the stupid <br> tag that screws up styling for threshold errors
            jQuery(document.getElementById("threshold.errors")).prev("br").remove();

            // if there are filter errors... set the focus to the first one of these
            yukon.ui.focusFirstError();
        },

        _store_filter_values = function() {
            var the_filter = jQuery(_filter_form_selector);
            _f_filter_values.from_datetime  = the_filter.find("input.f-from_datetime").val();
            _f_filter_values.from_hour        = the_filter.find("input.f-from_hour").val();
            _f_filter_values.to_datetime    = the_filter.find("input.f-to_datetime").val();
            _f_filter_values.to_hour          = the_filter.find("input.f-to_hour").val();
            _f_filter_values.threshold        = the_filter.find("input.f-threshold").val();
            _f_filter_values.include_disabled = the_filter.find("input.f-include_disabled_paos").is(":checked");
        },

        _restore_filter_values = function() {
            var the_filter = jQuery(_filter_form_selector);
            the_filter.find("input.f-from_datetime").val(_f_filter_values.from_datetime);
            the_filter.find("input.f-from_hour").val(_f_filter_values.from_hour);
            the_filter.find("input.f-to_datetime").val(_f_filter_values.to_datetime);
            the_filter.find("input.f-to_hour").val(_f_filter_values.to_hour);
            the_filter.find("input.f-threshold").val(_f_filter_values.threshold);
            the_filter.find("input.f-include_disabled_paos").attr("checked", _f_filter_values.include_disabled);
        },

        _filter_group_clicked = function() {
            open_leakFilterDialog();
            jQuery("[class^='chooseGroupIcon_deviceGroupNameSelectorTag_']", _leak_filter_dialog).trigger("click");
        },
        _filter_individual_clicked = function() {
            open_leakFilterDialog();
            selectDevicesPickerFilter.show();
        },
        _filter_to_date_clicked = function() {
            open_leakFilterDialog();
            jQuery(".f-to_datetime", _leak_filter_dialog).focus();
            return false;
        },
        _filter_date_range_clicked = function() {
            open_leakFilterDialog();
            jQuery(".f-from_datetime", _leak_filter_dialog).focus();
        },
        _filter_threshold_clicked = function(e) {
            open_leakFilterDialog();
            jQuery(".f-threshold", _leak_filter_dialog).focus();
        },
        _filter_reset_disabled_devices_clicked = function() {
            jQuery(".f-include_disabled_paos", _leak_filter_dialog).val(false);
            mod.filter_submit();
            return false;
        },
        _filter_disabled_devices_clicked = function() {
            open_leakFilterDialog();
            jQuery("input", _leak_filter_dialog).blur();
        },

        _set_filter_form_hidden_input = function(name, value) {
            jQuery(_filter_form_selector + ' input[name="'+name+'"]').remove();
            jQuery(_filter_form_selector).append('<input type="hidden" name="'+name+'" value="'+value+'"/>');
        },

        _set_schedule_form_hidden_input = function(name, value) {
            jQuery(_schedule_form_selector + ' input[name="'+name+'"]').remove();
            jQuery(_schedule_form_selector).append('<input type="hidden" name="'+name+'" value="'+value+'"/>');
        },

        _all_checked = function(){
            if (jQuery(this).is(":checked")) {
                var check_singles = jQuery(this).closest("table").find(_single_selector); 
                check_singles.each(function() {
                    var paoId = _get_row_pao_id(this);
                    _add_to_interval_data_form(paoId);
                });
            } else {
                jQuery(_interval_data_form_selector + " input[name='" + _form_input_name + "']").remove();
            }
            _set_interval_data_btn_disabled();
        },

        _single_checked = function(){
            var paoId = _get_row_pao_id(this);
            if (jQuery(this).is(':checked')) {
                _add_to_interval_data_form(paoId);
            } else {
                _remove_from_interval_data_form(paoId);
            }
            _set_interval_data_btn_disabled();
        },

        _set_interval_data_btn_disabled = function(){
            var checked = jQuery(_single_selector).is(":checked");
            jQuery(_interval_data_form_btn_selector).attr("disabled", !checked);
        },

        _export_leak_csv = function(){
            jQuery(_csv_leak_form_selector).submit();
        },

        _export_interval_csv = function(){
            jQuery(_csv_interval_form_selector).submit();
        },

        _view_cis_details = function(){
            var paoId = _get_row_pao_id(this);
            var in_array = jQuery.inArray(paoId, _f_cis_details_store);
            if (in_array === -1) {
                _f_cis_details_store.push(paoId);
                // only block the page if we haven't requested this before (will take longer)
                yukon.ui.blockPage();
            }
            
            var url = _get_row_account_info_url(this);
            jQuery('#accountInfoAjaxDialog').show();
            jQuery('#accountInfoAjaxDialog').load(url);
        },

        _disable_filter_buttons = function() {
            jQuery(".ui-button", ".ui-dialog-buttonset").attr("disabled", true);
        },

        _get_row_pao_id = function(elem){
            return jQuery(elem).closest("tr").find("input.the_pao_id").val();
        },

        _get_row_account_info_url = function(elem){
            return jQuery(elem).closest("tr").find("input.account_info_url").val();
        },

        _add_to_interval_data_form = function(paoId) {
            if (!jQuery(_interval_data_form_selector + " input[name='" + _form_input_name + "'][value='" + paoId + "']").length) {
                jQuery(_interval_data_form_selector).prepend("<input type='hidden' name='" + _form_input_name + "' value='"+paoId+"'>");
            }
        },
        _remove_from_interval_data_form = function(paoId) {
            jQuery(_interval_data_form_selector + " input[value='"+paoId+"']").remove();
        },
        mod;

    mod = {
        /* -------------- */
        /* public methods */
        /* -------------- */

        init: function(){
            if(!_initialized){
                /* report.jsp */
                jQuery(_all_selector).checkAll(_single_selector);
                jQuery(_all_selector).bind("change", _all_checked);
                jQuery(_single_selector).bind("change", _single_checked);
                jQuery(_leak_filter_dialog).bind("keyup", _init_filter_key_bindings);
                jQuery(document).on("dialogopen", _leak_filter_dialog, _filter_dialog_open);
                jQuery(document).bind("keyup", _init_open_filter_key_binding);
                jQuery("a.f-cis_details").bind("click", _view_cis_details);
                jQuery(".f-open_filter_dialog").bind("click", function(){yukon.ui._autofocus();});
                jQuery("#exportLeakCsv").bind("click", _export_leak_csv);
                _init_filter();
                _store_filter_values();
                _init_interval_data_form();
                
                /* report.jsp filter labels */
                jQuery(".filter-settings .f-filter_group_clicked").bind("click", _filter_group_clicked);
                jQuery(".filter-settings .f-filter_individual_clicked").bind("click", _filter_individual_clicked);
                jQuery(".filter-settings .f-filter_date_range .f-filter_to_date_clicked").bind("click", _filter_to_date_clicked);
                jQuery(".filter-settings .f-filter_date_range").bind("click", _filter_date_range_clicked);
                jQuery(".filter-settings .f-filter_threshold_clicked").bind("click", _filter_threshold_clicked);
                jQuery(".filter-settings .f-filter_reset_disabled_devices_clicked").bind("click", _filter_reset_disabled_devices_clicked);
                jQuery(".filter-settings .f-disabled_devices").bind("click", _filter_disabled_devices_clicked);
                jQuery(".filter-settings .f-reset_filter_submit").bind("click", this.reset_filter_submit);

                /* intervalData.jsp */
                jQuery("#exportIntervalCsv").bind("click", _export_interval_csv);

                _initialized = true;
            }
            if(jQuery("input#jobId").length) {
                open_leakScheduleDialog();
            }
            _init_schedule();
        },
        
        filter_group_selected_callback: function() {
            _set_filter_form_hidden_input("collectionType", "group");
        },
        
        filter_individual_selected_callback: function() {
            _set_filter_form_hidden_input("collectionType", "idList");
        },
        
        schedule_group_selected_callback: function() {
            _set_schedule_form_hidden_input("collectionType", "group");
        },
        
        schedule_individual_selected_callback: function() {
            _set_schedule_form_hidden_input("collectionType", "idList");
        },
        
        filter_submit: function(){
            _disable_filter_buttons();
            jQuery(_filter_form_selector).submit();
        },
        
        schedule_submit: function(){
            _disable_filter_buttons();
            jQuery(_schedule_form_selector).submit();
        },
        
        reset_filter_submit: function(){
            _disable_filter_buttons();
            jQuery(_reset_form_selector).submit();
        }
    };
    return mod;
})();

jQuery(function() {
    yukon.ami.waterLeakReport.init();
});