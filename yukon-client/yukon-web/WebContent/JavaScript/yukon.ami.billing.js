/**
 * Singleton that manages the old ami billing pages
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.ami.billing');

yukon.ami.billing = (function() {

    var _STOP_EVENT = function(event) {
            event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
        },

        _is_valid_billing_group = function() {
            var formatId = jQuery('#MForm [name=fileFormat] :selected').val();
            var itronTypeId = jQuery('#in_formatType_itronEvent').val();
            var bgVal = jQuery('#billGroup').val();
            var anyBillingGroup = bgVal != null && bgVal != undefined && bgVal.length > 0;
    
            var hasError = !anyBillingGroup && formatId != itronTypeId;
            var err = jQuery('#txt_selectGroup');
            if (hasError) {
                err.remove();
                jQuery('#row_billing_group .value').append(err);
                err.show();
                return false;
            }
            err.hide();
            return true;
        },
        
        _get_max_previous_days = function() {
        	var previousDays = 65535;
        	return previousDays;
        },

        _is_valid_demand_days = function() {
        	var demandDaysPrevious = jQuery('input[name=demandDays]').val();        	
            // Check if demand days is positive Integer
            var isPositiveInteger =  /^\d+$/.test(demandDaysPrevious);          
            var errDemandDaysPrevious = jQuery('#txt_invalidDemandDaysPrevious');
            var MAX_PREVIOUS_DAYS = _get_max_previous_days();
            if (!isPositiveInteger || demandDaysPrevious  > MAX_PREVIOUS_DAYS ) {
            	 errDemandDaysPrevious.remove();             	 
                 jQuery('#row-demand-days-previous .value').append(errDemandDaysPrevious);
                 errDemandDaysPrevious.show();
                 return false;
            }            
            errDemandDaysPrevious.hide();
            return true;
        },

        _is_valid_energy_days = function() {
        	var energyDaysPrevious = jQuery('input[name=energyDays]').val();
            // Check if energy days is positive Integer        	
            var isPositiveInteger =  /^\d+$/.test(energyDaysPrevious);   
            var errEnergyDaysPrevious = jQuery('#txt_invalidEnergyDaysPrevious');
            var MAX_PREVIOUS_DAYS =_get_max_previous_days();
            if (!isPositiveInteger || energyDaysPrevious > MAX_PREVIOUS_DAYS ) {
            	errEnergyDaysPrevious.remove();
             	jQuery('#row-energy-days-previous .value').append(errEnergyDaysPrevious);
             	errEnergyDaysPrevious.show();
                return false;
            }            
            errEnergyDaysPrevious.hide();
            return true;
        },
        
        _do_submit_settings_form = function(event, method, url) {
            _STOP_EVENT(event);
            if (!_is_valid_demand_days()) {
                return false;
            }    
            if (!_is_valid_energy_days()) {
                return false;
            }               
            if (!_is_valid_billing_group()) {
                return false;
            }
    
            jQuery.ajax({
                url: url,
                type: method,
                data: jQuery('#MForm').serialize()
            }).done( function(html) {
                _populate_generation_schedule_form(html);
            });
            return false;
        },

        _populate_generation_schedule_form = function(new_html) {
            var settingsPane = jQuery('#billing_generation_settings');
            var maybeNewPane = jQuery('#billing_generation_schedule');
            if (settingsPane.length < 1) {
                alert('PROGRAMMING PROBLEM: missing div#billing_generation_settings');
            }
            if (maybeNewPane.length > 0) { // Exists
                maybeNewPane.html(new_html);
            } else { // Must create it
                maybeNewPane = jQuery('<div id="billing_generation_schedule" class="dn clearfix stacked">\n'+ new_html +'\n</div>');
                maybeNewPane.insertAfter(settingsPane);
            }
            yukon.tag.scheduled_file_export_inputs.initializeFields();

            settingsPane.hide();
            maybeNewPane.show();
        },

        _do_refresh_schedules_jobs_list = function(send_data, after_function) {
            jQuery.ajax({
                url: _url_list_schedule_jobs,
                type: 'get',
                data: send_data
            }).done( function(html) {
                jQuery('#billing_tab_container').tabs('option','active',2); // THIRD tab
                jQuery('#billing_schedules_jobs').html(html);
                if (after_function != null && after_function != undefined) {
                    after_function();
                }
            });
        },

        _get_pagination_state = function(jQueryContainer) {
            var countPerPage = jQueryContainer.find('.perPageArea .selectedItem').text();
            var currPage = jQueryContainer.find('.paging-area').data('currentPage');
            return {'page': currPage, 'itemsPerPage': countPerPage};
        },

        _delete_schedule_job = function(jQueryBtn, params) {
            jQuery.ajax({
                url: _url_delete_scheduled_job,
                type: 'post',
                data: params
            }).done( function(results) {
                if (results.success) {
                    _do_refresh_schedules_jobs_list(
                        _get_pagination_state(jQuery('#billing_schedules_jobs')), function() {
                    });
                } else { // ?
                    alert('An error prevented deleting this job: \n\t'+ results.error);
                }
            });
        },

        _show_edit_schedule_job = function(jQueryBtn, params) {
            jQuery.ajax({
                url: _url_scheduled_billing_form,
                type: 'get',
                data: params
            }).done( function(html) {
                _populate_generation_schedule_form(html);
                jQuery('#billing_tab_container').tabs('option','active',0); // FIRST tab
            });
        },


        // TAB #2
        _get_format_ajax_page = function(event, url) {
            _STOP_EVENT(event);
            var currFormat = jQuery('#availableFormat :selected');
            var currFormatId = currFormat.val();
    
            jQuery.ajax({
                url: url,
                type: 'get',
                data: {'availableFormat': currFormatId}
            }).done( function(html) {
                _populate_setup_form(html);
                jQuery('#billing_setup_content').html(html);
            }).fail( function(msg) {
                alert('FAILED: '+ msg);
            });
            return false;
        },

        _do_format_ajax_op = function(event, action, url) {
            _STOP_EVENT(event);
            var the_data = {};
            if (action == 'delete') {
                var currFormat = jQuery('#availableFormat :selected');
                var currFormatId = currFormat.val();
                the_data = {'availableFormat': currFormatId};
            } else if (action == 'save') {
                the_data = jQuery('#billingFormatForm').serialize();
            } else {
                alert('Unknown action: \"' + event + '\"');
                return false;
            }

            jQuery.ajax({
                url: url,
                type: 'post',
                data: the_data
            }).done( function(data) {
                if (data.success) {
                    _do_refresh_billing_setup_list(null, null); // ? Show success message?
                } else {
                    alert('FAILED '+ opName +'\n'+ data.errors);
                }
            }).fail( function(error) { // Not the simple (msg, code, etc.) JQuery says...?
                alert('FAILED: '+ error.statusText +'\nError Code: '+ error.status +'\n'+ error.responseText);
            });
            return false;
        },

        _do_refresh_billing_setup_list = function(send_data, after_function) {
            jQuery.ajax({
                url: _url_format_setup,
                type: 'get',
                data: send_data
            }).done( function(html) {
                var overviewPane = jQuery('#billing_setup_overview');
                overviewPane.html(html);
                mod.reset_setup_tab();
                if (after_function != null && after_function != undefined) {
                    after_function();
                }
            });
        },

        _populate_setup_form = function(new_html) {
            var overviewPane = jQuery('#billing_setup_overview');
            var formPane = jQuery('#billing_setup_form');
            if (overviewPane.length < 1) {
                alert('PROGRAMMING PROBLEM = missing div#billing_setup_overview');
            }
            if (formPane.length > 0) { // Exists
                formPane.html(new_html);
            } else { // Must create it
                formPane = jQuery('<div id="billing_setup_form" class="dn">\n'+ new_html +'\n</div>');
                formPane.insertAfter(overviewPane);
            }
            overviewPane.hide();
            formPane.show();
        },
        _initialized = false,

        _url_generate_billing_file = yukon.url('/servlet/BillingServlet'),
        _url_scheduled_billing_form = yukon.url('/scheduledBilling/showForm'),
        _url_schedule_export = yukon.url('/scheduledBilling/scheduleExport.json'),

        _url_list_schedule_jobs = yukon.url('/billing/_jobs'),
        _url_delete_scheduled_job = yukon.url('/scheduledBilling/delete.json'),

        _url_format_copy = yukon.url('/dynamicBilling/_copy.html'),
        _url_format_create = yukon.url('/dynamicBilling/_create.html'),
        _url_format_delete = yukon.url('/dynamicBilling/delete.json'),
        _url_format_edit = yukon.url('/dynamicBilling/_edit.html'),
        _url_format_save = yukon.url('/dynamicBilling/save.json'),
        _url_base_setup = yukon.url('/dynamicBilling/'),
        mod;

    /* PUBLIC METHODS */
    mod = {
        init: function() {
            if (_initialized) {
                return;
            }

            var doc = jQuery(document);

            doc.on('click', '#MForm [name=generate]', function(){ jQuery('#MForm').submit();});
            doc.on('click', '#MForm [name=schedule]', mod.show_scheduled_billing_form);
            doc.on('submit', '#MForm', mod.do_generate_billing_file);

            doc.on('submit', '#scheduleForm', mod.do_schedule_billing_file_export);
            doc.on('click', '#billing_generation_schedule .f-cancel', mod.reset_generation_tab);
            jQuery('#billing_tab_container').tabs({
                activate: mod.on_tab_change });

            doc.on('click', '#tab_schedules a', mod.update_schedules_job_list);
            doc.on('click', '#billing_schedules_jobs button', mod.do_schedules_job_list_button);

            // 2nd tab
            doc.on('click change', '#formatForm #availableFormat', mod.unfreeze_billing_setup);
//            doc.on('click', '#formatForm [name=copy], #formatForm [name=create], #formatForm [name=edit]', show_billing_setup_form);
            doc.on('click', '#formatForm [name=delete]', mod.delete_billing_format);
            doc.on('click', '#btnCreateFormat', mod.show_create_form);
            doc.on('click', '#btnEditFormat', mod.show_edit_form);
            doc.on('click', '#btnCopyFormat', mod.show_copy_form);
            // @ formatDetail.jsp for Setup tab (2nd tab) after [Create]
            doc.on('click', '#btnCancelSetup', mod.reset_setup_tab);
            doc.on('submit', '#billingFormatForm', mod.do_save_format);
            doc.on('click', '#btnDeleteFormat', mod.do_delete_format);

            _initialized = true;
        },

        // Used by _jobs.jsp, ASSUMED TO EXIST BY scheduledFileExportJobs.tag
        delete_schedule_job : function(jobId) {
            _delete_schedule_job(null, {'jobId': jobId});
        },

        show_create_form: function(event) {
            return _get_format_ajax_page(event, _url_format_create);
        },

        show_edit_form: function(event) {
            return _get_format_ajax_page(event, _url_format_edit);
        },

        show_copy_form: function(event) {
            return _get_format_ajax_page(event, _url_format_copy);
        },

        do_delete_format: function(event) {
            return _do_format_ajax_op(event, 'delete', _url_format_delete);
        },

        do_save_format: function(event) {
            return _do_format_ajax_op(event, 'save', _url_format_save);
        },

        do_generate_billing_file: function(event) {
            if (!_is_valid_demand_days()) {
            	_STOP_EVENT(event);
                return false;
            }    
            if (!_is_valid_energy_days()) {
            	_STOP_EVENT(event);
                return false;
            }   
            if (!_is_valid_billing_group()) {
                _STOP_EVENT(event);
                return false;
            }
        },

        show_scheduled_billing_form: function(event) {
            return _do_submit_settings_form(event, 'get', _url_scheduled_billing_form);
        },

        on_tab_change : function(event, ui){
            var from = ui.oldPanel.children('div').attr('id');
            if(from !='billing_generation_settings') {
                return;
            }
            var to = ui.newPanel.children('div').attr('id');
            if(to ==='billing_generation_settings') {
                return;
            }
            mod.reset_generation_tab();
        },

        /**
         * We're leaving the file path, date/periodic information, and email address as-is.
         */
        reset_generation_tab: function(event) {
            if (event != null && event != undefined) {
                _STOP_EVENT(event);
            }
            var settingsPane = jQuery('#billing_generation_settings');
            var schedParams = jQuery('#billing_generation_schedule');
            schedParams.hide();
            settingsPane.hide();
            settingsPane.find('[name=fileFormat]').val('4'); // = CTI-CSV
            settingsPane.find('[name=endDate]').val(jQuery.datepicker.formatDate('mm/dd/yy', new Date()));
            settingsPane.find('[name=demandDays]').val('30');
            settingsPane.find('[name=energyDays]').val('7');
            settingsPane.find('[name=removeMultiplier]').removeAttr('checked');
            settingsPane.find('input[type=text][data-tree-id=billingTree]').val('Search');
            settingsPane.show();

            schedParams.find('[name=scheduleName], [name=exportFileName], #notificationEmailAddresses').val('');
            schedParams.find('#sameAsSchedName').attr('checked','checked');
            schedParams.find('#appendDateToFileName, #overrideFileExtension, #includeExportCopy').removeAttr('checked');
            schedParams.find('#timestampPatternField').val('_yyyyMMddHHmmss');
            schedParams.find('#exportFileExtension').val('.csv');
            schedParams.find('#exportPath :nth-child(1)').attr('selected', 'selected');
            schedParams.find('[name=scheduleCronString_CRONEXP_FREQ]').val('DAILY');   // Does this show/hide properly?
            schedParams.find('[name=scheduleCronString_CRONEXP_HOUR]').val('1');
            schedParams.find('[name=scheduleCronString_CRONEXP_MINUTE]').val('0');
            schedParams.find('[name=scheduleCronString_CRONEXP_AMPM]').val('AM');
            schedParams.find('[name=scheduleCronString_CRONEXP_DAILY_OPTION][value=EVERYDAY]').attr('checked','checked');

            var dailyCheckboxes = '[name=scheduleCronString_CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_SUN], [name=scheduleCronString_CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_MON], [name=scheduleCronString_CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_TUES], [name=scheduleCronString_CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_WED], [name=scheduleCronString_CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_THURS], [name=scheduleCronString_CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_FRI], [name=scheduleCronString_CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_SAT]';
            schedParams.find(dailyCheckboxes).removeAttr('checked');
            schedParams.find('[name=scheduleCronString_CRONEXP_MONTHLY_OPTION]').attr('checked','checked');
            schedParams.find('[name=scheduleCronString_CRONEXP_MONTHLY_OPTION_ON_DAY_X]').val('1');
            schedParams.find('[name=scheduleCronString_CRONEXP_CUSTOM_EXPRESSION]').val('0 0 1 1 * ?');
        
            // AND we have to reset the hidden form's values too!
            var hiddenForm2 = schedParams.find('#scheduleForm');
            hiddenForm2.find('[name=deviceGroups], [name=fileFormat], [name=demandDays], [name=energyDays], [name=removeMultiplier], [name=jobId], [name=_appendDateToFileName], [name=_overrideFileExtension], [name=_includeExportCopy]').val('');
        },


        /** Setup tab **/
        reset_setup_tab: function(event) {
            if (event != null && event != undefined) {
                _STOP_EVENT(event);
            }
            var overviewPane = jQuery('#billing_setup_overview');
            var formPane = jQuery('#billing_setup_form');
            overviewPane.show();
            formPane.hide();
            // TODO FIXME: What other fields to add here?
            formPane.children('[name=formatName], [name=headerField], [name=footerField]').val('');
            formPane.children('[name=selectedFields]').html('');
        },

        do_schedule_billing_file_export: function(event) {
            _STOP_EVENT(event);
            var form = jQuery('#scheduleForm');

            jQuery.ajax({
                url: _url_schedule_export,
                type: 'post',
                data: form.serialize()
            }).done( function(data) {
                // Clear existing error indicators + messages
                form.find('input').removeClass('error').closest('td').find('div.error').remove();

                if (data.success) {
                    _do_refresh_schedules_jobs_list(null, function() {
                        mod.reset_generation_tab();
                    });
                    return false;
                }
                var haveFieldErr = new Object();
                for(var ii=0; ii < data.errors.length; ii++) {
                    var err = data.errors[ii];
                    if (haveFieldErr[err.field]) { // Display only one message per field
                        continue;
                    }
                    haveFieldErr[err.field] = true;
                    var field = null;
                    if (err.field === 'scheduleCronString') {
                        field = form.find('[name='+ err.field +'_CRONEXP_FREQ]');
                        var opt = field.find(':selected');
                        if (opt.val() === 'WEEKLY') {
                        	form.find('#scheduleCronString_cronExpWeeklyDiv input').addClass('error');
                        } else if (opt.val() === 'CUSTOM') {
                        	form.find('#scheduleCronString_cronExpCustomDiv input').addClass('error');
                        }
                    } else {
                        field = form.find('[name='+ err.field +']');
                        field.addClass('error');
                    }
                    var row = field.closest('td');
                    var errTd = jQuery('<div class="error">'+ err.message +'</div>');
                    errTd.appendTo(row);
                }
            });
            return false;
        },

        update_schedules_job_list: function(event) {
            _STOP_EVENT(event);
            var href = jQuery(event.currentTarget).attr('href');
            var at = href.indexOf('?') + 1;
            _do_refresh_schedules_jobs_list(href.substr(at), null);
        },

        // no-op for Remove button: SEE scheduledFileExportJob.tag
        do_schedules_job_list_button : function(event) {
            var btn = jQuery(event.currentTarget);
            var href = btn.attr('data-url');
            if (href == null) { // history: navigate to the list
                return true;
            }
            var action = href.substr(0, href.indexOf('?'));
            var params = href.substr(href.indexOf('?')+1);
            if (action == 'delete') {
                _STOP_EVENT(event);

                return false;
            } else if (action == 'showForm') {
                _STOP_EVENT(event);
                _show_edit_schedule_job(btn, params);
                return false;
            }
            return true; // This should never be hit.
        },

        unfreeze_billing_setup : function(event){ //used to enable or disable buttons
            var selectedFormat = jQuery('#availableFormat :selected');
            if (selectedFormat.length == 1 ) {
                jQuery('#btnCopyFormat').removeAttr('disabled'); //able to copy any format

                if (selectedFormat.attr('isSystem') == 'true'){
                    jQuery('#btnEditFormat').attr('disabled','disabled'); //disable edit and delete if it is system format
                    jQuery('#btnDeleteFormat').attr('disabled','disabled');
                } else{
                    jQuery('#btnEditFormat').removeAttr('disabled'); //enable
                    jQuery('#btnDeleteFormat').removeAttr('disabled');
                }
            } else{
                jQuery('#btnEditFormat').attr('disabled','disabled'); //disable
                jQuery('#btnCopyFormat').attr('disabled','disabled');
                jQuery('#btnDeleteFormat').attr('disabled','disabled');
            }
        },

        // @ #billing_setup_overview
        show_billing_setup_form : function(event) {
            _STOP_EVENT(event);
            var btn = jQuery(event.currentTarget);
            var action = btn.attr('name');
            var formatId = jQuery('#formatForm availableFormat :selected').val();

            jQuery.ajax({
                url: _url_base_setup+'_'+ action +'.html',
                type: 'get',
                data: {'availableFormat': formatId}
            }).done( function(html) {
                _populate_setup_form(html);
            });
            return false;
        },

        // @ #billing_setup_overview
        delete_billing_format : function(event) {
            _STOP_EVENT(event);
            var formatId = jQuery('#formatForm availableFormat :selected').val();
            if (formatId == null || formatId == undefined) { // skip it
                _do_refresh_billing_setup_list(null, null);
                return false;
            }

            jQuery.ajax({
                url: _url_format_delete,
                type: 'post',
                data: {'availableFormat': formatId}
            }).done( function(data) {
                if (data.success) {
                    _do_refresh_billing_setup_list(null, function() {
                        alert(data.message);
                    });
                } else {
                    alert('FAILED DELETING #'+ formatId +'\n'+ data.errors);
                }
            });
            return false;
        }
    };
    return mod;
})();

jQuery(function() {
    yukon.ami.billing.init();
});
