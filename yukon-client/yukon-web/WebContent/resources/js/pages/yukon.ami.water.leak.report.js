yukon.namespace('yukon.ami.waterLeakReport');

/**
 * Module for the Water Leak Report page
 * @module yukon.ami.waterLeakReport
 * @requires JQUERY
 * @requires yukon
 */
yukon.ami.waterLeakReport = (function () {
    
    var 
    
    _initialized = false,
    _editMode = false,
    
    mod = {};

    mod = {
            
        init: function () {
            
            if (_initialized) return;
            
            /** Set url for leaks table paging/sorting. Use .attr since it's detected by css selector. */
            $('#leaks-container').attr('data-url', 'leaks?' + $('#filter-form').serialize());
            
            /** Attach 'select all' behavior. */
            $('#check-all-meters:checkbox').checkAll('#leaks-table tbody :checkbox');
            
            /** Show/hide intervals button on checkbox clicks. */
            $(document).on('click', '#leaks-table :checkbox', function (ev) {
                $('#intervals-btn').toggle($('#leaks-table :checked').length > 0);
            });
            
            /** Download csv file. */
            $('#download-report-btn').on('click', function (ev) { $('#csvLeakForm').submit(); });
            
            /** Build interval data url and go there. */
            $('#intervals-btn').click(function (ev) {
                var 
                paos = [], 
                url = 'intervalData?' + $('#filter-form').serialize() + '&paoIds=';
                
                // add selected pao id's
                $('#leaks-table tbody :checkbox').each(function() {
                    if ($(this).prop('checked')) {
                        paos.push($(this).data('paoId'));
                    }
                });
                url += paos.join(',');
                
                window.location.href = url;
            });
            
            $('#schedule-report-btn').click(function (ev) {
                var serializedForm = $('#filter-form').serialize();
                yukon.ui.dialog('#schedule-report-popup', 'schedule?' + serializedForm);
            });
            
            /** Open schedule popup a schedule */
            $(document).on('yukon.ami.water.leak.report.schedule.load', function (ev) {
                yukon.tag.scheduledFileExportInputs.initializeFields();
                if(!_editMode){
                    yukon.tag.scheduledFileExportInputs.setDefaultValues();
                }
                _editMode = false;
            });
            
            /** Edit a schedule */
            $(document).on('click', '.js-edit-job', function (ev) {
                _editMode = true;
                var jobId = $(this).data('jobId');
                yukon.ui.dialog('#schedule-report-popup', 'schedule?jobId=' + jobId);
            });
            
            /** Save a schedule */
            $(document).on('yukon.ami.water.leak.report.schedule.save', function (ev) {
                $('#schedule-form').ajaxSubmit({
                    success: function(data, status, xhr, $form) {
                        $('#schedule-report-popup').dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function(xhr, status, error, $form) {
                        $('#schedule-report-popup').html(xhr.responseText);
                        yukon.tag.scheduledFileExportInputs.initializeFields();
                    }
                });
            });
            
            /** Delete a schedule */
            $(document).on('yukon.job.delete', function (ev) {
                var jobId = $(ev.target).data('jobId');
                $.ajax('delete?jobId=' + jobId).done(function(data) {
                    window.location.href = window.location.href;
                });
            });
            
            _initialized = true;
        },
        
        /** Adjust device collection input after user selects a device group in filter settings. */
        filter_group_selected_callback: function () {
            $('#filter-form input[name="collectionType"]').val('group');
        },
        
        /** Adjust device collection input after user selects devices individually in filter settings. */
        filter_individual_selected_callback: function () {
            $('#filter-form input[name="collectionType"]').val('idList');
        },
        
        /** Adjust device collection input after user selects a device group in schedule settings. */
        schedule_group_selected_callback: function () {
            $('#schedule-form input[name="collectionType"]').val('group');
        },
        
        /** Adjust device collection input after user selects devices individually in schedule settings. */
        schedule_individual_selected_callback: function () {
            $('#schedule-form input[name="collectionType"]').val('idList');
        }
        
    };
    
    return mod;
})();

$(function () { yukon.ami.waterLeakReport.init(); });