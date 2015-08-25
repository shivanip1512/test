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
    
    /** @type {string} - Asset Total. */
    _assetTotal = "",
    
    /** @type {string} - Number of items to be shown per page */
    _itemsPerPage = "",
    
    /** @type {Object} - Reference of div */
    _aaDiv = '',

    /**
     * Get the filter attributes
     * @returns {Object} - Returns list of filter attributes.
     */
    _getFilter = function() {
        var filter = [];
        $('[data-filter].on').each(function(idx, item) {
            filter.push($(item).data('filter'));
        });
        return filter;
    },

    /**
     * Filter table data
     * @param {Object} event - jquery event object.
     * @returns {boolean} - Returns false after loading data.
     */
    _doFilterTable = function(event) {
        $(event.currentTarget).toggleClass('on');
        var data = {
            'assetId' : _assetId,
            'filter' : _getFilter(),
            'assetTotal':_assetTotal
        };
        if ("" != _itemsPerPage) {
            data.itemsPerPage = _itemsPerPage;
        }
        $('.device-detail-table').load('page', data);
        return false;
    },

    /**
     * Download data to a CSV
     * @param {Object} event - jquery event object.
     * @returns {boolean} - Returns false after download request.
     */
    _downloadToCsv = function(event) {
        var data = {
            'assetId' : _assetId,
            'filter' : _getFilter()
        }, param = $.param(data);
        window.location = 'downloadToCsv?' + param;
        return false;
    },

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
            _assetTotal = $("#assetTotal").val();
            _itemsPerPage = $("#itemsPerPage").val();
            _aaDiv = $('.js-asset-availability');

            $(document).on('click', '[data-filter]', _doFilterTable);
            $(document).on('click', '#dd-download', _downloadToCsv);
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
