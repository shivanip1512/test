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
        _waiting_to_finish_N_before_doing_counts: 0,
        
        _DEFAULT_NO_STATE_VALUE:            "null",
        _DEFAULT_NO_STATE_GROUP_VALUE:      "null",
        
        _deviceViolationEnum_point:         "POINT",
        
        _monitor_update_btn:                ".f_update_monitor",
        _monitor_btns_to_disable:           ".pageActionArea button, .ui-dialog-buttonset button",
        _monitor_delete_form:               ".f_delete_form",
        _monitor_delete_btn:                "button.f_delete_btn",
        _undo_row_links:                    ".undoRemoveBtn, .removeBtn",
        _supported_count_details:           ".f_details",
        _device_group_count:                ".f_device_group_count",
        _supported_count:                   ".f_supported_counts",
        _supported_count_details_list:      ".f_missing_list",
        _monitor_toggle_form:               ".f_toggle_enabled_form",
        _monitor_toggle_btn:                ".f_toggle_enabled",
        _processors_table_selector:         ".f_processors_table",
        _processors_table_body:             ".f_processors_table tbody",
        _processors_table_new_row_model:    ".f_processors_table .f_new_row_model",
        _monitor_form:                      "#monitor",
        _name_selector:                     "#name",
        _attribute_selector:	            ".f_attribute",
        _attribute_and_state_group:         ".f_attribute, .f_state_group",
        _device_group_selector:             "#groupName",
        _state_group_selector:              ".f_state_group",
        _processor_add_btn_selector:        ".f_add_processor",
        _violations_loading_selector:       ".f_violations_loading",
        _violations_links_selector:         ".f_violation_report_links",
        _new_row_model_class:               "f_new_row_model",
        _supported_details_trigger_class:   "f_details_trigger",
        _processor_class:                   "processor",
        _processor_selector:                ".processor",
        _add_point_selector:                ".add",
        _refresh_violations_selector:       "#refreshViolationsAfterAddingPoint",

        _missing_field_selector:            ".f_showProblem",
        _missing_field_help_selector:       ".f_showViolationHelp",

        _btn_create_update:                 ".pageActionArea button.f_update_monitor",

        // supported count "missing" text
        _missing_or_no_group_text:       ".f_missing_or_no_stategroup_text",
        _are_missing_points_text:        ".f_are_missing_points_text",
        _view_details_text:              ".f_view_details_text",
        _add_points_text:                ".f_add_points_text",

        // "are you sure" update dialog
        _update_dialog_ids:              "#update_loading_dialog, #update_missing_dialog",
        _update_or_create_event:         "e_ddm_update_or_create",
        _count_status:                   "data-count-status",
        _count_status_loading:           "loading",
        _count_status_missing:           "missing",
        _count_status_none_missing:      "none_missing",
        
        // ajax urls
        _url_device_group_count:             "getDeviceGroupCount",
        _url_supported_counts_by_id:         "getSupportedCountsById",
        _url_supported_counts_by_monitor:    "getSupportedCountsByMonitor",
        _url_state_groups:                   "getStateGroupsForAttribute",
        _url_states:                         "getStatesForGroup",
        _url_to_display_devices_in_violation:"getDeviceListInViolation",
        _url_to_add_points_to_display_devices:"forwardToAddPoints",

        init: function(){
            if(this._initialized) return;

            jQuery(this._monitor_form).on("change", this._device_group_selector, this._update_state_groups);
            jQuery(this._monitor_form).on("click", this._missing_field_selector, this._display_violation_device_list);
            jQuery(document).on("click", this._missing_field_help_selector, this._display_local_help);
            jQuery(document).on("click", '.f_showTooManyDevicesForPointHelp', this._display_local_help);
            jQuery(this._monitor_form).on("click", this._add_point_selector, this._display_violation_refresh);
            jQuery(this._monitor_form).on("click", this._refresh_violations_selector, this._do_violation_refresh);

            jQuery(this._processors_table_selector).on("change", this._attribute_selector, this._attribute_changed);
            jQuery(this._processors_table_selector).on("change", this._state_group_selector, this._state_group_changed);
            jQuery(this._processor_add_btn_selector).on("click", this._processor_add);
            jQuery(this._monitor_update_btn).on("click", this._show_update_dialog);
            jQuery(this._monitor_toggle_btn).on("click", this._monitor_toggle_enabled);
            jQuery(this._monitor_delete_btn).on("click", this._monitor_delete);
            jQuery(document).on(this._update_or_create_event, this._monitor_update_or_create);
            jQuery(document).on("click", "." + this._supported_details_trigger_class, this._supported_details_toggle);
            jQuery(document).on("click", this._undo_row_links, this._get_supported_counts);

            this._get_supported_counts({initial_load: true});

            if (jQuery(this._violations_loading_selector).length > 0) {
                this._get_violations_count();
            }

            // add our help icons next to the two column titles (Settings and Processors)
            jQuery('.f_settings_section .titleBar h3').addClass('labeled_icon_right icon_help f_open_settings_help');
            jQuery('.f_processors_section .titleBar h3').addClass('labeled_icon_right icon_help f_open_processors_help');

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
            DeviceDataMonitor._waiting_to_finish_N_before_doing_counts = 0;
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
                    if( data.count > 0 )
                    	jQuery('#monitor .f_add_processor').removeAttr("disabled");
                    else
                    	jQuery('#monitor .f_add_processor').attr("disabled", "disabled");
                }
            });
            
            var procs = jQuery('tr.processor .f_attribute');
            if(procs.length == 0)                       // if no processors:
                DeviceDataMonitor._get_supported_counts();
            else {										// else: refresh ALL state groups
                DeviceDataMonitor._waiting_to_finish_N_before_doing_counts = procs.length;
                for( var pp=0; pp < procs.length; pp++)
                    procs[pp].trigger('change');
            }
        },
        
        _get_supported_counts: function(params) {
            if(--DeviceDataMonitor._waiting_to_finish_N_before_doing_counts > 0)
                return;
            DeviceDataMonitor._hide_counts_and_show_loading();

            if (typeof params === 'undefined' || typeof params.initial_load === 'undefined' || !params.initial_load) {
                jQuery('.f_supported_devices_count').closest('td').flashColor({
                    color    : "#DAE2FF",
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
            var doValidate = false;
            if (jQuery(DeviceDataMonitor._monitor_form).length === 0) {
                // view.jsp
                url = DeviceDataMonitor._url_supported_counts_by_id;
                data = {'monitorId': jQuery('#monitorId').val()};
            } else {
                // edit.jsp
                url = DeviceDataMonitor._url_supported_counts_by_monitor;
                data = jQuery(DeviceDataMonitor._monitor_form).serialize();
                doValidate = true;
            }
            DeviceDataMonitor._supported_counts_xhr = jQuery.ajax({
                url: url,
                data: data,
                success: function(data) {
                    DeviceDataMonitor._get_supported_counts_success(data, '.f_supported_devices_count');
                    if(doValidate)
                    	DeviceDataMonitor._validate_processors();
                }})
            ;
        },

        _str_equal: function( str1, str2 ) {
            if( str1 == undefined && str2 == undefined)		return true;
            if( str1 == undefined || str2 == undefined)		return false;
            return str1.valueOf() == str2.valueOf();
        },
        _check_row_type_is_point: function(itemType) {
            return DeviceDataMonitor._str_equal(itemType,DeviceDataMonitor._deviceViolationEnum_point);
        },

        _get_supported_counts_success: function(data, counts_selector) {
            // clear out our data
            var countSel = counts_selector + ' ' + DeviceDataMonitor._supported_count;
            jQuery(countSel).empty().show();
            jQuery(counts_selector + ' '
                    + DeviceDataMonitor._supported_count_details + ' '
                    + DeviceDataMonitor._supported_count_details_list).empty();
            jQuery('.'+ DeviceDataMonitor._count_status_loading).hide();

            var addStyle = data.totalMissingCount === 0 ? '': 'border-bottom: 1px dotted #ccc;';
            var helpDivId = 'totalSupportedHelpId';
            jQuery(countSel).html("<div style='"+ addStyle +"padding: 3px 0;'>"+ data.totalSupportedCountMessage 
                    +"<a href='javascript:void(0);' class='icon_help f_showViolationHelp' target-id='"+ helpDivId +"' target-title='"+ data.totalSupportedCountHelpTitle +"'>&nbsp;</a>"
                    +"<div class='dn' id='"+ helpDivId +"'>"+ data.totalSupportedCountHelp +"</div></div>" );

            if (data.totalMissingCount === 0) {
                jQuery(DeviceDataMonitor._supported_count).attr(DeviceDataMonitor._count_status, DeviceDataMonitor._count_status_none_missing);
                DeviceDataMonitor._hide_loading_and_show_counts();
                return;
            }

            var ctrl_refresh = jQuery(DeviceDataMonitor._refresh_violations_selector);
            var refresh_key = null;
            if(ctrl_refresh.length > 0) {
                var add_key = ctrl_refresh.attr('data-add-key');
                if(add_key.length < 1) {
                    ctrl_refresh.hide();
                } else {
                    refresh_key = ctrl_refresh.attr('data-add-key');
                    ctrl_refresh.show();
                }
            }
            var same_point_violation_exists = false;

            jQuery(countSel).append(" <table><tbody>\n");
            for (var ii=0; ii < data.missingPointList.length; ii++) {
                var procRowMsgs = data.missingPointList[ii];
                for (var jj=0; jj < procRowMsgs.length; jj++) {
                    var rowId       = procRowMsgs[jj][0];
                    var itemType    = procRowMsgs[jj][1];
//                    var itemId      = procRowMsgs[jj][2];     // Not currently used
                    var itemName    = procRowMsgs[jj][3];
                    var usedLimitQry= procRowMsgs[jj][4];
                    var counts      = procRowMsgs[jj][5];
                    var missingText = procRowMsgs[jj][6];
                    var listTitle   = procRowMsgs[jj][7];
                    var helpTitle   = procRowMsgs[jj][8];
                    var helpText    = procRowMsgs[jj][9];
                    var addPointsTxt= procRowMsgs[jj][10];
                    var style       = (jj + 1 == procRowMsgs.length) ? "border-bottom: 1px dotted #ccc;" : "";
                    var ctrl_attr   = jQuery('[name="processors['+ rowId +'].attribute"]');
                    var attr_id     = ctrl_attr.find(':selected').val();
                    var row         = ctrl_attr.closest('tr');
                    var sg_id       = DeviceDataMonitor._get_state_group_value(row);

                    var url_params    = 'violationType='+ encodeURIComponent(itemType) +'&groupName='+  encodeURIComponent(jQuery('#groupName').val())
                                    +'&attribute='+ encodeURIComponent(attr_id)  +'&stateGroup='+ encodeURIComponent(sg_id);

                    var the_list_url = DeviceDataMonitor._url_to_display_devices_in_violation +'?'+ url_params;
                    var trId        = "violationRow"+ itemType + rowId;
                    var output      = "<tr style='"+ style +"padding: 3px 0;' id='"+ trId +"'>";
                    var popupListId = "violationListPopup"+ itemType + rowId;
                    var popupHelpId = "violationHelpPopup"+ itemType + rowId;
                    var countText   = ""+ counts;
                    var postHref    = '';
                    // If the device list is too large, show a warning instead of inaccurate numbers:
                    if(usedLimitQry && DeviceDataMonitor._str_equal(""+ usedLimitQry, "true")) {
                        countText = '';
                        var pt_help_title = jQuery("#pointUnknownNumberHelp").attr('target-title');
                        postHref = '<a class="warning f_showTooManyDevicesForPointHelp" href="javascript:void(0);" target-id="pointUnknownNumberHelp" target-title="'+ pt_help_title +'">&nbsp;</span>';
                    }
                    output += "<td><a class='labeled_icon magnifier error f_showProblem' href='javascript:void(0);' data-url='" + the_list_url + "' target-id='"+ popupListId +"' target-title='"+ listTitle +"'>" + countText + "</a><span class='labeled_icon loading'>"+ countText +"</span>"+ postHref +"<div class='f_problems_container dn' id='"+ popupListId +"'/></td>";
                    output += "<td>"+ missingText +"</td><td>"+ itemName +"</td>\n";
                    if(DeviceDataMonitor._check_row_type_is_point(itemType)) {
                        var add_points_url = DeviceDataMonitor._url_to_add_points_to_display_devices +'?'+ url_params;
                        output += "<td class='fr' style='white-space:nowrap;padding-left:0.5em;'><a href='"+ add_points_url +"' class='labeled_icon add' style='margin-top: 2px;' target='_blank' data-add-key='"+ encodeURIComponent(attr_id) +"'>"+ addPointsTxt +"</a></td>";
                        if( refresh_key != null && DeviceDataMonitor._str_equal(attr_id, refresh_key))
                            same_point_violation_exists = true;
                    } else
                        output += "<td>&nbsp;</td>\n";
                    jQuery(countSel).append(output +"<td><a href='javascript:void(0);' class='icon_help f_showViolationHelp' target-id='"+ popupHelpId +"' target-title='"+ helpTitle +"'>&nbsp;</a><div class='dn' id='"+ popupHelpId +"'>"+ helpText +"</div></td></td></tr>\n");
                    
                }
            }
            jQuery(countSel).append("</tbody></table>\n");
            
            if(! same_point_violation_exists) {
                ctrl_refresh.attr('data-add-key', '');
                ctrl_refresh.hide();
            }

            jQuery(DeviceDataMonitor._supported_count).attr(DeviceDataMonitor._count_status, DeviceDataMonitor._count_status_missing);
            DeviceDataMonitor._hide_loading_and_show_counts();
        },  // ENDS _get_supported_counts_success()

        _display_violation_refresh: function(event) {
            jQuery(DeviceDataMonitor._refresh_violations_selector).show();
            var key = jQuery(event.target).attr('data-add-key');
            jQuery(DeviceDataMonitor._refresh_violations_selector).attr('data-add-key', key);
        },

        _do_violation_refresh: function(event) {
            jQuery(DeviceDataMonitor._refresh_violations_selector).hide();
            DeviceDataMonitor._get_supported_counts();
        },

        _display_violation_device_list: function(event) {
            var problemAnchor = jQuery(event.currentTarget);
            problemAnchor.next().show();
            problemAnchor.hide();
            jQuery.ajax({
                url: problemAnchor.attr('data-url'),
                success: function(transport) {
                    var problemListContainer = jQuery('#'+ problemAnchor.attr('target-id'));
                    problemListContainer.html(transport);
                    problemListContainer.dialog({width: "auto", minWidth: 500, height: 500, modal: true, title: problemAnchor.attr('target-title')});
                    problemAnchor.next().hide();
                    problemAnchor.show();
                }
            });
        },

        _display_local_help: function(event) {
            var problemAnchor = jQuery(event.currentTarget);
            var problemHelpContainer = jQuery('#'+ problemAnchor.attr('target-id'));
            problemHelpContainer.dialog({width: 500, minWidth: 500, title: problemAnchor.attr('target-title'), modal: true});
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

        _get_proc_row_id_from_elem_name: function(jqueryElement) {
            var str     = jqueryElement.attr('name');
            var iStart  = str.indexOf("[")+1;
            var iEnd    = str.indexOf("]");
            var sInt    = str.substring(iStart,iEnd);
            var iInt    = parseInt(sInt, 10);
            return iInt;
        },

        _processor_add: function(e) {
            DeviceDataMonitor._waiting_to_finish_N_before_doing_counts = 0;
            var template_row = jQuery(DeviceDataMonitor._processors_table_new_row_model);
            var new_row      = template_row.clone();
            var new_undo_row = template_row.next('tr').clone();
            new_row.removeClass(DeviceDataMonitor._new_row_model_class);
            new_row.addClass(DeviceDataMonitor._processor_class);

            var last_index;
            if (jQuery('.f_processors_table tbody tr').length === 2) {
                last_index = 0;
                jQuery('.noItemsMessage').remove();
            } else {
                last_index  = 1+ DeviceDataMonitor._get_proc_row_id_from_elem_name(jQuery('.f_processors_table tbody tr select:last'));
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
            new_row.find('select')[0].selectedIndex = 0;	//last_row_selects[0].selectedIndex;

            jQuery(DeviceDataMonitor._processors_table_body).append(new_row);
            jQuery(DeviceDataMonitor._processors_table_body).append(new_undo_row);
            new_row.show();

            // focus our new row
            new_row.find('select').first().focus();
        },  // ENDS _processor_add

        _state_group_changed: function(event) {
            DeviceDataMonitor._state_group_changed_worker(jQuery(event.target));
        },

        /**
         * When a State Group control is changed, this will properly update the related States control.
         * 
         * Pre-req: if you want to do a single update to counts for multiple changes, do:
         *             DeviceDataMonitor._waiting_to_finish_N_before_doing_counts = n;
         * @param jqueryObj
         */
        _state_group_changed_worker: function(jqueryObj) {
            var row = jqueryObj.closest('tr');
            var state_group_id = DeviceDataMonitor._get_state_group_value(row);
            if( state_group_id.length < 1 ) {
                DeviceDataMonitor._get_supported_counts();
                return;
            }
            jQuery.ajax({
                url: this._url_states,
                data: {'stateGroupId': state_group_id},
                success: function(data) {
                    var DOM_states = row.find('.f_states');	// Div or control?
                    var ctrl_name = DOM_states.attr('name') != undefined ? DOM_states.attr('name') : DOM_states.find(':input').attr('name'); 
                    if (data.states.length > 1) {
                        DOM_states.replaceWith('<select class="f_states" name="'+ ctrl_name +'"></select>');
                        var state_select = row.find('.f_states');
                        for (var ii=0; ii < data.states.length; ii++) {
                            state_select.append('<option value="'+state_group_id+':'+data.states[ii].id+'">'+data.states[ii].text+'</option>');
                        }
                    } else {
                        if (data.states.length == 0) {
                            DeviceDataMonitor._blankout_states(row);
	                    } else if (data.states.length == 1) {
	                        DOM_states.replaceWith('<div class="f_states"><input type="hidden" name="'+ ctrl_name +'" value="'+ state_group_id+':'+data.states[0].id+'">'+ data.states[0].text +'</div>');
	                    }
                    }
                },
                complete: function() {
                    DeviceDataMonitor._get_supported_counts();
                }
            });
        }, // ENDS _state_group_changed_worker

        _blankout_states : function( containingRow ) {
            var select_state    = containingRow.find('select.f_states');
            var input_state     = containingRow.find('.f_states input');
            var ctrl_name       = select_state.length > 0 ? select_state.attr("name") : input_state.attr("name");
            var str             = '<div class="f_states"><input type="hidden" name="'+ ctrl_name +'" value="'+ this._DEFAULT_NO_STATE_VALUE +'">'+ jQuery('#str_na').text() +'</div>';
            containingRow.find('.f_states').replaceWith(str);
        },

        _attribute_changed: function(event) {
            DeviceDataMonitor._update_state_groups_worker(event);
        },
        _update_state_groups: function(event) {
            DeviceDataMonitor._update_state_groups_worker(event);
        },
        
        /**
         * This uses the Attribute control to update the related State Group.
         * @param event
         */
        _update_state_groups_worker: function(event) {
            var row         = jQuery(event.target).closest('tr');
            var attr_val    = row.find('select.f_attribute').find(":selected").val();
            var DOM_stategroups= row.find('.f_state_group');
            var select_sg   = row.find('select.f_state_group');
            var input_sg    = row.find('.f_state_group input');
            var ctrl_name   = select_sg.length > 0 ? select_sg.attr("name") : input_sg.attr("name");
            DOM_stategroups.hide();
            row.find('.f_states').hide();
            var DOM_feedback = jQuery("#canonicalCalculatingSpan").clone();
            DOM_feedback.removeAttr('id');
            jQuery(DOM_stategroups.closest('td')).append(DOM_feedback);
            DOM_feedback.show().flashColor({
                color : "#DAE2FF",
                duration : 3000
            });

            var row_id	= DeviceDataMonitor._get_proc_row_id_from_elem_name(row.find('select.f_attribute'));
            if (DeviceDataMonitor._get_state_groups_xhr && DeviceDataMonitor._get_state_groups_xhr[row_id] && DeviceDataMonitor._supported_counts_xhr[row_id] !== 'undefined') {
                DeviceDataMonitor._supported_counts_xhr[row_id].abort();
            }
            DeviceDataMonitor._supported_counts_xhr[row_id] = jQuery.ajax({
                url: this._url_state_groups,
                data: {'attributeKey': attr_val, 'groupName': jQuery('#groupName').val()},
                success: function(data) {
                    DOM_feedback.remove();
                    if (data.stateGroups.length > 1) {
                        DOM_stategroups.replaceWith('<select class="f_state_group" name="'+ ctrl_name +'"></select>');
                        DOM_stategroups = row.find('select.f_state_group');
                        for (var ii=0; ii < data.stateGroups.length; ii++) {
                            DOM_stategroups.append('<option value="'+data.stateGroups[ii].id+'">'+data.stateGroups[ii].name+'</option>');
                        }
                    } else {
                        var str = '<div class="f_state_group"><input type="hidden" name="'+ ctrl_name +'"';
                        if (data.stateGroups.length == 0) {
                            DOM_stategroups.replaceWith(str +' value="">'+ jQuery('#str_na').text() +'</div>');
	                    } else if (data.stateGroups.length == 1) {
	                        var fullString = str +' value="'+ data.stateGroups[0].id +'">'+ data.stateGroups[0].name +'</div>';
	                        DOM_stategroups.replaceWith(fullString);
	                    }
                    	DOM_stategroups = row.find('.f_state_group input');
                    }
                    DOM_stategroups.trigger('change');
                    if (data.stateGroups.length == 0)
                        DeviceDataMonitor._blankout_states(row);
                }
            });
            
            var attributeIsUNSELECTED = attr_val == '-1';
            if( ! attributeIsUNSELECTED ) {
                row.find('.f_attribute option[value="-1"]').remove();
            }
        }, // ENDS _update_state_groups_worker

        _get_state_group_value: function(row) {
            var state_group_select  = row.find('select.f_state_group :eq(0)');
            var state_group_input   = row.find('.f_state_group input :eq(0)');
            var state_group_id      = state_group_select.length > 0 ? state_group_select.find(":selected").val() : state_group_input.val();
            return state_group_id;
        },

        _validate_processors: function() {
            var missingCount = 0;
            var procs = [jQuery(DeviceDataMonitor._processor_selector)];
            procs.each(function(row){
                if( DeviceDataMonitor._get_state_group_value(row).length < 1 )
                    missingCount++;
            });

            if(missingCount > 0 || procs.length < 1)
                jQuery(DeviceDataMonitor._btn_create_update).attr('disabled','disabled');
            else
                jQuery(DeviceDataMonitor._btn_create_update).removeAttr('disabled');
        }
	};
}
jQuery(function() {
    DeviceDataMonitor.init();
});