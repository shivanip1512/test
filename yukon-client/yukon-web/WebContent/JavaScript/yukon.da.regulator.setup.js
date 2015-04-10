yukon.namespace('yukon.da.regulatorSetup');

/**
 * Module for the regulator setup page.
 * @module yukon.da.regulatorSetup
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.regulatorSetup = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    /** Timeout update callback reference for mapping results. */
    _updater = null,
    
    _templates = {
        successful: '<span class="label label-success">' + yg.text.successful + '</span>',
        failed: '<span class="label label-danger">' + yg.text.failed + '</span>'
    },
    
    _updateRecent = function () {
        
        var table = $('.js-task-table');
        
        $.ajax({
            url: yukon.url('/capcontrol/regulator-setup/map-attributes/recent'),
            contentType: 'application/json'
        }).done(function (tasks, status, xhr) {
            
            var row, percent;
            
            tasks.forEach(function (task) {
                
                row = $(table.find('[data-task="' + task.id + '"]')[0]);
                percent = yukon.percent(task.completed, task.total, 2); 
                
                if (!row.length) {
                    row = table.find('.js-recent-task-template').clone()
                    .removeClass('js-recent-task-template')
                    .attr('data-task', task.id).data('task', task.id)
                    .prependTo(table.find('tbody'));
                    
                    row.find('.js-task-start a').text(task.start);
                    row.find('.js-user').text(task.user);
                    row.find('.js-count').text(task.total);
                    row.show();
                    table.show().prev('.empty-list').hide();
                }
                row.find('.js-count-failed').text(task.failed);
                row.find('.js-count-partial').text(task.partial);
                row.find('.js-count-success').text(task.success);
                
                row.find('.js-complete').toggleClass('dn', !task.complete);
                row.find('.js-progress-bar').toggleClass('dn', task.complete)
                .find('.progress-bar').css({ width: percent });
                row.find('.js-progress-text').toggleClass('dn', task.complete)
                .text(percent);
            });
            
        }).always(function () {
            setTimeout(_updateRecent, 2000);
        });
    },
    
    /** Update the mapping table for a particular result. */
    _updateResult = function (result) {
        
        var tbody = $('.js-task-results .js-mappings');
        var status = result.status.type;
        
        $('.js-result-header').show();
        $('.js-automap-results').show()
        .find('.js-automap-result').text(result.status.text)
        .toggleClass('label-info', status === 'INCOMPLETE')
        .toggleClass('label-danger', status === 'FAILED' )
        .toggleClass('label-warning', status === 'PARTIALLY_SUCCESSFUL')
        .toggleClass('label-success', status === 'SUCCESSFUL');
        
        result.mappings.forEach(function (mapping) {
            var td = tbody.find('[data-mapping="' + mapping.type + '"] .js-result');
            if (mapping.success) {
                td.html(_templates.successful);
            } else {
                td.html(_templates.failed).append($('<span>').text(' - ' + mapping.text));
            }
        });
        
        if (!result.complete) {
            setTimeout(function () {
                $.ajax(yukon.url('/capcontrol/regulator-setup/map-attributes/' 
                        + result.taskId + '/result/' + result.regulatorId))
                .done(function (result) {
                    _updater = _updateResult(result);
                });
            }, 1000);
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            /** Show picker when start button clicked. */
            $('.js-new-task').click(function (ev) { yukon.pickers['regulatorPicker'].show(); });
            
            /** Retrieve a task result. */
            $(document).on('click', '.js-task-table tbody tr', function (ev) {
                
                if ($(ev.target).closest('.js-delete').length) {
                    // Don't load results if we are deleting, just propagate event up.
                    return true;
                }
                
                var taskId = $(this).data('task');
                // Get the task devices and the results table for the first device
                $.ajax(yukon.url('/capcontrol/regulator-setup/map-attributes/' + taskId + '/results'))
                .done(function (results) {
                    
                    $('.js-mapping-results-container').html(results);
                    
                    // Get the results data for the first device
                    var regId = $('.js-mapping-result-devices .selected').data('id');
                    $.ajax(yukon.url('/capcontrol/regulator-setup/map-attributes/' + taskId + '/result/' + regId))
                    .done(function (result) {
                        
                        debug.debug('Mapping Result', result);
                        
                        _updateResult(result);
                        
                    });
                });
                
            });
            
            /** Show the mapping results table for a device. */
            $(document).on('click', '.js-mapping-result-devices li', function (ev) {
                
                // Clear any updating in case the current one wasn't complete
                clearTimeout(_updater);
                var regId = $(this).data('id');
                var taskId = $(this).closest('[data-task]').data('task');
                
                $('.js-mappings .js-result').empty();
                
                $.ajax(yukon.url('/capcontrol/regulator-setup/map-attributes/' + taskId + '/result/' + regId))
                .done(function (result) {
                    
                    debug.debug('Mapping Result', result);
                    
                    _updateResult(result);
                    
                });
            });
            
            /** Delete a task. */
            $(document).on('click', '.js-delete', function (ev) {
                
                var row = $(this).closest('[data-task]');
                var table = $(this).closest('table');
                var taskId = row.data('task');
                
                $.ajax({
                    url : yukon.url('/capcontrol/regulator-setup/map-attributes/' + taskId),
                    type: 'delete'
                }).done(function () {
                    debug.debug('Task ' + taskId + ' delete.');
                    row.remove();
                    
                    if (!table.find('tbody tr').length) {
                        table.hide();
                        table.prev('.empty-list').show();
                    }
                    
                    var taskResults = $('.js-task-results');
                    if (taskResults.length) {
                        if (taskResults.data('task') === taskId) {
                            // Remove this task results
                            clearTimeout(_updater);
                            $('.js-mapping-results-container').empty();
                        }
                    }
                });
                
            });
            
            /** Start a mapping task after regulators were picked. */
            $(document).on('yukon:da:regulator:mapping:start', function (ev, items, picker) {
                
                debug.debug('Regulators chosen for mapping task:', items);
                var ids = items.map(function (item) { return item.paoId; });
                
                $.ajax({
                    url: yukon.url('/capcontrol/regulator-setup/map-attributes'),
                    type: 'post',
                    contentType: 'application/json',
                    data: JSON.stringify(ids),
                    dataType: 'json'
                })
                .done(function (task) {
                    debug.debug('New Task: ', task);
                });
            });
            
            _updateRecent();
            
            _initialized = true;
        },
        
    };
    
    return mod;
})();

$(function () { yukon.da.regulatorSetup.init(); });