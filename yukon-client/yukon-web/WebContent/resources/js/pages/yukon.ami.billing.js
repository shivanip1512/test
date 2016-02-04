/**
 * Handles the AMI billing page.
 * 
 * @module yukon.ami.billing
 * @requires JQUERY
 * @requires JQUERYUI
 * @requires yukon
 * @requires yukon.ami
 */

yukon.namespace('yukon.ami.billing');

yukon.ami.billing = (function() {

    var _STOP_EVENT = function(event) {
            event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
        },
        
        /** Validates if the billing group is selected or not.
         * @returns {boolean} - Returns false if no billing group is selected else returns true.
         */
        _is_valid_billing_group = function() {
            var formatId = $('#MForm [name=fileFormat] :selected').val();
            var itronTypeId = $('#in_formatType_itronEvent').val();
            var bgVal = $('#row-billing-group :hidden').val();
            var anyBillingGroup = bgVal != null && bgVal != undefined && bgVal.length > 0;
    
            var hasError = !anyBillingGroup && formatId != itronTypeId;
            var err = $('#txt_selectGroup');
            if (hasError) {
                err.remove();
                $('#row-billing-group .value').append(err);
                err.show();
                return false;
            }
            err.hide();
            return true;
        },
        
        /** Returns the maximum limit of previous days.
         *  @returns {number} previousDays - the maximum limit of previous days.
         */
        _get_max_previous_days = function() {
        	var previousDays = 65535;
        	return previousDays;
        },

        /** Validates if the demand days previous is a positive integer and within the max limit.
         *  @returns {boolean} - Returns true if valid else returns false.
         */
        _is_valid_demand_days = function() {
        	var demandDaysPrevious = $('input[name=demandDays]').val();        	
            // Check if demand days is positive Integer
            var isPositiveInteger =  /^\d+$/.test(demandDaysPrevious);          
            var errDemandDaysPrevious = $('#txt_invalidDemandDaysPrevious');
            var MAX_PREVIOUS_DAYS = _get_max_previous_days();
            if (!isPositiveInteger || demandDaysPrevious  > MAX_PREVIOUS_DAYS ) {
            	 errDemandDaysPrevious.remove();             	 
                 $('#row-demand-days-previous .value').append(errDemandDaysPrevious);
                 errDemandDaysPrevious.show();
                 return false;
            }            
            errDemandDaysPrevious.hide();
            return true;
        },

        /** Validates if the energy days previous is a positive integer and within the max limit.
         *  @return {boolean} -Returns true if valid else returns false.
         */
        _is_valid_energy_days = function() {
        	var energyDaysPrevious = $('input[name=energyDays]').val();
            // Check if energy days is positive Integer        	
            var isPositiveInteger =  /^\d+$/.test(energyDaysPrevious);   
            var errEnergyDaysPrevious = $('#txt_invalidEnergyDaysPrevious');
            var MAX_PREVIOUS_DAYS =_get_max_previous_days();
            if (!isPositiveInteger || energyDaysPrevious > MAX_PREVIOUS_DAYS ) {
            	errEnergyDaysPrevious.remove();
             	$('#row-energy-days-previous .value').append(errEnergyDaysPrevious);
             	errEnergyDaysPrevious.show();
                return false;
            }            
            errEnergyDaysPrevious.hide();
            return true;
        },
        
        /** Handles the submit form event.
         * @param {Object} event - jquery event object.
         * @param {string} method - method type.
         * @param {string} url - url to be invoked. 
         */
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
    
            $.ajax({
                url: url,
                type: method,
                data: $('#MForm').serialize()
            }).done( function(html) {
                _populate_generation_schedule_form(html);
            });
            return false;
        },
        
        /** 
         * Handles creating a schedule form
         * @param {string} new_html - the inner html for the new schedule pane.
         */
        _populate_generation_schedule_form = function(new_html) {
            var settingsPane = $('#billing_generation_settings');
            var maybeNewPane = $('#billing_generation_schedule');
            if (settingsPane.length < 1) {
                alert('PROGRAMMING PROBLEM: missing div#billing_generation_settings');
            }
            if (maybeNewPane.length > 0) { // Exists
                maybeNewPane.html(new_html);
            } else { // Must create it
                maybeNewPane = $('<div id="billing_generation_schedule" class="dn clearfix stacked">\n'+ new_html +'\n</div>');
                maybeNewPane.insertAfter(settingsPane);
            }
            yukon.tag.scheduledFileExportInputs.initializeFields();

            settingsPane.hide();
            maybeNewPane.show();
        },

        /** Refreshes the exported schedule jobs list.
         * @param {function} [after_function] - A callback function to fire when the refresh finishes.
         */
        _do_refresh_schedules_jobs_list = function(after_function) {
            $.ajax(_url_list_schedule_jobs).done(function (html) {
                $('#billing_tab_container').tabs('option','active',2); // THIRD tab
                $('#billing_schedules_jobs').html(html);
                if (after_function != null && after_function != undefined) {
                    after_function();
                }
            });
        },
        
        /** Deletes a schedule and refreshes the job list.
         * @param {Object} jQueryBtn - jquery button object.
         * @param {Object} params - Request parameters.
         */
        _delete_schedule_job = function(params) {
            $.ajax({
                url: _url_delete_scheduled_job,
                type: 'post',
                data: params
            }).done( function(results) {
                if (results.success) {
                    _do_refresh_schedules_jobs_list();
                } else { // ?
                    alert('An error prevented deleting this job: \n\t'+ results.error);
                }
            });
        },
        
        /** Populates the edit schedule page.
         * @param {Object} jQueryBtn - jquery button object.
         * @param {Object} params - Request parameters.
         */
        _show_edit_schedule_job = function(params) {
            $.ajax({
                url: _url_scheduled_billing_form,
                type: 'get',
                data: params
            }).done(function(html) {
                _populate_generation_schedule_form(html);
                $('#billing_tab_container').tabs('option','active',0); // FIRST tab
            });
        },


        /** Create/edit/copy an existing billing format
         * @param {Object} event - jquery event object.
         * @param {string} url - create/edit/copy URL. 
         */
        _get_format_ajax_page = function(event, url) {
            _STOP_EVENT(event);
            var currFormat = $('#availableFormat :selected');
            var currFormatId = currFormat.val();
    
            $.ajax({
                url: url,
                type: 'get',
                data: {'availableFormat': currFormatId}
            }).done( function(html) {
                _populate_setup_form(html);
                $('#billing_setup_content').html(html);
            }).fail( function(msg) {
                alert('FAILED: '+ msg);
            });
            return false;
        },

        /** Delete or save a billing format.
         * @param {Object} event - jquery event object.
         * @param {string} action - delete action/save action.
         * @param {string} url - delete url/save url.
         */
        _do_format_ajax_op = function(event, action, url) {
            _STOP_EVENT(event);
            var the_data = {};
            if (action == 'delete') {
                var currFormat = $('#availableFormat :selected');
                var currFormatId = currFormat.val();
                the_data = {'availableFormat': currFormatId};
            } else if (action == 'save') {
                the_data = $('#billingFormatForm').serialize();
            } else {
                alert('Unknown action: \"' + event + '\"');
                return false;
            }

            $.ajax({
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


        /** Refresh the billing setup list.
         * @param {Object} params - Request parameters.
         * @param {function} [after_function] - A callback function to fire when the refresh finishes.
         */
        _do_refresh_billing_setup_list = function(params, after_function) {
            $.ajax({
                url: _url_format_setup,
                type: 'get',
                data: params
            }).done( function(html) {
                var overviewPane = $('#billing_setup_overview');
                overviewPane.html(html);
                mod.reset_setup_tab();
                if (after_function != null && after_function != undefined) {
                    after_function();
                }
            });
        },

        /** Populates the setup form with given html.
         * @param {string} new_html - the inner html for the setup form.
         */
        _populate_setup_form = function(new_html) {
            var overviewPane = $('#billing_setup_overview');
            var formPane = $('#billing_setup_form');
            if (overviewPane.length < 1) {
                alert('PROGRAMMING PROBLEM = missing div#billing_setup_overview');
            }
            if (formPane.length > 0) { // Exists
                formPane.html(new_html);
            } else { // Must create it
                formPane = $('<div id="billing_setup_form" class="dn">\n'+ new_html +'\n</div>');
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
        
        mod = {};

    /* PUBLIC METHODS */
    mod = {
        init: function() {
            if (_initialized) {
                return;
            }

            var doc = $(document);

            doc.on('click', '#MForm [name=generate]', function(){ $('#MForm').submit();});
            doc.on('click', '#MForm [name=schedule]', mod.show_scheduled_billing_form);
            doc.on('submit', '#MForm', mod.do_generate_billing_file);

            doc.on('submit', '#scheduleForm', mod.do_schedule_billing_file_export);
            doc.on('click', '#billing_generation_schedule .js-cancel', mod.reset_generation_tab);
            $('#billing_tab_container').tabs({
                activate: mod.on_tab_change });

            doc.on('click', '#tab_schedules a', mod.update_schedules_job_list);

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
            doc.on('click', '.js-edit-job', mod.do_edit_schedule_job_list_button);
            /** Delete a schedule */
            $(document).on('yukon.job.delete', function (ev) {
                var jobId = $(ev.target).data('jobId');
                _delete_schedule_job({jobId: jobId});
            });
            _initialized = true;
        },


        /** Deletes a scheduled billing job and refreshes the job list.
         * Used by _jobs.jsp.
         * @param {string} jobId  - Id of the schedule which is to be deleted.
         */
        delete_schedule_job : function(jobId) {
            _delete_schedule_job({'jobId': jobId});
        },

        /** Handles the creation of new billing format.
         * @param {Object} event - jquery event object.
         */
        show_create_form: function(event) {
            return _get_format_ajax_page(event, _url_format_create);
        },

        /** Handles the editing an existing billing format.
         * @param {Object} event - jquery event object.
         */
        show_edit_form: function(event) {
            return _get_format_ajax_page(event, _url_format_edit);
        },

        /** Handles the copying of an existing billing format.
         * @param {Object} event - jquery event object.
         */
        show_copy_form: function(event) {
            return _get_format_ajax_page(event, _url_format_copy);
        },

        /** Handles deleting of an existing billing format.
         * @param {Object} event - jquery event object.
         */
        do_delete_format: function(event) {
            var currFormat = $('#availableFormat :selected'),
                currFormatId = currFormat.val();
            $.post(_url_format_delete, {'availableFormat': currFormatId}).done(function(result) {
                $('#billing_setup_overview').html(result);
            });
        },

        /** Handles saving a billing format.
         * @param {Object} event - jquery event object.
         */
        do_save_format: function(event) {
            return _do_format_ajax_op(event, 'save', _url_format_save);
        },

        /** Generates a billing file.
         * @param {Object} event - jquery event object.
         */
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

        /** Shows the scheduled billing exports.
         * @param {Object} event - jquery event object.
         */
        show_scheduled_billing_form: function(event) {
            return _do_submit_settings_form(event, 'get', _url_scheduled_billing_form);
        },

        /** Shows toggles between tabs on billing page.
         * @param {Object} event - jquery event object.
         */
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

        /** Resets the generation tab.
         * @param {Object} event - jquery event object.
         */
        reset_generation_tab: function(event) {
            if (event != null && event != undefined) {
                _STOP_EVENT(event);
            }
            var settingsPane = $('#billing_generation_settings');
            var schedParams = $('#billing_generation_schedule');
            schedParams.hide();
            settingsPane.hide();
            settingsPane.find('[name=fileFormat]').val('4'); // = CTI-CSV
            settingsPane.find('[name=endDate]').val($.datepicker.formatDate('mm/dd/yy', new Date()));
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


        /** Resets the Setup tab.
         * @param {Object} event - jquery event object.
         */
        reset_setup_tab: function(event) {
            if (event != null && event != undefined) {
                _STOP_EVENT(event);
            }
            var overviewPane = $('#billing_setup_overview');
            var formPane = $('#billing_setup_form');
            overviewPane.show();
            formPane.hide();
            // TODO FIXME: What other fields to add here?
            formPane.children('[name=formatName], [name=headerField], [name=footerField]').val('');
            formPane.children('[name=selectedFields]').html('');
        },

        /** Exports a billing file format.
         * @param {Object} event - jquery event object.
         */
        do_schedule_billing_file_export: function(event) {
            _STOP_EVENT(event);
            var form = $('#scheduleForm');

            $.ajax({
                url: _url_schedule_export,
                type: 'post',
                data: form.serialize()
            }).done( function(data) {
                // Clear existing error indicators + messages
                form.find('input').removeClass('error').closest('td').find('div.error').remove();

                if (data.success) {
                    _do_refresh_schedules_jobs_list(function() {
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
                        	form.find('#scheduleCronString-cron-exp-weekly input').addClass('error');
                        } else if (opt.val() === 'CUSTOM') {
                        	form.find('#scheduleCronString-cron-exp-custom input').addClass('error');
                        }
                    } else {
                        field = form.find('[name='+ err.field +']');
                        field.addClass('error');
                    }
                    var row = field.closest('td');
                    var errTd = $('<div class="error">'+ err.message +'</div>');
                    errTd.appendTo(row);
                }
            });
            return false;
        },

        /** Updates the schedule list.
         * @param {Object} event - jquery event object.
         */
        update_schedules_job_list: function(event) {
            _STOP_EVENT(event);
            _do_refresh_schedules_jobs_list();
        },

        /** Performs edit action on the exported schedule list item.
         * @param {Object} event - jquery event object.
         */
        do_edit_schedule_job_list_button : function(event) {
            var btn = $(this),
                jobId = btn.data('jobId');
                _STOP_EVENT(event);
                _show_edit_schedule_job({jobId: jobId});
                return false;
             
        },

        /** Enables/Disables the buttons on billing setup page.
         * @param {Object} event - jquery event object.
         */
        unfreeze_billing_setup : function(event){ //used to enable or disable buttons
            var selectedFormat = $('#availableFormat :selected');
            if (selectedFormat.length == 1 ) {
                $('#btnCopyFormat').removeAttr('disabled'); //able to copy any format

                if (selectedFormat.attr('isSystem') == 'true'){
                    $('#btnEditFormat').attr('disabled','disabled'); //disable edit and delete if it is system format
                    $('#btnDeleteFormat').attr('disabled','disabled');
                } else{
                    $('#btnEditFormat').removeAttr('disabled'); //enable
                    $('#btnDeleteFormat').removeAttr('disabled');
                }
            } else{
                $('#btnEditFormat').attr('disabled','disabled'); //disable
                $('#btnCopyFormat').attr('disabled','disabled');
                $('#btnDeleteFormat').attr('disabled','disabled');
            }
        },

        /** Shows the billing setup form.
         * @param {Object} event - jquery event object.
         */
        show_billing_setup_form : function(event) {
            _STOP_EVENT(event);
            var btn = $(event.currentTarget);
            var action = btn.attr('name');
            var formatId = $('#formatForm availableFormat :selected').val();

            $.ajax({
                url: _url_base_setup+'_'+ action +'.html',
                type: 'get',
                data: {'availableFormat': formatId}
            }).done( function(html) {
                _populate_setup_form(html);
            });
            return false;
        },

        /** Deletes a billing format
         * @param {Object} event - jquery event object.
         */
        delete_billing_format : function(event) {
            _STOP_EVENT(event);
            var formatId = $('#formatForm availableFormat :selected').val();
            if (formatId == null || formatId == undefined) { // skip it
                _do_refresh_billing_setup_list(null, null);
                return false;
            }

            $.ajax({
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

$(function() {
    yukon.ami.billing.init();
});
