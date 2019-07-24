yukon.namespace('yukon.dr.program.activityDetails');

/**
 * Module that handles the behavior on the setup of Program Widget view Details page.
 * 
 * @module yukon.dr.program.activityDetails
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.ui
 */
yukon.dr.program.activityDetails = (function() {

    var 
    _initialized = false,

    mod = {
            /** Initialize this module. */
            init: function () {

                if (_initialized) return;

                $("#js-scroll-details-table").scrollTableBody({rowsToDisplay: 17});

                $(".js-start-time-td").each(function (index, item) {
                    var startTime = $(item).find(".js-start-time-span").text();
                    $(item).attr("title", moment(startTime).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
                });

                $(".js-stop-time-td").each(function (index, item) {
                    var stopTime = $(item).find(".js-stop-time-span").text();
                    $(item).attr("title", moment(stopTime).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
                });

                _initialized = true;
            }

        };

        return mod;

    })();

$(function() {
    yukon.dr.program.activityDetails.init();
});
