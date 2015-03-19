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
            mappings = _paoTypeMap[paoType],
            table = $(document).find('.js-mappings-table');
        table.find('[data-mapping]').hide().find('input').prop('disabled', true);

        for (var i = 0, length = mappings.length; i < length; i += 1) {
            table.find('[data-mapping="' + mappings[i] + '"]').show().find('input').prop('disabled', false);
        }
    },

    /**
     * Object mapping PaoTypes to supported attributes. ex:
     * 
     *  { PAO_TYPE_1: ['Attr1', 'Attr2'],
     *    PAO_TYPE_2: ['Attr2', 'Attr3']
     *  }
     */
    _paoTypeMap = {},

    _updateRecentEvents = function () {

        $.ajax({
            url: yukon.url('/capcontrol/regulators/' + $('#regulator-id').val() + '/events'),
            data: {'lastUpdate': $('#regulator-events-last-update').val()}
        }).done(function (data) {

            $('#regulator-events-last-update').val(data.timestamp);
            var templateRow = $('#regulator-events-template-row');
            var body = $('#regulator-events').find('tbody');

            var events = data.events;
            
            if (events.length) {
                $('.js-ivvc-events-empty').hide();
                $('.js-ivvc-events-holder').show();
            }
            var event, newRow;

            for (var ii = events.length -1; ii >= 0; ii -=1) {
                event = events[ii];
                newRow = $(templateRow.clone()).removeAttr('id');

                newRow.find('.js-event-icon').addClass(event.icon);
                newRow.find('.js-message').html(event.message);
                newRow.find('.js-user').text(event.user);
                newRow.find('.js-timestamp').text(event.timestamp);

                body.prepend(newRow);
                newRow.flash();
            }

            body.find('tr:gt(20)').remove();

            setTimeout(_updateRecentEvents, 4000);

        });
    };

    var mod = {

            init : function () {

                _paoTypeMap = yukon.fromJson('#pao-type-map');
                _showHideMappings();

                $('#regulator-type').on('change', function () {
                    _showHideMappings();
                });

                $(document).on('yukon:da:regulator:delete', function () {
                    $('#delete-regulator').submit();
                });

                _updateRecentEvents();
            }
    };
    return mod;
}());

$(function () { yukon.da.regulator.init(); });
