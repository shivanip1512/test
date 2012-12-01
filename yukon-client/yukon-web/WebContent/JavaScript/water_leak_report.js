if(typeof(WaterLeakReport) === 'undefined'){
    /**
     * Singleton that manages the javascript in the Water Leak Report
     * 
     * @class Water Leak Report javascript
     * @author <a href="mailto:alex.delegard@cooperindustries.com">Alex Delegard</a>
     * @requires jQuery 1.6+
     */
    WaterLeakReport = {
        _interval_data_form_selector: "form#intervalDataForm",
        _interval_data_form_btn_selector: "form#intervalDataForm button",
        _filter_form_selector: "form#filterForm",
        _reset_form_selector: "form#resetForm",
        _csv_leak_form_selector: "form#csvLeakForm",
        _csv_interval_form_selector: "form#csvIntervalForm",
        _leak_filter_dialog: "#leakFilterDialog",
        _form_input_name: "selectedPaoIds",
        _all_selector: "input#f_check_all:checkbox",
        _single_selector: "tbody tr input.f_check_single:checkbox",
        _initialized: false,
        _f_cis_details_store: [],
        _f_filter_values: [],

        /* -------------- */
        /* public methods */
        /* -------------- */

        init: function(){
            if(!this._initialized){
                /* report.jsp */
                jQuery(WaterLeakReport._all_selector).checkAll(this._single_selector);
                jQuery(WaterLeakReport._all_selector).bind("change", this._all_checked);
                jQuery(WaterLeakReport._single_selector).bind("change", this._single_checked);
                jQuery(WaterLeakReport._leak_filter_dialog).bind("keyup", this._init_filter_key_bindings);
                jQuery(document).on("dialogopen", WaterLeakReport._leak_filter_dialog, this._filter_dialog_open);
                jQuery(document).bind("keyup", this._init_open_filter_key_binding);
                jQuery("a.f_cis_details").bind("click", this._view_cis_details);
                jQuery(".f_open_filter_dialog").bind("click", function(){Yukon.ui._autofocus();});
                jQuery("#exportLeakCsv").bind("click", this._export_leak_csv);
                this._init_filter();
                this._store_filter_values();
                this._init_interval_data_form();
                
                /* report.jsp filter labels */
                jQuery(".filter_container .f_filter_group_clicked").bind("click", this._filter_group_clicked);
                jQuery(".filter_container .f_filter_individual_clicked").bind("click", this._filter_individual_clicked);
                jQuery(".filter_container .f_filter_date_range .f_filter_to_date_clicked").bind("click", this._filter_to_date_clicked);
                jQuery(".filter_container .f_filter_date_range").bind("click", this._filter_date_range_clicked);
                jQuery(".filter_container .f_filter_threshold_clicked").bind("click", this._filter_threshold_clicked);
                jQuery(".filter_container .f_disabled_devices .f_filter_reset_disabled_devices_clicked").bind("click", this._filter_reset_disabled_devices_clicked);
                jQuery(".filter_container .f_disabled_devices").bind("click", this._filter_disabled_devices_clicked);
                jQuery(".filter_container .f_reset_filter_submit").bind("click", this.reset_filter_submit);

                /* intervalData.jsp */
                jQuery("#exportIntervalCsv").bind("click", this._export_interval_csv);

                this._initialized = true;
            }
        },
        
        group_selected_callback: function() {
            WaterLeakReport._set_filter_form_hidden_input("collectionType", "group");
        },
        
        individual_selected_callback: function() {
            WaterLeakReport._set_filter_form_hidden_input("collectionType", "idList");
        },
        
        filter_submit: function(){
            WaterLeakReport._disable_filter_buttons();
            jQuery(WaterLeakReport._filter_form_selector).submit();
        },
        
        reset_filter_submit: function(){
            WaterLeakReport._disable_filter_buttons();
            jQuery(WaterLeakReport._reset_form_selector).submit();
        },
        
        /* --------------- */
        /* private methods */
        /* --------------- */
        
        _init_filter: function() {
            if (jQuery("input#hasFilterError").val() === "true") {
                open_leakFilterDialog();
            }
        },
        
        _init_filter_key_bindings: function(e) {
            if (e.keyCode == 13) { // enter
                WaterLeakReport.filter_submit();
            }
        },
        
        _init_open_filter_key_binding: function(e) {
            var filter_dialog = jQuery("#leakFilterDialog");
            if (e.keyCode == 70 /* f (for "filter") */ &&
                    !jQuery(e.target).is("input") && /* don't open the dialog if the user is typing in an input field */
                    filter_dialog.length === 1 &&
                    !filter_dialog.is(":visible")) {
                open_leakFilterDialog();
            }
        },
        
        _init_interval_data_form: function(){
            jQuery(WaterLeakReport._single_selector + ":checked").each(function() {
                var paoId = WaterLeakReport._get_row_pao_id(this);
                WaterLeakReport._add_to_interval_data_form(paoId);
            });
            WaterLeakReport._set_interval_data_btn_disabled();
        },
        
        _filter_dialog_open: function(e) {
            if (WaterLeakReport._f_filter_values.length > 0) {
                WaterLeakReport._restore_filter_values();
            }
            var filter_text = jQuery("#filter_shortcut_text").val();
            jQuery("button.leakFilterSubmitButton").before('<span class="filter_shortcut">'+filter_text+'</span>');

            //remove the stupid <br> tag that screws up styling for threshold errors
            jQuery(document.getElementById("threshold.errors")).prev("br").remove();

            // if there are filter errors... set the focus to the first one of these
            Yukon.ui.focusFirstError();
        },
        
        _store_filter_values: function() {
            var the_filter = jQuery(WaterLeakReport._filter_form_selector);
            WaterLeakReport._f_filter_values.from_datetime  = the_filter.find("input.f_from_datetime").val();
            WaterLeakReport._f_filter_values.from_hour        = the_filter.find("input.f_from_hour").val();
            WaterLeakReport._f_filter_values.to_datetime    = the_filter.find("input.f_to_datetime").val();
            WaterLeakReport._f_filter_values.to_hour          = the_filter.find("input.f_to_hour").val();
            WaterLeakReport._f_filter_values.threshold        = the_filter.find("input.f_threshold").val();
            WaterLeakReport._f_filter_values.include_disabled = the_filter.find("input.f_include_disabled_paos").is(":checked");
        },
        
        _restore_filter_values: function() {
            var the_filter = jQuery(WaterLeakReport._filter_form_selector);
            the_filter.find("input.f_from_datetime").val(WaterLeakReport._f_filter_values.from_datetime);
            the_filter.find("input.f_from_hour").val(WaterLeakReport._f_filter_values.from_hour);
            the_filter.find("input.f_to_datetime").val(WaterLeakReport._f_filter_values.to_datetime);
            the_filter.find("input.f_to_hour").val(WaterLeakReport._f_filter_values.to_hour);
            the_filter.find("input.f_threshold").val(WaterLeakReport._f_filter_values.threshold);
            the_filter.find("input.f_include_disabled_paos").attr("checked", WaterLeakReport._f_filter_values.include_disabled);
        },
        
        _filter_group_clicked: function() {
            open_leakFilterDialog();
            jQuery("[class^='chooseGroupIcon_deviceGroupNameSelectorTag_']", WaterLeakReport._leak_filter_dialog).trigger("click");
        },
        _filter_individual_clicked: function() {
            open_leakFilterDialog();
            selectDevicesPicker.show();
        },
        _filter_to_date_clicked: function() {
            open_leakFilterDialog();
            jQuery(".f_to_datetime", WaterLeakReport._leak_filter_dialog).focus();
            return false;
        },
        _filter_date_range_clicked: function() {
            open_leakFilterDialog();
            jQuery(".f_from_datetime", WaterLeakReport._leak_filter_dialog).focus();
        },
        _filter_threshold_clicked: function(e) {
            open_leakFilterDialog();
            jQuery(".f_threshold", WaterLeakReport._leak_filter_dialog).focus();
        },
        _filter_reset_disabled_devices_clicked: function() {
            jQuery(".f_include_disabled_paos", WaterLeakReport._leak_filter_dialog).val(false);
            WaterLeakReport.filter_submit();
            return false;
        },
        _filter_disabled_devices_clicked: function() {
            open_leakFilterDialog();
            jQuery("input", WaterLeakReport._leak_filter_dialog).blur();
        },
        
        _set_filter_form_hidden_input: function(name, value) {
            jQuery(WaterLeakReport._filter_form_selector + ' input[name="'+name+'"]').remove();
            jQuery(WaterLeakReport._filter_form_selector).append('<input type="hidden" name="'+name+'" value="'+value+'"/>');
        },
        
        _all_checked: function(){
            if (jQuery(this).is(":checked")) {
                var check_singles = jQuery(this).closest("table").find(WaterLeakReport._single_selector); 
                check_singles.each(function() {
                    var paoId = WaterLeakReport._get_row_pao_id(this);
                    WaterLeakReport._add_to_interval_data_form(paoId);
                });
            } else {
                jQuery(WaterLeakReport._interval_data_form_selector + " input[name='" + WaterLeakReport._form_input_name + "']").remove();
            }
            WaterLeakReport._set_interval_data_btn_disabled();
        },
        
        _single_checked: function(){
            var paoId = WaterLeakReport._get_row_pao_id(this);
            if (jQuery(this).is(':checked')) {
                WaterLeakReport._add_to_interval_data_form(paoId);
            } else {
                WaterLeakReport._remove_from_interval_data_form(paoId);
            }
            WaterLeakReport._set_interval_data_btn_disabled();
        },

        _set_interval_data_btn_disabled: function(){
            var checked = jQuery(WaterLeakReport._single_selector).is(":checked");
            jQuery(WaterLeakReport._interval_data_form_btn_selector).attr("disabled", !checked);
        },

        _export_leak_csv: function(){
            jQuery(WaterLeakReport._csv_leak_form_selector).submit();
        },
        
        _export_interval_csv: function(){
            jQuery(WaterLeakReport._csv_interval_form_selector).submit();
        },
        
        _view_cis_details: function(){
            var paoId = WaterLeakReport._get_row_pao_id(this);
            var in_array = jQuery.inArray(paoId, WaterLeakReport._f_cis_details_store);
            if (in_array === -1) {
                WaterLeakReport._f_cis_details_store.push(paoId);
                // only block the page if we haven't requested this before (will take longer)
                Yukon.ui.blockPage();
            }
            
            var url = WaterLeakReport._get_row_account_info_url(this);
            jQuery('#accountInfoAjaxDialog').show();
            jQuery('#accountInfoAjaxDialog').load(url);
        },
        
        _disable_filter_buttons: function() {
            jQuery(".ui-button", ".ui-dialog-buttonset").attr("disabled", true);
        },
        
        _get_row_pao_id: function(elem){
            return jQuery(elem).closest("tr").find("input.the_pao_id").val();
        },
        
        _get_row_account_info_url: function(elem){
            return jQuery(elem).closest("tr").find("input.account_info_url").val();
        },
        
        _add_to_interval_data_form: function(paoId) {
            if (!jQuery(WaterLeakReport._interval_data_form_selector + " input[name='" + WaterLeakReport._form_input_name + "'][value='" + paoId + "']").length) {
                jQuery(WaterLeakReport._interval_data_form_selector).prepend("<input type='hidden' name='" + WaterLeakReport._form_input_name + "' value='"+paoId+"'>");
            }
        },
        _remove_from_interval_data_form: function(paoId) {
            jQuery(WaterLeakReport._interval_data_form_selector + " input[value='"+paoId+"']").remove();
        }
	};
}
jQuery(function() {
    WaterLeakReport.init();
});