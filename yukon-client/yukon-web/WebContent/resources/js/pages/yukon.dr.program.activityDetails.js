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

                _initialized = true;
            },

            setStartTimeTooltip: function (element) {
                return function (data) {
                    element.attr("title", moment(data.value).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
                }
            },

            setStopTimeTooltip: function (element) {
                return function (data) {
                    element.attr("title", moment(data.value).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
                }
            }

        };

        return mod;

    })();

$(function() {
    yukon.dr.program.activityDetails.init();
});
