yukon.namespace('yukon.da.zone');

/**
 * Module that manages the zone detail page in capcontrol
 *
 * @requires JQUERY
 * @requires MOMENT
 * @requires MOMENT_TZ
 * @requires yukon
 */

yukon.da.zone = (function () {

    'use strict';
    
    /** @type {number} - The regulator id. */
    var _id = null;
    
    /** @type {string} - Events updater timeout reference. */
    var _events_token = null;
    
    /** @type {Object} - A hash of TimeRange enum entry to hours it represents. */
    var _range_hours = {};
    
    var _updateRecentEvents = function (forceRedraw) {
        
        var drawn = false;
        
        var range = $('#ivvc-events-range').val();
        
        $.ajax({
            url: yukon.url('/capcontrol/zones/' + _id + '/events'),
            data: { 'range': range }
        }).done(function (events) {
            
            var body = $('#ivvc-events').find('tbody');
            var current = body.find('tr[data-event-id]');
            var eventIds = events.map(function (event) { return event.id; });
            var timeline = $('.js-events-timeline[data-zone-id="' + _id + '"]');
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
                drawn = true;
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
                    row.find('.js-device-name').text(event.deviceName);
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
                    
                    toAdd.push(event);
                }
            });
            
            if (toAdd.length) {
                timeline.timeline(options);
                timeline.timeline('addEvents', toAdd);
                drawn = true;
            }
            
            var hasEvents = body.find('[data-event-id]').length > 0;
            $('.js-ivvc-events-empty').toggle(!hasEvents);
            $('.js-ivvc-events-holder').toggle(hasEvents);
            timeline.toggle(hasEvents);
            
            if (forceRedraw && !drawn) {
                timeline.timeline(options);
                timeline.timeline('draw');
            }
            
            _events_token = setTimeout(_updateRecentEvents, yg.rp.updater_delay);
            
        });
    };
    
    var mod = {
        
        init : function () {
            
            /** The id of the zone. */
            _id = $('#zone-id').val();
            
            _range_hours = yukon.fromJson('#range-hours');
            
            /** User changed the events time range. Cancel updating timeout and restart. */
            $('#ivvc-events-range').on('change', function () {
                yukon.cookie.set('ivvc-regualtor', 'last-event-range', $(this).val());
                clearTimeout(_events_token);
                _updateRecentEvents();
            });
            
            $('.js-zone-editor').click(function () {
                var info = $('#zone-editor-info'),
                    url = info.data('editorUrl'),
                    title = info.data('editorTitle');
                openSimpleDialog('zoneWizardPopup', url, title, null, 'get');
            });

            $(document).on('click', '.js-command-button', function (event) {
                var button = $(this),
                    paoId = button.data('paoId'),
                    cmdId = button.data('commandId');
                
                doItemCommand(paoId, cmdId, event);
            });
            
            _updateRecentEvents();
        }
    };
    
    return mod;
}());

$(function () { yukon.da.zone.init(); });