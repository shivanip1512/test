/**
 * Updates the MAC Schedules list. 
 * 
 * @module yukon.ami.macs
 * @requires JQUERY
 * @requires yukon
 */

yukon.namespace('yukon.ami.macs');

yukon.ami.macs = (function () {
	
	/** Refreshes the list of scheduled scripts after every 5 seconds. */
    var _autoUpdatePageContent = function () {
        var tableContainer = $('#scripts-container'),
            reloadUrl = tableContainer.attr('data-url');
        tableContainer.load(reloadUrl, function () {
            setTimeout(_autoUpdatePageContent, 5000);
        });

    },
        mod = {
            init: function () {
                var tableContainer = $('#scripts-container');
                if (tableContainer.length === 1) {
                    _autoUpdatePageContent();
                }
            }
    };
    return mod;
}());

$(function () {
    yukon.ami.macs.init();
});
