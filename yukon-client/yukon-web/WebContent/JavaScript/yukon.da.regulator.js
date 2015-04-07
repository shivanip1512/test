yukon.namespace('yukon.da.regulator');

/**
 * Module that manages the regulator edit page in capcontrol
 *
 * @requires JQUERY
 * @requires yukon
 */

yukon.da.regulator = (function () {

    /**
     * Different regulator types (LTC, CL7) have different attributes.
     * this method sets up the attributes table to display only the supported attributes
     * and hides and disabled all the other inputs.
     */
    var _showHideMappings = function (mappings) {

        var paoType = $('#regulator-type').val(),
            mappings = _paoTypeToAttributes[paoType],
            table = $(document).find('.js-mappings-table');
        table.find('[data-mapping]').hide().find('input').prop('disabled', true);

        mappings.forEach(function (mapping, idx) {
            table.find('[data-mapping="' + mapping + '"]').show().find('input').prop('disabled', false);
        });
    },

    /**
     * @type {Object.<string, string>}
     *
     * Object mapping PaoTypes to supported attributes. ex:
     * 
     *  { PAO_TYPE_1: ['Attr1', 'Attr2'],
     *    PAO_TYPE_2: ['Attr2', 'Attr3']
     *  }
     */
    _paoTypeToAttributes = {},

    _updateRecentEvents = function () {

        $.ajax({
            url: yukon.url('/capcontrol/regulators/' + $('#regulator-id').val() + '/events'),
            data: { 'lastUpdate': $('#regulator-events-last-update').val() }
        }).done(function (data) {

            $('#regulator-events-last-update').val(data.timestamp);
            var templateRow = $('#regulator-events-template-row');
            var body = $('#regulator-events').find('tbody');

            var events = data.events;

            if (events.length) {
                $('.js-ivvc-events-empty').hide();
                $('.js-ivvc-events-holder').show();
            }

            events.reverse().forEach( function (event, idx) {

                var newRow = $(templateRow.clone()).removeAttr('id');

                newRow.find('.js-event-icon').addClass(event.icon);
                newRow.find('.js-message').html(event.message);
                newRow.find('.js-user').text(event.user);
                newRow.find('.js-timestamp').text(event.timestamp);

                body.prepend(newRow);
                newRow.flash();
            });

            body.find('tr:gt(20)').remove();

            setTimeout(_updateRecentEvents, 4000);

        });

    };

    var mod = {

            init : function () {

                _paoTypeToAttributes = yukon.fromJson('#pao-type-map');
                _showHideMappings();

                $('#regulator-type').on('change', function () {
                    _showHideMappings();
                });

                $(document).on('yukon:da:regulator:delete', function () {
                    $('#delete-regulator').submit();
                });

                $.ajax(yukon.url('/capcontrol/regulators/' + $('#regulator-id').val() + '/all-events'))
                .done(function (data) {

                    var begin = data.begin.millis;
                    var end = data.end.millis;

                    var container = $('#regulator-timeline-container');

                    data.tickMarks.forEach(function (tick) {
                        var timestamp = tick.millis;
                        var percent = yukon.percent(timestamp - begin, end - begin, 4);
                        var tick = $('<span>')
                        .addClass('tick-mark')
                        .css({left: percent});

                        container.append(tick);
                    });

                    Object.keys(data.events2).forEach(function (timestamp) {

                        var events = data.events2[timestamp];

                        var eventList = $('<ul>');

                        events.forEach(function (event) {
                            var eventItem = $('<li>').html(event.text.timestamp + ': ' + event.text.message);
                            eventList.append(eventItem);
                        });

                        var percent = yukon.percent(timestamp - begin, end - begin, 4);
                        var span = $('<span>')
                        .addClass('event')
                        .css({'left': percent });

                        span.append('<i class="M0 icon icon-bullet-go-up"/>');
                        
                        container.append(span);

                        span.tipsy({
                            html: true,
                            // some tooltips actually are around 175 px in height
                            gravity: $.fn.tipsy.autoBounds(175, 'nw'),
                            opacity: 1.0,
                            title: function () {
                                return eventList.html();
                            },
                            delayIn: 150,
                            fade: true
                        });
                    });
                });

                _updateRecentEvents();
            }
    };
    return mod;
}());

$(function () { yukon.da.regulator.init(); });
