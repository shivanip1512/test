yukon.namespace('yukon.ami.ddm');

/**
 * Module that manages the device data monitor feature
 * @module   yukon.ami.ddm
 * @requires JQUERY
 * @requires JQUERYUI
 */
yukon.ami.ddm = (function () {
    
    var _initialized = false,
        
        _waiting_to_finish_N_before_doing_counts = 0,
        
        /** {String} Reference to setInterval task for violations updates. */
        _violations_interval_id = null,
        
        _supported_counts_xhr = null,
        _group_count_xhr = null,
        
        /** Shows a update dialog based on the count_status. */
        _show_update_dialog = function () {
            var count_status = $('.js-supported-counts').attr('data-count-status');
            $('#update-loading-dialog, #update-missing-dialog').find('.user-message').remove();
            if (count_status === 'loading') {
                window['open_update-loading-dialog']();
            } else if (count_status === 'missing') {
                window['open_update-missing-dialog']();
            } else if (count_status === 'none_missing') {
                // sweet, let's just update this thing
                _monitor_save();
            }
        },
        
        /** 
         * Gets the number of devices within the monitoring device group (and all it's child groups) 
         * that support having all of the assigned processors. 
         */
        _get_supported_counts = function (params) {
            
            if (--_waiting_to_finish_N_before_doing_counts > 0) {
                return;
            }
            
            $('.js-supported-devices-count .js-details').hide();
            $('.js-supported-devices-count .js-supported-counts').hide();
            $('.js-supported-devices-count .js-loading').show();
            
            if (typeof params === 'undefined' || 
                typeof params.initial_load === 'undefined' || 
                !params.initial_load) {
                
                $('.js-supported-devices-count').closest('td')
                .flash({ color: '#dae2ff', duration: 3000 });
            }
            
            $('.js-supported-counts').attr('data-count-status', 'loading');
            
            // Check to see if we have a pending request that we need to cancel
            if (_supported_counts_xhr && _supported_counts_xhr !== 'undefined') {
                _supported_counts_xhr.abort();
            }
            
            var data;
            var url;
            var doValidate = false;
            
            if ($('.js-monitor-form').length === 0) {
                // view.jsp
                url = 'supported-count';
                data = {'monitorId': $('#monitor-id').val()};
            } else {
                // edit.jsp
                url = 'monitor-supported-count';
                data = $('.js-monitor-form').serialize();
                doValidate = true;
            }
            _supported_counts_xhr = $.ajax({
                url: url,
                data: data
            }).done(function (data, textStatus, jqXHR) {
                
                _get_supported_counts_success(data);
                
                if (doValidate) {
                    _validate_processors();
                }
            });
        },
        
        /** Gets supported success counts. 
         * @param {Object} data - Json containing Count related data.
         */
        _get_supported_counts_success = function (data) {
            
            var countSel = '.js-supported-devices-count .js-supported-counts',
                someHtml,
                newTable;
            
            // Clear out our data
            $(countSel).empty().show();
            $('.js-supported-devices-count .js-details .js-missing-list').empty();
            $('.js-loading').hide();
            
            var help = $('<div>').html(data.totalSupportedCountMessage).appendTo(countSel);
            
            $('<a>').attr('href', 'javascript:void(0);')
            .addClass('js-show-violation-help js-violation-help-link')
            .attr('target-id', 'total-supported-help')
            .attr('target-title', data.totalSupportedCountHelpTitle)
            .html('<i class="icon icon-help">&nbsp;</i>')
            .appendTo(help);
            
            $('<div>').addClass('dn')
            .attr('id', 'total-supported-help')
            .text(data.totalSupportedCountHelp)
            .appendTo(help);
            
            if (data.totalMissingCount === 0) {
                
                $('.js-supported-counts').attr('data-count-status', 'none_missing');
                $('.js-supported-devices-count .js-loading').hide();
                $('.js-supported-devices-count .js-supported-counts').show();
                
                return;
            }
            
            var ctrl_refresh = $('#refresh-violations');
            var refresh_key = null;
            
            if (ctrl_refresh.length > 0) {
                
                var add_key = ctrl_refresh.attr('data-add-key');
                
                if (add_key.length < 1) {
                    ctrl_refresh.hide();
                } else {
                    refresh_key = ctrl_refresh.attr('data-add-key');
                    ctrl_refresh.show();
                }
            }
            var same_point_violation_exists = false;
            
            newTable = $(countSel).html("<table><tbody></tbody></table>");
            
            data.missingPointList.forEach(function (violation) {
                
                var attribute = violation.attribute;
                
                var url_params = $.param({
                    violationType: violation.fieldType,
                    groupName: $('.js-monitor-group input').val(),
                    attribute: attribute,
                    stateGroup: violation.stateGroupId
                });
                
                var url = 'violating-devices?' + url_params;
                var output = '<tr id="violation-row-' + violation.fieldType + '-' + violation.rowId + '">';
                var popupListId = 'violation-list-popup-' + violation.fieldType + '-' + violation.rowId;
                var popupHelpId = 'violation-help-popup-' + violation.fieldType + '-' + violation.rowId;
                var countText = '' + violation.missingCount;
                var postHref = '';
                
                // If the device list is too large, show a warning instead of inaccurate numbers:
                if (violation.useLimitedQuery && '' + violation.useLimitedQuery === 'true') {
                    
                    countText = '';
                    var pt_help_title = $("#point-unknown-number-help").attr('target-title');
                    postHref = 
                        '<a class="js-show-too-many-device-for-point-help" href="javascript:void(0);"'
                        + ' target-id="point-unknown-number-help"'
                        + ' target-title="'+ pt_help_title +'">'
                            + '<i class="icon icon-warning">&nbsp;</i>'
                        + '</a>';
                }
                
                output +=
                    '<td>'
                        + '<a class="fl error js-show-problem-devices" href="javascript:void(0);"'
                        + ' data-url="' + url + '"'
                        + ' target-id="' + popupListId + '"'
                        + ' target-title="' + violation.listTitle + '">'
                            + '<i class="icon icon-magnifier"></i>'+ countText
                        + '</a>'
                        
                        + '<span class="fl js-loading dn">'
                            + '<i class="icon icon-spinner"></i>'
                            + '<span class="b-label">' + countText + '</span>'
                        + '</span>'
                        + postHref
                        + '<div class="dn" id="' + popupListId + '"></div>'
                    + '</td>';
                
                output +=
                      '<td>' + violation.missingText + '</td>'
                    + '<td>' + violation.fieldDisplayName + '</td>\n';
                
                // do not add 'add points' functionality unless we are editing
                if (violation.fieldType === 'POINT' && $('.js-monitor-form').length !== 0) {
                    
                    var add_points_url = 'forwardToAddPoints?' + url_params;
                    
                    output +=
                        '<td class="fr wsnw">'
                            + '<a href="' + add_points_url + '"'
                                + ' target="_blank"'
                                + ' data-add-key="' + attribute + '">'
                                + '<i class="icon icon-add"></i>'
                                + '<span class="b-label">' + violation.addPointsTxt + '</span>'
                            + '</a>'
                        + '</td>';
                    
                    if (refresh_key != null && attribute === refresh_key) {
                        same_point_violation_exists = true;
                    }
                } else {
                    output += '<td>&nbsp;</td>\n';
                }
                
                someHtml = output
                + '<td>'
                    + '<a class="js-show-violation-help js-violation-help-link" href="javascript:void(0);"'
                    + ' target-id="' + popupHelpId + '"'
                    + ' target-title="' + violation.helpTitle +'">'
                        + '<i class="icon icon-help">&nbsp;</i>'
                    + '</a>'
                    + '<div class="dn" id="' + popupHelpId + '">' + violation.helpText + '</div>'
                + '</td>'
                + '</tr>';
                
                $(newTable).find('tbody').append(someHtml);
            });
            
            if (! same_point_violation_exists) {
                ctrl_refresh.attr('data-add-key', '');
                ctrl_refresh.hide();
            }
            
            $('.js-supported-counts').attr('data-count-status', 'missing');
            $('.js-supported-devices-count .js-loading').hide();
            $('.js-supported-devices-count .js-supported-counts').show();
        },
        
        /** Displays the  refreshed violations section 
         * @param {Object} event - jquery event object.
         */
        _display_violation_refresh = function (event) {
            $('#refresh-violations').show();
            var key = $(event.target).attr('data-add-key');
            $('#refresh-violations').attr('data-add-key', key);
        },
        
        /** Open the local help dialog  
          * @param {Object} event - jquery event object.
        */
        _display_local_help = function (event) {
            var link = $(event.currentTarget);
            $('#'+ link.attr('target-id')).dialog({ width: 400, title: link.attr('target-title') });
        },
        
        /** Update or create monitors */
        _monitor_save = function () {
            _disable_btns();
            $('.js-monitor-form').submit();
        },
        
        /** Toggles Monitors enabling .*/
        _monitor_toggle_enabled = function () {
            _disable_btns();
            $('.js-toggle-enabled-form').submit();
        },
        
        /** Deletes a monitor.*/
        _monitor_delete = function () {
            _disable_btns();
            $('.js-delete-form').submit();
        },
        
        /** Disable the given set of buttons.*/
        _disable_btns = function () {
            $('.page-action-area button, .ui-dialog-buttonset button').prop('disabled', true);
        },
        
        /** Gets processed row id from element name. 
         * @param {Object} jqueryElement - Jquery element.
         */
        _get_proc_row_id_from_elem_name = function (jqueryElement) {
            var str     = jqueryElement.attr('name');
            var iStart  = str.indexOf("[")+1;
            var iEnd    = str.indexOf("]");
            var sInt    = str.substring(iStart,iEnd);
            var iInt    = parseInt(sInt, 10);
            return iInt;
        },
        
        /** Add a processor */
        _processor_add = function () {
            
            _waiting_to_finish_N_before_doing_counts = 0;
            
            var template_row = $('.js-processors-table .js-new-row-model');
            var new_row = template_row.clone();
            var new_undo_row = template_row.next('tr').clone();
            
            new_row.removeClass('js-new-row-model');
            new_row.addClass('processor');
            
            var last_index;
            if ($('.js-processors-table tbody tr').length === 2) {
                last_index = 0;
                $('.js-no-items-message').remove();
            } else {
                last_index  = 1+ _get_proc_row_id_from_elem_name($('.js-processors-table tbody tr select:last'));
            }
            
            new_row.find('[data-name]').each(function () {
                var select = $(this);
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
            
            $('.js-processors-table tbody').append(new_row);
            $('.js-processors-table tbody').append(new_undo_row);
            new_row.show()
            .find('select').first().focus();
        },
        
        /** Change the state of processors.
         * @param {Object} ev - Jquery event.
        */
        _state_group_changed = function (ev) {
            _state_group_changed_worker($(ev.target));
        },
        
        /**
         * When a State Group control is changed, this will properly update the related States control.
         * 
         * Pre-req: if you want to do a single update to counts for multiple changes, do:
         *             _waiting_to_finish_N_before_doing_counts = n;
         */
        _state_group_changed_worker = function (jqueryObj) {
            
            var row = jqueryObj.closest('tr');
            var state_group_id = _get_state_group_value(row);
            
            if (state_group_id.length < 1) {
                _get_supported_counts();
                return;
            }
            
            $.ajax({
                url: 'state-group-states',
                data: {'stateGroupId': state_group_id}
            }).done(function (data) {
                
                var DOM_states = row.find('.js-states');
                var ctrl_name = DOM_states.attr('name') != undefined 
                    ? DOM_states.attr('name') 
                    : DOM_states.find(':input').attr('name');
                
                if (data.states.length > 1) {
                    
                    DOM_states.replaceWith('<select class="js-states" name="'+ ctrl_name +'"></select>');
                    var state_select = row.find('.js-states');
                    
                    data.states.forEach(function (state) {
                        $('<option>')
                        .val(state_group_id + ':' + state.id)
                        .text(state.text)
                        .appendTo(state_select);
                    });
                    
                } else {
                    if (data.states.length == 0) {
                        _blankout_states(row);
                    } else if (data.states.length == 1) {
                        
                        var state = data.states[0];
                        DOM_states.replaceWith(
                        '<div class="js-states">' 
                            + '<input type="hidden" name="'+ ctrl_name +'" value="'+ state_group_id+':' + state.id + '">'
                                + state.text 
                        +'</div>');
                    }
                }
            }).always(function () {
                _get_supported_counts();
            });
        },
        
        /** Remove the states.
         * @param {Object} containingRow - Jquery element
         */
        _blankout_states = function (row) {
            
            var select_state = row.find('select.js-states');
            var input_state = row.find('.js-states input');
            
            var ctrl_name = select_state.length > 0 ? select_state.attr("name") : input_state.attr("name");
            var html = 
                '<div class="js-states">'
                    + '<input type="hidden" name="' + ctrl_name +'" value="' + "null" +'">'
                    + $('#str_na').text()
                + '</div>';
            
            row.find('.js-states').replaceWith(html);
        },
        
        /** Update the state on attribute change event.
         * @param {Object} ev - jquery event object.
         */
        _attribute_changed = function (ev) {
            _update_state_groups_worker(ev);
        },
        
        /** Update the state group.
         * @param {Object} ev - jquery event object.
         */  
        _update_state_groups = function (ev) {
            _update_state_groups_worker(ev);
        },
        
        /**
         * This uses the Attribute control to update the related State Group.
         * @param {Object} ev - jquery event object.
         */
        _update_state_groups_worker = function (ev) {
            
            var row = $(ev.target).closest('tr');
            var attr_val = row.find('select.js-attribute').find(':selected').val();
            var DOM_stategroups = row.find('.js-state-group');
            var select_sg = row.find('select.js-state-group');
            var input_sg = row.find('.js-state-group input');
            var ctrl_name = select_sg.length > 0 ? select_sg.attr('name') : input_sg.attr('name');
            var foundOpt;
            var selectEl;
            var selectParent;
            var savedSelect;
            var DOM_feedback = $('.js-calc-indicator').clone();
            var row_id = _get_proc_row_id_from_elem_name(row.find('select.js-attribute'));
            
            DOM_stategroups.hide();
            row.find('.js-states').hide();
            
            DOM_feedback.removeClass('js-calc-indicator');
            $(DOM_stategroups.closest('td')).append(DOM_feedback);
            DOM_feedback.show().flash({ color: '#dae2ff', duration: 3000 });
            
            if (_supported_counts_xhr 
                    && _supported_counts_xhr[row_id] 
                    && _supported_counts_xhr[row_id] !== 'undefined') {
                _supported_counts_xhr[row_id].abort();
            }
            
            _supported_counts_xhr[row_id] = $.ajax({
                url: 'attribute-state-groups',
                data: {
                    attribute: attr_val, 
                    groupName: $('.js-monitor-group input').val()
                }
            }).done(function (data, textStatus, jqXHR) {
                
                DOM_feedback.remove();
                
                if (data.stateGroups.length > 1) {
                    
                    DOM_stategroups.replaceWith('<select class="js-state-group" name="'+ ctrl_name +'"></select>');
                    DOM_stategroups = row.find('select.js-state-group');
                    data.stateGroups.forEach(function (sg) {
                        $('<option>')
                        .val(sg.id)
                        .text(sg.name)
                        .appendTo(DOM_stategroups);
                    });
                } else {
                    
                    var str = '<div class="js-state-group"><input type="hidden" name="'+ ctrl_name +'"';
                    if (data.stateGroups.length == 0) {
                        DOM_stategroups.replaceWith(str +' value="">'+ $('#str_na').text() +'</div>');
                    } else if (data.stateGroups.length == 1) {
                        var fullString = str +' value="'+ data.stateGroups[0].id +'">'+ data.stateGroups[0].name +'</div>';
                        DOM_stategroups.replaceWith(fullString);
                    }
                	DOM_stategroups = row.find('.js-state-group input');
                }
                
                DOM_stategroups.trigger('change');
                
                if (data.stateGroups.length == 0) {
                    _blankout_states(row);
                }
                
            });
            
            var attributeIsUNSELECTED = attr_val == '-1';
            
            if (! attributeIsUNSELECTED) {
                
                foundOpt = row.find('.js-attribute option[value="-1"]');
                if (0 !== foundOpt.length) {
                    
                    foundOpt.remove();
                    // there is a bug in IE9 that causes the option visible to the user to be one
                    // off from the option that is actually selected after a previous option has
                    // been removed. MS and SO describe a similar bug that is fixed by removing
                    // and adding the options to and from the DOM. The following seems to work.
                    selectEl = row.find('select.js-attribute');
                    selectParent = selectEl.parent();
                    savedSelect = selectEl.detach();
                    savedSelect.appendTo(selectParent);
                }
            }
        },
        
        /** Get the state group value of given row
         * @param {Object} row - jquery element.
         */
        _get_state_group_value = function (row) {
            
            var state_group_select = row.find('select.js-state-group');
            var state_group_input = row.find('.js-state-group input:hidden');
            var state_group_id = state_group_select.length > 0 ? state_group_select.val() : state_group_input.val();
            
            return state_group_id;
        },
        
        /** Validate the processors. */
        _validate_processors = function () {
            
            var missingCount = 0;
            var procs = $('.processor').filter(":visible");
            procs.each(function (idx, row) {
                var ctrl = _get_state_group_value($(row));   // for IE8
                if (ctrl == null || ctrl.length < 1) {
                    missingCount++;
                }
            });

            //only enable if not calculating
            var enable = $('.js-calculating-warning').hasClass('dn') ? false : true;
            $('.js-save-monitor').prop('disabled', missingCount > 0 || procs.length < 1 ? true : enable);
        },
        
        mod;
        
    mod = {
            
        init: function () {
            
            if (_initialized) return;
            
            $('.js-monitor-form').on('change', '.js-monitor-group input', _update_state_groups);
            
            /** Show list of problem devices. */
            $(document).on('click', '.js-show-problem-devices', function (ev) {
                
                var link = $(ev.currentTarget);
                link.next().show();
                link.hide();
                $.ajax({
                    url: link.data('url')
                }).done(function (html) {
                    var dialog = $('#'+ link.attr('target-id'));
                    dialog.html(html);
                    dialog.dialog({ width: 500, height: 200, title: link.attr('target-title') });
                    link.next().hide();
                    link.show();
                });
            });
            
            /** A device group was chosen. */
            $(document).on('yukon:device:group:picker:selection', function (ev, groups) {
                
                _waiting_to_finish_N_before_doing_counts = 0;
                // show the loading element and hide the count
                $('.js-device-group-count').hide().prev().show();
                var groupName = groups[0];
                
                // Update device group count
                if (_group_count_xhr && _group_count_xhr !== 'undefined') {
                    _group_count_xhr.abort();
                }
                _group_count_xhr = $.ajax({
                    url: 'device-group-count',
                    data: {'groupName': groupName}
                }).done(function (data) {
                    $('.js-device-group-count').text(data.count).show()
                    .prev().hide();
                    $('.js-monitor-form .js-add_processor').prop('disabled', data.count > 0 ? false : true);
                });
                
                // Update supported count
                var procs = $('tr.processor .js-attribute');
                if (procs.length == 0) {
                    _get_supported_counts();
                } else { 
                    // Refresh ALL state groups
                    _waiting_to_finish_N_before_doing_counts = procs.length;
                    procs.each(function (idx, proc) {
                        $(proc).trigger('change');
                    });
                }
            });
            
            $(document).on('click', '.js-show-violation-help', _display_local_help);
            
            $(document).on('click', '.js-show-too-many-device-for-point-help', _display_local_help);
            
            $('.js-monitor-form').on('click', '.add', _display_violation_refresh);
            
            $('.js-monitor-form').on('click', '#refresh-violations', function () {
                $('#refresh-violations').hide();
                _get_supported_counts();
            });
            
            $('.js-processors-table').on('change', '.js-attribute', _attribute_changed);
            
            $('.js-processors-table').on('change', '.js-state-group', _state_group_changed);
            
            $('.js-add_processor').on('click', _processor_add);
            
            $('.js-save-monitor').on('click', _show_update_dialog);
            
            $('.js-toggle-enabled').on('click', _monitor_toggle_enabled);
            
            $(document).on("yukon.dialog.confirm.ok", _monitor_delete);
            
            $(document).on('yukon:ami:ddm:save', _monitor_save);
            
            $(document).on('click', '.js-undo-remove-btn, .js-remove-btn', _get_supported_counts);
            
            $(document).on('click', '.js-recalculate', function () {
                $('.js-violations-calculating').removeClass('dn');
                $('.js-calculating-disable').attr('disabled', true);
                $('.js-calculating-warning').toggleClass('dn', false);
            });

            
            _get_supported_counts({ initial_load: true });
            
            // Add our help icons next to the two column titles (Settings and Processors)
            $('.js-settings-section .title-bar').append('<i class="icon icon-help js-open-settings-help cp"></i>');
            $('.js-processors-section .title-bar').append('<i class="icon icon-help js-open-processors-help cp"></i>');
            
            _initialized = true;
        },
        
        violationUpdater: function (data) {
            $(function () {
                var value = data.value;
                var count = +value;
                $('.js-violations').addClass('dn');
                $('.js-calculating-disable').removeAttr('disabled');
                $('.js-calculating-warning').toggleClass('dn', true);
                if (value === undefined) {
                    $('.js-violations-count').removeClass('dn').text('N/A');
                    return;
                }
                if (value === 'CALCULATING') {
                    $('.js-violations-calculating').removeClass('dn');
                    $('.js-calculating-disable').attr('disabled', true);
                    $('.js-calculating-warning').toggleClass('dn', false);
                }
                else if (value === 'NA') {
                    $('.js-violations-na').removeClass('dn');
                    _validate_processors();
                }
                else {
                    $('.js-violations-count').removeClass('dn').text(count);
                    if (count > 0) {
                        $('.js-violations-exist').removeClass('dn');
                    }
                    _validate_processors();
                }
            });
        }
        
    };
    
    return mod;
    
})();

$(function () { yukon.ami.ddm.init(); });