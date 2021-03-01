yukon.namespace('yukon.da.busview');

/**
 * Module for the busView page/
 * @module yukon.da.busview
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.busview = (function () {

    'use strict';
    
   
    var _initialized = false;
    
    /** @type {Object} - A hash of TimeRange enum entry to hours it represents. */
    var _range_hours = {};
    
    /** @type {string} - Events updater timeout reference. */
    var _events_token = null;

    var _updateRecentEvents = function () {
    
    var range = $('#ivvc-events-range').val();

        $(".js-events-timeline").each(function () {
            
            var _id = $(this).data('zoneId'),
                timeline = $(this);
            if (timeline.is(':visible')) {
                $.ajax({
                    url : yukon.url('/capcontrol/zones/' + _id + '/events'),
                    data : {
                        'range' : range
                    }
                }).done(function (events) {
                    var toAdd = [],
                        options = {},
                        now = new Date(),
                        hoursAgo = _range_hours[range],
                        begin = new Date(now.getTime() - (1000 * 60 * 60 * hoursAgo));
                    options.end = new Date().getTime();
                    options.begin = begin.getTime();
                    options.showLabels = true;
    
                    // Reverse order to add oldest first.
                    events.reverse().forEach(function (event) {
                        toAdd.push(event);
                    });
                    options.events = toAdd;
                    timeline.timeline(options);
                    timeline.timeline('draw');
                });
            }

        });
        _events_token = setTimeout(_updateRecentEvents, yg.rp.updater_delay);

    };

    var _updateRecentEventsTable = function () {
        /** @type {string} - Events updater timeout reference. */
   
        var range = $('#ivvc-events-range').val();

            var _id = $('#ivvc-bus-id').val();
            $.ajax({
                url : yukon.url('/capcontrol/buses/' + _id + '/events'),
                data : {
                    'range' : range
                }
            }).done(function (events) {
                
                var body = $('#ivvc-events').find('tbody');
                var current = body.find('tr[data-event-id]');
                var eventIds = events.map(function (event) { return event.id; });
                var currentIds = [];
                
                var options = {};
                options.end = new Date().getTime();
                var now = new Date();
                var hoursAgo = _range_hours[range];
                var begin = new Date(now.getTime() - (1000 * 60 * 60 * hoursAgo));
                options.begin = begin.getTime();
                // Remove any rows in the table and timeline that are not in the retrieved list.
                current.each(function (idx, row) {
                    row = $(row);
                    var id = row.data('eventId');
                    if (eventIds.indexOf(id) === -1) {
                        row.remove();
                    } else {
                        currentIds.push(id);
                    }
                });
                
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
                    }
                });
                
                var hasEvents = body.find('[data-event-id]').length > 0;
                $('.js-ivvc-events-empty').toggle(!hasEvents);
                $('.js-ivvc-events-holder').toggle(hasEvents);
                
            });
        setTimeout(_updateRecentEventsTable, yg.rp.updater_delay);

    };
    
    var _zoneHierarchyTreeClick = function (event, data) {
        var node = data.node;
        node.setSelected(true);
        $.ajax({
            url : yukon.url('/capcontrol/ivvc/zone/selectedZoneDetail?zoneId=' + node.data.id),
        }).done(function (zoneDetail) {
            $('.js-selected-zone-details').html(zoneDetail);
            yukon.ui.block($('#selectedZoneEvents'), 200);
        });
    };

   var mod = {

        /** Initialize this module. */
        init : function () {

            if (_initialized)
                return;
            
            _range_hours = yukon.fromJson('#range-hours');
            
            $('#zoneHierarchyTree').fancytree('option', 'activate', _zoneHierarchyTreeClick);
            
            /** User changed the events time range. Cancel updating timeout and restart. */
            $('#ivvc-events-range').on('change', function () {
                var url = yukon.url('/user/updateDisplayEventRangePreference.json'),
                    params = {
                        prefValue: $(this).val()
                    };
                $.ajax({ type: 'post', url: url, data: params });
                clearTimeout(_events_token);
                _updateRecentEvents();
                _updateRecentEventsTable();
            });
            
            $(document).on('click', '.js-command-button', function (event) {
                var button = $(this),
                    paoId = button.data('paoId'),
                    cmdId = button.data('commandId');
                
                doItemCommand(paoId, cmdId, event);
            });

            _updateRecentEvents();
            _updateRecentEventsTable();

            _initialized = true;
        }

    };

    return mod;
})();

$(function () {
    yukon.da.busview.init();
});