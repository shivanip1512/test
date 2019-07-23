yukon.namespace('yukon.dr.program.details');

/**
 * Module that handles the behavior on the setup Program Details page.
 * 
 * @module yukon.dr.program.details
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.ui
 */
yukon.dr.program.details = (function() {

    var 
    _initialized = false,

    mod = {
            /** Initialize this module. */
            init: function () {

                if (_initialized) return;

                $("#js-scroll-details-table").scrollTableBody({rowsToDisplay: 17});

                $("td.js-event-start-time").each(function (index, item) {
                    var eventStartTime = $(item).data('startDateTime');
                    $(item).attr("title", moment(eventStartTime).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
                });

                $("td.js-event-stop-time").each(function (index, item) {
                    var eventStopTime = $(item).data('stopDateTime');
                    if (eventStopTime === ''){
                        $(item).attr("title", $(unknownmsg).val());
                    } else {
                        $(item).attr("title", moment(eventStopTime).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
                    }
                });

                _initialized = true;
            }

        };

        return mod;

    })();

$(function() {
    yukon.dr.program.details.init();
});
