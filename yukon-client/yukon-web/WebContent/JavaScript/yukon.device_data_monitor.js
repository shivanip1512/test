if(typeof(DeviceDataMonitor) === 'undefined'){
    /**
     * Singleton that manages the javascript in the Device Data Monitor
     * 
     * @class Device Data Monitor javascript
     * @author <a href="mailto:alex.delegard@cooperindustries.com">Alex Delegard</a>
     * @requires jQuery 1.6+
     */
    DeviceDataMonitor = {
        _initialized: false,
        _monitor_form:                   ".f_monitor_form",
        _monitor_update_btn:             ".f_update_monitor",
        _monitor_btns_to_disable:        ".pageActionArea button, .ui-dialog-buttonset button",
        _monitor_delete_form:            ".f_delete_form",
        _monitor_delete_btn:             "button.f_delete_btn",
        _undo_row_links:                 ".undoRemoveBtn, .removeBtn",
        _supported_count_details:        ".f_details",
        _device_group_count:             ".f_device_group_count",
        _supported_count:                ".f_supported_counts",
        _supported_count_details_list:   ".f_missing_list",
        _monitor_toggle_form:            ".f_toggle_enabled_form",
        _monitor_toggle_btn:             ".f_toggle_enabled",
        _processors_table_selector:      ".f_processors_table",
        _processors_table_body:          ".f_processors_table tbody",
        _processors_table_new_row_model: ".f_processors_table .f_new_row_model",
        _attribute_and_state_group:      ".f_attribute, .f_state_group",
        _state_group_selector:           ".f_state_group",
        _processor_add_btn_selector:     ".f_add_processor",
        _violations_loading_selector:    ".f_violations_loading",
        _violations_links_selector:      ".f_violation_report_links",
        _new_row_model_class:            "f_new_row_model",
        _supported_details_trigger_class:"f_details_trigger",
        
        // supported count "missing" text
        _missing_or_no_group_text:       ".f_missing_or_no_stategroup_text",
        _are_missing_points_text:        ".f_are_missing_points_text",
        _view_details_text:              ".f_view_details_text",
        _add_points_text:              ".f_add_points_text",

        // "are you sure" update dialog
        _update_dialog_ids:              "#update_loading_dialog, #update_missing_dialog",
        _update_or_create_event:         "e_ddm_update_or_create",
        _count_status:                   "data-count-status",
        _count_status_loading:           "loading",
        _count_status_missing:           "missing",
        _count_status_none_missing:      "none_missing",
        
        // ajax urls
        _url_device_group_count:         "getDeviceGroupCount",
        _url_supported_counts_by_id:     "getSupportedCountsById",
        _url_supported_counts_by_monitor:"getSupportedCountsByMonitor",

        init: function(){
            if(this._initialized) return;

            jQuery(this._processors_table_selector).on("change", this._state_group_selector, this._state_group_changed);
            jQuery(this._processor_add_btn_selector).on("click", this._processor_add);
            jQuery(this._monitor_update_btn).on("click", this._show_update_dialog);
            jQuery(this._monitor_toggle_btn).on("click", this._monitor_toggle_enabled);
            jQuery(this._monitor_delete_btn).on("click", this._monitor_delete);
            jQuery(this._processors_table_selector).on("change", this._attribute_and_state_group, this._get_supported_counts);
            jQuery(document).on(this._update_or_create_event, this._monitor_update_or_create);
            jQuery(document).on("click", "." + this._supported_details_trigger_class, this._supported_details_toggle);
            jQuery(document).on("click", this._undo_row_links, this._get_supported_counts);

            this._get_supported_counts({initial_load: true});
            
            if (jQuery(this._violations_loading_selector).length > 0) {
                this._get_violations_count();
            }

            // add our help icons next to the two column titles (Settings and Processors)
            jQuery('.f_settings_section .formElementContainer_title h3').addClass('labeled_icon_right icon_help f_open_settings_help');
            jQuery('.f_processors_section .sectionContainer_title h3').addClass('labeled_icon_right icon_help f_open_processors_help');

            this._initialized = true;
        },

        _get_violations_count: function() {
            jQuery.ajax({
                url: 'getViolationsCount',
                data: {'monitorId': jQuery('#monitorId').val()},
                dataType: 'json',
                success: function(data) {
                    clearInterval(DeviceDataMonitor._violations_interval_id);
                    if (data.status === 'working') {
                        DeviceDataMonitor._violations_interval_id = setInterval(DeviceDataMonitor._get_violations_count, 3000);
                    } else {
                        var violations_container = jQuery(DeviceDataMonitor._violations_loading_selector).closest('span');
                        violations_container.html(data.count);
                        
                        if (data.count === 0) return;
                        var report_links = jQuery(DeviceDataMonitor._violations_links_selector).clone();
                        report_links.removeClass("dn");
                        violations_container.append(report_links);
                        return;
                    }
                }
            });
        },
        
        _show_update_dialog: function() {
            var count_status = jQuery(DeviceDataMonitor._supported_count).attr(DeviceDataMonitor._count_status);
            jQuery(DeviceDataMonitor._update_dialog_ids).find(".userMessage").remove();
            if (count_status === DeviceDataMonitor._count_status_loading) {
                open_update_loading_dialog();
            } else if (count_status === DeviceDataMonitor._count_status_missing) {
                open_update_missing_dialog();
            } else if (count_status === DeviceDataMonitor._count_status_none_missing) {
                // sweet, let's just update this thing
                DeviceDataMonitor._monitor_update_or_create();
            }
        },
        
        _supported_details_toggle: function(e) {
            var opts = {width: 500, height: "auto"};
            jQuery(DeviceDataMonitor._supported_count_details).dialog(opts);
        },
        
        _device_group_changed: function() {
            // show the loading element and hide the count
            jQuery(DeviceDataMonitor._device_group_count).prev().show();
            jQuery(DeviceDataMonitor._device_group_count).hide();
            var groupName = jQuery('#groupName').val();

            // check to see if we have a pending request that we need to cancel
            if (DeviceDataMonitor._group_count_xhr && DeviceDataMonitor._group_count_xhr !== 'undefined') {
                DeviceDataMonitor._group_count_xhr.abort();
            }
            DeviceDataMonitor._group_count_xhr = jQuery.ajax({
                url: DeviceDataMonitor._url_device_group_count,
                data: {'groupName': groupName},
                success: function(data) {
                    jQuery(DeviceDataMonitor._device_group_count).text(data.count);
                    jQuery(DeviceDataMonitor._device_group_count).prev().hide();
                    jQuery(DeviceDataMonitor._device_group_count).show();
                }
            });
            DeviceDataMonitor._get_supported_counts();
            
            jQuery(".f_add_points_btn").attr('href', '/bulk/addPoints/home?collectionType=group&group.name=' + encodeURIComponent(groupName));
        },
        
        _get_supported_counts: function(params) {
            DeviceDataMonitor._hide_counts_and_show_loading();
            
            if (typeof params === 'undefined' || typeof params.initial_load === 'undefined' || !params.initial_load) {
                jQuery('.f_supported_devices_count').closest('td').flashColor({
                    color : "#DAE2FF",
                    duration : 3000
                });
            }

            jQuery(DeviceDataMonitor._supported_count).attr(DeviceDataMonitor._count_status, DeviceDataMonitor._count_status_loading);
            
            // check to see if we have a pending request that we need to cancel
            if (DeviceDataMonitor._supported_counts_xhr && DeviceDataMonitor._supported_counts_xhr !== 'undefined') {
                DeviceDataMonitor._supported_counts_xhr.abort();
            }
            
            var data;
            var url;
            if (jQuery(DeviceDataMonitor._monitor_form).length === 0) {
                // view.jsp
                url = DeviceDataMonitor._url_supported_counts_by_id;
                data = {'monitorId': jQuery('#monitorId').val()};
            } else {
                // edit.jsp
                url = DeviceDataMonitor._url_supported_counts_by_monitor;
                data = jQuery(DeviceDataMonitor._monitor_form).serialize();
            }
            DeviceDataMonitor._supported_counts_xhr = jQuery.ajax({
                url: url,
                data: data,
                success: function(data) {
                    DeviceDataMonitor._get_supported_counts_success(data, '.f_supported_devices_count');
                }
            });
        },

        _get_supported_counts_success: function(data, counts_selector) {
            // clear out our data
            jQuery(counts_selector + ' ' + DeviceDataMonitor._supported_count).empty();
            jQuery(counts_selector + ' ' + 
                    + DeviceDataMonitor._supported_count_details + ' '
                    + DeviceDataMonitor._supported_count_details_list).empty();
            
            jQuery(counts_selector + ' ' + DeviceDataMonitor._supported_count).html(data.totalSupportedCount);
            
            if (data.totalMissingCount === 0) {
                jQuery(DeviceDataMonitor._supported_count).attr(DeviceDataMonitor._count_status, DeviceDataMonitor._count_status_none_missing);
                DeviceDataMonitor._hide_loading_and_show_counts();
                return;
            }
            
            var missing_ul = '<ul class="disc" style="margin: 7px 0;">';
            for (var i=0; i< data.missingPointList.length; i++) {
                missing_ul += '<li>' + data.missingPointList[i] + '</li>';
            }
            missing_ul += '</ul>';
            
            jQuery(counts_selector + ' ' + DeviceDataMonitor._supported_count).append(
                    ' (<span class="errorMessage">' + data.totalMissingCount + '</span> ' + jQuery(DeviceDataMonitor._are_missing_points_text).val() + ' <a class="'
                    + DeviceDataMonitor._supported_details_trigger_class
                    + '" href="javascript:void(0);">' + jQuery(DeviceDataMonitor._view_details_text).val() + '</a>)');
            jQuery(counts_selector + ' ' + DeviceDataMonitor._supported_count_details_list).html(missing_ul);
            
            jQuery(DeviceDataMonitor._supported_count).attr(DeviceDataMonitor._count_status, DeviceDataMonitor._count_status_missing);
            DeviceDataMonitor._hide_loading_and_show_counts();
        },
        
        _hide_counts_and_show_loading: function() {
            jQuery('.f_supported_devices_count ' + DeviceDataMonitor._supported_count_details).hide();
            jQuery('.f_supported_devices_count ' + DeviceDataMonitor._supported_count).hide();
            jQuery('.f_supported_devices_count .loading').show();
        },
        
        _hide_loading_and_show_counts: function() {
            jQuery('.f_supported_devices_count .loading').hide();
            jQuery('.f_supported_devices_count ' + DeviceDataMonitor._supported_count).show();
        },
        
        _monitor_update_or_create: function() {
            DeviceDataMonitor._disable_btns();
            jQuery(DeviceDataMonitor._monitor_form).submit();
        },

        _monitor_toggle_enabled: function() {
            DeviceDataMonitor._disable_btns();
            jQuery(DeviceDataMonitor._monitor_toggle_form).submit();
        },
        
        _monitor_delete: function() {
            DeviceDataMonitor._disable_btns();
            jQuery(DeviceDataMonitor._monitor_delete_form).submit();
        },
        
        _disable_btns: function() {
            jQuery(DeviceDataMonitor._monitor_btns_to_disable).attr("disabled", "disabled");            
        },
        
        _processor_add: function(e) {
            var new_row = jQuery(DeviceDataMonitor._processors_table_new_row_model).clone();
            var new_undo_row = jQuery(DeviceDataMonitor._processors_table_new_row_model).next('tr').clone();
            new_row.removeClass(DeviceDataMonitor._new_row_model_class);

            var last_index;
            if (jQuery('.f_processors_table tbody tr').length === 2) {
                last_index = 0;
                jQuery('.noItemsMessage').remove();
            } else {
                last_index = jQuery('.f_processors_table tbody tr select:last').attr('name').substring(11,12);
                last_index = parseInt(last_index, 10);
                last_index += 1;
            }

            new_row.find('[data-name]').each(function() {
                var select = jQuery(this);
                select.attr('name', select.attr('data-name'));
                select.removeAttr('data-name');
                
                /* increment the index
                 * i.e. change it from this:
                 *          "processors[20].attribute"
                 * to this: "processors[21].attribute" */
                var name = select.attr('name');
                var new_name = name.substr(0, 11) + last_index + name.substr(12, name.length);
                select.attr('name', new_name);
            });
            
            // set our new row's attribute select to that of the last row in the table
            var last_row_selects = jQuery(DeviceDataMonitor._processors_table_selector + ' tr:last').prev().find('select');
            new_row.find('select')[0].selectedIndex = last_row_selects[0].selectedIndex;

            jQuery(DeviceDataMonitor._processors_table_body).append(new_row);
            jQuery(DeviceDataMonitor._processors_table_body).append(new_undo_row);
            new_row.show();
            
            // focus our new row
            new_row.find('select').first().focus();
            
            // trigger a recalculation of our supported count
            DeviceDataMonitor._get_supported_counts();
        },
        
        _state_group_changed: function(e) {
            var state_group_id = jQuery(e.target).find(':selected').val();
            jQuery.ajax({
                url: 'getStatesForGroup',
                data: {'stateGroupId': state_group_id},
                success: function(data) {
                    var row_states = jQuery(e.target).closest('tr').find('.f_states');
                    if (data.states.length > 0) {
                        row_states.empty();
                    }
                    for (var i=0; i < data.states.length; i++) {
                        row_states.append('<option value="'+state_group_id+':'+data.states[i].id+'">'+data.states[i].text+'</option>');
                    }
                }
            });
        }
	};
}
jQuery(function() {
    DeviceDataMonitor.init();
});