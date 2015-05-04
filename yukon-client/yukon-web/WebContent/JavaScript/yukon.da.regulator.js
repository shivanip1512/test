yukon.namespace('yukon.da.regulator');

/**
 * Module that manages the regulator page in capcontrol
 *
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires yukon
 */

yukon.da.regulator = (function () {

    'use strict';
    
    /** @type {number} - The regulator id. */
    var _id = null;
    
    /** @type {string} - Events updater timeout reference. */
    var _events_token = null;
    
    var _templates = {
        successful: '<span class="label label-success">' + yg.text.successful + '</span>',
        failed: '<span class="label label-danger">' + yg.text.failed + '</span>'
    };
    
    /**
     * A mapping of appropriate attributes to show per regulator type.
     * 
     * @type {Object.<string, string>}
     * Object mapping PaoTypes to supported attributes. ex:
     *  { 
     *    PAO_TYPE_1: ['Attr1', 'Attr2'],
     *    PAO_TYPE_2: ['Attr2', 'Attr3']
     *  }
     */
    var _paoTypeToAttributes = {};
    
    /** @type {Object} - A hash of TimeRange enum entry to hours it represents. */
    var _range_hours = {};
    
    /**
     * Different regulator types (LTC, CL7) have different attributes.
     * this method sets up the attributes table to display only the supported attributes
     * and hides and disabled all the other inputs.
     */
    var _showHideMappings = function () {
        
        var paoType = $('#regulator-type').val(),
            mappings = _paoTypeToAttributes[paoType],
            table = $(document).find('.js-mappings-table');
        table.find('[data-mapping]').hide().find('input').prop('disabled', true);
        
        mappings.forEach(function (mapping) {
            table.find('[data-mapping="' + mapping + '"]').show().find('input').prop('disabled', false);
        });
    };
    
    var _updateRecentEvents = function () {
        
        var range = $('#ivvc-events-range').val();
        
        $.ajax({
            url: yukon.url('/capcontrol/regulators/' + _id + '/events'),
            data: { 'range': range }
        }).done(function (events) {
            
            var body = $('#regulator-events').find('tbody');
            var current = body.find('tr[data-event-id]');
            var eventIds = events.map(function (event) { return event.id; });
            var timeline = $('.js-events-timeline[data-regulator-id="' + _id + '"]');
            var toAdd = [], currentIds = [];
            
            var options = {};
            options.end = new Date().getTime();
            var now = new Date();
            var hoursAgo = _range_hours[range];
            var begin = new Date(now.getTime() - (1000 * 60 * 60 * hoursAgo));
            options.begin = begin.getTime();
            
            var eventsToRemove = [];
            // Remove any rows in the table and timeline that are not in the retrieved list.
            current.each(function (idx, row) {
                row = $(row);
                var id = row.data('eventId');
                if (eventIds.indexOf(id) === -1) {
                    row.remove();
                    eventsToRemove.push(id);
                } else {
                    currentIds.push(id);
                }
            });
            if (eventsToRemove.length) {
                timeline.timeline(options);
                timeline.timeline('removeEvents', eventsToRemove);
            }
            
            // Add any rows in the retrieved list that are not in the table.
            // Reverse order to add oldest first.
            events.reverse().forEach(function (event) {
                if (currentIds.indexOf(event.id) === -1) {
                    // New event not in table, add it to table and timeline.
                    var row = $('.js-event-template').clone().removeClass('js-event-template')
                    .attr('data-event-id', event.id)
                    .data('eventId', event.id)
                    .data('timestamp', event.timestamp);
                    
                    row.find('.js-event-icon').addClass(event.icon);
                    row.find('.js-message').html(event.message);
                    row.find('.js-user').text(event.user);
                    
                    var timeText = moment(event.timestamp).tz(yg.timezone).format(yg.formats.date.full);
                    row.find('.js-timestamp').text(timeText);
                    
                    var attached = false;
                    
                    body.find('tr').each(function (idx, tr) {
                        tr = $(tr);
                        if (!attached && tr.data('timestamp') < event.timestamp) {
                            row.insertBefore(tr);
                            attached = true;
                        } 
                       
                    });
                    
                    if (!attached) {
                        body.append(row);
                    }
                    
                    row.flash();
                    
                    toAdd.push(event);
                }
            });
            
            if (toAdd.length) {
                timeline.timeline(options);
                timeline.timeline('addEvents', toAdd);
            }
            
            var hasEvents = body.find('[data-event-id]').length > 0;
            $('.js-ivvc-events-empty').toggle(!hasEvents);
            $('.js-ivvc-events-holder').toggle(hasEvents);
            timeline.toggle(hasEvents);
            
            _events_token = setTimeout(_updateRecentEvents, yg.rp.updater_delay);
            
        });
    };
    
    var mod = {
        
        init : function () {
            
            /** The pao id of the regulator when in view/edit mode. */
            _id = $('#regulator-id').val();
            
            _range_hours = yukon.fromJson('#range-hours');
            
            _paoTypeToAttributes = yukon.fromJson('#pao-type-map');
            
            _showHideMappings();
            
            /** Show the correct attributes in the mapping table when the type of regulator changes. */
            $('#regulator-type').on('change', function () {
                _showHideMappings();
            });
            
            /** User confirmed intent to delete regulator. */
            $(document).on('yukon:da:regulator:delete', function () {
                $('#delete-regulator').submit();
            });
            
            /** User changed the events time range. Cancel updating timeout and restart. */
            $('#ivvc-events-range').on('change', function () {
                yukon.cookie.set('ivvc-regualtor', 'last-event-range', $(this).val());
                clearTimeout(_events_token);
                _updateRecentEvents();
            });
            
            /** User clicked the 'map' button on the map attributes popup. Start the task. */
            $(document).on('yukon:da:regulator:automap', function (ev) {
                
                var dialog = $(ev.target);
                debug.log(dialog);
                
                yukon.ui.elementGlass.show('.js-auto-map-dialog');
                dialog.parent().find('.ui-dialog-buttonpane').find('.ui-button').prop('disabled', true);
                 
                $.ajax(yukon.url('/capcontrol/regulators/' + _id + '/automap'))
                .done(function (result) {
                    
                    debug.debug('Mapping Result', result);
                    
                    yukon.ui.elementGlass.hide('.js-auto-map-dialog');
                    dialog.parent().find('.ui-dialog-buttonpane').find('.ui-button').prop('disabled', false);
                    
                    var tbody = $('.js-auto-map-dialog .js-mappings');
                    var status = result.status.type;
                    var success = status === 'SUCCESSFUL' || status === 'PARTIALLY_SUCCESSFUL';
                    
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
                    
                    if (success) {
                        $.ajax(yukon.url('/capcontrol/regulators/' + _id + '/mapping-table'))
                        .done(function (table) {
                            $('.js-mappings-container').html(table);
                        });
                    }
                    
                });
            });
            
            /** Reset the attribute mapping dialog when closed */
            $(document).on('dialogclose', '.js-auto-map-dialog', function (ev) {
                
                var dialog = $(this);
                
                dialog.find('.js-automap-results').hide();
                $('.js-result-header').hide();
                dialog.find('.js-result').empty();
            });
            
            _updateRecentEvents();
        }
    };
    
    return mod;
}());

$(function () { yukon.da.regulator.init(); });
