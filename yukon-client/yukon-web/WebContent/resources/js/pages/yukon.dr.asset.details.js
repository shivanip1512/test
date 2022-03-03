yukon.namespace('yukon.dr.assetDetails');

/**
 * Handles Asset Availability operations.
 * 
 * @module yukon.dr.assetDetails
 * @requires JQUERY
 * @requires yukon
 * @requires yukon.ui
 */
yukon.dr.assetDetails = (function() {

    var _initialized = false,
    
    /** @type {string} - Asset Id. */
    _assetId = "",
    
    /** @type {Object} - Reference of div */
    _aaDiv = '',

    /**
     * Send request to ping a device
     * @param {Object} event - jquery event object.
     * @returns {boolean} - Returns true after completion
     */
    _pingDevices = function(event) {
        $('#pingResults').show();
        $('.progressbar-percent-complete').text("0%");
        $('.progress-bar').width(0);
        var url = "pingDevices?assetId=" + _assetId;
        $.ajax({
            url : url,
            method : 'POST'
        });
        return true;
    },

    mod = {
        init : function() {
            if (_initialized) {
                return;
            }

            _assetId = $("#assetId").val();
            _aaDiv = $('.js-asset-availability');

            $(document).on('click', '#pingButton', _pingDevices);
            
            $(document).on('click', '.js-asset-availability-data-pie', function () {
                yukon.assetAvailability.pieChart.showDetailsPage(_assetId, $('.js-asset-availability-pie-chart-summary'));
            });

            if (_aaDiv.length) {
                yukon.ui.block(_aaDiv);
                $.ajax('assetAvailability', {
                    data : {
                        'paoId' : _assetId
                    }
                }).done(function(data) {
                    _aaDiv.html(data);
                    if ($("#js-asset-availability-summary").exists()) {
                        var chart = $('.js-asset-availability-pie-chart-summary'),
                            data = yukon.fromJson('#js-asset-availability-summary');
                        yukon.assetAvailability.pieChart.buildChart(chart, data, true);
                        $('input[name=statuses]').each(function() {
                            var statusButton = $(this);
                            if (!statusButton.prop("checked")) {
                                var legendItems = chart.highcharts().series[0].data;
                                for (var i = 0; i < legendItems.length; i++) {
                                    if (statusButton.val() == legendItems[i].filter) {
                                        legendItems[i].setVisible(false, false);
                                    }
                                }
                            }
                        });
                        chart.highcharts().redraw();
                    }
                    yukon.ui.unblock(_aaDiv);
                });
            }

            _initialized = true;
        },

        /** Unbusy the ping button */
        unbusyPingButton : function() {
            yukon.ui.unbusy($('#pingButton'));
        }

    };

    return mod;

}());

$(function() {
    yukon.dr.assetDetails.init();
});
