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

            if (_aaDiv.length) {
                yukon.ui.block(_aaDiv);
                $.ajax('assetAvailability', {
                    data : {
                        'paoId' : _assetId
                    }
                }).done(function(data) {
                    _aaDiv.html(data);
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
